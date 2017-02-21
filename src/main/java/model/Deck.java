package model;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;

public class Deck {
    private ArrayList<PowerCard> cards;
    private ScriptEngine engineHandle;
    public Deck(ScriptEngine engine){
        cards = new ArrayList<PowerCard>();
        engineHandle = engine;

        loadCards();
    }


    private void loadCards(){
        BufferedReader br = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/cards.json")
        ));
        String cards = "";
        String line;
        try {
            while((line = br.readLine()) != null){
                cards += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Invocable invocable = (Invocable) engineHandle;
        try {
            invocable.invokeFunction("loadCards", cards, this.cards);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        for(PowerCard p: this.cards){
            System.out.println("Title: "+ p.getName() + " Cost: " + p.getCost() );
        }
    }
}
