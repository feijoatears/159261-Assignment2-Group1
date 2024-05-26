package src.Objects;

import java.awt.*;

public class Door extends Object
{
    private boolean isLocked;

    public Door(){};
    public Door(int x, int y, int w, int h)
    {
        super(x,y,w,h);
    }
    public void setLocked(boolean locked)
    {
        isLocked = locked;
    }

    public boolean isLocked()
    {
        return isLocked;
    }
}
