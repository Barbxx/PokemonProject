package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;
import java.util.Arrays;
import java.util.List;

// Guante de Reflejo Cuarcítico - Permite recolectar el doble de recursos por un tiempo limitado.
public class GuanteReflejo extends ItemConsumible {

    public GuanteReflejo(int cantidad) {
        super("guante", "Guante de reflejo cuarcítico",
                "Utiliza guijarros brillantes para recolectar el doble de recursos.", cantidad);
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return true;
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("¡Guante Equipado! Durante 5 minutos recolectarás el doble de recursos.");
    }

    @Override
    public List<String> obtenerOpciones() {
        return Arrays.asList("Equipar", "Tirar");
    }
}
