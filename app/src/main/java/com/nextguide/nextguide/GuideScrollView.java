package com.nextguide.nextguide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by thomas on 5/2/15.
 */
public class GuideScrollView extends ScrollView implements GuideFragment.ScrollYListener {

    private boolean mDisableYListener = false;

    public GuideScrollView(Context context) {
        super(context);
    }

    public GuideScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GuideScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onScrollChanged(int l, int t, int old_l, int old_t) {
        if(mDisableYListener) {
            Log.d(getClass().getSimpleName(), "Ignoring Y Scroll change");
            return;
        }

        Log.d(getClass().getSimpleName(), "Scroll Y: " + getScrollY());

        for(GuideFragment.ScrollYListener listener: mListeners) {
            listener.scrollYChanged(getScrollY());
        }
    }

    // ScrollYListener

    public void scrollYChanged(int y) {
        Log.d(getClass().getSimpleName(), "Changing Y Scroll to " + y);

        // Avoid symmetric listener "echo" propagation effect:
        mDisableYListener = true;
        scrollTo(0, y);
        mDisableYListener = false;
    }

    private Set<GuideFragment.ScrollYListener> mListeners = new HashSet<GuideFragment.ScrollYListener>();

    public void addScrollYListener(GuideFragment.ScrollYListener listener) {
        mListeners.add(listener);
    }

    public void removeScrollYListener(GuideFragment.ScrollYListener listener) {
        mListeners.remove(listener);
    }

}
