package com.mypokemon.game.inventario.exceptions;

// Excepción lanzada cuando el inventario está lleno y no hay espacio disponible.
public class EspacioException extends Exception {

    public EspacioException(String m) {
        super(m);
    }
}
