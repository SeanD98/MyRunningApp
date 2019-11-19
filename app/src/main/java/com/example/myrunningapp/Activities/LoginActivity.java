package com.example.myrunningapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.AppUtils;
import com.example.myrunningapp.Utils.StyledMapActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //Variables
    EditText Username;
    EditText Password;

    TextView welcomeText;
    TextView subText;
    TextView signUpButton;
    TextView forgotPassButton;

    Button loginButton;
    Button skipButton;

    ImageView logo;
   // ImageView back_arrow_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

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
        forgotPassButton = (TextView) findViewById(R.id.forgot_pass_bttn_txt);

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

        forgotPassButton.startAnimation(slide_in_from_right);

        logo.startAnimation(zoom_in);

        HashMap<String, String> params;

        final String username = Username.getText().toString();
        final String password = Password.getText().toString();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if username and password is empty
                    login();
                    Toast.makeText(getApplicationContext(), "works", Toast.LENGTH_SHORT).show();
                    hideKeyboard();
                }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToSignUp);
            }
        });

        forgotPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        final String password = Password.getText().toString();

        MyHTTPClient.Username = username;
        MyHTTPClient.Password = password;

        if (username.isEmpty() || password.isEmpty()) {
            Username.startAnimation(shake);
        } else {
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
                                                Username.setText("");
                                                Password.setText("");
                                                Username.startAnimation(shake);
                                                Password.startAnimation(shake);
                                            }
                                        });
                                    } else {

                                        Integer userID = (Integer)data.get("userid");
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
                    }
                }
            });
            thread.start();
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
