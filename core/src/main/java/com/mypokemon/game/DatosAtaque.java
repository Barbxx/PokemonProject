package com.mypokemon.game;

import java.util.TreeMap;
import java.util.Map;

/**
 * Base de datos de todos los ataques del juego
 * Incluye ataques de daño y ataques de estado
 */
public class DatosAtaque {
    public String nombre;
    public int poder; // 0 para ataques de estado
    public String tipo;
    public int precision;
    public boolean esEstado; // true si es un ataque de estado (no hace daño directo)
    public String efecto; // Descripción del efecto de estado

    public DatosAtaque(String nombre, int poder, String tipo, int precision, boolean esEstado, String efecto) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
        this.esEstado = esEstado;
        this.efecto = efecto;
    }

    private static final Map<String, DatosAtaque> DATABASE = new TreeMap<>();

    static {
        // Ataques Normales
        DATABASE.put("Placaje", new DatosAtaque("Placaje", 11, "Normal", 100, false, ""));
        DATABASE.put("Arañazo", new DatosAtaque("Arañazo", 12, "Normal", 100, false, ""));
        DATABASE.put("Latigazo", new DatosAtaque("Latigazo", 10, "Normal", 100, false, ""));
        DATABASE.put("Destructor", new DatosAtaque("Destructor", 10, "Normal", 100, false, ""));
        DATABASE.put("Picotazo", new DatosAtaque("Picotazo", 11, "Normal", 100, false, ""));
        DATABASE.put("At. Rápido", new DatosAtaque("At. Rápido", 16, "Normal", 100, false, ""));
        DATABASE.put("Falsotortazo", new DatosAtaque("Falsotortazo", 12, "Normal", 100, false, ""));
        DATABASE.put("Sentencia", new DatosAtaque("Sentencia", 17, "Normal", 100, false, ""));
        DATABASE.put("Paz Mental", new DatosAtaque("Paz Mental", 14, "Normal", 100, false, ""));
        DATABASE.put("Recuperación", new DatosAtaque("Recuperación", 15, "Normal", 100, true, "Recupera HP"));
        DATABASE.put("Hiperrayo", new DatosAtaque("Hiperrayo", 13, "Normal", 90, false, ""));

        // Ataques Psíquicos
        DATABASE.put("Confusión", new DatosAtaque("Confusión", 18, "Psíquico", 100, false, ""));
        DATABASE.put("Copión", new DatosAtaque("Copión", 0, "Normal", 100, true, "Copia el último movimiento usado"));

        // Ataques Eléctricos
        DATABASE.put("Impactrueno", new DatosAtaque("Impactrueno", 12, "Eléctrico", 100, false, ""));
        DATABASE.put("Trueno", new DatosAtaque("Trueno", 25, "Eléctrico", 70, false, ""));
        DATABASE.put("Chispa", new DatosAtaque("Chispa", 18, "Eléctrico", 100, false, ""));

        // Ataques de Fuego
        DATABASE.put("Ascuas", new DatosAtaque("Ascuas", 15, "Fuego", 100, false, ""));
        DATABASE.put("Rueda Fuego", new DatosAtaque("Rueda Fuego", 13, "Fuego", 100, false, ""));
        DATABASE.put("Humareda", new DatosAtaque("Humareda", 17, "Fuego", 100, false, ""));

        // Ataques de Bicho
        DATABASE.put("Aire Afilado", new DatosAtaque("Aire Afilado", 20, "Volador", 95, false, ""));

        // Ataques de Fantasma
        DATABASE.put("Impresionar", new DatosAtaque("Impresionar", 10, "Fantasma", 100, false, ""));
        DATABASE.put("Polución", new DatosAtaque("Polución", 15, "Veneno", 100, false, ""));
        DATABASE.put("Infortunio", new DatosAtaque("Infortunio", 25, "Fantasma", 100, false, ""));
        DATABASE.put("Rencor", new DatosAtaque("Rencor", 18, "Fantasma", 100, false, ""));
        DATABASE.put("Sombra Vil", new DatosAtaque("Sombra Vil", 16, "Fantasma", 100, false, ""));

        // Ataques de Agua
        DATABASE.put("Burbuja", new DatosAtaque("Burbuja", 10, "Agua", 100, false, ""));
        DATABASE.put("Aqua Jet", new DatosAtaque("Aqua Jet", 16, "Agua", 100, false, ""));

        // Ataques de Dragón
        DATABASE.put("Ácido", new DatosAtaque("Ácido", 12, "Veneno", 100, false, ""));
        DATABASE.put("Absorber", new DatosAtaque("Absorber", 10, "Planta", 100, false, "Absorbe HP"));
        DATABASE.put("Dragoaliento", new DatosAtaque("Dragoaliento", 20, "Dragón", 100, false, ""));

        // Ataques de Hada
        DATABASE.put("Beso Drenaje", new DatosAtaque("Beso Drenaje", 15, "Hada", 100, false, "Absorbe HP"));
        DATABASE.put("B. Drenaje", new DatosAtaque("B. Drenaje", 14, "Hada", 100, false, "Absorbe HP"));

        // Ataques de Planta
        DATABASE.put("H. Afilada", new DatosAtaque("H. Afilada", 18, "Planta", 100, false, ""));
        DATABASE.put("Hoja Afilada", new DatosAtaque("Hoja Afilada", 16, "Planta", 100, false, ""));
        DATABASE.put("Follaje", new DatosAtaque("Follaje", 14, "Planta", 100, false, ""));

        // Ataques de Veneno/Siniestro
        DATABASE.put("Púas Tóxicas", new DatosAtaque("Púas Tóxicas", 0, "Veneno", 100, true, "Envenena al objetivo"));
        DATABASE.put("P. Tóxicas", new DatosAtaque("P. Tóxicas", 0, "Veneno", 100, true, "Envenena al objetivo"));
        DATABASE.put("Pin Misil", new DatosAtaque("Pin Misil", 18, "Bicho", 95, false, ""));
        DATABASE.put("Gas Venenoso", new DatosAtaque("Gas Venenoso", 0, "Veneno", 90, true, "Envenena al objetivo"));
        DATABASE.put("Mordisco", new DatosAtaque("Mordisco", 18, "Siniestro", 100, false, ""));

        // Ataques de Hielo
        DATABASE.put("Polvo Nieve", new DatosAtaque("Polvo Nieve", 15, "Hielo", 100, false, ""));
        DATABASE.put("P. Nieve", new DatosAtaque("P. Nieve", 15, "Hielo", 100, false, ""));
        DATABASE.put("R. Hielo", new DatosAtaque("R. Hielo", 20, "Hielo", 90, false, ""));

        // Ataques de Estado
        DATABASE.put("Hipnosis", new DatosAtaque("Hipnosis", 0, "Psíquico", 60, true, "Duerme al objetivo"));
        DATABASE.put("Onda Trueno", new DatosAtaque("Onda Trueno", 0, "Eléctrico", 90, true, "Paraliza al objetivo"));
        DATABASE.put("Dulce Aroma", new DatosAtaque("Dulce Aroma", 0, "Normal", 100, true, "Reduce evasión"));
        DATABASE.put("Refugio", new DatosAtaque("Refugio", 0, "Agua", 100, true, "Aumenta Defensa"));
        DATABASE.put("Gruñido", new DatosAtaque("Gruñido", 0, "Normal", 100, true, "Reduce Ataque"));
        DATABASE.put("Arena Arena", new DatosAtaque("Arena Arena", 0, "Tierra", 100, true, "Reduce Precisión"));
        DATABASE.put("Carga", new DatosAtaque("Carga", 0, "Eléctrico", 100, true, "Aumenta Def. Especial"));
        DATABASE.put("Encanto", new DatosAtaque("Encanto", 0, "Hada", 100, true, "Reduce Ataque"));
        DATABASE.put("Fortaleza", new DatosAtaque("Fortaleza", 0, "Normal", 100, true, "Aumenta Defensa"));
        DATABASE.put("Mofa", new DatosAtaque("Mofa", 0, "Siniestro", 100, true, "Provoca al objetivo"));
    }

    public static DatosAtaque get(String nombre) {
        return DATABASE.get(nombre);
    }

    public static boolean existe(String nombre) {
        return DATABASE.containsKey(nombre);
    }
}
