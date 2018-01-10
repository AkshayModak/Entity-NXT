package com.nextrr.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;

import one.DatabaseUtils;
import one.DefaultObjects;
import one.NextrrUtils;

public class FantasyCricketHelper implements Serializable {

	List<Map<String, Object>> team1FallOfWickets = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> team2FallOfWickets = new ArrayList<Map<String, Object>>();
	
	int team1Score = 0;
	int team2Score = 0;
	int team1Wickets = 0;
	int team2Wickets = 0;
	int team1BallsFaced = 0;
	int team2BallsFaced = 0;
	
	int singles = 0;
	int doubles = 0;
	int triples = 0;
	int fours = 0;
	int sixes = 0;
	int dots = 0;
	int wickets = 0;
	int wide = 0;
	int nb = 5;
	
	List<Map<String, Object>> team1PlayerDetailsDuringMatch = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> team2PlayerDetailsDuringMatch = new ArrayList<Map<String, Object>>();

	List<Map<String, Object>> team1PlayerDetails = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> team2PlayerDetails = new ArrayList<Map<String, Object>>();
	Map<String, Object> team1MatchInfoMap = new HashMap<String, Object>();
	Map<String, Object> team2MatchInfoMap = new HashMap<String, Object>();
	Map<String, Object> team1WicketAssistedByMap = new HashMap<String, Object>();
	Map<String, Object> team2WicketAssistedByMap = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public String playCricket(MultivaluedMap<String, String> params) {
		String tossPreference = params.getFirst("tossPreference");

		if (tossPreference == null) {
			short randomNumber = NextrrUtils.getShortRandomNumber(Short.valueOf("0"), Short.valueOf("1"));
			List<String> preferenceList = new ArrayList<String>();

			if (1 == randomNumber) {
				short innerRandomNumber = NextrrUtils.getShortRandomNumber(Short.valueOf("0"), Short.valueOf("1"));
				String computerPreference = "computer";
				if (1 == innerRandomNumber) {
					computerPreference = computerPreference + "-bowl";
				} else if (0 == innerRandomNumber) {
					computerPreference = computerPreference + "-bat";
				}
				preferenceList.add(computerPreference);
				return new Gson().toJson(preferenceList);
			} else if (0 == randomNumber) {
				preferenceList.add("user");
				return new Gson().toJson(preferenceList);
			}
		}

		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();

		for (String value : params.get("userEleven")) {
			value = value.substring(1, value.length()-1);           //remove curly brackets
			String[] keyValuePairs = value.split(",");              //split the string to creat key-value pairs
			Map<String, Object> map = new HashMap<String, Object>();

			for(String pair : keyValuePairs)                        //iterate over the pairs
			{
			    String[] entry = pair.split(":");                   //split the pairs to get key and value
			    entry[0] = entry[0].replaceAll("^\"|\"$", "");
			    String objectValue = entry[1].replaceAll("^\"|\"$", "");
			    if (NextrrUtils.isNumeric(objectValue)) {
			        int intValue = Integer.valueOf(entry[1].replaceAll("^\"|\"$", ""));
			        map.put(entry[0], intValue);
			    } else {
			        map.put(entry[0], objectValue);          //add them to the hashmap and trim whitespaces
			    }
			}
			paramList.add(map);
		}

		int positionIndex = 0;
		for (Map<String, Object> paramMap : paramList) {
			paramMap.put("position", positionIndex);
			positionIndex = positionIndex + 1;
		}

		List<Map<String, Object>> computerTeamList = new ArrayList<Map<String, Object>>();

		for (String value : params.get("computerPlayers")) {
			value = value.substring(1, value.length()-1);           //remove curly brackets
			String[] keyValuePairs = value.split(",");              //split the string to creat key-value pairs
			Map<String, Object> map = new HashMap<String, Object>();

			for(String pair : keyValuePairs)                        //iterate over the pairs
			{
			    String[] entry = pair.split(":");                   //split the pairs to get key and value
			    entry[0] = entry[0].replaceAll("^\"|\"$", "");
			    String objectValue = entry[1].replaceAll("^\"|\"$", "");
			    if (NextrrUtils.isNumeric(objectValue)) {
			        int intValue = Integer.valueOf(entry[1].replaceAll("^\"|\"$", ""));
			        map.put(entry[0], intValue);
			    } else {
			        map.put(entry[0], objectValue);          //add them to the hashmap and trim whitespaces
			    }
			}
			computerTeamList.add(map);
		}

		positionIndex = 0;
		for (Map<String, Object> paramMap : computerTeamList) {
			paramMap.put("position", positionIndex);
			positionIndex = positionIndex + 1;
		}

		setComputerPlayerDetails(computerTeamList);
		setTeam2PlayerDetails(paramList);
		playFirstInning();
		playSecondInning();

		int team1Score = (int) team1MatchInfoMap.get("teamScore");
		List<Map<String, Object>> team1BatsmanScoresList = (List<Map<String, Object>>) team1MatchInfoMap.get("batsmanScoresList");
		List<Map<String, Object>> team1BowlerDetails = (List<Map<String, Object>>) team1MatchInfoMap.get("bowlerDetails");
		int team1BallFaced = (int) team1MatchInfoMap.get("teamBallsFaced");
		List<Map<String, Object>> team1BatsmanBallsFacedDetails = (List<Map<String, Object>>) team1MatchInfoMap.get("batsmanBallsFacedList");
		List<Map<String, Object>> team1BatsmanFoursList = (List<Map<String, Object>>) team1MatchInfoMap.get("batsmanFoursList");
		List<Map<String, Object>> team1BatsmanSixesList = (List<Map<String, Object>>) team1MatchInfoMap.get("batsmanSixesList");
		List<Map<String, Object>> team1FallOfWicketDetails = (List<Map<String, Object>>) team1MatchInfoMap.get("fallOfWickets");

		int index = 0;
		List<Map<String, Object>> batsmanDetailsList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> fallOfWicketList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> batsman : team1BatsmanScoresList) {
			Map<String, Object> batsmanDetailsMap = new HashMap<String, Object>();
			String str_index = String.valueOf(index + 1);
			Map<String, Object> assistedByMap = (Map<String, Object>) team1WicketAssistedByMap.get(str_index);
			if (assistedByMap == null) {
				assistedByMap = new HashMap<String, Object>();
				if (Integer.valueOf((int) team1BatsmanBallsFacedDetails.get(index).get(str_index)) == 0) {
					assistedByMap.put("assistedBy", "");
				} else {
					assistedByMap.put("assistedBy", "Not Out");
				}
			}
			String name = team1PlayerDetails.get(index).get("firstName") +" "+ team1PlayerDetails.get(index).get("lastName");
			
			batsmanDetailsMap.put("batsmanInfo", name);
			batsmanDetailsMap.put("assistedBy", assistedByMap.get("assistedBy"));
			batsmanDetailsMap.put("runsScored", batsman.get(str_index));
			batsmanDetailsMap.put("ballsFaced", team1BatsmanBallsFacedDetails.get(index).get(str_index));
			batsmanDetailsMap.put("foursHit", team1BatsmanFoursList.get(index).get(str_index));
			batsmanDetailsMap.put("sixesHit", team1BatsmanSixesList.get(index).get(str_index));
			batsmanDetailsList.add(batsmanDetailsMap);
			index = index + 1;
		}
		
		index = 0;
		for (Map<String, Object> fow : team1FallOfWicketDetails) {
			index = index + 1;
			Map<String, Object> batsmanDetail =  (Map<String, Object>) fow.get("batsman");
			String batsmanName = batsmanDetail.get("firstName") + " " + batsmanDetail.get("lastName");
			Map<String, Object> fowMap = new HashMap<String, Object>();
			fowMap.put("fow", index +"-"+ fow.get("score")+" ("+batsmanName+" "+getTotalOversFaced((int) fow.get("wicketBallNo"))+")");
			fallOfWicketList.add(fowMap);
		}
		
		List<Map<String, String>> tempTeam1BowlerDetails = new ArrayList<Map<String, String>>();
		for (Map<String, Object> bowlerDetail : team1BowlerDetails) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("name", bowlerDetail.get("firstName") + " "+bowlerDetail.get("lastName"));
			tempMap.put("wides", String.valueOf(bowlerDetail.get("widesBowled")));
			tempMap.put("nobs", String.valueOf(bowlerDetail.get("nobsBowled")));
			tempMap.put("runsGiven", String.valueOf(bowlerDetail.get("runsExhausted")));
			tempMap.put("overs", String.valueOf(bowlerDetail.get("oversBowled")));
			tempMap.put("wickets", String.valueOf(bowlerDetail.get("wicketsTaken")));
			tempTeam1BowlerDetails.add(tempMap);
		}
		
		List<Object> resultList = new ArrayList<Object>();
		resultList.add(tossPreference);
		resultList.add(batsmanDetailsList);
		resultList.add(fallOfWicketList);
		resultList.add(tempTeam1BowlerDetails);
		resultList.add(team1Score+"/"+team1FallOfWicketDetails.size() + " ("+getTotalOversFaced(team1BallFaced)+")");
		
		List<Object> secondInningDetails = getSecondInningDetails(team2MatchInfoMap);
		
		for (Object secondInningDetail : secondInningDetails) {
			resultList.add(secondInningDetail);
		}
		
		return new Gson().toJson(resultList);
	}
	
	private void playFirstInning() {
		
		int batsmanOnStrike = 1;
		int batsmanOnNonStrike = 2;
		int lastBowler = -1;
		Map<String, Object> currentBowler = new HashMap<String, Object>();
		List<Map<String, Integer>> batsmanScoresList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> batsmanBallsFacedList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> batsmanFoursList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> batsmanSixesList = new ArrayList<Map<String, Integer>>();
		
		createBatsmanScoresMap(batsmanScoresList);
		createBatsmanBallsFacedMap(batsmanBallsFacedList);
		createBatsmanFoursList(batsmanFoursList);
		createBatsmanSixesList(batsmanSixesList);
		List<Map<String, Object>> bowlerList = getBowlersFromPlayerList(team2PlayerDetails);

		team1PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
		team1PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
		
		for (int j = 0; j < 300; j++) {
			team1BallsFaced = team1BallsFaced + 1;
			if (j == 0) {
				currentBowler = getBowler(team2PlayerDetails, j, lastBowler);
			}
			if (team1Wickets < 10) {
				int run = getRun(getPlayerDetails(batsmanOnStrike), j, team1Wickets, currentBowler);
				if (1 == run) {
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 1);
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					int temp = batsmanOnStrike;
					batsmanOnStrike = batsmanOnNonStrike;
					batsmanOnNonStrike = temp;
					team1Score = team1Score + 1;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 1;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (2 == run) {
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 2);
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team1Score = team1Score + 2;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 2;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (3 == run) {
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 3);
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					int temp = batsmanOnStrike;
					batsmanOnStrike = batsmanOnNonStrike;
					batsmanOnNonStrike = temp;
					team1Score = team1Score + 3;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 3;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (4 == run) {
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 4);
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team1Score = team1Score + 4;
					int batsmanFoursScored = (batsmanFoursList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanFoursList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanFoursScored + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 4;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (6 == run) {
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 6);
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team1Score = team1Score + 6;
					int batsmanSixesScored = (batsmanSixesList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanSixesList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanSixesScored + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 6;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (0 == run) {
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted");
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (-1 == run) {
					
					Map<String, Object> fallOfWicketsMap = new HashMap<String, Object>();
					Map<String, Object> batsmanDetailMap = team1PlayerDetailsDuringMatch.get(batsmanOnStrike -1);
					Map<String, Object> assistedByMap = new HashMap<String, Object>();
					
					String assistedBy = getBowlerAssistedBy(team2PlayerDetails, currentBowler);
					assistedByMap.put("assistedBy", assistedBy);
					team1WicketAssistedByMap.put(String.valueOf(batsmanOnStrike), assistedByMap);
					
					int batsmanBallsFaced = (batsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					batsmanDetailMap.put("ballFaced", batsmanBallsFaced);
					int batsmanRuns = (batsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					batsmanDetailMap.put("batsmanRuns", batsmanRuns);
					batsmanDetailMap.put("fallOfWicketRuns", team1Score);
					batsmanDetailMap.put("fallOfWicketBalls", team1BallsFaced);
					batsmanDetailMap.put("position", batsmanOnStrike);
					
					team1PlayerDetailsDuringMatch.set(batsmanOnStrike -1, batsmanDetailMap);
					
					fallOfWicketsMap.put("score", team1Score);
					fallOfWicketsMap.put("batsman", team1PlayerDetails.get(batsmanOnStrike - 1));
					fallOfWicketsMap.put("wicketBallNo",  team1BallsFaced);
					
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int wicketsTaken = (int) currentBowler.get("wicketsTaken") + 1;
								bowlerMap.put("wicketsTaken", wicketsTaken);
								bowlerMap.put("batsman", team2PlayerDetails.get(batsmanOnStrike - 1));
								bowlerList.set(a, bowlerMap);
							}
						}
					}
					
					(batsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					if (batsmanOnStrike > batsmanOnNonStrike) {
						if (batsmanOnStrike != 11) {
							batsmanOnStrike = batsmanOnStrike + 1;
						}
					} else {
						if (batsmanOnNonStrike != 11) {
							batsmanOnStrike = batsmanOnNonStrike + 1;
						}
					}
					team1PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
					team1Wickets = team1Wickets + 1;
					team1FallOfWickets.add(fallOfWicketsMap);
				}
				if (-2 == run) {
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int widesBowled = (int) currentBowler.get("widesBowled") + 1;
								bowlerMap.put("widesBowled", widesBowled);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (-3 == run) {
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int nobsBowled = (int) currentBowler.get("nobsBowled") + 1;
								bowlerMap.put("nobsBowled", nobsBowled);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (j%6 == 0) {
					int temp = batsmanOnNonStrike;
					batsmanOnNonStrike = batsmanOnStrike;
					batsmanOnStrike = temp;
					if (currentBowler != null && currentBowler.containsKey("position")) {
						lastBowler = (int) currentBowler.get("position");
					}
					currentBowler = getBowler(team2PlayerDetails, j, lastBowler);
					if (currentBowler.isEmpty()) {
						currentBowler = null;
					}
				}
				if (currentBowler != null) {
					for (int a = 0; a < bowlerList.size(); a++) {
						Map<String, Object> bowlerMap = bowlerList.get(a);
						if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
							int ballsBowled = (int) currentBowler.get("ballsBowled") + 1;
							bowlerMap.put("ballsBowled", ballsBowled);
							bowlerMap.put("oversBowled", getTotalOversFaced(ballsBowled));
							bowlerList.set(a, bowlerMap);
						}
					}
				}
			} else {
				break;
			}
		}

		if (batsmanOnStrike <= 11 && team1Wickets < 10) {
			team1MatchInfoMap.put("batsmanOnStrike", getPlayerDetails(Integer.valueOf(batsmanOnStrike)));
			team1MatchInfoMap.put("batsmanOnNonStrike", getPlayerDetails(Integer.valueOf(batsmanOnNonStrike)));
		} else {
			team1MatchInfoMap.put("batsmanOnStrike", getPlayerDetails(Integer.valueOf(batsmanOnStrike)));
		}
		
		team1MatchInfoMap.put("batsmanScoresList", batsmanScoresList);
		team1MatchInfoMap.put("batsmanBallsFacedList", batsmanBallsFacedList);
		team1MatchInfoMap.put("teamBallsFaced", team1BallsFaced);
		team1MatchInfoMap.put("batsmanFoursList", batsmanFoursList);
		team1MatchInfoMap.put("batsmanSixesList", batsmanSixesList);
		team1MatchInfoMap.put("fallOfWickets", team1FallOfWickets);
		team1MatchInfoMap.put("teamScore", team1Score);
		team1MatchInfoMap.put("bowlerDetails", bowlerList);
		team1MatchInfoMap.put("wicketAssistedByMap", team1WicketAssistedByMap);
	}
	
	void playSecondInning() {
		
		int batsmanOnStrike = 1;
		int batsmanOnNonStrike = 2;
		int lastBowler = -1;
		Map<String, Object> currentBowler = new HashMap<String, Object>();
		List<Map<String, Integer>> team2BatsmanScoresList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> team2BatsmanBallsFacedList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> team2BatsmanFoursList = new ArrayList<Map<String, Integer>>();
		List<Map<String, Integer>> team2BatsmanSixesList = new ArrayList<Map<String, Integer>>();
		
		createBatsmanScoresMap(team2BatsmanScoresList);
		createBatsmanBallsFacedMap(team2BatsmanBallsFacedList);
		createBatsmanFoursList(team2BatsmanFoursList);
		createBatsmanSixesList(team2BatsmanSixesList);
		List<Map<String, Object>> bowlerList = getBowlersFromPlayerList(team1PlayerDetails);
		
		team2PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
		team2PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
		
		for (int j = 0; j < 300; j++) {
			team2BallsFaced = team2BallsFaced + 1;
			if (j == 0) {
				currentBowler = getBowler(team1PlayerDetails, j, lastBowler);
			}
			if (team2Wickets < 10) {
				int run = getRun(getTeam2PlayerDetails(batsmanOnStrike), j, team2Wickets, currentBowler);
				if (1 == run) {
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 1);
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					int temp = batsmanOnStrike;
					batsmanOnStrike = batsmanOnNonStrike;
					batsmanOnNonStrike = temp;
					team2Score = team2Score + 1;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 1;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (2 == run) {
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 2);
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team2Score = team2Score + 2;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 2;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (3 == run) {
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 3);
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					int temp = batsmanOnStrike;
					batsmanOnStrike = batsmanOnNonStrike;
					batsmanOnNonStrike = temp;
					team2Score = team2Score + 3;
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 3;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (4 == run) {
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 4);
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team2Score = team2Score + 4;
					int batsmanFoursScored = (team2BatsmanFoursList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanFoursList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanFoursScored + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 4;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (6 == run) {
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanScoresList.get(batsmanOnStrike - 1)).put(String.valueOf(batsmanOnStrike), batsmanRuns + 6);
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					team2Score = team2Score + 6;
					int batsmanSixesScored = (team2BatsmanSixesList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanSixesList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanSixesScored + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted") + 6;
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (0 == run) {
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int bowlerRuns = (int) currentBowler.get("runsExhausted");
								bowlerMap.put("runsExhausted", bowlerRuns);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (-1 == run) {
					
					Map<String, Object> fallOfWicketsMap = new HashMap<String, Object>();
					Map<String, Object> batsmanDetailMap = team2PlayerDetailsDuringMatch.get(batsmanOnStrike -1);
					Map<String, Object> assistedByMap = new HashMap<String, Object>();
					
					String assistedBy = getBowlerAssistedBy(team1PlayerDetails, currentBowler);
					assistedByMap.put("assistedBy", assistedBy);
					team2WicketAssistedByMap.put(String.valueOf(batsmanOnStrike), assistedByMap);
					
					int batsmanBallsFaced = (team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).get((String.valueOf(batsmanOnStrike)));
					batsmanDetailMap.put("ballFaced", batsmanBallsFaced);
					int batsmanRuns = (team2BatsmanScoresList.get(batsmanOnStrike - 1)).get((String.valueOf(batsmanOnStrike)));
					batsmanDetailMap.put("batsmanRuns", batsmanRuns);
					batsmanDetailMap.put("fallOfWicketRuns", team1Score);
					batsmanDetailMap.put("fallOfWicketBalls", team2BallsFaced);
					batsmanDetailMap.put("position", batsmanOnStrike);
					
					team2PlayerDetailsDuringMatch.set(batsmanOnStrike -1, batsmanDetailMap);
					
					fallOfWicketsMap.put("score", team2Score);
					fallOfWicketsMap.put("batsman", team2PlayerDetails.get(batsmanOnStrike - 1));
					fallOfWicketsMap.put("wicketBallNo", team2BallsFaced);
					
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int wicketsTaken = (int) currentBowler.get("wicketsTaken") + 1;
								bowlerMap.put("wicketsTaken", wicketsTaken);
								bowlerMap.put("batsman", team2PlayerDetails.get(batsmanOnStrike - 1));
								bowlerList.set(a, bowlerMap);
							}
						}
					}
					
					(team2BatsmanBallsFacedList.get(batsmanOnStrike -1)).put(String.valueOf(batsmanOnStrike), batsmanBallsFaced + 1);
					if (batsmanOnStrike > batsmanOnNonStrike) {
						if (batsmanOnStrike != 11) {
							batsmanOnStrike = batsmanOnStrike + 1;
						}
					} else {
						if (batsmanOnNonStrike != 11) {
							batsmanOnStrike = batsmanOnNonStrike + 1;
						}
					}
					team2PlayerDetailsDuringMatch.add(new HashMap<String, Object>());
					team2Wickets = team2Wickets + 1;
					team2FallOfWickets.add(fallOfWicketsMap);
				}
				if (-2 == run) {
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int widesBowled = (int) currentBowler.get("widesBowled") + 1;
								bowlerMap.put("widesBowled", widesBowled);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (-3 == run) {
					if (currentBowler != null) {
						for (int a = 0; a < bowlerList.size(); a++) {
							Map<String, Object> bowlerMap = bowlerList.get(a);
							if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
								int nobsBowled = (int) currentBowler.get("nobsBowled") + 1;
								bowlerMap.put("nobsBowled", nobsBowled);
								bowlerList.set(a, bowlerMap);
							}
						}
					}
				}
				if (j%6 == 0) {
					int temp = batsmanOnNonStrike;
					batsmanOnNonStrike = batsmanOnStrike;
					batsmanOnStrike = temp;
					if (currentBowler != null && currentBowler.containsKey("position")) {
						lastBowler = (int) currentBowler.get("position");
					}
					currentBowler = getBowler(team1PlayerDetails, j, lastBowler);
					if (currentBowler.isEmpty()) {
						currentBowler = null;
					}
				}
				if (currentBowler != null) {
					for (int a = 0; a < bowlerList.size(); a++) {
						Map<String, Object> bowlerMap = bowlerList.get(a);
						if (((int) currentBowler.get("position")) == ((int) bowlerMap.get("position"))) {
							int ballsBowled = (int) currentBowler.get("ballsBowled") + 1;
							bowlerMap.put("ballsBowled", ballsBowled);
							bowlerMap.put("oversBowled", getTotalOversFaced(ballsBowled));
							bowlerList.set(a, bowlerMap);
						}
					}
				}
			} else {
				break;
			}
			if (team2Score > team1Score) {
				break;
			}
		}

		if (batsmanOnStrike <= 11 && team2Wickets < 10) {
			team2MatchInfoMap.put("batsmanOnStrike", getTeam2PlayerDetails(Integer.valueOf(batsmanOnStrike)));
			team2MatchInfoMap.put("batsmanOnNonStrike", getTeam2PlayerDetails(Integer.valueOf(batsmanOnNonStrike)));
		} else {
			team2MatchInfoMap.put("batsmanOnStrike", getTeam2PlayerDetails(Integer.valueOf(batsmanOnStrike)));
		}
		
		team2MatchInfoMap.put("batsmanScoresList", team2BatsmanScoresList);
		team2MatchInfoMap.put("batsmanBallsFacedList", team2BatsmanBallsFacedList);
		team2MatchInfoMap.put("teamBallsFaced", team2BallsFaced);
		team2MatchInfoMap.put("batsmanFoursList", team2BatsmanFoursList);
		team2MatchInfoMap.put("batsmanSixesList", team2BatsmanSixesList);
		team2MatchInfoMap.put("fallOfWickets", team2FallOfWickets);
		team2MatchInfoMap.put("teamScore", team2Score);
		team2MatchInfoMap.put("bowlerDetails", bowlerList);
		team2MatchInfoMap.put("wicketAssistedByMap", team2WicketAssistedByMap);
	}
	
	int getRun(Map<String, Object> player, int ball, int wicketsFell, Map<String, Object> currentBowler) {
		int rating = (int) player.get("rating");
		
		setRunsBasedOnCurrentScenario(rating, ball, player, wicketsFell, 5, 10);
		List<Integer> totalBalls = new ArrayList<Integer>();
		
		for (int i = 0; i < singles; i++) {
			totalBalls.add(1);
		}
		for (int i = 0; i < doubles; i++) {
			totalBalls.add(2);
		}
		for (int i = 0; i < triples; i++) {
			totalBalls.add(3);
		}
		for (int i = 0; i < fours; i++) {
			totalBalls.add(4);
		}
		for (int i = 0; i < sixes; i++) {
			totalBalls.add(6);
		}
		for (int i = 0; i < dots; i++) {
			totalBalls.add(0);
		}
		for (int i = 0; i < wickets; i++) {
			totalBalls.add(-1);
		}
		for (int i = 0; i < wide; i++) {
			totalBalls.add(-2);
		}
		for (int i = 0; i < nb; i++) {
			totalBalls.add(-3);
		}
		
		Collections.shuffle(totalBalls);

		return totalBalls.get(11);
	}
	
	private void setTeam2PlayerDetails(List<Map<String, Object>> paramList) {
		for (Map<String, Object> paramMap : paramList) {
			team2PlayerDetails.add(paramMap);
		}
	}
	
	private void setComputerPlayerDetails(List<Map<String, Object>> paramList) {
		for (Map<String, Object> paramMap : paramList) {
			team1PlayerDetails.add(paramMap);
		}
	}
	
	Map<String, Object> getPlayerDetails(int position) {
		position = position - 1;
		return team1PlayerDetails.get(position);
	}
	
	Map<String, Object> getTeam2PlayerDetails(int position) {
		position = position - 1;
		return team2PlayerDetails.get(position);
	}
	
	String getTotalOversFaced(int ballsFaced) {
		int oversFaced = ballsFaced/6;
		int temp = oversFaced * 6;
		int ballsRemaining = ballsFaced - temp;
		
		return oversFaced+"."+ballsRemaining;
	}
	
	void createBatsmanScoresMap(List<Map<String, Integer>> batsmanScoresList) {
		for (int index = 1; index <= 11; index++) {
			Map<String, Integer> tempMap = (Map<String, Integer>) new HashMap();
			tempMap.put(String.valueOf(index), 0);
			batsmanScoresList.add(tempMap);
		}
	}
	
	void createBatsmanBallsFacedMap(List<Map<String, Integer>> batsmanBallsFacedList) {
		
		for (int index = 1; index <= 11; index++) {
			Map<String, Integer> tempMap = (Map<String, Integer>) new HashMap();
			tempMap.put(String.valueOf(index), 0);
			batsmanBallsFacedList.add(tempMap);
		}
	}
	
	void createBatsmanFoursList(List<Map<String, Integer>> batsmanFoursList) {
		for (int index = 1; index <= 11; index++) {
			Map<String, Integer> tempMap = (Map<String, Integer>) new HashMap();
			tempMap.put(String.valueOf(index), 0);
			batsmanFoursList.add(tempMap);
		}
	}
	
	void createBatsmanSixesList(List<Map<String, Integer>> batsmanSixesList) {
		for (int index = 1; index <= 11; index++) {
			Map<String, Integer> tempMap = (Map<String, Integer>) new HashMap();
			tempMap.put(String.valueOf(index), 0);
			batsmanSixesList.add(tempMap);
		}
	}
	
	//worse the pitch better the pitchRating
	void setRunsBasedOnCurrentScenario(int rating, int ball, Map<String, Object> currentBowler, int wicketsFell, int pitchRating, int bowlerRating) {
		singles = 10 * rating;
		doubles = 3 * rating;
		triples = 1 * rating;
		fours = 3 * rating;
		sixes = 1 * rating;
		wickets = 5;
		wide = 10;
		nb = 3;
		
		if (ball < 240) {
			if (rating < 3) {
				dots = 50 * rating * 2 * bowlerRating/2;
				wickets = wickets * 3 * bowlerRating/2;
			} else if (rating < 5 && rating > 2){
				dots = 80 * bowlerRating/2;
				wickets = wickets * 2 * bowlerRating/2;
			} else {
				dots = 50 * bowlerRating/2;
			}
		} else {
			if (rating < 3) {
				dots = 50 * rating * 2 * bowlerRating/2;
				wickets = wickets * 3 * bowlerRating/2;
			} else if (rating < 5 && rating > 2){
				dots = 80 * bowlerRating/2;
				wickets = wickets * 2 * bowlerRating/2;
			} else {
				sixes = sixes * 2;
				fours = fours * 2 * bowlerRating/2;
				wickets = wickets * 2 * bowlerRating/2;
				dots = 40 * bowlerRating/2;
			}
		}
	}
	
	List<Map<String, Object>> getBowlersFromPlayerList(List<Map<String, Object>> playerList) {
		List<Map<String, Object>> bowlerList = new ArrayList<Map<String, Object>>();

		playerList.forEach((player) -> {
			String role = (String) player.get("role");
			if ("bowler".equals(role) || "all-rounder-spinner".equals(role) || "all-rounder-fast".equals(role) || "spinner".equals(role)) {
				bowlerList.add(player);
			}
		});

		return bowlerList;
	}
	
	Map<String, Object> getBowler(List<Map<String, Object>> playerList, int currentBall, int lastBowler) {
		
		List<Map<String, Object>> bowlerList = getBowlersFromPlayerList(playerList);
		Map<String, Object> currentBowlerMap = new HashMap<String, Object>();
		
		Collections.shuffle(bowlerList);
		for (Map<String, Object> bowlerMap : bowlerList) {
			if (currentBall%6 == 0) {
				if (bowlerMap.get("runsExhausted") == null) {
					bowlerMap.put("runsExhausted", 0);
				}
				if (bowlerMap.get("wicketsTaken") == null) {
					bowlerMap.put("wicketsTaken", 0);
				}
				if (bowlerMap.get("widesBowled") == null) {
					bowlerMap.put("widesBowled", 0);
				}
				if (bowlerMap.get("nobsBowled") == null) {
					bowlerMap.put("nobsBowled", 0);
				}
				if (bowlerMap.get("ballsBowled") == null) {
					bowlerMap.put("ballsBowled", 0);
				} else if ((int) bowlerMap.get("ballsBowled") < 60){
					return bowlerMap;
				} else {
					continue;
				}
				return bowlerMap;
			}
		}
		return currentBowlerMap;
	}
	
	String getBowlerAssistedBy(List<Map<String, Object>> playerDetails, Map<String, Object> bowler) {
		List<Map<String, Object>> assistedByList = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> fielderDetail : playerDetails) {
			if (fielderDetail.get("role").equals("wicketkeeper")) {
				for (int i=0; i < 8; i++) {
					assistedByList.add(fielderDetail);
				}
			} else {
				assistedByList.add(fielderDetail);
			}
		}
		Collections.shuffle(assistedByList);
		Map<String, Object> assistedByMap = (Map<String, Object>) assistedByList.get(11);
		
		List<String> wicketBy = new ArrayList<String>();
		
		if (assistedByMap.get("role").equals("wicketkeeper")) {
			for (int i = 0; i < 5; i++) {
				wicketBy.add("caught");
			}
			for (int i = 0; i < 3; i++) {
				wicketBy.add("bowled");
			}
			if (("spinner").equals(bowler.get("role")) || ("all-rounder-spinner").equals(bowler.get("role"))) {
				wicketBy.add("stumped");
			}
		} else {
			for (int i = 0; i < 5; i++) {
				wicketBy.add("caught");
			}
			for (int i = 0; i < 3; i++) {
				wicketBy.add("bowled");
			}
			wicketBy.add("run-out");
		}
		
		Collections.shuffle(wicketBy);
		if (wicketBy.get(2).equals("caught")) {
			return "c "+assistedByMap.get("lastName")+ " b "+bowler.get("lastName");
		} else if (wicketBy.get(2).equals("stumped")) {
			return "stumped "+assistedByMap.get("lastName")+ " b "+bowler.get("lastName");
		} else if (wicketBy.get(2).equals("run-out")) {
			return "run-out ("+assistedByMap.get("lastName")+ ")";
		} else if (wicketBy.get(2).equals("bowled")) {
			return "b "+bowler.get("lastName");
		}
		
		return "";
	}
	
	public String getAllPlayers() {
		DatabaseUtils dbUtils = new DatabaseUtils();
		Map<String, Object> resultMap = dbUtils.getAllEntityData("fantasy_cricket");
		List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> resultListMap : resultList) {
			resultListMap.put("countryName", NextrrUtils.getDescription("country_geo", "country_geo_id", (String) resultListMap.get("country_geo_id")));
			result.add(resultListMap);
		}

		return new Gson().toJson(result);
	}
	
	private List getSecondInningDetails(Map<String, Object> team2MatchInfoMap) {
		
		int team2Score = (int) team2MatchInfoMap.get("teamScore");
		List<Map<String, Object>> team2BatsmanScoresList = (List<Map<String, Object>>) team2MatchInfoMap.get("batsmanScoresList");
		List<Map<String, Object>> team2BowlerDetails = (List<Map<String, Object>>) team2MatchInfoMap.get("bowlerDetails");
		int team2BallFaced = (int) team2MatchInfoMap.get("teamBallsFaced");
		List<Map<String, Object>> team2BatsmanBallsFacedDetails = (List<Map<String, Object>>) team2MatchInfoMap.get("batsmanBallsFacedList");
		List<Map<String, Object>> team2BatsmanFoursList = (List<Map<String, Object>>) team2MatchInfoMap.get("batsmanFoursList");
		List<Map<String, Object>> team2BatsmanSixesList = (List<Map<String, Object>>) team2MatchInfoMap.get("batsmanSixesList");
		List<Map<String, Object>> team2FallOfWicketDetails = (List<Map<String, Object>>) team2MatchInfoMap.get("fallOfWickets");

		int index = 0;
		List<Map<String, Object>> batsmanDetailsList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> fallOfWicketList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> batsman : team2BatsmanScoresList) {
			Map<String, Object> batsmanDetailsMap = new HashMap<String, Object>();
			String str_index = String.valueOf(index + 1);
			Map<String, Object> assistedByMap = (Map<String, Object>) team2WicketAssistedByMap.get(str_index);
			if (assistedByMap == null) {
				assistedByMap = new HashMap<String, Object>();
				if (Integer.valueOf((int) team2BatsmanBallsFacedDetails.get(index).get(str_index)) == 0) {
					assistedByMap.put("assistedBy", "");
				} else {
					assistedByMap.put("assistedBy", "Not Out");
				}
			}
			String name = team2PlayerDetails.get(index).get("firstName") +" "+ team2PlayerDetails.get(index).get("lastName");
			
			batsmanDetailsMap.put("batsmanInfo", name);
			batsmanDetailsMap.put("assistedBy", assistedByMap.get("assistedBy"));
			batsmanDetailsMap.put("runsScored", batsman.get(str_index));
			batsmanDetailsMap.put("ballsFaced", team2BatsmanBallsFacedDetails.get(index).get(str_index));
			batsmanDetailsMap.put("foursHit", team2BatsmanFoursList.get(index).get(str_index));
			batsmanDetailsMap.put("sixesHit", team2BatsmanSixesList.get(index).get(str_index));
			batsmanDetailsList.add(batsmanDetailsMap);
			index = index + 1;
		}
		
		index = 0;
		for (Map<String, Object> fow : team2FallOfWicketDetails) {
			index = index + 1;
			Map<String, Object> batsmanDetail =  (Map<String, Object>) fow.get("batsman");
			String batsmanName = batsmanDetail.get("firstName") + " " + batsmanDetail.get("lastName");
			Map<String, Object> fowMap = new HashMap<String, Object>();
			fowMap.put("fow", index +"-"+ fow.get("score")+" ("+batsmanName+" "+getTotalOversFaced((int) fow.get("wicketBallNo"))+")");
			fallOfWicketList.add(fowMap);
		}
		
		List<Map<String, String>> tempTeam2BowlerDetails = new ArrayList<Map<String, String>>();
		for (Map<String, Object> bowlerDetail : team2BowlerDetails) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("name", bowlerDetail.get("firstName") + " "+bowlerDetail.get("lastName"));
			tempMap.put("wides", String.valueOf(bowlerDetail.get("widesBowled")));
			tempMap.put("nobs", String.valueOf(bowlerDetail.get("nobsBowled")));
			tempMap.put("runsGiven", String.valueOf(bowlerDetail.get("runsExhausted")));
			tempMap.put("overs", String.valueOf(bowlerDetail.get("oversBowled")));
			tempMap.put("wickets", String.valueOf(bowlerDetail.get("wicketsTaken")));
			tempTeam2BowlerDetails.add(tempMap);
		}
		
		List<Object> resultList = new ArrayList<Object>();
		resultList.add(batsmanDetailsList);
		resultList.add(fallOfWicketList);
		resultList.add(tempTeam2BowlerDetails);
		resultList.add(team2Score+"/"+team2FallOfWicketDetails.size() + " ("+getTotalOversFaced(team2BallFaced)+")");
		
		return resultList;
	}

	public String setFantasyCricketRecord(MultivaluedMap<String, String> params) {
        Map<String, Object> queryMap = new HashMap<String, Object>();

        String firstName = params.getFirst("firstName");
        String lastName = params.getFirst("lastName");
        String battingRating = params.getFirst("battingRating");
        String bowlingRating = params.getFirst("bowlingRating");
        String role = params.getFirst("role");
        String countryGeoId = params.getFirst("countryGeoId");
        String battingPosition = params.getFirst("battingPosition");

        queryMap.put("firstName", firstName);
        queryMap.put("lastName", lastName);
        queryMap.put("rating", battingRating);
        queryMap.put("bowlingRating", bowlingRating);
        queryMap.put("role", role);
        queryMap.put("country_geo_id", countryGeoId);
        queryMap.put("battingPosition", battingPosition);

        DatabaseUtils du = new DatabaseUtils();
        du.runCreateQuery("fantasy_cricket", queryMap);

        return new Gson().toJson("success");
    }

	public String removeFantasyCricketRecord(MultivaluedMap<String, String> params) {
		String fantasyCricketId = params.getFirst("fantasyCricketId");

		if (DefaultObjects.isNotEmpty(fantasyCricketId)) {
			DatabaseUtils du = new DatabaseUtils();
			du.runDeleteQuery("fantasy_cricket", "fantasy_cricket_id", fantasyCricketId);
		} else {
			return new Gson().toJson(new ArrayList<String>().add("error"));
		}

		return new Gson().toJson(new ArrayList<String>().add("success"));
    }

	public String setPlayAgainst(MultivaluedMap<String, String> params) {

		DatabaseUtils dbUtils = new DatabaseUtils();
		String playAgainst = params.getFirst("playAgainst");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("country_geo_id", playAgainst);
		Map<String, Object> fantasyCricketMap = dbUtils.getEntityDataWithConditions("fantasy_cricket", paramMap);

		List<Map<String, Object>> resultList = (List<Map<String, Object>>) fantasyCricketMap.get("result");
		resultList = NextrrUtils.sortCricketPlayingElevenMap(resultList);

		if (resultList == null) {
			return new Gson().toJson("error");
		}
		return new Gson().toJson(resultList);
	}

	public String updateFantasyCricket(MultivaluedMap<String, String> params) {

        Map<String, Object> queryMap = new HashMap<String, Object>();

        String fantasyCricketId = params.getFirst("fantasyCricketId");
        String firstName = params.getFirst("firstName");
        String lastName = params.getFirst("lastName");
        String battingRating = params.getFirst("battingRating");
        String bowlingRating = params.getFirst("bowlingRating");
        String role = params.getFirst("role");
        String countryGeoId = params.getFirst("countryGeoId");
        String battingPosition = params.getFirst("battingPosition");

        queryMap.put("fantasy_cricket_id", fantasyCricketId);
        queryMap.put("firstName", firstName);
        queryMap.put("lastName", lastName);
        queryMap.put("rating", battingRating);
        queryMap.put("bowlingRating", bowlingRating);
        queryMap.put("role", role);
        queryMap.put("country_geo_id", countryGeoId);
        queryMap.put("battingPosition", battingPosition);

        if (fantasyCricketId == null) {
            return new Gson().toJson("error");
        }

        DatabaseUtils du = new DatabaseUtils();
        du.runUpdateQuery("fantasy_cricket", queryMap, "fantasy_cricket_id");

        return new Gson().toJson("success");
	}
}