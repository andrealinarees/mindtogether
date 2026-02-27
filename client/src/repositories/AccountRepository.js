import HTTP from "../common/http";

const resource = "account";

export default {
  // Autenticar usuario
  async authenticate(credentials) {
    return (await HTTP.post(`${resource}/authenticate`, credentials)).data;
  },

  // Obtener datos de la cuenta actual
  async getAccount() {
    return (await HTTP.get(`${resource}`)).data;
  },

  // Registrar nueva cuenta
  async registerAccount(user) {
    return (await HTTP.post(`${resource}/register`, user)).data;
  },

  // Actualizar datos de la cuenta
  async updateAccount(user) {
    return (await HTTP.put(`${resource}`, user)).data;
  }
};
