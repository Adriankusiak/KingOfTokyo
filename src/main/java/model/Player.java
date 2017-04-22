package model;

public class Player {
    private String name;
    private String character;
    private int energy;
    private int victoryPoints;
    private int life;
    private int maxLife;
    private boolean isAlive;
    private boolean inTokyo;
    private int diceCount;

    public Player(String name, String character, int startingEnergy, int startingLife, int maxLife){
        this.name = name;
        this.character = character;
        this.energy = startingEnergy;
        this.life = startingLife;
        this.maxLife = maxLife;
        this.isAlive = true;
        this.inTokyo = false;
        this.diceCount = 6;
    }

    public void useEnergy(int toUse){
        energy = (toUse > energy) ? 0 : (energy-toUse);
    }

    public void takeLife(int toTake){
         if(toTake > life){
             life = 0;
             isAlive = false;
         }else{
             life -= toTake;
         }
    }

    public void heal(int toHeal){
        life = life+toHeal % maxLife;
    }

    public void increasePoints(int points){
        victoryPoints += points;
    }


    public void decreasePoints(int points){
        victoryPoints -= points;
    }

    public int getEnergy() {
        return energy;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getLife() {
        return life;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getDiceCount() {
        return diceCount;
    }
    public void setDiceCount(int count) {
        diceCount = count;
    }
}
