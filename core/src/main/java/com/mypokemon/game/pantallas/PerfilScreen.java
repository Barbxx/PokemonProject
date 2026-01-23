package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mypokemon.game.BasePokemonData;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.PokemonMain;

/**
 * Pantalla que muestra el perfil del explorador Incluye nombre, género,
 * estadísticas de mochila y Pokémon capturados.
 */
public class PerfilScreen extends ScreenAdapter {

    private final PokemonMain game;
    private final BaseScreen parentScreen;
    private final Explorador explorador;
    private final boolean esChico;

    private Texture backgroundTexture;
    private BitmapFont font;
    private BitmapFont titleFont;

    /**
     * Constructor de la pantalla de perfil. Carga el fondo correspondiente al
     * género y prepara las fuentes.
     *
     * @param game Referencia al juego principal.
     * @param parentScreen Pantalla desde la que se accedió (para volver).
     * @param explorador Datos del jugador a mostrar.
     * @param esChico Indica el género del jugador para seleccionar el fondo.
     */
    public PerfilScreen(PokemonMain game, BaseScreen parentScreen, Explorador explorador, boolean esChico) {
        this.game = game;
        this.parentScreen = parentScreen;
        this.explorador = explorador;
        this.esChico = esChico;

        this.font = new BitmapFont();
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.0f);

        // Cargar fondo según género
        String bgName = esChico ? "fondoPerfilChico.png" : "fondoPerfilChica.png";
        try {
            if (Gdx.files.internal(bgName).exists()) {
                this.backgroundTexture = new Texture(Gdx.files.internal(bgName));
            } else {
                // Fallback si no existe la imagen específica
                Gdx.app.log("PerfilScreen", "No se encontró " + bgName + ", usando fondo por defecto.");
                if (Gdx.files.internal("fondoMochila.png").exists()) {
                    this.backgroundTexture = new Texture(Gdx.files.internal("fondoMochila.png"));
                }
            }
        } catch (Exception e) {
            Gdx.app.error("PerfilScreen", "Error cargando fondo: " + e.getMessage());
        }
    }

    /**
     * Renderiza la información del perfil del jugador. Muestra texto centrado y
     * estadísticas.
     *
     * @param delta Tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            game.setScreen(parentScreen);
            dispose();
            return;
        }

        game.batch.begin();

        // Dibujar Fondo
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, 800, 480);
        }

        // Título
        titleFont.setColor(Color.GOLD);
        drawCenteredText(titleFont, "PERFIL DE ENTRENADOR", 400);

        // Información del explorador
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        float startY = 350;

        font.draw(game.batch, explorador.getNombre(), 250, startY);
        font.draw(game.batch, esChico ? "Chico" : "Chica", 250, startY - 60);

        // Info adicional
        int capacidad = explorador.getMochila().getCapacidadMaxima();
        int ocupado = explorador.getMochila().getEspacioOcupado();
        font.draw(game.batch, "Mochila: " + ocupado + "/" + capacidad, 100, startY - 150);

        int pokemonEnMochila = explorador.getEquipo().size();
        font.draw(game.batch, "Pokémon Capturados: " + pokemonEnMochila, 100, startY - 190);

        int pokemonEnPokedex = explorador.getRegistro().getEncounterOrder().size();
        int totalPokemon = BasePokemonData.getNombres().size();
        font.draw(game.batch, pokemonEnPokedex + "/" + totalPokemon, 300, startY - 235);

        game.batch.end();
    }

    /**
     * Dibuja un texto centrado horizontalmente en la posición y indicada.
     *
     * @param f Fuente a utilizar.
     * @param text Texto a dibujar.
     * @param y Posición vertical.
     */
    private void drawCenteredText(BitmapFont f, String text, float y) {
        GlyphLayout layout = new GlyphLayout(f, text);
        f.draw(game.batch, text, (800 - layout.width) / 2, y);
    }

    /**
     * Libera los recursos gráficos.
     */
    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
