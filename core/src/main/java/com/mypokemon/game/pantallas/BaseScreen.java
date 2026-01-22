package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.ITextureManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base abstracta para pantallas con gestión automática de texturas.
 * Todas las pantallas deben extender esta clase para beneficiarse de la gestión
 * de recursos y métodos auxiliares.
 */
public abstract class BaseScreen implements Screen, ITextureManager {

    protected final PokemonMain game;
    private final List<Texture> textures;

    public BaseScreen(PokemonMain game) {
        this.game = game;
        this.textures = new ArrayList<>();
    }

    /**
     * Carga una textura y la registra para su liberación automática.
     * 
     * @param path Ruta al archivo de textura.
     * @return La textura cargada, o null si la carga falla.
     */
    @Override
    public Texture loadTexture(String path) {
        try {
            Texture texture = new Texture(path);
            addTexture(texture);
            return texture;
        } catch (Exception e) {
            Gdx.app.error(this.getClass().getSimpleName(), "Could not load texture: " + path, e);
            return null;
        }
    }

    /**
     * Registra una textura para su liberación automática.
     * 
     * @param texture Textura a registrar.
     */
    @Override
    public void addTexture(Texture texture) {
        if (texture != null && !textures.contains(texture)) {
            textures.add(texture);
        }
    }

    /**
     * Libera todas las texturas registradas.
     */
    @Override
    public void disposeTextures() {
        for (Texture texture : textures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
    }

    /**
     * Gets the current screen width.
     * 
     * @return Screen width in pixels
     */
    protected float getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    /**
     * Gets the current screen height.
     * 
     * @return Screen height in pixels
     */
    protected float getScreenHeight() {
        return Gdx.graphics.getHeight();
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
        disposeTextures();
    }
}
