package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;

public class ItemData {
    public String nombre;
    public String descripcion;
    public Texture textura;

    public ItemData(String nombre, String descripcion, Texture textura) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textura = textura;
    }
}
