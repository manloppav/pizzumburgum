import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Badge, Alert, ListGroup, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { creacionService } from '../../services/creacionService';
import { productoService } from '../../services/productoService';

export const CrearCreacion = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [tipoCreacion, setTipoCreacion] = useState(null); // 'PIZZA_BASE' | 'HAMBURGUESA_BASE'
  const [productosSeleccionados, setProductosSeleccionados] = useState([]);

  // NUEVOS STATES PARA DATOS DE CREACIÓN
  const [datosCreacion, setDatosCreacion] = useState({
    nombre: '',
    descripcion: '',
    imagenUrl: ''
  });

  const [mostrarFormularioDatos, setMostrarFormularioDatos] = useState(false);

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const data = await productoService.listarTodos();
      console.log("PRODUCTOS DESDE API:", data);
      setProductos(data);
    } catch (err) {
      setError('Error al cargar productos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const seleccionarTipo = (tipo) => {
    setTipoCreacion(tipo);
    setProductosSeleccionados([]);
    setError('');
  };

  const toggleProducto = (producto) => {
    const yaSeleccionado = productosSeleccionados.find(p => p.id === producto.id);

    if (yaSeleccionado) {
      setProductosSeleccionados(productosSeleccionados.filter(p => p.id !== producto.id));
    } else {
      setProductosSeleccionados([...productosSeleccionados, producto]);
    }
    setError('');
  };

  const obtenerProductosPorCategoria = (categoria) => {
    return productos.filter(p => p.categoria === categoria);
  };

  const contarPorCategoria = (categoria) => {
    return productosSeleccionados.filter(p => p.categoria === categoria).length;
  };

  const validarCreacion = () => {
    const errores = [];

    if (tipoCreacion === 'PIZZA_BASE') {
      const tipoMasa = contarPorCategoria('TIPO_MASA');
      const tamanio = contarPorCategoria('TAMANIO_PIZZA');
      const toppings = contarPorCategoria('TOPPING_PIZZA');

      if (tipoMasa !== 1) errores.push('Debes seleccionar exactamente 1 tipo de masa');
      if (tamanio !== 1) errores.push('Debes seleccionar exactamente 1 tamaño');
      if (toppings > 5) errores.push('Máximo 5 toppings');

    } else if (tipoCreacion === 'HAMBURGUESA_BASE') {
      const tipoPan = contarPorCategoria('TIPO_PAN');
      const carne = contarPorCategoria('TIPO_CARNE');
      const salsa = contarPorCategoria('SALSA_HAMBURGUESA');
      const toppings = contarPorCategoria('TOPPING_HAMBURGUESA');

      if (tipoPan !== 1) errores.push('Debes seleccionar exactamente 1 tipo de pan');
      if (carne > 3) errores.push('Máximo 3 tipos de carne');
      if (salsa > 2) errores.push('Máximo 2 salsas');
      if (toppings > 5) errores.push('Máximo 5 toppings');
    }

    const bebidas = contarPorCategoria('BEBIDA');
    if (bebidas > 1) errores.push('Máximo 1 bebida');

    if (productosSeleccionados.length === 0) {
      errores.push('Debes seleccionar al menos un producto');
    }

    return errores;
  };

  const calcularPrecioTotal = () => {
    return productosSeleccionados.reduce((total, p) => total + parseFloat(p.precio), 0);
  };

  // NUEVO handleCrear: ahora abre el formulario de datos
  const handleCrear = async () => {
    const errores = validarCreacion();

    if (errores.length > 0) {
      setError(errores.join('. '));
      return;
    }

    // En lugar de abrir confirmación directamente, mostrar formulario de datos
    setMostrarFormularioDatos(true);
  };

  // NUEVO confirmarCreacion: guarda usando los datos del formulario
  const confirmarCreacion = async () => {
    try {
      setLoading(true);
      setError('');

      const creacionData = {
        usuarioId: user.id,
        nombre: datosCreacion.nombre.trim() || (tipoCreacion === 'PIZZA_BASE' ? 'Pizza personalizada' : 'Hamburguesa personalizada'),
        descripcion: datosCreacion.descripcion.trim() || null,
        imagenUrl: datosCreacion.imagenUrl.trim() || null,
        categoriaCreacion: tipoCreacion,
        productoIds: productosSeleccionados.map(p => p.id)
      };

      await creacionService.crear(creacionData);
      setSuccess('¡Creación guardada exitosamente!');

      setTimeout(() => {
        navigate('/mis-creaciones');
      }, 1500);

    } catch (err) {
      setError(err.response?.data?.message || 'Error al crear la creación');
      setMostrarFormularioDatos(false);
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

  const getCategoriaInfo = (categoria) => {
    const info = {
      // Pizza
      TIPO_MASA: { label: 'Tipo de Masa', icon: 'bi-circle', color: 'info', obligatorio: true },
      SALSA_PIZZA: { label: 'Salsa', icon: 'bi-droplet-fill', color: 'danger', obligatorio: false },
      TAMANIO_PIZZA: { label: 'Tamaño', icon: 'bi-rulers', color: 'warning', obligatorio: true },
      TOPPING_PIZZA: { label: 'Toppings', icon: 'bi-plus-circle', color: 'success', obligatorio: false },
      // Hamburguesa
      TIPO_PAN: { label: 'Tipo de Pan', icon: 'bi-circle', color: 'warning', obligatorio: true },
      TIPO_CARNE: { label: 'Tipo de Carne', icon: 'bi-egg-fried', color: 'danger', obligatorio: false },
      TIPO_QUESO: { label: 'Tipo de Queso', icon: 'bi-square-fill', color: 'warning', obligatorio: false },
      SALSA_HAMBURGUESA: { label: 'Salsa', icon: 'bi-droplet-fill', color: 'info', obligatorio: false },
      TOPPING_HAMBURGUESA: { label: 'Toppings', icon: 'bi-plus-circle', color: 'success', obligatorio: false },
      // Comunes
      ACOMPANIAMIENTO: { label: 'Acompañamiento', icon: 'bi-basket', color: 'secondary', obligatorio: false },
      BEBIDA: { label: 'Bebida', icon: 'bi-cup-straw', color: 'primary', obligatorio: false }
    };
    return info[categoria] || { label: categoria, icon: 'bi-circle', color: 'secondary', obligatorio: false };
  };

  const getCategoriasPermitidas = () => {
    if (tipoCreacion === 'PIZZA_BASE') {
      return ['TIPO_MASA', 'TAMANIO_PIZZA', 'SALSA_PIZZA', 'TOPPING_PIZZA', 'BEBIDA', 'ACOMPANIAMIENTO'];
    } else if (tipoCreacion === 'HAMBURGUESA_BASE') {
      return ['TIPO_PAN', 'TIPO_CARNE', 'TIPO_QUESO', 'SALSA_HAMBURGUESA', 'TOPPING_HAMBURGUESA', 'BEBIDA', 'ACOMPANIAMIENTO'];
    }
    return [];
  };

  const getLimitesCategoria = (categoria) => {
    if (tipoCreacion === 'PIZZA_BASE') {
      if (categoria === 'TIPO_MASA') return { min: 1, max: 1 };
      if (categoria === 'TAMANIO_PIZZA') return { min: 1, max: 1 };
      if (categoria === 'TOPPING_PIZZA') return { min: 0, max: 5 };
    } else if (tipoCreacion === 'HAMBURGUESA_BASE') {
      if (categoria === 'TIPO_PAN') return { min: 1, max: 1 };
      if (categoria === 'TIPO_CARNE') return { min: 0, max: 3 };
      if (categoria === 'SALSA_HAMBURGUESA') return { min: 0, max: 2 };
      if (categoria === 'TOPPING_HAMBURGUESA') return { min: 0, max: 5 };
    }
    if (categoria === 'BEBIDA') return { min: 0, max: 1 };
    return { min: 0, max: 999 };
  };

  if (!tipoCreacion) {
    return (
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col md={10}>
            <Card className="shadow-lg">
              <Card.Header className="bg-primary text-white text-center py-4">
                <h2 className="mb-0">
                  <i className="bi bi-star-fill me-2"></i>
                  Haz tu propia creación
                </h2>
              </Card.Header>
              <Card.Body className="p-5">
                <h4 className="text-center mb-4">¿Qué deseas crear hoy?</h4>
                <Row className="g-4">
                  <Col md={6}>
                    <Card
                      className="h-100 shadow hover-shadow cursor-pointer"
                      style={{ cursor: 'pointer', transition: 'transform 0.2s' }}
                      onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                      onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                      onClick={() => seleccionarTipo('PIZZA_BASE')}
                    >
                      <Card.Body className="text-center p-5">
                        <i className="bi bi-circle-fill text-danger" style={{ fontSize: '5rem' }}></i>
                        <h3 className="mt-3">Pizza Personalizada</h3>
                        <p className="text-muted">
                          Elige tu masa, tamaño, salsa y hasta 5 toppings
                        </p>
                        <Button variant="danger" size="lg" className="mt-3">
                          <i className="bi bi-arrow-right-circle me-2"></i>
                          Crear Pizza
                        </Button>
                      </Card.Body>
                    </Card>
                  </Col>

                  <Col md={6}>
                    <Card
                      className="h-100 shadow hover-shadow cursor-pointer"
                      style={{ cursor: 'pointer', transition: 'transform 0.2s' }}
                      onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                      onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                      onClick={() => seleccionarTipo('HAMBURGUESA_BASE')}
                    >
                      <Card.Body className="text-center p-5">
                        <i className="bi bi-circle-fill text-warning" style={{ fontSize: '5rem' }}></i>
                        <h3 className="mt-3">Hamburguesa Personalizada</h3>
                        <p className="text-muted">
                          Elige tu pan, hasta 3 carnes, queso y toppings
                        </p>
                        <Button variant="warning" size="lg" className="mt-3">
                          <i className="bi bi-arrow-right-circle me-2"></i>
                          Crear Hamburguesa
                        </Button>
                      </Card.Body>
                    </Card>
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      <Row>
        <Col lg={8}>
          <Card className="shadow mb-4">
            <Card.Header className={`bg-${tipoCreacion === 'PIZZA_BASE' ? 'danger' : 'warning'} text-white`}>
              <div className="d-flex justify-content-between align-items-center">
                <h4 className="mb-0">
                  <i className={`bi bi-circle-fill me-2`}></i>
                  {tipoCreacion === 'PIZZA_BASE' ? 'Crear Pizza Personalizada' : 'Crear Hamburguesa Personalizada'}
                </h4>
                <Button variant="light" size="sm" onClick={() => setTipoCreacion(null)}>
                  <i className="bi bi-arrow-left me-1"></i>
                  Cambiar
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
                <Alert variant="success">
                  <i className="bi bi-check-circle-fill me-2"></i>
                  {success}
                </Alert>
              )}

              {/* Categorías de productos */}
              {getCategoriasPermitidas().map(categoria => {
                const productosCat = obtenerProductosPorCategoria(categoria);
                if (productosCat.length === 0) return null;

                const catInfo = getCategoriaInfo(categoria);
                const limites = getLimitesCategoria(categoria);
                const seleccionados = contarPorCategoria(categoria);

                return (
                  <Card key={categoria} className="mb-3">
                    <Card.Header className="bg-light">
                      <div className="d-flex justify-content-between align-items-center">
                        <h6 className="mb-0">
                          <i className={`${catInfo.icon} text-${catInfo.color} me-2`}></i>
                          {catInfo.label}
                          {catInfo.obligatorio && <Badge bg="danger" className="ms-2">Obligatorio</Badge>}
                        </h6>
                        <Badge bg={seleccionados >= limites.min && seleccionados <= limites.max ? 'success' : 'warning'}>
                          {seleccionados}/{limites.max === 999 ? '∞' : limites.max}
                        </Badge>
                      </div>
                      {limites.min === limites.max && limites.min > 0 && (
                        <small className="text-muted">Selecciona exactamente {limites.min}</small>
                      )}
                      {limites.min !== limites.max && (
                        <small className="text-muted">
                          {limites.min > 0 ? `Mínimo ${limites.min}, ` : ''}
                          Máximo {limites.max}
                        </small>
                      )}
                    </Card.Header>
                    <Card.Body>
                      <Row>
                        {productosCat.map(producto => {
                          const seleccionado = productosSeleccionados.find(p => p.id === producto.id);
                          const limiteAlcanzado = seleccionados >= limites.max && !seleccionado;

                          return (
                            <Col md={6} lg={4} key={producto.id} className="mb-3">
                              <Card
                                className={`h-100 ${seleccionado ? 'border-success border-3' : ''} ${limiteAlcanzado ? 'opacity-50' : ''}`}
                                style={{ cursor: limiteAlcanzado ? 'not-allowed' : 'pointer' }}
                                onClick={() => !limiteAlcanzado && toggleProducto(producto)}
                              >
                                <Card.Body className="p-3">
                                  <div className="d-flex justify-content-between align-items-start mb-2">
                                    <div className="flex-grow-1">
                                      <strong>{producto.nombre}</strong>
                                      {seleccionado && (
                                        <Badge bg="success" className="ms-2">
                                          <i className="bi bi-check-lg"></i>
                                        </Badge>
                                      )}
                                    </div>
                                  </div>
                                  {producto.descripcion && (
                                    <small className="text-muted d-block mb-2">
                                      {producto.descripcion}
                                    </small>
                                  )}
                                  <div className="text-success">
                                    <strong>+{formatearPrecio(producto.precio)}</strong>
                                  </div>
                                </Card.Body>
                              </Card>
                            </Col>
                          );
                        })}
                      </Row>
                    </Card.Body>
                  </Card>
                );
              })}
            </Card.Body>
          </Card>
        </Col>

        {/* Panel lateral - Resumen */}
        <Col lg={4}>
          <Card className="shadow sticky-top" style={{ top: '20px' }}>
            <Card.Header className="bg-primary text-white">
              <h5 className="mb-0">
                <i className="bi bi-cart3 me-2"></i>
                Tu Creación
              </h5>
            </Card.Header>
            <Card.Body>
              {productosSeleccionados.length === 0 ? (
                <Alert variant="info">
                  <i className="bi bi-info-circle me-2"></i>
                  Selecciona productos para armar tu creación
                </Alert>
              ) : (
                <>
                  <ListGroup variant="flush">
                    {productosSeleccionados.map(producto => (
                      <ListGroup.Item key={producto.id} className="px-0">
                        <div className="d-flex justify-content-between align-items-start">
                          <div className="flex-grow-1">
                            <strong>{producto.nombre}</strong>
                            <br />
                            <small className="text-muted">
                              <Badge bg="secondary" className="me-1">
                                {getCategoriaInfo(producto.categoria).label}
                              </Badge>
                            </small>
                          </div>
                          <div className="text-end">
                            <div className="text-success">
                              {formatearPrecio(producto.precio)}
                            </div>
                            <Button
                              variant="link"
                              size="sm"
                              className="text-danger p-0"
                              onClick={() => toggleProducto(producto)}
                            >
                              <i className="bi bi-x-circle"></i>
                            </Button>
                          </div>
                        </div>
                      </ListGroup.Item>
                    ))}
                  </ListGroup>

                  <hr />

                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <h5 className="mb-0">Total:</h5>
                    <h4 className="mb-0 text-success">
                      {formatearPrecio(calcularPrecioTotal())}
                    </h4>
                  </div>

                  <Button
                    variant="primary"
                    size="lg"
                    className="w-100"
                    onClick={handleCrear}
                    disabled={loading}
                  >
                    <i className="bi bi-check-circle me-2"></i>
                    Guardar Creación
                  </Button>

                  <Button
                    variant="outline-danger"
                    size="sm"
                    className="w-100 mt-2"
                    onClick={() => setProductosSeleccionados([])}
                  >
                    <i className="bi bi-trash me-1"></i>
                    Limpiar Todo
                  </Button>
                </>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* MODAL DE DATOS DE LA CREACIÓN (antes se usaba uno de confirmación simple) */}
      <Modal show={mostrarFormularioDatos} onHide={() => setMostrarFormularioDatos(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="bi bi-pencil me-2"></i>
            Detalles de tu Creación
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-tag me-1"></i>
                Nombre de tu creación *
              </Form.Label>
              <Form.Control
                type="text"
                value={datosCreacion.nombre}
                onChange={(e) => setDatosCreacion({ ...datosCreacion, nombre: e.target.value })}
                placeholder={tipoCreacion === 'PIZZA_BASE' ? 'Ej: Mi Pizza Favorita' : 'Ej: Hamburguesa Suprema'}
                maxLength={150}
              />
              <Form.Text className="text-muted">
                Opcional. Si no lo completas, se guardará con un nombre por defecto.
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-chat-left-text me-1"></i>
                Descripción
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={datosCreacion.descripcion}
                onChange={(e) => setDatosCreacion({ ...datosCreacion, descripcion: e.target.value })}
                placeholder="Describe tu creación..."
                maxLength={1000}
              />
              <Form.Text className="text-muted">
                {datosCreacion.descripcion.length}/1000 caracteres (Opcional)
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-image me-1"></i>
                URL de Imagen
              </Form.Label>
              <Form.Control
                type="url"
                value={datosCreacion.imagenUrl}
                onChange={(e) => setDatosCreacion({ ...datosCreacion, imagenUrl: e.target.value })}
                placeholder="https://ejemplo.com/imagen.jpg"
                maxLength={500}
              />
              <Form.Text className="text-muted">
                Opcional. Puedes agregar una imagen personalizada.
              </Form.Text>
            </Form.Group>

            <Card className="bg-light">
              <Card.Body>
                <h6>Resumen:</h6>
                <ul className="mb-0">
                  <li><strong>Tipo:</strong> {tipoCreacion === 'PIZZA_BASE' ? 'Pizza' : 'Hamburguesa'}</li>
                  <li><strong>Productos:</strong> {productosSeleccionados.length}</li>
                  <li><strong>Total:</strong> <span className="text-success">{formatearPrecio(calcularPrecioTotal())}</span></li>
                </ul>
              </Card.Body>
            </Card>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setMostrarFormularioDatos(false)} disabled={loading}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={confirmarCreacion} disabled={loading}>
            {loading ? 'Guardando...' : 'Confirmar y Guardar'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};
