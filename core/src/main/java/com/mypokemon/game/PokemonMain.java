package com.mypokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypokemon.game.pantallas.MainMenuScreen;
import com.mypokemon.game.client.NetworkClient;

// Clase principal del juego que gestiona las transiciones entre pantallas y recursos globales.
public class PokemonMain extends Game {
    public SpriteBatch batch;
    public BitmapFont fuente;
    public NetworkClient clienteRed;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fuente = new BitmapFont();
        fuente.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null)
            batch.dispose();
        if (fuente != null)
            fuente.dispose();
    }
}
