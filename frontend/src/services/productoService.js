import api from './api';

export const productoService = {
  // Crear un producto
  async crear(producto) {
    const response = await api.post('/productos', producto);
    return response.data;
  },

  // Crear múltiples productos
  async crearVarios(productos) {
    const response = await api.post('/productos/bulk', productos);
    return response.data;
  },

  // Obtener un producto por ID
  async obtenerPorId(id) {
    const response = await api.get(`/productos/${id}`);
    return response.data;
  },

  // Actualizar atributos (PATCH)
  async actualizarAtributos(id, cambios) {
    const response = await api.patch(`/productos/${id}`, cambios);
    return response.data;
  },

  // Actualizar precio
  async actualizarPrecio(id, precio) {
    const response = await api.put(`/productos/${id}/precio`, { precio });
    return response.data;
  },

  // Actualizar descripción
  async actualizarDescripcion(id, descripcion) {
    const response = await api.put(`/productos/${id}/descripcion`, { descripcion });
    return response.data;
  },

  // Listar todos (si existe endpoint)
  async listarTodos() {
    const response = await api.get('/productos');
    return response.data;
  }
};