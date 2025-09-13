import React from "react";
import ParticleBackground from "../../components/ParticleBackground";
import "../../assets/css/Auth.css";
import Footer from "../../components/Footer";

export interface RegisterProps {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  error: string;
  onUsernameChange: (value: string) => void;
  onEmailChange: (value: string) => void;
  onPasswordChange: (value: string) => void;
  onConfirmPasswordChange: (value: string) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  onLogin: () => void;
}

const logoSrc = "/assets/imgs/logoTienda.png";

const Register: React.FC<RegisterProps> = ({
  username,
  email,
  password,
  confirmPassword,
  error,
  onUsernameChange,
  onEmailChange,
  onPasswordChange,
  onConfirmPasswordChange,
  onSubmit,
  onLogin,
}) => (
  <>
    {/* Fondo animado con partículas y logo */}
    <ParticleBackground logoSrc={logoSrc} />

    <section className="loginSection">
      <form onSubmit={onSubmit} autoComplete="off" noValidate className="loginForm">
        
        {/* Título */}
        <h1 className="welcomeText">
          ¡Regístrate en <span className="highlight">TempuroShop</span>!
        </h1>

        {/* Input de username */}
        <div className="divForm">
          <label htmlFor="username">Nombre de usuario</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={e => onUsernameChange(e.target.value)}
          />
        </div>

        {/* Input de email */}
        <div className="divForm">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={e => onEmailChange(e.target.value)}
          />
        </div>

        {/* Input de contraseña */}
        <div className="divForm">
          <label htmlFor="password">Contraseña</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={e => onPasswordChange(e.target.value)}
          />
        </div>

        {/* Input de confirmación de contraseña */}
        <div className="divForm">
          <label htmlFor="confirmPassword">Confirmar contraseña</label>
          <input
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={e => onConfirmPasswordChange(e.target.value)}
          />
        </div>

        {/* Mensaje de error */}
        {error && <div className="errorMessage">{error}</div>}

        {/* Botón de registro */}
        <button type="submit">Crear cuenta</button>

        {/* Link para volver al login */}
        <div className="registerLink" onClick={onLogin} style={{ cursor: "pointer" }}>
          ¿Ya tienes cuenta? <span>Inicia sesión</span>
        </div>

      </form>
    </section>
    <Footer/>
  </>
);

export default Register;
