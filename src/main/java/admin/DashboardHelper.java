package admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ws.rs.core.MultivaluedMap;
import com.google.gson.Gson;

import one.DatabaseUtils;
import one.NextrrUtils;

public class DashboardHelper {

    public String getVisits() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> result = dbUtils.getAllEntityData("visit");

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("result");
        List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

        for (Map<String, Object> map : resultList) {
            if (resultMap.containsKey((String) map.get("userIp"))) {
                List<Map<String, Object>> tempList = resultMap.get((String) map.get("userIp"));
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("ipAddress", map.get("userIp"));
                tempMap.put("country", map.get("userCountry"));
                tempMap.put("city", map.get("userCity"));
                tempMap.put("status", "Allowed");

                tempList.add(tempMap);
                resultMap.put((String) map.get("userIp"), tempList);
            } else {
                List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("ipAddress", map.get("userIp"));
                tempMap.put("country", map.get("userCountry"));
                tempMap.put("city", map.get("userCity"));
                tempMap.put("status", "Allowed");

                tempList.add(tempMap);
                resultMap.put((String) map.get("userIp"), tempList);
            }
        }

        resultMap.forEach((k, v) -> {
            List<Map<String, Object>> tempList = resultMap.get(k);
            Set<Map<String, Object>> tempSet = new HashSet<Map<String, Object>>(tempList);
            int totalVisits = resultMap.get(k).size();

            Iterator<Map<String, Object>> it = tempSet.iterator();

            while (it.hasNext()) {
                Map<String, Object> value = it.next();
                value.put("visits", totalVisits);
                finalList.add(value);
            }
        });
        result.put("visitsAnalysis", finalList);

        return new Gson().toJson(result);
    }

    public String getVisitsByCountries() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> visits = dbUtils.getAllEntityData("visit");

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) visits.get("result");
        List<String> countryList = new ArrayList<String>();
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

        for (Map<String, Object> map : resultList) {
            if (!resultMap.containsKey((String) map.get("userCountry"))) {
                if (!map.get("userCountry").equals("null")) {
                    countryList.add(map.get("userCountry").toString());
                }
            }
        }

        Set<String> tempSet = new HashSet<String>(countryList);
        List<String> uniqueCountryList = new ArrayList<String>(tempSet);
        List<Integer> visitsList = new ArrayList<Integer>();

        for (String countryName : uniqueCountryList) {
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("userCountry", countryName);
            Map<String, Object> visitsResult = dbUtils.getEntityDataWithConditions("visit", queryMap);

            List<Map<String, Object>> countryVisitsList = (List<Map<String, Object>>) visitsResult.get("result");
            visitsList.add(countryVisitsList.size());
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("countries", uniqueCountryList);
        result.put("visits", visitsList);

        return new Gson().toJson(result);
    }

    public String getVisitsByDate(String date) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> queryParams = new HashMap<String, Object>();

        queryParams.put("requestDate", date);
        Map<String, Object> result = dbUtils.getEntityDataWithConditions("visit", queryParams);

        return new Gson().toJson(result);
    }

    @SuppressWarnings("unchecked")
    public String getModulesDetails() {

        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> entityParams = new HashMap<String, Object>();
        Map<String, Object> moviesResult = dbUtils.getAllEntityData("movies");
        List<Map<String, Object>> moviesList = (List<Map<String, Object>>) moviesResult.get("result");

        Map<String, Object> moviesMap = new HashMap<String, Object>();
        moviesMap.put("totalRecords", moviesList.size());
        moviesMap.put("released", 0);
        moviesMap.put("upcoming", 0);

        for (Map<String, Object> movie : moviesList) {
            Date releaseDate = NextrrUtils.getDateFromString((String) movie.get("release_date"));

            if (NextrrUtils.isDateLessThanToday(releaseDate)) {
                int incrementReleased = (int) moviesMap.get("released") + 1;
                moviesMap.put("released", incrementReleased);
            } else {
                int incrementUpcoming = (int) moviesMap.get("upcoming") + 1;
                moviesMap.put("upcoming", incrementUpcoming);
            }
        }

        Map<String, Object> cricketResults = dbUtils.getAllEntityData("cricket");
        List<Map<String, Object>> cricketList = (List<Map<String, Object>>) cricketResults.get("result");
        Map<String, Object> cricketMap = new HashMap<String, Object>();
        cricketMap.put("totalRecords", cricketList.size());

        entityParams.put("sports_type_id", "CRICKET");
        Map<String, Object> cricketSeries = dbUtils.getEntityDataWithConditions("sports_league", entityParams);
        List<Map<String, Object>> cricketSeriesList = (List<Map<String, Object>>) cricketSeries.get("result");
        cricketMap.put("totalLeagues", cricketSeriesList.size());

        Map<String, Object> formula1Results = dbUtils.getAllEntityData("formula_one");
        List<Map<String, Object>> f1List = (List<Map<String, Object>>) formula1Results.get("result");
        Map<String, Object> f1Map = new HashMap<String, Object>();
        f1Map.put("totalRecords", f1List.size());
        f1Map.put("finished", 0);

        for (Map<String, Object> f1 : f1List) {
            Date finishDate = NextrrUtils.getDateFromString((String) f1.get("date"));

            if (NextrrUtils.isDateLessThanToday(finishDate)) {
                int incrementFinish = (int) f1Map.get("finished") + 1;
                f1Map.put("finished", incrementFinish);
            }
        }

        Map<String, Object> fantasyCricketResults = dbUtils.getAllEntityData("fantasy_cricket");
        List<Map<String, Object>> fcList = (List<Map<String, Object>>) fantasyCricketResults.get("result");
        Map<String, Object> fcMap = new HashMap<String, Object>();
        fcMap.put("totalRecords", fcList.size());

        List<String> fcCountries = new ArrayList<String>();
        for (Map<String, Object> fc : fcList) {
            fcCountries.add((String) fc.get("country_geo_id"));
        }
        Set<String> fcSet = new HashSet<String>(fcCountries);
        List<String> fcCountriesList = new ArrayList<String>(fcSet);
        fcMap.put("totalCountries", fcCountriesList.size());

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("movies", moviesMap);
        resultMap.put("cricket", cricketMap);
        resultMap.put("f1", f1Map);
        resultMap.put("fantasyCricket", fcMap);

        return new Gson().toJson(resultMap);
    }

    public String getAllContent() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> content = dbUtils.getAllEntityData("content");
        return new Gson().toJson(content);
    }

    public String createContent(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        String screen = params.getFirst("screen");
        String contentType = params.getFirst("contentType");
        String description = params.getFirst("description");
        String electronicText = params.getFirst("electronicText");

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("screen_content", screen);
        queryParams.put("content_type", contentType);
        queryParams.put("description", description);
        queryParams.put("electronicText", electronicText);

        dbUtils.runCreateQuery("content", queryParams);
        return new Gson().toJson("success");
    }

    public String updateContent(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        String contentId = params.getFirst("contentId");
        String screen = params.getFirst("screen");
        String contentType = params.getFirst("contentType");
        String description = params.getFirst("description");
        String electronicText = params.getFirst("electronicText");

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("content_id", contentId);
        queryParams.put("screen_content", screen);
        queryParams.put("content_type", contentType);
        queryParams.put("description", description);
        queryParams.put("electronicText", electronicText);

        dbUtils.runUpdateQuery("content", queryParams, "content_id");
        return new Gson().toJson("success");
    }

    public String removeContent(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        String contentId = params.getFirst("contentId");
        dbUtils.runDeleteQuery("content", contentId, "content_id");
        return new Gson().toJson("success");
    }

    public String getUserMessages() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> userMessages = dbUtils.getAllEntityData("user_message");
        return new Gson().toJson(userMessages.get("result"));
    }

    public String markMessageRead(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        String userMessageId = params.getFirst("id");
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("user_message_id", userMessageId);
        queryParams.put("hasRead", "Y");

        dbUtils.runUpdateQuery("user_message", queryParams, "user_message_id");
        return new Gson().toJson("success");
    }

    public String removeUserMessage(MultivaluedMap<String, String> params) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        String userMessageId = params.getFirst("id");
        dbUtils.runDeleteQuery("user_message", userMessageId, "user_message_id");
        return new Gson().toJson("success");
    }

    public String getUnreadMessagesCount() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> userMessages = dbUtils.getAllEntityData("user_message");
        int unreadCounter = 0;

        for (Map<String, Object> map : (List<Map<String, Object>>) userMessages.get("result")) {
            if (map.get("hasRead") == null) {
                unreadCounter = unreadCounter + 1;
            }
        }
        return new Gson().toJson(unreadCounter);
    }

    public String authenticateUser(String username, String password) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", false);
        if ("akshay".equals(username) && "modak".equals(password)) {
            resultMap.put("success", true);
            return new Gson().toJson(resultMap);
        }

        return new Gson().toJson(resultMap);
    }
}
