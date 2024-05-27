package src.generalClasses;

import java.awt.*;
import java.util.ArrayList;

import src.Characters.Enemy;
import src.Objects.Button;
import src.Objects.DamagingObject;
import src.Objects.Door;
import src.Objects.Key;

import static src.GameEngine.loadImage;

public class Level {
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Key> keys = new ArrayList<>();
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final ArrayList<DamagingObject> obstacles = new ArrayList<>();
    private final ArrayList<Door> doors = new ArrayList<>();

    private Door leftDoor = null, topDoor = null, rightDoor = null, bottomDoor = null;
    private Image image;

    public void setDoors(ArrayList<String> config) {
        for (String s : config) {
            System.out.println("Setting door: " + s);
            switch (s) {
                case "Up":
                    topDoor = new Door((500 / 2) - 20, 10, 55, 30);
                    Image topDoorImage = loadImage("resources/Objects/DoorTop.png");
                    if (topDoorImage == null) {
                        System.err.println("Error: cannot load image from path: resources/Objects/DoorTop.png");
                    }
                    topDoor.setImage(topDoorImage);
                    doors.add(topDoor);
                    break;
                case "Right":
                    rightDoor = new Door(500 - 42, 500 / 2 - 20, 30, 50);
                    Image rightDoorImage = loadImage("resources/Objects/DoorRight.png");
                    if (rightDoorImage == null) {
                        System.err.println("Error: cannot load image from path: resources/Objects/DoorRight.png");
                    }
                    rightDoor.setImage(rightDoorImage);
                    doors.add(rightDoor);
                    System.out.println("Added right door at (" + rightDoor.getPosX() + ", " + rightDoor.getPosY() + ")");
                    break;
                case "Down":
                    bottomDoor = new Door((500 / 2) - 20, 500 - 42, 50, 30);
                    Image bottomDoorImage = loadImage("resources/Objects/DoorBottom.png");
                    if (bottomDoorImage == null) {
                        System.err.println("Error: file does not exist at path: resources/Objects/DoorBottom.png");
                    }
                    bottomDoor.setImage(bottomDoorImage);
                    doors.add(bottomDoor);
                    System.out.println("Added down door at (" + bottomDoor.getPosX() + ", " + bottomDoor.getPosY() + ")");
                    break;
                case "Left":
                    leftDoor = new Door(10, 500 / 2 - 20, 30, 50);
                    Image leftDoorImage = loadImage("resources/Objects/DoorLeft.png");
                    if (leftDoorImage == null) {
                        System.err.println("Error: file does not exist at path: resources/Objects/DoorLeft.png");
                    }
                    leftDoor.setImage(leftDoorImage);
                    doors.add(leftDoor);
                    System.out.println("Added left door at (" + leftDoor.getPosX() + ", " + leftDoor.getPosY() + ")");
                    break;
            }
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Key> getKeys() {
        return keys;
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
}