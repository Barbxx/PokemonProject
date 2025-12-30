package com.mypokemon.game.utils;

import com.badlogic.gdx.Screen;
import com.mypokemon.game.PokemonMain;

/**
 * Abstract base class for screens to avoid repetitive empty methods.
 */
public abstract class BaseScreen implements Screen {

    protected final PokemonMain game;

    public BaseScreen(PokemonMain game) {
        this.game = game;
    }

    @Override
    public abstract void render(float delta);

    @Override
    public void show() {
        // Optional override
    }

    @Override
    public void resize(int width, int height) {
        // Optional override
    }

    @Override
    public void pause() {
        // Optional override
    }

    @Override
    public void resume() {
        // Optional override
    }

    @Override
    public void hide() {
        // Optional override
    }

    @Override
    public void dispose() {
        // Optional override
    }
}
