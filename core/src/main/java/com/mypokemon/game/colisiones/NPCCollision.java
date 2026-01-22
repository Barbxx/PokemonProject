package com.mypokemon.game.colisiones;

import com.badlogic.gdx.math.Rectangle;
import com.mypokemon.game.objects.NPC;

// Adaptador de colisión para entidades NPC. Permite que un NPC sea tratado como un objeto colisionable e interactivo dentro del sistema.

public class NPCCollision extends ColisionBase implements IInteractivo {

    // Instancia del NPC asociado a esta colisión.
    private NPC npc;

    // Distancia máxima a la que se puede hablar con el NPC.
    private float rangoInteraccion = 50f;

    // Crea un nuevo envoltorio de colisión para el NPC especificado.
    public NPCCollision(NPC npc) {
        this.npc = npc;
        // Crear Rectangle desde la posición y tamaño del NPC
        this.limites = new Rectangle(npc.getX(), npc.getY(), npc.getAncho(), npc.getAlto());
        this.tipo = "NPC";
    }

    // Delega la comprobación al método estaCerca del propio NPC.
    @Override
    public boolean estaEnRango(float x, float y) {
        return npc.estaCerca(x, y);
    }

    // Método para cumplir con la interfaz
    @Override
    public void interactuar() {
        // La interacción con NPCs se maneja en GameScreen
    }

    // Obtiene el rango de interacción.
    @Override
    public float obtenerRangoInteraccion() {
        return rangoInteraccion;
    }

    // Obtiene el mensaje de interacción.
    @Override
    public String obtenerMensajeInteraccion() {
        return "Presiona [T] para hablar con " + npc.getNombre();
    }

    // Obtiene el NPC asociado a esta colisión.
    public NPC obtenerNPC() {
        return npc;
    }
}
