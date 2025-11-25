import api from './api';

export const pedidoService = {
  // ============= ENDPOINTS PARA ADMIN =============

  async listarTodos() {
    const response = await api.get('/pedidos/admin/todos');
    return response.data;
  },

  async listarPorFecha(fecha) {
    const response = await api.get(`/pedidos/admin/fecha/${fecha}`);
    return response.data;
  },

  async listarPorRango(fechaInicio, fechaFin) {
    const response = await api.get('/pedidos/admin/rango', {
      params: { fechaInicio, fechaFin }
    });
    return response.data;
  },

  async listarPorEstado(estado) {
    const response = await api.get(`/pedidos/admin/estado/${estado}`);
    return response.data;
  },

  async cambiarEstado(id, nuevoEstado) {
    const response = await api.patch(`/pedidos/admin/${id}/estado`, {
      estado: nuevoEstado
    });
    return response.data;
  },

  // ============= ENDPOINTS PARA CLIENTES =============

  async listarMisPedidos() {
    const response = await api.get('/pedidos/mis-pedidos');
    return response.data;
  },

  async obtenerPorId(id) {
    const response = await api.get(`/pedidos/${id}`);
    return response.data;
  },

  async crearPedido(pedidoData) {
    const response = await api.post('/pedidos/crear', pedidoData);
    return response.data;
  }
};