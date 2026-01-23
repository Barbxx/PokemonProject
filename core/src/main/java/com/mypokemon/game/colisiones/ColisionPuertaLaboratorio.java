package com.mypokemon.game.colisiones;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.pantallas.GameScreen;
import com.mypokemon.game.pantallas.LaboratorioScreen;

// Maneja la interacción específica con la puerta del laboratorio. Permite al jugador entrar al seleccionar esta zona.
public class ColisionPuertaLaboratorio extends ZonaInteractiva {

    private Texture texturaLetrero;
    private float letreroX, letreroY;
    private PokemonMain juego;
    private GameScreen pantallaJuego;
    private Explorador explorador;

    /**
     * Constructor de la colisión de la puerta del laboratorio.
     *
     * @param x             Posición X.
     * @param y             Posición Y.
     * @param letrero       Textura del letrero indicador.
     * @param juego         Instancia principal del juego.
     * @param pantallaJuego Pantalla de juego actual.
     * @param explorador    Jugador explorador.
     */
    public ColisionPuertaLaboratorio(float x, float y, Texture letrero,
            PokemonMain juego, GameScreen pantallaJuego,
            Explorador explorador) {
        // Interacción cercana
        this.limites = new Rectangle(x + 10, y, 60, 70);
        this.tipo = "INTERACTIVO";
        this.rangoInteraccion = 45f;
        this.mensajeInteraccion = "Presiona [T] para entrar al laboratorio.";
        this.texturaLetrero = letrero;

        //Letrero Laboratorio
        this.letreroX = x + 80;
        this.letreroY = y + 50;
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
        this.explorador = explorador;
    }

    // Ejecuta la lógica al entrar al laboratorio
    @Override
    public void interactuar() {
        if (explorador.getEquipo().isEmpty()) {
            // Iniciar fade out y cambiar a LaboratorioScreen
            pantallaJuego.iniciarFadeOut(new LaboratorioScreen(juego, pantallaJuego));
        } else {
            pantallaJuego.mostrarNotificacion(
                    "La elección es permanente. No puedes volver a entrar.");
        }
    }

    /**
     * Renderiza el letrero indicador del laboratorio.
     *
     * @param batch SpriteBatch para dibujar.
     */
    public void renderizarLetrero(SpriteBatch batch) {
        if (texturaLetrero != null) {
            // Dibuja el letrero laboratorio
            batch.draw(texturaLetrero, letreroX, letreroY, 60, 60);
        }
    }
}
