import React from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { Link } from "react-router-dom";

export const DashboardCliente = () => {
  return (
    <Container className="py-5 text-center">

      <h2
        className="mb-5"
        style={{
          fontFamily: "'Chewy', cursive",
          fontSize: "3rem",
          color: "#222222",
          textShadow: "2px 2px #00000044"
        }}
      >
        ¿Qué te apetece hacer hoy?
      </h2>

      <Row className="g-4 justify-content-center">

        <Col md={5}>
          <Card
            className="shadow p-4"
            style={{
              borderRadius: "20px",
              backgroundColor: "#fff7e6",
              border: "3px solid #ffcc80"
            }}
          >
            <h3
              style={{
                fontFamily: "'Chewy', cursive",
                fontSize: "2rem",
                color: "#e65100"
              }}
            >
              🍕 Hacer una nueva creación
            </h3>

            <p className="text-muted">
              Diseña tu pizza o hamburguesa a tu gusto.
            </p>

            <Button
              as={Link}
              to="/crear-creacion"
              variant="warning"
              size="lg"
              className="mt-3"
              style={{
                fontFamily: "'Chewy', cursive",
                fontSize: "1.5rem",
                borderRadius: "15px"
              }}
            >
              Crear ahora
            </Button>
          </Card>
        </Col>

        <Col md={5}>
          <Card
            className="shadow p-4"
            style={{
              borderRadius: "20px",
              backgroundColor: "#e8f5e9",
              border: "3px solid #a5d6a7"
            }}
          >
            <h3
              style={{
                fontFamily: "'Chewy', cursive",
                fontSize: "2rem",
                color: "#1b5e20"
              }}
            >
              🍔 Ver mis creaciones
            </h3>

            <p className="text-muted">
              Mira y reutiliza las creaciones que ya hiciste.
            </p>

            <Button
              as={Link}
              to="/mis-creaciones"
              variant="success"
              size="lg"
              className="mt-3"
              style={{
                fontFamily: "'Chewy', cursive",
                fontSize: "1.5rem",
                borderRadius: "15px"
              }}
            >
              Ver creaciones
            </Button>
          </Card>
        </Col>

      </Row>
    </Container>
  );
};