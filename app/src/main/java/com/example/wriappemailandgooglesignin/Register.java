package com.example.wriappemailandgooglesignin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText registerFullName, registerEmail, registerPassword, confirmPassword;
    Button btnRegister;
    TextView tvAlreadyHaveAccount;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

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
        btnRegister.setOnClickListener(this);

        tvAlreadyHaveAccount=findViewById(R.id.tvAlreadyHaveAccount);
        tvAlreadyHaveAccount.setOnClickListener(this);

        fAuth=FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btnRegister: {
                registerNewUser();
                break;
            }
            case R.id.tvAlreadyHaveAccount:{
                startActivity(new Intent(this,LoginActivity.class));
                finish();
            }
        }
    }

    private void registerNewUser() {
        String fullName = registerFullName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();
        String confPassword = confirmPassword.getText().toString();
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
                //create user account
                String userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference=fStore.collection("user_profile").document(userID);
                Map<String,Object> user=new HashMap<>();
                user.put("Name",fullName);
                user.put("Email",email);
                user.put("Password",password);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void unused) {
Toast.makeText(Register.this,userID,Toast.LENGTH_LONG).show();
                    }
                });

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
}