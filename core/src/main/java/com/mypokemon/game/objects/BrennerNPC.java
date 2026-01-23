package com.mypokemon.game.objects;

/**
 * NPC que representa al Dr. Martin Brenner (Stranger Things).
 * Proporciona información sobre Arceus y el Upside Down.
 */
public class BrennerNPC extends NPC {
    private static final String[] DIALOG = {
            "Has llegado lejos Once, siempre supe que eras especial.",
            "Si ya has llegado hasta este punto, es porque deberías tener tus cinco especies al máximo nivel de investigación… ¿cierto? No me decepciones, sabes que no me gusta que me ocultes progresos.",
            "Si es así, el Sujeto Arceus te espera en esa cueva de allá... me recuerda mucho a la Cueva de Vecna. Puedo sentirlo desde aquí, es fascinante, pero extremadamente peligroso si no estás preparado...",
            "Si no has alcanzado el potencial que espero de ti, te recomiendo que regreses a las otras zonas para seguir subiendo de nivel. No podemos permitirnos un fallo en la fase de pruebas.",
            "Si logras vencerlo, su investigación será completada instantáneamente. Sería un logro científico sin precedentes, hijo mío.",
            "Y recuerda, en este experimento los errores tienen consecuencias muy reales... si pierdes, el 'Upside Down' reclamará tus pertenencias... quizás varios objetos de tu inventario...",
            "No dejes que el mundo se desmorone por un descuido."
    };

    /**
     * Constructor de BrennerNPC.
     * 
     * @param startX Posición X inicial.
     * @param startY Posición Y inicial.
     */
    public BrennerNPC(float startX, float startY) {
        super(startX, startY, 25, 35, "drBrennerSprite.png", "Dr. Martin Brenner", DIALOG);
        setPortrait("drBrenner.png");
        loadMusic("MusicaDrBrenner.mp3");
    }
}
