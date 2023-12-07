package algonquin.cst2335.mobilefinalproject.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class
 * Contains information on favourite locations and stores them
 * in a database
 *
 * @author Julianna Hawkins
 * @version 1.0
 */
@Entity
public class FavoriteLocation {

    /**
     * Primary key column
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public int id;

    /**
     * Latitude column
     */
    @ColumnInfo(name = "Latitude")
    public String latitude;

    /**
     * Longitude column
     */
    @ColumnInfo(name = "Longitude")
    public String longitude;

    /**
     * Timezone column
     */
    @ColumnInfo(name = "Timezone")
    public String timezone;

    /**
     * Sunrise column
     */
    @ColumnInfo(name = "Sunrise")
    public String sunrise;

    /**
     * Sunset column
     */
    @ColumnInfo(name = "Sunset")
    public String sunset;

    /**
     * emptry constructor
     */
    public FavoriteLocation() { }

    /**
     * parameterized constructor
     * @param latitude stores latitude
     * @param longitude stores longitude
     * @param timezone stores date/time
     * @param sunrise stores sunrise time
     * @param sunset stores sunset time
     */
    public FavoriteLocation(String latitude, String longitude, String timezone, String sunrise, String sunset) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    /**
     * Getter for latitude
     *
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * setter for latitude
     *
     * @param latitude latitude for fav location
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * getter for longitude
     *
     * @return longitude for fav location
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * setter for longitude
     *
     * @param longitude longitude for fav location
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * getter for timezone
     *
     * @return timezone for fav location
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * setter for timezone
     *
     * @param timezone timezone of fav location
     */
    public void setTimezone(String timezone){
        this.timezone = timezone;
    }

    /**
     * getter for sunrise time
     *
     * @return sunrise time of fav location
     */
    public String getSunrise() {
        return sunrise;
    }

    /**
     * setter for sunrise time
     *
     * @param sunrise time of fav location
     */
    public void setSunrise(String sunrise){
        this.sunrise = sunrise;
    }

    /**
     * getter for sunset time
     *
     * @return sunset time of favourite location
     */
    public String getSunset() {
        return sunset;
    }

    /**
     * setter for sunset time
     * @param sunset time of fav location
     */
    public void setSunset(String sunset){
        this.sunset = sunset;
    }

}//end of class