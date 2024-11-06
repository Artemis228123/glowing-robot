package Model;

import java.util.*;

public class Player {
    private final String id;
    private final List<AdventureCard> hand;
    private int shields;

    public Player(String id) {
        this.id = id;
        this.hand = new ArrayList<>();
        this.shields = 0;
    }

    public String getId() {
        return id;
    }

    public void addCardToHand(AdventureCard card) {
        hand.add(card);
        sortHand();
    }

    public void removeCardFromHand(AdventureCard card) {
        hand.remove(card);
    }

    public List<AdventureCard> getHand() {
        return Collections.unmodifiableList(hand);
    }
    public void clearHand() {
        hand.clear();
    }

    public void setHand(List<AdventureCard> newHand) {
        hand.clear();
        hand.addAll(newHand);
        sortHand();
    }



    public int getShields() {
        return shields;
    }

    public void addShields(int amount) {
        shields += amount;
    }

    public void loseShields(int amount) {
        shields = Math.max(0, shields - amount);
    }

    private void sortHand() {
        Collections.sort(hand, (c1, c2) -> {
            if (c1 instanceof FoeCard && c2 instanceof FoeCard) {
                return Integer.compare(c1.getValue(), c2.getValue());
            } else if (c1 instanceof FoeCard) {
                return -1;
            } else if (c2 instanceof FoeCard) {
                return 1;
            } else {
                WeaponCard w1 = (WeaponCard) c1;
                WeaponCard w2 = (WeaponCard) c2;
                if (w1.getValue() != w2.getValue()) {
                    return Integer.compare(w1.getValue(), w2.getValue());
                }
                // Sort swords before horses when values are equal
                if (w1.getType() == WeaponCard.WeaponType.SWORD &&
                        w2.getType() == WeaponCard.WeaponType.HORSE) {
                    return -1;
                } else if (w1.getType() == WeaponCard.WeaponType.HORSE &&
                        w2.getType() == WeaponCard.WeaponType.SWORD) {
                    return 1;
                }
                return w1.getId().compareTo(w2.getId());
            }
        });
    }

    public void trimHand(List<AdventureCard> cardsToDiscard) {
        hand.removeAll(cardsToDiscard);
        sortHand();
    }
}
