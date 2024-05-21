package src.generalClasses;

import java.util.ArrayList;

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

    public Level getCurrentLevel()
    {
        return map.get(currentFloor).get(currentRoom);
    }

    public int[] getCurrentCoords()
    {
        return new int[]{currentFloor, currentRoom};
    }


}

