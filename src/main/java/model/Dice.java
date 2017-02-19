package model;

import java.util.ArrayList;
import java.util.Random;

public class Dice {

    private static Random generator = new Random();

    public ArrayList<Integer> getRolls(int rollCount){
        ArrayList<Integer> toReturn = new ArrayList<>();

        for(int i = 0; i < rollCount; ++i){
            toReturn.add(generator.nextInt(6)+1);
        }

        return toReturn;
    }
}
