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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrunningapp.Network.MyHTTPClient;
import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.AppUtils;
import com.example.myrunningapp.Utils.StyledMapActivity;

import org.json.JSONObject;

import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class SignUpActivity extends AppCompatActivity {

    EditText email_signup;
    EditText signup_pass;
    EditText signup_pass_confirm;

    TextView welcomeTextSignup;
    TextView subTextSignup;
    TextView logInButton;
    TextView errorMessage;

    Button signUpButton;

    ImageView logo_signup;

    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        Animation slide_in_from_left_3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_from_left_to_center_third);


        //Initialisations
        email_signup = (EditText) findViewById(R.id.txt_email_signup);
        signup_pass = (EditText) findViewById(R.id.txt_pass_sign_up);
        signup_pass_confirm = (EditText) findViewById(R.id.txt_pass_confirm);

        errorMessage = (TextView) findViewById(R.id.errorMsg_txt);
        welcomeTextSignup = (TextView) findViewById(R.id.txt_signup);
        subTextSignup = (TextView) findViewById(R.id.txt_sign_up_screen);
        logInButton = (TextView) findViewById(R.id.log_in_bttn_txt);

        signUpButton = (Button) findViewById(R.id.bttn_signup);

        logo_signup = (ImageView) findViewById(R.id.logo_signup);

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
                errorMessage.setText("");
                signUp();
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLogin);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void clearTextFields(){
        email_signup.setText("");
        signup_pass.setText("");
        signup_pass_confirm.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void signUp() {
        final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);

        final String username = email_signup.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final String password = signup_pass.getText().toString();
        final String confirmedPass = signup_pass_confirm.getText().toString();

        MyHTTPClient.Username = username;
        MyHTTPClient.Password = password;

        if (!username.matches(emailPattern)){
            Toast.makeText(this, "Please enter a valid username and password", Toast.LENGTH_LONG).show();
            errorMessage.setText("Invalid email address");
        }
        if (username.matches(emailPattern) && password.matches(confirmedPass) && password.length() > 0) {
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
                                                signup_pass_confirm.setText("");
                                                email_signup.startAnimation(shake);
                                                signup_pass.startAnimation(shake);
                                                signup_pass_confirm.startAnimation(shake);
                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                            }
                                        });
                                    } else {

                                        Intent goToMaps = new Intent(getApplicationContext(), StyledMapActivity.class);
                                        startActivity(goToMaps);
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
                        AlertDialog.Builder LocationDisabledDialog = new AlertDialog.Builder(SignUpActivity.this);
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
            if (username.matches(emailPattern) && !password.matches(confirmedPass)){
                //if email is correct but passwords are wrong
                errorMessage.setText("Passwords don't match");
                signup_pass.setText("");
                signup_pass_confirm.setText("");
                signup_pass.startAnimation(shake);
                signup_pass_confirm.startAnimation(shake);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!username.matches(emailPattern)) {
                //if email is incorrect
                errorMessage.setText("Invalid email address");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else if (!username.matches(emailPattern) && !password.matches(confirmedPass)){
                //email and password are incorrect
                errorMessage.setText("Invalid email and password");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }
}
