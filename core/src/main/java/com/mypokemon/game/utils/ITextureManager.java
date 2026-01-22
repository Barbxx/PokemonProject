package com.mypokemon.game.utils;

import com.badlogic.gdx.graphics.Texture;

// Interfaz para la gestión consistente de texturas en las pantallas.
public interface ITextureManager {

    // Carga una textura desde la ruta especificada y la registra para su
    // liberación.
    Texture cargarTextura(String ruta);

    // Registra una textura para su liberación automática al cerrar la pantalla.
    void agregarTextura(Texture textura);

    // Libera todas las texturas registradas.
    void liberarTexturas();
}
