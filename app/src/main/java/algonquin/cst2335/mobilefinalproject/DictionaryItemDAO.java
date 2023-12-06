package algonquin.cst2335.mobilefinalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DictionaryItemDAO {

    @Insert
    void insertItemDefinition(DictionaryItem wordDefinition);

    @Delete
    void deleteItemDefinition(DictionaryItem word);

    @Query("Select * from word_definitions")
    List<DictionaryItem> getAllWords();

    @Query("Select * from word_definitions")
    List<DictionaryItem> getAllItemDefinitions();

    @Query("SELECT * FROM word_definitions WHERE word = :word")
    List<DictionaryItem> getSpecificWords(String word);

    @Query("DELETE FROM word_definitions WHERE word = :word")
    void deleteWord(String word);
}
