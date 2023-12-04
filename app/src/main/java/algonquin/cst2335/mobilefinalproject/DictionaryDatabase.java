package algonquin.cst2335.mobilefinalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {DictionaryItem.class}, version = 1)
@TypeConverters(Convert.class)
public abstract class DictionaryDatabase extends RoomDatabase {
    public abstract DictionaryItemDAO dictionaryItemDAO();
}
