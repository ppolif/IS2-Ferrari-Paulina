package com.example.club.dto;

import com.example.club.enums.FormaPago;
import com.example.club.enums.Rol;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

import java.time.LocalDate;

/**
 * Clase contenedora de todos los DTO (Data Transfer Object) de la aplicación.
 * <p>
 * Capa DTO (Arquitectura MVC): los DTO son objetos "planos" que viajan entre
 * la Vista (Thymeleaf) y el Controller, y entre el Controller y el Service.
 * Nunca se expone la Entidad JPA directamente en los formularios: esto evita
 * que Thymeleaf intente enlazar (binding) campos de la entidad que no deberían
 * ser editables (por ejemplo relaciones @OneToMany) y desacopla el modelo de
 * persistencia del modelo de presentación.
 * <p>
 * IMPORTANTE: todas las clases anidadas son "static" para poder instanciarse
 * sin necesidad de una instancia externa de DTOs (de lo contrario Spring no
 * podría crear el objeto de binding "th:object" en los formularios).
 */
public class DTOs {

    /**
     * DTO utilizado por el ADMINISTRADOR para dar de alta un nuevo Socio.
     * Crea, en una sola operación, el Usuario (login) y la Persona Socio asociada.
     * Solo un ADMIN puede acceder al formulario que usa este DTO.
     */
    @Data
    public static class AltaSocioDTO {
        private String nombre;
        private String apellido;
        private Integer dni;
        private String correo;
        private String clave;
        private String repetirClave;
        private Rol rol = Rol.SOCIO; // Rol de la cuenta a crear (SOCIO o ADMIN)
        private MultipartFile archivoFoto;
    }

    /**
     * DTO para editar los datos de un Usuario existente (correo / clave / rol).
     */
    @Data
    public static class UsuarioDTO {
        private String id;
        private String correo;
        private String clave;
        private Rol rol;
    }

    /**
     * DTO para crear o editar un grupo Familiar (solo datos propios, sin listas
     * de socios/familiares: esas relaciones se gestionan con acciones separadas).
     */
    @Data
    public static class FamiliaDTO {
        private Long id;
        private String apellido;
    }

    /**
     * DTO para asociar un Socio EXISTENTE a un grupo familiar como encargado/titular.
     */
    @Data
    public static class AsignarEncargadoDTO {
        private Long idFamilia;
        private Long idSocio;
    }

    /**
     * DTO para agregar un integrante SIN cuenta de acceso (Familiar) a un grupo.
     */
    @Data
    public static class FamiliarDTO {
        private Long id;
        private String nombre;
        private String apellido;
        private Integer dni;
        private String parentesco;
        private Long idFamilia;
        private MultipartFile archivoFoto;
    }

    /**
     * DTO para registrar el pago de la cuota. El pago puede aplicarse a un
     * grupo Familiar completo (idFamilia) o a un Socio individual sin familia
     * (idSocio). Solo uno de los dos debe completarse.
     */
    @Data
    public static class PagoCuotaDTO {
        private Long id;
        private Long idSocio;
        private Long idFamilia;
        private Double monto;
        private FormaPago formaPago;
        private String comprobante; // Código de transferencia / Mercado Pago
        private LocalDate fechaPago;
    }

    /**
     * DTO utilizado para registrar entrada/salida por portería (lector de DNI).
     */
    @Data
    public static class AccesoDTO {
        private Integer dni;
    }
}
