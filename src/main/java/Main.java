import Controller.GameController;
import Model.Game;
import View.ConsoleGameView;
import View.GameView;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        GameView view = new ConsoleGameView();
        GameController controller = new GameController(game, view);

        controller.startGame();
    }
}
