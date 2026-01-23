package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interfaz que define el comportamiento para objetos con los que el jugador
 * puede interactuar.
 * Permite verificar la cercanía, renderizar el objeto y liberar sus recursos.
 */
public interface Interactable {
    /**
     * Verifica si el objeto está lo suficientemente cerca para interactuar.
     * 
     * @param targetX Posición X del objetivo (jugador).
     * @param targetY Posición Y del objetivo (jugador).
     * @return true si está en rango de interacción.
     */
    boolean isClose(float targetX, float targetY);

    /**
     * Renderiza el objeto interactivo.
     * 
     * @param batch SpriteBatch utilizado para dibujar.
     */
    void render(SpriteBatch batch);

    /**
     * Libera los recursos utilizados por el objeto.
     */
    void dispose();
}
