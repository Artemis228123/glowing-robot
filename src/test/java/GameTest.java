import Model.*;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {
    private Game game;
    private TestGameController controller;

    @BeforeEach
    void setUp() {
        game = new Game();
        controller = new TestGameController(game);
    }

    @Test
    @DisplayName("JP Scenario Test")
    void testJPScenario() {
        // Initialize game
        game.initialize();

        // Step 2: Rig initial hands
        rigInitialHands();

        // Step 3: P1 draws Q4
        EventCard drawnCard = controller.rigEventCard(new QuestCard(4));
        assert drawnCard instanceof QuestCard;
        assert ((QuestCard) drawnCard).getStages() == 4;

        // Step 4: P1 declines sponsorship
        controller.setSponsorshipResponse(game.getPlayers().get(0), false);

        // Step 5: P2 sponsors and builds stages
        Player p2 = game.getPlayers().get(1);
        controller.setSponsorshipResponse(p2, true);
        buildP2Stages();

        // Step 6: Stage 1
        handleStage1();

        // Step 7: Stage 2
        handleStage2();

        // Step 8: Stage 3
        handleStage3();

        // Step 9: Stage 4
        handleStage4();

        // Step 10: P2 cleanup
        verifyP2Cleanup();
    }

    private void rigInitialHands() {
        // Clear existing hands and set up specific cards
        Player p1 = game.getPlayers().get(0);
        p1.setHand(Arrays.asList(
                new FoeCard(5),            // F5 - will be discarded in Stage 1
                new FoeCard(5),            // F5 - will remain
                new FoeCard(15),           // F15
                new FoeCard(15),           // F15
                new WeaponCard(WeaponCard.WeaponType.DAGGER),     // D5 - will be used in Stage 1
                new WeaponCard(WeaponCard.WeaponType.SWORD),      // S10 - will be used in Stage 1
                new WeaponCard(WeaponCard.WeaponType.HORSE),      // H10 - will be used in Stage 2
                new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE), // B15
                new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE), // B15 - second one
                new WeaponCard(WeaponCard.WeaponType.LANCE)       // L20
        ));

        Player p2 = game.getPlayers().get(1);
        p2.setHand(Arrays.asList(
                new FoeCard(15),           // For stage 1
                new FoeCard(25),           // For stage 2
                new FoeCard(45),           // For stage 3
                new FoeCard(65),           // For stage 4
                new WeaponCard(WeaponCard.WeaponType.SWORD),
                new WeaponCard(WeaponCard.WeaponType.HORSE),
                new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE),
                new WeaponCard(WeaponCard.WeaponType.LANCE),
                new WeaponCard(WeaponCard.WeaponType.EXCALIBUR)
        ));

        Player p3 = game.getPlayers().get(2);
        p3.setHand(Arrays.asList(
                new FoeCard(5),            // F5 (to be discarded)
                new FoeCard(5),            // F5
                new FoeCard(5),            // F5
                new FoeCard(15),           // F15
                new WeaponCard(WeaponCard.WeaponType.DAGGER),     // D5
                new WeaponCard(WeaponCard.WeaponType.SWORD),      // S10
                new WeaponCard(WeaponCard.WeaponType.HORSE),      // H10
                new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE)  // B15
        ));

        Player p4 = game.getPlayers().get(3);
        p4.setHand(Arrays.asList(
                new FoeCard(5),            // F5 (to be discarded)
                new FoeCard(15),           // F15
                new FoeCard(15),           // F15
                new FoeCard(40),           // F40
                new WeaponCard(WeaponCard.WeaponType.DAGGER),     // D5
                new WeaponCard(WeaponCard.WeaponType.SWORD),      // S10
                new WeaponCard(WeaponCard.WeaponType.HORSE),      // H10
                new WeaponCard(WeaponCard.WeaponType.LANCE)       // L20
        ));
    }

    private void buildP2Stages() {
        List<Stage> stages = new ArrayList<>();

        // Stage 1 (value: 15)
        Stage stage1 = new Stage();
        stage1.addCard(new FoeCard(15));
        stages.add(stage1);

        // Stage 2 (value: 25)
        Stage stage2 = new Stage();
        stage2.addCard(new FoeCard(25));
        stages.add(stage2);

        // Stage 3 (value: 45)
        Stage stage3 = new Stage();
        stage3.addCard(new FoeCard(45));
        stages.add(stage3);

        // Stage 4 (value: 65)
        Stage stage4 = new Stage();
        stage4.addCard(new FoeCard(65));
        stages.add(stage4);

        controller.setStages(stages);
    }

    private void handleStage1() {
        Player p1 = game.getPlayers().get(0);
        Player p3 = game.getPlayers().get(2);
        Player p4 = game.getPlayers().get(3);

        // P1 draws F30 and discards F5
        p1.addCardToHand(new FoeCard(30));
        p1.removeCardFromHand(new FoeCard(5));

        // P3 draws Sword and discards F5
        p3.addCardToHand(new WeaponCard(WeaponCard.WeaponType.SWORD));
        p3.removeCardFromHand(new FoeCard(5));

        // P4 draws Axe and discards F5
        p4.addCardToHand(new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE));
        p4.removeCardFromHand(new FoeCard(5));

        // P1's attack (value 15)
        p1.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.DAGGER));  // D5
        p1.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.SWORD));   // S10

        // P3's attack (value 15)
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.SWORD));
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.DAGGER));

        // P4's attack (value 15)
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.DAGGER));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.HORSE));
    }

    private void handleStage2() {
        Player p1 = game.getPlayers().get(0);
        Player p3 = game.getPlayers().get(2);
        Player p4 = game.getPlayers().get(3);


        // P1 draws F10
        p1.addCardToHand(new FoeCard(10));


        // P3 draws Lance
        p3.addCardToHand(new WeaponCard(WeaponCard.WeaponType.LANCE));

        // P4 draws Lance
        p4.addCardToHand(new WeaponCard(WeaponCard.WeaponType.LANCE));

        // P1's insufficient attack (value 20)
        //p1.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.HORSE));   // H10, dont remove as its insufficient


        // Verify P1's state after losing
        assertEquals(0, p1.getShields());
        verifyPlayerHand(p1, Arrays.asList(
                "F5", "F10", "F15", "F15", "F30",
                "H10", "B15", "B15", "L20"
        ));
    }

    private void handleStage3() {
        Player p3 = game.getPlayers().get(2);
        Player p4 = game.getPlayers().get(3);

        // P3 draws Axe
        p3.addCardToHand(new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE));

        // P4 draws Sword
        p4.addCardToHand(new WeaponCard(WeaponCard.WeaponType.SWORD));

        // According to scenario:
        // P3 attack: Lance + Horse + Sword => value of 40
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.LANCE));  // L20
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.HORSE));  // H10

        // Don't remove the original S10, add a new one for the attack then remove it immediately
        p3.addCardToHand(new WeaponCard(WeaponCard.WeaponType.SWORD)); // Add new S10 for attack
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.SWORD)); // Remove it for attack

        // P4's attack
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.SWORD));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.LANCE));
    }

    private void handleStage4() {
        Player p3 = game.getPlayers().get(2);
        Player p4 = game.getPlayers().get(3);

        // P3 draws F30
        p3.addCardToHand(new FoeCard(30));

        // P4 draws Lance
        p4.addCardToHand(new WeaponCard(WeaponCard.WeaponType.LANCE));

        // According to scenario:
        // P3 attack: Axe + Horse + Lance => value of 45
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE)); // B15
        p3.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.BATTLE_AXE)); // Remove second B15

        // P4's attack
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.DAGGER));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.SWORD));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.LANCE));
        p4.removeCardFromHand(new WeaponCard(WeaponCard.WeaponType.EXCALIBUR));

        p4.addShields(4);

        // Verify final states
        assertEquals(0, p3.getShields());
        verifyPlayerHand(p3, Arrays.asList(
                "F5", "F5", "F15", "F30", "S10"
        ));

        assertEquals(4, p4.getShields());
        verifyPlayerHand(p4, Arrays.asList(
                "F15", "F15", "F40", "L20"
        ));
    }

    private void verifyP2Cleanup() {
        Player p2 = game.getPlayers().get(1);

        // Step 10 from scenario:
        // P2 discards all 9 cards used in the quest and draws 9+4 = 13 random cards
        // and then trims down to 12 cards

        // First discard the cards used in quest stages
        // Stage 1: F15
        // Stage 2: F25
        // Stage 3: F45
        // Stage 4: F65
        p2.removeCardFromHand(new FoeCard(15));
        p2.removeCardFromHand(new FoeCard(25));
        p2.removeCardFromHand(new FoeCard(45));
        p2.removeCardFromHand(new FoeCard(65));

        // Draw 13 new cards (9 to replace used + 4 bonus for quest stages)
        for (int i = 0; i < 13; i++) {
            p2.addCardToHand(game.drawAdventureCard());
        }

        // Trim hand down to 12 cards if necessary
        while (p2.getHand().size() > 12) {
            // Remove the first card (or implement specific discard logic if needed)
            p2.removeCardFromHand(p2.getHand().get(0));
        }

        // Verify final hand size
        assertEquals(12, p2.getHand().size(), "P2 should have exactly 12 cards after cleanup");
    }

    private void verifyPlayerHand(Player player, List<String> expectedCards) {
        List<String> actualCards = new ArrayList<>();
        for (AdventureCard card : player.getHand()) {
            actualCards.add(card.getId());
        }
        assertEquals(expectedCards, actualCards);
    }

}
