import HTTP from "../common/http";

const resource = "users";

export default {
  // Listar todos los usuarios (solo ADMIN)
  async findAll() {
    return (await HTTP.get(`${resource}`)).data;
  },

  // Obtener un usuario por ID
  async findOne(id) {
    return (await HTTP.get(`${resource}/${id}`)).data;
  },

  // Activar un usuario (solo ADMIN)
  async activate(id) {
    return (await HTTP.put(`${resource}/${id}/active`)).data;
  },

  // Desactivar un usuario (solo ADMIN)
  async deactivate(id) {
    return await HTTP.delete(`${resource}/${id}/active`);
  },

  // Eliminar un usuario (solo ADMIN)
  async delete(id) {
    return await HTTP.delete(`${resource}/${id}`);
  }
};
