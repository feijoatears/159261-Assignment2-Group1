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
import src.Objects.Button;
import src.Objects.DamagingObject;
import src.Objects.Key;
import src.generalClasses.*;
import src.generalClasses.VolumeControl;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import javax.sound.sampled.Clip;

public class Assignment2 extends GameEngine
{
    static int framerate = 30;

    private boolean keyCollected = false;
    private boolean isKeyCollected = false;

    protected Image backgroundImage;
    public Player player = Player.getInstance();

    public boolean collisionHandled = false;

    public VolumeControl volumeControl = VolumeControl.getInstance();

    private int score = 0;

    private boolean inStartMenu = true;
    public boolean showMap = true;

    private int numLevels = 5;
    // door flippy shit
    private boolean flippyDoor = false;
    // walls, warning -> PAINFUL AS ALL HELL
    public ArrayList<Rectangle> walls = new ArrayList<>();



    // key shit
    public Key key;
    protected Image keyImage;
    public ArrayList<Key> keys = new ArrayList<>();

    // using a hashset to hold 2 key presses from user
    Set<Integer> keyPresses = new HashSet<>();

    public ArrayList<Button> buttons = new ArrayList<>();
    public ArrayList<DamagingObject> obstacles = new ArrayList<>();
    // Test variables, delete later
    boolean test1;




    MazeMap map = MazeMap.getInstance();
    /*
        current design ->
        'map' stores arraylists of each 'floor' (of which consists of 4 levels)
        each floor looks like " [] [] [] [] ",
        so the map w/ 20 lvls looks like:

        [] [] [] []
        [] [] [] []
        [] [x] [] []
        [] [] [] []
        [] [] [] []

        the x denotes the player position (can be randomised).
        going left will take player to floor 3, level 1,
        going up will take player to floor 2, level 2, etc

     */

    //function to change background of jpanel
    public void setBackgroundImage(Image backgroundImage)
    {
        this.backgroundImage = backgroundImage;
    }

    public static void main(String[] args)
    {
        createGame(new Assignment2(), framerate);
    }

    /**
     * Initializes the game, loads images and sounds, and sets up the initial game state.
     */
    public void init()
    {
        setWindowSize(500, 500);

        // set starting position
        map.setStart(1, 1);

        int spawnPosX = width() / 2 - player.getImage().getWidth(null) / 2; // using this to offset playing image to
        int spawnPosY = (int) (height() * 2 / 3.0) - player.getImage().getHeight(null) / 2; // using this to offset playing image to

        player.setPosX(spawnPosX);
        player.setPosY(spawnPosY);

        // WALLLLLLLLLLL - Nathan
        walls.clear();
        // basic example walls
        walls.add(new Rectangle(100, 100, 50, 10)); // wall at (100, 100) with width 50 and height 10
        walls.add(new Rectangle(200, 200, 10, 50)); // wall at (200, 200) with width 10 and height 50


        // Screen boundaries as walls
        walls.add(new Rectangle(0, 0, width(), 10)); // top boundary
        walls.add(new Rectangle(0, height() - 10, width(), 10)); // bottom boundary
        walls.add(new Rectangle(0, 0, 10, height())); // left boundary
        walls.add(new Rectangle(width() - 10, 0, 10, height())); // right boundary







        // Play background music in a loop
        if (volumeControl.getBackgroundMusic() != null) {
            volumeControl.getBackgroundMusic().loop(Clip.LOOP_CONTINUOUSLY);
        }

        player.reset();
        // Key stuff
        keyImage = loadImage("resources/Objects/key1.png");
        key = new Key(250, 250, keyImage);
        generateMap();

        //button testing
        buttons.add(new Button(300, 300, new ArrayList<>()
        {{
            add(loadAudio("resources/Sounds/buttonOn.wav"));
            add(loadAudio("resources/Sounds/buttonOff.wav"));
        }}
        ));

        //obstacle testing
        obstacles.add(new DamagingObject(loadImage("resources/Objects/spikes.png"), 400, 100, 50, 50));

    }
    /**
     * Updates the game state, including player movement and key collection logic.
     *
     * @param dt The time delta since the last update.
     */
    public void update(double dt)
    {
        if (inStartMenu) {
            return;
        }

        //restart
        if (player.getLives() <= 0)
        {
            init();
        }

        if (player.isMoving()) {
            player.move(walls);

            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getImage().getWidth(null), player.getImage().getHeight(null));
            if (key.checkCollision(playerRect) && !key.getIsUsed()) {
                player.collectKey(0); // for keyindex 0
                key.setUsed(true);
                System.out.println("Key collected!");
                keyCollected = true;
                updateScore(100); // Add 100 points for collecting the key
                if (volumeControl.getKeyCollectedSound()!= null) {
                    volumeControl.getKeyCollectedSound().start();
                }
                mFrame.repaint();
            }
        }

        //collision checks
        handleDoorCollision();
        for(Button b : buttons)
        {
            if(player.checkCollision(b.getHitbox()))
            {
                if(!b.getIsUsed())
                {
                    b.activate();
                    playAudio(b.getOnSound());
                }
            }
            else
            {
                if(b.getIsUsed())
                {
                    b.deactivate();
                    playAudio(b.getOffSound());
                }
            }
        }
        for(DamagingObject o : obstacles)
        {
            player.damage(o.getHitbox());
        }
        //added isKeyCollected flag - easier to debug
        if (player.hasKey(0) && !isKeyCollected) { // v0.0.11
            System.out.println("Player has collected the keyNum: 0!");
            isKeyCollected = true;
        }
    }
    /**
     * Updates the score.
     *
     * @param points The points to add to the score.
     */
    public void updateScore(int points) {
        score += points;
        System.out.println("Score: " + score);
    }
    /**
     * Paints the game components on the screen.
     */
    public void paintComponent()
    {
        if (inStartMenu) {
            displayStartMenu(mGraphics);
            return;
        }




        // have to create imageObserver so make an inline one
        mGraphics.drawImage(map.getCurrentLevel().getImage(), 0, 0, width(), height(), (img, infoflags, x, y, width, height) -> false);

        drawImage(player.getImage(), player.getPosX(), player.getPosY());

        changeColor(red);
        if (test1) {
            float opacity = 0.5f; // 50% opacity
            AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
            mGraphics.setComposite(opaque);

            drawSolidRectangle(player.getPosX(), player.getPosY(), player.getImage().getWidth(null), player.getImage().getHeight(null));

            drawSolidRectangle(0, (double) height() / 2 - 15, 30, 50);
            drawSolidRectangle(((double) width() / 2) - 20, 0 , 55, 30);
            drawSolidRectangle(width() - 30, (double) height() / 2 - 15, 30, 50);
            drawSolidRectangle(((double) width() / 2) - 20, height()-30, 50, 30);

            drawSolidRectangle(obstacles.getFirst().getPosX(), obstacles.getFirst().getPosY(), obstacles.getFirst().getWidth(), obstacles.getFirst().getHeight());


            // walls, gotta manually add for all tests ill try fix this

            // BLUE == INVISIBLE WALL
            changeColor(blue);
           // drawSolidRectangle(100, 100, 50, 10);
           // drawSolidRectangle(200, 200, 10, 50);
            for (Rectangle wall : walls) {
                drawSolidRectangle(wall.x, wall.y, wall.width, wall.height);
            }
            // ORANGE FOR BOUNDS
            changeColor(Color.ORANGE);
            drawSolidRectangle(0, 0, width(), 10);
            drawSolidRectangle(0, height() - 10, width(), 10);
            drawSolidRectangle(0, 0, 10, height());
            drawSolidRectangle(width() - 10, 0, 10, height());










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
        if(keyCollected) {
            mGraphics.setColor(Color.BLACK);
            mGraphics.fillRect(100, 350, 300, 100);
            mGraphics.setColor(Color.WHITE);
            mGraphics.drawRect(100, 350, 300, 100);
            mGraphics.drawString("You have collected a key!", 110, 370);
            mGraphics.drawString("Find the door it unlocks to escape.", 110, 390);
            mGraphics.drawString("ENTER", 350, 440);
        }
        collisionHandled = false;
        drawMap();

        for(Button b : buttons)
        {
            //draws ontop of player? need to find a workaround
            drawImage(b.getCurrentImage(), b.getPosX(), b.getPosY());
        }
        for(DamagingObject o : obstacles)
        {
            drawImage(o.getImage(), o.getPosX(), o.getPosY(), 50, 50);
        }
        for(int i = 0; i < player.getLives(); i++)
        {
            drawImage(player.getHeartImage(), 20 * i, 0);
        }
    }

    /**
     * Handles door collisions and updates the map and player position accordingly.
     */
    public void handleDoorCollision()
    {
        Rectangle leftDoor = new Rectangle(0, height() / 2 - 15, 30, 50),
                topDoor = new Rectangle((width() / 2) - 20, 0 , 55, 30),
                rightDoor = new Rectangle(width() - 30, height() / 2 - 15, 30, 50),
                bottomDoor = new Rectangle((width() / 2) - 20, height()-30, 50, 30);

        if (player.checkCollision(leftDoor) && !collisionHandled)
        {
            if (map.getCurrentRoomNum() <= 0 ||
                map.getMap().get(map.getCurrentFloorNum()).get(map.getCurrentRoomNum() - 1) == null)
            {
                return;
            }
            map.moveLeft();

            player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
            player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
            //player.setPosX(width() - 100);
            // player.setPosY(height() / 2);
            // flippyDoor = true;
            collisionHandled = true;

        }
        else if (player.checkCollision(rightDoor) && !collisionHandled)
        {
            if (map.getCurrentRoomNum() >= map.getMap().get(map.getCurrentFloorNum()).size() - 1 ||
                map.getMap().get(map.getCurrentFloorNum()).get(map.getCurrentRoomNum() + 1) == null)
            {
                return;
            }
            map.moveRight();
            player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
            player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
           // player.setPosX(100);
           // player.setPosY(height() / 2);
            // flippyDoor = true;
            collisionHandled = true;

        }
        else if (player.checkCollision(topDoor) && !collisionHandled)
        {
            if (map.getCurrentFloorNum() <= 0 ||
                map.getMap().get(map.getCurrentFloorNum() - 1).get(map.getCurrentRoomNum()) == null)
            {
                return;
            }
            map.moveUp();
            player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
            player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
            //player.setPosX(width() / 2);
            //player.setPosY(100);
            // flippyDoor = true;
            collisionHandled = true;
        }
        else if (player.checkCollision(bottomDoor) && !collisionHandled)
        {
            if (map.getCurrentFloorNum() >= map.getMap().size() - 1 ||
                map.getMap().get(map.getCurrentFloorNum() + 1).get(map.getCurrentRoomNum()) == null)
            {
                return;
            }
            map.moveDown();
            player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
            player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
            //player.setPosX(width() / 2);
            //player.setPosY(height() - 100);
            // flippyDoor = true;
            collisionHandled = true;
        }

        for(Key k : keys) {
            if (player.hasKey(k.getId()))
            {
                keyCollected = true;
            }
        }
    }

    /**
     * Generates the game map.
     */
    public void generateMap()
    {/*
            creating a testing map which looks like this:

                []
             [] [] []
                []

            player spawns in centre
        */
        //reset map array
        map.reset();
        //init arrays with null elements
        ArrayList<Level> floor1 = new ArrayList<>(),
                floor2 = new ArrayList<>(),
                floor3 = new ArrayList<>();

        floor1.add(null);
        floor1.add(new Level(loadImage("resources/Levels/level2.png")));
        floor1.add(null);

        floor2.add(new Level(loadImage("resources/Levels/level5.png")));
        floor2.add(new Level(loadImage("resources/Levels/level1.png")));
        floor2.add(new Level(loadImage("resources/Levels/level3.png")));

        floor3.add(null);
        floor3.add( new Level(loadImage("resources/Levels/level4.png")));
        floor3.add(null);

        map.addFloor(floor1);
        map.addFloor(1, floor2);
        map.addFloor(2, floor3);

        /*
            /////////////////////////////////////////////////////////////
            // this is the correct code for the program  with 20 lvls, //
            // testing code above                                      //
            /////////////////////////////////////////////////////////////

            //create a temporary arraylist to hold a 'floor'
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
                Level level = new Level(loadImage("resources/level" + (i + 1) + ".png"));
                temp.add(level);
            }
            map.add(temp);
        */
    }

    /**
     * Draws the minimap on the screen.
     */
    public void drawMap()
    {
        if(!showMap)
        {
            return;
        }
        changeColor(white);
        drawRectangle(25, 25, 100, 100);
        for (int i = 0; i < map.getMap().size(); i++)
        {
            ArrayList<Level> floor = map.getMap().get(i);
            for (int j = 0; j < map.getMap().get(i).size(); j++)
            {
                Level room = map.getMap().get(i).get(j);
                if(i == map.getCurrentFloorNum() && j == map.getCurrentRoomNum())
                {
                    changeColor(red);
                }
                drawCircle(50 + (25 * j), (25 * i) + 50, 5, 5);
                changeColor(white);
            }
        }
    }

    /**
     * Displays the start menu.
     *
     * @param g The Graphics object to draw on.
     */
    public void displayStartMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width(), height());
        g.setColor(Color.WHITE);
        g.drawString("Press ENTER to Start", width() / 2 - 50, height() / 2);
    }

    Direction lastDirection = null;

    /**
     * Handles key press events, including player movement and menu navigation.
     *
     * @param event The key event.
     */
    @Override
    public void keyPressed(KeyEvent event)
    {
        if (inStartMenu && event.getKeyCode() == KeyEvent.VK_ENTER) {
            inStartMenu = false;
            return;
        }

        if(event.getKeyCode() == KeyEvent.VK_M)
        {
            showMap = !showMap;
        }

        if (event.getKeyCode() == KeyEvent.VK_SPACE){
            test1 = true;
        } else if (event.getKeyCode() == KeyEvent.VK_ENTER){
            test1 = false;
            keyCollected = false;
        }

        if (event.getKeyCode() == KeyEvent.VK_G){
            mGraphics.clearRect(0, 0, width(), height());
            System.out.println();
        }

        //max of 2 key presses allowed
        if (keyPresses.size() >= 2)
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
        if(keyPresses.contains(KeyEvent.VK_W) && event.getKeyCode() == KeyEvent.VK_S ||
                keyPresses.contains(KeyEvent.VK_S) && event.getKeyCode() == KeyEvent.VK_W ||
                keyPresses.contains(KeyEvent.VK_A) && event.getKeyCode() == KeyEvent.VK_D ||
                keyPresses.contains(KeyEvent.VK_D) && event.getKeyCode() == KeyEvent.VK_A )
        {
            return;
        }

        keyPresses.add(event.getKeyCode());
        player.setMoving(true);
        Direction lastDirection = handleDirection();
        player.setDirection(lastDirection);
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

    /**
     * Handles key release events to stop player movement.
     *
     * @param event The key event.
     */
    @Override
    public void keyReleased(KeyEvent event)
    {
        keyPresses.remove(event.getKeyCode());
        if (keyPresses.isEmpty())
        {
            player.setMoving(false);
            lastDirection = null;
        }
        else
        {
            lastDirection = handleDirection();
            player.setDirection(lastDirection);
        }
    }

    /**
     * Determines the direction of player movement based on key presses.
     *
     * @return The direction of player movement.
     */
    private Direction handleDirection()
    {
        Direction d;
        boolean up = keyPresses.contains(KeyEvent.VK_W),
                down = keyPresses.contains(KeyEvent.VK_S),
                left = keyPresses.contains(KeyEvent.VK_A),
                right = keyPresses.contains(KeyEvent.VK_D);
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
