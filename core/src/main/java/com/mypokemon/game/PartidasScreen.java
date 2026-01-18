package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class PartidasScreen extends BaseScreen {

    Texture background;
    FileHandle[] saveFiles;
    int selectedIndex = 0;

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

        // Input Handling
        if (saveFiles.length > 0) {
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
                FileHandle selectedFile = saveFiles[selectedIndex];
                String fileName = selectedFile.name();
                String gameName = fileName.replace("_save.dat", "");
                String explorerName = gameName; // default

                try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(selectedFile.read())) {
                    Explorador exp = (Explorador) in.readObject();
                    if (exp != null)
                        explorerName = exp.getNombre();
                } catch (Exception e) {
                }

                Gdx.app.log("PartidasScreen", "Loading: " + gameName);
                game.setScreen(new GameScreen(game, "protagonistaMasculino1.png", 4, 4, explorerName, gameName));
                dispose();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(game.batch.getProjectionMatrix());
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

        // Panel Dimensions (Centered, occupancy ~60% width, ~50% height, placed lower)
        float panelW = 600;
        float panelH = 350;
        float panelX = (screenWidth - panelW) / 2;
        float panelY = (screenHeight - panelH) / 2 - 50; // Shifted slightly down to avoid logo center

        // Shadow/Border
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(panelX + 5, panelY - 5, panelW, panelH); // Shadow

        // Main Body
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f); // Dark elegant gray
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Header Strip
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.9f); // Pokemon Red
        shapeRenderer.rect(panelX, panelY + panelH - 60, panelW, 60);

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // Draw Text
        game.batch.begin();

        // Title (Centered in Header Strip)
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.5f);
        float titleY = panelY + panelH - 18;
        game.font.draw(game.batch, "PARTIDAS GUARDADAS", panelX, titleY, panelW, com.badlogic.gdx.utils.Align.center,
                false);

        // List
        game.font.getData().setScale(1.1f);

        // List Viewport inside panel
        float listStartY = panelY + panelH - 100;
        float spacing = 35;

        if (saveFiles.length == 0) {
            game.font.setColor(Color.GRAY);
            game.font.draw(game.batch, "No hay partidas guardadas.", panelX, panelY + panelH / 2, panelW,
                    com.badlogic.gdx.utils.Align.center, false);
        } else {
            // Show only up to 5 items to fit, or scrolling (basic implementation: max 6)
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
                    game.font.setColor(Color.YELLOW);
                    nameDisplay = "> " + nameDisplay + " <";
                } else {
                    game.font.setColor(Color.LIGHT_GRAY);
                }

                game.font.draw(game.batch, nameDisplay, panelX, textY, panelW, com.badlogic.gdx.utils.Align.center,
                        false);
            }
        }

        // Instructions
        game.font.getData().setScale(0.8f);
        game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "ESC: Volver   ENTER: Cargar", panelX, panelY + 30, panelW,
                com.badlogic.gdx.utils.Align.center, false);

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
