import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Badge, Alert, Spinner, Button, Modal } from 'react-bootstrap';
import { pedidoService } from '../../services/pedidoService';
import { ModalDetalleCreacion } from '../common/ModalDetalleCreacion';

export const MisPedidos = () => {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showDetalles, setShowDetalles] = useState(false);
  const [pedidoSeleccionado, setPedidoSeleccionado] = useState(null);
  const [showCreacion, setShowCreacion] = useState(false);
  const [creacionIdSeleccionada, setCreacionIdSeleccionada] = useState(null);


  useEffect(() => {
    cargarPedidos();
  }, []);

  const cargarPedidos = async () => {
    try {
      setLoading(true);
      const data = await pedidoService.listarMisPedidos();
      setPedidos(data);
    } catch (err) {
      setError('Error al cargar tus pedidos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const verDetalles = (pedido) => {
    setPedidoSeleccionado(pedido);
    setShowDetalles(true);
  };

  const getEstadoBadge = (estado) => {
    const badges = {
      PENDIENTE: { variant: 'warning', texto: 'Pendiente', icon: 'clock' },
      PREPARACION: { variant: 'info', texto: 'En Preparación', icon: 'fire' },
      EN_CAMINO: { variant: 'primary', texto: 'En Camino', icon: 'truck' },
      ENTREGADO: { variant: 'success', texto: 'Entregado', icon: 'check-circle' }
    };
    return badges[estado] || { variant: 'secondary', texto: estado, icon: 'circle' };
  };

  const formatearFecha = (fecha) => {
    return new Date(fecha).toLocaleString('es-UY', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatearPrecio = (precio) => {
    return new Intl.NumberFormat('es-UY', {
      style: 'currency',
      currency: 'UYU'
    }).format(precio);
  };

  const verDetalleCreacion = (creacionId) => {
    setCreacionIdSeleccionada(creacionId);
    setShowCreacion(true);
  };

  if (loading) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" variant="primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </Spinner>
          <p className="mt-3 text-muted">Cargando tus pedidos...</p>
        </div>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row>
        <Col>
          <Card className="shadow">
            <Card.Header className="bg-primary text-white">
              <h3 className="mb-0">
                <i className="bi bi-bag-check-fill me-2"></i>
                Mis Pedidos
              </h3>
            </Card.Header>
            <Card.Body>
              {error && (
                <Alert variant="danger" dismissible onClose={() => setError('')}>
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {error}
                </Alert>
              )}

              {pedidos.length === 0 ? (
                <Alert variant="info" className="text-center py-5">
                  <i className="bi bi-info-circle me-2" style={{ fontSize: '2rem' }}></i>
                  <h5 className="mt-3">Aún no tienes pedidos realizados</h5>
                  <p className="text-muted">Cuando realices tu primer pedido, aparecerá aquí.</p>
                </Alert>
              ) : (
                <>
                  <div className="d-flex justify-content-between align-items-center mb-4">
                    <p className="text-muted mb-0">
                      <i className="bi bi-list-ul me-2"></i>
                      Tienes <strong>{pedidos.length}</strong> pedido{pedidos.length !== 1 ? 's' : ''} registrado{pedidos.length !== 1 ? 's' : ''}
                    </p>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      onClick={cargarPedidos}
                    >
                      <i className="bi bi-arrow-clockwise me-1"></i>
                      Actualizar
                    </Button>
                  </div>

                  {/* Vista Desktop */}
                  <div className="d-none d-md-block">
                    <Table striped bordered hover responsive>
                      <thead className="table-dark">
                        <tr>
                          <th>Fecha</th>
                          <th>Dirección</th>
                          <th style={{ width: '120px' }}>Total</th>
                          <th style={{ width: '150px' }}>Estado</th>
                          <th style={{ width: '120px' }}>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {pedidos.map((pedido) => {
                          const estadoBadge = getEstadoBadge(pedido.estado);
                          return (
                            <tr key={pedido.id}>
                              <td>
                                <i className="bi bi-calendar3 me-2 text-muted"></i>
                                {formatearFecha(pedido.fechaHora)}
                              </td>
                              <td>
                                <i className="bi bi-geo-alt-fill me-2 text-muted"></i>
                                <small>{pedido.direccionEntrega}</small>
                              </td>
                              <td className="text-end">
                                <strong className="text-success">
                                  {formatearPrecio(pedido.precioTotal)}
                                </strong>
                              </td>
                              <td>
                                <Badge bg={estadoBadge.variant} className="w-100 py-2">
                                  <i className={`bi bi-${estadoBadge.icon} me-1`}></i>
                                  {estadoBadge.texto}
                                </Badge>
                              </td>
                              <td className="text-center">
                                <Button
                                  variant="outline-info"
                                  size="sm"
                                  onClick={() => verDetalles(pedido)}
                                >
                                  <i className="bi bi-eye me-1"></i>
                                  Ver
                                </Button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </Table>
                  </div>

                  {/* Vista Mobile */}
                  <div className="d-md-none">
                    {pedidos.map((pedido) => {
                      const estadoBadge = getEstadoBadge(pedido.estado);
                      return (
                        <Card key={pedido.id} className="mb-3 shadow-sm">
                          <Card.Body>
                            <div className="d-flex justify-content-between align-items-start mb-3">
                              <h5 className="mb-0">
                                <i className="bi bi-receipt me-2"></i>
                                Pedido #{pedido.id}
                              </h5>
                              <Badge bg={estadoBadge.variant} pill>
                                <i className={`bi bi-${estadoBadge.icon} me-1`}></i>
                                {estadoBadge.texto}
                              </Badge>
                            </div>

                            <div className="mb-2">
                              <small className="text-muted">
                                <i className="bi bi-calendar3 me-1"></i>
                                {formatearFecha(pedido.fechaHora)}
                              </small>
                            </div>

                            <div className="mb-2">
                              <small>
                                <i className="bi bi-geo-alt-fill me-1 text-muted"></i>
                                {pedido.direccionEntrega}
                              </small>
                            </div>

                            {pedido.observaciones && (
                              <div className="mb-2">
                                <small className="text-muted">
                                  <i className="bi bi-chat-left-text me-1"></i>
                                  {pedido.observaciones}
                                </small>
                              </div>
                            )}

                            <hr />

                            <div className="d-flex justify-content-between align-items-center">
                              <div>
                                <small className="text-muted d-block">Total:</small>
                                <h5 className="mb-0 text-success">
                                  {formatearPrecio(pedido.precioTotal)}
                                </h5>
                              </div>
                              <Button
                                variant="outline-info"
                                size="sm"
                                onClick={() => verDetalles(pedido)}
                              >
                                <i className="bi bi-eye me-1"></i>
                                Ver Detalles
                              </Button>
                            </div>
                          </Card.Body>
                        </Card>
                      );
                    })}
                  </div>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal de detalles */}
      <Modal show={showDetalles} onHide={() => setShowDetalles(false)} size="lg" centered>
        <Modal.Header closeButton className="bg-light">
          <Modal.Title>
            <i className="bi bi-receipt me-2"></i>
            Detalles del Pedido #{pedidoSeleccionado?.id}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {pedidoSeleccionado && (
            <>
              {/* Información general */}
              <Row className="mb-4">
                <Col md={6}>
                  <Card className="bg-light border-0">
                    <Card.Body>
                      <h6 className="text-muted mb-2">
                        <i className="bi bi-calendar3 me-2"></i>
                        Fecha del pedido
                      </h6>
                      <p className="mb-0">{formatearFecha(pedidoSeleccionado.fechaHora)}</p>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6}>
                  <Card className="bg-light border-0">
                    <Card.Body>
                      <h6 className="text-muted mb-2">
                        <i className="bi bi-info-circle me-2"></i>
                        Estado actual
                      </h6>
                      <Badge
                        bg={getEstadoBadge(pedidoSeleccionado.estado).variant}
                        className="py-2 px-3"
                      >
                        <i className={`bi bi-${getEstadoBadge(pedidoSeleccionado.estado).icon} me-1`}></i>
                        {getEstadoBadge(pedidoSeleccionado.estado).texto}
                      </Badge>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

              {/* Dirección de entrega */}
              <Card className="mb-3 border-primary">
                <Card.Body>
                  <h6 className="text-primary mb-2">
                    <i className="bi bi-geo-alt-fill me-2"></i>
                    Dirección de entrega
                  </h6>
                  <p className="mb-0">{pedidoSeleccionado.direccionEntrega}</p>
                </Card.Body>
              </Card>

              {/* Observaciones */}
              {pedidoSeleccionado.observaciones && (
                <Card className="mb-3 border-info">
                  <Card.Body>
                    <h6 className="text-info mb-2">
                      <i className="bi bi-chat-left-text me-2"></i>
                      Observaciones
                    </h6>
                    <p className="mb-0 text-muted">{pedidoSeleccionado.observaciones}</p>
                  </Card.Body>
                </Card>
              )}

              <hr className="my-4" />

              {/* Items del pedido */}
              <h5 className="mb-3">
                <i className="bi bi-cart3 me-2"></i>
                Items del pedido
              </h5>

              {pedidoSeleccionado.items && pedidoSeleccionado.items.length > 0 ? (
                <>
                  <div className="table-responsive">
                    <Table bordered hover>
                      <thead className="table-light">
                        <tr>
                          <th>Item</th>
                          <th style={{ width: '100px' }} className="text-center">Tipo</th>
                          <th style={{ width: '100px' }} className="text-center">Cantidad</th>
                          <th style={{ width: '130px' }} className="text-end">Subtotal</th>
                        </tr>
                      </thead>
                      <tbody>
                        {pedidoSeleccionado.items.map((item, index) => (
                          <tr key={item.id}>

                            {/* Celda del nombre con botón para ver detalles de creación */}
                            <td className="align-middle">
                              <div className="d-flex align-items-center">
                                <div className="bg-light rounded-circle p-2 me-2">
                                  <i className={`bi bi-${item.tipo === 'PRODUCTO' ? 'box' : 'star'}-fill`}></i>
                                </div>

                                <div>
                                  <strong>{item.nombreItem}</strong>

                                  {item.tipo === 'CREACIÓN' && item.creacionId && (
                                    <div>
                                      <Button
                                        variant="link"
                                        size="sm"
                                        className="p-0 text-decoration-none"
                                        onClick={() => verDetalleCreacion(item.creacionId)}
                                      >
                                        <i className="bi bi-eye me-1"></i>
                                        Ver detalles de la creación
                                      </Button>
                                    </div>
                                  )}
                                </div>
                              </div>
                            </td>

                            {/* Tipo */}
                            <td className="text-center">
                              <Badge bg={item.tipo === 'PRODUCTO' ? 'primary' : 'success'}>
                                {item.tipo}
                              </Badge>
                            </td>

                            {/* Cantidad */}
                            <td className="text-center">
                              <strong>{item.cantidad}</strong>
                            </td>

                            {/* Subtotal */}
                            <td className="text-end">
                              {formatearPrecio(item.subtotal)}
                            </td>

                          </tr>
                        ))}
                      </tbody>
                      <tfoot>
                        <tr className="table-success">
                          <td colSpan="3" className="text-end">
                            <strong>
                              <i className="bi bi-cash-coin me-2"></i>
                              TOTAL:
                            </strong>
                          </td>
                          <td className="text-end">
                            <strong style={{ fontSize: '1.2rem' }}>
                              {formatearPrecio(pedidoSeleccionado.precioTotal)}
                            </strong>
                          </td>
                        </tr>
                      </tfoot>
                    </Table>
                  </div>

                  {/* Resumen */}
                  <Card className="bg-light border-0 mt-3">
                    <Card.Body>
                      <Row className="text-center">
                        <Col xs={6}>
                          <h6 className="text-muted mb-1">Total Items</h6>
                          <h4 className="mb-0">
                            {pedidoSeleccionado.items.reduce((sum, item) => sum + item.cantidad, 0)}
                          </h4>
                        </Col>
                        <Col xs={6}>
                          <h6 className="text-muted mb-1">Total a Pagar</h6>
                          <h4 className="mb-0 text-success">
                            {formatearPrecio(pedidoSeleccionado.precioTotal)}
                          </h4>
                        </Col>
                      </Row>
                    </Card.Body>
                  </Card>
                </>
              ) : (
                <Alert variant="warning">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  No hay items registrados en este pedido
                </Alert>
              )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer className="bg-light">
          <Button variant="secondary" onClick={() => setShowDetalles(false)}>
            <i className="bi bi-x-circle me-1"></i>
            Cerrar
          </Button>
        </Modal.Footer>
      </Modal>
      <ModalDetalleCreacion
        show={showCreacion}
        onHide={() => setShowCreacion(false)}
        creacionId={creacionIdSeleccionada}
      />
    </Container>
  );
};