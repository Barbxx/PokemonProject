package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Pantalla para elegir el tipo de juego (Solitario o Compartido).
public class EleccionJuegoScreen extends BaseScreen {

    private Texture fondo;
    private Texture solitarioNormal, solitarioSeleccionado;
    private Texture compartidoNormal, compartidoSeleccionado;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer renderizadorFormas;

    private int opcionActual = -1;
    private final int OPCION_SOLITARIO = 0;
    private final int OPCION_COMPARTIDO = 1;

    private boolean preguntandoNombre = false;
    private String nombreIngresado = "";
    private String mensajeEstado = "";
    private OrthographicCamera camara;
    private Viewport viewport;
    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public EleccionJuegoScreen(PokemonMain juego) {
        super(juego);
        this.renderizadorFormas = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
        this.fondo = cargarTextura("menu_bg.jpg");
        this.solitarioNormal = cargarTextura("boton_solitario_normal.png");
        this.solitarioSeleccionado = cargarTextura("boton_solitario_seleccionado.png");
        this.compartidoNormal = cargarTextura("boton_compartida_normal.png");
        this.compartidoSeleccionado = cargarTextura("boton_compartida_seleccionado.png");

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char caracter) {
                if (preguntandoNombre) {
                    if (Character.isLetterOrDigit(caracter) || caracter == ' ') {
                        if (nombreIngresado.length() < 15)
                            nombreIngresado += caracter;
                    } else if (caracter == '\b') {
                        if (nombreIngresado.length() > 0)
                            nombreIngresado = nombreIngresado.substring(0, nombreIngresado.length() - 1);
                    } else if (caracter == '\r' || caracter == '\n')
                        confirmarNombre();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int k) {
                if (preguntandoNombre && k == Input.Keys.ESCAPE) {
                    preguntandoNombre = false;
                    mensajeEstado = "";
                    nombreIngresado = "";
                    return true;
                }
                return false;
            }
        });

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        viewport.apply();
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
        camara.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (!preguntandoNombre) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                opcionActual = (opcionActual == -1 || opcionActual == OPCION_SOLITARIO) ? OPCION_COMPARTIDO
                        : OPCION_SOLITARIO;
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
                opcionActual = (opcionActual == -1 || opcionActual == OPCION_COMPARTIDO) ? OPCION_SOLITARIO
                        : OPCION_COMPARTIDO;
            if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                    && opcionActual != -1) {
                preguntandoNombre = true;
                nombreIngresado = "";
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                juego.setScreen(new MainMenuScreen(juego));
                dispose();
                return;
            }
        }

        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        if (fondo != null)
            juego.batch.draw(fondo, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        float bw = 400, bh = 100, cx = ANCHO_VIRTUAL / 2, by = ALTO_VIRTUAL / 2;
        juego.batch.draw(opcionActual == OPCION_SOLITARIO ? solitarioSeleccionado : solitarioNormal, cx - bw / 2,
                by + 50, bw, bh);
        juego.batch.draw(opcionActual == OPCION_COMPARTIDO ? compartidoSeleccionado : compartidoNormal, cx - bw / 2,
                by - 150, bw, bh);
        juego.batch.end();

        if (preguntandoNombre) {
            renderizadorFormas.setProjectionMatrix(camara.combined);
            renderizadorFormas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
            renderizadorFormas.setColor(0, 0, 0, 0.8f);
            renderizadorFormas.rect(0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);
            renderizadorFormas.setColor(Color.DARK_GRAY);
            renderizadorFormas.rect(cx - 300, by - 100, 600, 200);
            renderizadorFormas.setColor(Color.WHITE);
            renderizadorFormas.rect(cx - 295, by - 95, 590, 190);
            renderizadorFormas.end();
            juego.batch.begin();
            juego.fuente.setColor(Color.BLACK);
            juego.fuente.getData().setScale(2.0f);
            juego.fuente.draw(juego.batch, "NOMBRE DE LA PARTIDA:", cx - 250, by + 50);
            juego.fuente.draw(juego.batch, nombreIngresado + ((System.currentTimeMillis() / 500 % 2 == 0) ? "|" : ""),
                    cx - 250, by);
            if (!mensajeEstado.isEmpty()) {
                juego.fuente.setColor(Color.RED);
                juego.fuente.getData().setScale(1.2f);
                juego.fuente.draw(juego.batch, mensajeEstado, cx - 250, by - 60);
            }
            juego.batch.end();
        }
    }

    private void confirmarNombre() {
        if (nombreIngresado.trim().isEmpty()) {
            mensajeEstado = "El nombre no puede estar vacío";
            return;
        }
        String nombre = nombreIngresado.trim();
        if (opcionActual == OPCION_SOLITARIO) {
            if (Gdx.files.local(nombre + " - .dat").exists()) {
                mensajeEstado = "Ya existe una partida con ese nombre";
                return;
            }
            juego.setScreen(new IntroScreen(juego, nombre));
        } else {
            juego.setScreen(new CompartidaScreen(juego, nombre));
        }
        dispose();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderizadorFormas.dispose();
    }
}
