package algonquin.cst2335.mobilefinalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.FragmentDefinitionBinding;

public class DefinitionFragment extends Fragment {
    private FragmentDefinitionBinding binding;
    private TextView wordTextView;
    private DictionaryItem word;
    private DictionaryDatabase db;
    private androidx.appcompat.widget.Toolbar toolbar;

    public DefinitionFragment(DictionaryItem word) {
        this.word = word;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDefinitionBinding.inflate(inflater, container, false);

        wordTextView = binding.wordTextView;
        wordTextView.setText(word.getWord());


        // Set up RecyclerView and adapter to display definitions
        RecyclerView recyclerView = binding.defragRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        WordandDefinitionAdapters adapter = new WordandDefinitionAdapters(word.getDefinitions().toString()); //
        recyclerView.setAdapter(adapter);

        db = Room.databaseBuilder(requireContext(), DictionaryDatabase.class, "dictionaryDatabase").build();
        DictionaryItemDAO dDAO = db.dictionaryItemDAO();


        toolbar = binding.deleteFragToolbar;
        toolbar.inflateMenu(R.menu.delete_word);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.deleteF) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Do you want to delete this Definition from your Favourites?")
                            .setTitle("Delete")
                            .setNegativeButton("No", (dialog, which) -> {
                                // User chose not to delete
                            })
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // User chose to delete


                                Executor thread1 = Executors.newSingleThreadExecutor();
                                thread1.execute(() -> {
                                    try {
                                        // Ensure that the deleteWordDefinition method is correctly implemented
                                        dDAO.deleteWord(word.getWord());
                                        Log.d("Deleted", "Rows affected: " + word.getWord());
                                    } catch (Exception e) {
                                        Log.e("DeleteError", "Error Deleting definition", e);
                                    }
                                });

                                // Log to check if the definition is being deleted
                                Log.d("definition", "definition deleted: " + word);

                                Snackbar.make(requireView(), "Definition deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", (btn) -> {
                                            // User clicked Undo
                                            Executor thread2 = Executors.newSingleThreadExecutor();
                                            thread2.execute(() -> {
                                                // undo the deletion in the database
                                                dDAO.insertItemDefinition(word);
                                            });
                                            adapter.notifyDataSetChanged();
                                        })
                                        .show();
                            });
                    builder.create().show();
            }
            return false;
        });
        return binding.getRoot();
    }


    // Adapter class for displaying word definitions in RecyclerView
    private static class WordandDefinitionAdapters extends RecyclerView.Adapter<WordandDefinitionAdapters.ViewHolder> {
        private final String definitions;

        public WordandDefinitionAdapters(String definitions) {
            this.definitions = definitions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.definition_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.definitionTextView.setText(definitions);
        }

        @Override
        public int getItemCount() {
            return 1; // Display only one definition in this example
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView definitionTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                definitionTextView = itemView.findViewById(R.id.definitionTextView);
            }
        }
    }
}
