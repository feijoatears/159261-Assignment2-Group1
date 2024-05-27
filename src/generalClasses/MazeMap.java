package src.generalClasses;

import src.Objects.Door;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static src.GameEngine.loadImage;

public class MazeMap {
    private static MazeMap instance;
    private ArrayList<ArrayList<Level>> map;

    private int currentFloor, currentRoom;

    public static MazeMap getInstance() {
        if (instance == null) {
            instance = new MazeMap();
        }
        return instance;
    }

    private MazeMap() {
        map = new ArrayList<>();
    }

    public void reset() {
        map = new ArrayList<>();
    }

    public void addFloor(ArrayList<Level> levels) {
        map.add(levels);
    }

    public void addFloor(int index, ArrayList<Level> levels) {
        map.add(index, levels);
    }

    public void addLevel(boolean isAtFront, int floorIndex, Level level) {
        if (isAtFront) {
            map.get(floorIndex).add(0, level);
            return;
        }
        map.get(floorIndex).add(level);
    }

    public ArrayList<ArrayList<Level>> getMap() {
        return map;
    }

    public void setStart(int floor, int room) {
        this.currentFloor = floor;
        this.currentRoom = room;
    }

    public int getCurrentRoomNum() {
        return currentRoom;
    }

    public int getCurrentFloorNum() {
        return currentFloor;
    }

    public Level getCurrentLevel() {
        return map.get(currentFloor).get(currentRoom);
    }

    public void moveUp() {
        currentFloor--;
    }

    public void moveDown() {
        currentFloor++;
    }

    public void moveLeft() {
        currentRoom--;
    }

    public void moveRight() {
        currentRoom++;
    }

    public ArrayList<Level> loadLevels() {
        ArrayList<ArrayList<String>> configs = new ArrayList<>();
        ArrayList<Level> levels = new ArrayList<>();

        String line = "";

        try {
            Scanner scanner = new Scanner(new File("resources/Levels/doors.settings"));
            int roomIndex = 0;
            ArrayList<String> currentConfig = new ArrayList<>();
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (!line.contains("{") && !line.contains("}") && !line.contains("Room")) {
                    if (line.contains(",")) {
                        line = line.split(",")[0];
                    }
                    currentConfig.add(line.trim());
                } else if (line.contains("Room") && !line.contains("Room 1:")) {
                    configs.add(roomIndex, currentConfig);
                    roomIndex++;
                    currentConfig = new ArrayList<>();
                }
            }
            configs.add(currentConfig);
            scanner.close();

            for (int i = 0; i < configs.size(); i++) {
                String levelString = "resources/Levels/ScaledRoom" + (i + 1) + ".png";
                Level level = new Level();
                Image img = loadImage(levelString);
                if (img == null) {
                    System.err.println("Error: cannot load image from path: " + levelString);
                } else {
                    System.out.println("Successfully loaded image from path: " + levelString);
                }
                level.setImage(img);
                level.setDoors(configs.get(i));
                levels.add(level);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return levels;
    }

    public void generate(int numLevels) {
        reset();
        ArrayList<Level> levels = loadLevels();
        Random random = new Random();
        int floors = (numLevels + 3) / 4; // Calculate the number of floors needed

        for (int i = 0; i < floors; i++) {
            ArrayList<Level> currentFloor = new ArrayList<>();
            for (int j = 0; j < 4 && numLevels > 0; j++, numLevels--) {
                Level newLevel = levels.get(random.nextInt(levels.size()));
                currentFloor.add(newLevel);

                // Ensure left and right door connections
                if (j > 0) {
                    // Connect left door of the current room to the right door of the previous room
                    Door leftDoor = createDoor("Left");
                    Door rightDoor = createDoor("Right");
                    newLevel.setLeftDoor(leftDoor);
                    currentFloor.get(j - 1).setRightDoor(rightDoor);
                    newLevel.getDoors().add(leftDoor);
                    currentFloor.get(j - 1).getDoors().add(rightDoor);
                }

                // Ensure up and down door connections
                if (i > 0) {
                    // Connect up door of the current room to the down door of the room above
                    Door upDoor = createDoor("Up");
                    Door downDoor = createDoor("Down");
                    newLevel.setTopDoor(upDoor);
                    map.get(i - 1).get(j).setBottomDoor(downDoor);
                    newLevel.getDoors().add(upDoor);
                    map.get(i - 1).get(j).getDoors().add(downDoor);
                }
            }
            map.add(currentFloor);
        }

        setStart(0, 0); // Set start to the first room on the first floor

        // Print the generated map for debugging
        printGeneratedMap();
    }

    private Door createDoor(String direction) {
        Door door = null;
        switch (direction) {
            case "Up":
                door = new Door((500 / 2) - 20, 10, 55, 30);
                door.setImage(loadImage("resources/Objects/DoorTop.png"));
                break;
            case "Right":
                door = new Door(500 - 42, 500 / 2 - 20, 30, 50);
                door.setImage(loadImage("resources/Objects/DoorRight.png"));
                break;
            case "Down":
                door = new Door((500 / 2) - 20, 500 - 42, 50, 30);
                door.setImage(loadImage("resources/Objects/DoorBottom.png"));
                break;
            case "Left":
                door = new Door(10, 500 / 2 - 20, 30, 50);
                door.setImage(loadImage("resources/Objects/DoorLeft.png"));
                break;
        }
        if (door.getImage() == null) {
            System.err.println("Error: cannot load image for direction: " + direction);
        }
        return door;
    }

    private void printGeneratedMap() {
        System.out.println("Generated Map:");
        for (int i = 0; i < map.size(); i++) {
            System.out.println("Floor " + i + ":");
            for (int j = 0; j < map.get(i).size(); j++) {
                System.out.println("  Room " + j + ":");
                Level level = map.get(i).get(j);
                ArrayList<String> doors = new ArrayList<>();
                if (level.getTopDoor() != null) doors.add("Up");
                if (level.getRightDoor() != null) doors.add("Right");
                if (level.getBottomDoor() != null) doors.add("Down");
                if (level.getLeftDoor() != null) doors.add("Left");
                System.out.println("    Doors: " + doors);
            }
        }
    }

    public int getCurrentLevelNumber() {
        return currentFloor * map.get(0).size() + currentRoom + 1;
    }


}
