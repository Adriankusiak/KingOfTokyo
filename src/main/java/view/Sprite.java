package view;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * Utility Image wrapper for loading sprite from a spritesheet and handle updating/drawing
 */
public class Sprite {
    protected ArrayList<Double> arrayOfIndexes;
    protected ArrayList<Image> spriteFrames;
    protected int frameCount;
    protected double[] pos;
    protected int[] animOffsets;
    protected int currentAnim;
    protected int currentIndex;
    protected int height, width;
    private String basePath = "/sprites/";
    private static HashMap<String, ArrayList<Image>> loadedSheets = new HashMap<>();
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
        currentAnim = 0;
        animOffsets = new int[]{0};
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
        currentAnim = 0;
        animOffsets = new int[]{0};
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
        g.drawImage(spriteFrames.get(currentIndex),0.0,
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
        ++currentIndex;
        if(animOffsets.length-1==currentAnim){
            if(currentIndex == frameCount) currentIndex = animOffsets[currentAnim];
        }else{
            if(currentIndex == animOffsets[currentAnim+1]){
                currentIndex = animOffsets[currentAnim];
            }
        }
    }


    private void loadSheet(String path, int frameCount){

        if(loadedSheets.containsKey(path)){
            spriteFrames = loadedSheets.get(path);
        }else{
            spriteFrames = new ArrayList<>();
            String spritePath = basePath+path;

            Image base = new Image(this.getClass().getResourceAsStream(spritePath));
            PixelReader reader = base.getPixelReader();


            for(int i = 0; i < frameCount; i++){
                WritableImage frame = new WritableImage(reader, (i*width),0, width, height);
                spriteFrames.add(frame);
            }
            loadedSheets.put(path, spriteFrames);

        }

    }



}
