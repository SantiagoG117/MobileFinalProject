package algonquin.cst2335.mobilefinalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DictionaryItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id = 0;

    @ColumnInfo(name = "word")
    protected String word;

    @ColumnInfo(name = "definition")
    protected List<String> definitions;

    public DictionaryItem() {
        definitions = new ArrayList<>();
    }

    public DictionaryItem(String w, List<String> d) {
        word = w;
        definitions = d;
    }

    public String getWord() {
        return word;
    }

    public List<String> getDefinition() {
        return definitions;
    }

    public void setDefinition(List<String> definition) {
        this.definitions = definition;
    }

    public void clearDefinitions() {
        if (definitions != null) {
            definitions.clear();
        }
    }

    public void addDefinition(String definition) {
        if (definitions == null) {
            definitions = new ArrayList<>();
        }
        this.definitions.add(definition);
    }
}
