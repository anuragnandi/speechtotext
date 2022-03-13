package com.example.wriapplogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText email, password;
    Button btnCreateAccount, btnLogin, btnForgetPassword;
    FirebaseAuth fAuth;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnForgetPassword = findViewById(R.id.btnForgetPassword);
        btnForgetPassword.setOnClickListener(this);

        reset_alert = new AlertDialog.Builder(this);

        inflater = this.getLayoutInflater();

//        btnCreateAccount.setOnClickListener(new View.OnClickListener(){
/*
    @Override
    public void onClick(View view) {
        Log.d(String.valueOf(this), "onClick: okkkk");

        startActivity(new Intent(getApplicationContext(),Register.class));

    }
});*/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnCreateAccount:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.btnLogin:
                userLogin();
                break;
            case R.id.btnForgetPassword:
                userForgetPassword();
                break;
        }
    }


    private void userForgetPassword() {
            // start alertdialog
            View v = inflater.inflate(R.layout.forgetpassword_popup, null);
            reset_alert.setTitle("Forgot Password?")
                    .setMessage("Enter Your email to get password Reset link.")
                    .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //validate the email address
                            EditText emailAddress = v.findViewById(R.id.reset_Email_PopUp);
                            if (emailAddress.getText().toString().isEmpty()) {
                                emailAddress.setError("Required Field");
                                return;
                            }
                            //send the reset link
                            fAuth.sendPasswordResetEmail(emailAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Login.this,"Password reset email Sent",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", null)
                    .setView(v)
                    .create().show();

        }



    private void userLogin() {
        //valida the user input
        if (email.getText().toString().isEmpty()) {
            email.setError("Email is required");
            return;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Password is required");
            return;
        }
        //data
        fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,
                        e.getMessage(),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}