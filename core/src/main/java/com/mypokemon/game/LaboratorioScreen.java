package com.mypokemon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.utils.ScreenUtils;
import com.mypokemon.game.utils.BaseScreen;

public class LaboratorioScreen extends BaseScreen {
    private GameScreen gameScreen;

    public LaboratorioScreen(PokemonMain game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Black background

        game.batch.begin();
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(2.0f);
        game.font.draw(game.batch, "BIENVENIDO AL LABORATORIO", 200, 300);

        game.font.getData().setScale(1.0f);
        game.font.draw(game.batch, "Presiona [ESC] para salir", 300, 200);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
        }
    }
}
