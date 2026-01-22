package com.mypokemon.game.colisiones;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.PokemonPrincipal;
import com.mypokemon.game.pantallas.GameScreen;
import com.mypokemon.game.pantallas.LaboratorioScreen;

/**
 * Colisión específica para la puerta del laboratorio.
 */
public class ColisionPuertaLaboratorio extends ZonaInteractiva {

    private Texture texturaLetrero;
    private float letreroX, letreroY;
    private Object juego; // Can be either PokemonMain or PokemonPrincipal
    private Object pantallaJuego; // Can be either GameScreen or PantallaJuego
    private Explorador explorador;

    // Constructor for PokemonPrincipal + GameScreen (used by GameScreen)
    public ColisionPuertaLaboratorio(float x, float y, Texture letrero,
            com.mypokemon.game.PokemonPrincipal juego, GameScreen pantallaJuego,
            Explorador explorador) {
        // Sin colisión física, solo interacción cercana
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO"; // Cambiado de ZONA para quitar colisión física
        this.rangoInteraccion = 45f; // Reducido de 35 a 25 para interacción muy cercana
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;
        // Ajustado para letrero más pequeño
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    // Constructor for PokemonMain + GameScreen (used by GameScreen with
    // PokemonMain)
    public ColisionPuertaLaboratorio(float x, float y, Texture letrero,
            com.mypokemon.game.PokemonMain juego, GameScreen pantallaJuego,
            Explorador explorador) {
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO";
        this.rangoInteraccion = 45f;
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    // Constructor for PokemonPrincipal + PantallaJuego (used by PantallaJuego)
    public ColisionPuertaLaboratorio(float x, float y, Texture letrero,
            com.mypokemon.game.PokemonPrincipal juego,
            com.mypokemon.game.pantallas.PantallaJuego pantallaJuego,
            Explorador explorador) {
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO";
        this.rangoInteraccion = 45f;
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    @Override
    public void interactuar() {
        if (explorador.getEquipo().isEmpty()) {
            // Iniciar fade out y cambiar a LaboratorioScreen
            if (juego instanceof com.mypokemon.game.PokemonMain && pantallaJuego instanceof GameScreen) {
                ((GameScreen) pantallaJuego).iniciarFadeOut(
                        new LaboratorioScreen((com.mypokemon.game.PokemonMain) juego, (GameScreen) pantallaJuego));
            } else if (juego instanceof com.mypokemon.game.PokemonPrincipal && pantallaJuego instanceof GameScreen) {
                ((GameScreen) pantallaJuego).iniciarFadeOut(
                        new LaboratorioScreen((com.mypokemon.game.PokemonMain) juego, (GameScreen) pantallaJuego));
            }
        } else {
            if (pantallaJuego instanceof GameScreen) {
                ((GameScreen) pantallaJuego).mostrarNotificacion(
                        "La elección es permanente. No puedes volver a entrar.");
            }
        }
    }

    /**
     * Renderiza el letrero del laboratorio con tamaño reducido.
     */
    public void renderizarLetrero(SpriteBatch batch) {
        if (texturaLetrero != null) {
            // Dibuja el letrero muy pequeño: 120x60 píxeles
            batch.draw(texturaLetrero, letreroX, letreroY, 60, 60);
        }
    }
}
