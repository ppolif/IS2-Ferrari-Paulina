package com.example.colegio.services;

import com.example.colegio.dto.RegistroAlumnoDTO;
import com.example.colegio.entities.Alumno;
import com.example.colegio.entities.Usuario;
import com.example.colegio.enums.Rol;
import com.example.colegio.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * La anotación @Service le avisa al framework que la clase tiene un rol de servicio dentro del sistema.
 */
@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * La anotación @Transactional abre y cierra la transacción de forma automática.
     */
    @Transactional
    public void registrarAlumno(RegistroAlumnoDTO dto) throws Exception {
        Usuario u = usuarioService.crearUsuario(dto.getCorreo(), dto.getClave(), Rol.ALUMNO);

        Alumno a = new Alumno();
        a.setNombre(dto.getNombre());
        a.setApellido(dto.getApellido());
        a.setDni(dto.getDni());
        a.setSexo(dto.getSexo());
        a.setFechaNacimiento(dto.getFechaNacimiento());
        a.setGrado(dto.getGrado());
        a.setLegajo(dto.getLegajo());

        // Asociamos el usuario al alumno
        a.setUsuario(u);

        alumnoRepo.save(a);
    }

    @Transactional
    public void modificarAlumno(Long id, RegistroAlumnoDTO dto) throws Exception {
        Alumno a = alumnoRepo.findById(id)
                .orElseThrow(() -> new Exception("Alumno no encontrado"));

        a.setNombre(dto.getNombre());
        a.setApellido(dto.getApellido());
        a.setDni(dto.getDni());
        a.setSexo(dto.getSexo());
        a.setFechaNacimiento(dto.getFechaNacimiento());
        a.setGrado(dto.getGrado());
        a.setLegajo(dto.getLegajo());

        alumnoRepo.save(a);
    }

    @Transactional
    public void eliminarAlumno(Long id) {
        alumnoRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Alumno> listarPorMateria(Long materiaId) {
        return alumnoRepo.findByMateriasCursadasId(materiaId);
    }
}
