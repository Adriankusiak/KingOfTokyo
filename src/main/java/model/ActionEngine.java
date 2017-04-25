package model;

import javafx.application.Platform;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ActionEngine {
    private ArrayDeque<GameAction> actionQueue;

    private static ActionEngine instance;

    private ActionEngine(){
        actionQueue = new ArrayDeque<>();
    }

    public static ActionEngine getInstance(){
        if(instance == null){
            instance = new ActionEngine();
        }
        return instance;
    }


    public void resolve(Game game) {
        new Thread(()->{
            while(game.isRunning()){
                while(!actionQueue.isEmpty()){
                    GameAction action = getAction();
                    Platform.runLater(()->resolveAction(game, action));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Event engine closed");
        }).start();
    }

    private void resolveAction(Game game, GameAction action) {
        ArrayList<PowerCard> cards = game.getCards();

        switch (action.getType()){
            case "END_TURN":
                for(PowerCard c : cards) c.onTurnEnd(game.getPlayers(), game.getDice(), action, this);
                if(action.isActive()) game.endTurn();
                break;
            case "START_TURN":
                for(PowerCard c : cards) c.onTurnStart(game.getPlayers(), game.getDice(), action, this);
                break;
            case "SELECT_DICE":
                game.selectDice(action.getDeciData().get(0));
                break;
            case "UNSELECT_DICE":
                game.unselectDice(action.getDeciData().get(0));
                break;
            case "ROLL":
                for(PowerCard c : cards) c.onRoll(game.getPlayers(), game.getDice(), action, this);
                game.setDice(action.getDeciData());
                break;
            case "RESOLVE":
                game.resolve();
                break;
            case "ATTACK":
                for(PowerCard c : cards) c.onAttack(game.getPlayers(), game.getDice(), action, this);
                break;
            case "DAMAGE":
                for(PowerCard c : cards) c.onDamage(game.getPlayers(), game.getDice(), action, this);
                break;
            case "HEAL":
                for(PowerCard c : cards) c.onHealthGain(game.getPlayers(), game.getDice(), action, this);
                break;
            case "ENERGISE":
                for(PowerCard c : cards) c.onEnergyGain(game.getPlayers(), game.getDice(), action, this);
                break;
            case "POINT_GAIN":
                for(PowerCard c : cards) c.onPointGain(game.getPlayers(), game.getDice(), action, this);
                break;
            case "LEAVE":
                for(PowerCard c : cards) c.onMove(game.getPlayers(), game.getDice(), action, this);
                break;
            case "BUY":
                for(PowerCard c : cards) c.onBuy(game.getPlayers(), game.getDice(), action, this);
                break;
            case "JOIN":
                String[] playerInfo = action.getStringData().split(":");
                game.addPlayer(playerInfo[0], playerInfo[1]);
                game.checkStart();
                break;
            default:
                break;
        }
    }
// TODO add all functionality for actions

    public synchronized void pushFront(GameAction retrievedAction) {
        actionQueue.addFirst(retrievedAction);
    }

    public synchronized void push(GameAction retrievedAction) {
        actionQueue.add(retrievedAction);
    }

    public synchronized GameAction getAction() {
        return actionQueue.removeFirst();
    }
}
