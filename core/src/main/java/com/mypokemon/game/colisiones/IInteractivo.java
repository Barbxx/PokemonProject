package com.mypokemon.game.colisiones;

/**
 * Interfaz para objetos con los que se puede interactuar.
 */
public interface IInteractivo extends IColisionable {

    /**
     * Ejecuta la interacción con este objeto.
     */
    void interactuar();

    /**
     * Verifica si el jugador está dentro de la distancia de interacción.
     * 
     * @param x Posición X del jugador.
     * @param y Posición Y del jugador.
     * @return true si está en rango, false en caso contrario.
     */
    boolean estaEnRango(float x, float y);

    /**
     * Obtiene el rango máximo permitido para interactuar.
     * 
     * @return Distancia máxima.
     */
    float obtenerRangoInteraccion();

    /**
     * Obtiene el mensaje que se mostrará al jugador cuando pueda interactuar.
     * 
     * @return Texto de ayuda (ej: "Presiona T para interactuar").
     */
    String obtenerMensajeInteraccion();
}
