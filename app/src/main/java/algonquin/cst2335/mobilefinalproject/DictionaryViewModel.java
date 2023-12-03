package algonquin.cst2335.mobilefinalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DictionaryViewModel extends ViewModel {
        public MutableLiveData<ArrayList<Dictionary.DictionaryItem>> words = new MutableLiveData<>();
}
