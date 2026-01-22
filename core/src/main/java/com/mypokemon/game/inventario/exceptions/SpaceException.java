package com.mypokemon.game.inventario.exceptions;

// Excepción que se lanza cuando no hay espacio en la mochila.
public class SpaceException extends Exception {
    public SpaceException(String mensaje) {
        super(mensaje);
    }
}
