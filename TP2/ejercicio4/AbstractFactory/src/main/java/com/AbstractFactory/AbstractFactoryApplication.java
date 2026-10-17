package com.AbstractFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AbstractFactoryApplication {

	public static void main(String[] args) {
		//Seleccionamos una familia de productos
		OutfitFactory factory = new OutfitEleganteFactory();

		//Cliente trabaja o va a interactuar SIN SABER las clases concretas
		TiendaRopa tienda = new TiendaRopa(factory);

		tienda.mostrarOutfit();
	}

}
