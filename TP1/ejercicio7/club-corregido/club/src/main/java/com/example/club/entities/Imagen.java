package com.example.club.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "imagen")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String nombre;
    private String mime;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] contenido;

    private boolean eliminado = false;
}
