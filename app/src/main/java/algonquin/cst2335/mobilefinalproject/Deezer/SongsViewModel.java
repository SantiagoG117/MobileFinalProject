package algonquin.cst2335.mobilefinalproject.Deezer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * @Author Santiago Garcia
 * Ensures that the song's data won't disappear if the phone is flipped.
 */
public class SongsViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Songs>> songs = new MutableLiveData<>();

    public MutableLiveData<Songs> selectedSongs = new MutableLiveData<>();

    public void setSelectedSongs(Songs selectedSong) {
        selectedSongs.setValue(selectedSong);
    }
}
