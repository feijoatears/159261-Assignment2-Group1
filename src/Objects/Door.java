package src.Objects;

import java.awt.*;

import static src.GameEngine.loadImage;

public class Door extends Object
{
    private boolean isFinalDoor = false;

    public Door(int x, int y, int w, int h)
    {
        super(x,y,w,h);
    }

    public void setFinalDoor()
    {
        this.image = loadImage("resources/Objects/finalWallGold.png");
        isFinalDoor = true;
    }
    public boolean getIsFinalDoor()
    {
        return isFinalDoor;
    }
}
