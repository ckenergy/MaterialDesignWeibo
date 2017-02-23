package com.ckenergy.transitionsview.transitions;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.ViewGroup;

public class MoveInfo {
    public int statusBarHeight;
    private String imgUrl;
    private int imgId;
    public Bitmap bitmap;

    public int translationY;
    public int translationX;
    //origin view
    public Point originPoint = new Point();
    public int originWidth;
    public int originHeight;
    //target view
    public Point targetPoint = new Point();
    public int targetWidth;
    public int targetHeight;

    public ViewGroup moveLayout;
    public float scale;

}
