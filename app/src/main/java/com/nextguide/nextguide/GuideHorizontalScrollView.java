package com.nextguide.nextguide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by thomas on 5/3/15.
 */
public class GuideHorizontalScrollView extends HorizontalScrollView
        implements GuideFragment.ScrollXListener {

    private boolean mDisableXListener = false;

    public GuideHorizontalScrollView(Context context) {
        super(context);
    }

    public GuideHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onScrollChanged(int l, int t, int old_l, int old_t) {

        if(mDisableXListener) {
            Log.d(getClass().getSimpleName(), "Ignoring X Scroll change");
            return;
        }

        //Log.d(getClass().getSimpleName(), "Scroll X: " + getScrollX());

        for(GuideFragment.ScrollXListener listener: mListeners) {
            listener.scrollXChanged(getScrollX());
        }
    }

    // ScrollXListener

    public void scrollXChanged(int x) {
        //Log.d(getClass().getSimpleName(), "Changing X Scroll to " + x);

        // Avoid symmetric listener "echo" propagation effect:
        mDisableXListener = true;
        setScrollX(x);
        mDisableXListener = false;
    }

    private Set<GuideFragment.ScrollXListener> mListeners = new HashSet<GuideFragment.ScrollXListener>();

    public void addScrollXListener(GuideFragment.ScrollXListener listener) {
        mListeners.add(listener);
    }

    public void removeScrollXListener(GuideFragment.ScrollXListener listener) {
        mListeners.remove(listener);
    }

}
