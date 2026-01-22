package com.mypokemon.game.inventario;

// Representa una receta de crafteo.
// Actúa como DTO (Data Transfer Object) - solo contiene datos, sin lógica.
public class Receta {
    private String idResultado;
    private String nombreResultado;
    public int reqPlantas;
    public int reqGuijarros;
    public int reqBayas;

    /**
     * Constructor de la receta.
     * 
     * @param idResultado     ID del ítem que produce la receta.
     * @param nombreResultado Nombre legible del resultado.
     * @param plantas         Cantidad de plantas requeridas.
     * @param guijarros       Cantidad de guijarros requeridos.
     * @param bayas           Cantidad de bayas requeridas.
     */
    public Receta(String idResultado, String nombreResultado, int plantas, int guijarros, int bayas) {
        this.idResultado = idResultado;
        this.nombreResultado = nombreResultado;
        this.reqPlantas = plantas;
        this.reqGuijarros = guijarros;
        this.reqBayas = bayas;
    }

    public String getIdResultado() {
        return idResultado;
    }

    public String getNombreResultado() {
        return nombreResultado;
    }

}
