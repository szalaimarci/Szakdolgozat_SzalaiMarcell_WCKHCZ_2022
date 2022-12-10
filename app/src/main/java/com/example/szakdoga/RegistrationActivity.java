package com.example.szakdoga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREF_KEY = RegistrationActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText userNameET;
    EditText userEmailET;
    EditText passwordET;
    EditText passwordAgainET;
    CheckBox contributeCheckbox;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 99) {
            finish();
        }

        userNameET = findViewById(R.id.userNameEditText);
        userEmailET = findViewById(R.id.userEmailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordAgainET = findViewById(R.id.passwordAgainEditText);
        contributeCheckbox = findViewById(R.id.contributeCheckbox);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameET.setText(userName);
        passwordET.setText(password);
        passwordAgainET.setText(password);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i(LOG_TAG, "onCreate");

    }

    public void regist(View view) {
        String userName = userNameET.getText().toString();
        String email = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();
        boolean checked = contributeCheckbox.isChecked();

        if (!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "A megadott jelszavak nem egyeznek meg!");
            return;
        }

        if (!checked) {
            Log.e(LOG_TAG, "A helyadatok használatához hozzá kell járulni!");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + userName + ", jelszó: " + password + ", email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(LOG_TAG, "Felhasználó sikeresen létrehozva!");
                    startFunctionalityPage();
                } else {
                    Log.d(LOG_TAG, "A felhasználót nem sikerült létrehozni!");
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void startFunctionalityPage() {
        Intent intent = new Intent(this, FunctionalityActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    public void onCheckboxClicked(View view) {
        Log.i(LOG_TAG, "checkbox clicked");
    }
}