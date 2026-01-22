package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonPrincipal;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.DatosBasePokemon;
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

import java.util.ArrayList;
import java.util.List;

public class PantallaPokedex extends PantallaNavegable {
    private Texture background;
    private Texture entryBg;
    private Texture whitePixel;
    private BitmapFont fontTitle;
    private BitmapFont fontText;
    private BitmapFont fontStats;

    private List<String> capturedNames;
    private List<String> pCacheNames = new ArrayList<>();
    private List<Texture> pCacheTextures = new ArrayList<>();
    private int selectedIndex = 0;
    private List<String> tCacheNames = new ArrayList<>();
    private List<Texture> tCacheTextures = new ArrayList<>();

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private final Explorador explorador;
    private int selectedGridX = 0;
    private int selectedGridY = 0;
    private final int GRID_COLS = 6;
    private final int GRID_ROWS = 4; // Changed to 4 rows

    public PantallaPokedex(PokemonPrincipal game, Screen returnScreen, Explorador explorador) {
        super(game, returnScreen);
        this.explorador = explorador;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        capturedNames = explorador.getRegistro().getEncounterOrder();

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

        // Info Panel (Left side - Lowered to match background "INFO PKMN" box)
        float infoX = 70;
        float infoY = 50;
        float infoW = 340;
        float infoH = 190;

        // fontTitle.setColor(Color.YELLOW);
        // fontTitle.getData().setScale(1.8f);
        // fontTitle.draw(game.batch, "POKƒâ€°DEX", 60, VIRTUAL_HEIGHT - 60);

        String currentPokemonName = (selectedIndex < capturedNames.size()) ? capturedNames.get(selectedIndex) : null;

        if (currentPokemonName != null) {
            DatosBasePokemon data = DatosBasePokemon.get(currentPokemonName);
            EspeciePokemon registro = explorador.getRegistro().getRegistro().get(currentPokemonName);

            Texture bigTex = getPokemonTexture(currentPokemonName);
            if (bigTex != null) {
                float bigSize = 250; // Centered in the dotted area
                float bigX = infoX + (infoW - bigSize) / 2;
                float bigY = infoY + infoH - 30; // Lowered further to center in the dotted area
                game.batch.draw(bigTex, bigX, bigY, bigSize, bigSize);
            }

            // Draw Types (Stacked vertically on the right)
            if (data != null && data.tipo != null) {
                String[] types = data.tipo.split(" / ");
                float typeX = infoX + 230; // Further right so it doesn't overlap text
                float typeY = infoY + infoH - 35; // Start aligned with name
                float typeW = 85;
                float typeH = 32;

                for (String t : types) {
                    String typeName = t.trim();
                    Texture tTex = null;
                    for (int i = 0; i < tCacheNames.size(); i++) {
                        if (tCacheNames.get(i).equals(typeName)) {
                            tTex = tCacheTextures.get(i);
                            break;
                        }
                    }

                    if (tTex == null) {
                        try {
                            String typePath = "Tipo" + typeName + ".png";
                            if (Gdx.files.internal(typePath).exists()) {
                                tTex = new Texture(Gdx.files.internal(typePath));
                                tCacheNames.add(typeName);
                                tCacheTextures.add(tTex);
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (tTex != null) {
                        game.batch.draw(tTex, typeX, typeY, typeW, typeH);
                        typeY -= typeH + 5; // One below the other
                    }
                }
            }

            fontTitle.setColor(Color.WHITE);
            fontTitle.getData().setScale(1.2f); // Slightly smaller to fit
            fontTitle.draw(game.batch, currentPokemonName.toUpperCase(), infoX + 20, infoY + infoH - 30);

            // Region determination
            String region = "OneFerxxo";

            fontText.setColor(Color.CYAN);
            fontText.getData().setScale(1.3f);

            fontText.draw(game.batch, "REGIƒâ€œN: " + region, infoX + 20, infoY + infoH - 70);

            fontText.setColor(Color.ORANGE);
            int nivelInv = (registro != null ? registro.getNivelInvestigacion() : 0);
            fontText.draw(game.batch, "NIVEL INV: " + nivelInv + "/10",
                    infoX + 20, infoY + infoH - 95);

            // Stats removed as requested

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
                    Texture tex = getPokemonTexture(pName);

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
        for (Texture t : pCacheTextures) {
            if (t != null)
                t.dispose();
        }
        for (Texture t : tCacheTextures) {
            if (t != null)
                t.dispose();
        }
    }

    private Texture getPokemonTexture(String pName) {
        if (pName == null)
            return null;

        // Buscar en el cache
        for (int i = 0; i < pCacheNames.size(); i++) {
            if (pCacheNames.get(i).equals(pName)) {
                return pCacheTextures.get(i);
            }
        }

        // Si no esta, lo cargamos
        try {
            String nameClean = pName.toLowerCase()
                    .replace(" h.", "")
                    .replace(" jr.", "-jr")
                    .replace(" ", "-")
                    .replace(".", "");
            String pokedexPath = nameClean + "PokedexSprite.png";
            Texture tex;
            if (Gdx.files.internal(pokedexPath).exists()) {
                tex = new Texture(Gdx.files.internal(pokedexPath));
            } else {
                tex = new Texture(Gdx.files.internal(nameClean + ".png"));
            }
            pCacheNames.add(pName);
            pCacheTextures.add(tex);
            return tex;
        } catch (Exception e) {
            return null;
        }
    }
}
