package com.example.club.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Audited
@Table(name = "registro_acceso")
public class RegistroAcceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vinculamos usando el DNI u objeto genérico para aceptar Socios o Familiares
    private Integer dniPersona;

    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida; // Nullable hasta que salga
}
