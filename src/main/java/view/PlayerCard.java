package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PlayerCard extends Sprite{

    private boolean initialized;
    private int health;
    private int energy;

    public PlayerCard() {
        super("playerCard.png", 897, 1218, 1, 180, 250);
        initialized = false;
    }

    @Override
    public void draw(GraphicsContext g){
        super.draw(g);
        if(initialized){
            g.setFill(Color.LIGHTGREEN);
            g.fillText(health+"", pos[0]+spriteWidth*0.36,pos[1]+spriteHeight*0.97);
            g.fillText(energy+"", pos[0]+spriteWidth*0.65,pos[1]+spriteHeight*0.97);
        }
    }

    public void initialize(int playerEnergy, int playerHealth){
        health = playerHealth;
        energy = playerEnergy;
        initialized = true;
    }

    public void setHealth(int newHealth){
        health = newHealth;
    }

    public void setEnergy(int newEnergy){
        energy = newEnergy;
    }


}
