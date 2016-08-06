package com.outplaysoftworks.sidedeck;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Random;

/** This class contains methods to create animations consisting of a randomized sequence of
 *  predefined drawables
 * Created by Billy on 6/8/2016.
 */
@SuppressWarnings("ALL")
public class RandomAnimationBuilder {

    private Integer frameCount;
    private Integer duration;
    private ArrayList<Drawable> drawables;
    private Integer frameDuration;
    private Random random = new Random(System.nanoTime());

    /**
     * Used to create an animation from a series of drawables with the frames in
     * a completely random order with uniform duration
     * @param drawables Arraylist of drawables to use for individual frames
     * @param duration Total animation duration
     * @param frameCount Total frames for animation
     */
    public RandomAnimationBuilder(ArrayList<Drawable> drawables, Integer duration, Integer frameCount){
        this.drawables = drawables;
        this.duration = duration;
        this.frameCount = frameCount;
        this.frameDuration = duration/frameCount;
    }

    public Integer getFrameDuration(){
        return frameDuration;
    }

    /**
     * Contructs an Animation Drawable using resources in the RandomAnimationBuilder object
     * @return Animation Drawable ready to use
     */
    public AnimationDrawable makeAnimation(){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        int max = drawables.size();
        for(int i = 0; i < frameCount; i++) {
            int rand = random.nextInt(max);
            animationDrawable.addFrame(drawables.get(rand), frameDuration);
        }
        return animationDrawable;
    }

    /**
     * Constructs an animation Drawable using resources in the RandomAnimationBuilder object
     * @param allowIdenticalConsecutiveFrames If true, allows consecutive frames to use the same image
     * @return Animation drawable ready to use
     */
    public AnimationDrawable makeAnimation(@SuppressWarnings("SameParameterValue") boolean allowIdenticalConsecutiveFrames) {
        AnimationDrawable animationDrawable = new AnimationDrawable();
        int max = drawables.size();
        int lastRandomNumber;
        int rand = 0;
        int i = 0;
        while (i < frameCount) {
            lastRandomNumber = rand;
            rand = random.nextInt(max);
            if (allowIdenticalConsecutiveFrames || lastRandomNumber != rand) {
                animationDrawable.addFrame(drawables.get(rand), frameDuration);
                i++;
            }
        }
        return animationDrawable;
    }
}
