package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;

// Pantalla de introducción que se muestra antes de la batalla con Arceus.
public class ArceusIntroScreen implements Screen {
    private final PokemonMain juego;
    private final com.badlogic.gdx.Screen pantallaPadre;
    private final Explorador explorador;
    private SpriteBatch batch;
    private Texture fondo;
    private BitmapFont fuente;
    private ShapeRenderer renderizadorFormas;

    private String[] mensajes;
    private int indiceMensajeActual;
    private float temporizadorMensaje;

    // Constructor de la pantalla de introducción de Arceus.
    public ArceusIntroScreen(PokemonMain juego, com.badlogic.gdx.Screen pantallaPadre, Explorador explorador) {
        this.juego = juego;
        this.pantallaPadre = pantallaPadre;
        this.explorador = explorador;
        this.batch = new SpriteBatch();
        this.renderizadorFormas = new ShapeRenderer();
        this.fuente = new BitmapFont();
        this.fuente.getData().setScale(2.0f);
        this.fuente.setColor(Color.WHITE);

        try {
            this.fondo = new Texture(Gdx.files.internal("fondoHitoFinal.png"));
        } catch (Exception e) {
            Gdx.app.error("ArceusIntroScreen", "No se pudo cargar fondoHitoFinal.png", e);
        }

        this.mensajes = new String[] {
                "A medida que te aproximas a la cueva, el aire se vuelve pesado, gélido, como si el tiempo mismo se detuviera ante tus pies…",
                "El pulso se te acelera… ¿Será Arceus?",
                "Sin embargo, al cruzar el umbral, el silencio es absoluto. No hay deidades, solo una flauta que se ve muy antigua…",
                "Por lo que tomaste una decisión…",
                "Tocaste la flauta…"
        };

        this.indiceMensajeActual = 0;
        this.temporizadorMensaje = 0;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                avanzarMensaje();
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                avanzarMensaje();
                return true;
            }
        });
    }

    // Avanza al siguiente mensaje o inicia la batalla con Arceus.
    private void avanzarMensaje() {
        if (indiceMensajeActual < mensajes.length - 1) {
            indiceMensajeActual++;
            temporizadorMensaje = 0;
        } else {
            Pokemon arceus = new Pokemon("Arceus", 10, 130, true, "Normal");
            juego.setScreen(new BattleScreen(juego, pantallaPadre, explorador, arceus));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        temporizadorMensaje += delta;

        batch.begin();
        if (fondo != null) {
            batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.end();

        renderizadorFormas.begin(ShapeRenderer.ShapeType.Filled);
        float anchoCaja = Gdx.graphics.getWidth() * 0.8f;
        float altoCaja = 200;
        float cajaX = (Gdx.graphics.getWidth() - anchoCaja) / 2;
        float cajaY = 50;

        renderizadorFormas.setColor(0, 0, 0, 0.8f);
        renderizadorFormas.rect(cajaX, cajaY, anchoCaja, altoCaja);
        renderizadorFormas.end();

        renderizadorFormas.begin(ShapeRenderer.ShapeType.Line);
        renderizadorFormas.setColor(Color.WHITE);
        renderizadorFormas.rect(cajaX, cajaY, anchoCaja, altoCaja);
        renderizadorFormas.end();

        batch.begin();
        if (indiceMensajeActual < mensajes.length) {
            String mensaje = mensajes[indiceMensajeActual];
            float textoX = cajaX + 20;
            float textoY = cajaY + altoCaja - 30;
            float anchoMaximo = anchoCaja - 40;

            dibujarTextoAjustado(mensaje, textoX, textoY, anchoMaximo);

            if (temporizadorMensaje % 1.0f < 0.5f) {
                fuente.draw(batch, "▼", cajaX + anchoCaja - 40, cajaY + 30);
            }
        }
        batch.end();
    }

    // Dibuja texto con ajuste de línea automático.
    private void dibujarTextoAjustado(String texto, float x, float y, float anchoMaximo) {
        String[] palabras = texto.split(" ");
        StringBuilder linea = new StringBuilder();
        float yActual = y;
        float altoLinea = fuente.getLineHeight();

        for (String palabra : palabras) {
            String lineaPrueba = linea.length() == 0 ? palabra : linea + " " + palabra;
            float anchoPrueba = fuente.draw(batch, lineaPrueba, 0, 0).width;

            if (anchoPrueba > anchoMaximo && linea.length() > 0) {
                fuente.draw(batch, linea.toString(), x, yActual);
                yActual -= altoLinea;
                linea = new StringBuilder(palabra);
            } else {
                linea = new StringBuilder(lineaPrueba);
            }
        }

        if (linea.length() > 0) {
            fuente.draw(batch, linea.toString(), x, yActual);
        }
    }

    @Override
    public void resize(int width, int height) {
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
        if (batch != null)
            batch.dispose();
        if (fondo != null)
            fondo.dispose();
        if (fuente != null)
            fuente.dispose();
        if (renderizadorFormas != null)
            renderizadorFormas.dispose();
    }
}
