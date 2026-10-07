import { useState } from "react";
import reactLogo from "./assets/react.svg";
import "./App.css";
import { InputCiudad } from "./component/InputCiudad";
import { ObtenerClima } from "./api/ObtenerClima";
import { BottonConsulta } from "./component/BottonConsultar";
import { DisplayClima } from "./component/DisplayClima";

function App() {
  const [ciudad, setCiudad] = useState("");
  const [clima, setClima] = useState("");

  const handlerObtenerClima = () => {
    ObtenerClima(ciudad, setClima);
  };

  return (
    <>
      <div className="container">
        <h1 className="mt-3"> Busca el clima Actual</h1>
        <InputCiudad ciudad={ciudad} setCiudad={setCiudad} />
        <BottonConsulta obtenerClima={handlerObtenerClima} />
        <DisplayClima clima={clima} />
      </div>
    </>
  );
}

export default App;
