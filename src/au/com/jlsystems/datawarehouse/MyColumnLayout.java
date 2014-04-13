package au.com.jlsystems.datawarehouse;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Justin Levy on 30/03/14.
 */
public class MyColumnLayout extends LinearLayout {
    public MyTextView columnHeading;

    public MyColumnLayout(Context context) {
        super(context);
        init();
    }

    public MyColumnLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyColumnLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
    }

//    @Override
//    public void onSizeChanged(int w, int h, int oldw, int oldh){
//        super.onSizeChanged(w, h, oldw, oldh);
///*        if(columnHeading != null){
//            if(columnHeading.getMeasuredWidth() < w){
//                columnHeading.getLayoutParams().width = w;
//            }
//        }*/
//    }
}
