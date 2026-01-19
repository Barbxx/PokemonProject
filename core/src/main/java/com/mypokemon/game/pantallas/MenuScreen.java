package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.IInputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

/**
 * Abstract base class for menu screens with keyboard and mouse navigation.
 * Provides common menu functionality like option selection and hover effects.
 */
public abstract class MenuScreen extends BaseScreen implements IInputHandler {

    protected int currentOption = -1;
    protected String[] options;
    protected Texture[] normalTextures;
    protected Texture[] selectedTextures;

    public MenuScreen(PokemonMain game) {
        super(game);
    }

    /**
     * Initializes menu options and loads their textures.
     * 
     * @param options      Array of option names
     * @param filePrefixes Array of file prefixes for textures (e.g., "boton_jugar")
     */
    protected void initializeMenuOptions(String[] options, String[] filePrefixes) {
        this.options = options;
        this.normalTextures = new Texture[options.length];
        this.selectedTextures = new Texture[options.length];

        for (int i = 0; i < options.length; i++) {
            normalTextures[i] = loadTexture(filePrefixes[i] + "_normal.png");
            selectedTextures[i] = loadTexture(filePrefixes[i] + "_seleccionado.png");
        }
    }

    /**
     * Handles keyboard navigation through menu options.
     */
    protected void handleMenuNavigation() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentOption == -1) {
                currentOption = options.length - 1;
            } else {
                currentOption--;
                if (currentOption < 0) {
                    currentOption = options.length - 1;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (currentOption == -1) {
                currentOption = 0;
            } else {
                currentOption++;
                if (currentOption >= options.length) {
                    currentOption = 0;
                }
            }
        }
    }

    /**
     * Checks which menu option is being hovered by the mouse.
     * 
     * @param buttonX      X position of buttons
     * @param buttonY      Array of Y positions for each button
     * @param buttonWidth  Width of buttons
     * @param buttonHeight Height of buttons
     * @return Index of hovered option, or -1 if none
     */
    protected int getHoveredOption(float buttonX, float[] buttonY, float buttonWidth, float buttonHeight) {
        float mouseX = Gdx.input.getX();
        float mouseY = getScreenHeight() - Gdx.input.getY();

        for (int i = 0; i < options.length; i++) {
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                    mouseY >= buttonY[i] && mouseY <= buttonY[i] + buttonHeight) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Called when a menu option is selected (clicked or Enter pressed).
     * 
     * @param optionIndex Index of the selected option
     */
    protected abstract void handleOptionSelected(int optionIndex);

    @Override
    public boolean handleKeyPress(int keycode) {
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            if (currentOption != -1) {
                handleOptionSelected(currentOption);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseClick(float x, float y) {
        // Override in subclasses to handle mouse clicks on menu options
        return false;
    }

    @Override
    public void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return handleKeyPress(keycode);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = getScreenHeight() - screenY;
                return handleMouseClick(screenX, y);
            }
        });
    }

    @Override
    public void show() {
        setupInputProcessor();
    }
}
