package com.example.wriapplogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.charset.StandardCharsets;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText registerFullName, registerEmail, registerPassword, confirmPassword;
    Button btnRegister, btnLogin;

    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        registerFullName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        fAuth=FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        String fullName = registerFullName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();
        String confPassword = confirmPassword.getText().toString();
        switch (view.getId())
        {
            case R.id.btnRegister: {
                if (fullName.isEmpty()) {
                    registerFullName.setError("Full Name is required");
                    return;
                }

                if (email.isEmpty()) {
                    registerEmail.setError("Email is required");
                    return;
                }
                if (password.isEmpty()) {
                    registerPassword.setError("Password is required");
                    return;

                }
                if (!confPassword.equals(password)) {
                    confirmPassword.setError("password do not match");
                    return;
                }
                //create the user using firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // send user to MainActivity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
            case R.id.btnLogin:{
                startActivity(new Intent(this,Login.class));
                finish();
            }
        }




    }
}