package com.nextrr.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;

import architecture.utils.DebugWrapper;
import one.DatabaseUtils;
import one.DefaultObjects;
import one.First;
import one.NextrrUtils;

public class MoviesServices {
	
	public String getMovies(MultivaluedMap<String, String> params) {
		
		ArrayList<Map> movieList = new ArrayList<Map>();
		DatabaseUtils dbUtils = new DatabaseUtils();
		String movieType = params.getFirst("movieType");
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("movie_type_id", movieType.toUpperCase());
		Map<String, Object> movies = dbUtils.getEntityDataWithConditions("movies", queryMap);
		List<Map<String, Object>> movieResultList = (List<Map<String, Object>>)movies.get("result");
		
		for (Map<String, Object> movieResult : movieResultList) {
			String movieName = (String) movieResult.get("movie_name");
			int movieId = Integer.valueOf((String) movieResult.get("movie_id"));
			String releaseDate = DefaultObjects.formatDate((String) movieResult.get("release_date"));
			String trailer = (String) movieResult.get("trailer_link");
			List<String> trailerList = NextrrUtils.getListFromCommaSeparatedString(trailer);
			String cast = (String) movieResult.get("cast");

			Map<String, Object> movieMap = MoviesHelper.getMovieMap(movieName, movieId, releaseDate, trailerList, cast);
			movieList.add(movieMap);
		}
		
		return new Gson().toJson(movieList);
	}
	
	public String getMoviesToEdit() {
		
		ArrayList<Map> movieList = new ArrayList<Map>();
		DatabaseUtils dbUtils = new DatabaseUtils();
		Map<String, Object> movies = dbUtils.getAllEntityData("movies");
		Map<String, Object> movieTypes = dbUtils.getAllEntityData("movie_type");
		List<Map<String, Object>> movieResultList = (List<Map<String, Object>>)movies.get("result");
		Map<String, Object> result = DefaultObjects.getSuccessMap();
		
		for (Map<String, Object> movieResult : movieResultList) {
			String movieName = (String) movieResult.get("movie_name");
			int movieId = Integer.valueOf((String) movieResult.get("movie_id"));
			String releaseDate = DefaultObjects.formatDate((String) movieResult.get("release_date"));
			String trailer = (String) movieResult.get("trailer_link");
			String cast = (String) movieResult.get("cast");
			String movieType = (String) movieResult.get("movie_type_id");

			Map<String, Object> movieMap = MoviesHelper.getEditMovieMap(movieName, movieId, releaseDate, trailer, cast, movieType);
			movieList.add(movieMap);
		}
		
		result.put("movieList", movieList);
		result.put("movieTypes", movieTypes);
		
		return new Gson().toJson(result);
	}
	
	public String setMovie(MultivaluedMap<String, String> params) {

		Map<String, Object> queryMap = new HashMap<String, Object>();
		String movieName = params.getFirst("movieName");
		String cast = NextrrUtils.listToCommaSeperatedString(params.get("cast"));
		String trailer = params.getFirst("trailer");
		String releaseDate = params.getFirst("releaseDate");
		String movieType = params.getFirst("movieType");
		
		queryMap.put("movie_name", movieName);
		queryMap.put("cast", cast);
		if (DefaultObjects.isNotEmpty(trailer)) {
			queryMap.put("trailer_link", trailer);
		}
		queryMap.put("movie_type_id", movieType);
		queryMap.put("release_date", releaseDate);
		
		DatabaseUtils du = new DatabaseUtils();
		du.runCreateQuery("movies", queryMap);
		
		return new Gson().toJson(new ArrayList<String>().add("success"));
	}
	
	public String removeMovie(MultivaluedMap<String, String> params) {
		
		String movieId = params.getFirst("movieId");
		
		if (DefaultObjects.isNotEmpty(movieId)) {
			DatabaseUtils du = new DatabaseUtils();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("movie_id", movieId);
			du.runDeleteQuery("movies", "movie_id", movieId);
		} else {
			return new Gson().toJson(new ArrayList<String>().add("error"));
		}
		
		return new Gson().toJson(new ArrayList<String>().add("success"));
	}
	
	public String updateMovie(MultivaluedMap<String, String> params) {
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		
		String movieName = params.getFirst("movieName");
		String movieId = params.getFirst("movieId");
		String cast = NextrrUtils.listToCommaSeperatedString(params.get("cast"));
		String trailer = params.getFirst("trailer");
		String releaseDate = params.getFirst("releaseDate");
		String movieType = params.getFirst("movieType");
		
		queryMap.put("movie_name", movieName);
		queryMap.put("movie_id", movieId);
		queryMap.put("cast", cast);
		if (trailer != null) {
			queryMap.put("trailer_link", trailer);
		}
		queryMap.put("release_date", releaseDate);
		queryMap.put("movie_type_id", movieType);
		
		if (DefaultObjects.isNotEmpty(movieId)) {
			DatabaseUtils du = new DatabaseUtils();
			String primaryKey = "movie_id";
			du.runUpdateQuery("movies", queryMap, primaryKey);
		} else {
			return new Gson().toJson(new ArrayList<String>().add("error"));
		}
		
		return new Gson().toJson(new ArrayList<String>().add("success"));
	}
	
}