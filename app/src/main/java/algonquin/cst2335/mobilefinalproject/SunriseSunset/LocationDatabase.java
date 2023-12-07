package algonquin.cst2335.mobilefinalproject.SunriseSunset;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import algonquin.cst2335.mobilefinalproject.data.FavoriteLocation;

//import algonquin.cst2335.data.FavoriteLocation;

/**
 * This abstract class creates the database used to store saved favourite locations
 * @author Julianna Hawkins
 * @version 1.0
 */
@Database(entities = {FavoriteLocation.class}, version = 1)
public abstract class LocationDatabase extends RoomDatabase {

    /**
     * Abstract method retrieves DAO
     *
     * @return FavoriteLocationDAO
     */
    public abstract FavoriteLocationDAO favorite_Location_DAO();
}