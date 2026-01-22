package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;

// Clase abstracta base para todos los objetos que tienen colisión.

public abstract class ColisionBase implements IColisionable {

    protected Rectangle limites;
    protected String tipo;

    /**
     * Obtiene el rectángulo de límites de la colisión.
     * 
     * @return Rectángulo de colisión.
     */
    @Override
    public Rectangle obtenerLimites() {
        return limites;
    }

    /**
     * Obtiene el tipo de colisión.
     * 
     * @return Cadena que representa el tipo
     */
    @Override
    public String obtenerTipo() {
        return tipo;
    }

    /**
     * Verifica si hay colisión con un rectángulo dado.
     * 
     * @param x     Posición X del centro.
     * @param y     Posición Y del centro.
     * @param ancho Ancho del rectángulo.
     * @param alto  Alto del rectángulo.
     * @return true si hay solapamiento.
     */
    @Override
    public boolean verificarColision(float x, float y, float ancho, float alto) {
        Rectangle area = new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
        return limites.overlaps(area);
    }
}
