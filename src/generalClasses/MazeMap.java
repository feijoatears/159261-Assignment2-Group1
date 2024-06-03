package src.generalClasses;

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

    public String getCurrentLocation()
    {
        return "Floor: " + (currentFloor + 1) + " Room: " + (currentRoom + 1);
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


    public Level getCurrentLevel()
    {
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

    public void loadConfigs()
    {
        String line;

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
        } catch (Exception e)
        {
            System.out.println("Couldn't load door configurations: " + e.getMessage());
        }
    }

    public ArrayList<Level> loadLevels()
    {
        ArrayList<Level> levels = new ArrayList<>();

        for (int i = 0; i < configs.size(); i++) {
            String levelString = "resources/Levels/ScaledRoom" + (i + 1) + ".png";
            Level level = new Level("Room" + (i + 1));
            Image img = loadImage(levelString);

            level.setImage(img);
            level.setDoors(configs.get(i));


            levels.add(level);
        }
        return levels;
    }

    public void generate(int numLevels)
    {
        boolean mapSet;
        int floors = (int) Math.sqrt(numLevels),
            rooms = (int) Math.ceil((double) numLevels / floors);

        do
        {
            mapSet = true;
            map = new ArrayList<>();
            for (int x = 0; x < floors; x++)
            {
                ArrayList<Level> floor = new ArrayList<>();
                for (int y = 0; y < rooms; y++)
                {
                    Level current;
                    boolean validLevel;

                    // ensures doors link between rooms
                    do
                    {
                        validLevel = true;
                        current = new Level(levels.get(new Random().nextInt(levels.size())));
                        if (x > 0)
                        {
                            // if upper rooms doors doesn't link with current rooms doors, regen current room
                            Level up = map.get(x - 1).get(y);
                            if ((up.getBottomDoor() == null && current.getTopDoor() != null) ||
                                    (up.getBottomDoor() != null && current.getTopDoor() == null)) {
                                validLevel = false;
                            }
                        }
                        if (y > 0)
                        {
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
            Set<Point> visited = new HashSet<>();
            dfs(0, 0, visited);
            // if any rooms are inaccessible, regenerate the map
            if(visited.size() < map.size() * map.getLast().size())
            {
                mapSet = false;
            }
            // randomise player start
            setStart(new Random().nextInt(map.size()), new Random().nextInt(map.getLast().size()));


            int finalDoorPosY = new Random().nextInt(map.getLast().size());
            int finalDoorPosX = new Random().nextBoolean() ? 0 : map.size() - 1;
            Level finalLevel = map.get(finalDoorPosX).get(finalDoorPosY);


            boolean finalDoorSet = false;
            if (finalDoorPosX == 0)
            {
                // top floor
                if (finalLevel.getTopDoor() != null)
                {
                    finalLevel.setFinalDoor("up");
                    finalDoorSet = true;
                }
                if (!finalDoorSet && finalLevel.getLeftDoor() != null && finalDoorPosY == 0)
                {
                    finalLevel.setFinalDoor("left");
                    finalDoorSet = true;
                }
                if (!finalDoorSet && finalLevel.getRightDoor() != null && finalDoorPosY == map.getFirst().size() - 1)
                {
                    finalLevel.setFinalDoor("right");
                    finalDoorSet = true;
                }
            } else {
                // bottom floor
                if (finalLevel.getBottomDoor() != null) {
                    finalLevel.setFinalDoor("down");
                    finalDoorSet = true;
                }
                if (!finalDoorSet && finalLevel.getLeftDoor() != null && finalDoorPosY == 0) {
                    finalLevel.setFinalDoor("left");
                    finalDoorSet = true;
                }
                if (!finalDoorSet && finalLevel.getRightDoor() != null && finalDoorPosY == map.getFirst().size() - 1) {
                    finalLevel.setFinalDoor("right");
                    finalDoorSet = true;
                }
            }
            if (!finalDoorSet)
            {
                mapSet = false;
            }
        } while (!mapSet);

        //create final door
        setCollisions();
    }
    public void setCollisions()
    {
        //3D array
        ArrayList<ArrayList<ArrayList<Integer>>> rooms = new ArrayList<>();
        ArrayList<ArrayList<Integer>> roomCollisions = new ArrayList<>();
        String line;
        try
        {
            Scanner scanner = new Scanner(new File("resources/Levels/collisions.settings"));
            while (scanner.hasNextLine())
            {
                line = scanner.nextLine();
                if (!line.contains("{") && !line.contains("}") && !line.contains("Room") && !line.isEmpty())
                {
                    ArrayList<Integer> roomObject = new ArrayList<>();
                    roomObject.add(Integer.parseInt((line.split(",")[0].trim()))); // position X
                    roomObject.add(Integer.parseInt((line.split(",")[1].trim()))); // position Y
                    roomObject.add(Integer.parseInt((line.split(",")[2].trim()))); // width
                    roomObject.add(Integer.parseInt((line.split(",")[3].trim()))); // height
                    roomCollisions.add(roomObject);
                }
                else
                {
                    if(line.contains("}"))
                    {
                        rooms.add(roomCollisions);
                        roomCollisions = new ArrayList<>();
                    }
                }
            }
            scanner.close();
        }
        catch (Exception e)
        {
            System.out.println("Couldn't load room collision configurations: " + e.getMessage());
        }

        //4x nested loop this is terrible on performance im sorry
        for (ArrayList<Level> levelArrayList : map) {
            for (Level level : levelArrayList)
            {
                for (int k = 0; k < rooms.size(); k++)
                {
                    if (level.getHash() == Objects.hash(levels.get(k)))
                    {
                        for (int l = 0; l < rooms.get(k).size(); l++)
                        {
                            level.addInvisibleWall
                            (
                                new InvisibleWall
                                (
                                    rooms.get(k).get(l).get(0),
                                    rooms.get(k).get(l).get(1),
                                    rooms.get(k).get(l).get(2),
                                    rooms.get(k).get(l).get(3)
                                )
                            );
                        }
                    }
                }
            }
        }
    }

    // recursive depth first search to ensure all rooms are accessible
    public void dfs(int x, int y, Set<Point> visited)
    {
        Point point = new Point(x, y);
        if (visited.contains(point))
        {
            return;
        }
        visited.add(point);

        // up
        if (x > 0 && map.get(x - 1).get(y).getBottomDoor() != null)
        {
            if (map.get(x).get(y).getTopDoor() != null)
            {
                dfs(x - 1, y, visited);
            }
        }
        // down
        if (x + 1 < map.size() && map.get(x + 1).get(y).getTopDoor() != null)
        {
            if (map.get(x).get(y).getBottomDoor() != null)
            {
                dfs(x + 1, y, visited);
            }
        }
        // left
        if (y > 0 && map.get(x).get(y - 1).getRightDoor() != null)
        {
            if (map.get(x).get(y).getLeftDoor() != null)
            {
                dfs(x, y - 1, visited);
            }
        }
        // right
        if (y + 1 < map.get(x).size() && map.get(x).get(y + 1).getLeftDoor() != null)
        {
            if (map.get(x).get(y).getRightDoor() != null)
            {
                dfs(x, y + 1, visited);
            }
        }
        // PS: my life expectancy halved writing this
    }
}
