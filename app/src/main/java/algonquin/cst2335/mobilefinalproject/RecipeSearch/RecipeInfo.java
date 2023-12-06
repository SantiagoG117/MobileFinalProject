package algonquin.cst2335.mobilefinalproject.RecipeSearch;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Class represents a recipe in local Room DB
 * Contains columns, constructors, and getters/setters
 * @author Jackson Coghill 041089141
 * @version 1.0
 */
@Entity
public class RecipeInfo {

    /**
     * All of the columns for the DB
     */
    @PrimaryKey
    @ColumnInfo(name="recipeID")
    private int recipeID;

    @ColumnInfo(name="recipeTitle")
    private String recipeTitle;

    @ColumnInfo(name="recipeImageURL")
    private String recipeImageURL;

    @ColumnInfo(name="recipeSummary")
    private String recipeSummary;

    @ColumnInfo(name="recipeURL")
    private String recipeURL;


    /**
     * RecipeInfo constructor with three attributes.
     * Ignored so as to not conflict with class' default constructor
     * @param recipeID ID of recipe
     * @param recipeTitle Title of recipe
     * @param recipeImageURL URL of recipe
     */
    @Ignore
    public RecipeInfo(int recipeID, String recipeTitle, String recipeImageURL) {
        this.recipeID = recipeID;
        this.recipeTitle = recipeTitle;
        this.recipeImageURL = recipeImageURL;
    }

    /**
     * Fully fleshed out constructor with all the recipe variables
     * @param recipeID ID
     * @param recipeTitle Title
     * @param recipeImageURL URL
     * @param recipeSummary Summary
     * @param recipeURL URL
     */
    public RecipeInfo(int recipeID, String recipeTitle, String recipeImageURL, String recipeSummary, String recipeURL) {
        this.recipeID = recipeID;
        this.recipeTitle = recipeTitle;
        this.recipeImageURL = recipeImageURL;
        this.recipeSummary = recipeSummary;
        this.recipeURL = recipeURL;
    }

    /**
     * Get RecipeID
     * @return recipe ID
     */
    public int getRecipeID() {
        return recipeID;
    }

    /**
     * Set RecipeID
     * @param recipeID recipe ID
     */
    public void setRecipeID(int recipeID){
        this.recipeID = recipeID;
    }

    /**
     *  Get RecipeTitle
     * @return recipe title
     */
    public String getRecipeTitle(){
        return recipeTitle;
    }

    /**
     * set RecipeTitle
     * @param recipeTitle recipe title
     */
    public void setRecipeTitle(String recipeTitle){
        this.recipeTitle = recipeTitle;
    }

    /**
     * Get RecipeURL
     * @return recipe URL
     */
    public String getRecipeImageURL(){
        return recipeImageURL;
    }

    /**
     * Set Recipe Image(URL)
     * @param recipeImageURL recipe image URL
     */
    public void setRecipeImageURL(String recipeImageURL){
        this.recipeImageURL = recipeImageURL;
    }

    /**
     * Get recipe summary
     * @return recipe summary
     */
    public String getRecipeSummary(){
        return recipeSummary;
    }

    /**
     * Set recipe summary
     * @param recipeSummary recipe summary
     */
    public void setRecipeSummary(String recipeSummary){
        this.recipeSummary = recipeSummary;
    }

    /**
     * Get Recipe URL
     * @return recipe url
     */
    public String getRecipeURL(){
        return recipeURL;
    }

    /**
     * Set Recipe URL
     * @param recipeURL Recipe URL
     */
    public void setRecipeURL(String recipeURL){
        this.recipeURL = recipeURL;
    }

}

