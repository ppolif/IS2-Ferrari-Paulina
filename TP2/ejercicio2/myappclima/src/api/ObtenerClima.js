import axios from 'axios'


export const ObtenerClima = async (ciudad, setClima) => {
  try {
    const response = await axios.get(`http://localhost:8080/ciudad/clima/${ciudad}`);

    console.log(response.data);
    setClima(JSON.stringify(response.data));
    
  } catch (error) {
    console.error('Error al obtener el clima:', error);
  }
}
