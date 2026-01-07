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
    private class RecipeDisplay {
        String name;
        String desc;
        String cost;

        public RecipeDisplay(String name, String desc, String cost) {
            this.name = name;
            this.desc = desc;
            this.cost = cost;
        }
    }

    private List<RecipeDisplay>[] categoryItems;

    public CrafteoScreen(PokemonMain game, GameScreen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        loadAssets();
        initRecipes();
    }

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
        } catch (Exception e) {
            Gdx.app.error("CrafteoScreen", "Asset Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initRecipes() {
        categoryItems = new ArrayList[3];
        for (int i = 0; i < 3; i++)
            categoryItems[i] = new ArrayList<>();

        // RED: Poké Balls
        categoryItems[0].add(new RecipeDisplay("Heavy Ball", "Captura mejor a bajo nivel", "1 Planta + 5 Guijarros"));

        // PURPLE: Special / Utilities (Assuming Middle is Purple per user request
        // order: Red, Purple, Yellow)
        // Wait, User said: "arriba aparezcan los botones botonCrafteoRojo, al lado
        // derecho botonCrafteoMorado y al lado botonCrafteoAmarillo centrado"
        // This usually means Left: Red, Center: Purple, Right: Yellow? Or Red, then
        // Purple to its right, then Yellow to its right?
        // Let's assume the user meant a sequence: Red -> Purple -> Yellow.
        // Array index 1 maps to Purple in my code above.

        categoryItems[1].add(new RecipeDisplay("Lure", "Atrae Pokémon raros (2 min)", "1 Planta + 3 Bayas"));
        categoryItems[1].add(new RecipeDisplay("Repelente", "Evita encuentros (100 pasos)", "4 Plantas"));
        categoryItems[1].add(new RecipeDisplay("Amuleto", "Aumenta drop rate", "10 Guijarros"));

        // YELLOW: Medicines
        categoryItems[2].add(new RecipeDisplay("Ungüento Herbal", "Cura 20 HP", "3 Plantas + 1 Baya"));
        categoryItems[2].add(new RecipeDisplay("Elixir", "Restaura 1 Movimiento", "2 Bayas + 2 Guijarros"));
        categoryItems[2].add(new RecipeDisplay("Revivir", "Revive con 50% HP", "5 Plantas + 5 Bayas + 1 G."));
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
        drawList();

        // Exit Hint
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(1.0f);
        game.font.draw(game.batch, "Presione ESC para salir", 20, 30);
        game.font.setColor(Color.WHITE);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(returnScreen);
            return;
        }

        // Arrow Keys for Category
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCategory = (selectedCategory + 1) % 3;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCategory = (selectedCategory - 1 + 3) % 3;
        }

        // Mouse Hover for Category
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        float btnW = 320;
        float btnH = 120;
        float startX = (VIRTUAL_WIDTH - (3 * btnW + 40)) / 2;
        float startY = VIRTUAL_HEIGHT - 210;

        for (int i = 0; i < 3; i++) {
            float x = startX + i * (btnW + 20);
            if (mousePos.x >= x && mousePos.x <= x + btnW &&
                    mousePos.y >= startY && mousePos.y <= startY + btnH) {
                selectedCategory = i;
            }
        }
    }

    private void drawButtons() {
        float btnW = 320;
        float btnH = 120;
        // Centered layout for 3 buttons with 20px spacing
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

    private void drawList() {
        List<RecipeDisplay> items = categoryItems[selectedCategory];
        float startX = 200;
        float startY = VIRTUAL_HEIGHT - 370;
        float lineHeight = 50;

        game.font.getData().setScale(1.2f);
        game.font.setColor(Color.YELLOW);
        String catTitle = (selectedCategory == 0) ? "POKÉ BALLS"
                : ((selectedCategory == 1) ? "UTILIDADES" : "MEDICINAS");
        game.font.draw(game.batch, catTitle, startX, startY + 50);

        game.font.getData().setScale(1.0f);

        for (int i = 0; i < items.size(); i++) {
            RecipeDisplay item = items.get(i);
            float y = startY - (i * lineHeight * 2);

            game.font.setColor(Color.WHITE);
            // Bullet point
            game.font.draw(game.batch, "- " + item.name, startX, y);

            // Description below
            game.font.setColor(Color.LIGHT_GRAY);
            game.font.draw(game.batch, "  " + item.desc, startX + 20, y - 25);

            // Cost on the right
            game.font.setColor(Color.CYAN);
            game.font.draw(game.batch, "Costo: " + item.cost, startX + 400, y - 25);
        }
        game.font.setColor(Color.WHITE);
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
        // ReturnScreen is shared, do not dispose it here
    }
}
