import api from './api';

export const creacionService = {
  async obtenerPorId(id) {
    const response = await api.get(`/creaciones/${id}`);
    return response.data;
  },

  async obtenerDetallada(id) {
    const response = await api.get(`/creaciones/${id}/detallada`);
    return response.data;
  },

  async crear(creacionData) {
    const response = await api.post('/creaciones', creacionData);
    return response.data;
  },

  async listarMisCreaciones() {
    const response = await api.get('/creaciones/mis-creaciones');
    return response.data;
  }
};