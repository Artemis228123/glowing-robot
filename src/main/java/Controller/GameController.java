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
            view.clearScreen();
            view.displayMessage("Player " + player.getId() + ", do you want to sponsor this quest?");
            if (view.getYesNoChoice("")) {
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

        // Initial participants
        for (Player player : game.getPlayers()) {
            if (player != quest.getSponsor() &&
                    view.getYesNoChoice(player.getId() + ", do you want to participate?")) {
                activeParticipants.add(player);
                quest.addParticipant(player);
            }
        }

        // Resolve each stage
        List<Stage> stages = quest.getStages();
        for (int stageNum = 0; stageNum < stages.size() && !activeParticipants.isEmpty(); stageNum++) {
            Stage stage = stages.get(stageNum);
            view.displayMessage("\nResolving Stage " + (stageNum + 1));

            // Handle withdrawals before stage resolution
            Set<Player> stageParticipants = handleStageWithdrawals(activeParticipants, stageNum + 1);
            if (stageParticipants.isEmpty()) {
                view.displayMessage("No participants remain for this stage. Quest is over.");
                break;
            }

            resolveStage(quest, stage, stageParticipants, activeParticipants);
        }
    }

    private void resolveStage(Quest quest, Stage stage, Set<Player> stageParticipants,
                              Set<Player> activeParticipants) {
        // Draw cards for participants
        for (Player participant : stageParticipants) {
            handleCardDrawAndDiscard(participant, 1);
        }

        // Resolve attacks
        Set<Player> stageWinners = new HashSet<>();
        for (Player participant : stageParticipants) {
            Attack attack = buildAttack(participant);
            if (attack.getValue() >= stage.getValue()) {
                stageWinners.add(participant);
            }
            discardAttackCards(participant, attack);
        }

        // Update active participants
        activeParticipants.clear();
        activeParticipants.addAll(stageWinners);

        // If this is the last stage, award shields and check for victory
        if (stage == quest.getStages().get(quest.getStages().size() - 1)) {
            awardShieldsAndCheckVictory(quest, stageWinners);
        }
    }

    private void awardShieldsAndCheckVictory(Quest quest, Set<Player> winners) {
        for (Player winner : winners) {
            int shieldsAwarded = quest.getQuestCard().getStages();
            winner.addShields(shieldsAwarded);
            view.displayMessage(winner.getId() + " wins " + shieldsAwarded + " shields!");

            // Immediate victory check after each shield award
            if (winner.getShields() >= 7) {
                game.setGameOver(true);
                view.displayMessage(winner.getId() + " has won the game!");
                announceWinners();
                return;
            }
        }
    }


    private Set<Player> handleStageWithdrawals(Set<Player> activeParticipants, int stageNumber) {
        Set<Player> stageParticipants = new HashSet<>();

        for (Player participant : activeParticipants) {
            view.clearScreen();
            view.displayMessage("Stage " + stageNumber);
            view.displayMessage("Player " + participant.getId() + "'s turn");
            view.displayPlayerHand(participant);

            if (!view.getYesNoChoice("Do you want to withdraw from the quest?")) {
                stageParticipants.add(participant);
            } else {
                view.displayMessage("Player " + participant.getId() + " has withdrawn from the quest.");
            }
        }

        return stageParticipants;
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

    private void handleCardDrawAndDiscard(Player player, int cardsToDraw) {
        // Draw cards first
        List<AdventureCard> drawnCards = new ArrayList<>();
        for (int i = 0; i < cardsToDraw; i++) {
            AdventureCard card = game.drawAdventureCard();
            drawnCards.add(card);
            player.addCardToHand(card);
        }

        // Then handle discarding if over limit
        trimHandIfNeeded(player);
    }


    private void trimHandIfNeeded(Player player) {
        while (player.getHand().size() > 12) {
            view.clearScreen();
            view.displayMessage("Player " + player.getId() + " must discard " +
                    (player.getHand().size() - 12) + " cards");
            view.displayPlayerHand(player);

            int choice = view.getCardChoice(player);
            if (choice > 0 && choice <= player.getHand().size()) {
                AdventureCard card = player.getHand().get(choice - 1);
                game.discardAdventureCard(card);
                player.removeCardFromHand(card);
            }
        }
    }

    private void endTurn() {
        view.displayMessage("Press Enter to leave the hot seat...");
        view.waitForKeyPress();
        view.clearScreen();
        game.nextTurn();
        Player nextPlayer = game.getCurrentPlayer();
        view.displayMessage("Player " + nextPlayer.getId() + "'s turn!");
        view.displayPlayerHand(nextPlayer);
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
