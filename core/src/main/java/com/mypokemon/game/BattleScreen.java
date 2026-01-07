package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import java.util.List;
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
    private int selectedOption = 0; // 0: Atacar, 1: Mochila, 2: Pokemon, 3: Huir
    private boolean showMoveMenu = false;
    private int selectedMove = 0;
    private Texture selectedBorder;

    // Textures
    private Texture backgroundTexture;
    private Texture enemyTexture;
    private Texture playerTexture; // Nueva textura para el jugador
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
            // Por ahora, usamos a Piplup para simular la batalla como se solicitó
            this.pokemonJugador = new Pokemon("Piplup", 5, 20, false, "Agua");
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
        selectedBorder = createColorTexture(Color.LIME);

        // Crear plataforma verde (elipse)
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
                    if (showMoveMenu) {
                        List<Movimiento> movs = pokemonJugador.getMovimientos();
                        if (btnAtacarRect.contains(x, y) && movs.size() > 0) {
                            performMove(0);
                            return true;
                        }
                        if (btnMochilaRect.contains(x, y) && movs.size() > 1) {
                            performMove(1);
                            return true;
                        }
                        if (btnPokemonRect.contains(x, y) && movs.size() > 2) {
                            performMove(2);
                            return true;
                        }
                        if (btnHuirRect.contains(x, y) && movs.size() > 3) {
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
                        updateInfo("Abriendo Mochila...");
                        return true;
                    } else if (btnPokemonRect.contains(x, y)) {
                        selectedOption = 2;
                        updateInfo("Viendo Pokémon...");
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
                        if (showMoveMenu) {
                            showMoveMenu = false;
                            return true;
                        }
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
                        if (currentSelection < maxMoves)
                            selectedMove = currentSelection;
                    } else {
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
            case 0:
                showMoveMenu = true;
                selectedMove = 0;
                break;
            case 1:
                updateInfo("Abriendo Mochila...");
                break;
            case 2:
                updateInfo("Viendo Pokémon...");
                break;
            case 3:
                handleHuir();
                break;
        }
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
        int dano = mov.ejecutar(pokemonJugador, pokemonEnemigo);
        updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ".");

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

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        float bgScale = 1f;
        float bgX = 0;
        float bgY = 0;

        if (backgroundTexture != null) {
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();
            float texW = backgroundTexture.getWidth();
            float texH = backgroundTexture.getHeight();
            bgScale = Math.min(screenW / texW, screenH / texH);
            float drawnW = texW * bgScale;
            float drawnH = texH * bgScale;
            bgX = (screenW - drawnW) / 2f;
            bgY = (screenH - drawnH) / 2f;
            game.batch.draw(backgroundTexture, bgX, bgY, drawnW, drawnH);
        }

        if (enemyTexture != null) {
            float enemyRelativeX = backgroundTexture != null ? backgroundTexture.getWidth() * 0.70f : 550;
            float enemyRelativeY = backgroundTexture != null ? backgroundTexture.getHeight() * 0.42f : 300;
            float enemyW = 280;
            float enemyH = 280;
            float drawX = bgX + (enemyRelativeX * bgScale) - (enemyW / 2);
            float drawY = bgY + (enemyRelativeY * bgScale);
            game.batch.draw(enemyTexture, drawX, drawY, enemyW, enemyH);
        }

        if (playerTexture != null) {
            float playerRelativeX = backgroundTexture != null ? backgroundTexture.getWidth() * 0.28f : 200;
            float playerRelativeY = backgroundTexture != null ? backgroundTexture.getHeight() * 0.28f : 150;
            float playerW = 280;
            float playerH = 280;
            float drawX = bgX + (playerRelativeX * bgScale) - (playerW / 2);
            float drawY = bgY + (playerRelativeY * bgScale);
            game.batch.draw(playerTexture, drawX, drawY, playerW, playerH);
        }

        drawMessageBox();

        if (showMoveMenu) {
            List<Movimiento> movs = pokemonJugador.getMovimientos();
            drawButton(btnAtacarRect, movs.size() > 0 ? movs.get(0).getNombre() : "-", selectedMove == 0);
            drawButton(btnMochilaRect, movs.size() > 1 ? movs.get(1).getNombre() : "-", selectedMove == 1);
            drawButton(btnPokemonRect, movs.size() > 2 ? movs.get(2).getNombre() : "-", selectedMove == 2);
            drawButton(btnHuirRect, movs.size() > 3 ? movs.get(3).getNombre() : "-", selectedMove == 3);
        } else {
            drawButton(btnAtacarRect, "Atacar", selectedOption == 0);
            drawButton(btnMochilaRect, "Mochila", selectedOption == 1);
            drawButton(btnPokemonRect, "Pokemon", selectedOption == 2);
            drawButton(btnHuirRect, "Huir", selectedOption == 3);
        }

        drawEnemyInfo();
        game.batch.end();
    }

    private void drawMessageBox() {
        float boxWidth = Gdx.graphics.getWidth();
        float boxHeight = 160;
        float boxX = 0;
        float boxY = 0;
        game.batch.draw(boxBg, boxX, boxY, boxWidth, boxHeight);
        game.batch.draw(borderBg, 0, boxHeight, boxWidth, 4);
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(game.batch, infoText, 40, boxHeight / 2 + 10);
    }

    private void drawEnemyInfo() {
        float infoX = 50;
        float infoY = Gdx.graphics.getHeight() - 100;
        float infoW = 280;
        float infoH = 60;
        game.batch.draw(borderBg, infoX - 2, infoY - 2, infoW + 4, infoH + 4);
        game.batch.draw(buttonBg, infoX, infoY, infoW, infoH);
        font.getData().setScale(1.0f);
        font.draw(game.batch, pokemonEnemigo.getNombre().toUpperCase(), infoX + 15, infoY + infoH - 15);
        font.draw(game.batch, "Nv" + pokemonEnemigo.getNivel(), infoX + infoW - 70, infoY + infoH - 15);
        font.draw(game.batch, "PS", infoX + 15, infoY + 25);
        game.batch.draw(hpBarBg, infoX + 50, infoY + 15, 200, 12);
        float hpPercent = pokemonEnemigo.getHpActual() / pokemonEnemigo.getHpMaximo();
        game.batch.draw(hpBarFill, infoX + 50, infoY + 15, 200 * hpPercent, 12);
    }

    private void drawButton(Rectangle rect, String text, boolean selected) {
        Texture border = selected ? selectedBorder : borderBg;
        game.batch.draw(border, rect.x - 4, rect.y - 4, rect.width + 8, rect.height + 8);
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
        if (selectedBorder != null)
            selectedBorder.dispose();
        if (baseCircleTexture != null)
            baseCircleTexture.dispose();
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (enemyTexture != null)
            enemyTexture.dispose();
        if (playerTexture != null)
            playerTexture.dispose();
    }
}
