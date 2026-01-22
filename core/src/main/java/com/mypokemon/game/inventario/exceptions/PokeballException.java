package com.mypokemon.game.inventario.exceptions;

// Excepción lanzada cuando se intenta realizar una acción que requiere Pokéballs pero no hay ninguna disponible en el inventario.

public class PokeballException extends Exception {

    /**
     * Crea una nueva excepción con un mensaje detallado.
     * 
     * @param m El mensaje que describe el error (ej: "No tienes Pokéballs").
     */
    public PokeballException(String m) {
        super(m);
    }
}
