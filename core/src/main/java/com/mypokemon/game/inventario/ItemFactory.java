package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.objetoscrafteados.*;

/**
 * Factory para crear ítems del inventario.
 * Ahora crea instancias de clases concretas específicas en lugar de genéricas.
 */
public class ItemFactory {

    /**
     * Crea un recurso básico con su nombre correcto.
     * Solo para recursos verdaderamente básicos (Planta, Guijarro).
     * Otros ítems como Baya Aranja se crean mediante crearCrafteado().
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
     * Crea un ítem crafteado con la clase concreta apropiada.
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

    /**
     * Clase interna para ítems crafteados genéricos (fallback).
     * Solo se usa si se intenta crear un ítem no reconocido.
     */
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
