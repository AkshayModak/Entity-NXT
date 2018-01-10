package one;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import architecture.utils.DebugWrapper;

public class NextrrUtils {

    public static final String CLASS_NAME = NextrrUtils.class.getName();

    public static String listToCommaSeperatedString(List<String> list) {
        return String.join(",", list);
    }

    public static List<String> getListFromCommaSeparatedString(String text) {
        if (text != null && text != "") {
            List<String> result = null;
            if (text.contains(",")) {
                result = Arrays.asList(text.split("\\s*,\\s*"));
            } else {
                List<String> tempList = new ArrayList<>();
                tempList.add(text);
                result = tempList;
            }
            return result;
        } else {
            return null;
        }
    }

    public static String escapeMetaCharacters(String inputString) {
        final String[] metaCharacters = { "'" };
        String outputString = "";
        for (int i = 0; i < metaCharacters.length; i++) {
            if (inputString.contains(metaCharacters[i])) {
                outputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
                inputString = outputString;
                return outputString;
            }
        }
        return inputString;
    }

    public static Object getFirstFromList(Map<String, Object> map) {
        List resultList = (List) map.get("result");
        if (!map.isEmpty()) {
            Map<String, Object> resultMap = (Map<String, Object>) resultList.get(0);
            return resultMap;
        }
        return DefaultObjects.getErrorMap();
    }

    public static Map<String, Object> getEmptyMap() {
        return new HashMap<String, Object>();
    }

    public static String valueToStringOrEmpty(Map<String, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }

    public static Boolean isPresentValue(Map<String, ?> map, String key) {
        String value = (String) map.get(key);
        return value == null || value.length() == 0 ? false : true;
    }

    public static String getYearFromDate(String date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date parsedDate = df.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);
            String year = String.valueOf(cal.get(Calendar.YEAR));
            return year;
        } catch (ParseException pe) {
            DebugWrapper.logError(pe, CLASS_NAME);
            return "error";
        }
    }

    public static Date getDateFromString(String date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            Date parsedDate = df.parse(date);
            return parsedDate;
        } catch (ParseException pe) {
            DebugWrapper.logError(pe, CLASS_NAME);
            return null;
        }
    }

    public static Boolean isDateLessThanToday(Date date) {
        Date today = new Date();
        if (date.before(today)) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
                                                // '-' and decimal.
    }

    public static short getShortRandomNumber(short min, short max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        short randomNum = (short) ThreadLocalRandom.current().nextInt(min, max + 1);
        return randomNum;
    }

    public static List<Map<String, Object>> sortCricketPlayingElevenMap(List<Map<String, Object>> unsortedList) {

        List<Map<String, Object>> sortedList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 11; i++) {
            sortedList.add(new HashMap<String, Object>());
        }

        for (Map<String, Object> unsortedMap : unsortedList) {
            int battingPosition = Integer.valueOf(String.valueOf(unsortedMap.get("battingPosition")));
            if (battingPosition != 0) {
                sortedList.set(battingPosition - 1, unsortedMap);
            }
        }
        return sortedList;
    }

    public static String getDescription(String entityName, String primaryKey, String primaryKeyValue) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(primaryKey, primaryKeyValue);
        Map<String, Object> resultMap = dbUtils.getEntityDataWithConditions(entityName, queryParams);
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");

        return (String) resultList.get(0).get("description");
    }

    public static String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }
}