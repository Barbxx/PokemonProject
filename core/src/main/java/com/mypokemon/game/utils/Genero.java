package com.mypokemon.game.utils;

/**
 * Representa el género del personaje del jugador.
 * Utilizado para determinar la apariencia (sprites) y textos.
 */
public enum Genero {
    CHICO,
    CHICA;

    /**
     * Convierte una cadena de texto a un valor del enum Genero.
     * Es insensible a mayúsculas/minúsculas.
     * Defaults to CHICO if invalid.
     * 
     * @param str La cadena ("CHICO", "CHICA", etc.)
     * @return El enum correspondiente.
     */
    public static Genero fromString(String str) {
        if (str == null)
            return CHICO;
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CHICO;
        }
    }
}
