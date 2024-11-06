package Model;

import java.util.*;

public class Game {
    private final List<Player> players;
    private final Deck<AdventureCard> adventureDeck;
    private final Deck<EventCard> eventDeck;
    private int currentPlayerIndex;
    private boolean gameOver;

    public Game() {
        this.players = new ArrayList<>();
        this.adventureDeck = new Deck<>();
        this.eventDeck = new Deck<>();
        this.currentPlayerIndex = 0;
        this.gameOver = false;
    }

    public void initialize() {
        initializePlayers();
        initializeDecks();
        dealInitialHands();
    }

    private void initializePlayers() {
        for (int i = 1; i <= 4; i++) {
            players.add(new Player("P" + i));
        }
    }

    private void initializeDecks() {
        // Initialize Adventure Deck
        // Foes
        addFoeCards(5, 8);
        addFoeCards(10, 7);
        addFoeCards(15, 8);
        addFoeCards(20, 7);
        addFoeCards(25, 7);
        addFoeCards(30, 4);
        addFoeCards(35, 4);
        addFoeCards(40, 2);
        addFoeCards(50, 2);
        addFoeCards(70, 1);

        // Weapons
        addWeaponCards(WeaponCard.WeaponType.DAGGER, 6);
        addWeaponCards(WeaponCard.WeaponType.HORSE, 12);
        addWeaponCards(WeaponCard.WeaponType.SWORD, 16);
        addWeaponCards(WeaponCard.WeaponType.BATTLE_AXE, 8);
        addWeaponCards(WeaponCard.WeaponType.LANCE, 6);
        addWeaponCards(WeaponCard.WeaponType.EXCALIBUR, 2);

        // Initialize Event Deck
        // Quest cards
        addQuestCards(2, 3);
        addQuestCards(3, 4);
        addQuestCards(4, 3);
        addQuestCards(5, 2);

        // Event action cards
        eventDeck.addCard(new EventActionCard(EventActionCard.EventType.PLAGUE));
        addEventActionCards(EventActionCard.EventType.QUEENS_FAVOR, 2);
        addEventActionCards(EventActionCard.EventType.PROSPERITY, 2);

        // Shuffle both decks
        adventureDeck.shuffle();
        eventDeck.shuffle();
    }

    private void addFoeCards(int value, int count) {
        for (int i = 0; i < count; i++) {
            adventureDeck.addCard(new FoeCard(value));
        }
    }

    private void addWeaponCards(WeaponCard.WeaponType type, int count) {
        for (int i = 0; i < count; i++) {
            adventureDeck.addCard(new WeaponCard(type));
        }
    }

    private void addQuestCards(int stages, int count) {
        for (int i = 0; i < count; i++) {
            eventDeck.addCard(new QuestCard(stages));
        }
    }

    private void addEventActionCards(EventActionCard.EventType type, int count) {
        for (int i = 0; i < count; i++) {
            eventDeck.addCard(new EventActionCard(type));
        }
    }

    private void dealInitialHands() {
        for (Player player : players) {
            for (int i = 0; i < 12; i++) {
                player.addCardToHand(adventureDeck.drawCard());
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        checkGameOver();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void checkGameOver() {
        for (Player player : players) {
            if (player.getShields() >= 7) {
                gameOver = true;
                break;
            }
        }
    }

    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        if (!gameOver) return winners;

        for (Player player : players) {
            if (player.getShields() >= 7) {
                winners.add(player);
            }
        }
        return winners;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public AdventureCard drawAdventureCard() {
        return adventureDeck.drawCard();
    }

    public EventCard drawEventCard() {
        return eventDeck.drawCard();
    }

    public void discardAdventureCard(AdventureCard card) {
        adventureDeck.discard(card);
    }

    public void discardEventCard(EventCard card) {
        eventDeck.discard(card);
    }
}
