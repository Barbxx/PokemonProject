package com.mypokemon.game;

import java.util.HashMap;
import java.util.Map;

public class BasePokemonData {
    public String nombre;
    public String descripcion;
    public int psMin, psMax;
    public int atqMin, atqMax;
    public int velMin, velMax;
    public String[] movimientosIniciales;
    public String[] inmunidades;
    public String tipo;
    public int puntosInvestigacion;

    public BasePokemonData(String nombre, String descripcion, int psMin, int psMax, int atqMin, int atqMax, int velMin,
            int velMax, String tipo, String[] movimientos, String[] inmunidades, int puntos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.psMin = psMin;
        this.psMax = psMax;
        this.atqMin = atqMin;
        this.atqMax = atqMax;
        this.velMin = velMin;
        this.velMax = velMax;
        this.tipo = tipo;
        this.movimientosIniciales = movimientos;
        this.inmunidades = inmunidades;
        this.puntosInvestigacion = puntos;
    }

    private static final Map<String, BasePokemonData> DATABASE = new HashMap<>();

    static {
        // Nivel 1
        DATABASE.put("Stantler",
                new BasePokemonData("Stantler", "Sus cuernos curvados alteran el flujo del aire y crean distorsiones.",
                        73, 91, 95, 113, 85, 103, "Normal", new String[] { "Placaje", "Confusión" },
                        new String[] { "Fantasma" }, 3));
        DATABASE.put("Pichu",
                new BasePokemonData("Pichu", "Le cuesta controlar su energía y se asusta de sus chispazos.", 20, 29, 40,
                        49, 60, 69, "Eléctrico", new String[] { "Impactrueno", "Latigazo" }, new String[] {}, 2));
        DATABASE.put("Mime Jr.",
                new BasePokemonData("Mime Jr.", "Imita movimientos ajenos para comprender emociones.", 20, 29, 45, 54,
                        60, 69, "Psíquico", new String[] { "Confusión", "Copión" }, new String[] { "Dragón" }, 3));
        DATABASE.put("Chimchar", new BasePokemonData("Chimchar", "Escalador experto cuya llama se apaga al dormir.", 44,
                53, 58, 67, 61, 70, "Fuego", new String[] { "Arañazo", "Ascuas" }, new String[] {}, 2));
        DATABASE.put("Scyther",
                new BasePokemonData("Scyther", "Sus guadañas cortan troncos de un solo golpe.", 70, 88, 110, 128, 105,
                        123, "Bicho", new String[] { "At. Rápido", "Falsotortazo" }, new String[] { "Tierra" }, 3));

        // Nivel 2
        DATABASE.put("Turtwig",
                new BasePokemonData("Turtwig", "Su concha es de tierra endurecida; si tiene sed, su hoja se marchita.",
                        55, 64, 64, 73, 31, 40, "Planta", new String[] { "Placaje", "H. Afilada", "Refugio" },
                        new String[] {}, 2));
        DATABASE.put("Gastly",
                new BasePokemonData("Gastly", "Cuerpo de gas que duerme a su presa en segundos.", 30, 39, 100, 118, 80,
                        98, "Fantasma", new String[] { "Impresionar", "Polución" }, new String[] { "Normal", "Lucha" },
                        3));
        DATABASE.put("Goomy", new BasePokemonData("Goomy", "Considerado el tipo Dragón más débil; necesita humedad.",
                45, 54, 75, 93, 40, 49, "Dragón", new String[] { "Burbuja", "Ácido", "Absorber" }, new String[] {}, 2));
        DATABASE.put("Togepi",
                new BasePokemonData("Togepi", "Su cascarón está lleno de felicidad para corazones puros.", 35, 44, 65,
                        74, 20, 29, "Hada", new String[] { "Picotazo", "Beso Drenaje", "Dulce Aroma" },
                        new String[] { "Dragón" }, 3));

        // Nivel 3
        DATABASE.put("Growlithe H.",
                new BasePokemonData("Growlithe H.", "Posee pelaje grueso y cuerno de roca; es muy leal.", 60, 69, 75,
                        93, 55, 64, "Fuego", new String[] { "Placaje", "Ascuas", "Mordisco" }, new String[] {}, 2));
        DATABASE.put("Qwilfish H.",
                new BasePokemonData("Qwilfish H.", "Dispara espinas cargadas de veneno denso.", 65, 74, 95, 113, 85,
                        103, "Siniestro", new String[] { "Picotazo", "P. Tóxicas", "Pin Misil" },
                        new String[] { "Psíquico" }, 3));
        DATABASE.put("Piplup",
                new BasePokemonData("Piplup", "Muy orgulloso; su plumón lo protege del frío intenso.", 53, 62, 56, 65,
                        40, 49, "Agua", new String[] { "Burbuja", "Destructor", "Gruñido" }, new String[] {}, 2));
        DATABASE.put("Basculin",
                new BasePokemonData("Basculin", "Posee una resistencia física increíble nadando contra corriente.", 70,
                        88, 92, 110, 98, 116, "Agua", new String[] { "Placaje", "Burbuja", "Aqua Jet" },
                        new String[] {}, 2));

        // Nivel 4
        DATABASE.put("Sneasel H.",
                new BasePokemonData("Sneasel H.", "Escala riscos helados con garras venenosas.", 55, 64, 95, 113, 115,
                        133, "Lucha", new String[] { "Arañazo", "Gas Venenoso", "Mofa" }, new String[] {}, 2));
        DATABASE.put("Gible",
                new BasePokemonData("Gible", "Ataca con potentes mandíbulas en cuevas profundas.", 58, 67, 70, 88, 42,
                        51, "Dragón", new String[] { "Placaje", "Dragoaliento", "Arena Arena" },
                        new String[] { "Eléctrico" }, 3));
        DATABASE.put("Voltorb H.",
                new BasePokemonData("Voltorb H.", "Descarga energía eléctrica por su orificio superior.", 40, 49, 55,
                        64, 100, 118, "Eléctrico", new String[] { "Trueno", "Carga", "Chispa" }, new String[] {}, 2));
        DATABASE.put("Cleffa",
                new BasePokemonData("Cleffa", "Se cree que llegó en un meteorito; aparece con luna clara.", 50, 59, 55,
                        64, 15, 24, "Hada", new String[] { "Destructor", "B. Drenaje", "Encanto" },
                        new String[] { "Dragón" }, 3));

        // Nivel 5
        DATABASE.put("Bergmite",
                new BasePokemonData("Bergmite", "Bloquea ataques con el escudo de hielo de su lomo.", 55, 64, 85, 103,
                        28, 37, "Hielo", new String[] { "Placaje", "Polvo Nieve", "Fortaleza" }, new String[] {}, 2));
        DATABASE.put("Snorunt",
                new BasePokemonData("Snorunt", "Los hogares visitados por él prosperarán por generaciones.", 50, 59, 50,
                        59, 50, 59, "Hielo", new String[] { "P. Nieve", "Impresionar", "R. Hielo", "Mordisco" },
                        new String[] {}, 2));
        DATABASE.put("Zorua H.",
                new BasePokemonData("Zorua H.", "Nacido del rencor; crea ilusiones aterradoras para protegerse.", 35,
                        44, 85, 103, 70, 88, "Fantasma", new String[] { "Arañazo", "Rencor", "Sombra Vil", "Mofa" },
                        new String[] { "Normal", "Lucha", "Fantasma" }, 3));
    }

    public static BasePokemonData get(String nombre) {
        return DATABASE.get(nombre);
    }
}
