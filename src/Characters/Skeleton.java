package src.Characters;

import static src.GameEngine.loadImage;

import java.awt.*;

public class Skeleton extends Enemy {
    public Skeleton(int posX, int posY, int speed, int damage) {
        super(loadImage("resources/Sprites/skeletonWhite.png"), posX, posY, speed, damage);
        this.hitbox = new Rectangle(posX, posY, getImage().getWidth(null), getImage().getHeight(null));// Load the skeleton image
    }

    @Override
    public void chasePlayer(Player player) {
        // Implement the chase behavior specific to the Skeleton if we want to
        super.chasePlayer(player);
    }
}