package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;

// Contenedor simple para los datos visuales y descriptivos de un ítem del juego.
public class DatosItem {
    public String nombre;
    public String descripcion;
    public Texture textura;

    public DatosItem(String nombre, String descripcion, Texture textura) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textura = textura;
    }
}
