package algonquin.cst2335.mobilefinalproject;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//import algonquin.cst2335.mobilefinalproject.databinding.ActivityMainBinding;
//
//public class MainActivity extends AppCompatActivity {
//
//    ActivityMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState); //Calls parent onCreate()
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView( binding.getRoot());
//
//    }
//}

import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;

import algonquin.cst2335.mobilefinalproject.RecipeSearch.RecipeSearchActivity;
import algonquin.cst2335.mobilefinalproject.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    /*
     * Binding automatically loads all of the Widgets in the layout and assigns them to Java variables automatically.
     * The compiler will automatically declare the variables for us.
     *
     * Binding guarantees that the variables we are using are part of the layout we have inflated.
     */
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recipeButton.setOnClickListener(click -> {
            startActivity(new Intent(this, RecipeSearchActivity.class));
        });
    }
}