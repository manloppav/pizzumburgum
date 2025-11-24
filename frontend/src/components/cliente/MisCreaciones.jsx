import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Badge, Alert, Spinner, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { creacionService } from '../../services/creacionService';
import { ModalDetalleCreacion } from '../common/ModalDetalleCreacion';

export const MisCreaciones = () => {
  const [creaciones, setCreaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showDetalle, setShowDetalle] = useState(false);
  const [creacionSeleccionada, setCreacionSeleccionada] = useState(null);

  useEffect(() => {
    cargarCreaciones();
  }, []);

  const cargarCreaciones = async () => {
    try {
      setLoading(true);
      const data = await creacionService.listarMisCreaciones();
      setCreaciones(data);
    } catch (err) {
      setError('Error al cargar tus creaciones');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const verDetalle = (id) => {
    setCreacionSeleccionada(id);
    setShowDetalle(true);
  };

  const formatearPrecio = (precio) => {
    return new Intl.NumberFormat('es-UY', {
      style: 'currency',
      currency: 'UYU'
    }).format(precio);
  };

  const getCategoriaInfo = (categoria) => {
    if (categoria === 'PIZZA_BASE') {
      return { texto: 'Pizza', color: 'danger', icon: 'bi-circle-fill' };
    }
    return { texto: 'Hamburguesa', color: 'warning', icon: 'bi-circle-fill' };
  };

  if (loading) {
    return (
      <Container className="py-5">
        <div className="text-center">
          <Spinner animation="border" variant="primary" />
          <p className="mt-3 text-muted">Cargando tus creaciones...</p>
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
              <div className="d-flex justify-content-between align-items-center">
                <h3 className="mb-0">
                  <i className="bi bi-star-fill me-2"></i>
                  Mis Creaciones
                </h3>
                <Button variant="light" as={Link} to="/crear-creacion">
                  <i className="bi bi-plus-circle me-1"></i>
                  Nueva Creación
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

              {creaciones.length === 0 ? (
                <Alert variant="info" className="text-center py-5">
                  <i className="bi bi-info-circle" style={{ fontSize: '3rem' }}></i>
                  <h5 className="mt-3">Aún no tienes creaciones</h5>
                  <p className="text-muted">¡Crea tu primera pizza o hamburguesa personalizada!</p>
                  <Button variant="primary" as={Link} to="/crear-creacion" className="mt-3">
                    <i className="bi bi-plus-circle me-2"></i>
                    Crear Ahora
                  </Button>
                </Alert>
              ) : (
                <>
                  <p className="text-muted mb-4">
                    Tienes <strong>{creaciones.length}</strong> creacion{creaciones.length !== 1 ? 'es' : ''}
                  </p>

                  <Row>
                    {creaciones.map(creacion => {
                      const catInfo = getCategoriaInfo(creacion.categoriaCreacion);
                      return (
                        <Col key={creacion.id} md={6} lg={4} className="mb-4">
                          <Card className="h-100 shadow-sm hover-shadow" style={{ cursor: 'pointer' }}>
                            {creacion.imagenUrl ? (
                              <Card.Img
                                variant="top"
                                src={creacion.imagenUrl}
                                style={{ height: '200px', objectFit: 'cover' }}
                              />
                            ) : (
                              <div
                                className={`bg-${catInfo.color} d-flex align-items-center justify-content-center`}
                                style={{ height: '200px' }}
                              >
                                <i className={`${catInfo.icon} text-white`} style={{ fontSize: '5rem' }}></i>
                              </div>
                            )}
                            <Card.Body>
                              <div className="d-flex justify-content-between align-items-start mb-2">
                                <h5 className="mb-0">{creacion.nombre}</h5>
                                <Badge bg={catInfo.color}>
                                  {catInfo.texto}
                                </Badge>
                              </div>

                              {creacion.descripcion && (
                                <p className="text-muted small mb-2">
                                  {creacion.descripcion.length > 100
                                    ? creacion.descripcion.substring(0, 100) + '...'
                                    : creacion.descripcion}
                                </p>
                              )}

                              <div className="mb-3">
                                <small className="text-muted">
                                  <i className="bi bi-box me-1"></i>
                                  {creacion.productos?.length || 0} productos
                                </small>
                              </div>

                              <div className="d-flex justify-content-between align-items-center">
                                <h4 className="text-success mb-0">
                                  {formatearPrecio(creacion.precioTotal)}
                                </h4>
                                <Button
                                  variant="outline-primary"
                                  size="sm"
                                  onClick={() => verDetalle(creacion.id)}
                                >
                                  <i className="bi bi-eye me-1"></i>
                                  Ver Detalle
                                </Button>
                              </div>
                            </Card.Body>
                          </Card>
                        </Col>
                      );
                    })}
                  </Row>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <ModalDetalleCreacion
        show={showDetalle}
        onHide={() => setShowDetalle(false)}
        creacionId={creacionSeleccionada}
      />
    </Container>
  );
};