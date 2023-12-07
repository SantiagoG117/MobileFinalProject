package algonquin.cst2335.mobilefinalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import algonquin.cst2335.mobilefinalproject.databinding.ActivityMainBinding;

/**
 * The main activity class for the mobile final project.
 * Represents the entry point of the application and handles the creation of the main user interface.
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    /**
     * Called when the activity is starting. This is where most initialization should go.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the click listener for the dictionaryButton
        binding.dictionaryButton.setOnClickListener(click -> {
            // Start the Dictionary activity when the button is clicked
            startActivity(new Intent(this, Dictionary.class));
        });
    }
}