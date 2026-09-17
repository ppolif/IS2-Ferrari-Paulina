package com.example.colegio.services;

import com.example.colegio.entities.Alumno;
import com.example.colegio.entities.Materia;
import com.example.colegio.entities.Nota;
import com.example.colegio.repositories.AlumnoRepository;
import com.example.colegio.repositories.MateriaRepository;
import com.example.colegio.repositories.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotaService {

    @Autowired
    private MateriaRepository materiaRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private NotaRepository notaRepo;

    @Transactional
    public void registrarNota(Long materiaId, Long alumnoId, int valor, String correoProfesor) throws Exception {
        if (valor < 1 || valor > 10) {
            throw new Exception("La calificación debe ser un valor numérico entre 1 y 10.");
        }

        Materia materia = materiaRepo.findById(materiaId)
                .orElseThrow(() -> new Exception("Materia no encontrada."));

        // Validar que el profesor asignado a la materia sea el que intenta registrar la nota
        if (materia.getProfesor() == null || materia.getProfesor().getUsuario() == null
                || !materia.getProfesor().getUsuario().getCorreo().equalsIgnoreCase(correoProfesor)) {
            throw new Exception("No tienes autorización para calificar en esta materia.");
        }

        Alumno alumno = alumnoRepo.findById(alumnoId)
                .orElseThrow(() -> new Exception("Alumno no encontrado."));

        // Validar que el alumno esté inscripto en la materia
        if (materia.getAlumnos() == null || materia.getAlumnos().stream().noneMatch(a -> a.getId().equals(alumnoId))) {
            throw new Exception("El alumno no se encuentra inscripto en esta materia.");
        }

        Nota nota = new Nota();
        nota.setValor(valor);
        nota.setAlumno(alumno);

        if (materia.getNotas() == null) {
            materia.setNotas(new ArrayList<>());
        }
        materia.getNotas().add(nota);
        materiaRepo.save(materia);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<Nota>> obtenerNotasPorMateriaAgrupadasPorAlumno(Long materiaId) throws Exception {
        Materia materia = materiaRepo.findById(materiaId)
                .orElseThrow(() -> new Exception("Materia no encontrada."));

        if (materia.getNotas() == null) {
            return Collections.emptyMap();
        }

        return materia.getNotas().stream()
                .filter(n -> n.getAlumno() != null)
                .collect(Collectors.groupingBy(n -> n.getAlumno().getId()));
    }

    @Transactional(readOnly = true)
    public List<Nota> obtenerNotasPorAlumno(Long alumnoId) {
        return notaRepo.findByAlumnoId(alumnoId);
    }
}
