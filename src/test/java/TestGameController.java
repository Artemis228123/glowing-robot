import Model.*;
import java.util.*;

public class TestGameController {
    private final Game game;
    private final Map<Player, Boolean> sponsorshipResponses;
    private final Map<Player, Attack> playerAttacks;
    private final Map<Player, AdventureCard> riggedCards;
    private final Map<Player, AdventureCard> riggedDiscards;
    private List<Stage> stages;

    public TestGameController(Game game) {
        this.game = game;
        this.sponsorshipResponses = new HashMap<>();
        this.playerAttacks = new HashMap<>();
        this.riggedCards = new HashMap<>();
        this.riggedDiscards = new HashMap<>();
    }

    public EventCard rigEventCard(EventCard card) {
        // Rig for compulsory scenario
        return new QuestCard(4);
    }

    public void setSponsorshipResponse(Player player, boolean willSponsor) {
        sponsorshipResponses.put(player, willSponsor);
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public void setPlayerAttack(Player player, Attack attack) {
        playerAttacks.put(player, attack);
    }

    public void rigDrawnCard(Player player, AdventureCard card) {
        riggedCards.put(player, card);
    }

    public void rigDiscardChoice(Player player, AdventureCard card) {
        riggedDiscards.put(player, card);
    }

    public boolean getSponsorshipResponse(Player player) {
        return sponsorshipResponses.getOrDefault(player, false);
    }

    public Attack getPlayerAttack(Player player) {
        return playerAttacks.get(player);
    }

    public AdventureCard getNextRiggedCard(Player player) {
        return riggedCards.get(player);
    }

    public AdventureCard getDiscardChoice(Player player) {
        return riggedDiscards.get(player);
    }

    public List<Stage> getStages() {
        return stages;
    }
}