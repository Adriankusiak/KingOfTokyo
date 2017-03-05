package model;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;

public class PowerCard {
    private String name;
    private String cost;
    private String description;
    private String type;
    private Player owner;
    private ScriptObjectMirror scriptCard;

    public PowerCard(String name, String cost, String description, String type, ScriptObjectMirror card){
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.type = type;
        scriptCard = card;
    }

    public String getName() {
        return name;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getCost() {
        return cost;
    }


    public String getDescription() {
        return description;
    }

    public void onDamage(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onDamage", players, dice, action, engine);
    }
    public void onAttack(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onAttack", players, dice, action, engine);
    }
    public void onRoll(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onRoll", players, dice, action, engine);
    }
    public void onHealthGain(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onHealthGain", players, dice, action, engine);
    }
    public void onPointGain(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onPointGain", players, dice, action, engine);
    }
    public void onEnergyGain(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onEnergyGain", players, dice, action, engine);
    }
    public void onMove(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onMove", players, dice, action, engine);
    }
    public void onTurnStart(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onDamage");
    }
    public void onTurnEnd(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onDamage");
    }

    public void onBuy(ArrayList<Player> players, ArrayList<Die> dice, GameAction action, ActionEngine engine){
        scriptCard.callMember("onBuy");
    }

}
