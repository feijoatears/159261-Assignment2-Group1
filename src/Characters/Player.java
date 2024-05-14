package src.Characters;

public class Player extends Character
{
    private static Player instance;

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

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
}
