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

import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class Assignment2 extends GameEngine
{

    
    static int framerate = 30;
    protected Image backgroundImage;
    public Player player = new Player();


    public static void main(String[] args) 
    {
        createGame(new Assignment2(), framerate);
    }
    
    public void init() 
    {
        setWindowSize(600, 600);
        backgroundImage = loadImage("resources/TestBackground1.jpg");
        mPanel.repaint();
    }

    public void update(double dt) {}

    public void paintComponent() {

        if (mGraphics == null) return;

        if (backgroundImage != null) {
            drawImage(backgroundImage, 0, 0, width(), height());
        }

    }


    
    

    public void setupWindow(int width, int height)
    {
        // testing for adding a PNG background






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