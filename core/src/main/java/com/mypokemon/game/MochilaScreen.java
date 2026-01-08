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

/**
 * Screen for the inventory (Mochila).
 * Pure SpriteBatch logic with robust texture loading and coordinate handling.
 */
public class MochilaScreen extends BaseScreen {
    private Texture background;
    private Texture[] buttonsNormal;
    private Texture[] buttonsSelected;
    private String[] buttonNames = { "botonIrojo", "botonIazul", "botonIamarillo", "botonImarron", "botonImorado" };

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

    // Helper class for grid items
    // Data Model
    public class ItemData {
        public String nombre;
        public String descripcion;
        public Texture textura;

        public ItemData(String nombre, String descripcion, Texture textura) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.textura = textura;
        }
    }

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
    private final GameScreen returnScreen;
    private final float VIRTUAL_WIDTH = 1280;
    private final float VIRTUAL_HEIGHT = 720;

    private Vector3 mousePos = new Vector3();

    private com.mypokemon.game.logic.Crafteo crafteo;

    public MochilaScreen(PokemonMain game, GameScreen returnScreen) {
        super(game);
        this.returnScreen = returnScreen;
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
        pixmap.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        pixmap.fill();
        textureFondoSlot = new Texture(pixmap);
        pixmap.dispose();

        // Font setup
        fontContador = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        fontContador.getData().setScale(1.5f); // Increased scale from 0.8 to 1.5

        Gdx.app.log("MochilaScreen", "Positions and assets initialized");
    }

    @Override
    public void render(float delta) {
        // Handle input to return
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(returnScreen);
            return;
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

        // Draw Buttons
        if (buttonsNormal != null && buttonsSelected != null && buttonPositions != null) {
            for (int i = 0; i < buttonNames.length; i++) {
                if (i >= buttonPositions.length)
                    break;

                float bx = buttonPositions[i][0];
                float by = buttonPositions[i][1];

                boolean isHovered = mousePos.x >= bx && mousePos.x <= bx + buttonWidth &&
                        mousePos.y >= by && mousePos.y <= by + buttonHeight;

                if (isHovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    selectedIndex = i; // Mouse override
                }

                // Show as selected if it's the current category OR hovered
                boolean isSelected = (i == selectedIndex || isHovered);

                Texture tex = isSelected && i < buttonsSelected.length && buttonsSelected[i] != null
                        ? buttonsSelected[i]
                        : (i < buttonsNormal.length ? buttonsNormal[i] : null);

                if (tex != null) {
                    game.batch.draw(tex, bx, by, buttonWidth, buttonHeight);
                }
            }
        }

        // Handle Mouse Click on Grid (NEW)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int columnas = 3;
            float size = 120f;
            float margen = 15f;

            for (int i = 0; i < 6; i++) {
                float x = 400 + (i % columnas) * (size + margen);
                float y = 350 - (i / columnas) * (size + margen);

                if (mousePos.x >= x && mousePos.x <= x + size &&
                        mousePos.y >= y && mousePos.y <= y + size) {
                    indexSeleccionado = i;
                    break;
                }
            }
        }

        // Handle Input for Selection (Arrow Keys)
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            indexSeleccionado++;
            if (indexSeleccionado > 5)
                indexSeleccionado = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            indexSeleccionado--;
            if (indexSeleccionado < 0)
                indexSeleccionado = 5;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            indexSeleccionado -= 3;
            if (indexSeleccionado < 0)
                indexSeleccionado += 6;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            indexSeleccionado += 3;
            if (indexSeleccionado > 5)
                indexSeleccionado -= 6;
        }

        // Handle Crafting Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedIndex == 4) {
                // Nothing special on ENTER for now, maybe open details?
            } else {
                String objetoSeleccionado = obtenerNombrePorIndice(indexSeleccionado);
                if (objetoSeleccionado != null && !objetoSeleccionado.isEmpty()) {
                    boolean exito = crafteo.intentarCraftear(objetoSeleccionado,
                            returnScreen.getExplorador().getMochila());
                    if (exito) {
                        Gdx.app.log("Crafting", "Success: " + objetoSeleccionado);
                    } else {
                        Gdx.app.log("Crafting", "Failed: " + objetoSeleccionado);
                    }
                }
            }
        }

        // Draw new UI
        dibujarMochila(game.batch);
        dibujarExplicacion(game.batch);

        game.batch.end();
    }

    private String obtenerNombrePorIndice(int index) {
        if (selectedIndex == 0) { // Red Button: Materiales (Base items, not craftable usually, but kept for
                                  // context)
            return null;
        } else if (selectedIndex == 1) { // Blue Button
            if (index == 1)
                return "Heavy Ball";
        } else if (selectedIndex == 2) { // Yellow Button: Crafting
            // Mapping indices to craftable items for testing/usage
            switch (index) {
                case 0:
                    return "Ungüento Herbal"; // Mapped to Poción Herbal
                case 1:
                    return "Elixir";
                case 2:
                    return "Revivir";
                case 3:
                    return "Repelente";
                case 4:
                    return "Amuleto";
                default:
                    return null;
            }
        } else if (selectedIndex == 3) { // Brown Button
            if (index == 0)
                return "Lure";
        }
        return null; // Add logic for other categories as needed
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void dibujarMochila(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (returnScreen == null || returnScreen.getExplorador() == null)
            return;

        int columnas = 3;
        float size = 120f; // Increased size to 120
        float margen = 15f; // Increased margin slightly

        for (int i = 0; i < 6; i++) {
            float x = 400 + (i % columnas) * (size + margen);
            float y = 350 - (i / columnas) * (size + margen);

            // 1. Dibujar fondo del slot
            if (textureFondoSlot != null) {
                batch.draw(textureFondoSlot, x, y, size, size);
            }

            // 2. Dibujar marco de selección
            if (i == indexSeleccionado && whitePixel != null) {
                batch.setColor(Color.YELLOW);
                batch.draw(whitePixel, x - 2, y - 2, size + 4, size + 4);
                batch.setColor(Color.WHITE);
                if (textureFondoSlot != null) {
                    batch.draw(textureFondoSlot, x, y, size, size);
                }
            }

            // 3. Dibujar contenido
            dibujarContenido(batch, i, x, y, size);
        }

    }

    private void dibujarContenido(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, int i, float x, float y,
            float size) {
        Inventario inventario = returnScreen.getExplorador().getMochila();

        if (selectedIndex == 0) { // Red Button: Materiales (Planta, Baya, Guijarro)
            if (i == 0) { // Slot 1: Planta
                if (texPlanta != null) {
                    batch.draw(texPlanta, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getPlantas(), x + 40, y + 5);
                }
            } else if (i == 1) { // Slot 2: Baya (Requested order: planta, baya, guijarro)
                if (texBaya != null) {
                    batch.draw(texBaya, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getBayas(), x + 40, y + 5);
                }
            } else if (i == 2) { // Slot 3: Guijarro
                if (texGuijarro != null) {
                    batch.draw(texGuijarro, x + 8, y + 8, size - 16, size - 16);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getGuijarros(), x + 40, y + 5);
                }
            }
        } else if (selectedIndex == 1) { // Blue Button: Pokebolas
            if (i == 0) { // Slot 1: Pokeball
                if (texPokeball != null) {
                    batch.draw(texPokeball, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getPokeBalls(), x + 40, y + 5);
                }
            } else if (i == 1) { // Slot 2: Heavy Ball
                if (texHeavyBall != null) {
                    batch.draw(texHeavyBall, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getHeavyBalls(), x + 40, y + 5);
                }
            }
        } else if (selectedIndex == 2) { // Yellow Button: Pociones / Crafteo
            // ... existing code for potions ...
            if (i == 0) {
                if (texPocionHerbal != null) {
                    batch.draw(texPocionHerbal, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getUnguentos(), x + 40, y + 5);
                }
            } else if (i == 1) {
                if (texElixir != null) {
                    batch.draw(texElixir, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getElixires(), x + 40, y + 5);
                }
            } else if (i == 2) {
                if (texRevivir != null) {
                    batch.draw(texRevivir, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getRevivires(), x + 40, y + 5);
                }
            } else if (i == 3) {
                if (texRepelente != null) {
                    batch.draw(texRepelente, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getRepelentes(), x + 40, y + 5);
                }
            } else if (i == 4) {
                if (texAmuleto != null) {
                    batch.draw(texAmuleto, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getAmuletos(), x + 40, y + 5);
                }
            }
        } else if (selectedIndex == 3) { // Brown Button: Recursos Especiales (Lure)
            if (i == 0) {
                if (texLure != null) {
                    batch.draw(texLure, x + 2, y + 2, size - 4, size - 4);
                    if (fontContador != null)
                        fontContador.draw(batch, "x" + inventario.getLures(), x + 40, y + 5);
                }
            }
        } else if (selectedIndex == 4) { // Purple Button: Pokemon
            List<Pokemon> equipo = returnScreen.getExplorador().getEquipo();
            if (i < equipo.size()) {
                Pokemon p = equipo.get(i);
                // Draw Pokemon Name (since we only have sprites in Pokemon class which are
                // TextureRegions, and we need Textures or complex drawing here)
                // For now, drawing name mostly. If sprite is available as Texture we could draw
                // it.
                // Using a placeholder color for the pokemon slot or just name

                // Draw small name
                fontContador.setColor(Color.CYAN);
                fontContador.getData().setScale(0.7f);
                fontContador.draw(batch, p.getNombre(), x + 5, y + size - 5);

                // HP Bar in grid cell
                batch.setColor(Color.RED);
                batch.draw(whitePixel, x + 5, y + 5, size - 10, 5);
                float hpPercent = p.getHpActual() / p.getHpMaximo();
                batch.setColor(Color.GREEN);
                batch.draw(whitePixel, x + 5, y + 5, (size - 10) * hpPercent, 5);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void dibujarExplicacion(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        String titulo = "";
        String desc = "";
        boolean mostrarImagenTitulo = false;

        if (selectedIndex == 0) { // Red / Materiales
            switch (indexSeleccionado) {
                case 0:
                    // Planta Medicinal -> Show Image instead of Text
                    mostrarImagenTitulo = true;
                    titulo = "Planta Medicinal"; // Debug
                    desc = ""; // Empty description as requested
                    break;
                case 1:
                    titulo = "Baya Aranja";
                    desc = "Fruto dulce. Restaura salud.";
                    break;
                case 2:
                    titulo = "Guijarro";
                    desc = "Piedra lisa. Esencial para fabricar tus propias Poké Balls.";
                    break;
                default:
                    titulo = "";
                    desc = "";
                    break;
            }
        } else if (selectedIndex == 1) { // Blue / Pokebolas
            if (indexSeleccionado == 0) {
                titulo = "Poké Ball";
                desc = "Dispositivo para atrapar Pokémon.";
            } else if (indexSeleccionado == 1) {
                titulo = "Poké Ball de Peso";
                desc = "Mejor captura en nivel bajo. (5 Guijarros + 1 Planta)";
            } else {
                titulo = "";
                desc = "";
            }
        } else if (selectedIndex == 2) { // Yellow / Pociones
            switch (indexSeleccionado) {
                case 0:
                    titulo = "Poción Herbal";
                    desc = "Cura 20 HP. (3 Plantas + 1 Baya)";
                    break;
                case 1:
                    titulo = "Elíxir de Energía";
                    desc = "Restaura un movimiento. (2 Bayas + 2 Guijarros)";
                    break;
                case 2:
                    titulo = "Revivir Casero";
                    desc = "Revive con 50% HP. (5 Plantas + 5 Bayas + 1 Guijarro)";
                    break;
                case 3:
                    titulo = "Repelente Orgánico";
                    desc = "Evita encuentros por 100 pasos. (4 Plantas)";
                    break;
                case 4:
                    titulo = "Amuleto de Suerte";
                    desc = "Aumenta drop rate de materiales. (10 Guijarros)";
                    break;
                default:
                    titulo = "";
                    desc = "";
                    break;
            }
        } else if (selectedIndex == 3) { // Brown / Lures
            if (indexSeleccionado == 0) {
                titulo = "Cebo de Bayas (Lure)";
                desc = "Atrae Pokémon raros por 2 min. (3 Bayas + 1 Planta)";
            } else {
                titulo = "";
                desc = "";
            }
        }

        if (mostrarImagenTitulo && texMarcoVerde != null) {
            // Draw image instead of title text
            // Text is roughly at y=350, so draw image around there

            // PARA AGRANDAR EL MARCO:
            // Cambia los valores 500 (ancho) y 180 (alto) en la siguiente linea:
            // Y position changed from 340 to 200
            batch.draw(texMarcoVerde, 30, 200, 500, 160);

            // Draw description below usual place
            if (game.font != null && !desc.isEmpty()) {
                game.font.setColor(Color.WHITE);
                game.font.getData().setScale(1.0f);
                game.font.draw(batch, desc, 100, 310);
            }
        } else if (!titulo.isEmpty() && game.font != null) {
            game.font.setColor(Color.YELLOW);
            game.font.getData().setScale(1.2f);
            game.font.draw(batch, desc, 100, 310); // Description moved UP (was 110)
        } else if (selectedIndex == 4) {
            // Pokemon Details
            List<Pokemon> equipo = returnScreen.getExplorador().getEquipo();
            if (indexSeleccionado < equipo.size()) {
                Pokemon p = equipo.get(indexSeleccionado);
                titulo = p.getNombre() + " Nv." + p.getNivel();
                desc = "HP: " + (int) p.getHpActual() + "/" + (int) p.getHpMaximo() + " | Tipo: " + p.getTipo();
            } else {
                titulo = "Espacio vacío";
                desc = "No hay Pokémon en este slot.";
            }

            if (!titulo.isEmpty() && game.font != null) {
                game.font.setColor(Color.YELLOW);
                game.font.getData().setScale(1.2f);
                game.font.draw(batch, titulo, 100, 350);
                game.font.setColor(Color.WHITE);
                game.font.getData().setScale(1.0f);
                game.font.draw(batch, desc, 100, 310);
            }
        }

        // Draw Exit Instruction
        // Draw Exit Instruction - REMOVED per user request
        /*
         * if (game.font != null) {
         * game.font.setColor(Color.LIGHT_GRAY);
         * game.font.getData().setScale(1.2f);
         * game.font.draw(batch, "Presione ESC para salir", VIRTUAL_WIDTH - 350, 50);
         * game.font.setColor(Color.WHITE); // Reset
         * game.font.getData().setScale(1.0f);
         * }
         */
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
    }
}
