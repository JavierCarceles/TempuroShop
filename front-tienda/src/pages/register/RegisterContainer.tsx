// src/pages/RegisterContainer.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Register from "./Register";
import { apiFetch } from "../../services/ApiFetch";

const RegisterContainer: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    if (!username || !email || !password || !confirmPassword) {
      setError("Por favor, rellena todos los campos");
      return;
    }

    if (password !== confirmPassword) {
      setError("Las contraseñas no coinciden");
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
      const response = await apiFetch("http://localhost:8080/auth/register", {
        method: "POST",
        body: JSON.stringify({ username, email, password }),
      });

      const data = await response.text();

      if (response.ok) {
        navigate("/login");
      } else {
        setError(data || "Error en el registro");
      }
    } catch {
      setError("Error al conectar con el servidor");
    }
  };

  return (
    <Register
      username={username}
      email={email}
      password={password}
      confirmPassword={confirmPassword}
      error={error}
      onUsernameChange={setUsername}
      onEmailChange={setEmail}
      onPasswordChange={setPassword}
      onConfirmPasswordChange={setConfirmPassword}
      onSubmit={handleSubmit}
      onLogin={() => navigate("/login")}
    />
  );
};

export default RegisterContainer;
