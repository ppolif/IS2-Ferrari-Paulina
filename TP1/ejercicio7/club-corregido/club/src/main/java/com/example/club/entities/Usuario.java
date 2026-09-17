package com.example.club.entities;


import com.example.club.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String correo;
    private String clave;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean eliminado = false;
}
