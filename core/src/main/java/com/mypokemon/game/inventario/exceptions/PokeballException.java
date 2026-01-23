package com.mypokemon.game.inventario.exceptions;

// Excepción lanzada cuando no hay Pokéballs disponibles en el inventario.
public class PokeballException extends Exception {

    /**
     * Crea una nueva excepción de Pokéballs insuficientes.
     * 
     * @param m Mensaje de error.
     */
    public PokeballException(String m) {
        super(m);
    }
}
