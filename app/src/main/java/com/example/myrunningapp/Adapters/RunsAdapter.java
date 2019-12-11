package com.example.myrunningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myrunningapp.Activities.MainActivity;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.DataController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunsAdapter extends BaseExpandableListAdapter {

    private List<String> mRunsList;
    private HashMap<String, List<String>> childData;
    private Context mContext;
    private List<Map<String, Object>> mRuns;
    Map<String, Object> run;

    public RunsAdapter (Context context, List<String> headers, List <Map<String, Object>> runs){
        mRunsList = headers;
        childData = new HashMap<>();
        mContext = context;
        mRuns = runs;
        run = new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        if (mRunsList != null){
            return mRunsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mRuns.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (mRunsList != null){
            return this.mRunsList.get(groupPosition);
        } else {
            return "";
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mRuns.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView lblHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        run = (Map<String, Object>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblChildData);
        TextView txtListChildTitle = (TextView) convertView.findViewById(R.id.childTitle);
        Button listButton = (Button) convertView.findViewById(R.id.view_on_map_bttn);

            if (childPosition == 0) {
                txtListChildTitle.setText("Distance");

                String distanceTxt = run.get("distance").toString();
                //int distanceInt = Integer.parseInt(distanceTxt);
                float distanceFloat = Float.parseFloat(distanceTxt);

                    if (distanceFloat > 40.0f) {
                        //meters
                        txtListChild.setText(distanceTxt + " meters");
                    } else {
                        //miles
                        txtListChild.setText(distanceTxt + " miles");
                    }
            }
            if (childPosition == 1) {
                //Glitch in Database returns speed in place of time, solution was to swap the variables
                txtListChildTitle.setText("Speed");
                txtListChild.setText(run.get("time").toString());
            }
            if (childPosition == 2) {
                txtListChildTitle.setText("Time");
                txtListChild.setText(run.get("speed").toString());
            }
            if (childPosition == 3) {
                txtListChildTitle.setText("ID");
                txtListChild.setText(run.get("runID").toString().replace(".0", ""));
            }
            if (childPosition == 4) {
                txtListChildTitle.setText("");
                txtListChild.setText("");
            }

            //Could not be completed in time, returns the latitude and longitude points for the users runs

//        if (childPosition == 5) {
//            txtListChildTitle.setText("Points");
//            listButton.setVisibility(View.VISIBLE);
//            txtListChild.setVisibility(View.GONE);
//            //txtListChild.setText(run.get("runObj").toString());
//            //start act with intent and pass the runObj to there to draw a line
//        }

//        listButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMethod();
//            }
//        });

            return convertView;

    }

    public void clickMethod(){
        Log.d("", "Button Clicked");
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
