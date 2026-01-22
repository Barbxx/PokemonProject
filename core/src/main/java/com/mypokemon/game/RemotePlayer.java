package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mypokemon.game.utils.TextureUtils;

/**
 * Representa a otro jugador conectado en el modo multijugador.
 * Gestiona su posición, animaciones y renderizado.
 */
public class RemotePlayer {
    public float x, y;
    public TextureRegion currentFrame;
    public float stateTime;
    public String name = ""; // Vacio por defecto

    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;

    /**
     * Constructor del jugador remoto.
     * Divide la hoja de sprites para crear las animaciones.
     * 
     * @param sheet Textura completa del personaje.
     * @param cols  Número de columnas en la textura.
     * @param rows  Número de filas en la textura.
     */
    public RemotePlayer(Texture sheet, int cols, int rows) {
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
     * Actualiza la posición y animación del jugador remoto.
     * 
     * @param delta     Tiempo transcurrido.
     * @param targetX   Posición X objetivo.
     * @param targetY   Posición Y objetivo.
     * @param direction Dirección de movimiento ("LEFT", "RIGHT", "UP", "DOWN").
     */
    public void update(float delta, float targetX, float targetY, String direction) {
        this.x = targetX;
        this.y = targetY;

        stateTime += delta;

        if (direction.equals("LEFT"))
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        else if (direction.equals("RIGHT"))
            currentFrame = walkRight.getKeyFrame(stateTime, true);
        else if (direction.equals("UP"))
            currentFrame = walkUp.getKeyFrame(stateTime, true);
        else
            currentFrame = walkDown.getKeyFrame(stateTime, true);
    }
}
