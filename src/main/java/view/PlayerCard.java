package view;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PlayerCard extends Sprite{

    private double shimmerSpeed;
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
    private double shimmerTime;
    private double shimmerInterval;
    private boolean shimmerOn;
    private double shimmerPercentage;
    private boolean isSecondCheck;
    private double shimmerAngle;
    private boolean shimmered;


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
        shimmerOn = false;
        shimmerPercentage = 0;
        shimmerSpeed = 230;
        isSecondCheck = false;
        shimmerAngle = 44;
        shimmered = false;
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
        if(shimmerOn){
            double x = 175*shimmerPercentage/100;
            double y = 220*shimmerPercentage/100;
            double distance = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
            if(y>108) distance = Math.sqrt(Math.pow(175-x,2) + Math.pow(220-y,2));
            double xDelta = distance*Math.sin(Math.toRadians(shimmerAngle));
            double yDelta = distance*Math.cos(Math.toRadians(shimmerAngle));
            double endX = x + xDelta;
            double endY = y - yDelta;
            double startX = x - xDelta;
            double startY = y + yDelta;
           // transContext.fillRect(startX,startY, 10, 10);
            //transContext.fillRect(x,y, 5, 5);
            //transContext.fillRect(endX,endY, 10, 10);
            transContext.setGlobalBlendMode(BlendMode.SRC_ATOP);
            transContext.setStroke(Color.WHITE);
            transContext.setGlobalAlpha(0.9);
            BoxBlur blur = new BoxBlur();
            blur.setWidth(10);
            blur.setHeight(10);
            blur.setIterations(1);
            transContext.setEffect(blur);
            //transContext.setLineCap(StrokeLineCap.ROUND);
            //transContext.setLineJoin(StrokeLineJoin.ROUND);
            transContext.setLineWidth(8);
            transContext.strokeLine(startX,startY,endX,endY);
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = transfCanvas.snapshot(params, null);
        //if(shimmerOn) drawShimmer(snapshot);
        g.drawImage(snapshot, pos[0], pos[1]);
        if(initialized){
            g.setFill(Color.LIGHTGREEN);
            g.fillText(health+"", pos[0]+spriteWidth*0.36,pos[1]+spriteHeight*0.97);
            g.fillText(energy+"", pos[0]+spriteWidth*0.65,pos[1]+spriteHeight*0.97);
        }
    }

    private void drawShimmer(WritableImage snapshot) {
        //PixelReader
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
        if(shimmerOn){
            shimmerPercentage =  (shimmerPercentage + deltaFactor*shimmerSpeed);
            if(shimmerPercentage > 100){
                if(isSecondCheck){
                    shimmerPercentage = 0;
                    shimmerOn = false;
                    isSecondCheck = false;
                }else {
                    shimmerPercentage = 100;
                    isSecondCheck = true;
                }
            }
        }
        if(mousePos[0] < pos[0] || mousePos[0] > pos[0] + spriteWidth || mousePos[1] < pos[1]){
            applyTransform(deltaFactor, originals);
            shimmered = false;
        }
        else{
            calculatePerspectives(mousePos, deltaFactor);

            if(!shimmerOn && !shimmered){
                shimmerOn = true;
                shimmered = true;
            }

        }
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
