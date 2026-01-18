package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class CompartidaScreen extends BaseScreen {

    Texture background;
    com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    // States
    // 0 = Menu (Crear / Unirse)
    // 1 = Crear Codigo Input
    // 2 = Unirse Input
    // 3 = Waiting for P2 (Host only)
    int currentState = 0;

    // Menu Options
    final int OPTION_CREAR = 0;
    final int OPTION_UNIRSE = 1;
    int selectedOption = 0;

    // Inputs
    String inputGameName = "";
    String inputPassword = "";
    boolean typingPassword = false; // logic toggle

    String statusMessage = "";

    public CompartidaScreen(PokemonMain game) {
        super(game);
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("CompartidaScreen", "No bg found");
        }

        // Input Processor
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (currentState == 1 || currentState == 2) {
                    // Determine target string
                    if (!typingPassword && currentState == 1) { // Input Game Name
                        if (isValidChar(character) && inputGameName.length() < 15)
                            inputGameName += character;
                    } else { // Input Password
                        if (isValidChar(character) && inputPassword.length() < 8)
                            inputPassword += character;
                    }

                    if (character == '\b') { // Backspace
                        if (!typingPassword && currentState == 1) {
                            if (inputGameName.length() > 0)
                                inputGameName = inputGameName.substring(0, inputGameName.length() - 1);
                        } else {
                            if (inputPassword.length() > 0)
                                inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                        }
                    }

                    if (character == '\r' || character == '\n') { // Enter
                        handleEnterInput();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (currentState == 0) {
                        game.setScreen(new EleccionJuegoScreen(game));
                        dispose();
                    } else {
                        // Reset and go back
                        currentState = 0;
                        inputGameName = "";
                        inputPassword = "";
                        typingPassword = false;
                        statusMessage = "";
                    }
                    return true;
                }

                // Tab to switch fields in Host mode
                if (currentState == 1 && keycode == Input.Keys.TAB) {
                    typingPassword = !typingPassword;
                }

                // Menu Navigation
                if (currentState == 0) {
                    if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
                        selectedOption = OPTION_CREAR;
                    if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
                        selectedOption = OPTION_UNIRSE;
                    if (keycode == Input.Keys.ENTER) {
                        if (selectedOption == OPTION_CREAR) {
                            currentState = 1;
                            typingPassword = false; // Start with Name
                        } else {
                            currentState = 2; // Join
                            typingPassword = true; // Only has password field (usually needs host IP/Name too, but User
                                                   // asked only Password?)
                            // User Request: 'ingresando la "¿Contraseña?:"'.
                            // Wait, usually joining needs a Code or IP. User said "Sends code to server".
                            // Assuming local server or centralized? Logic implies centralized if just code.
                            // But request says "Socket del invitado envia el codigo".
                        }
                    }
                }
                return false;
            }
        });
    }

    private boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || c == ' ';
    }

    private void handleEnterInput() {
        if (currentState == 1) {
            // Check filled
            if (inputGameName.isEmpty() || inputPassword.isEmpty()) {
                statusMessage = "Completa ambos campos";
                return;
            }

            // Host Logic
            // TODO: Start Server Thread waiting for P2
            // For now, mockup transition
            statusMessage = "Esperando Jugador 2...";
            // transition to IntroScreen? User said "Anfitrion avanza a IntroScreen, pero
            // bloqueo mapa"
            // We need to implement Server logic first.
            startHost(inputGameName, inputPassword);

        } else if (currentState == 2) {
            if (inputPassword.isEmpty()) {
                statusMessage = "Ingresa codigo";
                return;
            }
            // Join Logic
            startClient(inputPassword);
        }
    }

    private void startHost(String gameName, String pass) {
        // Mockup
        // Server handles file creation & waits for 2nd player using Synchronized
        com.mypokemon.game.network.NetworkClient.startHostMode(gameName, pass);
        // Go Intro
        this.game.setScreen(new IntroScreen(this.game, gameName));
        dispose();
    }

    private void startClient(String pass) {
        // Mockup
        boolean connected = com.mypokemon.game.network.NetworkClient.connectToHost(pass);
        if (connected) {
            // get game name from server? assume sync
            game.setScreen(new IntroScreen(this.game, "RemoteGame"));
            dispose();
        } else {
            statusMessage = "No se pudo conectar (¿Sala llena/Clave mal?)";
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        game.batch.begin();
        if (background != null)
            game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Draw UI
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Central Box
        float boxW = 500;
        float boxH = 300;
        float boxX = (w - boxW) / 2;
        float boxY = (h - boxH) / 2;

        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(boxX, boxY, boxW, boxH);

        if (currentState == 0) {
            // Two large "Buttons"
            float btnW = 200;
            float btnH = 150;
            float gap = 40;

            // Left: Create
            if (selectedOption == OPTION_CREAR)
                shapeRenderer.setColor(0.3f, 0.8f, 0.3f, 1f);
            else
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
            shapeRenderer.rect(boxX + gap, boxY + (boxH - btnH) / 2, btnW, btnH);

            // Right: Join
            if (selectedOption == OPTION_UNIRSE)
                shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 1f);
            else
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
            shapeRenderer.rect(boxX + boxW - gap - btnW, boxY + (boxH - btnH) / 2, btnW, btnH);
        } else if (currentState == 1) { // Host Input
            // Name Field
            shapeRenderer.setColor(typingPassword ? Color.GRAY : Color.WHITE);
            shapeRenderer.rect(boxX + 50, boxY + 180, 400, 40);

            // Pass Field
            shapeRenderer.setColor(typingPassword ? Color.WHITE : Color.GRAY);
            shapeRenderer.rect(boxX + 50, boxY + 100, 400, 40);
        } else if (currentState == 2) { // Join Input
            // Pass Field Only
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(boxX + 50, boxY + 140, 400, 40);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        // Text
        game.batch.begin();
        if (currentState == 0) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "CREAR CODIGO", boxX + 40, boxY + 150 + 20, 200, 1, false);
            game.font.draw(game.batch, "UNIRSE", boxX + boxW - 200 - 40, boxY + 150 + 20, 200, 1, false);
            game.font.draw(game.batch, "< Selecciona >", boxX, boxY + 30, boxW, 1, false);
        } else if (currentState == 1) {
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch,
                    inputGameName + ((!typingPassword && System.currentTimeMillis() % 1000 < 500) ? "|" : ""),
                    boxX + 60, boxY + 180 + 28);
            game.font.draw(game.batch,
                    inputPassword + ((typingPassword && System.currentTimeMillis() % 1000 < 500) ? "|" : ""), boxX + 60,
                    boxY + 100 + 28);

            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "Nombre Partida:", boxX + 50, boxY + 240);
            game.font.draw(game.batch, "Contraseña (Codigo):", boxX + 50, boxY + 160);
            game.font.draw(game.batch, "TAB: Cambiar campo   ENTER: Crear", boxX, boxY + 50, boxW, 1, false);
        } else if (currentState == 2) {
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, inputPassword + ((System.currentTimeMillis() % 1000 < 500) ? "|" : ""),
                    boxX + 60, boxY + 140 + 28);

            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "¿Contraseña / Codigo?", boxX + 50, boxY + 200);
            game.font.draw(game.batch, "ENTER: Unirse", boxX, boxY + 50, boxW, 1, false);
        }

        if (!statusMessage.isEmpty()) {
            game.font.setColor(Color.RED);
            game.font.draw(game.batch, statusMessage, boxX, boxY + boxH - 10, boxW, 1, false);
        }

        game.batch.end();
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
