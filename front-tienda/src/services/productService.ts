import type { Product } from "../types/Product";

const fallbackProducts: Product[] = [
  { id: 1, name: "Auriculares Gaming", price: 59.99, image: "/assets/imgs/auriculares.png", description: "Auriculares con sonido envolvente y micrófono incorporado.", stock: 12 },
  { id: 2, name: "Teclado Mecánico", price: 89.99, image: "/assets/imgs/teclado.png", description: "Teclado mecánico con retroiluminación RGB y switches táctiles.", stock: 5 },
  { id: 3, name: "Monitor 27''", price: 199.99, image: "/assets/imgs/monitor.png", description: "Monitor de 27 pulgadas con resolución 1440p y 144Hz.", stock: 7 },
  { id: 4, name: "Silla Gamer", price: 149.99, image: "/assets/imgs/silla.png", description: "Silla ergonómica con soporte lumbar y reclinable.", stock: 3 },
  { id: 5, name: "Ratón RGB", price: 39.99, image: "/assets/imgs/raton.png", description: "Ratón gamer con iluminación RGB y sensor de alta precisión.", stock: 10 },
  { id: 6, name: "Micrófono Streaming", price: 79.99, image: "/assets/imgs/microfono.png", description: "Micrófono condensador USB ideal para streaming y podcast.", stock: 0 },
];

export async function fetchProducts(): Promise<Product[]> {
  try {
    const res = await fetch("http://localhost:8080/api/products");
    if (!res.ok) throw new Error("Error en la API");
    return await res.json();
  } catch (err) {
    console.error(err);
    console.warn("⚠️ No se pudo conectar a la API, usando productos de ejemplo...");
    return fallbackProducts;
  }
}