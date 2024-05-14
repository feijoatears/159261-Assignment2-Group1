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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.*;


public class Assignment2 extends GameEngine
{
    static int framerate = 30;

    protected Image backgroundImage;
    public Player player = Player.getInstance();

    //using a hashset to hold 2 key presses from user
    Set<Integer> keys = new HashSet<>();

    //can be moved to player class
    public boolean isMoving = false;

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
        backgroundImage = loadImage("resources/TestBackground1.png");

        //temporary player img
        player.setImage("resources/nosferatu.png");
    }

    public void update(double dt)
    {
        if(isMoving)
        {
            player.move();
        }
    }

    public void paintComponent()
    {
        //have to create imageObserver so make an inline one
        mGraphics.drawImage(backgroundImage, 0, 0, width(), height(),  new ImageObserver()
        {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) { return false; }
        });

        drawImage(player.getImage(), player.getPosX(), player.getPosY());
    }

    Direction lastDirection = null;
    @Override
    public void keyPressed(KeyEvent event)
    {
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