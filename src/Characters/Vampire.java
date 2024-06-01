package src.Characters;

import static src.GameEngine.loadImage;

public class Vampire extends Enemy
{
    public Vampire(int x, int y, int s, int d)
    {
        super(x, y, s, d, "resources/Sprites/vampireFrames/vampireFrame");
    }
}
