package com.mypokemon.game.inventario.interfaces;

// Interfaz para ítems que pueden proporcionar información detallada. Opcional para ítems que necesiten mostrar información adicional.
public interface IInformativo {

    /**
     * Obtiene información detallada sobre el ítem.
     * 
     * @return Descripción detallada del ítem
     */
    String getInfoDetallada();
}
