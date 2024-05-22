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
    private boolean slotMachineRolling = false;
    private String slotMachineResult = "Press X to roll";

    private char[] slotChars = {'X', '!', '@', '#', '$', '%', '^', '&', '*'};
    private char[][] slots = new char[3][3];
    private int[] slotPositions = {0, 0, 0};

    private Timer slotMachineTimer;
    private int animationStep = 0;


    public Button(int posX, int posY, ArrayList<AudioClip> sounds)
    {
        super(posX, posY, sounds);

        images = new ArrayList<>();
        images.add(GameEngine.loadImage("resources/Objects/buttonOn.png"));
        images.add(GameEngine.loadImage("resources/Objects/buttonOff.png"));
        this.image = images.getFirst();

        this.hitbox = new Rectangle(posX, posY, image.getHeight(null), image.getHeight(null));


        initializeSlots();

    }

    public Image getCurrentImage()
    {
        return this.image;
    }

    public void activate() {
        this.image = images.get(1);
        isUsed = true;
        slotMachineActive = true;
    }

    public void deactivate() {
        this.image = images.get(0);
        isUsed = false;
        slotMachineActive = false;
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


    private void initializeSlots() {
        Random random = new Random();
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                slots[i][j] = slotChars[random.nextInt(slotChars.length)];
            }
        }
    }

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
        g.drawString("Slot Machine!", x + 10, y + 20);

        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                g.drawString(String.valueOf(slots[i][j]), x + 40 + (i * 40), y + 60 + (j * 20));
            }
        }

        g.drawString(slotMachineResult, x + 10, y + 120);
        g.drawString("Press X to roll", x + 10, y + 140);
    }




    public void rollSlotMachine() {
        if (slotMachineRolling) return;

        slotMachineRolling = true;
        slotMachineResult = "Rolling...";
        animationStep = 0;
        slotMachineTimer = new Timer();
        slotMachineTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSlots();
            }
        }, 0, 100);
    }

    private void updateSlots() {
        animationStep++;
        Random random = new Random();
        for (int i = 0; i < slots.length; i++) {
            slotPositions[i] = (slotPositions[i] + 1) % slotChars.length;
            for (int j = 0; j < slots[i].length; j++) {
                slots[i][j] = slotChars[(slotPositions[i] + j) % slotChars.length];
            }
        }

        if (animationStep >= 20) {
            slotMachineTimer.cancel();
            slotMachineRolling = false;
            checkSlotMachineResult();
        }
    }

    private void checkSlotMachineResult() {
        Random random = new Random();
        if (random.nextInt(5) == 0) {
            slotMachineResult = "You Win!";
        } else {
            slotMachineResult = "Try Again!";
        }
    }


}
