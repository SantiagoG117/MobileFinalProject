package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.DictionaryMenuBinding;
import algonquin.cst2335.mobilefinalproject.databinding.FragmentDefinitionBinding;

public class Dictionary extends AppCompatActivity {
    private RecyclerView.Adapter<MyRowHolder> myAdapter;
    private static ArrayList<DictionaryItem> words = new ArrayList<>();
    private static DictionaryViewModel dictionaryModel;
    private DictionaryItemDAO dDAO;
    private Executor thread = Executors.newSingleThreadExecutor();
    private DictionaryMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DictionaryMenuBinding.inflate(getLayoutInflater());  // Inflating binding
        setContentView(binding.getRoot());

        DictionaryDatabase db = Room.databaseBuilder(getApplicationContext(), DictionaryDatabase.class, "dictionary").build();
        dDAO = db.dictionaryItemDAO();

        RecyclerView recyclerView = binding.recyclerView;  // Updated reference
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dictionaryModel = new ViewModelProvider(this).get(DictionaryViewModel.class);

        binding.searchButton.setOnClickListener(click -> {
            String word = binding.searchEditText.getText().toString();

            // Create a DictionaryItem object for a word
            DictionaryItem displayWord = new DictionaryItem(word, new ArrayList<>());

            words.add(displayWord);

            myAdapter.notifyItemInserted(words.size() - 1);
            binding.searchEditText.setText("");

            makeApiRequest(word, displayWord);

            thread.execute(() -> {
                long wordId = dDAO.insertItem(displayWord);
                displayWord.id = (int) wordId;
                Log.d("TAG", "The id created is: " + displayWord.id);
            });
        });

        recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                FragmentDefinitionBinding binding = FragmentDefinitionBinding.inflate(getLayoutInflater());
                return new MyRowHolder(binding.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                DictionaryItem dictionary = words.get(position);
                holder.wordTextView.setText(dictionary.getWord());
                holder.definitionTextView.setText(TextUtils.join("\n", dictionary.getDefinition()));
            }

            @Override
            public int getItemCount() {
                return words.size();
            }
        });

        if (words.isEmpty()) {
            dictionaryModel.words.setValue(words);

            thread.execute(() -> {
                words.addAll(dDAO.getAllWords()); // When you get the data from the database
                runOnUiThread(() -> recyclerView.setAdapter(myAdapter)); // Loads the RecyclerView
            });
        }

        dictionaryModel.selectedWord.observe(this, (newWordValue) -> {
            DefinitionFragment wordFrag = new DefinitionFragment(newWordValue);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("");
            tx.replace(R.id.fragmentLocation, wordFrag);
            tx.commitAllowingStateLoss();
        });
    }

    private void makeApiRequest(String word, DictionaryItem displayWord) {
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray meanings = response.getJSONArray("meanings");

                            // Clear existing definitions
                            displayWord.clearDefinitions();

                            // Iterate through meanings and add definitions to the list
                            for (int i = 0; i < meanings.length(); i++) {
                                JSONObject meaning = meanings.getJSONObject(i);
                                JSONArray definitions = meaning.getJSONArray("definitions");
                                for (int j = 0; j < definitions.length(); j++) {
                                    String definition = definitions.getJSONObject(j).getString("definition");
                                    displayWord.addDefinition(definition);
                                }
                            }

                            myAdapter.notifyDataSetChanged();

                            // Update the definitions in the local database
                            thread.execute(() -> {
                                dDAO.updateItem(displayWord);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", "API Request Error: " + error.toString());
                    }
                });
        queue.add(request);
    }

    static class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView wordTextView;
        public TextView definitionTextView;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            definitionTextView = itemView.findViewById(R.id.definitionTextView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                DictionaryItem selected = words.get(position);
                dictionaryModel.selectedWord.postValue(selected);
            });
        }
    }
}
