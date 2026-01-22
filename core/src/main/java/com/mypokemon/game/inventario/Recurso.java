package com.mypokemon.game.inventario;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un recurso básico recolectable.
 * Ejemplos: Planta, Baya, Guijarro.
 */
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
     * Obtiene las opciones disponibles para el recurso (normalmente solo 'Tirar').
     * Recursos especiales deben sobrescribir esto en subclases.
     * 
     * @return Lista de opciones.
     */
    @Override
    public List<String> getOpciones() {
        List<String> opciones = new ArrayList<>();

        // Todos los recursos básicos solo se pueden tirar
        // Los recursos con funcionalidad especial (como Baya Aranja para curar)
        // deben ser creados como sus clases específicas (BayaAranja)
        opciones.add("Tirar");

        return opciones;
    }
}
