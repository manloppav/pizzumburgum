import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Container, Row, Col, Form, Button, Alert, Card } from 'react-bootstrap';

export const Register = () => {
    const navigate = useNavigate();
    const { register } = useAuth();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const [formData, setFormData] = useState({
        nombre: '',
        apellido: '',
        cedulaIdentidad: '',
        email: '',
        password: '',
        fechaNacimiento: '',
        telefono: '',
        direccion: {
            calle: '',
            numero: '',
            apartamento: '',
            barrio: '',
            principal: true
        },
        tarjeta: {
            numeroTarjeta: '',
            fechaVencimiento: '',
            cvv: '',
            titular: '',
            tipo: 'VISA',
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

    const handleTarjetaChange = (e) => {
        let value = e.target.value;

        // Formatear número de tarjeta (solo números)
        if (e.target.name === 'numeroTarjeta') {
            value = value.replace(/\D/g, '');
        }

        // Formatear fecha MM/YY
        if (e.target.name === 'fechaVencimiento') {
            value = value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
        }

        // Formatear CVV (solo números)
        if (e.target.name === 'cvv') {
            value = value.replace(/\D/g, '');
        }

        setFormData({
            ...formData,
            tarjeta: {
                ...formData.tarjeta,
                [e.target.name]: value
            }
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        const dataToSend = {
            ...formData,
            direccion: {
                ...formData.direccion,
                numero: parseInt(formData.direccion.numero)
            }
        };

        try {
            await register(dataToSend);
            navigate('/dashboard');
        } catch (err) {
            setError(err.error || 'Error al registrar usuario');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-5">
            <Row className="justify-content-center">
                <Col md={10} lg={8}>
                    <Card className="shadow">
                        <Card.Body className="p-4">
                            <h2 className="text-center mb-4">Crear Cuenta</h2>

                            {error && <Alert variant="danger">{error}</Alert>}

                            <Form onSubmit={handleSubmit}>
                                {/* DATOS PERSONALES */}
                                <h5 className="mb-3 text-primary">
                                    <i className="bi bi-person-fill me-2"></i>Datos Personales
                                </h5>
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
                                                minLength={2}
                                                maxLength={100}
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
                                                minLength={2}
                                                maxLength={100}
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
                                                pattern="\d{7,8}-\d"
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

                                <Form.Group className="mb-4">
                                    <Form.Label>Contraseña *</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                        minLength={8}
                                    />
                                </Form.Group>

                                {/* DIRECCIÓN */}
                                <hr className="my-4" />
                                <h5 className="mb-3 text-primary">
                                    <i className="bi bi-geo-alt-fill me-2"></i>Dirección de Entrega
                                </h5>

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

                                {/* TARJETA DE CRÉDITO */}
                                <hr className="my-4" />
                                <h5 className="mb-3 text-primary">
                                    <i className="bi bi-credit-card-fill me-2"></i>Método de Pago
                                </h5>

                                <Form.Group className="mb-3">
                                    <Form.Label>Número de Tarjeta *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="numeroTarjeta"
                                        value={formData.tarjeta.numeroTarjeta}
                                        onChange={handleTarjetaChange}
                                        placeholder="1234567890123456"
                                        required
                                        minLength={13}
                                        maxLength={19}
                                    />
                                    <Form.Text className="text-muted">
                                        Solo números, sin espacios
                                    </Form.Text>
                                </Form.Group>

                                <Row>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Vencimiento *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="fechaVencimiento"
                                                value={formData.tarjeta.fechaVencimiento}
                                                onChange={handleTarjetaChange}
                                                placeholder="MM/YY"
                                                required
                                                maxLength={5}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>CVV *</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="cvv"
                                                value={formData.tarjeta.cvv}
                                                onChange={handleTarjetaChange}
                                                placeholder="123"
                                                required
                                                minLength={3}
                                                maxLength={4}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Tipo *</Form.Label>
                                            <Form.Select
                                                name="tipo"
                                                value={formData.tarjeta.tipo}
                                                onChange={handleTarjetaChange}
                                            >
                                                <option value="VISA">Visa</option>
                                                <option value="MASTERCARD">Mastercard</option>
                                                <option value="AMEX">American Express</option>
                                                <option value="OCA">OCA</option>
                                                <option value="CREDITEL">Creditel</option>
                                            </Form.Select>
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Form.Group className="mb-4">
                                    <Form.Label>Titular *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="titular"
                                        value={formData.tarjeta.titular}
                                        onChange={handleTarjetaChange}
                                        placeholder="Nombre como aparece en la tarjeta"
                                        required
                                        minLength={2}
                                    />
                                </Form.Group>

                                <Alert variant="info">
                                    <i className="bi bi-shield-check me-2"></i>
                                    Tus datos están seguros. No guardamos tu número de tarjeta completo.
                                </Alert>

                                <Button
                                    variant="primary"
                                    type="submit"
                                    className="w-100 mt-3"
                                    size="lg"
                                    disabled={loading}
                                >
                                    {loading ? 'Registrando...' : 'Crear Cuenta'}
                                </Button>
                            </Form>

                            <div className="text-center mt-3">
                                <Link to="/login">¿Ya tienes cuenta? Inicia sesión</Link>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};