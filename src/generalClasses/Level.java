package src.generalClasses;

import java.awt.*;
import java.util.ArrayList;

import src.Direction;
import src.GameEngine;

public class Level
{
    ArrayList<Rectangle> doors = new ArrayList<>();

    Rectangle leftDoor = new Rectangle(0, 500 / 2 - 15, 30, 50),
            topDoor = new Rectangle((500 / 2) - 20, 0 , 55, 30),
            rightDoor = new Rectangle(500 - 30, 500 / 2 - 15, 30, 50),
            bottomDoor = new Rectangle((500 / 2) - 20, 500-30, 50, 30);

    private int index;
    private Image image;

    public void setDoors(ArrayList<String> config)
    {
        for(String s : config)
        {
            if(s.equals("Up"))
            {
                doors.add(topDoor);
            }
            if(s.equals("Right"))
            {
                doors.add(rightDoor);
            }
            if (s.equals("Down"))
            {
                doors.add(bottomDoor);
            }
            if (s.equals("Left"))
            {
                doors.add(leftDoor);
            }
        }
    }

    public Rectangle getTopDoor()
    {
        return topDoor;
    }
    public Rectangle getRightDoor()
    {
        return rightDoor;
    }
    public Rectangle getBottomDoor()
    {
        return bottomDoor;
    }
    public Rectangle getLeftDoor()
    {
        return leftDoor;
    }


    public Level(Image image)
    {
        this.image = image;
    };
    public Level()
    {

    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
    }
}
