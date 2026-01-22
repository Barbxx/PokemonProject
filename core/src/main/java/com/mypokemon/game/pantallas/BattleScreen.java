package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.mypokemon.game.Explorador;
import com.mypokemon.game.Pokemon;
import com.mypokemon.game.Movimiento;

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
    private Pokemon pokemonJugador; // Removed final to allow switching
    private final Pokemon pokemonEnemigo;

    // ...

    /**
     * Cambia el Pokémon activo del jugador durante la batalla
     * 
     * @param nuevo El nuevo Pokémon a usar en batalla
     */
    public void cambiarPokemon(Pokemon nuevo) {
        this.pokemonJugador = nuevo;
        updateInfo("¡Adelante " + nuevo.getNombre() + "!");

        // Reload textures for new pokemon
        try {
            if (playerBackTexture != null)
                playerBackTexture.dispose();

            String name = nuevo.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
            String path = name + " atras.png";
            if (Gdx.files.internal(path).exists()) {
                playerBackTexture = new Texture(Gdx.files.internal(path));
            } else {
                if (Gdx.files.internal(name + ".png").exists())
                    playerBackTexture = new Texture(Gdx.files.internal(name + ".png"));
            }
        } catch (Exception e) {
        }
    }

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
    private float damageTextX = 500;
    private float damageTextY = 400;
    private int selectedOption = 0; // 0: Atacar, 1: Mochila, 2: Pokemon, 3: Huir
    private boolean showMoveMenu = false;
    private boolean showPokedex = false;
    private int selectedMove = 0;
    private Texture selectedBorder;
    private int puntosInvestigacionGanados = 0;

    // Animation State
    private enum AnimState {
        NONE, THROWING, CAPTURE_CHECK
    }

    private AnimState animState = AnimState.NONE;
    private float animTimer = 0;
    private float ballX, ballY;
    private float ballTargetX = 400, ballTargetY = 350; // Default center
    private String currentBallType;

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
    private Texture statusBarTexture;
    private com.badlogic.gdx.audio.Music battleMusic;

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
            Pokemon pokemonOriginal = explorador.getEquipo().get(0);

            // Actualizar el nivel del Pokémon del jugador al nivel de investigación actual
            // de la Pokédex
            String nombrePokemon = pokemonOriginal.getNombre();
            int nivelInvestigacionActual = pokemonOriginal.getNivel(); // Nivel por defecto

            if (explorador.getRegistro().getRegistro().containsKey(nombrePokemon)) {
                nivelInvestigacionActual = explorador.getRegistro().getRegistro().get(nombrePokemon)
                        .getNivelInvestigacion();
            }

            // Recrear el Pokémon con el nivel actualizado para que las estadísticas se
            // recalculen
            this.pokemonJugador = new Pokemon(nombrePokemon, nivelInvestigacionActual, 0,
                    pokemonOriginal.isLegendario(), pokemonOriginal.getTipo());

            // Restaurar HP actual si no está en full
            float hpActualOriginal = pokemonOriginal.getHpActual();
            if (hpActualOriginal < pokemonJugador.getHpMaximo()) {
                pokemonJugador.recibirDaño(pokemonJugador.getHpMaximo() - hpActualOriginal);
            }
        } else {
            // Revertido a Piplup como se solicitó para simulación
            this.pokemonJugador = new Pokemon("Piplup", 5, 20, false, "Agua");
        }

        // Resetear modificadores temporales al iniciar batalla (Elixir)
        if (pokemonJugador != null)
            pokemonJugador.resetModificadoresTemporales();
        if (pokemonEnemigo != null)
            pokemonEnemigo.resetModificadoresTemporales();

        // Registrar avistamiento solo con encontrarlo
        explorador.getRegistro().registrarAvistamiento(enemigo.getNombre());

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
            Gdx.app.log("BattleScreen", "No se pudo cargar el fondo");
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

        selectedBorder = createColorTexture(Color.LIME);

        try {
            statusBarTexture = new Texture(Gdx.files.internal("barraPokemon.png"));
        } catch (Exception e) {
            Gdx.app.log("BattleScreen", "Could not load barraPokemon.png");
            statusBarTexture = borderBg; // Fallback
        }

        baseCircleTexture = createCircleTexture(new Color(0.3f, 0.6f, 0.2f, 0.8f));

        // Musica de batalla
        if (!pokemonEnemigo.getNombre().equalsIgnoreCase("Arceus")) {
            try {
                battleMusic = Gdx.audio.newMusic(Gdx.files.internal("batallaPokemon.mp3"));
                battleMusic.setLooping(true);
                battleMusic.setVolume(0.5f);
                battleMusic.play();
            } catch (Exception e) {
                Gdx.app.log("BattleScreen", "No se pudo cargar batallaPokemon.mp3");
            }
        }
    }

    /**
     * Crea una textura circular con el color especificado
     * 
     * @param color Color del círculo
     * @return Textura circular creada
     */
    private Texture createCircleTexture(Color color) {
        Pixmap circle = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        circle.setColor(color);
        circle.fillCircle(64, 64, 60);
        Texture t = new Texture(circle);
        circle.dispose();
        return t;
    }

    /**
     * Crea una textura de un solo píxel con el color especificado
     * 
     * @param color Color de la textura
     * @return Textura de color sólido
     */
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

    /**
     * Maneja la acción seleccionada en el menú principal de batalla
     */
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

    /**
     * Abre la pantalla de mochila durante la batalla
     */
    private void abrirMochila() {
        game.setScreen(new MochilaScreen(game, this, explorador));
    }

    /**
     * Usa un ítem durante la batalla
     * 
     * @param tipo Tipo de ítem a usar (pokeball, heavyball, pocion, etc.)
     */
    public void usarItemEnBatalla(String tipo) {
        if (tipo.equals("pokeball") || tipo.equals("heavyball")) {
            if (pokemonEnemigo.getNombre().equalsIgnoreCase("Arceus")) {
                updateInfo("¡Las Poké Balls no funcionan contra el Dios Pokémon!");
                damageText = "¡INMUNE!";
                damageTextX = 350;
                damageTextY = 400;
                damageTextTimer = 2.0f;
                return;
            }

            updateInfo("¡Usaste una " + (tipo.equals("heavyball") ? "Heavy Ball" : "Poké Ball") + "!");
            float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();
            int nivelEnemigo = pokemonEnemigo.getNivel();

            // Capture Logic
            boolean capturado = false;

            if (tipo.equals("heavyball")) {
                // Heavy Ball works better on low-level Pokémon (0-3)
                if (nivelEnemigo <= 3) {
                    // Better threshold for low-level Pokémon
                    if (hpPercent <= 0.40f)
                        capturado = true;
                } else {
                    // Standard threshold for higher-level Pokémon
                    if (hpPercent <= 0.20f)
                        capturado = true;
                }
            } else {
                // Standard Pokeball (20% threshold regardless of level)
                if (hpPercent <= 0.20f)
                    capturado = true;
            }

            if (capturado) {
                // Success!
                updateInfo("¡Captura exitosa! " + pokemonEnemigo.getNombre() + " se unió a tu equipo.");
                explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), true);
                explorador.agregarAlEquipo(pokemonEnemigo);

                // Show 'CAPTURA EXITOSA' text on screen
                damageText = "¡CAPTURA EXITOSA!";
                damageTextX = 350; // Center
                damageTextY = 400;
                damageTextTimer = 2.0f;

                endBattle(true);
            } else {
                updateInfo(
                        "¡Fallo la captura! " + (tipo.equals("heavyball") ? "¡Casi!" : "Debe estar mas debilitado."));
                // Show 'FALLÓ' text on screen
                damageText = "¡FALLÓ!";
                damageTextX = 350; // Center
                damageTextY = 400;
                damageTextTimer = 2.0f;
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

    /**
     * Verifica si la captura del Pokémon enemigo fue exitosa
     */
    private void checkCapture() {
        float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();
        int nivelEnemigo = pokemonEnemigo.getNivel();
        boolean capturado = false;

        if (currentBallType.equals("heavyball")) {
            // Heavy Ball works better on low-level Pokémon (0-3)
            if (nivelEnemigo <= 3) {
                if (hpPercent <= 0.40f)
                    capturado = true;
            } else {
                if (hpPercent <= 0.20f)
                    capturado = true;
            }
        } else {
            // Standard Pokeball (20% threshold regardless of level)
            if (hpPercent <= 0.20f)
                capturado = true;
        }

        if (capturado) {
            updateInfo("¡Captura exitosa! " + pokemonEnemigo.getNombre() + " se unió a tu equipo.");
            explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), true);
            explorador.agregarAlEquipo(pokemonEnemigo);

            // Show 'EXITOSA' text on screen
            damageText = "¡EXITOSA!";
            damageTextX = 350; // Center
            damageTextY = 400;
            damageTextTimer = 2.0f;

            endBattle(true);
        } else {
            updateInfo("¡Fallaste la captura!");
            // Show 'FALLÓ' text on screen
            damageText = "¡FALLASTE!";
            damageTextX = 350; // Center
            damageTextY = 400;
            damageTextTimer = 2.0f;
            animState = AnimState.NONE;
            currentState = BattleState.ENEMY_TURN;
            performEnemyTurnWithDelay();
        }
    }

    /**
     * Obtiene el Pokémon activo del jugador
     * 
     * @return Pokémon del jugador
     */
    public Pokemon getPokemonJugador() {
        return pokemonJugador;
    }

    /**
     * Ejecuta el turno del enemigo con un retraso para mejorar la experiencia
     * visual
     */
    private void performEnemyTurnWithDelay() {
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            performEnemyTurn();
        });
    }

    /**
     * Abre la pantalla de Pokémon para cambiar el Pokémon activo
     */
    private void abrirPokemon() {
        // Redirigir a MochilaScreen en la pestaña de Pokémon (índice 3)
        MochilaScreen mochila = new MochilaScreen(game, this, explorador);
        mochila.setSelectedIndex(3); // Método que añadiremos en MochilaScreen
        game.setScreen(mochila);
    }

    /**
     * Maneja la acción de huir de la batalla
     */
    private void handleHuir() {
        updateInfo("¡Escapaste sin problemas!");
        endBattle(false);
    }

    /**
     * Ejecuta un movimiento seleccionado por el jugador
     * 
     * @param moveIndex Índice del movimiento a ejecutar
     */
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
                damageTextX = 500; // Over Enemy
                damageTextY = 400;
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
                        damageTextX = 500; // Over Enemy
                        damageTextY = 400;
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

    /**
     * Ejecuta el turno del Pokémon enemigo
     */
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
            damageText = "-" + dano;
            damageTextX = 160;
            damageTextY = 330;
            damageTextTimer = 2.0f;
        } else {
            Movimiento mov = movimientos.get((int) (Math.random() * movimientos.size()));
            int dano = mov.ejecutar(pokemonEnemigo, pokemonJugador);
            if (dano == -1) {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ". ¡"
                        + pokemonJugador.getNombre() + " es inmune a " + mov.getTipo() + "!");
            } else if (dano > 0) {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ". Daño: " + dano);
                damageText = "-" + dano;
                damageTextX = 160; // Over Player (approx center)
                damageTextY = 330;
                damageTextTimer = 2.0f;
            } else {
                updateInfo(pokemonEnemigo.getNombre() + " usó " + mov.getNombre() + ", pero falló.");
            }
        }

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.PLAYER_TURN;
        }
    }

    /**
     * Verifica el estado de la batalla y determina si hay un ganador
     */
    private void checkBattleStatus() {
        if (pokemonEnemigo.getHpActual() <= 0) {
            updateInfo("¡" + pokemonEnemigo.getNombre() + " se debilitó!");

            // Victoria: +1 Punto Investigación para el quien vence

            // SI ES ARCEUS: El derrotado (Arceus) se completa al nivel 10 por el hito.
            if (pokemonEnemigo.getNombre().equalsIgnoreCase("Arceus")) {
                explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), false);
                puntosInvestigacionGanados = 10;
            } else {
                puntosInvestigacionGanados = 1;
            }

            // El que vence (Jugador) siempre recibe +1 por la experiencia de combate
            explorador.getRegistro().registrarAccion(pokemonJugador.getNombre(), false);

            // Recompensa de recursos
            String recursoId = Math.random() < 0.5 ? "planta" : "guijarro";
            try {
                explorador.getMochila()
                        .agregarItem(com.mypokemon.game.inventario.ItemFactory.crearRecurso(recursoId, 1));
                updateInfo("Ganaste +" + puntosInvestigacionGanados + " Inv. y encontraste 1 " + recursoId + ".");
            } catch (com.mypokemon.game.inventario.exceptions.SpaceException e) {
                updateInfo("Ganaste +" + puntosInvestigacionGanados + " Inv. pero tu inventario está lleno.");
            }

            endBattle(true);
        } else if (pokemonJugador.getHpActual() <= 0) {
            updateInfo("¡Tu Pokémon se debilitó!");

            // Derrota: Penalización (sin incremento de investigación para el enemigo)
            String perdido = explorador.getMochila().perderObjetoCrafteado();
            if (perdido != null) {
                updateInfo("Perdiste 1 " + perdido + " en la huida.");
            } else {
                updateInfo("No tenías objetos para perder.");
            }

            endBattle(false);
        }
    }

    /**
     * Actualiza el diseño de los botones según el tamaño de la ventana
     */
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

    /**
     * Finaliza la batalla y redirige a la pantalla correspondiente
     * 
     * @param victory True si el jugador ganó, false si perdió o huyó
     */
    private void endBattle(final boolean victory) {
        if (battleMusic != null) {
            battleMusic.stop();
        }
        currentState = BattleState.END_BATTLE;

        // Resetear modificadores temporales (Elixir)
        if (pokemonJugador != null)
            pokemonJugador.resetModificadoresTemporales();
        if (pokemonEnemigo != null)
            pokemonEnemigo.resetModificadoresTemporales();

        // Si derrotó a Arceus, ir a créditos finales
        if (victory && pokemonEnemigo.getNombre().equalsIgnoreCase("Arceus")) {
            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                Gdx.app.postRunnable(() -> game.setScreen(new CreditsScreen(game, explorador.getNombre())));
            });
        } else {
            // Batalla normal, volver a la pantalla anterior
            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                Gdx.app.postRunnable(() -> game.setScreen(parentScreen));
            });
        }
    }

    /**
     * Actualiza el texto de información mostrado en la batalla
     * 
     * @param text Texto a mostrar
     */
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
            float pY = 203;
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
            drawButton(btnPokemonRect, "Pokémon", selectedOption == 2);
            drawButton(btnHuirRect, "Huir", selectedOption == 3);
        }

        // Animation Logic
        if (animState == AnimState.THROWING) {
            animTimer += delta;
            float duration = 1.0f;
            float progress = Math.min(animTimer / duration, 1.0f);

            // Interpolate pos
            float currentX = ballX + (ballTargetX - ballX) * progress;
            float currentY = ballY + (ballTargetY - ballY) * progress;

            // Draw Ball (White circle with red top if possible, or just a small circle)
            game.batch.draw(baseCircleTexture, currentX - 15, currentY - 15, 30, 30); // Simple ball representation

            if (progress >= 1.0f) {
                animState = AnimState.CAPTURE_CHECK;
                // Trigger check
                checkCapture();
            }
        }

        // Mostrar texto de daño si está activo
        if (!damageText.isEmpty() && damageTextTimer > 0) {
            font.setColor(Color.RED);
            font.getData().setScale(2.0f);
            font.draw(game.batch, damageText, damageTextX, damageTextY);
            font.getData().setScale(1.0f);
        }

        drawEnemyInfo();
        game.batch.end();
    }

    /**
     * Dibuja la Pokédex en pantalla durante la batalla
     */
    private void drawPokedex() {
        // Dibujar un fondo oscuro para la pokedex
        game.batch.draw(borderBg, 50, 50, 700, 500);
        game.batch.draw(buttonBg, 60, 60, 680, 480);

        font.setColor(Color.BLACK);
        font.getData().setScale(1.2f);
        font.draw(game.batch, "POKÉDEX - Pokemon Capturados", 250, 520);
        font.getData().setScale(1.0f);

        List<Pokemon> equipo = explorador.getEquipo();
        float yOffset = 470;

        if (equipo.isEmpty()) {
            font.draw(game.batch, "No has capturado ningún Pokemon todavía.", 100, yOffset);
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

    /**
     * Dibuja el cuadro de mensajes en la batalla
     */
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

    /**
     * Dibuja la información del Pokémon enemigo y del jugador
     */
    private void drawEnemyInfo() {
        // Enemy Info (Top Left)
        float infoX = 10;
        float infoY = viewport.getWorldHeight() - 40; // Start higher for text

        font.getData().setScale(1.1f);
        font.setColor(Color.BLACK);

        // Name
        font.draw(game.batch, pokemonEnemigo.getNombre().toUpperCase(), infoX + 10, infoY);
        // Level next to name or slightly offset
        font.draw(game.batch, "Nv.Inv" + pokemonEnemigo.getNivel(), infoX + 220, infoY);

        // Status Bar BELOW the text
        // Scale down: original bar is likely large, let's make it more compact. width
        // around 240, height around 60?
        float barWidth = 260;
        float barHeight = 70;
        float barX = infoX;
        float barY = infoY - barHeight - 10; // Below text

        if (statusBarTexture != null) {
            game.batch.draw(statusBarTexture, barX, barY, barWidth, barHeight);
        }

        // HP Text inside bar area
        font.getData().setScale(0.8f);
        font.draw(game.batch, (int) pokemonEnemigo.getHpActual() + "/" + (int) pokemonEnemigo.getHpMaximo(),
                barX + 140, barY + 30); // Adjusted relative to bar

        // HP Fill
        // Need to estimate fill bar position within the scaled image
        // Assuming standard ratios, if image is scaled, internal offsets scale too.
        // Let's approximate:
        float fillWidth = 120;
        float fillHeight = 8;

        // Fine tuned for the 260x70 scale
        // Previously: barX + 118, barY + 38 for unknown size.
        // Let's try to center it visually within the "bar" part of the texture.
        game.batch.draw(hpBarFill, barX + 90, barY + 28,
                fillWidth * (pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo()), fillHeight);

        // Player Info (Bottom Right)
        float pInfoX = viewport.getWorldWidth() - 280; // Right side
        float pInfoY = 300; // Raised further to 300 as requested to clear menu

        font.getData().setScale(1.1f);
        font.draw(game.batch, pokemonJugador.getNombre().toUpperCase(), pInfoX + 10, pInfoY);
        font.draw(game.batch, "Nv.Inv" + pokemonJugador.getNivel(), pInfoX + 220, pInfoY);

        // Bar below text
        float pBarX = pInfoX;
        float pBarY = pInfoY - barHeight - 10;

        if (statusBarTexture != null) {
            game.batch.draw(statusBarTexture, pBarX, pBarY, barWidth, barHeight);
        }

        font.getData().setScale(0.8f);
        font.draw(game.batch, (int) pokemonJugador.getHpActual() + "/" + (int) pokemonJugador.getHpMaximo(),
                pBarX + 140, pBarY + 30);

        game.batch.draw(hpBarFill, pBarX + 90, pBarY + 28,
                fillWidth * (pokemonJugador.getHpActual() / pokemonJugador.getHpMaximo()), fillHeight);

        font.getData().setScale(1.0f); // Reset scale
        font.setColor(Color.BLACK); // Reset color
    }

    /**
     * Dibuja un botón en la pantalla
     * 
     * @param rect     Rectángulo que define la posición y tamaño del botón
     * @param text     Texto a mostrar en el botón
     * @param selected Si el botón está seleccionado
     */
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
        if (statusBarTexture != null)
            statusBarTexture.dispose();
        if (battleMusic != null) {
            battleMusic.dispose();
        }
    }
}
