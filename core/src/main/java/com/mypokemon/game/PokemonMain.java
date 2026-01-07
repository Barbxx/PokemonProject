package com.mypokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PokemonMain extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Use LibGDX default font for now
        font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Important!
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
