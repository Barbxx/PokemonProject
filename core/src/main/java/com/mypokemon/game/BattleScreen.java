package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.List;

public class BattleScreen extends ScreenAdapter {

    private final PokemonMain game;
    private final com.badlogic.gdx.Screen parentScreen;
    private final Explorador explorador;
    private final Pokemon pokemonJugador;
    private final Pokemon pokemonEnemigo;

    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;

    // UI Elements (Manual rectangles)
    private Rectangle btnAtacarRect;
    private Rectangle btnHuirRect;
    private Rectangle btnMochilaRect;
    private Rectangle btnPokemonRect;

    // Rectángulos para el menú de movimientos (cuadrícula 2x2)
    private Rectangle btnMove0Rect;
    private Rectangle btnMove1Rect;
    private Rectangle btnMove2Rect;
    private Rectangle btnMove3Rect;

    // UI State
    private String infoText = "...";
    private String damageText = ""; // Mostrar daño recibido
    private float damageTextTimer = 0;
    private int selectedOption = 0; // 0: Atacar, 1: Mochila, 2: Pokemon, 3: Huir
    private boolean showMoveMenu = false;
    private boolean showPokedex = false;
    private int selectedMove = 0;
    private Texture selectedBorder;

    // Textures
    private Texture backgroundTexture;
    private Texture enemyTexture;
    private Texture playerBackTexture;
    private Texture buttonBg;
    private Texture boxBg;
    private Texture borderBg;
    private Texture hpBarBg;
    private Texture hpBarFill;
    private Texture baseCircleTexture;

    // Battle State
    private enum BattleState {
        PLAYER_TURN, ENEMY_TURN, END_BATTLE
    }

    private BattleState currentState;

    public BattleScreen(PokemonMain game, com.badlogic.gdx.Screen parentScreen, Explorador explorador,
            Pokemon enemigo) {
        this.game = game;
        this.parentScreen = parentScreen;
        this.explorador = explorador;
        this.pokemonEnemigo = enemigo;

        if (!explorador.getEquipo().isEmpty()) {
            this.pokemonJugador = explorador.getEquipo().get(0);
        } else {
            // Revertido a Piplup como se solicitó para simulación
            this.pokemonJugador = new Pokemon("Piplup", 5, 20, false, "Agua");
        }

        this.font = new BitmapFont();
        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.viewport.apply(); // Aplicar el viewport inicial
        this.currentState = BattleState.PLAYER_TURN;

        // Load textures
        try {
            if (Gdx.files.internal("fondoBatalla.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("fondoBatalla.png"));
            } else if (Gdx.files.internal("fondoBatalla.jpg").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("fondoBatalla.jpg"));
            }
        } catch (Exception e) {
            Gdx.app.log("BattleScreen", "Could not load background");
        }

        try {
            String name = enemigo.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
            String path = name + ".png";

            if (Gdx.files.internal(path).exists()) {
                enemyTexture = new Texture(Gdx.files.internal(path));
            } else {
                enemyTexture = new Texture(Gdx.files.internal("jigglypuff.png"));
            }
        } catch (Exception e) {
            enemyTexture = createColorTexture(Color.RED);
        }

        // Load Player Back Texture
        try {
            String name = pokemonJugador.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr")
                    .replace(" ", "-");
            String path = name + " atras.png";
            if (Gdx.files.internal(path).exists()) {
                playerBackTexture = new Texture(Gdx.files.internal(path));
            } else {
                // Fallback to front texture or default
                if (Gdx.files.internal(name + ".png").exists())
                    playerBackTexture = new Texture(Gdx.files.internal(name + ".png"));
            }
        } catch (Exception e) {
        }

        buttonBg = createColorTexture(Color.WHITE); // Botones blancos
        boxBg = createColorTexture(new Color(0.15f, 0.25f, 0.45f, 1));
        borderBg = createColorTexture(new Color(0.8f, 0.7f, 0.2f, 1));
        hpBarBg = createColorTexture(Color.GRAY);
        hpBarFill = createColorTexture(Color.GREEN);
        selectedBorder = createColorTexture(Color.LIME);

        baseCircleTexture = createCircleTexture(new Color(0.3f, 0.6f, 0.2f, 0.8f));
    }

    private Texture createCircleTexture(Color color) {
        Pixmap circle = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        circle.setColor(color);
        circle.fillCircle(64, 64, 60);
        Texture t = new Texture(circle);
        circle.dispose();
        return t;
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    @Override
    public void show() {
        // Disposición de botones en cuadrícula 2x2 centrada en el cuadro de acciones
        float btnWidth = 140;
        float btnHeight = 40;
        float spacing = 15;
        // El cuadro de acciones está en el lado derecho (400 a 800)
        // Ancho total de la cuadrícula = 140 * 2 + 15 = 295
        // StartX para centrar en 400-800: 400 + (400 - 295) / 2 = 452.5
        float startX = 452.5f;
        // StartY para centrar verticalmente en relación al cuadro de mensajes
        float startY = 40;

        // Fila superior: Atacar | Mochila
        btnAtacarRect = new Rectangle(startX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMochilaRect = new Rectangle(startX + btnWidth + spacing, startY + btnHeight + spacing, btnWidth, btnHeight);

        // Fila inferior: Pokemon | Huir
        btnPokemonRect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnHuirRect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);

        // Crear rectángulos para el menú de movimientos (cuadrícula 2x2 perfecta)
        // Fila superior: Movimiento 0 | Movimiento 1
        btnMove0Rect = new Rectangle(startX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMove1Rect = new Rectangle(startX + btnWidth + spacing, startY + btnHeight + spacing, btnWidth, btnHeight);
        // Fila inferior: Movimiento 2 | Movimiento 3
        btnMove2Rect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnMove3Rect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convertir coordenadas de pantalla a coordenadas del mundo (viewport)
                // unproject espera un Vector3, pero podemos hacerlo a mano o usar
                // camera.unproject
                // La forma correcta con viewport:
                com.badlogic.gdx.math.Vector3 touchPos = new com.badlogic.gdx.math.Vector3(screenX, screenY, 0);
                viewport.unproject(touchPos);

                float x = touchPos.x;
                float y = touchPos.y;

                if (currentState == BattleState.PLAYER_TURN) {
                    if (showMoveMenu) {
                        // Menú de movimientos - usar rectángulos específicos
                        List<Movimiento> movs = pokemonJugador.getMovimientos();
                        if (btnMove0Rect.contains(x, y) && movs.size() > 0) {
                            performMove(0);
                            return true;
                        }
                        if (btnMove1Rect.contains(x, y) && movs.size() > 1) {
                            performMove(1);
                            return true;
                        }
                        if (btnMove2Rect.contains(x, y) && movs.size() > 2) {
                            performMove(2);
                            return true;
                        }
                        if (btnMove3Rect.contains(x, y) && movs.size() > 3) {
                            performMove(3);
                            return true;
                        }
                        showMoveMenu = false;
                        return true;
                    }

                    if (btnAtacarRect.contains(x, y)) {
                        selectedOption = 0;
                        handleSelectedAction();
                        return true;
                    } else if (btnHuirRect.contains(x, y)) {
                        selectedOption = 3;
                        handleHuir();
                        return true;
                    } else if (btnMochilaRect.contains(x, y)) {
                        selectedOption = 1;
                        abrirMochila();
                        return true;
                    } else if (btnPokemonRect.contains(x, y)) {
                        selectedOption = 2;
                        abrirPokemon();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }

                if (currentState == BattleState.PLAYER_TURN) {
                    if (keycode == com.badlogic.gdx.Input.Keys.B || keycode == com.badlogic.gdx.Input.Keys.X) {
                        if (showMoveMenu || showPokedex) {
                            showMoveMenu = false;
                            showPokedex = false;
                            return true;
                        }
                    }

                    if (keycode == com.badlogic.gdx.Input.Keys.K) {
                        showPokedex = !showPokedex;
                        if (showPokedex) {
                            showMoveMenu = false;
                            updateInfo("Abriendo Pokedex...");
                        }
                        return true;
                    }

                    int currentSelection = showMoveMenu ? selectedMove : selectedOption;
                    int maxMoves = pokemonJugador.getMovimientos().size();

                    if (keycode == com.badlogic.gdx.Input.Keys.UP) {
                        if (currentSelection >= 2)
                            currentSelection -= 2;
                    } else if (keycode == com.badlogic.gdx.Input.Keys.DOWN) {
                        if (currentSelection <= 1)
                            currentSelection += 2;
                    } else if (keycode == com.badlogic.gdx.Input.Keys.LEFT) {
                        if (currentSelection % 2 != 0)
                            currentSelection -= 1;
                    } else if (keycode == com.badlogic.gdx.Input.Keys.RIGHT) {
                        if (currentSelection % 2 == 0)
                            currentSelection += 1;
                    } else if (keycode == com.badlogic.gdx.Input.Keys.ENTER
                            || keycode == com.badlogic.gdx.Input.Keys.Z) {
                        if (showMoveMenu) {
                            if (selectedMove < maxMoves)
                                performMove(selectedMove);
                        } else {
                            handleSelectedAction();
                        }
                        return true;
                    }

                    if (showMoveMenu) {
                        if (currentSelection < 4)
                            selectedMove = currentSelection;
                    } else {
                        if (currentSelection <= 3)
                            selectedOption = currentSelection;
                    }
                    return true;
                }
                return false;
            }
        });

        updateInfo("¡Un " + pokemonEnemigo.getNombre() + " salvaje apareció!");
    }

    private void handleSelectedAction() {
        switch (selectedOption) {
            case 0: // Atacar
                showMoveMenu = true;
                selectedMove = 0;
                break;

            case 1: // Mochila
                abrirMochila();
                break;
            case 2: // Pokémon
                abrirPokemon();
                break;
            case 3: // Huir
                handleHuir();
                break;
        }
    }

    private void abrirMochila() {
        game.setScreen(new MochilaScreen(game, this, explorador));
    }

    public void usarItemEnBatalla(String tipo) {
        if (tipo.equals("pokeball")) {
            updateInfo("¡Usaste una Poké Ball!");
            float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();

            if (hpPercent <= 0.20f) {
                // Success!
                updateInfo("¡" + pokemonEnemigo.getNombre() + " ha sido capturado!");
                explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), true);
                explorador.agregarAlEquipo(pokemonEnemigo);
                endBattle(true);
            } else {
                updateInfo("¡El Pokémon escapó! Su HP debe estar en 20% o menos.");
                currentState = BattleState.ENEMY_TURN;
                performEnemyTurnWithDelay();
            }
        } else if (tipo.equals("pocion")) {
            updateInfo("¡Usaste una Poción! Tu Pokémon recuperó 20 PS.");
            pokemonJugador.recuperarSalud(20);
            currentState = BattleState.ENEMY_TURN;
            performEnemyTurnWithDelay();
        }
    }

    private void performEnemyTurnWithDelay() {
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            performEnemyTurn();
        });
    }

    private void abrirPokemon() {
        // Mostrar información del equipo
        updateInfo("Consultando equipo...");
        // Por ahora mostrar info básica, luego se puede crear una pantalla dedicada
        StringBuilder info = new StringBuilder("Equipo:\n");
        for (int i = 0; i < explorador.getEquipo().size(); i++) {
            Pokemon p = explorador.getEquipo().get(i);
            info.append((i + 1)).append(". ").append(p.getNombre())
                    .append(" - PS: ").append((int) p.getHpActual()).append("/").append((int) p.getHpMaximo());
            if (p.isDebilitado()) {
                info.append(" [DEBILITADO]");
            }
            info.append("\n");
        }
        updateInfo(info.toString());
    }

    private void handleHuir() {
        updateInfo("¡Escapaste sin problemas!");
        endBattle(false);
    }

    private void performMove(int moveIndex) {
        showMoveMenu = false;
        List<Movimiento> movs = pokemonJugador.getMovimientos();
        if (moveIndex >= movs.size())
            return;

        Movimiento mov = movs.get(moveIndex);

        // Determinar orden de ataque basado en velocidad
        boolean jugadorPrimero = pokemonJugador.getVelocidad() >= pokemonEnemigo.getVelocidad();

        if (jugadorPrimero) {
            // Jugador ataca primero
            int dano = mov.ejecutar(pokemonJugador, pokemonEnemigo);
            if (dano == -1) {
                updateInfo("¡" + pokemonEnemigo.getNombre() + " es inmune a " + mov.getTipo() + "!");
            } else if (dano > 0) {
                updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ". Daño: " + dano);
                damageText = "-" + dano;
                damageTextTimer = 2.0f;
            } else {
                updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ", pero falló.");
            }

            checkBattleStatus();
            if (currentState != BattleState.END_BATTLE) {
                currentState = BattleState.ENEMY_TURN;
                Gdx.app.postRunnable(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    performEnemyTurn();
                });
            }
        } else {
            // Enemigo ataca primero
            currentState = BattleState.ENEMY_TURN;
            performEnemyTurn();

            // Luego el jugador si sigue vivo
            if (currentState != BattleState.END_BATTLE) {
                Gdx.app.postRunnable(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    int dano = mov.ejecutar(pokemonJugador, pokemonEnemigo);
                    if (dano == -1) {
                        updateInfo("¡" + pokemonEnemigo.getNombre() + " es inmune a " + mov.getTipo() + "!");
                    } else if (dano > 0) {
                        updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ". Daño: " + dano);
                        damageText = "-" + dano;
                        damageTextTimer = 2.0f;
                    } else {
                        updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ", pero falló.");
                    }
                    checkBattleStatus();
                    currentState = BattleState.PLAYER_TURN;
                });
            }
        }
    }

    private void performEnemyTurn() {
        if (currentState != BattleState.ENEMY_TURN)
            return;

        // El enemigo usa un ataque aleatorio
        List<Movimiento> movimientos = pokemonEnemigo.getMovimientos();
        if (movimientos.isEmpty()) {
            // Ataque básico si no tiene movimientos
            int dano = (int) (pokemonEnemigo.getAtaque() * 0.3);
            pokemonJugador.recibirDaño(dano);
            updateInfo(pokemonEnemigo.getNombre() + " usó Placaje. Daño: " + dano);
        } else {
            Movimiento mov = movimientos.get((int) (Math.random() * movimientos.size()));
            int dano = mov.ejecutar(pokemonEnemigo, pokemonJugador);
            if (dano == -1) {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ". ¡"
                        + pokemonJugador.getNombre() + " es inmune a " + mov.getTipo() + "!");
            } else if (dano > 0) {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ". Daño: " + dano);
            } else {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ", pero falló.");
            }
        }

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.PLAYER_TURN;
        }
    }

    private void checkBattleStatus() {
        if (pokemonEnemigo.getHpActual() <= 0) {
            updateInfo("¡" + pokemonEnemigo.getNombre() + " se debilitó!");

            // Victoria: +1 Punto Investigación para el jugador
            // explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), false);
            // // REMOVED per user request
            // También investigar al propio Pokémon por la experiencia de combate
            explorador.getRegistro().registrarAccion(pokemonJugador.getNombre(), false);

            // Recompensa de recursos
            String recurso = Math.random() < 0.5 ? "planta" : "guijarro";
            explorador.getMochila().recolectarRecurso(recurso, 1);
            updateInfo("Ganaste +1 Inv. y encontraste 1 " + recurso + ".");

            endBattle(true);
        } else if (pokemonJugador.getHpActual() <= 0) {
            updateInfo("¡Tu Pokémon se debilitó!");

            // Derrota: Penalización
            String perdido = explorador.getMochila().perderObjetoCrafteado();
            if (perdido != null) {
                updateInfo("Perdiste 1 " + perdido + " en la huida.");
            } else {
                updateInfo("No tenías objetos para perder.");
            }

            endBattle(false);
        }
    }

    private void updateLayout() {
        // Actualizar posiciones de botones basándose en el nuevo tamaño del mundo
        float btnWidth = 140; // Menos largos (era 180)
        float btnHeight = 50;
        float spacing = 15;

        // El bloque se centra en la derecha
        // Ancho bloque: 140*2 + 15 = 295
        // Alto bloque: 50*2 + 15 = 115

        // Centro X = 600
        // Centro Y = 110 (Más arriba)

        float startX = 600 - (295 / 2);
        float startY = 110 - (115 / 2);

        btnAtacarRect = new Rectangle(startX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMochilaRect = new Rectangle(startX + btnWidth + spacing, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnPokemonRect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnHuirRect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);

        // Restore Move Menu Rectangles (Same layout as main menu)
        btnMove0Rect = new Rectangle(startX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMove1Rect = new Rectangle(startX + btnWidth + spacing, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMove2Rect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnMove3Rect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);
    }

    private void endBattle(final boolean victory) {
        currentState = BattleState.END_BATTLE;
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            Gdx.app.postRunnable(() -> game.setScreen(parentScreen));
        });
    }

    private void updateInfo(String text) {
        infoText = text;
        System.out.println("[BATTLE] " + text);
    }

    @Override
    public void resize(int width, int height) {
        // Actualizar el viewport cuando cambia el tamaño de la ventana
        viewport.update(width, height, true); // true centra la cámara
        updateLayout(); // Update button positions on resize
    }

    @Override
    public void render(float delta) {
        // Actualizar timer de texto de daño
        if (damageTextTimer > 0) {
            damageTextTimer -= delta;
            if (damageTextTimer < 0) {
                damageText = "";
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (backgroundTexture != null) {
            // Dibujar usando las dimensiones del mundo virtual (800x600)
            game.batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        if (enemyTexture != null) {
            float enemyX = 400;
            float enemyY = 350;
            float enemySize = 280;
            game.batch.draw(baseCircleTexture, enemyX - 20, enemyY - 40, enemySize + 40, 100);
            game.batch.draw(enemyTexture, enemyX, enemyY, enemySize, enemySize);
        }

        // Draw Player Back Sprite (Bottom Left corner area)
        if (playerBackTexture != null) {
            float pX = 20;
            float pY = 190;
            float pSize = 280;
            game.batch.draw(playerBackTexture, pX, pY, pSize, pSize);
        }

        drawMessageBox();

        if (showPokedex) {
            drawPokedex();
        } else if (showMoveMenu) {
            // Mostrar movimientos del Pokémon en cuadrícula 2x2 perfecta
            List<Movimiento> movs = pokemonJugador.getMovimientos();
            drawButton(btnMove0Rect, movs.size() > 0 ? movs.get(0).getNombre() : "-", selectedMove == 0);
            drawButton(btnMove1Rect, movs.size() > 1 ? movs.get(1).getNombre() : "-", selectedMove == 1);
            drawButton(btnMove2Rect, movs.size() > 2 ? movs.get(2).getNombre() : "-", selectedMove == 2);
            drawButton(btnMove3Rect, movs.size() > 3 ? movs.get(3).getNombre() : "-", selectedMove == 3);
        } else {
            // Menú principal de batalla (2x2 centrado)
            drawButton(btnAtacarRect, "Atacar", selectedOption == 0);
            drawButton(btnMochilaRect, "Mochila", selectedOption == 1);
            drawButton(btnPokemonRect, "Pokemon", selectedOption == 2);
            drawButton(btnHuirRect, "Huir", selectedOption == 3);
        }

        // Mostrar texto de daño si está activo
        if (!damageText.isEmpty() && damageTextTimer > 0) {
            font.setColor(Color.RED);
            font.getData().setScale(2.0f);
            font.draw(game.batch, damageText, 500, 400);
            font.getData().setScale(1.0f);
        }

        drawEnemyInfo();
        game.batch.end();
    }

    private void drawPokedex() {
        // Dibujar un fondo oscuro para la pokedex
        game.batch.draw(borderBg, 50, 50, 700, 500);
        game.batch.draw(buttonBg, 60, 60, 680, 480);

        font.setColor(Color.BLACK);
        font.getData().setScale(1.2f);
        font.draw(game.batch, "POKEDEX - Pokemon Capturados", 250, 520);
        font.getData().setScale(1.0f);

        List<Pokemon> equipo = explorador.getEquipo();
        float yOffset = 470;

        if (equipo.isEmpty()) {
            font.draw(game.batch, "No has capturado ningun Pokemon todavía.", 100, yOffset);
        } else {
            for (Pokemon p : equipo) {
                String desc = p.getNombre() + ": " + p.getDescripcion();
                // Ajuste de texto básico
                font.draw(game.batch, desc, 100, yOffset);
                yOffset -= 30;
                if (yOffset < 100)
                    break; // Límite de pantalla
            }
        }

        font.draw(game.batch, "Presiona B para cerrar", 300, 90);
    }

    private void drawMessageBox() {
        // float boxWidth = viewport.getWorldWidth();
        // float boxHeight = 160;
        // game.batch.draw(boxBg, 0, 0, boxWidth, boxHeight);
        // game.batch.draw(borderBg, 0, boxHeight, boxWidth, 4);
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        // Centrar texto en la mitad izquierda (0 a 400)
        GlyphLayout layout = new GlyphLayout(font, infoText);
        float textX = (400 - layout.width) / 2;
        // Más arriba, centrado en Y=110 aprox
        float textY = 110 + (layout.height / 2);

        font.draw(game.batch, infoText, textX, textY);
    }

    private void drawEnemyInfo() {
        float infoX = 50;
        // Posicionar relativo al borde superior del mundo virtual
        float infoY = viewport.getWorldHeight() - 110;
        float infoW = 300;
        float infoH = 80; // Increased height for better spacing

        game.batch.draw(borderBg, infoX - 2, infoY - 2, infoW + 4, infoH + 4);
        game.batch.draw(buttonBg, infoX, infoY, infoW, infoH);

        font.getData().setScale(1.1f);
        font.setColor(Color.BLACK);
        // Name at top left
        font.draw(game.batch, pokemonEnemigo.getNombre().toUpperCase(), infoX + 15, infoY + infoH - 15);
        // Level at top right
        font.draw(game.batch, "Nv" + pokemonEnemigo.getNivel(), infoX + infoW - 80, infoY + infoH - 15);

        // HP Text in middle
        font.getData().setScale(1.0f);
        font.draw(game.batch, "PS: " + (int) pokemonEnemigo.getHpActual() + "/" + (int) pokemonEnemigo.getHpMaximo(),
                infoX + 15, infoY + 45);

        // HP Bar at bottom
        game.batch.draw(hpBarBg, infoX + 15, infoY + 15, infoW - 30, 18); // Thicker bar
        float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();
        game.batch.draw(hpBarFill, infoX + 15, infoY + 15, (infoW - 30) * hpPercent, 18);

        // Draw Player Info as well
        float pInfoX = viewport.getWorldWidth() - infoX - infoW;
        float pInfoY = 240; // Raised from 180

        game.batch.draw(borderBg, pInfoX - 2, pInfoY - 2, infoW + 4, infoH + 4);
        game.batch.draw(buttonBg, pInfoX, pInfoY, infoW, infoH);

        font.getData().setScale(1.1f);
        font.setColor(Color.BLACK);
        font.draw(game.batch, pokemonJugador.getNombre().toUpperCase(), pInfoX + 15, pInfoY + infoH - 15);

        font.getData().setScale(1.0f);
        font.draw(game.batch, "PS: " + (int) pokemonJugador.getHpActual() + "/" + (int) pokemonJugador.getHpMaximo(),
                pInfoX + 15, pInfoY + 45);

        game.batch.draw(hpBarBg, pInfoX + 15, pInfoY + 15, infoW - 30, 18);
        float pHPPercent = pokemonJugador.getHpActual() / pokemonJugador.getHpMaximo();
        game.batch.draw(hpBarFill, pInfoX + 15, pInfoY + 15, (infoW - 30) * pHPPercent, 18);

        font.getData().setScale(1.0f); // Reset scale
    }

    private void drawButton(Rectangle rect, String text, boolean selected) {
        Texture border = selected ? selectedBorder : borderBg;
        game.batch.draw(border, rect.x - 4, rect.y - 4, rect.width + 8, rect.height + 8);
        game.batch.draw(buttonBg, rect.x, rect.y, rect.width, rect.height);

        font.setColor(Color.BLACK); // Texto negro para fondo blanco
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = rect.x + (rect.width - layout.width) / 2;
        float textY = rect.y + (rect.height + layout.height) / 2;
        font.draw(game.batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        font.dispose();
        if (buttonBg != null)
            buttonBg.dispose();
        if (boxBg != null)
            boxBg.dispose();
        if (borderBg != null)
            borderBg.dispose();
        if (hpBarBg != null)
            hpBarBg.dispose();
        if (hpBarFill != null)
            hpBarFill.dispose();
        if (selectedBorder != null)
            selectedBorder.dispose();
        if (baseCircleTexture != null)
            baseCircleTexture.dispose();
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (enemyTexture != null)
            enemyTexture.dispose();
        if (playerBackTexture != null)
            playerBackTexture.dispose();
    }
}
