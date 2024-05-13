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

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;


public class Assignment2 extends GameEngine
{
    static int framerate = 30;

    protected Image backgroundImage;
    public Player player = new Player();

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
        backgroundImage = loadImage("resources/priest.png"); //using png as an example


    }

    public void update(double dt)
    {

    }

    public void paintComponent()
    {
        //have to create imageObserver so make an inline one
        mGraphics.drawImage(backgroundImage, 0, 0, width(), height(),  new ImageObserver()
        {
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) { return false; }
        });


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
}