package com.nextguide.nextguide;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuideFragment extends Fragment {
    private static final String ARG_HEADEND_ID = "headendId";
    private static final String DEFAULT_HEADEND_ID = "DITV807";

    private String mHeadendId;

    private OnFragmentInteractionListener mListener;
    private View mView;

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

        GuideScrollView guideScrollView = (GuideScrollView)mView.findViewById(R.id.guide_scroll_view);
        ChannelListView channelListView = (ChannelListView)mView.findViewById(R.id.channel_list);
        guideScrollView.addScrollYListener(channelListView);
        channelListView.addScrollYListener(guideScrollView);

        GuideHorizontalScrollView guideHorizontalScrollView = (GuideHorizontalScrollView)mView.findViewById(R.id.guide_horiz_scroll_view);
        GuideHorizontalScrollView timeHeaderScrollView = (GuideHorizontalScrollView)mView.findViewById(R.id.time_header_scroll_view);
        guideHorizontalScrollView.addScrollXListener(timeHeaderScrollView);
        timeHeaderScrollView.addScrollXListener(guideHorizontalScrollView);

        requestChannels();

        return mView;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_guide, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        if(item.getItemId() == R.id.guide_settings) {
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

    private void requestChannels() {
        String url = WebManager.getInstance(getActivity()).getRequestChannelsUrl(mHeadendId);
        Log.d(getClass().getSimpleName(), "URL:" + url);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(getClass().getSimpleName(), "Response: " + response.toString());
                buildChannelList(response);
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

        ListView channelListView = (ListView)mView.findViewById(R.id.channel_list);
        channelListView.setAdapter(channelListAdapter);
    }
}
