package src.generalClasses;

import java.awt.*;
import src.Direction;

public class Level
{
    private int index;
    private Image image;

    private Direction doorDirections;


    public Level(Image image)
    {
        this.image = image;
    };
    public Level(){}

    public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
    }
}
