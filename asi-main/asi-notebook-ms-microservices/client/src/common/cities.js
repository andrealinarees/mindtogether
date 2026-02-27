// Lista de ciudades españolas con sus coordenadas
// Usada para el selector de ciudad en registro/perfil y para obtener clima
export const CITY_COORDINATES = {
  'A Coruña': { latitude: 43.3623, longitude: -8.4115 },
  'Albacete': { latitude: 38.9943, longitude: -1.8585 },
  'Alicante': { latitude: 38.3452, longitude: -0.4810 },
  'Almería': { latitude: 36.8381, longitude: -2.4597 },
  'Ávila': { latitude: 40.6561, longitude: -4.6813 },
  'Badajoz': { latitude: 38.8794, longitude: -6.9707 },
  'Barcelona': { latitude: 41.3851, longitude: 2.1734 },
  'Bilbao': { latitude: 43.2630, longitude: -2.9350 },
  'Burgos': { latitude: 42.3439, longitude: -3.6970 },
  'Cáceres': { latitude: 39.4753, longitude: -6.3724 },
  'Cádiz': { latitude: 36.5271, longitude: -6.2886 },
  'Castellón': { latitude: 39.9864, longitude: -0.0513 },
  'Ceuta': { latitude: 35.8894, longitude: -5.3213 },
  'Ciudad Real': { latitude: 38.9848, longitude: -3.9276 },
  'Córdoba': { latitude: 37.8882, longitude: -4.7794 },
  'Cuenca': { latitude: 40.0704, longitude: -2.1374 },
  'Gijón': { latitude: 43.5322, longitude: -5.6611 },
  'Girona': { latitude: 41.9794, longitude: 2.8214 },
  'Granada': { latitude: 37.1773, longitude: -3.5986 },
  'Guadalajara': { latitude: 40.6331, longitude: -3.1674 },
  'Huelva': { latitude: 37.2614, longitude: -6.9447 },
  'Huesca': { latitude: 42.1401, longitude: -0.4080 },
  'Jaén': { latitude: 37.7796, longitude: -3.7849 },
  'Las Palmas': { latitude: 28.1236, longitude: -15.4366 },
  'León': { latitude: 42.5987, longitude: -5.5671 },
  'Lleida': { latitude: 41.6175, longitude: 0.6200 },
  'Logroño': { latitude: 42.4627, longitude: -2.4450 },
  'Lugo': { latitude: 43.0097, longitude: -7.5567 },
  'Madrid': { latitude: 40.4168, longitude: -3.7038 },
  'Málaga': { latitude: 36.7213, longitude: -4.4214 },
  'Melilla': { latitude: 35.2923, longitude: -2.9381 },
  'Murcia': { latitude: 37.9922, longitude: -1.1307 },
  'Ourense': { latitude: 42.3361, longitude: -7.8640 },
  'Oviedo': { latitude: 43.3614, longitude: -5.8493 },
  'Palencia': { latitude: 42.0096, longitude: -4.5288 },
  'Palma': { latitude: 39.5696, longitude: 2.6502 },
  'Pamplona': { latitude: 42.8125, longitude: -1.6458 },
  'Pontevedra': { latitude: 42.4334, longitude: -8.6444 },
  'Salamanca': { latitude: 40.9701, longitude: -5.6635 },
  'San Sebastián': { latitude: 43.3183, longitude: -1.9812 },
  'Santander': { latitude: 43.4623, longitude: -3.8100 },
  'Santiago': { latitude: 42.8782, longitude: -8.5448 },
  'Segovia': { latitude: 40.9429, longitude: -4.1088 },
  'Sevilla': { latitude: 37.3891, longitude: -5.9845 },
  'Soria': { latitude: 41.7665, longitude: -2.4790 },
  'Tarragona': { latitude: 41.1189, longitude: 1.2445 },
  'Tenerife': { latitude: 28.4682, longitude: -16.2546 },
  'Teruel': { latitude: 40.3456, longitude: -1.1065 },
  'Toledo': { latitude: 39.8628, longitude: -4.0273 },
  'Valencia': { latitude: 39.4699, longitude: -0.3763 },
  'Valladolid': { latitude: 41.6523, longitude: -4.7245 },
  'Vigo': { latitude: 42.2328, longitude: -8.7226 },
  'Vitoria': { latitude: 42.8467, longitude: -2.6716 },
  'Zamora': { latitude: 41.5034, longitude: -5.7467 },
  'Zaragoza': { latitude: 41.6488, longitude: -0.8891 }
};

// Obtener lista de ciudades ordenadas alfabéticamente
export const getCityList = () => {
  return Object.keys(CITY_COORDINATES).sort();
};

// Obtener coordenadas de una ciudad
export const getCityCoordinates = (cityName) => {
  // Normalizar el nombre de la ciudad (buscar coincidencia exacta)
  const normalizedCity = Object.keys(CITY_COORDINATES).find(
    city => city.toLowerCase() === cityName.toLowerCase()
  );
  return CITY_COORDINATES[normalizedCity] || CITY_COORDINATES['Madrid']; // Madrid por defecto
};

