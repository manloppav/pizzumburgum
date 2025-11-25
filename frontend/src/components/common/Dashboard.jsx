import React from "react";
import { useAuth } from "../../context/AuthContext";
import { DashboardCliente } from '../../components/cliente/DashboardCliente';
import { DashboardAdmin } from "../../components/admin/DashboardAdmin";

export const Dashboard = () => {
  const { user } = useAuth();

  const esAdmin =
    user?.rol === "ADMIN" ||
    user?.role === "ADMIN" ||
    (Array.isArray(user?.roles) && user.roles.includes("ADMIN"));

  return esAdmin ? <DashboardAdmin /> : <DashboardCliente />;
};