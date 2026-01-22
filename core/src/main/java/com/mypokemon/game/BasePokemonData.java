package com.mypokemon.game;

import java.util.TreeMap;
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
        public String[] movimientosNivel5; // Movimientos desbloqueados en Nivel 5+
        public String[] inmunidades;

        // Para el sistema de investigación
        public int puntosInvestigacion = 2; // Por defecto: Victoria = +1, Captura = +2

        /**
         * Crea una nueva entrada de datos para una especie Pokémon.
         * 
         * @param nombre       Nombre de la especie.
         * @param descripcion  Descripción de la Pokédex.
         * @param ps0          Puntos de Salud base (Nivel 0).
         * @param atq0         Ataque base (Nivel 0).
         * @param def0         Defensa base (Nivel 0).
         * @param ate0         Ataque Especial base (Nivel 0).
         * @param dfe0         Defensa Especial base (Nivel 0).
         * @param vel0         Velocidad base (Nivel 0).
         * @param psB          Bonificación de PS (Nivel 5+).
         * @param atqB         Bonificación de Ataque (Nivel 5+).
         * @param defB         Bonificación de Defensa (Nivel 5+).
         * @param ateB         Bonificación de Ataque Especial (Nivel 5+).
         * @param dfeB         Bonificación de Defensa Especial (Nivel 5+).
         * @param velB         Bonificación de Velocidad (Nivel 5+).
         * @param tipo         Tipo elemental del Pokémon.
         * @param movIniciales Movimientos disponibles desde el inicio.
         * @param movNivel5    Movimientos desbloqueados a nivel 5.
         * @param inmunidades  Tipos a los que es inmune.
         */
        public BasePokemonData(String nombre, String descripcion,
                        int ps0, int atq0, int def0, int ate0, int dfe0, int vel0,
                        int psB, int atqB, int defB, int ateB, int dfeB, int velB,
                        String tipo, String[] movIniciales, String[] movNivel5, String[] inmunidades) {
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
                this.movimientosNivel5 = movNivel5;
                this.inmunidades = inmunidades;
        }

        private static final Map<String, BasePokemonData> DATABASE = new TreeMap<>();

        static {
                // ZONA 1 (Nivel de Dificultad 1)

                DATABASE.put("Stantler", new BasePokemonData(
                                "Stantler",
                                "Sus cuernos curvados alteran el flujo del aire y crean una distorsión en la realidad que marea a quien los mira.",
                                55, 95, 62, 85, 65, 85,
                                9, 9, 0, 0, 0, 9,
                                "Normal / Psíquico",
                                new String[] { "Placaje", "Confusión" },
                                new String[] { "Hipnosis" },
                                new String[] { "Fantasma" }));

                DATABASE.put("Pichu", new BasePokemonData(
                                "Pichu",
                                "Le cuesta controlar la energía y se asusta de sus propios chispazos.",
                                20, 40, 15, 35, 35, 60,
                                5, 5, 0, 0, 0, 5,
                                "Eléctrico",
                                new String[] { "Impactrueno", "Latigazo" },
                                new String[] { "Onda Trueno" },
                                new String[] {}));

                DATABASE.put("Mime Jr.", new BasePokemonData(
                                "Mime Jr.",
                                "Imita movimientos de otros para comprender sus intenciones y emociones.",
                                20, 25, 45, 70, 90, 60,
                                5, 0, 5, 0, 0, 5,
                                "Psíquico / Hada",
                                new String[] { "Confusión", "Copión" },
                                new String[] { "Hipnosis" },
                                new String[] {}));

                DATABASE.put("Chimchar", new BasePokemonData(
                                "Chimchar",
                                "La llama de su cola se apaga cuando duerme. Es un experto escalador.",
                                44, 58, 44, 58, 44, 61,
                                5, 5, 0, 0, 0, 5,
                                "Fuego",
                                new String[] { "Arañazo", "Ascuas" },
                                new String[] { "Rueda Fuego" },
                                new String[] {}));

                DATABASE.put("Scyther", new BasePokemonData(
                                "Scyther",
                                "Sus guadañas son tan afiladas que pueden cortar troncos de un solo golpe.",
                                50, 110, 80, 55, 80, 105,
                                9, 9, 0, 0, 0, 9,
                                "Bicho / Volador",
                                new String[] { "At. Rápido", "Falsotortazo" },
                                new String[] { "Aire Afilado" },
                                new String[] { "Tierra" }));

                // ZONA 2 (Nivel de Dificultad 2)

                DATABASE.put("Gastly", new BasePokemonData(
                                "Gastly",
                                "Cuerpo de gas. Puede dormir a su presa en segundos.",
                                30, 35, 30, 100, 35, 80,
                                5, 0, 0, 9, 0, 9,
                                "Fantasma / Veneno",
                                new String[] { "Impresionar", "Polución" },
                                new String[] { "Infortunio" },
                                new String[] { "Normal", "Lucha" }));

                DATABASE.put("Goomy", new BasePokemonData(
                                "Goomy",
                                "El tipo Dragón más débil. Su cuerpo viscoso necesita humedad.",
                                45, 50, 35, 55, 75, 40,
                                5, 0, 0, 0, 9, 5,
                                "Dragón",
                                new String[] { "Burbuja", "Ácido" },
                                new String[] { "Absorber" },
                                new String[] {}));

                DATABASE.put("Togepi", new BasePokemonData(
                                "Togepi",
                                "Su cascarón comparte buena suerte con personas de corazón puro.",
                                35, 20, 65, 40, 65, 20,
                                5, 0, 5, 0, 0, 5,
                                "Hada",
                                new String[] { "Picotazo", "Beso Drenaje" },
                                new String[] { "Dulce Aroma" },
                                new String[] { "Dragón" }));

                DATABASE.put("Turtwig", new BasePokemonData(
                                "Turtwig",
                                "Concha de tierra endurecida. Si tiene sed, la hoja de su cabeza se marchita.",
                                55, 68, 64, 45, 55, 31,
                                5, 5, 5, 0, 0, 0,
                                "Planta",
                                new String[] { "Placaje", "H. Afilada" },
                                new String[] { "Refugio" },
                                new String[] {}));

                // ZONA 3 (Nivel de Dificultad 3)

                DATABASE.put("Growlithe H.", new BasePokemonData(
                                "Growlithe H.",
                                "Pelaje grueso y cuerno de roca por la actividad volcánica.",
                                60, 75, 45, 65, 50, 55,
                                5, 9, 0, 0, 0, 5,
                                "Fuego / Roca",
                                new String[] { "Placaje", "Ascuas" },
                                new String[] { "Mordisco" },
                                new String[] {}));

                DATABASE.put("Qwilfish H.", new BasePokemonData(
                                "Qwilfish H.",
                                "Dispara espinas cargadas de un veneno oscuro y denso.",
                                65, 95, 85, 55, 55, 103,
                                5, 9, 9, 0, 0, 0,
                                "Siniestro / Veneno",
                                new String[] { "Picotazo", "P. Tóxicas" },
                                new String[] { "Pin Misil" },
                                new String[] { "Psíquico" }));

                DATABASE.put("Piplup", new BasePokemonData(
                                "Piplup",
                                "Muy orgulloso. Su plumón lo protege del frío intenso.",
                                53, 51, 53, 61, 56, 40,
                                5, 0, 0, 9, 0, 5,
                                "Agua",
                                new String[] { "Burbuja", "Destructor" },
                                new String[] { "Gruñido" },
                                new String[] {}));

                DATABASE.put("Basculin Red-Striped", new BasePokemonData(
                                "Basculin Red-Striped",
                                "Resistencia física increíble al nadar contra la corriente.",
                                70, 92, 65, 80, 55, 116,
                                9, 9, 0, 0, 0, 9,
                                "Agua",
                                new String[] { "Placaje", "Aqua Jet" },
                                new String[] { "Burbuja" },
                                new String[] {}));

                // ZONA 4 (Nivel de Dificultad 4)

                DATABASE.put("Sneasel H.", new BasePokemonData(
                                "Sneasel H.",
                                "Escala riscos helados con sus garras venenosas.",
                                55, 95, 55, 35, 75, 115,
                                5, 9, 0, 0, 0, 9,
                                "Lucha / Veneno",
                                new String[] { "Arañazo", "Gas Venenoso" },
                                new String[] { "Mofa" },
                                new String[] {}));

                DATABASE.put("Gible", new BasePokemonData(
                                "Gible",
                                "Ataca a todo lo que se mueva con sus potentes mandíbulas.",
                                58, 70, 45, 40, 45, 42,
                                5, 9, 0, 0, 0, 5,
                                "Dragón / Tierra",
                                new String[] { "Placaje", "Dragoaliento" },
                                new String[] { "Arena Arena" },
                                new String[] { "Eléctrico" }));

                DATABASE.put("Voltorb H.", new BasePokemonData(
                                "Voltorb H.",
                                "Aspecto similar a las Poké Balls antiguas; descarga energía por su orificio.",
                                40, 30, 50, 55, 55, 100,
                                5, 0, 0, 5, 0, 9,
                                "Eléctrico / Planta",
                                new String[] { "Trueno", "Carga" },
                                new String[] { "Chispa" },
                                new String[] {}));

                DATABASE.put("Cleffa", new BasePokemonData(
                                "Cleffa",
                                "Se cree que llegó en un meteorito. Solo aparece con luna clara.",
                                50, 25, 28, 45, 55, 15,
                                5, 0, 0, 0, 5, 5,
                                "Hada",
                                new String[] { "Destructor", "B. Drenaje" },
                                new String[] { "Encanto" },
                                new String[] { "Dragón" }));

                // ZONA 5 (Nivel de Dificultad 5)

                DATABASE.put("Bergmite", new BasePokemonData(
                                "Bergmite",
                                "Bloquea ataques con el escudo de hielo de su lomo.",
                                55, 69, 85, 32, 35, 28,
                                5, 5, 9, 0, 0, 0,
                                "Hielo",
                                new String[] { "Placaje", "Polvo Nieve" },
                                new String[] { "Fortaleza" },
                                new String[] {}));

                DATABASE.put("Snorunt", new BasePokemonData(
                                "Snorunt",
                                "Se dice que los hogares visitados por él prosperarán.",
                                50, 50, 50, 50, 50, 50,
                                5, 5, 0, 0, 0, 5,
                                "Hielo",
                                new String[] { "Polvo Nieve", "Impresionar" },
                                new String[] { "Mordisco" },
                                new String[] {}));

                DATABASE.put("Zorua H.", new BasePokemonData(
                                "Zorua H.",
                                "Remanente de almas que perecieron en el frío. Crea ilusiones de rencor.",
                                35, 60, 40, 85, 40, 70,
                                5, 0, 0, 9, 0, 9,
                                "Normal / Fantasma",
                                new String[] { "Arañazo", "Rencor" },
                                new String[] { "Sombra Vil" },
                                new String[] { "Normal", "Lucha" }));

                DATABASE.put("Jigglypuff", new BasePokemonData(
                                "Jigglypuff",
                                "Un Pokémon que duerme todo el día. Su canto es encantador.",
                                60, 45, 45, 45, 45, 45,
                                5, 5, 5, 5, 5, 5,
                                "Normal",
                                new String[] { "Placaje", "Canto" },
                                new String[] { "Mordisco" },
                                new String[] { "Normal" }));

                DATABASE.put("Rowlet",
                                new BasePokemonData("Rowlet",
                                                "Utiliza fotosíntesis para acumular energía de día y ataca al anochecer.",
                                                45, 55, 55, 50, 50, 42, 15, 5,
                                                0,
                                                0, 0, 5, "Planta / Volador",
                                                new String[] { "Picotazo", "Follaje" },
                                                new String[] { "Impresionar", "Hoja Afilada" },
                                                new String[] { "Fuego", "Hielo", "Roca" }));

                DATABASE.put("Cyndaquil", new BasePokemonData("Cyndaquil",
                                "Suele enroscarse como una bola. Si se asusta, su espalda arde con fuerza.", 40, 52, 43,
                                60, 50,
                                65, 20, 5, 0, 0, 0, 5, "Fuego",
                                new String[] { "Placaje", "Ascuas" },
                                new String[] { "Humareda", "Rueda Fuego" },
                                new String[] { "Agua", "Tierra", "Roca" }));

                DATABASE.put("Oshawott", new BasePokemonData("Oshawott",
                                "Combate usando la vieira de su ombligo para cortar bayas o detener ataques.", 45, 55,
                                45, 63, 45, 45,
                                22, 5, 0, 0, 0, 5, "Agua",
                                new String[] { "Placaje", "Burbuja" },
                                new String[] { "Aqua Jet", "Falsotortazo" },
                                new String[] { "Planta", "Eléctrico" }));

                DATABASE.put("Arceus", new BasePokemonData("Arceus",
                                "Se dice que nació de un huevo en medio de la nada y dio forma al universo entero con sus mil brazos.",
                                130, 120, 120, 120, 120, 120,
                                0, 0, 0, 0, 0, 0, "Normal",
                                new String[] { "Sentencia", "Paz Mental" },
                                new String[] { "Recuperación", "Hiperrayo" },
                                new String[] { "Lucha" }));
        }

        /**
         * Obtiene los datos base de un Pokémon por su nombre.
         * 
         * @param nombre Nombre de la especie.
         * @return Datos base del Pokémon o null si no existe.
         */
        public static BasePokemonData get(String nombre) {
                return DATABASE.get(nombre);
        }

        /**
         * Obtiene todos los nombres de las especies registradas.
         * 
         * @return Conjunto de nombres de Pokémon.
         */
        public static java.util.Set<String> getNombres() {
                return DATABASE.keySet();
        }

        /**
         * Obtiene el nombre de la especie.
         * 
         * @return Nombre del Pokémon.
         */
        public String getNombre() {
                return nombre;
        }

        /**
         * Obtiene la descripción de la Pokédex.
         * 
         * @return Descripción del Pokémon.
         */
        public String getDescripcion() {
                return descripcion;
        }

        /**
         * Obtiene los PS base (Nivel 0).
         * 
         * @return Puntos de Salud base.
         */
        public double getHpBase() {
                return psNivel0;
        }

        /**
         * Obtiene el Ataque base (Nivel 0).
         * 
         * @return Puntos de Ataque base.
         */
        public double getAtqBase() {
                return atqNivel0;
        }

        /**
         * Obtiene la Velocidad base (Nivel 0).
         * 
         * @return Puntos de Velocidad base.
         */
        public double getVelBase() {
                return velNivel0;
        }

        /**
         * Obtiene el tipo elemental.
         * 
         * @return Tipo del Pokémon.
         */
        public String getTipo() {
                return tipo;
        }

        /**
         * Calcula las estadísticas de PS según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Salud calculados.
         */
        public int calcularPS(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return psNivel0;
                } else {
                        return psNivel0 + psBonus;
                }
        }

        /**
         * Calcula las estadísticas de Ataque según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Ataque calculados.
         */
        public int calcularAtaque(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return atqNivel0;
                } else {
                        return atqNivel0 + atqBonus;
                }
        }

        /**
         * Calcula las estadísticas de Defensa según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Defensa calculados.
         */
        public int calcularDefensa(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return defNivel0;
                } else {
                        return defNivel0 + defBonus;
                }
        }

        /**
         * Calcula las estadísticas de Ataque Especial según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Ataque Especial calculados.
         */
        public int calcularAtaqueEspecial(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return ateNivel0;
                } else {
                        return ateNivel0 + ateBonus;
                }
        }

        /**
         * Calcula las estadísticas de Defensa Especial según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Defensa Especial calculados.
         */
        public int calcularDefensaEspecial(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return dfeNivel0;
                } else {
                        return dfeNivel0 + dfeBonus;
                }
        }

        /**
         * Calcula las estadísticas de Velocidad según el nivel de investigación.
         * 
         * @param nivelInvestigacion Nivel de investigación (0-10).
         * @return Puntos de Velocidad calculados.
         */
        public int calcularVelocidad(int nivelInvestigacion) {
                if (nivelInvestigacion < 5) {
                        return velNivel0;
                } else {
                        return velNivel0 + velBonus;
                }
        }
}
