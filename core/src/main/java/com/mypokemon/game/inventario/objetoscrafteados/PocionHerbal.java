package com.mypokemon.game.inventario.objetoscrafteados;

import java.util.Arrays;
import java.util.List;

/**
 * Poción Herbal - Cura 20 HP.
 */
public class PocionHerbal extends ItemCurativo {

    public PocionHerbal(int cantidad) {
        super("pocion", "Poción Herbal", "Cura 20 HP.", cantidad, 20);
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Curar", "Tirar");
    }
}
