package com.mypokemon.game.inventario;

import com.mypokemon.game.inventario.objetoscrafteados.*;
import com.mypokemon.game.inventario.recursos.BayaAranja;
import com.mypokemon.game.inventario.recursos.Guijarro;
import com.mypokemon.game.inventario.recursos.PlantaMedicinal;

// Fábrica para crear objetos del inventario a partir de sus clases concretas.
public class ObjectFactory {

    // Crea un recurso básico (Planta, Guijarro, Baya Aranja).
    public static Recurso crearRecurso(String id, int cantidad) {
        switch (id.toLowerCase()) {
            case "planta":
                return new PlantaMedicinal(cantidad);
            case "guijarro":
                return new Guijarro(cantidad);
            case "baya":
                return new BayaAranja(cantidad);
            default:
                return new Recurso(id, id, cantidad);
        }
    }

    // Crea un objeto crafteado con la clase concreta apropiada.
    public static ItemCrafteado crearCrafteado(String id, int cantidad) {
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
                return new ObjetoCrafteadoGenerico(id, id, "Descripción no disponible.", cantidad);
        }
    }

    // Crea un objeto genérico por ID.
    public static Item crearObjeto(String id, int cantidad) {
        if (id.equals("planta") || id.equals("guijarro") || id.equals("baya"))
            return crearRecurso(id, cantidad);
        return crearCrafteado(id, cantidad);
    }

    private static class ObjetoCrafteadoGenerico extends ItemCrafteado {
        public ObjetoCrafteadoGenerico(String id, String nombre, String descripcion, int cantidad) {
            super(id, nombre, descripcion, cantidad);
        }

        @Override
        public java.util.List<String> obtenerOpciones() {
            return java.util.Arrays.asList("Tirar");
        }
    }
}
