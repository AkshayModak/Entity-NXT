package architecture.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Utility {

    public static Boolean hasMapKeyValuePair(List<Map<String, Object>> paramList, String key, String value) {
        Boolean isAvailable = false;
        for (Map<String, Object> paramMap : paramList) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if ((key).equalsIgnoreCase(entry.getKey().toString()) && (value).equalsIgnoreCase(entry.getValue().toString())) {
                    isAvailable = true;
                }
            }
        }
        return isAvailable;
    }

    public static Map<String, Object> returnSuccess() {
        Map<String, Object> successMap = new HashMap<String, Object>();
        successMap.put("status", "success");
        return successMap;
    }

    public static Map<String, Object> returnError() {
        Map<String, Object> errorMap = new HashMap<String, Object>();
        errorMap.put("status", "error");
        return errorMap;
    }

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
        return returnError();
    }

    public static Map<String, Object> getMap() {
        return new HashMap<String, Object>();
    }

    public static Map<String, Object> getErrorMap() {
        Map<String, Object> errorMap = new HashMap<String, Object>();
        errorMap.put("status", "error");
        return errorMap;
    }

    public static Boolean isNotEmpty(Object objValue) {
        String value = String.valueOf(objValue);
        value = value.replaceAll("\\s+", "");
        if ((value != null) && (value.length() > 0) && !(value.isEmpty()) && !(value.equals("null"))) {
            return true;
        }
        return false;
    }

    public static Boolean isEmpty(Object objValue) {
        return objValue == null;
    }

    public static Map<String, String[]> removeKeyValueFromRequestMap(Map<String, String[]> map, List<String> keyList) {
        Iterator<Map.Entry<String, String[]>> filterEntries = map.entrySet().iterator();

        Map<String, String[]> resultMap = new HashMap<>();

        while (filterEntries.hasNext()) {
            Map.Entry<String, String[]> entry = filterEntries.next();
            String key = entry.getKey();
            if (!keyList.contains(key)) {
                resultMap.put(key, entry.getValue());
            }
        }
        return resultMap;
    }

    public static boolean containsAKeyword(String myString, List<String> keywords){
        myString = myString.toLowerCase();
        for(String keyword : keywords){
            keyword = keyword.toLowerCase();
            if(myString.contains(keyword)){
                return true;
            }
        }
        return false; // Never found match.
    }
}