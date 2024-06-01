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

    public void setFinalDoor(String doorType)
    {
        if(doorType.equals("up"))
        {
            this.image = loadImage("resources/Objects/ExitDoorTop.png");
        }
        if(doorType.equals("down"))
        {
            this.image = loadImage("resources/Objects/ExitDoorBottom.png");
        }
        if(doorType.equals("left"))
        {
            this.image = loadImage("resources/Objects/ExitDoorLeft.png");
        }
        if(doorType.equals("right"))
        {
            this.image = loadImage("resources/Objects/ExitDoorRight.png");
        }

        this.isFinalDoor = true;
    }
    public void removeFinalDoor(String doorType)
    {
        //40 x 32 img
        if(doorType.equals("up"))
        {
            if(this.image.getHeight(null) != 32 && this.getIsFinalDoor())
            {
                System.out.println("- up door");
                this.image = loadImage("resources/Objects/DoorTop.png");
            }
        }
        if(doorType.equals("down"))
        {
            if(this.image.getHeight(null) != 32 && this.getIsFinalDoor())
            {
                System.out.println("-");
                this.image = loadImage("resources/Objects/DoorBottom.png");
            }
        }
        if(doorType.equals("left"))
        {
            if(this.image.getHeight(null) != 40 && this.getIsFinalDoor())
            {
                System.out.println("-");
                this.image = loadImage("resources/Objects/DoorLeft.png");
            }
        }
        if(doorType.equals("right"))
        {
            if(this.image.getHeight(null) != 40 && this.getIsFinalDoor())
            {
                System.out.println("-");
                this.image = loadImage("resources/Objects/DoorRight.png");
            }
        }

        this.isFinalDoor = false;
    }
    public boolean getIsFinalDoor()
    {
        return this.isFinalDoor;
    }
}
