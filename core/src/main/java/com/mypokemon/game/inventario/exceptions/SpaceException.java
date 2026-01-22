package com.mypokemon.game.inventario.exceptions;

/**
 * Excepción lanzada cuando el inventario está lleno y no hay espacio
 * disponible.
 * Checked exception - debe ser manejada explícitamente.
 */
public class SpaceException extends Exception {

    public SpaceException(String m) {
        super(m);
    }
}
