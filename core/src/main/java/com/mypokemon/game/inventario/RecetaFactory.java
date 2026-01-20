package com.mypokemon.game.inventario;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory para crear recetas de crafteo.
 * Centraliza todas las recetas del juego.
 */
public class RecetaFactory {

    /**
     * Crea y retorna todas las recetas del juego.
     */
    public static List<Receta> crearTodasLasRecetas() {
        List<Receta> recetas = new ArrayList<>();

        // 1. Poké Ball - 2 Plantas + 3 Guijarros
        recetas.add(new Receta("pokeball", "Poké Ball", 2, 3, 0));

        // 2. Poké Ball de Peso - 1 Planta + 5 Guijarros
        recetas.add(new Receta("heavyball", "Poké Ball de Peso", 1, 5, 0));

        // 3. Poción Herbal - 3 Plantas + 1 Baya
        recetas.add(new Receta("pocion", "Poción Herbal", 3, 0, 1));

        // 4. Elíxir de Piel de Piedra - 7 Guijarros
        recetas.add(new Receta("elixir", "Elíxir de Piel de Piedra", 0, 7, 0));

        // 5. Revivir Casero - 5 Plantas + 1 Guijarro + 5 Bayas
        recetas.add(new Receta("revivir", "Revivir Casero", 5, 1, 5));

        // 6. Reproductor de música - 1 Baya
        recetas.add(new Receta("reproductor", "Reproductor de música", 0, 0, 1));

        // 7. Guante de reflejo - 13 Guijarros
        recetas.add(new Receta("guante", "Guante de reflejo cuarcítico", 0, 13, 0));

        // 8. Frijol mágico - 1 Planta
        recetas.add(new Receta("frijol", "Frijol mágico", 1, 0, 0));

        return recetas;
    }

    /**
     * Crea una receta específica por su ID.
     */
    public static Receta crearReceta(String id) {
        switch (id.toLowerCase()) {
            case "pokeball":
                return new Receta("pokeball", "Poké Ball", 2, 3, 0);
            case "heavyball":
                return new Receta("heavyball", "Poké Ball de Peso", 1, 5, 0);
            case "pocion":
                return new Receta("pocion", "Poción Herbal", 3, 0, 1);
            case "elixir":
                return new Receta("elixir", "Elíxir de Piel de Piedra", 0, 7, 0);
            case "revivir":
                return new Receta("revivir", "Revivir Casero", 5, 1, 5);
            case "reproductor":
                return new Receta("reproductor", "Reproductor de música", 0, 0, 1);
            case "guante":
                return new Receta("guante", "Guante de reflejo cuarcítico", 0, 13, 0);
            case "frijol":
                return new Receta("frijol", "Frijol mágico", 1, 0, 0);
            default:
                return null;
        }
    }
}
