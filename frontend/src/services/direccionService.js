import api from './api';

export const direccionService = {
  // Listar todas las direcciones del usuario autenticado
  async listarMisDirecciones(userId) {
    const response = await api.get(`/usuarios/${userId}/direcciones`);
    return response.data;
  },

  // Crear nueva dirección
  async crearDireccion(userId, direccionData) {
    const response = await api.post(`/usuarios/${userId}/direcciones`, direccionData);
    return response.data;
  },

  // Marcar dirección existente como principal
  async marcarComoPrincipal(userId, direccionId) {
    const response = await api.patch(
      `/usuarios/${userId}/direcciones/${direccionId}/principal`
    );
    return response.data;
  },

  // Eliminar dirección
  async eliminarDireccion(userId, direccionId) {
    await api.delete(`/usuarios/${userId}/direcciones/${direccionId}`);
  }
};
