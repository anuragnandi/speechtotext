package com.example.wriappemailandgooglesignin;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email, password;
    ImageView googleImg;
    TextView tvForgetPassword,tvCreateAccount;
    Button btnLogin;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvCreateAccount.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        googleImg=findViewById(R.id.googleImg);
        googleImg.setOnClickListener(this);

        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvForgetPassword.setOnClickListener(this);

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        fAuth = FirebaseAuth.getInstance();
        currentUser=fAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvCreateAccount:
                startActivity(new Intent(this, Register.class));
                finish();
                break;
            case R.id.btnLogin:
                emailLogin();
                break;

            case R.id.tvForgetPassword:
                userForgetPassword();
                break;
            case R.id.googleImg:
                googleLogin();
                break;
        }
    }

    private void googleLogin() {
        startActivity(new Intent(this,GoogleSignInActivity.class));
        finish();
    }

    private void userForgetPassword() {

        // start alertdialog
        View v = inflater.inflate(R.layout.reset_popup, null);
        reset_alert.setTitle("Forgot Password?")
                .setMessage("Enter Your Email to get password Reset link.")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //validate the email address
                        EditText emailAddress = v.findViewById(R.id.email_PopUP);
                        if (emailAddress.getText().toString().isEmpty()) {
                            emailAddress.setError("Required Field");
                            return;
                        }
                        //send the reset link
                        fAuth.sendPasswordResetEmail(emailAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this,"Password reset Email Sent",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null)
                .setView(v)
                .create().show();
    }


    private void emailLogin() {
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
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,
                        e.getMessage(),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser!= null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}