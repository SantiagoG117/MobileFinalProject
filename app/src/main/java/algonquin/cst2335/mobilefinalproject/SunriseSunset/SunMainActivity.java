package algonquin.cst2335.mobilefinalproject.SunriseSunset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import algonquin.cst2335.SunriseSunset.databinding.ActivityMainBinding;
import algonquin.cst2335.mobilefinalproject.R;
import algonquin.cst2335.mobilefinalproject.databinding.SunActivityMainBinding;

/**
 * This class is the main activity of the app
 * It handles user interaction and interacts with other classes
 * to provide information and populate the database
 * @author Julianna Hawkins
 * @version 1.0
 */
public class SunMainActivity extends AppCompatActivity {

    /**
     * Binding main activity
     */
    private SunActivityMainBinding sunMainActivityBinding;

    /**
     * stores user input latitude
     */
    String latitude_input;

    /**
     * stores user input longitude
     */
    String longitude_input;

    /**
     * file name for SharedPreferneces
     */
    private static final String PREFS_NAME = "MyPrefsFile";

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

        SunActivityMainBinding mainActivityBinding = SunActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        setSupportActionBar(mainActivityBinding.myToolbar);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); //makes numbers stay after closing
        mainActivityBinding.latitudeInput.setText(preferences.getString("latitude", ""));
        mainActivityBinding.longitudeInput.setText(preferences.getString("longitude", ""));

        mainActivityBinding.lookupButton.setOnClickListener(view -> {
            latitude_input = mainActivityBinding.latitudeInput.getText().toString();
            longitude_input = mainActivityBinding.longitudeInput.getText().toString();

            if (!latitude_input.isEmpty() && !longitude_input.isEmpty()) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("latitude", latitude_input);
                editor.putString("longitude", longitude_input);

                editor.apply();

                Intent goToLocation = new Intent(this, LocationActivity.class);
                goToLocation.putExtra("latitude", latitude_input);
                goToLocation.putExtra("longitude", longitude_input);
                startActivity(goToLocation);

            }
            else {
                Toast.makeText(this, R.string.latitude_longitude_isEmpty, Toast.LENGTH_LONG).show();
            }
        });

        mainActivityBinding.viewFavoriteLocationsButton.setOnClickListener(view -> {
            Intent navigateToFavoriteLocations = new Intent(this, FavoriteLocationList.class);
            startActivity(navigateToFavoriteLocations);
        });
    }

    /**
     *
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }//end of OnCreateOptionsMenu

    /**
     * adds functionality to the buttons on the toolbar
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                androidx.appcompat.app.AlertDialog.Builder aboutBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
                aboutBuilder.setMessage("Provide latitude and longitude coordinates \n" +
                                "Click 'lookup' so see sunrise and sunset times \n" +
                                "Use the 'save' button to save a location to favourites \n" +
                                "Use the 'show favourites' button to show your favourite locations \n" +
                                "Happy searching!")
                        .setTitle("About Lookup")
                        .setNegativeButton("OK", (dialog, which) -> {})
                        .show();
                return true;

            case R.id.menu_clear:
                sunMainActivityBinding.latitudeInput.setText("");
                sunMainActivityBinding.longitudeInput.setText("");

//                latitudeInput.remove(position);
//                longitudeInput.remove(position);
//                myAdapter.notifyItemRemoved(position);
//                Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
//                        .setAction("Undo", cl -> {
//                            messages.add(position, removedMessage);
//                            myAdapter.notifyItemInserted(position);

                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                //clear textViews
                editor.remove("latitude");
                editor.remove("longitude");
                editor.apply();
                Toast.makeText(this, R.string.clear_message, Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }//end of switch
    }//end of options selected item

}//end of class