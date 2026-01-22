package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ItemCrafteado;
import com.mypokemon.game.inventario.interfaces.IUsable;
import com.mypokemon.game.inventario.ResultadoUso;

// Clase abstracta base para todos los objetos consumibles que se pueden usar en Pokémon.
public abstract class ItemConsumible extends ItemCrafteado implements IUsable {

    public ItemConsumible(String id, String nombre, String descripcion, int cantidad) {
        super(id, nombre, descripcion, cantidad);
    }

    @Override
    public abstract boolean puedeUsar(Pokemon pokemon);

    @Override
    public abstract ResultadoUso usar(Pokemon pokemon, Inventario inventario);
}
