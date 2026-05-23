package com.football.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.xml.bind.JAXBException;
import com.football.model.*;
import com.football.service.FootballService;
import com.football.util.FootballFunctions;
import com.football.util.FootballUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.containers.Scene;
import com.football.containers.ScoreBug;

public class Viz_Santosh_Trophy extends Scene{
	
	public String session_selected_broadcaster = "VIZ_SANTOSH_TROPHY";
	
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	private String logo_path = "D:\\\\DOAD_In_House_Everest\\\\Everest_Sports\\\\Everest_SantoshTrophy_2023\\\\Logos\\\\";
	//private String colors_path = "D:\\\\DOAD_In_House_Everest\\\\Everest_Sports\\\\Everest_SantoshTrophy_2023\\\\Colours\\\\";
	//private String photo_path = "C:\\Images\\I-League\\";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	public ObjectMapper objectMapper = new ObjectMapper();
	
	public Viz_Santosh_Trophy() {
		super();
	}
	
	public ScoreBug updateScoreBug(PrintWriter print_writer,List<Scene> scenes, Match match,FootballService footballService) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, print_writer, scenes.get(0).getScene_path(),match,footballService.getTeamColors(), session_selected_broadcaster);
			scorebug = populateExtraTime(true,scorebug,print_writer,null,match,session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(PrintWriter print_writer,String whatToProcess,Match match,Clock clock, FootballService footballService,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException{
		
		/*if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			
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
		}*/
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH":
		case "POPULATE-FF-MATCHID": case "POPULATE-FF-PLAYINGXI":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-LT-MATCHID":
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH":
				break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,print_writer, valueToProcess.split(",")[1],match,footballService.getTeamColors(), session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME":
				populateExtraTime(false,scorebug,print_writer,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME_BOTH":
				populateExtraTimeBoth(false,scorebug,print_writer,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(), footballService.getTeams(),
						match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
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
			
		case "ANIMATE-IN-SCOREBUG": 
		case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-PLAYINGXI":
		case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-LT_MATCHID":
		case "CLEAR-ALL": 
		case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-OUT-EXTRA_TIME":
		case "ANIMATE-OUT": 
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-SCOREBUG":
				AnimateInGraphics(print_writer, "SCOREBUG");
				//processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-MATCHID":
				AnimateInGraphics(print_writer, "MATCHID");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				AnimateInGraphics(print_writer, "PLAYINGXI");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				AnimateInGraphics(print_writer, "SCOREUPDATE");
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						//processAnimation(print_writer, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						//processAnimation(print_writer, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						//processAnimation(print_writer, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-LT_MATCHID":
				AnimateInGraphics(print_writer, "LT_MATCHID");
				which_graphics_onscreen = "LT_MATCHID";
				break;
			case "CLEAR-ALL":
				print_writer.println("-1 SCENE CLEANUP\0");
				print_writer.println("-1 IMAGE CLEANUP\0");
				print_writer.println("-1 GEOM CLEANUP\0");
				print_writer.println("-1 FONT CLEANUP\0");

				print_writer.println("-1 IMAGE INFO\0");
				print_writer.println("-1 RENDERER SET_OBJECT SCENE*" + valueToProcess.split(",")[0] + "\0");

				print_writer.println("-1 RENDERER INITIALIZE\0");
				print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE\0");
				print_writer.println("-1 RENDERER*UPDATE SET 0\0");
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");

				print_writer.println("-1 RENDERER*UPDATE SET 1\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*/Default/ScoreBug-Single\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER INITIALIZE\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE SHOW 0.0\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 1\0");

				print_writer.println("-1 SCENE CLEANUP\0");
				print_writer.println("-1 IMAGE CLEANUP\0");
				print_writer.println("-1 GEOM CLEANUP\0");
				print_writer.println("-1 FONT CLEANUP\0");
				which_graphics_onscreen = "";
				is_infobar = false;
				scorebug.setScorebug_on_screen(false);
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					AnimateOutGraphics(print_writer, "SCOREBUG");
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT-EXTRA_TIME":
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*AddedMinOut START \0");
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*ExtraTimeOut START \0");
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID":
					AnimateOutGraphics(print_writer, which_graphics_onscreen);
					which_graphics_onscreen = "";
					break;
				}
				break;
			}
			break;
			}
		return null;
	}
	
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}
	
	public void AnimateInGraphics(PrintWriter print_writer, String whichGraphic) throws InterruptedException, IOException {
		
		switch (whichGraphic) {
		case "SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*In START \0");
			break;
		case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			break;
		}
	}
	public void AnimateOutGraphics(PrintWriter print_writer, String whichGraphic) throws IOException {
		
		switch (whichGraphic.toUpperCase()) {
		case "SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		}
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String viz_sence_path,Match match,List<TeamColor>color, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamScore"+ " SET " + 
					match.getHomeTeamScore() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamScore"+ " SET " + 
					match.getAwayTeamScore() + "\0");	
			
			if(is_this_updating == false) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgEventLogo"+ " SET " + 
						logo_path + "TLogo" + FootballUtil.PNG_EXTENSION  + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName"+ " SET " + 
						match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName"+ " SET " + 
						match.getAwayTeam().getTeamName4() + "\0");
				
				for(TeamColor tcolor : color) {
					if(match.getHomeTeamJerseyColor().equalsIgnoreCase(tcolor.getColorType())) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamColour1"+ " SET " + 
								tcolor.getRgb() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamColour2"+ " SET " + 
								tcolor.getRgb() + "\0");
					}
					if(match.getAwayTeamJerseyColor().equalsIgnoreCase(tcolor.getColorType())) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamColour1"+ " SET " + 
								tcolor.getRgb() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamColour2"+ " SET " + 
								tcolor.getRgb() + "\0");
					}
				}
			}
		}
		return scorebug;
	}
	public ScoreBug populateExtraTime(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAddedMinutes" + " SET " + time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*AddedMinIn START \0");
		}
		return scorebug;
	}
	public ScoreBug populateExtraTimeBoth(boolean is_this_updating,ScoreBug scorebug,PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAddedMinutes" + " SET " + time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*AddedMinIn START \0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*ExtraTimeIn START \0");
		}
		return scorebug;
	}
	
	public void populateMatchId(PrintWriter print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgHomeTeamBadge"+ " SET " + 
					logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgAwayTeamBadge"+ " SET " + 
					logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName01"+ " SET " + "" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName01"+ " SET " + "" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName02"+ " SET " + 
					match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName02"+ " SET " + 
					match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tVenue"+ " SET " + 
					"LIVE FROM "+ match.getVenueName().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.900 \0");
		}
	}
	public void populatePlayingXI(PrintWriter print_writer,String viz_scene, int TeamId,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0,l=4;
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName01"+ " SET " + "" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName02"+ " SET " + 
					team.get(TeamId-1).getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgTeamBadge"+ " SET " + 
					logo_path + team.get(TeamId-1).getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");

			if(team.get(TeamId-1).getTeamCoach() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachDesignation"+ " SET " + "" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachFirstName"+ " SET " + "" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachLastName"+ " SET " + "" + "\0");
				
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachDesignation"+ " SET " + "COACH: " + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachFirstName"+ " SET " + team.get(TeamId-1).getTeamCoach().split(" ")[0].toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tCoachLastName"+ " SET " + team.get(TeamId-1).getTeamCoach().split(" ")[1].toUpperCase() + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vLines"+ " SET " + "11" + "\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*TacticalIn SHOW 0.0 \0");
			//print_writer.println("-1 RENDERER*TREE*$Main$All$TacticalGrp*ACTIVE SET 0 \0");
			if(TeamId == match.getHomeTeamId()) {
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tNumber0" + row_id + " SET " + hs.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tFirstName0" + row_id + " SET " + hs.getFirstname().toUpperCase() + "\0");
					if(hs.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tLastName0" + row_id + " SET " + hs.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tLastName0" + row_id + " SET " + "" + "\0");
					}
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
			}else if(TeamId == match.getAwayTeamId()) {
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tNumber0" + row_id + " SET " + as.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tFirstName0" + row_id + " SET " + as.getFirstname().toUpperCase() + "\0");
					if(as.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tLastName0" + row_id + " SET " + as.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tLastName0" + row_id + " SET " + "" + "\0");
					}
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 1 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$GoalKeeperIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$CaptainIcon*ACTIVE SET 0 \0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$PlayerListAll$PlayerGrp0" + row_id + "$NameAndIcons$YellowCardIcon*ACTIVE SET 0 \0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
			}
		}
	}
	
	public void populateScoreUpdate(PrintWriter print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgHomeTeamBadge"+ " SET " + 
					logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgAwayTeamBadge"+ " SET " + 
					logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.HALF)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + 
						clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FULL)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + 
						clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FIRST)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.SECOND)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + "EXTRA TIME 1" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tGamePart"+ " SET " + "EXTRA TIME 2" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName01"+ " SET " + "" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName01"+ " SET " + "" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName02"+ " SET " + 
					match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName02"+ " SET " + 
					match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tSeperator"+ " SET " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScoreInfo" + " SET " + "1" + "\0");
			
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

								stats_txt = stats_txt.trim() + "," + 
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
			
			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore()==0) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + "0" + "\0");
			}else if(match.getHomeTeamScore() > match.getAwayTeamScore()) {
				if(home_stats.size() <= 5) {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + home_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + "5" + "\0");
				}
			}else if(match.getHomeTeamScore() < match.getAwayTeamScore()){
				if(away_stats.size() <= 5) {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + away_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + "5" + "\0");
				}
			}else {
				if(home_stats.size() > away_stats.size()) {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + home_stats.size() + "\0");
				}else if(home_stats.size() < away_stats.size()) {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + away_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + home_stats.size() + "\0");
				}
			}
			
			for(int j=1;j<=5;j++) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamScorer0" + j + " SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamScoreTime0" + j + " SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamScorer0" + j + " SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamScoreTime0" + j + " SET " + 
						"" + "\0");
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamScorer0" + (i+1) + " SET " + 
						home_stats.get(i).split(" ")[0] + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamScoreTime0" + (i+1) + " SET " + 
						home_stats.get(i).split(" ")[1] + "\0");
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamScorer0" + (i+1) + " SET " + 
						away_stats.get(i).split(" ")[0] + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamScoreTime0" + (i+1) + " SET " + 
						away_stats.get(i).split(" ")[1] + "\0");
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");
		}
	}
	public void populateLtMatchId(PrintWriter print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgHomeTeamBadge"+ " SET " + 
					logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "lgAwayTeamBadge"+ " SET " + 
					logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + FootballUtil.PNG_EXTENSION  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName01"+ " SET " + "" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName01"+ " SET " + "" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tHomeTeamName02"+ " SET " + 
					match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tAwayTeamName02"+ " SET " + 
					match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tSeperator"+ " SET " + "VS" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScoreInfo" + " SET " + "0" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "vScorer" + " SET " + "0" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main*FUNCTION*ControlObject*in SET ON " + "tInfo"+ " SET " + 
					"LIVE FROM "+ match.getVenueName().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");
		}
	}
}