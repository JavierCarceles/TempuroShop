import React from "react";
import "../assets/css/Footer.css";

const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="footer-left">
        <p>Contacto: <span>+34 618811769</span></p>
        <p>Email: <span>javiercarceles@outlook.es</span></p>
      </div>
      <div className="footer-right">
        <a href="https://github.com/JavierCarceles" target="_blank" rel="noopener noreferrer">GitHub</a>
        <a href="https://www.linkedin.com/in/javier-c%C3%A1rceles-fern%C3%A1ndez-474545214/" target="_blank" rel="noopener noreferrer">LinkedIn</a>
      </div>
      <div className="footer-bottom">
        &copy; {new Date().getFullYear()} TempuroShop. Todos los derechos reservados.
      </div>
    </footer>
  );
};

export default Footer;

