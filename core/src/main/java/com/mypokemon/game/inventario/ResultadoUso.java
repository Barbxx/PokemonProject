package com.mypokemon.game.inventario;

import java.io.Serializable;

// Clase que representa el resultado de usar un ítem.
// Contiene información sobre si el uso fue exitoso, un mensaje descriptivo, y si el ítem debe ser consumido.

public class ResultadoUso implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean exito;
    private final String mensaje;
    private final boolean consumirItem;

    /**
     * Constructor para crear un resultado de uso.
     * 
     * @param exito        true si el uso fue exitoso.
     * @param mensaje      Mensaje descriptivo del resultado.
     * @param consumirItem true si el ítem debe ser consumido.
     */
    public ResultadoUso(boolean exito, String mensaje, boolean consumirItem) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.consumirItem = consumirItem;
    }

    /**
     * Indica si el uso del ítem fue exitoso.
     * 
     * @return true si tuvo éxito.
     */
    public boolean isExito() {
        return exito;
    }

    /**
     * Obtiene el mensaje descriptivo del resultado.
     * 
     * @return Mensaje de texto.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Indica si el ítem debe ser consumido tras el uso.
     * 
     * @return true si se debe reducir la cantidad del ítem.
     */
    public boolean isConsumirItem() {
        return consumirItem;
    }

    /**
     * Crea un resultado exitoso que consume el ítem.
     * 
     * @param mensaje Detalle del éxito.
     * @return Instancia de ResultadoUso.
     */
    public static ResultadoUso exito(String mensaje) {
        return new ResultadoUso(true, mensaje, true);
    }

    /**
     * Crea un resultado fallido (no consume el ítem).
     * 
     * @param mensaje Razón del fallo.
     * @return Instancia de ResultadoUso.
     */
    public static ResultadoUso fallo(String mensaje) {
        return new ResultadoUso(false, mensaje, false);
    }

    /**
     * Crea un resultado exitoso que NO consume el ítem.
     * 
     * @param mensaje Detalle del éxito.
     * @return Instancia de ResultadoUso.
     */
    public static ResultadoUso exitoSinConsumir(String mensaje) {
        return new ResultadoUso(true, mensaje, false);
    }
}
