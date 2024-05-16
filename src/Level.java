package src;

import java.awt.*;

public class Level
{
    private int index;
    private Image image;

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
