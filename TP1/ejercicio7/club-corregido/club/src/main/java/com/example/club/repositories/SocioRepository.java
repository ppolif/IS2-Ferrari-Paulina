package com.example.club.repositories;

import com.example.club.entities.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {


    @Query("SELECT s FROM Socio s WHERE s.nombre LIKE %:q% OR s.apellido LIKE %:q% OR CAST(s.dni AS string) LIKE %:q%")
    List<Socio> findByNombreODni(@Param("q") String q);

    // Socios que todavía no pertenecen a ningún grupo familiar (candidatos a encargados)
    List<Socio> findByFamiliaIsNull();

    // Socio dueño de la cuenta de usuario logueada (para la vista "Mi perfil")
    Optional<Socio> findByUsuario_Correo(String correo);
}
