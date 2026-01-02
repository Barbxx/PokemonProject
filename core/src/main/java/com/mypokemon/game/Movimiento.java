package com.mypokemon.game;

import java.io.Serializable;
import java.util.Random;

public class Movimiento implements Serializable {
    private String nombre;
    private int poder;
    private String tipo;
    private int precision;

    public Movimiento(String nombre, int poder, String tipo, int precision) {
        this.nombre = nombre;
        this.poder = poder;
        this.tipo = tipo;
        this.precision = precision;
    }

    public int ejecutar(Pokemon atacante, Pokemon defensor) {
        if (new Random().nextInt(100) < this.precision) {
            // Fórmula simplificada de daño para la simulación
            int dañoBase = (int) (((atacante.getNivel() * 0.5) + this.poder) - 5);

            // Lógica de efectividad (Ejemplo: Agua vence a Fuego)
            float multiplicador = calcularEfectividad(this.tipo, defensor.getTipo());

            int dañoFinal = (int) (dañoBase * multiplicador);
            // Ensure damage is at least 1 if hit, unless calculation intends 0
            if (dañoFinal < 1)
                dañoFinal = 1;

            defensor.recibirDaño(dañoFinal);
            return dañoFinal;
        }
        return 0; // El ataque falló
    }

    private float calcularEfectividad(String tipoAtaque, String tipoDefensor) {
        // Aquí puedes implementar una tabla de tipos sencilla
        if (tipoAtaque.equals("Agua") && tipoDefensor.equals("Fuego"))
            return 2.0f;
        if (tipoAtaque.equals("Planta") && tipoDefensor.equals("Agua"))
            return 2.0f;
        return 1.0f;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getPoder() {
        return poder;
    }

    public String getTipo() {
        return tipo;
    }

    public int getPrecision() {
        return precision;
    }
}
