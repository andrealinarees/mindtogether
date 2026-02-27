import axios from "axios";

const OPEN_METEO_BASE_URL = "https://api.open-meteo.com/v1";

export default {
  /**
   * Obtener el clima actual para unas coordenadas
   * @param {number} latitude - Latitud
   * @param {number} longitude - Longitud
   * @param {string} timezone - Zona horaria
   */
  async getCurrentWeather(latitude, longitude, timezone = "Europe/Madrid") {
    const params = {
      latitude,
      longitude,
      current_weather: true,
      timezone
    };
    return (await axios.get(`${OPEN_METEO_BASE_URL}/forecast`, { params })).data;
  },

  /**
   * Obtener pronóstico diario del clima
   * @param {number} latitude - Latitud
   * @param {number} longitude - Longitud
   * @param {string} timezone - Zona horaria
   */
  async getDailyForecast(latitude, longitude, timezone = "Europe/Madrid") {
    const params = {
      latitude,
      longitude,
      daily: "temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode",
      timezone,
      forecast_days: 7
    };
    return (await axios.get(`${OPEN_METEO_BASE_URL}/forecast`, { params })).data;
  },

  /**
   * Obtener sugerencia de hábitos según el clima
   * @param {Object} currentWeather - Datos del clima actual de Open-Meteo
   * @param {string} habitLocation - Ubicación del hábito (INTERIOR/EXTERIOR)
   */
  getWeatherSuggestion(currentWeather, habitLocation) {
    if (!currentWeather || !currentWeather.current_weather) {
      return { suggestion: "No hay datos meteorológicos disponibles", type: "info" };
    }

    const { temperature, weathercode } = currentWeather.current_weather;

    // Códigos WMO Weather interpretation:
    // 0: Despejado
    // 1-3: Parcialmente nublado
    // 45,48: Niebla
    // 51-67: Lluvia
    // 71-77: Nieve
    // 80-99: Lluvia intensa/tormenta

    const isRaining = weathercode >= 51 && weathercode <= 67;
    const isStorming = weathercode >= 80;
    const isSnowing = weathercode >= 71 && weathercode <= 77;
    const isClear = weathercode <= 3;

    if (habitLocation === "EXTERIOR") {
      if (isRaining || isStorming) {
        return {
          suggestion: "⚠️ Hoy llueve. Recuerda que si tienes hábitos de exterior, el clima no es favorable.",
          type: "warning"
        };
      } else if (isSnowing) {
        return {
          suggestion: "❄️ Está nevando. Las condiciones no son ideales para hábitos de exterior.",
          type: "warning"
        };
      } else if (isClear && temperature > 15 && temperature < 30) {
        return {
          suggestion: "☀️ Día perfecto para actividades de exterior. ¡Aprovecha el buen clima! Recuerda aplicarte crema solar.",
          type: "success"
        };
      } else if (temperature >= 30) {
        return {
          suggestion: "🌡️ Día caluroso. Si realizas hábitos de exterior, hidrátate bien y protégete del sol.",
          type: "info"
        };
      } else if (temperature < 10) {
        return {
          suggestion: "🥶 Hace frío. Si sales, abrígate bien para tus hábitos de exterior.",
          type: "info"
        };
      } else {
        return {
          suggestion: "🌤️ Buen día para tus hábitos de exterior.",
          type: "success"
        };
      }
    } else {
      // Hábito INTERIOR
      if (isRaining || isStorming || isSnowing) {
        return {
          suggestion: "🏠 Perfecto día para enfocarte en tus hábitos de interior.",
          type: "success"
        };
      } else if (isClear && temperature > 15 && temperature < 30) {
        return {
          suggestion: "☀️ Aunque el clima está excelente, aprovecha para completar tus hábitos de interior también.",
          type: "info"
        };
      } else {
        return {
          suggestion: "✅ Buen momento para tus hábitos de interior.",
          type: "success"
        };
      }
    }
  }
};
