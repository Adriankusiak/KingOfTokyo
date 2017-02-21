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

    public void newGame(int playerCount, int localPlayer, int startingPlayer, ArrayList<String> names, ArrayList<String> characters, int startingEnergy, int startingLife, int maxLife) {
        for (int i = 0; i < playerCount; ++i) {
            players.add(new Player(names.get(i), characters.get(i), startingEnergy, startingLife, maxLife));
        }
        currentIndex = startingPlayer;
        currentPlayer = players.get(startingPlayer);
        localPlayerIndex = localPlayer;
    }
}
