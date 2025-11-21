import api from './api';

export const creacionService = {
  async obtenerPorId(id) {
    const response = await api.get(`/creaciones/${id}`);
    return response.data;
  }
};