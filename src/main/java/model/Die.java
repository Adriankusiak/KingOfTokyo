package model;

import java.util.Random;

public class Die {

    private static Random generator = new Random();
    private int value;
    private boolean rollable;
    private boolean selected;
    private int rolls;


    public Die(int rolls){
        value = 0;
        rollable=true;
        this.rolls = rolls;
        selected = false;
    }

    public int roll(){
        rolls -= 1;
        value = generator.nextInt(6);
        checkRollable();
        return value;
    }

    private void checkRollable() {
        rollable = rolls>0;
    }

    public void addRoll(){
        rolls+=1;
        checkRollable();
    }

    public void subRoll(){
        rolls-=1;
        checkRollable();
    }

    public void setRolls(int val){
        rolls = val;
        checkRollable();
    }

    public boolean isRollable() {return rollable;}
    public int getValue(){return value;}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public void setValue(Integer value) {
        this.value = value;
    }
}
