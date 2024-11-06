package Model;

public class EventActionCard extends EventCard {
    private final EventType type;

    public enum EventType {
        PLAGUE("Plague"),
        QUEENS_FAVOR("Queen's Favor"),
        PROSPERITY("Prosperity");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public EventActionCard(EventType type) {
        super("E" + type.getName().charAt(0), 0);
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
