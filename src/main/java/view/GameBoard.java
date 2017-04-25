package view;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import model.Player;

/**
 * Animated interface class to represent game's state.
 */
public class GameBoard extends StackPane {
    private double[] mousePos;
    private Canvas gameView;
    private GraphicsContext graphicsContext;
    private BoardInterface boardInterface;
    private ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

    private Label turnLabel;
    private HBox labelPane;
    private Label winnerLabel;
    private ArrayList<PlayerCard> playerCards;

    /**
     * Constructs new GameBoard object with initial option to choose player count,
     * and game representation being at default (4 seeds in each house).
     */
    public GameBoard(){
        boardInterface = new BoardInterface();
        gameView = new Canvas(960,640);
        mousePos = new double[2];
        boardInterface.setOnMouseMoved((arg)->{
             mousePos[0] = arg.getSceneX();
             mousePos[1] = arg.getSceneY();
        });
        graphicsContext = gameView.getGraphicsContext2D();
        turnLabel = new Label();
        labelPane = new HBox();
        winnerLabel  = new Label();
        setupView();
        startLoop();
    }

    /**
     * Registers an EventHandler for all house buttons in interface.
     * @param handler The handler to register.
     */
    public void setDiceHandler(EventHandler<ActionEvent> handler){
        boardInterface.setDiceHandler(handler);
    }

    /**
     * Registers an EventHandler for all player number option buttons in interface.
     * @param handler The handler to register.
     */
    public void setPlayerOptionHandler(EventHandler<ActionEvent> handler){
        boardInterface.setPlayerOptionHandler(handler);
    }

    /**
     * Registers an EventHandler for all game option buttons in interface.
     * @param handler The handler to register.
     */
    public void setGameOptionHandler(EventHandler<ActionEvent> handler){
        boardInterface.setGameOptionHandler(handler);
    }

    /**
     * Check for whether representation is being updated on screen to match game state changes.
     * @return boolean flag for whether game changes are being drawn, true if yes, false otherwise.
     */
    public synchronized boolean isAnimating(){
        return false; // TODO
    }

    public void write() {
        System.out.println("Board:");

    }


    /**
     * Resets the GameBoard to represent beginning of new game, with player number choice interface.
     */
    public void reset(){

        spriteList.clear();
        //spriteList.add(new Sprite("board.png",640,400,1));
        turnLabel.setText("");
        labelPane.getChildren().clear();
        labelPane.getChildren().add(turnLabel);
        boardInterface.setPlayerChoice();
    }

    /**
     * Switched interface to main game buttons so the game interaction can begin.
     * @param playerCount
     */
    public void switchToGame(int playerCount){
        boardInterface.setGameInterface();
        Sprite dieArea = new Sprite("dieArea.png", 703, 890, 1, 180, 250);
        dieArea.setPos(0, 0);
        spriteList.add(dieArea);
        if(playerCards == null){
            playerCards = new ArrayList<>();
            double cardSpace = (960-playerCount*180)/(playerCount+1);
            for(int i = 0; i < playerCount; ++i){
                PlayerCard card = new PlayerCard(this);
                card.setPos(cardSpace+(180+cardSpace)*i, 390);
                playerCards.add(card);
            }
        }
        for(int i = 0; i < playerCount; ++i){
            spriteList.add(playerCards.get(i));
        }

    }

    /**
     * Changes displayed turn status
     * @param playerN Number of player whose turn should now be displayed (1 or 2)
     */
    public void setTurnLabel(int playerN){
        switch(playerN){
            case 1:
                turnLabel.setText("Player 1's Turn");
                break;
            case 2:
                turnLabel.setText("Player 2's Turn");
                break;
            default:
                break;
        }
    }

    /**
     * Sets the screen to show an end game winner result.
     * @param winner Value communicating who the winner was.
     * Can be 0,1,2 for a draw, player one and player two respectively.
     */
    public void setWinner(int winner){
        switch(winner){
            case 0:
                winnerLabel.setText("Draw!");
                break;
            case 1:
                winnerLabel.setText("Player One Wins!");
                break;

            case 2:
                winnerLabel.setText("Player Two Wins!");
                break;

            default:
                break;
        }

        labelPane.getChildren().clear();
        labelPane.getChildren().add(winnerLabel);
    }



    private void startLoop(){
        new Thread(){{this.setDaemon(true);}
            double updateInterval = 1000000000/60;
            double lastTime = System.nanoTime();
            public void run(){
                while(true){
                    try{
                        Thread.sleep(15);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    double curTime = System.nanoTime();
                    double delta = curTime - lastTime;
                    if(delta > updateInterval){

                        update(delta);

                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                draw();
                            }
                        });

                        lastTime = curTime;
                    }
                }

            }

        }.start();

    }

    private void draw(){
        graphicsContext.clearRect(0, 0, gameView.getWidth(), gameView.getHeight());
        for(Sprite s : spriteList){
            s.draw(graphicsContext);

        }
    }

    private void update(double delta){
        for(Sprite s : spriteList){
            s.update(delta);
        }
    }

    private void setupView(){
        this.getChildren().add(gameView);
        this.getChildren().add(labelPane);
        this.getChildren().add(boardInterface); // add interface here

        labelPane.setAlignment(Pos.TOP_CENTER);
        turnLabel.setFont(Font.font("Cambria", 50));
        winnerLabel.setFont(Font.font("Cambria", 70));
        winnerLabel.setTextFill(Paint.valueOf("Red"));
        winnerLabel.setEffect(new DropShadow());

        reset();
    }


    public String getIP() {
        return boardInterface.getIP();
    }

    public int getPlayerCount() {
        return Integer.parseInt(boardInterface.getPlayerCount());
    }

    public void updateDiceValues(ArrayList<Integer> integers) {
        boardInterface.updateDiceValues(integers);
    }

    public int getPort() {
        return boardInterface.getPort();
    }

    public void updateSelected(ArrayList<Boolean> selected) {
        boardInterface.updateSelected(selected);
    }

    public void updatePlayers(ArrayList<Player> players) {
    }

    public String getPlayerName() {
        return boardInterface.getPlayerName();
    }

    public String getPlayerChar() {
        return boardInterface.getPlayerChar();
    }

    public int getStartingLife() {
        return boardInterface.getStartingLife();
    }

    public int getMaxLife() {
        return boardInterface.getMaxLife();
    }

    public int getMaxPoints() {
        return boardInterface.getMaxPoints();
    }

    public int getStartingEnergy() {
        return boardInterface.getStartingEnergy();
    }

    public double[] getMousePos(){
        return mousePos;
    }
}