package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;

// Clase abstracta base para todas las colisiones del juego. Tiene lo básico para gestionar límites y detección de solapamiento.

public abstract class ColisionBase implements IColisionable {

    // Área rectangular que define los límites físicos de la colisión.
    protected Rectangle limites;

    // Etiqueta que identifica la categoría de la colisión (NPC, ZONA, INTERACTIVO)
    protected String tipo;

    // Devuelve el rectángulo que define el área de colisión.
    @Override
    public Rectangle obtenerLimites() {
        return limites;
    }

    // Devuelve la categoría de este objeto colisionable.
    @Override
    public String obtenerTipo() {
        return tipo;
    }

    // Utiliza el solapamiento de rectángulos para determinar si un área colisiona con este objeto.
    @Override
    public boolean verificarColision(float x, float y, float ancho, float alto) {
        Rectangle area = new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
        return limites.overlaps(area);
    }
}
