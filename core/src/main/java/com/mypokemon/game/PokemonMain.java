package com.mypokemon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mypokemon.game.pantallas.MainMenuScreen;

/**
 * Clase principal del juego Pokemon.
 * Gestiona la inicializacion del juego y los recursos globales como batch y
 * font.
 */
public class PokemonMain extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public com.mypokemon.game.client.NetworkClient networkClient;

    /**
     * Inicializa el juego creando los recursos necesarios y mostrando el menu
     * principal.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // LibGDX font
        font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * Renderiza el frame actual del juego.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Libera los recursos del juego al cerrarlo.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
