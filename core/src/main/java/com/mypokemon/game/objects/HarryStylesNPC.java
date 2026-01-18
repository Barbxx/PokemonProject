package com.mypokemon.game.objects;

public class HarryStylesNPC extends NPC {
    private static final String[] DIALOG = {
            "¡Hola, cariño! Bienvenido a la región de Hisui. Es un lugar verdaderamente maravilloso, ¿no te parece? Pero también tiene su lado salvaje, y me gustaría que te sientas seguro y con mucha confianza allá afuera.",
            "Me encantaría que trabajaras en tu propia autonomía. Tu inventario es donde sucede la magia, pero por favor, sé cuidadoso con el espacio; queremos que todo fluya con orden.",
            "¿Por qué no vas a buscar algunos Guijarros y Plantas? Podrás crear unas Poké Balls realmente encantadoras.\n¡Vamos, adelante!",
            "Trata a todos con amabilidad... ¡ese Pokédex no se va a completar solo! Con amor, H."
    };

    public HarryStylesNPC(float startX, float startY) {
        super(startX, startY, 25, 35, "harryStylesSprite.png", "Cartógrafo Harry Styles", DIALOG);
        setPortrait("harryStyles.png");
    }
}
