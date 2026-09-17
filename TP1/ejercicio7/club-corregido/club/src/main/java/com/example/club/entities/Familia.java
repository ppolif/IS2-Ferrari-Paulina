package com.example.club.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Audited
@Table(name = "familia")
public class Familia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Apellido del grupo familiar (ej: "Familia Gonzalez")
    private String apellido;

    // Encargados del grupo: Socios existentes (con cuenta de acceso) asignados como responsables
    @OneToMany(mappedBy = "familia", cascade = CascadeType.ALL)
    private List<Socio> socios = new ArrayList<>(); // Encargados / Titulares

    // Miembros del grupo sin cuenta propia (hijos, familiares a cargo, etc.)
    @OneToMany(mappedBy = "familia", cascade = CascadeType.ALL)
    private List<Familiar> familiares = new ArrayList<>(); // Adherentes

    // Cantidad total de integrantes del grupo (no persistido, se calcula en runtime)
    @Transient
    @NotAudited
    public int getIntegrantes() {
        int socioCount = socios == null ? 0 : socios.size();
        int familiarCount = familiares == null ? 0 : familiares.size();
        return socioCount + familiarCount;
    }
}
