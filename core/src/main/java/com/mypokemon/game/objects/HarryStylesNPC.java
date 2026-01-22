package com.mypokemon.game.objects;

public class HarryStylesNPC extends NPC {
    private static final String[] DIALOG = {
            "¡Hola, cariño! Bienvenido a la región OneFerxxo. Es un lugar verdaderamente maravilloso, ¿no te parece? Tiene una energía mágic, distinta a de donde vengo. Pero también tiene su lado salvaje, como habrás podido notar.",
            "Supongo que ya te encontraste con algunos pequeños amiguitos en el pasto... son criaturas adorables, ¿verdad? La mayoría de las veces son totalmente inofensivos, solo buscan un poco de amor.",
            "Oh, ¡pero mira eso! Veo que ya tienes uno acompañándote... se ven fantásticos juntos. Si quieres capturar más amigos para que se unan a nuestra familia, tengo un par de recomendaciones para ti.",
            "El inventario es donde sucede toda la magia, cielo. Allí podrás craftear objetos que te ayudarán en esta aventura, como Poké Balls, pociones de recuperación y otras cositas que te mantendrán entretenido y con el espíritu en alto durante tu viaje.",
            "¡Vamos, adelante! Y nunca lo olvides: trata a todos con amabilidad... ¡esa misión no se va a completar sola!",
            "Con todo mi amor, H."
    };

    public HarryStylesNPC(float startX, float startY) {
        super(startX - 20, startY - 15, 25, 35, "harryStylesSprite.png", "Cartógrafo Harry Styles", DIALOG);
        setPortrait("harryStyles.png");
        loadMusic("MusicaHarryStyles.mp3");
    }
}
