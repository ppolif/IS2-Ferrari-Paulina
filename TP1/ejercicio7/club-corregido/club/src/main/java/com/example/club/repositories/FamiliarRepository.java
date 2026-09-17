package com.example.club.repositories;

import com.example.club.entities.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {
}
