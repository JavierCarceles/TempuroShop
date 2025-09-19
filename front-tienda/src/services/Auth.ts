// src/services/Auth.ts

export function getAccessTokenExpiry(token: string) {
  const payloadBase64 = token.split('.')[1];
  const decoded = JSON.parse(atob(payloadBase64));
  return decoded.exp * 1000;
}

export async function refreshAccessToken() {
  const response = await fetch("http://localhost:8080/auth/refresh", {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok) {
    window.location.href = "/login";
    return;
  }

  const data = await response.json();
  localStorage.setItem("token", data.accessToken);
  scheduleRefresh(data.accessToken);
}

export function scheduleRefresh(accessToken: string) {
  const expiresIn = getAccessTokenExpiry(accessToken) - Date.now();
  setTimeout(refreshAccessToken, expiresIn - 60_000);
}
