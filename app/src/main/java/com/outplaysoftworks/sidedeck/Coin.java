package com.outplaysoftworks.sidedeck;

import android.content.Context;


public class Coin {
    private Face face;
    protected Context context;
    public Coin(Double number, Context context){
        this.context = context;
        if(number >0.5d){
            face = new Face(FACES.HEADS, context);
        }else if(number <= 0.5d){
            face = new Face(FACES.TAILS, context);
        }
    }
    public Face getFace(){
        return this.face;
    }
}

class Face{
    public String getFaceString() {
        return faceString;
    }

    /*public FACES getFace() {
        return face;
    }*/

    private String faceString;
    /*FACES face;*/

    @Override
    public String toString(){
        return this.getFaceString();
    }
    public Face(FACES face, Context context){
        if(face == FACES.TAILS){
            faceString = context.getResources().getString(R.string.coinsTails);
            /*face = FACES.TAILS;*/
        } else if(face == FACES.HEADS){
            faceString = context.getResources().getString(R.string.coinHeads);
            /*face = FACES.HEADS;*/
        }
    }
}

enum FACES{
    HEADS, TAILS
}
