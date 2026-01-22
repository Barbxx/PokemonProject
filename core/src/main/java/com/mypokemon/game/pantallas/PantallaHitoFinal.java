package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonPrincipal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class PantallaHitoFinal extends PantallaBase {
    private Texture background;
    private GlyphLayout layout;
    private String text = "¡HAS COMPLETADO EL HITO FINAL!\n\nEl Dr. Brenner está orgulloso de tu investigación.\n\nPresiona CUALQUIER TECLA para volver al menú.";

    public PantallaHitoFinal(final PokemonPrincipal game) {
        super(game);
        try {
            background = new Texture("menu_bg.jpg");
        } catch (Exception e) {
        }
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.batch.begin();
        if (background != null) {
            game.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.WHITE);
        layout.setText(game.font, text, Color.WHITE, Gdx.graphics.getWidth() - 100, com.badlogic.gdx.utils.Align.center,
                true);
        game.font.draw(game.batch, layout, 50, Gdx.graphics.getHeight() / 2 + layout.height / 2);

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            game.setScreen(new PantallaMenuPrincipal(game));
        }
    }

    @Override
    public void dispose() {
        if (background != null)
            background.dispose();
    }
}
