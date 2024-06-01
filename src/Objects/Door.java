package src.Objects;

import java.awt.*;

public class Door extends Object
{
    private boolean isFinalDoor = false;

    public Door(int x, int y, int w, int h)
    {
        super(x,y,w,h);
    }

    public void setFinalDoor()
    {
        isFinalDoor = true;
    }
    public boolean getIsFinalDoor()
    {
        return isFinalDoor;
    }
}
