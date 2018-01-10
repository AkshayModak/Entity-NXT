package com.nextrr.helper;

import architecture.utils.DebugWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;

import one.DatabaseUtils;
import one.DefaultObjects;
import one.First;

public class Formula1Helper {

    public static final String className = Formula1Helper.class.getName();

    public String getFormula1Schedule() {

        First f1First = new First();
        ArrayList<Map> formula1List = new ArrayList<Map>();
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> f1MainRaces = dbUtils.getAllEntityData("formula_one");
        List<Map<String, Object>> f1ScheduleList = (List<Map<String, Object>>) f1MainRaces.get("result");

        for (Map<String, Object> f1ScheduleMap : f1ScheduleList) {
            DebugWrapper.logInfo("=========f1ScheduleMap=========" + f1ScheduleMap, className);
            Map<String, Object> formula1Map = new HashMap<String, Object>();
            String id = (String) f1ScheduleMap.get("formula_one_id");
            String name = (String) f1ScheduleMap.get("name");
            String city = (String) f1ScheduleMap.get("city");
            String country = (String) f1ScheduleMap.get("country");
            String date = (String) DefaultObjects.formatDate((String) f1ScheduleMap.get("date"));
            String time = (String) DefaultObjects.formatTimeForFrontEnd((String) f1ScheduleMap.get("time"));
            String raceType = (String) f1ScheduleMap.get("race_type_id");
            String imagePath = (String) f1ScheduleMap.get("imagePath");
            String circuitGuide = (String) f1ScheduleMap.get("circuitGuide");

            formula1Map.put("mainRace",
                    f1First.getF1MainRace(id, name, city, country, date, time, raceType, imagePath, circuitGuide));

            Map<String, Object> formula1Params = new HashMap<String, Object>();
            formula1Params.put("formula_one_id", id);

            Map<String, Object> f1PracticeAndQualifying = dbUtils.getEntityDataWithConditions("formula_one_practice",
                    formula1Params);
            List<Map<String, Object>> f1PracticeAndQualifyingList = (List<Map<String, Object>>) f1PracticeAndQualifying
                    .get("result");

            for (Map<String, Object> f1PracticeAndQualifyingMap : f1PracticeAndQualifyingList) {
                String practiceId = (String) f1PracticeAndQualifyingMap.get("formula_one_practice_id");
                String formulaOneId = (String) f1PracticeAndQualifyingMap.get("formula_one_id");
                String practiceDate = (String) DefaultObjects
                        .formatDate((String) f1PracticeAndQualifyingMap.get("date"));
                String practiceTime = (String) DefaultObjects
                        .formatTimeForFrontEnd((String) f1PracticeAndQualifyingMap.get("time"));
                String sessionName = (String) f1PracticeAndQualifyingMap.get("name");
                String raceTypeId = (String) f1PracticeAndQualifyingMap.get("race_type_id");

                formula1Map.put(raceTypeId, f1First.getF1SessionSchedule(sessionName, practiceDate, practiceTime,
                        formulaOneId, raceTypeId, practiceId));
            }
            formula1List.add(formula1Map);
        }

        return new Gson().toJson(formula1List);
    }

    public String getFormula1ToEdit() {

        First f1First = new First();
        Map<String, Object> result = DefaultObjects.getSuccessMap();
        ArrayList<Map> formula1List = new ArrayList<Map>();

        Map<String, Object> raceTypeMap = new HashMap<String, Object>();

        DatabaseUtils dbUtils = new DatabaseUtils();
        raceTypeMap = dbUtils.getAllEntityData("formula1_race_type");

        Map<String, Object> f1MainRaces = dbUtils.getF1Schedule("formula_one", null);
        List<Map<String, Object>> f1ScheduleList = (List<Map<String, Object>>) f1MainRaces.get("result");

        for (Map<String, Object> f1ScheduleMap : f1ScheduleList) {
            Map<String, Object> formula1Map = new HashMap<String, Object>();
            String formulaOneId = (String) f1ScheduleMap.get("formula_one_id");
            String name = (String) f1ScheduleMap.get("name");
            String city = (String) f1ScheduleMap.get("city");
            String country = (String) f1ScheduleMap.get("country");
            String date = (String) f1ScheduleMap.get("date");
            String time = (String) f1ScheduleMap.get("time");
            String raceType = (String) f1ScheduleMap.get("race_type_id");
            String imagePath = (String) f1ScheduleMap.get("imagePath");
            String circuitGuide = (String) f1ScheduleMap.get("circuitGuide");

            formula1Map.put("result", f1First.getF1MainRace(formulaOneId, name, city, country, date, time, raceType,
                    imagePath, circuitGuide));

            formula1List.add(formula1Map);
            Map<String, Object> formula1Params = new HashMap<String, Object>();
            formula1Params.put("formula_one_id", formulaOneId);

            Map<String, Object> f1PracticeAndQualifying = dbUtils.getF1Schedule("formula_one_practice", formula1Params);
            List<Map<String, Object>> f1PracticeAndQualifyingList = (List<Map<String, Object>>) f1PracticeAndQualifying
                    .get("result");

            for (Map<String, Object> f1PracticeAndQualifyingMap : f1PracticeAndQualifyingList) {

                Map<String, Object> formula1PracticeMap = new HashMap<String, Object>();

                String formulaOnePracticeId = (String) f1PracticeAndQualifyingMap.get("formula_one_practice_id");
                formulaOneId = (String) f1PracticeAndQualifyingMap.get("formula_one_id");
                String practiceDate = (String) f1PracticeAndQualifyingMap.get("date");
                String practiceTime = (String) f1PracticeAndQualifyingMap.get("time");
                String sessionName = (String) f1PracticeAndQualifyingMap.get("name");
                String raceTypeId = (String) f1PracticeAndQualifyingMap.get("race_type_id");

                formula1PracticeMap.put("result", f1First.getF1SessionSchedule(sessionName, practiceDate, practiceTime,
                        formulaOneId, raceTypeId, formulaOnePracticeId));
                formula1List.add(formula1PracticeMap);
            }
        }

        result.put("formula1List", formula1List);
        result.put("raceTypes", raceTypeMap);

        return new Gson().toJson(result);
    }

    public String updateF1Practice(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        String date = params.get("date").get(0);
        String raceType = params.get("raceType").get(0);
        String formulaOneId = params.get("formulaOneId").get(0);
        String time = params.get("time").get(0);
        if (params.get("name") != null) {
            String name = params.getFirst("name");
            queryMap.put("name", name);
        }
        String imagePath = params.getFirst("imagePath");

        if (params.get("formulaOnePracticeId") != null) {
            String formulaOnePracticeId = params.get("formulaOnePracticeId").get(0);
            queryMap.put("formula_one_practice_id", formulaOnePracticeId);
        }
        if (params.get("city") != null && "MAINRACE".equals(raceType)) {
            String city = params.get("city").get(0);
            queryMap.put("city", city);
        }
        if (params.get("country") != null && "MAINRACE".equals(raceType)) {
            String country = params.get("country").get(0);
            queryMap.put("country", country);
        }
        if (params.get("circuitGuide") != null && "MAINRACE".equals(raceType)) {
            String circuitGuide = params.getFirst("circuitGuide");
            queryMap.put("circuitGuide", circuitGuide);
        }

        queryMap.put("formula_one_id", formulaOneId);
        queryMap.put("time", time);
        queryMap.put("date", date);
        queryMap.put("race_type_id", raceType);

        if (params.get("imagePath") != null) {
            queryMap.put("imagePath", imagePath);
        }

        DatabaseUtils du = new DatabaseUtils();

        if ("MAINRACE".equals(raceType)) {
            String primaryKey = "formula_one_id";
            du.runUpdateQuery("formula_one", queryMap, primaryKey);
        } else {
            String primaryKey = "formula_one_practice_id";
            du.runUpdateQuery("formula_one_practice", queryMap, primaryKey);
        }
        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

    public String setF1Schedule(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        String date = params.get("date").get(0);
        String raceType = params.get("raceType").get(0);
        String time = params.get("time").get(0);
        if (params.get("name") != null) {
            String name = params.get("name").get(0);
            queryMap.put("name", name);
        }
        String imagePath = params.getFirst("imagePath");
        if (params.get("city") != null) {
            String city = params.get("city").get(0);
            queryMap.put("city", city);
        }
        if (params.get("country") != null) {
            String country = params.get("country").get(0);
            queryMap.put("country", country);
        }
        if (params.get("circuitGuide") != null && "MAINRACE".equals(raceType)) {
            String circuitGuide = params.getFirst("circuitGuide");
            queryMap.put("circuitGuide", circuitGuide);
        }

        queryMap.put("time", time);
        queryMap.put("date", date);
        queryMap.put("race_type_id", raceType);
        if (params.get("imagePath") != null) {
            queryMap.put("imagePath", imagePath);
        }

        DatabaseUtils du = new DatabaseUtils();

        if ("MAINRACE".equals(raceType)) {
            du.runCreateQuery("formula_one", queryMap);
        } else {
            queryMap.put("formula_one_id", params.get("formula1Id").get(0));
            du.runCreateQuery("formula_one_practice", queryMap);
        }

        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

    public String removeF1Schedule(MultivaluedMap<String, String> params) {

        String raceType = params.get("raceType").get(0);
        DatabaseUtils du = new DatabaseUtils();

        if ("MAINRACE".equals(raceType)) {
            String id = params.get("formula1Id").get(0);
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("formula_one_id", id);
            Map<String, Object> formula1PracticeMap = du.getEntityDataWithConditions("formula_one_practice", queryMap);

            List<Map<String, Object>> formula1PracticeList = (List<Map<String, Object>>) formula1PracticeMap
                    .get("result");

            for (Map<String, Object> f1Map : formula1PracticeList) {
                String practiceId = (String) f1Map.get("formula_one_practice_id");
                du.runDeleteQuery("formula_one_practice", "formula_one_practice_id", practiceId);
            }

            du.runDeleteQuery("formula_one", "formula_one_id", id);
        } else {
            String id = params.get("formula1PracticeId").get(0);
            du.runDeleteQuery("formula_one_practice", "formula_one_practice_id", id);
        }

        return new Gson().toJson(new ArrayList<String>().add("success"));
    }

}