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
    boolean saveFileExists = false;
    String saveFileName = "Directioner_save.dat";
    String displayText = "No saved games found.";

    public PartidasScreen(PokemonMain game) {
        super(game);
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("PartidasScreen", "Could not load background: " + e.getMessage());
        }

        // Check for save file
        FileHandle file = Gdx.files.local(saveFileName);
        if (file.exists()) {
            saveFileExists = true;
            displayText = "1. " + saveFileName + " (Size: " + file.length() + " bytes)";
        }
    }

    @Override
    public void render(float delta) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        // Simple interaction: if file exists and Enter/Click, load it (Placeholder
        // logic)
        if (saveFileExists) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                // Here we would trigger the loading logic
                // For now, maybe just go to game or stay here with a message
                // game.setScreen(new GameScreen(game)); // Example
                Gdx.app.log("PartidasScreen", "Loading save file...");
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // Title
        game.font.setColor(Color.YELLOW);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "PARTIDAS GUARDADAS", 0, screenHeight - 50, screenWidth,
                com.badlogic.gdx.utils.Align.center, false);

        // List
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.2f);

        GlyphLayout layout = new GlyphLayout(game.font, displayText);
        float textX = (screenWidth - layout.width) / 2;
        float textY = screenHeight / 2;

        game.font.draw(game.batch, displayText, textX, textY);

        // Instruction
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "Press ESC to return", 0, 50, screenWidth, com.badlogic.gdx.utils.Align.center,
                false);

        game.batch.end();
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
    }
}
