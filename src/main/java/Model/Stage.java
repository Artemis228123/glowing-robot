package Model;

import java.util.*;

public class Stage {
    private final List<AdventureCard> cards;
    private int value;
    private FoeCard foe;

    public Stage() {
        this.cards = new ArrayList<>();
        this.value = 0;
    }

    public boolean addCard(AdventureCard card) {
        if (card instanceof FoeCard) {
            if (foe != null) {
                return false; // Only one foe allowed per stage
            }
            foe = (FoeCard) card;
        } else {
            WeaponCard weaponCard = (WeaponCard) card;
            if (cards.stream()
                    .filter(c -> c instanceof WeaponCard)
                    .map(c -> (WeaponCard) c)
                    .anyMatch(w -> w.getType() == weaponCard.getType())) {
                return false; // No duplicate weapon types allowed
            }
        }

        cards.add(card);
        value += card.getValue();
        return true;
    }

    public int getValue() {
        return value;
    }

    public List<AdventureCard> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public boolean isValid() {
        return foe != null;
    }
}
