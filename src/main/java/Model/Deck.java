package Model;

import java.util.*;

public class Deck<T extends Card> {
    private final Stack<T> cards;
    private final Stack<T> discardPile;

    public Deck() {
        this.cards = new Stack<>();
        this.discardPile = new Stack<>();
    }

    public void addCard(T card) {
        cards.push(card);
    }

    public T drawCard() {
        if (cards.isEmpty() && !discardPile.isEmpty()) {
            reshuffleDiscardPile();
        }
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in deck!");
        }
        return cards.pop();
    }

    public void discard(T card) {
        discardPile.push(card);
    }

    private void reshuffleDiscardPile() {
        List<T> tempCards = new ArrayList<>(discardPile);
        Collections.shuffle(tempCards);
        cards.addAll(tempCards);
        discardPile.clear();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }
}