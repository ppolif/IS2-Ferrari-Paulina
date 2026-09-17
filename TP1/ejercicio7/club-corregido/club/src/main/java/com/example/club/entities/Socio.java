package com.example.club.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Audited
@Table(name = "socio")
public class Socio extends Persona {

    @Column(unique = true)
    private Integer nroSocio;

    @ManyToOne
    @JoinColumn(name = "familia_id")
    private Familia familia; // Puede ser null si es individual
}
