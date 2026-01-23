package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.ITextureManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base abstracta para todas las pantallas del juego.
 * Proporciona gestión automática de recursos (texturas) y acceso al objeto
 * principal del juego.
 * Implementa Screen para el ciclo de vida de LibGDX e ITextureManager para la
 * carga de recursos.
 */
public abstract class BaseScreen implements Screen, ITextureManager {

    protected final PokemonMain game;
    private final List<Texture> textures;

    /**
     * Constructor de BaseScreen.
     * 
     * @param game Instancia principal del juego.
     */
    public BaseScreen(PokemonMain game) {
        this.game = game;
        this.textures = new ArrayList<>();
    }

    /**
     * Carga una textura y la registra para su eliminación automática.
     * 
     * @param path Ruta al archivo de textura.
     * @return Textura cargada, o null si falla la carga.
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
     * Registra una textura para eliminación automática.
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
     * Elimina todas las texturas registradas.
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
     * Obtiene el ancho actual de la pantalla.
     * 
     * @return Ancho de la pantalla en píxeles.
     */
    protected float getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    /**
     * Obtiene el alto actual de la pantalla.
     * 
     * @return Alto de la pantalla en píxeles.
     */
    protected float getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public abstract void render(float delta);

    @Override
    public void show() {
        // Sobrescritura opcional
    }

    @Override
    public void resize(int width, int height) {
        // Sobrescritura opcional
    }

    @Override
    public void pause() {
        // Sobrescritura opcional
    }

    @Override
    public void resume() {
        // Sobrescritura opcional
    }

    @Override
    public void hide() {
        // Sobrescritura opcional
    }

    /**
     * Libera los recursos de la pantalla. Por defecto limpia todas las texturas
     * registradas.
     */
    @Override
    public void dispose() {
        disposeTextures();
    }
}
