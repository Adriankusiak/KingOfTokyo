package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import model.Game;
import view.GameBoard;


/**
 * Controller for the game
 */
public class GameController {
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
                game.hostGame(gameBoard.getPlayerCount());
            }else{
                game.joinGame(gameBoard.getIP());
            }

            gameBoard.switchToGame();
        });

        gameBoard.setGameOptionHandler( event->{
            gameBoard.updateDiceValues(game.rollSelected());
            gameBoard.unselectAll();
        });

        gameBoard.setDiceHandler(event->{
            ToggleButton dieButton = (ToggleButton)(event.getSource());
            int dieNum = Integer.parseInt(dieButton.getUserData().toString());
            if(game.isSelected(dieNum)){
                game.unselectDice(dieNum);
            }else game.selectDice(dieNum);
        });
    }
}
