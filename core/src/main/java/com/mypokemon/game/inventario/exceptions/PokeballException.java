package com.mypokemon.game.inventario.exceptions;

// Excepción lanzada cuando no hay Pokéballs disponibles en el inventario.
public class PokeballException extends Exception {

    public PokeballException(String m) {
        super(m);
    }
}
