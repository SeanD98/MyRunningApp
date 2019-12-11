package com.example.myrunningapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.AppUtils;
import com.example.myrunningapp.Utils.DataController;
import com.example.myrunningapp.Utils.StyledMapActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class LoginActivity extends AppCompatActivity {

    //Variables
    EditText Username;
    EditText Password;

    TextView welcomeText;
    TextView subText;
    TextView signUpButton;
    TextView errorMessage;

    Button loginButton;
    Button skipButton;

    ImageView logo;

    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                clearTextFields();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
        });

        //Animations
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        Animation slide_in_from_left_1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center);
        Animation slide_in_from_left_2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center_second);
        Animation slide_in_from_right = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_right_to_center);

        Username = (EditText) findViewById(R.id.txt_email);
        Password = (EditText) findViewById(R.id.txt_pass);

        welcomeText = (TextView) findViewById(R.id.txt_login);
        subText = (TextView) findViewById(R.id.txt_log_in_screen);
        signUpButton = (TextView) findViewById(R.id.sign_up_bttn_txt);
        errorMessage = (TextView) findViewById(R.id.errorMsg_txt);

        loginButton = (Button) findViewById(R.id.bttn_login);
        skipButton = (Button) findViewById(R.id.bttn_skip);

        logo = findViewById(R.id.logo_login);

        final Intent goToSignUp = new Intent(this, SignUpActivity.class);
        final Intent goToHome = new Intent(this, HomeActivity.class);

        welcomeText.startAnimation(slide_up);
        subText.startAnimation(slide_up);
        loginButton.startAnimation(slide_up);
        skipButton.startAnimation(slide_up);

        Username.startAnimation(slide_in_from_left_1);
        Password.startAnimation(slide_in_from_left_2);
        signUpButton.startAnimation(slide_in_from_left_1);

        logo.startAnimation(zoom_in);

        HashMap<String, String> params;

        final String username = Username.getText().toString();
        final String password = Password.getText().toString();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();
                    hideKeyboard();
                }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToSignUp);
            }
        });
    }

    public void clearTextFields(){
        Username.setText("");
        Password.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    public void login() {
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);

        final String username = Username.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final String password = Password.getText().toString();

        MyHTTPClient.Username = username;
        MyHTTPClient.Password = password;

        if (username.isEmpty() || password.isEmpty()) {
            Username.startAnimation(shake);
            Toast.makeText(this, "Please enter a username and password", Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (!username.matches(emailPattern) && username.length() > 0){
            Username.startAnimation(shake);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            Toast.makeText(this, "Please enter a vaild email address", Toast.LENGTH_LONG).show();
        } else if (username.matches(emailPattern) && password.length() > 0){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyHTTPClient.Login(username, password, new MyHTTPClient.APILoginCallback() {
                            @Override
                            public void onLoginResponse(boolean success, String response) {

                                try {
                                    JSONObject jsonObj = new JSONObject(response);

                                    Map<String, Object> responseJson = AppUtils.jsonToMap(jsonObj);
                                    Map<String, Object> data = (Map<String, Object>) responseJson.get("data");

                                    if (data.get("success").toString().equalsIgnoreCase("false")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Password.setText("");
                                                Username.startAnimation(shake);
                                                Password.startAnimation(shake);
                                                errorMessage.setText("Incorrect email or password!");
                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                            }
                                        });
                                    } else {
                                        Integer userID = (Integer)data.get("userid");
                                        if (userID != null) {
                                            DataController.getInstance(getApplicationContext()).DCuserID = userID;
                                        }
                                        String userName = data.get("username").toString();

                                        Intent goToMap = new Intent(getApplicationContext(), StyledMapActivity.class);
                                        startActivity(goToMap);
                                    }
                                }
                                catch(Exception e)
                                {
                                    Log.e("", e.toString());
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialog.Builder LocationDisabledDialog = new AlertDialog.Builder(LoginActivity.this);
                        LocationDisabledDialog.setTitle("No Network Connection");
                        LocationDisabledDialog.setMessage("Please ensure the device is connected to the internet");
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
        } else {
            if (username.matches(emailPattern) && password.length() <1 ){
                //if email is correct but password isnt
                Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                errorMessage.setText("Invalid password");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!username.matches(emailPattern)){
                errorMessage.setText("Invalid email address");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please enter a valid email address & password", Toast.LENGTH_LONG).show();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    public void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
