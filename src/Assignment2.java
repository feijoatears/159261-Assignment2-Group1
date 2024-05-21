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
import src.generalClasses.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Assignment2 extends GameEngine
{
    static int framerate = 30;

    private boolean keyCollected = false;
    private boolean isKeyCollected = false;

    protected Image backgroundImage;
    protected Image playerSpriteSheet;
    public Player player = Player.getInstance();

    public boolean collisionHandled = false;

    private Image[] humanFrames;
    private int currentFrameIndex = 0;
    private final int[] eastFrames = {0, 1},
            southFrames = {2, 3},
            westFrames = {8, 9},
            northFrames = {6, 7};

    private Clip backgroundMusic;
    private Clip keyCollectedSound;

    private int score = 0;

    private boolean inStartMenu = true;
    public boolean showMap = true;

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
    MazeMap map = MazeMap.getInstance();

    private int numLevels = 5;

    // key shit
    public Key key;
    protected Image keyImage;
    public ArrayList<Key> keys = new ArrayList<>();

    // using a hashset to hold 2 key presses from user
    Set<Integer> keyPresses = new HashSet<>();



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

    /**
     * Initializes the game, loads images and sounds, and sets up the initial game state.
     */
    public void init()
    {
        setWindowSize(500, 500);
        map.setStart(1, 1);
        for (ArrayList<Level> floor : map.getMap()) {
            for (Level level : floor) {
                System.out.println(level);
            }
        }
        playerSpriteSheet = loadImage("resources/Player-spritesheet.png");
        int numFrames = 10;
        int frameWidth = playerSpriteSheet.getWidth(null) / numFrames;
        int frameHeight = playerSpriteSheet.getHeight(null);
        humanFrames = new Image[numFrames];
        for (int i = 0; i < numFrames; i++){
            humanFrames[i] = subImage(playerSpriteSheet, i * frameWidth, 0 , frameWidth, frameHeight);
        }
        player.setImage(humanFrames[0]);

        // Load sound files
        backgroundMusic = loadSound("resources/C.wav");
        keyCollectedSound = loadSound("resources/Key.wav");

        // Play background music in a loop
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }

        // Key stuff
        keyImage = loadImage("resources/key1.png");
        key = new Key(250, 250, keyImage);
        generateMap();
    }

    /**
     * Loads a sound file.
     *
     * @param filepath The path to the sound file.
     * @return The loaded Clip object.
     */
    private Clip loadSound(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        if (player.isMoving()) {
            player.move();
            if (player.getDirection() == Direction.East) {
                currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
                player.setImage(humanFrames[eastFrames[currentFrameIndex]]);
            } else if (player.getDirection() == Direction.West) {
                currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
                player.setImage(humanFrames[westFrames[currentFrameIndex]]);
            } else if (player.getDirection() == Direction.North ||
                    player.getDirection() == Direction.Northeast ||
                    player.getDirection() == Direction.Northwest) {
                currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
                player.setImage(humanFrames[northFrames[currentFrameIndex]]);
            } else {
                currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
                player.setImage(humanFrames[southFrames[currentFrameIndex]]);
            }

            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getImage().getWidth(null), player.getImage().getHeight(null));
            if (key.checkCollision(playerRect) && !key.getIsUsed()) {
                player.collectKey(0); // for keyindex 0
                key.setUsed(true);
                System.out.println("Key collected!");
                keyCollected = true;
                updateScore(100); // Add 100 points for collecting the key
                if (keyCollectedSound != null) {
                    keyCollectedSound.start();
                }
                mFrame.repaint();
            }
        }
        handleDoorCollision();

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

        // LEAVE OR TEARING WILL HAPPEN
        //mGraphics.clearRect(0, 0, width(), height());

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

        if (player.checkCollision(leftDoor) && !collisionHandled) {
            if (map.getCurrentRoomNum() <= 0 || map.getMap().get(map.getCurrentFloorNum()).get(map.getCurrentRoomNum() - 1) == null) {
                return;
            }
            map.moveLeft();
            player.setPosX(width() - 100);
            player.setPosY(height() / 2);
            collisionHandled = true;
            clearBackground(width(), height());
        } else if (player.checkCollision(rightDoor) && !collisionHandled) {
            if (map.getCurrentRoomNum() >= map.getMap().get(map.getCurrentFloorNum()).size() - 1 || map.getMap().get(map.getCurrentFloorNum()).get(map.getCurrentRoomNum() + 1) == null) {
                return;
            }
            map.moveRight();
            player.setPosX(100);
            player.setPosY(height() / 2);
            collisionHandled = true;
            clearBackground(width(), height());
        } else if (player.checkCollision(topDoor) && !collisionHandled) {
            if (map.getCurrentFloorNum() <= 0 || map.getMap().get(map.getCurrentFloorNum() - 1).get(map.getCurrentRoomNum()) == null) {
                return;
            }
            map.moveUp();
            player.setPosX(width() / 2);
            player.setPosY(100);
            collisionHandled = true;
            clearBackground(width(), height());
        } else if (player.checkCollision(bottomDoor) && !collisionHandled) {
            if (map.getCurrentFloorNum() >= map.getMap().size() - 1 || map.getMap().get(map.getCurrentFloorNum() + 1).get(map.getCurrentRoomNum()) == null) {
                return;
            }
            map.moveDown();
            player.setPosX(width() / 2);
            player.setPosY(height() - 100);
            collisionHandled = true;
            clearBackground(width(), height());
        }

        for(Key k : keys) {
            if (player.hasKey(k.getId())) {
                keyCollected = true;
            }
        }
    }

    /**
     * Generates the game map.
     */
    public void generateMap()
    {
        //init arrays with null elements
        ArrayList<Level> floor1 = new ArrayList<>(),
                floor2 = new ArrayList<>(),
                floor3 = new ArrayList<>();

        floor1.add(null);
        floor1.add(new Level(loadImage("resources/level2.png")));
        floor1.add(null);

        floor2.add(new Level(loadImage("resources/level5.png")));
        floor2.add(new Level(loadImage("resources/level1.png")));
        floor2.add(new Level(loadImage("resources/level3.png")));

        floor3.add(null);
        floor3.add( new Level(loadImage("resources/level4.png")));
        floor3.add(null);

        map.addFloor(floor1);
        map.addFloor(1, floor2);
        map.addFloor(2, floor3);
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
