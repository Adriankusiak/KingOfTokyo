package model;

import java.io.Serializable;
import java.util.ArrayList;

public class GameAction implements Serializable{
    private String type;
    private ArrayList<Integer> deciData;
    private String stringData;

    public GameAction(String type, ArrayList<Integer> deciData, String stringData){
        this.type = type;
        this.deciData = deciData;
        this.stringData = stringData;
    }

    public String getType(){
        return type;
    }

    public ArrayList<Integer> getDeciData() {
        return deciData;
    }

    public String getStringData() {
        return stringData;
    }
}
