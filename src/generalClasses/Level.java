package src.generalClasses;

import java.awt.*;
import java.util.ArrayList;

import src.Characters.Enemy;
import src.Direction;
import src.GameEngine;
import src.Objects.Button;
import src.Objects.DamagingObject;
import src.Objects.Door;
import src.Objects.Key;

import static src.GameEngine.loadImage;

public class Level
{
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Key> keys = new ArrayList<>();
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final ArrayList<DamagingObject> obstacles = new ArrayList<>();
    private final ArrayList<Door> doors = new ArrayList<>();

    Door leftDoor = null,
            topDoor = null,
            rightDoor = null,
            bottomDoor = null;

    private Image image;

    public void setDoors(ArrayList<String> config)
    {
        for(String s : config)
        {
            if(s.equals("Up"))
            {
                topDoor = new Door((500 / 2) - 20, 10 , 55, 30);
                topDoor.setImage(loadImage("resources/Objects/DoorTop.png"));
                doors.add(topDoor);
            }
            if(s.equals("Right"))
            {
                rightDoor = new Door(500 - 42, 500 / 2 - 20, 30, 50);
                rightDoor.setImage(loadImage("resources/Objects/DoorRight.png"));
                doors.add(rightDoor);
            }
            if (s.equals("Down"))
            {
                bottomDoor = new Door((500 / 2) - 20, 500-42, 50, 30);
                bottomDoor.setImage(loadImage("resources/Objects/DoorBottom.png"));
                doors.add(bottomDoor);
            }
            if (s.equals("Left"))
            {
                leftDoor = new Door(10, 500 / 2 - 20, 30, 50);
                leftDoor.setImage(loadImage("resources/Objects/DoorLeft.png"));
                doors.add(leftDoor);
            }
        }
    }

    public Door getTopDoor()
    {
        return topDoor;
    }
    public Door getRightDoor()
    {
        return rightDoor;
    }
    public Door getBottomDoor()
    {
        return bottomDoor;
    }
    public Door getLeftDoor()
    {
        return leftDoor;
    }
    public ArrayList<Door> getDoors() {
        return doors;
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public ArrayList<DamagingObject> getObstacles() {
        return obstacles;
    }


    public ArrayList<Enemy> getEnemies() {
        return enemies;
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
