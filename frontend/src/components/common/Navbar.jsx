import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Navbar as BSNavbar, Container, Nav, Button } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';

export const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <BSNavbar bg="dark" variant="dark" expand="lg" className="mb-4">
      <Container>
        <BSNavbar.Brand as={Link} to="/">
          PizzumBurgum
        </BSNavbar.Brand>

        <BSNavbar.Toggle aria-controls="basic-navbar-nav" />
        <BSNavbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">

            {isAuthenticated ? (
              <>
                {/* SOLO PARA USUARIOS LOGUEADOS */}
                <Nav.Link as={Link} to="/dashboard">
                  Dashboard
                </Nav.Link>

                {/* SOLO ADMIN */}
                {user?.rol === 'ADMIN' && (
                  <>
                    <Nav.Link as={Link} to="/admin">
                      <i className="bi bi-gear-fill me-1"></i>
                      Panel Admin
                    </Nav.Link>
                    <Nav.Link as={Link} to="/admin/productos">
                      <i className="bi bi-box-seam me-1"></i>
                      Productos
                    </Nav.Link>
                    <Nav.Link as={Link} to="/admin/pedidos">
                      <i className="bi bi-clipboard-data me-1"></i>
                      Pedidos
                    </Nav.Link>
                  </>
                )}

                {/* SOLO CLIENTE */}
                {user?.rol === 'CLIENTE' && (
                  <>
                    <Nav.Link as={Link} to="/mis-pedidos">
                      <i className="bi bi-bag-check me-1"></i>
                      Mis Pedidos
                    </Nav.Link>
                  </>
                )}

                {/* PARA TODOS LOS USUARIOS AUTENTICADOS */}

                {/* NOMBRE + CERRAR SESIÓN */}
                <Nav.Item className="d-flex align-items-center ms-3">
                  <span className="text-light me-3">
                    Hola, {user?.nombre}
                  </span>
                  <Button
                    variant="outline-light"
                    size="sm"
                    onClick={handleLogout}
                  >
                    Cerrar Sesión
                  </Button>
                </Nav.Item>

              </>
            ) : (
              <>
                {/* SI NO ESTÁ AUTENTICADO */}
                <Nav.Link as={Link} to="/login">
                  Iniciar Sesión
                </Nav.Link>
                <Nav.Link as={Link} to="/register">
                  Registrarse
                </Nav.Link>
              </>
            )}

          </Nav>
        </BSNavbar.Collapse>
      </Container>
    </BSNavbar>
  );
};
