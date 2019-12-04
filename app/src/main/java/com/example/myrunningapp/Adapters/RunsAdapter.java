package com.example.myrunningapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.myrunningapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunsAdapter extends BaseExpandableListAdapter {

    private List<String> mRunsList;
    private HashMap<String, List<String>> childData;
    private Context mContext;
    private List<Map<String, Object>> mRuns;

    public RunsAdapter (Context context, List<String> headers, List <Map<String, Object>> runs, HashMap<String, List<String>> childs){
        mRunsList = headers;
        childData = childs;
        mContext = context;
        mRuns = runs;
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
        return this.childData.get(this.mRunsList.get(groupPosition)).size();
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
        return this.childData.get(this.mRunsList.get(groupPosition))
                .get(childPosition);
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
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView != null){
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblChildData);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
