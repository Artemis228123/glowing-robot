package Model;

import java.util.*;

public class Quest {
    private final QuestCard questCard;
    private final List<Stage> stages;
    private final Player sponsor;
    private final Set<Player> participants;
    private final Set<Player> winners;

    public Quest(QuestCard questCard, Player sponsor) {
        this.questCard = questCard;
        this.stages = new ArrayList<>();
        this.sponsor = sponsor;
        this.participants = new HashSet<>();
        this.winners = new HashSet<>();
    }

    public boolean addStage(Stage stage) {
        if (stages.size() > 0 &&
                stage.getValue() <= stages.get(stages.size() - 1).getValue()) {
            return false;
        }
        stages.add(stage);
        return true;
    }

    public void addParticipant(Player player) {
        if (player != sponsor) {
            participants.add(player);
        }
    }

    public void removeParticipant(Player player) {
        participants.remove(player);
    }

    public void addWinner(Player player) {
        winners.add(player);
    }

    public QuestCard getQuestCard() {
        return questCard;
    }

    public List<Stage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    public Player getSponsor() {
        return sponsor;
    }

    public Set<Player> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public Set<Player> getWinners() {
        return Collections.unmodifiableSet(winners);
    }
}
