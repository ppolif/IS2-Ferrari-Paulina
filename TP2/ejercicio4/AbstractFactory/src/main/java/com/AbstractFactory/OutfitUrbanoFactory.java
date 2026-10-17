package com.AbstractFactory;

public class OutfitUrbanoFactory implements OutfitFactory{
    @Override
    public Jean crearJean() {
        return new JeanCargo();
    }

    @Override
    public Remera crearRemera() {
        return new RemeraOversize();
    }
}
