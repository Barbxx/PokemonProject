package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Clase base abstracta para pantallas estáticas que solo muestran una imagen de fondo.
public abstract class StaticDisplayScreen extends BaseScreen implements INavigable {

    protected Texture texturaFondo;
    protected final Screen pantallaRetorno;

    protected OrthographicCamera camara;
    protected Viewport vista;
    protected static final float ANCHO_VIRTUAL = 1280f;
    protected static final float ALTO_VIRTUAL = 720f;

    public StaticDisplayScreen(PokemonMain juego, Screen pantallaRetorno, String rutaFondo) {
        super(juego);
        this.pantallaRetorno = pantallaRetorno;
        this.texturaFondo = cargarTextura(rutaFondo);

        camara = new OrthographicCamera();
        vista = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        vista.apply();
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
        camara.update();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            navegarAtras();
            return;
        }
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        dibujarFondo();
        dibujarContenido(delta);
        juego.batch.end();
    }

    // Renderiza la imagen de fondo para llenar la pantalla virtual.
    protected void dibujarFondo() {
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
    }

    // Sobrescribir para renderizar contenido adicional.
    protected void dibujarContenido(float delta) {
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width, height, true);
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
    }

    @Override
    public void navegarAtras() {
        if (pantallaRetorno != null) {
            juego.setScreen(pantallaRetorno);
            dispose();
        }
    }

    @Override
    public void navegarA(Screen pantalla) {
        juego.setScreen(pantalla);
        dispose();
    }

    @Override
    public Screen obtenerPantallaRetorno() {
        return pantallaRetorno;
    }
}
