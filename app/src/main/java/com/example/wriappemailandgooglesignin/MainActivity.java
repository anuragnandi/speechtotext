package com.example.wriappemailandgooglesignin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final int RESULT_SPEECH = 1;
    TextView tvVerifyMsg, tvWelcome ,tvText;
    Button btnLogout, btnVerify;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    ImageButton btnSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvVerifyMsg = findViewById(R.id.tvVerifyMsg);
        btnVerify = findViewById(R.id.btnVerify);
        btnLogout = findViewById(R.id.btnLogout);
        tvText = findViewById(R.id.tvText);
        btnLogout.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        btnSpeak = findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvText.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();

        if (!currentUser.isEmailVerified()) {
            tvVerifyMsg.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);

        }


        String userName = currentUser.getEmail();
  /*    SharedPreferences preferences=getSharedPreferences("MyPref",MODE_PRIVATE);

        String userName=preferences.getString("userName","");
        String userEmail=preferences.getString("userEmail","");*/

        tvWelcome.setText("Welcome " + userName);
  /*
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    tvWelcome.setText("Welcome  " + account.getDisplayName().toString());
                }
            }
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(text.get(0));
                }
                break;
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

    private void emailVerify() {
        currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Verification Email sent", Toast.LENGTH_LONG).show();
                tvVerifyMsg.setVisibility(View.GONE);
                btnVerify.setVisibility(View.GONE);
               /* String nam=currentUser.getDisplayName().toString();
                Toast.makeText(MainActivity.this,nam,Toast.LENGTH_LONG).show();
                tvWelcome.setText("Welcome  "+currentUser.getDisplayName().toString());*/


            }

        });
    }

    private void userLogout() {
        fAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.resetUserPassword) {
            resetPassword();
        }

        if (item.getItemId() == R.id.updateEmailMenu) {
            updateEmailAddress();
        }
        if (item.getItemId() == R.id.deleteAccountMenu) {
            deleteEmail();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEmail() {

        reset_alert.setTitle("Delete account Permanently?")
                .setMessage("Are you sure to delete this account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                fAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void updateEmailAddress() {
        View v = inflater.inflate(R.layout.reset_popup, null);
        reset_alert.setTitle("Update Email?")
                .setMessage("Enter Your new Email Address")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //validate the email address
                        EditText emailAddress = v.findViewById(R.id.email_PopUP);
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

    private void resetPassword() {
        startActivity(new Intent(this, ResetPassword.class));
        finish();
    }
}