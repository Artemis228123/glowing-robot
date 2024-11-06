package Model;

public abstract class Card {
    private final String id;
    private final int value;

    public Card(String id, int value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card card = (Card) obj;
        return id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
