package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Interface para cualquier objeto interactuable del juego. Sigue el principio ISP.
public interface GameEntidad {
    boolean estaCerca(float targetX, float targetY);

    void renderizar(SpriteBatch batch);

    void liberar();
}
