package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

// Pantalla que muestra información "Acerca de" del juego.
public class AcercaDeScreen extends StaticDisplayScreen {

    public AcercaDeScreen(final PokemonMain juego) {
        super(juego, new MainMenuScreen(juego), "pantallaAcercaDe.png");
    }
}
