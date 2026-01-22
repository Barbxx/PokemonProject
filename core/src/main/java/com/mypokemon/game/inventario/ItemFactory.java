package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.objetoscrafteados.*;

// Factory para crear ítems del inventario.

public class ItemFactory {

    /**
     * Crea un recurso básico a partir de su ID.
     * Solo para recursos simples (Planta, Guijarro) y algunos especiales como BayaAranja.
     * 
     * @param id       Identificador del recurso.
     * @param cantidad Cantidad a crear.
     * @return Instancia de Recurso (o subclase apropiada)
     */
    public static Recurso crearRecurso(String id, int cantidad) {
        switch (id.toLowerCase()) {
            case "planta":
                return new Recurso(id, "Planta Medicinal", cantidad);
            case "guijarro":
                return new Recurso(id, "Guijarro", cantidad);
            case "baya":
                // Baya Aranja (ahora es Recurso con IUsable)
                return new BayaAranja(cantidad);
            default:
                return new Recurso(id, id, cantidad);
        }
    }

    /**
     * Crea un objeto crafteado instanciando la clase concreta correspondiente al ID.
     * 
     * @param id       Identificador del objeto (pokeball, pocion)
     * @param cantidad Cantidad a crear.
     * @return Instancia específica de ObjetoCrafteado.
     */
    public static ObjetoCrafteado crearCrafteado(String id, int cantidad) {
        switch (id.toLowerCase()) {
            case "pokeball":
                return new Pokeball(cantidad);
            case "heavyball":
                return new PokeballPesada(cantidad);
            case "pocion":
                return new PocionHerbal(cantidad);
            case "elixir":
                return new ElixirPielPiedra(cantidad);
            case "revivir":
                return new RevivirCasero(cantidad);
            case "reproductor":
                return new ReproductorMusica(cantidad);
            case "guante":
                return new GuanteReflejo(cantidad);
            case "frijol":
                return new FrijolMagico(cantidad);
            default:
                // Fallback para ítems desconocidos - crear clase genérica temporal
                return new ObjetoCrafteadoGenerico(id, id, "Descripción no disponible.", cantidad);
        }
    }

    // Clase interna para ítems crafteados genéricos (fallback).
    // Solo se usa si se intenta crear un ítem no reconocido.
    private static class ObjetoCrafteadoGenerico extends ObjetoCrafteado {
        public ObjetoCrafteadoGenerico(String id, String nombre, String descripcion, int cantidad) {
            super(id, nombre, descripcion, cantidad);
        }

        @Override
        public java.util.List<String> getOpciones() {
            return java.util.Arrays.asList("Tirar");
        }
    }
}
