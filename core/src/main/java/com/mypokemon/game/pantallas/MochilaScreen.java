package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.Pokemon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Screen;
import com.mypokemon.game.inventario.Item;

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
    private Texture texReproductor;
    private Texture texGuante;

    // New Textures for Heavy Ball and Lure
    private Texture texHeavyBall;
    private Texture texFrijol;

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
        public Item itemReal; // Referencia al ítem real del sistema OO

        public ItemData(String nombre, String descripcion, Texture textura, int cantidad, Item itemReal) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.textura = textura;
            this.cantidad = cantidad;
            this.itemReal = itemReal;
        }

        // Constructor de compatibilidad para Pokémon (que no son Items)
        public ItemData(String nombre, String descripcion, Texture textura, int cantidad) {
            this(nombre, descripcion, textura, cantidad, null);
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

    // STATE MACHINE
    private enum InventoryState {
        BROWSING,
        OPTIONS_MENU,
        SELECT_POKEMON_TARGET,
        SELECT_MOVE_TARGET // Future expansion
    }

    private InventoryState currentState = InventoryState.BROWSING;

    // Options Menu Data
    private List<String> currentOptions = new ArrayList<>();
    private int selectedOptionIndex = 0;
    private ItemData selectedItemForAction = null;
    // currentActionType eliminado - ya no se necesita con el sistema OO

    // Feedback
    private String feedbackMessage = "";
    private float feedbackTimer = 0;

    public MochilaScreen(PokemonMain game, Screen returnScreen, Explorador explorador) {
        super(game);
        this.returnScreen = returnScreen;
        this.explorador = explorador;
        Gdx.app.log("MochilaScreen", "Constructor call started");

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
                texElixir = new Texture(Gdx.files.internal("elixirPielPiedra.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing elixirPielPiedra.png");
            }
            try {
                texRevivir = new Texture(Gdx.files.internal("revivircasero.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing revivircasero.png");
            }
            try {
                texReproductor = new Texture(Gdx.files.internal("reproductor.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing reproductor.png");
            }
            try {
                texGuante = new Texture(Gdx.files.internal("guanteReflejo.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing guanteReflejo.png");
            }
            try {
                texHeavyBall = new Texture(Gdx.files.internal("pokeballpeso.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing pokeballpeso.png");
            }
            try {
                texFrijol = new Texture(Gdx.files.internal("frijolMagico.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing frijolMagico.png");
            }
            try {
                texMarcoVerde = new Texture(Gdx.files.internal("marcoVerde.png"));
            } catch (Exception e) {
                Gdx.app.error("Mochila", "Missing marcoVerde.png");
            }

        } catch (Exception e) {
            Gdx.app.error("MochilaScreen", "Error loading textures: " + e.getMessage());
        }

        // Define Button Positions
        buttonPositions = new float[buttonNames.length][2];
        float totalWidth = (buttonNames.length * buttonWidth) + ((buttonNames.length - 1) * 20);
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;
        float startY = VIRTUAL_HEIGHT - 220;

        for (int i = 0; i < buttonNames.length; i++) {
            buttonPositions[i][0] = startX + i * (buttonWidth + 20);
            buttonPositions[i][1] = startY;
        }

        // Create white pixel
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);

        // Create dark gray pixel
        pixmap.setColor(0.0f, 0.0f, 0.0f, 0.5f);
        pixmap.fill();
        textureFondoSlot = new Texture(pixmap);
        pixmap.dispose();

        // Font setup
        fontContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fontContador.getData().setScale(1.5f);

        Gdx.app.log("MochilaScreen", "Positions and assets initialized");
        updateVisibleItems();
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
        } else if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
            drawPokemonSelectionPrompt(game.batch);
        }

        // Draw Feedback Message (abajo a la izquierda, en rojo)
        if (feedbackTimer > 0) {
            game.font.setColor(Color.RED);
            game.font.getData().setScale(1.2f);
            game.font.draw(game.batch, feedbackMessage, 100, 80);
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
            // Check if we are in Pokemon Tab (3) or Item Tabs (<3)
            int totalItems = (selectedIndex == 3) ? explorador.getEquipo().size() : visibleItems.size();
            if (indexSeleccionado < totalItems) {
                if (selectedIndex < 3) {
                    selectedItemForAction = visibleItems.get(indexSeleccionado);
                    openOptionsMenu(selectedItemForAction);
                } else if (selectedIndex == 3) {
                    // Open options for Pokemon
                    openOptionsMenu(null);
                }
            }
        }

        // Mouse Check for "OPCIONES" button
        float btnX = 100;
        float btnY = 160;
        float btnW = 150;
        float btnH = 40;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW &&
                    mousePos.y >= btnY && mousePos.y <= btnY + btnH) {

                int totalItems = (selectedIndex == 3) ? explorador.getEquipo().size() : visibleItems.size();
                if (indexSeleccionado < totalItems) {
                    if (selectedIndex < 3) {
                        selectedItemForAction = visibleItems.get(indexSeleccionado);
                        openOptionsMenu(selectedItemForAction);
                    } else if (selectedIndex == 3) {
                        openOptionsMenu(null);
                    }
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

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        updateVisibleItems();
    }

    private void openOptionsMenu(ItemData item) {
        currentOptions.clear();

        // ** POQUEMON TAB LOGIC **
        if (selectedIndex == 3) {
            // Case: Pokemon Selected
            currentOptions.add("Cambiar a principal");
            // Add Info or other stuff if desired, but user asked for "una sola opcion"
            selectedIndex = 3; // Ensure we stick to this tab
        }
        // ** ITEM LOGIC **
        else if (item.itemReal != null) {
            // NUEVO SISTEMA OO: Usa el método getOpciones() del ítem real
            currentOptions.addAll(item.itemReal.getOpciones());
        } else {
            // Fallback para casos sin itemReal (no debería ocurrir en el futuro)
            currentOptions.add("Tirar");
        }

        selectedOptionIndex = 0;
        currentState = InventoryState.OPTIONS_MENU;
    }

    private void executeOption(String option) {
        if (selectedItemForAction == null && selectedIndex != 3)
            return;

        // **OPCIÓN: Cambiar a principal (POKEMON)**
        if (option.equals("Cambiar a principal")) {
            // Logic: Swap selected pokemon with first pokemon in team
            int index = indexSeleccionado;
            List<Pokemon> equipo = explorador.getEquipo();

            if (index > 0 && index < equipo.size()) {
                Pokemon selected = equipo.get(index);
                Pokemon currentMain = equipo.get(0);

                // If in battle and current main is fainted or simply user wants to switch
                // context
                // The team order defines the 'main' pokemon usually.

                // Swap in list
                equipo.set(0, selected);
                equipo.set(index, currentMain);

                showFeedback("¡" + selected.getNombre() + " es ahora tu Pokémon principal!");

                if (returnScreen instanceof BattleScreen) {
                    ((BattleScreen) returnScreen).cambiarPokemon(selected);
                }
            } else {
                showFeedback("Ya es el principal.");
            }
            currentState = InventoryState.BROWSING;
            return;
        }

        // **OPCIÓN: Tirar**
        if (option.equals("Tirar")) {
            explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1);
            showFeedback("Tiraste 1 " + selectedItemForAction.nombre);
            updateVisibleItems();
            if (indexSeleccionado >= visibleItems.size())
                indexSeleccionado = Math.max(0, visibleItems.size() - 1);
            currentState = InventoryState.BROWSING;
            return;
        }

        // **OPCIÓN: Lanzar** (Pokéballs)
        if (option.equals("Lanzar")) {
            if (returnScreen instanceof BattleScreen) {
                if (explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1)) {
                    BattleScreen battle = (BattleScreen) returnScreen;
                    String internalName = mapNameToInternal(selectedItemForAction.nombre);
                    battle.usarItemEnBatalla(internalName);
                    game.setScreen(battle);
                }
            } else {
                showFeedback("No puedes lanzar una pokebola, no estas en batalla.");
                currentState = InventoryState.BROWSING;
            }
            return;
        }

        // **OPCIÓN: Información**
        if (option.equals("Información")) {
            currentState = InventoryState.BROWSING;
            return;
        }

        // **SISTEMA OO: Opciones de uso (Curar, Revivir, Usar, Comer, Encender,
        // Equipar)**
        if (selectedItemForAction.itemReal != null && selectedItemForAction.itemReal.esUsable()) {

            // Caso especial: Equipar, Encender, Apagar o Tomar (No requieren target Pokemon
            // manual o requieren validación especial)
            if (option.equals("Equipar") || option.equals("Encender") || option.equals("Apagar")
                    || option.equals("Tomar")) {
                com.mypokemon.game.inventario.interfaces.IUsable itemUsable = (com.mypokemon.game.inventario.interfaces.IUsable) selectedItemForAction.itemReal;

                // Validación para Tomar (Solo en batalla)
                if (option.equals("Tomar")) {
                    if (!(returnScreen instanceof BattleScreen)) {
                        showFeedback("no puedes tomar el elixir porque no estas en batalla.");
                        currentState = InventoryState.BROWSING;
                        return;
                    }
                }

                // Lógica específica para Guante
                if (option.equals("Equipar") && selectedItemForAction.itemReal.getId().equals("guante")) {
                    explorador.activarGuante(300f); // Duración de 5 minutos
                }

                // Determinar target (para Tomar usamos el pokemon activo en batalla)
                Pokemon target = null;
                if (option.equals("Tomar") && returnScreen instanceof BattleScreen) {
                    target = ((BattleScreen) returnScreen).getPokemonJugador();
                }

                com.mypokemon.game.inventario.ResultadoUso resultado = itemUsable.usar(target, explorador.getMochila());
                showFeedback(resultado.getMensaje());
                updateVisibleItems();
                currentState = InventoryState.BROWSING;
                return;
            }

            // El ítem es usable - ir al selector de Pokémon para opciones que SÍ requieren
            // target
            if (option.equals("Curar") || option.equals("Revivir") || option.equals("Usar") || option.equals("Comer")) {
                // Switch to Pokemon Tab (Index 3)
                selectedIndex = 3;
                updateVisibleItems();
                indexSeleccionado = 0; // Reset selection to first pokemon
            }
            currentState = InventoryState.SELECT_POKEMON_TARGET;
        } else {
            showFeedback("Este ítem no se puede usar.");
            currentState = InventoryState.BROWSING;
        }
    }

    private void applyItemToPokemon(Pokemon p) {
        if (selectedItemForAction == null || selectedItemForAction.itemReal == null) {
            showFeedback("Error: ítem no disponible");
            currentState = InventoryState.BROWSING;
            return;
        }

        Item item = selectedItemForAction.itemReal;

        // NUEVO SISTEMA OO: Verificar si el ítem es usable
        if (item.esUsable()) {
            com.mypokemon.game.inventario.interfaces.IUsable itemUsable = (com.mypokemon.game.inventario.interfaces.IUsable) item;

            // Usar el ítem en el Pokémon
            com.mypokemon.game.inventario.ResultadoUso resultado = itemUsable.usar(p,
                    explorador.getMochila());

            // Mostrar mensaje de resultado
            showFeedback(resultado.getMensaje());
        } else {
            showFeedback("Este ítem no se puede usar en Pokémon.");
        }

        // Regresar a la pestaña de materiales y actualizar vista
        selectedIndex = 0;
        updateVisibleItems();
        currentState = InventoryState.BROWSING;
    }

    private void handleGridNavigation() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            indexSeleccionado++;
            int totalItems = (selectedIndex == 3) ? explorador.getEquipo().size() : visibleItems.size();
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                if (indexSeleccionado >= 6)
                    indexSeleccionado = 0;
            } else {
                if (indexSeleccionado >= totalItems)
                    indexSeleccionado = 0;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indexSeleccionado--;
            int totalItems = (selectedIndex == 3) ? explorador.getEquipo().size() : visibleItems.size();
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                if (indexSeleccionado < 0)
                    indexSeleccionado = 5;
            } else {
                if (indexSeleccionado < 0)
                    indexSeleccionado = Math.max(0, totalItems - 1);
            }
        }

        // UP/DOWN Navigation (Grid 3 columns)
        int columns = 3;
        int totalItems;
        if (currentState == InventoryState.SELECT_POKEMON_TARGET || selectedIndex == 3) {
            totalItems = explorador.getEquipo().size();
            if (currentState == InventoryState.SELECT_POKEMON_TARGET)
                totalItems = 6; // Fixed grid for target selection
        } else {
            totalItems = visibleItems.size();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (totalItems > 0) {
                indexSeleccionado += columns;
                if (indexSeleccionado >= totalItems) {
                    // Wrap to top same column or just clamp? Standard varies.
                    // Let's loop to top:
                    indexSeleccionado = indexSeleccionado % columns;
                    // Ensure valid
                    if (indexSeleccionado >= totalItems)
                        indexSeleccionado = 0;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (totalItems > 0) {
                indexSeleccionado -= columns;
                if (indexSeleccionado < 0) {
                    // Wrap to bottom
                    int rows = (int) Math.ceil((double) totalItems / columns);
                    int bottomIndex = indexSeleccionado + (rows * columns);
                    if (bottomIndex >= totalItems)
                        bottomIndex -= columns;
                    indexSeleccionado = bottomIndex;
                }
            }
        }

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
            return "pocion";
        if (display.equals("Elíxir de Piel de Piedra"))
            return "elixir";
        if (display.equals("Revivir Casero"))
            return "revivir";
        if (display.equals("Reproductor de música"))
            return "reproductor";
        if (display.equals("Guante de reflejo cuarcítico"))
            return "guante";
        if (display.equals("Frijol mágico"))
            return "frijol";
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
        // Position it lower to avoid covering description text
        float mx = 100;
        float my = 120; // Bottom of the menu (bajado para no tapar el texto)
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

    private void drawPokemonSelectionPrompt(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        game.font.setColor(Color.YELLOW);
        game.font.getData().setScale(1.5f);
        game.font.draw(batch, "SELECCIONA UN POKÉMON", 700, VIRTUAL_HEIGHT - 50);
        game.font.getData().setScale(1.0f);
        game.font.setColor(Color.WHITE);
    }

    private void updateVisibleItems() {
        visibleItems.clear();
        Inventario inventory = explorador.getMochila();

        if (selectedIndex == 0) { // Red Button: Materiales
            // Obtener ítems reales del inventario
            Item planta = inventory.getItem("planta");
            if (planta != null && planta.getCantidad() > 0)
                visibleItems.add(new ItemData("Planta Medicinal", "Ingrediente básico para medicinas.",
                        texPlanta, planta.getCantidad(), planta));

            Item baya = inventory.getItem("baya");
            if (baya != null && baya.getCantidad() > 0)
                visibleItems.add(new ItemData("Baya Aranja", "Restaura el 10% de los PS del Pokémon.",
                        texBaya, baya.getCantidad(), baya));

            Item guijarro = inventory.getItem("guijarro");
            if (guijarro != null && guijarro.getCantidad() > 0)
                visibleItems.add(new ItemData("Guijarro", "Ingrediente principal para fabricar Poké Balls.",
                        texGuijarro, guijarro.getCantidad(), guijarro));

        } else if (selectedIndex == 1) { // Blue Button: Pokebolas
            Item pokeball = inventory.getItem("pokeball");
            if (pokeball != null && pokeball.getCantidad() > 0)
                visibleItems.add(new ItemData("Poké Ball", "Dispositivo para atrapar Pokémon.",
                        texPokeball, pokeball.getCantidad(), pokeball));

            Item heavyball = inventory.getItem("heavyball");
            if (heavyball != null && heavyball.getCantidad() > 0)
                visibleItems.add(new ItemData("Poké Ball de Peso", "Dispositivo con mejor captura en nivel bajo.",
                        texHeavyBall, heavyball.getCantidad(), heavyball));

        } else if (selectedIndex == 2) { // Yellow Button: Pociones + Crafteo + Lure
            Item pocion = inventory.getItem("pocion");
            if (pocion != null && pocion.getCantidad() > 0)
                visibleItems.add(new ItemData("Poción Herbal", "Restaura el 20% de los PS del Pokémon.",
                        texPocionHerbal, pocion.getCantidad(), pocion));

            Item elixir = inventory.getItem("elixir");
            if (elixir != null && elixir.getCantidad() > 0)
                visibleItems.add(new ItemData("Elíxir de Piel de Piedra",
                        "Aumenta la potencia del ataque (+3 por ataque).",
                        texElixir, elixir.getCantidad(), elixir));

            Item revivir = inventory.getItem("revivir");
            if (revivir != null && revivir.getCantidad() > 0)
                visibleItems.add(new ItemData("Revivir Casero", "Restaura el 50% de los PS del Pokémon.",
                        texRevivir, revivir.getCantidad(), revivir));

            Item reproductor = inventory.getItem("reproductor");
            if (reproductor != null && reproductor.getCantidad() > 0)
                visibleItems.add(new ItemData("Reproductor de música", "Permite escuchar música de fondo.",
                        texReproductor, reproductor.getCantidad(), reproductor));

            Item guante = inventory.getItem("guante");
            if (guante != null && guante.getCantidad() > 0)
                visibleItems.add(new ItemData("Guante de reflejo cuarcítico",
                        "Utilizan guijarros brillantes para recolectar doble recurso.",
                        texGuante, guante.getCantidad(), guante));

            Item frijol = inventory.getItem("frijol");
            if (frijol != null && frijol.getCantidad() > 0)
                visibleItems.add(new ItemData("Frijol mágico", "Restaura el 100% de los PS del Pokémon.",
                        texFrijol, frijol.getCantidad(), frijol));

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

    // Old methods replaced by new logic

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

                // Añadir nivel de investigación
                com.mypokemon.game.EspeciePokemon especie = explorador.getRegistro().getRegistro().get(p.getNombre());
                if (especie != null) {
                    desc += "\nInv: " + especie.getNivelInvestigacion() + "/10";
                } else {
                    desc += "\nInv: 0/10";
                }
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
                float btnY = 160; // Bajado para no tapar el texto
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
        if (texReproductor != null)
            texReproductor.dispose();
        if (texGuante != null)
            texGuante.dispose();
        if (texHeavyBall != null)
            texHeavyBall.dispose();
        if (texFrijol != null)
            texFrijol.dispose();
        if (texMarcoVerde != null)
            texMarcoVerde.dispose();

        for (Texture t : pokemonTextureCache.values()) {
            if (t != null)
                t.dispose();
        }
        pokemonTextureCache.clear();
    }
}
