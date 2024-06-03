package src.Objects;

import src.GameEngine;
import src.GameEngine.AudioClip;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MathButton extends Button {
    private boolean quizActive = false;
    private String quizQuestion = "";
    private String quizAnswer = "";
    private final Random random = new Random();
    private boolean isAnswered = false;

    public MathButton(int posX, int posY, ArrayList<AudioClip> sounds) {
        super(posX, posY, sounds);
        generateQuizQuestion();
    }

    private void generateQuizQuestion() {
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;
        int result = num1 + num2;
        quizQuestion = num1 + " + " + num2 + " = ?";
        quizAnswer = String.valueOf(result);
    }

    public boolean checkAnswer(String answer) {
        isAnswered = answer.equals(quizAnswer);
        return isAnswered;
    }

    public void toggleQuiz() {
        quizActive = !quizActive;
    }

    public String getQuizQuestion() {
        return quizQuestion;
    }

    public String getQuizAnswer() {
        return quizAnswer;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    @Override
    public void activate() {
        super.activate();
        toggleQuiz();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        toggleQuiz();
    }

    @Override
    public void showPopup(Graphics2D g, int width, int height) {
        if (!quizActive) return;

        int popupWidth = 300;
        int popupHeight = 150;
        int x = (width - popupWidth) / 2;
        int y = (height - popupHeight) / 2;

        g.setColor(Color.BLACK);
        g.fillRect(x, y, popupWidth, popupHeight);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, popupWidth, popupHeight);
        g.drawString("Math Quiz!", x + 115, y + 20);
        g.drawString(quizQuestion, x + 130, y + 40);
        g.drawString("Press Enter to submit answer", x + 115, y + 60);
    }
}
