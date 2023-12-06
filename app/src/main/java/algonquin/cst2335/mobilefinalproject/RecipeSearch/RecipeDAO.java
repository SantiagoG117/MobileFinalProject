package algonquin.cst2335.mobilefinalproject.RecipeSearch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * DAO interface operates between RecipeInfo and RecipeDB
 * Defines "CRUD" methods
 * @author Jackson Coghill 041089141
 * @version 1.0
 */
@Dao
public interface RecipeDAO {

    /**
     * Inserts recipe object into DB
     * @param recipe recipe obj to be added into DB
     */
    @Insert
    void insertRecipe (RecipeInfo recipe);

    /**
     * Deletes recipe from DB
     * @param recipe to be deleted
     */
    @Delete
    void deleteRecipe (RecipeInfo recipe);

    /**
     * Retrieves recipe from DB
     * @param recipeID ID of recipe
     * @return corresponding recipe to ID
     */
    @Query("Select * from RecipeInfo where recipeID = :recipeID limit 1")
    RecipeInfo getRecipeByID(int recipeID);

    /**
     * Retrieves all saved recipes
     * @return list of all saved recipes in DB
     */
    @Query ("Select * from RecipeInfo")
    List<RecipeInfo> getAllSavedRecipes();

}