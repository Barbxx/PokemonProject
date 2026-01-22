package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Vector2;

/**
 * Clase abstracta que define zonas específicas del mapa con las que se puede
 * interactuar.
 * Ejemplo: Puertas, letreros, objetos en el suelo.
 */
public abstract class ZonaInteractiva extends ColisionBase implements IInteractivo {

    protected float rangoInteraccion;
    protected String mensajeInteraccion;

    /**
     * Verifica si el jugador está dentro del radio de interacción.
     * Utiliza la distancia euclidiana desde el centro de la zona.
     * 
     * @param x Posición X del jugador.
     * @param y Posición Y del jugador.
     * @return true si está cerca.
     */
    @Override
    public boolean estaEnRango(float x, float y) {
        float centroX = limites.x + limites.width / 2;
        float centroY = limites.y + limites.height / 2;
        return Vector2.dst(x, y, centroX, centroY) < rangoInteraccion;
    }

    /**
     * Obtiene el rango configurado para esta zona.
     * 
     * @return Radio de interacción.
     */
    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    /**
     * Obtiene el mensaje de ayuda configurado.
     * 
     * @return Mensaje de interacción.
     */
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
