package algonquin.cst2335.mobilefinalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * @Author Santiago Garcia
 * Data Access Object interface for saving songs to a Playlist
 */
@Dao
public interface DeezerDAO {

    @Insert
    public long insertSong(Songs song);

    @Query("Select * from Songs")
    List<Songs> getAllSongs();

    @Delete
    void deleteSongFromPlayList(Songs song);

    @Query("SELECT * FROM Songs WHERE title = :title")
    List<Songs> searchSong(String title);
}
