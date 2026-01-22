package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Vector2;

/**
 * Clase abstracta para definir áreas en el mapa que activan interacciones.
 * Se utiliza para puertas, letreros u otros elementos que no son necesariamente
 * entidades físicas móviles.
 */
public abstract class ZonaInteractiva extends ColisionBase implements IInteractivo {

    /** Rango máximo de proximidad para activar la zona. */
    protected float rangoInteraccion;

    /** Texto informativo que se muestra cuando el jugador está en rango. */
    protected String mensajeInteraccion;

    /**
     * {@inheritDoc}
     * Verifica la distancia entre el jugador y el centro de la zona.
     */
    @Override
    public boolean estaEnRango(float x, float y) {
        float centroX = limites.x + limites.width / 2;
        float centroY = limites.y + limites.height / 2;
        return Vector2.dst(x, y, centroX, centroY) < rangoInteraccion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String obtenerMensajeInteraccion() {
        return mensajeInteraccion;
    }

    /**
     * Método abstracto que define la lógica específica de la zona (ej: abrir
     * puerta).
     */
    @Override
    public abstract void interactuar();
}
