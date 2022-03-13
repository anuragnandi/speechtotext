package com.example.wriapplogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvVerifyMsg;
    Button btnLogout, btnVerify;

    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {

        tvVerifyMsg = findViewById(R.id.tvVerifyMsg);
        btnVerify = findViewById(R.id.btnVerify);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        fAuth=  FirebaseAuth.getInstance();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();


        if (! currentUser.isEmailVerified()) {
            tvVerifyMsg.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout: {
                userLogout();
                break;
            }
            case R.id.btnVerify: {
                emailVerify();
                break;
            }
        }
    }

    private void userLogout() {
        fAuth.signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    private void emailVerify() {
        fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Verification Email sent", Toast.LENGTH_LONG).show();
                tvVerifyMsg.setVisibility(View.GONE);
                btnVerify.setVisibility(View.GONE);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.resetUserPasswordMenu) {
            resetPassword();
        }

        else if (item.getItemId() == R.id.updateEmailMenu) {
            updateEmailAddress();
        }
        else if (item.getItemId() == R.id.deleteAccountMenu)
        {
            deleteEmail();
        }
        return super.onOptionsItemSelected(item);
    }

    // use ResetPassword Activity with Save button
    private void resetPassword() {
        startActivity(new Intent(this, ResetPassword.class));
        finish();

    }


    private void updateEmailAddress() {
        View v = inflater.inflate(R.layout.forgetpassword_popup, null);
        reset_alert.setTitle("Update Email?")
                .setMessage("Enter Your new Email Address")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int i){
                        //validate the email address
                        EditText emailAddress = v.findViewById(R.id.reset_Email_PopUp);
                        if (emailAddress.getText().toString().isEmpty()) {
                            emailAddress.setError("Required Field");
                            return;
                        }
                        //send the reset link
                        currentUser.updateEmail(emailAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Email Update", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null)
                .setView(v)
                .create().show();
    }

    //using Popup window
    private void deleteEmail() {

        reset_alert.setTitle("Delete account Permanently?")
                .setMessage("Are you sure to delete this account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Account Deleted",Toast.LENGTH_LONG).show();
                                fAuth.signOut();
                                startActivity(new Intent(MainActivity.this,Login.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
