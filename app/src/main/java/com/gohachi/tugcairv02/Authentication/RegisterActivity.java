package com.gohachi.tugcairv02.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gohachi.tugcairv02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;



    private EditText mEmail, mFullname, mPassword, mPasswordConfirm, mContactPhone;
    private Button mDaftar;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_register);

        FirebaseInit();
        FirestoreInit();


        mEmail = findViewById(R.id.assistant_email);
        mFullname = findViewById(R.id.assistant_full_name);
        mPassword = findViewById(R.id.assistant_password);
        mPasswordConfirm = findViewById(R.id.assistant_password_confirm);
        mDaftar = findViewById(R.id.assistant_signin);
        mContactPhone = findViewById(R.id.assistant_contact_person);

        mDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailuser, passworduser, passwordconfirmuser;

                emailuser = mEmail.getText().toString().trim();
                passworduser = mPassword.getText().toString();
                passwordconfirmuser = mPasswordConfirm.getText().toString();

                if (emailuser.isEmpty() || passworduser.isEmpty() || passwordconfirmuser.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Form cannot be empty!", Toast.LENGTH_SHORT).show();
                }else if(!passworduser.equals(passwordconfirmuser)){
                    Toast.makeText(RegisterActivity.this, "Password is not match!", Toast.LENGTH_SHORT).show();
                }else if(passworduser.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 character!", Toast.LENGTH_SHORT).show();
                }else if(!(emailuser.isEmpty() && passworduser.isEmpty()) && passworduser.length() >= 6){
//                    Toast.makeText(RegisterActivity.this, "Success create account!", Toast.LENGTH_SHORT).show();

                    mAuth.createUserWithEmailAndPassword(emailuser, passwordconfirmuser)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "A verification has been sent to your email!", Toast.LENGTH_SHORT).show();
                                        addAccountToFirebase(user.getEmail());
                                        redirectPage(RegisterActivity.this, LoginActivity.class);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Registration failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }                                    // ...
                                }
                            });
                }
            }
        });
    }


    private void FirebaseInit() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void FirestoreInit(){
        mDatabase = FirebaseFirestore.getInstance();
    }

    private void redirectPage(Activity activity, Class goTo) {
        Intent intent = new Intent(activity, goTo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void addAccountToFirebase(String emailuser){
        String email, password, full_name, no_phone, role;
        email = emailuser;
        password = mPassword.getText().toString();
        full_name = mFullname.getText().toString();
        no_phone = mContactPhone.getText().toString();
        role = "user";
        Date timenow = Calendar.getInstance().getTime();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("full_name", full_name);
        user.put("key_pass", password);
        user.put("no_phone", no_phone);
        user.put("role", role);
        user.put("created_at", timenow);
        user.put("updated_at", null);
        // Add a new document with a generated ID
        mDatabase.collection("account")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());//
                        Toast.makeText(RegisterActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(RegisterActivity.this, "Error! : "+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
