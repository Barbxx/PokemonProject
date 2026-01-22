package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Screen;

// INavegable - Interfaz para pantallas que soportan navegación entre ellas.
public interface INavigable {

    // Navega hacia atrás a la pantalla anterior.
    void navegarAtras();

    // Navega a una nueva pantalla.
    void navegarA(Screen pantalla);

    // Obtiene la pantalla a la que regresar al navegar hacia atrás.
    Screen obtenerPantallaRetorno();
}
