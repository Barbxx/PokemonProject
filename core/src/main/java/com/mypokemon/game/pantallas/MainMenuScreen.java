package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Pantalla principal del menú del juego. Muestra las opciones principales (Jugar, Cargar, Ayuda, Acerca de, Salir).
public class MainMenuScreen extends BaseScreen {

    private Texture texturaFondo;
    private Texture[] texturasNormales;
    private Texture[] texturasSeleccionadas;

    private String[] opciones = { "PLAY", "CARGAR", "HELP", "ABOUT", "EXIT" };
    private String[] prefijosArchivos = { "boton_jugar", "boton_cargar", "boton_ayuda", "boton_acercade",
            "boton_salir" };

    private int opcionActual = -1;
    private float alfaFade = 0f;
    private boolean estaIniciando = false;
    private String subPantallaActual = null;

    private float anchoCajaMenu = 370;
    private float altoCajaMenu = 260;

    private OrthographicCamera camara;
    private Viewport viewport;
    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    // Constructor de la pantalla del menú principal.
    public MainMenuScreen(final PokemonMain juego) {
        super(juego);
        try {
            texturaFondo = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("MainMenu", "Error al cargar fondo", e);
        }

        texturasNormales = new Texture[opciones.length];
        texturasSeleccionadas = new Texture[opciones.length];

        for (int i = 0; i < opciones.length; i++) {
            try {
                texturasNormales[i] = new Texture(prefijosArchivos[i] + "_normal.png");
                texturasSeleccionadas[i] = new Texture(prefijosArchivos[i] + "_seleccionado.png");
            } catch (Exception e) {
                Gdx.app.log("MainMenu", "Error al cargar botones", e);
            }
        }

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        viewport.apply();
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
        camara.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        float sw = ANCHO_VIRTUAL, sh = ALTO_VIRTUAL;
        float bw = 300, bh = 80, esp = -15, totalH = (opciones.length * bh) + ((opciones.length - 1) * esp),
                startY = (sh + totalH) / 2 - 100;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            opcionActual = (opcionActual == -1) ? opciones.length - 1
                    : (opcionActual == 0 ? opciones.length - 1 : opcionActual - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            opcionActual = (opcionActual == -1) ? 0 : (opcionActual == opciones.length - 1 ? 0 : opcionActual + 1);
        }

        boolean confirmar = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if (opcionActual != -1 && confirmar) {
            if (opcionActual == 0) {
                juego.setScreen(new EleccionJuegoScreen(juego));
                dispose();
                return;
            } else if (opcionActual == 1) {
                juego.setScreen(new PartidasScreen(juego));
                dispose();
                return;
            } else if (opcionActual == 2)
                subPantallaActual = "HELP";
            else if (opcionActual == 3) {
                juego.setScreen(new AcercaDeScreen(juego));
                dispose();
                return;
            } else if (opcionActual == 4)
                Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
            subPantallaActual = null;

        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (texturaFondo != null)
            juego.batch.draw(texturaFondo, 0, 0, sw, sh);

        for (int i = 0; i < opciones.length; i++) {
            float bY = startY - (i * (bh + esp)) - bh, bX = (sw - bw) / 2;
            Texture aDibujar = (i == opcionActual && texturasSeleccionadas[i] != null) ? texturasSeleccionadas[i]
                    : texturasNormales[i];
            if (aDibujar != null)
                juego.batch.draw(aDibujar, bX, bY, bw, bh);
            else {
                GlyphLayout l = new GlyphLayout(juego.fuente, opciones[i]);
                juego.fuente.setColor(i == opcionActual ? Color.YELLOW : Color.WHITE);
                juego.fuente.draw(juego.batch, l, (sw - l.width) / 2, bY + (bh + l.height) / 2);
            }
        }

        if (subPantallaActual != null) {
            juego.batch.setColor(0, 0, 0, 0.8f);
            if (texturasSeleccionadas[0] != null)
                juego.batch.draw(texturasSeleccionadas[0], 50, 50, sw - 100, sh - 100);
            juego.batch.setColor(Color.WHITE);
            juego.fuente.getData().setScale(1.5f);
            juego.fuente.draw(juego.batch, "SCREEN: " + subPantallaActual, 0, sh / 2 + 20, sw,
                    com.badlogic.gdx.utils.Align.center, false);
            juego.fuente.getData().setScale(1.0f);
            juego.fuente.draw(juego.batch, "PRESS ESC TO GO BACK", 0, sh / 2 - 40, sw,
                    com.badlogic.gdx.utils.Align.center, false);
        }
        juego.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
    }

    @Override
    public void dispose() {
        if (texturaFondo != null)
            texturaFondo.dispose();
        if (texturasNormales != null)
            for (Texture t : texturasNormales)
                if (t != null)
                    t.dispose();
        if (texturasSeleccionadas != null)
            for (Texture t : texturasSeleccionadas)
                if (t != null)
                    t.dispose();
    }
}
