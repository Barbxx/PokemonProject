package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.utils.IInputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;

/**
 * Clase base abstracta para pantallas de menú con navegación por teclado y
 * ratón.
 * Proporciona funcionalidad común como selección de opciones y efectos
 * visuales.
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
     * Inicializa las opciones del menú y carga sus texturas.
     * 
     * @param options      Array de nombres de opciones.
     * @param filePrefixes Array de prefijos de archivo para las texturas (ej:
     *                     "boton_jugar").
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
     * Gestiona la navegación por menú mediante teclado (Flechas y WASD).
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
     * Comprueba qué opción del menú está bajo el ratón.
     * 
     * @param buttonX      Posición X de los botones.
     * @param buttonY      Array de posiciones Y de cada botón.
     * @param buttonWidth  Ancho de los botones.
     * @param buttonHeight Alto de los botones.
     * @return Índice de la opción bajo el ratón, o -1 si ninguna.
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
     * Se llama cuando se selecciona una opción (clic o Enter).
     * 
     * @param optionIndex Índice de la opción seleccionada.
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
