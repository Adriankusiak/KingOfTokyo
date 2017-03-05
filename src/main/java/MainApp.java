import controller.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
        Game game = new Game();
        new GameController(gameBoard, game);
        String css = this.getClass().getResource("/style.css").toExternalForm();
        Scene scene = new Scene(gameBoard);
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((e)-> game.endRunning());
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}