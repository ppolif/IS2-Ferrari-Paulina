package com.example.club.services;

import com.example.club.dto.DTOs;
import com.example.club.entities.Imagen;
import com.example.club.entities.Socio;
import com.example.club.entities.Usuario;
import com.example.club.enums.Rol;
import com.example.club.repositories.SocioRepository;
import com.example.club.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa SERVICE: contiene la lógica de negocio de Usuario (login/seguridad).
 * Los Controllers nunca hablan directo con los Repository: siempre pasan por
 * el Service, que es quien orquesta transacciones, validaciones y el uso de
 * otros Services/Repositories relacionados (Socio, Imagen).
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Alta de un Socio (o de otro Administrador) realizada EXCLUSIVAMENTE por un ADMIN.
     * Crea el Usuario (credenciales) y, si el rol es SOCIO, crea también la
     * Persona Socio asociada en la misma transacción (@Transactional: si algo
     * falla, se revierte todo -> no queda un Usuario "huérfano" sin Socio).
     */
    @Transactional
    public void altaDeSocio(DTOs.AltaSocioDTO dto) throws Exception {
        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new Exception("El correo ya está registrado");
        }
        if (dto.getClave() == null || !dto.getClave().equals(dto.getRepetirClave())) {
            throw new Exception("Las contraseñas no coinciden");
        }

        Usuario u = new Usuario();
        u.setCorreo(dto.getCorreo());
        u.setClave(passwordEncoder.encode(dto.getClave()));
        u.setRol(dto.getRol() != null ? dto.getRol() : Rol.SOCIO);
        u.setEliminado(false);
        usuarioRepository.save(u); // INSERT INTO usuario

        // Solo generamos la ficha de Socio si la cuenta creada es de tipo SOCIO
        if (u.getRol() == Rol.SOCIO) {
            Socio s = new Socio();
            s.setNombre(dto.getNombre());
            s.setApellido(dto.getApellido());
            s.setDni(dto.getDni());
            s.setUsuario(u);

            // Correlativo simple de número de socio
            Integer siguienteNro = (int) (socioRepository.count() + 1);
            s.setNroSocio(siguienteNro);

            if (dto.getArchivoFoto() != null && !dto.getArchivoFoto().isEmpty()) {
                Imagen img = imagenService.crearImagen(dto.getArchivoFoto());
                s.setFoto(img);
            }

            socioRepository.save(s); // INSERT INTO socio
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(); // SELECT * FROM usuario
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) throws Exception {
        return usuarioRepository.findById(id).orElseThrow(() -> new Exception("Usuario no encontrado"));
    }

    @Transactional
    public void actualizarUsuario(String id, DTOs.UsuarioDTO dto) throws Exception {
        Usuario u = buscarPorId(id);
        u.setCorreo(dto.getCorreo());
        if (dto.getClave() != null && !dto.getClave().isEmpty()) {
            u.setClave(passwordEncoder.encode(dto.getClave()));
        }
        u.setRol(dto.getRol());
        usuarioRepository.save(u); // UPDATE usuario
    }

    /**
     * Baja LÓGICA (no física) del usuario: se marca "eliminado" en lugar de
     * hacer DELETE, para no perder el historial de auditoría (Envers) ni
     * romper relaciones ya persistidas (pagos, accesos, etc.).
     */
    @Transactional
    public void eliminarUsuario(String id) throws Exception {
        Usuario u = buscarPorId(id);
        u.setEliminado(true);
        usuarioRepository.save(u);
    }
}
