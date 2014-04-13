package au.com.jlsystems.datawarehouse;

import android.content.Context;
//import android.util.Log;
import android.widget.TextView;

/**
 * Created by Justin Levy on 24/03/14.
 */
public class MyTextView extends TextView {
    private int index1 = 0;
    private int index2 = 0;
    private int changeTextWidthTo = 0;
    private int changeTextHeightTo = 0;
//    private static final int PADDING_LEFT = 3;
//    private static final int PADDING_TOP = 1;
//    private static final int PADDING_RIGHT = 3;
//    private static final int PADDING_BOTTOM = 1;

    private Object reference;

    public MyTextView colHeading;
    public  MyTextView colNext;
    public MyTextView rowHeading;
    public  MyTextView rowNext;
    public MyColumnLayout myColumnLayout;

    public MyTextView(Context context, String text) {
        super(context);
        super.setText(text);
        initMyTextView();
    }

    public int getChangeTextWidthTo() {
        return changeTextWidthTo;
    }

    public int getChangeTextHeightTo() {
        return changeTextHeightTo;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    private void initMyTextView() {
//        setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != oldw){
            if(colHeading != null){
                if(colHeading.changeTextWidthTo < w){
                    colHeading.changeTextWidthTo = w;
                }
            }
        }
        if(h != oldh){
            if(rowHeading != null){
                if(rowHeading.changeTextHeightTo < h){
                    rowHeading.changeTextHeightTo = h;
                }
            }
        }
//        Log.w(this.getClass().getName(), String.format("onSizeChanged [%d,%d](%d,%d)->(%d,%d)", index1, index2, oldw, oldh, w, h));
    }

}
