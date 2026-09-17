package com.example.club.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Audited
@Table(name = "familiar")
public class Familiar extends Persona {

    private String parentesco;

    @ManyToOne
    @JoinColumn(name = "familia_id", nullable = false)
    private Familia familia;
}
