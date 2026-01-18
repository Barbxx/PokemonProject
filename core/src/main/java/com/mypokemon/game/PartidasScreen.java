package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class PartidasScreen extends BaseScreen {

    Texture background;
    FileHandle[] saveFiles;
    int selectedIndex = 0;
    boolean selectingAction = false;
    int actionIndex = 0;

    // UI
    com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

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
        saveFiles = local.list((dir, name) -> name.endsWith("_save.dat"));

        if (saveFiles == null) {
            saveFiles = new FileHandle[0];
        }
    }

    @Override
    public void render(float delta) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

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

        // Mouse Input
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY(); // World Y
        boolean clicked = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        // Input Handling
        if (saveFiles.length > 0) {
            String gameNameForLogic = saveFiles[selectedIndex].name().replace("_save.dat", "");

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

                // MOUSE SELECTION PHASE (List Items)
                int maxItems = 7;
                int startIdx = Math.max(0, Math.min(selectedIndex - 3, saveFiles.length - maxItems));
                int endIdx = Math.min(saveFiles.length, startIdx + maxItems);

                for (int i = startIdx; i < endIdx; i++) {
                    float itemY = listStartY - ((i - startIdx) * spacing);
                    // Check bounds (approx width for text)
                    if (mouseX >= panelX && mouseX <= panelX + panelW &&
                            mouseY >= itemY - spacing / 2 && mouseY <= itemY + spacing / 2) {

                        if (clicked) {
                            selectedIndex = i;
                            selectingAction = true;
                            actionIndex = 0; // Default to Play
                        }
                    }
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

                // Mouse over Left Button (JUGAR)
                if (mouseX >= btnPlayX && mouseX <= btnPlayX + btnW &&
                        mouseY >= btnY && mouseY <= btnY + btnH) {
                    if (clicked) {
                        actionIndex = 0;
                        // Execute immediately
                    } else {
                        // Optional: Hover effect logic just by setting actionIndex?
                        // Ideally we want to just select it if the user moves mouse there.
                        // But for now let's just create a click trigger below.
                    }
                }

                // Mouse over Right Button (BORRAR)
                if (mouseX >= btnDelX && mouseX <= btnDelX + btnW &&
                        mouseY >= btnY && mouseY <= btnY + btnH) {
                    if (clicked) {
                        actionIndex = 1;
                    }
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    selectingAction = false;
                }

                // Execute Logic (Join Key Enter and Mouse Click)
                boolean actionTriggered = Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

                // Check clicks on buttons again for Trigger
                if (clicked) {
                    if (mouseX >= btnPlayX && mouseX <= btnPlayX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                        actionIndex = 0;
                        actionTriggered = true;
                    }
                    if (mouseX >= btnDelX && mouseX <= btnDelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                        actionIndex = 1;
                        actionTriggered = true;
                    }
                }

                if (actionTriggered) {
                    if (actionIndex == 0) {
                        // PLAY
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        String explorerName = gameNameForLogic; // default

                        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(selectedFile.read())) {
                            Explorador exp = (Explorador) in.readObject();
                            if (exp != null)
                                explorerName = exp.getNombre();
                        } catch (Exception e) {
                        }

                        Gdx.app.log("PartidasScreen", "Loading: " + gameNameForLogic);
                        game.setScreen(new GameScreen(game, "protagonistaMasculino1.png", 4, 4, explorerName,
                                gameNameForLogic));
                        dispose();
                        return;
                    } else {
                        // DELETE
                        FileHandle selectedFile = saveFiles[selectedIndex];
                        selectedFile.delete();
                        Gdx.app.log("PartidasScreen", "Deleted: " + gameNameForLogic);

                        // Refresh List
                        FileHandle local = Gdx.files.local(".");
                        saveFiles = local.list((dir, name) -> name.endsWith("_save.dat"));
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
        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }
        game.batch.end();

        // Draw UI Panel (Glassmorphism / Dark Box)
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
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
                String fileName = file.name();
                String saveName = fileName.replace("_save.dat", "");
                String explorerName = "???";

                try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(file.read())) {
                    Explorador exp = (Explorador) in.readObject();
                    if (exp != null)
                        explorerName = exp.getNombre();
                } catch (Exception e) {
                }

                String nameDisplay = saveName + " - " + explorerName;
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
    public void dispose() {
        if (background != null)
            background.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
