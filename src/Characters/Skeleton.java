package src.Characters;

import static src.GameEngine.loadImage;

public class Skeleton extends Enemy
{
    public Skeleton(int x, int y, int s, int d)
    {
        super(loadImage("resources/Sprites/skeletonWhite.png"),x,y,s,d);
    }
}
