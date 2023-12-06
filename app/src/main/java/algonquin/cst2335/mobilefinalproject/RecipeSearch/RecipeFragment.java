package algonquin.cst2335.mobilefinalproject.RecipeSearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import androidx.room.Room;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.RecipeFragmentLayoutBinding;

import com.google.android.material.snackbar.Snackbar;

/**
 * A fragment overlay representing one recipe's data, can be accessed through RecipeSearch or Cookbook
 * Loads recipes through local DB or API.
 * Functionality for saving/deleting recipes to/from database
 *
 * @author Jackson Coghill 041089141
 * @version 1.0
 */
public class RecipeFragment extends Fragment {

    /**
     * DB instance of recipe data in fragment
     */
    private RecipeDB recipeDB;
    /**
     * DAO interacts with DB/info
     */
    private RecipeDAO recipeDAO;
    /**
     * Distinct recipe to be admired/saved/deleted
     */
    private RecipeInfo selectedRecipe;
    /**
     * Volley queue, handles API requests
     */
    private RequestQueue queue;
    /**
     * Access xml layout of Fragment
     */
    private RecipeFragmentLayoutBinding binding;

    /**
     * Specific recipe details to be shown in Fragment
     * @param recipe to be displayed/CRUDed
     */
    public RecipeFragment(RecipeInfo recipe){
        selectedRecipe = recipe;
    }

    /**
     * Loads fragment upon creation
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the View (or Null) for the Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d("RecipeFragment", "onCreateView");

        binding = RecipeFragmentLayoutBinding.inflate(inflater);
        queue = Volley.newRequestQueue(requireContext());

        /**
         * Allows toolbar to be displayed/accessed alongside Fragment, (false) statement to remove
         * MobileProject header from toolbar
         */
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.recipeToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


        /**
         * Loads local DB
         */
        loadDB();
        /**
         * Loads recipe details from DB or from API
         */
        loadRecipe();

        /**
         * Binds functionality to Save/Delete buttons displayed on Fragment
         */
        binding.saveRecipe.setOnClickListener(view -> saveRecipe());
        binding.deleteRecipe.setOnClickListener(view -> deleteRecipe());

        return binding.getRoot();
    }

        /**
         *  Initializes local DB, builds the RecipeDB Room
         *  Log was very helpful in troubleshooting errors, so I've left it in
         */
        private void loadDB() {
            Log.d("RecipeFragment", "loadDB");

            recipeDB = Room.databaseBuilder(requireContext().getApplicationContext(), RecipeDB.class, "RecipeDB").build();
            recipeDAO = recipeDB.recipeDAO();
        }

        /**
         * Loads recipe data from Room or from API
         */
        private void loadRecipe() {
                Log.d("RecipeFragment", "loadRecipe");

                Executor recipeThread = Executors.newSingleThreadExecutor();
                recipeThread.execute(() -> {
                    RecipeInfo recipe = recipeDAO.getRecipeByID(selectedRecipe.getRecipeID());

                    if (recipe !=null) {
                        loadFromDB(recipe);
                    } else {
                        loadFromURL();
                    }
                });
            }

        /**
         * Gets data from Room DB, sets recipe details to fragment's Image/TextViews
         * @param recipe recipe details
         */
        private void loadFromDB(RecipeInfo recipe) {
                    Log.d("RecipeFragment", "loadFromDB");

                    requireActivity().runOnUiThread(() -> {
                        binding.recipeFragTitle.setText(recipe.getRecipeTitle());
                        binding.recipeFragTitle.setVisibility(View.VISIBLE);

                        /**
                         * Picasso made loading the Image from ImageURL super easy
                         */
                        Picasso.get().load(recipe.getRecipeImageURL()).into(binding.recipeFragImage);
                        binding.recipeFragImage.setVisibility(View.VISIBLE);

                        binding.recipeFragURL.setText(recipe.getRecipeURL());
                        selectedRecipe.setRecipeURL(recipe.getRecipeURL());
                        binding.recipeFragTitle.setVisibility(View.VISIBLE);

                        binding.recipeFragSummary.setText(recipe.getRecipeSummary());
                        selectedRecipe.setRecipeSummary(recipe.getRecipeSummary());
                        binding.recipeFragSummary.setVisibility(View.VISIBLE);

                    });
                }

        /**
         * Loads recipe data from external API call using Volley.
         * Displays retrieved data in the frag
         */
        private void loadFromURL() {
                    Log.d("RecipeFragment", "loadFromURL");

                    // I burned through a whole lot of my API quota trying to get things working well(enough) and looking good(ish)
                    // At one stage, before I was unaware I'd hit the cap, I spent 30 minutes trying to debug why nothing
                    // was appearing on screen before realizing I'd capped out.
                    // Here's a spare API key in case it happens again: 5de7a79091b24bb7a4b8f3e23d09863e
                    String apiKey = "9fb46fc20e564bc5bf308c2bfd6bf97d";
                    String url = "https://api.spoonacular.com/recipes/"
                            + selectedRecipe.getRecipeID()
                            + "/information?apiKey="
                            + apiKey;

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            (response) -> {
                                try {
                                    String recipeTitle = response.getString("title");
                                    String recipeImage = response.getString("image");
                                    String recipeSummary = response.getString("summary");
                                    String recipeSourceURL = response.getString("sourceUrl");

                                    requireActivity().runOnUiThread(() -> {
                                        binding.recipeFragTitle.setText(recipeTitle);
                                        binding.recipeFragTitle.setVisibility(View.VISIBLE);

                                        Picasso.get().load(recipeImage).into(binding.recipeFragImage);
                                        binding.recipeFragImage.setVisibility(View.VISIBLE);

                                        binding.recipeFragURL.setText(recipeSourceURL);
                                        selectedRecipe.setRecipeURL(recipeSourceURL);
                                        binding.recipeFragTitle.setVisibility(View.VISIBLE);

                                        binding.recipeFragSummary.setText(HtmlCompat.fromHtml(recipeSummary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                                        selectedRecipe.setRecipeSummary(recipeSummary);
                                        binding.recipeFragSummary.setVisibility(View.VISIBLE);
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                             },
                            error -> error.printStackTrace());
                    queue.add(request);
            }

        /**
         * Saves selected recipe to local DB, first checks if it already exists in DB, and returns a Snackbar saying
         * if recipe was added or not
         */
        private void saveRecipe() {
                Executor recipeThread = Executors.newSingleThreadExecutor();
                recipeThread.execute(() -> {
                    RecipeInfo existingRecipe = recipeDAO.getRecipeByID(selectedRecipe.getRecipeID());
                    if (existingRecipe == null) {
                        recipeDAO.insertRecipe(selectedRecipe);
                        requireActivity().runOnUiThread(() -> {
                            Snackbar.make(requireView(), "Recipe saved to Cookbook", Snackbar.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Snackbar.make(requireView(), "Recipe already in Cookbook", Snackbar.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            // Tried to get the delete to update in the Cookbook immediately. Tried a few different ways (recipeArray.remove and
            // recipeAdapter.notifyDataSetChanged) in the runOnUiThread, but they were all causing it to crash. If I had more time, I'd fix this.
            /**
             * Deletes selected recipe from local DB, checks if recipe exists in DB first, returns a Snack
             * saying what actions were taken either way.
             */
            private void deleteRecipe() {
                Executor recipeThread = Executors.newSingleThreadExecutor();
                recipeThread.execute(() -> {
                    RecipeInfo existingRecipe = recipeDAO.getRecipeByID(selectedRecipe.getRecipeID());
                    if (existingRecipe != null) {
                    recipeDAO.deleteRecipe(selectedRecipe);
                    requireActivity().runOnUiThread(() -> {
                        Snackbar.make(requireView(), "Recipe deleted from Cookbook", Snackbar.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Snackbar.make(requireView(), "Recipe not found in Cookbook to delete", Snackbar.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        }


