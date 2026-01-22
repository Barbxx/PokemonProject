package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;

import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Guante de Reflejo Cuarcítico - Equipa guante para recolectar doble recurso.

public class GuanteReflejo extends ItemConsumible {

    public GuanteReflejo(int cantidad) {
        super("guante", "Guante de reflejo cuarcítico",
                "Utilizan guijarros brillantes para recolectar doble recurso.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return true; // No requiere Pokémon
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡Guante Equipado! Por 5 minutos podrás recolectar doble recurso.");
    }

    @Override
    public List<String> getOpciones() {
        return Arrays.asList("Equipar", "Tirar");
    }
}
