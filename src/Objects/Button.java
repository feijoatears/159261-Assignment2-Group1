package src.Objects;


import src.GameEngine;
import src.GameEngine.AudioClip;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Button extends Object
{
    //activated, deactivated imgs
    private final ArrayList<Image> images;
    private AudioClip onSound, offSound;
    private boolean slotMachineActive = false;
    private boolean rolling = false;
    private String slotMachineResult = "";
    private int[][] slots = new int[3][3];
    private Random random = new Random();
    private boolean rigged = false;
    private int currentSlot = 0;
    private int currentRow = 0;
    private int[] finalRows = new int[3];
    private int[] selectedColumns = {1, 1, 1};




    public Button(int posX, int posY, ArrayList<AudioClip> sounds)
    {
        super(posX, posY, sounds);

        images = new ArrayList<>();
        images.add(GameEngine.loadImage("resources/Objects/buttonOn.png"));
        images.add(GameEngine.loadImage("resources/Objects/buttonOff.png"));
        this.image = images.getFirst();

        this.hitbox = new Rectangle(posX, posY, image.getHeight(null), image.getHeight(null));


    }

    public Image getCurrentImage()
    {
        return this.image;
    }

    public void activate() {
        this.image = images.get(1);
        isUsed = true;
        toggleSlotMachine();
    }

    public void deactivate() {
        this.image = images.get(0);
        isUsed = false;
        toggleSlotMachine();
    }

    public AudioClip getOnSound()
    {
        return sounds.getFirst();
    }
    public AudioClip getOffSound()
    {
        return sounds.get(1);
    }

    // slots stuff


    public void showPopup(Graphics2D g, int width, int height) {
        if (!slotMachineActive) return;

        int popupWidth = 300;
        int popupHeight = 150;
        int x = (width - popupWidth) / 2;
        int y = (height - popupHeight) / 2;

        g.setColor(Color.BLACK);
        g.fillRect(x, y, popupWidth, popupHeight);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, popupWidth, popupHeight);
        g.drawString("Slot Machine!", x + 115, y + 20);
        g.drawString(slotMachineResult, x + 130, y + 40);
        g.drawString("Press X to roll", x + 115, y + 60);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (rolling && j == currentSlot && i == finalRows[j]) {
                    g.setColor(Color.RED);
                } else if (!rolling && i == finalRows[j]) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(Integer.toString(slots[i][j]), x + 115 + j * 30, y + 80 + i * 30);
            }
        }
    }

    public void rollSlotMachine() {
        if (!rolling) {
            rolling = true;
            slotMachineResult = "Rolling...";
            new Thread(() -> {
                try {
                    // Initial spinning of all slots
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 3; j++) {
                            for (int k = 0; k < 3; k++) {
                                slots[j][k] = random.nextInt(5);
                            }
                        }
                        Thread.sleep(100);
                    }
                    // Stop each column one by one
                    for (int j = 0; j < 3; j++) {
                        for (int i = 0; i < 10; i++) {
                            for (int k = 0; k < 3; k++) {
                                if (rigged && j == 2 && i == 9) {
                                    slots[1][0] = slots[1][1] = slots[1][2] = random.nextInt(5);
                                    slots[0][j] = slots[1][j];
                                    slots[2][j] = slots[1][j];
                                } else {
                                    slots[k][j] = random.nextInt(5);
                                }
                            }
                            currentSlot = j;  // update current slot being stopped
                            if (i == 9) {  // highlight final row
                                finalRows[j] = 1;  // middle row = final row
                            }
                            Thread.sleep(100);
                        }
                    }
                    determineResult();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rolling = false;
            }).start();
        }
    }

    private void determineResult() {
        // Determine if the middle row matches
        if (slots[1][0] == slots[1][1] && slots[1][1] == slots[1][2]) {
            slotMachineResult = "You Win!";
        } else {
            slotMachineResult = "Try Again!";
        }
        rigged = false;
    }

    public void toggleSlotMachine() {
        slotMachineActive = !slotMachineActive;
    }



    public void rigSlotMachine() {
        slots[1][0] = random.nextInt(5);
        slots[1][1] = slots[1][0];
        slots[1][2] = slots[1][0];
        slotMachineResult = "You Win!";
    }

}
