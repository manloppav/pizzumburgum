import React, { useState, useEffect } from 'react';
import { Modal, Card, Row, Col, Badge, Table, Alert, Spinner, Image } from 'react-bootstrap';
import { creacionService } from '../../services/creacionService';

export const ModalDetalleCreacion = ({ show, onHide, creacionId }) => {
  const [creacion, setCreacion] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (show && creacionId) {
      cargarCreacion();
    }
  }, [show, creacionId]);

  const cargarCreacion = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await creacionService.obtenerPorId(creacionId);
      setCreacion(data);
    } catch (err) {
      setError('Error al cargar la creación');
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

  const getCategoriaInfo = (categoria) => {
    const categorias = {
      PIZZA_BASE: { texto: 'Pizza', icon: 'bi-circle-fill', color: 'danger' },
      HAMBURGUESA_BASE: { texto: 'Hamburguesa', icon: 'bi-circle-fill', color: 'warning' }
    };
    return categorias[categoria] || { texto: categoria, icon: 'bi-circle', color: 'secondary' };
  };

  const getCategoriaProductoInfo = (categoria) => {
    const categorias = {
      // PIZZA
      TIPO_MASA: { texto: 'Tipo de Masa', icon: 'bi-circle', color: 'info', grupo: 'Pizza' },
      SALSA_PIZZA: { texto: 'Salsa Pizza', icon: 'bi-droplet-fill', color: 'danger', grupo: 'Pizza' },
      TAMANIO_PIZZA: { texto: 'Tamaño Pizza', icon: 'bi-rulers', color: 'warning', grupo: 'Pizza' },
      TOPPING_PIZZA: { texto: 'Topping Pizza', icon: 'bi-plus-circle', color: 'success', grupo: 'Pizza' },

      // HAMBURGUESA
      TIPO_PAN: { texto: 'Tipo de Pan', icon: 'bi-circle', color: 'warning', grupo: 'Hamburguesa' },
      TIPO_CARNE: { texto: 'Tipo de Carne', icon: 'bi-egg-fried', color: 'danger', grupo: 'Hamburguesa' },
      TIPO_QUESO: { texto: 'Tipo de Queso', icon: 'bi-square-fill', color: 'warning', grupo: 'Hamburguesa' },
      SALSA_HAMBURGUESA: { texto: 'Salsa', icon: 'bi-droplet-fill', color: 'info', grupo: 'Hamburguesa' },
      TOPPING_HAMBURGUESA: { texto: 'Topping', icon: 'bi-plus-circle', color: 'success', grupo: 'Hamburguesa' },

      // COMUNES
      ACOMPANIAMIENTO: { texto: 'Acompañamiento', icon: 'bi-basket', color: 'secondary', grupo: 'Común' },
      BEBIDA: { texto: 'Bebida', icon: 'bi-cup-straw', color: 'primary', grupo: 'Común' }
    };
    return categorias[categoria] || { texto: categoria, icon: 'bi-circle', color: 'secondary', grupo: 'Otro' };
  };

  const agruparProductosPorCategoria = () => {
    if (!creacion?.productos) return {};

    return creacion.productos.reduce((grupos, producto) => {
      const info = getCategoriaProductoInfo(producto.categoria);
      const grupo = info.grupo;

      if (!grupos[grupo]) {
        grupos[grupo] = [];
      }
      grupos[grupo].push(producto);

      return grupos;
    }, {});
  };

  const handleClose = () => {
    setCreacion(null);
    setError('');
    onHide();
  };

  return (
    <Modal show={show} onHide={handleClose} size="xl" centered>
      <Modal.Header closeButton className="bg-primary text-white">
        <Modal.Title>
          <i className="bi bi-star-fill me-2"></i>
          Detalle de la Creación
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading ? (
          <div className="text-center py-5">
            <Spinner animation="border" variant="primary" />
            <p className="mt-3 text-muted">Cargando creación...</p>
          </div>
        ) : error ? (
          <Alert variant="danger">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {error}
          </Alert>
        ) : creacion ? (
          <>
            {/* Header con imagen y nombre */}
            <Row className="mb-4">
              <Col md={4}>
                {creacion.imagenUrl ? (
                  <Image
                    src={creacion.imagenUrl}
                    alt={creacion.nombre}
                    rounded
                    fluid
                    className="shadow-sm"
                  />
                ) : (
                  <div
                    className="bg-light d-flex align-items-center justify-content-center rounded shadow-sm"
                    style={{ height: '200px' }}
                  >
                    <i className="bi bi-image text-muted" style={{ fontSize: '4rem' }}></i>
                  </div>
                )}
              </Col>
              <Col md={8}>
                <h3 className="mb-3">
                  <i className={`${getCategoriaInfo(creacion.categoriaCreacion).icon} text-${getCategoriaInfo(creacion.categoriaCreacion).color} me-2`}></i>
                  {creacion.nombre}
                </h3>

                <div className="mb-3">
                  <Badge bg={getCategoriaInfo(creacion.categoriaCreacion).color} className="me-2 py-2 px-3">
                    <i className="bi bi-tag-fill me-1"></i>
                    {getCategoriaInfo(creacion.categoriaCreacion).texto}
                  </Badge>

                  {creacion.nombreUsuario && (
                    <Badge bg="info" className="py-2 px-3">
                      <i className="bi bi-person-fill me-1"></i>
                      Creado por: {creacion.nombreUsuario}
                    </Badge>
                  )}
                </div>

                {creacion.descripcion && (
                  <Card className="bg-light border-0 mb-3">
                    <Card.Body>
                      <h6 className="text-muted mb-2">
                        <i className="bi bi-chat-quote me-2"></i>
                        Descripción
                      </h6>
                      <p className="mb-0">{creacion.descripcion}</p>
                    </Card.Body>
                  </Card>
                )}

                <Card className="border-success">
                  <Card.Body>
                    <div className="d-flex justify-content-between align-items-center">
                      <h6 className="text-muted mb-0">
                        <i className="bi bi-cash-coin me-2"></i>
                        Precio Total
                      </h6>
                      <h3 className="text-success mb-0">
                        {formatearPrecio(creacion.precioTotal)}
                      </h3>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            </Row>

            <hr className="my-4" />

            {/* Lista de productos/ingredientes agrupados */}
            <h5 className="mb-3">
              <i className="bi bi-list-check me-2"></i>
              Productos/Ingredientes ({creacion.productos?.length || 0})
            </h5>

            {creacion.productos && creacion.productos.length > 0 ? (
              <>
                {Object.entries(agruparProductosPorCategoria()).map(([grupo, productos]) => (
                  <Card key={grupo} className="mb-3">
                    <Card.Header className="bg-light">
                      <h6 className="mb-0">
                        <i className="bi bi-tag-fill me-2"></i>
                        {grupo}
                      </h6>
                    </Card.Header>
                    <Card.Body className="p-0">
                      <Table hover className="mb-0">
                        <thead className="table-light">
                          <tr>
                            <th style={{ width: '50px' }} className="text-center">#</th>
                            <th>Producto</th>
                            <th style={{ width: '180px' }}>Categoría</th>
                            <th style={{ width: '120px' }} className="text-end">Precio</th>
                          </tr>
                        </thead>
                        <tbody>
                          {productos.map((producto, index) => {
                            const catInfo = getCategoriaProductoInfo(producto.categoria);
                            return (
                              <tr key={producto.id}>
                                <td className="text-center align-middle">{index + 1}</td>
                                <td className="align-middle">
                                  <div>
                                    <strong>{producto.nombre}</strong>
                                    {producto.descripcion && (
                                      <div>
                                        <small className="text-muted">{producto.descripcion}</small>
                                      </div>
                                    )}
                                  </div>
                                </td>
                                <td className="align-middle">
                                  <Badge bg={catInfo.color}>
                                    <i className={`${catInfo.icon} me-1`}></i>
                                    {catInfo.texto}
                                  </Badge>
                                </td>
                                <td className="text-end align-middle">
                                  {formatearPrecio(producto.precio)}
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </Table>
                    </Card.Body>
                  </Card>
                ))}

                {/* Total general */}
                <Card className="border-success">
                  <Card.Body>
                    <Row>
                      <Col className="text-end">
                        <h5 className="mb-0">
                          <i className="bi bi-calculator me-2"></i>
                          PRECIO TOTAL:
                        </h5>
                      </Col>
                      <Col xs="auto">
                        <h4 className="mb-0 text-success">
                          {formatearPrecio(creacion.precioTotal)}
                        </h4>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>
              </>
            ) : (
              <Alert variant="warning">
                <i className="bi bi-exclamation-triangle me-2"></i>
                Esta creación no tiene productos asociados
              </Alert>
            )}

            {/* Resumen */}
            <Row className="mt-4">
              <Col xs={4}>
                <Card className="bg-light text-center">
                  <Card.Body>
                    <i className="bi bi-box text-primary" style={{ fontSize: '2rem' }}></i>
                    <h6 className="text-muted mt-2 mb-1">Total Productos</h6>
                    <h4 className="mb-0">{creacion.productos?.length || 0}</h4>
                  </Card.Body>
                </Card>
              </Col>
              <Col xs={4}>
                <Card className="bg-light text-center">
                  <Card.Body>
                    <i className="bi bi-tag text-info" style={{ fontSize: '2rem' }}></i>
                    <h6 className="text-muted mt-2 mb-1">Categorías</h6>
                    <h4 className="mb-0">{Object.keys(agruparProductosPorCategoria()).length}</h4>
                  </Card.Body>
                </Card>
              </Col>
              <Col xs={4}>
                <Card className="bg-light text-center">
                  <Card.Body>
                    <i className="bi bi-cash-coin text-success" style={{ fontSize: '2rem' }}></i>
                    <h6 className="text-muted mt-2 mb-1">Precio Final</h6>
                    <h4 className="mb-0 text-success">
                      {formatearPrecio(creacion.precioTotal)}
                    </h4>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          </>
        ) : null}
      </Modal.Body>
    </Modal>
  );
};