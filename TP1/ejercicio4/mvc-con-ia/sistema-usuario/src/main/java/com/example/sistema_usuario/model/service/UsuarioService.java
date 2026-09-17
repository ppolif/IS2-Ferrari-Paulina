package com.example.sistema_usuario.model.service;

import com.example.sistema_usuario.model.domain.Usuario;
import com.example.sistema_usuario.model.persistencia.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;


/**
 * CAPA DE SERVICIO (Business Logic)
 * Ahora incluye los métodos extraídos del diagrama de clases.
 */

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Verifica que la clave y su repetición sean idénticas.
     * Basado en: comprobarNuevaContraseña(clave:String, repeticion:String):Bool
     */
    public boolean comprobarNuevaContraseña(String clave, String repeticion) {
        return clave != null && clave.equals(repeticion);
    }

    /**
     * Valida si un correo ya existe en el sistema.
     * Basado en: validarCorreoExistente(correo:String):bool
     */
    public boolean validarCorreoExistente(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
    }

    /**
     * Registro actualizado que incluye validaciones previas.
     */
    public void registrarUsuario(Usuario usuario, String repeticionClave) throws Exception {
        // 1. Validar que las contraseñas coincidan
        if (!comprobarNuevaContraseña(usuario.getClave(), repeticionClave)) {
            throw new Exception("Las contraseñas no coinciden. Intente nuevamente.");
        }

        // 2. Validar que el correo no esté en uso
        if (validarCorreoExistente(usuario.getCorreo())) {
            throw new Exception("El correo ingresado ya se encuentra registrado.");
        }

        usuario.setActivo(true);
        usuario.setIntentosPorDia(0);
        usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario mediante su correo.
     * Basado en: buscarUsuario(correo:String): Usuario
     */
    public Usuario buscarUsuario(String correo) {
        // Retorna el usuario si lo encuentra, o null si no existe.
        return usuarioRepository.findByCorreo(correo).orElse(null);
    }

    /**
     * Borra un usuario validando credenciales y estado.
     * Basado en: borrarUsuario(correo:String, clave:String, activo:bool):void
     */
    public void borrarUsuario(String correo, String clave, boolean activo) throws Exception {
        Usuario usuario = buscarUsuario(correo);

        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }

        // Verificamos que la clave coincida y el estado coincida con lo solicitado
        if (usuario.getClave().equals(clave) && usuario.isActivo() == activo) {
            usuarioRepository.delete(usuario);
        } else {
            throw new Exception("Credenciales o estado incorrecto para eliminar la cuenta.");
        }
    }

    /**
     * Edita los datos de un usuario existente buscándolo por su correo.
     * Basado en: editarUsuario(nombre:String, apellido:String, dni:int, correo:String, fechaNac:Date, clave:String):void
     */
    public void editarUsuario(String nombre, String apellido, int dni, String correo, LocalDate fechaNac, String clave) throws Exception {
        Usuario usuarioExistente = buscarUsuario(correo);

        if (usuarioExistente != null) {
            usuarioExistente.setNombre(nombre);
            usuarioExistente.setApellido(apellido);
            usuarioExistente.setDni(dni);
            usuarioExistente.setFechaNac(fechaNac);
            usuarioExistente.setClave(clave);

            // save() actúa como un "update" si el ID (o entidad) ya existe en la BD
            usuarioRepository.save(usuarioExistente);
        } else {
            throw new Exception("No se puede editar: Usuario no encontrado.");
        }
    }

    /**
     * Lógica equivalente a los métodos ingresar(), bloquearUsuario() y reiniciarIntentos().
     * @return true si el login es exitoso, lanza Excepciones si hay errores o bloqueos.
     */
    // En UsuarioService.java
    public Usuario autenticar(String correo, String clave) throws Exception {
        Optional<Usuario> optUsuario = usuarioRepository.findByCorreo(correo);

        if (optUsuario.isEmpty()) {
            throw new Exception("no_registrado");
        }

        Usuario usuario = optUsuario.get();

        // Verificamos si está bloqueado
        if (!usuario.isActivo()) {
            throw new Exception("cuenta_bloqueada");
        }

        // Comparamos contraseñas
        if (usuario.getClave().equals(clave)) {
            // Reiniciamos intentos
            usuario.setIntentosPorDia(0);
            usuarioRepository.save(usuario);

            // ¡EXCELENTE! Retornamos el objeto completo
            return usuario;
        } else {
            usuario.setIntentosPorDia(usuario.getIntentosPorDia() + 1);

            if (usuario.getIntentosPorDia() >= 3) {
                usuario.setActivo(false);
                usuarioRepository.save(usuario);
                throw new Exception("cuenta_bloqueada");
            }

            usuarioRepository.save(usuario);
            throw new Exception("clave_incorrecta");
        }
    }

}
