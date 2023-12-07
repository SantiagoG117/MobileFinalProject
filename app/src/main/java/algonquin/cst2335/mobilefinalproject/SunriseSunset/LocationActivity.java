package algonquin.cst2335.mobilefinalproject.SunriseSunset;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import algonquin.cst2335.mobilefinalproject.R;

/**
 * This class displays information on the sunrise, sunset, time
 * zone based on user input latitude and longitude
 *
 * @author Julianna Hawkins
 * @version 1.0
 */
public class LocationActivity extends AppCompatActivity {


    /**
     * TextView displaying sunrise info
     */
    private TextView sunriseInfo;
    /**
     * TextView displaying sunset info
     */
    private TextView sunsetInfo;
    /**
     * Latitude of the location
     */
    private String latitude;
    /**
     * Longitude of the location
     */
    private String longitude;
    /**
     * Button for saving location to favorites array
     */
    /**
     * TextView displaying time zone info
     */
    private TextView timeZoneInfo;
    private Button saveButton;
    /**
     * Button for returning to main screen of sunrise app
     */
    private Button backToMain;

    /**
     * This class initiates the app
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location_output);

        sunriseInfo = findViewById(R.id.sunrise_output);
        sunsetInfo = findViewById(R.id.sunset_output);
        saveButton = findViewById(R.id.save_location_button);
        timeZoneInfo = findViewById(R.id.time_zone_output);
        backToMain = findViewById(R.id.back_button);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new riseSetTask(latitude, longitude, requestQueue);
        }//end of if

        saveButton.setOnClickListener(click -> {
            String timeZoneOutputFavorite = timeZoneInfo.getText().toString();
            String sunriseOutputFavorite = sunriseInfo.getText().toString();
            String sunsetOutputFavorite = sunsetInfo.getText().toString();

            Intent sentToFavorite = new Intent(this, FavoriteLocationList.class);

            sentToFavorite.putExtra("latitudeInput", latitude);
            sentToFavorite.putExtra("longitudeInput", longitude);
            sentToFavorite.putExtra("timezone", timeZoneOutputFavorite);
            sentToFavorite.putExtra("sunrise", sunriseOutputFavorite);
            sentToFavorite.putExtra("sunset", sunsetOutputFavorite);

            startActivity(sentToFavorite);
            Toast.makeText(this, R.string.save_location_sentence, Toast.LENGTH_LONG).show();
        });

        backToMain.setOnClickListener(click -> {
            Intent backToMain = new Intent(this, SunMainActivity.class);
            startActivity(backToMain);
        });
    }

    /**
     * inner class that executes json request via volley
     */
    public class riseSetTask {

        /**
         * latitude to use for search
         */
        private final String latitude;
        /**
         * longitude to use for search
         */
        private final String longitude;
        /**
         * used to make the network/HTTP request
         */
        private final RequestQueue requestQueue;

        /**
         * Parameterized constructor
         *
         * @param latitude latitude of the location
         * @param longitude longitude of the location
         * @param requestQueue used for making network requests
         */
        public riseSetTask(String latitude, String longitude, RequestQueue requestQueue) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.requestQueue = requestQueue;

            execute();
        }

        /**
         * Executes task
         */
        private void execute() {
            String urlBuilder = buildUrl(Double.parseDouble(latitude), Double.parseDouble(longitude));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    urlBuilder,
                    null,
                    response -> {
                        try {
                            handleResult(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> error.printStackTrace()
            );

            requestQueue.add(jsonObjectRequest);
        }//end of execute

        /**
         * Creates URL needed for HTTP request
         *
         * @param latitude user provided latitude
         * @param longitude user provided longitude
         * @return URL containing latitude and longitude
         */
        @NonNull
        private String buildUrl(double latitude, double longitude) {
            return String.format("https://api.sunrisesunset.io/json?lat=%f&lng=%f", latitude, longitude);
        }

        /**
         * @param result JSON string representing the API response that contains location info
         * @throws JSONException is to display if there is an issue parsing the JSON result
         */
        private void handleResult(String result) throws JSONException {
            if (result != null) {
                JSONObject jsonResponse = new JSONObject(result);
                JSONObject results = jsonResponse.getJSONObject("results");

                // Update UI components with the extracted information
                runOnUiThread(() -> {
                    timeZoneInfo.setText(results.optString("timezone"));
                    sunriseInfo.setText(results.optString("sunrise"));
                    sunsetInfo.setText(results.optString("sunset"));
                });
            } else {
                // Display an error message if the result is null
                runOnUiThread(() ->
                        Toast.makeText(LocationActivity.this, R.string.error_message, Toast.LENGTH_LONG).show()
                );
            }//end of else
        }//end of handle result
    }//end of rise set task
}//end of location activity