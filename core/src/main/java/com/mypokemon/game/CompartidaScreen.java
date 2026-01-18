package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class CompartidaScreen extends BaseScreen {

    Texture background;
    Texture crearCodigoNormal, crearCodigoSelected;
    Texture unirseNormal, unirseSelected;

    // 0 = Crear Codigo, 1 = Unirse
    int currentOption = -1;
    final int OPTION_CREAR = 0;
    final int OPTION_UNIRSE = 1;

    public CompartidaScreen(PokemonMain game) {
        super(game);
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
            Gdx.app.log("CompartidaScreen", "Could not load background: " + e.getMessage());
        }

        try {
            crearCodigoNormal = new Texture("boton_crearCodigo_normal.png");
            crearCodigoSelected = new Texture("boton_crearCodigo_seleccionado.png");
            unirseNormal = new Texture("boton_unirse_normal.png");
            unirseSelected = new Texture("boton_unirse_seleccionado.png");
        } catch (Exception e) {
            Gdx.app.log("CompartidaScreen", "Could not load button textures: " + e.getMessage());
        }
    }

    @Override
    public void render(float delta) {
        // Update Logic
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Layout (Made bigger and centered as requested)
        float buttonWidth = 500;
        float buttonHeight = 120;
        float spacing = -15;

        float centerX = (screenWidth - buttonWidth) / 2;
        float totalHeight = (buttonHeight * 2) + spacing;
        // Center vertically: (ScreenHeight / 2) + (TotalHeight / 2) gives the top Y
        float startY = (screenHeight + totalHeight) / 2 - 100;

        float crearY = startY - buttonHeight;
        float unirseY = crearY - spacing - buttonHeight;

        // Input Handling
        float mouseX = Gdx.input.getX();
        float mouseY = screenHeight - Gdx.input.getY();

        int hovered = -1;

        if (mouseX >= centerX && mouseX <= centerX + buttonWidth &&
                mouseY >= crearY && mouseY <= crearY + buttonHeight) {
            hovered = OPTION_CREAR;
        } else if (mouseX >= centerX && mouseX <= centerX + buttonWidth &&
                mouseY >= unirseY && mouseY <= unirseY + buttonHeight) {
            hovered = OPTION_UNIRSE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentOption == -1)
                currentOption = OPTION_UNIRSE;
            else if (currentOption == OPTION_UNIRSE)
                currentOption = OPTION_CREAR;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1)
                currentOption = OPTION_CREAR;
            else if (currentOption == OPTION_CREAR)
                currentOption = OPTION_UNIRSE;
        }

        if (hovered != -1) {
            currentOption = hovered;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (currentOption == OPTION_CREAR) {
                Gdx.app.log("CompartidaScreen", "Crear Codigo selected");
                // Future logic
            } else if (currentOption == OPTION_UNIRSE) {
                Gdx.app.log("CompartidaScreen", "Unirse selected");
                // Future logic
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new EleccionJuegoScreen(game));
            dispose();
            return;
        }

        // Draw Logic
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        Texture texCrear = crearCodigoNormal;
        if (currentOption == OPTION_CREAR) {
            if (crearCodigoSelected != null)
                texCrear = crearCodigoSelected;
        }
        if (texCrear != null) {
            game.batch.draw(texCrear, centerX, crearY, buttonWidth, buttonHeight);
        }

        Texture texUnirse = unirseNormal;
        if (currentOption == OPTION_UNIRSE) {
            if (unirseSelected != null)
                texUnirse = unirseSelected;
        }
        if (texUnirse != null) {
            game.batch.draw(texUnirse, centerX, unirseY, buttonWidth, buttonHeight);
        }

        game.batch.end();
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (crearCodigoNormal != null)
            crearCodigoNormal.dispose();
        if (crearCodigoSelected != null)
            crearCodigoSelected.dispose();
        if (unirseNormal != null)
            unirseNormal.dispose();
        if (unirseSelected != null)
            unirseSelected.dispose();
    }
}
