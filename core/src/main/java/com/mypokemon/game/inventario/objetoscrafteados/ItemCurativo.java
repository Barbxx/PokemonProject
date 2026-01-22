package com.mypokemon.game.inventario.objetoscrafteados;

import com.mypokemon.game.Pokemon;
import com.mypokemon.game.inventario.Inventario;
import com.mypokemon.game.inventario.ResultadoUso;

// Clase abstracta base para objetos que curan salud (HP) a un Pokémon.
public abstract class ItemCurativo extends ItemConsumible {
    protected final float cantidadCuracion;

    public ItemCurativo(String id, String nombre, String descripcion, int cantidad, float cantidadCuracion) {
        super(id, nombre, descripcion, cantidad);
        this.cantidadCuracion = cantidadCuracion;
    }

    @Override
    public boolean puedeUsar(Pokemon pokemon) {
        return pokemon.obtenerHpActual() < pokemon.obtenerHpMaximo() && !pokemon.estaDebilitado();
    }

    @Override
    public ResultadoUso usar(Pokemon pokemon, Inventario inventario) {
        if (!puedeUsar(pokemon))
            return ResultadoUso.fallo("No tiene efecto.");
        pokemon.curar(cantidadCuracion);
        inventario.consumirItem(id, 1);
        return ResultadoUso.exito("Curaste a " + pokemon.obtenerNombre());
    }

    public float obtenerCantidadCuracion() {
        return cantidadCuracion;
    }
}
