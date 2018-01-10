package com.nextrr.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import one.DatabaseUtils;
import one.DefaultObjects;
import one.NextrrUtils;

import javax.ws.rs.core.MultivaluedMap;
import com.google.gson.Gson;

import architecture.utils.DebugWrapper;

public class CricketHelper {

    public final String className = CricketHelper.class.getName();

    public String setCricket(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        String sportsChildTypeId = params.getFirst("matchType");
        String teamOneGeoId = params.getFirst("teamOneId");
        String teamTwoGeoId = params.getFirst("teamTwoId");
        String stadium = params.getFirst("stadium");
        String country = params.getFirst("country");
        String city = params.getFirst("city");
        String time = params.getFirst("time");
        String matchNumber = params.getFirst("matchNumber");
        String sports_league_id = params.getFirst("sports_league");

        if (params.getFirst("fromDate") != null) {
            String fromDate = params.getFirst("fromDate");
            queryMap.put("match_from_date", fromDate);
        }

        if (params.getFirst("toDate") != null) {
            String toDate = params.getFirst("toDate");
            queryMap.put("match_to_date", toDate);
        }
        queryMap.put("match_number", matchNumber);
        queryMap.put("sports_child_type_id", sportsChildTypeId);
        queryMap.put("team_one_geoId", teamOneGeoId);
        queryMap.put("team_two_geoId", teamTwoGeoId);
        queryMap.put("stadium", stadium);
        queryMap.put("city", city);
        queryMap.put("time", time);
        queryMap.put("country_geoId", country);
        queryMap.put("sports_league_id", sports_league_id);

        DatabaseUtils du = new DatabaseUtils();

        du.runCreateQuery("cricket", queryMap);

        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

    public String getIntlCricket() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> resultMap = dbUtils.getAllEntityData("cricket");
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");

        for (Map<String, Object> cricket : resultList) {
            if (cricket.containsKey("match_from_date")) {
                cricket.put("match_from_date", DefaultObjects.formatDate((String) cricket.get("match_from_date")));
            }
            if (cricket.containsKey("match_to_date") && cricket.get("match_to_date") != null) {
                cricket.put("match_to_date", DefaultObjects.formatDate((String) cricket.get("match_to_date")));
            }
            if (cricket.containsKey("sports_league_id")) {
                cricket.put("series_id", cricket.get("sports_league_id"));
            }
        }
        return new Gson().toJson(resultMap);
    }

    public String getIntlCricketToDisplay() {

        String[] cricketMatchList = { "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th" };

        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> resultMap = dbUtils.getAllEntityData("cricket");

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        resultMap.forEach((key, value) -> {
            List<Map<String, Object>> valueList = (List<Map<String, Object>>) value;

            for (int i = 0; i < valueList.size(); i++) {

                if (valueList.get(i).get("match_number") != null
                        && Integer.valueOf((String) valueList.get(i).get("match_number")) != 0) {
                    valueList.get(i).put("match_number",
                            cricketMatchList[Integer.valueOf((String) valueList.get(i).get("match_number")) - 1]);
                }

                if (valueList.get(i).get("match_to_date") != null
                        && !"N/A".equals((String) valueList.get(i).get("match_to_date"))) {
                    valueList.get(i).put("match_date", valueList.get(i).get("match_to_date"));
                } else {
                    valueList.get(i).put("match_date", valueList.get(i).get("match_from_date"));
                }

                if (valueList.get(i).get("team_one_geoId") != null) {
                    paramMap.clear();
                    paramMap.put("country_geo_id", valueList.get(i).get("team_one_geoId"));
                    Map<String, Object> countryGeoResult = dbUtils.getFirstEntityDataWithConditions("country_geo",
                            paramMap);
                    valueList.get(i).put("team_one_geoId", countryGeoResult.get("description"));
                    valueList.get(i).put("team_one_flag", countryGeoResult.get("flag_image_path"));
                }

                if (valueList.get(i).get("team_two_geoId") != null) {
                    paramMap.clear();
                    paramMap.put("country_geo_id", valueList.get(i).get("team_two_geoId"));
                    Map<String, Object> countryGeoResult = dbUtils.getFirstEntityDataWithConditions("country_geo",
                            paramMap);
                    valueList.get(i).put("team_two_geoId", countryGeoResult.get("description"));
                    valueList.get(i).put("team_two_flag", countryGeoResult.get("flag_image_path"));
                }

                if (valueList.get(i).get("sports_child_type_id") != null) {
                    paramMap.clear();
                    paramMap.put("sports_child_type_id", valueList.get(i).get("sports_child_type_id"));
                    paramMap.put("sports_type_id", "CRICKET");
                    Map<String, Object> countryGeoResult = dbUtils.getFirstEntityDataWithConditions("sports_child_type",
                            paramMap);
                    valueList.get(i).put("sports_child_type_id", countryGeoResult.get("description"));
                }

                if (valueList.get(i).get("time") != null) {
                    valueList.get(i).put("time",
                            DefaultObjects.formatTimeForFrontEnd((String) valueList.get(i).get("time")));
                }

                if (valueList.get(i).get("match_from_date") != null) {
                    valueList.get(i).put("match_from_date",
                            DefaultObjects.formatDate((String) valueList.get(i).get("match_from_date"), "CRICKET"));
                }

                if (valueList.get(i).get("match_to_date") != null) {
                    valueList.get(i).put("match_to_date",
                            DefaultObjects.formatDate((String) valueList.get(i).get("match_to_date"), "CRICKET"));
                }
                if (valueList.get(i).get("sports_league_id") != null) {
                    valueList.get(i).put("series_id", valueList.get(i).get("sports_league_id"));
                    Map<String, Object> queryMap = new HashMap<String, Object>();
                    queryMap.put("sports_league_id", valueList.get(i).get("sports_league_id"));
                    if (!queryMap.isEmpty()) {
                        Boolean trueFalse = NextrrUtils.isPresentValue(queryMap, "sports_league_id");
                        if (trueFalse) {
                            Map<String, Object> sportsLeagueMap = dbUtils
                                    .getFirstEntityDataWithConditions("sports_league", queryMap);

                            if (sportsLeagueMap != null) {
                                valueList.get(i).put("series_description", sportsLeagueMap.get("description"));
                                valueList.get(i).put("year", sportsLeagueMap.get("year"));
                            }
                        }
                    }
                }
            }
            resultMap.put(key, valueList);
        });
        return new Gson().toJson(resultMap);
    }

    public String removeCricket(MultivaluedMap<String, String> params) {

        String cricketId = params.getFirst("cricketId");

        if (DefaultObjects.isNotEmpty(cricketId)) {
            DatabaseUtils du = new DatabaseUtils();
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("movie_id", cricketId);
            du.runDeleteQuery("cricket", "cricket_id", cricketId);
        } else {
            return new Gson().toJson(new ArrayList<String>().add("error"));
        }

        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

    public String updateCricket(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        String cricketId = params.getFirst("cricketId");
        String sportsChildTypeId = params.getFirst("matchType");
        String teamOneGeoId = params.getFirst("teamOneId");
        String teamTwoGeoId = params.getFirst("teamTwoId");
        String stadium = params.getFirst("stadium");
        String country = params.getFirst("country");
        String city = params.getFirst("city");
        String time = params.getFirst("time");
        String matchNumber = params.getFirst("matchNumber");
        String sports_league_id = params.getFirst("sports_league");

        if (params.getFirst("fromDate") != null) {
            String fromDate = params.getFirst("fromDate");
            queryMap.put("match_from_date", fromDate);
        }

        if (params.getFirst("toDate") != null) {
            String toDate = params.getFirst("toDate");
            queryMap.put("match_to_date", toDate);
        }
        queryMap.put("match_number", matchNumber);
        queryMap.put("cricket_id", cricketId);
        queryMap.put("sports_child_type_id", sportsChildTypeId);
        queryMap.put("team_one_geoId", teamOneGeoId);
        if (!"N/A".equals(teamTwoGeoId)) {
            queryMap.put("team_two_geoId", teamTwoGeoId);
        }
        queryMap.put("stadium", stadium);
        queryMap.put("city", city);
        queryMap.put("time", time);
        queryMap.put("country_geoId", country);
        queryMap.put("sports_league_id", sports_league_id);

        DatabaseUtils du = new DatabaseUtils();

        if (DefaultObjects.isNotEmpty(cricketId)) {
            DatabaseUtils dbUtils = new DatabaseUtils();
            du.runUpdateQuery("cricket", queryMap, "cricket_id");
        } else {
            return new Gson().toJson(new ArrayList<String>().add("error"));
        }

        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

    public String getCricketLeagues() {
        GenericHelper genericHelper = new GenericHelper();
        List<Map<String, Object>> countryAssocList = (List<Map<String, Object>>) genericHelper
                .getCountryAssoc("CRICKET", null);
        List<Map<String, Object>> resultList = new ArrayList();
        for (Map<String, Object> countryAssoc : countryAssocList) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            DatabaseUtils dbUtils = new DatabaseUtils();
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("country_geo_id", countryAssoc.get("country_id"));
            Map<String, Object> cricketAssocResult = dbUtils.getEntityDataWithConditions("cricket_assoc", queryParams);

            if (!cricketAssocResult.isEmpty()) {
                List<Map<String, Object>> cricketAssocList = (List<Map<String, Object>>) cricketAssocResult
                        .get("result");
                if (!cricketAssocList.isEmpty()) {
                    resultMap.put("country_geo_id", countryAssoc.get("country_id"));
                    resultMap.put("country_description", countryAssoc.get("description"));
                    List<Map<String, Object>> sportsLeagueList = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> cricketAssoc : cricketAssocList) {
                        cricketAssoc.forEach((key, value) -> {
                            if ("sports_league_id".equals(key)) {
                                queryParams.clear();
                                queryParams.put("sports_league_id", value);
                                Map<String, Object> sports_league = dbUtils
                                        .getFirstEntityDataWithConditions("sports_league", queryParams);
                                sportsLeagueList.add(sports_league);
                            }
                        });
                    }
                    if (!sportsLeagueList.isEmpty()) {
                        resultMap.put("sports_leagues", sportsLeagueList);
                    }
                }
                if (!resultMap.isEmpty() && resultMap.containsKey("sports_leagues")) {
                    resultList.add(resultMap);
                }
            }
        }
        return new Gson().toJson(resultList);
    }

    public String getAllRawCricketLeagues() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> sportsLeagueMap = dbUtils.getAllEntityData("sports_league");

        return new Gson().toJson(sportsLeagueMap);
    }
}