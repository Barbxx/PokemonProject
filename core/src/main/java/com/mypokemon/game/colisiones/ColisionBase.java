package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase abstracta base para todas las colisiones.
 */
public abstract class ColisionBase implements IColisionable {

    protected Rectangle limites;
    protected String tipo;

    @Override
    public Rectangle obtenerLimites() {
        return limites;
    }

    @Override
    public String obtenerTipo() {
        return tipo;
    }

    @Override
    public boolean verificarColision(float x, float y, float ancho, float alto) {
        Rectangle area = new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
        return limites.overlaps(area);
    }
}
