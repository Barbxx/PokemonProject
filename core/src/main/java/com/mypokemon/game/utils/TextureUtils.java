package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Utilidades para la manipulación y creación de texturas.
 */
public class TextureUtils {

    /**
     * Creates a solid color texture of specified size.
     */
    /**
     * Crea una textura de color sólido con el tamaño y color especificados.
     * Útil para crear fondos de menús o elementos de UI simples.
     * 
     * @param width  Ancho de la textura.
     * @param height Alto de la textura.
     * @param color  Color de la textura.
     * @return Una nueva instancia de Texture.
     */
    public static Texture createSolidTexture(int width, int height, Color color) {
        // Ensure dimensions are valid
        if (width <= 0)
            width = 1;
        if (height <= 0)
            height = 1;

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
