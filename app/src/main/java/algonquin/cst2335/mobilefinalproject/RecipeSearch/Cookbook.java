package algonquin.cst2335.mobilefinalproject.RecipeSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.MainActivity;
import algonquin.cst2335.mobilefinalproject.R;
import algonquin.cst2335.mobilefinalproject.databinding.ActivityCookbookBinding;

/**
 * This class serves as the representation of a Cookbook, or a place where the user can store all
 * of their favourite recipes. It accesses the database of saved recipes and displays them using a
 * recycler view. It also contains the code to use the toolbar for navigation and add'l information.
 *
 * @author Jackson Coghill 041089141
 * @version 1.0
 *
 */
public class Cookbook extends AppCompatActivity {

    /**
     * DB instance stores recipe data locally
     */
    private RecipeDB recipeDB;
    /**
     * Data Access Object interacts with DB
     */
    private RecipeDAO recipeDAO;
    /**
     * Access xml layout through bound object
     */
    private ActivityCookbookBinding binding;
    /**
     * Manages recycler view data
     */
    private RecyclerView.Adapter recipeAdapter;
    /**
     * Array stores list of RecipeInfo objs
     */
    private ArrayList<RecipeInfo> recipeArray;

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

        /**
         * Binding Cookbook xml and initializing ArrayListm, etc.
         */
        binding = ActivityCookbookBinding.inflate(getLayoutInflater());
        recipeArray = new ArrayList<>();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.recipeToolbar);
        /**
         * Removes the MobileProject text from the toolbar to replace it with individual app name
         */
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recipeRecyclerView();
        loadDB();
        loadRecipe();
    }

    /**
     * Initializes DB and DAO
     */
    private void loadDB() {
        recipeDB = Room.databaseBuilder(getApplicationContext(), RecipeDB.class, "RecipeDB").build();
        recipeDAO = recipeDB.recipeDAO();
    }

    /**
     * Loads recipes from DB, runs adapter.notify on UI thread -background operations updated to main thread
     */
    private void loadRecipe() {
        Executor recipeThread = Executors.newSingleThreadExecutor();
        recipeThread.execute(() -> {
            recipeArray.addAll(recipeDAO.getAllSavedRecipes());

            runOnUiThread(() -> recipeAdapter.notifyDataSetChanged());
        });
    }

    /**
     * Initialize recycler view, sets up adapter for display
     */
    private void recipeRecyclerView() {
        binding.cookbookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cookbookRecyclerView.setAdapter(recipeAdapter = new RecyclerView.Adapter<Cookbook.RecipeSearchHolder>() {
            @NonNull
            @Override
            public Cookbook.RecipeSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_recycler, parent, false);

                return new Cookbook.RecipeSearchHolder(view);
            }

            /**
             * Displays recipe data, sets Title, Image, ID to corresponding Cookbook Image/TextViews
             * @param holder   The ViewHolder which should be updated to represent the contents of the
             *                 item at the given position in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull Cookbook.RecipeSearchHolder holder, int position) {
                RecipeInfo recipeInfo = recipeArray.get(position);

                holder.recipeTitle.setText(recipeInfo.getRecipeTitle());
                Picasso.get().load(recipeInfo.getRecipeImageURL()).into(holder.recipeImage);
                holder.recipeID.setText("ID: " + String.valueOf(recipeInfo.getRecipeID()));

            }

            /**
             * Counts number of items in data set for display
             * @return size of Array
             */
            @Override
            public int getItemCount() {
                return recipeArray.size();
            }

            /**
             * Get item view type at specific position
             * @param position position to query
             * @return 0, only type of view in recycler
             */
            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });
    }

    /**
     * Handles menu items
     * @param menu The options menu in which you place your items.
     *
     * @return true/false for handling of menu items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate( R.menu.recipe_menu, menu);

        return true;
    }

    /**
     * Switch cases for menu item handling
     * @param item The menu item that was selected.
     *
     * @return true if menu item handled
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch( item.getItemId() )
        {
            case R.id.recipeSearchIcon:
                Intent recipeSearchIntent = new Intent(Cookbook.this, RecipeSearchActivity.class);
                startActivity(recipeSearchIntent);
                break;

            case R.id.recipeCookbookIcon:
                Intent cookbookIntent = new Intent(Cookbook.this, Cookbook.class);
                startActivity(cookbookIntent);
                break;

            case R.id.Exit:
                Intent recipeHomeIntent = new Intent(Cookbook.this, MainActivity.class);
                startActivity(recipeHomeIntent);
                break;


            case R.id.aboutMenu:
                Toast.makeText(this, getString(R.string.aboutMenuString), Toast.LENGTH_LONG).show();


                break;

            case R.id.helpMenu:
                AlertDialog.Builder builder = new AlertDialog.Builder(Cookbook.this);
                builder.setMessage(Cookbook.this.getString(R.string.helpMenuString));
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return true;
    }

    /**
     *  ViewHolder, displays various recipe values of Title, ID, Image
     */
    class RecipeSearchHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        TextView recipeID;
        ImageView recipeImage;

        /**
         * ViewHolder class' constructor
         * @param itemView represents each recipe value in recycler
         */
        public RecipeSearchHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                int position = getAbsoluteAdapterPosition();
                RecipeInfo selectedRecipe = recipeArray.get(position);
                RecipeFragment recipeFragment = new RecipeFragment(selectedRecipe);
                Bundle args = new Bundle();

                args.putString("CallingActivity", "RecipeSearchActivity");
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.recipeFragmentLocation, recipeFragment).commit();
            });

            /**
             * Updates the values of the Image/TextViews
             */
            recipeTitle = itemView.findViewById(R.id.recipeTitleRecycler);
            recipeImage = itemView.findViewById(R.id.recipeImageRecycler);
            recipeID = itemView.findViewById(R.id.recipeIDRecycler);
        }
    }

}