package view;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Utility Image wrapper for loading sprite from a spritesheet and handle updating/drawing
 */
public class Sprite {
    protected ArrayList<Double> arrayOfIndexes;
    protected Image sheet;
    protected int frameCount;
    protected double[] pos;
    protected int currentIndex;
    protected double height, width;
    private String basePath = "/sprites/";
    private static HashMap<String, Image> loadedSheets = new HashMap<String, Image>();
    private static HashMap<String, ArrayList<Double>> loadedSheetIndexes = new HashMap<String, ArrayList<Double>>();
    protected double spriteHeight;
    protected double spriteWidth;

    /**
     * Constructs new Sprite object from spritesheet at path, using specified parameters to process it.
     * Default position is (0,0).
     * @param path Path to spritesheet image.
     * @param width Width of single sprite clip.
     * @param height Height of single sprite clip.
     * @param frameCount Number of frames of target sprite in given sheet image.
     */
    public Sprite(String path, int width, int height, int frameCount){
        currentIndex = 0;
        pos = new double[]{0,0};
        this.height = height;
        this.width = width;
        this.frameCount = frameCount;
        loadSheet(path,frameCount);
    }



    /**
     * Constructs new Sprite object from spritesheet at path, using specified parameters to process it.
     * Default position is (0,0).
     * @param path Path to spritesheet image.
     * @param width Width of single sprite clip.
     * @param height Height of single sprite clip.
     * @param frameCount Number of frames of target sprite in given sheet image.
     */
    public Sprite(String path, int width, int height, int frameCount, int spriteWidth, int spriteHeight){
        currentIndex = 0;
        pos = new double[]{0,0};
        this.height = height;
        this.width = width;
        this.frameCount = frameCount;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        loadSheet(path,frameCount);
    }


    /**
     * Retrieves the position coordinates of the Sprite.
     * @return Array of two doubles holding x and y coordinates.
     */
    public double[] getPos(){
        return pos;
    }

    /**
     *  Sets the position of the Sprite to given coordinates.
     * @param x New x coordinate to be set.
     * @param y New y coordinate to be set.
     */
    public void setPos(double x, double y){
        this.pos = new double[]{x,y};
    }

    /**
     * Draws the current Sprite frame at the Sprite's position to given Graphics context.
     * @param g GraphicsContext object to draw onto.
     */
    public void draw(GraphicsContext g){
        g.drawImage(sheet,arrayOfIndexes.get(currentIndex).doubleValue(),
                0.0, width, height, pos[0], pos[1], spriteWidth, spriteHeight);
    }

    /**
     * Updates the Sprite by switching to next frame.
     * @param delta Time difference since last update in nanoseconds. Unused in base method.
     */
    public void update(double delta){
        nextFrame();
    }


    /**
     * Switches to the next clipping in spritesheet image, wrapping around to beginning at end.
     */
    protected void nextFrame(){
        currentIndex = ++currentIndex%frameCount;
    }


    private void loadSheet(String path, int frameCount){

        if(loadedSheets.containsKey(path)){
            sheet = loadedSheets.get(path);
            arrayOfIndexes = loadedSheetIndexes.get(path);
        }else{
            arrayOfIndexes = new ArrayList<Double>();
            String spritePath = basePath+path;

            sheet = new Image(this.getClass().getResourceAsStream(spritePath));


            for(int i = 0; i < frameCount; i++){
                arrayOfIndexes.add((double) (i*width));
            }
            loadedSheets.put(path, sheet);
            loadedSheetIndexes.put(path, arrayOfIndexes);
        }

    }



}
