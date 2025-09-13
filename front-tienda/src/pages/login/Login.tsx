import React from "react";
import ParticleBackground from "../../components/ParticleBackground";
import "../../assets/css/Auth.css";
import Footer from "../../components/Footer";

export interface LoginProps {
  email: string;
  password: string;
  error: string;
  onEmailChange: (value: string) => void;
  onPasswordChange: (value: string) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  onRegister: () => void;
}

const logoSrc = "/assets/imgs/logoTienda.png";

const Login: React.FC<LoginProps> = ({
  email,
  password,
  error,
  onEmailChange,
  onPasswordChange,
  onSubmit,
  onRegister,
}) => (
  <>
    <ParticleBackground logoSrc={logoSrc} />
    <section className="loginSection">
      <form onSubmit={onSubmit} autoComplete="off" noValidate className="loginForm">
        <h1 className="welcomeText">
          ¡Bienvenido a <span className="highlight">TempuroShop</span>!
        </h1>
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
        {error && <div className="errorMessage">{error}</div>}
        <button type="submit">Iniciar Sesión</button>
        <div className="registerLink" onClick={onRegister} style={{ cursor: "pointer" }}>
          ¿No tienes una cuenta? Regístrate
        </div>
      </form>
    </section>
    <Footer />
  </>
);

export default Login;
