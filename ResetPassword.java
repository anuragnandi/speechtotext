package com.example.wriapplogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{

    EditText newPassword,confirmPassword;
    Button btnSave;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPassword=findViewById(R.id.newPassword);
        confirmPassword=findViewById(R.id.confirmPassword);
        btnSave=findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        user= FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onClick(View view) {
        if(newPassword.getText().toString().isEmpty()){
            newPassword.setError("Please Enter The New Password!");
            return;
        }
        if(confirmPassword.getText().toString().isEmpty()){
            confirmPassword.setError("Please Enter The Confirmation Password!");
            return;
        }
        if(!newPassword.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password Do Not Match");
            return;
        }

        user.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ResetPassword.this,"Password Update",Toast.LENGTH_LONG).show();
                startActivity(new Intent(ResetPassword.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResetPassword.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        }
    }
