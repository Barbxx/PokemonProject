package com.mypokemon.game.inventario.exceptions;

/**
 * Excepción lanzada cuando no hay Pokéballs disponibles en el inventario.
 * Checked exception - debe ser manejada explícitamente.
 */
public class PokeballException extends Exception {

    public PokeballException(String m) {
        super(m);
    }
}
