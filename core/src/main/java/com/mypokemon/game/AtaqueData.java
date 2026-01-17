package com.mypokemon.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Base de datos de todos los ataques del juego
 * Incluye ataques de daño y ataques de estado
 */
public class AtaqueData {
    public String nombre;
    public int poder; // 0 para ataques de estado
    public String tipo;
    public int precision;
    public boolean esEstado; // true si es un ataque de estado (no hace daño directo)
    public String efecto; // Descripción del efecto de estado

    public AtaqueData(String nombre, int poder, String tipo, int precision, boolean esEstado, String efecto) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
        this.esEstado = esEstado;
        this.efecto = efecto;
    }

    private static final Map<String, AtaqueData> DATABASE = new HashMap<>();

    static {
        // Ataques Normales
        DATABASE.put("Placaje", new AtaqueData("Placaje", 11, "Normal", 100, false, ""));
        DATABASE.put("Arañazo", new AtaqueData("Arañazo", 12, "Normal", 100, false, ""));
        DATABASE.put("Latigazo", new AtaqueData("Latigazo", 10, "Normal", 100, false, ""));
        DATABASE.put("Destructor", new AtaqueData("Destructor", 10, "Normal", 100, false, ""));
        DATABASE.put("Picotazo", new AtaqueData("Picotazo", 11, "Normal", 100, false, ""));
        DATABASE.put("At. Rápido", new AtaqueData("At. Rápido", 16, "Normal", 100, false, ""));
        DATABASE.put("Falsotortazo", new AtaqueData("Falsotortazo", 12, "Normal", 100, false, ""));
        DATABASE.put("Sentencia", new AtaqueData("Sentencia", 17, "Normal", 100, false, ""));
        DATABASE.put("Paz Mental", new AtaqueData("Paz Mental", 14, "Normal", 100, false, ""));
        DATABASE.put("Recuperación", new AtaqueData("Recuperación", 15, "Normal", 100, true, "Recupera HP"));
        DATABASE.put("Hiperrayo", new AtaqueData("Hiperrayo", 13, "Normal", 90, false, ""));

        // Ataques Psíquicos
        DATABASE.put("Confusión", new AtaqueData("Confusión", 18, "Psíquico", 100, false, ""));
        DATABASE.put("Copión", new AtaqueData("Copión", 0, "Normal", 100, true, "Copia el último movimiento usado"));

        // Ataques Eléctricos
        DATABASE.put("Impactrueno", new AtaqueData("Impactrueno", 12, "Eléctrico", 100, false, ""));
        DATABASE.put("Trueno", new AtaqueData("Trueno", 25, "Eléctrico", 70, false, ""));
        DATABASE.put("Chispa", new AtaqueData("Chispa", 18, "Eléctrico", 100, false, ""));

        // Ataques de Fuego
        DATABASE.put("Ascuas", new AtaqueData("Ascuas", 15, "Fuego", 100, false, ""));
        DATABASE.put("Rueda Fuego", new AtaqueData("Rueda Fuego", 13, "Fuego", 100, false, ""));
        DATABASE.put("Humareda", new AtaqueData("Humareda", 17, "Fuego", 100, false, ""));

        // Ataques de Bicho
        DATABASE.put("Aire Afilado", new AtaqueData("Aire Afilado", 20, "Volador", 95, false, ""));

        // Ataques de Fantasma
        DATABASE.put("Impresionar", new AtaqueData("Impresionar", 10, "Fantasma", 100, false, ""));
        DATABASE.put("Polución", new AtaqueData("Polución", 15, "Veneno", 100, false, ""));
        DATABASE.put("Infortunio", new AtaqueData("Infortunio", 25, "Fantasma", 100, false, ""));
        DATABASE.put("Rencor", new AtaqueData("Rencor", 18, "Fantasma", 100, false, ""));
        DATABASE.put("Sombra Vil", new AtaqueData("Sombra Vil", 16, "Fantasma", 100, false, ""));

        // Ataques de Agua
        DATABASE.put("Burbuja", new AtaqueData("Burbuja", 10, "Agua", 100, false, ""));
        DATABASE.put("Aqua Jet", new AtaqueData("Aqua Jet", 16, "Agua", 100, false, ""));

        // Ataques de Dragón
        DATABASE.put("Ácido", new AtaqueData("Ácido", 12, "Veneno", 100, false, ""));
        DATABASE.put("Absorber", new AtaqueData("Absorber", 10, "Planta", 100, false, "Absorbe HP"));
        DATABASE.put("Dragoaliento", new AtaqueData("Dragoaliento", 20, "Dragón", 100, false, ""));

        // Ataques de Hada
        DATABASE.put("Beso Drenaje", new AtaqueData("Beso Drenaje", 15, "Hada", 100, false, "Absorbe HP"));
        DATABASE.put("B. Drenaje", new AtaqueData("B. Drenaje", 14, "Hada", 100, false, "Absorbe HP"));

        // Ataques de Planta
        DATABASE.put("H. Afilada", new AtaqueData("H. Afilada", 18, "Planta", 100, false, ""));
        DATABASE.put("Hoja Afilada", new AtaqueData("Hoja Afilada", 16, "Planta", 100, false, ""));
        DATABASE.put("Follaje", new AtaqueData("Follaje", 14, "Planta", 100, false, ""));

        // Ataques de Veneno/Siniestro
        DATABASE.put("Púas Tóxicas", new AtaqueData("Púas Tóxicas", 0, "Veneno", 100, true, "Envenena al objetivo"));
        DATABASE.put("P. Tóxicas", new AtaqueData("P. Tóxicas", 0, "Veneno", 100, true, "Envenena al objetivo"));
        DATABASE.put("Pin Misil", new AtaqueData("Pin Misil", 18, "Bicho", 95, false, ""));
        DATABASE.put("Gas Venenoso", new AtaqueData("Gas Venenoso", 0, "Veneno", 90, true, "Envenena al objetivo"));
        DATABASE.put("Mordisco", new AtaqueData("Mordisco", 18, "Siniestro", 100, false, ""));

        // Ataques de Hielo
        DATABASE.put("Polvo Nieve", new AtaqueData("Polvo Nieve", 15, "Hielo", 100, false, ""));
        DATABASE.put("P. Nieve", new AtaqueData("P. Nieve", 15, "Hielo", 100, false, ""));
        DATABASE.put("R. Hielo", new AtaqueData("R. Hielo", 20, "Hielo", 90, false, ""));

        // Ataques de Estado
        DATABASE.put("Hipnosis", new AtaqueData("Hipnosis", 0, "Psíquico", 60, true, "Duerme al objetivo"));
        DATABASE.put("Onda Trueno", new AtaqueData("Onda Trueno", 0, "Eléctrico", 90, true, "Paraliza al objetivo"));
        DATABASE.put("Dulce Aroma", new AtaqueData("Dulce Aroma", 0, "Normal", 100, true, "Reduce evasión"));
        DATABASE.put("Refugio", new AtaqueData("Refugio", 0, "Agua", 100, true, "Aumenta Defensa"));
        DATABASE.put("Gruñido", new AtaqueData("Gruñido", 0, "Normal", 100, true, "Reduce Ataque"));
        DATABASE.put("Arena Arena", new AtaqueData("Arena Arena", 0, "Tierra", 100, true, "Reduce Precisión"));
        DATABASE.put("Carga", new AtaqueData("Carga", 0, "Eléctrico", 100, true, "Aumenta Def. Especial"));
        DATABASE.put("Encanto", new AtaqueData("Encanto", 0, "Hada", 100, true, "Reduce Ataque"));
        DATABASE.put("Fortaleza", new AtaqueData("Fortaleza", 0, "Normal", 100, true, "Aumenta Defensa"));
        DATABASE.put("Mofa", new AtaqueData("Mofa", 0, "Siniestro", 100, true, "Provoca al objetivo"));
    }

    public static AtaqueData get(String nombre) {
        return DATABASE.get(nombre);
    }

    public static boolean existe(String nombre) {
        return DATABASE.containsKey(nombre);
    }
}
