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

    public void setIsFinalDoor(boolean finalDoor)
    {
        isFinalDoor = finalDoor;
    }

    public boolean getIsFinalDoor()
    {
        return this.isFinalDoor;
    }
}
