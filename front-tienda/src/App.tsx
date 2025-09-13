import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginContainer from './pages/login/LoginContainer';
import RegisterContainer from './pages/register/RegisterContainer';

const App: React.FC = () => {
  const handleLogin = () => {
    console.log('Usuario logueado');
  };

  const t = {
    fillAllFields: 'Por favor, rellena todos los campos',
    loginError: 'Error en login',
    serverError: 'No se pudo conectar al servidor',
  };

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginContainer onLogin={handleLogin} t={t} />} />
      <Route path="/register" element={<RegisterContainer />} />
      <Route path="/home" element={<div>Home page</div>} />
    </Routes>
  );
};

export default App;
