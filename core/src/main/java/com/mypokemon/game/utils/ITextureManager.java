package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Texture;

/**
 * Interface for consistent texture management across screens.
 * Implementing classes should handle texture loading and disposal
 * automatically.
 */
public interface ITextureManager {

    /**
     * Loads a texture from the specified path and registers it for automatic
     * disposal.
     * 
     * @param path Path to the texture file
     * @return Loaded texture, or null if loading fails
     */
    Texture loadTexture(String path);

    /**
     * Registers a texture for automatic disposal when the screen is disposed.
     * 
     * @param texture Texture to register
     */
    void addTexture(Texture texture);

    /**
     * Disposes all registered textures.
     * This is called automatically by the screen's dispose method.
     */
    void disposeTextures();
}
