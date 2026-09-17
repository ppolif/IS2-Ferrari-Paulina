package com.example.club.entities;


import com.example.club.enums.EstadoPago;
import com.example.club.enums.FormaPago;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import java.time.LocalDate;

@Data
@Entity
@Audited
@Table(name = "pago_cuota")
public class PagoCuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "familia_id")
    private Familia familia; // Si paga el grupo familiar

    @ManyToOne
    @JoinColumn(name = "socio_id")
    private Socio socio; // Si paga individual (sin familia)

    private Double monto;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    private String comprobante; // Código de transferencia/MP

    private LocalDate fechaPago;
}