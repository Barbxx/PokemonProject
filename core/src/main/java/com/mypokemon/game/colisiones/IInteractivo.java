package com.mypokemon.game.colisiones;

// Interfaz para objetos con los que se puede interactuar.

public interface IInteractivo extends IColisionable {

    // Ejecuta la interacción con este objeto.
    void interactuar();

    /**
     * Verifica si el jugador está en rango de interacción.
     * 
     * @param x Posición X del jugador
     * @param y Posición Y del jugador
     * @return true si está en rango, false en caso contrario
     */
    boolean estaEnRango(float x, float y);

    /**
     * Obtiene el rango de interacción.
     * 
     * @return Distancia máxima para interactuar
     */
    float obtenerRangoInteraccion();

    /**
     * Obtiene el mensaje de ayuda para interactuar.
     * 
     * @return Texto como "Presiona T para entrar"
     */
    String obtenerMensajeInteraccion();
}
