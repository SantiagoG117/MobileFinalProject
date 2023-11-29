package algonquin.cst2335.mobilefinalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class AlbumsViewModel extends ViewModel {
    /**
     * Ensures that the album's data won't disappear if the phone is flipped.
     */
    public MutableLiveData<ArrayList<DeezerAlbum>> deezerAlbum = new MutableLiveData<>();

    public MutableLiveData<DeezerAlbum> selectedAlbums = new MutableLiveData<>();

}
