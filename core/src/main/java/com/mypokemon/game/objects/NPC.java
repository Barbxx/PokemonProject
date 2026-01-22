package com.mypokemon.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// Clase abstracta que representa un NPC. Sigue los principios SRP y OCP.
public abstract class NPC implements GameEntidad {
    protected Texture sprite;
    protected Texture retrato;
    protected float posX, posY;
    protected float ancho, alto;
    protected String nombre;
    protected String[] paginasDialogo;
    protected float distanciaInteraccion = 45f;

    public NPC(float x, float y, float ancho, float alto, String rutaTextura, String nombre, String[] paginasDialogo) {
        this.posX = x;
        this.posY = y;
        this.ancho = ancho;
        this.alto = alto;
        this.nombre = nombre;
        this.paginasDialogo = paginasDialogo;

        try {
            if (rutaTextura != null) {
                this.sprite = new Texture(Gdx.files.internal(rutaTextura));
            }
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error cargando textura: " + rutaTextura);
        }
    }

    public void setRetrato(String rutaRetrato) {
        try {
            this.retrato = new Texture(Gdx.files.internal(rutaRetrato));
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error cargando retrato: " + rutaRetrato);
        }
    }

    public Texture getRetrato() {
        return retrato != null ? retrato : sprite;
    }

    public String getNombre() {
        return nombre;
    }

    public String[] getDialogo() {
        return paginasDialogo;
    }

    @Override
    public boolean estaCerca(float playerX, float playerY) {
        return Vector2.dst(playerX, playerY, posX, posY) < distanciaInteraccion;
    }

    @Override
    public void renderizar(SpriteBatch batch) {
        if (sprite != null) {
            batch.draw(sprite, posX, posY, ancho, alto);
        }
    }

    @Override
    public void liberar() {
        if (sprite != null)
            sprite.dispose();
        if (retrato != null)
            retrato.dispose();
        if (musica != null)
            musica.dispose();
    }

    // Gestión de Música
    protected com.badlogic.gdx.audio.Music musica;

    public void cargarMusica(String rutaMusica) {
        try {
            if (Gdx.files.internal(rutaMusica).exists()) {
                this.musica = Gdx.audio.newMusic(Gdx.files.internal(rutaMusica));
                this.musica.setLooping(true);
            }
        } catch (Exception e) {
            Gdx.app.log("NPC", "Error cargando música: " + rutaMusica, e);
        }
    }

    public com.badlogic.gdx.audio.Music getMusica() {
        return musica;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public boolean superpone(float ox, float oy, float oancho, float oalto) {
        return posX < ox + oancho && posX + ancho > ox && posY < oy + oalto && posY + alto > oy;
    }
}
