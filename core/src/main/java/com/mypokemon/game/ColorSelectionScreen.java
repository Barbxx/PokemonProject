package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class ColorSelectionScreen implements Screen {
    final PokemonMain game;

    Texture redTexture, greenTexture, blueTexture, yellowTexture;
    Rectangle redBounds, greenBounds, blueBounds, yellowBounds;

    public ColorSelectionScreen(final PokemonMain game) {
        this.game = game;

        // generated colored textures
        redTexture = createColorTexture(Color.RED);
        greenTexture = createColorTexture(Color.GREEN);
        blueTexture = createColorTexture(Color.BLUE);
        yellowTexture = createColorTexture(Color.YELLOW);

        // Define positions (simple 2x2 grid centered)
        float buttonSize = 100;
        float spacing = 20;
        float startX = (Gdx.graphics.getWidth() - (buttonSize * 2 + spacing)) / 2;
        float startY = (Gdx.graphics.getHeight() - (buttonSize * 2 + spacing)) / 2;

        // Top Row
        redBounds = new Rectangle(startX, startY + buttonSize + spacing, buttonSize, buttonSize);
        greenBounds = new Rectangle(startX + buttonSize + spacing, startY + buttonSize + spacing, buttonSize,
                buttonSize);

        // Bottom Row
        blueBounds = new Rectangle(startX, startY, buttonSize, buttonSize);
        yellowBounds = new Rectangle(startX + buttonSize + spacing, startY, buttonSize, buttonSize);
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f); // Dark gray background

        game.batch.begin();
        game.batch.draw(redTexture, redBounds.x, redBounds.y, redBounds.width, redBounds.height);
        game.batch.draw(greenTexture, greenBounds.x, greenBounds.y, greenBounds.width, greenBounds.height);
        game.batch.draw(blueTexture, blueBounds.x, blueBounds.y, blueBounds.width, blueBounds.height);
        game.batch.draw(yellowTexture, yellowBounds.x, yellowBounds.y, yellowBounds.width, yellowBounds.height);

        // Draw some text instructions
        // Assuming font exists in PokemonMain, otherwise we might need to load one or
        // use default
        if (game.font != null) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "SELECT A COLOR (CHOOSE RED TO PLAY)", redBounds.x - 50,
                    redBounds.y + redBounds.height + 40);
        }

        game.batch.end();

        // Handle Input
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y

            if (redBounds.contains(mouseX, mouseY)) {
                // RED -> Start Game with Protagonista Femenino (replacing jugador1)
                game.setScreen(new GameScreen(game, "protagonistaFemenino.png", 4, 4));
                dispose();
            } else if (greenBounds.contains(mouseX, mouseY)) {
                // GREEN -> Start Game with Protagonista Masculino 1
                // Assuming standard 4x4 layout
                game.setScreen(new GameScreen(game, "protagonistaMasculino1.png", 4, 4));
                dispose();
            } else if (blueBounds.contains(mouseX, mouseY)) {
                // BLUE -> Start Game with Protagonista Masculino 2
                // Based on standard 4x4 layout
                game.setScreen(new GameScreen(game, "protagonistaMasculino2.png", 4, 4));
                dispose();
            } else if (yellowBounds.contains(mouseX, mouseY)) {
                // YELLOW -> Start Game with Dr. Brenner
                // Based on standard 4x4 layout
                game.setScreen(new GameScreen(game, "drBrenner.png", 4, 4));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        redTexture.dispose();
        greenTexture.dispose();
        blueTexture.dispose();
        yellowTexture.dispose();
    }
}
