import { keycloakConfig, authUrls, buildAuthUrl, buildLogoutUrl } from '../config/keycloak';

class AuthService {
  constructor() {
    this.accessToken = null;
    this.refreshToken = null;
    this.idToken = null;
    this.tokenExpiry = 0;
    this.refreshTimeout = null;

    // Load tokens from localStorage on init
    this.loadTokens();
    this.setupAutoRefresh();
  }

  loadTokens() {
    this.accessToken = localStorage.getItem('access_token');
    this.refreshToken = localStorage.getItem('refresh_token');
    this.idToken = localStorage.getItem('id_token');
    const expiry = localStorage.getItem('token_expiry');
    this.tokenExpiry = expiry ? parseInt(expiry, 10) : 0;
  }

  saveTokens(tokens) {
    this.accessToken = tokens.access_token;
    this.refreshToken = tokens.refresh_token;
    this.idToken = tokens.id_token;
    this.tokenExpiry = Date.now() + tokens.expires_in * 1000;

    localStorage.setItem('access_token', tokens.access_token);
    localStorage.setItem('refresh_token', tokens.refresh_token);
    localStorage.setItem('id_token', tokens.id_token);
    localStorage.setItem('token_expiry', this.tokenExpiry.toString());

    this.setupAutoRefresh();
  }

  clearTokens() {
    this.accessToken = null;
    this.refreshToken = null;
    this.idToken = null;
    this.tokenExpiry = 0;

    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('id_token');
    localStorage.removeItem('token_expiry');

    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
      this.refreshTimeout = null;
    }
  }

  setupAutoRefresh() {
    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
    }

    if (!this.accessToken || !this.refreshToken) {
      return;
    }

    // Refresh 1 minute before expiry
    const refreshIn = this.tokenExpiry - Date.now() - 60000;

    if (refreshIn > 0) {
      this.refreshTimeout = setTimeout(() => {
        this.refreshAccessToken();
      }, refreshIn);
    }
  }

  // Redirect to Keycloak login
  login() {
    const redirectUri = `${window.location.origin}/callback`;
    window.location.href = buildAuthUrl(redirectUri);
  }

  // Handle callback from Keycloak
  async handleCallback(code, state) {
    const savedState = sessionStorage.getItem('oauth_state');

    if (state !== savedState) {
      console.error('State mismatch');
      return false;
    }

    sessionStorage.removeItem('oauth_state');

    try {
      const redirectUri = `${window.location.origin}/callback`;

      const response = await fetch(authUrls.token, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'authorization_code',
          client_id: keycloakConfig.clientId,
          code: code,
          redirect_uri: redirectUri,
        }),
      });

      if (!response.ok) {
        const error = await response.text();
        console.error('Token exchange failed:', error);
        return false;
      }

      const tokens = await response.json();
      this.saveTokens(tokens);

      // Sync user with backend
      await this.syncUser();

      return true;
    } catch (error) {
      console.error('Callback handling failed:', error);
      return false;
    }
  }

  // Sync user with backend after login
  async syncUser() {
    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/sync', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${this.accessToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        console.log('User synced successfully');
      }
    } catch (error) {
      console.warn('User sync failed:', error);
    }
  }

  // Logout
  logout() {
    const redirectUri = window.location.origin;
    const logoutUrl = buildLogoutUrl(redirectUri, this.idToken);
    this.clearTokens();
    window.location.href = logoutUrl;
  }

  // Get access token (with auto-refresh)
  async getAccessToken() {
    if (!this.accessToken) {
      return null;
    }

    // Refresh if expired or about to expire
    if (Date.now() >= this.tokenExpiry - 30000) {
      const refreshed = await this.refreshAccessToken();
      if (!refreshed) {
        return null;
      }
    }

    return this.accessToken;
  }

  // Refresh access token
  async refreshAccessToken() {
    if (!this.refreshToken) {
      return false;
    }

    try {
      const response = await fetch(authUrls.token, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          grant_type: 'refresh_token',
          client_id: keycloakConfig.clientId,
          refresh_token: this.refreshToken,
        }),
      });

      if (!response.ok) {
        this.clearTokens();
        return false;
      }

      const tokens = await response.json();
      this.saveTokens(tokens);
      return true;
    } catch (error) {
      console.error('Token refresh failed:', error);
      this.clearTokens();
      return false;
    }
  }

  // Check if user is authenticated
  isAuthenticated() {
    return !!this.accessToken && Date.now() < this.tokenExpiry;
  }

  // Get decoded token payload
  getTokenPayload() {
    if (!this.accessToken) {
      return null;
    }

    try {
      const parts = this.accessToken.split('.');
      if (parts.length !== 3) {
        return null;
      }

      const payload = parts[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch (error) {
      console.error('Token decode failed:', error);
      return null;
    }
  }

  // Get user ID from token
  getUserId() {
    const payload = this.getTokenPayload();
    return payload?.sub || null;
  }

  // Get username from token
  getUsername() {
    const payload = this.getTokenPayload();
    return payload?.preferred_username || null;
  }

  // Get email from token
  getEmail() {
    const payload = this.getTokenPayload();
    return payload?.email || null;
  }

  // Check if user has role
  hasRole(role) {
    const payload = this.getTokenPayload();
    const roles = payload?.realm_access?.roles || [];
    return roles.includes(role);
  }

  // Check if user is admin
  isAdmin() {
    return this.hasRole('ADMIN');
  }
}

// Export singleton instance
export const authService = new AuthService();