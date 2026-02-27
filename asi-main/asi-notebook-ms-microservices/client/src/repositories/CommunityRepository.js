import HTTP from "../common/http";

const resource = "communities";

export default {
  // Listar todas las comunidades
  async findAll() {
    return (await HTTP.get(`${resource}`)).data;
  },

  // Listar mis comunidades
  async findMyCommunities() {
    return (await HTTP.get(`${resource}/my-communities`)).data;
  },

  // Obtener una comunidad por ID
  async findById(id) {
    return (await HTTP.get(`${resource}/${id}`)).data;
  },

  // Buscar comunidades
  async search(query) {
    return (await HTTP.get(`${resource}/search`, { params: { query } })).data;
  },

  // Crear una nueva comunidad
  async create(community) {
    return (await HTTP.post(`${resource}`, community)).data;
  },

  // Actualizar una comunidad
  async update(id, community) {
    return (await HTTP.put(`${resource}/${id}`, community)).data;
  },

  // Eliminar una comunidad
  async delete(id) {
    return await HTTP.delete(`${resource}/${id}`);
  },

  // Unirse a una comunidad
  async join(id) {
    return (await HTTP.post(`${resource}/${id}/join`)).data;
  },

  // Abandonar una comunidad
  async leave(id) {
    return await HTTP.delete(`${resource}/${id}/leave`);
  },

  // Obtener miembros de una comunidad
  async getMembers(id) {
    return (await HTTP.get(`${resource}/${id}/members`)).data;
  },

  // Obtener número de miembros
  async getMemberCount(id) {
    return (await HTTP.get(`${resource}/${id}/members/count`)).data;
  },

  // Obtener publicaciones de una comunidad
  async getEntries(id) {
    return (await HTTP.get(`${resource}/${id}/entries`)).data;
  },

  // Crear una publicación en una comunidad
  async createEntry(id, entry) {
    return (await HTTP.post(`${resource}/${id}/entries`, entry)).data;
  },

  // Actualizar una publicación
  async updateEntry(communityId, entryId, entry) {
    return (await HTTP.put(`${resource}/${communityId}/entries/${entryId}`, entry)).data;
  },

  // Eliminar una publicación
  async deleteEntry(communityId, entryId) {
    return await HTTP.delete(`${resource}/${communityId}/entries/${entryId}`);
  }
};

