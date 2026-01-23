package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que muestra la información de Ayuda al jugador.
 * Permite visualizar los controles y mecánicas básicas.
 */
public class AyudaScreen extends StaticDisplayScreen {

    /**
     * Constructor de AyudaScreen.
     * 
     * @param game Instancia principal del juego.
     */
    public AyudaScreen(final PokemonMain game) {
        super(game, new MainMenuScreen(game), "pantallaAyuda.png");
    }
}
