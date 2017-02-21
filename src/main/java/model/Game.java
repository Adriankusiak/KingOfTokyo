package model;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    }

    public void joinGame(String ip){
        hosting = false;
    }


    public void hostGame(int players){
        hosting = true;
    }

    public void newGame(int maxPoints, int playerCount, int localPlayer, int startingPlayer, ArrayList<String> names, ArrayList<String> characters, int startingEnergy, int startingLife, int maxLife){
        for(int i = 0; i < playerCount; ++i){
            players.add(new Player(names.get(i),  characters.get(i), startingEnergy, startingLife, maxLife));
        }
        pointsToWin = maxPoints;
        currentIndex = startingPlayer;
        currentPlayer = players.get(startingPlayer);
        localPlayerIndex = localPlayer;
        won = false;
        new Thread(()->{
            gameLoop();
        }).start();
    }

    public void endTurn(){

    }

    public boolean playerTurn(){
        return (localPlayerIndex==currentIndex);
    }

    private void gameLoop(){
        while(!won){
            waitForAction();
            checkWin();
        }
    }

    private void waitForAction() {
        actionEngine.stack(communicator.getActions());
        actionEngine.resolve();
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
}
