package com.example.szakdoga;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemList extends FunctionalityActivity {

    private static final String LOG_TAG = ItemList.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<Items> mItemList;
    private ArrayList<Items> mItemList2;
    private ItemAdapter mAdapter;
    private FirebaseFirestore db;

    private static boolean isLocal = true;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adatok lekérdezése...");
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.i(LOG_TAG, "Authenticated user!");
        } else {
            Log.i(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mItemList = new ArrayList<>();
        mAdapter = new ItemAdapter(ItemList.this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        EventChangeListener();


    }


    private void EventChangeListener() {


        db.collection("Items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.i(LOG_TAG, "onEvent: " + error.getMessage());
                            return;
                        }

                        for (DocumentChange document : value.getDocumentChanges()) {
                             if (document.getType() == DocumentChange.Type.ADDED) {
                                Log.i(LOG_TAG, "Barcodeok: " + barcode + " és " + document.getDocument().get("barcode"));
                                if (document.getDocument().get("barcode").toString().equals(barcode)) {
                                    if (isLocal) {
                                        if (document.getDocument().get("city").toString().equals(city)) {
                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                            String ido = format1.format(document.getDocument().getDate("date")).toString();
                                            Items it = new Items(document.getDocument().get("price").toString() + " Ft", document.getDocument().get("city").toString(), document.getDocument().get("street").toString(), ido, (Boolean) document.getDocument().get("reported"));
                                            mItemList.add(it);
                                            Log.i(LOG_TAG, "Lokálisan hozzáadva");
                                        }
                                    } else {
                                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                        String ido = format1.format(document.getDocument().getDate("date")).toString();
                                        Items it = new Items(document.getDocument().get("price").toString() + " Ft", document.getDocument().get("city").toString(), document.getDocument().get("street").toString(), ido, (Boolean) document.getDocument().get("reported"));
                                        mItemList.add(it);
                                        Log.i(LOG_TAG, "Nem lokálisan hozzáadva");
                                    }
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.item_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.area_selector:
                if (isLocal) {
                    isLocal = !isLocal;
                    recreate();
                    item.setIcon(R.drawable.all);
                } else {
                    isLocal = !isLocal;
                    item.setIcon(R.drawable.home);
                    recreate();
                }
                Log.i(LOG_TAG, "menu: area selector");
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

}