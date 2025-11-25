import api from './api';

export const tarjetaService = {
  // Listar tarjetas del usuario autenticado
  async listarMisTarjetas() {
    const response = await api.get('/tarjetas/mis-tarjetas');
    return response.data;
  },

  // Crear nueva tarjeta
  async crear(tarjetaData) {
    const response = await api.post('/tarjetas', tarjetaData);
    return response.data;
  },

  // Marcar tarjeta como principal
  async marcarComoPrincipal(tarjetaId) {
    const response = await api.patch(`/tarjetas/${tarjetaId}/principal`);
    return response.data;
  },
};