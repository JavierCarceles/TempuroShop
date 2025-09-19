import { refreshAccessToken } from "./Auth";

/**
 * Wrapper para fetch que añade automáticamente:
 * - Authorization con access token
 * - credentials: include (cookie refresh_token)
 * - refresh automático si da 401
 */
export async function apiFetch(url: string, options: RequestInit = {}) {
  // Aseguramos que headers sea un objeto
  const token = localStorage.getItem("token");
  options.headers = {
    ...(options.headers as Record<string, string>),
    "Content-Type": "application/json",
    ...(token ? { "Authorization": `Bearer ${token}` } : {}),
  };

  // Enviar cookies (refresh_token) automáticamente
  options.credentials = "include";

  let response = await fetch(url, options);

  // Si el access token ha caducado
  if (response.status === 401) {
    try {
      await refreshAccessToken(); // refresca token y lo guarda en localStorage
      options.headers = {
        ...(options.headers as Record<string, string>),
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
      };
      response = await fetch(url, options);
    } catch {
      // Si refrescar falla → forzar login
      window.location.href = "/login";
      return response;
    }
  }

  return response;
}
