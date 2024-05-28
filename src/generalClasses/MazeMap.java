package src.generalClasses;

import src.Objects.Door;

import java.awt.Image;
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

    public void reset()
    {
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
    public ArrayList<Level> loadLevels()
    {
        ArrayList<Level> levels = new ArrayList<>();

        for (int i = 0; i < configs.size(); i++)
        {
            String levelString = "resources/Levels/ScaledRoom" + (i + 1) + ".png";
            Level level = new Level();
            Image img = loadImage(levelString);

            level.setImage(img);
            level.setDoors(configs.get(i));
            levels.add(level);
        }

        return levels;
    }

    public void generate(int numLevels)
    {
        reset();
        ArrayList<Level> temp = new ArrayList<>();

        for(int i = 0; i < numLevels; i++)
        {
            //every fourth level, create a new floor (5x4)
            if(temp.size() > 3)
            {
                map.add(temp);
                temp = new ArrayList<>();
            }

            int random = new Random().nextInt(0,levels.size());

            temp.add(levels.get(random));

            /*
                gonna have to find a way to make all rooms accessible,
                there might be a chance that the room generation will
                result in inaccessible rooms


                set player start to room 1

                if(levelString.equals("resources/Levels/room1.png"))
                {
                    setStart(i, temp.size() - 1);
                }
            */
        }
        map.add(temp);

        int playerStartX = new Random().nextInt(0, map.size()),
            playerStartY = new Random().nextInt(0, map.getFirst().size());

        setStart(playerStartX,playerStartY);

        //corners
        map.getFirst().set(0, levels.get(2));
        map.getFirst().set(3, levels.get(3));
        map.getLast().set(3, levels.get(1));
        map.getLast().set(3, levels.get(8));

        boolean[][] roomsVisited = new boolean[map.size()][map.getLast().size()];

        if(!traverse(playerStartX, playerStartY, roomsVisited))
        {
            generate(numLevels);
        }

    }


    public boolean traverse(int x, int y, boolean[][] visitedRooms)
    {
        /*
            will return if all rooms are able to be visited by the player
            however this will still mean that if you take a wrong turn,
            you could reach an area which you cannot leave, since the
            door linking is busted
        */

        visitedRooms[x][y] = true;
        boolean allRoomsVisited = true;
        for(int i = 0; i < map.size(); i++)
        {
            for (int j = 0; j < map.get(i).size(); j++)
            {
                if(!visitedRooms[i][j])
                {
                    allRoomsVisited = false;
                }
            }
        }
        if(allRoomsVisited)
        {
            return true;
        }

        boolean pathFound = false;

        Level up = null,
                down = null,
                left = null,
                right = null;

        //check for surrounding rooms
        if(x - 1 > 0 && x - 1 < map.size() && map.get(x - 1).get(y) != null) { up = map.get(x - 1).get(y); }
        if(x + 1 < map.size() && map.get(x + 1).get(y) != null) { down = map.get(x + 1).get(y); }
        if(y - 1 > 0 && map.get(x).get(y - 1) != null) { left = map.get(x).get(y - 1); }
        if(y + 1 < map.get(x).size() && map.get(x).get(y + 1) != null) { right = map.get(x).get(y + 1); }

        //traverse
        if(up != null && !visitedRooms[x - 1][y] && map.get(x).get(y).getTopDoor() != null)
        {
            if(up.getBottomDoor() == null)
            {
                visitedRooms[x-1][y] = false;
            }
            pathFound = traverse(x - 1, y, visitedRooms);
        }
        if(down != null && !visitedRooms[x + 1][y] && map.get(x).get(y).getBottomDoor() != null)
        {
            if(down.getTopDoor() == null)
            {
                visitedRooms[x+1][y] = false;
            }
            pathFound = traverse(x + 1, y, visitedRooms);
        }
        if(left != null && !visitedRooms[x][y - 1] && map.get(x).get(y).getLeftDoor() != null)
        {
            if(left.getRightDoor() == null)
            {
                visitedRooms[x][y-1] = false;
            }
            pathFound = traverse(x, y - 1, visitedRooms);
        }
        if(right != null && !visitedRooms[x][y + 1] && map.get(x).get(y).getRightDoor() != null)
        {
            if(right.getLeftDoor() == null)
            {
                visitedRooms[x][y+1] = false;
            }
            pathFound = traverse(x, y + 1, visitedRooms);
        }
        return pathFound;
    }


    /* easily the most painful code ive ever written
    public void connectDoors()
    {
        int[] topDoorOptions = {0, 1, 5, 6, 7, 8, 9},
                bottomDoorOptions = {0, 2, 3, 4, 5, 6, 9},
                leftDoorOptions = {0, 3, 4, 5, 7, 8, 10},
                rightDoorOptions = {0, 1, 2, 4, 6, 7, 10};

        for (int x = 0; x < map.size(); x++) {
            for (int y = 0; y < map.get(x).size(); y++) {
                Level current = map.get(x).get(y),
                        up = null,
                        down = null,
                        left = null,
                        right = null;

                if ((x - 1) > 0 && map.get(x - 1).get(y) != null) { up = map.get(x - 1).get(y); }
                if ((x + 1) < map.size() && map.get(x + 1).get(y) != null) { down = map.get(x + 1).get(y); }
                if ((y - 1) > 0 && map.get(x).get(y - 1) != null) { left = map.get(x).get(y - 1); }
                if ((y + 1) < map.get(x).size() && map.get(x).get(y + 1) != null) { right = map.get(x).get(y + 1); }

                if (up != null)
                {
                    int rand = new Random().nextInt(0, topDoorOptions.length);
                    if(current.getTopDoor() != null && up.getBottomDoor() == null)
                    {
                        map.get(x - 1).set(y, levels.getFirst());

                    }
                }
                if (down != null)
                {
                    if(current.getBottomDoor() != null && down.getTopDoor() == null)
                    {
                        map.get(x + 1).set(y, levels.getFirst());
                        System.out.println(x + " " + y + " - down -> " + map.get(x+1).get(y).getTopDoor());
                    }
                }
                if (left != null && current.getLeftDoor() != null)
                {
                }
                if (right != null && current.getRightDoor() != null)
                {
                }
            }
        }
    }*/






}
/**/
