package com.nextrr.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;

import one.DatabaseUtils;

public class ContentHelper {

    public final String className = CricketHelper.class.getName();

    public String setMessage(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String messageDateTime = sdf.format(cal.getTime());

        String userEmail = params.getFirst("email");
        String message = params.getFirst("message");

        if (userEmail != null && message != null) {
            queryMap.put("messageDateTime", messageDateTime);
            queryMap.put("userEmail", userEmail);
            queryMap.put("message", message);
            DatabaseUtils dbUtils = new DatabaseUtils();
            dbUtils.runCreateQuery("user_message", queryMap);

            return new Gson().toJson("success");
        }
        return new Gson().toJson("error");
    }

    public String getContentByCondition(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("screen_content", params.getFirst("screen"));
        Map<String, Object> result = dbUtils.getEntityDataWithConditions("content", queryMap);

        return new Gson().toJson(result);
    }
}
