package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PartidasScreen extends BaseScreen {

    Texture background;
    FileHandle[] saveFiles;
    int selectedIndex = 0;
    boolean selectingAction = false;
    int actionIndex = 0;

    // UI
    com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    // Camera and Viewport for fixed aspect ratio
    private OrthographicCamera camera;
    private Viewport viewport;
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    public PartidasScreen(PokemonMain game) {
        super(game);
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("PartidasScreen", "Could not load background: " + e.getMessage());
        }

        // Find Save Files
        FileHandle local = Gdx.files.local(".");
        saveFiles = local.list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));

        if (saveFiles == null) {
            saveFiles = new FileHandle[0];
        }

        // Setup camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        float screenWidth = VIRTUAL_WIDTH;
        float screenHeight = VIRTUAL_HEIGHT;

        // Geometry Calculations
        float panelW = 600;
        float panelH = 350;
        float panelX = (screenWidth - panelW) / 2;
        float panelY = (screenHeight - panelH) / 2 - 50;

        float btnW = 120;
        float btnH = 40;
        float btnY = panelY + 20;
        float btnPlayX = panelX + (panelW / 2) - btnW - 20;
        float btnDelX = panelX + (panelW / 2) + 20;

        float listStartY = panelY + panelH - 100;
        float spacing = 35;

        // Input Handling
        if (saveFiles.length > 0) {
            String gameNameForLogic = saveFiles[selectedIndex].nameWithoutExtension();

            if (!selectingAction) {
                // FILE SELECTION PHASE (Keyboard)
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    selectedIndex--;
                    if (selectedIndex < 0)
                        selectedIndex = saveFiles.length - 1;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    selectedIndex++;
                    if (selectedIndex >= saveFiles.length)
                        selectedIndex = 0;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    selectingAction = true;
                    actionIndex = 0; // Default to Play
                }
            } else {
                // ACTION SELECTION PHASE (Play / Delete)
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                    actionIndex = 0; // Play
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                    actionIndex = 1; // Delete
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    selectingAction = false;
                }

                // Execute Logic (Only Enter key, removed mouse clicks)
                boolean actionTriggered = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

                if (actionTriggered) {
                    if (actionIndex == 0) {
                        // PLAY
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        String fullFileName = selectedFile.name();

                        // Pass FULL filename so Explorador.cargarProgreso can find it.
                        Gdx.app.log("PartidasScreen", "Loading: " + fullFileName);
                        game.setScreen(new GameScreen(game, "protagonistaMasculino1.png", 4, 4, "",
                                fullFileName));
                        dispose();
                        return;
                    } else {
                        // DELETE
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        selectedFile.delete();
                        Gdx.app.log("PartidasScreen", "Deleted: " + selectedFile.name());

                        // Refresh List
                        FileHandle local = Gdx.files.local(".");
                        saveFiles = local.list((dir, name) -> name.contains(" - ") && name.endsWith(".dat"));
                        if (saveFiles == null)
                            saveFiles = new FileHandle[0];

                        selectedIndex = 0;
                        selectingAction = false;
                        if (saveFiles.length == 0) {
                            // No files left
                        }
                    }
                }
            }
        }
        if (!selectingAction && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        // Draw Background
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }
        game.batch.end();

        // Draw UI Panel (Glassmorphism / Dark Box)
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        // Shadow/Border
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(panelX + 5, panelY - 5, panelW, panelH);

        // Main Body
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Header Strip
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.9f);
        shapeRenderer.rect(panelX, panelY + panelH - 60, panelW, 60);

        // Action Buttons Backgrounds (Visual cues)
        if (saveFiles.length > 0) {
            // Jugar Btn (Left)
            if (selectingAction && actionIndex == 0)
                shapeRenderer.setColor(0.8f, 0.8f, 0.2f, 1f); // Active Yellow
            else
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); // Inactive Gray
            shapeRenderer.rect(btnPlayX, btnY, btnW, btnH);

            // Borrar Btn (Right)
            if (selectingAction && actionIndex == 1)
                shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f); // Active Red
            else
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f); // Inactive Gray
            shapeRenderer.rect(btnDelX, btnY, btnW, btnH);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        // Draw Text
        game.batch.begin();

        // Title
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.5f);
        float titleY = panelY + panelH - 18;
        game.font.draw(game.batch, "PARTIDAS GUARDADAS", panelX, titleY, panelW, com.badlogic.gdx.utils.Align.center,
                false);

        // List
        game.font.getData().setScale(1.1f);

        if (saveFiles.length == 0) {
            game.font.setColor(Color.GRAY);
            game.font.draw(game.batch, "No hay partidas guardadas.", panelX, panelY + panelH / 2, panelW,
                    com.badlogic.gdx.utils.Align.center, false);
        } else {
            int maxItems = 7;
            int startIdx = Math.max(0, Math.min(selectedIndex - 3, saveFiles.length - maxItems));
            int endIdx = Math.min(saveFiles.length, startIdx + maxItems);

            for (int i = startIdx; i < endIdx; i++) {
                FileHandle file = saveFiles[i];
                String nameDisplay = file.nameWithoutExtension(); // Display filename directly
                float textY = listStartY - ((i - startIdx) * spacing);

                if (i == selectedIndex) {
                    if (selectingAction)
                        game.font.setColor(Color.LIME); // Selected but locked
                    else
                        game.font.setColor(Color.YELLOW); // Active selection
                    nameDisplay = "> " + nameDisplay + " <";
                } else {
                    game.font.setColor(Color.LIGHT_GRAY);
                }

                game.font.draw(game.batch, nameDisplay, panelX, textY, panelW, com.badlogic.gdx.utils.Align.center,
                        false);
            }

            // Draw Button Labels
            game.font.getData().setScale(1.0f);

            // JUGAR Text
            game.font.setColor(Color.WHITE);
            // Centering text roughly inside button
            game.font.draw(game.batch, "JUGAR", btnPlayX, btnY + 28, btnW, com.badlogic.gdx.utils.Align.center, false);
            // BORRAR Text
            game.font.draw(game.batch, "BORRAR", btnDelX, btnY + 28, btnW, com.badlogic.gdx.utils.Align.center, false);
        }

        // Instruction Footer (Moved down)
        // Only show if NOT selecting action
        if (!selectingAction) {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.GRAY);
            game.font.draw(game.batch, "ESC: Volver   ENTER: Seleccionar", panelX, panelY - 10, panelW,
                    com.badlogic.gdx.utils.Align.center, false);
        } else {
            game.font.getData().setScale(0.8f);
            game.font.setColor(Color.GRAY);
            game.font.draw(game.batch, "<- -> : Elegir   ENTER: Confirmar   ESC: Cancelar", panelX, panelY - 10, panelW,
                    com.badlogic.gdx.utils.Align.center, false);
        }

        game.batch.end();
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
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
