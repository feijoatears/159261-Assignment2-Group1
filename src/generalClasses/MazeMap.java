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

    public String getCurrentLocation()
    {
        return "Floor: " + (currentFloor + 1) + " Room: " + (currentRoom + 1);
    }

    public void addFloor(ArrayList<Level> levels) {
        map.add(levels);
    }

    public void addFloor(int index, ArrayList<Level> levels) {
        map.add(index, levels);
    }

    public void addLevel(boolean isAtFront, int floorIndex, Level level) {
        if (isAtFront) {
            map.get(floorIndex).addFirst(level);
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
        map = new ArrayList<>();
        int floors = (int) Math.sqrt(numLevels),
            rooms = (int) Math.ceil((double) numLevels / floors);

        for (int x = 0; x < floors; x++)
        {
            ArrayList<Level> floor = new ArrayList<>();
            for (int y = 0; y < rooms; y++)
            {
                Level current = null;
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

        System.out.println(map.size() + " " + map.getFirst().size() + " - x*y");
        // check to see if all floors are able to be visited from every other room
        try
        {
            for (int i = 0; i < map.size(); i++)
            {
                for (int j = 0; j < map.getLast().size(); j++)
                {
                    // make new adjacency list for room coordinates
                    Set<Point> visited = new HashSet<>();
                    dfs(i, j, visited);
                    if (visited.size() < map.size() * map.getLast().size()) {
                        // if any rooms are inaccessible, regenerate the map
                        generate(numLevels);
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            generate(numLevels);
        }

        // randomise player start
        setStart(new Random().nextInt(map.size()), new Random().nextInt(map.getLast().size()));

        //create final door
        setFinalDoor();
        setCollisions();

       /* for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++)
            {
                if(map.get(i).get(j).getTopDoor() != null)
                {
                    if(map.get(i).get(j).getTopDoor().getIsFinalDoor())
                    {
                        System.out.println("final door at: " + i + " " + j);
                    }
                }
                if(map.get(i).get(j).getBottomDoor() != null)
                {
                    if(map.get(i).get(j).getBottomDoor().getIsFinalDoor())
                    {
                        System.out.println("final door at: " + i + " " + j);
                    }
                }
                if(map.get(i).get(j).getLeftDoor() != null)
                {
                    if(map.get(i).get(j).getLeftDoor().getIsFinalDoor())
                    {
                        System.out.println("final door at: " + i + " " + j);
                    }
                }
                if(map.get(i).get(j).getRightDoor() != null)
                {
                    if(map.get(i).get(j).getRightDoor().getIsFinalDoor())
                    {
                        System.out.println("final door at: " + i + " " + j);
                    }
                }
            }
        }*/
    }
    public void setCollisions()
    {
        //3D array
        ArrayList<ArrayList<ArrayList<Integer>>> rooms = new ArrayList<>();
        ArrayList<ArrayList<Integer>> roomCollisions = new ArrayList<>();
        String line = null;
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
            e.printStackTrace();
        }

        for(int i = 0; i < map.size(); i++)
        {
            for(int j = 0; j < map.get(i).size(); j++)
            {
                for(int k = 0; k < rooms.size(); k++)
                {
                    if(map.get(i).get(j).getHash() == Objects.hash(levels.get(k)))
                    {
                        for (int l = 0; l < rooms.get(k).size(); l++)
                        {
                            for (int m = 0; m < rooms.get(k).get(l).size(); m++)
                            {
                                map.get(i).get(j).addInvisibleWall(
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
    }
    public void setFinalDoor()
    {
        int finalDoorPosY = new Random().nextInt(map.getFirst().size());
        int finalDoorPosX;

        finalDoorPosX = new Random().nextBoolean() ? 0 : map.size() - 1;


        Level finalLevel = map.get(finalDoorPosX).get(finalDoorPosY);

        //clear the map
        for (ArrayList<Level> floor : map) {
            for (Level current : floor) {
                if (current.getTopDoor() != null && current.getTopDoor().getIsFinalDoor()) {
                    current.removeFinalDoor("up");
                }
                if (current.getBottomDoor() != null && current.getBottomDoor().getIsFinalDoor()) {
                    current.removeFinalDoor("down");
                }
                if (current.getLeftDoor() != null && current.getLeftDoor().getIsFinalDoor()) {
                    current.removeFinalDoor("left");
                }
                if (current.getRightDoor() != null && current.getRightDoor().getIsFinalDoor()) {
                    current.removeFinalDoor("right");
                }
            }
        }

        if (finalDoorPosY == 0) {
            // top-left corner
            if (finalLevel.getLeftDoor() != null) {
                finalLevel.setFinalDoor("left");
            } else if (finalLevel.getTopDoor() != null) {
                finalLevel.setFinalDoor("up");
            }
        } else if (finalDoorPosY == map.getFirst().size() - 1)
        {
            // top-right corner
            if (finalLevel.getRightDoor() != null) {
                finalLevel.setFinalDoor("right");
            } else if (finalLevel.getTopDoor() != null) {
                finalLevel.setFinalDoor("up");
            }
        }
        else if (finalDoorPosX == map.size() - 1 && finalDoorPosY == map.getFirst().size() - 1)
        {
            // bottom-right corner
            if (finalLevel.getRightDoor() != null) {
                finalLevel.setFinalDoor("right");
            } else if (finalLevel.getBottomDoor() != null) {
                finalLevel.setFinalDoor("down");
            }
        }
        else
        {
            if (finalDoorPosX == 0)
            {
                // top floor, not a corner
                if (finalLevel.getTopDoor() != null)
                {
                    finalLevel.setFinalDoor("up");
                }
                else
                {
                    setFinalDoor();
                }
            }
            else
            {
                // bottom floor, not a corner
                if (finalLevel.getBottomDoor() != null)
                {
                    finalLevel.setFinalDoor("down");
                }
                else
                {
                    setFinalDoor();
                }
            }
        }
        // Debug statement to indicate the position of the final door
        System.out.println("Final door set at: Floor " + finalDoorPosX + ", Room " + finalDoorPosY);
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
