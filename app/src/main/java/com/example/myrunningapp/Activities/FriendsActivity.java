package com.example.myrunningapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.RequestQueue;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.Adapters.RunsAdapter;
import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Models.Person;
import com.example.myrunningapp.Adapters.PersonAdapter;
import com.example.myrunningapp.Utils.AppUtils;
import com.example.myrunningapp.Utils.DataController;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    ExpandableListView friendsListView;
    List<Map<String, Object>> runsList = new ArrayList<>();
    PersonAdapter mPersonAdapter;
    public String friend = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ArrayList<Person> mPeopleCollection = new ArrayList<Person>();
        friendsListView = (ExpandableListView) findViewById(R.id.friends_list_expandable_view);
        //PersonAdapter mPersonAdapter = new PersonAdapter(this,mPeopleCollection);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyHTTPClient.getAllUsers(new MyHTTPClient.APIGetFriendsCallback() {
                        @Override
                        public void onAllFriendsResponse(boolean success, String serverResponse) {
                            try {
                                Gson gson = new GsonBuilder().create();
                                Map<String, Object> responseMap = new HashMap<String, Object>();
                                ArrayList<Map<String, Object>> friendsListResponse = new ArrayList<Map<String, Object>>();
                                Type map = new TypeToken<Map<String, Object>>() {
                                }.getType();
                                responseMap = gson.fromJson(serverResponse, map);
                                friendsListResponse = (ArrayList<Map<String, Object>>) responseMap.get("data");

                                final ArrayList<String> friendHeaders = new ArrayList<>();

                                if (friendsListResponse != null) {
                                    for (int i = 0; i < friendsListResponse.size(); i++) {
                                        friendHeaders.add(friendsListResponse.get(i).get("username").toString());
                                    }
                                }

                                final ArrayList<Map<String, Object>> finalFriendsListResponse = friendsListResponse;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPersonAdapter = new PersonAdapter(getApplicationContext(), friendHeaders, finalFriendsListResponse);
                                        friendsListView.setAdapter(mPersonAdapter);
                                    }
                                });

                            } catch (Exception e) {
                                Log.e("", e.toString());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder LocationDisabledDialog = new AlertDialog.Builder(FriendsActivity.this);
                    LocationDisabledDialog.setTitle("No Network Connection");
                    LocationDisabledDialog.setMessage("Cannot retrieve friends at the moment. \nPlease ensure the device is connected to the internet");
                    LocationDisabledDialog.setIcon(R.drawable.thumbnail);
                    LocationDisabledDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                    AlertDialog alert = LocationDisabledDialog.create();
                    alert.show();
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("                     Friends"); //Temp until gravity solution is found
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        goToAddFriends();
        return true;
    }

    public void goToAddFriends(){
        Intent goToFriends = new Intent(this, AddFriendActivity.class);
        startActivity(goToFriends);
    }
}
