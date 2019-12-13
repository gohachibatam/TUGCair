package com.gohachi.tugcairv02.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gohachi.tugcairv02.Authentication.LoginActivity;
import com.gohachi.tugcairv02.Gps.GpsUtils;
import com.gohachi.tugcairv02.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mBtnLogout;
    private boolean isGPS = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseAuthInit();

        mBtnLogout = findViewById(R.id.btnLogOut);

        Bundle bundleVar = getIntent().getExtras();
        isGPS = bundleVar.getBoolean("statusGPS");

        if(isGPS == false){
            Toast.makeText(this, "Please turn on GPS to use this app!", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            redirectPage(DashboardActivity.this, LoginActivity.class);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppDashboardActivity.this, R.id.nav_host_fragment);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
////        NavigationUI.setupWithNavController(navView, navController);BarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
////        NavController navController = Navigation.findNavController(

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Toast.makeText(DashboardActivity.this, "You pressed Home!", Toast.LENGTH_SHORT).show();
                    case R.id.navigation_person:
                        Toast.makeText(DashboardActivity.this, "You pressed Person!", Toast.LENGTH_SHORT).show();
                    case R.id.navigation_notifications:
                        Toast.makeText(DashboardActivity.this, "You pressed Notifications!", Toast.LENGTH_SHORT).show();
                    case R.id.navigation_profile:
                        Toast.makeText(DashboardActivity.this, "You pressed My Profile!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                redirectPage(DashboardActivity.this, LoginActivity.class);
            }
        });
    }

    private void FirebaseAuthInit() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
