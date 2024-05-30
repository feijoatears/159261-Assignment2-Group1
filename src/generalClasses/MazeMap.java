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

            // Add invisible walls for Room1 as an example
            if (levelString.contains("ScaledRoom1.png")) {
                level.addInvisibleWall(new InvisibleWall(110, 20, 110, 43)); // top
                level.addInvisibleWall(new InvisibleWall(280, 20, 110, 43));

                level.addInvisibleWall(new InvisibleWall(6, 110, 43, 110)); // top mid
                level.addInvisibleWall(new InvisibleWall(434, 110, 43, 110));


                level.addInvisibleWall(new InvisibleWall(6, 290, 43, 110)); // bottom mid
                level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110));

                level.addInvisibleWall(new InvisibleWall(110, 434, 110, 43));
                level.addInvisibleWall(new InvisibleWall(280, 434, 110, 43)); // bottom
            }


            if (levelString.contains("ScaledRoom2.png")) {
                level.addInvisibleWall(new InvisibleWall(95, 20, 80, 30));
                level.addInvisibleWall(new InvisibleWall(320, 20, 80, 30));


                level.addInvisibleWall(new InvisibleWall(210, 350, 90, 100));


                level.addInvisibleWall(new InvisibleWall(166, 416, 180, 40));
            }

            if (levelString.contains("ScaledRoom3.png")) {
                level.addInvisibleWall(new InvisibleWall(210, 45, 90, 105));
                level.addInvisibleWall(new InvisibleWall(85, 35, 75, 40));



                level.addInvisibleWall(new InvisibleWall(85, 420, 75, 80));
                level.addInvisibleWall(new InvisibleWall(315, 434, 110, 43)); // bottom right lib
            }


            if (levelString.contains("ScaledRoom4.png")) {
                level.addInvisibleWall(new InvisibleWall(0, 0, 70, 70)); // plant top left
                level.addInvisibleWall(new InvisibleWall(115, 30, 75, 40)); // top left closet

                level.addInvisibleWall(new InvisibleWall(310, 30, 90, 120)); // bed
                level.addInvisibleWall(new InvisibleWall(265, 45, 200, 30)); // drawer

                level.addInvisibleWall(new InvisibleWall(75, 434, 110, 43)); // bottom left lib
                level.addInvisibleWall(new InvisibleWall(315, 434, 110, 43)); // bottom right lib

            }



            if (levelString.contains("ScaledRoom5.png")) {
                level.addInvisibleWall(new InvisibleWall(90, 20, 110, 43)); // top left lib
                level.addInvisibleWall(new InvisibleWall(300, 20, 110, 43)); // top right lib


                level.addInvisibleWall(new InvisibleWall(190, 125, 110, 43)); // couch top
                level.addInvisibleWall(new InvisibleWall(190, 325, 110, 43)); // couch bot


                level.addInvisibleWall(new InvisibleWall(90, 434, 110, 43)); // bottom left lib
                level.addInvisibleWall(new InvisibleWall(300, 434, 110, 43)); // bottom right lib
            }

            if (levelString.contains("ScaledRoom6.png")) {
                level.addInvisibleWall(new InvisibleWall(434, 85, 43, 110)); // top mid lib
                level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110)); // bot mid lib

                level.addInvisibleWall(new InvisibleWall(163, 185, 50, 125)); // left couch
                level.addInvisibleWall(new InvisibleWall(250, 125, 50, 50)); // top couch
                level.addInvisibleWall(new InvisibleWall(250, 320, 50, 50)); // top couch


            }

            if (levelString.contains("ScaledRoom7.png")) {
                level.addInvisibleWall(new InvisibleWall(190, 120, 120, 255)); // big table

                // right
                level.addInvisibleWall(new InvisibleWall(434, 95, 43, 110)); // top mid lib
                level.addInvisibleWall(new InvisibleWall(434, 290, 43, 110)); // bot mid lib


                // left
                level.addInvisibleWall(new InvisibleWall(25, 95, 43, 110)); // top mid lib
                level.addInvisibleWall(new InvisibleWall(25, 290, 43, 110)); // bot mid lib

            }



            if (levelString.contains("ScaledRoom12.png")) {
                level.addInvisibleWall(new InvisibleWall(100, 50, 300, 10));
                level.addInvisibleWall(new InvisibleWall(150, 150, 10, 100));
                level.addInvisibleWall(new InvisibleWall(250, 250, 200, 10));
                level.addInvisibleWall(new InvisibleWall(300, 350, 10, 150));
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
