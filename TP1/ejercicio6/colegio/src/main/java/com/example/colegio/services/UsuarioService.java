package com.example.colegio.services;

import com.example.colegio.dto.CambioClaveDTO;
import com.example.colegio.entities.Usuario;
import com.example.colegio.enums.Rol;
import com.example.colegio.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(String correo, String clavePlana, Rol rol) throws Exception {
        if (usuarioRepository.findByCorreo(correo).isPresent()) {
            throw new Exception("El correo electrónico ya se encuentra registrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clavePlana));
        usuario.setRol(rol);
        return usuario;
    }

    @Transactional
    public void cambiarClave(String correo, CambioClaveDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getClaveActual(), usuario.getClave())) {
            throw new Exception("La contraseña actual es incorrecta");
        }

        usuario.setClave(passwordEncoder.encode(dto.getNuevaClave()));
        usuarioRepository.save(usuario);
    }
}
