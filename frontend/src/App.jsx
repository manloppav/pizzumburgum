import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Login } from './components/auth/Login';
import { Register } from './components/auth/Register';
import { PrivateRoute } from './components/auth/PrivateRoute';
import { Navbar } from './components/common/Navbar';
import { AdminPanel } from './components/admin/AdminPanel';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { ListarPedidos } from './components/admin/ListarPedidos';
import { MisPedidos } from './components/cliente/MisPedidos';
import { GestionProductos } from './components/admin/GestionProductos';
import { CrearCreacion } from './components/cliente/CrearCreacion';
import { MisCreaciones } from './components/cliente/MisCreaciones';
import { MisDirecciones } from './components/cliente/MisDirecciones';
import { MisTarjetas } from './components/cliente/MisTarjetas';
import { Carrito } from './components/cliente/Carrito';
import { Dashboard } from './components/common/Dashboard';

import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  return (
    <div
      style={{
        backgroundImage: "url('/fondoPizzumBurgum.png')",
        backgroundSize: "750px",
        backgroundRepeat: "repeat",
        minHeight: "100vh",
      }}
    >
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
              path="/admin/productos"
              element={
                <PrivateRoute adminOnly>
                  <GestionProductos />
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

            <Route
              path="/crear-creacion"
              element={
                <PrivateRoute>
                  <CrearCreacion />
                </PrivateRoute>
              }
            />

            <Route
              path="/mis-creaciones"
              element={
                <PrivateRoute>
                  <MisCreaciones />
                </PrivateRoute>
              }
            />

            <Route
              path="/mis-direcciones"
              element={
                <PrivateRoute>
                  <MisDirecciones />
                </PrivateRoute>
              }
            />

            <Route
              path="/mis-tarjetas"
              element={
                <PrivateRoute>
                  <MisTarjetas />
                </PrivateRoute>
              }
            />

            <Route
              path="/carrito"
              element={
                <PrivateRoute>
                  <Carrito />
                </PrivateRoute>
              }
            />

            <Route path="/" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </AuthProvider>
      </Router>
    </div>
  );
}

export default App;