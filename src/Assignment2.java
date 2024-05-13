package src;



/*
    Assignment 2

    Created by:
        Lyle Hunt
        Jamie Nicholson
        Tony Benefield
        Nathan Burr
 */

public class Assignment2 extends GameEngine
{
    static int framerate = 30;
    
    public static void main(String[] args) 
    {
        createGame(new Assignment2(), framerate);
    }
    
    public void init() 
    {
        setWindowSize(600, 600);
    }

    public void update(double dt) {}

    public void paintComponent() {}




    
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