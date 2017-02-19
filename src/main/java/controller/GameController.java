package controller;

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

    }
}
