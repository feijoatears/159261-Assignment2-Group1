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

    private MazeMap()
    {
        map = new ArrayList<>();
        loadConfigs();
        levels = loadLevels();
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
    public void loadConfigs()
    {
        String line = "";

        try
        {
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
        }
        catch (Exception e)
        {

        }
    }

    public ArrayList<Level> loadLevels() {
        ArrayList<Level> levels = new ArrayList<>();

        for (int i = 0; i < configs.size(); i++) {
            String levelString = "resources/Levels/ScaledRoom" + (i + 1) + ".png";
            Level level = new Level();
            Image img = loadImage(levelString);

            level.setImage(img);
            level.setDoors(configs.get(i));


            if (levelString.contains("Room1")) {
                // Define the hitboxes for Room1
                level.addInvisibleWall(new InvisibleWall(50, 50, 100, 10));
                level.addInvisibleWall(new InvisibleWall(200, 50, 10, 100));
                level.addInvisibleWall(new InvisibleWall(50, 200, 100, 10));
                level.addInvisibleWall(new InvisibleWall(50, 50, 10, 100));
            }



            levels.add(level);
        }

        return levels;
    }


    public void generate(int numLevels)
    {
        map = new ArrayList<>();

        int dimensions = numLevels / 10;

        for(int x = 0; x < dimensions; x++)
        {
            ArrayList<Level> floor = new ArrayList<>();
            for(int y = 0; y < dimensions; y++)
            {
                Level current = null;
                boolean validLevel;

                //ensures doors link between rooms
                do
                {
                    validLevel = true;
                    current = levels.get(new Random().nextInt(levels.size()));
                    if(x > 0)
                    {
                        //if upper rooms doors doesn't link with current rooms doors, regen current room
                        Level up = map.get(x - 1).get(y);
                        if((up.getBottomDoor() == null && current.getTopDoor() != null) ||
                                (up.getBottomDoor() != null && current.getTopDoor() == null))
                        {
                            validLevel = false;
                        }
                    }
                    if(y > 0)
                    {
                        //same with left room
                        Level left = floor.get(y - 1);
                        if((left.getRightDoor() == null && current.getLeftDoor() != null) ||
                                (left.getRightDoor() != null && current.getLeftDoor() == null))
                        {
                            validLevel = false;
                        }
                    }
                } while (!validLevel);
                floor.add(current);
            }
            //if all rooms on the floor link ok, add them to the map
            map.add(floor);
        }
        //check to see if all floors are able to be visited from every other room
        for (int i = 0; i < map.size(); i++)
        {
            for (int j = 0; j < map.getLast().size(); j++)
            {
                //make new adjacency list for room coordinates
                Set<Point> visited = new HashSet<>();
                dfs(i,j,visited);
                if(visited.size() < map.size() * map.getLast().size())
                {
                    //if any rooms are inaccessible, regenerate the map
                    generate(numLevels);
                }
            }
        }

        //randomise player start
        setStart(new Random().nextInt(map.size()), new Random().nextInt(map.getLast().size()));
    }

    //recursive depth first search to ensure all rooms are accessible
    public void dfs (int x, int y, Set<Point> visited)
    {
        Point point = new Point(x, y);
        if (visited.contains(point))
        {
            return;
        }
        visited.add(point);

        //up
        if (x > 0)
        {
            if(map.get(x).get(y).getTopDoor() != null)
            {
                dfs(x - 1, y, visited);
            }
        }
        //down
        if (x + 1 < map.size())
        {
            if(map.get(x).get(y).getBottomDoor() != null)
            {
                dfs(x + 1, y, visited);
            }
        }
        //left
        if (y > 0)
        {
            if(map.get(x).get(y).getLeftDoor() != null)
            {
                dfs(x, y - 1, visited);
            }
        }
        //right
        if (y + 1 < map.get(x).size())
        {
            if(map.get(x).get(y).getTopDoor() != null)
            {
                dfs(x, y + 1, visited);
            }
        }
        //PS: my life expectancy halved writing this
    }
}
