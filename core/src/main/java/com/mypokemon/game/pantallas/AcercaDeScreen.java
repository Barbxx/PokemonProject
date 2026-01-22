package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que muestra información "Acerca de" del juego.
 * Extiende StaticDisplayScreen para el renderizado automático del fondo y la
 * navegación.
 */
public class AcercaDeScreen extends StaticDisplayScreen {

    public AcercaDeScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAcercaDe.png");
    }
}
