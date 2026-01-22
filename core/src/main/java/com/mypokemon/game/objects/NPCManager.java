package com.mypokemon.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la coleccion de NPCs en el juego.
 * Maneja el ciclo de vida de los NPCs y su recuperacion.
 */
public class NPCManager {
    private List<NPC> npcs;

    /**
     * Constructor del gestor de NPCs.
     */
    public NPCManager() {
        this.npcs = new ArrayList<>();
    }

    /**
     * Agrega un NPC a la lista de NPCs gestionados.
     * 
     * @param npc NPC a agregar
     */
    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    /**
     * Obtiene la lista de todos los NPCs.
     * 
     * @return Lista de NPCs
     */
    public List<NPC> getAllNPCs() {
        return npcs;
    }

    /**
     * Renderiza todos los NPCs en pantalla.
     * 
     * @param batch SpriteBatch para renderizar
     */
    public void render(SpriteBatch batch) {
        for (NPC npc : npcs) {
            npc.render(batch);
        }
    }

    /**
     * Obtiene el NPC mas cercano al jugador dentro del rango de interaccion.
     * 
     * @param playerX Posicion X del jugador
     * @param playerY Posicion Y del jugador
     * @return NPC cercano o null si no hay ninguno cerca
     */
    public NPC getCloseNPC(float playerX, float playerY) {
        for (NPC npc : npcs) {
            if (npc.isClose(playerX, playerY)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Verifica si hay colision con algun NPC.
     * 
     * @param x Posicion X a verificar
     * @param y Posicion Y a verificar
     * @param w Ancho del area a verificar
     * @param h Alto del area a verificar
     * @return true si hay colision, false en caso contrario
     */
    public boolean checkCollision(float x, float y, float w, float h) {
        for (NPC npc : npcs) {
            if (npc.overlaps(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Libera los recursos de todos los NPCs.
     */
    public void dispose() {
        for (NPC npc : npcs) {
            npc.dispose();
        }
        npcs.clear();
    }
}
