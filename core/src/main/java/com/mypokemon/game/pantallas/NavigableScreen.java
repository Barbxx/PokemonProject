package com.mypokemon.game.pantallas;

import com.mypokemon.game.PokemonMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

// Clase base abstracta para pantallas que soportan navegación hacia atrás.
public abstract class NavigableScreen extends BaseScreen implements INavigable {

    protected final Screen pantallaRetorno;

    public NavigableScreen(PokemonMain juego, Screen pantallaRetorno) {
        super(juego);
        this.pantallaRetorno = pantallaRetorno;
    }

    @Override
    public void navegarAtras() {
        if (pantallaRetorno != null) {
            juego.setScreen(pantallaRetorno);
            dispose();
        } else {
            Gdx.app.log(this.getClass().getSimpleName(), "Advertencia: No hay pantalla de retorno configurada");
        }
    }

    @Override
    public void navegarA(Screen pantalla) {
        juego.setScreen(pantalla);
        dispose();
    }

    @Override
    public Screen obtenerPantallaRetorno() {
        return pantallaRetorno;
    }

    // Gestiona la pulsación de la tecla ESC para navegar hacia atrás.
    protected boolean gestionarTeclaEscape() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navegarAtras();
            return true;
        }
        return false;
    }
}
