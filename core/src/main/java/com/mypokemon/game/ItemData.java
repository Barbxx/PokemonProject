package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;

/**
 * Clase contenedora simple para los datos visuales y descriptivos de un ítem.
 */
public class ItemData {
    public String nombre;
    public String descripcion;
    public Texture textura;

    /**
     * Constructor para ItemData.
     * 
     * @param nombre      Nombre del ítem.
     * @param descripcion Descripción del ítem.
     * @param textura     Textura del ítem.
     */
    public ItemData(String nombre, String descripcion, Texture textura) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textura = textura;
    }
}
