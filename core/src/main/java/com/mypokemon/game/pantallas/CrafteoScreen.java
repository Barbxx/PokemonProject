package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.inventario.exceptions.EspacioException;
import com.mypokemon.game.inventario.ItemFactory;
import com.mypokemon.game.inventario.Crafteo;
import com.mypokemon.game.inventario.Receta;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class CrafteoScreen extends BaseScreen {

    private Texture background;
    private GameScreen returnScreen;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Virtual resolution
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    // Buttons
    private Texture texBtnGris, texBtnGrisSel;

    // Logic
    private Crafteo crafteoLogic;
    private List<Receta> recetas;

    // Item Textures
    private Texture texPlanta, texGuijarro, texBaya;
    // Renaming legacy texRepelente->texReproductor, texAmuleto->texGuante,
    // texCebo->texFrijol
    private Texture texPokebola, texPokebolaPeso, texPocionHerbal, texElixir, texRevivir, texReproductor, texGuante,
            texFrijol;
    private Texture textureFondoSlot;
    private Texture textureWhite;

    // Selected Recipe Index
    private int selectedRecipeIndex = -1;

    // Crafting Feedback
    private String craftingMessage = "";
    private float craftingMessageTimer = 0;

    public CrafteoScreen(PokemonMain game, GameScreen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        // Usar instancia centralizada de Explorador
        this.crafteoLogic = returnScreen.getExplorador().getCrafteoSystem();

        loadAssets();
        initRecipes();
    }

    private void loadAssets() {
        try {
            background = new Texture(Gdx.files.internal("fondoCrafteo.png"));

            // Load Grey Button
            texBtnGris = new Texture(Gdx.files.internal("botonCrafteoGris.png"));
            texBtnGrisSel = new Texture(Gdx.files.internal("botonCrafteoGris_seleccionado.png"));

            // Load Item Textures
            try {
                texPlanta = new Texture(Gdx.files.internal("planta.png"));
                texGuijarro = new Texture(Gdx.files.internal("guijarro.png"));
                texBaya = new Texture(Gdx.files.internal("baya.png"));

                // Load requested items
                texPokebola = new Texture(Gdx.files.internal("pokeball.png"));
                texPokebolaPeso = new Texture(Gdx.files.internal("pokeballPeso.png"));
                texPocionHerbal = new Texture(Gdx.files.internal("pocionHerbal.png"));
                texElixir = new Texture(Gdx.files.internal("elixirPielPiedra.png"));
                texRevivir = new Texture(Gdx.files.internal("revivirCasero.png"));

                // New standard naming
                texReproductor = new Texture(Gdx.files.internal("reproductor.png"));
                texGuante = new Texture(Gdx.files.internal("guanteReflejo.png"));
                texFrijol = new Texture(Gdx.files.internal("frijolMagico.png"));

            } catch (Exception e) {
                Gdx.app.error("Crafteo", "Error loading item textures", e);
            }

            // Create visible slot background (Lighter)
            Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
            pixmap.setColor(0.0f, 0.0f, 0.0f, 0.4f);
            pixmap.fill();
            textureFondoSlot = new Texture(pixmap);

            // Create White Pixel for Borders
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            textureWhite = new Texture(pixmap);
            pixmap.dispose();

        } catch (Exception e) {
            Gdx.app.error("CrafteoScreen", "Asset Error: " + e.getMessage());
        }
    }

    private void initRecipes() {
        // Load recipes specifically from the Crafteo logic
        this.recetas = crafteoLogic.obtenerTodasLasRecetas();
    }

    // Helper to get texture by ID result
    private Texture getTextureForId(String id) {
        switch (id.toLowerCase()) {
            case "pokeball":
                return texPokebola;
            case "heavyball":
                return texPokebolaPeso;
            case "pocion":
                return texPocionHerbal;
            case "elixir":
                return texElixir;
            case "revivir":
                return texRevivir;
            case "reproductor":
                return texReproductor;
            case "guante":
                return texGuante;
            case "frijol":
                return texFrijol;
            default:
                return null;
        }
    }

    // Helper to extract description using ItemFactory
    private String getDescriptionForId(String id) {
        return ItemFactory.crearCrafteado(id, 1).getDescripcion();
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(0, 0, 0, 1);

        if (craftingMessageTimer > 0) {
            craftingMessageTimer -= delta;
            if (craftingMessageTimer < 0)
                craftingMessageTimer = 0;
        }

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        drawGrid();
        drawGrisButton();
        drawDetailsPanel();

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(returnScreen);
            return;
        }

        // Mouse Input
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(mousePos);

            // Check Recipe Selection - GRID LOGIC
            float gridX = 100;
            float gridY = VIRTUAL_HEIGHT - 350;
            float slotSize = 120; // Match drawGrid
            float gap = 20;
            int cols = 4;

            for (int i = 0; i < recetas.size(); i++) {
                float x = gridX + (i % cols) * (slotSize + gap);
                float y = gridY - (i / cols) * (slotSize + gap);

                if (mousePos.x >= x && mousePos.x <= x + slotSize &&
                        mousePos.y >= y && mousePos.y <= y + slotSize) {
                    selectedRecipeIndex = i;
                }
            }

            // Check Button Click
            float btnW = 320;
            float btnH = 120;
            float btnX = 100;
            float btnY = 60;

            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW &&
                    mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                if (selectedRecipeIndex != -1) {
                    attemptCrafting(recetas.get(selectedRecipeIndex));
                } else {
                    craftingMessage = "¡Selecciona una receta primero!";
                    craftingMessageTimer = 3;
                }
            }
        }
    }

    private void attemptCrafting(Receta r) {
        try {
            crafteoLogic.craftear(r.getIdResultado(), returnScreen.getExplorador().getMochila());
            craftingMessage = "¡Crafteaste con éxito!";
        } catch (EspacioException e) {
            craftingMessage = "¡Inventario lleno!";
        } catch (IllegalArgumentException e) {
            craftingMessage = "¡No tienes los materiales!";
        } catch (Exception e) {
            craftingMessage = "Error inesperado: " + e.getMessage();
            Gdx.app.error("Crafteo", "Error al craftear", e);
        }
        craftingMessageTimer = 3;
    }

    private void drawGrisButton() {
        float btnW = 320;
        float btnH = 120;
        float x = 200; // Bottom Left
        float y = 60;

        Texture tex = texBtnGris;

        // Hover Check
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        if (mousePos.x >= x && mousePos.x <= x + btnW &&
                mousePos.y >= y && mousePos.y <= y + btnH) {
            tex = texBtnGrisSel;
        }

        if (tex != null) {
            game.batch.draw(tex, x, y, btnW, btnH);
        }
    }

    private void drawGrid() {
        float gridX = 100;
        float gridY = VIRTUAL_HEIGHT - 350;
        float slotSize = 120;
        float gap = 20;
        int cols = 4;

        game.font.getData().setScale(1.0f);

        for (int i = 0; i < recetas.size(); i++) {
            Receta r = recetas.get(i);
            float x = gridX + (i % cols) * (slotSize + gap);
            float y = gridY - (i / cols) * (slotSize + gap);

            // Draw Slot Background
            if (textureFondoSlot != null) {
                game.batch.setColor(Color.WHITE);
                game.batch.draw(textureFondoSlot, x, y, slotSize, slotSize);
            }

            // Draw Icon
            Texture icon = getTextureForId(r.getIdResultado());
            if (icon != null) {
                float iconSize = slotSize * 0.7f;
                float iconX = x + (slotSize - iconSize) / 2;
                float iconY = y + (slotSize - iconSize) / 2;
                game.batch.draw(icon, iconX, iconY, iconSize, iconSize);
            }

            // Selection Highlight
            if (i == selectedRecipeIndex) {
                float borderThickness = 3;
                game.batch.setColor(Color.YELLOW);
                if (textureWhite != null) {
                    game.batch.draw(textureWhite, x - borderThickness, y + slotSize, slotSize + 2 * borderThickness,
                            borderThickness);
                    game.batch.draw(textureWhite, x - borderThickness, y - borderThickness,
                            slotSize + 2 * borderThickness, borderThickness);
                    game.batch.draw(textureWhite, x - borderThickness, y - borderThickness, borderThickness,
                            slotSize + 2 * borderThickness);
                    game.batch.draw(textureWhite, x + slotSize, y - borderThickness, borderThickness,
                            slotSize + 2 * borderThickness);
                }
                game.batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawDetailsPanel() {
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < recetas.size()) {
            Receta selected = recetas.get(selectedRecipeIndex);

            float detailsX = 700;
            float detailsY = VIRTUAL_HEIGHT - 300;

            // DRAW LARGE IMAGE
            Texture icon = getTextureForId(selected.getIdResultado());
            if (icon != null) {
                float largeSize = 130;
                float largeX = detailsX + 200;
                float largeY = detailsY - 20;
                game.batch.draw(icon, largeX, largeY, largeSize, largeSize);
            }

            // Title and Desc
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, selected.getNombreResultado(), detailsX + 90, detailsY - 40);

            game.font.setColor(Color.LIGHT_GRAY);
            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, getDescriptionForId(selected.getIdResultado()), detailsX + 90, detailsY - 80);

            // Ingredients List
            float ingY = detailsY - 200;

            // Draw ingredients explicitly via helper to avoid Map
            if (selected.reqPlantas > 0) {
                drawIndidualIngredient("planta", selected.reqPlantas, detailsX, ingY);
                ingY -= 50;
            }
            if (selected.reqGuijarros > 0) {
                drawIndidualIngredient("guijarro", selected.reqGuijarros, detailsX, ingY);
                ingY -= 50;
            }
            if (selected.reqBayas > 0) {
                drawIndidualIngredient("baya", selected.reqBayas, detailsX, ingY);
                ingY -= 50;
            }

            game.font.setColor(Color.WHITE);
        }

        // Draw Crafting Feedback Message
        if (craftingMessageTimer > 0) {
            game.font.getData().setScale(1.2f);
            if (craftingMessage.contains("éxito"))
                game.font.setColor(Color.GREEN);
            else
                game.font.setColor(Color.RED);

            game.font.draw(game.batch, craftingMessage, 100, 200);
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1.0f);
        }
    }

    private void drawIndidualIngredient(String nombreIng, int cantidadReq, float detailsX, float ingY) {
        // Get real owned amount
        int owned = returnScreen.getExplorador().getMochila().getCantidad(nombreIng);

        // Render Icon
        Texture ingIcon = null;
        if (nombreIng.equalsIgnoreCase("planta"))
            ingIcon = texPlanta;
        else if (nombreIng.equalsIgnoreCase("guijarro"))
            ingIcon = texGuijarro;
        else if (nombreIng.equalsIgnoreCase("baya"))
            ingIcon = texBaya;

        if (ingIcon != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(ingIcon, detailsX + 100, ingY - 30, 30, 30);
        }

        // Name (Capitalized for display)
        String displayName = nombreIng.substring(0, 1).toUpperCase() + nombreIng.substring(1);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, displayName, detailsX + 150, ingY - 10);

        // Count (Owned / Required)
        if (owned >= cantidadReq)
            game.font.setColor(Color.GREEN);
        else
            game.font.setColor(Color.RED);

        game.font.draw(game.batch, owned + "/" + cantidadReq, detailsX + 390, ingY - 10);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
        if (texBtnGris != null)
            texBtnGris.dispose();
        if (texBtnGrisSel != null)
            texBtnGrisSel.dispose();
        if (textureFondoSlot != null)
            textureFondoSlot.dispose();
        if (textureWhite != null)
            textureWhite.dispose();

        if (texPlanta != null)
            texPlanta.dispose();
        if (texGuijarro != null)
            texGuijarro.dispose();
        if (texBaya != null)
            texBaya.dispose();

        if (texPokebola != null)
            texPokebola.dispose();
        if (texPokebolaPeso != null)
            texPokebolaPeso.dispose();
        if (texPocionHerbal != null)
            texPocionHerbal.dispose();
        if (texElixir != null)
            texElixir.dispose();
        if (texRevivir != null)
            texRevivir.dispose();

        // Dispose renamed
        if (texReproductor != null)
            texReproductor.dispose();
        if (texGuante != null)
            texGuante.dispose();
        if (texFrijol != null)
            texFrijol.dispose();
    }
}
