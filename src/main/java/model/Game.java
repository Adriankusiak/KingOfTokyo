package model;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
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

    public Game(){
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
        Runtime.getRuntime().addShutdownHook(new Thread(()->running = false));
    }

    public void joinGame(String ip){
        hosting = false;
        try {
            communicator = new Communicator(true, ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void hostGame(int players){
        hosting = true;
        try {
            communicator = new Communicator(false, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        newGame(4, 3, 0, 0,
               new ArrayList<String>(Arrays.asList("Adrian", "Córdoba", "La Plata")),
               new ArrayList<String>(Arrays.asList("Grizzly", "Cthullu", "blash")),
                0, 10, 12);
    }

    public void newGame(int maxPoints, int playerCount, int localPlayer, int startingPlayer, ArrayList<String> names, ArrayList<String> characters, int startingEnergy, int startingLife, int maxLife){
        players = new ArrayList<>();
        for(int i = 0; i < playerCount; ++i){
            players.add(new Player(names.get(i),  characters.get(i), startingEnergy, startingLife, maxLife));
        }
        this.playerCount = playerCount;
        pointsToWin = maxPoints;
        currentIndex = startingPlayer;
        currentPlayer = players.get(startingPlayer);
        localPlayerIndex = localPlayer;
        won = false;

    }

    public void selectDice(int i){
        dice.get(i).setSelected(true);
        if(hosting) communicator.informAllSelect(i);
        else communicator.informSelect(i);
    }

    public void unselectDice(int i){
        dice.get(i).setSelected(false);
        if(hosting) communicator.informAllUnselect(i);
        else communicator.informUnselect(i);
    }

    public ArrayList<Integer> rollSelected(){
        ArrayList<Integer> vals = new ArrayList<>();
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

        return vals;
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

            if(hosting) communicator.informAllResolve(vals);
            else communicator.informResolve(vals);
            phase = 1;
        }


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

    public boolean isSelected(int dieNum){
        return dice.get(dieNum).isSelected();
    }

    public boolean isRunning() {
        return running;
    }
}
