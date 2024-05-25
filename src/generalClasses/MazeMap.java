package src.generalClasses;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import static src.GameEngine.loadImage;

public class MazeMap
{
    private static MazeMap instance;
    private ArrayList<ArrayList<Level>> map;

    private int currentFloor,
                currentRoom;

    public static MazeMap getInstance()
    {
        if(instance == null)
        {
            instance = new MazeMap();
        }
        return instance;
    }

    private MazeMap()
    {
        map = new ArrayList<>();
    }

    public void reset()
    {
        map = new ArrayList<ArrayList<Level>>();
    }

    public void addFloor(ArrayList<Level> levels)
    {
        map.add(levels);
    }
    public void addFloor(int index, ArrayList<Level> levels)
    {
        map.add(index, levels);
    }
    public void addLevel(boolean isAtFront, int floorIndex, Level level)
    {
        //adds to front if true, back if false
        if (isAtFront)
        {
            map.get(floorIndex).addFirst(level);
            return;
        }
        map.get(floorIndex).add(level);
    }

    public ArrayList<ArrayList<Level>> getMap()
    {
        return map;
    }

    public void setStart(int floor, int room)
    {
        this.currentFloor = floor;
        this.currentRoom = room;
    }

    public int getCurrentRoomNum()
    {
        return currentRoom;
    }
    public int getCurrentFloorNum()
    {
        return currentFloor;
    }
    public Level getCurrentLevel()
    {
        return map.get(currentFloor).get(currentRoom);
    }

    public void moveUp()
    {
        currentFloor--;
    }
    public void moveDown()
    {
        currentFloor++;
    }
    public void moveLeft()
    {
        currentRoom--;
    }
    public void moveRight()
    {
        currentRoom++;
    }

    public ArrayList<ArrayList<String>> getDoorConfigs()
    {
        ArrayList<ArrayList<String>> configs = new ArrayList<>();
        String line = "";

        try
        {
            Scanner scanner = new Scanner(new File("resources/Levels/doors.settings"));
            int roomIndex = 0;
            ArrayList<String> currentConfig = new ArrayList<>();
            while (scanner.hasNextLine())
            {
                line = scanner.nextLine();
                if(!line.contains("{") && !line.contains("}") && !line.contains("Room"))
                {
                    if(line.contains(","))
                    {
                        line = line.split(",")[0];
                    }
                    currentConfig.add(line.trim());
                }
                else if(line.contains("Room") && !line.contains("Room 1"))
                {
                    roomIndex++;
                }
                else if(line.contains("{"))
                {
                    configs.add(roomIndex, currentConfig);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return configs;
    }
    public void generate(int numLevels)
    {

        ArrayList<ArrayList<String>> configs = getDoorConfigs();

        reset();
        ArrayList<Level> temp = new ArrayList<>();
        for(int i = 0; i < numLevels; i++)
        {
            //every fourth level, create a new floor (5x4)
            if(temp.size() > 3)
            {
                map.add(temp);
                //don't call .clear(), will point to same object in memory???
                temp = new ArrayList<>();
            }
            String levelString = "resources/Levels/room" + (i + 1) + ".png";

            Level level = new Level();
            Image img = loadImage(levelString);
            level.setImage(img);

            level.setDoors(configs.get(i));

            //corner logic (just hardcoding for now)
            if(temp.isEmpty() && map.isEmpty()) // top right corner
            {
                level.setImage(loadImage("resources/Levels/room3.png"));
                level.setDoors(configs.get(2));
            }
            else if(temp.size() == 3 && map.isEmpty())
            {
                level.setImage(loadImage("resources/Levels/room4.png"));
                level.setDoors(configs.get(3));
            }
            else if(temp.isEmpty() && map.size() == 3)
            {
                level.setImage(loadImage("resources/Levels/room2.png"));
                level.setDoors(configs.get(1));
            }
            else if(temp.size() == 3 && map.size() == 3)
            {
                level.setImage(loadImage("resources/Levels/room9.png"));
                level.setDoors(configs.get(8));
            }
            temp.add(level);

            /*
                gonna have to find a way to make all rooms accessible,
                there might be a chance that the room generation will
                result in inaccessible rooms
             */
        }

        map.add(temp);

        setStart(new Random().nextInt(1, map.size()), new Random().nextInt(1, map.get(0).size()));
    }
/*if(levelString.equals("resources/Levels/room1.png"))
    {
        setStart(i, temp.size() - 1);
    }*/
}

