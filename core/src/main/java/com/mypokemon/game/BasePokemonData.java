package com.mypokemon.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Base de datos completa de las 20 especies de Pokémon
 * Cada Pokémon tiene estadísticas de Nivel 0 y Nivel 5+
 * Nivel 0-4: Stats base
 * Nivel 5-10: Stats base + bonificación
 */
public class BasePokemonData {
        public String nombre;
        public String descripcion;

        // Estadísticas Nivel 0 (base)
        public int psNivel0;
        public int atqNivel0;
        public int defNivel0;
        public int ateNivel0; // Ataque Especial
        public int dfeNivel0; // Defensa Especial
        public int velNivel0;

        // Bonificaciones al alcanzar Nivel 5
        public int psBonus;
        public int atqBonus;
        public int defBonus;
        public int ateBonus;
        public int dfeBonus;
        public int velBonus;

        public String tipo;
        public String[] movimientosIniciales; // Movimientos disponibles desde Nivel 0
        public String movimientoNivel5; // Movimiento desbloqueado en Nivel 5
        public String[] inmunidades;

        // Para el sistema de investigación
        public int puntosInvestigacion = 2; // Por defecto: Victoria = +1, Captura = +2

        public BasePokemonData(String nombre, String descripcion,
                        int ps0, int atq0, int def0, int ate0, int dfe0, int vel0,
                        int psB, int atqB, int defB, int ateB, int dfeB, int velB,
                        String tipo, String[] movIniciales, String movNivel5, String[] inmunidades) {
                this.nombre = nombre;
                this.descripcion = descripcion;
                this.psNivel0 = ps0;
                this.atqNivel0 = atq0;
                this.defNivel0 = def0;
                this.ateNivel0 = ate0;
                this.dfeNivel0 = dfe0;
                this.velNivel0 = vel0;
                this.psBonus = psB;
                this.atqBonus = atqB;
                this.defBonus = defB;
                this.ateBonus = ateB;
                this.dfeBonus = dfeB;
                this.velBonus = velB;
                this.tipo = tipo;
                this.movimientosIniciales = movIniciales;
                this.movimientoNivel5 = movNivel5;
                this.inmunidades = inmunidades;
        }

        private static final Map<String, BasePokemonData> DATABASE = new HashMap<>();

        static {
                // ZONA 1 (Nivel de Dificultad 1)

                DATABASE.put("Stantler", new BasePokemonData(
                                "Stantler",
                                "Sus cuernos curvados alteran el flujo del aire y crean una distorsión en la realidad que marea a quien los mira.",
                                73, 95, 62, 85, 65, 85,
                                9, 9, 0, 0, 0, 9,
                                "Normal / Psíquico",
                                new String[] { "Placaje", "Confusión" },
                                "Hipnosis",
                                new String[] { "Fantasma" }));

                DATABASE.put("Pichu", new BasePokemonData(
                                "Pichu",
                                "Le cuesta controlar la energía y se asusta de sus propios chispazos.",
                                20, 40, 15, 35, 35, 60,
                                5, 5, 0, 0, 0, 5,
                                "Eléctrico",
                                new String[] { "Impactrueno", "Latigazo" },
                                "Onda Trueno",
                                new String[] {}));

                DATABASE.put("Mime Jr.", new BasePokemonData(
                                "Mime Jr.",
                                "Imita movimientos de otros para comprender sus intenciones y emociones.",
                                20, 25, 45, 70, 90, 60,
                                5, 0, 5, 0, 0, 5,
                                "Psíquico / Hada",
                                new String[] { "Confusión", "Copión" },
                                "Hipnosis",
                                new String[] {}));

                DATABASE.put("Chimchar", new BasePokemonData(
                                "Chimchar",
                                "La llama de su cola se apaga cuando duerme. Es un experto escalador.",
                                44, 58, 44, 58, 44, 61,
                                5, 5, 0, 0, 0, 5,
                                "Fuego",
                                new String[] { "Arañazo", "Ascuas" },
                                "Rueda Fuego",
                                new String[] {}));

                DATABASE.put("Scyther", new BasePokemonData(
                                "Scyther",
                                "Sus guadañas son tan afiladas que pueden cortar troncos de un solo golpe.",
                                70, 110, 80, 55, 80, 105,
                                9, 9, 0, 0, 0, 9,
                                "Bicho / Volador",
                                new String[] { "At. Rápido", "Falsotortazo" },
                                "Aire Afilado",
                                new String[] { "Tierra" }));

                // ZONA 2 (Nivel de Dificultad 2)

                DATABASE.put("Gastly", new BasePokemonData(
                                "Gastly",
                                "Cuerpo de gas. Puede dormir a su presa en segundos.",
                                30, 35, 30, 100, 35, 80,
                                5, 0, 0, 9, 0, 9,
                                "Fantasma / Veneno",
                                new String[] { "Impresionar", "Polución" },
                                "Infortunio",
                                new String[] { "Normal", "Lucha" }));

                DATABASE.put("Goomy", new BasePokemonData(
                                "Goomy",
                                "El tipo Dragón más débil. Su cuerpo viscoso necesita humedad.",
                                45, 50, 35, 55, 75, 40,
                                5, 0, 0, 0, 9, 5,
                                "Dragón",
                                new String[] { "Burbuja", "Ácido" },
                                "Absorber",
                                new String[] {}));

                DATABASE.put("Togepi", new BasePokemonData(
                                "Togepi",
                                "Su cascarón comparte buena suerte con personas de corazón puro.",
                                35, 20, 65, 40, 65, 20,
                                5, 0, 5, 0, 0, 5,
                                "Hada",
                                new String[] { "Picotazo", "Beso Drenaje" },
                                "Dulce Aroma",
                                new String[] { "Dragón" }));

                DATABASE.put("Turtwig", new BasePokemonData(
                                "Turtwig",
                                "Concha de tierra endurecida. Si tiene sed, la hoja de su cabeza se marchita.",
                                55, 68, 64, 45, 55, 31,
                                5, 5, 5, 0, 0, 0,
                                "Planta",
                                new String[] { "Placaje", "H. Afilada" },
                                "Refugio",
                                new String[] {}));

                // ZONA 3 (Nivel de Dificultad 3)

                DATABASE.put("Growlithe H.", new BasePokemonData(
                                "Growlithe H.",
                                "Pelaje grueso y cuerno de roca por la actividad volcánica.",
                                60, 75, 45, 65, 50, 55,
                                5, 9, 0, 0, 0, 5,
                                "Fuego / Roca",
                                new String[] { "Placaje", "Ascuas" },
                                "Mordisco",
                                new String[] {}));

                DATABASE.put("Qwilfish H.", new BasePokemonData(
                                "Qwilfish H.",
                                "Dispara espinas cargadas de un veneno oscuro y denso.",
                                65, 95, 85, 55, 55, 103,
                                5, 9, 9, 0, 0, 0,
                                "Siniestro / Veneno",
                                new String[] { "Picotazo", "P. Tóxicas" },
                                "Pin Misil",
                                new String[] { "Psíquico" }));

                DATABASE.put("Piplup", new BasePokemonData(
                                "Piplup",
                                "Muy orgulloso. Su plumón lo protege del frío intenso.",
                                53, 51, 53, 61, 56, 40,
                                5, 0, 0, 9, 0, 5,
                                "Agua",
                                new String[] { "Burbuja", "Destructor" },
                                "Gruñido",
                                new String[] {}));

                DATABASE.put("Basculin", new BasePokemonData(
                                "Basculin",
                                "Resistencia física increíble al nadar contra la corriente.",
                                70, 92, 65, 80, 55, 116,
                                9, 9, 0, 0, 0, 9,
                                "Agua",
                                new String[] { "Placaje", "Aqua Jet" },
                                "Burbuja",
                                new String[] {}));

                // ZONA 4 (Nivel de Dificultad 4)

                DATABASE.put("Sneasel H.", new BasePokemonData(
                                "Sneasel H.",
                                "Escala riscos helados con sus garras venenosas.",
                                55, 95, 55, 35, 75, 115,
                                5, 9, 0, 0, 0, 9,
                                "Lucha / Veneno",
                                new String[] { "Arañazo", "Gas Venenoso" },
                                "Mofa",
                                new String[] {}));

                DATABASE.put("Gible", new BasePokemonData(
                                "Gible",
                                "Ataca a todo lo que se mueva con sus potentes mandíbulas.",
                                58, 70, 45, 40, 45, 42,
                                5, 9, 0, 0, 0, 5,
                                "Dragón / Tierra",
                                new String[] { "Placaje", "Dragoaliento" },
                                "Arena Arena",
                                new String[] { "Eléctrico" }));

                DATABASE.put("Voltorb H.", new BasePokemonData(
                                "Voltorb H.",
                                "Aspecto similar a las Poké Balls antiguas; descarga energía por su orificio.",
                                40, 30, 50, 55, 55, 100,
                                5, 0, 0, 5, 0, 9,
                                "Eléctrico / Planta",
                                new String[] { "Trueno", "Carga" },
                                "Chispa",
                                new String[] {}));

                DATABASE.put("Cleffa", new BasePokemonData(
                                "Cleffa",
                                "Se cree que llegó en un meteorito. Solo aparece con luna clara.",
                                50, 25, 28, 45, 55, 15,
                                5, 0, 0, 0, 5, 5,
                                "Hada",
                                new String[] { "Destructor", "B. Drenaje" },
                                "Encanto",
                                new String[] { "Dragón" }));

                // ZONA 5 (Nivel de Dificultad 5)

                DATABASE.put("Bergmite", new BasePokemonData(
                                "Bergmite",
                                "Bloquea ataques con el escudo de hielo de su lomo.",
                                55, 69, 85, 32, 35, 28,
                                5, 5, 9, 0, 0, 0,
                                "Hielo",
                                new String[] { "Placaje", "Polvo Nieve" },
                                "Fortaleza",
                                new String[] {}));

                DATABASE.put("Snorunt", new BasePokemonData(
                                "Snorunt",
                                "Se dice que los hogares visitados por él prosperarán.",
                                50, 50, 50, 50, 50, 50,
                                5, 5, 0, 0, 0, 5,
                                "Hielo",
                                new String[] { "Polvo Nieve", "Impresionar" },
                                "Mordisco",
                                new String[] {}));

                DATABASE.put("Zorua H.", new BasePokemonData(
                                "Zorua H.",
                                "Remanente de almas que perecieron en el frío. Crea ilusiones de rencor.",
                                35, 60, 40, 85, 40, 70,
                                5, 0, 0, 9, 0, 9,
                                "Normal / Fantasma",
                                new String[] { "Arañazo", "Rencor" },
                                "Sombra Vil",
                                new String[] { "Normal", "Lucha" }));

                // STARTERS (Adicionales si no están en la lista)
                DATABASE.put("Rowlet",
                                new BasePokemonData("Rowlet", "Lanza plumas afiladas.", 68, 55, 55, 50, 50, 42, 5, 5, 0,
                                                0, 0, 5, "Planta / Volador", new String[] { "Tackle" }, "Leafage",
                                                new String[] {}));
                DATABASE.put("Cyndaquil", new BasePokemonData("Cyndaquil", "Tímido pero fogoso.", 39, 52, 43, 60, 50,
                                65, 5, 5, 0, 0, 0, 5, "Fuego", new String[] { "Tackle" }, "Ember", new String[] {}));
                DATABASE.put("Oshawott", new BasePokemonData("Oshawott", "Lucha con su concha.", 55, 55, 45, 63, 45, 45,
                                5, 5, 0, 0, 0, 5, "Agua", new String[] { "Tackle" }, "Water Gun", new String[] {}));
        }

        public static BasePokemonData get(String nombre) {
                return DATABASE.get(nombre);
        }

        public static java.util.Set<String> getNombres() {
                return DATABASE.keySet();
        }

        public String getNombre() {
                return nombre;
        }

        public String getDescripcion() {
                return descripcion;
        }

        public double getHpBase() {
                return psNivel0;
        }

        public double getAtqBase() {
                return atqNivel0;
        }

        public double getVelBase() {
                return velNivel0;
        }

        public String getTipo() {
                return tipo;
        }

        /**
         * Calcula las estadísticas de un Pokémon según su nivel de investigación (0-10)
         */
        public int calcularPS(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return psNivel0;
                } else {
                        return psNivel0 + psBonus;
                }
        }

        public int calcularAtaque(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return atqNivel0;
                } else {
                        return atqNivel0 + atqBonus;
                }
        }

        public int calcularDefensa(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return defNivel0;
                } else {
                        return defNivel0 + defBonus;
                }
        }

        public int calcularAtaqueEspecial(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return ateNivel0;
                } else {
                        return ateNivel0 + ateBonus;
                }
        }

        public int calcularDefensaEspecial(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return dfeNivel0;
                } else {
                        return dfeNivel0 + dfeBonus;
                }
        }

        public int calcularVelocidad(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return velNivel0;
                } else {
                        return velNivel0 + velBonus;
                }
        }
}
