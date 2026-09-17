package com.example.club.services;

import com.example.club.entities.Imagen;
import com.example.club.repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;

    @Transactional
    public Imagen crearImagen(MultipartFile archivo) throws Exception {
        if (archivo != null && !archivo.isEmpty()) {
            Imagen img = new Imagen();
            img.setNombre(archivo.getOriginalFilename());
            img.setMime(archivo.getContentType());
            img.setContenido(archivo.getBytes());
            return imagenRepository.save(img);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Imagen> listarTodas() {
        return imagenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Imagen buscarPorId(String id) throws Exception {
        return imagenRepository.findById(id).orElseThrow(() -> new Exception("Imagen no encontrada"));
    }

    @Transactional
    public Imagen actualizarImagen(String id, MultipartFile archivo) throws Exception {
        if (archivo != null && !archivo.isEmpty()) {
            Imagen img = buscarPorId(id);
            img.setNombre(archivo.getOriginalFilename());
            img.setMime(archivo.getContentType());
            img.setContenido(archivo.getBytes());
            return imagenRepository.save(img);
        }
        return null;
    }

    @Transactional
    public void eliminarImagen(String id) {
        imagenRepository.deleteById(id);
    }
}