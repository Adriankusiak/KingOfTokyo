package controller;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import model.Game;
import view.GameBoard;

import java.util.Observable;
import java.util.Observer;


/**
 * Controller for the game
 */
public class GameController implements Observer{
    /**
     * The view of the game
     */
    private GameBoard gameBoard;
    /**
     * The model of the game
     */
    private Game game;

    /**
     * Builds a new controller
     *
     * @param gameBoard the view that the controller will control
     * @param game the game instance that will be played
     */
    public GameController(GameBoard gameBoard, Game game) {
        this.gameBoard = gameBoard;
        this.game = game;
        game.addObserver(this);
        setActionListeners();
    }


    /**
     * Set the three action listeners received from the view
     */
    private void setActionListeners() {
        gameBoard.setPlayerOptionHandler(event -> {
            String value = ((Button) event.getSource()).getUserData().toString();
            System.out.println(value);
            if(value.equals("HOST")){
                game.hostGame(gameBoard.getPlayerName(), gameBoard.getPlayerChar(), gameBoard.getPlayerCount(), gameBoard.getMaxPoints(),
                        gameBoard.getStartingEnergy(), gameBoard.getStartingLife(), gameBoard.getMaxLife());
            }else{
                game.joinGame(gameBoard.getIP(), gameBoard.getPort(), gameBoard.getPlayerName(), gameBoard.getPlayerChar());
            }

            gameBoard.switchToGame();
        });

        gameBoard.setGameOptionHandler( event->{
            game.rollSelected();

        });

        gameBoard.setDiceHandler(event->{
            ToggleButton dieButton = (ToggleButton)(event.getSource());
            int dieNum = Integer.parseInt(dieButton.getUserData().toString());
            if(game.isSelected(dieNum)){
                game.tryUnselect(dieNum);
            }else game.trySelect(dieNum);
            gameBoard.updateSelected(game.getSelected());
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        gameBoard.updateDiceValues(game.getDiceValues());
        gameBoard.updateSelected(game.getSelected());
        gameBoard.updatePlayers(game.getPlayers());
        System.out.println("updated");
    }
}
