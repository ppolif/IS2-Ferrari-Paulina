package com.example.colegio.services;

import com.example.colegio.dto.CambioClaveDTO;
import com.example.colegio.dto.RegistroProfesorDTO;
import com.example.colegio.entities.Profesor;
import com.example.colegio.entities.Usuario;
import com.example.colegio.enums.Rol;
import com.example.colegio.repositories.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Service: Informa a Spring que contiene lógica de negocio.
 */
@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository profesorRepo;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * @Transactional: Abre y cierra transacciones de BD automáticamente.
     */
    @Transactional
    public void registrarProfesor(RegistroProfesorDTO dto) throws Exception {
        Usuario u = usuarioService.crearUsuario(dto.getCorreo(), dto.getClave(), Rol.PROFESOR);

        Profesor p = new Profesor();
        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setDni(dto.getDni());
        p.setSexo(dto.getSexo());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setTitulo(dto.getTitulo());
        p.setUsuario(u);

        profesorRepo.save(p);

        // El envío de correo se maneja con try-catch para que una falla en SMTP no provoque un rollback en la BD
        try {
            enviarCorreoBienvenida(u.getCorreo(), p.getNombre());
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo enviar el correo de bienvenida: " + e.getMessage());
        }
    }

    @Transactional
    public void cambiarClave(String correo, CambioClaveDTO dto) throws Exception {
        usuarioService.cambiarClave(correo, dto);
    }

    private void enviarCorreoBienvenida(String correo, String nombre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(correo);
        message.setSubject("Bienvenido al Sistema Académico");
        message.setText("Hola " + nombre + ", tu cuenta ha sido creada exitosamente.");
        mailSender.send(message);
    }

    @Transactional
    public void modificarProfesor(Long id, RegistroProfesorDTO dto) throws Exception {
        Profesor p = profesorRepo.findById(id)
                .orElseThrow(() -> new Exception("Profesor no encontrado"));

        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setDni(dto.getDni());
        p.setSexo(dto.getSexo());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setTitulo(dto.getTitulo());

        // Como el registro ya existe en la BD, save() hace un UPDATE automático.
        profesorRepo.save(p);
    }

    @Transactional
    public void eliminarProfesor(Long id) {
        // Ejecuta un DELETE FROM tabla WHERE id = ?.
        // Esto también borrará al Usuario asociado gracias a la configuración en cascada de JPA.
        profesorRepo.deleteById(id);
    }
}
