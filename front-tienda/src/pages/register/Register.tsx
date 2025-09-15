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
  <div className="authPage">
    <ParticleBackground logoSrc={logoSrc} />
    <section className="authSection">
      <form onSubmit={onSubmit} autoComplete="off" noValidate className="authForm">
        <h1 className="welcomeText">
          ¡Regístrate en <span className="highlight">TempuroShop</span>!
        </h1>
        <div className="divForm">
          <label htmlFor="username">Nombre de usuario</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={e => onUsernameChange(e.target.value)}
          />
        </div>
        <div className="divForm">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={e => onEmailChange(e.target.value)}
          />
        </div>
        <div className="divForm">
          <label htmlFor="password">Contraseña</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={e => onPasswordChange(e.target.value)}
          />
        </div>
        <div className="divForm">
          <label htmlFor="confirmPassword">Confirmar contraseña</label>
          <input
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={e => onConfirmPasswordChange(e.target.value)}
          />
        </div>
        {error && <div className="errorMessage">{error}</div>}
        <button type="submit">Crear cuenta</button>
        <div className="registerLink" onClick={onLogin} style={{ cursor: "pointer" }}>
          ¿Ya tienes cuenta? <span>Inicia sesión</span>
        </div>
      </form>
    </section>
    <Footer/>
  </div>
);

export default Register;
