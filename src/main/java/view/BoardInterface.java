package view;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private TextField pointField;
    private TextField healthField;
    private TextField maxHealthField;
    private TextField addressField;
    private TextField portField;
    private ArrayList<Button> choiceButtons;
    private ArrayList<ToggleButton> diceButtons;
    private ArrayList<Image> dice;
    private Button joinGame;
    private Button hostGame;
    private TextField nameField;

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

        nameField = new TextField();
        playerField = new TextField();
        energyField = new TextField();
        pointField = new TextField();
        healthField = new TextField();
        maxHealthField = new TextField();
        addressField = new TextField();
        portField = new TextField();
        hostGame = new Button("Host A Game");
        hostGame.setUserData("HOST");
        hostGame.disableProperty().setValue(true);
        joinGame = new Button("Join A Game");
        joinGame.setUserData("JOIN");
        joinGame.disableProperty().setValue(true);

        nameField.setPromptText("Enter player name");
        playerField.setPromptText("Enter player Count");
        energyField.setPromptText("Enter starting energy");
        pointField.setPromptText("Enter victory point goal");
        healthField.setPromptText("Enter starting health");
        maxHealthField.setPromptText("Enter max health");
        addressField.setPromptText("Enter IP Address");
        portField.setPromptText("Enter Port Number");

        // force the fields to be numeric only
        playerField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                playerField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            String currText = playerField.getText();
            if(currText.length() != 0){
                int val = Integer.parseInt(currText.substring(currText.length()-1));
                if(val > 4)  playerField.setText("4");
                else if(val < 2)  playerField.setText("2");
                else playerField.setText(val+"");
            }
            checkHostFields();
        });

        energyField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                energyField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            checkHostFields();
        });

        pointField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pointField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        healthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                healthField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        maxHealthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxHealthField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            addressField.setText(newValue.replaceAll("[^\\d\\.]", ""));

            checkJoinFields();
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            checkJoinFields();
        });
        choiceButtons = new ArrayList<>();
        choiceButtons.add(hostGame);
        choiceButtons.add(joinGame);
        playerChoice.getChildren().addAll(nameField, playerField, energyField, pointField, healthField, maxHealthField, hostGame, addressField, portField, joinGame);
        playerChoice.setSpacing(20);
        playerChoice.setAlignment(Pos.CENTER);

        Button roll = new Button();
        roll.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("/sprites/rollButton.png"),180,80,true,true)));
        roll.setUserData("ROLL");
        roll.setId("rollButton");
        gameOptions.getChildren().add(roll);

    }

    private void checkJoinFields() {
        String IPV4_PATTERN = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
        boolean addressProper = true;
        if(!addressField.getText().matches(IPV4_PATTERN)){
            addressProper = false;
            if(!addressField.getStyleClass().contains("error"))addressField.getStyleClass().add("error");
        }else{
            if(addressField.getStyleClass().contains("error"))addressField.getStyleClass().remove("error");
        }

        if(portField.getText().length()==0 || addressField.getText().length()==0 || !addressProper) joinGame.disableProperty().setValue(true);
        else joinGame.disableProperty().setValue(false);
    }

    private void checkHostFields() {
        if(playerField.getText().length()==0 || energyField.getText().length()==0) hostGame.disableProperty().setValue(true);
        else hostGame.disableProperty().setValue(false);
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
