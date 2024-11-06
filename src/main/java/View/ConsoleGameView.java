package View;

import Model.*;
import java.util.*;

public class ConsoleGameView implements GameView {
    private final Scanner scanner;

    public ConsoleGameView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String error) {
        System.err.println("Error: " + error);
    }

    @Override
    public void displayPlayerHand(Player player) {
        System.out.println("\nPlayer " + player.getId() + "'s hand:");
        List<AdventureCard> hand = player.getHand();
        for (int i = 0; i < hand.size(); i++) {
            System.out.printf("%d: %s  ", i + 1, hand.get(i));
        }
        System.out.println("\nShields: " + player.getShields());
    }

    @Override
    public void displayCurrentStage(Stage stage) {
        System.out.println("\nCurrent stage value: " + stage.getValue());
        System.out.println("Cards: " + stage.getCards());
    }

    @Override
    public void displayAttack(Attack attack) {
        System.out.println("\nAttack value: " + attack.getValue());
        System.out.println("Weapons: " + attack.getWeapons());
    }

    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void waitForKeyPress() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    @Override
    public int getCardChoice(Player player) {
        System.out.println("Enter card position (1-" + player.getHand().size() +
                ") or 0 to quit:");
        return scanner.nextInt();
    }

    @Override
    public boolean getYesNoChoice(String prompt) {
        System.out.println(prompt + " (y/n):");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}
