package src.Objects;

import java.awt.*;

public class InvisibleWall extends Object {
    public InvisibleWall(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
        this.hitbox = new Rectangle(posX, posY, width, height);
    }
}
