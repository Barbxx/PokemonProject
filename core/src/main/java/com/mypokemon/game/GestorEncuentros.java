package com.mypokemon.game;

import java.util.TreeMap;
import java.util.Map;
import java.util.Random;

// Gestiona la lógica de encuentros con Pokémon salvajes en el mapa.
public class GestorEncuentros {
    private static final Random azar = new Random();

    private static final Map<Integer, Float> PROBABILIDADES = new TreeMap<>();
    static {
        PROBABILIDADES.put(1, 0.25f);
        PROBABILIDADES.put(2, 0.40f);
        PROBABILIDADES.put(3, 0.55f);
        PROBABILIDADES.put(4, 0.75f);
        PROBABILIDADES.put(5, 0.90f);
    }

    private static final Map<Integer, String[]> POKEMON_POR_ZONA = new TreeMap<>();
    static {
        POKEMON_POR_ZONA.put(1, new String[] { "Stantler", "Pichu", "Mime Jr.", "Chimchar", "Scyther", "Oshawott" });
        POKEMON_POR_ZONA.put(2, new String[] { "Turtwig", "Gastly", "Goomy", "Togepi", "Rowlet" });
        POKEMON_POR_ZONA.put(3, new String[] { "Growlithe H.", "Qwilfish H.", "Piplup", "Basculin Red-Striped" });
        POKEMON_POR_ZONA.put(4, new String[] { "Sneasel H.", "Gible", "Voltorb H.", "Cleffa" });
        POKEMON_POR_ZONA.put(5, new String[] { "Bergmite", "Snorunt", "Zorua H.", "Cyndaquil H." });
    }

    // Verifica aleatoriamente si ocurre un encuentro basado en el nivel de la zona.
    public static boolean verificarEncuentro(int nivel) {
        float probabilidad = PROBABILIDADES.getOrDefault(nivel, 0.10f);
        return azar.nextFloat() < probabilidad;
    }

    // Selecciona un Pokémon aleatorio disponible para el nivel de zona dado.
    public static String obtenerPokemonAleatorio(int nivel) {
        String[] pokemones = POKEMON_POR_ZONA.get(nivel);
        if (pokemones == null || pokemones.length == 0)
            return "Desconocido";
        return pokemones[azar.nextInt(pokemones.length)];
    }
}
