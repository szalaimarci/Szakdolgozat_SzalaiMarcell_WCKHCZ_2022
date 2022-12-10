package com.example.szakdoga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilActivity extends AppCompatActivity {
    private static final String LOG_TAG = FunctionalityActivity.class.getName();
    private FirebaseUser user;

    EditText passwordET;
    EditText passwordNewET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 99) {
            finish();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i(LOG_TAG, "Azonosított felhasználó!");
        } else {
            Log.i(LOG_TAG, "Nem azonosított felhasználó!");
            finish();
        }

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passwordET = findViewById(R.id.editTextPassword);
        passwordNewET = findViewById(R.id.editTextPasswordNew);

        TextView userEmail = (TextView) findViewById(R.id.profilTextUserEmail);


        String email = user.getEmail();
        Log.i(LOG_TAG, "Email: " + email);

        userEmail.setText("E-mail cím: " + email);
    }

    public void newPass(View view) {
        String password = passwordET.getText().toString();
        String passwordNew = passwordNewET.getText().toString();
        String email = user.getEmail().toString();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(LOG_TAG, "Password updated");
                                        finish();
                                    } else {
                                        Log.i(LOG_TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.i(LOG_TAG, "Error auth failed");
                        }
                    }
                });
    }

    public void cancel(View view) {
        this.finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.finish();
        return true;
    }
}