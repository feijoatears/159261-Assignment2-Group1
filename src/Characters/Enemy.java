package src.Characters;

import src.Direction;

import java.awt.*;

public class Enemy extends Character {
    private int speed = 3;

    public Enemy(int posX, int posY, Direction direction, Image image) {
        super(posX, posY, direction, image);

    }

    public void followPlayer(Player player) {
        int playerX = player.getPosX();
        int playerY = player.getPosY();

        int dx = playerX - this.getPosX();
        int dy = playerY - this.getPosY();

        Direction moveDirection;
        if(Math.abs(dx) > Math.abs(dy)) {
            if(dx > 0){
                moveDirection = Direction.East;
            } else {
                moveDirection = Direction.West;
            }
        } else {
            if(dy > 0 ) {
                moveDirection = Direction.South;
            } else {
                moveDirection = Direction.North;
            }
        }

        this.move(moveDirection);
    }

    private void move (Direction direction) {
        switch(direction) {
            case North:
                this.setPosY(this.getPosY() - speed);
                break;
            case South:
                this.setPosY(this.getPosY() - speed);
                break;
            case West:
                this.setPosX(this.getPosX() - speed);
                break; 
            case East:
                this.setPosX(this.getPosX() - speed);
                break;    
            case Northeast:
                this.setPosY(this.getPosY() - speed);
                this.setPosX(this.getPosX() + speed);
                break;
            case NorthWest:
                this.setPosY(this.getPosY() - speed);
                this.setPosX(this.getPosX() - speed);
                break;
            case Southeast:
                this.setPosY(this.getPosY() + speed);
                this.setPosX(this.getPosX() + speed);
                break;   
            case Southwest:
                this.setPosY(this.getPosY() + speed);
                this.setPosX(this.getPosX() - speed);
                break;
        }
    }
}


        
