package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;

// Interfaz base para todos los objetos que tienen colisión.

public interface IColisionable {

    /**
     * Verifica si hay colisión con el área especificada.
     * 
     * @param x     Posición X
     * @param y     Posición Y
     * @param ancho Ancho del área
     * @param alto  Alto del área
     * @return true si hay colisión, false en caso contrario
     */
    boolean verificarColision(float x, float y, float ancho, float alto);

    /**
     * Obtiene los límites del objeto colisionable.
     * 
     * @return Rectangle con los límites
     */
    Rectangle obtenerLimites();

    /**
     * Obtiene el tipo de colisión.
     * 
     * @return NPC, ZONA, TERRENO
     */
    String obtenerTipo();
}
