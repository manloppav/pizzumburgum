import React from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { Link } from "react-router-dom";

export const DashboardAdmin = () => {
  return (
    <Container className="py-5">
      <h2 className="text-center mb-5 fw-bold">
        Panel de Administración
      </h2>

      <Row className="g-4 justify-content-center">

        <Col md={5}>
          <Card className="shadow text-center p-4">
            <h4 className="mb-3">Gestionar Pedidos</h4>
            <p className="text-muted">
              Revisa y administra los pedidos de los clientes.
            </p>
            <Button
              as={Link}
              to="/admin/pedidos"
              variant="primary"
              size="lg"
              className="mt-3"
            >
              Ir a Pedidos
            </Button>
          </Card>
        </Col>

        <Col md={5}>
          <Card className="shadow text-center p-4">
            <h4 className="mb-3">Gestionar Productos</h4>
            <p className="text-muted">
              Modifica, agrega o elimina productos.
            </p>
            <Button
              as={Link}
              to="/admin/productos"
              variant="success"
              size="lg"
              className="mt-3"
            >
              Ir a Productos
            </Button>
          </Card>
        </Col>

      </Row>
    </Container>
  );
};