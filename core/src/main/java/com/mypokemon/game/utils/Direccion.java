package com.mypokemon.game.utils;

/**
 * Representa las direcciones cardinales de movimiento en el juego.
 */
public enum Direccion {
    ARRIBA,
    ABAJO,
    IZQUIERDA,
    DERECHA;

    public static Direccion fromString(String str) {
        if (str == null)
            return ABAJO;
        switch (str.toUpperCase()) {
            case "UP":
                return ARRIBA;
            case "DOWN":
                return ABAJO;
            case "LEFT":
                return IZQUIERDA;
            case "RIGHT":
                return DERECHA;
            default:
                return ABAJO;
        }
    }
}
