package algonquin.cst2335.mobilefinalproject.Deezer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * @Author Santiago Garcia
 * Creates database to hold the songs
 */
@Database(entities = {Songs.class},version = 1)
public abstract class SongsDatabase extends RoomDatabase {
    public abstract DeezerDAO deezerDao();
}
