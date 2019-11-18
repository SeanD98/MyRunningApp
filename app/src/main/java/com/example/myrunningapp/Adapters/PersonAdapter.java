package com.example.myrunningapp.Adapters;

import com.example.myrunningapp.Models.Person;
import com.example.myrunningapp.R;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class PersonAdapter extends BaseExpandableListAdapter {

    private List<String> mPeopleList;
    private Context mContext;
    private List<Map<String, Object>> mFriends;

    public PersonAdapter(Context context, List<String> headers, List<Map<String, Object>> friends) {
        mPeopleList = headers;
        mContext = context;
        mFriends = friends;
    }

    @Override
    public int getGroupCount() {
        if (mPeopleList != null) {
            return mPeopleList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (mPeopleList != null){
            return this.mPeopleList.get(groupPosition);
        } else {
            return "";
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mFriends.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.mContext.getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView lblHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}


