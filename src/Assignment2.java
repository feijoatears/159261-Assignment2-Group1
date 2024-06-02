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
import src.Characters.Player;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;


public class Assignment2 extends GameEngine {
    static int framerate = 30;

    private boolean keyCollected = false,
                    collisionHandled = false,
                    inStartMenu = true,
                    showMap = true,
                    showButtonPopup = false,
                    gameOver = false,
                    gameWon = false,
                    showHelpScreen = false,
                    settings = false,
                    test1;

    public static Player player = Player.getInstance();

    public VolumeControl volumeControl = VolumeControl.getInstance();

    Set<Integer> keyPresses = new HashSet<>();

    public Key key;

    private Button activeButton = null;

    static MazeMap map = MazeMap.getInstance();


    private TimerPopUp timerPopUp;
    private JSlider volumeSlider;
    private boolean showVolumeSlider = false;

    private Font cinzel = null;

    public static void main(String[] args) {
        createGame(new Assignment2(), framerate);

    }

    public void initPlayer()
    {
        int spawnPosY = 250;
        int spawnPosX = 100;
        player.setPosX(spawnPosX);
        player.setPosY(spawnPosY);
        player.setLives(settingsOptionValues[0]);
        player.setSpeed(settingsOptionValues[2]);
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
                if (random.nextBoolean())
                {
                    // Randomly generate position within the room
                    int posX = random.nextInt(windowWidth);
                    int posY = random.nextInt(windowHeight);


                    room.getEnemies().add(new Vampire(posX, posY, settingsOptionValues[3], 1));
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

        // Specific mid
        int specificFloorIndex = 5;
        int specificRoomIndex = 5;

        // Ensure the specific indices are within the map size bounds
        if (specificFloorIndex < map.getMap().size() && specificRoomIndex < map.getMap().get(specificFloorIndex).size()) {
            Level specificRoom = map.getMap().get(specificFloorIndex).get(specificRoomIndex);

            specificRoom.getButtons().add(new Button(300, 300, new ArrayList<>() {{
                add(loadAudio("resources/Sounds/buttonOn.wav"));
                add(loadAudio("resources/Sounds/buttonOff.wav"));
            }}));

            boolean validPosition = false;
            boolean validPosition2 = false;
            int posX = 0, posY = 0;

            /**
             * This is for randomly generating the key and button, for button im making it always spawn in room 5, 5.
             */
            while (!validPosition || !validPosition2) {
                // Generate a random position
                posX = random.nextInt(width() - 100) + 50;
                posY = random.nextInt(height() - 100) + 50;

                // Check if the position is valid (not overlapping with obstacles, walls, etc.)
                validPosition = true;
                validPosition2 = true;
                Rectangle keyHitbox = new Rectangle(posX, posY, 32, 32); // Assuming key size is 32x32

                // Check collision with invisible walls in the random room
                for (InvisibleWall wall : randomRoom.getInvisibleWalls()) {
                    if (wall.getHitbox().intersects(keyHitbox)) {
                        validPosition = false;
                        break;
                    }
                }

                // Check collision with obstacles in the random room
                if (validPosition) {
                    for (DamagingObject obstacle : randomRoom.getObstacles()) {
                        if (obstacle.getHitbox().intersects(keyHitbox)) {
                            validPosition = false;
                            break;
                        }
                    }
                }

                // Check collision with invisible walls in the specific room
                for (InvisibleWall wall : specificRoom.getInvisibleWalls()) {
                    if (wall.getHitbox().intersects(keyHitbox)) {
                        validPosition2 = false;
                        break;
                    }
                }

                // Check collision with obstacles in the specific room
                if (validPosition2) {
                    for (DamagingObject obstacle : specificRoom.getObstacles()) {
                        if (obstacle.getHitbox().intersects(keyHitbox)) {
                            validPosition2 = false;
                            break;
                        }
                    }
                }
            }

            // Check if a key already exists in the level
            if (randomRoom.getKeys().isEmpty()) {
                // Create the key at the valid position
                key = new Key(posX, posY);
                randomRoom.getKeys().add(key); // Add the key to the selected room only

                // Debug statement for key position
                System.out.println("Key placed at position: (" + posX + ", " + posY + ") in room (" + randomFloorIndex + ", " + randomRoomIndex + ")");
            }

            System.out.println("Random floor index: " + randomFloorIndex + ", Random room index: " + randomRoomIndex); // For testing, delete later

            // Initialize objects for the current level
            map.getCurrentLevel().getObstacles().add(new DamagingObject(loadImage("resources/Objects/spikes.png"), 400, 100, 50, 50));
        } else {
            System.out.println("spike Specific floor or room index out of bounds.");
        }
    }





    /**
     * Initializes the game, loads images and sounds, and sets up the initial game state.
     */
    public void init()
    {
        setWindowSize(500, 500);
        // Set the initial volume to 20%
        VolumeControl.getInstance().setMasterVolume(0.50f);
        Random random = new Random();

        //load font
        if(cinzel == null)
        {
            try
            {
                InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/Cinzel-Regular.ttf");
                assert is != null;
                cinzel = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            }
            catch (Exception e)
            {
                System.out.println("Couldn't load font: " + e.getMessage());
            }
        }

        // TESTING NOTE, CHANGE numLevels TO '1' TO DEBUG FOR KEY AND FINISH FASTER (it spawns correctly just takes ages to find bc well 100 rooms)
        map.generate(settingsOptionValues[1]); // Generate the map
        map.setStart(random.nextInt(map.getMap().size()),random.nextInt(map.getMap().getLast().size()));
        player.discardKey();

        //System.out.println(random.nextInt(3) + 3);
        if (volumeControl.getBackgroundMusic() != null) {
            volumeControl.getBackgroundMusic().loop(Clip.LOOP_CONTINUOUSLY);
        }

        initPlayer();
        initEnemies();
        initObjects();


    }
    public void reloadinit()
    {

        VolumeControl.getInstance().setMasterVolume(0.50f);
        Random random = new Random();

        //load font
        if(cinzel == null)
        {
            try
            {
                InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/Cinzel-Regular.ttf");
                assert is != null;
                cinzel = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            }
            catch (Exception e)
            {
                System.out.println("Couldn't load font: " + e.getMessage());
            }
        }

        // TESTING NOTE, CHANGE numLevels TO '1' TO DEBUG FOR KEY AND FINISH FASTER (it spawns correctly just takes ages to find bc well 100 rooms)
        map.generate(settingsOptionValues[1]); // Generate the map
        map.setStart(random.nextInt(map.getMap().size()),random.nextInt(map.getMap().getLast().size()));
        player.discardKey();

        //System.out.println(random.nextInt(3) + 3);
        if (volumeControl.getBackgroundMusic() != null) {
            volumeControl.getBackgroundMusic().loop(Clip.LOOP_CONTINUOUSLY);
        }

        initPlayer();
        initEnemies();
        initObjects();


    }

    /**
     * Updates the game state, including player movement and key collection logic.
     *
     * @param dt The time delta since the last update.
     */
    public void update(double dt)
    {
        if(inStartMenu || showHelpScreen || settings || gameOver|| gameWon )
        {
            return;
        }

        if (collisionHandled)
        {
            collisionHandled = false;
            return;
        }

        // Restart if player has no lives
        if (player.getLives() <= 0) {
            gameOver();
            return;
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
            enemy.handleIWallCollision(currentLevel);
        }
        if (keyCollected)
        {
            //updateScore(100); // Add 100 points for collecting the key
            if (volumeControl.getKeyCollectedSound() != null) {
                volumeControl.getKeyCollectedSound().start();
            }
        }

        handleDoorCollision();
        player.handleWallCollision(currentLevel); // Pass the current level here
        player.damage(currentLevel.getObstacles(), currentLevel.getEnemies());
        player.attackUpdate(dt);
    }

    /* public boolean startQuizGame(MathButton mathButton) {
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
    }*/







    /**
     * Paints the game components on the screen.
     */
    @Override
    public void paintComponent()
    {

        if (inStartMenu)
        {
            displayStartMenu();
            return;
        }
        else if (showHelpScreen)
        {
            displayHelpScreen();
            return;
        }
        if (gameOver)
        {
            gameOver();
            return;
        }
        if (gameWon) {
            showCongratulatoryMessage();
            return;
        }
        if(settings)
        {
            displaySettings();
            return;
        }
        else
        {
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

            changeColor(white);
            drawText(width() - 125, 20, map.getCurrentLocation(), "Cinzel", 12);
            if (test1)
            {
                showTests(currentLevel);
            }

            player.showDamagedCircle(mGraphics);

            collisionHandled = false;
        }
    }



    /**
     * Handles door collisions and updates the map and player position accordingly.
     */
    public void handleDoorCollision() {
        final int doorConst = 20; // gap between door and player
        final int offset = 50; // offset to move player away from the door

        for (Door door : map.getCurrentLevel().getDoors())
        {
            if (player.checkCollision(door.getHitbox()) && !collisionHandled)
            {
                if(door.getIsFinalDoor() && player.hasKey())
                {
                    showCongratulatoryMessage(); // Show congratulations message and end the game
                    return;
                }
                else
                {
                    if (door == map.getCurrentLevel().getLeftDoor())
                    {
                        if (map.getCurrentRoomNum() > 0) {
                            map.moveLeft();
                            player.setPosX(width() - player.getWidth() - doorConst - offset);
                            player.setPosY(player.getPosY());
                            collisionHandled = true;
                            initEnemies();
                        }
                    }
                    if (door == map.getCurrentLevel().getRightDoor()) {
                        if (map.getCurrentRoomNum() < map.getMap().get(map.getCurrentFloorNum()).size() - 1) {
                            map.moveRight();
                            player.setPosX(doorConst + offset);
                            player.setPosY(player.getPosY());
                            collisionHandled = true;
                            initEnemies();
                        }
                    }
                    if (door == map.getCurrentLevel().getTopDoor()) {
                        if (map.getCurrentFloorNum() > 0) {
                            map.moveUp();
                            player.setPosX(player.getPosX());
                            player.setPosY(height() - player.getHeight() - doorConst - offset);
                            collisionHandled = true;
                            initEnemies();
                        }
                    }
                    if (door == map.getCurrentLevel().getBottomDoor()) {
                        if (map.getCurrentFloorNum() < map.getMap().size() - 1) {
                            map.moveDown();
                            player.setPosX(player.getPosX());
                            player.setPosY(doorConst + offset);
                            collisionHandled = true;
                            initEnemies();
                        }
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

        drawRectangle(25, 25, (7.5 * map.getMap().getLast().size()) + 2.5, (7.5 * map.getMap().size()) + 2.5);
        for (int i = 0; i < map.getMap().size(); i++)
        {
            changeColor(new Color(1, 1, 1, 0.5f));
            ArrayList<Level> floor = map.getMap().get(i);

            for (int j = 0; j < map.getMap().get(i).size(); j++)
            {
                Level room = map.getMap().get(i).get(j);
                if(i == map.getCurrentFloorNum() && j == map.getCurrentRoomNum())
                {
                    changeColor(red);
                }
                drawSolidRectangle(27.5 + (7.5 * j), 27.5 + (7.5 * i), 5, 5);
                changeColor(new Color(1, 1, 1, 0.5f));
            }
        }
    }
    public void drawMapObjects(Level currentLevel)
    {
        for (Button b : currentLevel.getButtons())
        {
            Image buttonImage = b.getCurrentImage();
            if (buttonImage == null)
            {
                System.err.println("Error: button image is null");
            }
            else
            {
                drawImage(buttonImage, b.getPosX(), b.getPosY());
            }
        }
        for (DamagingObject o : currentLevel.getObstacles())
        {
            Image obstacleImage = o.getImage();
            if (obstacleImage == null)
            {
                System.err.println("Error: obstacle image is null");
            }
            else
            {
                drawImage(obstacleImage, o.getPosX(), o.getPosY(), 50, 50);
            }
        }
        for (int i = 0; i < player.getLives(); i++)
        {
            Image heartImage = player.getHeartImage();
            if (heartImage == null)
            {
                System.err.println("Error: heart image is null");
            }
            else
            {
                drawImage(heartImage, 20 * i, 0);
            }
        }
        for (Door d : currentLevel.getDoors())
        {
            Image doorImage = d.getImage();
            if (doorImage == null)
            {
                System.err.println("Error: door image is null for door at position (" + d.getPosX() + ", " + d.getPosY() + ")");
            }
            else
            {
                drawImage(doorImage, d.getPosX(), d.getPosY());
            }
        }
        for (Key k : currentLevel.getKeys())
        {
            drawImage(k.getImage(), k.getPosX(), k.getPosY());
        }
    }

    public void drawCharacters(Level currentLevel)
    {
        Image playerImage = player.getImage();
        if (playerImage == null)
        {
            System.err.println("Error: player image is null");
        }
        else
        {
            drawImage(playerImage, player.getPosX(), player.getPosY());
        }

        for (Enemy enemy : currentLevel.getEnemies())
        {
            Image enemyImage = enemy.getImage();
            if (enemyImage == null)
            {
                System.err.println("Error: enemy image is null");
            }
            else
            {
                drawImage(enemyImage, enemy.getPosX(), enemy.getPosY(), enemy.getWidth(), enemy.getHeight());
            }
        }
        drawImage(player.getImage(), player.getPosX(), player.getPosY());
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
     */
    public void displayStartMenu()
    {
        changeBackgroundColor(black);
        clearBackground(width(), height());

        Image titleImage = loadImage("resources/Sprites/title.png");

        drawImage(titleImage,125, 50, (double) width() /2, (double) height() /3);

        changeColor(white);
        drawText(((double) width() / 2) - 75, ((double) height() / 2), "Press ENTER to Start", "Cinzel", 14);
        drawText(((double) width() / 2) - 55, ((double) height() / 2) + 25, "Press H for Help", "Cinzel", 14);
        drawText(((double) width() / 2) - 130, ((double) height() / 2) + 50, "Press T to change the game settings", "Cinzel", 14);
    }
    public void displayHelpScreen()
    {
        changeBackgroundColor(black);
        clearBackground(width(), height());

        changeColor(white);
        drawText(50,50, "Controls:", "Cinzel", 14);
        drawText( 50, 70,"W (Up), A (Left), S (Down), D (Right), F (Attack)", "Cinzel" ,14);
        drawText(50, 170, "Game Objective:", "Cinzel" ,14);
        drawText(50, 190, "Navigate through the maze, collect keys,", "Cinzel" ,14);
        drawText(50, 210, "Avoid enemies and obstacles, and reach the exit door.", "Cinzel" ,14);
        drawText(50, 300, "Press Q to return to main menu", "Cinzel" ,14);
        drawText(50, 320, "Press V to toggle volume control", "Cinzel" ,14);
        drawText(50, 340, "Press T to toggle timer", "Cinzel" ,14);
    }
    private void gameOver()
    {
        changeBackgroundColor(black);
        clearBackground(width(), height());

        Image titleImage = loadImage("resources/Sprites/died.png");

        drawImage(titleImage, 0, 50, width(), (double) height() / 4);
        changeColor(Color.WHITE);
        drawText((double) width() /2 - 130, (double) height() / 2 + 10, "Press Q to return to the main menu", "Cinzel", 14);
        gameOver = true;
    }

    private Timer resetTimer;

    private void showCongratulatoryMessage() {
        changeBackgroundColor(Color.BLACK);
        clearBackground(width(), height());

        Image congratsImage = loadImage("resources/Sprites/kick.png");

        if (congratsImage != null) {
            drawImage(congratsImage, (double) width() / 2 - congratsImage.getWidth(null) / 2,
                    (double) height() / 2 - congratsImage.getHeight(null) / 2);
        }

        changeColor(Color.WHITE);
        drawText((double) width() / 2 - 130, (double) height() / 2 + 10,
                "Press Q to return to the main menu", "Cinzel", 14);
        gameWon = true;

        // Schedule the reset to occur after a delay
        resetTimer = new Timer(3000, e -> reset());
        resetTimer.setRepeats(false); // Ensure the timer only runs once
        resetTimer.start();
    }


    private boolean resetInProgress = false;
    private void reset() {
        if (resetInProgress) return; // Check if a reset is already in progress
        resetInProgress = true; // Set flag to indicate a reset is in progress

        // Reset game state
        keyCollected = false;
        collisionHandled = false;
        inStartMenu = true;
        showMap = true;
        showButtonPopup = false;
        gameOver = false;
        gameWon = false;
        showHelpScreen = false;
        settings = false;
        test1 = false;
        keyPresses.clear();
        player.discardKey();
        player.reset();

        // Close the timer window if it is open
        if (timerPopUp != null) {
            timerPopUp.dispose();
        }

        // Close the volume slider window if it is open
        if (volumeSliderPopUp != null) {
            volumeSliderPopUp.dispose();
        }

        // Initialize the game
        init();

        resetInProgress = false; // Reset the flag once the reset is complete
    }


    public void reload() {
        // Reset game state variables
        keyCollected = false;
        collisionHandled = false;
        inStartMenu = false;
        showMap = true;
        showButtonPopup = false;
        gameOver = false;
        gameWon = false;
        showHelpScreen = false;
        settings = false;
        test1 = false;
        keyPresses.clear();

        // Reset the player
        player.reset();
        player.discardKey();

        // Reset volume
        volumeControl.reset();

        // Close the timer window if it is open
        if (timerPopUp != null) {
            timerPopUp.dispose();
        }

        // Close the volume slider window if it is open
        if (volumeSliderPopUp != null) {
            volumeSliderPopUp.dispose();
        }

        reloadinit();

    }

    static private String[] settingsOptionStrings = new String[]
            {
                "Number of Lives: ",
                "Number of Rooms: ",
                "Player Speed",
                "Enemy Speed",
            };
    static private int[] settingsOptionValues = new int[]
            {
                    3,
                    100,
                    10,
                    2
            };
    private int optionIndex = 0;

    private void displaySettings()
    {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        changeColor(white);

        drawText(40, 50, "Settings", "Cinzel", 26);

        for (int i = 0; i < settingsOptionStrings.length; i++)
        {
            if(i == optionIndex)
            {
                drawBoldText(40, (40 * i) + 100, settingsOptionStrings[i], "Cinzel", 19);
                drawBoldText(300, (40 * i) + 100, String.valueOf(settingsOptionValues[i]), "Cinzel", 19);
            }
            else
            {
                drawText(40, (40 * i) + 100, settingsOptionStrings[i], "Cinzel", 18);
                drawText(300, (40 * i) + 100, String.valueOf(settingsOptionValues[i]), "Cinzel", 19);
            }
        }

        drawText(15, height() - 10, "Press Q to return to the main menu", "Cinzel", 12);
    }

    /**
     * Handles key press events, including player movement and menu navigation.
     */
    Direction lastDirection = null;
    @Override
    public void keyPressed(KeyEvent event)
    {
        int keycode = event.getKeyCode();
        if(keycode == KeyEvent.VK_R)
        {
            reload();
            init();
        }
        if(keycode == KeyEvent.VK_M)
        {
            showMap = !showMap;
        }

        if(inStartMenu)
        {

            if(keycode == KeyEvent.VK_T)
            {
                settings = true;
                inStartMenu = false;
            }
            if(keycode == KeyEvent.VK_ENTER)
            {
                init();
                inStartMenu = false;
            }
            if(keycode == KeyEvent.VK_H)
            {
                showHelpScreen = true;
                inStartMenu = false;
            }
        }
        if(settings)
        {
            if(keycode == KeyEvent.VK_DOWN)
            {
                optionIndex = (optionIndex == settingsOptionStrings.length - 1) ? 0 : optionIndex + 1;
            }
            if(keycode == KeyEvent.VK_UP)
            {
                optionIndex = (optionIndex == 0) ? settingsOptionStrings.length - 1: optionIndex - 1;

            }
            if(keycode == KeyEvent.VK_ENTER)
            {
                if(optionIndex == 0)
                {
                    settingsOptionValues[0] = (settingsOptionValues[0] > 2)? 1: settingsOptionValues[0] + 1 ;
                }
                if(optionIndex == 1)
                {
                    settingsOptionValues[1] = (settingsOptionValues[1] > 95) ? 25 : settingsOptionValues[1] + 5;
                }
                if(optionIndex == 2)
                {
                    settingsOptionValues[2] = (settingsOptionValues[2] > 19) ? 1 : settingsOptionValues[2] + 1;
                }
                if(optionIndex == 3)
                {
                    settingsOptionValues[3] = (settingsOptionValues[3] > 19) ? 1 : settingsOptionValues[3] + 1;
                }
            }
        }
        if(event.getKeyCode() == KeyEvent.VK_Q)
        {
            if(showHelpScreen)
            {
                showHelpScreen = false;
            }
            if(gameOver)
            {
                gameOver = false;
            }
            if(settings)
            {
                settings = false;
            }
            inStartMenu = true;
            // Cancel the reset timer if it is running
            if (resetTimer != null) {
            resetTimer.stop();
            resetTimer = null;
            }

            // Immediately reset the game
           reset();
        }

        if(event.getKeyCode() == KeyEvent.VK_7)
        {
            gameOver = true;
        }


        if (event.getKeyCode() == KeyEvent.VK_F)
        {
            player.attack(map.getCurrentLevel().getEnemies());
            player.startAttackAnimation();
        }

        if (event.getKeyCode() == KeyEvent.VK_V) {
            if (volumeSliderPopUp != null && isVolumeSliderWindowOpen) {
                volumeSliderPopUp.dispose();
            } else {
                volumeSliderPopUp = new VolumeSliderPopUp();
            }
        }


        if (event.getKeyCode() == KeyEvent.VK_T) {
            if (timerPopUp != null && isTimerWindowOpen) {
                timerPopUp.dispose();
            } else {
                timerPopUp = new TimerPopUp();
            }
        }

        if (event.getKeyCode() == KeyEvent.VK_SPACE)
        {
            test1 = !test1;
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
     * Menu/Player Options
     */

    /**
     * Handles key release events to stop player movement.
     */

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

        changeColor(blue);
        drawSolidRectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());
        changeColor(orange);
        drawSolidRectangle(player.getHitbox().x, player.getHitbox().y, player.getHitbox().width, player.getHitbox().height);

        changeColor(red);
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




    private boolean isTimerWindowOpen = false;
    public class TimerPopUp {
        private JFrame frame;
        private JLabel timerLabel;
        private Timer timer;
        private int elapsedTime = 0;

        public TimerPopUp() {
            if (isTimerWindowOpen) return; // Check if the timer window is already open

            isTimerWindowOpen = true; // Set flag to indicate the timer window is open

            frame = new JFrame("Timer");
            frame.setSize(200, 100);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    isTimerWindowOpen = false; // Reset flag when window is closed
                    stopTimer(); // Stop the timer when the window is closed
                }
            });

            timerLabel = new JLabel("Time: 0 s", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Arial", Font.BOLD, 24));

            frame.add(timerLabel);
            frame.setVisible(true);

            createTimer();
            startTimer(); // Start the timer when the window is opened
        }

        private void createTimer() {
            timer = new Timer(1000, e -> {
                elapsedTime++;
                timerLabel.setText("Time: " + elapsedTime + " s");
            });
        }

        public void startTimer() {
            timer.start();
        }

        public void stopTimer() {
            timer.stop();
        }

        public void dispose() {
            frame.dispose();
            isTimerWindowOpen = false; // Reset flag when disposing
            stopTimer(); // Ensure timer is stopped when disposing
        }
    }

    private static boolean isVolumeSliderWindowOpen = false;
    private VolumeSliderPopUp volumeSliderPopUp;


    public class VolumeSliderPopUp {
        private JFrame frame;
        private JSlider volumeSlider;

        public VolumeSliderPopUp() {
            if (Assignment2.isVolumeSliderWindowOpen) return; // Check if the volume slider window is already open

            Assignment2.isVolumeSliderWindowOpen = true; // Set flag to indicate the volume slider window is open

            frame = new JFrame("Volume Control");
            frame.setSize(300, 100);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    Assignment2.isVolumeSliderWindowOpen = false; // Reset flag when window is closed
                }
            });

            volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 20); // Start with volume set to 20%
            volumeSlider.addChangeListener(e -> {
                int value = volumeSlider.getValue();
                float volume = value / 100f;
                VolumeControl.getInstance().setMasterVolume(volume); // Set the master volume
            });

            frame.add(volumeSlider);
            frame.setVisible(true);
        }



        public void dispose() {
            frame.dispose();
            Assignment2.isVolumeSliderWindowOpen = false; // Reset flag when disposing
        }
    }




}
