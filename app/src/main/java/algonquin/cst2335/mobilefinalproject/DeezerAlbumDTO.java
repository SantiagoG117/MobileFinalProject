package algonquin.cst2335.mobilefinalproject;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class DeezerAlbumDTO {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private long albumId;

    @ColumnInfo(name = "albumTitle")
    private String title;

    @ColumnInfo(name = "artistName")
    private String artistName;

    @ColumnInfo(name = "coverURL")
    private String coverUrl;



    private String albumRelease;

    private String albumLabel;

    public DeezerAlbumDTO(long albumId, String title, String artistName, String coverUrl) {
        this.albumId = albumId;
        this.title = title;
        this.artistName = artistName;
        this.coverUrl = coverUrl;
    }


    public long getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getAlbumRelease() {
        return albumRelease;
    }

    public void setAlbumRelease(String albumRelease) {
        this.albumRelease = albumRelease;
    }

    public String getAlbumLabel() {
        return albumLabel;
    }

    public void setAlbumLabel(String albumLabel) {
        this.albumLabel = albumLabel;
    }
}
