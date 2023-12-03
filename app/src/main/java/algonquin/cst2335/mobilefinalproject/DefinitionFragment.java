package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.mobilefinalproject.databinding.FragmentDefinitionBinding;

public class DefinitionFragment extends Fragment {
    DictionaryItem selected;

    public DefinitionFragment(DictionaryItem d) {
        selected = d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentDefinitionBinding binding = FragmentDefinitionBinding.inflate(inflater);

        binding.wordTextView.setText(selected.word);
        binding.definitionTextView.setText(selected.definition);
        return binding.getRoot();
    }
}
