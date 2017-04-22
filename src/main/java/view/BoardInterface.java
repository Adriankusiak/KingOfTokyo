package view;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.util.ArrayList;

/**
 * Specialised pane containing all button interfaces for the GameBoard view.
 */
public class BoardInterface extends VBox{

    private GridPane dicePane;
    private VBox playerChoice;
    private HBox gameOptions;
    private TextField playerField;
    private TextField energyField;
    private TextField addressField;
    private TextField portField;
    private ArrayList<Button> choiceButtons;
    private ArrayList<ToggleButton> diceButtons;
    private ArrayList<Image> dice;

    /**
     * Constructs new BoardInterface object, with it's state set to display player number interface.
     */
    public BoardInterface(){
        setupImages();
        setupLayouts();
        setPlayerChoice();
    }

    private void setupImages() {
        dice = new ArrayList<>();
        int requestedSize = 67;
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/oneDice.png"), requestedSize, requestedSize, true, true));
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/twoDice.png"), requestedSize, requestedSize, true, true));
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/threeDice.png"), requestedSize, requestedSize, true, true));
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/attackDice.png"), requestedSize, requestedSize, true, true));
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/heartDice.png"), requestedSize, requestedSize, true, true));
        dice.add(new Image(this.getClass().getResourceAsStream("/sprites/energyDice.png"), requestedSize, requestedSize, true, true));
    }

    /**
     * Registers an EventHandler for all die buttons.
     * @param handler The handler to register.
     */
    public void setDiceHandler(EventHandler<ActionEvent> handler){
        for(ToggleButton dieButton : diceButtons){
            dieButton.setOnAction(handler);
        }
    }

    /**
     * Registers an EventHandler for all player selection buttons.
     * @param handler The handler to register.
     */
    public void setPlayerOptionHandler(EventHandler<ActionEvent> handler){
        for(Button pO : choiceButtons){
             pO.setOnAction(handler);
        }
    }

    /**
     * Registers an EventHandler for all game option buttons.
     * @param handler The handler to register.
     */
    public void setGameOptionHandler(EventHandler<ActionEvent> handler){
        for(Node gO : gameOptions.getChildren()){
            ((Button) gO).setOnAction(handler);
        }
    }

    /**
     * Switched interface to display player count buttons.
     */
    public void setPlayerChoice(){
        this.setAlignment(Pos.CENTER);
        this.getChildren().clear();
        this.getChildren().add(playerChoice);
    }

    /**
     * Switched interface to display main game buttons.
     */
    public void setGameInterface() {
        this.setAlignment(Pos.TOP_LEFT);
        this.getChildren().clear();
        this.getChildren().addAll(dicePane, gameOptions);
    }

    private void setupLayouts(){
        this.setSpacing(25);
        diceButtons = new ArrayList<>();
        dicePane = new GridPane();
        dicePane.setGridLinesVisible(true);
      //  dicePane.setAlignment(Pos.CENTER);
       // dicePane.setVgap(125);
        dicePane.setHgap(19);
       // dicePane.setPrefWrapLength(700);
        dicePane.setPadding(new Insets(1,0,0,15));
        for(int i = 0; i < 6; ++i){
            ToggleButton newDieButton = new ToggleButton();

            newDieButton.setId("dieButton");
            newDieButton.setUserData(i);
            newDieButton.setGraphic(new ImageView(dice.get(0)));
            dicePane.add(newDieButton, (i%2), (int) Math.floor(i/2));
            diceButtons.add(newDieButton);
        }


        playerChoice = new VBox();
        gameOptions = new HBox();


        playerField = new TextField("Enter Player Count");
        energyField = new TextField("Enter starting energy");
        addressField = new TextField("Enter IP Address");
        portField = new TextField("Enter Port Number");
        Button hostGame = new Button("Host A Game");
        hostGame.setUserData("HOST");
        Button joinGame = new Button("Join A Game");
        joinGame.setUserData("JOIN");

        choiceButtons = new ArrayList<Button>();
        choiceButtons.add(hostGame);
        choiceButtons.add(joinGame);
        playerChoice.getChildren().addAll(playerField, energyField, hostGame, addressField, portField, joinGame);
        playerChoice.setSpacing(20);
        playerChoice.setAlignment(Pos.CENTER);

        Button roll = new Button();
        roll.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("/sprites/rollButton.png"),180,80,true,true)));
        roll.setUserData("ROLL");
        roll.setId("rollButton");
        gameOptions.getChildren().add(roll);

    }

    public String getIP() {
        return addressField.getText();
    }

    public String getPlayerCount() {
        return playerField.getText();
    }

    public void updateDiceValues(ArrayList<Integer> integers) {
        for(int i = 0; i < diceButtons.size(); ++i){
            ToggleButton dieButton = diceButtons.get(i);
            int newValue = integers.get(i);
            dieButton.setGraphic(new ImageView(dice.get(newValue)));
        }
    }

    public int getPort() {
        return Integer.parseInt(portField.getText());
    }

    public void updateSelected(ArrayList<Boolean> selected) {
        for(int i = 0; i < diceButtons.size(); ++i){
            diceButtons.get(i).setSelected(selected.get(i));
        }
    }
}
