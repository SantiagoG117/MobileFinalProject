package algonquin.cst2335.mobilefinalproject.RecipeSearch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Database class for the app, extends RoomDatabase
 * @author Jackson Coghill 041089141
 * @version 1.0
 */
@Database(entities = {RecipeInfo.class}, version= 1, exportSchema= false)
public abstract class RecipeDB extends RoomDatabase {
    /**
     * Method to access and CRUD the DB
     * @return DAO object to interface with the RecipeInfo
     */
    public abstract RecipeDAO recipeDAO();
}

