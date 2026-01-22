package com.mypokemon.game.utils;

/**
 * Interface for consistent input handling across screens.
 * Implementing classes should handle keyboard and mouse input.
 */
public interface IGestorEntrada {

    /**
     * Handles keyboard key press events.
     * 
     * @param keycode The keycode of the pressed key
     * @return true if the input was handled, false otherwise
     */
    boolean handleKeyPress(int keycode);

    /**
     * Handles mouse click events.
     * 
     * @param x X coordinate of the click
     * @param y Y coordinate of the click
     * @return true if the input was handled, false otherwise
     */
    boolean handleMouseClick(float x, float y);

    /**
     * Sets up the input processor for this screen.
     * This is called automatically when the screen is shown.
     */
    void setupInputProcessor();
}




