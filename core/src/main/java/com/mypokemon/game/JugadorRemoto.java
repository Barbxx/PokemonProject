package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mypokemon.game.utils.UtilidadesTextura;

public class JugadorRemoto {
    public float x, y;
    public TextureRegion currentFrame;
    public float stateTime;
    public String name = ""; // Default empty

    // Animations (Reusing same texture logic as local player)
    private Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;

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




