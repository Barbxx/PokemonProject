package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.ITextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.List;

// Clase base abstracta para todas las pantallas del juego. Gestiona la carga y liberación de recursos.
public abstract class BaseScreen implements Screen, ITextureManager {
    protected final PokemonMain juego;
    private final List<Texture> texturas;

    public BaseScreen(PokemonMain juego) {
        this.juego = juego;
        this.texturas = new ArrayList<>();
    }

    @Override
    public Texture cargarTextura(String ruta) {
        try {
            Texture t = new Texture(ruta);
            agregarTextura(t);
            return t;
        } catch (Exception e) {
            Gdx.app.error(this.getClass().getSimpleName(), "Error al cargar textura: " + ruta, e);
            return null;
        }
    }

    @Override
    public void agregarTextura(Texture t) {
        if (t != null && !texturas.contains(t))
            texturas.add(t);
    }

    @Override
    public void liberarTexturas() {
        for (Texture t : texturas)
            if (t != null)
                t.dispose();
        texturas.clear();
    }

    protected float obtenerAnchoPantalla() {
        return Gdx.graphics.getWidth();
    }

    protected float obtenerAltoPantalla() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public abstract void render(float delta);

    @Override
    public void show() {
    }

    @Override
    public void resize(int ancho, int alto) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        liberarTexturas();
    }
}
