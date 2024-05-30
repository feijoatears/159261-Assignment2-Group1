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


            if (levelString.contains("ScaledRoom8.png")) {


                // right
                level.addInvisibleWall(new InvisibleWall(440, 115, 20, 60)); // top right bonk
                level.addInvisibleWall(new InvisibleWall(440, 310, 20, 60)); // bot right bonk


                // left
                level.addInvisibleWall(new InvisibleWall(35, 115, 20, 60)); // top left bonk
                level.addInvisibleWall(new InvisibleWall(35, 310, 20, 60)); // bot left bonk


                level.addInvisibleWall(new InvisibleWall(140, 185, 50, 125)); // left couch
                level.addInvisibleWall(new InvisibleWall(310, 185, 50, 125)); // right

                level.addInvisibleWall(new InvisibleWall(225, 125, 50, 50)); // top chair
               // level.addInvisibleWall(new InvisibleWall(220, 210, 60, 70));



            }


            if (levelString.contains("ScaledRoom9.png")) {


                level.addInvisibleWall(new InvisibleWall(260, 350, 90, 100)); // bed
                level.addInvisibleWall(new InvisibleWall(214, 416, 180, 40)); // tables

                level.addInvisibleWall(new InvisibleWall(95, 20, 80, 30)); // top left box
                level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // bot left box


                level.addInvisibleWall(new InvisibleWall(315, 20, 110, 43)); // top right lib
            }


/**
 *
 *
 *
 *              ROOMS 10 - 13 ARE EMPTY AND THUS SHOULD BE BOSS/PUZZLE ROOMS
 *
 *
 *
 *
 *
 *
 */



            if (levelString.contains("ScaledRoom14.png")) {


                level.addInvisibleWall(new InvisibleWall(210, 30, 90, 120)); // bed
                level.addInvisibleWall(new InvisibleWall(160, 40, 180, 30)); // drawer


                level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // bot left box
                level.addInvisibleWall(new InvisibleWall(325, 430, 80, 30)); // bot right box

                level.addInvisibleWall(new InvisibleWall(75, 40, 15, 15)); // top left plant
                level.addInvisibleWall(new InvisibleWall(405, 40, 15, 15)); // top right plant
            }



            if (levelString.contains("ScaledRoom15.png")) {


                level.addInvisibleWall(new InvisibleWall(115, 30, 90, 120)); // bed
                level.addInvisibleWall(new InvisibleWall(80, 40, 330, 30)); // drawer base

                level.addInvisibleWall(new InvisibleWall(290, 70, 120, 10)); // drawer depth


                level.addInvisibleWall(new InvisibleWall(90, 430, 80, 30)); // bot left box

            }


            if (levelString.contains("ScaledRoom16.png")) {


                level.addInvisibleWall(new InvisibleWall(115, 30, 90, 120)); // bed

                level.addInvisibleWall(new InvisibleWall(80, 20, 80, 60)); // top left box
                level.addInvisibleWall(new InvisibleWall(95, 430, 80, 30)); // bot left box
                level.addInvisibleWall(new InvisibleWall(335, 435, 80, 30)); // bot right box
                level.addInvisibleWall(new InvisibleWall(335, 40, 65, 40)); // top right box
            }


            if (levelString.contains("ScaledRoom17.png")) {


                level.addInvisibleWall(new InvisibleWall(335, 30, 80, 120)); // bed

                level.addInvisibleWall(new InvisibleWall(70, 20, 80, 60)); // top left box
                level.addInvisibleWall(new InvisibleWall(75, 440, 125, 45)); // bot left box
                level.addInvisibleWall(new InvisibleWall(300, 440, 125, 45)); // bot right box
                level.addInvisibleWall(new InvisibleWall(240, 40, 65, 40)); // top right box
                level.addInvisibleWall(new InvisibleWall(185, 40, 15, 15)); // plant
            }

            if (levelString.contains("ScaledRoom18.png")) {


                level.addInvisibleWall(new InvisibleWall(0, 75, 60, 345)); // bed

                level.addInvisibleWall(new InvisibleWall(70, 20, 80, 60)); // left wall

                level.addInvisibleWall(new InvisibleWall(320, 70, 75, 30)); // top right box
            }

            if (levelString.contains("ScaledRoom19.png")) {


                level.addInvisibleWall(new InvisibleWall(30, 200, 40, 100)); // firepit

                level.addInvisibleWall(new InvisibleWall(150, 195, 30, 100)); // left wall


            }

            if (levelString.contains("ScaledRoom20.png"))
            {
                level.addInvisibleWall(new InvisibleWall(175,175, 110, 45));
                level.addInvisibleWall(new InvisibleWall(70,430, 360, 50));

            }

            if (levelString.contains("ScaledRoom21.png"))
            {
                level.addInvisibleWall(new InvisibleWall(75,0, 125, 70));
                level.addInvisibleWall(new InvisibleWall(300,0, 125, 70));
                level.addInvisibleWall(new InvisibleWall(185,180, 120, 120));
            }

            if (levelString.contains("ScaledRoom22.png"))
            {
                level.addInvisibleWall(new InvisibleWall(70,80, 160, 135));
                level.addInvisibleWall(new InvisibleWall(275,80, 160, 135));
                level.addInvisibleWall(new InvisibleWall(70,280, 160, 135));
                level.addInvisibleWall(new InvisibleWall(275,280, 160, 135));
            }

            if (levelString.contains("ScaledRoom23.png"))
            {
                level.addInvisibleWall(new InvisibleWall(130, 150, 240, 40));
                level.addInvisibleWall(new InvisibleWall(130, 75, 240, 40));
                level.addInvisibleWall(new InvisibleWall(80, 0, 360, 60));
                level.addInvisibleWall(new InvisibleWall(80, 440, 115, 50));
                level.addInvisibleWall(new InvisibleWall(305, 440, 115, 50));
            }
            if (levelString.contains("ScaledRoom24.png"))
            {
                level.addInvisibleWall(new InvisibleWall(170, 185, 160, 75));
            }

            if (levelString.contains("ScaledRoom25.png"))
            {
                level.addInvisibleWall(new InvisibleWall(75, 360, 125, 50));
                level.addInvisibleWall(new InvisibleWall(295, 330, 125, 55));
                level.addInvisibleWall(new InvisibleWall(80, 75, 125, 75));
                level.addInvisibleWall(new InvisibleWall(345, 0, 50, 100));
            }

            if(levelString.contains("ScaledRoom26.png"))
            {
                level.addInvisibleWall(new InvisibleWall(100, 0, 110, 70));
                level.addInvisibleWall(new InvisibleWall(25,210, 130, 70));
                level.addInvisibleWall(new InvisibleWall(0, 315, 80,120));
                level.addInvisibleWall(new InvisibleWall(305, 0, 125, 75));
                level.addInvisibleWall(new InvisibleWall(425, 325, 70, 75));
            }

            if(levelString.contains("ScaledRoom27.png"))
            {
                level.addInvisibleWall(new InvisibleWall(100, 0, 110, 70));
                level.addInvisibleWall(new InvisibleWall(25,210, 130, 70));
                level.addInvisibleWall(new InvisibleWall(0, 315, 80,120));
                level.addInvisibleWall(new InvisibleWall(305, 0, 125, 75));
                level.addInvisibleWall(new InvisibleWall(425, 325, 70, 75));
            }

            if(levelString.contains("ScaledRoom28.png"))
            {
                level.addInvisibleWall(new InvisibleWall(95, 20,80,120));
                level.addInvisibleWall(new InvisibleWall(180,30, 20 , 35));
                level.addInvisibleWall(new InvisibleWall(300, 0, 120, 70));
                level.addInvisibleWall(new InvisibleWall(10, 330, 70, 70));
                level.addInvisibleWall(new InvisibleWall(70, 430, 80, 65));
                level.addInvisibleWall(new InvisibleWall(185, 420, 125, 60));
                level.addInvisibleWall(new InvisibleWall(350, 425, 80, 75));
            }
            if(levelString.contains("ScaledRoom29.png"))
            {
                level.addInvisibleWall(new InvisibleWall(70, 0,120,70));
                level.addInvisibleWall(new InvisibleWall(70,420, 80 , 70));
                level.addInvisibleWall(new InvisibleWall(350, 420, 80, 70));
                level.addInvisibleWall(new InvisibleWall(200, 430, 100, 55));
                level.addInvisibleWall(new InvisibleWall(320, 30, 75, 125));
                level.addInvisibleWall(new InvisibleWall(410, 40, 30, 40));
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
