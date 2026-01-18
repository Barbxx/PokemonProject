package com.mypokemon.game.objects;

public class BrennerNPC extends NPC {
    private static final String[] DIALOG = {
            "Has llegado lejos, 'Once', o como sea que te llamen aquí.",
            "Has recolectado datos de 5 especies diferentes, un progreso fascinante. Pero ahora debes completar una última misión.",
            "El sujeto Arceus te espera en esta cavidad que me recuerda a la Cueva de Vecna; siento la misma oscuridad y el tic-tac de un reloj.",
            "Si logras vencerlo, tu investigación será completada instantáneamente.",
            "Recuerda: los errores tienen consecuencias, y si pierdes, el 'Upside Down' reclamará tus pertenencias."
    };

    public BrennerNPC(float startX, float startY) {
        super(startX, startY, 25, 35, "drBrennerSprite.png", "Dr. Martin Brenner", DIALOG);
        setPortrait("drBrenner.png");
    }
}
