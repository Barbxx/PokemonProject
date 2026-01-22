package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Screen;

/**
 * Interface for screens that support navigation between screens.
 * Implementing classes should maintain a return screen and handle navigation.
 */
public interface INavegable {

    /**
     * Navigates back to the previous screen.
     * Disposes the current screen and sets the return screen as active.
     */
    void navigateBack();

    /**
     * Navigates to a new screen.
     * 
     * @param screen The screen to navigate to
     */
    void navigateTo(Screen screen);

    /**
     * Gets the screen to return to when navigating back.
     * 
     * @return The return screen, or null if none is set
     */
    Screen getReturnScreen();
}




