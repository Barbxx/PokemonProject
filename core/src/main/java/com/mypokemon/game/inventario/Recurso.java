package com.mypokemon.game.inventario;

import java.util.ArrayList;
import java.util.List;

// Representa un recurso básico recolectable.

public class Recurso extends Item {

    /**
     * Constructor para un recurso.
     * 
     * @param id       ID único del recurso.
     * @param nombre   Nombre legible.
     * @param cantidad Cantidad inicial.
     */
    public Recurso(String id, String nombre, int cantidad) {
        super(id, nombre, cantidad);
    }

    /**
     * Obtiene las opciones disponibles para el recurso
     * Recursos especiales deben sobrescribir esto en subclases.
     * 
     * @return Lista de opciones.
     */
    @Override
    public List<String> getOpciones() {
        List<String> opciones = new ArrayList<>();

        // Todos los recursos básicos se pueden tirar
        opciones.add("Tirar");

        return opciones;
    }
}
