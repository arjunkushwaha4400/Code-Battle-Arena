export const keycloakConfig = {
  url: 'http://localhost:8180',
  realm: 'coding-arena',
  clientId: 'arena-app',
};

export const authUrls = {
  authorize: `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/auth`,
  token: `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/token`,
  logout: `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/logout`,
  userinfo: `${keycloakConfig.url}/realms/${keycloakConfig.realm}/protocol/openid-connect/userinfo`,
};

// Generate random string for state parameter
function generateRandomString(length) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

// Build authorization URL
export function buildAuthUrl(redirectUri) {
  const state = generateRandomString(32);
  sessionStorage.setItem('oauth_state', state);

  const params = new URLSearchParams({
    client_id: keycloakConfig.clientId,
    redirect_uri: redirectUri,
    response_type: 'code',
    scope: 'openid profile email',
    state: state,
  });

  return `${authUrls.authorize}?${params.toString()}`;
}

// Build logout URL
export function buildLogoutUrl(redirectUri, idToken) {
  const params = new URLSearchParams({
    client_id: keycloakConfig.clientId,
    post_logout_redirect_uri: redirectUri,
  });

  if (idToken) {
    params.append('id_token_hint', idToken);
  }

  return `${authUrls.logout}?${params.toString()}`;
}