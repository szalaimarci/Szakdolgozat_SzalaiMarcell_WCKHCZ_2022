package com.example.szakdoga;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class qrscanner extends FunctionalityActivity implements ZXingScannerView.ResultHandler {

    private static final String LOG_TAG = FunctionalityActivity.class.getName();
    ZXingScannerView scannerView;
    DatabaseReference dbref;
    private String alert_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void handleResult(Result result) {
        setBarcode(result.getText());
        Log.i(LOG_TAG, "Barcode beolvasva: " + getBarcode());
        priceInput();
    }

    public void priceInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(qrscanner.this, R.style.AlertDialogTheme);
        builder.setTitle("Kérlek add meg a termék árát!");
        final EditText input = new EditText(qrscanner.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert_Text = input.getText().toString();
                Log.i(LOG_TAG, "Az ár: " + alert_Text);
                if (alert_Text.equals("") || alert_Text.equals("0") || alert_Text.isEmpty() || alert_Text == null || alert_Text.startsWith("0")) {
                    Log.i(LOG_TAG, "Termék ára nem lett beolvasva: " + alert_Text);
                    dialog.cancel();
                    onBackPressed();
                } else {
                    setM_Text(input.getText().toString());
                    Log.i(LOG_TAG, "Termék ára beolvasva: " + alert_Text);
                    getLastLocation();
                    onBackPressed();
                }
            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FunctionalityActivity.scantext.setText("sikertelen");
                onBackPressed();
            }
        });

        builder.show();
    }

    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
        Log.i(LOG_TAG, "onPause");
    }

    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }
}