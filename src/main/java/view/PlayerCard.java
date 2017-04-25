package view;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PlayerCard extends Sprite{

    private boolean initialized;
    private int health;
    private int energy;
    private Canvas transfCanvas;
    private GraphicsContext transContext;
    private double[][] transforms;
    private static double[][] originals = new double[][]{{0,0},
        {180.0,0.0},
        {180.0,250.0},
        {0.0,250}};

    private double transSpeed;
    GameBoard boardHandle;
    private double[][] transformLeftTop;
    private double[][] transformLeftBottom;
    private double[][] transformRightBottom;
    private double[][] transformRightTop;
    private double currentIndexFractional;

    public PlayerCard(GameBoard board) {
        super("playerCard.png", 180, 250, 96, 180, 250);
        boardHandle = board;
        initialized = false;
        transfCanvas = new Canvas(180,250);
        transContext = transfCanvas.getGraphicsContext2D();
        setupTransforms();
        transSpeed = 30;
        currentAnim = 0;
        animOffsets = new int[]{0,50};
    }

    private void setupTransforms() {
        transforms = new double[][]{
                {0,0},
                {180.0,0.0},
                {180.0,250.0},
                {0.0,250}};
        transformLeftTop = new double[][]{
                {5,5},
                {180.0,0.0},
                {180.0,250.0},
                {0.0,250.0}};

        transformRightTop = new double[][]{
                {0,0},
                {175.0,5.0},
                {180.0,250.0},
                {0.0,250}};

        transformLeftBottom = new double[][]{
                {0,0},
                {180.0,0.0},
                {180.0,250.0},
                {5.0,245}};
        transformRightBottom = new double[][]{
                {0,0},
                {180.0,0.0},
                {175.0,245.0},
                {0.0,250}};
    }

    @Override
    public void draw(GraphicsContext g){
        transfCanvas = new Canvas(180,250);
        transContext = transfCanvas.getGraphicsContext2D();
        PerspectiveTransform perspectiveTrasform = new PerspectiveTransform();
        perspectiveTrasform.setUlx(transforms[0][0]);
        perspectiveTrasform.setUly(transforms[0][1]);
        perspectiveTrasform.setUrx(transforms[1][0]);
        perspectiveTrasform.setUry(transforms[1][1]);
        perspectiveTrasform.setLrx(transforms[2][0]);
        perspectiveTrasform.setLry(transforms[2][1]);
        perspectiveTrasform.setLlx(transforms[3][0]);
        perspectiveTrasform.setLly(transforms[3][1]);
        transfCanvas.setEffect(perspectiveTrasform);
        transfCanvas.setCache(false);
        transContext.drawImage(spriteFrames.get(currentIndex), 0.0,
                0.0, width, height, 0, 0, spriteWidth, spriteHeight);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        g.drawImage(transfCanvas.snapshot(params, null), pos[0], pos[1]);
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

    @Override
    public void update(double delta){
        int FPS = 20;
        double deltaFactor = delta/1000000000;
        double[] mousePos = boardHandle.getMousePos();
        if(mousePos[0] < pos[0] || mousePos[0] > pos[0] + spriteWidth || mousePos[1] < pos[1]){
            applyTransform(deltaFactor, originals);
        }
        else calculatePerspectives(mousePos, deltaFactor);
        currentIndexFractional += delta/(1000000000/FPS);
        if(currentIndexFractional >= currentIndex+1) {
            nextFrame();
            if(currentIndex == animOffsets[currentAnim]) currentIndexFractional = animOffsets[currentAnim];
            if(currentAnim == 0 && currentIndex == animOffsets[currentAnim+1]-1){
                currentAnim =1;
            }
        }
    }

    private void calculatePerspectives(double[] mousePos, double deltaFactor) {
        if(mousePos[0]< pos[0]+spriteWidth/2){
            if(mousePos[1] < pos[1]+spriteHeight/2) applyTransform(deltaFactor, transformLeftTop);
            else applyTransform(deltaFactor, transformLeftBottom);
        }else{
            if(mousePos[1] < pos[1]+spriteHeight/2) applyTransform(deltaFactor, transformRightTop);
            else applyTransform(deltaFactor, transformRightBottom);
        }

    }

    private void applyTransform(double deltaFactor, double[][] transformMatrix) {
        for(int i = 0; i < transforms.length; ++i){
            for(int j = 0; j < transforms[i].length; ++j){
                if(transforms[i][j] > transformMatrix[i][j]){
                    transforms[i][j] -= deltaFactor*transSpeed;
                    if(transforms[i][j] < transformMatrix[i][j]) transforms[i][j] = transformMatrix[i][j];
                }else if(transforms[i][j] < transformMatrix[i][j]){
                    transforms[i][j] += deltaFactor*transSpeed;
                    if(transforms[i][j] > transformMatrix[i][j]) transforms[i][j] = transformMatrix[i][j];
                }
            }
        }
    }

}
