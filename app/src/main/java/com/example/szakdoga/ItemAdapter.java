package com.example.szakdoga;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<Items> mItemsData;
    private Context mContext;
    private static final String LOG_TAG = FunctionalityActivity.class.getName();
    private FirebaseFirestore db;


    ItemAdapter(Context context, ArrayList<Items> itemsData) {
        this.mItemsData = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {

        Items currentItem = mItemsData.get(position);

        holder.mPriceText.setText(currentItem.getPrice());
        holder.mPlaceText.setText(currentItem.getCity() + ", " + currentItem.getStreet());
        holder.mDateText.setText(currentItem.getDate2());
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "Az item: " + FunctionalityActivity.barcode + ", " + currentItem.getStreet() + ", " + currentItem.getPrice());
                Log.i(LOG_TAG, "Vágott: " + currentItem.getPrice().substring(0, currentItem.getPrice().length()-3));
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.mButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.recycle_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                db.collection("Items")
                                        .whereEqualTo("barcode",FunctionalityActivity.barcode )
                                        .whereEqualTo("street", currentItem.getStreet())
                                        .whereEqualTo("price", currentItem.getPrice().substring(0, currentItem.getPrice().length()-3))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i(LOG_TAG, "Jelenteni valók száma: " + task.getResult().size());
                                                    Log.i(LOG_TAG, "Tartalom: " + task.getResult());
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().update("reported", true);
                                                    }
                                                } else {
                                                    Log.i(LOG_TAG, "Nem sikerült a lekérdezés");
                                                }
                                            }
                                        });
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        if (currentItem.isReported()) {
            holder.mImage.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "Van warning");
        } else {
            holder.mImage.setVisibility(View.INVISIBLE);
            Log.i(LOG_TAG, "Nincs warning");
        }

    }

    @Override
    public int getItemCount() {
        return mItemsData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPriceText;
        private TextView mPlaceText;
        private TextView mDateText;
        private TextView mButton;
        private ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String hely = "geo:0,0?q=" +mItemsData.get(pos).getCity() + ", " + mItemsData.get(pos).getStreet();

                    Log.i(LOG_TAG, "A hely: " + hely);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(hely));
                    mContext.startActivity(intent);
                }
            });

            mPriceText = itemView.findViewById(R.id.priceTitle);
            mPlaceText = itemView.findViewById(R.id.placeTitle);
            mDateText = itemView.findViewById(R.id.dateTitle);
            mButton = (TextView) itemView.findViewById(R.id.textViewOptions);
            mImage = itemView.findViewById(R.id.imageViewW);

        }

        public void bindTo(Items currentItem) {

            mPriceText.setText(currentItem.getPrice());
            mPlaceText.setText(currentItem.getCity());
            mDateText.setText(currentItem.getStreet());
        }
    }


}


