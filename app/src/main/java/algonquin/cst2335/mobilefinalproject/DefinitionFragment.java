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

/**
 * A Fragment class responsible for displaying the details of a word and its definitions.
 */
public class DefinitionFragment extends Fragment {
    private FragmentDefinitionBinding binding;
    private TextView wordTextView;
    private DictionaryItem word;
    private DictionaryDatabase db;
    private androidx.appcompat.widget.Toolbar toolbar;

    /**
     * Constructs a new DefinitionFragment with the specified word.
     * @param word The DictionaryItem representing the word and its definitions.
     */
    public DefinitionFragment(DictionaryItem word) {
        this.word = word;
    }

    /**
     * Inflates the layout for the fragment, initializes UI elements, and sets up the RecyclerView to display definitions.
     * @param inflater           The LayoutInflater object that can be used to inflate views
     * @param container          The parent view that the fragment's UI should be attached to
     * @param savedInstanceState The saved state of the fragment (if available)
     * @return The root view of the fragment
     */
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

    /**
     * A RecyclerView Adapter for displaying word definitions in a RecyclerView.
     */
    private static class WordandDefinitionAdapters extends RecyclerView.Adapter<WordandDefinitionAdapters.ViewHolder> {
        /**
         * The string containing the definitions to be displayed.
         */
        private final String definitions;

        /**
         * Constructs a new WordandDefinitionAdapters with the specified definitions.
         * @param definitions The string containing definitions to be displayed.
         */
        public WordandDefinitionAdapters(String definitions) {
            this.definitions = definitions;
        }

        /**
         * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
         * @param parent   The ViewGroup into which the new View will be added
         * @param viewType The type of the new View
         * @return A new ViewHolder that holds a View of the given view type
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.definition_list, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Called by RecyclerView to display the data at the specified position.
         * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position
         * @param position The position of the item within the adapter's data set
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.definitionTextView.setText(definitions);
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         * @return The total number of items in this adapter's data set
         */
        @Override
        public int getItemCount() {
            return 1; // Display only one definition in this example
        }

        /**
         * ViewHolder class for displaying word definitions in RecyclerView items.
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            /**
             * The TextView for displaying the definition.
             */
            TextView definitionTextView;

            /**
             * Constructs a new ViewHolder for displaying word definitions.
             * @param itemView The View representing the item
             */
            public ViewHolder(View itemView) {
                super(itemView);
                definitionTextView = itemView.findViewById(R.id.definitionTextView);
            }
        }
    }
}
