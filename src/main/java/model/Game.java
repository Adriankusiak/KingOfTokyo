package model;


import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.StageStyle;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Observable{
    private ArrayList<Player> players;
    private ScriptEngine engine;
    private Deck deck;
    private int currentIndex;
    private Player currentPlayer;
    private int localPlayerIndex;
    private boolean hosting;
    private int pointsToWin;
    private Player winner;
    private boolean won;
    private ActionEngine actionEngine;
    private Communicator communicator;
    private int phase;
    private int playerCount;
    private ArrayList<Die> dice;
    private boolean running;
    private ArrayList<PowerCard> cardsInPlay;
    private int startingEnergy;
    private int startingLife;
    private int maxLife;
    private boolean initialized;

    public Game(){
        actionEngine = ActionEngine.getInstance();
        cardsInPlay = new ArrayList<>();
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new InputStreamReader(
                    this.getClass().getResourceAsStream("/scripts/CardLoader.js")
            ));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        deck = new Deck(engine);
        dice = new ArrayList<>();
        for(int i = 0; i < 8; ++i){
            dice.add(new Die(3));
        }
        running = true;
        phase = -2;
    }

    public void joinGame(String ip, int port, String playerName){
        hosting = false;
        initialized = false;
        try {
            communicator = new Communicator(this, true, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        actionEngine.resolve(this);

        String playerCharacter = showCharacterChoice();

        GameAction joinAction = new GameAction("JOIN",null, playerName+":"+playerCharacter);
        inform(joinAction);
        actionEngine.push(joinAction);

    }


    public void initialiseJoin(ArrayList<Integer> deciData, String stringData) {
        newGame(deciData.get(0), deciData.get(1), deciData.get(2), deciData.get(3),
                deciData.get(4), deciData.get(5), deciData.get(6));

        String[] splitData = stringData.split(":");

        for(String s : splitData){
            String[] innerSplit = s.split(",");
            System.out.println("Player "+ innerSplit[0] + " is playing as "+innerSplit[1]+"!");
            addPlayer(innerSplit[0], innerSplit[1]);
        }
        initialized = true;
    }
    public void hostGame(String playerName, int players, int maxPoints, int startingEnergy, int startingLife, int maxLife){
        hosting = true;
        initialized = true;
        actionEngine.resolve(this);
        try {
            communicator = new Communicator(this, false, "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        newGame(maxPoints, players, 0, ThreadLocalRandom.current().nextInt(0, players),
                startingEnergy, startingLife, maxLife);
        addPlayer(playerName, showCharacterChoice());
    }

    public void newGame(int maxPoints, int playerCount, int localPlayer, int startingPlayer, int startingEnergy, int startingLife, int maxLife){
        this.startingEnergy = startingEnergy;
        this.startingLife = startingLife;
        this.maxLife = maxLife;
        this.players = new ArrayList<>();
        this.playerCount = playerCount;
        this.pointsToWin = maxPoints;
        this.currentIndex = startingPlayer;
        this.localPlayerIndex = localPlayer;
        this.won = false;
    }

    public void addPlayer(String name, String character){
        players.add(new Player(name,  character, startingEnergy, startingLife, maxLife));
        this.setChanged();
        this.notifyObservers();
    }

    public void trySelect(int i){
        if(!playerTurn()) return;
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        GameAction action = new GameAction("SELECT_DICE", data, null);
        actionEngine.push(action);
        inform(action);
    }


    public void tryUnselect(int i){
        if(!playerTurn()) return;
        ArrayList<Integer> data = new ArrayList<>();
        data.add(i);
        GameAction action = new GameAction("UNSELECT_DICE", data, null);
        actionEngine.push(action);
        inform(action);

    }

    public void selectDice(int i){
        dice.get(i).setSelected(true);
        this.setChanged();
        this.notifyObservers();
    }

    public void unselectDice(int i){
        dice.get(i).setSelected(false);
        this.setChanged();
        this.notifyObservers();
    }

    public ArrayList<Integer> rollSelected(){

        ArrayList<Integer> vals = new ArrayList<>();
        if(!playerTurn() || phase != 0) return vals;
        boolean selectedRollable = false;
        for(int i = 0; i< currentPlayer.getDiceCount(); ++i){
            Die d = dice.get(i);
            if(d.isSelected() && d.isRollable()){
                selectedRollable = true;
            }
            vals.add(d.getValue());
        }
        if(!selectedRollable) return vals;
        vals = new ArrayList<>();
        for(int i = 0; i < currentPlayer.getDiceCount(); ++i){
            Die d = dice.get(i);
            if(d.isSelected() && d.isRollable()) vals.add(d.roll());
            else{
                vals.add(d.getValue());
                d.subRoll();
            }
            d.setSelected(false);

        }
        if(hosting) communicator.informAllRoll(vals);
        else communicator.informRoll(vals);

        this.setChanged();
        this.notifyObservers();
        return vals;
    }

    public void setDice(ArrayList<Integer> values){
        for(int i = 0; i < values.size(); ++i){
            Die d = dice.get(i);
            d.setValue(values.get(i));
            d.setSelected(false);

        }
        this.setChanged();
        this.notifyObservers();
    }

    public void accept(){
        GameAction action = new GameAction("RESOLVE", null, null);
        actionEngine.push(action);
        inform(action);
    }

    public void resolve(){
        Integer[] vals = new Integer[6];

        for(int i = 0; i < currentPlayer.getDiceCount(); ++i){
            Die d = dice.get(i);
            switch(d.getValue()){
                case 1:
                    ++vals[0];
                    break;
                case 2:
                    ++vals[1];
                    break;
                case 3:
                    ++vals[2];
                    break;
                case 4:
                    ++vals[3];
                    break;
                case 5:
                    ++vals[4];
                    break;
                case 6:
                    ++vals[5];
                    break;
                default:
                    break;
            }

            phase = 1;
        }


    }

    public void tryEndTurn(){
        GameAction action = new GameAction("END_TURN", null, null);
        actionEngine.push(action);
        inform(action);
    }

    public void endTurn(){
        phase = -1;
        currentIndex = currentIndex +1 % playerCount;
        if(hosting) communicator.informAllEndTurn();
        else communicator.informEndTurn();
    }

    public boolean playerTurn(){
        return (localPlayerIndex==currentIndex);
    }


    private boolean checkWin() {
        for(Player p : players){
            if(p.getVictoryPoints() == pointsToWin){
                winner = p;
                won = true;
                return won;
            }
        }
        return won;
    }

    public void inform(GameAction action){
        if(hosting) communicator.informAll(action);
        else communicator.inform(action);
    }

    public boolean isSelected(int dieNum){
        return dice.get(dieNum).isSelected();
    }

    public boolean isRunning() {
        return running;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Die> getDice() {
        return dice;
    }

    public ArrayList<PowerCard> getCards() {
        return cardsInPlay;
    }

    public void endRunning() {
        running = false;
        if(communicator!= null) communicator.endRunning();
    }

    public ArrayList<Integer> getDiceValues() {
        ArrayList<Integer> values = new ArrayList<>();
        for(Die d: dice) values.add(d.getValue());
        return values;
    }

    public ArrayList<Boolean> getSelected() {
        ArrayList<Boolean> values = new ArrayList<>();
        for(Die d: dice) values.add(d.isSelected());
        return values;
    }


    public GameAction newPlayerAction() {
        ArrayList<Integer> gameData = new ArrayList<>();
        gameData.add(pointsToWin);
        gameData.add(playerCount);
        gameData.add(players.size());
        gameData.add(currentIndex);
        gameData.add(startingEnergy);
        gameData.add(startingLife);
        gameData.add(maxLife);
        String playerData = "";
        for(Player p : players){
            playerData += p.getName()+","+p.getCharacter() + ":";
        }
        return new GameAction("INITIALIZE", gameData, playerData);
    }

    public void checkStart() {
        if(players.size() == playerCount && phase == -2) {
            phase = 0;
            currentPlayer = players.get(currentIndex);
            System.out.println("Started");
        }
    }

    public int playerCount() {
        return playerCount;
    }

    private String showCharacterChoice(){
        while(!initialized){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(players.size());
        FlowPane characterPane = new FlowPane();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Choose an option");
        alert.setHeaderText("Character Choice");
        DialogPane alertPane = alert.getDialogPane();
        alertPane.setContent(characterPane);
        alertPane.setExpanded(false);

        //To make enter key press the actual focused button, not the first one. Just like pressing "space".
        alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
                try {
                    Robot r = new Robot();
                    r.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                    r.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ToggleGroup g = new ToggleGroup();

        String[] options = new String[]{"oneDice.png","twoDice.png", "threeDice.png"};
        ArrayList<String> taken = new ArrayList<>();
        for(Player p : players) {
            taken.add(p.getCharacter());
            System.out.println(p.getCharacter());
        }
        for(String opt : options){
            ToggleButton tb = new ToggleButton();
            tb.setUserData(opt);
            tb.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream("/sprites/"+opt), 100, 100, true, true)));
            if(taken.contains(opt)) tb.setDisable(true);
            g.getToggles().add(tb);
            characterPane.getChildren().add(tb);
        }

        for(Toggle t : g.getToggles()){
            if(((ToggleButton)t).isDisabled()) continue;
            else{
                t.setSelected(true);
                break;
            }
        }
        List<ButtonType> buttons = new ArrayList<>();

        buttons.add(new ButtonType("Choose"));


        alert.getButtonTypes().setAll(buttons);

        Optional<ButtonType> result = alert.showAndWait();

        if (!result.isPresent()) {
            return "attackDice.png";
        } else {
            return g.getSelectedToggle().getUserData().toString();
        }
    }
}
