package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Utilidades para la creación y manipulación de texturas.
 */
public class TextureUtils {

    /**
     * Crea una textura de color sólido del tamaño especificado.
     * 
     * @param width  Ancho de la textura.
     * @param height Alto de la textura.
     * @param color  Color de relleno.
     * @return Textura generada.
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
