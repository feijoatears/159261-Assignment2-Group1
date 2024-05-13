package src;

public class Character {
    protected int posX;
    protected int posY;
    public int facingDir;
    boolean[] hasKey = new boolean[10];

    // Constructor
    public Character(int posX, int posY, int facingDir) {
        this.posX = posX;
        this.posY = posY;
        this.facingDir = facingDir;
    }

    // Getters and setters for posX
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    // Getters and setters for posY
    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    // Getters and setters for facingDir
    public int getFacingDir() {
        return facingDir;
    }

    public void setFacingDir(int facingDir) {
        this.facingDir = facingDir;
    }

    // Method to check if a specific key is present
    public boolean hasKey(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < hasKey.length) {
            return hasKey[keyIndex];
        }
        return false;
    }

    // Method to set a key as collected
    public void collectKey(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < hasKey.length) {
            hasKey[keyIndex] = true;
        }
    }
}
