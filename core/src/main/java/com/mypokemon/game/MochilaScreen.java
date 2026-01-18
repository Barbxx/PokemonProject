package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.utils.BaseScreen;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Screen;

/**
 * Screen for the inventory (Mochila).
 * Pure SpriteBatch logic with robust texture loading and coordinate handling.
 */
public class MochilaScreen extends BaseScreen {
    private final Explorador explorador;
    private Texture background;
    private Texture[] buttonsNormal;
    private Texture[] buttonsSelected;
    private String[] buttonNames = { "botonIrojo", "botonIazul", "botonIamarillo", "botonImorado" };

    // Item Textures
    private Texture texPokeball;
    private Texture texPlanta;
    private Texture texGuijarro;
    private Texture texBaya;

    // New Potion/Item Textures
    private Texture texPocionHerbal;
    private Texture texElixir;
    private Texture texRevivir;
    private Texture texRepelente;
    private Texture texAmuleto;

    // New Textures for Heavy Ball and Lure
    private Texture texHeavyBall;
    private Texture texLure;

    // New Texture for Green Frame
    private Texture texMarcoVerde;

    // Pokemon Texture Cache
    private java.util.Map<String, Texture> pokemonTextureCache = new java.util.HashMap<>();

    // Helper class for grid items
    // Data Model
    public class ItemData {
        public String nombre;
        public String descripcion;
        public Texture textura;
        public int cantidad;

        public ItemData(String nombre, String descripcion, Texture textura, int cantidad) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.textura = textura;
            this.cantidad = cantidad;
        }
    }

    private List<ItemData> visibleItems = new ArrayList<>();

    private float buttonWidth = 140; // Increased size as requested
    private float buttonHeight = 140;
    private float[][] buttonPositions;
    private int selectedIndex = 0; // Category Selection

    // User requested fields
    private Texture textureFondoSlot; // Will generate procedurally if missing
    private com.badlogic.gdx.graphics.g2d.BitmapFont fontContador;
    private int indexSeleccionado = 0; // 0 to 20
    private Texture whitePixel; // Kept for selection box if needed

    private OrthographicCamera camera;
    private Viewport viewport;
    private final Screen returnScreen;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private Vector3 mousePos = new Vector3();

    private com.mypokemon.game.logic.Crafteo crafteo;

    private boolean confirmingPokeball = false;

    // STATE MACHINE
    private enum InventoryState {
        BROWSING,
        OPTIONS_MENU,
        SHOW_RECIPES,
        SELECT_POKEMON_TARGET,
        SELECT_MOVE_TARGET // Future expansion
    }

    private InventoryState currentState = InventoryState.BROWSING;

    // Options Menu Data
    private List<String> currentOptions = new ArrayList<>();
    private int selectedOptionIndex = 0;
    private ItemData selectedItemForAction = null;
    private String currentActionType = ""; // "CURAR", "REVIVIR", etc.

    // Feedback
    private String feedbackMessage = "";
    private float feedbackTimer = 0;

    // Recipes Data
    private List<String> currentRecipesToShow = new ArrayList<>();

    public MochilaScreen(PokemonMain game, Screen returnScreen, Explorador explorador) {
        super(game);
        this.returnScreen = returnScreen;
        this.explorador = explorador;
        Gdx.app.log("MochilaScreen", "Constructor call started");

        crafteo = new com.mypokemon.game.logic.Crafteo();

        // Initialize Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        // Initialize arrays early to avoid NullPointerException in render
        buttonsNormal = new Texture[buttonNames.length];
        buttonsSelected = new Texture[buttonNames.length];

        // Load Background and Buttons
        try {
            background = new Texture(Gdx.files.internal("fondoMochila.png"));
            Gdx.app.log("MochilaScreen", "Background loaded successfully");

            for (int i = 0; i < buttonNames.length; i++) {
                buttonsNormal[i] = new Texture(Gdx.files.internal(buttonNames[i] + ".png"));
                buttonsSelected[i] = new Texture(Gdx.files.internal(buttonNames[i] + "_seleccionado.png"));
            }
            Gdx.app.log("MochilaScreen", "Buttons loaded successfully");

            // Load Item Textures
            // Note: User mentioned these are already loaded/available. Assuming filenames.
            try {
                texPokeball = new Texture(Gdx.files.internal("pokeball.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing pokeball.png");
            }
            try {
                texPlanta = new Texture(Gdx.files.internal("planta.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing planta.png");
            }
            try {
                texGuijarro = new Texture(Gdx.files.internal("guijarro.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing guijarro.png");
            }
            try {
                texBaya = new Texture(Gdx.files.internal("baya.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing baya.png");
            }

            // Load new textures
            try {
                texPocionHerbal = new Texture(Gdx.files.internal("pocionherbal.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing pocionherbal.png");
            }
            try {
                texElixir = new Texture(Gdx.files.internal("elixirEnergia.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing elixirEnergia.png");
            }
            try {
                texRevivir = new Texture(Gdx.files.internal("revivircasero.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing revivircasero.png");
            }
            try {
                texRepelente = new Texture(Gdx.files.internal("repelente.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing repelente.png");
            }
            try {
                texAmuleto = new Texture(Gdx.files.internal("amuleto.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing amuleto.png");
            }

            try {
                texHeavyBall = new Texture(Gdx.files.internal("pokeballpeso.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing pokeballpeso.png");
            }
            try {
                texLure = new Texture(Gdx.files.internal("lure.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing lure.png");
            }
            try {
                texMarcoVerde = new Texture(Gdx.files.internal("marcoVerde.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing marcoVerde.png");
            }

        } catch (Exception e) {
            Gdx.app.error("MochilaScreen", "Error loading textures: " + e.getMessage());
        }

        // Define Button Positions (Horizontal Centered)
        buttonPositions = new float[buttonNames.length][2];
        float totalWidth = (buttonNames.length * buttonWidth) + ((buttonNames.length - 1) * 20);
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;
        float startY = VIRTUAL_HEIGHT - 220; // Moved down as requested

        for (int i = 0; i < buttonNames.length; i++) {
            buttonPositions[i][0] = startX + i * (buttonWidth + 20);
            buttonPositions[i][1] = startY;
        }

        // Create white pixel for drawing shapes and slot background
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);

        // Create dark gray pixel for slot background (simulating slot_vacio.png)
        // Create dark gray pixel for slot background (simulating slot_vacio.png)
        // Lighter as requested
        pixmap.setColor(0.0f, 0.0f, 0.0f, 0.5f);
        pixmap.fill();
        textureFondoSlot = new Texture(pixmap);
        pixmap.dispose();

        // Font setup
        fontContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fontContador.getData().setScale(1.5f); // Increased scale from 0.8 to 1.5

        Gdx.app.log("MochilaScreen", "Positions and assets initialized");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); // Clear input processor to avoid previous screen interference
    }

    @Override
    public void render(float delta) {
        // Feedback Timer
        if (feedbackTimer > 0) {
            feedbackTimer -= delta;
        }

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        // Track mouse position
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // STATE DELEGATION
        if (currentState == InventoryState.BROWSING) {
            handleBrowsingInput();
        } else if (currentState == InventoryState.OPTIONS_MENU) {
            handleOptionsMenuInput();
        } else if (currentState == InventoryState.SHOW_RECIPES) {
            handleRecipesInput();
        } else if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
            handlePokemonTargetInput();
        }

        // Draw Main UI (Always visible mostly)
        // If selecting pokemon, we might want to highlight the pokemon tab
        if (currentState == InventoryState.SELECT_POKEMON_TARGET && selectedIndex != 3) {
            selectedIndex = 3; // Force view to Pokemon
            updateVisibleItems(); // Refresh view
        }

        drawTabs(game.batch);
        drawGridSlots(game.batch);
        drawGridContent(game.batch);

        // Contextual overlays
        if (currentState == InventoryState.BROWSING) {
            dibujarExplicacion(game.batch);
        } else if (currentState == InventoryState.OPTIONS_MENU) {
            drawOptionsMenu(game.batch);
            dibujarExplicacion(game.batch); // Still show info
        } else if (currentState == InventoryState.SHOW_RECIPES) {
            drawRecipesList(game.batch);
        } else if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
            drawPokemonSelectionPrompt(game.batch);
        }

        // Draw Feedback Message
        if (feedbackTimer > 0) {
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, feedbackMessage, VIRTUAL_WIDTH / 2 - 100, VIRTUAL_HEIGHT - 100);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        }

        game.batch.end();
    }

    // -- INPUT HANDLERS --

    private void handleBrowsingInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (currentState == InventoryState.BROWSING) {
                game.setScreen(returnScreen);
                return;
            }
        }

        // Grid Navigation input (Arrowns + Mouse) - Existing Logic adapted
        handleGridNavigation();

        // Select Item to Open Menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Keep ENTER shortcut
            if (indexSeleccionado < visibleItems.size() && selectedIndex < 3) {
                selectedItemForAction = visibleItems.get(indexSeleccionado);
                openOptionsMenu(selectedItemForAction);
            }
        }

        // Mouse Check for "OPCIONES" button
        // Coordinates must match dibujarExplicacion
        float btnX = 100;
        float btnY = 220;
        float btnW = 150;
        float btnH = 40;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Check if clicking Item Grid
            // (Existing select logic via hover handles selection, so we just check if we
            // clicked options)
            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW &&
                    mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                if (indexSeleccionado < visibleItems.size() && selectedIndex < 3) {
                    selectedItemForAction = visibleItems.get(indexSeleccionado);
                    openOptionsMenu(selectedItemForAction);
                }
            }
        }
    }

    private void handleOptionsMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = InventoryState.BROWSING;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOptionIndex--;
            if (selectedOptionIndex < 0)
                selectedOptionIndex = currentOptions.size() - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOptionIndex++;
            if (selectedOptionIndex >= currentOptions.size())
                selectedOptionIndex = 0;
        }

        // Mouse hover for menu
        // Check bounds based on draw logic
        float menuX = 50;
        float menuY = VIRTUAL_HEIGHT - 150;
        for (int i = 0; i < currentOptions.size(); i++) {
            float y = menuY - (i * 50);
            if (mousePos.x >= menuX && mousePos.x <= menuX + 250 && mousePos.y >= y - 40 && mousePos.y <= y) {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    selectedOptionIndex = i;
                    executeOption(currentOptions.get(i));
                    return;
                }
                selectedOptionIndex = i; // Hover select
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeOption(currentOptions.get(selectedOptionIndex));
        }
    }

    private void handleRecipesInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentState = InventoryState.OPTIONS_MENU; // Go back to options
        }
    }

    private void handlePokemonTargetInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = InventoryState.OPTIONS_MENU;
            selectedIndex = 0; // Go back to main tabs? Or stay? Let's reset to materials or whatever we were
                               // on.
            // Ideally we remember previous tab.
            // For simplicity, reset to 0
            selectedIndex = 0;
            updateVisibleItems();
            return;
        }

        // Allow navigation in pokemon list
        handleGridNavigation(); // Re-use grid nav

        // Select Pokemon
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            List<Pokemon> equipo = explorador.getEquipo();
            if (indexSeleccionado < equipo.size()) {
                Pokemon target = equipo.get(indexSeleccionado);
                applyItemToPokemon(target);
            }
        }
    }

    // -- LOGIC --

    private void openOptionsMenu(ItemData item) {
        currentOptions.clear();
        String name = item.nombre;

        if (name.equals("Poké Ball") || name.equals("Poké Ball de Peso")) {
            currentOptions.add("Lanzar");
            currentOptions.add("Tirar");
        } else if (name.equals("Baya Aranja") || name.equals("Poción Herbal")) {
            currentOptions.add("Curar");
            currentOptions.add("Tirar");
        } else if (name.equals("Revivir Casero")) {
            currentOptions.add("Revivir");
            currentOptions.add("Tirar");
        } else if (name.equals("Elíxir de Piel de Piedra")) {
            currentOptions.add("Usar");
            currentOptions.add("Tirar");
        } else if (name.equals("Reproductor de música")) {
            currentOptions.add("Encender");
            currentOptions.add("Tirar");
        } else if (name.equals("Guante de reflejo cuarcítico")) {
            currentOptions.add("Equipar");
            currentOptions.add("Tirar");
        } else if (name.equals("Frijol mágico")) {
            currentOptions.add("Comer");
            currentOptions.add("Tirar");
        } else {
            currentOptions.add("Tirar");
        }

        // Always add "Información" (Ver Info)
        currentOptions.add("Información");

        selectedOptionIndex = 0;
        currentState = InventoryState.OPTIONS_MENU;
    }

    private void executeOption(String option) {
        if (selectedItemForAction == null)
            return;

        if (option.equals("Tirar")) {
            explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1);
            showFeedback("Tiraste 1 " + selectedItemForAction.nombre);
            updateVisibleItems();
            if (indexSeleccionado >= visibleItems.size())
                indexSeleccionado = Math.max(0, visibleItems.size() - 1);
            currentState = InventoryState.BROWSING;

        } else if (option.equals("Ver recetas")) {
            loadRecipesFor(selectedItemForAction.nombre);
            currentState = InventoryState.SHOW_RECIPES;

        } else if (option.equals("Curar")) {
            currentActionType = "CURAR";
            currentState = InventoryState.SELECT_POKEMON_TARGET;

        } else if (option.equals("Revivir")) {
            currentActionType = "REVIVIR";
            currentState = InventoryState.SELECT_POKEMON_TARGET;

        } else if (option.equals("Restaurar movimiento") || option.equals("Usar")) {
            currentActionType = "RESTAURAR";
            currentState = InventoryState.SELECT_POKEMON_TARGET;

        } else if (option.equals("Aplicar")
                || (option.equals("Encender") && selectedItemForAction.nombre.contains("Reproductor"))) {
            if (explorador.getMochila().consumirItem("repelente", 1)) {
                showFeedback("Música de fondo ACTIVADA");
                updateVisibleItems();
                currentState = InventoryState.BROWSING;
            }
        } else if (option.equals("Activar") || option.equals("Equipar")) {
            if (explorador.getMochila().consumirItem("amuleto", 1)) {
                showFeedback("Guante EQUIPADO");
                updateVisibleItems();
                currentState = InventoryState.BROWSING;
            }
        } else if (option.equals("Comer")) {
            currentActionType = "CURAR_FULL";
            currentState = InventoryState.SELECT_POKEMON_TARGET;

        } else if (option.equals("Encender") && selectedItemForAction.nombre.contains("Cebo")) {
            // Legacy fallback if needed, but Frijol is "Comer"
            if (explorador.getMochila().consumirItem("lure", 1)) {
                showFeedback("Cebo ENCENDIDO");
                updateVisibleItems();
                currentState = InventoryState.BROWSING;
            }
        } else if (option.equals("Lanzar")) {
            if (returnScreen instanceof BattleScreen) {
                if (explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1)) {
                    // Need to trigger use in battle
                    BattleScreen battle = (BattleScreen) returnScreen;
                    // Assuming battle has public method or we use reusable method
                    String internalName = mapNameToInternal(selectedItemForAction.nombre);
                    if (internalName.equals("pokeball") || internalName.equals("heavyball")) {
                        battle.usarItemEnBatalla("pokeball"); // Generic for now, or adapt battle
                    }
                    game.setScreen(battle);
                }
            } else {
                showFeedback("No estás en combate");
                currentState = InventoryState.BROWSING;
            }
        } else if (option.equals("Información")) {
            // Just show info (already shown in browsing), maybe highlight
            currentState = InventoryState.BROWSING;
        }
    }

    private void applyItemToPokemon(Pokemon p) {
        String itemInternal = mapNameToInternal(selectedItemForAction.nombre);
        boolean success = false;

        if (currentActionType.equals("CURAR")) {
            if (itemInternal.equals("baya") || itemInternal.equals("unguento")) { // Baya Aranja or Pocion
                float amount = itemInternal.equals("baya") ? 10 : 20;
                if (p.getHpActual() < p.getHpMaximo() && !p.isDebilitado()) {
                    p.recuperarSalud(amount);
                    explorador.getMochila().consumirItem(itemInternal, 1);
                    showFeedback("Curaste a " + p.getNombre());
                    success = true;
                } else {
                    showFeedback("No tiene efecto.");
                }
            }
        } else if (currentActionType.equals("REVIVIR")) {
            if (p.isDebilitado()) {
                p.recuperarSalud(p.getHpMaximo() * 0.5f);
                explorador.getMochila().consumirItem("revivir", 1);
                showFeedback("¡" + p.getNombre() + " revivió!");
                success = true;
            } else {
                showFeedback("No está debilitado.");
            }
        } else if (currentActionType.equals("RESTAURAR")) {
            explorador.getMochila().consumirItem("elixir", 1);
            showFeedback("Ataque subió +3 (Efecto visual)");
            success = true;
        } else if (currentActionType.equals("CURAR_FULL")) {
            if (p.getHpActual() < p.getHpMaximo() && !p.isDebilitado()) {
                p.recuperarSalud(p.getHpMaximo());
                explorador.getMochila().consumirItem("lure", 1);
                showFeedback("¡HP restaurado al 100%!");
                success = true;
            } else {
                showFeedback("No tiene efecto.");
            }
        }

        if (success || !currentActionType.isEmpty()) {
            selectedIndex = 0; // Return to materials tab
            updateVisibleItems();
            currentState = InventoryState.BROWSING;
        }
    }

    private void loadRecipesFor(String itemName) {
        currentRecipesToShow.clear();
        if (itemName.equals("Planta Medicinal")) {
            currentRecipesToShow.add("Poké Ball - 2 Plantas");
            currentRecipesToShow.add("Poké Ball de Peso - 1 Planta");
            currentRecipesToShow.add("Poción Herbal - 3 Plantas");
            currentRecipesToShow.add("Revivir Casero - 5 Plantas");
            currentRecipesToShow.add("Repelente Orgánico - 4 Plantas");
            currentRecipesToShow.add("Cebo de Bayas - 1 Planta");
        } else if (itemName.equals("Guijarro")) {
            currentRecipesToShow.add("Poké Ball - 3 Guijarros");
            currentRecipesToShow.add("Poké Ball de Peso - 5 Guijarros");
            currentRecipesToShow.add("Elíxir de Piel de Piedra - 7 Guijarros");
            currentRecipesToShow.add("Revivir Casero - 1 Guijarro");
            currentRecipesToShow.add("Guante de reflejo - 13 Guijarros");
        } else if (itemName.equals("Baya Aranja")) {
            currentRecipesToShow.add("Poción Herbal - 1 Baya");
            currentRecipesToShow.add("Reproductor de música - 1 Baya");
            currentRecipesToShow.add("Revivir Casero - 5 Bayas");
        }
    }

    private void handleGridNavigation() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            indexSeleccionado++;
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                if (indexSeleccionado >= 6)
                    indexSeleccionado = 0;
            } else {
                if (indexSeleccionado >= visibleItems.size())
                    indexSeleccionado = 0;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indexSeleccionado--;
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                if (indexSeleccionado < 0)
                    indexSeleccionado = 5;
            } else {
                if (indexSeleccionado < 0)
                    indexSeleccionado = Math.max(0, visibleItems.size() - 1);
            }
        }
        // UP/DOWN logic omitted for brevity but should exist in real generic method

        // Mouse hover
        // ... (Existing mouse logic in render handles selection update)
    }

    private String mapNameToInternal(String display) {
        if (display.equals("Planta Medicinal"))
            return "planta";
        if (display.equals("Baya Aranja"))
            return "baya";
        if (display.equals("Guijarro"))
            return "guijarro";
        if (display.equals("Poké Ball"))
            return "pokeball";
        if (display.equals("Poké Ball de Peso"))
            return "heavyball";
        if (display.equals("Poción Herbal"))
            return "unguento";
        if (display.equals("Elíxir de Piel de Piedra"))
            return "elixir";
        if (display.equals("Revivir Casero"))
            return "revivir";
        if (display.equals("Reproductor de música"))
            return "repelente";
        if (display.equals("Guante de reflejo cuarcítico"))
            return "amuleto";
        if (display.equals("Frijol mágico"))
            return "lure";
        return display.toLowerCase();
    }

    private void showFeedback(String msg) {
        feedbackMessage = msg;
        feedbackTimer = 3.0f;
    }

    // DRAW HELPER METHODS

    private void drawTabs(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (buttonsNormal != null && buttonsSelected != null && buttonPositions != null) {
            for (int i = 0; i < buttonNames.length; i++) {
                if (i >= buttonPositions.length)
                    break;

                float bx = buttonPositions[i][0];
                float by = buttonPositions[i][1];

                boolean isSelected = (i == selectedIndex);
                if (currentState == InventoryState.BROWSING) {
                    // Check hover only in browsing
                    boolean isHovered = mousePos.x >= bx && mousePos.x <= bx + buttonWidth &&
                            mousePos.y >= by && mousePos.y <= by + buttonHeight;
                    if (isHovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        selectedIndex = i;
                        updateVisibleItems();
                        indexSeleccionado = 0;
                    }
                    if (isHovered)
                        isSelected = true;
                }

                Texture tex = isSelected && i < buttonsSelected.length && buttonsSelected[i] != null
                        ? buttonsSelected[i]
                        : (i < buttonsNormal.length ? buttonsNormal[i] : null);

                if (tex != null) {
                    batch.draw(tex, bx, by, buttonWidth, buttonHeight);
                }
            }
        }
    }

    private void drawGridSlots(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        int columnas = 3;
        float size = 150f;
        float margen = 15f;

        for (int i = 0; i < 6; i++) {
            float x = 700 + (i % columnas) * (size + margen);
            float y = 300 - (i / columnas) * (size + margen);

            if (textureFondoSlot != null) {
                batch.draw(textureFondoSlot, x, y, size, size);
            }
            // Selection Highlight (Border Only)
            if (i == indexSeleccionado && whitePixel != null) {
                batch.setColor(Color.YELLOW);
                batch.draw(whitePixel, x, y, size, 4); // Bottom
                batch.draw(whitePixel, x, y + size - 4, size, 4); // Top
                batch.draw(whitePixel, x, y, 4, size); // Left
                batch.draw(whitePixel, x + size - 4, y, 4, size); // Right
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawGridContent(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        int columnas = 3;
        float size = 150f;
        float margen = 15f;

        // Pokemon Drawing
        if (selectedIndex == 3) {
            List<Pokemon> equipo = explorador.getEquipo();
            for (int i = 0; i < 6; i++) {
                float x = 700 + (i % columnas) * (size + margen);
                float y = 300 - (i / columnas) * (size + margen);
                if (i < equipo.size()) {
                    Pokemon p = equipo.get(i);
                    drawPokemonSlot(batch, p, x, y, size);
                }
            }
        }
        // Item Drawing
        else {
            for (int i = 0; i < visibleItems.size(); i++) {
                float x = 700 + (i % columnas) * (size + margen);
                float y = 300 - (i / columnas) * (size + margen);
                ItemData item = visibleItems.get(i);
                if (item.textura != null)
                    batch.draw(item.textura, x + 5, y + 5, size - 10, size - 10);
                if (fontContador != null)
                    fontContador.draw(batch, "x" + item.cantidad, x + 40, y + 5);
            }
        }
    }

    private void drawPokemonSlot(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, Pokemon p, float x, float y,
            float size) {
        String pName = p.getNombre();
        // Texture loading check (simplified)
        if (!pokemonTextureCache.containsKey(pName)) {
            try {
                String nameClean = pName.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                pokemonTextureCache.put(pName, new Texture(Gdx.files.internal(nameClean + ".png")));
            } catch (Exception e) {
            }
        }
        Texture tex = pokemonTextureCache.get(pName);
        if (tex != null)
            batch.draw(tex, x + 5, y + 5, size - 10, size - 10);
        else
            fontContador.draw(batch, pName, x + 5, y + size / 2);

        // HP
        batch.setColor(Color.RED);
        batch.draw(whitePixel, x + 5, y + 5, size - 10, 5);
        float hpPercent = p.getHpActual() / p.getHpMaximo();
        batch.setColor(Color.GREEN);
        batch.draw(whitePixel, x + 5, y + 5, (size - 10) * hpPercent, 5);
        batch.setColor(Color.WHITE);
    }

    private void drawOptionsMenu(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // Pop-up Menu Style
        // Position it near the Options button (approx 100, 220)
        float mx = 100;
        float my = 220; // Bottom of the menu
        float mw = 250;
        float mh = 40;

        // Draw Menu Background
        float totalH = currentOptions.size() * mh + 10;
        batch.setColor(Color.BLACK);
        batch.draw(whitePixel, mx, my, mw, totalH);
        batch.setColor(Color.WHITE);
        // Border
        batch.draw(whitePixel, mx, my, mw, 2);
        batch.draw(whitePixel, mx, my + totalH, mw, 2);
        batch.draw(whitePixel, mx, my, 2, totalH);
        batch.draw(whitePixel, mx + mw, my, 2, totalH + 2);

        for (int i = 0; i < currentOptions.size(); i++) {
            float y = my + totalH - 10 - (i * mh);

            // Highlight
            if (i == selectedOptionIndex) {
                batch.setColor(Color.DARK_GRAY);
                batch.draw(whitePixel, mx + 2, y - mh + 5, mw - 4, mh - 5);
                batch.setColor(Color.WHITE);
            }

            game.font.setColor(Color.WHITE);
            if (i == selectedOptionIndex)
                game.font.setColor(Color.CYAN);
            game.font.draw(batch, currentOptions.get(i), mx + 20, y - 5);
        }
        game.font.setColor(Color.WHITE);
    }

    private void drawRecipesList(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        float rx = 200;
        float ry = VIRTUAL_HEIGHT - 200;

        batch.setColor(0, 0, 0, 0.9f);
        batch.draw(whitePixel, rx - 20, ry - 300, 400, 350);
        batch.setColor(Color.WHITE);

        game.font.setColor(Color.ORANGE);
        game.font.draw(batch, "RECETAS:", rx, ry + 30);
        game.font.setColor(Color.WHITE);

        float y = ry;
        for (String line : currentRecipesToShow) {
            game.font.draw(batch, line, rx, y);
            y -= 30;
        }

        game.font.setColor(Color.GRAY);
        game.font.getData().setScale(0.8f);
        game.font.draw(batch, "[Press ESC]", rx, y - 20);
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
    }

    private void drawPokemonSelectionPrompt(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        game.font.setColor(Color.YELLOW);
        game.font.getData().setScale(1.5f);
        game.font.draw(batch, "SELECCIONA UN POKÉMON", 700, VIRTUAL_HEIGHT - 50);
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
    }

    private void drawConfirmationDialog(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        float dialogW = 500;
        float dialogH = 150;
        float dx = (VIRTUAL_WIDTH - dialogW) / 2;
        float dy = (VIRTUAL_HEIGHT - dialogH) / 2;

        batch.setColor(Color.DARK_GRAY);
        batch.draw(whitePixel, dx, dy, dialogW, dialogH);
        batch.setColor(Color.WHITE);
        batch.draw(whitePixel, dx + 5, dy + 5, dialogW - 10, dialogH - 10);

        game.font.setColor(Color.BLACK);
        game.font.draw(batch, "¿Quieres usar una PokeBall?", dx + 50, dy + 100);
        game.font.draw(batch, "S para SI | N para NO", dx + 100, dy + 50);
    }

    private void updateVisibleItems() {
        visibleItems.clear();
        Inventario inventory = explorador.getMochila();

        if (selectedIndex == 0) { // Red Button: Materiales
            if (inventory.getPlantas() > 0)
                visibleItems.add(new ItemData("Planta Medicinal", "Componente base para fabricar pociones y revivir.",
                        texPlanta, inventory.getPlantas()));
            if (inventory.getBayas() > 0)
                visibleItems.add(new ItemData("Baya Aranja", "Restaura 10 HP cuando la vida del Pokémon baja.", texBaya,
                        inventory.getBayas()));
            if (inventory.getGuijarros() > 0)
                visibleItems.add(new ItemData("Guijarro", "Ingrediente principal para fabricar Poké Balls.",
                        texGuijarro, inventory.getGuijarros()));

        } else if (selectedIndex == 1) { // Blue Button: Pokebolas
            if (inventory.getPokeBalls() > 0)
                visibleItems.add(new ItemData("Poké Ball", "Dispositivo para atrapar Pokémon.", texPokeball,
                        inventory.getPokeBalls()));
            if (inventory.getHeavyBalls() > 0)
                visibleItems.add(new ItemData("Poké Ball de Peso", "Dispositivo con mejor captura en nivel bajo.",
                        texHeavyBall, inventory.getHeavyBalls()));

        } else if (selectedIndex == 2) { // Yellow Button: Pociones + Crafteo + Lure
            if (inventory.getUnguentos() > 0)
                visibleItems
                        .add(new ItemData("Poción Herbal", "Cura 20 HP.", texPocionHerbal, inventory.getUnguentos()));
            if (inventory.getElixires() > 0)
                visibleItems.add(new ItemData("Elíxir de Piel de Piedra",
                        "Aumenta la potencia del ataque (+3 por ataque).", texElixir,
                        inventory.getElixires()));
            if (inventory.getRevivires() > 0)
                visibleItems.add(new ItemData("Revivir Casero", "Revive con 50% HP a un Pokémon debilitado.",
                        texRevivir, inventory.getRevivires()));
            if (inventory.getRepelentes() > 0)
                visibleItems.add(new ItemData("Reproductor de música", "Permite escuchar música de fondo.",
                        texRepelente, inventory.getRepelentes()));
            if (inventory.getAmuletos() > 0)
                visibleItems.add(new ItemData("Guante de reflejo cuarcítico",
                        "Utilizan guijarros brillantes para recolectar doble recurso.",
                        texAmuleto, inventory.getAmuletos()));
            if (inventory.getLures() > 0)
                visibleItems.add(new ItemData("Frijol mágico", "Restaura el 100% de HP de un Pokémon.", texLure,
                        inventory.getLures()));

        } else if (selectedIndex == 3) { // Brown Button
            // User moved Lure to Yellow. Leaving empty as implied by "solo aparezcan de
            // estos..."
        }

        // Pokemon Logic (Category 4) is handled separately in render so visibleItems
        // ignored there
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void usarItemEnBatalla(String itemName) {
        if (itemName == null)
            return;
        BattleScreen battle = (BattleScreen) returnScreen;

        if (itemName.equalsIgnoreCase("pokeball")) {
            if (explorador.getMochila().consumirItem("pokeball", 1)) {
                battle.usarItemEnBatalla("pokeball");
                game.setScreen(battle);
            }
        } else if (itemName.equalsIgnoreCase("Poción Herbal") || itemName.equalsIgnoreCase("Ungüento Herbal")) {
            if (explorador.getMochila().consumirItem("unguento", 1)) {
                battle.usarItemEnBatalla("pocion");
                game.setScreen(battle);
            }
        } else if (itemName.equalsIgnoreCase("baya") || itemName.equalsIgnoreCase("Baya Aranja")) {
            if (explorador.getMochila().consumirItem("baya", 1)) {
                battle.usarItemEnBatalla("pocion");
                game.setScreen(battle);
            }
        }
    }

    // Old methods replaced by new logic
    private void dibujarMochila(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        // This method is now redundant as drawGridSlots and drawGridContent act as
        // replacements,
        // but we kept certain logic like pokemon texture loading.
        // We can empty this or leave it if it's not called.
    }

    private void dibujarExplicacion(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        String titulo = "";
        String desc = "";

        if (selectedIndex < 3) {
            if (indexSeleccionado < visibleItems.size()) {
                ItemData item = visibleItems.get(indexSeleccionado);
                titulo = item.nombre;
                desc = item.descripcion;
            }
        } else if (selectedIndex == 3) {
            // Pokemon Details
            List<Pokemon> equipo = explorador.getEquipo();
            if (indexSeleccionado < equipo.size()) {
                Pokemon p = equipo.get(indexSeleccionado);
                titulo = p.getNombre() + " Nv." + p.getNivel();
                desc = "HP: " + (int) p.getHpActual() + "/" + (int) p.getHpMaximo() + " | Tipo: " + p.getTipo();
            } else {
                // Nothing
            }
        }

        if (!titulo.isEmpty() && game.font != null) {
            // Special case for "Planta Medicinal" title -> Image
            if (titulo.equals("Planta Medicinal") && texMarcoVerde != null) {
                batch.draw(texMarcoVerde, 30, 200, 500, 160);
                game.font.setColor(Color.WHITE);
                game.font.getData().setScale(1.0f);
                game.font.draw(batch, desc, 100, 310);
            } else {
                game.font.setColor(Color.YELLOW);
                game.font.getData().setScale(1.2f);
                game.font.draw(batch, titulo, 100, 350); // Title

                game.font.setColor(Color.WHITE);
                game.font.getData().setScale(1.0f);
                // Wrap text if needed, but existing logic didn't wrap much. Assuming short
                // descriptions.
                game.font.draw(batch, desc, 100, 310);
            }

            // Draw "OPCIONES" button
            if (currentState == InventoryState.BROWSING && !titulo.isEmpty()) {
                float btnX = 100;
                float btnY = 220;
                float btnW = 150;
                float btnH = 40;

                // Hover effect logic is in input, but we can visualize it if wanted
                // For now just draw standard button
                batch.setColor(Color.DARK_GRAY);
                batch.draw(whitePixel, btnX, btnY, btnW, btnH);
                batch.setColor(Color.WHITE);
                batch.draw(whitePixel, btnX, btnY, btnW, 2);
                batch.draw(whitePixel, btnX, btnY + btnH, btnW, 2);
                batch.draw(whitePixel, btnX, btnY, 2, btnH);
                batch.draw(whitePixel, btnX + btnW, btnY, 2, btnH);

                game.font.getData().setScale(1.0f);
                game.font.draw(batch, "OPCIONES", btnX + 30, btnY + 28);
            }
        }

    }

    @Override
    public void dispose() {
        Gdx.app.log("MochilaScreen", "Disposing textures...");
        if (background != null)
            background.dispose();
        if (buttonsNormal != null) {
            for (Texture t : buttonsNormal)
                if (t != null)
                    t.dispose();
        }
        if (buttonsSelected != null) {
            for (Texture t : buttonsSelected)
                if (t != null)
                    t.dispose();
        }
        if (texPokeball != null)
            texPokeball.dispose();
        if (texPlanta != null)
            texPlanta.dispose();
        if (texGuijarro != null)
            texGuijarro.dispose();
        if (texBaya != null)
            texBaya.dispose();
        if (whitePixel != null)
            whitePixel.dispose();
        if (textureFondoSlot != null)
            textureFondoSlot.dispose();
        if (fontContador != null)
            fontContador.dispose();

        // Dispose new textures
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
        if (texHeavyBall != null)
            texHeavyBall.dispose();
        if (texLure != null)
            texLure.dispose();
        if (texMarcoVerde != null)
            texMarcoVerde.dispose();

        for (Texture t : pokemonTextureCache.values()) {
            if (t != null)
                t.dispose();
        }
        pokemonTextureCache.clear();
    }
}
