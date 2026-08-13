// Usa localhost mientras desarrollas en tu PC:
const API_BASE_URL = 'http://localhost:8080'; 

// URL de OCI para cuando el despliegue esté verificado:
// const API_BASE_URL = 'http://146.181.61.160:8080';
export const clasificarContenido = async (titulo, texto) => {
  const response = await fetch(`${API_BASE_URL}/contenido`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ titulo, texto }),
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Error al clasificar el contenido');
  }
  return await response.json();
};

export const obtenerHistorial = async (categoria = '') => {
  const url = categoria 
    ? `${API_BASE_URL}/contenido?categoria=${categoria}`
    : `${API_BASE_URL}/contenido`;
    
  const response = await fetch(url);
  if (!response.ok) throw new Error('Error al cargar historial');
  return await response.json();
};