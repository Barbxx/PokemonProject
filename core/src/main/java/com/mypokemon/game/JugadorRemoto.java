package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mypokemon.game.utils.UtilidadesTextura;

/**
 * Representa a un jugador remoto en una sesión multijugador.
 * Gestiona su posición, animación actual e interpolación básica para el
 * renderizado.
 */
public class JugadorRemoto {
    /** Posición X actual en el mundo. */
    public float x;
    /** Posición Y actual en el mundo. */
    public float y;
    /** Frame actual de la animación para renderizar. */
    public TextureRegion currentFrame;
    /** Tiempo de estado acumulado para animaciones. */
    public float stateTime;
    /** Nombre del jugador remoto. */
    public String name = ""; // Vacío por defecto

    // Animaciones (Reutilizando la misma lógica de texturas que el jugador local)
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;

    /**
     * Constructor del jugador remoto.
     * 
     * @param sheet Textura completa (spritesheet) del personaje.
     * @param cols  Número de columnas en el sheet.
     * @param rows  Número de filas en el sheet.
     */
    public JugadorRemoto(Texture sheet, int cols, int rows) {
        int frameWidth = sheet.getWidth() / cols;
        int frameHeight = sheet.getHeight() / rows;
        TextureRegion[][] frames = TextureRegion.split(sheet, frameWidth, frameHeight);

        if (frames.length >= 4) {
            walkDown = new Animation<>(0.15f, frames[0]);
            walkLeft = new Animation<>(0.15f, frames[1]);
            walkRight = new Animation<>(0.15f, frames[2]);
            walkUp = new Animation<>(0.15f, frames[3]);
            currentFrame = frames[0][0];
        }
    }

    /**
     * Actualiza el estado visual del jugador remoto.
     * 
     * @param delta     Tiempo transcurrido desde el último frame.
     * @param targetX   Nueva posición X objetivo.
     * @param targetY   Nueva posición Y objetivo.
     * @param direccion Dirección de movimiento (Enum Direccion) para la animación.
     */
    public void update(float delta, float targetX, float targetY, com.mypokemon.game.utils.Direccion direccion) {
        this.x = targetX;
        this.y = targetY;

        stateTime += delta;

        if (direccion == null)
            return;

        switch (direccion) {
            case IZQUIERDA:
                currentFrame = walkLeft.getKeyFrame(stateTime, true);
                break;
            case DERECHA:
                currentFrame = walkRight.getKeyFrame(stateTime, true);
                break;
            case ARRIBA:
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case ABAJO:
            default:
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                break;
        }
    }
}
