package com.mypokemon.game;

import com.badlogic.gdx.graphics.Texture;

/**
 * Contenedor simple para los datos visuales y descriptivos de un objeto del
 * juego.
 * Utilizado por el sistema de inventario y renderizado.
 */
public class DatosObjeto {
    /** Nombre visible del objeto. */
    public String nombre;
    /** Descripción detallada del objeto para la interfaz. */
    public String descripcion;
    /** Textura (icono) del objeto. */
    public Texture textura;

    /**
     * Constructor de datos de objeto.
     * 
     * @param nombre      Nombre del objeto.
     * @param descripcion Descripción del objeto.
     * @param textura     Textura cargada.
     */
    public DatosObjeto(String nombre, String descripcion, Texture textura) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textura = textura;
    }
}
