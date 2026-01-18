package com.mypokemon.game;

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
import com.mypokemon.game.utils.BaseScreen;
import java.util.ArrayList;
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

    private List<RecipeDisplay> recipes;

    // Data Structure for Recipes
    // Data Structure for Recipes
    private class Ingredient {
        String name;
        int required;

        public Ingredient(String name, int required) {
            this.name = name;
            this.required = required;
        }
    }

    private class RecipeDisplay {
        String name;
        String desc;
        Texture icon; // Added icon field
        List<Ingredient> ingredients;

        public RecipeDisplay(String name, String desc, Texture icon) {
            this.name = name;
            this.desc = desc;
            this.icon = icon;
            this.ingredients = new ArrayList<>();
        }

        public void addIngredient(String name, int required) {
            ingredients.add(new Ingredient(name, required));
        }
    }

    // Item Textures
    private Texture texPlanta, texGuijarro, texBaya;
    private Texture texPokebola, texPokebolaPeso, texPocionHerbal, texElixir, texRevivir, texRepelente, texAmuleto,
            texCebo;
    private Texture textureFondoSlot; // Added for visible grid background
    private Texture textureWhite; // For borders

    // Selected Recipe Index for Middle Category
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

        loadAssets();
        initRecipes();
    }
    // ... loadAssets omitted for brevity ... (Assuming it stays same or I need to
    // preserve it carefully if I touched surrounding lines)
    // Wait, replacing lines 35-115 covers loadAssets too. I must preserve
    // loadAssets.
    // Let me target smaller chunks to avoid deleting loadAssets.

    // RE-TARGETING to just RecipeDisplay class definition first

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
                texElixir = new Texture(Gdx.files.internal("elixirEnergia.png"));
                texRevivir = new Texture(Gdx.files.internal("revivirCasero.png"));
                texRepelente = new Texture(Gdx.files.internal("repelente.png"));
                texAmuleto = new Texture(Gdx.files.internal("amuleto.png"));
                texCebo = new Texture(Gdx.files.internal("lure.png"));

            } catch (Exception e) {
                Gdx.app.error("Crafteo", "Error loading item textures", e);
            }

            // Create visible slot background (Lighter)
            Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
            pixmap.setColor(0.0f, 0.0f, 0.0f, 0.4f); // Lighter (Lower alpha)
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

    @SuppressWarnings("unchecked")
    private void initRecipes() {
        recipes = new ArrayList<>();

        // 1. Poké Ball
        RecipeDisplay r1 = new RecipeDisplay("Poké Ball", "Dispositivo para atrapar Pokémon.", texPokebola);
        r1.addIngredient("Planta", 2);
        r1.addIngredient("Guijarro", 3);
        recipes.add(r1);

        // 2. Poké Ball de Peso
        RecipeDisplay r2 = new RecipeDisplay("Poké Ball de Peso", "Dispositivo con mejor captura en nivel bajo.",
                texPokebolaPeso);
        r2.addIngredient("Guijarro", 5);
        r2.addIngredient("Planta", 1);
        recipes.add(r2);

        // 3. Poción Herbal
        RecipeDisplay r3 = new RecipeDisplay("Poción Herbal", "Cura 20 HP.", texPocionHerbal);
        r3.addIngredient("Planta", 3);
        r3.addIngredient("Baya", 1);
        recipes.add(r3);

        // 4. Elíxir de Piel de Piedra
        RecipeDisplay r4 = new RecipeDisplay("Elíxir de Piel de Piedra",
                "Aumenta la potencia del ataque (+3 por ataque).", texElixir);
        r4.addIngredient("Guijarro", 7);
        r4.addIngredient("Planta", 1);
        recipes.add(r4);

        // 5. Revivir Casero
        RecipeDisplay r5 = new RecipeDisplay("Revivir Casero", "Revive con 50% HP a un Pokémon debilitado.",
                texRevivir);
        r5.addIngredient("Planta", 5);
        r5.addIngredient("Baya", 5);
        r5.addIngredient("Guijarro", 1);
        recipes.add(r5);

        // 6. Reproductor de música
        RecipeDisplay r6 = new RecipeDisplay("Reproductor de música",
                "Permite que el jugador pueda escuchar musica de\nfondo durante la partida.",
                texRepelente);
        r6.addIngredient("Guijarro", 9);
        r6.addIngredient("Baya", 1);
        recipes.add(r6);

        // 7. Guante de reflejo cuarcítico
        RecipeDisplay r7 = new RecipeDisplay("Guante de reflejo cuarcítico",
                "Utilizan guijarros pulidos que brillan como\nespejos, su destello permite recolectar el doble\nde recursos.",
                texAmuleto);
        r7.addIngredient("Guijarro", 13);
        r7.addIngredient("Planta", 5);
        recipes.add(r7);

        // 8. Frijol mágico
        RecipeDisplay r8 = new RecipeDisplay("Frijol mágico",
                "Creado mediante energía vital y raíces de plantas,\nal ser ingerido por un pokemon se restaura el\n100% de su HP.",
                texCebo);
        r8.addIngredient("Guijarro", 20);
        r8.addIngredient("Planta", 20);
        recipes.add(r8);
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

            for (int i = 0; i < recipes.size(); i++) {
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
                    attemptCrafting(recipes.get(selectedRecipeIndex));
                } else {
                    craftingMessage = "¡Selecciona una receta primero!";
                    craftingMessageTimer = 3;
                }
            }
        }
    }

    private void attemptCrafting(RecipeDisplay r) {
        // 1. Check Ingredients
        boolean canCraft = true;
        for (Ingredient ing : r.ingredients) {
            int owned = returnScreen.getExplorador().getMochila().getCantidad(ing.name.toLowerCase());
            if (owned < ing.required) {
                canCraft = false;
                break;
            }
        }

        if (canCraft) {
            // 2. Consume Ingredients
            for (Ingredient ing : r.ingredients) {
                returnScreen.getExplorador().getMochila().consumirItem(ing.name.toLowerCase(), ing.required);
            }
            // 3. Add Result (Mapping Name to Internal ID if needed, or loosely based on
            // name)
            // Simplified: adding 1 of the item. Assuming addItem uses same naming
            // convention or we map it.
            // Using a helper or direct check.
            String itemId = r.name; // Basic mapping
            if (itemId.contains("Poké Ball"))
                itemId = "pokeball";
            else if (itemId.contains("Poción"))
                itemId = "unguento"; // Poción Herbal -> unguento logic in Mochila
            else if (itemId.contains("Elíxir"))
                itemId = "elixir";
            else if (itemId.contains("Revivir"))
                itemId = "revivir";
            else if (itemId.contains("Reproductor"))
                itemId = "repelente"; // Maps to internal repelente slot
            else if (itemId.contains("Guante"))
                itemId = "amuleto"; // Maps to internal amuleto slot
            else if (itemId.contains("Frijol"))
                itemId = "lure"; // Maps to internal lure slot
            else if (itemId.contains("Guijarro"))
                itemId = "guijarro"; // fallback

            // NOTE: Special case for Heavy Ball if needed, but 'pokeball' covers basic.
            // If internal ID needed for Heavy Ball is 'heavyball', adapt:
            if (r.name.equals("Poké Ball de Peso"))
                itemId = "heavyball";

            returnScreen.getExplorador().getMochila().agregarItem(itemId.toLowerCase(), 1);

            craftingMessage = "¡Crafteaste con éxito!";
        } else {
            craftingMessage = "¡No tienes los materiales suficientes!";
        }
        craftingMessageTimer = 3;
    }

    private void drawGrisButton() {
        float btnW = 320;
        float btnH = 120; // Assuming similar dimensions to old buttons or using texture size
        float x = 200; // Bottom Left
        float y = 60;

        Texture tex = texBtnGris;

        // Hover Check
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        // Simple hover check (y-flipped mouse coordinates are handled by unproject
        // usually, but Gdx.input.getY() is top-down. viewport.unproject handles this.)
        if (mousePos.x >= x && mousePos.x <= x + btnW &&
                mousePos.y >= y && mousePos.y <= y + btnH) {
            tex = texBtnGrisSel;
        }

        if (tex != null) {
            game.batch.draw(tex, x, y, btnW, btnH);
        }
    }

    private void drawGrid() {
        // GRID
        float gridX = 100;
        float gridY = VIRTUAL_HEIGHT - 350;
        float slotSize = 120; // Increased size
        float gap = 20;
        int cols = 4;

        game.font.getData().setScale(1.0f);

        for (int i = 0; i < recipes.size(); i++) {
            RecipeDisplay r = recipes.get(i);
            float x = gridX + (i % cols) * (slotSize + gap);
            float y = gridY - (i / cols) * (slotSize + gap); // Adjust logic if going upwards, but this is downwards
                                                             // spacing usually

            // Draw Slot Background
            if (textureFondoSlot != null) {
                game.batch.setColor(Color.WHITE);
                game.batch.draw(textureFondoSlot, x, y, slotSize, slotSize);
            }

            // Draw Icon
            if (r.icon != null) {
                // Center icon in top part of slot
                float iconSize = slotSize * 0.7f; // Slightly larger or same
                float iconX = x + (slotSize - iconSize) / 2;
                float iconY = y + (slotSize - iconSize) / 2; // Perfectly centered
                game.batch.draw(r.icon, iconX, iconY, iconSize, iconSize);
            }

            // Draw Name (REMOVED)
            // game.font.setColor(Color.WHITE);
            // game.font.getData().setScale(0.8f);
            // game.font.draw(game.batch, r.name, x + 5, y + 25);

            // Selection Highlight (Yellow Border)
            if (i == selectedRecipeIndex) {
                float borderThickness = 3;
                game.batch.setColor(Color.YELLOW);
                if (textureWhite != null) {
                    // Top
                    game.batch.draw(textureWhite, x - borderThickness, y + slotSize, slotSize + 2 * borderThickness,
                            borderThickness);
                    // Bottom
                    game.batch.draw(textureWhite, x - borderThickness, y - borderThickness,
                            slotSize + 2 * borderThickness, borderThickness);
                    // Left
                    game.batch.draw(textureWhite, x - borderThickness, y - borderThickness, borderThickness,
                            slotSize + 2 * borderThickness);
                    // Right
                    game.batch.draw(textureWhite, x + slotSize, y - borderThickness, borderThickness,
                            slotSize + 2 * borderThickness);
                }
                game.batch.setColor(Color.WHITE);
            }
        }

        // DETAILS PANEL (RIGHT)
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < recipes.size()) {
            RecipeDisplay selected = recipes.get(selectedRecipeIndex);
            float detailsX = 700;
            float detailsY = VIRTUAL_HEIGHT - 300;

            // DRAW LARGE IMAGE ABOVE INFO
            if (selected.icon != null) {
                float largeSize = 130;
                // Position above the text. Text starts at detailsY + 80.
                // Let's put it around detailsY + 120
                float largeX = detailsX + 200; // Indented slightly
                float largeY = detailsY - 20;
                game.batch.draw(selected.icon, largeX, largeY, largeSize, largeSize);
            }

            // Title and Desc
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, selected.name, detailsX + 90, detailsY - 40);

            game.font.setColor(Color.LIGHT_GRAY);
            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, selected.desc, detailsX + 90, detailsY - 80);

            // Ingredients Header REMOVED

            // Ingredients List
            float ingY = detailsY - 200; // Adjusted start since Header is gone
            for (Ingredient ing : selected.ingredients) {
                int owned = returnScreen.getExplorador().getMochila().getCantidad(ing.name.toLowerCase());

                // Render Icon
                Texture ingIcon = null;
                if (ing.name.equalsIgnoreCase("Planta"))
                    ingIcon = texPlanta;
                else if (ing.name.equalsIgnoreCase("Guijarro"))
                    ingIcon = texGuijarro;
                else if (ing.name.equalsIgnoreCase("Baya"))
                    ingIcon = texBaya;

                if (ingIcon != null) {
                    game.batch.setColor(Color.WHITE);
                    game.batch.draw(ingIcon, detailsX + 100, ingY - 30, 30, 30);
                }

                // Name
                game.font.setColor(Color.WHITE);
                game.font.draw(game.batch, ing.name, detailsX + 150, ingY - 10);

                // Count (Owned / Required)
                if (owned >= ing.required)
                    game.font.setColor(Color.GREEN);
                else
                    game.font.setColor(Color.RED);

                game.font.draw(game.batch, owned + "/" + ing.required, detailsX + 390, ingY - 10);

                ingY -= 50;
            }
            game.font.setColor(Color.WHITE);
        }

        // Draw Crafting Message
        if (craftingMessageTimer > 0) {
            game.font.getData().setScale(1.2f);
            if (craftingMessage.contains("éxito"))
                game.font.setColor(Color.GREEN);
            else
                game.font.setColor(Color.RED);

            game.font.draw(game.batch, craftingMessage, 100, 200); // Above button
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1.0f);
        }
    }

    private void drawList() {
        // Deprecated by separated draw methods, keeping empty to satisfy calls if any
        // remainder exists
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

        // Dispose new textures
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
        if (texRepelente != null)
            texRepelente.dispose();
        if (texAmuleto != null)
            texAmuleto.dispose();
        if (texCebo != null)
            texCebo.dispose();
    }
}
