package com.mypokemon.game.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mypokemon.game.PokemonPrincipal;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.DatosBasePokemon;

public class PantallaPerfil extends ScreenAdapter {

    private final PokemonPrincipal game;
    private final PantallaBase parentScreen;
    private final Explorador explorador;
    private final boolean esChico;

    private Texture backgroundTexture;
    private BitmapFont font;

    public PantallaPerfil(PokemonPrincipal game, PantallaBase parentScreen, Explorador explorador, boolean esChico) {
        this.game = game;
        this.parentScreen = parentScreen;
        this.explorador = explorador;
        this.esChico = esChico;

        this.font = new BitmapFont();

        // Cargar fondo segun genero
        String bgName = esChico ? "fondoPerfilChico.png" : "fondoPerfilChica.png";
        try {
            if (Gdx.files.internal(bgName).exists()) {
                this.backgroundTexture = new Texture(Gdx.files.internal(bgName));
            } else {
                // Fallback si no existe la imagen especifica
                Gdx.app.log("PantallaPerfil", "No se encontro " + bgName + ", usando fondo por defecto.");
                if (Gdx.files.internal("fondoMochila.png").exists()) {
                    this.backgroundTexture = new Texture(Gdx.files.internal("fondoMochila.png"));
                }
            }
        } catch (Exception e) {
            Gdx.app.error("PantallaPerfil", "Error cargando fondo: " + e.getMessage());
        }
    }

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

        // Informacion del Entrenador
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        float startY = 350;

        font.draw(game.batch, explorador.getNombre(), 200, startY);
        font.draw(game.batch, (esChico ? "Chico" : "Chica"), 200, startY - 55);

        // Info adicional
        int capacidad = explorador.getMochila().getCapacidadMaxima();
        int ocupado = explorador.getMochila().getEspacioOcupado();
        font.draw(game.batch, "Mochila: " + ocupado + "/" + capacidad, 60, startY - 150);

        int nPokemon = explorador.getEquipo().size();
        font.draw(game.batch, "Pokemon: " + nPokemon + "/6", 60, startY - 190);

        int especiesPokedex = explorador.getRegistro().getCapturedOrder().size();
        int totalEspecies = DatosBasePokemon.getNombres().size();
        font.draw(game.batch, especiesPokedex + "/" + totalEspecies, 300, startY - 235);

        int misiones = explorador.getMisionesCompletadas();
        font.draw(game.batch, misiones + "/4", 300, startY - 285);

        game.batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
