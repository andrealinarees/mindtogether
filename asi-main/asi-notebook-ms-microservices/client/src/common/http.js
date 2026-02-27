import axios from "axios";
import { BACKEND_URL } from "../constants.js";
import auth from "./auth.js";

const HTTP = axios.create({
  baseURL: BACKEND_URL
});

const onUnauthorized = () => {
  console.error("Access denied!");

  auth.logout();
};
const onResponseSuccess = (response) => response;

// si el servidor nos devuelve un 401 o 403,
// estamos intentando acceder a un recurso sin
// los permisos correctos
const onResponseFailure = (err) => {
  const status = err.response.status;
  // excepto cuando estemos haciendo login
  if (!err.config.url.includes("authenticate")) {
    if (status == 401 || status == 403) {
      onUnauthorized();
    }
  }
  return Promise.reject(err);
};

// en cada request hay que añadir el token de autenticación
// y el userId si es que los tenemos
const onRequest = (config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // Agregar X-User-Id para los microservicios que lo necesiten
  const userId = localStorage.getItem("userId");
  if (userId) {
    config.headers['X-User-Id'] = userId;
  } else {
    console.warn('⚠️ No hay userId en localStorage');
  }

  return config;
};

HTTP.interceptors.response.use(onResponseSuccess, onResponseFailure);
HTTP.interceptors.request.use(onRequest);

export default HTTP;
