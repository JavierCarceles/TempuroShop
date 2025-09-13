// src/pages/LoginContainer.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Login from "./Login";
import { scheduleRefresh } from "../../services/Auth";
import { apiFetch } from "../../services/ApiFetch";

interface LoginContainerProps {
  onLogin: () => void;
  t: {
    fillAllFields?: string;
    loginError?: string;
    serverError?: string;
  };
}

const LoginContainer: React.FC<LoginContainerProps> = ({ onLogin, t }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    if (!email || !password) {
      setError(t.fillAllFields || "Por favor, rellena todos los campos");
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z]).{8,}$/;

    if (!emailPattern.test(email)) {
      setError("Email no válido");
      return;
    }

    if (!passwordPattern.test(password)) {
      setError("Contraseña debe tener al menos 8 caracteres y contener mayúscula y minúscula");
      return;
    }

    try {
      const response = await apiFetch("http://localhost:8080/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem("nombreUsuario", email);
        localStorage.setItem("token", data.accessToken);
        scheduleRefresh(data.accessToken);
        onLogin();
        navigate("/home");
      } else {
        setError(data.message || t.loginError || "Error en login");
      }
    } catch {
      setError(t.serverError || "Email o contraseña incorrectos");
    }
  };

  return (
    <Login
      email={email}
      password={password}
      error={error}
      onEmailChange={setEmail}
      onPasswordChange={setPassword}
      onSubmit={handleSubmit}
      onRegister={() => navigate("/register")}
    />
  );
};

export default LoginContainer;
