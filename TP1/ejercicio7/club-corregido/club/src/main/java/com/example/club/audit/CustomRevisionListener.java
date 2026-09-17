package com.example.club.audit;


import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        Revision revision = (Revision) revisionEntity;
        String usuarioActual = "Desconocido";

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
            }
        } catch (Exception e) {}

        revision.setUsuario(usuarioActual);
    }
}
