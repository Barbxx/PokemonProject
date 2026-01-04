package com.mypokemon.game.logic;

public class Receta {
    public String nombreResultado;
    public int reqPlantas, reqGuijarros, reqBayas;

    public Receta(String nombre, int p, int g, int b) {
        this.nombreResultado = nombre;
        this.reqPlantas = p;
        this.reqGuijarros = g;
        this.reqBayas = b;
    }
}
