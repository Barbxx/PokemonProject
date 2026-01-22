package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Vector2;

/**
 * Clase abstracta para zonas interactivas (puertas, letreros, etc.).
 */
public abstract class ZonaInteractiva extends ColisionBase implements IInteractivo {

    protected float rangoInteraccion;
    protected String mensajeInteraccion;

    @Override
    public boolean estaEnRango(float x, float y) {
        float centroX = limites.x + limites.width / 2;
        float centroY = limites.y + limites.height / 2;
        return Vector2.dst(x, y, centroX, centroY) < rangoInteraccion;
    }

    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    @Override
    public String obtenerMensajeInteraccion() {
        return mensajeInteraccion;
    }

    /**
     * Método abstracto que deben implementar las subclases para definir
     * qué sucede al interactuar.
     */
    @Override
    public abstract void interactuar();
}
