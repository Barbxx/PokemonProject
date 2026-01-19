package com.mypokemon.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract class representing an NPC.
 * Follows Open/Close Principle (OCP) as new NPCs can be created by extending
 * this class.
 * Implements Single Responsibility Principle (SRP) by handling only NPC state
 * and rendering.
 */
public abstract class NPC implements Interactable {
    protected Texture sprite;
    protected Texture portrait;
    protected float x, y;
    protected float width, height;
    protected String name;
    protected String[] dialogPages;
    protected float interactionDistance = 45f; // Reducido de 30 a 20 para permitir acercarse más de frente

    public NPC(float x, float y, float width, float height, String texturePath, String name, String[] dialogPages) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.dialogPages = dialogPages;

        try {
            if (texturePath != null) {
                this.sprite = new Texture(Gdx.files.internal(texturePath));
            }
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error loading texture: " + texturePath);
        }
    }

    public void setPortrait(String portraitPath) {
        try {
            this.portrait = new Texture(Gdx.files.internal(portraitPath));
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error loading portrait: " + portraitPath);
        }
    }

    public Texture getPortrait() {
        return portrait != null ? portrait : sprite;
    }

    public String getName() {
        return name;
    }

    public String[] getDialog() {
        return dialogPages;
    }

    @Override
    public boolean isClose(float playerX, float playerY) {
        return Vector2.dst(playerX, playerY, x, y) < interactionDistance;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (sprite != null) {
            batch.draw(sprite, x, y, width, height);
        }
    }

    @Override
    public void dispose() {
        if (sprite != null)
            sprite.dispose();
        if (portrait != null)
            portrait.dispose();
    }

    // Getters for position/size if needed for collision distinct from interaction
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean overlaps(float ox, float oy, float owidth, float oheight) {
        return x < ox + owidth && x + width > ox && y < oy + oheight && y + height > oy;
    }
}
