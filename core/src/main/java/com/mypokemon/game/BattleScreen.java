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
import java.util.List;

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

        buttonBg = createColorTexture(new Color(0.1f, 0.2f, 0.4f, 1));
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
        float btnWidth = 140;
        float btnHeight = 40;
        float spacing = 10;
        float startX = 480;
        float startY = 20;

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
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        game.batch.begin();

        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if (enemyTexture != null) {
            float enemyX = 500;
            float enemyY = 220;
            float enemySize = 280;
            game.batch.draw(baseCircleTexture, enemyX - 20, enemyY - 40, enemySize + 40, 100);
            game.batch.draw(enemyTexture, enemyX, enemyY, enemySize, enemySize);
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
        game.batch.draw(boxBg, 0, 0, boxWidth, boxHeight);
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
    }
}
