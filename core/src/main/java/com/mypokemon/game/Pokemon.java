package com.mypokemon.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Pokemon implements Serializable {
    private String nombre;
    private int nivel;
    private float hpMaximo;
    private float hpActual;
    private boolean esLegendario;
    private boolean debilitado;
    private String tipo;
    private List<Movimiento> movimientos;

    // Para LibGDX (marcar como transient para que no intente serializar la imagen)
    private transient TextureRegion sprite;

    public Pokemon(String nombre, int nivel, float hpMaximo, boolean esLegendario, String tipo) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.hpMaximo = hpMaximo;
        this.hpActual = hpMaximo;
        this.esLegendario = esLegendario;
        this.tipo = tipo;
        this.movimientos = new ArrayList<>();
        this.debilitado = false;
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
