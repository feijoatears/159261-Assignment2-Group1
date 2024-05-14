package src.Characters;

public class Player extends Character
{
    private static Player instance;

    private Player() { };

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
                this.setPosY(posY - 1);
                break;
            }
            case South:
            {
                this.setPosY(posY + 1);
                break;
            }
            case West:
            {
                this.setPosX(posX - 1);
                break;
            }
            case East:
            {
                this.setPosX(posX + 1);
                break;
            }
            case Northwest:
            {
                this.setPosY(posY - 1);
                this.setPosX(posX - 1);
                break;
            }
            case Northeast:
            {
                this.setPosY(posY - 1);
                this.setPosX(posX + 1);
                break;
            }
            case Southwest:
            {
                this.setPosY(posY + 1);
                this.setPosX(posX - 1);
                break;
            }
            case Southeast:
            {
                this.setPosY(posY + 1);
                this.setPosX(posX + 1);
                break;
            }
        }
    }
}
