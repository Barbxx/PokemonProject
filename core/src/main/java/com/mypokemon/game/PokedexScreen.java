package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.utils.BaseScreen;
import java.util.ArrayList;
import java.util.List;

public class PokedexScreen extends BaseScreen {
    private final com.badlogic.gdx.Screen returnScreen;
    private Texture background;
    private Texture entryBg;
    private Texture whitePixel;
    private BitmapFont fontTitle;
    private BitmapFont fontText;
    private BitmapFont fontStats;

    private List<String> capturedNames;
    private int selectedIndex = 0;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    public PokedexScreen(PokemonMain game, com.badlogic.gdx.Screen returnScreen, Explorador explorador) {
        super(game);
        this.returnScreen = returnScreen;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        capturedNames = new ArrayList<>();
        Pokedex registro = explorador.getRegistro();
        // Get all captured pokemon from the database filtered by the registration
        for (String name : BasePokemonData.getNombres()) {
            EspeciePokemon especie = registro.getRegistro().get(name);
            if (especie != null && especie.isCapturado()) {
                capturedNames.add(name);
            }
        }

        // Font Setup
        fontTitle = new BitmapFont();
        fontTitle.getData().setScale(2.5f);
        fontText = new BitmapFont();
        fontText.getData().setScale(1.5f);
        fontStats = new BitmapFont();
        fontStats.getData().setScale(1.2f);

        // Textures
        try {
            background = new Texture(Gdx.files.internal("fondoPokedex.png"));
        } catch (Exception e) {
        }

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        entryBg = new Texture(pixmap);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.setScreen(returnScreen);
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex--;
            if (selectedIndex < 0)
                selectedIndex = Math.max(0, capturedNames.size() - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex++;
            if (selectedIndex >= capturedNames.size())
                selectedIndex = 0;
        }

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // Overlay darkening
        game.batch.setColor(0, 0, 0, 0.5f);
        game.batch.draw(whitePixel, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        game.batch.setColor(Color.WHITE);

        // Sidebar with Names
        float sidebarW = 400;
        game.batch.draw(entryBg, 20, 20, sidebarW, VIRTUAL_HEIGHT - 40);

        fontTitle.setColor(Color.YELLOW);
        fontTitle.draw(game.batch, "POKÉDEX", 50, VIRTUAL_HEIGHT - 60);

        if (capturedNames.isEmpty()) {
            fontText.setColor(Color.LIGHT_GRAY);
            fontText.draw(game.batch, "Sin registros...", 50, VIRTUAL_HEIGHT - 150);
        } else {
            float listY = VIRTUAL_HEIGHT - 150;
            for (int i = 0; i < capturedNames.size(); i++) {
                if (i == selectedIndex) {
                    game.batch.setColor(Color.CYAN);
                    game.batch.draw(whitePixel, 30, listY - 35, sidebarW - 20, 50);
                    game.batch.setColor(Color.WHITE);
                    fontText.setColor(Color.BLACK);
                } else {
                    fontText.setColor(Color.WHITE);
                }
                fontText.draw(game.batch, capturedNames.get(i), 50, listY);
                listY -= 60;
                if (listY < 50)
                    break; // Pagination would be better, but for 20 slots it's okay
            }
        }

        // Details Panel
        if (!capturedNames.isEmpty() && selectedIndex < capturedNames.size()) {
            String name = capturedNames.get(selectedIndex);
            BasePokemonData data = BasePokemonData.get(name);

            float detailX = sidebarW + 50;
            float detailW = VIRTUAL_WIDTH - sidebarW - 70;

            // Draw Card Background
            game.batch.draw(entryBg, detailX, 20, detailW, VIRTUAL_HEIGHT - 40);

            // Name and Type
            fontTitle.setColor(Color.WHITE);
            fontTitle.draw(game.batch, name.toUpperCase(), detailX + 30, VIRTUAL_HEIGHT - 80);

            fontText.setColor(Color.CYAN);
            fontText.draw(game.batch, "TIPO: " + (data != null ? data.tipo : "Desconocido"), detailX + 30,
                    VIRTUAL_HEIGHT - 140);

            // Description
            fontText.setColor(Color.WHITE);
            if (data != null && data.descripcion != null) {
                // Manual wrap or use layout
                fontText.draw(game.batch, data.descripcion, detailX + 30, VIRTUAL_HEIGHT - 200, detailW - 60,
                        com.badlogic.gdx.utils.Align.left, true);
            }

            // Stats
            if (data != null) {
                float statsY = 220;
                fontStats.setColor(Color.ORANGE);
                fontStats.draw(game.batch, "HP: " + (int) data.getHpBase(), detailX + 30, statsY);
                fontStats.draw(game.batch, "ATQ: " + (int) data.getAtqBase(), detailX + 180, statsY);
                fontStats.draw(game.batch, "VEL: " + (int) data.getVelBase(), detailX + 330, statsY);
            }

            // Image Placeholder/Logic
            // User requested images. We should load them based on name.
            try {
                String imgPath = name.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-")
                        + ".png";
                if (Gdx.files.internal(imgPath).exists()) {
                    Texture tex = new Texture(Gdx.files.internal(imgPath));
                    game.batch.draw(tex, detailX + (detailW - 250) / 2, 250, 250, 250);
                    // Note: In a real app we'd cache these textures
                }
            } catch (Exception e) {
            }
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (entryBg != null)
            entryBg.dispose();
        if (whitePixel != null)
            whitePixel.dispose();
        fontTitle.dispose();
        fontText.dispose();
        fontStats.dispose();
    }
}
