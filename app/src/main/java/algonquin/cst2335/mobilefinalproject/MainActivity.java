package algonquin.cst2335.mobilefinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import algonquin.cst2335.mobilefinalproject.databinding.ActivityMainBinding;

/**
 * @Author Santiago Garcia
 * Main Activity for the Deezer application
 */
public class MainActivity extends AppCompatActivity {

    /*
    * Binding automatically loads all of the Widgets in the layout and assigns them to Java variables automatically.
    * The compiler will automatically declare the variables for us.
    *
    * Binding guarantees that the varibles we are using are part of the layout we have inflated.
    */
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.deezerButton.setOnClickListener(click -> {
            startActivity(new Intent(this, Deezer.class));
        });
    }
}