package src;
/*
    Assignment 2

    Created by:
        Lyle Hunt
        Jamie Nicholson
        Tony Benefield
        Nathan Burr
 */


import src.Characters.*;
import src.Objects.Key;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;


public class Assignment2 extends GameEngine
{
    static int framerate = 30;

    protected Image backgroundImage;
    protected Image playerSpriteSheet;
    public Player player = Player.getInstance();

    public boolean collisionHandled = false;

    private Image[] humanFrames;
    private int currentFrameIndex = 0;
    private final int[] eastFrames = {0, 1};
    private final int[] southFrames = {2, 3};
    private final int[] westFrames = {8, 9};
    private final int[] northFrames = {6, 7};

    //arraylist to store all levels in map

    private ArrayList<ArrayList<Level>> map = new ArrayList<>();
    private Level currentLevel;

    private int numLevels = 20, levelIndex = 0;

    //can be moved to player class
    public boolean isMoving = false;


    // key shit
    public Key key;
    protected Image keyImage;
    // using a hashset to hold 2 key presses from user
    Set<Integer> keys = new HashSet<>();



    // Test variables, delete later
    boolean test1;

    //function to change background of jpanel
    public void setBackgroundImage(Image backgroundImage)
    {
        this.backgroundImage = backgroundImage;
    }

    public static void main(String[] args)
    {
        createGame(new Assignment2(), framerate);

    }
    public void init()
    {
        setWindowSize(500, 500);

        //create a temporary arraylist to hold a 'floor'
        ArrayList<Level> temp = new ArrayList<>();
        for(int i = 0; i < numLevels; i++)
        {
            //every fourth level, create a new floor (5x4)
            if(i % 4 == 0)
            {
                map.add(temp);
                temp.clear();
            }
            Level level = new Level(loadImage("resources/level" + i));
            temp.add(level);
        }
        map.add(temp);

        Random r = new Random();
        System.out.println(r.nextInt(2));



        playerSpriteSheet = loadImage("resources/scaledHuman-spritesheet copy.png");

        int numFrames = 10;
        int frameWidth = playerSpriteSheet.getWidth(null) / numFrames;
        int frameHeight = playerSpriteSheet.getHeight(null);

        humanFrames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++){
            humanFrames[i] = subImage(playerSpriteSheet, i * frameWidth, 0 , frameWidth, frameHeight);
        }

        player.setImage(humanFrames[0]);

        // key stuff
        keyImage = loadImage("resources/key1.png");
        key = new Key(250, 250, keyImage);
    }

    public void update(double dt)
    {
        if(isMoving)
        {
            player.move();
            // clearBackground(width(), height());
            // removed switch to eliminate duplicates
            if(player.getDirection() == Direction.East)
            {
                currentFrameIndex = (currentFrameIndex + 1)% eastFrames.length;
                player.setImage(humanFrames[eastFrames[currentFrameIndex]]);
            }
            else if (player.getDirection() == Direction.West)
            {
                currentFrameIndex = (currentFrameIndex + 1)% westFrames.length;
                player.setImage(humanFrames[westFrames[currentFrameIndex]]);
            }
            else if(player.getDirection() == Direction.North ||
                    player.getDirection() == Direction.Northeast ||
                    player.getDirection() ==Direction.Northwest)
            {
                currentFrameIndex = (currentFrameIndex + 1)% northFrames.length;
                player.setImage(humanFrames[northFrames[currentFrameIndex]]);
            }
            else
            {
                currentFrameIndex = (currentFrameIndex + 1)% southFrames.length;
                player.setImage(humanFrames[southFrames[currentFrameIndex]]);
            }

            Rectangle leftDoor = new Rectangle(0, height() / 2 - 15, 30, 50);
            Rectangle topDoor = new Rectangle((width() / 2) - 20, 0 , 55, 30);
            Rectangle rightDoor = new Rectangle(width() - 30, height() / 2 - 15, 30, 50);
            Rectangle bottomDoor = new Rectangle((width() / 2) - 20, height()-30, 50, 30);

            if (player.checkCollision(leftDoor)) {
                System.out.println("Left Door");
                if (!collisionHandled) {
                    levelIndex++;
                    /*if (levelIndex < levels.size()) {
                        currentLevel = levels.get(0).get(levelIndex);
                    }*/
                    collisionHandled = true;
                    clearBackground(width(), height());
                }
            }

            if (player.checkCollision(topDoor))
            {
                System.out.println("Top Door");
                // if statements for checking key

                for (int i = 0; i < 10; i++){
                    if (player.hasKey(0)){

                    }
                }
            }
            if (player.checkCollision(rightDoor))
            {
                System.out.println("Right Door");

                // if statements for checking key
            }
            if(player.checkCollision(bottomDoor))
            {
                System.out.println("Bottom Door");
            }

            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getImage().getWidth(null), player.getImage().getHeight(null));
            if (key.checkCollision(playerRect) && !key.getIsUsed())
            {
                player.collectKey(0); // for keyindex 0
                key.setUsed(true);
                System.out.println("Key collected!");
            }
        }

        if (player.hasKey(0)) { // v0.0.11
            System.out.println("Player has collected the keyNum: 0!");
        }
    }

    public void paintComponent()
    {
        // have to create imageObserver so make an inline one



        mGraphics.clearRect(0, 0, width(), height());
       // mGraphics.drawImage(currentLevel.getImage(), 0, 0, width(), height(), null);

        mGraphics.drawImage(currentLevel.getImage(), 0, 0, width(), height(), (img, infoflags, x, y, width, height) -> false);

        drawImage(player.getImage(), player.getPosX(), player.getPosY());
        // drawImage(keyImage, key.getPosX(), key.getPosY());    -->   draw key in place

        // Drawing basic door hitbox, testing enable with space, disable with enter
        changeColor(red);
        if (test1) {
            float opacity = 0.5f; // 50% opacity
            AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
            mGraphics.setComposite(opaque);

            drawSolidRectangle(player.getPosX(), player.getPosY(), player.getImage().getWidth(null), player.getImage().getHeight(null));

            drawSolidRectangle(0, (double) height() / 2 - 15, 30, 50);
            drawSolidRectangle(((double) width() / 2) - 20, 0 , 55, 30);
            drawSolidRectangle(width() - 30, (double) height() / 2 - 15, 30, 50);
            drawSolidRectangle( ((double) width() / 2) - 20, height()-30, 50, 30);

            mGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        if (!key.getIsUsed()) {
            drawImage(keyImage, key.getPosX(), key.getPosY());

            // Draw a semi-transparent rectangle over the key, needed for when the background of the key goes away
            float keyOpacity = 0.5f; // 50% opacity
            AlphaComposite keyAc = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, keyOpacity);
            mGraphics.setComposite(keyAc);


            drawSolidRectangle(key.getPosX(), key.getPosY(), keyImage.getWidth(null), keyImage.getHeight(null));

            mGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    Direction lastDirection = null;
    @Override
    public void keyPressed(KeyEvent event)
    {


        if (event.getKeyCode() == KeyEvent.VK_SPACE){
            test1 = true;
        } else if (event.getKeyCode() == KeyEvent.VK_ENTER){
            test1 = false;
        }


        if (event.getKeyCode() == KeyEvent.VK_G){
            mGraphics.clearRect(0, 0, width(), height());
            System.out.println();
        }

        //max of 2 key presses allowed
        if (keys.size() >= 2)
        {
            return;
        }
        //ignore any keys but movement keys
        if( (event.getKeyCode() != KeyEvent.VK_W) &&
            (event.getKeyCode() != KeyEvent.VK_A) &&
            (event.getKeyCode() != KeyEvent.VK_S) &&
            (event.getKeyCode() != KeyEvent.VK_D) )
        {
            return;
        }

        //stops player from switching to opposite direction if they're already moving
        //i.e. moving left if they're moving right already
        if(keys.contains(KeyEvent.VK_W) && event.getKeyCode() == KeyEvent.VK_S ||
           keys.contains(KeyEvent.VK_S) && event.getKeyCode() == KeyEvent.VK_W ||
           keys.contains(KeyEvent.VK_A) && event.getKeyCode() == KeyEvent.VK_D ||
           keys.contains(KeyEvent.VK_D) && event.getKeyCode() == KeyEvent.VK_A )
        {
            return;
        }

        keys.add(event.getKeyCode());
        isMoving = true;
        Direction lastDirection = handleDirection();
        player.setDirection(lastDirection);
    }
    @Override
    public void keyReleased(KeyEvent event)
    {
        keys.remove(event.getKeyCode());
        if (keys.isEmpty())
        {
            isMoving = false;
            lastDirection = null;
        }
        else
        {
            lastDirection = handleDirection();
            player.setDirection(lastDirection);
        }
    }
    /*
     * TODO:
     * 
     * Maze generation algorithm
     * (Sprite) Animations
     * AI and Player Movement
     * Collisions
     * Interactable items (keys, doors)
     * Menu/Player Options
     *  
     */
    private Direction handleDirection()
    {
        Direction d;

        boolean up = keys.contains(KeyEvent.VK_W),
                down = keys.contains(KeyEvent.VK_S),
                left = keys.contains(KeyEvent.VK_A),
                right = keys.contains(KeyEvent.VK_D);

        if (up && right)
        {
            d = Direction.Northeast;
        }
        else if (up && left)
        {
            d = Direction.Northwest;
        }
        else if (down && right)
        {
            d = Direction.Southeast;
        }
        else if (down && left)
        {
            d = Direction.Southwest;
        }
        else if(up)
        {
            d = Direction.North;
        }
        else if(down)
        {
            d = Direction.South;
        }
        else if (left)
        {
            d = Direction.West;
        }
        else if (right)
        {
            d = Direction.East;
        }
        else
        {
            //may cause errors, maybe need to add a 'none' direction
            return null;
        }
        return d;
    }
}
