package algonquin.cst2335.mobilefinalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DictionaryItem {
    @ColumnInfo(name = "word")
    protected String word;

    @ColumnInfo(name = "definition")
    protected String definition;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id = 0;

    public DictionaryItem() {}

    public DictionaryItem(String w, String d) {
        word = w;
        definition = d;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
