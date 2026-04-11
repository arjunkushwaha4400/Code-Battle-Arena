import { authService } from './authService';

const API_BASE_URL = 'http://localhost:8080/api/v1';

class ApiService {
  async request(endpoint, options = {}) {
    const token = await authService.getAccessToken();

    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    console.log("from api " , token)

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Request failed' }));
      throw new Error(error.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    if (!data.success) {
      throw new Error(data.message || 'Request failed');
    }

    return data.data;
  }

  // ============ Auth ============
  async syncUser() {
    return this.request('/auth/sync', { method: 'POST' });
  }

  // ============ Users ============
  async getCurrentUser() {
    return this.request('/users/me');
  }

  async getUserById(id) {
    return this.request(`/users/${id}`);
  }

  async getUserProfile(username) {
    return this.request(`/users/profile/${username}`);
  }

  async updateProfile(data) {
    return this.request('/users/me', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async searchUsers(query, page = 0, size = 20) {
    return this.request(`/users/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
  }

  // ============ Leaderboard ============
  async getLeaderboard(page = 0, size = 20) {
    return this.request(`/leaderboard?page=${page}&size=${size}`);
  }

  async getTopPlayers(count = 10) {
    return this.request(`/leaderboard/top?count=${count}`);
  }

  // ============ Problems ============
  async getProblems(page = 0, size = 20) {
    return this.request(`/problems?page=${page}&size=${size}`);
  }

  async getProblemsByDifficulty(difficulty, page = 0, size = 20) {
    return this.request(`/problems/difficulty/${difficulty}?page=${page}&size=${size}`);
  }

  async getProblemById(id) {
    return this.request(`/problems/${id}`);
  }

  async getRandomProblem(difficulty) {
    const url = difficulty ? `/problems/random?difficulty=${difficulty}` : '/problems/random';
    return this.request(url);
  }

  // ============ Rooms ============
  async createRoom(data) {
    return this.request('/rooms', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async joinRoom(roomCode) {
    return this.request('/rooms/join', {
      method: 'POST',
      body: JSON.stringify({ roomCode }),
    });
  }

  async leaveRoom(roomId) {
    return this.request(`/rooms/${roomId}/leave`, { method: 'POST' });
  }

  async getRoomById(id) {
    return this.request(`/rooms/${id}`);
  }

  async getRoomByCode(roomCode) {
    return this.request(`/rooms/code/${roomCode}`);
  }

  async getMyRooms() {
    return this.request('/rooms/my-rooms');
  }

  // ============ Matchmaking ============
  async joinMatchmaking(data) {
    return this.request('/matchmaking/join', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async leaveMatchmaking() {
    return this.request('/matchmaking/leave', { method: 'POST' });
  }

  async getMatchmakingStatus() {
    return this.request('/matchmaking/status');
  }

  // ============ Submissions ============
  async submitCode(data) {
    return this.request('/submissions', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getSubmissionById(id) {
    return this.request(`/submissions/${id}`);
  }

  async getSubmissionsByRoom(roomId) {
    return this.request(`/submissions/room/${roomId}`);
  }

  // ============ AI ============
  async getHint(data) {
    return this.request('/ai/hints', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }
}

// Export singleton instance
export const apiService = new ApiService();