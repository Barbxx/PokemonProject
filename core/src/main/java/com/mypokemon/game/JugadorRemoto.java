package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Representa a un jugador remoto en una sesión multijugador.
public class JugadorRemoto {
    public float x, y;
    public TextureRegion cuadroActual;
    public float tiempoEstado;
    public String nombre = "";

    private Animation<TextureRegion> animAbajo, animIzquierda, animDerecha, animArriba;

    // Constructor del jugador remoto.
    public JugadorRemoto(Texture hojaSpritres, int columnas, int filas) {
        int anchoCuadro = hojaSpritres.getWidth() / columnas;
        int altoCuadro = hojaSpritres.getHeight() / filas;
        TextureRegion[][] cuadros = TextureRegion.split(hojaSpritres, anchoCuadro, altoCuadro);

        if (cuadros.length >= 4) {
            animAbajo = new Animation<>(0.15f, cuadros[0]);
            animIzquierda = new Animation<>(0.15f, cuadros[1]);
            animDerecha = new Animation<>(0.15f, cuadros[2]);
            animArriba = new Animation<>(0.15f, cuadros[3]);
            cuadroActual = cuadros[0][0];
        }
    }

    // Actualiza el estado visual del jugador remoto.
    public void actualizar(float delta, float objetivoX, float objetivoY,
            com.mypokemon.game.utils.Direccion direccion) {
        this.x = objetivoX;
        this.y = objetivoY;
        tiempoEstado += delta;
        if (direccion == null)
            return;

        switch (direccion) {
            case IZQUIERDA:
                cuadroActual = animIzquierda.getKeyFrame(tiempoEstado, true);
                break;
            case DERECHA:
                cuadroActual = animDerecha.getKeyFrame(tiempoEstado, true);
                break;
            case ARRIBA:
                cuadroActual = animArriba.getKeyFrame(tiempoEstado, true);
                break;
            case ABAJO:
            default:
                cuadroActual = animAbajo.getKeyFrame(tiempoEstado, true);
                break;
        }
    }
}
