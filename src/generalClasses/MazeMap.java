package src.generalClasses;

import src.Objects.Door;
import src.Objects.InvisibleWall;

import java.awt.*;
import java.io.File;
import java.util.*;

import static src.GameEngine.loadImage;

public class MazeMap {
    private static MazeMap instance;
    private ArrayList<ArrayList<Level>> map;
    private final ArrayList<Level> levels;
    private final ArrayList<ArrayList<String>> configs = new ArrayList<>();
    private int currentFloor, currentRoom;

    public static MazeMap getInstance() {
        if (instance == null) {
            instance = new MazeMap();
        }
        return instance;
    }

    private MazeMap() {
        map = new ArrayList<>();
        loadConfigs();
        levels = loadLevels();

    }

    public String getCurrentLocation() {
        Level currentLevel = map.get(currentFloor).get(currentRoom);
        return "Floor: " + currentFloor + " Room: " + currentRoom + " Level: " + currentLevel.getName();
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

    public void setCurrentFloorAndRoom(int floor, int room) {
        this.currentFloor = floor;
        this.currentRoom = room;
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

    public void loadConfigs() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Level> loadLevels() {
        ArrayList<Level> levels = new ArrayList<>();

        for (int i = 0; i < configs.size(); i++) {
            String levelString = "resources/Levels/ScaledRoom" + (i + 1) + ".png";
            Level level = new Level("Room" + (i + 1));
            Image img = loadImage(levelString);

            level.setImage(img);
            level.setDoors(configs.get(i));

            // Add invisible walls for each room with precise hitboxes
            if (levelString.contains("ScaledRoom1.png")) {
                addScaledRoom1Walls(level);
            } else if (levelString.contains("ScaledRoom2.png")) {
                addScaledRoom2Walls(level);
            } else if (levelString.contains("ScaledRoom3.png")) {
                addScaledRoom3Walls(level);
            } else if (levelString.contains("ScaledRoom4.png")) {
                addScaledRoom4Walls(level);
            } else if (levelString.contains("ScaledRoom5.png")) {
                addScaledRoom5Walls(level);
            } else if (levelString.contains("ScaledRoom6.png")) {
                addScaledRoom6Walls(level);
            } else if (levelString.contains("ScaledRoom7.png")) {
                addScaledRoom7Walls(level);
            } else if (levelString.contains("ScaledRoom8.png")) {
                addScaledRoom8Walls(level);
            } else if (levelString.contains("ScaledRoom9.png")) {
                addScaledRoom9Walls(level);
            } else if (levelString.contains("ScaledRoom14.png")) {
                addScaledRoom14Walls(level);
            } else if (levelString.contains("ScaledRoom15.png")) {
                addScaledRoom15Walls(level);
            } else if (levelString.contains("ScaledRoom16.png")) {
                addScaledRoom16Walls(level);
            } else if (levelString.contains("ScaledRoom17.png")) {
                addScaledRoom17Walls(level);
            } else if (levelString.contains("ScaledRoom18.png")) {
                addScaledRoom18Walls(level);
            } else if (levelString.contains("ScaledRoom19.png")) {
                addScaledRoom19Walls(level);
            } else if (levelString.contains("ScaledRoom20.png")) {
                addScaledRoom20Walls(level);
            } else if (levelString.contains("ScaledRoom21.png")) {
                addScaledRoom21Walls(level);
            } else if (levelString.contains("ScaledRoom22.png")) {
                addScaledRoom22Walls(level);
            } else if (levelString.contains("ScaledRoom23.png")) {
                addScaledRoom23Walls(level);
            } else if (levelString.contains("ScaledRoom24.png")) {
                addScaledRoom24Walls(level);
            } else if (levelString.contains("ScaledRoom25.png")) {
                addScaledRoom25Walls(level);
            } else if (levelString.contains("ScaledRoom26.png")) {
                addScaledRoom26Walls(level);
            } else if (levelString.contains("ScaledRoom27.png")) {
                addScaledRoom27Walls(level);
            } else if (levelString.contains("ScaledRoom28.png")) {
                addScaledRoom28Walls(level);
            } else if (levelString.contains("ScaledRoom29.png")) {
                addScaledRoom29Walls(level);
            }

            levels.add(level);
        }

        return levels;
    }

    private void addScaledRoom1Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(110, 20, 110, 43)); // Top left
        level.addInvisibleWall(new InvisibleWall(280, 20, 110, 43)); // Top right
        level.addInvisibleWall(new InvisibleWall(6, 110, 43, 110)); // Top mid-left
        level.addInvisibleWall(new InvisibleWall(434, 110, 43, 110)); // Top mid-right
        level.addInvisibleWall(new InvisibleWall(6, 290, 43, 110)); // Bottom mid-left
        level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110)); // Bottom mid-right
        level.addInvisibleWall(new InvisibleWall(110, 434, 110, 43)); // Bottom left
        level.addInvisibleWall(new InvisibleWall(280, 434, 110, 43)); // Bottom right
    }

    private void addScaledRoom2Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(95, 20, 80, 30)); // Top left
        level.addInvisibleWall(new InvisibleWall(320, 20, 80, 30)); // Top right
        level.addInvisibleWall(new InvisibleWall(210, 350, 90, 100)); // Bottom mid
        level.addInvisibleWall(new InvisibleWall(166, 416, 180, 40)); // Bottom
    }

    private void addScaledRoom3Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(210, 45, 90, 105)); // Top mid
        level.addInvisibleWall(new InvisibleWall(85, 35, 75, 40)); // Top left
        level.addInvisibleWall(new InvisibleWall(85, 420, 75, 80)); // Bottom left
        level.addInvisibleWall(new InvisibleWall(315, 434, 110, 43)); // Bottom right
    }

    private void addScaledRoom4Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(0, 0, 70, 70)); // Top left plant
        level.addInvisibleWall(new InvisibleWall(115, 30, 75, 40)); // Top left closet
        level.addInvisibleWall(new InvisibleWall(310, 30, 90, 120)); // Bed
        level.addInvisibleWall(new InvisibleWall(265, 45, 200, 30)); // Drawer
        level.addInvisibleWall(new InvisibleWall(75, 434, 110, 43)); // Bottom left
        level.addInvisibleWall(new InvisibleWall(315, 434, 110, 43)); // Bottom right
    }

    private void addScaledRoom5Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(90, 20, 110, 43)); // Top left
        level.addInvisibleWall(new InvisibleWall(300, 20, 110, 43)); // Top right
        level.addInvisibleWall(new InvisibleWall(190, 125, 110, 43)); // Couch top
        level.addInvisibleWall(new InvisibleWall(190, 325, 110, 43)); // Couch bottom
        level.addInvisibleWall(new InvisibleWall(90, 434, 110, 43)); // Bottom left
        level.addInvisibleWall(new InvisibleWall(300, 434, 110, 43)); // Bottom right
    }

    private void addScaledRoom6Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(434, 85, 43, 110)); // Top mid right
        level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110)); // Bottom mid right
        level.addInvisibleWall(new InvisibleWall(163, 185, 50, 125)); // Left couch
        level.addInvisibleWall(new InvisibleWall(250, 125, 50, 50)); // Top couch
        level.addInvisibleWall(new InvisibleWall(250, 320, 50, 50)); // Bottom couch
    }

    private void addScaledRoom7Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(190, 120, 120, 255)); // Big table
        level.addInvisibleWall(new InvisibleWall(434, 95, 43, 110)); // Top mid right
        level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110)); // Bottom mid right
        level.addInvisibleWall(new InvisibleWall(25, 95, 43, 110)); // Top mid left
        level.addInvisibleWall(new InvisibleWall(25, 290, 43, 110)); // Bottom mid left
    }

    private void addScaledRoom8Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(440, 115, 20, 60)); // Top right bonk
        level.addInvisibleWall(new InvisibleWall(440, 310, 20, 60)); // Bottom right bonk
        level.addInvisibleWall(new InvisibleWall(35, 115, 20, 60)); // Top left bonk
        level.addInvisibleWall(new InvisibleWall(35, 310, 20, 60)); // Bottom left bonk
        level.addInvisibleWall(new InvisibleWall(140, 185, 50, 125)); // Left couch
        level.addInvisibleWall(new InvisibleWall(310, 185, 50, 125)); // Right couch
        level.addInvisibleWall(new InvisibleWall(225, 125, 50, 50)); // Top chair
    }

    private void addScaledRoom9Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(260, 350, 90, 100)); // Bed
        level.addInvisibleWall(new InvisibleWall(214, 416, 180, 40)); // Tables
        level.addInvisibleWall(new InvisibleWall(95, 20, 80, 30)); // Top left box
        level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // Bottom left box
        level.addInvisibleWall(new InvisibleWall(315, 20, 110, 43)); // Top right lib
    }

    private void addScaledRoom14Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(210, 30, 90, 120)); // Bed
        level.addInvisibleWall(new InvisibleWall(160, 40, 180, 30)); // Drawer
        level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // Bottom left box
        level.addInvisibleWall(new InvisibleWall(325, 430, 80, 30)); // Bottom right box
        level.addInvisibleWall(new InvisibleWall(75, 40, 15, 15)); // Top left plant
        level.addInvisibleWall(new InvisibleWall(405, 40, 15, 15)); // Top right plant
    }

    private void addScaledRoom15Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(115, 30, 90, 120)); // Bed
        level.addInvisibleWall(new InvisibleWall(80, 40, 330, 30)); // Drawer base
        level.addInvisibleWall(new InvisibleWall(290, 70, 120, 10)); // Drawer depth
        level.addInvisibleWall(new InvisibleWall(90, 430, 80, 30)); // Bottom left box
    }

    private void addScaledRoom16Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(115, 30, 90, 120)); // Bed
        level.addInvisibleWall(new InvisibleWall(80, 20, 80, 60)); // Top left box
        level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // Bottom left box
        level.addInvisibleWall(new InvisibleWall(335, 435, 80, 30)); // Bottom right box
        level.addInvisibleWall(new InvisibleWall(335, 40, 65, 40)); // Top right box
    }

    private void addScaledRoom17Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(335, 30, 80, 120)); // Bed
        level.addInvisibleWall(new InvisibleWall(70, 20, 80, 60)); // Top left box
        level.addInvisibleWall(new InvisibleWall(75, 440, 125, 45)); // Bottom left box
        level.addInvisibleWall(new InvisibleWall(300, 440, 125, 45)); // Bottom right box
        level.addInvisibleWall(new InvisibleWall(240, 40, 65, 40)); // Top right box
        level.addInvisibleWall(new InvisibleWall(185, 40, 15, 15)); // Plant
    }

    private void addScaledRoom18Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(0, 75, 60, 345)); // Bed
        level.addInvisibleWall(new InvisibleWall(70, 20, 80, 60)); // Left wall
        level.addInvisibleWall(new InvisibleWall(320, 70, 75, 30)); // Top right box
    }

    private void addScaledRoom19Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(30, 200, 40, 100)); // Firepit
        level.addInvisibleWall(new InvisibleWall(150, 195, 30, 100)); // Left wall
    }

    private void addScaledRoom20Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(175, 175, 110, 45));
        level.addInvisibleWall(new InvisibleWall(70, 430, 360, 50));
    }

    private void addScaledRoom21Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(75, 0, 125, 70));
        level.addInvisibleWall(new InvisibleWall(300, 0, 125, 70));
        level.addInvisibleWall(new InvisibleWall(185, 180, 120, 120));
    }

    private void addScaledRoom22Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(70, 80, 160, 135));
        level.addInvisibleWall(new InvisibleWall(275, 80, 160, 135));
        level.addInvisibleWall(new InvisibleWall(70, 280, 160, 135));
        level.addInvisibleWall(new InvisibleWall(275, 280, 160, 135));
    }

    private void addScaledRoom23Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(130, 150, 240, 40));
        level.addInvisibleWall(new InvisibleWall(130, 75, 240, 40));
        level.addInvisibleWall(new InvisibleWall(80, 0, 360, 60));
        level.addInvisibleWall(new InvisibleWall(80, 440, 115, 50));
        level.addInvisibleWall(new InvisibleWall(305, 440, 115, 50));
    }

    private void addScaledRoom24Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(170, 185, 160, 75));
    }

    private void addScaledRoom25Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(75, 360, 125, 50));
        level.addInvisibleWall(new InvisibleWall(295, 330, 125, 55));
        level.addInvisibleWall(new InvisibleWall(80, 75, 125, 75));
        level.addInvisibleWall(new InvisibleWall(345, 0, 50, 100));
    }

    private void addScaledRoom26Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(100, 0, 110, 70));
        level.addInvisibleWall(new InvisibleWall(25, 210, 130, 70));
        level.addInvisibleWall(new InvisibleWall(0, 315, 80, 120));
        level.addInvisibleWall(new InvisibleWall(305, 0, 125, 75));
        level.addInvisibleWall(new InvisibleWall(425, 325, 70, 75));
    }

    private void addScaledRoom27Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(100, 0, 110, 70));
        level.addInvisibleWall(new InvisibleWall(25, 210, 130, 70));
        level.addInvisibleWall(new InvisibleWall(0, 315, 80, 120));
        level.addInvisibleWall(new InvisibleWall(305, 0, 125, 75));
        level.addInvisibleWall(new InvisibleWall(425, 325, 70, 75));
    }

    private void addScaledRoom28Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(95, 20, 80, 120));
        level.addInvisibleWall(new InvisibleWall(180, 30, 20, 35));
        level.addInvisibleWall(new InvisibleWall(300, 0, 120, 70));
        level.addInvisibleWall(new InvisibleWall(10, 330, 70, 70));
        level.addInvisibleWall(new InvisibleWall(70, 430, 80, 65));
        level.addInvisibleWall(new InvisibleWall(185, 420, 125, 60));
        level.addInvisibleWall(new InvisibleWall(350, 425, 80, 75));
    }

    private void addScaledRoom29Walls(Level level) {
        level.addInvisibleWall(new InvisibleWall(70, 0, 120, 70));
        level.addInvisibleWall(new InvisibleWall(70, 420, 80, 70));
        level.addInvisibleWall(new InvisibleWall(350, 420, 80, 70));
        level.addInvisibleWall(new InvisibleWall(200, 430, 100, 55));
        level.addInvisibleWall(new InvisibleWall(320, 30, 75, 125));
        level.addInvisibleWall(new InvisibleWall(410, 40, 30, 40));
    }

    public void generate(int numLevels) {
        map = new ArrayList<>();

        int dimensions = numLevels / 10;

        for (int x = 0; x < dimensions; x++) {
            ArrayList<Level> floor = new ArrayList<>();
            for (int y = 0; y < dimensions; y++) {
                Level current = null;
                boolean validLevel;

                // ensures doors link between rooms
                do {
                    validLevel = true;
                    current = levels.get(new Random().nextInt(levels.size()));
                    if (x > 0) {
                        // if upper rooms doors doesn't link with current rooms doors, regen current room
                        Level up = map.get(x - 1).get(y);
                        if ((up.getBottomDoor() == null && current.getTopDoor() != null) ||
                                (up.getBottomDoor() != null && current.getTopDoor() == null)) {
                            validLevel = false;
                        }
                    }
                    if (y > 0) {
                        // same with left room
                        Level left = floor.get(y - 1);
                        if ((left.getRightDoor() == null && current.getLeftDoor() != null) ||
                                (left.getRightDoor() != null && current.getLeftDoor() == null)) {
                            validLevel = false;
                        }
                    }
                } while (!validLevel);
                floor.add(current);
            }
            // if all rooms on the floor link ok, add them to the map
            map.add(floor);
        }
        // check to see if all floors are able to be visited from every other room
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.getLast().size(); j++) {
                // make new adjacency list for room coordinates
                Set<Point> visited = new HashSet<>();
                dfs(i, j, visited);
                if (visited.size() < map.size() * map.getLast().size()) {
                    // if any rooms are inaccessible, regenerate the map
                    generate(numLevels);
                }
            }
        }

        // randomise player start
        setStart(new Random().nextInt(map.size()), new Random().nextInt(map.getLast().size()));
    }

    // recursive depth first search to ensure all rooms are accessible
    public void dfs(int x, int y, Set<Point> visited) {
        Point point = new Point(x, y);
        if (visited.contains(point)) {
            return;
        }
        visited.add(point);

        // up
        if (x > 0) {
            if (map.get(x).get(y).getTopDoor() != null) {
                dfs(x - 1, y, visited);
            }
        }
        // down
        if (x + 1 < map.size()) {
            if (map.get(x).get(y).getBottomDoor() != null) {
                dfs(x + 1, y, visited);
            }
        }
        // left
        if (y > 0) {
            if (map.get(x).get(y).getLeftDoor() != null) {
                dfs(x, y - 1, visited);
            }
        }
        // right
        if (y + 1 < map.get(x).size()) {
            if (map.get(x).get(y).getTopDoor() != null) {
                dfs(x, y + 1, visited);
            }
        }
        // PS: my life expectancy halved writing this
    }


}
