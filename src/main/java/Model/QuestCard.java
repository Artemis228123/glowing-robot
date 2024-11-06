package Model;


public class QuestCard extends EventCard {
    private final int stages;

    public QuestCard(int stages) {
        super("Q" + stages, stages);
        this.stages = stages;
    }

    public int getStages() {
        return stages;
    }
}
