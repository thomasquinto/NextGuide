package com.nextguide.nextguide;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by thomas on 5/2/15.
 */
public class WebManager {

    public static final String NG_API_HOST = "http://api-guide.nextguide.tv/";

    private static WebManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private WebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized WebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    // API Methods:

    public String getRequestChannelsUrl(String headendId) {
        return String.format("%sapi/get_channels.json?headend_id=%s&flags=h,s", NG_API_HOST, headendId);
    }

    public String getRequestGridUrl(String headendId) {
        return String.format("%stmsapi/v1/lineups/%s/grid?startDateTime=2015-05-06T00:00-04:00&endDateTime=2015-05-06T00:59-04:00&stationId=91035,10750,10575,10685,10443,10643,10593,10409,11530,11069,14909,16604,14948,66110,56032,82682,61313,65694,72305,58982,70478,62955,63137,59534,72611,62894,57806,75761,46172,63649,68502,60308,49433,72276,12788,12789,12790,12791,12793,12795,12796,12797,12798,12799,22422,31548,31303,12800,31549,31550,22423,31551,22424,12802,44307,22425,22426,31552,47376,22427,44308,22428,31553,22429,47218,44696,22430,47220,43504,22431,43505,12803,22432,22433,59762,43507,22434,44309,31554,44310,43508,22435,44311,43509,44312,47379,22436,47222,44313,22437,82531,22438,22439,44314,22760,32261,59737,44315,44316&size=Basic&imageSize=Lg&imageAspectTV=4x3&imageText=true&excludeChannels=adult&enhancedCallSign=true&api_key=5pajcaw85sbyyv4xws88kekc", NG_API_HOST, headendId);
    }

    public String requestGrid() {
        //TODO
        return null;
    }

}
