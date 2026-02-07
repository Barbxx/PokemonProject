package com.mypokemon.game.pantallas;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.Item;

/**
 * Pantalla de inventario (Mochila). Gestiona la visualización de items,
 * categorías y Pokémon, así como el uso de objetos. Utiliza lógica de
 * SpriteBatch para renderizado robusto.
 */
public class MochilaScreen extends BaseScreen {

    private final Explorador explorador;
    private Texture background;
    private Texture[] buttonsNormal;
    private Texture[] buttonsSelected;
    private String[] buttonNames = {"botonIrojo", "botonIazul", "botonIamarillo", "botonImorado"};

    // Texturas de objetos
    private Texture texPokeball;
    private Texture texPlanta;
    private Texture texGuijarro;
    private Texture texBaya;

    // Nuevas texturas de objetos
    private Texture texPocionHerbal;
    private Texture texElixir;
    private Texture texRevivir;
    private Texture texReproductor;
    private Texture texGuante;

    // Nuevas texturas para Heavy Ball y Lure
    private Texture texHeavyBall;
    private Texture texFrijol;

    // Nueva textura para el marco verde
    private Texture texMarcoVerde;

    // Caché de texturas de Pokémon
    private java.util.Map<String, Texture> pokemonTextureCache = new java.util.TreeMap<>();
    private java.util.Map<String, Texture> typeTextureCache = new java.util.TreeMap<>();

    // Clase auxiliar para elementos de la cuadrícula
    // Modelo de datos
    public class ItemData {

        public String nombre;
        public String descripcion;
        public Texture textura;
        public int cantidad;
        public Item itemReal;

        public ItemData(String nombre, String descripcion, Texture textura, int cantidad, Item itemReal) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.textura = textura;
            this.cantidad = cantidad;
            this.itemReal = itemReal;
        }

        // Constructor de compatibilidad para Pokémon
        public ItemData(String nombre, String descripcion, Texture textura, int cantidad) {
            this(nombre, descripcion, textura, cantidad, null);
        }
    }

    private List<ItemData> visibleItems = new ArrayList<>();

    private float buttonWidth = 140;
    private float buttonHeight = 140;
    private float[][] buttonPositions;
    private int selectedIndex = 0;

    // Campos solicitados por el usuario
    private Texture textureFondoSlot;
    private com.badlogic.gdx.graphics.g2d.BitmapFont fontContador;
    private int indexSeleccionado = 0;
    private Texture whitePixel;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final Screen returnScreen;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private Vector3 mousePos = new Vector3();

    // Máquina de estados
    private enum InventoryState {
        BROWSING,
        OPTIONS_MENU,
        SELECT_POKEMON_TARGET,
        SELECT_MOVE_TARGET
    }

    private InventoryState currentState = InventoryState.BROWSING;

    // Datos del menú de opciones
    private List<String> currentOptions = new ArrayList<>();
    private int selectedOptionIndex = 0;
    private ItemData selectedItemForAction = null;

    // Retroalimentación
    private String feedbackMessage = "";
    private float feedbackTimer = 0;

    /**
     * Constructor de la pantalla de Mochila.
     *
     * @param game Juego principal.
     * @param returnScreen Pantalla a la cual regresar al cerrar.
     * @param explorador Datos del jugador (inventario y equipo).
     */
    public MochilaScreen(PokemonMain game, Screen returnScreen, Explorador explorador) {
        super(game);
        this.returnScreen = returnScreen;
        this.explorador = explorador;
        Gdx.app.log("MochilaScreen", "Constructor call started");

        // Cámara y Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        // Inicializar arrays
        buttonsNormal = new Texture[buttonNames.length];
        buttonsSelected = new Texture[buttonNames.length];

        // Cargar fondo y botones
        try {
            background = new Texture(Gdx.files.internal("fondoMochila.png"));
            Gdx.app.log("MochilaScreen", "Background loaded successfully");

            for (int i = 0; i < buttonNames.length; i++) {
                buttonsNormal[i] = new Texture(Gdx.files.internal(buttonNames[i] + ".png"));
                buttonsSelected[i] = new Texture(Gdx.files.internal(buttonNames[i] + "_seleccionado.png"));
            }
            Gdx.app.log("MochilaScreen", "Buttons loaded successfully");

            // Cargar texturas de objetos
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

            // Cargar nuevas texturas
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

        // Definir posiciones de los botones
        buttonPositions = new float[buttonNames.length][2];
        float totalWidth = (buttonNames.length * buttonWidth) + ((buttonNames.length - 1) * 20);
        float startX = (VIRTUAL_WIDTH - totalWidth) / 2;
        float startY = VIRTUAL_HEIGHT - 220;

        for (int i = 0; i < buttonNames.length; i++) {
            buttonPositions[i][0] = startX + i * (buttonWidth + 20);
            buttonPositions[i][1] = startY;
        }

        // Crear pixel blanco
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1,
                com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);

        // Crear pixel gris oscuro
        pixmap.setColor(0.0f, 0.0f, 0.0f, 0.5f);
        pixmap.fill();
        textureFondoSlot = new Texture(pixmap);
        pixmap.dispose();

        // Configuración de fuentes
        fontContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fontContador.getData().setScale(1.5f);

        Gdx.app.log("MochilaScreen", "Positions and assets initialized");
        updateVisibleItems();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Renderiza la interfaz de la mochila: fondo, pestañas, grilla de items y
     * menús contextuales.
     *
     * @param delta Tiempo transcurrido.
     */
    @Override
    public void render(float delta) {
        // Temporizador de retroalimentación
        if (feedbackTimer > 0) {
            feedbackTimer -= delta;
        }

        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        // Posicion Mouse
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);

        game.batch.begin();

        if (background != null) {
            game.batch.draw(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        }

        // DELEGACIÓN DE ESTADOS
        if (currentState == InventoryState.BROWSING) {
            handleBrowsingInput();
        } else if (currentState == InventoryState.OPTIONS_MENU) {
            handleOptionsMenuInput();
        } else if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
            handlePokemonTargetInput();
        }

        // Dibujar Main UI
        if (currentState == InventoryState.SELECT_POKEMON_TARGET && selectedIndex != 3) {
            selectedIndex = 3;
            updateVisibleItems();
        }

        drawTabs(game.batch);
        drawGridSlots(game.batch);
        drawGridContent(game.batch);

        if (currentState == InventoryState.BROWSING) {
            dibujarExplicacion(game.batch);
        } else if (currentState == InventoryState.OPTIONS_MENU) {
            drawOptionsMenu(game.batch);
            dibujarExplicacion(game.batch);
        } else if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
            drawPokemonSelectionPrompt(game.batch);
        }

        // Mensaje de retroalimentación
        if (feedbackTimer > 0) {
            game.font.setColor(Color.RED);
            game.font.getData().setScale(1.2f);
            game.font.draw(game.batch, feedbackMessage, 100, 80);
            game.font.getData().setScale(1.0f);
            game.font.setColor(Color.WHITE);
        }

        game.batch.end();
    }

    // MANEJADORES DE ENTRADA
    private void handleBrowsingInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (currentState == InventoryState.BROWSING) {
                game.setScreen(returnScreen);
                return;
            }
        }

        // Navegacion (Flechas + Mouse)
        handleGridNavigation();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

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

        float btnX = 100;
        float btnY = 160;
        float btnW = 150;
        float btnH = 40;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW
                    && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {

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
            if (selectedOptionIndex < 0) {
                selectedOptionIndex = currentOptions.size() - 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOptionIndex++;
            if (selectedOptionIndex >= currentOptions.size()) {
                selectedOptionIndex = 0;
            }
        }

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
                selectedOptionIndex = i;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeOption(currentOptions.get(selectedOptionIndex));
        }
    }

    private void handlePokemonTargetInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = InventoryState.OPTIONS_MENU;
            selectedIndex = 0;
            updateVisibleItems();
            return;
        }

        handleGridNavigation();

        // Seleccionar Pokemon
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            List<Pokemon> equipo = explorador.getEquipo();
            if (indexSeleccionado < equipo.size()) {
                Pokemon target = equipo.get(indexSeleccionado);
                applyItemToPokemon(target);
            }
        }
    }

    // LOGICA

    /**
     * Cambia la pestaña seleccionada (categoría de items o Pokémon).
     *
     * @param index Índice de la pestaña (0-3).
     */
    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        updateVisibleItems();
    }

    /**
     * Despliega el menú de opciones para un ítem seleccionado.
     *
     * @param item Datos del ítem seleccionado.
     */
    private void openOptionsMenu(ItemData item) {
        currentOptions.clear();

        if (selectedIndex == 3) {

            currentOptions.add("Cambiar a principal");

            selectedIndex = 3;
        } // LOGICA DE ITEMS
        else if (item.itemReal != null) {

            currentOptions.addAll(item.itemReal.getOpciones());
        } else {

            currentOptions.add("Tirar");
        }

        selectedOptionIndex = 0;
        currentState = InventoryState.OPTIONS_MENU;
    }

    /**
     * Ejecuta una opción seleccionada del menú (ej: Usar, Tirar).
     *
     * @param option Nombre de la opción.
     */
    private void executeOption(String option) {
        if (selectedItemForAction == null && selectedIndex != 3) {
            return;
        }

        // OPCIÓN: Cambiar pokemon a principal
        if (option.equals("Cambiar a principal")) {

            int index = indexSeleccionado;
            List<Pokemon> equipo = explorador.getEquipo();

            if (index > 0 && index < equipo.size()) {
                Pokemon selected = equipo.get(index);
                Pokemon currentMain = equipo.get(0);

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

        // OPCIÓN: Tirar
        if (option.equals("Tirar")) {
            explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1);
            showFeedback("Tiraste 1 " + selectedItemForAction.nombre);
            updateVisibleItems();
            if (indexSeleccionado >= visibleItems.size()) {
                indexSeleccionado = Math.max(0, visibleItems.size() - 1);
            }
            currentState = InventoryState.BROWSING;
            return;
        }

        // OPCIÓN: Lanzar pokeball
        if (option.equals("Lanzar")) {
            if (returnScreen instanceof BattleScreen) {
                if (explorador.getMochila().consumirItem(mapNameToInternal(selectedItemForAction.nombre), 1)) {
                    BattleScreen battle = (BattleScreen) returnScreen;
                    String internalName = mapNameToInternal(selectedItemForAction.nombre);
                    battle.usarItemEnBatalla(internalName);
                    game.setScreen(battle);
                }
            } else {
                showFeedback("No puedes lanzar una poke ball, no estas en batalla.");
                currentState = InventoryState.BROWSING;
            }
            return;
        }

        // OPCIÓN: Información
        if (option.equals("Información")) {
            currentState = InventoryState.BROWSING;
            return;
        }

        // Opciones de uso (Curar, Revivir, Usar, Comer, Encender, Equipar)
        if (selectedItemForAction.itemReal != null && selectedItemForAction.itemReal.esUsable()) {

            // Caso especial: Equipar, Encender, Apagar o Tomar
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
                    explorador.activarGuante(300f); // Duración de 5 minutos de uso
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

            // El ítem es usable
            if (option.equals("Curar") || option.equals("Revivir") || option.equals("Usar") || option.equals("Comer")) {

                selectedIndex = 3;
                updateVisibleItems();
                indexSeleccionado = 0;
            }
            currentState = InventoryState.SELECT_POKEMON_TARGET;
        } else {
            showFeedback("Este ítem no se puede usar.");
            currentState = InventoryState.BROWSING;
        }
    }

    /**
     * Aplica el ítem seleccionado al Pokémon objetivo.
     *
     * @param p Pokémon objetivo.
     */
    private void applyItemToPokemon(Pokemon p) {
        if (selectedItemForAction == null || selectedItemForAction.itemReal == null) {
            showFeedback("Error: ítem no disponible");
            currentState = InventoryState.BROWSING;
            return;
        }

        Item item = selectedItemForAction.itemReal;

        // Verificar si el ítem es usable
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
                if (indexSeleccionado >= 6) {
                    indexSeleccionado = 0;
                }
            } else {
                if (indexSeleccionado >= totalItems) {
                    indexSeleccionado = 0;
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indexSeleccionado--;
            int totalItems = (selectedIndex == 3) ? explorador.getEquipo().size() : visibleItems.size();
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                if (indexSeleccionado < 0) {
                    indexSeleccionado = 5;
                }
            } else {
                if (indexSeleccionado < 0) {
                    indexSeleccionado = Math.max(0, totalItems - 1);
                }
            }
        }

        // ARRIBA/ABAJO Navegacion
        int columns = 3;
        int totalItems;
        if (currentState == InventoryState.SELECT_POKEMON_TARGET || selectedIndex == 3) {
            totalItems = explorador.getEquipo().size();
            if (currentState == InventoryState.SELECT_POKEMON_TARGET) {
                totalItems = 6;
            }
        } else {
            totalItems = visibleItems.size();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (totalItems > 0) {
                indexSeleccionado += columns;
                if (indexSeleccionado >= totalItems) {

                    indexSeleccionado = indexSeleccionado % columns;

                    if (indexSeleccionado >= totalItems) {
                        indexSeleccionado = 0;
                    }
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (totalItems > 0) {
                indexSeleccionado -= columns;
                if (indexSeleccionado < 0) {

                    int rows = (int) Math.ceil((double) totalItems / columns);
                    int bottomIndex = indexSeleccionado + (rows * columns);
                    if (bottomIndex >= totalItems) {
                        bottomIndex -= columns;
                    }
                    indexSeleccionado = bottomIndex;
                }
            }
        }

    }

    private String mapNameToInternal(String display) {
        if (display.equals("Planta Medicinal")) {
            return "planta";
        }
        if (display.equals("Baya Aranja")) {
            return "baya";
        }
        if (display.equals("Guijarro")) {
            return "guijarro";
        }
        if (display.equals("Poké Ball")) {
            return "pokeball";
        }
        if (display.equals("Poké Ball de Peso")) {
            return "heavyball";
        }
        if (display.equals("Poción Herbal")) {
            return "pocion";
        }
        if (display.equals("Elíxir de Piel de Piedra")) {
            return "elixir";
        }
        if (display.equals("Revivir Casero")) {
            return "revivir";
        }
        if (display.equals("Reproductor de música")) {
            return "reproductor";
        }
        if (display.equals("Guante de reflejo cuarcítico")) {
            return "guante";
        }
        if (display.equals("Frijol mágico")) {
            return "frijol";
        }
        return display.toLowerCase();
    }

    private void showFeedback(String msg) {
        feedbackMessage = msg;
        feedbackTimer = 3.0f;
    }

    // METODOS DE AYUDA
    private void drawTabs(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (buttonsNormal != null && buttonsSelected != null && buttonPositions != null) {
            for (int i = 0; i < buttonNames.length; i++) {
                if (i >= buttonPositions.length) {
                    break;
                }

                float bx = buttonPositions[i][0];
                float by = buttonPositions[i][1];

                boolean isSelected = (i == selectedIndex);
                if (currentState == InventoryState.BROWSING) {

                    boolean isHovered = mousePos.x >= bx && mousePos.x <= bx + buttonWidth
                            && mousePos.y >= by && mousePos.y <= by + buttonHeight;
                    if (isHovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        selectedIndex = i;
                        updateVisibleItems();
                        indexSeleccionado = 0;
                    }
                    if (isHovered) {
                        isSelected = true;
                    }
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
            // Resaltado de selección
            if (i == indexSeleccionado && whitePixel != null) {
                batch.setColor(Color.YELLOW);
                batch.draw(whitePixel, x, y, size, 4); // Abajo
                batch.draw(whitePixel, x, y + size - 4, size, 4); // Arriba
                batch.draw(whitePixel, x, y, 4, size); // Izquierda
                batch.draw(whitePixel, x + size - 4, y, 4, size); // Derecha
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawGridContent(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        int columnas = 3;
        float size = 150f;
        float margen = 15f;

        // Dibujar Pokemon
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
        } // Dibujar Items
        else {
            for (int i = 0; i < visibleItems.size(); i++) {
                float x = 700 + (i % columnas) * (size + margen);
                float y = 300 - (i / columnas) * (size + margen);
                ItemData item = visibleItems.get(i);
                if (item.textura != null) {
                    batch.draw(item.textura, x + 5, y + 5, size - 10, size - 10);
                }
                if (fontContador != null) {
                    fontContador.draw(batch, "x" + item.cantidad, x + 40, y + 5);
                }
            }
        }
    }

    private void drawPokemonSlot(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, Pokemon p, float x, float y,
            float size) {
        String pName = p.getNombre();

        if (!pokemonTextureCache.containsKey(pName)) {
            try {
                String nameClean = pName.toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
                pokemonTextureCache.put(pName, new Texture(Gdx.files.internal(nameClean + ".png")));
            } catch (Exception e) {
            }
        }
        Texture tex = pokemonTextureCache.get(pName);
        if (tex != null) {
            batch.draw(tex, x + 5, y + 5, size - 10, size - 10);
        } else {
            fontContador.draw(batch, pName, x + 5, y + size / 2);
        }

        // HP
        batch.setColor(Color.RED);
        batch.draw(whitePixel, x + 5, y + 5, size - 10, 5);
        float hpPercent = p.getHpActual() / p.getHpMaximo();
        batch.setColor(Color.GREEN);
        batch.draw(whitePixel, x + 5, y + 5, (size - 10) * hpPercent, 5);
        batch.setColor(Color.WHITE);
    }

    private void drawOptionsMenu(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {

        float mx = 100;
        float my = 120;
        float mw = 250;
        float mh = 40;

        // Fondo del Menu
        float totalH = currentOptions.size() * mh + 10;
        batch.setColor(Color.BLACK);
        batch.draw(whitePixel, mx, my, mw, totalH);
        batch.setColor(Color.WHITE);
        // Borde
        batch.draw(whitePixel, mx, my, mw, 2);
        batch.draw(whitePixel, mx, my + totalH, mw, 2);
        batch.draw(whitePixel, mx, my, 2, totalH);
        batch.draw(whitePixel, mx + mw, my, 2, totalH + 2);

        for (int i = 0; i < currentOptions.size(); i++) {
            float y = my + totalH - 10 - (i * mh);

            // Resaltar
            if (i == selectedOptionIndex) {
                batch.setColor(Color.DARK_GRAY);
                batch.draw(whitePixel, mx + 2, y - mh + 5, mw - 4, mh - 5);
                batch.setColor(Color.WHITE);
            }

            game.font.setColor(Color.WHITE);
            if (i == selectedOptionIndex) {
                game.font.setColor(Color.CYAN);
            }
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

        if (selectedIndex == 0) { // Materiales
            // Obtener ítems reales del inventario
            Item planta = inventory.getItem("planta");
            if (planta != null && planta.getCantidad() > 0) {
                visibleItems.add(new ItemData("Planta Medicinal", "Ingrediente básico para medicinas.",
                        texPlanta, planta.getCantidad(), planta));
            }

            Item baya = inventory.getItem("baya");
            if (baya != null && baya.getCantidad() > 0) {
                visibleItems.add(new ItemData("Baya Aranja", "Restaura el 10% de HP del Pokémon.",
                        texBaya, baya.getCantidad(), baya));
            }

            Item guijarro = inventory.getItem("guijarro");
            if (guijarro != null && guijarro.getCantidad() > 0) {
                visibleItems.add(new ItemData("Guijarro", "Ingrediente principal para fabricar Poké Balls.",
                        texGuijarro, guijarro.getCantidad(), guijarro));
            }

        } else if (selectedIndex == 1) { // Pokebolas
            Item pokeball = inventory.getItem("pokeball");
            if (pokeball != null && pokeball.getCantidad() > 0) {
                visibleItems.add(new ItemData("Poké Ball", "Dispositivo para atrapar Pokémon.",
                        texPokeball, pokeball.getCantidad(), pokeball));
            }

            Item heavyball = inventory.getItem("heavyball");
            if (heavyball != null && heavyball.getCantidad() > 0) {
                visibleItems.add(new ItemData("Poké Ball de Peso", "Dispositivo con mejor captura en nivel bajo.",
                        texHeavyBall, heavyball.getCantidad(), heavyball));
            }

        } else if (selectedIndex == 2) { // Pociones o Crafteo
            Item pocion = inventory.getItem("pocion");
            if (pocion != null && pocion.getCantidad() > 0) {
                visibleItems.add(new ItemData("Poción Herbal", "Restaura el 20% de HP del Pokémon.",
                        texPocionHerbal, pocion.getCantidad(), pocion));
            }

            Item elixir = inventory.getItem("elixir");
            if (elixir != null && elixir.getCantidad() > 0) {
                visibleItems.add(new ItemData("Elíxir de Piel de Piedra",
                        "Aumenta la potencia del ataque (+3 por ataque).",
                        texElixir, elixir.getCantidad(), elixir));
            }

            Item revivir = inventory.getItem("revivir");
            if (revivir != null && revivir.getCantidad() > 0) {
                visibleItems.add(new ItemData("Revivir Casero", "Restaura el 50% de HP del Pokémon.",
                        texRevivir, revivir.getCantidad(), revivir));
            }

            Item reproductor = inventory.getItem("reproductor");
            if (reproductor != null && reproductor.getCantidad() > 0) {
                visibleItems
                        .add(new ItemData("Reproductor de música", "Permite escuchar música de fondo durante el viaje.",
                                texReproductor, reproductor.getCantidad(), reproductor));
            }

            Item guante = inventory.getItem("guante");
            if (guante != null && guante.getCantidad() > 0) {
                visibleItems.add(new ItemData("Guante de reflejo cuarcítico",
                        "Utilizan guijarros brillantes para recolectar doble recurso.",
                        texGuante, guante.getCantidad(), guante));
            }

            Item frijol = inventory.getItem("frijol");
            if (frijol != null && frijol.getCantidad() > 0) {
                visibleItems.add(new ItemData("Frijol mágico", "Restaura el 100% de HP del Pokémon.",
                        texFrijol, frijol.getCantidad(), frijol));
            }

        } else if (selectedIndex == 3) {

        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

            List<Pokemon> equipo = explorador.getEquipo();
            if (indexSeleccionado < equipo.size()) {
                Pokemon p = equipo.get(indexSeleccionado);
                titulo = p.getNombre() + " Nv." + p.getNivel();
                desc = "HP: " + (int) p.getHpActual() + "/" + (int) p.getHpMaximo();

                // nivel de investigación
                com.mypokemon.game.EspeciePokemon especie = explorador.getRegistro().getRegistro().get(p.getNombre());
                if (especie != null) {
                    desc += "\nInv: " + especie.getNivelInvestigacion() + "/10";
                } else {
                    desc += "\nInv: 0/10";
                }

                // Cargar texturas de tipo para este pokemon
                com.mypokemon.game.BasePokemonData pokemonData = com.mypokemon.game.BasePokemonData.get(p.getNombre());
                if (pokemonData != null && pokemonData.tipo != null) {
                    String[] tipos = pokemonData.tipo.split(" / ");
                    for (String tipo : tipos) {
                        String tipoKey = tipo.trim();
                        if (!typeTextureCache.containsKey(tipoKey)) {
                            try {
                                typeTextureCache.put(tipoKey, new Texture(Gdx.files.internal("Tipo " + tipoKey + ".png")));
                            } catch (Exception e) {
                                Gdx.app.error("MochilaScreen", "Error loading type texture: Tipo " + tipoKey + ".png");
                            }
                        }
                    }
                }
            } else {

            }
        }

        if (!titulo.isEmpty() && game.font != null) {

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

                game.font.draw(batch, desc, 100, 310);

                // Dibujar imagenes de tipo si esta en la pestaña de Pokemon (Boton morado)
                if (selectedIndex == 3 && indexSeleccionado < explorador.getEquipo().size()) {
                    Pokemon p = explorador.getEquipo().get(indexSeleccionado);
                    com.mypokemon.game.BasePokemonData pokemonData = com.mypokemon.game.BasePokemonData.get(p.getNombre());
                    if (pokemonData != null && pokemonData.tipo != null) {
                        String[] tipos = pokemonData.tipo.split(" / ");
                        float tipoX = 320;
                        float tipoY = 315;
                        float tipoWidth = 130;
                        float tipoHeight = 34;
                        float tipoSpacingY = 38;

                        for (int i = 0; i < tipos.length; i++) {
                            String tipoKey = tipos[i].trim();
                            Texture tipoTexture = typeTextureCache.get(tipoKey);
                            if (tipoTexture != null) {
                                batch.draw(tipoTexture, tipoX, tipoY - (i * tipoSpacingY), tipoWidth, tipoHeight);
                            }
                        }
                    }
                }
            }

            // BOTON OPCIONES
            if (currentState == InventoryState.BROWSING && !titulo.isEmpty()) {
                float btnX = 100;
                float btnY = 160;
                float btnW = 150;
                float btnH = 40;

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
        if (background != null) {
            background.dispose();
        }
        if (buttonsNormal != null) {
            for (Texture t : buttonsNormal) {
                if (t != null) {
                    t.dispose();
                }
            }
        }
        if (buttonsSelected != null) {
            for (Texture t : buttonsSelected) {
                if (t != null) {
                    t.dispose();
                }
            }
        }
        if (texPokeball != null) {
            texPokeball.dispose();
        }
        if (texPlanta != null) {
            texPlanta.dispose();
        }
        if (texGuijarro != null) {
            texGuijarro.dispose();
        }
        if (texBaya != null) {
            texBaya.dispose();
        }
        if (whitePixel != null) {
            whitePixel.dispose();
        }
        if (textureFondoSlot != null) {
            textureFondoSlot.dispose();
        }
        if (fontContador != null) {
            fontContador.dispose();
        }

        // Dispose new textures
        if (texPocionHerbal != null) {
            texPocionHerbal.dispose();
        }
        if (texElixir != null) {
            texElixir.dispose();
        }
        if (texRevivir != null) {
            texRevivir.dispose();
        }
        if (texReproductor != null) {
            texReproductor.dispose();
        }
        if (texGuante != null) {
            texGuante.dispose();
        }
        if (texHeavyBall != null) {
            texHeavyBall.dispose();
        }
        if (texFrijol != null) {
            texFrijol.dispose();
        }
        if (texMarcoVerde != null) {
            texMarcoVerde.dispose();
        }

        for (Texture t : pokemonTextureCache.values()) {
            if (t != null) {
                t.dispose();
            }
        }
        pokemonTextureCache.clear();

        for (Texture t : typeTextureCache.values()) {
            if (t != null) {
                t.dispose();
            }
        }
        typeTextureCache.clear();
    }
}
