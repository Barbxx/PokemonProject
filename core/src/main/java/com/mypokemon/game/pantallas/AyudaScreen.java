package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que muestra la información de Ayuda.
 * Extiende StaticDisplayScreen para renderizado automático de fondo y
 * navegación.
 */
public class AyudaScreen extends StaticDisplayScreen {

    public AyudaScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAyuda.png");
    }
}
