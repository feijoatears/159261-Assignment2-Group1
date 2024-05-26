package src.Characters;

import static src.GameEngine.loadImage;

public class Vampire extends Enemy
{
    public Vampire(int x, int y, int s, int d)
    {
        super(loadImage("resources/Sprites/Vampire-SpriteSheetFinal.png"), x, y, s, d);
    }
}
