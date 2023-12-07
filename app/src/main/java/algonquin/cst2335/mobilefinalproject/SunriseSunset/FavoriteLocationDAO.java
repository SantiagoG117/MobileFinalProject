package algonquin.cst2335.mobilefinalproject.SunriseSunset;
import algonquin.cst2335.mobilefinalproject.data.FavoriteLocation;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


/**
 * This interface is the data access object
 * It performs all needed database operations
 *
 * @author Julianna Hawkins
 * @version 1.0
 */
@Dao
public interface FavoriteLocationDAO {

    /**
     * Inserts a new favourite location into the database
     * @param fav_location
     * @return
     */
    @Insert
    long insertFavoriteLocation(FavoriteLocation fav_location);

    /**
     * Select statement retrieves all saved favourite locations
     *
     * @return A list of all FavoriteLocation objects in the database
     */
    @Query("SELECT * FROM FavoriteLocation;")
    List<FavoriteLocation> getAllFavoriteLocations();

    /**
     * Deletes a specified entry to be deleted
     *
     * @param fav_location an object to be deleted
     */
    @Delete
    void deleteFavoriteLocation(FavoriteLocation fav_location);
}