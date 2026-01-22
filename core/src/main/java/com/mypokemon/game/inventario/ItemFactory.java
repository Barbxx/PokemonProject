package com.mypokemon.game.inventario;

/**
 * Alias para ObjectFactory - mantiene compatibilidad con código existente.
 * 
 * @deprecated Use ObjectFactory instead
 */
@Deprecated
public class ItemFactory {

    public static Recurso crearRecurso(String id, int cantidad) {
        return ObjectFactory.crearRecurso(id, cantidad);
    }

    public static ObjetoCrafteado crearCrafteado(String id, int cantidad) {
        return ObjectFactory.crearCrafteado(id, cantidad);
    }
}
