package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Interfaz
public interface Interactable {
    boolean isClose(float targetX, float targetY);

    void render(SpriteBatch batch);

    void dispose();
}
