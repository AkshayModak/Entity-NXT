package architecture.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utility {

    /*public static Boolean hasMapKeyValuePair(Map<String, Object> paramMap, String key, String value) {
        Boolean isAvailable = false;
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if ((key).equalsIgnoreCase(entry.getKey()) && (value).equalsIgnoreCase(entry.getValue().toString())) {
                System.out.println("===value==="+entry.getValue());
                isAvailable = true;
            }
        }
        return isAvailable;
    }*/
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
}