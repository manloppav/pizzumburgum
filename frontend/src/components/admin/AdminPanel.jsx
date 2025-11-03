import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert } from 'react-bootstrap';
import { authService } from '../../services/authService';

export const AdminPanel = () => {
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const [formData, setFormData] = useState({
        nombre: '',
        apellido: '',
        cedulaIdentidad: '',
        email: '',
        password: '',
        fechaNacimiento: '',
        telefono: '',
        rol: 'ADMIN',
        direccion: {
            calle: '',
            numero: '',
            apartamento: '',
            barrio: '',
            principal: true
        }
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleDireccionChange = (e) => {
        setFormData({
            ...formData,
            direccion: {
                ...formData.direccion,
                [e.target.name]: e.target.value
            }
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        const dataToSend = {
            ...formData,
            direccion: {
                ...formData.direccion,
                numero: parseInt(formData.direccion.numero)
            }
        };

        try {
            await authService.registerAdmin(dataToSend);
            setSuccess(`Usuario ${formData.rol} creado exitosamente`);

            // Reset form
            setFormData({
                nombre: '',
                apellido: '',
                cedulaIdentidad: '',
                email: '',
                password: '',
                fechaNacimiento: '',
                telefono: '',
                rol: 'ADMIN',
                direccion: {
                    calle: '',
                    numero: '',
                    apartamento: '',
                    barrio: '',
                    principal: true
                }
            });
        } catch (err) {
            setError(err.error || 'Error al crear usuario');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-5">
            <Row>
                <Col lg={10} className="mx-auto">
                    <Card className="shadow">
                        <Card.Header className="bg-primary text-white">
                            <h3 className="mb-0">Panel de Administración</h3>
                        </Card.Header>
                        <Card.Body className="p-4">
                            <h5 className="mb-4">Crear Nuevo Admin</h5>

                            {error && <Alert variant="danger" dismissible onClose={() => setError('')}>{error}</Alert>}
                            {success && <Alert variant="success" dismissible onClose={() => setSuccess('')}>{success}</Alert>}

                            <Form onSubmit={handleSubmit}>
                                {/* Datos Personales */}
                                <h6 className="mb-3 text-primary">Datos Personales</h6>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Nombre *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="nombre"
                                                value={formData.nombre}
                                                onChange={handleChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Apellido *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="apellido"
                                                value={formData.apellido}
                                                onChange={handleChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Cédula *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="cedulaIdentidad"
                                                value={formData.cedulaIdentidad}
                                                onChange={handleChange}
                                                placeholder="12345678-9"
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Fecha de Nacimiento *</Form.Label>
                                            <Form.Control
                                                type="date"
                                                name="fechaNacimiento"
                                                value={formData.fechaNacimiento}
                                                onChange={handleChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Email *</Form.Label>
                                            <Form.Control
                                                type="email"
                                                name="email"
                                                value={formData.email}
                                                onChange={handleChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Teléfono *</Form.Label>
                                            <Form.Control
                                                type="tel"
                                                name="telefono"
                                                value={formData.telefono}
                                                onChange={handleChange}
                                                placeholder="+59899123456"
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Contraseña *</Form.Label>
                                            <Form.Control
                                                type="password"
                                                name="password"
                                                value={formData.password}
                                                onChange={handleChange}
                                                minLength={8}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                {/* Dirección */}
                                <hr className="my-4" />
                                <h6 className="mb-3 text-primary">Dirección</h6>

                                <Row>
                                    <Col md={8}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Calle *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="calle"
                                                value={formData.direccion.calle}
                                                onChange={handleDireccionChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Número *</Form.Label>
                                            <Form.Control
                                                type="number"
                                                name="numero"
                                                value={formData.direccion.numero}
                                                onChange={handleDireccionChange}
                                                required
                                                min={1}
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Apartamento</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="apartamento"
                                                value={formData.direccion.apartamento}
                                                onChange={handleDireccionChange}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Barrio *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="barrio"
                                                value={formData.direccion.barrio}
                                                onChange={handleDireccionChange}
                                                required
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Button
                                    variant="primary"
                                    type="submit"
                                    className="w-100 mt-3"
                                    disabled={loading}
                                >
                                    {loading ? 'Creando usuario...' : 'Crear Usuario'}
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};