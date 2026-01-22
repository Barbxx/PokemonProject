package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.mypokemon.game.client.NetworkClient;

// Pantalla que gestiona la conexión multijugador en red local.
public class CompartidaScreen extends BaseScreen {
    private String nombreJugador;
    private String textoEstado = "Inicializando...";
    private boolean estaConectando = false;

    private OrthographicCamera camara;
    private Viewport viewport;
    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public CompartidaScreen(PokemonMain juego, String nombreJugador) {
        super(juego);
        this.nombreJugador = nombreJugador;
        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        viewport.apply();
        camara.position.set(ANCHO_VIRTUAL / 2, ALTO_VIRTUAL / 2, 0);
        camara.update();
    }

    @Override
    public void show() {
        iniciarDescubrimiento();
    }

    private void iniciarDescubrimiento() {
        textoEstado = "Buscando Servidor en RED LOCAL...";
        estaConectando = true;
        if (juego.clienteRed == null)
            juego.clienteRed = new NetworkClient();

        new Thread(() -> {
            String ip = juego.clienteRed.descubrirIPServidor();
            if (ip != null) {
                Gdx.app.postRunnable(() -> textoEstado = "Servidor encontrado en: " + ip + "\nConectando...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                juego.clienteRed.establecerEscucha(msg -> {
                    if (msg.startsWith("MATCH_START")) {
                        Gdx.app.postRunnable(() -> {
                            juego.setScreen(new IntroScreen(juego, "SharedGame"));
                            dispose();
                        });
                    }
                });
                boolean ok = juego.clienteRed.conectar(ip, nombreJugador);
                if (ok)
                    Gdx.app.postRunnable(() -> textoEstado = "Conectado. Esperando Jugador 2...");
                else {
                    Gdx.app.postRunnable(() -> textoEstado = "Error al conectar con " + ip);
                    estaConectando = false;
                }
            } else {
                Gdx.app.postRunnable(
                        () -> textoEstado = "No se encontró servidor.\nEjecuta el 'GameServer' en una PC.");
                estaConectando = false;
            }
        }).start();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        juego.fuente.setColor(Color.WHITE);
        juego.fuente.getData().setScale(2.5f);
        juego.fuente.draw(juego.batch, "MODO COMPARTIDO", 0, ALTO_VIRTUAL - 100, ANCHO_VIRTUAL, Align.center, false);
        juego.fuente.getData().setScale(2.0f);
        if (estaConectando) {
            juego.fuente.setColor(Color.YELLOW);
            if (System.currentTimeMillis() % 1000 < 500)
                juego.fuente.draw(juego.batch, textoEstado, 0, ALTO_VIRTUAL / 2, ANCHO_VIRTUAL, Align.center, false);
        } else {
            juego.fuente.setColor(Color.RED);
            juego.fuente.draw(juego.batch, textoEstado, 0, ALTO_VIRTUAL / 2, ANCHO_VIRTUAL, Align.center, false);
            juego.fuente.getData().setScale(1.2f);
            juego.fuente.setColor(Color.WHITE);
            juego.fuente.draw(juego.batch, "PRESIONA ESC PARA REGRESAR", 0, 100, ANCHO_VIRTUAL, Align.center, false);
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                juego.setScreen(new EleccionJuegoScreen(juego));
                dispose();
            }
        }
        juego.batch.end();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
}
