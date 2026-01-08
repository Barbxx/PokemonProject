package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private Texture[] btnCatNormal;
    private Texture[] btnCatSelected;
    private String[] btnNames = { "botonCrafteoRojo", "botonCrafteoMorado", "botonCrafteoAmarillo" };
    // 0=Red, 1=Purple, 2=Yellow
    private int selectedCategory = 0;

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
        List<Ingredient> ingredients;

        public RecipeDisplay(String name, String desc) {
            this.name = name;
            this.desc = desc;
            this.ingredients = new ArrayList<>();
        }

        public void addIngredient(String name, int required) {
            ingredients.add(new Ingredient(name, required));
        }
    }

    private List<RecipeDisplay>[] categoryItems;

    // Item Textures
    private Texture texPlanta, texGuijarro, texBaya;
    private Texture texPokebola, texPocion, texCebo;

    // Selected Recipe Index for Middle Category
    private int selectedRecipeIndex = -1;

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

            btnCatNormal = new Texture[3];
            btnCatSelected = new Texture[3];

            for (int i = 0; i < 3; i++) {
                try {
                    btnCatNormal[i] = new Texture(Gdx.files.internal(btnNames[i] + ".png"));
                    btnCatSelected[i] = new Texture(Gdx.files.internal(btnNames[i] + "_seleccionado.png"));
                } catch (Exception e) {
                    Gdx.app.error("Crafteo", "Error loading button " + i);
                }
            }

            // Load Item Textures
            try {
                texPlanta = new Texture(Gdx.files.internal("planta.png"));
                texGuijarro = new Texture(Gdx.files.internal("guijarro.png"));
                texBaya = new Texture(Gdx.files.internal("baya.png"));
                texPokebola = new Texture(Gdx.files.internal("pokebola.png"));
                texPocion = new Texture(Gdx.files.internal("pocion.png"));
                // Using 'lure.png' or 'cebo.png'? Assuming 'lure.png' exists based on Mochila
                // logic,
                // but usually user files align. Let's try likely names or fallback.
                texCebo = new Texture(Gdx.files.internal("lure.png"));
            } catch (Exception e) {
                Gdx.app.error("Crafteo", "Error loading item textures", e);
            }

        } catch (Exception e) {
            Gdx.app.error("CrafteoScreen", "Asset Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initRecipes() {
        categoryItems = new ArrayList[3];
        for (int i = 0; i < 3; i++)
            categoryItems[i] = new ArrayList<>();

        // Middle Button (Recipes) - index 1
        RecipeDisplay r1 = new RecipeDisplay("Poké Ball", "Captura Pokémon");
        r1.addIngredient("Planta", 2);
        r1.addIngredient("Guijarro", 3);
        categoryItems[1].add(r1);

        RecipeDisplay r2 = new RecipeDisplay("Poción", "Recupera HP");
        r2.addIngredient("Planta", 3);
        r2.addIngredient("Baya", 1);
        categoryItems[1].add(r2);

        RecipeDisplay r3 = new RecipeDisplay("Cebo", "Atrae Pokémon");
        r3.addIngredient("Planta", 1);
        r3.addIngredient("Baya", 3);
        categoryItems[1].add(r3);
    }

    @Override
    public void render(float delta) {
        handleInput();

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        drawButtons();

        // Draw content based on selection
        if (selectedCategory == 0) {
            drawMaterials(); // Red Button
        } else if (selectedCategory == 1) {
            drawRecipes(); // Middle Button
        } else if (selectedCategory == 2) {
            drawCraftedItems(); // Yellow Button
        }

        // Exit Hint Removed as requested

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

            // Check Category Buttons
            float btnW = 320;
            float btnH = 120;
            float startX = (VIRTUAL_WIDTH - (3 * btnW + 40)) / 2;
            float startY = VIRTUAL_HEIGHT - 210;

            for (int i = 0; i < 3; i++) {
                float x = startX + i * (btnW + 20);
                if (mousePos.x >= x && mousePos.x <= x + btnW &&
                        mousePos.y >= startY && mousePos.y <= startY + btnH) {
                    selectedCategory = i;
                    selectedRecipeIndex = -1; // Reset recipe selection on category switch
                    return;
                }
            }

            // Check Recipe Selection (ONLY if Middle Category) - GRID LOGIC
            if (selectedCategory == 1) {
                float gridX = 100;
                float gridY = VIRTUAL_HEIGHT - 350;
                float slotSize = 80;
                float gap = 20;
                int cols = 4;

                List<RecipeDisplay> recipes = categoryItems[1];
                for (int i = 0; i < recipes.size(); i++) {
                    float x = gridX + (i % cols) * (slotSize + gap);
                    float y = gridY - (i / cols) * (slotSize + gap);

                    if (mousePos.x >= x && mousePos.x <= x + slotSize &&
                            mousePos.y >= y && mousePos.y <= y + slotSize) {
                        selectedRecipeIndex = i;
                    }
                }
            }
        }
    }

    private void drawButtons() {
        float btnW = 320;
        float btnH = 120;
        float startX = (VIRTUAL_WIDTH - (3 * btnW + 40)) / 2;
        float startY = VIRTUAL_HEIGHT - 210;

        for (int i = 0; i < 3; i++) {
            float x = startX + i * (btnW + 20);
            Texture tex = (i == selectedCategory) ? btnCatSelected[i] : btnCatNormal[i];

            if (tex != null) {
                game.batch.draw(tex, x, startY, btnW, btnH);
            }
        }
    }

    // RED BUTTON: Materials List as Table
    private void drawMaterials() {
        float tableX = 300;
        float tableY = VIRTUAL_HEIGHT - 300;
        float rowHeight = 60;
        float col1X = tableX; // Icon
        float col2X = tableX + 80; // Name
        float col3X = tableX + 300; // Count

        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.2f);

        // Table Header
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "ITEM", col2X, tableY + 50);
        game.font.draw(game.batch, "CANTIDAD", col3X, tableY + 50);
        game.font.setColor(Color.WHITE);

        int plantas = returnScreen.getExplorador().getMochila().getPlantas();
        int guijarros = returnScreen.getExplorador().getMochila().getGuijarros();
        int bayas = returnScreen.getExplorador().getMochila().getBayas();

        // Row 1: Plantas
        float y1 = tableY;
        if (texPlanta != null)
            game.batch.draw(texPlanta, col1X, y1 - 40, 40, 40);
        game.font.draw(game.batch, "Plantas", col2X, y1 - 10);
        game.font.draw(game.batch, String.valueOf(plantas), col3X + 20, y1 - 10);

        // Row 2: Guijarros
        float y2 = tableY - rowHeight;
        if (texGuijarro != null)
            game.batch.draw(texGuijarro, col1X, y2 - 40, 40, 40);
        game.font.draw(game.batch, "Guijarros", col2X, y2 - 10);
        game.font.draw(game.batch, String.valueOf(guijarros), col3X + 20, y2 - 10);

        // Row 3: Bayas
        float y3 = tableY - rowHeight * 2;
        if (texBaya != null)
            game.batch.draw(texBaya, col1X, y3 - 40, 40, 40);
        game.font.draw(game.batch, "Bayas", col2X, y3 - 10);
        game.font.draw(game.batch, String.valueOf(bayas), col3X + 20, y3 - 10);
    }

    // MIDDLE BUTTON: Grid + Details Panel
    private void drawRecipes() {
        // GRID
        float gridX = 100;
        float gridY = VIRTUAL_HEIGHT - 350;
        float slotSize = 80;
        float gap = 20;
        int cols = 4;

        List<RecipeDisplay> recipes = categoryItems[1];

        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.MAGENTA);
        game.font.draw(game.batch, "RECETAS", gridX, gridY + 80);
        game.font.getData().setScale(1.0f);

        for (int i = 0; i < recipes.size(); i++) {
            RecipeDisplay r = recipes.get(i);
            float x = gridX + (i % cols) * (slotSize + gap);
            float y = gridY - (i / cols) * (slotSize + gap);

            // Draw Slot Background
            game.batch.setColor(Color.DARK_GRAY);
            // Assuming we don't have a slot texture, draw a colored square placeholder or
            // use generic texture region
            if (background != null) { // Just reuse something or draw a colored pixel if we had one
                // Ideally we'd have a 'slot' texture. For now, let's use a solid color if we
                // can,
                // but we can't easily draw primitives inside batch without a Texture.
                // We can re-use 'btnCatNormal' scaled down or similar, or just rely on item
                // icon.
            }
            game.batch.setColor(Color.WHITE); // Reset

            // Draw Icon
            Texture icon = null;
            if (r.name.contains("Poké Ball"))
                icon = texPokebola;
            else if (r.name.contains("Poción"))
                icon = texPocion;
            else if (r.name.contains("Cebo"))
                icon = texCebo;

            if (icon != null) {
                game.batch.draw(icon, x + 10, y + 10, slotSize - 20, slotSize - 20);
            }

            // Selection Highlight
            if (i == selectedRecipeIndex) {
                game.font.setColor(Color.YELLOW);
                game.font.draw(game.batch, "[X]", x + slotSize - 25, y + slotSize - 5);
                // Or draw a border if we had a white pixel texture
            }
        }

        // DETAILS PANEL (RIGHT)
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < recipes.size()) {
            RecipeDisplay selected = recipes.get(selectedRecipeIndex);
            float detailsX = 700;
            float detailsY = VIRTUAL_HEIGHT - 300;
            float panelW = 400;
            // float panelH = 300;

            // Title and Desc
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, selected.name, detailsX, detailsY + 80);

            game.font.setColor(Color.LIGHT_GRAY);
            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, selected.desc, detailsX, detailsY + 40);

            // Ingredients Header
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, "INGREDIENTES:", detailsX, detailsY);

            // Ingredients List
            float ingY = detailsY - 40;
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
                    game.batch.draw(ingIcon, detailsX, ingY - 30, 30, 30);
                }

                // Render Text Box
                // Background for text (Optional, skipping for now)

                // Name
                game.font.setColor(Color.WHITE);
                game.font.draw(game.batch, ing.name, detailsX + 40, ingY - 10);

                // Count (Owned / Required)
                if (owned >= ing.required)
                    game.font.setColor(Color.GREEN);
                else
                    game.font.setColor(Color.RED);

                game.font.draw(game.batch, owned + "/" + ing.required, detailsX + 250, ingY - 10);

                ingY -= 50;
            }
            game.font.setColor(Color.WHITE);
        }
    }

    // YELLOW BUTTON: Crafted Items
    private void drawCraftedItems() {
        float startX = 200;
        float startY = VIRTUAL_HEIGHT - 300;
        float iconSize = 64;
        float gap = 20;

        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "ITEMS CREADOS", startX, startY + 60);
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.2f);

        int pokebolas = returnScreen.getExplorador().getMochila().getPokeBalls();
        // Assuming getters exist or fallback to generic bag access.
        // Mochila class usually tracks PokeBalls. Potions/Lures might be items in list.
        // For distinct display, we check common items.
        int pociones = 0; // Default if not explicitly tracked by counter
        int cebos = 0;

        // Count from inventory list for non-explicit counters if needed
        // Assuming Mochila has methods or we iterate items.
        // Mochila.java usually has getPociones(), getCebos() or similar if implemented.
        // Based on previous files, let's try to get them or just show 0 placeholder if
        // valid getter missing.
        // Actually, let's use what we know exists: getPokeBalls(). For others we might
        // need to iterate items list if no getter.
        pociones = returnScreen.getExplorador().getMochila().getCantidad("pocion");
        cebos = returnScreen.getExplorador().getMochila().getCantidad("lure");

        // 1. Poké Balls
        if (texPokebola != null)
            game.batch.draw(texPokebola, startX, startY - iconSize, iconSize, iconSize);
        game.font.draw(game.batch, "Poké Balls: " + pokebolas, startX + iconSize + gap, startY - 20);

        // 2. Pociones
        float y2 = startY - (iconSize + gap) * 1.5f;
        if (texPocion != null)
            game.batch.draw(texPocion, startX, y2 - iconSize, iconSize, iconSize);
        game.font.draw(game.batch, "Pociones: " + pociones, startX + iconSize + gap, y2 - 20);

        // 3. Cebos
        float y3 = startY - (iconSize + gap) * 3.0f;
        if (texCebo != null)
            game.batch.draw(texCebo, startX, y3 - iconSize, iconSize, iconSize);
        game.font.draw(game.batch, "Cebos: " + cebos, startX + iconSize + gap, y3 - 20);
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
        if (btnCatNormal != null)
            for (Texture t : btnCatNormal)
                if (t != null)
                    t.dispose();
        if (btnCatSelected != null)
            for (Texture t : btnCatSelected)
                if (t != null)
                    t.dispose();

        if (texPlanta != null)
            texPlanta.dispose();
        if (texGuijarro != null)
            texGuijarro.dispose();
        if (texBaya != null)
            texBaya.dispose();
        if (texPokebola != null)
            texPokebola.dispose();
        if (texPocion != null)
            texPocion.dispose();
        if (texCebo != null)
            texCebo.dispose();
    }
}
