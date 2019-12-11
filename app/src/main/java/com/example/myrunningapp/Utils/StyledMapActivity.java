package com.example.myrunningapp.Utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myrunningapp.Activities.FriendsActivity;
import com.example.myrunningapp.Activities.LoginActivity;
import com.example.myrunningapp.Activities.RunsActivity;
import com.example.myrunningapp.Activities.SettingsActivity;
import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class StyledMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Handler timerHandler = new Handler();

    TextView timerTextView;
    TextView speedTxt;
    TextView distancetxt;
    TextView calorieCounterTxt;
    TextView calorieTitle;

    Button dismissButton;

    ConstraintLayout instructionContainer;

    long startTime = 0;
    float[] Distance = new float[1];
    float overallDistance;
    float distanceTo1Place;
    float Speed;
    float SpeedTo1Place;
    double caloriesBurned;
    double caloriesTo1Place;

    String json;
    ArrayList<LatLng> gotPoints;

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;

    public Location mLocation;
    private ArrayList<LatLng> points;
    Polyline line;
    MarkerOptions marker = new MarkerOptions();
    LocationManager mLocationManager;

    boolean onFirstLaunch = true;
    FloatingActionButton startButton;
    boolean start;
    FloatingActionButton stopButton;
    boolean stop;

    private static final String TAG = StyledMapActivity.class.getSimpleName();
    private static final String SELECTED_STYLE = "selected_style";
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1;
    private static final float SMALLEST_DISPLACEMENT = 0.25F;
    private static final long LOCATION_REFRESH_TIME = 300;
    private static final float LOCATION_REFRESH_DISTANCE = 10;
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PERMISSIONS_REQUEST_LOCATION_2 = 99;

    private int mSelectedStyleId = R.string.style_label_default;
    private int mStyleIds[] = {
            R.string.style_label_retro,
            R.string.style_label_night,
            R.string.style_label_grayscale,
            R.string.style_label_no_pois_no_transit,
            R.string.style_label_default,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedStyleId = savedInstanceState.getInt(SELECTED_STYLE);
        }
        setContentView(R.layout.activity_maps);

        timerTextView = (TextView) findViewById(R.id.run_timer_txtv);
        timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
        distancetxt = (TextView) findViewById(R.id.distance);
        calorieCounterTxt = (TextView) findViewById(R.id.calorie_counter_txt);
        calorieTitle = (TextView) findViewById(R.id.calorie_title);
        speedTxt = (TextView) findViewById(R.id.speed_txt);

        instructionContainer = (ConstraintLayout) findViewById(R.id.instruction_container_layout);

        dismissButton = (Button) findViewById(R.id.dismiss_bttn);

        points = new ArrayList<>();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION_2);
            }
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionContainer.setVisibility(View.GONE);
            }
        });

        startButton = (FloatingActionButton) findViewById(R.id.start_button);
        stopButton = (FloatingActionButton) findViewById(R.id.stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.hide();
                stopButton.show();
                start = true;
                stop = false;
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                distancetxt.setText("0.00");
                //speedTxt.setText(String.valueOf(Speed));
                if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1) {
                    calorieCounterTxt.setVisibility(View.VISIBLE);
                    calorieCounterTxt.setText("0" + " k/cal");
                    //calorieIcon.setVisibility(View.VISIBLE);
                }
                mMap.clear();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(StyledMapActivity.this);
                dialog.setMessage("Save this run?");
                dialog.setTitle("Save");
                dialog.setIcon(R.drawable.thumbnail);

                dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //storeRuns();
                        stopButton.hide();
                        startButton.show();
                        start = false;
                        stop = true;
                        timerHandler.removeCallbacks(timerRunnable);
                        //overallDistance = 0;
                        calorieCounterTxt.setText("");
                        distancetxt.setText("");
                        timerTextView.setText("");
                        timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                        speedTxt.setText("");
                        points.clear();
                        //show load screen
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopButton.hide();
                        startButton.show();
                        start = false;
                        stop = true;
                        timerHandler.removeCallbacks(timerRunnable);
                        overallDistance = 0;
                        calorieCounterTxt.setText("");
                        distancetxt.setText("");
                        timerTextView.setText("");
                        timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                        speedTxt.setText("");
                        points.clear();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.show();

                stopButton.hide();
                startButton.show();
                start = false;
                stop = true;
                storeRuns();
                timerHandler.removeCallbacks(timerRunnable);
                overallDistance = 0;
                points.clear();
            }
        });

        if (DataController.getInstance(getApplicationContext()).calorieCounterState == 0){
            calorieCounterTxt.setVisibility(View.GONE);
            calorieTitle.setVisibility(View.GONE);
        }
        if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1){
            calorieCounterTxt.setVisibility(View.VISIBLE);
            calorieTitle.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        if (DataController.getInstance(getApplicationContext()).calorieCounterState == 0){
            calorieCounterTxt.setVisibility(View.GONE);
            calorieTitle.setVisibility(View.GONE);
        }
        if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1){
            calorieCounterTxt.setVisibility(View.VISIBLE);
            calorieTitle.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_STYLE, mSelectedStyleId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        marker.alpha(0.0f);
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        setSelectedStyle();

        if (onFirstLaunch) {
            mMap = googleMap;
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }

            Location LastKnownLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (LastKnownLoc != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LastKnownLoc.getLatitude(), LastKnownLoc.getLongitude()), 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(LastKnownLoc.getLatitude(), LastKnownLoc.getLongitude()))
                        .zoom(20)
                        .bearing(90)
                        .tilt(40)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                marker.position(new LatLng(LastKnownLoc.getLatitude(), LastKnownLoc.getLongitude()));
                marker.title("You are here!");
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.running_icon));
                mMap.addMarker(marker);
                onFirstLaunch = false;
            }
        }
    }

    public void zoomMapToUser(GoogleMap googleMap)
    {
        //marker.alpha(0.0f);
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                .zoom(20)
                .bearing(90)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        marker.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        marker.title("You are here!");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.running_icon));
        mMap.addMarker(marker);
        onFirstLaunch = false;
    }


    private final LocationListener mLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(final Location location) {
            mLocation = location;
            if (onFirstLaunch) {
                zoomMapToUser(mMap);
            } else {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);

                if(points != null && !points.isEmpty())
                {
                    //marker.alpha(0.0f);
                    LatLng LastLoc = points.get(points.size()-1);
                    double lastLocLat = LastLoc.latitude;
                    double lastLocLong = LastLoc.longitude;

                    double newLocLat = latLng.latitude;
                    double newLocLong = latLng.longitude;
                    Location.distanceBetween(lastLocLat, lastLocLong, newLocLat, newLocLong, Distance);

                    Speed = overallDistance += Distance[0]; //this it?

                    //convert distance to miles when measurementUnits switch is ON
                    if (DataController.getInstance(getApplicationContext()).measurementUnits == 0){
                        //If its OFF, units == meters
                        distanceTo1Place = Math.round(overallDistance);
                    } else if (DataController.getInstance(getApplicationContext()).measurementUnits == 1){
                        //If its ON, units == miles
                        distanceTo1Place = Math.round((overallDistance) * 0.000621371);
                    }


                    Log.d("", "Distance =" + distanceTo1Place);

                    if (DataController.getInstance(getApplicationContext()).measurementUnits == 0){
                        distancetxt.setText((distanceTo1Place + "m"));
                    }
                    else if (DataController.getInstance(getApplicationContext()).measurementUnits == 1){
                        distancetxt.setText((distanceTo1Place + " miles"));
                    }
                }

                points.add(latLng);
                redrawLine();
                marker.position(new LatLng(latLng.latitude, latLng.longitude));
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.running_icon));

                mMap.addMarker(marker);

                Speed = location.getSpeedAccuracyMetersPerSecond();
                SpeedTo1Place = Math.round(Speed);
                speedTxt.setText(String.valueOf(SpeedTo1Place));

                if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1) {
                    caloriesBurned = distanceTo1Place * (0.06);
                    caloriesTo1Place = Math.round(caloriesBurned);
                    calorieCounterTxt.setText(String.valueOf(caloriesTo1Place));
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            AlertDialog.Builder LocationDisabledDialog = new AlertDialog.Builder(StyledMapActivity.this);
            LocationDisabledDialog.setTitle("Network Changed");
            LocationDisabledDialog.setMessage("Please ensure Location Services are available");
            LocationDisabledDialog.setIcon(R.drawable.thumbnail);
            LocationDisabledDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Network status changed", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = LocationDisabledDialog.create();
            alert.show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            AlertDialog.Builder LocationDisabledDialog2 = new AlertDialog.Builder(StyledMapActivity.this);
            LocationDisabledDialog2.setTitle("Location Found");
            LocationDisabledDialog2.setMessage("Device location has been recognised");
            LocationDisabledDialog2.setIcon(R.drawable.thumbnail);
            LocationDisabledDialog2.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Setting for location", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = LocationDisabledDialog2.create();
            alert.show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            AlertDialog.Builder LocationDisabledDialog3 = new AlertDialog.Builder(StyledMapActivity.this);
            LocationDisabledDialog3.setTitle("Location Lost");
            LocationDisabledDialog3.setMessage("Please ensure Location Services are enabled");
            LocationDisabledDialog3.setIcon(R.drawable.thumbnail);
            LocationDisabledDialog3.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Searching for location", Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog alert = LocationDisabledDialog3.create();
            alert.show();
        }
    };

    private void redrawLine() {
        if (start) {
            mMap.clear();
            PolylineOptions options = new PolylineOptions().width(20).color(R.color.colorAccent).geodesic(true);
            for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                options.add(point);
            }
            line = mMap.addPolyline(options);
            //mMap.addMarker(marker);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); //added
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.styled_map, menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.theme_menu_item) {
            showStylesDialog();
        }
        if (item.getItemId() == R.id.friends_menu_item) {
            Intent goToFriends = new Intent(this, FriendsActivity.class);
            startActivity(goToFriends);
        }
        if (item.getItemId() == R.id.settings_menu_item) {
            Intent goToSettings = new Intent(this, SettingsActivity.class);
            startActivity(goToSettings);
        }
        if (item.getItemId() == R.id.runs_menu_item) {
            Intent goToRuns = new Intent(this, RunsActivity.class);
            startActivity(goToRuns);
        }
        return true;
    }


    private void showStylesDialog() {
        List<String> styleNames = new ArrayList<>();
        for (int style : mStyleIds) {
            styleNames.add(getString(style));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.style_choose));
        builder.setItems(styleNames.toArray(new CharSequence[styleNames.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedStyleId = mStyleIds[which];
                        String msg = getString(R.string.style_set_to, getString(mSelectedStyleId));
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                        setSelectedStyle();
                    }
                });
        builder.show();
    }


    private void setSelectedStyle() {
        MapStyleOptions style;
        switch (mSelectedStyleId) {
            case R.string.style_label_retro:
                // Sets the retro style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
                timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                break;
            case R.string.style_label_night:
                // Sets the night style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
                timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                break;
            case R.string.style_label_grayscale:
                // Sets the grayscale style via raw resource JSON.
                style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_grayscale);
                timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                break;
            case R.string.style_label_no_pois_no_transit:
                // Sets the no POIs or transit style via JSON string.
                style = new MapStyleOptions("[" +
                        "  {" +
                        "    \"featureType\":\"poi.business\"," +
                        "    \"elementType\":\"all\"," +
                        "    \"stylers\":[" +
                        "      {" +
                        "        \"visibility\":\"off\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\":\"transit\"," +
                        "    \"elementType\":\"all\"," +
                        "    \"stylers\":[" +
                        "      {" +
                        "        \"visibility\":\"off\"" +
                        "      }" +
                        "    ]" +
                        "  }" +
                        "]");
                break;
            case R.string.style_label_default:
                //Removes previously set style, by setting it to null.
                style = null;
                //timerTextView.setTextColor(getResources().getColor(R.color.app_background));
                timerTextView.setTextColor(getResources().getColor(R.color.txt_colour_3));
                break;
            default:

                return;
        }
        mMap.setMapStyle(style);
    }

    public void storeRuns()
    {
        json = new Gson().toJson(points);
        final String time = String.valueOf(timerTextView.getText());
        final String distance = String.valueOf(distanceTo1Place);
        final String speed = String.valueOf(SpeedTo1Place);
        final int _userID = DataController.getInstance(getApplicationContext()).DCuserID;
        final ArrayList<LatLng> runObj = gotPoints; //initisaltion which the http.post runs below uses

        final String userID = Integer.toString(_userID);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyHTTPClient.postRuns(time, distance, speed, userID, json, new MyHTTPClient.APIPostRunsCallback() {
                        @Override
                        public void onPostRunsResponse(String serverResponse) {
                            try {

                                Gson gson = new GsonBuilder().create();
                                Map<String, String> responseMap = new HashMap<>();
                                Type map = new TypeToken<Map<String, String>>() {}.getType();
                                responseMap = gson.fromJson(serverResponse, map);

                                String success = responseMap.get("success");
                                String msg = responseMap.get("msg");
                            } catch (Exception e){
                                Log.e("", e.toString());
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("", e.toString());
                    AlertDialog.Builder LocationDisabledDialog = new AlertDialog.Builder(StyledMapActivity.this);
                    LocationDisabledDialog.setTitle("No Network Connection");
                    LocationDisabledDialog.setMessage("Cannot store run to the cloud, no network connection found. \nPlease connect the device to the internet and try again.");
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
}