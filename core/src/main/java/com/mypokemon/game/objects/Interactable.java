package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface implementing the Integration Segregation Principle (ISP).
 * Defines the contract for any object that can be interacted with.
 */
public interface Interactable {
    boolean isClose(float targetX, float targetY);

    void render(SpriteBatch batch);

    void dispose();
}
