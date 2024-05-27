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
import src.Objects.*;
import src.Objects.Button;
import src.Objects.MathButton;
import src.generalClasses.*;
import src.generalClasses.VolumeControl;
import src.Characters.Skeleton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import javax.sound.sampled.Clip;

import static com.sun.org.apache.xerces.internal.util.DOMUtil.setVisible;

public class Assignment2 extends GameEngine
{
    private int score = 0;
    static int framerate = 30;

    private boolean keyCollected = false,
            collisionHandled = false,
            inStartMenu = true,
            showMap = true,
            showButtonPopup = false,
            test1;

    public static Player player = Player.getInstance();

    public VolumeControl volumeControl = VolumeControl.getInstance();

    Set<Integer> keyPresses = new HashSet<>();

    public Key key;

    private Button activeButton = null;

    static MazeMap map = MazeMap.getInstance();

    private int previousFloor = -1;
    private int previousRoom = -1;

    public static void main(String[] args)
    {
        createGame(new Assignment2(), framerate);
    }
    public void initPlayer()
    {
        player.setLives(3);

        int spawnPosX = width() / 2 - player.getImage().getWidth(null) / 2;
        int spawnPosY = (int) (height() * 2 / 3.0) - player.getImage().getHeight(null) / 2;

        player.setPosX(spawnPosX);
        player.setPosY(spawnPosY);

        player.setSpeed(10);
    }
    public void initEnemies() {
        map.getCurrentLevel().getEnemies().clear();

        //map.getCurrentLevel().getEnemies().add(new Skeleton(50, 50, 3, 2)); // Add Skeleton at specified position


        map.getCurrentLevel().getEnemies().add(new Vampire(50, 50, 2, 1));
    }
    public void initObjects() {
        key = new Key(250, 250);

        // Add MathButton for the math quiz in level 5
        if (map.getCurrentLevelNumber() == 5) {
            map.getCurrentLevel().getButtons().add(new MathButton(300, 300, new ArrayList<>()
            {{
                add(loadAudio("resources/Sounds/buttonOn.wav"));
                add(loadAudio("resources/Sounds/buttonOff.wav"));
            }}
            ));
        }

        map.getCurrentLevel().getButtons().add(new Button(300, 300, new ArrayList<>()
        {{
            add(loadAudio("resources/Sounds/buttonOn.wav"));
            add(loadAudio("resources/Sounds/buttonOff.wav"));
        }}
        ));

        map.getCurrentLevel().getObstacles().add(new DamagingObject(loadImage("resources/Objects/spikes.png"), 400, 100, 50, 50));
    }


    /**
     * Initializes the game, loads images and sounds, and sets up the initial game state.
     */
    public void init()
    {
        setWindowSize(500, 500);

        map.generate(20);// Generate a maze with 24 rooms
        map.setStart(0, 0);
        setVisible(true);

        if (volumeControl.getBackgroundMusic() != null)
        {
            volumeControl.getBackgroundMusic().loop(Clip.LOOP_CONTINUOUSLY);
        }

        initPlayer();
        initEnemies();
        initObjects();
    }

    private void setVisible(boolean b) {
    }

    /**
     * Updates the game state, including player movement and key collection logic.
     *
     * @param dt The time delta since the last update.
     */
    public void update(double dt)
    {
        if (collisionHandled) {
            collisionHandled = false;
            return;
        }

        if (inStartMenu) {
            return;
        }

        // Restart if player has no lives
        if (player.getLives() <= 0)
        {
            init();
        }

        if (player.isMoving()) {
            player.move();
            showButtonPopup = player.handleButtonCollision(map.getCurrentLevel().getButtons());
            keyCollected = player.handleKeyCollision(key);

            // Check if the player interacts with a button
            if (showButtonPopup && activeButton != null) {
                if (activeButton instanceof MathButton) {
                    MathButton mathButton = (MathButton) activeButton;
                    boolean quizCompleted = startQuizGame(mathButton);
                    if (quizCompleted) {
                        key.setPosX(200);
                        key.setPosY(200);
                        keyCollected = true;
                        System.out.println("Quiz completed! Key has spawned.");
                    } else {
                        System.out.println("Quiz failed. Try again.");
                    }
                }
            }
        }


        // Update enemy movement and interactions
        for (Enemy enemy : map.getCurrentLevel().getEnemies())
        {
            enemy.chasePlayer(player);
        }

        if(keyCollected)
        {
            updateScore(100); // Add 100 points for collecting the key
            if (volumeControl.getKeyCollectedSound()!= null) {
                volumeControl.getKeyCollectedSound().start();
            }
        }

        handleDoorCollision();
        player.handleWallCollision();
        player.damage(map.getCurrentLevel().getObstacles(), map.getCurrentLevel().getEnemies());

        printCurrentLevel(); // Print the current level during updates

    }



    public boolean startQuizGame(MathButton mathButton) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(mathButton.getQuizQuestion());
        String answer = scanner.nextLine();
        boolean isCorrect = mathButton.checkAnswer(answer);
        if (isCorrect) {
            System.out.println("Correct!");
        } else {
            System.out.println("Incorrect. The correct answer was: " + mathButton.getQuizAnswer());
        }
        return isCorrect;
    }

    /**
     * Paints the game components on the screen.
     */
    public void paintComponent() {
        Image levelImage = map.getCurrentLevel().getImage();
        if (levelImage == null) {
            System.err.println("Error: current level image is null");
        } else {
            drawImage(levelImage, 0, 0, width(), height());
        }

        drawMapObjects();
        drawCharacters();
        drawMap();

        if (inStartMenu) {
            displayStartMenu(mGraphics);
            return;
        }

        if (test1) {
            showTests();
        }

        if (!key.getIsUsed()) {
            Image keyImage = key.getImage();
            if (keyImage == null) {
                System.err.println("Error: key image is null");
            } else {
                drawImage(keyImage, key.getPosX(), key.getPosY());
            }
        }

        if (showButtonPopup && activeButton != null) {
            activeButton.showPopup(mGraphics, width(), height());
        }

        collisionHandled = false;
    }


    /**
     * Handles door collisions and updates the map and player position accordingly.
     */
    public void handleDoorCollision() {
        final int doorConst = 20; // gap between door and player
        for (Door door : map.getCurrentLevel().getDoors()) {
            if (player.checkCollision(door.getHitbox()) && !collisionHandled) {
                if (door == map.getCurrentLevel().getLeftDoor()) {
                    if (map.getCurrentRoomNum() > 0) {
                        map.moveLeft();
                        player.setPosX(width() - player.getPosX() - player.getWidth() - doorConst);
                        player.setPosY(height() - player.getPosY() - player.getHeight());
                        collisionHandled = true;
                        printCurrentLevel(); // Print the current level
                    }
                }
                if (door == map.getCurrentLevel().getRightDoor()) {
                    if (map.getCurrentRoomNum() < map.getMap().get(map.getCurrentFloorNum()).size() - 1) {
                        map.moveRight();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null) + doorConst);
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
                        collisionHandled = true;
                        printCurrentLevel(); // Print the current level
                    }
                }
                if (door == map.getCurrentLevel().getTopDoor()) {
                    if (map.getCurrentFloorNum() > 0) {
                        map.moveUp();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null) - doorConst);
                        collisionHandled = true;
                        printCurrentLevel(); // Print the current level
                    }
                }
                if (door == map.getCurrentLevel().getBottomDoor()) {
                    if (map.getCurrentFloorNum() < map.getMap().size() - 1) {
                        map.moveDown();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null) + doorConst);
                        collisionHandled = true;
                        printCurrentLevel(); // Print the current level
                    }
                }
            }
        }
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


                drawSolidCircle(45 + (20 * j), 35 + (20 * i), 3);
                changeColor(white);
            }
        }
    }
    public void drawMapObjects() {
        for (Button b : map.getCurrentLevel().getButtons()) {
            Image buttonImage = b.getCurrentImage();
            if (buttonImage == null) {
                System.err.println("Error: button image is null");
            } else {
                drawImage(buttonImage, b.getPosX(), b.getPosY());
            }
        }
        for (DamagingObject o : map.getCurrentLevel().getObstacles()) {
            Image obstacleImage = o.getImage();
            if (obstacleImage == null) {
                System.err.println("Error: obstacle image is null");
            } else {
                drawImage(obstacleImage, o.getPosX(), o.getPosY(), 50, 50);
            }
        }
        for (int i = 0; i < player.getLives(); i++) {
            Image heartImage = player.getHeartImage();
            if (heartImage == null) {
                System.err.println("Error: heart image is null");
            } else {
                drawImage(heartImage, 20 * i, 0);
            }
        }
        for (Door d : map.getCurrentLevel().getDoors()) {
            Image doorImage = d.getImage();
            if (doorImage == null) {
                System.err.println("Error: door image is null for door at position (" + d.getPosX() + ", " + d.getPosY() + ")");
            } else {
                drawImage(doorImage, d.getPosX(), d.getPosY());
            }
        }
    }



    public void drawCharacters() {
        Image playerImage = player.getImage();
        if (playerImage == null) {
            System.err.println("Error: player image is null");
        } else {
            drawImage(playerImage, player.getPosX(), player.getPosY());
        }

        for (Enemy enemy : map.getCurrentLevel().getEnemies()) {
            Image enemyImage = enemy.getImage();
            if (enemyImage == null) {
                System.err.println("Error: enemy image is null");
            } else {
                drawImage(enemyImage, enemy.getPosX(), enemy.getPosY(), enemy.getWidth(), enemy.getHeight());
            }
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
     * Displays the start menu.
     *
     * @param g The Graphics object to draw on.
     */
    public void displayStartMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width(), height());

        Image titleImage = loadImage("resources/Sprites/title.png");
        
        g.drawImage(titleImage, 125,50, width()/2,height()/3,null);
        

        g.setColor(Color.WHITE);
        g.drawString("Press ENTER to Start", width() / 2-65, height() / 2);    }

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
        if(event.getKeyCode() == KeyEvent.VK_R)
        {
            init();
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

        // ========================================================
        // GAMBLE TIME
        // ========================================================
        if (event.getKeyCode() == KeyEvent.VK_X && showButtonPopup && activeButton != null) {
            activeButton.rollSlotMachine();
            mFrame.repaint();
        }
        // rig slot machine
        if (event.getKeyCode() == KeyEvent.VK_R && showButtonPopup && activeButton != null) {
            activeButton.rigSlotMachine();
            mFrame.repaint();
        }


        // Handle attack key
        if (event.getKeyCode() == KeyEvent.VK_F) {
            player.attack(map.getCurrentLevel().getEnemies());
        }

        // ========================================================

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
        Direction d = null;
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

        return d;
    }

    /**
     * Prints the current level, floor, and room information.
     */
    public void printCurrentLevel() {
        int currentFloor = map.getCurrentFloorNum();
        int currentRoom = map.getCurrentRoomNum();

        if (currentFloor != previousFloor || currentRoom != previousRoom) {
            int currentLevel = map.getCurrentLevelNumber();
            System.out.println("Current Level: " + currentLevel + " (Floor: " + currentFloor + ", Room: " + currentRoom + ")");
            previousFloor = currentFloor;
            previousRoom = currentRoom;
        }
    }

    public void showTests()
    {
        changeColor(red);
        float opacity = 0.5f; // 50% opacity
        AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        mGraphics.setComposite(opaque);

        drawSolidRectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());

        for(Door d: map.getCurrentLevel().getDoors())
        {
            if(d != null)
            {
                drawSolidRectangle(d.getPosX(), d.getPosY(), d.getWidth(),d.getHeight());
            }
        }
        for(Enemy e : map.getCurrentLevel().getEnemies())
        {
            drawSolidRectangle(e.getPosX(),e.getPosY(),e.getWidth(),e.getHeight());
        }
        //drawSolidRectangle(map.getCurrentLevel().getObstacles().getFirst().getPosX(), map.getCurrentLevel().getObstacles().getFirst().getPosY(), map.getCurrentLevel().getObstacles().getFirst().getWidth(), map.getCurrentLevel().getObstacles().getFirst().getHeight());

        // ORANGE FOR BOUNDS
        changeColor(Color.GREEN);
        changeColor(orange);
        drawSolidRectangle(0, height() - 10, width(), 10);
        drawSolidRectangle(0, 0, 10, height());
        drawSolidRectangle(width() - 10, 0, 10, height());
        mGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
