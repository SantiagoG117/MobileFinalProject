package algonquin.cst2335.mobilefinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import algonquin.cst2335.mobilefinalproject.Deezer.Deezer;
import algonquin.cst2335.mobilefinalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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

        binding.deezerButton.setOnClickListener(click -> {
            startActivity(new Intent(this, Deezer.class));
        });
    }


}