package com.mypokemon.game.inventario;

import java.util.List;

/**
 * Alias para FabricaRecetas - mantiene compatibilidad con código existente.
 * 
 * @deprecated Use FabricaRecetas instead
 */
@Deprecated
public class RecetaFactory {

    public static List<Receta> crearTodasLasRecetas() {
        return FabricaRecetas.crearTodasLasRecetas();
    }

    public static Receta crearReceta(String id) {
        return FabricaRecetas.crearReceta(id);
    }
}
