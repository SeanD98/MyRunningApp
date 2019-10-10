package com.example.myrunningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.AppUtils;

import org.json.JSONObject;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText email_signup;
    EditText signup_pass;
    EditText signup_pass_confirm;

    TextView welcomeTextSignup;
    TextView subTextSignup;
    TextView logInButton;

    Button signUpButton;

    ImageView logo_signup;
    ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Animations
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        Animation slide_in_from_left_1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center);
        Animation slide_in_from_left_2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center_second);
        Animation slide_in_from_left_3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center_third);


        //Initialisations
        email_signup = (EditText) findViewById(R.id.txt_email_signup);
        signup_pass = (EditText) findViewById(R.id.txt_pass_sign_up);
        signup_pass_confirm = (EditText) findViewById(R.id.txt_pass_confirm);

        welcomeTextSignup = (TextView) findViewById(R.id.txt_signup);
        subTextSignup = (TextView) findViewById(R.id.txt_sign_up_screen);
        logInButton = (TextView) findViewById(R.id.log_in_bttn_txt);

        signUpButton = (Button) findViewById(R.id.bttn_signup);

        logo_signup = (ImageView) findViewById(R.id.logo_signup);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);

        //Intents
        final Intent goToLogin = new Intent(this, LoginActivity.class);
        final Intent goToHome = new Intent(this, HomeActivity.class);


        //Animation initialisations
        logo_signup.startAnimation(zoom_in);
        welcomeTextSignup.startAnimation(slide_up);
        subTextSignup.startAnimation(slide_up);
        signUpButton.startAnimation(slide_up);

        email_signup.startAnimation(slide_in_from_left_1);
        signup_pass.startAnimation(slide_in_from_left_2);
        signup_pass_confirm.startAnimation(slide_in_from_left_3);

        logInButton.startAnimation(slide_in_from_left_1);

        //On-Click Methods
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLogin);
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToHome);
            }
        });
    }

//    EditText email_signup;
//    EditText signup_pass;
//    EditText signup_pass_confirm;


    public void signUp() {
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);
        //Get text from textboxes
        final String username = email_signup.getText().toString();
        final String password = signup_pass.getText().toString();

        MyHTTPClient.Username = username;
        MyHTTPClient.Password = password;

        if (username.isEmpty() || password.isEmpty()) {
            email_signup.startAnimation(shake);
            signup_pass.startAnimation(shake);
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MyHTTPClient.SignUp(username, password, new MyHTTPClient.APISignUpCallback() {
                            @Override
                            public void onSignUpResponse(boolean success, String response) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);

                                    Map<String, Object> responseJson = AppUtils.jsonToMap(jsonObj);
                                    Map<String, Object> data = (Map<String, Object>) responseJson.get("data");

                                    if (data.get("success").toString().equalsIgnoreCase("false")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                email_signup.setText("");
                                                signup_pass.setText("");
                                                email_signup.startAnimation(shake);
                                                signup_pass.startAnimation(shake);
                                            }
                                        });
                                    } else {

                                        Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(goToMain);
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
}
