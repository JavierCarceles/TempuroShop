import React from 'react';
import { Routes, Route } from 'react-router-dom';
import LoginContainer from './pages/login/LoginContainer';
import RegisterContainer from './pages/register/RegisterContainer';
import Home from './pages/home/Home';

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
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<LoginContainer onLogin={handleLogin} t={t} />} />
      <Route path="/register" element={<RegisterContainer />} />
    </Routes>
  );
};

export default App; 