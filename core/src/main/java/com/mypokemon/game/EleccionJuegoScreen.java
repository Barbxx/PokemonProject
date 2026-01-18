package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class EleccionJuegoScreen extends BaseScreen {

    Texture background;
    Texture solitarioNormal, solitarioSelected;
    Texture compartidaNormal, compartidaSelected;

    // 0 = Solitario, 1 = Compartida
    int currentOption = -1; // -1 means nothing selected initially, or we can default to 0
    final int OPTION_SOLITARIO = 0;
    final int OPTION_COMPARTIDA = 1;

    public EleccionJuegoScreen(PokemonMain game) {
        super(game);
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
    }

    @Override
    public void render(float delta) {
        // Update Logic
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Layout
        float buttonWidth = 500; // Increased from 300
        float buttonHeight = 120; // Increased from 80
        float spacing = -15; // Decreased to negative to overlap/tighten

        // Positions
        float centerX = (screenWidth - buttonWidth) / 2;
        float totalHeight = (buttonHeight * 2) + spacing;
        // Move significantly lower.
        // Previously: (screenHeight + totalHeight) / 2; (Centered)
        // New: Centered - 150 pixels (visually lower)
        float startY = (screenHeight + totalHeight) / 2 - 100;

        float solitarioY = startY - buttonHeight;
        float compartidaY = solitarioY - spacing - buttonHeight;

        // Input Handling
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();

        int hovered = -1;

        // Check hover Solitario
        if (mouseX >= centerX && mouseX <= centerX + buttonWidth &&
                mouseY >= solitarioY && mouseY <= solitarioY + buttonHeight) {
            hovered = OPTION_SOLITARIO;
        }
        // Check hover Compartida
        else if (mouseX >= centerX && mouseX <= centerX + buttonWidth &&
                mouseY >= compartidaY && mouseY <= compartidaY + buttonHeight) {
            hovered = OPTION_COMPARTIDA;
        }

        // Keyboard Navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentOption == -1)
                currentOption = OPTION_COMPARTIDA; // Start from bottom
            else if (currentOption == OPTION_COMPARTIDA)
                currentOption = OPTION_SOLITARIO;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1)
                currentOption = OPTION_SOLITARIO; // Start from top
            else if (currentOption == OPTION_SOLITARIO)
                currentOption = OPTION_COMPARTIDA;
        }

        // Apply hover if mouse is active (optional: only if mouse moved, but simple
        // hover is fine)
        if (hovered != -1) {
            currentOption = hovered;
        }

        // Selection / Action
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (currentOption == OPTION_SOLITARIO) {
                // Start IntegroScreen / Jugar logic
                game.setScreen(new IntroScreen(game)); // Assuming IntroScreen starts the flow
                dispose();
                return;
            } else if (currentOption == OPTION_COMPARTIDA) {
                game.setScreen(new CompartidaScreen(game));
                dispose();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        // Draw Logic
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // Draw Solitario
        Texture texSolitario = solitarioNormal;
        if (currentOption == OPTION_SOLITARIO) {
            if (solitarioSelected != null)
                texSolitario = solitarioSelected;
        }
        if (texSolitario != null) {
            game.batch.draw(texSolitario, centerX, solitarioY, buttonWidth, buttonHeight);
        }

        // Draw Compartida
        Texture texCompartida = compartidaNormal;
        if (currentOption == OPTION_COMPARTIDA) {
            if (compartidaSelected != null)
                texCompartida = compartidaSelected;
        }
        if (texCompartida != null) {
            game.batch.draw(texCompartida, centerX, compartidaY, buttonWidth, buttonHeight);
        }

        game.batch.end();
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
    }
}
