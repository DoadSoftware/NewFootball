package com.football.broadcaster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import com.football.model.*;
import com.football.service.FootballService;
import com.football.util.FootballFunctions;
import com.football.util.FootballUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.containers.Scene;
import com.football.containers.ScoreBug;

public class Santosh_Trophy extends Scene{
	
	public String session_selected_broadcaster = "SANTOSH_TROPHY";
	
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	private String logo_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_SantoshTrophy_2023\\Logos\\";
	private String colors_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_SantoshTrophy_2023\\Colours\\";
	private String photo_path = "C:\\Images\\SANTOSH-TROPHY\\";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	public ObjectMapper objectMapper = new ObjectMapper();
	
	public Santosh_Trophy() {
		super();
	}
	
	public ScoreBug updateScoreBug(PrintWriter print_writer,List<Scene> scenes, Match match) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, print_writer, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
			scorebug = populateExtraTime(true,scorebug,print_writer,null,match,session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(PrintWriter print_writer,String whatToProcess,Match match,Clock clock, FootballService footballService, List<Scene> scenes,
			String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException{
		
		if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			/*if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				System.out.println("hello");
				penalties = new ArrayList<String>();
			}*/
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penaltiesremove = new ArrayList<String>();
//					penalties.remove(penalties.size() - 1);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
					penalties = new ArrayList<String>();
				}
			}
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");

			for(String pen : penalties)
			{
				//System.out.println("ELSE LOOP - " + iHomeCont);
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");

//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
			
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					
//					penaltiesremove.add(String.valueOf(penalties.get(penalties.size() - 1)));
//					penalties.removeAll(penaltiesremove);
//					penalties.remove(penalties.size() - 1);
//					penaltiesremove = new ArrayList<String>();
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_STATS": case "POPULATE-RED_CARD": case "POPULATE-EXTRA_TIME": case "POPULATE-SCOREBUG_STATS_TWO":
		case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-FF-MATCHID": case "POPULATE-FF-PLAYINGXI": case "POPULATE-L3-BUG-DB":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-SPONSOR": case "POPULATE-L3-SUBSTITUTE":
		case "POPULATE-L3-MATCHPROMO": case "POPULATE-L3-STAFF": case "POPULATE-FF-MATCHSTATS": case "POPULATE-LT-BUG_REPLAY": case "POPULATE-FF-PROMO": 
		case "POPULATE-DOUBLE_PROMO": case "POPULATE-HERO-SPONSOR": case "POPULATE-POINTS_TABLE": case "POPULATE-FF-FORMATION": case "POPULATE-OFFICIALS":
		case "POPULATE-DOUBLE_SUBS": case "POPULATE-SCOREBUG-CARD": case "POPULATE-SCOREBUG-SUBS": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-HIGHLIGHT":
		case "POPULATE-PENALTY": case "POPULATE-L3-SINGLE_SUBSTITUTE": case "POPULATE-FF-PLAYINGXI_CHANGEON":
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-RED_CARD": case "POPULATE-EXTRA_TIME": case "POPULATE-SPONSOR": case "POPULATE-SCOREBUG_STATS_TWO":
			case "POPULATE-SCOREBUG-CARD":	case "POPULATE-SCOREBUG-SUBS": case "POPULATE-EXTRA_TIME_BOTH":
				break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				break;
			 case "POPULATE-FF-MATCHID": case "POPULATE-FF-FORMATION": case "POPULATE-FF-MATCHSTATS": case "POPULATE-FF-PROMO":
			 case "POPULATE-FF-PLAYINGXI_CHANGEON": case "POPULATE-POINTS_TABLE":
				 scenes.get(0).setScene_path(valueToProcess.split(",")[1]);
				 scenes.get(0).scene_load(print_writer,session_selected_broadcaster);
				 break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,print_writer, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-SCOREBUG_STATS_TWO":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					switch(scorebug.getLast_scorebug_stat().toUpperCase()) {
					case FootballUtil.YELLOW: case FootballUtil.RED: case FootballUtil.OFF_SIDE: case FootballUtil.SHOTS: case FootballUtil.POSSESSION: 
					case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.CORNERS: case FootballUtil.TACKLES: case "CORNERS_HOME": case "CORNERS_AWAY":
					case "YELLOW_HOME": case "YELLOW_AWAY": case "RED_HOME": case "RED_AWAY": case "YELLOW_CARD": case "YELLOW_RED": case "RED_CARD":
					case "SINGLE": case "DOUBLE": case "GOAL":
						processAnimation(print_writer, "ExtraInfo_Out", "START", session_selected_broadcaster,1);
						break;
					}
					TimeUnit.MILLISECONDS.sleep(100);
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,print_writer,match,session_selected_broadcaster);
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,print_writer,match,session_selected_broadcaster);
				}
				break;
			case "POPULATE-SCOREBUG-CARD":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					switch(scorebug.getLast_scorebug_stat().toUpperCase()) {
					case FootballUtil.YELLOW: case FootballUtil.RED: case FootballUtil.OFF_SIDE: case FootballUtil.SHOTS: case FootballUtil.POSSESSION: 
					case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.CORNERS: case FootballUtil.TACKLES: case "CORNERS_HOME": case "CORNERS_AWAY":
					case "YELLOW_HOME": case "YELLOW_AWAY": case "RED_HOME": case "RED_AWAY": case "YELLOW_CARD": case "YELLOW_RED": case "RED_CARD":
					case "SINGLE": case "DOUBLE": case "GOAL":
						processAnimation(print_writer, "ExtraInfo_Out", "START", session_selected_broadcaster,1);
						break;
					}
					TimeUnit.MILLISECONDS.sleep(100);
					scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]), Integer.valueOf(valueToProcess.split(",")[3]), match, session_selected_broadcaster);
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]), Integer.valueOf(valueToProcess.split(",")[3]), match, session_selected_broadcaster);
				}
				break;
			case "POPULATE-SCOREBUG-SUBS":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					switch(scorebug.getLast_scorebug_stat().toUpperCase()) {
					case FootballUtil.YELLOW: case FootballUtil.RED: case FootballUtil.OFF_SIDE: case FootballUtil.SHOTS: case FootballUtil.POSSESSION: 
					case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.CORNERS: case FootballUtil.TACKLES: case "CORNERS_HOME": case "CORNERS_AWAY":
					case "YELLOW_HOME": case "YELLOW_AWAY": case "RED_HOME": case "RED_AWAY": case "YELLOW_CARD": case "YELLOW_RED": case "RED_CARD":
					case "SINGLE": case "DOUBLE": case "GOAL":
						processAnimation(print_writer, "ExtraInfo_Out", "START", session_selected_broadcaster,1);
						break;
					}
					TimeUnit.MILLISECONDS.sleep(100);
					scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
					populateScorebugSubs(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]), footballService.getAllPlayer(), match, session_selected_broadcaster);
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
					populateScorebugSubs(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]), footballService.getAllPlayer(), match, session_selected_broadcaster);
				}
				break;
			case "POPULATE-EXTRA_TIME":
				populateExtraTime(false,scorebug,print_writer,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME_BOTH":
				populateExtraTimeBoth(false,scorebug,print_writer,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-RED_CARD":
				populateRedcard(false,scorebug,print_writer,match,session_selected_broadcaster);
				break;
			case "POPULATE-HIGHLIGHT":
				populateHighlight(print_writer, valueToProcess.split(",")[0], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER":
				//System.out.println("Value1 : " + valueToProcess.split(",")[1] + "Value2 : " + valueToProcess.split(",")[2]);
				for(NameSuper ns : footballService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(print_writer, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				populateNameSuperPlayer(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER-CARD":
				populateNameSuperCard(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(print_writer,valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-HERO-SPONSOR":
				populateAds(print_writer,valueToProcess.split(",")[1],valueToProcess.split(",")[2], match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(), footballService.getTeams(),
						match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI_CHANGEON":
				populatePlayingXIChangeOn(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(), footballService.getTeams(),
						match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-BUG-DB":
				for(Bugs bug : footballService.getBugs()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateBugsDB(print_writer, valueToProcess.split(",")[1], bug, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-LT-BUG_REPLAY":
				populateBug_replay(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-PENALTY":
				populateLtPenalty(print_writer, valueToProcess.split(",")[1],valueToProcess, footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(print_writer, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SUBSTITUTE":
				populateSubstitute(print_writer, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						footballService.getAllPlayer(),footballService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SINGLE_SUBSTITUTE":
				populateSingleSubstitute(print_writer, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),
						footballService.getAllPlayer(),footballService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHPROMO":
				populateMatchPromo(print_writer, valueToProcess.split(",")[1], match,footballService.getFixtures(),footballService.getTeams(),footballService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),footballService.getTeams(),
						footballService.getFixtures(),footballService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(print_writer, valueToProcess.split(",")[1], match,footballService.getFixtures(),footballService.getTeams(),footballService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table = null;
				
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML").exists()) {
					league_table = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML"));
				}
				
				populatePointsTable(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2],league_table.getLeagueTeams(),footballService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-L3-STAFF":
				for(Staff st : footballService.getStaffs()) {
					  if(st.getStaffId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateStaff(print_writer, valueToProcess.split(",")[1], st,footballService.getTeams(), match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-FF-FORMATION":
				populateFormation(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						footballService.getFormations(),match , session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_SUBS":
				populateDoubleSubs(print_writer, valueToProcess.split(",")[1],footballService.getTeams(),match, session_selected_broadcaster);
				break;
			case "POPULATE-OFFICIALS":
				populateOfficials(print_writer, valueToProcess.split(",")[1],footballService.getOfficials(),match, session_selected_broadcaster);
				break;
			}
			
		case "NAMESUPER_GRAPHICS-OPTIONS": 
			return objectMapper.writeValueAsString(footballService.getNameSupers());
		case "BUG_DB_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(footballService.getBugs());
		case "STAFF_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(footballService.getStaffs());
		case "PROMO_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(FootballFunctions.processAllFixtures(footballService));
			
		case "ANIMATE-IN-SCOREBUG": case "CLEAR-ALL": case "ANIMATE-OUT": case "ANIMATE-OUT-SCOREBUG_STAT": case"ANIMATE-OUT-RED_CARD":	case "ANIMATE-OUT-EXTRA_TIME":
		case "ANIMATE-IN-SPONSOR": case "ANIMATE-OUT-SPONSOR": case "ANIMATE-IN-FORMATION": case "ANIMATE-CHANGE_ON_FORMATION": case "ANIMATE-CHANGE_ON_FORMATION_WITHOUT_IMAGE":
		case "ANIMATE-CHANGE_ON": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER": case "ANIMATE-IN-NAMESUPERDB": 
		case "ANIMATE-IN-BUG-DB": case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-NAMESUPER_CARD": case "ANIMATE-IN-ASTON-ADS":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-MATCHPROMO": case "ANIMATE-IN-STAFF": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-BUG_REPLAY":
		case "ANIMATE-IN-PROMO": case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-FORMATION_WITHOUT_IMAGE":
		case "ANIMATE-IN-DOUBLE_SUBS": case "ANIMATE-IN-OFFICIALS": case "ANIMATE-IN-HIGHLIGHT": case "ANIMATE-IN-PENALTY": case "ANIMATE-IN-SINGLE_SUBSTITUTE":
		case "ANIMATE-IN-PLAYINGXI_CHANGEON":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-BUG_REPLAY":
				if(scorebug.isScorebug_on_screen() == true) {
					processAnimation(print_writer, "LT_In", "START", session_selected_broadcaster,1);
					TimeUnit.MILLISECONDS.sleep(200);
				}
				break;
			}
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-PENALTY":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-HIGHLIGHT":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "HIGHLIGHT";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-MATCHID":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-PLAYINGXI_CHANGEON":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "PLAYINGXI_CHANGEON";
				break;
			case "ANIMATE-IN-BUG-DB":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-BUG_REPLAY":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "BUG_REPLAY";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						processAnimation(print_writer, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						processAnimation(print_writer, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						processAnimation(print_writer, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-ASTON-ADS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "ASTON-ADS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-SINGLE_SUBSTITUTE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "SINGLE_SUBSTITUTE";
				break;
			case "ANIMATE-IN-MATCHPROMO":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "MATCHPROMO";
				break;
			case "ANIMATE-IN-STAFF":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-PROMO":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "PROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-POINTS_TABLE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-CHANGE_ON_FORMATION":
				populateChangeOnFormation(print_writer,Integer.valueOf(valueToProcess.split(",")[1]),valueToProcess.split(",")[2],match , session_selected_broadcaster);
				processAnimation(print_writer, "In", "CONTINUE", session_selected_broadcaster,2);
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In CONTINUE;");
				break;
			case "ANIMATE-CHANGE_ON":
				processAnimation(print_writer, "ChangeIn", "START", session_selected_broadcaster,1);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$group$FF$2nd*CONTAINER SET ACTIVE 1;");
				break;
			case "ANIMATE-IN-SCOREBUG":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Team_Score_Extra_GRP$Sponsor_On_Off$Sponsor_Top_Grp*CONTAINER SET ACTIVE 1;");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-SPONSOR":
				processAnimation(print_writer, "SponsorIn", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-IN-FORMATION":
				print_writer.println("LAYER1*EVEREST*STAGE START;");
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "FORMATION";
				break;
			case "ANIMATE-IN-FORMATION_WITHOUT_IMAGE":
				print_writer.println("LAYER1*EVEREST*STAGE START;");
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "FORMATION_WITHOUT";
				break;
			case "ANIMATE-IN-DOUBLE_SUBS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "DOUBLE_SUBS";
				break;
			case "ANIMATE-IN-OFFICIALS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
				which_graphics_onscreen = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG_STAT":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					switch(scorebug.getLast_scorebug_stat().toUpperCase()) {
					case FootballUtil.YELLOW: case FootballUtil.RED: case FootballUtil.OFF_SIDE: case FootballUtil.SHOTS: case FootballUtil.POSSESSION: 
					case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.CORNERS: case FootballUtil.TACKLES: case "CORNERS_HOME": case "CORNERS_AWAY":
					case "YELLOW_HOME": case "YELLOW_AWAY": case "RED_HOME": case "RED_AWAY": case "YELLOW_CARD": case "YELLOW_RED": case "RED_CARD":
					case "SINGLE": case "DOUBLE": case "GOAL":
						processAnimation(print_writer, "ExtraInfo_Out", "START", session_selected_broadcaster,1);
						break;
					}
				}
				scorebug.setLast_scorebug_stat("");
				scorebug.setScorebug_stat("");
				break;
			case"ANIMATE-OUT-RED_CARD":	
				processAnimation(print_writer, "RedCardInfo_Out", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-OUT-EXTRA_TIME":
				processAnimation(print_writer, "ExtraTime_Out", "START", session_selected_broadcaster,1);
				processAnimation(print_writer, "InjuryTime_Out", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-OUT-SPONSOR":
				processAnimation(print_writer, "SponsorOut", "START", session_selected_broadcaster,1);
				break;
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					processAnimation(print_writer, "Out", "START", session_selected_broadcaster,1);
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
					case "MATCHID": case "FORMATION": case "FORMATION_WITHOUT": case "MATCHSTATS": case "PROMO":
					case "PLAYINGXI_CHANGEON": case "POINTS_TABLE":
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,1);
						break;
					case "NAMESUPERDB": case "NAMESUPER": case "BUG-DB": case "SCOREUPDATE": case "MATCHSTATUS": case "NAMESUPER_CARD":
					case "ASTON-ADS": case "SUBSTITUTE": case "MATCHPROMO": case "STAFF": case "DOUBLE_PROMO": case "PLAYINGXI": 
					case "DOUBLE_SUBS": case "OFFICIALS": case "HIGHLIGHT": case "PENALTY": case "SINGLE_SUBSTITUTE":
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,2);
						TimeUnit.MILLISECONDS.sleep(400);
						break;
					case "BUG_REPLAY":
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,2);
						if(scorebug.isScorebug_on_screen() == true) {
							processAnimation(print_writer, "LT_Out", "START", session_selected_broadcaster, 1);
							is_infobar = true;
							scorebug.setScorebug_on_screen(true);
						}
						break;
					}
					which_graphics_onscreen = "";
					break;
			}
			break;
			}
		return null;
	}
	
	public void processAnimation(PrintWriter print_writer, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		
		switch(which_broadcaster.toUpperCase()) {
		case FootballUtil.SANTOSH_TROPHY:
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}

	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			//print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamScore " + match.getHomeTeamScore() + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamScore " + match.getAwayTeamScore() + ";");
			
			//System.out.println("Player : " + match.getHomeSquad().get(0).getFull_name());
			if(is_this_updating == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName4() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName4() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamColour2 " + colors_path + "ticker\\" 
						+ match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "ticker\\" 
						+ match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + ";");
				
				if(match.getClock().getMatchHalves().equalsIgnoreCase("first")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHalfHead " + "1st HALF" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("second")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHalfHead " + "2nd HALF" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra1")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHalfHead " + "ET-1" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra2")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHalfHead " + "ET-2" + ";");
				}
				
				scorebug.setLast_scorebug_stat("");
				scorebug.setScorebug_stat("");
			}
		}
		return scorebug;
	}
	public ScoreBug populateScoreBugStats(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer, Match match, String selectedbroadcaster) 
			throws MalformedURLException, IOException, CsvException, InterruptedException {
		
		
		String home_stats = "0", away_stats = "0";
		int l = 4,home_yellow=0,home_red=0,away_yellow=0,away_red=0;
		//float hs=0,as=0;
		
		for(MatchStats ms : match.getMatchStats()) {
			if(ms.getStats_type().equalsIgnoreCase(FootballUtil.YELLOW)) {
				for(Player hs : match.getHomeSquad()) {
					if(hs.getPlayerId() == ms.getPlayerId()) {
						home_yellow = home_yellow + 1;
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(hsub.getPlayerId() == ms.getPlayerId()) {
						home_yellow = home_yellow + 1;
					}
				}
				for(Player as : match.getAwaySquad()) {
					if(as.getPlayerId() == ms.getPlayerId()) {
						away_yellow = away_yellow + 1;
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(asub.getPlayerId() == ms.getPlayerId()) {
						away_yellow = away_yellow + 1;
					}
				}
			}else if(ms.getStats_type().equalsIgnoreCase(FootballUtil.RED)) {
				for(Player hs : match.getHomeSquad()) {
					if(hs.getPlayerId() == ms.getPlayerId()) {
						home_red = home_red + 1;
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(hsub.getPlayerId() == ms.getPlayerId()) {
						home_red = home_red + 1;
					}
				}
				for(Player as : match.getAwaySquad()) {
					if(as.getPlayerId() == ms.getPlayerId()) {
						away_red = away_red + 1;
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(asub.getPlayerId() == ms.getPlayerId()) {
						away_red = away_red + 1;
					}
				}
			}
		}
		
		switch(scorebug.getScorebug_stat().toUpperCase()) {
		case FootballUtil.YELLOW:
			home_stats = String.valueOf(home_yellow);
			away_stats = String.valueOf(away_yellow);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "YELLOW CARD" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			break;
		case FootballUtil.RED:
			home_stats = String.valueOf(home_red);
			away_stats = String.valueOf(away_red);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "RED CARD" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			break;
		}
		
		
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vExtraDataSelection 0;");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraDataHome " + home_stats.trim() + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraDataAway " + away_stats.trim() + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		
		
		if(is_this_updating == false) {
			processAnimation(print_writer, "ExtraInfo_In", "START", session_selected_broadcaster,1);
			TimeUnit.MILLISECONDS.sleep(l);
		}
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		
		/*try {
	         URL url = new URL(FootballUtil.API_PATH1 + match.getMatchId()+ FootballUtil.API_PATH2);
	         URLConnection connection = url.openConnection();
	         connection.connect();
	         LiveMatchData my_data = new ObjectMapper().readValue(url, LiveMatchData.class);
				if(my_data.getTeamShortMatchStats().getTeam_stats_data().size() > 0) {
					for(int i = 0; i <= my_data.getTeamShortMatchStats().getTeam_stats_data().size() -1; i++ ) {
						//System.out.println(Arrays.toString(read_for_api.get(i)));
						if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() ==  match.getHomeTeam().getTeamApiId()) {
							switch(scorebug.getScorebug_stat().toUpperCase()) {
							case FootballUtil.YELLOW: case "YELLOW_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "YELLOW CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "YELLOW_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									hs = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									home_stats = FootballFunctions.replace(hs);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "YELLOW CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.RED: case "RED_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "RED CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "RED_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									hs = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									home_stats = FootballFunctions.replace(hs);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "RED CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.OFF_SIDE: 
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Offsides")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "OFFSIDES" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.SHOTS:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "SHOTS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.POSSESSION:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Ball possession, %")) {
									home_stats = String.valueOf(Math.round(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()));
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "POSSESSION (%)" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.SHOTS_ON_TARGET:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots on target")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "SHOTS ON TARGET" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.CORNERS: case "CORNERS_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "CORNERS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "CORNERS_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									hs = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									home_stats = FootballFunctions.replace(hs);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "CORNERS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.TACKLES:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Tackles")) {
									home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "TACKLES" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							}
							
						}
						//System.out.println(StringUtils.capitalize(match.getAwayTeam().getTeamName2().toLowerCase()));
						if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() ==  match.getAwayTeam().getTeamApiId()) {
							switch(scorebug.getScorebug_stat().toUpperCase()) {
							case FootballUtil.YELLOW: case "YELLOW_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "YELLOW CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "YELLOW_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									as = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									away_stats = FootballFunctions.replace(as);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "YELLOW CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.RED: case "RED_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "RED CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "RED_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									as = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									away_stats = FootballFunctions.replace(as);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "RED CARD" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.OFF_SIDE: 
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Offsides")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "OFFSIDES" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.SHOTS:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "SHOTS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.POSSESSION:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Ball possession, %")) {
									away_stats = String.valueOf(Math.round(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()));
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "POSSESSION (%)" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.SHOTS_ON_TARGET:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots on target")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "SHOTS ON TARGET" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case FootballUtil.CORNERS: case "CORNERS_HOME":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "CORNERS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;
							case "CORNERS_AWAY":
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									as = my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue() + 1 ;
									away_stats = FootballFunctions.replace(as);
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "CORNERS" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							case FootballUtil.TACKLES:
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Tackles")) {
									away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
								}
								print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoHead " + "TACKLES" + ";");
								TimeUnit.MILLISECONDS.sleep(l);
								break;	
							}
						}
					}
				} else {
					System.out.println("Error");
				}
	         //System.out.println("Internet is connected");
	      } catch (MalformedURLException e) {
	         System.out.println("Internet is not connected");
	      } catch (IOException e) {
	         System.out.println("Internet is not connected");
	      }*/
		
		return scorebug;
	}
	public ScoreBug populateScorebugCard(ScoreBug scorebug,PrintWriter print_writer,int TeamId,int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vExtraDataSelection 1;");
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeOrAwayData 0;");

				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber " + hs.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerName " + hs.getTicker_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber " + hsub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerName " + hsub.getTicker_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			else {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeOrAwayData 1;");

				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber " + as.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerName " + as.getTicker_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber " + asub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerName " + asub.getTicker_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
			switch(scorebug.getScorebug_stat().toUpperCase())
			{
			case "YELLOW_CARD":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCards 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoCardHead " + "YELLOW CARD" +";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "RED_CARD":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCards 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoCardHead " + "RED CARD" +";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCards 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoCardHead " + "SECOND YELLOW" +";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "GOAL":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCards 3;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoCardHead " + "GOAL SCORER" +";");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}
		}
		processAnimation(print_writer, "ExtraInfo_In", "START", session_selected_broadcaster,1);
		
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateScorebugSubs(ScoreBug scorebug,PrintWriter print_writer,int TeamId,List<Player> plyr, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraInfoCardHead " + "SUBSTITUTION" +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vExtraDataSelection 2;");
			 
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(TeamId ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			 
			if(TeamId == plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTeamId()) {
				if(match.getHomeTeamId() == plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
							FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeOrAwayData 0;");
					TimeUnit.MILLISECONDS.sleep(l);
					switch(scorebug.getScorebug_stat().toUpperCase())
					{
					case "SINGLE":
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeNumberOfPlayers 1;");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET HometPlayerNumber2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerName2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeArrow2 " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeArrow1 " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
						
						break;
					}
				}else if(match.getAwayTeamId() == plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
							FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeOrAwayData 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					switch(scorebug.getScorebug_stat().toUpperCase())
					{
					case "SINGLE":
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayNumberOfPlayers 1;");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerName2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayArrow2 " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayArrow1 " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
						
						break;
					}
				}
			}
		}
		processAnimation(print_writer, "ExtraInfo_In", "START", session_selected_broadcaster,1);
		
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateRedcard(boolean is_this_updating, ScoreBug scorebug, PrintWriter print_writer, Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		
		String home_stats = "0", away_stats = "0";
		
		try {
	         URL url = new URL(FootballUtil.API_PATH1 + match.getMatchId()+ FootballUtil.API_PATH2);
	         URLConnection connection = url.openConnection();
	         connection.connect();
	         LiveMatchData my_data = new ObjectMapper().readValue(new URL(FootballUtil.API_PATH1 + match.getMatchId()
				+ FootballUtil.API_PATH2), LiveMatchData.class);
				
				if(my_data.getTeamShortMatchStats().getTeam_stats_data().size() > 0) {
					for(int i = 0; i <= my_data.getTeamShortMatchStats().getTeam_stats_data().size() -1; i++ ) {
						if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() ==  Integer.valueOf(match.getHomeTeam().getTeamApiId())) {
							if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
								home_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
							}
							
						}
						if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() ==  Integer.valueOf(match.getAwayTeam().getTeamApiId())) {
							if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
								away_stats = FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue());
							}
						}
						
					}
				}
	         //System.out.println("Internet is connected");
	      } catch (MalformedURLException e) {
	         System.out.println("Internet is not connected");
	      } catch (IOException e) {
	         System.out.println("Internet is not connected");
	      }
		
		if(home_stats.equalsIgnoreCase("0")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard1 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard2 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard4 " + "0" + ";");
		}else if(home_stats.equalsIgnoreCase("1")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard2 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard4 " + "0" + ";");
		}else if(home_stats.equalsIgnoreCase("2")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard4 " + "0" + ";");
		}else if(home_stats.equalsIgnoreCase("3")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard3 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard4 " + "0" + ";");
		}else if(home_stats.equalsIgnoreCase("4")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard3 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRedCard4 " + "1" + ";");
		}
		
		if(away_stats.equalsIgnoreCase("0")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard1 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard2 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard4 " + "0" + ";");
		}else if(away_stats.equalsIgnoreCase("1")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard2 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard4 " + "0" + ";");
		}else if(away_stats.equalsIgnoreCase("2")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard3 " + "0" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard4 " + "0" + ";");
		}else if(away_stats.equalsIgnoreCase("3")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard3 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard4 " + "0" + ";");
		}else if(away_stats.equalsIgnoreCase("4")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard1 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard2 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard3 " + "1" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRedCard4 " + "1" + ";");
		}
		
		if(is_this_updating == false) {
			processAnimation(print_writer, "RedCardInfo_In", "START", session_selected_broadcaster,1);
		}
		return scorebug;
	}
	public ScoreBug populateExtraTime(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		
		if(is_this_updating == false) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraTime " + "+" + time_value + ";");
			processAnimation(print_writer, "InjuryTime_In", "START", session_selected_broadcaster,1);
		}
		
		return scorebug;
		
	}
	public ScoreBug populateExtraTimeBoth(boolean is_this_updating,ScoreBug scorebug,PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		
		if(is_this_updating == false) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tExtraTime " + "+" + time_value + ";");
			processAnimation(print_writer, "ExtraTime_In", "START", session_selected_broadcaster,1);
			processAnimation(print_writer, "InjuryTime_In", "START", session_selected_broadcaster,1);
		}
		
		return scorebug;
		
	}
	
	public void populateNameSuper(PrintWriter print_writer,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			if(ns.getSponsor() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + "TLogo" + FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + "Sponsor\\" + ns.getSponsor() + FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "\\I-League\\Colour1" + ";");
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "\\I-League\\Colour2" + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$FF$LogoGrp$group$Logo*CONTAINER SET ACTIVE 0;");
			
			if(ns.getFirstname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + "" +";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			else if(ns.getSurname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + ns.getFirstname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + "" +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + 
						ns.getFirstname().toUpperCase() + " " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + " " +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + ns.getSubLine().toUpperCase() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
		
	}
	public void populateNameSuperPlayer(PrintWriter print_writer,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			String Home_or_Away="";
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$FF$LogoGrp$group$Logo*CONTAINER SET ACTIVE 1;");
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hs.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hs.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hsub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hsub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}
					}
				}
			}
			else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + as.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + as.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + asub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + asub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						}
					}
				}
			}
			
			switch(captainGoalKeeper.toUpperCase())
			{
			case "CAPTAIN":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + captainGoalKeeper.toUpperCase() + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				break;
			case "PLAYER OF THE MATCH":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "HERO OF THE MATCH " + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				break;
			case "GOAL_KEEPER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "GOAL KEEPER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				break;
			case "PLAYER_TODAY_GOAL":
				int player_goal_count=0;
				for(MatchStats ms : match.getMatchStats()) {
					if(ms.getStats_type().equalsIgnoreCase("goal")) {
						if(ms.getPlayerId() == playerId) {
							player_goal_count = player_goal_count + 1;
						}
					}else if(ms.getStats_type().equalsIgnoreCase("penalty")) {
						if(ms.getPlayerId() == playerId) {
							player_goal_count = player_goal_count + 1;
						}
					}
				}
				if(player_goal_count != 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "GOALS TODAY - " + player_goal_count + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				}else {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + Home_or_Away + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				}
				
				break;
			case "GOAL_SCORER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "GOAL SCORER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				break;
			case "CAPTAIN-GOALKEEPER":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "CAPTAIN & GOAL KEEPER" + " , " + Home_or_Away + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
				break;
			}

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 35;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateNameSuperCard(PrintWriter print_writer,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + "\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hs.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hs.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hs.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + hsub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + hsub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + hsub.getRole().toUpperCase() + " - " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
						FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + "\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
				//TimeUnit.MILLISECONDS.sleep(l);
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + as.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + as.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + as.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + asub.getJersey_number() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + asub.getFull_name().toUpperCase() +";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + asub.getRole().toUpperCase() + " - " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTime " + " " + ";");
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case FootballUtil.YELLOW:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case FootballUtil.RED:
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vCard 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 92.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateMatchId(PrintWriter print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int l = 4;
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getMatchIdent().toUpperCase()+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName "+ match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName "+ match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "LIVE FROM "+ match.getVenueName().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateSubstitute(PrintWriter print_writer,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					FootballUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(Team_id ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			
			switch(Num_Of_Subs.toUpperCase())
			{
			case "SINGLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				break;
			case "DOUBLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				break;
				
			case "TRIPLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2C " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1C " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				break;
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateSingleSubstitute(PrintWriter print_writer,String viz_scene,int Team_id,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws IOException, InterruptedException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(Team_id - 1).getTeamName4() + 
					FootballUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "SUBSTITUTIONS" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(Team_id ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 0;");
			//TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2 " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2 " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
		
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1 " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1 " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 36.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	
	public void populateAds(PrintWriter print_writer,String viz_scene ,String whichAd , Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			//System.out.println(whichAd.toUpperCase());
			switch(whichAd.toUpperCase()){
			case "XPULSE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAD " + logo_path + "Xpulse" + FootballUtil.PNG_EXTENSION + ";");
				break;
			case "PASSION":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAD " + logo_path + "Passion" + FootballUtil.PNG_EXTENSION + ";");
				break;
			case "GLAMOUR":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAD " + logo_path + "Glamour_Aston" + FootballUtil.PNG_EXTENSION + ";");
				break;
			case "SPLENDOR":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAD " + logo_path + "Splendor_Aston" + FootballUtil.PNG_EXTENSION + ";");
				break;
			case "DESTINI":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAD " + logo_path + "Hero_Aston_AD" + FootballUtil.PNG_EXTENSION + ";");
				break;	
			}
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateBug_replay(PrintWriter print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			/*if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerFirstName1 " + bug.getText1().toUpperCase() +";");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info1*CONTAINER SET ACTIVE 0;");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + bug.getText2().toUpperCase() +";");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info3*CONTAINER SET ACTIVE 0;");
			}else if(bug.getText1() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerFirstName1 " + bug.getText1().toUpperCase() +";");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info1*CONTAINER SET ACTIVE 0;");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info2*CONTAINER SET ACTIVE 0;");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info3*CONTAINER SET ACTIVE 0;");
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerFirstName1 " + bug.getText2().toUpperCase() +";");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info1*CONTAINER SET ACTIVE 0;");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info2*CONTAINER SET ACTIVE 0;");
				//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$All$Lft_Grp$Data$Info3*CONTAINER SET ACTIVE 0;");
			}*/
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 32.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populatePlayingXI(PrintWriter print_writer,String viz_scene, int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int i,row_id = 0,row_id_sub=0,l=4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$group$FF$2nd*CONTAINER SET ACTIVE 0;");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent().toUpperCase() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayingXI " + "STARTING XI" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSUBSTITUTES " + "SUBSTITUTES" + ";");
			
			if(TeamId == match.getHomeTeamId()) {
				if(team.get(match.getHomeTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + team.get(match.getHomeTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + " " + ";");
				}
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + team.get(match.getHomeTeamId()-1).getTeamName1().toUpperCase() + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				if(match.getHomeTeamFormationId() > 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + 
							formation.get(match.getHomeTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getHomeTeamFormationId() == 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + "" + ";");
				}
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$FF$Data$Team$Subs$Player" + row_id_sub + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}

			}else if(TeamId == match.getAwayTeamId()){
				
				if(team.get(match.getAwayTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + team.get(match.getAwayTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + " " + ";");
				}
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + team.get(match.getAwayTeamId()-1).getTeamName1().toUpperCase() + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				if(match.getAwayTeamFormationId() > 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + 
							formation.get(match.getAwayTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getAwayTeamFormationId() == 0) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + "" + ";");
				}

				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main$FF$Data$Team$Subs$Player" + row_id_sub + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 112.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			TimeUnit.SECONDS.sleep(1);
		}
	}
	public void populatePlayingXIChangeOn(PrintWriter print_writer,String viz_scene, int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int i,row_id = 0,row_id_sub=0,row_id2 = 0,row_id_sub2 = 0,l=4;
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$group$FF$2nd*CONTAINER SET ACTIVE 0;");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent().toUpperCase() + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + match.getMatchIdent().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayingXI " + "STARTING XI" + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSUBSTITUTES " + "SUBSTITUTES" + ";");
			
			if(TeamId == match.getHomeTeamId()) {
				if(team.get(match.getHomeTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + team.get(match.getHomeTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + " " + ";");
				}
				
				if(team.get(match.getAwayTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName02 " + team.get(match.getAwayTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName02 " + " " + ";");
				}
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + team.get(match.getHomeTeamId()-1).getTeamName1().toUpperCase() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader02 " + team.get(match.getAwayTeamId()-1).getTeamName1().toUpperCase() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo02 " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo02 " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				if(match.getHomeTeamFormationId() > 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + 
							formation.get(match.getHomeTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getHomeTeamFormationId() == 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + "" + ";");
				}
				
				if(match.getAwayTeamFormationId() > 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation02 " + 
							formation.get(match.getAwayTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getAwayTeamFormationId() == 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation02 " + "" + ";");
				}
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$FF$1st$Data$Team$Subs$Player" + row_id_sub + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				
				for(Player hs : match.getAwaySquad()) {
					row_id2 = row_id2 + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id2 + "02" + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id2 + "02" + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub2 = row_id_sub2 + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$FF$2nd$Data$Team$Subs$Player" + row_id_sub2 + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub2 + "02" + " " + match.getAwaySubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub2  + "02" + " " + match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub2 + "02" + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub2 + "02" + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				
			}else if(TeamId == match.getAwayTeamId()){
				if(team.get(match.getHomeTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName02 " + team.get(match.getHomeTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName02 " + " " + ";");
				}
				
				if(team.get(match.getAwayTeamId()-1).getTeamCoach() != null) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + team.get(match.getAwayTeamId()-1).getTeamCoach().toUpperCase() + ";");
				}else {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " + " " + ";");
				}
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + team.get(match.getAwayTeamId()-1).getTeamName1().toUpperCase() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader02 " + team.get(match.getHomeTeamId()-1).getTeamName1().toUpperCase() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo " + logo_path + 
						match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo02 " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgBGLogo02 " + logo_path + 
						match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				
				if(match.getHomeTeamFormationId() > 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation02 " + 
							formation.get(match.getHomeTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getHomeTeamFormationId() == 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation02 " + "" + ";");
				}
				
				if(match.getAwayTeamFormationId() > 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + 
							formation.get(match.getAwayTeamFormationId() -1).getFormDescription() + ";");
				}else if(match.getAwayTeamFormationId() == 0) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + "" + ";");
				}

				for(Player hs : match.getAwaySquad()) {
					row_id = row_id + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getAwaySubstitutesPerTeam()-1; i++) {
					row_id_sub = row_id_sub + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$FF$1st$Data$Team$Subs$Player" + row_id_sub + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub + " " + match.getAwaySubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getAwaySubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
		//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				
				
				for(Player hs : match.getHomeSquad()) {
					row_id2 = row_id2 + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber"+ row_id2 + "02" + " " + hs.getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName"+ row_id2 + "02" + " " + hs.getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "C" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + "C & GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRole"+ row_id2 + "02" + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(i = 0; i <= match.getHomeSubstitutesPerTeam()-1; i++) {
					row_id_sub2 = row_id_sub2 + 1;
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$FF$2nd$Data$Team$Subs$Player" + row_id_sub2 + "_Grp" + "*CONTAINER SET ACTIVE 1;");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row_id_sub2 + "02" + " " + match.getHomeSubstitutes().get(i).getJersey_number() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row_id_sub2 + "02" + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub2 + "02" + " " + "GK" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row_id_sub2 + "02" + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 112.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			TimeUnit.SECONDS.sleep(1);
		}
	}
	
	public void populateDoubleSubs(PrintWriter print_writer,String viz_scene,List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			int i,row = 0,row_id_sub=0,l=4;
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase().trim() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				/*
				 * print_writer.
				 * println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tCoachName " +
				 * team.get(match.getHomeTeamId()-1).getTeamCoach().toUpperCase() + ";");
				 * TimeUnit.MILLISECONDS.sleep(l);
				 */
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSUBSTITUTES "+ "SUBSTITUTES" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(i = 0; i <= 10; i++) {
				//for(Player hos : match.getHomeSubstitutes()) {
					row = row + 1;
					if(i < match.getHomeSubstitutesPerTeam()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row + " " + match.getHomeSubstitutes().get(i).getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row + " " + match.getHomeSubstitutes().get(i).getFull_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(match.getHomeSubstitutes().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row + " " + "GK" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}else {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row + " " + " " + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
						
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubPlayerName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubRole"+ row + " " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubCard"+ row +" "+ "0;");
					TimeUnit.MILLISECONDS.sleep(l);
				}
					
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase().trim() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				/*
				 * print_writer.
				 * println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayCoachName "
				 * + team.get(match.getAwayTeamId()-1).getTeamCoach().toUpperCase() + ";");
				 * TimeUnit.MILLISECONDS.sleep(l);
				 */
				for(int j = 0; j <= 10; j++) {
				//for(Player hos : match.getHomeSubstitutes()) {
				//if(match.getAwayTeamId() == match.getAwaySubstitutes().get(i).getTeamId()) {
					row_id_sub = row_id_sub + 1;
					if(j < match.getAwaySubstitutesPerTeam()) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerNumber"+ row_id_sub + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerName"+ row_id_sub + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);

						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerNumber"+ row_id_sub + " " + match.getAwaySubstitutes().get(j).getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerName"+ row_id_sub + " " + match.getAwaySubstitutes().get(j).getFull_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						
						if(match.getAwaySubstitutes().get(j).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubRole"+ row_id_sub +" "+ "GK" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}else {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubRole"+ row_id_sub +" "+ " " + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
						
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerNumber"+ row_id_sub + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubPlayerName"+ row_id_sub + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubRole"+ row_id_sub +" "+ " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwaySubCard"+ row_id_sub +" "+ "0;");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				
				print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 107.0;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
				print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
				print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateBugsDB(PrintWriter print_writer,String viz_scene, Bugs bug ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + " " +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + " " +";");
				
			}else if(bug.getText1() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText1().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "  " +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "  " +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "  " +";");
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + bug.getText2().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "  " +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + "  " +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "  " +";");
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 32.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateScoreUpdate(PrintWriter print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			int l=4;
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.HALF)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FULL)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + clock.getMatchHalves().toUpperCase() + " TIME"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FIRST)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "1st HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.SECOND)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "2nd HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "ET - 1"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "ET - 2"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getHomeTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 0 && match.getHomeTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getHomeTeamScore() > 2 && match.getHomeTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeScorerData " + "3" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			if(match.getAwayTeamScore() == 0 ) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "0" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 0 && match.getAwayTeamScore() <= 2) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getAwayTeamScore() > 2 && match.getAwayTeamScore() <= 4) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayScorerData " + "3" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = footballService.getPlayer(FootballUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {

								stats_txt = stats_txt + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case FootballUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case FootballUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)) {
					stats_txt_og = footballService.getPlayer(FootballUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						/*for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL))) {

								stats_txt_og = stats_txt_og + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}*/
						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case FootballUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case FootballUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers1 " + h1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers2 " + h2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6) {
					h3 = h3 + home_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamPlayers3 " + h3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers1 " + a1 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers2 " + a2 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6) {
					a3 = a3 + away_stats.get(i);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamPlayers3 " + a3 + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 58.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchStatus(PrintWriter print_writer,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			int l = 4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getMatchIdent().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "FIRST HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "SECOND HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "EXTRA TIME 2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead1 " + FootballUtil.SHOTS + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead2 " + FootballUtil.SHOTS_ON_TARGET.replace("_", " ") + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead3 " + FootballUtil.RED + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead4 " + FootballUtil.YELLOW + " CARDS" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead5 " + "TACKLES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead6 " + "OFFSIDES" + ";");			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead7 " + FootballUtil.CORNERS + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead8 " + "POSSESSION (%)" + ";");
			
			 try {
		         URL url = new URL(FootballUtil.API_PATH1 + match.getMatchId()+ FootballUtil.API_PATH2);
		         URLConnection connection = url.openConnection();
		         connection.connect();
		         LiveMatchData my_data = new ObjectMapper().readValue(url, LiveMatchData.class);
					
		         if(my_data.getTeamShortMatchStats().getTeam_stats_data().size() > 0) {
						for(int i = 0; i <= my_data.getTeamShortMatchStats().getTeam_stats_data().size() -1; i++ ) {
							if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() ==  Integer.valueOf(match.getHomeTeam().getTeamApiId())) {
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue1 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots on target")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue2 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue3 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue4 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Tackles")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue5 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Offsides")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue6 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue7 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Ball possession, %")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamValue8 " + Math.round(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
							}
							
							if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getTeam_id() == Integer.valueOf(match.getAwayTeam().getTeamApiId())) {
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue1 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Shots on target")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue2 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Red card")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue3 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Yellow card")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue4 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Tackles")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue5 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Offsides")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue6 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Corner")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue7 " + FootballFunctions.replace(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
								if(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getParam_name().equalsIgnoreCase("Ball possession, %")) {
									print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamValue8 " +Math.round(my_data.getTeamShortMatchStats().getTeam_stats_data().get(i).getValue()) + ";");
									TimeUnit.MILLISECONDS.sleep(l);
								}
							}
						}
					} else {
						System.out.println("Error");
					}
		         //System.out.println("Internet is connected");
		      } catch (MalformedURLException e) {
		         System.out.println("Internet is not connected");
		      } catch (IOException e) {
		         System.out.println("Internet is not connected");
		      }
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 100.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromo(PrintWriter print_writer,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			int count = 0 , count1 = 0,row_id = 0,l = 4 ;
			String groun = "";
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "UPCOMING MATCHES" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Fixture fx :fixture) {
				if(match.getMatchFileName().replace(".xml", "").equalsIgnoreCase(fx.getMatchfilename())) {
					count = fx.getMatchnumber();
					count1 = count + 4;
				}
			}
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "1" + ";");
			
			for(;count < count1;) {
			//if(count < count1) {
				row_id = row_id + 1;
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getHometeamid() - 1).getTeamName4().toUpperCase() + 
						FootballUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
						FootballUtil.PNG_EXTENSION+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName" + row_id + " " + team.get(fixture.get(count).getHometeamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName" + row_id + " " + team.get(fixture.get(count).getAwayteamid() - 1).getTeamName1().toUpperCase() + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(count).getVenue())) {
						groun = ground.get(j).getCity();
					}
				}
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(count).getMatchfilename().toUpperCase() + " - " + fixture.get(count).getDate() + " (" + fixture.get(count).getTime() + ") LIVE FROM " + groun + ";");
				TimeUnit.MILLISECONDS.sleep(l);
				
				count = count + 1;
				
			}
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateStaff(PrintWriter print_writer,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + team.get(st.getClubId() - 1).getTeamName4() + 
					FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1 " + "" +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerLastName1 " + st.getName().toUpperCase() +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + st.getRole().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 35;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateMatchStats(PrintWriter print_writer,String viz_scene,FootballService footballService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			
			int l = 4;
			String Home_player="",Away_player="";
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + "TLogo" + FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + match.getClock().getMatchHalves().toUpperCase() + " TIME"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "FIRST HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "SECOND HALF"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "EXTRA TIME 1"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "EXTRA TIME 2"+ ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "LIVE FROM "+ match.getVenueName().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = footballService.getPlayer(FootballUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {

								stats_txt = stats_txt + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case FootballUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case FootballUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)) {
					stats_txt_og = footballService.getPlayer(FootballUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						/*for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL))) {

								stats_txt_og = stats_txt_og + "," + 
									FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}*/
						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case FootballUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case FootballUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i<6) {
					Home_player = Home_player + home_stats.get(i) + "\n";
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i<6) {
					Away_player = Away_player + away_stats.get(i) + "\n";
				}
			}
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeGoalers " + Home_player + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayGoalers " + Away_player + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromoSingle(PrintWriter print_writer,String viz_sence_path, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			
			
			
			int l =4;
			String grounds = "";
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + fix.get(match_number - 1).getMatchfilename().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + TM.getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName "+ TM.getTeamName1().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			Calendar cal = Calendar.getInstance();
			//cal.add(Calendar.DATE, +1);
			if(fix.get(match_number - 1).getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()))) {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + "UP NEXT " + "(" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				for(int j = 0; j <= ground.size()-1; j++) {
					if(ground.get(j).getGroundId() == Integer.valueOf(fix.get(match_number - 1).getVenue())) {
						grounds = ground.get(j).getFullname();
					}
				}
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo " + fix.get(match_number - 1).getDate() + " (" + fix.get(match_number - 1).getTime() + ") - LIVE FROM " + grounds + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 196.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
				
		}
	}
	public void populateMatchDoublePromo(PrintWriter print_writer,String viz_scene,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			
			int row_id = 1 ,l=4;
			String Date = "",grou = "";
			Calendar cal = Calendar.getInstance();
			
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "TOMORROW'S MATCHES" + ";");
			
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getTournament().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vMatches " + "0" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int i = 0; i <= fixture.size()-1; i++) {
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					for(int j = 0; j <= ground.size()-1; j++) {
						if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(i).getVenue())) {
							grou = ground.get(j).getFullname();
						}
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toUpperCase() + 
							FootballUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayLogo" + row_id + " " + logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toUpperCase() + 
							FootballUtil.PNG_EXTENSION+ ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamFirstName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamLastName" + row_id + " " + team.get(fixture.get(i).getHometeamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName2().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName" + row_id + " " + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName3().toUpperCase() + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo" + row_id + " " + fixture.get(i).getMatchfilename().toUpperCase() + " - " + "(" + fixture.get(i).getTime() + ") LIVE FROM " + grou + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					row_id = row_id +1;
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 85.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populatePointsTable(PrintWriter print_writer,String viz_sence_path,String Group,List<LeagueTeam> point_table, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		
		
		int row_no=0,l=4;
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "POINTS TABLE" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + "TLogo" + FootballUtil.PNG_EXTENSION + ";");
		
		if(Group.equalsIgnoreCase("LeagueTableA")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "GROUP A" + ";");
		}else if(Group.equalsIgnoreCase("LeagueTableB")) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + "GROUP B" + ";");
		}
		
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadA " + "POS" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadB " + "TEAMS" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadC " + "P" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadD " + "W" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadE " + "D" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadF " + "L" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadG " + "GD" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatHeadH " + "PTS" + ";");
		TimeUnit.MILLISECONDS.sleep(l);
		
		for(int i = 0; i <= point_table.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			for(Team tm : team) {
				if(tm.getTeamName1().contains(point_table.get(i).getTeamName())) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsTeamLogo0" + row_no + " " + logo_path + 
							tm.getTeamName4() + FootballUtil.PNG_EXTENSION + ";");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(point_table.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsTeamName0" + row_no + " " + tm.getTeamName4().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsTeamName0" + row_no + " " + "(Q) " + tm.getTeamName4().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
				
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "A" + " " + point_table.get(i).getPlayed() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "B" + " " + point_table.get(i).getWon() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "C" + " " + point_table.get(i).getDrawn() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "D" + " " + point_table.get(i).getLost() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "E" + " " + point_table.get(i).getGD() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPointsStatValue" + (row_no + 1) + "F" + " " + point_table.get(i).getPoints() + ";");
			TimeUnit.MILLISECONDS.sleep(l);

		}
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 0;");
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 121.0;");
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		TimeUnit.SECONDS.sleep(1);
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Infobar*CONTAINER SET ACTIVE 1;");
		print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		
	}
	public void populateFormation(PrintWriter print_writer,String viz_scene, int TeamId,String Type,List<Formation> formation,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			int row_id = 0,l=4;
			
			switch(Type.toUpperCase()) {
			case "FORMATION_WITH_IMAGE":
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
							match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + form.getFormDescription() + ";");
							
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					
					for(Player hs : match.getHomeSquad()) {
						row_id = row_id + 1;
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerFirstName"+ row_id + " " + hs.getTicker_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomePlayerImage"+ row_id + " " + photo_path + match.getHomeTeam().getTeamName4().toUpperCase() + "\\" + hs.getPhoto() + ".png" + ";");
						//TimeUnit.MILLISECONDS.sleep(l);
					}
					
					/*print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");

					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
				
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName " + match.getAwayTeam().getTeamName2() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName " + match.getAwayTeam().getTeamName3() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubHeader " + match.getTournament().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					
					for(Player as : match.getAwaySquad()) {
						row = row + 1;
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + as.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerLastName"+ row + " " + as.getTicker_name().toUpperCase() + ";");
					}*/

				}else {
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + 
							match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + form.getFormDescription() + ";");
							
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					
					for(Player as : match.getAwaySquad()) {
						row_id = row_id + 1;
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + as.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerFirstName"+ row_id + " " + as.getTicker_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomePlayerImage"+ row_id + " " + photo_path + match.getAwayTeam().getTeamName4().toUpperCase() + "\\" + as.getPhoto() + ".png" + ";");
						//TimeUnit.MILLISECONDS.sleep(800);
					}
					
					/*print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName " + match.getHomeTeam().getTeamName2() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName " + match.getHomeTeam().getTeamName3() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubHeader " + match.getTournament().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					
					for(Player hs : match.getHomeSquad()) {
						row = row + 1;
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + hs.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerLastName"+ row + " " + hs.getTicker_name().toUpperCase() + ";");
					}*/

				}
				break;
			case "FORMATION_WITHOUT_IMAGE":
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + form.getFormDescription() + ";");
							
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					
					for(Player hs : match.getHomeSquad()) {
						row_id = row_id + 1;
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + hs.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerFirstName"+ row_id + " " + hs.getTicker_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(800);
					}
					
					/*print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");

					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
				
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName " + match.getAwayTeam().getTeamName2() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName " + match.getAwayTeam().getTeamName3() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubHeader " + match.getMatchIdent().toUpperCase() + " - " + "STARTING XI"  + ";");
					
					if(match.getAwayTeamFormationId() != 0) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayFormation " + "( " + 
								formation.get(match.getAwayTeamFormationId() - 1 ).getFormDescription() + " )" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayFormation " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0"  + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0"  + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColor " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					for(Player as : match.getAwaySquad()) {
						row = row + 1;
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + as.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerLastName"+ row + " " + as.getTicker_name().toUpperCase() + ";");
					}*/

				}else {
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamLogo " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");

					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFormation " + form.getFormDescription() + ";");
							
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					for(Player as : match.getAwaySquad()) {
						row_id = row_id + 1;
						
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerNumber"+ row_id + " " + as.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomePlayerFirstName"+ row_id + " " + as.getTicker_name().toUpperCase() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					/*print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamFirstName " + match.getHomeTeam().getTeamName2() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamLastName " + match.getHomeTeam().getTeamName3() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwaySubHeader " + match.getMatchIdent().toUpperCase() + " - " + "STARTING XI"  + ";");
					
					if(match.getHomeTeamFormationId() != 0) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayFormation " + "( " + 
								formation.get(match.getHomeTeamFormationId() - 1 ).getFormDescription() + " )" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayFormation " + " " + ";");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos1 " + form.getFormOrds1X() + "_" + form.getFormOrds1Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos2 " + form.getFormOrds2X() + "_" + form.getFormOrds2Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos3 " + form.getFormOrds3X() + "_" + form.getFormOrds3Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos4 " + form.getFormOrds4X() + "_" + form.getFormOrds4Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos5 " + form.getFormOrds5X() + "_" + form.getFormOrds5Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos6 " + form.getFormOrds6X() + "_" + form.getFormOrds6Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos7 " + form.getFormOrds7X() + "_" + form.getFormOrds7Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos8 " + form.getFormOrds8X() + "_" + form.getFormOrds8Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos9 " + form.getFormOrds9X() + "_" + form.getFormOrds9Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos10 " + form.getFormOrds10X() + "_" + form.getFormOrds10Y() + "_" + "0.0" + ";");
							print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerPos11 " + form.getFormOrds11X() + "_" + form.getFormOrds11Y() + "_" + "0.0" + ";");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColor " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					for(Player hs : match.getHomeSquad()) {
						row = row + 1;
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerNumber"+ row + " " + hs.getJersey_number() + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerFirstName"+ row + " " + "" + ";");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayPlayerLastName"+ row + " " + hs.getTicker_name().toUpperCase() + ";");
					}*/
				}
				break;	
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 115.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			
		}
	}
	public void populateChangeOnFormation(PrintWriter print_writer,int TeamId,String Type,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			switch(Type.toUpperCase()) {
			case "FORMATION_WITH_IMAGE":
				if(TeamId == match.getHomeTeamId()) {					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");

				}else {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");

				}
				break;
			case "FORMATION_WITHOUT_IMAGE":
				if(TeamId == match.getHomeTeamId()) {
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Away\\" + match.getAwayTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");

				}else {				
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour1 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour1" + FootballUtil.PNG_EXTENSION + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamColour2 " + colors_path + "Home\\" + match.getHomeTeam().getTeamName1() + 
							"\\Colour2" + FootballUtil.PNG_EXTENSION + ";");
					
				}
				break;	
			}
		}
	}
	public void populateOfficials(PrintWriter print_writer,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			

			int l=4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName1 " + officials.get(0).getReferee().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName2 " + officials.get(0).getFourthOfficial().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName3 " + officials.get(0).getAssistantReferee2().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOfficialName4 " + officials.get(0).getAssistantReferee1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 50.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateHighlight(PrintWriter print_writer,String viz_scene, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			int l = 4;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + "HIGHLIGHTS" + ";");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "HALF TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "FULL TIME" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "FIRST HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "SECOND HALF" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "EXTRA TIME 1" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1A " + "EXTRA TIME 2" + ";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1B " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tInfo1C " + "" + ";");
			TimeUnit.MILLISECONDS.sleep(l);
				

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 35;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
		}
	}
	public void populateLtPenalty(PrintWriter print_writer,String viz_scene,String valueToProcess,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l=4;
			int iHomeCont = 0, iAwayCont = 0;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + match.getHomeTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + match.getAwayTeam().getTeamName4() + 
					FootballUtil.PNG_EXTENSION+ ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName1().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 2" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 1" + ";");
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 2" + ";");
				}
				
				
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePenalty" + iHomeCont + " 0" + ";");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPenalty" + iAwayCont + " 0" + ";");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 40.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
}