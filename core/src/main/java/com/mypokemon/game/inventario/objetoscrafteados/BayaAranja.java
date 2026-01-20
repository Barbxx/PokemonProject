package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

/**
 * Baya Aranja - Cura 10 HP.
 */
public class BayaAranja extends ItemCurativo {

    public BayaAranja(int cantidad) {
        super("baya", "Baya Aranja", "Restaura 10 HP cuando la vida del Pokémon baja.", cantidad, 10);
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
