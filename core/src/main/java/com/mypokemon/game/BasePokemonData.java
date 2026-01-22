package com.mypokemon.game;

import java.util.TreeMap;
import java.util.Map;

/**
 * Alias para DatosBasePokemon - mantiene compatibilidad con código existente.
 * 
 * @deprecated Use DatosBasePokemon instead
 */
@Deprecated
public class BasePokemonData {

    private final DatosBasePokemon datos;

    private BasePokemonData(DatosBasePokemon datos) {
        this.datos = datos;
        this.descripcion = datos != null ? datos.getDescripcion() : null;
    }

    public static BasePokemonData get(String nombre) {
        DatosBasePokemon datos = DatosBasePokemon.get(nombre);
        return datos != null ? new BasePokemonData(datos) : null;
    }

    public static java.util.Set<String> getNombres() {
        return DatosBasePokemon.getNombres();
    }

    // Expose fields
    public String descripcion;

    // Getter methods
    public String getDescripcion() {
        return datos != null ? datos.getDescripcion() : null;
    }

    public String getNombre() {
        return datos != null ? datos.getNombre() : null;
    }

    public String getTipo() {
        return datos != null ? datos.getTipo() : null;
    }
}
