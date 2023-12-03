package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import algonquin.cst2335.mobilefinalproject.databinding.DictionaryMenuBinding;

public class Dictionary extends AppCompatActivity {
    DictionaryMenuBinding binding;
    private RecyclerView.Adapter myAdapter;
    ArrayList<DictionaryItem> words = new ArrayList<>();
    DictionaryViewModel dictionaryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DictionaryMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setAdapter(new RecyclerView.Adapter<>() )

    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView search;

    }

    public class DictionaryItem {
        private String word;
        private String definition;

        public DictionaryItem(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }

        public String getWord() {
            return word;
        }

        public String getDefinition() {
            return definition;
        }
    }
}

