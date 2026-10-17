package com.Singleton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SingletonApplication {

	public static void main(String[] args) {

		//1er módulo
		System.out.println("Módulo de Autenticación");
		Configuracion config1 = Configuracion.getInstancia();
		config1.mostrarMensaje();

		//2do módulo
		System.out.println("Módulo de Reportes");
		Configuracion config2 = Configuracion.getInstancia();
		config2.mostrarMensaje();

		//Comprobación
		System.out.println("¿Ambas referencias en memoria, son iguales?");
		System.out.println(config1==config2); //debería devolver true
		System.out.println(config1);
		System.out.println(config2);
	}

}
