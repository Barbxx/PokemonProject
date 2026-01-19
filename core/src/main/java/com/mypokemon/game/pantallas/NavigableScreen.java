package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

/**
 * Abstract base class for screens that support navigation back to a previous
 * screen.
 * Provides automatic ESC key handling to return to the previous screen.
 */
public abstract class NavigableScreen extends BaseScreen implements INavigable {

    protected final Screen returnScreen;

    public NavigableScreen(PokemonMain game, Screen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;
    }

    /**
     * Navigates back to the return screen.
     * Automatically disposes this screen.
     */
    @Override
    public void navigateBack() {
        if (returnScreen != null) {
            game.setScreen(returnScreen);
            dispose();
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "Warning: No return screen set");
        }
    }

    /**
     * Navigates to a new screen.
     * Automatically disposes this screen.
     * 
     * @param screen The screen to navigate to
     */
    @Override
    public void navigateTo(Screen screen) {
        game.setScreen(screen);
        dispose();
    }

    /**
     * Gets the screen to return to when navigating back.
     * 
     * @return The return screen, or null if none is set
     */
    @Override
    public Screen getReturnScreen() {
        return returnScreen;
    }

    /**
     * Handles the ESC key press to navigate back.
     * Subclasses can override this to customize escape behavior.
     * 
     * @return true if ESC was handled, false otherwise
     */
    protected boolean handleEscapeKey() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigateBack();
            return true;
        }
        return false;
    }
}
