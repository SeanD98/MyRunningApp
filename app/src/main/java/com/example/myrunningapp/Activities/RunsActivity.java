package com.example.myrunningapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.Adapters.RunsAdapter;
import com.example.myrunningapp.Models.Runs;
import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.DataController;
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
    public String run = "";


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
                                Map<String, Object> responseMap = new HashMap<>();
                                ArrayList<Map<String, Object>> runsListResponse = new ArrayList<Map<String, Object>>();
                                Type map = new TypeToken<Map<String, Object>>() {
                                }.getType();
                                responseMap = gson.fromJson(serverResponse, map);
                                runsListResponse = (ArrayList<Map<String, Object>>) responseMap.get("data");

                                final ArrayList<String> runHeaders = new ArrayList<>();

                                if (runsListResponse != null) {
                                    for (int i = 0; i < runsListResponse.size(); i++) {
                                        runHeaders.add(runsListResponse.get(i).get("time").toString());
                                    }
                                }

                                final ArrayList<Map<String, Object>> finalRunsListResponse = runsListResponse;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRunsAdapter = new RunsAdapter(getApplicationContext(), runHeaders, finalRunsListResponse);
                                        runsListView.setAdapter(mRunsAdapter);
                                    }
                                });
                            } catch (Exception e){
                                Log.e("", e.toString());
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
}
