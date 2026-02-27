import HTTP from "@/common/http";

const resource = "categories";

export default {
  // Obtener todas las categorías
  async findAll() {
    return (await HTTP.get(resource)).data;
  }
};
