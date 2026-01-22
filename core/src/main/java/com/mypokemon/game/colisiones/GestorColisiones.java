package com.mypokemon.game.colisiones;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

// Gestor centralizado de todas las colisiones del juego.
public class GestorColisiones {

    // Lista de todos los objetos que implementan IColisionable.
    private List<IColisionable> colisiones;

    // Lista de objetos con los que se puede interactuar.
    private List<IInteractivo> interactivos;

    // Capa del mapa que contiene la información de colisión del terreno.
    private TiledMapTileLayer capaColisionTerreno;

    // Inicializa las listas de colisiones e interactivos.
    public GestorColisiones() {
        colisiones = new ArrayList<>();
        interactivos = new ArrayList<>();
    }

    /**
     * Establece la capa de colisión del terreno del mapa actual.
     * 
     * @param capa La capa TiledMapTileLayer que define las colisiones.
     */
    public void establecerCapaColisionTerreno(TiledMapTileLayer capa) {
        this.capaColisionTerreno = capa;
    }

    /**
     * Agrega un objeto colisionable al gestor.
     * Si el objeto también es interactivo, se añade a la lista de interactivos.
     * 
     * @param colision El objeto colisionable a añadir.
     */
    public void agregarColision(IColisionable colision) {
        colisiones.add(colision);
        if (colision instanceof IInteractivo) {
            interactivos.add((IInteractivo) colision);
        }
    }

    /**
     * Verifica si un área específica colisiona con algún tile sólido del terreno.
     * 
     * @param x     Posición X central del área.
     * @param y     Posición Y central del área.
     * @param ancho Ancho del área.
     * @param alto  Alto del área.
     * @return true si alguna esquina del área toca un tile sólido, false en caso
     *         contrario.
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
     * Verifica si un área específica colisiona con algún NPC registrado.
     * 
     * @param x     Posición X central del área.
     * @param y     Posición Y central del área.
     * @param ancho Ancho del área.
     * @param alto  Alto del área.
     * @return true si hay colisión con un NPC, false en caso contrario.
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
     * Verifica todas las colisiones posibles (terreno + objetos dinámicos)
     * 
     * @param x     Posición X central.
     * @param y     Posición Y central.
     * @param ancho Ancho del área de verificación.
     * @param alto  Alto del área de verificación.
     * @return true si hay alguna colisión, false si el camino está despejado.
     */
    public boolean verificarTodasLasColisiones(float x, float y, float ancho, float alto) {

        // Verificar terreno
        if (verificarColisionTerreno(x, y, ancho, alto)) {
            return true;
        }

        // Verificar todos los objetos colisionables (NPCs, Puertas, etc.)
        for (IColisionable colision : colisiones) {
            if (colision.verificarColision(x, y, ancho, alto)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el objeto interactivo más cercano al jugador que se encuentre dentro
     * de su rango.
     * 
     * @param x Posición X del jugador.
     * @param y Posición Y del jugador.
     * @return El objeto interactivo más cercano en rango, o null si ninguno es
     *         accesible.
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
     * Obtiene una lista de todos los objetos interactivos que están en el rango del
     * jugador.
     * 
     * @param x Posición X del jugador.
     * @param y Posición Y del jugador.
     * @return Lista de objetos interactivos en el rango actual.
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

    // Borra todas las listas de colisiones y objetos interactivos cargados.
    public void limpiar() {
        colisiones.clear();
        interactivos.clear();
    }
}
