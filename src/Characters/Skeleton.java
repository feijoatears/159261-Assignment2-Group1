package src.Characters;

import src.generalClasses.Level;

import static src.GameEngine.loadImage;

import java.awt.*;

public class Skeleton extends Enemy {
    public Skeleton(int posX, int posY, int speed, int damage) {
        super(posX, posY, speed, damage, "");
        this.hitbox = new Rectangle(posX, posY, getImage().getWidth(null), getImage().getHeight(null));// Load the skeleton image
    }

    @Override
    public void chasePlayer(Player player, Level level) {
        // Implement the chase behavior specific to the Skeleton if we want to
        super.chasePlayer(player, level);
    }
}