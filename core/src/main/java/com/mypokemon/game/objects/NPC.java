package com.mypokemon.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Clase abstracta que representa a un NPC (Personaje No Jugable).
 * Sigue el Principio Abierto/Cerrado (OCP), permitiendo crear nuevos NPCs
 * extendiendo esta clase.
 * Implementa el Principio de Responsabilidad Única (SRP) manejando solo su
 * estado y renderizado.
 */
public abstract class NPC implements Interactable {
    protected Texture sprite;
    protected Texture portrait;
    protected float x, y;
    protected float width, height;
    protected String name;
    protected String[] dialogPages;
    protected float interactionDistance = 45f; // Reducido de 30 a 20 para permitir acercarse más de frente

    /**
     * Constructor para inicializar un NPC.
     * 
     * @param x           Coordenada X inicial.
     * @param y           Coordenada Y inicial.
     * @param width       Ancho del NPC.
     * @param height      Alto del NPC.
     * @param texturePath Ruta de la textura del sprite.
     * @param name        Nombre del NPC.
     * @param dialogPages Páginas de diálogo del NPC.
     */
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

    /**
     * Establece el retrato del NPC para los diálogos.
     * 
     * @param portraitPath Ruta de la imagen del retrato.
     */
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

    /**
     * Verifica si el jugador está lo suficientemente cerca para interactuar.
     * 
     * @param playerX Posición X del jugador.
     * @param playerY Posición Y del jugador.
     * @return true si está dentro de la distancia de interacción.
     */
    @Override
    public boolean isClose(float playerX, float playerY) {
        return Vector2.dst(playerX, playerY, x, y) < interactionDistance;
    }

    /**
     * Renderiza el sprite del NPC.
     * 
     * @param batch SpriteBatch utilizado para dibujar.
     */
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
        if (music != null)
            music.dispose();
    }

    // Music Management
    protected com.badlogic.gdx.audio.Music music;

    /**
     * Carga y configura música asociada al NPC.
     * 
     * @param musicPath Ruta del archivo de música.
     */
    public void loadMusic(String musicPath) {
        try {
            if (Gdx.files.internal(musicPath).exists()) {
                this.music = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
                this.music.setLooping(true);
            }
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error loading music: " + musicPath, e);
        }
    }

    public com.badlogic.gdx.audio.Music getMusic() {
        return music;
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
