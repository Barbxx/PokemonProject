package com.mypokemon.game;

import java.util.TreeMap;
import java.util.Map;
import java.util.Random;

public class GestorEncuentros {
    private static final Random random = new Random();

    // Probabilidades por nivel
    private static final Map<Integer, Float> PROBABILIDADES = new TreeMap<>();
    static {
        PROBABILIDADES.put(1, 0.15f);
        PROBABILIDADES.put(2, 0.25f);
        PROBABILIDADES.put(3, 0.35f);
        PROBABILIDADES.put(4, 0.50f);
        PROBABILIDADES.put(5, 0.80f);
    }

    // Pokémon por nivel
    private static final Map<Integer, String[]> POKEMON_POR_ZONA = new TreeMap<>();
    static {
        POKEMON_POR_ZONA.put(1, new String[] { "Stantler", "Pichu", "Mime Jr.", "Chimchar", "Scyther", "Oshawott" });
        POKEMON_POR_ZONA.put(2, new String[] { "Turtwig", "Gastly", "Goomy", "Togepi", "Rowlet" });
        POKEMON_POR_ZONA.put(3, new String[] { "Growlithe H.", "Qwilfish H.", "Piplup", "Basculin Red-Striped" });
        POKEMON_POR_ZONA.put(4, new String[] { "Sneasel H.", "Gible", "Voltorb H.", "Cleffa" });
        POKEMON_POR_ZONA.put(5, new String[] { "Bergmite", "Snorunt", "Zorua H.", "Cyndaquil H." });
    }

    public static boolean verificarEncuentro(int nivel) {
        float probabilidad = PROBABILIDADES.getOrDefault(nivel, 0.10f);
        return random.nextFloat() < probabilidad;
    }

    public static String obtenerPokemonAleatorio(int nivel) {
        String[] pokemons = POKEMON_POR_ZONA.get(nivel);
        if (pokemons == null || pokemons.length == 0)
            return "Desconocido";
        return pokemons[random.nextInt(pokemons.length)];
    }
}
