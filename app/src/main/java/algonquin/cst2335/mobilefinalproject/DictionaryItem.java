package algonquin.cst2335.mobilefinalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "word_definitions")
public class DictionaryItem extends ArrayList<DictionaryItem> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "word")
    protected String word;

    @ColumnInfo(name = "definition")
    protected String definitions;

    public DictionaryItem(String word, String definitions) {
        this.word = word;
        this.definitions = definitions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public String getDefinitions() {
        return definitions;
    }
}
