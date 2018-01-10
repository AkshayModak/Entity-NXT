package com.nextrr.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.util.stream.Collectors;
import com.google.gson.Gson;
import javax.ws.rs.core.MultivaluedMap;

import one.DatabaseUtils;
import one.DefaultObjects;
import one.NextrrUtils;
import architecture.utils.DebugWrapper;

public class GenericHelper {
	
	public final String className = GenericHelper.class.getName();
	
	public String getCountriesBySport(String sport) {
		DebugWrapper.logInfo("Initiating getCountriesBySports of GenericHelper", className);
		ArrayList<Map> formula1List = new ArrayList<Map>();
		DatabaseUtils dbUtils = new DatabaseUtils();
		
		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put("sports_type_id", sport);
		Map<String, Object> countryAssoc = dbUtils.getEntityDataWithConditions("country_assoc", conditionParams);
		List<Object> countryAssocList = (List) countryAssoc.get("result");
		
		List<Map<String, Object>> countryGeoList = new ArrayList<Map<String, Object>>();
		
		for(Object countryGeoId : countryAssocList) {
			conditionParams.clear();
			Map<String, Object> geoId = (Map<String, Object>) countryGeoId;
			conditionParams.put("country_geo_id", geoId.get("country_id"));
			Map<String, Object> country_geo = dbUtils.getFirstEntityDataWithConditions("country_geo", conditionParams);
			countryGeoList.add(country_geo);
		}
		DebugWrapper.logInfo("getCountriesBySports finished running", className);
		return new Gson().toJson(countryGeoList);
	}

	public Object getCountryAssoc(String sport, String _format) {
		DebugWrapper.logInfo("Initiating getCountryAssoc of GenericHelper", className);
		ArrayList<Map> formula1List = new ArrayList<Map>();
		DatabaseUtils dbUtils = new DatabaseUtils();

		Map<String, Object> conditionParams = new HashMap<String, Object>();
		conditionParams.put("sports_type_id", sport);
		Map<String, Object> countryAssoc = dbUtils.getEntityDataWithConditions("country_assoc", conditionParams);
		List<Object> countryAssocList = (List) countryAssoc.get("result");

		DebugWrapper.logInfo("getCountryAssoc finished running", className);
		if ("GSON".equals(_format)) {
			return new Gson().toJson(countryAssocList);
		}
		return (Object) countryAssocList;
	}

	public String addSportsLeague(MultivaluedMap<String, String> params) {

		Map<String, Object> queryMap = new HashMap<String, Object>();

		String series_name = params.getFirst("series_name");
		String series_location = params.getFirst("series_location");
		String series_from_date = params.getFirst("series_from_date");
		String series_to_date = params.getFirst("series_to_date");
		String sports_type_id = params.getFirst("sports_type_id");
		String country_with = params.getFirst("country_with");

		queryMap.put("description", series_name);
		queryMap.put("country_geo_id", series_location);
		queryMap.put("to_date", series_to_date);
		queryMap.put("from_date", series_from_date);
		queryMap.put("sports_type_id", sports_type_id);

		String fromYear = NextrrUtils.getYearFromDate(series_from_date);
		String toYear = NextrrUtils.getYearFromDate(series_to_date);

		if (!fromYear.equals(toYear)) {
			queryMap.put("year", fromYear + "-" + toYear);
		} else {
			queryMap.put("year", fromYear);
		}

		DatabaseUtils du = new DatabaseUtils();
		du.runCreateQuery("sports_league", queryMap);

		Map<String, Object> sportsLeagueMap = du.getFirstEntityDataWithConditions("sports_league", queryMap);
		String sports_league_id = (String) sportsLeagueMap.get("sports_league_id");
		queryMap.clear();

		queryMap.put("sports_league_id", sports_league_id);
		queryMap.put("country_geo_id", series_location);
		du.runCreateQuery("cricket_assoc", queryMap);

		if (DefaultObjects.isNotEmpty(country_with)) {
			queryMap.put("country_geo_id", country_with);
			du.runCreateQuery("cricket_assoc", queryMap);
		}

		return new Gson().toJson(new ArrayList<String>().add("success"));
	}

	public String removeSportsLeague(MultivaluedMap<String, String> params) {
		DebugWrapper.logInfo("Initiating removeSportsLeague of GenericHelper", className);

		String sports_league_id = params.getFirst("sports_league_id");

		if (DefaultObjects.isNotEmpty(sports_league_id)) {
			DatabaseUtils du = new DatabaseUtils();
			du.runDeleteQuery("cricket_assoc", "sports_league_id", sports_league_id);
			du.runDeleteQuery("sports_league", "sports_league_id", sports_league_id);
		} else {
			return new Gson().toJson(new ArrayList<String>().add("error"));
		}

		return new Gson().toJson(new ArrayList<String>().add("success"));
	}
}