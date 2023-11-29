package algonquin.cst2335.mobilefinalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;

@Entity
public class Songs {
    //? Attributes
    /**
     * The ID stored in the database
     */
    @PrimaryKey
    @ColumnInfo(name = "SongID")
    private long id;

    /**
     * Title of the song
     */
    @ColumnInfo(name = "title")
    private String title;

    /**
     * Duration of the song in seconds
     */
    @ColumnInfo(name = "duration")
    private int duration;

    /**
     * Album's name
     */
    @ColumnInfo(name = "albumName")
    private String albumName;

    /**
     * Album's cover Image
     */
    @ColumnInfo(name = "albumCover")
    private String albumCover;

    /**
     * Atrist's name
     */
    @ColumnInfo(name = "artistName")
    private String artistName;

    //?Constructor
    public Songs(long id, String title, int duration, String albumName, String albumCover, String artistName) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.albumName = albumName;
        this.albumCover = albumCover;
        this.artistName = artistName;
    }

    //?Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationInMMSS() {
        int minute = duration / 60;
        int second = duration % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minute, second);
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
