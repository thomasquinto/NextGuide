package com.nextguide.nextguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 5/3/15.
 */
public class ChannelListAdapter extends BaseAdapter implements ListAdapter {

    public static final String IMAGE_HOST_URL = "http://tmsimg.com/assets/";

    Context mContext;

    public static class ChannelListItem {
        String channelNum;
        String callSign;
        String stationId;
        String imageUrl;
    }

    public static class ChannelListItemView extends FrameLayout {

        View mView;
        ChannelListItem mItem;

        TextView mCallSignView;
        ImageView mImageView;

        public ChannelListItemView(Context context) {
            super(context);
            mView = (ViewGroup) View.inflate(context, R.layout.channel_list_item, this);
        }

        public void setItem(ChannelListItem item) {
            //Log.d(getClass().getSimpleName(), "CHANNEL: " + item.callSign);

            mItem = item;

            TextView channelNumView = (TextView)mView.findViewById(R.id.channel_num);
            channelNumView.setText(item.channelNum);

            mCallSignView = (TextView)mView.findViewById(R.id.channel_callsign);
            mCallSignView.setText(item.callSign);

            mImageView = (ImageView)mView.findViewById(R.id.channel_image);
            loadImage();
        }

        public void loadImage() {

            if(mItem.imageUrl == null || mItem.imageUrl.trim().equals("") || mItem.imageUrl.trim().equals("null")) {
                if(mImageView.getVisibility() == VISIBLE) {
                    mView.post(new Runnable() {
                        public void run() {
                            mImageView.setVisibility(GONE);
                            mCallSignView.setVisibility(VISIBLE);
                        }
                    });
                }
                return;
            }

            ImageLoader imageLoader = WebManager.getInstance(getContext()).getImageLoader();

            String url = mItem.imageUrl;
            if(!url.startsWith("http")) url = IMAGE_HOST_URL + url;

            //Log.d(getClass().getSimpleName(), "Loading image: " + url);
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

                    //Log.d(getClass().getSimpleName(), "Loaded image: " + imageContainer.getRequestUrl());

                    if (imageContainer.getRequestUrl().endsWith(mItem.imageUrl)) {
                        Bitmap bm = imageContainer.getBitmap();
                        mImageView.setImageBitmap(bm);
                        mImageView.setVisibility(VISIBLE);
                        mCallSignView.setVisibility(GONE);
                    } else {
                        //Log.d(getClass().getSimpleName(), "Expecting image: " + mItem.imageUrl + ", but got image: " + imageContainer.getRequestUrl());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(getClass().getSimpleName(), "Volley Error", volleyError);
                }
            });
        }
    }

    List<ChannelListItem> mList = new ArrayList<ChannelListItem>();

    public ChannelListAdapter(Context context, JSONArray channelArray) {
        super();

        mContext = context;
        initChannelList(channelArray);
    }

    private void initChannelList(JSONArray channelArray) {
        mList.clear();

        for(int i=0; i<channelArray.length(); i++) {

            try {
                JSONObject jsonObj = (JSONObject)channelArray.get(i);

                ChannelListItem channelListItem = new ChannelListItem();

                channelListItem.channelNum = "" + jsonObj.getInt("channel_num");
                channelListItem.callSign = jsonObj.getString("callsign");
                channelListItem.stationId = jsonObj.getString("prg_svc_id");
                channelListItem.imageUrl = jsonObj.getString("image_url");

                /*
                Log.d(getClass().getSimpleName(),
                        "Channel List Item " + i + ": " +
                                channelListItem.channelNum +
                                "(" + channelListItem.callSign + ")");
                */

                mList.add(channelListItem);
            } catch(Exception e) {
                Log.e(getClass().getSimpleName(), "Exception:", e);
            }
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = new ChannelListItemView(mContext);
        }

        ((ChannelListItemView) convertView).setItem(mList.get(position));

        return convertView;
    }

    //ListAdapter:
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    //ListAdapter:
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
