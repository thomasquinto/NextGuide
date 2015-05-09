package com.nextguide.nextguide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by thomas on 5/3/15.
 */
public class ChannelListView extends ListView implements GuideFragment.ScrollYListener {

    private boolean mDisableYListener = false;

    public ChannelListView(Context context) {
        super(context);
    }

    public ChannelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int getScroll() {
        View c = getChildAt(0); //this is the first visible row
        if(c == null) return 0;

        int scrollY = -c.getTop();
        for (int i = 0; i < getFirstVisiblePosition(); ++i) {
            scrollY += c.getHeight(); //add all heights of the views that are gone
        }
        return scrollY;
    }

    public void onScrollChanged(int l, int t, int old_l, int old_t) {

        if(mDisableYListener) {
            Log.d(getClass().getSimpleName(), "Ignoring Y Scroll change");
            return;
        }

        int scrollY = getScroll();

        Log.d(getClass().getSimpleName(), "Scroll Y: " + scrollY);

        for(GuideFragment.ScrollYListener listener : mListeners) {
            listener.scrollYChanged(scrollY);
        }
    }

    // ScrollYListener

    public void scrollYChanged(int y) {
        Log.d(getClass().getSimpleName(), "Changing Y Scroll to " + y);

        int index = 0;
        int pos = 0;

        View c = getChildAt(0); //this is the first visible row
        if(c != null) {
            index = y / c.getHeight();
            pos = -(y % c.getHeight());
        }

        Log.d(getClass().getSimpleName(), "Setting new index/position: " + index + "/" + pos);

        // Avoid symmetric listener "echo" propagation effect:
        mDisableYListener = true;
        this.setSelectionFromTop(index, pos);
        mDisableYListener = false;
    }

    private Set<GuideFragment.ScrollYListener> mListeners = new HashSet<GuideFragment.ScrollYListener>();

    public void addScrollYListener(GuideFragment.ScrollYListener listener) {
        mListeners.add(listener);
    }

    public void removeScrollYListener(GuideFragment.ScrollYListener listener) {
        mListeners.remove(listener);
    }

    public int[] getScrollIndexAndPosition() {
        int[] indexAndPos = new int[2];

        // save index and top position
        // see http://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
        indexAndPos[0] = getFirstVisiblePosition();
        View v = getChildAt(0);
        indexAndPos[1] = (v == null) ? 0 : (v.getTop() - getPaddingTop());

        return indexAndPos;
    }
}
