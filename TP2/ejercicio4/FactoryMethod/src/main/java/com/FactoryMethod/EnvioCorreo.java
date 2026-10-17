package com.FactoryMethod;

public class EnvioCorreo implements Envio {
    @Override
    public void enviarPaquete() {
        System.out.println("Enviando paquete mediante correo postal...");
    }
}
