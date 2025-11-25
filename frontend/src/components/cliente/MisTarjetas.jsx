import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, ListGroup, Badge, Alert, Modal, Form, Spinner } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import { tarjetaService } from '../../services/tarjetaService';

export const MisTarjetas = () => {
  const { user } = useAuth();
  const [tarjetas, setTarjetas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Estado del modal
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    numeroTarjeta: '',
    fechaVencimiento: '',
    cvv: '',
    titular: '',
    tipo: 'VISA',
    principal: false
  });

  useEffect(() => {
    cargarTarjetas();
  }, []);

  const cargarTarjetas = async () => {
    try {
      setLoading(true);
      const data = await tarjetaService.listarMisTarjetas();
      setTarjetas(data);
    } catch (err) {
      setError('Error al cargar las tarjetas');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const abrirModalNuevaTarjeta = () => {
    setFormData({
      numeroTarjeta: '',
      fechaVencimiento: '',
      cvv: '',
      titular: '',
      tipo: 'VISA',
      principal: false
    });
    setShowModal(true);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;

    let newValue = value;

    // Formatear número de tarjeta (solo dígitos, máximo 19)
    if (name === 'numeroTarjeta') {
      newValue = value.replace(/\D/g, '').slice(0, 19);
    }

    // Formatear fecha (MM/YY)
    if (name === 'fechaVencimiento') {
      newValue = value.replace(/\D/g, '');
      if (newValue.length >= 2) {
        newValue = newValue.slice(0, 2) + '/' + newValue.slice(2, 4);
      }
    }

    // Formatear CVV (solo dígitos, máximo 4)
    if (name === 'cvv') {
      newValue = value.replace(/\D/g, '').slice(0, 4);
    }

    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : newValue
    });
  };

  const guardarTarjeta = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError('');

      await tarjetaService.crear({
        numeroTarjeta: formData.numeroTarjeta,
        fechaVencimiento: formData.fechaVencimiento,
        cvv: formData.cvv,
        titular: formData.titular,
        tipo: formData.tipo,
        principal: formData.principal
      });

      setSuccess('Tarjeta agregada exitosamente');
      setShowModal(false);
      await cargarTarjetas();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data || 'Error al guardar la tarjeta');
    } finally {
      setLoading(false);
    }
  };

  const marcarComoPrincipal = async (tarjetaId) => {
    try {
      setLoading(true);
      setError('');

      await tarjetaService.marcarComoPrincipal(tarjetaId);

      setSuccess('Tarjeta principal actualizada');
      await cargarTarjetas();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data || 'Error al actualizar la tarjeta principal');
    } finally {
      setLoading(false);
    }
  };

  const getTipoIcono = (tipo) => {
    const iconos = {
      VISA: '💳',
      MASTERCARD: '💳',
      AMEX: '💳',
      DISCOVER: '💳',
      DINERS: '💳',
      CABAL: '💳',
      OCA: '💳'
    };
    return iconos[tipo] || '💳';
  };

  if (loading && tarjetas.length === 0) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" variant="primary" />
          <p className="mt-3">Cargando tarjetas...</p>
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
                  <i className="bi bi-credit-card-fill me-2"></i>
                  Mis Tarjetas
                </h3>
                <Button
                  variant="light"
                  size="sm"
                  onClick={abrirModalNuevaTarjeta}
                  disabled={loading}
                >
                  <i className="bi bi-plus-circle me-1"></i>
                  Nueva Tarjeta
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

              {tarjetas.length === 0 ? (
                <Alert variant="info" className="text-center py-4">
                  <i className="bi bi-credit-card" style={{ fontSize: '3rem' }}></i>
                  <h5 className="mt-3">No tienes tarjetas registradas</h5>
                  <p className="text-muted">Agrega tu primera tarjeta de pago</p>
                  <Button variant="primary" onClick={abrirModalNuevaTarjeta} className="mt-2">
                    <i className="bi bi-plus-circle me-2"></i>
                    Agregar Tarjeta
                  </Button>
                </Alert>
              ) : (
                <ListGroup variant="flush">
                  {tarjetas.map((tarjeta) => (
                    <ListGroup.Item key={tarjeta.id} className="px-0 py-3">
                      <Row className="align-items-center">
                        <Col md={8}>
                          <div className="d-flex align-items-start">
                            <span className="me-3" style={{ fontSize: '2rem' }}>
                              {getTipoIcono(tarjeta.tipo)}
                            </span>
                            <div>
                              <h6 className="mb-1">
                                {tarjeta.tipo} **** **** **** {tarjeta.ultimos4Digitos}
                                {tarjeta.principal && (
                                  <Badge bg="success" className="ms-2">
                                    Principal
                                  </Badge>
                                )}
                              </h6>
                              <small className="text-muted d-block">
                                {tarjeta.titular}
                              </small>
                              <small className="text-muted">
                                Vence: {tarjeta.fechaVencimiento}
                              </small>
                            </div>
                          </div>
                        </Col>

                        <Col md={4} className="text-end">
                          {!tarjeta.principal && (
                            <Button
                              variant="outline-success"
                              size="sm"
                              onClick={() => marcarComoPrincipal(tarjeta.id)}
                              disabled={loading}
                            >
                              <i className="bi bi-star me-1"></i>
                              Marcar Principal
                            </Button>
                          )}
                        </Col>
                      </Row>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              )}

              {tarjetas.length > 0 && (
                <Alert variant="info" className="mt-4 mb-0">
                  <i className="bi bi-info-circle me-2"></i>
                  <strong>Nota:</strong> La tarjeta marcada como principal se usará por defecto en tus pedidos.
                  {tarjetas.length === 1 && ' Debes tener al menos una tarjeta registrada.'}
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal para agregar nueva tarjeta */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className="bi bi-plus-circle me-2"></i>
            Nueva Tarjeta
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={guardarTarjeta}>
          <Modal.Body>
            <Alert variant="warning" className="small">
              <i className="bi bi-shield-lock me-2"></i>
              Tus datos están protegidos. Solo guardamos los últimos 4 dígitos.
            </Alert>

            <Form.Group className="mb-3">
              <Form.Label>
                Número de Tarjeta *
              </Form.Label>
              <Form.Control
                type="text"
                name="numeroTarjeta"
                value={formData.numeroTarjeta}
                onChange={handleInputChange}
                placeholder="1234 5678 9012 3456"
                pattern="\d{13,19}"
                maxLength={19}
                required
              />
              <Form.Text className="text-muted">
                {formData.numeroTarjeta.length} / 19 dígitos
              </Form.Text>
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    Vencimiento * (MM/YY)
                  </Form.Label>
                  <Form.Control
                    type="text"
                    name="fechaVencimiento"
                    value={formData.fechaVencimiento}
                    onChange={handleInputChange}
                    placeholder="12/25"
                    pattern="^(0[1-9]|1[0-2])/\d{2}$"
                    maxLength={5}
                    required
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    CVV *
                  </Form.Label>
                  <Form.Control
                    type="text"
                    name="cvv"
                    value={formData.cvv}
                    onChange={handleInputChange}
                    placeholder="123"
                    pattern="\d{3,4}"
                    maxLength={4}
                    required
                  />
                  <Form.Text className="text-muted">
                    Código de seguridad
                  </Form.Text>
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>
                Titular *
              </Form.Label>
              <Form.Control
                type="text"
                name="titular"
                value={formData.titular}
                onChange={handleInputChange}
                placeholder="NOMBRE APELLIDO"
                minLength={2}
                maxLength={100}
                style={{ textTransform: 'uppercase' }}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                Tipo de Tarjeta *
              </Form.Label>
              <Form.Select
                name="tipo"
                value={formData.tipo}
                onChange={handleInputChange}
                required
              >
                <option value="VISA">VISA</option>
                <option value="MASTERCARD">MasterCard</option>
                <option value="AMEX">American Express</option>
                <option value="DINERS">Diners Club</option>
                <option value="CABAL">Cabal</option>
                <option value="OCA">OCA</option>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                name="principal"
                label="Marcar como tarjeta principal"
                checked={formData.principal}
                onChange={handleInputChange}
              />
              <Form.Text className="text-muted">
                Si marcas esta opción, se desmarcará tu tarjeta principal actual.
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
                  Guardar Tarjeta
                </>
              )}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};