package com.example.myrunningapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.Adapters.RunsAdapter;
import com.example.myrunningapp.Models.Runs;
import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.DataController;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Console;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Class used to return previous runs based on the users userID number.
public class RunsActivity extends AppCompatActivity {

    ExpandableListView runsListView;
    List<Map<String, Object>> runsList = new ArrayList<>();
    RunsAdapter mRunsAdapter;
    ExpandableListAdapter runsAdapter;
    HashMap<String, List<String>> listDataChild = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_runs);

        ArrayList<Runs> mRunsCollection = new ArrayList<>();
        runsListView = findViewById(R.id.runs_list_expandable_view);
        getRuns();
    }

    public void getRuns(){

        final int userID = DataController.getInstance(getApplicationContext()).DCuserID;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyHTTPClient.getRuns(new MyHTTPClient.APIGetRunsCallback() {
                        @Override
                        public void onGetRunsResponse(boolean success, String serverResponse) {
                            try {
                                Gson gson = new GsonBuilder().create();

                                Map<String, Object> responseMap;
                                Type map = new TypeToken<Map<String, Object>>() {}.getType();
                                responseMap = gson.fromJson(serverResponse, map);

                                Map<String, Object> responseData;
                                responseData = (Map<String, Object>) responseMap.get("data");

                                ArrayList<Map<String, Object>> runRes;
                                runRes = (ArrayList<Map<String, Object>>) responseData.get("runs");

                                for(int i = 0; i < runRes.size(); i++) {
                                    Map<String, Object> run = new HashMap<>();
                                    run.put("distance", runRes.get(i).get("distance"));
                                    run.put("speed", runRes.get(i).get("speed"));
                                    run.put("time", runRes.get(i).get("time"));
                                    run.put("runID", runRes.get(i).get("runID"));
                                    run.put("date", runRes.get(i).get("date"));
                                    run.put("runObj", convertToList(runRes.get(i).get("runObj").toString()));

                                    runsList.add(run);
                                }

                                final ArrayList<String> runHeaders = new ArrayList<>();
                                final ArrayList<String> childData = new ArrayList<>();
                                if(runsList != null) {
                                    for(int i = 0; i < runsList.size(); i++) {
                                        runHeaders.add(runsList.get(i).get("date").toString());
                                    }
                                }
//
//                                Map<String, Object> runData = new HashMap<>();
//                                //Adding child data
//                                List<String> top250 = new ArrayList<String>();
//                                top250.add("123");
//                                top250.add("1234");
//                                top250.add("12345");
//                                top250.add("123456");
//                                top250.add("1234567");
//                                top250.add("12345678");
//                                top250.add("123456789");
//
//                                listDataChild.put(runHeaders.get(0), top250);


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRunsAdapter = new RunsAdapter(getApplicationContext(), runHeaders, runsList);
                                        runsListView.setAdapter(mRunsAdapter);
                                    }
                                });
                            } catch (Exception e){
                                Log.e("Failed Parsing Runs", e.toString());
                            }
                        }
                    });
                } catch (Exception e){
                    e.toString();
                }
            }
        });
        thread.start();
    }

    public ArrayList<LatLng> convertToList(String points)
    {
        ArrayList<LatLng> pointsList = new ArrayList<>();
        Type arrayList = new TypeToken<ArrayList<LatLng>>(){
        }.getType();

        pointsList = new Gson().fromJson(points, arrayList);
        return pointsList;
    }
}
