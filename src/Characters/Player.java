package src.Characters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class Player extends Character
{
    private static Player instance;
    private Set<Integer> pressedKeys = new HashSet<>();

    private Player() { };
    private int speed = 5;

    public static Player getInstance()
    {
        if(instance == null)
        {
            instance = new Player();
        }
        return instance;
    }

    public void move()
    {
        //unique to player bc controllable, other sprites will move in a pre-programmed manner
        switch (this.getDirection())
        {
            case North:
            {
                this.setPosY(posY - speed);
                break;
            }
            case South:
            {
                this.setPosY(posY + speed);
                break;
            }
            case West:
            {
                this.setPosX(posX - speed);
                break;
            }
            case East:
            {
                this.setPosX(posX + speed);
                break;
            }
            case Northwest:
            {
                this.setPosY(posY - speed);
                this.setPosX(posX - speed);
                break;
            }
            case Northeast:
            {
                this.setPosY(posY - speed);
                this.setPosX(posX + speed);
                break;
            }
            case Southwest:
            {
                this.setPosY(posY + speed);
                this.setPosX(posX - speed);
                break;
            }
            case Southeast:
            {
                this.setPosY(posY + speed);
                this.setPosX(posX + speed);
                break;
            }
        }
    }


    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();


        if(keyCode == KeyEvent.VK_RIGHT){
            if(pressedKeys.contains(KeyEvent.VK_UP)) {
                setDirection(Direction.Northeast);
            } else if(pressedKeys.contains(KeyEvent.VK_DOWN)){
                setDirection(Direction.Southeast);
            } else {
                setDirection(Direction.East);
            }
        } else if(keyCode == KeyEvent.VK_LEFT){
            if(pressedKeys.contains(KeyEvent.VK_UP)) {
                setDirection(Direction.Northwest);
            } else if(pressedKeys.contains(KeyEvent.VK_DOWN)){
                setDirection(Direction.Southwest);
            } else {
                setDirection(Direction.West);
            }
        } else if (keyCode == KeyEvent.VK_UP) {
            if(!pressedKeys.contains(KeyEvent.VK_RIGHT) && !pressedKeys.contains(KeyEvent.VK_LEFT)) {
                setDirection(Direction.North);
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
            if(!pressedKeys.contains(KeyEvent.VK_RIGHT) && !pressedKeys.contains(KeyEvent.VK_LEFT)) {
                setDirection(Direction.South);
            }
        }    

        pressedKeys.add(keyCode);
    }


    public boolean checkCollision(Rectangle other) {
        Rectangle playerRect = new Rectangle(this.posX, this.posY, this.image.getWidth(null), this.image.getHeight(null));
        return playerRect.intersects(other);
    }
    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
}
