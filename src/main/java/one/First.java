package one;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "user")
public class First implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private MultivaluedMap<String, String> queryParams;
	private String imagePath;
	private String alt;
	private Map<String, Object> movieDetails = new HashMap<String, Object>();
	
	public First() {
		
	}
	
	public First(String alt, String imagePath){
		this.alt = alt;
		this.imagePath = imagePath;
	}
	
	@XmlElement
	public void setQueryParams(MultivaluedMap<String, String> queryParams){
		this.queryParams = queryParams;
	}
	
	public void setName(String settingName) {
		name = settingName;
	}
	
	public Map setMovieDetails(int movieId, String movieName, String fullImagePath) {
		
		Map<String, Object> movieDetails = new HashMap<String, Object>();
		movieDetails.put("movieId", movieId);
		movieDetails.put("movieName", movieName);
		movieDetails.put("fullImagePath", fullImagePath);
		
		return movieDetails;
	}
	
	public Map getMovieDetails() {
		return movieDetails;
	}
	
	public String getName() {
		return queryParams.getFirst("name");
	}
	
	public String getLastName() {
		return queryParams.getFirst("lastName");
	}
	
	public String getImagePath(String alt, String imagePath){
		return imagePath;
	}
	
	public Map getMovieIdAndImage(int movieId, String movieName, String movieImage) {
		Map<String, Object> movieImageAndIdMap = new HashMap<String, Object>();
		
		movieImageAndIdMap.put("movieId", movieId);
		movieImageAndIdMap.put("movieImagePath", movieImage);
		movieImageAndIdMap.put("movieName", movieName);

		return movieImageAndIdMap;
	}
	
	public Map<String, Object> getF1MainRace(String id, String name, String city, String country, String date, String time, String raceType, String imagePath, String circuitGuide) {
		
		Map<String, Object> raceMap = new HashMap<String, Object>();
		
		raceMap.put("id", id);
		raceMap.put("name", name);
		raceMap.put("city", city);
		raceMap.put("country", country);
		raceMap.put("date", date);
		raceMap.put("time", time);
		raceMap.put("type", raceType);
		raceMap.put("imagePath", imagePath);
		raceMap.put("circuitGuide", circuitGuide);
		
		return raceMap;
	}
	
	public Map<String, Object> getF1SessionSchedule(String name, String date, String time, String id, String raceTypeId, String practiceId) {
		
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		
		sessionMap.put("name", name);
		sessionMap.put("time", time);
		sessionMap.put("date", date);
		sessionMap.put("id", id);
		sessionMap.put("type", raceTypeId);
		sessionMap.put("practiceId", practiceId);
		
		return sessionMap;
	}
	
}
