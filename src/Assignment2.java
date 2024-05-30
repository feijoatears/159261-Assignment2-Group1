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
import java.lang.reflect.Array;
import java.util.*;
import javax.sound.sampled.Clip;


public class Assignment2 extends GameEngine
{
    private int score = 0;
    static int framerate = 30;

    private boolean showHelpScreen = false;


    private boolean finalDoorSpawned = false;
    private FinalDoor finalDoor = null;
    private Image congratsImage = null;



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

    public static void main(String[] args)
    {
        createGame(new Assignment2(), framerate);
    }
    public void initPlayer()
    {
        player.setLives(3);

       // int spawnPosX = width() / 2 - player.getImage().getWidth(null) / 2;
     //   int spawnPosY = (int) (height() * 2 / 3.0) - player.getImage().getHeight(null) / 2;

        int spawnPosY = 250;
        int spawnPosX = 100;

        player.setPosX(spawnPosX);
        player.setPosY(spawnPosY);

        player.setSpeed(20);
    }


    public void initEnemies() {
        Random random = new Random();

        int windowWidth = width(); // Assuming width() gives the width of the game window
        int windowHeight = height(); // Assuming height() gives the height of the game window

        for (ArrayList<Level> floor : map.getMap()) {
            for (Level room : floor) {
                // Clear existing enemies in the current room
                room.getEnemies().clear();

                // Randomly decide whether to add an enemy
                if (random.nextBoolean()) {
                    // Randomly generate position within the room
                    int posX = random.nextInt(windowWidth);
                    int posY = random.nextInt(windowHeight);
                   // room.getEnemies().add(new Vampire(posX, posY, 2, 1));
                }
            }
        }
    }

    public void initObjects() {
        Random random = new Random();

        // Select a random floor
        int randomFloorIndex = random.nextInt(map.getMap().size());
        ArrayList<Level> randomFloor = map.getMap().get(randomFloorIndex);

        // Select a random room within that floor
        int randomRoomIndex = random.nextInt(randomFloor.size());
        Level randomRoom = randomFloor.get(randomRoomIndex);

        // Set the key's position in the random room
        int posX = random.nextInt(width() - 100) + 50;
        int posY = random.nextInt(height() - 100) + 50;
        key = new Key(posX, posY);
        randomRoom.getKeys().add(key); // Add the key to the selected room only

        // Initialize objects for the current level (assuming current level is initially set)
        map.getCurrentLevel().getButtons().add(new Button(300, 300, new ArrayList<>() {{
            add(loadAudio("resources/Sounds/buttonOn.wav"));
            add(loadAudio("resources/Sounds/buttonOff.wav"));
        }}));

        map.getCurrentLevel().getObstacles().add(new DamagingObject(loadImage("resources/Objects/spikes.png"), 400, 100, 50, 50));
    }

    /**
     * Initializes the game, loads images and sounds, and sets up the initial game state.
     */
    public void init() {
        setWindowSize(500, 500);

        map.generate(100); // Generate the map
        map.setStart(0, 0); // Set the starting level
        map.setCurrentFloorAndRoom(0, 0); // Ensure the current level is set correctly

        if (volumeControl.getBackgroundMusic() != null) {
            volumeControl.getBackgroundMusic().loop(Clip.LOOP_CONTINUOUSLY);
        }

        initPlayer();
        initEnemies();
        initObjects();

        congratsImage = loadImage("resources/Sprites/kick.png");
    }

    /**
     * Updates the game state, including player movement and key collection logic.
     *
     * @param dt The time delta since the last update.
     */
    public void update(double dt) {
        if (collisionHandled) {
            collisionHandled = false;
            return;
        }

        if (inStartMenu || showHelpScreen) {
            return;
        }

        // Restart if player has no lives
        if (player.getLives() <= 0) {
            init();
        }

        Level currentLevel = map.getCurrentLevel(); // Ensure currentLevel is set

        if (currentLevel == null) {
            System.err.println("Error: currentLevel is null");
            return; // Safeguard to prevent further null reference issues
        }

        if (player.isMoving()) {
            player.move();
            showButtonPopup = player.handleButtonCollision(currentLevel.getButtons());

            // Check key collision only in the current room
            Iterator<Key> keyIterator = currentLevel.getKeys().iterator();
            while (keyIterator.hasNext()) {
                Key key = keyIterator.next();
                if (player.handleKeyCollision(key)) {
                    keyIterator.remove(); // Remove the key from the level
                    keyCollected = true;
                    break;
                }
            }
        }

        // Update enemy movement and interactions
        for (Enemy enemy : currentLevel.getEnemies()) {
            enemy.chasePlayer(player, currentLevel);
            enemy.handleWallCollision(currentLevel);
        }

        if (keyCollected) {
            //updateScore(100); // Add 100 points for collecting the key
            if (volumeControl.getKeyCollectedSound() != null) {
                volumeControl.getKeyCollectedSound().start();
            }

            if (!finalDoorSpawned) {
                spawnFinalDoor();
            }
        }

        handleDoorCollision();
        player.handleWallCollision(currentLevel); // Pass the current level here
        player.damage(currentLevel.getObstacles(), currentLevel.getEnemies());
    }


    private void spawnFinalDoor() {
        Random random = new Random();
        int x = random.nextInt(width() - 100) + 50;
        int y = random.nextInt(height() - 100) + 50;
        Image doorImage = loadImage("resources/Objects/DEBUGWALL.png");
        if (doorImage == null) {
            System.err.println("Error: Final door image is null");
        } else {
            System.out.println("Final door image loaded successfully");
        }
        finalDoor = new FinalDoor(Objects.requireNonNull(doorImage), x, y);
        finalDoorSpawned = true;
    }

    public boolean startQuizGame(MathButton mathButton)
    {
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
    @Override
    public void paintComponent() {
        Level currentLevel = map.getCurrentLevel();
        Image levelImage = currentLevel.getImage();
        if (levelImage == null) {
            System.err.println("Error: current level image is null");
        } else {
            drawImage(levelImage, 0, 0, width(), height());
        }

        drawMapObjects(currentLevel); // Pass current level to draw objects
        drawCharacters(currentLevel); // Pass current level to draw characters
        drawMap();

        mGraphics.setColor(Color.WHITE);
        mGraphics.drawString(map.getCurrentLocation(), 10, 20);

        if (inStartMenu) {
            displayStartMenu(mGraphics);
            return;
        } else if (showHelpScreen) {
            displayHelpScreen(mGraphics);
            return;
        }

        if (test1) {
            showTests(currentLevel);
        }

        if (finalDoorSpawned && finalDoor != null) {
            Image doorImage = finalDoor.getImage();
            if (doorImage == null) {
                System.err.println("Error: final door image is null");
            }
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
                        initEnemies();
                    }
                }
                if (door == map.getCurrentLevel().getRightDoor()) {
                    if (map.getCurrentRoomNum() < map.getMap().get(map.getCurrentFloorNum()).size() - 1) {
                        map.moveRight();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null) + doorConst);
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null));
                        collisionHandled = true;
                        initEnemies();
                    }
                }
                if (door == map.getCurrentLevel().getTopDoor()) {
                    if (map.getCurrentFloorNum() > 0) {
                        map.moveUp();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null) - doorConst);
                        collisionHandled = true;
                        initEnemies();
                    }
                }
                if (door == map.getCurrentLevel().getBottomDoor()) {
                    if (map.getCurrentFloorNum() < map.getMap().size() - 1) {
                        map.moveDown();
                        player.setPosX(width() - player.getPosX() - player.getImage().getWidth(null));
                        player.setPosY(height() - player.getPosY() - player.getImage().getHeight(null) + doorConst);
                        collisionHandled = true;
                        initEnemies();
                    }
                }
            }
        }

        if (finalDoorSpawned && finalDoor != null && player.checkCollision(finalDoor.getHitbox())) {
            showCongratulatoryMessage();
        }
    }

    private void showCongratulatoryMessage() {
        // Clear the screen and draw the congratulatory image
        mGraphics.setColor(Color.BLACK);
        mGraphics.fillRect(0, 0, width(), height());
        if (congratsImage != null) {
            mGraphics.drawImage(congratsImage, width() / 2 - congratsImage.getWidth(null) / 2, height() / 2 - congratsImage.getHeight(null) / 2, null);
        }
        mFrame.repaint();

        // Introduce a small delay before closing the game
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // End the game
        System.exit(0);
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


                drawSolidCircle(40 + (8 * j), 40 + (8 * i), 3);
                changeColor(white);
            }
        }
    }
    public void drawMapObjects(Level currentLevel) {
        for (Button b : currentLevel.getButtons()) {
            Image buttonImage = b.getCurrentImage();
            if (buttonImage == null) {
                System.err.println("Error: button image is null");
            } else {
                drawImage(buttonImage, b.getPosX(), b.getPosY());
            }
        }
        for (DamagingObject o : currentLevel.getObstacles()) {
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
        for (Door d : currentLevel.getDoors()) {
            Image doorImage = d.getImage();
            if (doorImage == null) {
                System.err.println("Error: door image is null for door at position (" + d.getPosX() + ", " + d.getPosY() + ")");
            } else {
                drawImage(doorImage, d.getPosX(), d.getPosY());
            }
        }
        for (Key k : currentLevel.getKeys()) {
            Image keyImage = k.getImage();
            if (keyImage == null) {
                System.err.println("Error: key image is null");
            } else {
                drawImage(keyImage, k.getPosX(), k.getPosY());
            }
        }
    }




    public void drawCharacters(Level currentLevel) {
        Image playerImage = player.getImage();
        if (playerImage == null) {
            System.err.println("Error: player image is null");
        } else {
            drawImage(playerImage, player.getPosX(), player.getPosY());
        }

        for (Enemy enemy : currentLevel.getEnemies()) {
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
//    public void updateScore(int points) {
//        score += points;
//        System.out.println("Score: " + score);
//    }

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
        g.drawString("Press ENTER to Start", width() / 2-65, height() / 2);
        g.drawString("Press H for Help", width()/2-55, height()/2+20);     
    }
    public void displayHelpScreen(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0,0,width(),height());

        g.setColor(Color.WHITE);
        g.drawString("Controls:", 50,50);
        g.drawString("-Movement: W (Up), A (Left), S (Down), D (Right)", 50, 70);
        g.drawString("Game Objective:", 50, 170);
        g.drawString("Navigate through the maze, collect keys,", 50, 190);
        g.drawString("avoid enemies and obstacles, and reach the exit door.", 50, 210);
        g.drawString("Press Q to return to main menu", 50, 300);
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
        if(event.getKeyCode() == KeyEvent.VK_R)
        {
            init();
        }
        if(event.getKeyCode() == KeyEvent.VK_M)
        {
            showMap = !showMap;
        }
        if(inStartMenu && event.getKeyCode() == KeyEvent.VK_H)
        {
            showHelpScreen = true;
            inStartMenu = false;
        }
         if(event.getKeyCode() == KeyEvent.VK_Q && showHelpScreen)
        {
            showHelpScreen = false;
            inStartMenu = true;
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


    public void showTests(Level currentLevel) {
        changeColor(red);
        float opacity = 0.5f; // 50% opacity
        AlphaComposite opaque = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        mGraphics.setComposite(opaque);

        drawSolidRectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());

        for (Door d : currentLevel.getDoors()) {
            drawSolidRectangle(d.getPosX(), d.getPosY(), d.getWidth(), d.getHeight());
        }
        for (Enemy e : currentLevel.getEnemies()) {
            drawSolidRectangle(e.getPosX(), e.getPosY(), e.getWidth(), e.getHeight());
        }
        for (InvisibleWall wall : currentLevel.getInvisibleWalls()) {
            drawSolidRectangle(wall.getPosX(), wall.getPosY(), wall.getWidth(), wall.getHeight());
        }
        // ORANGE FOR BOUNDS
        changeColor(Color.GREEN);
        changeColor(orange);
        drawSolidRectangle(0, height() - 10, width(), 10);
        drawSolidRectangle(0, 0, 10, height());
        drawSolidRectangle(width() - 10, 0, 10, height());
        mGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }


}
