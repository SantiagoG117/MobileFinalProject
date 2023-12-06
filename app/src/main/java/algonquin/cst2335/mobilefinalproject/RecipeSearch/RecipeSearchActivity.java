package algonquin.cst2335.mobilefinalproject.RecipeSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;


import algonquin.cst2335.mobilefinalproject.MainActivity;
import algonquin.cst2335.mobilefinalproject.R;
import algonquin.cst2335.mobilefinalproject.databinding.ActivityRecipeSearchBinding;


/**
 * Our "Main" Activity class for the app, the entry point from which we will search for recipes
 * and add them to our Cookbook (DB)
 * @author Jackson Coghill 041089141
 * @version 1.0
 */
public class RecipeSearchActivity extends AppCompatActivity {

    /**
     * Binds to the xml layout
     */
    private ActivityRecipeSearchBinding binding;
    /**
     * Shared prefs to save the last search entry
     */
    private SharedPreferences prefs;
    /**
     * Array to store the RecipeInfo in the DB
     */
    private ArrayList<RecipeInfo> recipeArray;

    /**
     * RecycleView to display search results on the main page
     */
    private RecyclerView.Adapter recipeAdapter;

    /**
     * Queue for API Volley
     */
    protected RequestQueue queue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Inflates layout for UI
         */
        binding = ActivityRecipeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * Initialize Volley queue for API calls
         */
        queue = Volley.newRequestQueue(this);
        recipeArray = new ArrayList<>();
        recipeRecyclerView();

        /**
         * Setup Toolbar at top of screen for navigation/information
         */
        setSupportActionBar(binding.recipeToolbar);
        /**
         * Prevent toolbar from displaying default "MobileProject" string/title
         */
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        /** Search button used to retrieve recipe results from Spoonacular / has saved searches from shared prefs
         *
         */
        binding.recipeSearchButton.setOnClickListener(click -> {
            saveSearchRecipes();
            loadSearchRecipes();
        });

        String searchKeyword = loadSearchPreferences();

        if (!searchKeyword.equals("")) {
            loadSearchRecipes();
        }
    }

    /**
     * Loads saved search result from SharedPreferences
     * @return saved search text
     */
    private String loadSearchPreferences() {
        prefs = getSharedPreferences("RecipeSearches", Context.MODE_PRIVATE);
        binding.recipeInput.setText(prefs.getString("SearchText", ""));

        return binding.recipeInput.getText().toString();
    }

    /**
     * Saves search word to SharedPrefs
     */
    private void saveSearchRecipes() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("SearchText", binding.recipeInput.getText().toString());
        editor.apply();
    }

    /**
     * Loads recipes based on user's search, pulls data through API call, request through Volley
     */
    private void loadSearchRecipes() {

            // I burned through a whole lot of my API quota trying to get things working well (enough) and looking good (ish)
            // At one stage, before I was unaware I'd hit the cap, I spent 30 minutes trying to debug why nothing
            // was appearing on screen before realizing I'd capped out.
            // Here's a spare API key in case it happens again: 5de7a79091b24bb7a4b8f3e23d09863e
            String apiKey = "9fb46fc20e564bc5bf308c2bfd6bf97d";
            String stringURL = "https://api.spoonacular.com/recipes/complexSearch?query="
                    + URLEncoder.encode(binding.recipeInput.getText().toString())
                    + "&apiKey="
                    + apiKey;

        Log.d("RecipeSearchActivity", "URL for API request: " + stringURL);

            JsonObjectRequest recipeRequest = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {

                        try {
                            recipeArray.clear();
                            JSONArray recipeResults = response.getJSONArray("results");

                            Log.d("RecipeSearchActivity", "Number of results from API: " + recipeResults.length());

                            for (int i = 0; i < recipeResults.length(); i++) {
                                JSONObject thisRecipe = recipeResults.getJSONObject(i);
                                int recipeID = thisRecipe.getInt("id");
                                String recipeImageURL = thisRecipe.getString("image");
                                String recipeTitle = thisRecipe.getString("title");

                                recipeArray.add(new RecipeInfo(recipeID, recipeTitle, recipeImageURL));

                            }
                            Log.d("RecipeSearchActivity", "Recipe array size: " + recipeArray.size());

                            recipeAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        error.printStackTrace();
                    });

            queue.add(recipeRequest);

       // });
    }

        /**
         * Sets up recyclerView to display data pulled from API call/loaded data,

         */
        private void recipeRecyclerView() {
            Log.d("RecipeSearchActivity", "Setting up RecyclerView");
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recipeRecycler.setLayoutManager(new LinearLayoutManager(this));
            binding.recipeRecycler.setAdapter(recipeAdapter = new RecyclerView.Adapter<RecipeSearchHolder>() {
                /**
                 * RecipeSearchHolder for RecyclerView of given types
                 * @param parent   The ViewGroup into which the new View will be added after it is bound to
                 *                 an adapter position.
                 * @param viewType The view type of the new View.
                 * @return SearchHolder of a given type
                 */
                @NonNull
                @Override
                public RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_recycler, parent, false);

                    Log.d("RecipeSearchActivity", "Creating ViewHolder");

                    return new RecipeSearchHolder(view);
                }

                /**
                 * Displays data in a certain position
                 * @param holder   The ViewHolder which should be updated to represent the contents of the
                 *                 item at the given position in the data set.
                 * @param position The position of the item within the adapter's data set.
                 */
                @Override
                public void onBindViewHolder(@NonNull RecipeSearchHolder holder, int position) {
                    RecipeInfo recipe = recipeArray.get(position);

                    Log.d("RecipeSearchActivity", "Binding data to ViewHolder at position: " + position);

                    holder.recipeTitle.setText("Title:\n" + recipe.getRecipeTitle());
                    holder.recipeID.setText("ID:\n" + String.valueOf(recipe.getRecipeID()));
                    Picasso.get().load(recipe.getRecipeImageURL()).into(holder.recipeImageURL);

                    if (holder.recipeImageURL !=null) {
                        Picasso.get().load(recipe.getRecipeImageURL()).into(holder.recipeImageURL);
                    }
                }

                /**
                 * Total number of recipes held in RecyclerView
                 * @return size of RecipeArray
                 */
                @Override
                public int getItemCount() {

                    Log.d("RecipeSearchActivity", "Number of items in RecyclerView: " + recipeArray.size());

                    return recipeArray.size();
                }
            });
        }

    /**
     * Options menu creation for navigation/information
     * @param menu The options menu in which you place your items.
     *
     * @return true for display of menu details/functionality
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate( R.menu.recipe_menu, menu);

        return true;
    }

    /**
     * Case switch to direct clicks on various menu items
     * @param item The menu item that was selected.
     *
     * @return true to end
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch( item.getItemId() )
        {
            case R.id.recipeSearchIcon:
                Intent recipeSearchIntent = new Intent(RecipeSearchActivity.this, RecipeSearchActivity.class);
                startActivity(recipeSearchIntent);
                break;

            case R.id.recipeCookbookIcon:
                Intent cookbookIntent = new Intent(RecipeSearchActivity.this, Cookbook.class);
                startActivity(cookbookIntent);
                break;

            case R.id.Exit:
                Intent recipeHomeIntent = new Intent(RecipeSearchActivity.this, MainActivity.class);
                startActivity(recipeHomeIntent);
                break;


            case R.id.aboutMenu:
                Toast.makeText(this, getString(R.string.aboutMenuString), Toast.LENGTH_LONG).show();


                break;

            case R.id.helpMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeSearchActivity.this);
                builder.setMessage(RecipeSearchActivity.this.getString(R.string.helpMenuString));
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return true;
    }


    /**
     * Represents individual recipe items in the ViewHolder
     */
    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        TextView recipeID;
        ImageView recipeImageURL;

        /**
         * Constructor for RecipeSearchHolder class
         * @param itemView single item in RecyclerView
         */
        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            /**
             * Retrieves recipe position, info of selected recipe, fragment showing recipe details,
             * bundles arguments
             */
            itemView.setOnClickListener(click -> {
                int position = getAbsoluteAdapterPosition();
                RecipeInfo selectedRecipe = recipeArray.get(position);
                RecipeFragment recipeFragment = new RecipeFragment(selectedRecipe);
                Bundle args = new Bundle();

                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            /**
             * Initialize Views in the layout with loaded recipe details
             */
            recipeTitle = itemView.findViewById(R.id.recipeTitleRecycler);
            recipeID = itemView.findViewById(R.id.recipeIDRecycler);
            recipeImageURL = itemView.findViewById(R.id.recipeImageRecycler);
        }
    }
}