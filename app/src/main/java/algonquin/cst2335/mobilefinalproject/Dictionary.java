package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
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
    DictionaryMenuBinding binding;
    private RecyclerView.Adapter myAdapter;
    private ArrayList<DictionaryItem> words = new ArrayList<>();
    private DictionaryViewModel dictionaryModel;
    private DictionaryItemDAO dDAO;
    private Executor thread = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DictionaryMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DictionaryDatabase db = Room.databaseBuilder(getApplicationContext(), DictionaryDatabase.class, "dictionary").build();
        dDAO = db.dictionaryItemDAO();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dictionaryModel = new ViewModelProvider(this).get(DictionaryViewModel.class);

//        words = dictionaryModel.words.getValue();

        binding.searchButton.setOnClickListener(click -> {
            String word = binding.searchEditText.getText().toString();

            // Create a DictionaryItem object for a word
            DictionaryItem displayWord = new DictionaryItem(word, "1. A greeting (salutation) said when meeting someone or acknowledging someones arrival or presence");
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

        class MyRowHolder extends RecyclerView.ViewHolder {
            public TextView wordTextView;
            public TextView definitionTextView;

            public MyRowHolder(@NonNull View itemView) {
                super(itemView);
                wordTextView = itemView.findViewById(R.id.wordTextView);
                definitionTextView = itemView.findViewById(R.id.definitionTextView);

                itemView.setOnClickListener(clk -> {
                    int position = getAbsoluteAdapterPosition();
                    DictionaryItem selected = words.get(position);
                    dictionaryModel.selectedWord.setValue(selected);
                });
            }
        }

        binding.recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
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
                holder.definitionTextView.setText(dictionary.getDefinition());
            }

            @Override
            public int getItemCount() {
                return words.size();
            }
        });

        if (words == null) {
            dictionaryModel.words.setValue(words = new ArrayList<>());

            thread.execute(() -> {
                words.addAll(dDAO.getAllWords()); // When you get the data from database
                runOnUiThread(() -> binding.recyclerView.setAdapter(myAdapter)); // Loads the RecyclerView
            });
        }

        dictionaryModel.selectedWord.observe(this, (newWordValue) -> {
            DefinitionFragment wordFrag = new DefinitionFragment(newWordValue);
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("");
            tx.replace(R.id.fragmentLocation, wordFrag);
            tx.commit();
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
                            JSONObject firstMeaning = meanings.getJSONObject(0);
                            JSONArray definitions = firstMeaning.getJSONArray("definitions");
                            String definition = definitions.getJSONObject(0).getString("definition");

                            displayWord.setDefinition(definition);
                            myAdapter.notifyDataSetChanged();

                            // Update the definition in the local database
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

}

