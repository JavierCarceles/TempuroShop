import React from "react";
import type { Product } from "../../types/Product";
import "../../assets/css/Home.css";

interface ProductCardProps {
  product: Product;
}

const ProductCard: React.FC<ProductCardProps> = ({ product }) => {
  return (
    <div className="productCard">
      <img src={product.image} alt={product.name} className="productImg" />
      <h2 className="productName">{product.name}</h2>
      <p className="productPrice">€{product.price.toFixed(2)}</p>
      <p className="productDescription">{product.description}</p>
      <p className={`productStock ${product.stock > 0 ? "inStock" : "outStock"}`}>
        {product.stock > 0 ? `Disponible: ${product.stock}` : "Agotado"}
      </p>
      <button
        className="buyButton"
        disabled={product.stock === 0}
        onClick={() => window.location.href = `/product/${product.id}`}
      >
        Comprar
      </button>
    </div>
  );
};

export default ProductCard;