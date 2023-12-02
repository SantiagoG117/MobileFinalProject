package algonquin.cst2335.mobilefinalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SyllablesFragment extends Fragment {
    private String syllables;
    private String count;
    private TextView syllablesTextView;
    private TextView countTextView;

    public SyllablesFragment(String syllables, String count) {
        this.syllables = syllables;
        this.count = count;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_syllables, container, false);

        syllablesTextView = view.findViewById(R.id.syllables_textView);
        countTextView = view.findViewById(R.id.count_textView);

        syllablesTextView.setText("");
        countTextView.setText("");

        syllablesTextView.setText(syllables);
        countTextView.setText(count);

        return view;
    }
}
