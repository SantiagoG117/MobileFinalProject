package algonquin.cst2335.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import algonquin.cst2335.mobileproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Calls parent onCreate()

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());

    }
}