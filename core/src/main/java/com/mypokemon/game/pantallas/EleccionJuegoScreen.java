package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EleccionJuegoScreen extends BaseScreen {

    Texture background;
    Texture solitarioNormal, solitarioSelected;
    Texture compartidaNormal, compartidaSelected;

    // UI Helpers
    com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    // 0 = Solitario, 1 = Compartida
    int currentOption = -1;
    final int OPTION_SOLITARIO = 0;
    final int OPTION_COMPARTIDA = 1;

    // Logic States
    boolean isAskingName = false;
    String inputName = "";
    String statusMessage = "";

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    public EleccionJuegoScreen(PokemonMain game) {
        super(game);
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("EleccionJuego", "Could not load background: " + e.getMessage());
        }

        try {
            solitarioNormal = new Texture("boton_solitario_normal.png");
            solitarioSelected = new Texture("boton_solitario_seleccionado.png");
            compartidaNormal = new Texture("boton_compartida_normal.png");
            compartidaSelected = new Texture("boton_compartida_seleccionado.png");
        } catch (Exception e) {
            Gdx.app.log("EleccionJuego", "Could not load button textures: " + e.getMessage());
        }

        // Setup Input Processor for typing
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (isAskingName) {
                    if (Character.isLetterOrDigit(character) || character == ' ') {
                        if (inputName.length() < 15) {
                            inputName += character;
                        }
                    } else if (character == '\b') { // Backspace
                        if (inputName.length() > 0) {
                            inputName = inputName.substring(0, inputName.length() - 1);
                        }
                    } else if (character == '\r' || character == '\n') { // Enter
                        if (currentOption == OPTION_SOLITARIO) {
                            startSoloGame();
                        }
                        // Compartida handled directly in main input loop now, no name asking
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (isAskingName) {
                    if (keycode == Input.Keys.ESCAPE) {
                        isAskingName = false;
                        statusMessage = "";
                        inputName = "";
                        return true;
                    }
                }
                return false;
            }
        });

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        // Clear Screen
        ScreenUtils.clear(0, 0, 0, 1);

        // --- Update Logic ---
        float screenWidth = VIRTUAL_WIDTH;
        float screenHeight = VIRTUAL_HEIGHT;

        // Layout
        float buttonWidth = 500;
        float buttonHeight = 120;
        float spacing = -15;

        // Positions
        float centerX = (screenWidth - buttonWidth) / 2;
        float totalHeight = (buttonHeight * 2) + spacing;
        float startY = (screenHeight + totalHeight) / 2 - 100;

        float solitarioY = startY - buttonHeight;
        float compartidaY = solitarioY - spacing - buttonHeight;

        // Input Handling (Only keyboard, no mouse)
        if (!isAskingName) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                if (currentOption == -1)
                    currentOption = OPTION_COMPARTIDA;
                else if (currentOption == OPTION_COMPARTIDA)
                    currentOption = OPTION_SOLITARIO;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if (currentOption == -1)
                    currentOption = OPTION_SOLITARIO;
                else if (currentOption == OPTION_SOLITARIO)
                    currentOption = OPTION_COMPARTIDA;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (currentOption == OPTION_SOLITARIO) {
                    isAskingName = true;
                    inputName = ""; // Reset
                    statusMessage = "";
                } else if (currentOption == OPTION_COMPARTIDA) {
                    startCompartidaGame();
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return;
            }
        }
        // --- Draw Logic ---
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // Draw Buttons
        // Dim them if asking name
        if (isAskingName) {
            game.batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        }

        Texture texSolitario = (currentOption == OPTION_SOLITARIO) ? solitarioSelected : solitarioNormal;
        if (texSolitario != null)
            game.batch.draw(texSolitario, centerX, solitarioY, buttonWidth, buttonHeight);

        Texture texCompartida = (currentOption == OPTION_COMPARTIDA) ? compartidaSelected : compartidaNormal;
        if (texCompartida != null)
            game.batch.draw(texCompartida, centerX, compartidaY, buttonWidth, buttonHeight);

        game.batch.setColor(Color.WHITE);
        game.batch.end();

        // Draw Name Input Overlay
        if (isAskingName) {
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                    com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

            // Backdrop
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);

            // Box
            float boxW = 400;
            float boxH = 200;
            float boxX = (screenWidth - boxW) / 2;
            float boxY = (screenHeight - boxH) / 2;

            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(boxX, boxY, boxW, boxH);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.rect(boxX + 5, boxY + 5, boxW - 10, boxH - 10);

            // Text Input Field
            float fieldW = 300;
            float fieldH = 40;
            float fieldX = boxX + (boxW - fieldW) / 2;
            float fieldY = boxY + 80;
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(fieldX, fieldY, fieldW, fieldH);

            shapeRenderer.end();
            Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

            // Text
            game.batch.begin();
            game.font.getData().setScale(1.2f);

            // Title
            game.font.setColor(Color.BLACK);
            // Only Solitario asks for name here now
            String title = "¿Nombre de la partida?";
            game.font.draw(game.batch, title, boxX, boxY + boxH - 30, boxW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Input
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, inputName + (System.currentTimeMillis() % 1000 > 500 ? "|" : ""), fieldX + 10,
                    fieldY + 28);

            // Instructions
            game.font.getData().setScale(0.8f);
            game.font.draw(game.batch, "Enter: Confirmar   Esc: Cancelar", boxX, boxY + 40, boxW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Status/Error
            if (!statusMessage.isEmpty()) {
                game.font.setColor(Color.RED);
                game.font.draw(game.batch, statusMessage, boxX, boxY + 65, boxW, com.badlogic.gdx.utils.Align.center,
                        false);
            }

            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1.0f);
            game.batch.end();
        }
    }

    private void startSoloGame() {
        if (inputName.trim().isEmpty()) {
            statusMessage = "¡El nombre no puede estar vacío!";
            return;
        }

        // Check if file already exists
        if (Gdx.files.local(inputName + "_save.dat").exists()) {
            statusMessage = "nombre de partida ya existe";
            return;
        }

        // Transition to Intro directly (No Sockets)
        game.setScreen(new IntroScreen(game, inputName));
        dispose();
    }

    private void startCompartidaGame() {
        // No input validation needed as we don't ask for generic password/name anymore
        // Pass a generic temporary ID, the real name is asked in IntroScreen
        game.setScreen(new CompartidaScreen(game, "Explorador"));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (solitarioNormal != null)
            solitarioNormal.dispose();
        if (solitarioSelected != null)
            solitarioSelected.dispose();
        if (compartidaNormal != null)
            compartidaNormal.dispose();
        if (compartidaSelected != null)
            compartidaSelected.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
