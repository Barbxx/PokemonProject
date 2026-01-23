package com.mypokemon.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Representa un Pokémon con sus estadísticas, movimientos y estado de batalla.
 * Incluye gestión de HP, ataques, nivel de investigación y modificadores
 * temporales.
 */
public class Pokemon implements Serializable {
    private String nombre;
    private int nivel;
    private float hpMaximo;
    private float hpActual;
    private float ataque;
    private float velocidad;
    private String descripcion;
    private String[] inmunidades;
    private boolean esLegendario;
    private boolean debilitado;
    private String tipo;
    private List<Movimiento> movimientos;
    private transient float modificadorAtaqueTemporal = 0; // Para el Elixir de Piel de Piedra

    // Para LibGDX (marcar como transient para que no intente serializar la imagen)
    private transient TextureRegion sprite;

    /**
     * Constructor de Pokémon que inicializa sus estadísticas basadas en el nivel de
     * investigación.
     *
     * @param nombre             Nombre del Pokémon
     * @param nivelInvestigacion Nivel de investigación del 0 al 10 que determina
     *                           las estadísticas
     * @param hpMaximo           HP máximo del Pokémon
     * @param esLegendario       Indica si es un Pokémon legendario
     * @param tipo               Tipo elemental del Pokémon
     */
    public Pokemon(String nombre, int nivelInvestigacion, float hpMaximo, boolean esLegendario, String tipo) {
        this.nombre = nombre;
        this.nivel = nivelInvestigacion;
        this.hpMaximo = hpMaximo;
        this.hpActual = hpMaximo;
        this.esLegendario = esLegendario;
        this.tipo = tipo;
        this.movimientos = new ArrayList<>();
        this.debilitado = false;

        // Cargar datos base si existen
        BasePokemonData data = BasePokemonData.get(nombre);
        if (data != null) {
            this.descripcion = data.descripcion;
            this.inmunidades = data.inmunidades;
            this.tipo = data.tipo;

            // Calcular estadísticas basadas en el nivel de investigación (0-10)
            this.hpMaximo = data.calcularPS(nivelInvestigacion);
            this.hpActual = this.hpMaximo;
            this.ataque = data.calcularAtaque(nivelInvestigacion);
            this.velocidad = data.calcularVelocidad(nivelInvestigacion);

            // Agregar movimientos iniciales
            for (String movNombre : data.movimientosIniciales) {
                AtaqueData ataqueData = AtaqueData.get(movNombre);
                if (ataqueData != null) {
                    this.agregarMovimiento(
                            new Movimiento(movNombre, ataqueData.poder, ataqueData.tipo, ataqueData.precision));
                }
            }

            // Si el nivel de investigación es 5 o mayor, desbloquear los movimientos especiales
            if (nivelInvestigacion >= 5 && data.movimientosNivel5 != null) {
                for (String movExtra : data.movimientosNivel5) {
                    AtaqueData ataqueData = AtaqueData.get(movExtra);
                    if (ataqueData != null) {
                        this.agregarMovimiento(new Movimiento(movExtra, ataqueData.poder, ataqueData.tipo,
                                ataqueData.precision));
                    }
                }
            }
        }
    }

    /**
     * Establece el sprite visual del Pokemon.
     *
     * @param sprite Region de textura para renderizar el Pokemon
     */
    public void setSprite(TextureRegion sprite) {
        this.sprite = sprite;
    }

    /**
     * Obtiene el sprite visual del Pokemon.
     *
     * @return Region de textura del Pokemon
     */
    public TextureRegion getSprite() {
        return sprite;
    }

    /**
     * Obtiene el HP actual del Pokemon.
     *
     * @return HP actual
     */
    public float getHpActual() {
        return hpActual;
    }

    /**
     * Obtiene el HP maximo del Pokemon.
     *
     * @return HP maximo
     */
    public float getHpMaximo() {
        return hpMaximo;
    }

    /**
     * Obtiene el nombre del Pokemon.
     *
     * @return Nombre del Pokemon
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Verifica si el Pokémon está debilitado.
     *
     * @return true si está debilitado, false en caso contrario
     */
    public boolean isDebilitado() {
        return debilitado;
    }

    /**
     * Obtiene el nivel de investigacion del Pokemon.
     *
     * @return Nivel de investigacion del 0 al 10
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Verifica si el Pokemon es legendario.
     *
     * @return true si es legendario, false en caso contrario
     */
    public boolean isLegendario() {
        return esLegendario;
    }

    /**
     * Obtiene el tipo elemental del Pokémon.
     *
     * @return Tipo del Pokémon
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene el ataque total del Pokémon incluyendo modificadores temporales.
     *
     * @return Valor de ataque total
     */
    public float getAtaque() {
        return ataque + modificadorAtaqueTemporal;
    }

    /**
     * Obtiene el modificador temporal de ataque actual.
     *
     * @return Valor del modificador temporal
     */
    public float getModificadorAtaqueTemporal() {
        return modificadorAtaqueTemporal;
    }

    /**
     * Establece un modificador temporal de ataque.
     *
     * @param mod Valor del modificador a aplicar
     */
    public void setModificadorAtaqueTemporal(float mod) {
        this.modificadorAtaqueTemporal = mod;
    }

    /**
     * Reinicia todos los modificadores temporales a 0.
     */
    public void resetModificadoresTemporales() {
        this.modificadorAtaqueTemporal = 0;
    }

    /**
     * Obtiene la velocidad del Pokemon.
     *
     * @return Valor de velocidad
     */
    public float getVelocidad() {
        return velocidad;
    }

    /**
     * Obtiene la descripcion del Pokemon.
     *
     * @return Texto descriptivo del Pokemon
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el array de inmunidades del Pokemon.
     *
     * @return Array de tipos a los que es inmune
     */
    public String[] getInmunidades() {
        return inmunidades;
    }

    /**
     * Aplica dano al Pokemon y verifica si queda debilitado.
     *
     * @param cantidad Cantidad de dano a recibir
     */
    public void recibirDaño(float cantidad) {
        this.hpActual -= cantidad;
        if (this.hpActual <= 0) {
            this.hpActual = 0;
            this.debilitado = true;
        }
    }

    /**
     * Ejecuta un movimiento contra un Pokémon objetivo.
     *
     * @param indice   Índice del movimiento en la lista de movimientos
     * @param objetivo Pokémon que recibirá el ataque
     */
    public void usarMovimiento(int indice, Pokemon objetivo) {
        if (indice >= 0 && indice < movimientos.size()) {
            Movimiento mov = movimientos.get(indice);
            System.out.println(this.nombre + " usa " + mov.getNombre() + "!");

            int dañoRealizado = mov.ejecutar(this, objetivo);

            if (dañoRealizado > 0) {
                System.out.println("¡Es muy efectivo! Daño: " + dañoRealizado);
            } else {
                System.out.println("¡El ataque falló!");
            }

            if (objetivo.hpActual <= 0) {
                // Evento Misión 2
                System.out.println("¡" + objetivo.nombre + " ha sido derrotado! +1 Punto de Investigación (Misión 2)");
            }
        }
    }

    /**
     * Intenta capturar el Pokémon basándose en su HP actual y el ratio de la
     * Pokeball.
     *
     * @param ratioBala Efectividad de la Pokeball usada
     * @return true si la captura fue exitosa, false en caso contrario
     */
    public boolean intentarCaptura(float ratioBala) {
        // Lógica para determinar si el Pokémon se captura

        if (debilitado)
            return false;

        float porcentajeVida = hpActual / hpMaximo;

        double chance = (1.0 - (porcentajeVida * 0.8)) * ratioBala;

        return Math.random() < chance;
    }

    /**
     * Recupera HP del Pokémon y elimina el estado debilitado si aplica.
     *
     * @param cantidad Cantidad de HP a recuperar
     */
    public void recuperarSalud(float cantidad) {
        // Utilizado cuando el jugador usa una Baya desde el Inventario.
        this.hpActual += cantidad;
        if (this.hpActual > hpMaximo) {
            this.hpActual = hpMaximo;
        }
        // Si estaba debilitado y se cura, deja de estar debilitado.
        if (this.hpActual > 0) {
            this.debilitado = false;
        }
    }

    /**
     * Calcula los puntos de investigación ganados según si fue capturado o
     * derrotado.
     *
     * @param fueCapturado true si fue capturado, false si solo fue derrotado
     * @return 2 puntos si fue capturado, 1 punto si fue derrotado
     */
    public int calcularPuntosInvestigacion(boolean fueCapturado) {
        // Lógica para Hito Final
        return fueCapturado ? 2 : 1;
    }

    /**
     * Agrega un movimiento a la lista de movimientos del Pokemon.
     *
     * @param mov Movimiento a agregar
     */
    public void agregarMovimiento(Movimiento mov) {
        this.movimientos.add(mov);
    }

    /**
     * Obtiene la lista de movimientos del Pokemon.
     *
     * @return Lista de movimientos disponibles
     */
    public List<Movimiento> getMovimientos() {
        return movimientos;
    }
}
