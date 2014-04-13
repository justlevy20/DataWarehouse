package au.com.jlsystems.datawarehouse;

import android.content.Context;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Justin Levy on 30/03/14.
 */
public class LinkedScrollView extends ScrollView {
        public boolean cascadeScroll = true;
        public ArrayList<LinkedScrollView> others = new
                ArrayList<LinkedScrollView>();

        public LinkedScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);

            if (cascadeScroll) {
                for (LinkedScrollView other : others) {
                    other.cascadeScroll = false;
                    other.scrollTo(l, t);
                    other.cascadeScroll = true;
                }
            }
        }

}
