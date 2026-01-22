package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.client.NetworkClient;

public class CompartidaScreen extends BaseScreen {

    private String playerName;
    private String statusText = "Inicializando...";
    private boolean isConnecting = false;

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    public CompartidaScreen(PokemonMain game, String playerName) {
        super(game);
        this.playerName = playerName;

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void show() {
        startDiscovery();
    }

    private void startDiscovery() {
        statusText = "Buscando Servidor en RED LOCAL...";
        isConnecting = true;

        // Inicializar Client en Main si no existe
        if (game.networkClient == null) {
            game.networkClient = new NetworkClient();
        }

        new Thread(() -> {
            // 1. Auto-descubrimiento (UDP Beacon)
            String serverIP = game.networkClient.discoverServerIP();

            if (serverIP != null) {
                Gdx.app.postRunnable(() -> statusText = "Servidor encontrado en: " + serverIP + "\nConectando...");

                try {
                    Thread.sleep(500); // Pequeña pausa visual
                } catch (InterruptedException e) {
                }

                // 2. Configurar Listener antes de conectar para evitar perder mensajes de
                // inicio rápidos
                game.networkClient.setListener(msg -> {
                    if (msg.startsWith("MATCH_START")) {
                        Gdx.app.postRunnable(() -> {
                            game.setScreen(new IntroScreen(game, "SharedGame"));
                            dispose();
                        });
                    }
                });

                // 3. Conexión TCP
                boolean connected = game.networkClient.connect(serverIP, playerName);

                if (connected) {
                    Gdx.app.postRunnable(() -> statusText = "Conectado. Esperando Jugador 2...");
                } else {
                    Gdx.app.postRunnable(() -> statusText = "Error al conectar con TCP " + serverIP);
                    isConnecting = false;
                }
            } else {
                Gdx.app.postRunnable(() -> statusText = "No se encontró servidor.\nEjecuta el 'GameServer' en una PC.");
                isConnecting = false;
            }
        }).start();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        float w = VIRTUAL_WIDTH;
        float h = VIRTUAL_HEIGHT;

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "MODO COMPARTIDO", 0, h - 100, w, Align.center, false);

        game.font.getData().setScale(1.2f);
        if (isConnecting) {
            game.font.setColor(Color.YELLOW);
            // Efecto de parpadeo simple
            if (System.currentTimeMillis() % 1000 < 500) {
                game.font.draw(game.batch, statusText, 0, h / 2, w, Align.center, false);
            } else {
                game.font.draw(game.batch, statusText.replace("...", "   "), 0, h / 2, w, Align.center, false);
            }
        } else {
            game.font.setColor(Color.RED);
            game.font.draw(game.batch, statusText, 0, h / 2, w, Align.center, false);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "Presiona ESC para volver", 0, h / 2 - 60, w, Align.center, false);
        }

        game.batch.end();

        if (!isConnecting && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            // Cancelar / Salir
            if (game.networkClient != null) {
                game.networkClient.stop();
                game.networkClient = null;
            }
            game.setScreen(new EleccionJuegoScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }
}
