package algonquin.cst2335.mobilefinalproject.Deezer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * @Author Santiago Garcia
 * Ensures that the album's data won't disappear if the phone is flipped.
 */
public class AlbumsViewModel extends ViewModel {

    public MutableLiveData<ArrayList<DeezerAlbumDTO>> deezerAlbum = new MutableLiveData<>();

    public MutableLiveData<DeezerAlbumDTO> selectedAlbums = new MutableLiveData<>();

}
