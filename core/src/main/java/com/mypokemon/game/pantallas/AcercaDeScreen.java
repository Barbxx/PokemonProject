package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

// Pantalla que muestra información "Acerca De" del juego.

public class AcercaDeScreen extends StaticDisplayScreen {

    public AcercaDeScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAcercaDe.png");
    }
}
