package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import java.util.List;
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
    private Texture buttonBg;
    private Texture boxBg;
    private Texture borderBg;
    private Texture hpBarBg;
    private Texture hpBarFill;
    private Texture baseCircleTexture;
    private Texture playerTexture;

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
            // Cargar textura del jugador
            if (Gdx.files.internal("jugador1.png").exists()) {
                playerTexture = new Texture(Gdx.files.internal("jugador1.png"));
            } else {
                playerTexture = createColorTexture(Color.BLUE);
            }

            // Map pokemon names to texture files (e.g. "Growlithe H." -> "growlithe.png")
            String name = enemigo.getNombre().toLowerCase().replace(" h.", "").replace(" jr.", "-jr").replace(" ", "-");
            String path = name + ".png";

            if (Gdx.files.internal(path).exists()) {
                enemyTexture = new Texture(Gdx.files.internal(path));
            } else {
                enemyTexture = new Texture(Gdx.files.internal("jigglypuff.png"));
            }
        } catch (Exception e) {
            enemyTexture = createColorTexture(Color.RED);
            playerTexture = createColorTexture(Color.BLUE);
        }

        // Colores y texturas para la interfaz
        buttonBg = createColorTexture(new Color(0.1f, 0.2f, 0.4f, 1));
        boxBg = createColorTexture(new Color(0.15f, 0.25f, 0.45f, 1));
        borderBg = createColorTexture(new Color(0.8f, 0.7f, 0.2f, 1));
        hpBarBg = createColorTexture(Color.GRAY);
        hpBarFill = createColorTexture(Color.GREEN);
        selectedBorder = createColorTexture(Color.LIME);

        // Crear plataforma verde (elipse)
        baseCircleTexture = createCircleTexture(new Color(0.3f, 0.6f, 0.2f, 0.8f));
    }

    private Texture createCircleTexture(Color color) {
        int width = 256;
        int height = 128;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(width / 2, height / 2, height / 2); // fillCircle usa radio, para elipse manual es más
                                                              // complejo pero esto sirve de base
        // Para una elipse real en Pixmap tendríamos que dibujar punto a punto o usar
        // una textura externa,
        // pero fillCircle en un pixmap no cuadrado se estira si lo dibujamos así.
        // Mejor creamos un círculo y lo dibujamos estirado en el batch.
        pixmap.dispose();

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
        // Define buttons (Right side above/inside message box area)
        float btnWidth = 140;
        float btnHeight = 40;
        float spacing = 10;
        float startX = 480;
        float startY = 20;

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
                        // Selección de movimientos con clic (opcional, foco en teclado)
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

                        // Clic fuera para volver
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
                    // Tecla para volver atrás (B o X)
                    if (keycode == com.badlogic.gdx.Input.Keys.B || keycode == com.badlogic.gdx.Input.Keys.X) {
                        if (showMoveMenu) {
                            showMoveMenu = false;
                            return true;
                        }
                    }

                    // Navegación con flechas
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
                        // Limitar selección al número de movimientos existentes
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

    private void performCapture() {
        // Obsoleto según requerimientos actuales pero mantenemos lógica interna si es
        // necesario
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tecla ESC para cerrar el juego
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        game.batch.begin();

        // 1. Fondo de batalla ajustado al tamaño de la pantalla
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // 2. Imagen del Pokémon sobre el círculo verde (ampliada y pisando la base)
        if (enemyTexture != null) {
            float enemyX = 500;
            float enemyY = 220;
            float enemySize = 280; // Ampliada de 230 a 280

            // Dibujar base debajo
            game.batch.draw(baseCircleTexture, enemyX - 20, enemyY - 40, enemySize + 40, 100);
            // Dibujar Pokémon tocando/pisando la plataforma
            game.batch.draw(enemyTexture, enemyX, enemyY, enemySize, enemySize);
        }

        // 3. Recuadro de mensaje siguiendo la imagen de referencia
        drawMessageBox();

        // 4. Botones: Menú principal o Movimientos
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

        // Info del Pokémon Enemigo (Recuadro superior izquierdo según referencia)
        drawEnemyInfo();

        game.batch.end();
    }

    private void drawMessageBox() {
        float boxWidth = Gdx.graphics.getWidth() - 40;
        float boxHeight = 110;
        float boxX = 20;
        float boxY = 15;

        // Borde dorado
        game.batch.draw(borderBg, boxX - 4, boxY - 4, boxWidth + 8, boxHeight + 8);
        // Fondo azul
        game.batch.draw(boxBg, boxX, boxY, boxWidth, boxHeight);

        // Texto del mensaje
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(game.batch, infoText, boxX + 30, boxY + boxHeight - 40);
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

    private void drawButton(Rectangle rect, String text, boolean selected) {
        // Borde para botones (Resaltado si está seleccionado)
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
