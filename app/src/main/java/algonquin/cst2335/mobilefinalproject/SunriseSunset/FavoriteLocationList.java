package algonquin.cst2335.mobilefinalproject.SunriseSunset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.R;
import algonquin.cst2335.mobilefinalproject.data.FavoriteLocation;
import algonquin.cst2335.mobilefinalproject.databinding.FavoriteLocationListBinding;

//import algonquin.cst2335.SunriseSunset.databinding.FavoriteLocationListBinding;
//import algonquin.cst2335.data.FavoriteLocation;

/**
 * This class allows the list of favourite locations to be
 * viewed and manipulated
 *
 * @author Julianna Hawkins
 * @version 1.0
 */
public class FavoriteLocationList extends AppCompatActivity {

    /**
     * Binding for favorites list
     */
    private FavoriteLocationListBinding favoriteLocationListBinding;
    /**
     * DAO to communicate with the database
     */
    private FavoriteLocationDAO DAO;
    /**
     * Adapter to display favourites in a recycler view
     */
    private FavoriteLocationAdapter adapter;
    /**
     * Arraylist to store favourite locations once saved
     */
    private final ArrayList<FavoriteLocation> favorite_locations = new ArrayList<>();

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteLocationListBinding = FavoriteLocationListBinding.inflate(getLayoutInflater());
        setContentView(favoriteLocationListBinding.getRoot());

        LocationDatabase DB = Room.databaseBuilder(getApplicationContext(), LocationDatabase.class, "locationData")
                .build();
        DAO = DB.favorite_Location_DAO();

        //recycler view
        favoriteLocationListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteLocationAdapter();

        favoriteLocationListBinding.recyclerView.setAdapter(adapter);


        Button backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(click -> startActivity(new Intent(this, SunMainActivity.class)));

        retrieveDataFromIntent();

        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            List<FavoriteLocation> allLocations = DAO.getAllFavoriteLocations();
            runOnUiThread(() -> favoriteAdapterUpdateUI(allLocations));
        });
    }

    /**
     * Retrieves data and inserts it into favourite locations
     */
    private void retrieveDataFromIntent() {
        String timezone = getIntent().getStringExtra("timezone");
        String sunrise = getIntent().getStringExtra("sunrise");
        String sunset = getIntent().getStringExtra("sunset");
        String longitude = getIntent().getStringExtra("longitudeInput");
        String latitude = getIntent().getStringExtra("latitudeInput");

        FavoriteLocation favsOrder = new FavoriteLocation(latitude, longitude, timezone, sunrise, sunset);

        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            if (favsOrder.getTimezone() != null) {
                DAO.insertFavoriteLocation(favsOrder);
            }

            List<FavoriteLocation> allLocations = DAO.getAllFavoriteLocations();
            runOnUiThread(() -> {
                favoriteAdapterUpdateUI(allLocations);
            });
        });
    }

    /**
     * Updatse the UI of the favorite location list adapter
     *
     * @param allLocations List of all favorite locations retrieved from the database
     */
    private void favoriteAdapterUpdateUI(List<FavoriteLocation> allLocations) {
        favorite_locations.clear();
        favorite_locations.addAll(allLocations);
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView adapter displays whole of favorite location array
     */
    class FavoriteLocationAdapter extends RecyclerView.Adapter<MyRowHolder> {

        /**
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return
         */
        @Override
        public MyRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.favorite_location_elements, parent, false);
            return new MyRowHolder(itemView);
        }

        /**
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(MyRowHolder holder, int position) {
            SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yyyy hh-mm-ss");
            String current_date_and_time = date_format.format(new Date());

            FavoriteLocation favorite_location_position = favorite_locations.get(position);

            holder.sunrise_favorite_output.setText(getString(R.string.sunrise_sentence) + " " + favorite_location_position.getSunrise());
            holder.sunset_favorite_output.setText(getString(R.string.sunset_sentence) + " " + favorite_location_position.getSunset());
            holder.timezone_favorite_output.setText(getString(R.string.time_zone_sentence) + " " + favorite_location_position.getTimezone());
            holder.timeStamp_favorite_output.setText(getString(R.string.date_time_sentence) + " " + current_date_and_time);

            holder.itemView.setOnClickListener(click -> updateDelete(position));
        }

        /**
         *
         * @return the size of favourite locations array
         */
        @Override
        public int getItemCount() {
            return favorite_locations.size();
        }
    }

    /**
     * ViewHolder for the RecyclerView object
     */
    class MyRowHolder extends RecyclerView.ViewHolder {

        /**
         * TextView displaying the sunrise time of a specified favorite location
         */
        public TextView sunrise_favorite_output;
        /**
         * TextView displaying the sunset time of a specified favorite location
         */
        public TextView sunset_favorite_output;
        /**
         * TextView displaying the timezone of a specified favorite location
         */
        public TextView timezone_favorite_output;
        /**
         * TextView displaying date and time of a specified favorite location
         */
        public TextView timeStamp_favorite_output;

        /**
         * Creates new MyRowHolder object
         *
         * @param itemView
         */
        public MyRowHolder(View itemView) {
            super(itemView);
            sunrise_favorite_output = itemView.findViewById(R.id.sunrise_for_list);
            sunset_favorite_output = itemView.findViewById(R.id.sunset_for_list);
            timezone_favorite_output= itemView.findViewById(R.id.timezone_for_list);
            timeStamp_favorite_output = itemView.findViewById(R.id.current_date_time_for_list);
        }
    }

//    AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this);
//                    builder.setMessage("Do you want to delete the message: " + messageText.getText())
//                            .setTitle("Question: ")
//    // Clicking on No shouldn't delete anything, so we leave the lambda function empty
//                            .setNegativeButton("No", (dialog, cl) -> {})
//            //Clicking on yes should remove the message that the row delete it from the database and update the adapter
//            //that something has been removed so the RecyclerView can update itself
//            .setPositiveButton("Yes", ((dialog, c) -> {
//        Executor thread = Executors.newSingleThreadExecutor();
//        ChatMessage removedMessage = messages.get(position);
//        thread.execute(() -> {
//            mDAO.deleteMessage(removedMessage);
//        });
//
//        messages.remove(position);
//        myAdapter.notifyItemRemoved(position);
//        Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
//                .setAction("Undo", cl -> {
//                    messages.add(position, removedMessage);
//                    myAdapter.notifyItemInserted(position);
//                })
//                .show();
//    }))
//            .create().show();
//
//}*/;

    /**
     * Display an alert dialog for delete or update whe favourite location is clicked on
     *
     * @param position Position of the clicked item in the list
     */
    private void updateDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_title);
        builder.setMessage(R.string.alert_sentence);

        builder.setPositiveButton(R.string.delete_option, (dialog, which) -> {
            delete(position);
        });

        builder.setNegativeButton(R.string.update_option, (dialog, which) -> {
            update(position);
            Toast.makeText(this, R.string.update_sentence, Toast.LENGTH_SHORT).show();
            delete(position);
        });

        builder.show();
    }

    /**
     * Delete favorite location from array list
     *
     * @param position Position of item to be deleted
     */
    private void delete(int position) {
        FavoriteLocation deletedLocation = favorite_locations.get(position);
        favorite_locations.remove(position);
        adapter.notifyItemRemoved(position);

        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> DAO.deleteFavoriteLocation(deletedLocation));

        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, getResources().getString(R.string.delete_sentence), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.undo), btn -> {
                    thread.execute(() -> {
                        DAO.insertFavoriteLocation(deletedLocation);
                    });

                    favorite_locations.add(position, deletedLocation);
                    adapter.notifyItemInserted(position);
                })
                //builder.show();
                .show();
    }

    /**
     * Updates a favorite location
     *
     * @param position Position of the item to be updated
     */
    private void update(int position) {
        FavoriteLocation click_location = favorite_locations.get(position);
        String latitude = click_location.getLatitude();
        String longitude = click_location.getLongitude();

        Intent update_location = new Intent(this, LocationActivity.class);

        update_location.putExtra("latitude", latitude);
        update_location.putExtra("longitude", longitude);
        startActivity(update_location);
    }
}//end of class