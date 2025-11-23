import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Table, Badge, Alert, Modal, Form, Tabs, Tab } from 'react-bootstrap';
import { productoService } from '../../services/productoService';

export const GestionProductos = () => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modoModal, setModoModal] = useState('crear'); // 'crear' | 'editar'
  const [productoSeleccionado, setProductoSeleccionado] = useState(null);

  const categoriasProducto = [
    { value: 'TIPO_MASA', label: 'Tipo de Masa', grupo: 'Pizza' },
    { value: 'SALSA_PIZZA', label: 'Salsa Pizza', grupo: 'Pizza' },
    { value: 'TAMANIO_PIZZA', label: 'Tamaño Pizza', grupo: 'Pizza' },
    { value: 'TOPPING_PIZZA', label: 'Topping Pizza', grupo: 'Pizza' },
    { value: 'TIPO_PAN', label: 'Tipo de Pan', grupo: 'Hamburguesa' },
    { value: 'TIPO_CARNE', label: 'Tipo de Carne', grupo: 'Hamburguesa' },
    { value: 'TIPO_QUESO', label: 'Tipo de Queso', grupo: 'Hamburguesa' },
    { value: 'SALSA_HAMBURGUESA', label: 'Salsa Hamburguesa', grupo: 'Hamburguesa' },
    { value: 'TOPPING_HAMBURGUESA', label: 'Topping Hamburguesa', grupo: 'Hamburguesa' },
    { value: 'ACOMPANIAMIENTO', label: 'Acompañamiento', grupo: 'Común' },
    { value: 'BEBIDA', label: 'Bebida', grupo: 'Común' }
  ];

  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    imagenUrl: '',
    categoria: 'TIPO_MASA'
  });

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const data = await productoService.listarTodos();
      setProductos(data);
    } catch (err) {
      setError('Error al cargar productos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const limpiarFormulario = () => {
    setFormData({
      nombre: '',
      descripcion: '',
      precio: '',
      imagenUrl: '',
      categoria: 'TIPO_MASA'
    });
    setProductoSeleccionado(null);
  };

  const abrirModalCrear = () => {
    limpiarFormulario();
    setModoModal('crear');
    setShowModal(true);
  };

  const abrirModalEditar = (producto) => {
    setFormData({
      nombre: producto.nombre,
      descripcion: producto.descripcion || '',
      precio: producto.precio.toString(),
      imagenUrl: producto.imagenUrl || '',
      categoria: producto.categoria
    });
    setProductoSeleccionado(producto);
    setModoModal('editar');
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      if (modoModal === 'crear') {
        // Crear producto completo
        const productoData = {
          nombre: formData.nombre.trim(),
          descripcion: formData.descripcion.trim() || null,
          precio: parseFloat(formData.precio),
          imagenUrl: formData.imagenUrl.trim() || null,
          categoria: formData.categoria
        };
        await productoService.crear(productoData);
        setSuccess('Producto creado exitosamente');
      } else {
        // Editar: Actualizar cada campo con su endpoint específico
        const id = productoSeleccionado.id;

        // 1. Actualizar nombre, imagenUrl y categoría con PATCH
        const cambiosAtributos = {};
        if (formData.nombre !== productoSeleccionado.nombre) {
          cambiosAtributos.nombre = formData.nombre.trim();
        }
        if (formData.imagenUrl !== (productoSeleccionado.imagenUrl || '')) {
          cambiosAtributos.imagenUrl = formData.imagenUrl.trim() || null;
        }
        if (formData.categoria !== productoSeleccionado.categoria) {
          cambiosAtributos.categoria = formData.categoria;
        }

        if (Object.keys(cambiosAtributos).length > 0) {
          await productoService.actualizarAtributos(id, cambiosAtributos);
        }

        // 2. Actualizar precio con PUT específico
        const nuevoPrecio = parseFloat(formData.precio);
        if (nuevoPrecio !== parseFloat(productoSeleccionado.precio)) {
          await productoService.actualizarPrecio(id, nuevoPrecio);
        }

        // 3. Actualizar descripción con PUT específico
        const nuevaDescripcion = formData.descripcion.trim() || '';
        const descripcionActual = productoSeleccionado.descripcion || '';
        if (nuevaDescripcion !== descripcionActual) {
          await productoService.actualizarDescripcion(id, nuevaDescripcion);
        }

        setSuccess('Producto actualizado exitosamente');
      }

      setShowModal(false);
      limpiarFormulario();
      await cargarProductos();

      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Error al guardar el producto');
    } finally {
      setLoading(false);
    }
  };

  const getCategoriaInfo = (categoria) => {
    const info = categoriasProducto.find(c => c.value === categoria);
    return info || { label: categoria, grupo: 'Otro' };
  };

  const getColorGrupo = (grupo) => {
    const colores = {
      'Pizza': 'danger',
      'Hamburguesa': 'warning',
      'Común': 'info'
    };
    return colores[grupo] || 'secondary';
  };

  const formatearPrecio = (precio) => {
    return new Intl.NumberFormat('es-UY', {
      style: 'currency',
      currency: 'UYU'
    }).format(precio);
  };

  const agruparPorCategoria = () => {
    const grupos = {};
    categoriasProducto.forEach(cat => {
      if (!grupos[cat.grupo]) {
        grupos[cat.grupo] = [];
      }
    });

    productos.forEach(producto => {
      const info = getCategoriaInfo(producto.categoria);
      if (grupos[info.grupo]) {
        grupos[info.grupo].push(producto);
      }
    });

    return grupos;
  };

  return (
    <Container fluid className="py-4">
      <Row>
        <Col>
          <Card className="shadow-lg">
            <Card.Header className="bg-primary text-white py-3">
              <div className="d-flex justify-content-between align-items-center">
                <h2 className="mb-0">
                  <i className="bi bi-box-seam-fill me-2"></i>
                  Gestión de Productos
                </h2>
                <Button variant="light" onClick={abrirModalCrear}>
                  <i className="bi bi-plus-circle me-2"></i>
                  Nuevo Producto
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

              {/* Resumen */}
              <Row className="mb-4">
                <Col md={3}>
                  <Card className="text-center border-primary">
                    <Card.Body>
                      <i className="bi bi-box text-primary" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Total Productos</h6>
                      <h3 className="text-primary mb-0">{productos.length}</h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-danger">
                    <Card.Body>
                      <i className="bi bi-circle-fill text-danger" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Pizza</h6>
                      <h3 className="text-danger mb-0">
                        {productos.filter(p => getCategoriaInfo(p.categoria).grupo === 'Pizza').length}
                      </h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-warning">
                    <Card.Body>
                      <i className="bi bi-circle-fill text-warning" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Hamburguesa</h6>
                      <h3 className="text-warning mb-0">
                        {productos.filter(p => getCategoriaInfo(p.categoria).grupo === 'Hamburguesa').length}
                      </h3>
                    </Card.Body>
                  </Card>
                </Col>
                <Col md={3}>
                  <Card className="text-center border-info">
                    <Card.Body>
                      <i className="bi bi-cup-straw text-info" style={{ fontSize: '2rem' }}></i>
                      <h6 className="text-muted mt-2">Comunes</h6>
                      <h3 className="text-info mb-0">
                        {productos.filter(p => getCategoriaInfo(p.categoria).grupo === 'Común').length}
                      </h3>
                    </Card.Body>
                  </Card>
                </Col>
              </Row>

              {/* Productos agrupados por categoría */}
              <Tabs defaultActiveKey="todos" className="mb-3">
                <Tab eventKey="todos" title={`Todos (${productos.length})`}>
                  <Table striped bordered hover responsive>
                    <thead className="table-dark">
                      <tr>
                        <th style={{ width: '60px' }}>ID</th>
                        <th>Nombre</th>
                        <th style={{ width: '180px' }}>Categoría</th>
                        <th style={{ width: '120px' }} className="text-end">Precio</th>
                        <th style={{ width: '120px' }} className="text-center">Acciones</th>
                      </tr>
                    </thead>
                    <tbody>
                      {productos.map(producto => {
                        const catInfo = getCategoriaInfo(producto.categoria);
                        return (
                          <tr key={producto.id}>
                            <td className="text-center align-middle">{producto.id}</td>
                            <td className="align-middle">
                              <div className="d-flex align-items-center">
                                {producto.imagenUrl && (
                                  <img
                                    src={producto.imagenUrl}
                                    alt={producto.nombre}
                                    style={{ width: '40px', height: '40px', objectFit: 'cover' }}
                                    className="rounded me-2"
                                  />
                                )}
                                <div>
                                  <strong>{producto.nombre}</strong>
                                  {producto.descripcion && (
                                    <div><small className="text-muted">{producto.descripcion}</small></div>
                                  )}
                                </div>
                              </div>
                            </td>
                            <td className="align-middle">
                              <Badge bg={getColorGrupo(catInfo.grupo)}>
                                {catInfo.label}
                              </Badge>
                            </td>
                            <td className="text-end align-middle">
                              <strong className="text-success">
                                {formatearPrecio(producto.precio)}
                              </strong>
                            </td>
                            <td className="text-center align-middle">
                              <Button
                                variant="outline-primary"
                                size="sm"
                                onClick={() => abrirModalEditar(producto)}
                              >
                                <i className="bi bi-pencil"></i>
                              </Button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </Tab>

                {Object.entries(agruparPorCategoria()).map(([grupo, prods]) => (
                  <Tab
                    key={grupo}
                    eventKey={grupo}
                    title={`${grupo} (${prods.length})`}
                  >
                    {prods.length === 0 ? (
                      <Alert variant="info">No hay productos en esta categoría</Alert>
                    ) : (
                      <Table striped bordered hover responsive>
                        <thead className="table-light">
                          <tr>
                            <th style={{ width: '60px' }}>ID</th>
                            <th>Nombre</th>
                            <th style={{ width: '180px' }}>Categoría</th>
                            <th style={{ width: '120px' }} className="text-end">Precio</th>
                            <th style={{ width: '120px' }} className="text-center">Acciones</th>
                          </tr>
                        </thead>
                        <tbody>
                          {prods.map(producto => {
                            const catInfo = getCategoriaInfo(producto.categoria);
                            return (
                              <tr key={producto.id}>
                                <td className="text-center align-middle">{producto.id}</td>
                                <td className="align-middle">
                                  <div className="d-flex align-items-center">
                                    {producto.imagenUrl && (
                                      <img
                                        src={producto.imagenUrl}
                                        alt={producto.nombre}
                                        style={{ width: '40px', height: '40px', objectFit: 'cover' }}
                                        className="rounded me-2"
                                      />
                                    )}
                                    <div>
                                      <strong>{producto.nombre}</strong>
                                      {producto.descripcion && (
                                        <div><small className="text-muted">{producto.descripcion}</small></div>
                                      )}
                                    </div>
                                  </div>
                                </td>
                                <td className="align-middle">
                                  <Badge bg={getColorGrupo(grupo)}>
                                    {catInfo.label}
                                  </Badge>
                                </td>
                                <td className="text-end align-middle">
                                  <strong className="text-success">
                                    {formatearPrecio(producto.precio)}
                                  </strong>
                                </td>
                                <td className="text-center align-middle">
                                  <Button
                                    variant="outline-primary"
                                    size="sm"
                                    onClick={() => abrirModalEditar(producto)}
                                  >
                                    <i className="bi bi-pencil"></i>
                                  </Button>
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </Table>
                    )}
                  </Tab>
                ))}
              </Tabs>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Modal Crear/Editar */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
        <Modal.Header closeButton className="bg-primary text-white">
          <Modal.Title>
            <i className={`bi bi-${modoModal === 'crear' ? 'plus-circle' : 'pencil'} me-2`}></i>
            {modoModal === 'crear' ? 'Nuevo Producto' : 'Editar Producto'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    <i className="bi bi-tag me-1"></i>
                    Nombre *
                  </Form.Label>
                  <Form.Control
                    type="text"
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    placeholder="Ej: Masa Napolitana"
                    required
                    minLength={2}
                    maxLength={150}
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    <i className="bi bi-cash me-1"></i>
                    Precio (UYU) *
                  </Form.Label>
                  <Form.Control
                    type="number"
                    name="precio"
                    value={formData.precio}
                    onChange={handleChange}
                    placeholder="0.00"
                    step="0.01"
                    min="0.01"
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-list me-1"></i>
                Categoría *
              </Form.Label>
              <Form.Select
                name="categoria"
                value={formData.categoria}
                onChange={handleChange}
                required
              >
                <optgroup label="🍕 Pizza">
                  <option value="TIPO_MASA">Tipo de Masa</option>
                  <option value="SALSA_PIZZA">Salsa Pizza</option>
                  <option value="TAMANIO_PIZZA">Tamaño Pizza</option>
                  <option value="TOPPING_PIZZA">Topping Pizza</option>
                </optgroup>
                <optgroup label="🍔 Hamburguesa">
                  <option value="TIPO_PAN">Tipo de Pan</option>
                  <option value="TIPO_CARNE">Tipo de Carne</option>
                  <option value="TIPO_QUESO">Tipo de Queso</option>
                  <option value="SALSA_HAMBURGUESA">Salsa Hamburguesa</option>
                  <option value="TOPPING_HAMBURGUESA">Topping Hamburguesa</option>
                </optgroup>
                <optgroup label="🍟 Comunes">
                  <option value="ACOMPANIAMIENTO">Acompañamiento</option>
                  <option value="BEBIDA">Bebida</option>
                </optgroup>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-chat-left-text me-1"></i>
                Descripción
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
                placeholder="Descripción detallada del producto (opcional)"
                maxLength={1000}
              />
              <Form.Text className="text-muted">
                {formData.descripcion.length}/1000 caracteres
              </Form.Text>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                <i className="bi bi-image me-1"></i>
                URL de Imagen
              </Form.Label>
              <Form.Control
                type="url"
                name="imagenUrl"
                value={formData.imagenUrl}
                onChange={handleChange}
                placeholder="https://ejemplo.com/imagen.jpg"
                maxLength={500}
              />
              {formData.imagenUrl && (
                <div className="mt-2">
                  <img
                    src={formData.imagenUrl}
                    alt="Preview"
                    style={{ maxWidth: '200px', maxHeight: '200px' }}
                    className="rounded border"
                    onError={(e) => {
                      e.target.style.display = 'none';
                    }}
                  />
                </div>
              )}
            </Form.Group>
          </Modal.Body>

          <Modal.Footer className="bg-light">
            <Button variant="secondary" onClick={() => setShowModal(false)} disabled={loading}>
              <i className="bi bi-x-circle me-1"></i>
              Cancelar
            </Button>
            <Button variant="primary" type="submit" disabled={loading}>
              <i className={`bi bi-${loading ? 'hourglass-split' : 'check-circle'} me-1`}></i>
              {loading ? 'Guardando...' : (modoModal === 'crear' ? 'Crear Producto' : 'Guardar Cambios')}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};