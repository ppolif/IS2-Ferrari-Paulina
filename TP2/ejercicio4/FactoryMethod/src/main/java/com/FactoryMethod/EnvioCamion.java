package com.FactoryMethod;

public class EnvioCamion implements Envio{

    @Override
    public void enviarPaquete() {
        System.out.println("Enviando paquete mediante camión...");
    }
}
