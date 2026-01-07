package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

public class BattleScreen extends ScreenAdapter {

    private final PokemonMain game;
    private final com.badlogic.gdx.Screen parentScreen;
    private final Explorador explorador;
    private final Pokemon pokemonJugador;
    private final Pokemon pokemonEnemigo;

    private BitmapFont font;
    private OrthographicCamera camera;

    // UI Elements (Manual rectangles)
    private Rectangle btnAtacarRect;
    private Rectangle btnHuirRect;
    private Rectangle btnMochilaRect;
    private Rectangle btnPokemonRect;

    // UI State
    private String infoText = "...";

    // Textures
    private Texture backgroundTexture;
    private Texture enemyTexture;
    private Texture playerTexture; // Nueva textura para el jugador
    private Texture buttonBg;
    private Texture boxBg;
    private Texture borderBg;
    private Texture hpBarBg;
    private Texture hpBarFill;

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
            this.pokemonJugador = new Pokemon("Partner", 5, 20, false, "Normal");
        }

        this.font = new BitmapFont();
        this.camera = new OrthographicCamera();
        this.currentState = BattleState.PLAYER_TURN;

        // Load textures
        try {
            // Se debe abrir el archivo "fondoBatalla.png"
            if (Gdx.files.internal("fondoBatalla.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("fondoBatalla.png"));
            } else if (Gdx.files.internal("fondoBatalla.jpg").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("fondoBatalla.jpg"));
            }
        } catch (Exception e) {
            Gdx.app.log("BattleScreen", "Could not load background");
        }

        try {
            // Map pokemon names to texture files (e.g. "Growlithe H." -> "growlithe.png")
            String name = enemigo.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
            String path = name + ".png";

            if (Gdx.files.internal(path).exists()) {
                enemyTexture = new Texture(Gdx.files.internal(path));
            } else {
                // Si no existe, dejar null o usar un placeholder invisible
                enemyTexture = null;
            }
        } catch (Exception e) {
            enemyTexture = createColorTexture(Color.RED);
        }

        try {
            // Cargar textura del jugador (vista trasera)
            // Aplicar la misma logica de reemplazo que para el enemigo para asegurar
            // compatibilidad con todos
            String rawName = pokemonJugador.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr")
                    .replace(" ", "-");
            // Intentar cargar "nombre atras.png"
            String backPath = rawName + " atras.png";

            if (Gdx.files.internal(backPath).exists()) {
                playerTexture = new Texture(Gdx.files.internal(backPath));
            } else {
                // Fallback a vista frontal si no existe trasera
                String frontPath = rawName + ".png";
                if (Gdx.files.internal(frontPath).exists()) {
                    playerTexture = new Texture(Gdx.files.internal(frontPath));
                } else {
                    // Fallback final: dejar null si no hay imagen
                    playerTexture = null;
                }
            }
        } catch (Exception e) {
            playerTexture = createColorTexture(Color.BLUE);
        }

        // Colores para la interfaz según referencia
        buttonBg = createColorTexture(new Color(0.1f, 0.2f, 0.4f, 1)); // Azul oscuro para botones
        boxBg = createColorTexture(new Color(0.15f, 0.25f, 0.45f, 1)); // Azul para el recuadro
        borderBg = createColorTexture(new Color(0.8f, 0.7f, 0.2f, 1)); // Dorado/Amarillo para el borde
        hpBarBg = createColorTexture(Color.GRAY);
        hpBarFill = createColorTexture(Color.GREEN);
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
        // Define buttons (Right side inside message box area)
        float btnWidth = 220; // Más grandes
        float btnHeight = 50; // Más altos
        float spacing = 15; // Más espaciado

        // Calcular startX para que queden a la derecha con un margen
        float rightMargin = 50;
        float startX = Gdx.graphics.getWidth() - (btnWidth * 2 + spacing) - rightMargin;
        float startY = 25; // Un poco más arriba dentro de la caja de 160px

        // Botones requeridos: Atacar, Huir, Mochila, Pokémon
        btnAtacarRect = new Rectangle(startX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnMochilaRect = new Rectangle(startX + btnWidth + spacing, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnPokemonRect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnHuirRect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Gdx.graphics.getHeight() - screenY;
                float x = screenX;

                if (currentState == BattleState.PLAYER_TURN) {
                    if (btnAtacarRect.contains(x, y)) {
                        performPlayerAttack();
                        return true;
                    } else if (btnHuirRect.contains(x, y)) {
                        updateInfo("¡Escapaste sin problemas!");
                        endBattle(false);
                        return true;
                    } else if (btnMochilaRect.contains(x, y)) {
                        updateInfo("Abriendo Mochila...");
                        // Implementación futura
                        return true;
                    } else if (btnPokemonRect.contains(x, y)) {
                        updateInfo("Viendo Pokémon...");
                        // Implementación futura
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
                return false;
            }
        });

        updateInfo("¡Un " + pokemonEnemigo.getNombre() + " salvaje apareció!");
    }

    private void performPlayerAttack() {
        int dano = 0;
        if (!pokemonJugador.getMovimientos().isEmpty()) {
            Movimiento mov = pokemonJugador.getMovimientos().get(0);
            dano = mov.ejecutar(pokemonJugador, pokemonEnemigo);
            updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ".");
        } else {
            dano = 5;
            pokemonEnemigo.recibirDaño((float) dano);
            updateInfo(pokemonJugador.getNombre() + " atacó.");
        }

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.ENEMY_TURN;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    performEnemyTurn();
                }
            });
        }
    }

    private void performEnemyTurn() {
        if (currentState != BattleState.ENEMY_TURN)
            return;

        int dano = 5;
        pokemonJugador.recibirDaño(dano);
        updateInfo("El enemigo atacó.");

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.PLAYER_TURN;
        }
    }

    private void checkBattleStatus() {
        if (pokemonEnemigo.getHpActual() <= 0) {
            updateInfo("¡" + pokemonEnemigo.getNombre() + " se debilitó!");
            explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), false);
            endBattle(true);
        } else if (pokemonJugador.getHpActual() <= 0) {
            updateInfo("¡Tu Pokémon se debilitó!");
            endBattle(false);
        }
    }

    private void endBattle(final boolean victory) {
        currentState = BattleState.END_BATTLE;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                Gdx.app.postRunnable(() -> game.setScreen(parentScreen));
            }
        });
    }

    private void updateInfo(String text) {
        infoText = text;
        System.out.println("[BATTLE] " + text);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tecla ESC para cerrar el juego
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Variables para la posición del fondo (calculadas previamente)
        float bgScale = 1f;
        float bgX = 0;
        float bgY = 0;

        // 1. Fondo de batalla dinámico (Aspect Fit)
        if (backgroundTexture != null) {
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();
            float texW = backgroundTexture.getWidth();
            float texH = backgroundTexture.getHeight();

            // Escala para ajustar la imagen completa en pantalla (sin recortes, manteniendo
            // proporción)
            bgScale = Math.min(screenW / texW, screenH / texH);

            float drawnW = texW * bgScale;
            float drawnH = texH * bgScale;
            bgX = (screenW - drawnW) / 2f;
            bgY = (screenH - drawnH) / 2f;

            game.batch.draw(backgroundTexture, bgX, bgY, drawnW, drawnH);
        }

        // 2. Imagen del Pokémon Enemigo sobre el círculo verde derecho (Arriba a la
        // derecha)
        // Posición relativa al tamaño original de la imagen de fondo (ej. 75% ancho,
        // 40% alto)
        if (enemyTexture != null) {
            // Ajustar estos factores (0.75f, 0.4f) según la ubicación exacta del círculo en
            // la imagen
            float enemyRelativeX = backgroundTexture != null ? backgroundTexture.getWidth() * 0.70f : 550; // Ajustado a
                                                                                                           // 70%
            float enemyRelativeY = backgroundTexture != null ? backgroundTexture.getHeight() * 0.42f : 300; // Ajustado
                                                                                                            // a 42%

            // Mejor fijo o poco escalado, pero probemos fijo por ahora o semi-escalado:
            // Si el fondo se hace muy pequeño, el pokemon debe achicarse.
            float enemyW = 280; // Aumentado (antes 180)
            float enemyH = 280;

            // Coordenadas en pantalla
            float drawX = bgX + (enemyRelativeX * bgScale) - (enemyW / 2);
            float drawY = bgY + (enemyRelativeY * bgScale);

            game.batch.draw(enemyTexture, drawX, drawY, enemyW, enemyH);
        }

        // 2.5. Imagen del Pokémon Jugador (Abajo a la izquierda)
        if (playerTexture != null) {
            // Posición relativa para el círculo izquierdo
            float playerRelativeX = backgroundTexture != null ? backgroundTexture.getWidth() * 0.28f : 200; // Ajustado
                                                                                                            // a 28%
            float playerRelativeY = backgroundTexture != null ? backgroundTexture.getHeight() * 0.28f : 150; // Ajustado
                                                                                                             // a 28%

            float playerW = 280;
            float playerH = 280;

            // Coordenadas en pantalla
            float drawX = bgX + (playerRelativeX * bgScale) - (playerW / 2);
            float drawY = bgY + (playerRelativeY * bgScale);

            game.batch.draw(playerTexture, drawX, drawY, playerW, playerH);
        }

        // 3. Recuadro de mensaje siguiendo la imagen de referencia
        drawMessageBox();

        // 4. Botones: Atacar, Huir, Mochila, Pokémon
        drawButton(btnAtacarRect, "Atacar");
        drawButton(btnMochilaRect, "Mochila");
        drawButton(btnPokemonRect, "Pokemon");
        drawButton(btnHuirRect, "Huir");

        // Info del Pokémon Enemigo (Recuadro superior izquierdo según referencia)
        drawEnemyInfo();

        game.batch.end();
    }

    private void drawMessageBox() {
        float boxWidth = Gdx.graphics.getWidth();
        float boxHeight = 160; // Aumentado para tapar el fondo
        float boxX = 0;
        float boxY = 0;

        // Fondo azul de borde a borde para "cortar" el círculo inferior
        game.batch.draw(boxBg, boxX, boxY, boxWidth, boxHeight);

        // Borde dorado superior
        game.batch.draw(borderBg, 0, boxHeight, boxWidth, 4);

        // Texto del mensaje desplazado a la izquierda
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(game.batch, infoText, 40, boxHeight / 2 + 10);
    }

    private void drawEnemyInfo() {
        float infoX = 50;
        float infoY = Gdx.graphics.getHeight() - 100;
        float infoW = 280;
        float infoH = 60;

        // Recuadro de info (Estilo similar al de referencia)
        game.batch.draw(borderBg, infoX - 2, infoY - 2, infoW + 4, infoH + 4);
        game.batch.draw(buttonBg, infoX, infoY, infoW, infoH);

        font.getData().setScale(1.0f);
        font.draw(game.batch, pokemonEnemigo.getNombre().toUpperCase(), infoX + 15, infoY + infoH - 15);

        // Nivel 0 indicado como solicitado
        font.draw(game.batch, "Nv" + pokemonEnemigo.getNivel(), infoX + infoW - 70, infoY + infoH - 15);

        // Barra de PS (HP)
        font.draw(game.batch, "PS", infoX + 15, infoY + 25);
        // Barra de fondo
        game.batch.draw(hpBarBg, infoX + 50, infoY + 15, 200, 12);
        // Barra de vida (Verde)
        float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();
        game.batch.draw(hpBarFill, infoX + 50, infoY + 15, 200 * hpPercent, 12);
    }

    private void drawButton(Rectangle rect, String text) {
        // Borde para botones
        game.batch.draw(borderBg, rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4);
        game.batch.draw(buttonBg, rect.x, rect.y, rect.width, rect.height);

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
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (enemyTexture != null)
            enemyTexture.dispose();
        if (playerTexture != null)
            playerTexture.dispose();
    }
}
