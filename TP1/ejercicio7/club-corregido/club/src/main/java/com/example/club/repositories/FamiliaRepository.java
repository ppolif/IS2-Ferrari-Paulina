package com.example.club.repositories;

import com.example.club.entities.Familia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamiliaRepository extends JpaRepository<Familia, Long> {
    Optional<Familia> findByApellido(String apellido);
}
