package com.example.club.audit;


import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import java.util.Date;

@Entity
@Table(name = "revision_info")
@RevisionEntity(CustomRevisionListener.class)
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_seq")
    @SequenceGenerator(name = "revision_seq", sequenceName = "seq_revision_id", allocationSize = 1)
    @RevisionNumber
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @RevisionTimestamp
    private Date fecha;

    private String usuario;

    // Getters y Setters
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
