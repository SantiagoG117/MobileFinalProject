package algonquin.cst2335.mobilefinalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DictionaryItemDAO {
    @Insert
    long insertItem(DictionaryItem d);

    @Query("Select * from DictionaryItem")
    List<DictionaryItem> getAllWords();

    @Update
    void updateItem(DictionaryItem dictionaryItem);

    @Delete
    void deleteItem(DictionaryItem d);
}
