package com.gohachi.tugcairv02.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.gohachi.tugcairv02.Gps.GpsUtils;
import com.gohachi.tugcairv02.Pages.DashboardActivity;
import com.gohachi.tugcairv02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean loggedIn, isVerified;
    private boolean isGPS = false;

    private EditText mUsername, mPassword;
    private TextView mSignUp;
    private Button mBtnSignIn;
    private ProgressBar mProgressBarLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_login);

        FirebaseInit();

        // TODO: Need authorize GPS
        new GpsUtils(LoginActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        loggedIn = isLoggedIn();
        if (loggedIn) {
            goToDashboard(isGPS);
        }

        mUsername = findViewById(R.id.assistant_email);
        mPassword = findViewById(R.id.assistant_password);
        mBtnSignIn = findViewById(R.id.assistant_signin);
        mSignUp = findViewById(R.id.assistant_signup);
        mProgressBarLogin = findViewById(R.id.progressBarLogin);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(username.isEmpty() && password.isEmpty())) {
                    showProgress();
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgress();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            isVerified = user.isEmailVerified();

                            if (task.isSuccessful() && isVerified) {
                                //  login sucess
                                //  go to dashboard
                                goToDashboard(isGPS);
                            }else if(!isVerified){
                                showMessageBox("Please verify your email first!");
                            }else {
                                //  login failed
                                showMessageBox("Login failed. Please check username and password!");
                            }
                        }
                    });
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });
    }

    private void FirebaseInit() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean isLoggedIn() {

        if (mAuth.getCurrentUser() != null) {
            isVerified =  mAuth.getCurrentUser().isEmailVerified();
            if (isVerified){
                return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }


    private void goToDashboard(Boolean gpsStatus ) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("statusGPS", gpsStatus);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showMessageBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Login");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void hideProgress() {
        mProgressBarLogin.setVisibility(View.GONE);
        mUsername.setEnabled(true);
        mPassword.setEnabled(true);
    }

    private void showProgress() {
        mProgressBarLogin.setVisibility(View.VISIBLE);
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }



}
