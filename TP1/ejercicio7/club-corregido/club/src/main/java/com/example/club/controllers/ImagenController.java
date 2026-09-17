package com.example.club.controllers;

import com.example.club.entities.Imagen;
import com.example.club.services.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Capa CONTROLLER: expone las imágenes (fotos de perfil/acceso) guardadas
 * como BLOB en la tabla "imagen" (ver entidad Imagen e ImagenService).
 * <p>
 * Las vistas socio_detalle.html / familia_detalle.html usan
 * th:src="@{/img/foto/{id}(id=${socio.foto.id})}" para pintar la foto sin
 * tener que exponer un endpoint de archivos estáticos ni guardar nada en
 * el disco del servidor.
 * <p>
 * Requiere sesión iniciada (regla "/img/**".authenticated() en
 * SecurityConfig) para no dejar las fotos de los socios accesibles a
 * cualquiera que adivine el id.
 */
@Controller
@RequestMapping("/img")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> verFoto(@PathVariable String id) {
        try {
            Imagen imagen = imagenService.buscarPorId(id);
            MediaType tipo = (imagen.getMime() != null)
                    ? MediaType.parseMediaType(imagen.getMime())
                    : MediaType.IMAGE_JPEG;
            return ResponseEntity.ok()
                    .contentType(tipo)
                    .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                    .body(imagen.getContenido());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
