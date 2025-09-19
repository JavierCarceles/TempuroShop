import React, { useEffect, useState } from "react";
import ParticleBackground from "../../components/ParticleBackground";
import Footer from "../../components/Footer";
import "../../assets/css/Home.css";
import type { Product } from "../../types/Product";
import { fetchProducts } from "../../services/productService";
import ProductCard from "./ProductCard";
import { useNavigate } from "react-router-dom";
import logoTienda from "../../assets/imgs/logoTienda.png";



const Home: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchProducts();
        setProducts(data);
      } catch (err) {
        console.error("Error cargando productos:", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const toggleMenu = () => setMenuOpen(!menuOpen);
  const goToLogin = () => navigate("/login");

  return (
    <>
      <ParticleBackground />

      <nav className="navbar">
        <div className="navLeft" onClick={() => navigate("/")}>
         <img src={logoTienda} alt="Logo TempuroShop"/>
        </div>

        <div className="navCenter">
          <input type="text" placeholder="Buscar productos..." />
        </div>

        <div className="navRight">
          <button className="hamburger" onClick={toggleMenu}>
            <span className="line"></span>
            <span className="line"></span>
            <span className="line"></span>
            <span className="fallback-text">Menu</span>
          </button>

          {menuOpen && (
            <div className="menuDropdown">
              <div className="menuItem" onClick={goToLogin}>
                Login
              </div>
            </div>
          )}
        </div>
      </nav>

      <section className="homePage">
        <h1 className="homeTitle">
          🔥 Bienvenido a <span className="highlight">TempuroShop</span>
        </h1>

        {loading && <p className="loadingText">Cargando productos...</p>}

        <div className="productGrid">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>

      <Footer />
    </>
  );
};

export default Home;
