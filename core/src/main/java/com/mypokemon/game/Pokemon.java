package com.mypokemon.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

// Representa una instancia individual de un Pokémon en el juego. Contiene sus estadísticas, movimientos y estado.
public class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int nivel;
    private float hpMaximo, hpActual, ataque, velocidad;
    private String descripcion, tipo;
    private String[] inmunidades;
    private boolean esLegendario, debilitado;
    private List<Movimiento> movimientos;
    private transient float modificadorAtaqueTemporal = 0;
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

        DatosBasePokemon d = DatosBasePokemon.get(nombre);
        if (d != null) {
            this.descripcion = d.descripcion;
            this.inmunidades = d.inmunidades;
            this.tipo = d.tipo;
            this.hpMaximo = d.calcularPS(nivelInvestigacion);
            this.hpActual = this.hpMaximo;
            this.ataque = d.calcularAtaque(nivelInvestigacion);
            this.velocidad = d.calcularVelocidad(nivelInvestigacion);
            for (String n : d.movimientosIniciales) {
                DatosAtaque a = DatosAtaque.get(n);
                if (a != null)
                    this.movimientos.add(new Movimiento(n, a.poder, a.tipo, a.precision));
            }
            if (nivelInvestigacion >= 5 && d.movimientosNivel5 != null) {
                for (String n : d.movimientosNivel5) {
                    DatosAtaque a = DatosAtaque.get(n);
                    if (a != null)
                        this.movimientos.add(new Movimiento(n, a.poder, a.tipo, a.precision));
                }
            }
        }
    }

    public void curar(float c) {
        this.hpActual += c;
        if (this.hpActual > hpMaximo)
            this.hpActual = hpMaximo;
        if (this.hpActual > 0)
            this.debilitado = false;
    }

    public void recibirDaño(float d) {
        this.hpActual -= d;
        if (this.hpActual <= 0) {
            this.hpActual = 0;
            this.debilitado = true;
        }
    }

    public boolean intentarCaptura(float ratio) {
        if (debilitado)
            return false;
        float porcentajeHp = hpActual / hpMaximo;
        double probabilidad = (1.0 - (porcentajeHp * 0.8)) * ratio;
        return Math.random() < probabilidad;
    }

    public void reiniciarModificadoresTemporales() {
        this.modificadorAtaqueTemporal = 0;
    }

    // Getters y Setters
    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerNivel() {
        return nivel;
    }

    public float obtenerHpActual() {
        return hpActual;
    }

    public float obtenerHpMaximo() {
        return hpMaximo;
    }

    public float obtenerAtaque() {
        return ataque + modificadorAtaqueTemporal;
    }

    public float obtenerVelocidad() {
        return velocidad;
    }

    public String obtenerTipo() {
        return tipo;
    }

    public List<Movimiento> obtenerMovimientos() {
        return movimientos;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }

    public boolean esLegendario() {
        return esLegendario;
    }

    public boolean estaDebilitado() {
        return debilitado;
    }

    public void establecerModificadorAtaqueTemporal(float m) {
        this.modificadorAtaqueTemporal = m;
    }

    public float obtenerModificadorAtaqueTemporal() {
        return modificadorAtaqueTemporal;
    }

    public String[] obtenerInmunidades() {
        return inmunidades;
    }
}
