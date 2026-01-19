package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.BasePokemonData;
import com.mypokemon.game.EspeciePokemon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokedexScreen extends NavigableScreen {
    private Texture background;
    private Texture entryBg;
    private Texture whitePixel;
    private BitmapFont fontTitle;
    private BitmapFont fontText;
    private BitmapFont fontStats;

    private List<String> capturedNames;
    private Map<String, Texture> textureCache = new HashMap<>();
    private int selectedIndex = 0;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private final Explorador explorador;
    private int selectedGridX = 0;
    private int selectedGridY = 0;
    private final int GRID_COLS = 6;
    private final int GRID_ROWS = 4; // Changed to 4 rows

    public PokedexScreen(PokemonMain game, Screen returnScreen, Explorador explorador) {
        super(game, returnScreen);
        this.explorador = explorador;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        capturedNames = explorador.getRegistro().getCapturedOrder();

        // Font Setup
        fontTitle = new BitmapFont();
        fontTitle.getData().setScale(2.5f);
        fontText = new BitmapFont();
        fontText.getData().setScale(1.5f);
        fontStats = new BitmapFont();
        fontStats.getData().setScale(1.2f);

        // Textures
        background = loadTexture("fondoPokedex.png");

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
            navigateBack();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedGridY--;
            if (selectedGridY < 0)
                selectedGridY = GRID_ROWS - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedGridY++;
            if (selectedGridY >= GRID_ROWS)
                selectedGridY = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedGridX--;
            if (selectedGridX < 0)
                selectedGridX = GRID_COLS - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedGridX++;
            if (selectedGridX >= GRID_COLS)
                selectedGridX = 0;
        }

        selectedIndex = selectedGridY * GRID_COLS + selectedGridX;

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // Overlay darkening REMOVED
        game.batch.setColor(Color.WHITE);

        // Sin registros text position
        if (capturedNames.isEmpty()) {
            fontText.setColor(Color.LIGHT_GRAY);
            fontText.draw(game.batch, "Sin registros...", 80, VIRTUAL_HEIGHT - 500);
        }

        // Info Panel (Left side - Lowered to match background "INFO PKMN" box)
        float infoX = 70;
        float infoY = 50;
        float infoW = 340;
        float infoH = 190;

        // fontTitle.setColor(Color.YELLOW);
        // fontTitle.getData().setScale(1.8f);
        // fontTitle.draw(game.batch, "POKÉDEX", 60, VIRTUAL_HEIGHT - 60);

        String currentPokemonName = (selectedIndex < capturedNames.size()) ? capturedNames.get(selectedIndex) : null;

        if (currentPokemonName != null) {
            BasePokemonData data = BasePokemonData.get(currentPokemonName);
            EspeciePokemon registro = explorador.getRegistro().getRegistro().get(currentPokemonName);

            // Load/Get Texture for Big Display
            String pName = currentPokemonName;
            if (!textureCache.containsKey(pName)) {
                try {
                    String nameClean = pName.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                    textureCache.put(pName, new Texture(Gdx.files.internal(nameClean + ".png")));
                } catch (Exception e) {
                }
            }
            Texture bigTex = textureCache.get(pName);
            if (bigTex != null) {
                float bigSize = 450;
                float bigX = infoX + (infoW - bigSize) / 2;
                float bigY = infoY + infoH - 130; // Position above the text box
                game.batch.draw(bigTex, bigX, bigY, bigSize, bigSize);
            }

            fontTitle.setColor(Color.WHITE);
            fontTitle.getData().setScale(1.3f);
            fontTitle.draw(game.batch, currentPokemonName.toUpperCase(), infoX + 20, infoY + infoH - 30);

            fontText.setColor(Color.CYAN);
            fontText.getData().setScale(1.5f);
            fontText.draw(game.batch, "TIPO: " + (data != null ? data.tipo : "???"), infoX + 20, infoY + infoH - 70);

            fontText.setColor(Color.ORANGE);
            int nivelInv = (registro != null ? registro.getNivelInvestigacion() : 0);
            fontText.draw(game.batch, "NIVEL INV: " + nivelInv + "/10",
                    infoX + 20, infoY + infoH - 90);

            // Display Stats
            if (data != null) {
                int ps = data.calcularPS(nivelInv);
                int atq = data.calcularAtaque(nivelInv);
                int vel = data.calcularVelocidad(nivelInv);

                fontStats.setColor(Color.GREEN);
                fontStats.draw(game.batch, "PS: " + ps + " | ATQ: " + atq + " | VEL: " + vel,
                        infoX + 20, infoY + infoH - 115);
            }

            fontText.setColor(Color.WHITE);
            fontText.getData().setScale(0.9f);
            if (data != null && data.descripcion != null) {
                fontText.draw(game.batch, data.descripcion, infoX + 20, infoY + infoH - 145, infoW - 40,
                        com.badlogic.gdx.utils.Align.left, true);
            }
        }

        // Grid (Right side - Aligned with image boxes)
        float gridStartX = 530;
        float gridStartY = 420; // Adjusted for 4 rows
        float boxSize = 100; // Decreased size to fit 4 rows
        float spacing = 15;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int index = row * GRID_COLS + col;
                float bx = gridStartX + col * (boxSize + spacing);
                float by = gridStartY - row * (boxSize + spacing);

                // Draw Grid Box Background (Darker than background)
                game.batch.setColor(0, 0, 0, 0.4f); // Semi-transparent black
                game.batch.draw(whitePixel, bx, by, boxSize, boxSize);
                game.batch.setColor(Color.WHITE);

                // Selection Highlight
                if (row == selectedGridY && col == selectedGridX) {
                    game.batch.setColor(Color.YELLOW);
                    // Draw outer border
                    game.batch.draw(whitePixel, bx - 3, by - 3, boxSize + 6, 3);
                    game.batch.draw(whitePixel, bx - 3, by + boxSize, boxSize + 6, 3);
                    game.batch.draw(whitePixel, bx - 3, by - 3, 3, boxSize + 6);
                    game.batch.draw(whitePixel, bx + boxSize, by - 3, 3, boxSize + 6);
                    game.batch.setColor(Color.WHITE);
                }

                if (index < capturedNames.size()) {
                    String pName = capturedNames.get(index);
                    if (!textureCache.containsKey(pName)) {
                        try {
                            String nameClean = pName.toLowerCase().replace(" h.", "").replace(" jr.", "-jr")
                                    .replace(" ", "-");
                            textureCache.put(pName, new Texture(Gdx.files.internal(nameClean + ".png")));
                        } catch (Exception e) {
                        }
                    }
                    Texture tex = textureCache.get(pName);
                    if (tex != null) {
                        game.batch.draw(tex, bx + 10, by + 10, boxSize - 20, boxSize - 20);
                    }
                }
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
        super.dispose(); // Automatically disposes registered textures
        if (entryBg != null)
            entryBg.dispose();
        if (whitePixel != null)
            whitePixel.dispose();
        fontTitle.dispose();
        fontText.dispose();
        fontStats.dispose();
        for (Texture t : textureCache.values()) {
            if (t != null)
                t.dispose();
        }
    }
}
