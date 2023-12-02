package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DictionaryFragment extends Fragment {
    TextView meaningText;
    TextView synonymText;
    String meaning;
    String synonym;

    public DictionaryFragment(String meaning, String synonym) {
        this.meaning = meaning;
        this.synonym = synonym;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        meaningText = view.findViewById(R.id.meaning_textView);
        synonymText = view.findViewById(R.id.synonyms_textView);

        meaningText.setText("");
        synonymText.setText("");

        meaningText.setText(meaning);
        synonymText.setText(synonym);

        return view;
    }
}
