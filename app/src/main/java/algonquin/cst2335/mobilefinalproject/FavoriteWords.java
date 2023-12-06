package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.FavoritesPageBinding;
import algonquin.cst2335.mobilefinalproject.databinding.FragmentWordBinding;

public class FavoriteWords extends AppCompatActivity {
    private FavoritesPageBinding binding;
    private WordAdapter wordAdapter;
    private ArrayList<DictionaryItem> wordList = new ArrayList<>();
    private DictionaryViewModel wordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FavoritesPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.faveToolBar);
        setSupportActionBar(toolbar);

        wordModel = new ViewModelProvider(this).get(DictionaryViewModel.class);

        // Set up RecyclerView and adapter to display saved words
        RecyclerView recyclerView = binding.wordRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Fetch saved words from the database
        DictionaryDatabase db = Room.databaseBuilder(getApplicationContext(), DictionaryDatabase.class, "dictionaryDatabase").build();
        DictionaryItemDAO dDAO = db.dictionaryItemDAO();

        if (wordList.isEmpty()) {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                List<DictionaryItem> allWords = dDAO.getAllWords();
                runOnUiThread(() -> {
                    wordList.addAll(allWords);
                    if (wordAdapter == null) {
                        wordAdapter = new WordAdapter(wordList);
                        recyclerView.setAdapter(wordAdapter);
                    } else {
                        wordAdapter.notifyDataSetChanged();
                    }
                });
            });
        }
    }

    private class WordAdapter extends RecyclerView.Adapter<WordViewHolder> {
        private final List<DictionaryItem> wordTermList;

        public WordAdapter(List<DictionaryItem> wordTermList) {
            this.wordTermList = wordTermList;
        }

        @NonNull
        @Override
        public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FragmentWordBinding wordBinding = FragmentWordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new WordViewHolder(wordBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            DictionaryItem wordEntity = wordTermList.get(position);
            holder.termTextView.setText(wordEntity.getWord());

            androidx.appcompat.widget.Toolbar toolbar = holder.wordInfo;

            toolbar.inflateMenu(R.menu.fave_word);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.infoButton) {
                    try {
                        DefinitionFragment wordDetailFragment = new DefinitionFragment(wordEntity);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.addToBackStack("yessirrr");
                        transaction.replace(R.id.frag, wordDetailFragment);
                        transaction.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    Log.e("WordAdapter", "WordEntity is null");
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return wordTermList.size();
        }
    }

    private static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView termTextView;
        androidx.appcompat.widget.Toolbar wordInfo;


        public WordViewHolder(View itemView) {
            super(itemView);
            termTextView = itemView.findViewById(R.id.recyclerWordView);
            wordInfo = itemView.findViewById(R.id.details);
        }
    }
}
