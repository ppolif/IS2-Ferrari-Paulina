package com.example.club.repositories;

import com.example.club.entities.Usuario;
import com.example.club.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    // Usado por DataInitializer para saber si hace falta crear el primer
    // administrador "semilla" al arrancar la aplicación.
    boolean existsByRolAndEliminadoFalse(Rol rol);
}
