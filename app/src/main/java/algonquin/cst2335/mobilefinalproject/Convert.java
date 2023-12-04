package algonquin.cst2335.mobilefinalproject;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Convert {

    @TypeConverter
    public static String fromList(List<String> definitions) {
        if (definitions == null) {
            return null;
        }
        return String.join(",", definitions);
    }

    @TypeConverter
    public static List<String> toList(String definitions) {
        if (definitions == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(definitions.split(",")));
    }
}

