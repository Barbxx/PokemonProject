package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MainMenuScreen implements Screen {

    final PokemonMain game;
    Texture background;

    // Arrays to hold textures for each option [0]=Play, [1]=Profile, etc.
    Texture[] normalTextures;
    Texture[] selectedTextures;

    // Menu Options (Keep strings for fallback or logic, though rendering uses
    // images now)
    String[] options = { "PLAY", "PROFILE", "HELP", "ABOUT" };
    String[] filePrefixes = { "boton_jugar", "boton_perfil", "boton_ayuda", "boton_acercade" };

    int currentOption = -1;

    // Layout constants
    float menuBoxWidth = 370;
    float menuBoxHeight = 260;

    public MainMenuScreen(final PokemonMain game) {
        this.game = game;

        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("MainMenu", "Could not load menu_bg.jpg: " + e.getMessage());
        }

        // Load button textures
        normalTextures = new Texture[options.length];
        selectedTextures = new Texture[options.length];

        for (int i = 0; i < options.length; i++) {
            try {
                normalTextures[i] = new Texture(filePrefixes[i] + "_normal.png");
                selectedTextures[i] = new Texture(filePrefixes[i] + "_seleccionado.png");
            } catch (Exception e) {
                Gdx.app.log("MainMenu", "Could not load images for option " + i + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void render(float delta) {
        // --- 1. Update Logic ---

        // Calculate layout variables first for Input detection
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 300;
        float buttonHeight = 80;
        float spacing = 5;
        float totalMenuHeight = (options.length * buttonHeight) + ((options.length - 1) * spacing);
        float startY = (screenHeight + totalMenuHeight) / 2 - 100;

        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();

        // Determine which option is hovered
        int hoveredOption = -1;
        for (int i = 0; i < options.length; i++) {
            float buttonY = startY - (i * (buttonHeight + spacing)) - buttonHeight;
            float buttonX = (screenWidth - buttonWidth) / 2;
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                hoveredOption = i;
                break; // Found it
            }
        }

        // Mouse Selection Logic: Only update if mouse moved
        // This allows keyboard to work without being overridden by a stationary mouse
        if (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) {
            currentOption = hoveredOption;
        }

        // Keyboard Selection Logic
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentOption == -1) {
                currentOption = options.length - 1; // Start at bottom
            } else {
                currentOption--;
                if (currentOption < 0)
                    currentOption = options.length - 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1) {
                currentOption = 0; // Start at top
            } else {
                currentOption++;
                if (currentOption >= options.length)
                    currentOption = 0;
            }
        }

        // Action Logic (Enter or Click)
        boolean click = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        boolean enter = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if ((hoveredOption != -1 && click) || (currentOption != -1 && enter)) {
            // Determine target: priority to hover if click, else currentOption
            int target = (click && hoveredOption != -1) ? hoveredOption : currentOption;

            if (target == 0) { // PLAY
                game.setScreen(new IntroScreen(game));
                dispose();
                return;
            }
            // Add other cases here
        }

        // --- 2. Draw Logic ---

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        for (int i = 0; i < options.length; i++) {
            float buttonY = startY - (i * (buttonHeight + spacing)) - buttonHeight;
            float buttonX = (screenWidth - buttonWidth) / 2;

            Texture textureToDraw = null;

            if (i == currentOption) {
                if (selectedTextures[i] != null)
                    textureToDraw = selectedTextures[i];
            } else {
                if (normalTextures[i] != null)
                    textureToDraw = normalTextures[i];
            }

            if (textureToDraw != null) {
                game.batch.draw(textureToDraw, buttonX, buttonY, buttonWidth, buttonHeight);
            } else {
                // FALLBACK
                GlyphLayout layout = new GlyphLayout(game.font, options[i]);
                float textX = (screenWidth - layout.width) / 2;
                float textY = buttonY + (buttonHeight + layout.height) / 2;

                if (i == currentOption)
                    game.font.setColor(Color.YELLOW);
                else
                    game.font.setColor(Color.WHITE);

                game.font.draw(game.batch, layout, textX, textY);
            }
        }
        game.batch.end();
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
        if (background != null)
            background.dispose();
        for (Texture t : normalTextures)
            if (t != null)
                t.dispose();
        for (Texture t : selectedTextures)
            if (t != null)
                t.dispose();
    }
}
