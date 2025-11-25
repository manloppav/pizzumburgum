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
import { useAuth } from '../../context/AuthContext';
import { direccionService } from '../../services/direccionService';

export const MisDirecciones = () => {
  const { user, isAuthenticated, loading: authLoading } = useAuth();

  const [direcciones, setDirecciones] = useState([]);
  const [loading, setLoading] = useState(true);   // loading de datos
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    calle: '',
    numero: '',
    apartamento: '',
    barrio: '',
    principal: false
  });

  // Cargar direcciones cuando:
  // - haya terminado de cargar el AuthContext
  // - el usuario esté autenticado
  useEffect(() => {
    if (authLoading) return;

    if (!isAuthenticated || !user) {
      setLoading(false);
      setError('Debes iniciar sesión para ver tus direcciones');
      return;
    }

    cargarDirecciones();
  }, [authLoading, isAuthenticated, user]);

  const cargarDirecciones = async () => {
    try {
      setLoading(true);
      const data = await direccionService.listarMisDirecciones(user.id);
      setDirecciones(data);
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Error al cargar las direcciones');
    } finally {
      setLoading(false);
    }
  };

  const abrirModalNuevaDireccion = () => {
    setFormData({
      calle: '',
      numero: '',
      apartamento: '',
      barrio: '',
      principal: false
    });
    setShowModal(true);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const guardarDireccion = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError('');

      await direccionService.crearDireccion(user.id, {
        calle: formData.calle,
        numero: parseInt(formData.numero, 10),
        apartamento: formData.apartamento || null,
        barrio: formData.barrio,
        principal: formData.principal
      });

      setSuccess('Dirección agregada exitosamente');
      setShowModal(false);
      await cargarDirecciones();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Error al guardar la dirección');
    } finally {
      setLoading(false);
    }
  };

  const marcarComoPrincipal = async (direccionId) => {
    try {
      setLoading(true);
      setError('');

      await direccionService.marcarComoPrincipal(user.id, direccionId);

      setSuccess('Dirección principal actualizada');
      await cargarDirecciones();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Error al actualizar la dirección principal');
    } finally {
      setLoading(false);
    }
  };

  const eliminarDireccion = async (direccionId, direccionCompleta) => {
    if (!window.confirm(`¿Estás seguro de eliminar la dirección:\n${direccionCompleta}?`)) {
      return;
    }

    try {
      setLoading(true);
      setError('');

      await direccionService.eliminarDireccion(user.id, direccionId);

      setSuccess('Dirección eliminada exitosamente');
      await cargarDirecciones();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Error al eliminar la dirección');
    } finally {
      setLoading(false);
    }
  };

  // Mientras el AuthContext está inicializando
  if (authLoading) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" />
          <p className="mt-3">Verificando sesión...</p>
        </div>
      </Container>
    );
  }

  // Si no está autenticado
  if (!isAuthenticated || !user) {
    return (
      <Container className="py-5">
        <Alert variant="warning" className="text-center">
          Debes iniciar sesión para gestionar tus direcciones.
        </Alert>
      </Container>
    );
  }

  // Cargando direcciones iniciales
  if (loading && direcciones.length === 0) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" variant="primary" />
          <p className="mt-3">Cargando direcciones...</p>
        </div>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row>
        <Col lg={10} className="mx-auto">
          <Card className="shadow">
            <Card.Header className="bg-primary text-white">
              <div className="d-flex justify-content-between align-items-center">
                <h3 className="mb-0">
                  <i className="bi bi-geo-alt-fill me-2"></i>
                  Mis Direcciones
                </h3>
                <Button
                  variant="light"
                  size="sm"
                  onClick={abrirModalNuevaDireccion}
                  disabled={loading}
                >
                  <i className="bi bi-plus-circle me-1"></i>
                  Nueva Dirección
                </Button>
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

              {direcciones.length === 0 ? (
                <Alert variant="info" className="text-center py-4">
                  <i className="bi bi-geo" style={{ fontSize: '3rem' }}></i>
                  <h5 className="mt-3">No tienes direcciones registradas</h5>
                  <p className="text-muted">Agrega tu primera dirección de entrega</p>
                  <Button variant="primary" onClick={abrirModalNuevaDireccion} className="mt-2">
                    <i className="bi bi-plus-circle me-2"></i>
                    Agregar Dirección
                  </Button>
                </Alert>
              ) : (
                <ListGroup variant="flush">
                  {direcciones.map((direccion) => (
                    <ListGroup.Item key={direccion.id} className="px-0 py-3">
                      <Row className="align-items-center">
                        <Col md={8}>
                          <div className="d-flex align-items-start">
                            <i
                              className="bi bi-house-door-fill text-primary me-3"
                              style={{ fontSize: '1.5rem' }}
                            ></i>
                            <div>
                              <h6 className="mb-1">
                                {direccion.direccionCompleta}
                                {direccion.principal && (
                                  <Badge bg="success" className="ms-2">
                                    Principal
                                  </Badge>
                                )}
                              </h6>
                              <small className="text-muted">
                                {direccion.calle} {direccion.numero}
                                {direccion.apartamento && `, Apto ${direccion.apartamento}`}
                              </small>
                            </div>
                          </div>
                        </Col>

                        <Col md={4} className="text-end">
                          {!direccion.principal && (
                            <Button
                              variant="outline-success"
                              size="sm"
                              onClick={() => marcarComoPrincipal(direccion.id)}
                              disabled={loading}
                              className="me-2"
                            >
                              <i className="bi bi-star me-1"></i>
                              Marcar Principal
                            </Button>
                          )}

                          <Button
                            variant="outline-danger"
                            size="sm"
                            onClick={() =>
                              eliminarDireccion(direccion.id, direccion.direccionCompleta)
                            }
                            disabled={loading || direcciones.length === 1}
                            title={
                              direcciones.length === 1
                                ? 'No puedes eliminar tu única dirección'
                                : 'Eliminar dirección'
                            }
                          >
                            <i className="bi bi-trash"></i>
                          </Button>
                        </Col>
                      </Row>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              )}

              {direcciones.length > 0 && (
                <Alert variant="info" className="mt-4 mb-0">
                  <i className="bi bi-info-circle me-2"></i>
                  <strong>Nota:</strong> La dirección marcada como principal se usará por
                  defecto en tus pedidos.
                  {direcciones.length === 1 && ' Debes tener al menos una dirección registrada.'}
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal para agregar nueva dirección */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className="bi bi-plus-circle me-2"></i>
            Nueva Dirección
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={guardarDireccion}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Calle *</Form.Label>
              <Form.Control
                type="text"
                name="calle"
                value={formData.calle}
                onChange={handleInputChange}
                placeholder="Ej: Av. Italia"
                maxLength={200}
                required
              />
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Número *</Form.Label>
                  <Form.Control
                    type="number"
                    name="numero"
                    value={formData.numero}
                    onChange={handleInputChange}
                    placeholder="1234"
                    min="1"
                    required
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Apartamento (opcional)</Form.Label>
                  <Form.Control
                    type="text"
                    name="apartamento"
                    value={formData.apartamento}
                    onChange={handleInputChange}
                    placeholder="Ej: 101"
                    maxLength={100}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Barrio *</Form.Label>
              <Form.Control
                type="text"
                name="barrio"
                value={formData.barrio}
                onChange={handleInputChange}
                placeholder="Ej: Pocitos"
                maxLength={100}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                name="principal"
                label="Marcar como dirección principal"
                checked={formData.principal}
                onChange={handleInputChange}
              />
              <Form.Text className="text-muted">
                Si marcas esta opción, se desmarcará tu dirección principal actual.
              </Form.Text>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)} disabled={loading}>
              Cancelar
            </Button>
            <Button variant="primary" type="submit" disabled={loading}>
              {loading ? (
                <>
                  <Spinner animation="border" size="sm" className="me-2" />
                  Guardando...
                </>
              ) : (
                <>
                  <i className="bi bi-save me-2"></i>
                  Guardar Dirección
                </>
              )}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};