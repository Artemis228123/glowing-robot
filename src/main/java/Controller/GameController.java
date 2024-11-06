package Controller;

import Model.*;
import View.GameView;
import java.util.*;

public class GameController {
    private final Game game;
    private final GameView view;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }

    public void startGame() {
        game.initialize();
        while (!game.isGameOver()) {
            playTurn();
        }
        announceWinners();
    }

    private void playTurn() {
        Player currentPlayer = game.getCurrentPlayer();
        view.clearScreen();
        view.displayMessage("It's " + currentPlayer.getId() + "'s turn!");
        view.displayPlayerHand(currentPlayer);

        EventCard eventCard = game.drawEventCard();
        view.displayMessage("Drew event card: " + eventCard);

        if (eventCard instanceof QuestCard) {
            handleQuest((QuestCard) eventCard);
        } else {
            handleEvent((EventActionCard) eventCard);
        }

        endTurn();
    }

    private void handleQuest(QuestCard questCard) {
        Player sponsor = findSponsor(questCard);
        if (sponsor == null) {
            view.displayMessage("No one sponsored the quest.");
            return;
        }

        Quest quest = new Quest(questCard, sponsor);
        if (!buildQuest(quest)) {
            view.displayMessage("Failed to build quest properly.");
            return;
        }

        resolveQuest(quest);
    }

    private Player findSponsor(QuestCard questCard) {
        Player currentPlayer = game.getCurrentPlayer();
        List<Player> players = game.getPlayers();
        int currentIndex = players.indexOf(currentPlayer);

        for (int i = 0; i < players.size(); i++) {
            int index = (currentIndex + i) % players.size();
            Player player = players.get(index);
            view.displayPlayerHand(player);
            if (view.getYesNoChoice("Do you want to sponsor this quest?")) {
                return player;
            }
        }
        return null;
    }

    private boolean buildQuest(Quest quest) {
        Player sponsor = quest.getSponsor();
        view.displayPlayerHand(sponsor);

        for (int stageNum = 1; stageNum <= quest.getQuestCard().getStages(); stageNum++) {
            Stage stage = new Stage();
            view.displayMessage("Building stage " + stageNum);

            while (true) {
                int choice = view.getCardChoice(sponsor);
                if (choice == 0) {
                    if (!stage.isValid()) {
                        view.displayError("Stage cannot be empty");
                        continue;
                    }
                    if (!quest.addStage(stage)) {
                        view.displayError("Insufficient value for this stage");
                        continue;
                    }
                    break;
                }

                AdventureCard card = sponsor.getHand().get(choice - 1);
                if (!stage.addCard(card)) {
                    view.displayError("Invalid card selection");
                    continue;
                }
                view.displayCurrentStage(stage);
            }
        }
        return true;
    }

    private void resolveQuest(Quest quest) {
        Set<Player> activeParticipants = new HashSet<>();

        // Get initial participants
        for (Player player : game.getPlayers()) {
            if (player != quest.getSponsor() &&
                    view.getYesNoChoice(player.getId() + ", do you want to participate?")) {
                activeParticipants.add(player);
                quest.addParticipant(player);
            }
        }

        // Resolve each stage
        List<Stage> stages = quest.getStages();
        for (int i = 0; i < stages.size() && !activeParticipants.isEmpty(); i++) {
            Stage stage = stages.get(i);
            view.displayMessage("\nResolving Stage " + (i + 1));

            // Draw cards for participants
            for (Player participant : activeParticipants) {
                AdventureCard drawnCard = game.drawAdventureCard();
                participant.addCardToHand(drawnCard);
                trimHandIfNeeded(participant);
            }

            // Resolve attacks
            Set<Player> stageWinners = new HashSet<>();
            for (Player participant : activeParticipants) {
                Attack attack = buildAttack(participant);
                if (attack.getValue() >= stage.getValue()) {
                    stageWinners.add(participant);
                }
                discardAttackCards(participant, attack);
            }

            activeParticipants = stageWinners;
            if (i == stages.size() - 1) {
                // Last stage - award shields
                for (Player winner : activeParticipants) {
                    winner.addShields(quest.getQuestCard().getStages());
                    quest.addWinner(winner);
                }
            }
        }

        // Cleanup after quest
        cleanupQuest(quest);
    }

    private void cleanupQuest(Quest quest) {
        Player sponsor = quest.getSponsor();
        int cardsUsed = quest.getStages().stream()
                .mapToInt(stage -> stage.getCards().size())
                .sum();

        // Discard quest cards and draw new ones
        for (Stage stage : quest.getStages()) {
            for (AdventureCard card : stage.getCards()) {
                game.discardAdventureCard(card);
                sponsor.removeCardFromHand(card);
            }
        }

        // Draw new cards
        int cardsToDraw = cardsUsed + quest.getQuestCard().getStages();
        for (int i = 0; i < cardsToDraw; i++) {
            sponsor.addCardToHand(game.drawAdventureCard());
        }

        trimHandIfNeeded(sponsor);
    }

    private Attack buildAttack(Player player) {
        Attack attack = new Attack();
        view.displayPlayerHand(player);

        while (true) {
            int choice = view.getCardChoice(player);
            if (choice == 0) break;

            AdventureCard card = player.getHand().get(choice - 1);
            if (!(card instanceof WeaponCard)) {
                view.displayError("Only weapon cards can be used in attacks");
                continue;
            }

            if (!attack.addWeapon((WeaponCard) card)) {
                view.displayError("Cannot use duplicate weapon types");
                continue;
            }

            view.displayAttack(attack);
        }

        return attack;
    }

    private void discardAttackCards(Player player, Attack attack) {
        for (WeaponCard weapon : attack.getWeapons()) {
            game.discardAdventureCard(weapon);
            player.removeCardFromHand(weapon);
        }
    }

    private void handleEvent(EventActionCard eventCard) {
        switch (eventCard.getType()) {
            case PLAGUE:
                game.getCurrentPlayer().loseShields(2);
                view.displayMessage(game.getCurrentPlayer().getId() +
                        " loses 2 shields!");
                break;
            case QUEENS_FAVOR:
                for (int i = 0; i < 2; i++) {
                    game.getCurrentPlayer().addCardToHand(game.drawAdventureCard());
                }
                trimHandIfNeeded(game.getCurrentPlayer());
                break;
            case PROSPERITY:
                for (Player player : game.getPlayers()) {
                    for (int i = 0; i < 2; i++) {
                        player.addCardToHand(game.drawAdventureCard());
                    }
                    trimHandIfNeeded(player);
                }
                break;
        }
    }

    private void trimHandIfNeeded(Player player) {
        while (player.getHand().size() > 12) {
            view.displayPlayerHand(player);
            view.displayMessage("You must discard " +
                    (player.getHand().size() - 12) + " cards.");
            int choice = view.getCardChoice(player);
            if (choice > 0 && choice <= player.getHand().size()) {
                AdventureCard card = player.getHand().get(choice - 1);
                game.discardAdventureCard(card);
                player.removeCardFromHand(card);
            }
        }
    }

    private void endTurn() {
        view.displayMessage("End of turn");
        view.waitForKeyPress();
        game.nextTurn();
    }

    private void announceWinners() {
        List<Player> winners = game.getWinners();
        if (winners.isEmpty()) {
            view.displayMessage("Game over! No winners yet.");
        } else {
            view.displayMessage("Game over! Winners:");
            for (Player winner : winners) {
                view.displayMessage(winner.getId() + " with " +
                        winner.getShields() + " shields!");
            }
        }
    }
}
