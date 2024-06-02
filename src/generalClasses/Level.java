package src.generalClasses;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import src.Characters.Enemy;
import src.Objects.*;
import src.Objects.Button;
import src.Objects.Object;

import static src.GameEngine.loadImage;

public class Level {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Key> keys = new ArrayList<>();
    private ArrayList<Button> buttons = new ArrayList<>();
    private ArrayList<DamagingObject> obstacles = new ArrayList<>();
    private ArrayList<Door> doors = new ArrayList<>();
    private final ArrayList<InvisibleWall> invisibleWalls = new ArrayList<>();  // Properly declare the invisible walls list
    private String name;
    private Door topDoor = null,
            bottomDoor = null,
            leftDoor = null,
            rightDoor = null;
    private Image image;
    private int hash;

    public void setDoors(ArrayList<String> config)
    {
        for (String s : config)
        {
            switch (s)
            {
                case "Up":
                    topDoor = new Door((500 / 2) - 20, 10, 55, 30);
                    topDoor.setImage(loadImage("resources/Objects/DoorTop.png"));
                    doors.add(topDoor);
                    break;
                case "Right":
                    rightDoor = new Door(500 - 42, 500 / 2 - 20, 30, 50);
                    rightDoor.setImage(loadImage("resources/Objects/DoorRight.png"));
                    doors.add(rightDoor);
                    break;
                case "Down":
                    bottomDoor = new Door((500 / 2) - 20, 500 - 42, 50, 30);
                    bottomDoor.setImage(loadImage("resources/Objects/DoorBottom.png"));
                    doors.add(bottomDoor);
                    break;
                case "Left":
                    leftDoor = new Door(10, 500 / 2 - 20, 30, 50);
                    leftDoor.setImage(loadImage("resources/Objects/DoorLeft.png"));
                    doors.add(leftDoor);
                    break;
            }
        }
    }
    //DO NOT REMOVE THIS - stops levels from being referenced in memory
    //stops doors being shared
    public Level(Level l)
    {
        this.hash = Objects.hash(l);
        this.image = l.image;
        // Create new instances for mutable objects
        this.topDoor = l.topDoor != null ? new Door((500 / 2) - 20, 10, 55, 30) : null;
        this.bottomDoor = l.bottomDoor != null ? new Door((500 / 2) - 20, 500 - 42, 50, 30) : null;
        this.rightDoor = l.rightDoor != null ? new Door(500 - 42, 500 / 2 - 20, 30, 50) : null;
        this.leftDoor = l.leftDoor != null ? new Door(10, 500 / 2 - 20, 30, 50) : null;

        if(this.topDoor != null)
        {
            this.topDoor.setImage(l.topDoor.getImage());
            this.doors.add(this.topDoor);
        }
        if(this.bottomDoor != null)
        {
            this.bottomDoor.setImage(l.bottomDoor.getImage());
            this.doors.add(this.bottomDoor);
        }
        if(this.leftDoor != null)
        {
            this.leftDoor.setImage(l.leftDoor.getImage());
            this.doors.add(this.leftDoor);
        }
        if(this.rightDoor != null)
        {
            this.rightDoor.setImage(l.rightDoor.getImage());
            this.doors.add(this.rightDoor);
        }

        this.buttons = new ArrayList<>(l.buttons);
        this.enemies = new ArrayList<>(l.enemies);
        this.obstacles = new ArrayList<>(l.obstacles);
        this.keys = new ArrayList<>(l.keys);
        this.name = l.name;
    }

    public boolean isPositionClear(Rectangle rect) {
        for (InvisibleWall wall : getInvisibleWalls()) {
            if (wall.getHitbox().intersects(rect)) {
                return false;
            }
        }
        for (DamagingObject obstacle : getObstacles()) {
            if (obstacle.getHitbox().intersects(rect)) {
                return false;
            }
        }
        // Add checks for other types of obstacles if necessary
        return true;
    }

    // Add invisible wall to the list
    public void addInvisibleWall(InvisibleWall wall) {
        invisibleWalls.add(wall);
    }

    // Get the list of invisible walls
    public ArrayList<InvisibleWall> getInvisibleWalls() {
        return invisibleWalls;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Key> getKeys() {
        return keys;
    }

    public void addKey(Key key) {
        keys.add(key);
    }

    public void removeKey(Key key) {
        keys.remove(key);
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public ArrayList<DamagingObject> getObstacles() {
        return obstacles;
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Door getLeftDoor() {
        return leftDoor;
    }

    public void setLeftDoor(Door leftDoor) {
        this.leftDoor = leftDoor;
    }

    public Door getRightDoor() {
        return rightDoor;
    }

    public void setRightDoor(Door rightDoor) {
        this.rightDoor = rightDoor;
    }

    public Door getTopDoor() {
        return topDoor;
    }

    public void setTopDoor(Door topDoor) {
        this.topDoor = topDoor;
    }

    public Door getBottomDoor() {
        return bottomDoor;
    }

    public void setBottomDoor(Door bottomDoor) {
        this.bottomDoor = bottomDoor;
    }

    public Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getHash()
    {
        return hash;
    }

    public void setFinalDoor(String doorType)
    {
        if ("up".equals(doorType) && this.getTopDoor() != null)
        {
            this.getTopDoor().setImage(loadImage("resources/Objects/ExitDoorTop.png"));
            this.getTopDoor().setIsFinalDoor(true);
        }
        if ("down".equals(doorType) && this.getBottomDoor() != null)
        {
            this.getBottomDoor().setImage(loadImage("resources/Objects/ExitDoorBottom.png"));
            this.getBottomDoor().setIsFinalDoor(true);
        }
        if ("left".equals(doorType) && this.getLeftDoor() != null)
        {
            this.getLeftDoor().setImage(loadImage("resources/Objects/ExitDoorLeft.png"));
            this.getLeftDoor().setIsFinalDoor(true);
        }
        if ("right".equals(doorType) && this.getRightDoor() != null)
        {
            this.getRightDoor().setImage(loadImage("resources/Objects/ExitDoorRight.png"));
            this.getRightDoor().setIsFinalDoor(true);
        }
    }

    public void removeFinalDoor(String doorType)
    {
        if ("up".equals(doorType) && this.getTopDoor() != null && this.getTopDoor().getIsFinalDoor())
        {
            this.getTopDoor().setImage(loadImage("resources/Objects/DoorTop.png"));
            this.getTopDoor().setIsFinalDoor(false);
        }
        if ("down".equals(doorType) && this.getBottomDoor() != null && this.getBottomDoor().getIsFinalDoor())
        {
            this.getBottomDoor().setImage(loadImage("resources/Objects/DoorBottom.png"));
            this.getBottomDoor().setIsFinalDoor(false);
        }
        if ("left".equals(doorType) && this.getLeftDoor() != null && this.getLeftDoor().getIsFinalDoor())
        {
            this.getLeftDoor().setImage(loadImage("resources/Objects/DoorLeft.png"));
            this.getLeftDoor().setIsFinalDoor(false);
        }
        if ("right".equals(doorType) && this.getRightDoor() != null && this.getRightDoor().getIsFinalDoor())
        {
            this.getRightDoor().setImage(loadImage("resources/Objects/DoorRight.png"));
            this.getRightDoor().setIsFinalDoor(false);
        }
    }

    // Generate a hash for the room based on its properties
    public int generateRoomHash()
    {
        return Objects.hash
        (
            name,
            topDoor != null ? topDoor.hashCode() : 0,
            bottomDoor != null ? bottomDoor.hashCode() : 0,
            leftDoor != null ? leftDoor.hashCode() : 0,
            rightDoor != null ? rightDoor.hashCode() : 0,
            enemies,
            keys,
            buttons,
            obstacles,
            invisibleWalls
        );
    }

    @Override
    public int hashCode() {
        return generateRoomHash();
    }


}
