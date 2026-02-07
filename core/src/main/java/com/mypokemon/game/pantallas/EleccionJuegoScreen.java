package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que permite al usuario elegir entre el modo Solitario y el modo
 * Compartido (Multijugador). También maneja la entrada del nombre para nuevas
 * partidas en solitario.
 */
public class EleccionJuegoScreen extends BaseScreen {

    Texture background;
    Texture solitarioNormal, solitarioSelected;
    Texture compartidaNormal, compartidaSelected;

    // UI Ayudantes
    com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    // 0 = Solitario, 1 = Compartida
    int currentOption = -1;
    final int OPTION_SOLITARIO = 0;
    final int OPTION_COMPARTIDA = 1;

    // Logica estados
    boolean isAskingName = false;
    String inputName = "";
    String statusMessage = "";

    // Camara y Viewport
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    /**
     * Constructor de la pantalla. Carga recursos y configura el procesador de
     * entrada para texto.
     *
     * @param game Instancia principal del juego.
     */
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

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (isAskingName) {
                    if (Character.isLetterOrDigit(character) || character == ' ') {
                        if (inputName.length() < 15) {
                            inputName += character;
                        }
                    } else if (character == '\b') {
                        if (inputName.length() > 0) {
                            inputName = inputName.substring(0, inputName.length() - 1);
                        }
                    } else if (character == '\r' || character == '\n') { // Enter
                        if (currentOption == OPTION_SOLITARIO) {
                            startSoloGame();
                        }
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

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    /**
     * Renderiza la pantalla, botones y el cuadro de entrada de nombre si es
     * necesario.
     *
     * @param delta Tiempo transcurrido del frame.
     */
    @Override
    public void render(float delta) {
        // Limpiar pantalla
        ScreenUtils.clear(0, 0, 0, 1);

        // Logica actualizacion
        float screenWidth = VIRTUAL_WIDTH;
        float screenHeight = VIRTUAL_HEIGHT;

        // Botones
        float buttonWidth = 500;
        float buttonHeight = 120;
        float spacing = -15;

        // Posiciones
        float centerX = (screenWidth - buttonWidth) / 2;
        float totalHeight = (buttonHeight * 2) + spacing;
        float startY = (screenHeight + totalHeight) / 2 - 100;

        float solitarioY = startY - buttonHeight;
        float compartidaY = solitarioY - spacing - buttonHeight;

        // Entrada de los botones
        if (!isAskingName) {
            // Detección Cursor
            int mouseScreenX = Gdx.input.getX();
            int mouseScreenY = Gdx.input.getY();

            // Convertir coordenadas de pantalla a coordenadas del mundo
            com.badlogic.gdx.math.Vector3 worldCoords = new com.badlogic.gdx.math.Vector3(mouseScreenX, mouseScreenY, 0);
            camera.unproject(worldCoords, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

            float mouseX = worldCoords.x;
            float mouseY = worldCoords.y;

            // Verifica si el cursor está sobre algún botón
            int hoveredOption = -1;
            if (mouseX >= centerX && mouseX <= centerX + buttonWidth) {
                if (mouseY >= solitarioY && mouseY <= solitarioY + buttonHeight) {
                    hoveredOption = OPTION_SOLITARIO;
                } else if (mouseY >= compartidaY && mouseY <= compartidaY + buttonHeight) {
                    hoveredOption = OPTION_COMPARTIDA;
                }
            }

            if (hoveredOption != -1) {
                currentOption = hoveredOption;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                if (currentOption == -1) {
                    currentOption = OPTION_COMPARTIDA;
                } else if (currentOption == OPTION_COMPARTIDA) {
                    currentOption = OPTION_SOLITARIO;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if (currentOption == -1) {
                    currentOption = OPTION_SOLITARIO;
                } else if (currentOption == OPTION_SOLITARIO) {
                    currentOption = OPTION_COMPARTIDA;
                }
            }

            boolean enter = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
            boolean mouseClick = Gdx.input.justTouched(); // Deteccion click

            if (enter || mouseClick) {
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
        // Logica de Dibujo
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // Dibujar Botones
        if (isAskingName) {
            game.batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        }

        Texture texSolitario = (currentOption == OPTION_SOLITARIO) ? solitarioSelected : solitarioNormal;
        if (texSolitario != null) {
            game.batch.draw(texSolitario, centerX, solitarioY, buttonWidth, buttonHeight);
        }

        Texture texCompartida = (currentOption == OPTION_COMPARTIDA) ? compartidaSelected : compartidaNormal;
        if (texCompartida != null) {
            game.batch.draw(texCompartida, centerX, compartidaY, buttonWidth, buttonHeight);
        }

        game.batch.setColor(Color.WHITE);
        game.batch.end();

        if (isAskingName) {
            Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                    com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

            // Fondo
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);

            float boxW = 400;
            float boxH = 200;
            float boxX = (screenWidth - boxW) / 2;
            float boxY = (screenHeight - boxH) / 2;

            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(boxX, boxY, boxW, boxH);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.rect(boxX + 5, boxY + 5, boxW - 10, boxH - 10);

            float fieldW = 300;
            float fieldH = 40;
            float fieldX = boxX + (boxW - fieldW) / 2;
            float fieldY = boxY + 80;
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(fieldX, fieldY, fieldW, fieldH);

            shapeRenderer.end();
            Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

            // Texto
            game.batch.begin();
            game.font.getData().setScale(1.2f);

            // Titulo
            game.font.setColor(Color.BLACK);
            // En caso de Solitario pide nombre
            String title = "¿Nombre de la partida?";
            game.font.draw(game.batch, title, boxX, boxY + boxH - 30, boxW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Entrada
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, inputName + (System.currentTimeMillis() % 1000 > 500 ? "|" : ""), fieldX + 10,
                    fieldY + 28);

            // Instrucciones
            game.font.getData().setScale(0.8f);
            game.font.draw(game.batch, "Enter: Confirmar   Esc: Cancelar", boxX, boxY + 40, boxW,
                    com.badlogic.gdx.utils.Align.center, false);

            // Estado/Error
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

    /**
     * Inicia una partida en modo Solitario. Valida el nombre y transiciona a la
     * pantalla de Introducción.
     */
    private void startSoloGame() {
        if (inputName.trim().isEmpty()) {
            statusMessage = "¡El nombre no puede estar vacío!";
            return;
        }

        // Chequeo si el archivo ya existe
        FileHandle[] existing = Gdx.files.local(".")
                .list((dir, name) -> name.startsWith(inputName + " - ") && name.endsWith(".dat"));

        if (existing != null && existing.length > 0) {
            statusMessage = "nombre de partida ya existe";
            return;
        }

        // Transicion para intro
        game.setScreen(new IntroScreen(game, inputName));
        dispose();
    }

    private void startCompartidaGame() {

        game.setScreen(new CompartidaScreen(game, "Explorador"));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
    }

    /**
     * Libera los recursos (texturas y renderer) de la pantalla.
     */
    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
        if (solitarioNormal != null) {
            solitarioNormal.dispose();
        }
        if (solitarioSelected != null) {
            solitarioSelected.dispose();
        }
        if (compartidaNormal != null) {
            compartidaNormal.dispose();
        }
        if (compartidaSelected != null) {
            compartidaSelected.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
