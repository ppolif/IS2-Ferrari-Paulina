package com.Builder;

//2 Builder general
public interface BuilderCasa {

    void reiniciar();
    void construirEstructura(String estructura);
    void contruirPisos(int cantidad);
    void construirPiscina(boolean siONo);
    void construirGarage(boolean siONo);
    void construirJardin(boolean siONo);
    void agregarExtra(String extra);

}