package dam.project.easyroute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Josh on 05.12.2015.
 */
public class Utile {

    private static String LIST_SEPARATOR = ",";

    public static String convertArrayListToString(ArrayList<String> stringList) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : stringList) {
            stringBuffer.append(str).append(LIST_SEPARATOR);
        }

        // Remove last separator
        int lastIndex = stringBuffer.lastIndexOf(LIST_SEPARATOR);
        stringBuffer.delete(lastIndex, lastIndex + LIST_SEPARATOR.length() + 1);

        return stringBuffer.toString();
    }

    public static ArrayList<String> convertStringToArrayList(String str) {
        return new ArrayList<String>(Arrays.asList(str.split(LIST_SEPARATOR)));
    }
}
