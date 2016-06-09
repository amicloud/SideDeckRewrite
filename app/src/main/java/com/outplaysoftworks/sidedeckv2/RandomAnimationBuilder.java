package com.outplaysoftworks.sidedeckv2;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Billy on 6/8/2016.
 */
public class RandomAnimationBuilder {

    private Integer frameCount;
    private Integer duration;
    private ArrayList<Drawable> drawables;
    private Integer frameDuration;
    private Random random = new Random(System.nanoTime());

    public RandomAnimationBuilder(ArrayList<Drawable> drawables, Integer duration, Integer frameCount){
        this.drawables = drawables;
        this.duration = duration;
        this.frameCount = frameCount;
        this.frameDuration = duration/frameCount;
    }

    public AnimationDrawable makeAnimation(){
        AnimationDrawable animationDrawable = new AnimationDrawable();
        int max = drawables.size();
        for(int i = 0; i < frameCount; i++) {
            int rand = random.nextInt(max);
            animationDrawable.addFrame(drawables.get(rand), frameDuration);
        }
        return animationDrawable;
    }
}
