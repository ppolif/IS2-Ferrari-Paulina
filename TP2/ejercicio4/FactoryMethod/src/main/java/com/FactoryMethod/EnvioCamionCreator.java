package com.FactoryMethod;

public class EnvioCamionCreator extends EnvioCreator{
    @Override
    protected Envio crearEnvio() {
        return new EnvioCamion();
    }
}
