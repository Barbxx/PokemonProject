package com.mypokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypokemon.game.pantallas.PantallaMenuPrincipal;

public class PokemonPrincipal extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public com.mypokemon.game.client.ClienteRed ClienteRed;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Use LibGDX default font for now
        font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        this.setScreen(new PantallaMenuPrincipal(this));
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



