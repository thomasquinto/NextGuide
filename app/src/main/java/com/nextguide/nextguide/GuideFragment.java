package com.nextguide.nextguide;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuideFragment extends Fragment {
    private static final String DEFAULT_HEADEND_ID = "DITV807";

    private static final String ARG_HEADEND_ID = "headendId";
    private static final String ARG_SCROLL_Y = "scrollY";
    private static final String ARG_CHANNEL_ARRAY = "channelArray";

    private String mHeadendId;
    private int scrollY;
    private JSONArray mChannelArray;

    private OnFragmentInteractionListener mListener;
    private View mView;
    private ChannelListView mChannelListView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param headendId TMS Headend ID
     * @return A new instance of fragment GuideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuideFragment newInstance(String headendId) {
        GuideFragment fragment = new GuideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEADEND_ID, headendId);
        fragment.setArguments(args);
        return fragment;
    }

    public GuideFragment() {
        mHeadendId = DEFAULT_HEADEND_ID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeadendId = getArguments().getString(ARG_HEADEND_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_guide, container, false);

        GuideScrollView guideScrollView = (GuideScrollView) mView.findViewById(R.id.guide_scroll_view);
        mChannelListView = (ChannelListView) mView.findViewById(R.id.channel_list);
        guideScrollView.addScrollYListener(mChannelListView);
        mChannelListView.addScrollYListener(guideScrollView);

        GuideHorizontalScrollView guideHorizontalScrollView = (GuideHorizontalScrollView) mView.findViewById(R.id.guide_horiz_scroll_view);
        GuideHorizontalScrollView timeHeaderScrollView = (GuideHorizontalScrollView) mView.findViewById(R.id.time_header_scroll_view);
        guideHorizontalScrollView.addScrollXListener(timeHeaderScrollView);
        timeHeaderScrollView.addScrollXListener(guideHorizontalScrollView);

        if(savedInstanceState != null) {

            if(savedInstanceState.getInt(ARG_SCROLL_Y) != 0) {
                scrollY = savedInstanceState.getInt(ARG_SCROLL_Y);
                Log.d(getClass().getSimpleName(), "SAVED Scroll Y :" + scrollY);
            }

            if(savedInstanceState.getString(ARG_CHANNEL_ARRAY) != null) {
                try {
                    mChannelArray = new JSONArray(savedInstanceState.getString(ARG_CHANNEL_ARRAY));
                    initChannels(mChannelArray);
                } catch(JSONException je) {
                    Log.w(getClass().getSimpleName(), "Channel JSON Exception", je);
                }
            }

        }

        if(mChannelArray == null) {
            requestChannels();
        }

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_guide, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.guide_settings) {
            return true;
        }

        // Non-applicable menu item we did not handle:
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onSaveInstanceState (Bundle outState) {
        outState.putInt(ARG_SCROLL_Y, mChannelListView.getVerticalScroll());
        outState.putString(ARG_CHANNEL_ARRAY, mChannelArray.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(getClass().getSimpleName(), "onDestroyView() invoked");

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public interface ScrollYListener {
        public void scrollYChanged(int y);
    }

    public interface ScrollXListener {
        public void scrollXChanged(int x);
    }

    private void initChannels(final JSONArray channelArray) {
        mChannelArray = channelArray;

        buildChannelList(channelArray);

        //TODO: call requestGrid that will invoke buildChannelRows in callback:
        //requestGrid();

        //TEST ONLY!!!
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                buildChannelRowsTest(channelArray);
            }
        }, 100);

        if(scrollY != 0) {
            Log.d(getClass().getSimpleName(), "Setting scroll Y to :" + scrollY);
            mChannelListView.setInitialScrollY(scrollY);
        }
    }

    private void requestChannels() {
        String url = WebManager.getInstance(getActivity()).getRequestChannelsUrl(mHeadendId);
        Log.d(getClass().getSimpleName(), "URL:" + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(getClass().getSimpleName(), "Response: " + response.toString());

                /*
                JSONArray jsonArray = new JSONArray();
                for(int i=0; i < 20; i++) {
                    try {
                        jsonArray.put(i, response.get(i));
                    } catch(Exception e) {}
                }
                response = jsonArray;
                */

                initChannels(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getSimpleName(), "Response Error:", error);
            }
        });

        // Add the request to the RequestQueue.
        WebManager.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void buildChannelList(JSONArray channelArray) {
        ChannelListAdapter channelListAdapter = new ChannelListAdapter(getActivity(), channelArray);

        ListView channelListView = (ListView) mView.findViewById(R.id.channel_list);
        channelListView.setAdapter(channelListAdapter);
    }

    private void buildChannelRows(JSONArray gridResult) {

    }

    private class Show {
        String title;
        String subTitle;
        Date startDate;
        Date endDate;
    }

    private class AiringCell {
        Show show;
    }

    private class ChannelRow {
        int stationId;
        List<AiringCell> cells = new ArrayList<AiringCell>();
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public void buildChannelRowsTest(JSONArray channelArray) {
        Log.d(getClass().getSimpleName(), "NUM ROWS: " + channelArray.length());

        TableLayout tl = (TableLayout)mView.findViewById(R.id.guide_layout);
        int pixelHeight = GuideFragment.dpToPx(52, getActivity());
        pixelHeight += 2;

        String channelNum;

        for (int i = 0; i < channelArray.length(); i++) {

            try {
                JSONObject jsonObj = (JSONObject) channelArray.get(i);

                channelNum = "" + jsonObj.getInt("channel_num");

                TableRow tr = new TableRow(getActivity());
                //android.widget.TableRow.LayoutParams lp = new android.widget.TableRow.LayoutParams(android.widget.TableRow.LayoutParams.MATCH_PARENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT);
                android.widget.TableRow.LayoutParams lp = new android.widget.TableRow.LayoutParams(android.widget.TableRow.LayoutParams.MATCH_PARENT, pixelHeight);
                tr.setLayoutParams(lp);

                TextView tvLeft = new TextView(getActivity());
                tvLeft.setLayoutParams(lp);
                if (i % 2 == 0) {
                    tvLeft.setBackgroundColor(Color.parseColor("#DCDCDC"));
                } else {
                    tvLeft.setBackgroundColor(Color.parseColor("#CAC9C9"));
                }

                tvLeft.setText("Channel " + channelNum + ".1 Index " + i);
                TextView tvCenter = new TextView(getActivity());
                tvCenter.setLayoutParams(lp);

                if (i % 2 == 0) {
                    tvCenter.setBackgroundColor(Color.parseColor("#D3D3D3"));
                } else {
                    tvCenter.setBackgroundColor(Color.parseColor("#DCDCDC"));
                }
                tvCenter.setText("Channel " + channelNum + ".2 Index " + i);
                TextView tvRight = new TextView(getActivity());
                tvRight.setLayoutParams(lp);

                if (i % 2 == 0) {
                    tvRight.setBackgroundColor(Color.parseColor("#CAC9C9"));
                } else {
                    tvRight.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
                tvRight.setText("Channel " + channelNum + ".3 Index " + i);

                tr.addView(tvLeft);
                tr.addView(tvCenter);
                tr.addView(tvRight);

                tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, pixelHeight));

            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Exception:", e);
            }
        }
    }

    /*
    private void initChannelRows(JSONArray gridArray) {
        List<ChannelRow> rows = new ArrayList<ChannelRow>();
        Map<Integer, ChannelRow> stationIdToRow = new HashMap<Integer, ChannelRow>();

        for(int i=0; i < gridArray.length(); i++) {

            try {
                JSONObject jsonObj = (JSONObject) gridArray.get(i);

                ChannelRow channelRow = new ChannelRow();

                JSONArray airings = jsonObj.getJSONArray("airings");
                for(int j=0; j < airings.length(); j++) {

                }

                channelListItem.channelNum = "" + jsonObj.getInt("channel_num");
                channelListItem.callSign = jsonObj.getString("callsign");
                channelListItem.stationId = jsonObj.getString("prg_svc_id");
                channelListItem.imageUrl = jsonObj.getString("image_url");

                Log.d(getClass().getSimpleName(),
                        "Channel List Item " + i + ": " +
                                channelListItem.channelNum +
                                "(" + channelListItem.callSign + ")");

                mList.add(channelListItem);
            } catch(Exception e) {
                Log.e(getClass().getSimpleName(), "Exception:", e);
            }
        }
    }
    */
}
