package com.FactoryMethod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FactoryMethodApplication {

	public static void main(String[] args) {

		EnvioCreator envioCorreo = new EnvioCorreoCreator();
		envioCorreo.procesarEnvio();

		EnvioCreator envioMoto = new EnvioMotoCreator();
		envioMoto.procesarEnvio();

		EnvioCreator envioCamion = new EnvioCamionCreator();
		envioCamion.procesarEnvio();
	}

}
