import React, { useState, useEffect } from 'react';
import {Container, Row, Col, Card, Form, Button, Table, Badge, Alert, Spinner, Modal, InputGroup} from 'react-bootstrap';
import { pedidoService } from '../../services/pedidoService';
import { ModalDetalleCreacion } from '../common/ModalDetalleCreacion';

export const ListarPedidos = () => {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0]);
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [totalPedidos, setTotalPedidos] = useState(0);
  const [filtroEstado, setFiltroEstado] = useState('TODOS');
  const [showDetalles, setShowDetalles] = useState(false);
  const [pedidoSeleccionado, setPedidoSeleccionado] = useState(null);
  const [modoFiltro, setModoFiltro] = useState('fecha');
  const [showCreacion, setShowCreacion] = useState(false);
  const [creacionIdSeleccionada, setCreacionIdSeleccionada] = useState(null);

  useEffect(() => {
    buscarPedidos();
  }, []);

  const buscarPedidos = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await pedidoService.listarPorFecha(fecha);
      setPedidos(response.pedidos);
      setTotalPedidos(response.cantidad);
      setModoFiltro('fecha');
      setFiltroEstado('TODOS');
    } catch (err) {
      setError('Error al cargar pedidos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const buscarPorRango = async () => {
    if (!fechaInicio || !fechaFin) {
      setError('Debes seleccionar ambas fechas');
      return;
    }

    if (new Date(fechaInicio) > new Date(fechaFin)) {
      setError('La fecha de inicio debe ser anterior a la fecha fin');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const pedidos = await pedidoService.listarPorRango(fechaInicio, fechaFin);
      setPedidos(pedidos);
      setTotalPedidos(pedidos.length);
      setModoFiltro('rango');
      setFiltroEstado('TODOS');
    } catch (err) {
      setError('Error al cargar pedidos');
    } finally {
      setLoading(false);
    }
  };

  const buscarTodos = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const pedidos = await pedidoService.listarTodos();
      setPedidos(pedidos);
      setTotalPedidos(pedidos.length);
      setModoFiltro('todos');
      setFiltroEstado('TODOS');
    } catch (err) {
      setError('Error al cargar pedidos');
    } finally {
      setLoading(false);
    }
  };

  const buscarPorEstado = async (estado) => {
    setLoading(true);
    setError('');
    setSuccess('');
    setFiltroEstado(estado);

    try {
      if (estado === 'TODOS') {
        await buscarTodos();
      } else {
        const pedidos = await pedidoService.listarPorEstado(estado);
        setPedidos(pedidos);
        setTotalPedidos(pedidos.length);
      }
    } catch (err) {
      setError('Error al cargar pedidos');
    } finally {
      setLoading(false);
    }
  };

  const cambiarEstado = async (pedidoId, nuevoEstado) => {
    try {
      await pedidoService.cambiarEstado(pedidoId, nuevoEstado);
      setSuccess(`Estado del pedido #${pedidoId} actualizado correctamente`);

      // Recargar según el filtro activo
      if (modoFiltro === 'fecha') {
        await buscarPedidos();
      } else if (modoFiltro === 'rango') {
        await buscarPorRango();
      } else {
        await buscarTodos();
      }

      // Auto-ocultar mensaje de éxito
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Error al cambiar estado del pedido');
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
      month: '2-digit',
      day: '2-digit',
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

  const calcularTotalVentas = () => {
    return pedidos.reduce((total, pedido) => total + parseFloat(pedido.precioTotal), 0);
  };

  const contarPorEstado = (estado) => {
    return pedidos.filter((p) => p.estado === estado).length;
  };

  const verDetalleCreacion = (creacionId) => {
    setCreacionIdSeleccionada(creacionId);
    setShowCreacion(true);
  };

  return (
    <Container fluid className="py-4">
      <Row>
        <Col>
          <Card className="shadow-lg">
            <Card.Header className="bg-primary text-white py-3">
              <h2 className="mb-0">
                <i className="bi bi-clipboard-data-fill me-2"></i>
                Gestión de Pedidos
              </h2>
            </Card.Header>
            <Card.Body className="p-4">
              {/* Alertas */}
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

              {/* Panel de filtros */}
              <Card className="mb-4 border-primary">
                <Card.Header className="bg-light">
                  <h5 className="mb-0">
                    <i className="bi bi-funnel-fill me-2"></i>
                    Filtros de Búsqueda
                  </h5>
                </Card.Header>
                <Card.Body>
                  <Row className="g-3">
                    {/* Buscar por fecha única */}
                    <Col md={3}>
                      <Form.Label>
                        <i className="bi bi-calendar-date me-1"></i>
                        Buscar por fecha
                      </Form.Label>
                      <InputGroup>
                        <Form.Control
                          type="date"
                          value={fecha}
                          onChange={(e) => setFecha(e.target.value)}
                        />
                        <Button variant="primary" onClick={buscarPedidos}>
                          <i className="bi bi-search"></i>
                        </Button>
                      </InputGroup>
                    </Col>

                    {/* Buscar por rango */}
                    <Col md={4}>
                      <Form.Label>
                        <i className="bi bi-calendar-range me-1"></i>
                        Buscar por rango
                      </Form.Label>
                      <InputGroup>
                        <Form.Control
                          type="date"
                          placeholder="Desde"
                          value={fechaInicio}
                          onChange={(e) => setFechaInicio(e.target.value)}
                        />
                        <Form.Control
                          type="date"
                          placeholder="Hasta"
                          value={fechaFin}
                          onChange={(e) => setFechaFin(e.target.value)}
                        />
                        <Button variant="primary" onClick={buscarPorRango}>
                          <i className="bi bi-search"></i>
                        </Button>
                      </InputGroup>
                    </Col>

                    {/* Filtrar por estado */}
                    <Col md={2}>
                      <Form.Label>
                        <i className="bi bi-filter-circle me-1"></i>
                        Estado
                      </Form.Label>
                      <Form.Select
                        value={filtroEstado}
                        onChange={(e) => buscarPorEstado(e.target.value)}
                      >
                        <option value="TODOS">Todos</option>
                        <option value="PENDIENTE">Pendiente</option>
                        <option value="PREPARACION">Preparación</option>
                        <option value="EN_CAMINO">En Camino</option>
                        <option value="ENTREGADO">Entregado</option>
                      </Form.Select>
                    </Col>

                    {/* Botón ver todos */}
                    <Col md={2}>
                      <Form.Label className="d-block">&nbsp;</Form.Label>
                      <Button
                        variant="outline-primary"
                        onClick={buscarTodos}
                        className="w-100"
                      >
                        <i className="bi bi-list-ul me-1"></i>
                        Ver Todos
                      </Button>
                    </Col>

                    {/* Botón refrescar */}
                    <Col md={1}>
                      <Form.Label className="d-block">&nbsp;</Form.Label>
                      <Button
                        variant="outline-secondary"
                        onClick={() => {
                          if (modoFiltro === 'fecha') buscarPedidos();
                          else if (modoFiltro === 'rango') buscarPorRango();
                          else buscarTodos();
                        }}
                        className="w-100"
                      >
                        <i className="bi bi-arrow-clockwise"></i>
                      </Button>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>

              {/* Estadísticas */}
              <Row className="mb-4">
                <Col md={3}>
                  <Card className="text-center border-primary">
                    <Card.Body>
                      <i className="bi bi-receipt text-primary" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Total Pedidos</h6>
                      <h3 className="text-primary mb-0">{totalPedidos}</h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-success">
                    <Card.Body>
                      <i className="bi bi-cash-coin text-success" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Total Ventas</h6>
                      <h3 className="text-success mb-0">
                        {formatearPrecio(calcularTotalVentas())}
                      </h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-warning">
                    <Card.Body>
                      <i className="bi bi-clock text-warning" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Pendientes</h6>
                      <h3 className="text-warning mb-0">{contarPorEstado('PENDIENTE')}</h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-info">
                    <Card.Body>
                      <i className="bi bi-truck text-info" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">En Camino</h6>
                      <h3 className="text-info mb-0">{contarPorEstado('EN_CAMINO')}</h3>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

              {/* Tabla de pedidos */}
              {loading ? (
                <div className="text-center py-5">
                  <Spinner
                    animation="border"
                    variant="primary"
                    role="status"
                    style={{ width: '3rem', height: '3rem' }}
                  >
                    <span className="visually-hidden">Cargando...</span>
                  </Spinner>
                  <p className="mt-3 text-muted">Cargando pedidos...</p>
                </div>
              ) : pedidos.length === 0 ? (
                <Alert variant="info" className="text-center py-5">
                  <i className="bi bi-info-circle" style={{ fontSize: '3rem' }}></i>
                  <h5 className="mt-3">No se encontraron pedidos</h5>
                  <p className="text-muted">Intenta con otros filtros de búsqueda</p>
                </Alert>
              ) : (
                <div className="table-responsive">
                  <Table striped bordered hover>
                    <thead className="table-dark">
                      <tr>
                        <th style={{ width: '70px' }} className="text-center">
                          ID
                        </th>
                        <th style={{ width: '180px' }}>Fecha/Hora</th>
                        <th>Cliente</th>
                        <th>Dirección</th>
                        <th style={{ width: '120px' }} className="text-end">
                          Total
                        </th>
                        <th style={{ width: '140px' }} className="text-center">
                          Estado
                        </th>
                        <th style={{ width: '200px' }} className="text-center">
                          Acciones
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {pedidos.map((pedido) => {
                        const estadoBadge = getEstadoBadge(pedido.estado);
                        return (
                          <tr key={pedido.id}>
                            <td className="text-center align-middle">
                              <strong className="text-primary">#{pedido.id}</strong>
                            </td>
                            <td className="align-middle">
                              <small>
                                <i className="bi bi-calendar3 me-1 text-muted"></i>
                                {formatearFecha(pedido.fechaHora)}
                              </small>
                            </td>
                            <td className="align-middle">
                              <div>
                                <strong>{pedido.nombreCliente}</strong>
                              </div>
                              <small className="text-muted">
                                <i className="bi bi-envelope me-1"></i>
                                {pedido.emailCliente}
                              </small>
                            </td>
                            <td className="align-middle">
                              <small>
                                <i className="bi bi-geo-alt-fill me-1 text-muted"></i>
                                {pedido.direccionEntrega}
                              </small>
                            </td>
                            <td className="text-end align-middle">
                              <strong className="text-success">
                                {formatearPrecio(pedido.precioTotal)}
                              </strong>
                            </td>
                            <td className="text-center align-middle">
                              <Badge bg={estadoBadge.variant} className="w-100 py-2">
                                <i className={`bi bi-${estadoBadge.icon} me-1`}></i>
                                {estadoBadge.texto}
                              </Badge>
                            </td>
                            <td className="align-middle">
                              <div className="d-flex gap-2">
                                <Form.Select
                                  size="sm"
                                  value={pedido.estado}
                                  onChange={(e) => cambiarEstado(pedido.id, e.target.value)}
                                  style={{ width: '140px' }}
                                >
                                  <option value="PENDIENTE">Pendiente</option>
                                  <option value="PREPARACION">Preparación</option>
                                  <option value="EN_CAMINO">En Camino</option>
                                  <option value="ENTREGADO">Entregado</option>
                                </Form.Select>
                                <Button
                                  size="sm"
                                  variant="outline-info"
                                  onClick={() => verDetalles(pedido)}
                                  title="Ver detalles"
                                >
                                  <i className="bi bi-eye"></i>
                                </Button>
                              </div>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal de detalles */}
      <Modal show={showDetalles} onHide={() => setShowDetalles(false)} size="lg" centered>
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className="bi bi-receipt-cutoff me-2"></i>
            Detalles del Pedido #{pedidoSeleccionado?.id}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {pedidoSeleccionado && (
            <>
              {/* Información del cliente y pedido */}
              <Row className="mb-4">
                <Col md={6}>
                  <Card className="border-primary">
                    <Card.Body>
                      <h6 className="text-primary mb-3">
                        <i className="bi bi-person-circle me-2"></i>
                        Información del Cliente
                      </h6>
                      <p className="mb-1">
                        <strong>Nombre:</strong> {pedidoSeleccionado.nombreCliente}
                      </p>
                      <p className="mb-0">
                        <strong>Email:</strong> {pedidoSeleccionado.emailCliente}
                      </p>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={6}>
                  <Card className="border-info">
                    <Card.Body>
                      <h6 className="text-info mb-3">
                        <i className="bi bi-info-circle me-2"></i>
                        Información del Pedido
                      </h6>
                      <p className="mb-1">
                        <strong>Fecha:</strong> {formatearFecha(pedidoSeleccionado.fechaHora)}
                      </p>
                      <p className="mb-0">
                        <strong>Estado:</strong>{' '}
                        <Badge bg={getEstadoBadge(pedidoSeleccionado.estado).variant}>
                          <i
                            className={`bi bi-${getEstadoBadge(pedidoSeleccionado.estado).icon} me-1`}
                          ></i>
                          {getEstadoBadge(pedidoSeleccionado.estado).texto}
                        </Badge>
                      </p>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

              {/* Dirección de entrega */}
              <Card className="mb-3 border-success">
                <Card.Body>
                  <h6 className="text-success mb-2">
                    <i className="bi bi-geo-alt-fill me-2"></i>
                    Dirección de Entrega
                  </h6>
                  <p className="mb-0">{pedidoSeleccionado.direccionEntrega}</p>
                </Card.Body>
              </Card>

              {/* Observaciones */}
              {pedidoSeleccionado.observaciones && (
                <Card className="mb-3 border-warning">
                  <Card.Body>
                    <h6 className="text-warning mb-2">
                      <i className="bi bi-chat-left-text me-2"></i>
                      Observaciones del Cliente
                    </h6>
                    <p className="mb-0 text-muted">{pedidoSeleccionado.observaciones}</p>
                  </Card.Body>
                </Card>
              )}

              <hr className="my-4" />

              {/* Items del pedido */}
              <h5 className="mb-3">
                <i className="bi bi-cart3 me-2"></i>
                Detalle de Items
              </h5>

              {pedidoSeleccionado.items && pedidoSeleccionado.items.length > 0 ? (
                <>
                  <Table bordered hover>
                    <thead className="table-light">
                      <tr>
                        <th style={{ width: '50px' }} className="text-center">
                          #
                        </th>
                        <th>Item</th>
                        <th style={{ width: '100px' }} className="text-center">
                          Tipo
                        </th>
                        <th style={{ width: '100px' }} className="text-center">
                          Cantidad
                        </th>
                        <th style={{ width: '130px' }} className="text-end">
                          Subtotal
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {pedidoSeleccionado.items.map((item, index) => (
                        <tr key={item.id}>
                          {/* Columna # */}
                          <td className="text-center align-middle">{index + 1}</td>

                          {/* Item: icono, nombre y botón de creación */}
                          <td className="align-middle">
                            <div className="d-flex align-items-center">
                              <div className="bg-light rounded-circle p-2 me-2">
                                <i
                                  className={`bi bi-${
                                    item.tipo === 'PRODUCTO' ? 'box' : 'star'
                                  }-fill`}
                                ></i>
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
                          <td className="text-center align-middle">
                            <Badge bg={item.tipo === 'PRODUCTO' ? 'primary' : 'success'}>
                              {item.tipo}
                            </Badge>
                          </td>

                          {/* Cantidad */}
                          <td className="text-center align-middle">
                            <strong>{item.cantidad}</strong>
                          </td>

                          {/* Subtotal */}
                          <td className="text-end align-middle">
                            {formatearPrecio(item.subtotal)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr className="table-success">
                        <td colSpan="4" className="text-end align-middle">
                          <strong>
                            <i className="bi bi-cash-coin me-2"></i>
                            TOTAL DEL PEDIDO:
                          </strong>
                        </td>
                        <td className="text-end align-middle">
                          <h4 className="mb-0 text-success">
                            {formatearPrecio(pedidoSeleccionado.precioTotal)}
                          </h4>
                        </td>
                      </tr>
                    </tfoot>
                  </Table>

                  {/* Resumen estadístico */}
                  <Row className="mt-3">
                    <Col xs={4}>
                      <Card className="bg-light text-center">
                        <Card.Body>
                          <small className="text-muted">Total Items</small>
                          <h4 className="mb-0">
                            {pedidoSeleccionado.items.reduce(
                              (sum, item) => sum + item.cantidad,
                              0
                            )}
                          </h4>
                        </Card.Body>
                      </Card>
                    </Col>
                    <Col xs={4}>
                      <Card className="bg-light text-center">
                        <Card.Body>
                          <small className="text-muted">Productos</small>
                          <h4 className="mb-0">
                            {pedidoSeleccionado.items.filter(
                              (i) => i.tipo === 'PRODUCTO'
                            ).length}
                          </h4>
                        </Card.Body>
                      </Card>
                    </Col>
                    <Col xs={4}>
                      <Card className="bg-light text-center">
                        <Card.Body>
                          <small className="text-muted">Creaciones</small>
                          <h4 className="mb-0">
                            {pedidoSeleccionado.items.filter(
                              (i) => i.tipo === 'CREACIÓN'
                            ).length}
                          </h4>
                        </Card.Body>
                      </Card>
                    </Col>
                  </Row>
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

      {/* Modal de detalles de creación */}
      <ModalDetalleCreacion
        show={showCreacion}
        onHide={() => setShowCreacion(false)}
        creacionId={creacionIdSeleccionada}
      />
    </Container>
  );
};
