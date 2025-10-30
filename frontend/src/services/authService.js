import api from './api';
import { TOKEN_KEY, USER_KEY } from '../utils/constants';

export const authService = {
  async register(userData) {
    try {
      const response = await api.post('/auth/registro', userData);
      const { token, ...user } = response.data;

      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));

      return response.data;
    } catch (error) {
      throw error.response?.data || { error: 'Error al registrar usuario' };
    }
  },

  async login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials);
      const { token, ...user } = response.data;

      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));

      return response.data;
    } catch (error) {
      throw error.response?.data || { error: 'Error al iniciar sesión' };
    }
  },

  async registerAdmin(userData) {
    try {
      const response = await api.post('/auth/registro-admin', userData);
      return response.data;
    } catch (error) {
      throw error.response?.data || { error: 'Error al registrar administrador' };
    }
  },

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '/login';
  },

  getCurrentUser() {
    const userStr = localStorage.getItem(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  isAuthenticated() {
    return !!this.getToken();
  },

  isAdmin() {
    const user = this.getCurrentUser();
    return user?.rol === 'ADMIN';
  }
};