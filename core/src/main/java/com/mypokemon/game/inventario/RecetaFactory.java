package com.mypokemon.game.inventario;

import java.util.ArrayList;
import java.util.List;

// Fábrica para crear recetas de fabricación. Centraliza todas las recetas del juego.
public class RecetaFactory {

    // Crea y retorna todas las recetas del juego.
    public static List<Receta> crearTodasLasRecetas() {
        List<Receta> recetas = new ArrayList<>();
        recetas.add(new Receta("pokeball", "Poké Ball", 2, 3, 0));
        recetas.add(new Receta("heavyball", "Poké Ball de Peso", 1, 5, 0));
        recetas.add(new Receta("pocion", "Poción Herbal", 3, 0, 1));
        recetas.add(new Receta("elixir", "Elíxir de Piel de Piedra", 1, 7, 0));
        recetas.add(new Receta("revivir", "Revivir Casero", 5, 1, 5));
        recetas.add(new Receta("reproductor", "Reproductor de música", 0, 9, 1));
        recetas.add(new Receta("guante", "Guante de reflejo cuarcítico", 5, 13, 0));
        recetas.add(new Receta("frijol", "Frijol mágico", 20, 20, 0));
        return recetas;
    }

    // Crea una receta específica por su ID.
    public static Receta crearReceta(String id) {
        switch (id.toLowerCase()) {
            case "pokeball":
                return new Receta("pokeball", "Poké Ball", 2, 3, 0);
            case "heavyball":
                return new Receta("heavyball", "Poké Ball de Peso", 1, 5, 0);
            case "pocion":
                return new Receta("pocion", "Poción Herbal", 3, 0, 1);
            case "elixir":
                return new Receta("elixir", "Elíxir de Piel de Piedra", 1, 7, 0);
            case "revivir":
                return new Receta("revivir", "Revivir Casero", 5, 1, 5);
            case "reproductor":
                return new Receta("reproductor", "Reproductor de música", 0, 9, 1);
            case "guante":
                return new Receta("guante", "Guante de reflejo cuarcítico", 5, 13, 0);
            case "frijol":
                return new Receta("frijol", "Frijol mágico", 20, 20, 0);
            default:
                return null;
        }
    }
}
