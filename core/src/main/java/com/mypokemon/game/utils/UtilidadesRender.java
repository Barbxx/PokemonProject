package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Utilidades para renderizado de gráficos 2D.
 * Versión duplicada/alias de RenderUtils.
 */
public class UtilidadesRender {

    /**
     * Dibuja un rectángulo con bordes redondeados.
     * 
     * @param shapeRenderer Renderizador de formas.
     * @param x             Posición X.
     * @param y             Posición Y.
     * @param width         Ancho del rectángulo.
     * @param height        Alto del rectángulo.
     * @param radius        Radio de las esquinas.
     * @param color         Color del rectángulo.
     */
    public static void drawRoundedRect(ShapeRenderer shapeRenderer, float x, float y, float width, float height,
            float radius, Color color) {
        // Clamp radius to ensure it doesn't exceed dimensions
        float maxRadius = Math.min(width, height) / 2f;
        radius = Math.min(radius, maxRadius);

        shapeRenderer.setColor(color);

        // Center Rect (Full width, height minus top/bottom radius)
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);

        // Left Rect
        shapeRenderer.rect(x, y + radius, radius, height - 2 * radius);

        // Right Rect
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2 * radius);

        // Corner Arcs (Using 20 segments for smooth look)
        int segments = 20;
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90, segments); // Bottom Left
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90, segments); // Bottom Right
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90, segments); // Top Left
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90, segments); // Top Right
    }
}
