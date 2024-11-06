package View;

import Model.*;

import java.util.*;

public interface GameView {
    void displayMessage(String message);
    void displayError(String error);
    void displayPlayerHand(Player player);
    void displayCurrentStage(Stage stage);
    void displayAttack(Attack attack);
    void clearScreen();
    void waitForKeyPress();
    int getCardChoice(Player player);
    boolean getYesNoChoice(String prompt);
}
