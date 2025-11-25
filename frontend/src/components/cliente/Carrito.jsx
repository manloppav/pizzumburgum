import React, { useState, useEffect } from 'react';
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  ListGroup,
  Badge,
  Alert,
  Modal,
  Form,
  Spinner
} from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { carritoService } from '../../services/carritoService';
import { pedidoService } from '../../services/pedidoService';
import { tarjetaService } from '../../services/tarjetaService';
import { direccionService } from '../../services/direccionService';

export const Carrito = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [carrito, setCarrito] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Modal de confirmación de pedido
  const [showConfirmarPedido, setShowConfirmarPedido] = useState(false);
  const [tarjetas, setTarjetas] = useState([]);
  const [direcciones, setDirecciones] = useState([]);
  const [pedidoData, setPedidoData] = useState({
    tarjetaId: '',
    direccionEntrega: '',
    observaciones: ''
  });

  useEffect(() => {
    if (user?.id) {
      cargarCarrito();
    }
  }, [user]);

  const cargarCarrito = async () => {
    try {
      setLoading(true);
      const data = await carritoService.obtenerCarrito(user.id); // debe devolver CarritoDTO
      setCarrito(data);
    } catch (err) {
      setError('Error al cargar el carrito');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const cargarDatosParaPedido = async () => {
    try {
      const [tarjetasData, direccionesData] = await Promise.all([
        tarjetaService.listarMisTarjetas(user.id),
        direccionService.listarMisDirecciones(user.id)
      ]);

      setTarjetas(tarjetasData);
      setDirecciones(direccionesData);

      const tarjetaPrincipal = tarjetasData.find((t) => t.principal);
      const direccionPrincipal = direccionesData.find((d) => d.principal);

      setPedidoData({
        tarjetaId: tarjetaPrincipal?.id || '',
        direccionEntrega: direccionPrincipal
          ? `${direccionPrincipal.calle} ${direccionPrincipal.numero}, ${direccionPrincipal.barrio}`
          : '',
        observaciones: ''
      });
    } catch (err) {
      setError('Error al cargar datos de pago y entrega');
      console.error(err);
    }
  };

  const actualizarCantidad = async (itemId, nuevaCantidad) => {
    if (nuevaCantidad < 1) return;

    try {
      const carritoActualizado = await carritoService.actualizarCantidad(
        itemId,
        user.id,
        nuevaCantidad
      );
      setCarrito(carritoActualizado);
    } catch (err) {
      setError('Error al actualizar cantidad');
      console.error(err);
    }
  };

  const eliminarItem = async (itemId) => {
    try {
      const carritoActualizado = await carritoService.eliminarItem(itemId, user.id);
      setCarrito(carritoActualizado);
      setSuccess('Item eliminado del carrito');
      setTimeout(() => setSuccess(''), 2000);
    } catch (err) {
      setError('Error al eliminar item');
      console.error(err);
    }
  };

  const vaciarCarrito = async () => {
    if (!window.confirm('¿Estás seguro de vaciar todo el carrito?')) return;

    try {
      await carritoService.vaciarCarrito(user.id);
      await cargarCarrito();
      setSuccess('Carrito vaciado');
      setTimeout(() => setSuccess(''), 2000);
    } catch (err) {
      setError('Error al vaciar carrito');
      console.error(err);
    }
  };

  const iniciarConfirmacionPedido = async () => {
    if (!carrito?.items || carrito.items.length === 0) {
      setError('El carrito está vacío');
      return;
    }

    await cargarDatosParaPedido();
    setShowConfirmarPedido(true);
  };

  const confirmarPedido = async () => {
    try {
      setLoading(true);
      setError('');

      if (!pedidoData.tarjetaId) {
        setError('Debes seleccionar una tarjeta');
        return;
      }

      if (!pedidoData.direccionEntrega) {
        setError('Debes ingresar una dirección de entrega');
        return;
      }

      await pedidoService.crearPedido(pedidoData);

      setSuccess('¡Pedido realizado exitosamente!');
      setShowConfirmarPedido(false);

      setTimeout(() => {
        navigate('/mis-pedidos');
      }, 1500);
    } catch (err) {
      setError(err.response?.data || 'Error al crear el pedido');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatearPrecio = (precio) => {
    return new Intl.NumberFormat('es-UY', {
      style: 'currency',
      currency: 'UYU'
    }).format(precio);
  };

  if (loading && !carrito) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" variant="primary" />
          <p className="mt-3">Cargando carrito...</p>
        </div>
      </Container>
    );
  }

  const carritoVacio = !carrito?.items || carrito.items.length === 0;

  return (
    <Container className="py-5">
      <Row>
        <Col>
          <Card className="shadow">
            <Card.Header className="bg-primary text-white">
              <div className="d-flex justify-content-between align-items-center">
                <h3 className="mb-0">
                  <i className="bi bi-cart3 me-2"></i>
                  Mi Carrito
                </h3>
                {!carritoVacio && (
                  <Button variant="outline-light" size="sm" onClick={vaciarCarrito}>
                    <i className="bi bi-trash me-1"></i>
                    Vaciar
                  </Button>
                )}
              </div>
            </Card.Header>

            <Card.Body className="p-4">
              {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  {error}
                </Alert>
              )}

              {success && (
                <Alert variant="success" dismissible onClose={() => setSuccess('')}>
                  <i className="bi bi-check-circle-fill me-2"></i>
                  {success}
                </Alert>
              )}

              {carritoVacio ? (
                <Alert variant="info" className="text-center py-5">
                  <i className="bi bi-cart-x" style={{ fontSize: '4rem' }}></i>
                  <h4 className="mt-3">Tu carrito está vacío</h4>
                  <p className="text-muted">¡Agrega productos o creaciones para comenzar!</p>
                  <Button
                    variant="primary"
                    onClick={() => navigate('/crear-creacion')}
                    className="mt-3"
                  >
                    <i className="bi bi-plus-circle me-2"></i>
                    Haz una nueva creación
                  </Button>
                </Alert>
              ) : (
                <>
                  {/* LISTADO DE ITEMS */}
                  <ListGroup variant="flush">
                    {carrito.items.map((item) => {
                      const nombreItem =
                        item.productoNombre ??
                        item.creacionNombre ??
                        'Item sin nombre';

                      const esProducto = !!item.productoId;
                      const esCreacion = !!item.creacionId;

                      return (
                        <ListGroup.Item key={item.id} className="px-0 py-3">
                          <Row className="align-items-center">
                            <Col md={6}>
                              <div>
                                <h6 className="mb-1">{nombreItem}</h6>

                                {(esProducto || esCreacion) && (
                                  <Badge bg={esProducto ? 'primary' : 'success'}>
                                    {esProducto ? 'Producto' : 'Creación'}
                                  </Badge>
                                )}
                              </div>
                            </Col>

                            <Col md={2} className="text-center">
                              <small className="text-muted d-block">Precio unitario</small>
                              <strong>{formatearPrecio(item.precioUnitario)}</strong>
                            </Col>

                            <Col md={2}>
                              <div className="d-flex justify-content-center align-items-center gap-2">
                                <Button
                                  variant="outline-secondary"
                                  size="sm"
                                  onClick={() =>
                                    actualizarCantidad(item.id, item.cantidad - 1)
                                  }
                                  disabled={item.cantidad <= 1}
                                >
                                  <i className="bi bi-dash"></i>
                                </Button>
                                <span className="mx-2">
                                  <strong>{item.cantidad}</strong>
                                </span>
                                <Button
                                  variant="outline-secondary"
                                  size="sm"
                                  onClick={() =>
                                    actualizarCantidad(item.id, item.cantidad + 1)
                                  }
                                >
                                  <i className="bi bi-plus"></i>
                                </Button>
                              </div>
                            </Col>

                            <Col md={2} className="text-end">
                              <div>
                                <small className="text-muted d-block">Subtotal</small>
                                <h5 className="text-success mb-0">
                                  {formatearPrecio(item.subtotal)}
                                </h5>
                              </div>
                              <Button
                                variant="link"
                                size="sm"
                                className="text-danger p-0 mt-1"
                                onClick={() => eliminarItem(item.id)}
                              >
                                <i className="bi bi-trash"></i> Eliminar
                              </Button>
                            </Col>
                          </Row>
                        </ListGroup.Item>
                      );
                    })}
                  </ListGroup>

                  <hr className="my-4" />

                  <Row>
                    <Col md={8}>
                      <Alert variant="info">
                        <i className="bi bi-info-circle me-2"></i>
                        <strong>Nota:</strong> Los precios mostrados son los vigentes al
                        momento de agregar al carrito.
                      </Alert>
                    </Col>
                    <Col md={4}>
                      <Card className="bg-light">
                        <Card.Body>
                          <div className="d-flex justify-content-between align-items-center mb-3">
                            <h5 className="mb-0">Total:</h5>
                            <h3 className="mb-0 text-success">
                              {formatearPrecio(carrito.total)}
                            </h3>
                          </div>
                          <Button
                            variant="primary"
                            size="lg"
                            className="w-100"
                            onClick={iniciarConfirmacionPedido}
                          >
                            <i className="bi bi-check-circle me-2"></i>
                            Proceder al Pago
                          </Button>
                        </Card.Body>
                      </Card>
                    </Col>
                  </Row>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal de confirmación de pedido */}
      <Modal
        show={showConfirmarPedido}
        onHide={() => setShowConfirmarPedido(false)}
        size="lg"
        centered
      >
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className="bi bi-credit-card me-2"></i>
            Confirmar Pedido
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-credit-card-fill me-1"></i>
                Método de Pago *
              </Form.Label>
              <Form.Select
                value={pedidoData.tarjetaId}
                onChange={(e) =>
                  setPedidoData({ ...pedidoData, tarjetaId: e.target.value })
                }
                required
              >
                <option value="">Selecciona una tarjeta</option>
                {tarjetas.map((tarjeta) => (
                  <option key={tarjeta.id} value={tarjeta.id}>
                    {tarjeta.tipo} **** {tarjeta.ultimos4Digitos} - {tarjeta.titular}
                    {tarjeta.principal && ' (Principal)'}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-geo-alt-fill me-1"></i>
                Dirección de Entrega *
              </Form.Label>
              <Form.Select
                value={pedidoData.direccionEntrega}
                onChange={(e) =>
                  setPedidoData({ ...pedidoData, direccionEntrega: e.target.value })
                }
                required
              >
                <option value="">Selecciona una dirección</option>
                {direcciones.map((dir) => (
                  <option
                    key={dir.id}
                    value={`${dir.calle} ${dir.numero}, ${dir.barrio}`}
                  >
                    {dir.calle} {dir.numero}, {dir.barrio}
                    {dir.principal && ' (Principal)'}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-chat-left-text me-1"></i>
                Observaciones
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={pedidoData.observaciones}
                onChange={(e) =>
                  setPedidoData({ ...pedidoData, observaciones: e.target.value })
                }
                placeholder="Instrucciones especiales de entrega (opcional)"
                maxLength={1000}
              />
            </Form.Group>

            <Card className="bg-light">
              <Card.Body>
                <h6>Resumen del Pedido:</h6>
                <ul className="mb-0">
                  <li>
                    <strong>Items:</strong> {carrito?.items.length}
                  </li>
                  <li>
                    <strong>Total:</strong>{' '}
                    <span className="text-success">
                      {formatearPrecio(carrito?.total || 0)}
                    </span>
                  </li>
                </ul>
              </Card.Body>
            </Card>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowConfirmarPedido(false)}
            disabled={loading}
          >
            Cancelar
          </Button>
          <Button variant="primary" onClick={confirmarPedido} disabled={loading}>
            {loading ? 'Procesando...' : 'Confirmar Pedido'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};