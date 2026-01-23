package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class RenderUtils {

    /**
     * Dibuja un rectángulo con esquinas redondeadas utilizando ShapeRenderer.
     * 
     * @param shapeRenderer Instancia de ShapeRenderer para el dibujo.
     * @param x             Coordenada X inicial.
     * @param y             Coordenada Y inicial.
     * @param width         Ancho del rectángulo.
     * @param height        Alto del rectángulo.
     * @param radius        Radio de las esquinas redondeadas.
     * @param color         Color de relleno.
     */
    public static void drawRoundedRect(ShapeRenderer shapeRenderer, float x, float y, float width, float height,
            float radius, Color color) {

        float maxRadius = Math.min(width, height) / 2f;
        radius = Math.min(radius, maxRadius);

        shapeRenderer.setColor(color);

        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);

        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius);

        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius);

        int segments = 20;
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90, segments); // Bottom Left
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90, segments); // Bottom Right
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90, segments); // Top Left
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90, segments); // Top Right
    }
}
