import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Login } from './components/auth/Login';
import { Register } from './components/auth/Register';
import { PrivateRoute } from './components/auth/PrivateRoute';
import { Navbar } from './components/common/Navbar';
import { AdminPanel } from './components/admin/AdminPanel';
import { Container, Card } from 'react-bootstrap';
import { ListarPedidos } from './components/admin/ListarPedidos';
import { MisPedidos } from './components/cliente/MisPedidos';
import 'bootstrap/dist/css/bootstrap.min.css';

function Dashboard() {
  return (
    <Container className="py-5">
      <Card className="shadow">
        <Card.Body className="p-5 text-center">
          <h1 className="mb-4">Bienvenido a PizzumBurgum</h1>
          <p className="lead">Has iniciado sesión exitosamente</p>
        </Card.Body>
      </Card>
    </Container>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <PrivateRoute adminOnly>
                <AdminPanel />
              </PrivateRoute>
            }
          />
          <Route
            path="/admin/pedidos"
            element={
              <PrivateRoute adminOnly>
                <ListarPedidos />
              </PrivateRoute>
            }
          />
          <Route
            path="/mis-pedidos"
            element={
              <PrivateRoute>
                <MisPedidos />
              </PrivateRoute>
            }
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;