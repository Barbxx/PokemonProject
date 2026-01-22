package com.mypokemon.game.inventario;

import java.io.Serializable;

// Representa el resultado de usar un ítem (éxito, mensaje descriptivo y si debe consumirse).
public class ResultadoUso implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean exito;
    private final String mensaje;
    private final boolean consumirItem;

    // Crea un resultado de uso con el estado de éxito, mensaje y si consume el
    // ítem.
    public ResultadoUso(boolean exito, String mensaje, boolean consumirItem) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.consumirItem = consumirItem;
    }

    // Indica si el uso fue exitoso.
    public boolean isExito() {
        return exito;
    }

    // Obtiene el mensaje descriptivo del resultado.
    public String getMensaje() {
        return mensaje;
    }

    // Indica si el ítem debe ser consumido del inventario.
    public boolean isConsumirItem() {
        return consumirItem;
    }

    // Crea un resultado exitoso que consume el ítem.
    public static ResultadoUso exito(String mensaje) {
        return new ResultadoUso(true, mensaje, true);
    }

    // Crea un resultado fallido que no consume el ítem.
    public static ResultadoUso fallo(String mensaje) {
        return new ResultadoUso(false, mensaje, false);
    }

    // Crea un resultado exitoso sin consumir el ítem.
    public static ResultadoUso exitoSinConsumir(String mensaje) {
        return new ResultadoUso(true, mensaje, false);
    }
}
