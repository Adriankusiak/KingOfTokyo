import controller.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Game;
import view.GameBoard;

public class MainApp extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameBoard gameBoard = new GameBoard();

        new GameController(gameBoard, new Game());

        primaryStage.setScene(new Scene(gameBoard));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}