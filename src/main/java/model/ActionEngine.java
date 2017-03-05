package model;

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


    public void resolve(Game game, ArrayList<PowerCard> cards) {
        new Thread(()->{
            while(game.isRunning()){
                while(!actionQueue.isEmpty()){
                    resolveAction(game, cards, getAction());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void resolveAction(Game game, ArrayList<PowerCard> cards, GameAction action) {
        switch (action.getType()){

        }
    }

    public void stack(ArrayList<GameAction> actions) {
    }

    public synchronized void push(GameAction retrievedAction) {
        actionQueue.add(retrievedAction);
    }

    public synchronized GameAction getAction() {
        return actionQueue.getFirst();
    }
}
