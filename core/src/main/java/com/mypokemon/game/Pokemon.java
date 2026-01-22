package com.mypokemon.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Representa una instancia individual de un Pokémon en el juego.
 * Contiene estadísticas dinámicas, movimientos y estado de salud.
 */
public class Pokemon implements Serializable {
    /** Nombre de la especie del Pokémon. */
    private String nombre;
    /** Nivel de investigación (o nivel base) del Pokémon. */
    private int nivel;
    /** Puntos de salud máximos. */
    private float hpMaximo;
    /** Puntos de salud actuales. */
    private float hpActual;
    /** Valor de ataque utilizado para calcular el daño. */
    private float ataque;
    /** Velocidad que determina el orden de los turnos en combate. */
    private float velocidad;
    /** Descripción biográfica del Pokémon. */
    private String descripcion;
    /** Tipos contra los que este Pokémon no recibe daño. */
    private String[] inmunidades;
    /** Indica si es un Pokémon legendario (como Arceus). */
    private boolean esLegendario;
    /** Indica si el Pokémon ha quedado fuera de combate (0 HP). */
    private boolean debilitado;
    /** Tipo elemental principal del Pokémon. */
    private String tipo;
    /** Lista de movimientos que el Pokémon puede usar en batalla. */
    private List<Movimiento> movimientos;
    /**
     * Modificador temporal de ataque (aplicado por ítems como el Elixir). No se
     * persiste.
     */
    private transient float modificadorAtaqueTemporal = 0;

    // Para LibGDX (marcar como transient para que no intente serializar la imagen)
    private transient TextureRegion sprite;

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
        DatosBasePokemon data = DatosBasePokemon.get(nombre);
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
                DatosAtaque ataqueData = DatosAtaque.get(movNombre);
                if (ataqueData != null) {
                    this.agregarMovimiento(
                            new Movimiento(movNombre, ataqueData.poder, ataqueData.tipo, ataqueData.precision));
                }
            }

            // Si el nivel de investigación es 5 o mayor, desbloquear los movimientos
            // especiales
            if (nivelInvestigacion >= 5 && data.movimientosNivel5 != null) {
                for (String movExtra : data.movimientosNivel5) {
                    DatosAtaque ataqueData = DatosAtaque.get(movExtra);
                    if (ataqueData != null) {
                        this.agregarMovimiento(new Movimiento(movExtra, ataqueData.poder, ataqueData.tipo,
                                ataqueData.precision));
                    }
                }
            }
        }
    }

    public void tocarGrito() {
        // Implementación básica de grito de pokemon (puede ser un sonido en el futuro)
        System.out.println("¡" + this.nombre + " lanza un grito!");
    }

    public void setSprite(TextureRegion sprite) {
        this.sprite = sprite;
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    // Getters necessary for logic potentially
    public float getHpActual() {
        return hpActual;
    }

    public float getHpMaximo() {
        return hpMaximo;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isDebilitado() {
        return debilitado;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean isLegendario() {
        return esLegendario;
    }

    public String getTipo() {
        return tipo;
    }

    public float getAtaque() {
        return ataque + modificadorAtaqueTemporal;
    }

    public float getModificadorAtaqueTemporal() {
        return modificadorAtaqueTemporal;
    }

    public void setModificadorAtaqueTemporal(float mod) {
        this.modificadorAtaqueTemporal = mod;
    }

    public void resetModificadoresTemporales() {
        this.modificadorAtaqueTemporal = 0;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String[] getInmunidades() {
        return inmunidades;
    }

    public void recibirDaño(float cantidad) {
        this.hpActual -= cantidad;
        if (this.hpActual <= 0) {
            this.hpActual = 0;
            this.debilitado = true;
        }
    }

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

    public boolean intentarCaptura(float ratioBala) {
        // Lógica para determinar si el Pokémon entra en la Poké Ball (Misión 2: +2
        // puntos de progreso)
        // Probabilidad aumenta a medida que disminuye la vida.
        if (debilitado)
            return false; // No se puede capturar si está debilitado (en algunos juegos, en otros sí,
                          // asumimos lógica estándar)

        float porcentajeVida = hpActual / hpMaximo;
        // Si tiene 100% vida, dificultad es alta. Si tiene 1%, dificultad baja.
        // Base chance: Si tiene 1 hp (casi 0), chance ~ 1.0 * ratio.
        // Si tiene full hp, chance ~ 0.0 * ratio (imposible).
        // Ajustemos para que siempre haya probabilidad.

        double chance = (1.0 - (porcentajeVida * 0.8)) * ratioBala;
        // Ejemplo: Full HP (1.0) -> chance = 0.2 * ratio. Low HP (0.1) -> chance = 0.92
        // * ratio.

        return Math.random() < chance;
    }

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

    public int calcularPuntosInvestigacion(boolean fueCapturado) {
        // Lógica para la Misión 2 y Hito Final
        return fueCapturado ? 2 : 1;
    }

    public void agregarMovimiento(Movimiento mov) {
        this.movimientos.add(mov);
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }
}
