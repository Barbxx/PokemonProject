package com.mypokemon.game.colisiones;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor centralizado de todas las colisiones del juego.
 */
public class GestorColisiones {

    private List<IColisionable> colisiones;
    private List<IInteractivo> interactivos;
    private TiledMapTileLayer capaColisionTerreno;

    public GestorColisiones() {
        colisiones = new ArrayList<>();
        interactivos = new ArrayList<>();
    }

    /**
     * Establece la capa de colisión del terreno del mapa.
     * 
     * @param capa Capa de tiles que contiene las propiedades de colisión.
     */
    public void establecerCapaColisionTerreno(TiledMapTileLayer capa) {
        this.capaColisionTerreno = capa;
    }

    /**
     * Agrega un objeto colisionable al gestor.
     * Si el objeto también es interactivo, se registra para interacciones.
     * 
     * @param colision Objeto que implementa IColisionable.
     */
    public void agregarColision(IColisionable colision) {
        colisiones.add(colision);
        if (colision instanceof IInteractivo) {
            interactivos.add((IInteractivo) colision);
        }
    }

    /**
     * Verifica colisión con el terreno del mapa.
     * Comprueba si el rectángulo especificado solapa con algún tile sólido.
     * 
     * @param x     Posición X central.
     * @param y     Posición Y central.
     * @param ancho Ancho del área.
     * @param alto  Alto del área.
     * @return true si hay colisión con terreno.
     */
    public boolean verificarColisionTerreno(float x, float y, float ancho, float alto) {
        if (capaColisionTerreno == null)
            return false;

        // Definir esquinas del jugador para verificar
        float[] esquinas = {
                x - ancho / 2, y - alto / 2, // Inferior-izquierda
                x + ancho / 2, y - alto / 2, // Inferior-derecha
                x - ancho / 2, y + alto / 2, // Superior-izquierda
                x + ancho / 2, y + alto / 2 // Superior-derecha
        };

        for (int i = 0; i < esquinas.length; i += 2) {
            int celdaX = (int) (esquinas[i] / capaColisionTerreno.getTileWidth());
            int celdaY = (int) (esquinas[i + 1] / capaColisionTerreno.getTileHeight());

            TiledMapTileLayer.Cell celda = capaColisionTerreno.getCell(celdaX, celdaY);
            if (celda != null && celda.getTile() != null) {
                // Permitir pasar por la grama (solo usarla para encuentros, no para colisión)
                Object zonaEncuentro = celda.getTile().getProperties().get("ZonaEncuentro");
                if (zonaEncuentro != null) {
                    continue; // Ignorar colisión con grama, permitir pasar
                }
                return true; // Hay colisión con otro tipo de tile
            }
        }
        return false;
    }

    /**
     * Verifica colisión con NPCs registrados.
     * 
     * @param x     Posición X.
     * @param y     Posición Y.
     * @param ancho Ancho.
     * @param alto  Alto.
     * @return true si hay colisión.
     */
    public boolean verificarColisionNPCs(float x, float y, float ancho, float alto) {
        for (IColisionable colision : colisiones) {
            if (colision.obtenerTipo().equals("NPC")) {
                if (colision.verificarColision(x, y, ancho, alto)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifica todas las colisiones posibles (terreno y objetos).
     * 
     * @param x     Posición X.
     * @param y     Posición Y.
     * @param ancho Ancho.
     * @param alto  Alto.
     * @return true si hay alguna colisión.
     */
    public boolean verificarTodasLasColisiones(float x, float y, float ancho, float alto) {
        // Verificar terreno
        if (verificarColisionTerreno(x, y, ancho, alto)) {
            return true;
        }

        // Verificar NPCs
        if (verificarColisionNPCs(x, y, ancho, alto)) {
            return true;
        }

        return false;
    }

    /**
     * Obtiene el objeto interactivo más cercano al jugador.
     * 
     * @return El objeto interactivo más cercano, o null si ninguno está en rango
     */
    public IInteractivo obtenerInteractivoMasCercano(float x, float y) {
        IInteractivo masCercano = null;
        float distanciaMinima = Float.MAX_VALUE;

        for (IInteractivo interactivo : interactivos) {
            if (interactivo.estaEnRango(x, y)) {
                Rectangle limites = interactivo.obtenerLimites();
                float distancia = Vector2.dst(
                        x, y,
                        limites.x + limites.width / 2,
                        limites.y + limites.height / 2);
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    masCercano = interactivo;
                }
            }
        }

        return masCercano;
    }

    /**
     * Obtiene todos los objetos interactivos en rango del jugador.
     * 
     * @return Lista de objetos interactivos en rango
     */
    public List<IInteractivo> obtenerTodosEnRango(float x, float y) {
        List<IInteractivo> resultado = new ArrayList<>();
        for (IInteractivo interactivo : interactivos) {
            if (interactivo.estaEnRango(x, y)) {
                resultado.add(interactivo);
            }
        }
        return resultado;
    }

    /**
     * Limpia todas las colisiones y objetos interactivos registrados.
     */
    public void limpiar() {
        colisiones.clear();
        interactivos.clear();
    }
}
