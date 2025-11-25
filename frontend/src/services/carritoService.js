import api from './api';

export const carritoService = {
  async obtenerCarrito(usuarioId) {
    const response = await api.get(`/carrito?usuarioId=${usuarioId}`);
    return response.data;
  },

  async agregarProducto(productoId, usuarioId, cantidad = 1) {
    const response = await api.post(`/carrito/productos/${productoId}`, {
      usuarioId: usuarioId,
      cantidad: cantidad || 1
    });
    return response.data;
  },

  async agregarCreacion(creacionId, usuarioId, cantidad = 1) {
    const response = await api.post(`/carrito/creaciones/${creacionId}`, {
      usuarioId: usuarioId,
      cantidad: cantidad || 1
    });
    return response.data;
  },

  async actualizarCantidad(itemId, usuarioId, nuevaCantidad) {
    const response = await api.put(`/carrito/items/${itemId}`, {
      usuarioId: usuarioId,
      nuevaCantidad: nuevaCantidad || 1
    });
    return response.data;
  },

  async eliminarItem(itemId, usuarioId) {
    const response = await api.delete(`/carrito/items/${itemId}?usuarioId=${usuarioId}`);
    return response.data;
  },

  async vaciarCarrito(usuarioId) {
    await api.delete(`/carrito/vaciar?usuarioId=${usuarioId}`);
  }
};