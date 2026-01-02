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
import com.badlogic.gdx.utils.ScreenUtils;

public class BattleScreen extends ScreenAdapter {

    private final PokemonMain game;
    private final com.badlogic.gdx.Screen parentScreen;
    private final Explorador explorador;
    private final Pokemon pokemonJugador;
    private final Pokemon pokemonEnemigo;

    private BitmapFont font;

    // UI Elements (Manual rectangles)
    private Rectangle btnAtacarRect;
    private Rectangle btnCapturarRect;
    private Rectangle btnHuirRect;

    // UI State
    private String infoText = "...";

    // Textures
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture buttonBg;

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
        this.currentState = BattleState.PLAYER_TURN;

        // Load textures
        try {
            playerTexture = new Texture(Gdx.files.internal("jugador1.png"));
            enemyTexture = new Texture(Gdx.files.internal("jigglypuff.png"));
        } catch (Exception e) {
            playerTexture = createColorTexture(Color.BLUE);
            enemyTexture = createColorTexture(Color.RED);
        }
        buttonBg = createColorTexture(Color.DARK_GRAY);
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
        // Define buttons (bottom of screen)
        float btnWidth = 150;
        float btnHeight = 50;
        float spacing = 20;
        float startX = 50;
        float startY = 30;

        btnAtacarRect = new Rectangle(startX, startY, btnWidth, btnHeight);
        btnCapturarRect = new Rectangle(startX + btnWidth + spacing, startY, btnWidth, btnHeight);
        btnHuirRect = new Rectangle(startX + (btnWidth + spacing) * 2, startY, btnWidth, btnHeight);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen Y to Gdx coordinate Y (bottom-up)
                float y = Gdx.graphics.getHeight() - screenY;
                float x = screenX;

                if (currentState == BattleState.PLAYER_TURN) {
                    if (btnAtacarRect.contains(x, y)) {
                        performPlayerAttack();
                        return true;
                    } else if (btnCapturarRect.contains(x, y)) {
                        performCapture();
                        return true;
                    } else if (btnHuirRect.contains(x, y)) {
                        updateInfo("¡Escapaste sin problemas!");
                        endBattle(false);
                        return true;
                    }
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
            updateInfo(pokemonJugador.getNombre() + " usó " + mov.getNombre() + ". Daño: " + dano);
        } else {
            dano = 5;
            pokemonEnemigo.recibirDaño((float) dano);
            updateInfo(pokemonJugador.getNombre() + " atacó. Daño: " + dano);
        }

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.ENEMY_TURN;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
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
        updateInfo("Enemigo atacó. " + pokemonJugador.getNombre() + " recibio " + dano + " daño.");

        checkBattleStatus();
        if (currentState != BattleState.END_BATTLE) {
            currentState = BattleState.PLAYER_TURN;
        }
    }

    private void performCapture() {
        Inventario inv = explorador.getMochila();
        if (inv.getPokeBalls() > 0) {
            boolean capturado = pokemonEnemigo.intentarCaptura(1.0f);
            if (capturado) {
                updateInfo("¡" + pokemonEnemigo.getNombre() + " fue capturado!");
                explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), true);
                explorador.agregarPokemonEquipo(pokemonEnemigo);
                endBattle(true);
            } else {
                updateInfo("¡La captura falló!");
                currentState = BattleState.ENEMY_TURN;
                performEnemyTurn();
            }
        } else {
            updateInfo("¡No tienes Poké Balls!");
        }
    }

    private void checkBattleStatus() {
        if (pokemonEnemigo.getHpActual() <= 0) {
            updateInfo("¡" + pokemonEnemigo.getNombre() + " se debilitó!");
            explorador.getRegistro().registrarAccion(pokemonEnemigo.getNombre(), false);
            endBattle(true);
        } else if (pokemonJugador.getHpActual() <= 0) {
            updateInfo("¡" + pokemonJugador.getNombre() + " se debilitó! Fin del juego...");
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
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        // Draw Pokemon
        game.batch.draw(playerTexture, 100, 150, 200, 200);
        game.batch.draw(enemyTexture, Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 300, 200, 200);

        // Draw HUD Text
        font.draw(game.batch, pokemonEnemigo.getNombre() + " Lvl:" + pokemonEnemigo.getNivel(),
                Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 80);
        font.draw(game.batch, "HP: " + (int) pokemonEnemigo.getHpActual() + "/" + (int) pokemonEnemigo.getHpMaximo(),
                Gdx.graphics.getWidth() - 300, Gdx.graphics.getHeight() - 100);

        font.draw(game.batch, pokemonJugador.getNombre() + " Lvl:" + pokemonJugador.getNivel(),
                100, 130);
        font.draw(game.batch, "HP: " + (int) pokemonJugador.getHpActual() + "/" + (int) pokemonJugador.getHpMaximo(),
                100, 110);

        // Draw UI
        font.draw(game.batch, infoText, 50, 100);

        drawButton(btnAtacarRect, "Atacar");
        drawButton(btnCapturarRect, "Capturar");
        drawButton(btnHuirRect, "Huir");

        game.batch.end();
    }

    private void drawButton(Rectangle rect, String text) {
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
        if (playerTexture.getWidth() == 1)
            playerTexture.dispose();
        if (enemyTexture.getWidth() == 1)
            enemyTexture.dispose();
    }
}
