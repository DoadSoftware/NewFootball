package com.football.broadcaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import jakarta.xml.bind.JAXBContext;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import com.football.model.*;
import com.football.service.FootballService;
import com.football.util.FootballFunctions;
import com.football.util.FootballUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.EuroLeague.Events;
import com.football.EuroLeague.LineUp;
import com.football.EuroLeague.LiveMatch;
import com.football.EuroLeague.PassMatrix;
import com.football.EuroLeague.Players;
import com.football.containers.FootballData;
import com.football.containers.Scene;
import com.football.containers.ScoreBug;
import com.football.controller.IndexController;

public class Super_Cup extends Scene{
	
	public String session_selected_broadcaster = FootballUtil.SUPER_CUP;
	
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public String which_LBand_onscreen = "",which_Right_LBand_onscreen = "",which_Bottom_LBand_onscreen = "";
	public boolean is_infobar = false;
	public boolean extraTime = false;
	public String flag_path = "IMAGE*/Default/Essentials/Flag_Id/";
	public String Lt_flag_path = "IMAGE*/Default/Essentials/Flags/";
	
	public String logo_path = "IMAGE*/Default/Essentials/Badges/";
	public String logo_bw_path = "IMAGE*/Default/Essentials/BadgesBW/";
	public String logo_outline_path = "IMAGE*/Default/Essentials/BadgesOutline/";
	public String logo2_path = "IMAGE*/Default/Design/";
	private String colors_path = "C:\\Images\\Super_Cup\\Colours\\";
	private String photos_path = "C:\\Images\\Super_Cup\\Photos\\";
	private String image_path = "C:\\Sports\\Football\\Statistic\\Match_Data\\";
	private String status;
	private String slashOrDash = "-",previousGFX="";
	private String formation = "",top_stats_value="";
	private int count = 0,TeamId=0,which_side=1;
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	public static LeaderBoard leaderBoard;
	public String vtp = "";
	public Player player;
	public static FootballData data = new FootballData();
	public ObjectMapper objectMapper = new ObjectMapper();
	
	public Super_Cup() {
		super();
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ScoreBug updateScoreBug(List<PrintWriter> print_writer, List<Scene> scenes, Match match,FootballService footballService) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(print_writer, true,scorebug, scenes.get(0).getScene_path(), match, session_selected_broadcaster);
			scorebug = populateExtraTime(print_writer, true,scorebug, null, match, session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(List<PrintWriter> print_writer, String whatToProcess,Match match,Clock clock, FootballService footballService, List<Scene> scenes, 
			String valueToProcess) throws Exception{
		System.out.println(whatToProcess);
		if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator"
					+ "$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + match.getHomePenaltiesHits() + "\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator"
					+ "$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + match.getAwayPenaltiesHits() + "\0", print_writer);
			
			System.out.println(penalties.toString());
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + 
							iHomeCont + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0", print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0 && 
					match.getHomePenaltiesMisses() == 0 && match.getAwayPenaltiesMisses() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0 && 
					match.getHomePenaltiesMisses() == 0 && match.getAwayPenaltiesMisses() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			if((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses()) != 0 && (match.getAwayPenaltiesHits()+match.getAwayPenaltiesHits()) != 0) {
				if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
					if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
						penalties = new ArrayList<String>();
					}
				}
			}
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0", print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0", print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0", print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0", print_writer);

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0", print_writer);

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0", print_writer);
			
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0", print_writer);
					
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		
		case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD": 
		case "POPULATE-EXTRA_TIME_HALF": case "POPULATE-SCOREBUG-CARD": case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON": case "POPULATE-SCOREBUG-PROMO":
		case "POPULATE-SCOREBUG_TEAM_STATS": case "POPULATE-SCOREBUG_PLAYER_STATS": case "POPULATE-HEADTOHEAD": case "POPULATE-SCOREBUG_STATS_API":
			
		case "POPULATE-FF-MATCHID": case "POPULATE-FF-PROMO": case "POPULATE-FF-PLAYINGXI": case "POPULATE-FF-MATCHSTATS": case "POPULATE-DOUBLE_PROMO": 
		case "POPULATE-FF-TEAMS": case "POPULATE-POINTS_TABLE": case "POPULATE-FIXTURES": case "POPULATE-POINTS_TABLE2": case "POPULATE-PLAYOFFS": 
		case "POPULATE-ROAD-TO-FINAL": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-ATTACKING_ZONE": case "POPULATE-FF-PLAYER_TOUCH_MAP":
		case "POPULATE-FF-TOURNAMENT_STATS": case "POPULATE-FF-TEAM_COMPARISON": case "POPULATE-FF-HEADTOHEAD": case "POPULATE-FF-FIXTUREANDRESULT":
		case "POPULATE-FF-TEAM_TOUCH":case "POPULATE-FF-PLAYER-PROFILE":case "POPULATE-FF-PLAYER-POINTER":case "POPULATE_HIGHLIGHT_SCORE_BUG":
		case "POPULATE-FF_SCORE":
			
		case "POPULATE-HOMESUB": case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB": case "POPULATE-QUAIFIERS": case "POPULATE-FF-CHETTRI":
		case "POPULATE-PENALTY": case "POPULATE-CHANGE_PENALTY": case "POPULATE-PLAYOFF_TREE":
		case "POPULATE-LOF-LINEUP": case "POPULATE-LOF-VERTICAL_FLIPPER": case "POPULATE-LOF-LEADERBOARD": case "POPULATE-VERTICAL_CHANGE_ON":
		case "POPULATE-CHETTRI_CHANGE_ON": case "POPULATE-LOF-AVG_FORMATION":
			
		case "POPULATE-L3-BUG-DB": case "POPULATE-HIGHLIGHT_SCOREBUG": case "POPULATE-MINI_POINTS_TABLE":
		
		case "POPULATE-LT-PROMO": case "POPULATE-L3-TEAMFIXTURE": case "POPULATE-L3-SCOREUPDATE": case "POPULATE-LT-MATCHID": 
		case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-L3-NAMESUPER-CARD": case "POPULATE-L3-SUBSTITUTE": 
		case "POPULATE-OFFICIALS": case "POPULATE-L3-HEATMAP": case "POPULATE-L3-TOP_STATS": case "POPULATE-L3-STAFF": case "POPULATE-LT-RESULT":
		case "POPULATE-LT-PLAYER_STATS":
		
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD": case "POPULATE-SCOREBUG-CARD":
			case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON": case "POPULATE-EXTRA_TIME_HALF": case "POPULATE-SCOREBUG-PROMO":
			case "POPULATE-SCOREBUG_TEAM_STATS": case "POPULATE-SCOREBUG_PLAYER_STATS": case "POPULATE-HEADTOHEAD": case "POPULATE-SCOREBUG_STATS_API":
			
			case "POPULATE-HOMESUB": case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB":
			case "POPULATE-CHANGE_PENALTY": case "POPULATE-VERTICAL_CHANGE_ON": case "POPULATE-CHETTRI_CHANGE_ON":
				break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID": case "POPULATE-FF-PROMO": case "POPULATE-FF-PLAYINGXI": case "POPULATE-FF-MATCHSTATS": case "POPULATE-DOUBLE_PROMO": 
			case "POPULATE-FF-TEAMS": case "POPULATE-POINTS_TABLE": case "POPULATE-FIXTURES": case "POPULATE-POINTS_TABLE2": case "POPULATE-PLAYOFFS": 
			case "POPULATE-ROAD-TO-FINAL": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-ATTACKING_ZONE": case "POPULATE-FF-PLAYER_TOUCH_MAP":
			case "POPULATE-FF-TOURNAMENT_STATS": case "POPULATE-FF-TEAM_COMPARISON": case "POPULATE-FF-HEADTOHEAD": case "POPULATE-FF-FIXTUREANDRESULT":
			case "POPULATE-FF-TEAM_TOUCH": case "POPULATE-FF_SCORE":
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE SHOW 0.0\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Reset START \0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6*ACTIVE SET 0 \0", print_writer);
				break;
			case "POPULATE-LOF-LINEUP": case "POPULATE-LOF-LEADERBOARD": case "POPULATE-L3-TOP_STATS": case "POPULATE-LOF-AVG_FORMATION":
			case "POPULATE_HIGHLIGHT_SCORE_BUG":
				if(which_graphics_onscreen == "" && which_graphics_onscreen.isEmpty()) {
					scenes.get(2).setScene_path(valueToProcess.split(",")[1]);
					scenes.get(2).scene_load(print_writer,session_selected_broadcaster);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE SHOW 0.0\0", print_writer);
				}
				break;
			default:
				scenes.get(2).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(2).scene_load(print_writer,session_selected_broadcaster);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE SHOW 0.0\0", print_writer);
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(print_writer, false,scorebug, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG-PROMO":
				scorebug.setScorebug_promo(valueToProcess.split(",")[1]);
				populateScoreBugPromo(print_writer, false,scorebug,Integer.valueOf(valueToProcess.split(",")[1]),footballService.getTeams(),
						footballService.getFixtures(),footballService.getGrounds(),match , session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(100);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 Promo_In 1.000 \0", print_writer);

				break;
			case "POPULATE-HEADTOHEAD":
				if(scorebug.getLast_scorebug_headTohead_stat() != null && !scorebug.getLast_scorebug_headTohead_stat().trim().isEmpty()) {
					scorebug.setScorebug_headTohead_stat(valueToProcess.split(",")[1]);
					populateScoreBugHeadToHeadStats(print_writer, false, scorebug,footballService.getTeams(),footballService.getHeadToHeadStats(), 
							match, session_selected_broadcaster);
				}else {
					scorebug.setScorebug_headTohead_stat(valueToProcess.split(",")[1]);
					populateScoreBugHeadToHeadStats(print_writer, false, scorebug,footballService.getTeams(),footballService.getHeadToHeadStats(), 
							match, session_selected_broadcaster);
				}
				TimeUnit.MILLISECONDS.sleep(100);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 HeadToHead_In 0.900 \0", print_writer);

				break;
			case "POPULATE-SCOREBUG_TEAM_STATS":
				if(scorebug.getLast_scorebug_team_stat() != null && !scorebug.getLast_scorebug_team_stat().trim().isEmpty()) {
					scorebug.setScorebug_team_stat(valueToProcess.split(",")[1]);
					populateScoreBugTeamStats(print_writer, false, scorebug, Integer.valueOf(valueToProcess.split(",")[1]), 
							footballService.getTeams(), footballService.getTeamStats(), match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
				}else {
					scorebug.setScorebug_team_stat(valueToProcess.split(",")[1]);
					populateScoreBugTeamStats(print_writer, false, scorebug, Integer.valueOf(valueToProcess.split(",")[1]), 
							footballService.getTeams(), footballService.getTeamStats(), match, session_selected_broadcaster);
				}
				TimeUnit.MILLISECONDS.sleep(100);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 TeamStats_In 2.140 \0", print_writer);
				break;
			case "POPULATE-SCOREBUG_PLAYER_STATS":
				if(scorebug.getLast_scorebug_player_stat() != null && !scorebug.getLast_scorebug_player_stat().trim().isEmpty()) {
					scorebug.setScorebug_player_stat(valueToProcess.split(",")[4]);
					populateScoreBugPlayerStats(print_writer, false, scorebug, Integer.valueOf(valueToProcess.split(",")[1]), valueToProcess.split(",")[2],
							Integer.valueOf(valueToProcess.split(",")[4]), valueToProcess.split(",")[3], footballService.getTeams(), footballService.getAllPlayer(), 
							footballService.getPlayerStats(), match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
				}else {
					scorebug.setScorebug_player_stat(valueToProcess.split(",")[4]);
					populateScoreBugPlayerStats(print_writer, false, scorebug, Integer.valueOf(valueToProcess.split(",")[1]), valueToProcess.split(",")[2],
							Integer.valueOf(valueToProcess.split(",")[4]), valueToProcess.split(",")[3], footballService.getTeams(), footballService.getAllPlayer(), 
							footballService.getPlayerStats(), match, session_selected_broadcaster);
				}
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 StatsImage_In 2.800 \0", print_writer);
				break;
				
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-SCOREBUG_STATS_API":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					if(whatToProcess.equalsIgnoreCase("POPULATE-SCOREBUG_STATS")) {
						scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
						populateScoreBugStats(print_writer, false,scorebug,Integer.valueOf(valueToProcess.split(",")[4]),Integer.valueOf(valueToProcess.split(",")[5]),
								match,session_selected_broadcaster);
					}else if(whatToProcess.equalsIgnoreCase("POPULATE-SCOREBUG_STATS_API")) {
						populateScoreBugStatsAPI(print_writer, false,scorebug,match,session_selected_broadcaster,footballService);
					}
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					if(whatToProcess.equalsIgnoreCase("POPULATE-SCOREBUG_STATS")) {
						scorebug.setScorebug_stat(valueToProcess.split(",")[2]);
						populateScoreBugStats(print_writer, false,scorebug,Integer.valueOf(valueToProcess.split(",")[4]),Integer.valueOf(valueToProcess.split(",")[5]),
								match,session_selected_broadcaster);
					}else if(whatToProcess.equalsIgnoreCase("POPULATE-SCOREBUG_STATS_API")) {
						populateScoreBugStatsAPI(print_writer, false,scorebug,match,session_selected_broadcaster,footballService);
					}
				}
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 Stats_In 1.000 \0", print_writer);

				break;
			case "POPULATE-SCOREBUG-CARD":	
				if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0", print_writer);
					TimeUnit.MILLISECONDS.sleep(500);
					
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(print_writer, scorebug, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					
				}else {
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(print_writer, scorebug, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
				}
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 Cards_In 2.580 \0", print_writer);
				break;
			case "POPULATE-SUBS_CHANGE_ON":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 Substitutes$Change 1.140 \0", print_writer);
				break;
			case "POPULATE-SCOREBUG-SUBS":
				scorebug.setScorebug_subs(valueToProcess.split(",")[2]);
				populateScorebugSubs(print_writer, scorebug, Integer.valueOf(valueToProcess.split(",")[1]), footballService.getAllPlayer(), match, 
						session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(100);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 Substitutes$Subtitutes_In 1.140 \0", print_writer);

				break;
			case "POPULATE-RED_CARD":
				populateRedcard(print_writer, false,scorebug,Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),
						match,session_selected_broadcaster);
				//TimeUnit.MILLISECONDS.sleep(100);
				//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/ScoreBug" + " C:/Temp/Preview.png In 1.500 RedCards_In 0.500s \0", print_writer);
				break;
			case "POPULATE-EXTRA_TIME_HALF":
				populateETONE_TWO(print_writer, false,scorebug,match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME":
				populateExtraTime(print_writer, false,scorebug,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME_BOTH":
				populateExtraTimeBoth(print_writer, false,scorebug,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-L3-TEAMFIXTURE":
				populateTeamFixture(print_writer,valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]), match, 
						session_selected_broadcaster,footballService.getTeams(),footballService.getFixtures());
				break;
			case "POPULATE-FF-MATCHID":
				populateMatchId(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster,footballService.getVariousTexts());
				break;
			case "POPULATE-FF_SCORE":
				populateFFMatchScore(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster,footballService.getVariousTexts());
				break;
			case "POPULATE_HIGHLIGHT_SCORE_BUG":
				populateBugScores(print_writer,match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-HEADTOHEAD":
				populateFF_H2H(print_writer,valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-PLAYOFFS":
				populatePlayoffs(print_writer, valueToProcess.split(",")[1],footballService.getPlayoffs(),footballService.getTeams(),
						footballService.getVariousTexts(),session_selected_broadcaster,match);
				break;
			case "POPULATE-FF-TEAMS":
				populateFFTeams(print_writer,valueToProcess.split(",")[1],footballService.getTeams(), footballService.getVariousTexts(), match, session_selected_broadcaster);
				break;
			case "POPULATE-ATTACKING_ZONE": case "POPULATE-FF-PLAYER_TOUCH_MAP":
				EuroLeague.Event(data);
				switch(whatToProcess.toUpperCase()) {
				case "POPULATE-ATTACKING_ZONE":
					populateAttacking(print_writer,valueToProcess.split(",")[1], match, data, Integer.valueOf(valueToProcess.split(",")[2]));
					break;
				case "POPULATE-FF-PLAYER_TOUCH_MAP":
					popualtePlayerTouchMap(print_writer,valueToProcess.split(",")[1], match, data, Integer.valueOf(valueToProcess.split(",")[2]), Integer.valueOf(valueToProcess.split(",")[3]));
					break;
				}
				break;
			case "POPULATE-LT-PLAYER_STATS":
				populateLTPlayerStats(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),footballService.getTeams(),
						FootballFunctions.processAllPlayerStats(footballService), match , session_selected_broadcaster);
				break;
			case "POPULATE-LT-PROMO":
				populateLTMatchPromoSingle(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),footballService.getTeams(),
						footballService.getFixtures(),footballService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),footballService.getTeams(),
						footballService.getFixtures(),footballService.getGrounds(),match , session_selected_broadcaster,footballService);
				break;
			case "POPULATE-PENALTY":
				populateLtPenalty(print_writer, valueToProcess.split(",")[1],valueToProcess, footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-CHANGE_PENALTY":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change_Out START \0", print_writer);
				TimeUnit.MILLISECONDS.sleep(800);
				populateLtPenaltyChange(print_writer, match,session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(800);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change_In START \0", print_writer);
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
			case "POPULATE-L3-STAFF":
				for(Staff st : footballService.getStaffs()) {
					  if(st.getStaffId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateStaff(print_writer, valueToProcess.split(",")[1], st,footballService.getTeams(), match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-SUBSTITUTE":
				scorebug.setScorebug_subs(valueToProcess.split(",")[4]);
				populateSubstitute(print_writer, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						footballService.getAllPlayer(),footballService.getTeams(), match, session_selected_broadcaster);
				break;	
			case "POPULATE-LOF-AVG_FORMATION":
				if(which_graphics_onscreen.equalsIgnoreCase("AVG_FORMATION")) {
					which_side = 2;
				}else {
					which_side = 1;
				}
				TeamId = Integer.valueOf(valueToProcess.split(",")[2]);
				populateAvgFormation(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(),
						footballService.getTeams(),footballService.getVariousTexts(),match, session_selected_broadcaster);
				
				if(which_side == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png In 2.000 \0", print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png Change 2.000 \0", print_writer);
				}
				break;
			case "POPULATE-LOF-LINEUP":
				if(which_graphics_onscreen.equalsIgnoreCase("LOF_LINEUP")) {
					which_side = 2;
				}else {
					which_side = 1;
				}
				TeamId = Integer.valueOf(valueToProcess.split(",")[2]);
				populateLofLineUp(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(),
						footballService.getTeams(),footballService.getVariousTexts(),match, session_selected_broadcaster);
				
				if(which_side == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png In 2.000 \0", print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png Change 2.000 \0", print_writer);
				}
				break;
				
			case "POPULATE-LOF-LEADERBOARD":
				if(which_graphics_onscreen.equalsIgnoreCase("LOF_LEADERBOARD")) {
					which_side = 2;
				}else {
					which_side = 1;
				}
				TeamId = Integer.valueOf(valueToProcess.split(",")[3]);
				top_stats_value = valueToProcess.split(",")[4];
				for(LeaderBoard leaderboard : FootballFunctions.processAllLeaderBoards(footballService)) {
					if(leaderboard.getLeaderboardId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						leaderBoard = leaderboard;
						populateLofLeaderBoard(print_writer, valueToProcess.split(",")[1], leaderboard, footballService.getTeams(), 
								match, session_selected_broadcaster);
					}
				}
				
				if(valueToProcess.split(",")[4].equalsIgnoreCase("with_photo")) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$Out$Select_EventLogo*FUNCTION*Omo*vis_con SET 1 \0", print_writer);
					String pos_perview = "";
					pos_perview = "Side" + which_side + "$Anim_Highlight$" + Integer.valueOf(valueToProcess.split(",")[3]) + "In 1.000";
					
					for(int i=5;i>=Integer.valueOf(valueToProcess.split(",")[3]);i--) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Side" + which_side + "$Position_Change$" + i + "$In START \0", print_writer);
						pos_perview = pos_perview + " Side" + which_side + "$Position_Change$" + i + "$In 1.000";
					}
					if(which_side == 1) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png In 2.000 " + pos_perview + "\0", print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png Change 2.000 " + pos_perview + "\0", print_writer);
					}
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$Out$Select_EventLogo*FUNCTION*Omo*vis_con SET " + "1" + "\0", print_writer);
					if(which_side == 1) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png In 2.000 \0", print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png Change 2.000 \0", print_writer);
					}
				}
				AnimateInLeaderBoardPlayer(print_writer,valueToProcess.split(",")[4],Integer.valueOf(valueToProcess.split(",")[3]));
				break;
			case "POPULATE-FF-CHETTRI":
				populateChettri(print_writer,valueToProcess.split(",")[1], valueToProcess.split(",")[2], session_selected_broadcaster);
				break;
			case "POPULATE-CHETTRI_CHANGE_ON":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change START \0", print_writer);
				break;
				
			case "POPULATE-LOF-VERTICAL_FLIPPER": case "POPULATE-VERTICAL_CHANGE_ON":
				if(whatToProcess.equalsIgnoreCase("POPULATE-LOF-VERTICAL_FLIPPER")) {
					TeamId = Integer.valueOf(valueToProcess.split(",")[2]);
					populateLofVerticalFlipper(print_writer,valueToProcess.split(",")[1], 1, Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFormations(),
							footballService.getTeams(),match, session_selected_broadcaster);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/VerticalFlipper" + " C:/Temp/Preview.png In 2.200 \0", print_writer);
					count = count + 1;
				}else {
					if((formation.split("-").length == 3 && count == 4)||(formation.split("-").length == 4 && count == 5)||(formation.split("-").length == 5 && count == 6)) {
						this.status = FootballUtil.END;
						return status;
					}
					else {
						populateLofVerticalFlipper(print_writer,"/Default/VerticalFlipper", 2, 0,footballService.getFormations(),footballService.getTeams(),
								match, session_selected_broadcaster);
						TimeUnit.MILLISECONDS.sleep(1000);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change START \0", print_writer);
						
						TimeUnit.MILLISECONDS.sleep(2200);
						
						populateLofVerticalFlipper(print_writer,"/Default/VerticalFlipper", 1, 0,footballService.getFormations(),footballService.getTeams(),
								match, session_selected_broadcaster);
						TimeUnit.MILLISECONDS.sleep(1000);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change SHOW 0.0 \0", print_writer);
						count = count + 1;
					}
				}
				break;
			case "POPULATE-FF-FIXTUREANDRESULT":
				populateFF_FixtureAndResult(print_writer, valueToProcess.split(",")[1], ((valueToProcess.split(",").length > 2 && 
						!valueToProcess.split(",")[2].equalsIgnoreCase("undefined")) ? valueToProcess.split(",")[3] : " "), valueToProcess.split(",")[2], 
						FootballFunctions.processAllFixtures(footballService),footballService.getPlayoffs(),match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-TEAM_TOUCH":
				populateFF_TeamTouch(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3],
						match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYER-PROFILE":
				populateFF_Profile(print_writer, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYER-POINTER":	
				populateFF_Pointers(print_writer, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						footballService.getFormations(), footballService.getTeams(),footballService.getVariousTexts(),match, session_selected_broadcaster);
				break;
			case "POPULATE-HOMESUB":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team1$DataIn 2.520 LineUp$Team1$Change 2.100 \0", print_writer);
				break;
			case "POPULATE-AWAYXI":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team1$DataIn 0.000 LineUp$Team2$DataIn 2.520 \0", print_writer);
				break;
			case "POPULATE-AWAYSUB":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team2$DataIn 2.520 LineUp$Team2$Change 2.100 \0", print_writer);
				break;	
			case "POPULATE-L3-HEATMAP":
				populateHeatMapPeakDistance(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3] ,
						Integer.valueOf(valueToProcess.split(",")[4]),footballService.getAllPlayer(),match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-TOP_STATS":
				if(which_graphics_onscreen.equalsIgnoreCase("TOP_STATS")) {
					which_side = 2;
				}else {
					which_side = 1;
				}
				top_stats_value = valueToProcess.split(",")[2];
				switch(valueToProcess.split(",")[2].toUpperCase()) {
				case "TEAM TOP SPEED": case "HIGHEST DISTANCE": case "BEST RUNNER": case "BEST SPRINTER":
					populateTopStats(print_writer, valueToProcess.split(",")[1], valueToProcess.split(",")[2], FootballFunctions.getTopStatsDatafromXML(match),
							footballService.getAllPlayer(), footballService.getTeams(), match, session_selected_broadcaster);
					break;
				case "TOUCHES": case "DUEL WON": case "SUCCESSFUL DRIBBLE": case "RECOVERIES": case "AERIAL DUELS WON":
					populateTopStats(print_writer, valueToProcess.split(",")[1], valueToProcess.split(",")[2],footballService.getAllPlayer(), 
							footballService.getTeams(), match, session_selected_broadcaster);
					break;
				}
				
				if(which_side == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png In 2.000 \0", print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + valueToProcess.split(",")[1] + " C:/Temp/Preview.png Change 2.000 \0", print_writer);
				}
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(print_writer, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-TOURNAMENT_STATS":
				populateTournamentStats(print_writer, valueToProcess.split(",")[1],footballService, match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-TEAM_COMPARISON":
				populateTeamComparison(print_writer, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
				
			case "POPULATE-OFFICIALS":
				populateOfficials(print_writer, valueToProcess.split(",")[1],footballService.getOfficials(),match, session_selected_broadcaster);
				break;
			case "POPULATE-HIGHLIGHT_SCOREBUG":
				populateBugHighlight(print_writer,valueToProcess.split(",")[1],match,clock, session_selected_broadcaster);
				break;
			 case "POPULATE-L3-BUG-DB":
				 for(Bugs bug : footballService.getBugs()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							populateBug(print_writer,valueToProcess.split(",")[1],bug,match,clock, session_selected_broadcaster);
					  }
				}
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(print_writer,valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2], match,footballService.getFixtures(),
						footballService.getTeams(),footballService.getGrounds(),footballService.getVariousTexts(), session_selected_broadcaster);
				break;
			case "POPULATE-LT-RESULT":
				populateMatchResult(print_writer, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),footballService.getFixtures(),footballService.getTeams(),footballService.getGrounds(),
						session_selected_broadcaster,match);
				break;
			case "POPULATE-FIXTURES":
				populateFixtures_6(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2],valueToProcess.split(",")[3],footballService.getPlayoffs(),
						FootballFunctions.processAllFixtures(footballService),session_selected_broadcaster,match);
				
//				populateFixtures_7(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2],valueToProcess.split(",")[3],footballService.getPlayoffs(),
//						FootballFunctions.processAllFixtures(footballService),session_selected_broadcaster,match);
				break;
			case "POPULATE-PLAYOFF_TREE":
				populatePlayOffTree(print_writer, valueToProcess.split(",")[1],footballService.getPlayoffs(),footballService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-QUAIFIERS":
				populateQulifiers(print_writer, valueToProcess.split(",")[1],session_selected_broadcaster,match);
				break;
			case "POPULATE-POINTS_TABLE2":
				LeagueTable league_table1 = null;
				LeagueTable league_table2 = null;
				if(valueToProcess.split(",")[2].equalsIgnoreCase("SemiFinal1")) {
					if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML").exists()) {
						league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML"));
					}
					if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML").exists()) {
						league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML"));
					}
					
				}else if(valueToProcess.split(",")[2].equalsIgnoreCase("SemiFinal2")) {
					if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableC" + ".XML").exists()) {
						league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableC" + ".XML"));
					}
					if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableD" + ".XML").exists()) {
						league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableD" + ".XML"));
					}
				}
				populatePointsTableGrp(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2],league_table1.getLeagueTeams(),league_table2.getLeagueTeams(),
						footballService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-ROAD-TO-FINAL":
				LeagueTable league_table3 = null;
				LeagueTable league_table4 = null;
				
							
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + match.getHomeTeam().getTeamGroup() + ".XML").exists()) {
					league_table3 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + match.getHomeTeam().getTeamGroup() + ".XML"));
				}
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + match.getAwayTeam().getTeamGroup() + ".XML").exists()) {
					league_table4 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTable" + match.getAwayTeam().getTeamGroup() + ".XML"));
				}
				
				populateRoadToFinal(print_writer, valueToProcess.split(",")[1],league_table3.getLeagueTeams(),league_table4.getLeagueTeams(),
						footballService.getTeams(),session_selected_broadcaster,match);
				break;	
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table = null;
				
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML").exists()) {
					league_table = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML"));
				}
				
				populatePointsTable(print_writer, valueToProcess.split(",")[1],valueToProcess.split(",")[2],league_table.getLeagueTeams(),footballService.getTeams(),
						session_selected_broadcaster,match,footballService);
				break;
			case "POPULATE-MINI_POINTS_TABLE":
				LeagueTable mini_league_table = null, leagueTable  = null;
				List<LeagueTeam> new_league_table = null;
				
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML").exists()) {
					mini_league_table = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML"));
					leagueTable = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML"));
				}
				
				if(valueToProcess.split(",")[3].equalsIgnoreCase("AS IT STAND")) {
					new_league_table = FootballFunctions.PointsTableAsStanding(leagueTable.getLeagueTeams(), match, FootballUtil.FULL);
				}
				
				populateMiniPointsTable(print_writer, valueToProcess.split(",")[1], valueToProcess.split(",")[2], valueToProcess.split(",")[3],
						mini_league_table.getLeagueTeams(), new_league_table, footballService.getTeams(), session_selected_broadcaster, match, footballService);
				break;
			}
		
		case"TOP_STATS-OPTIONS_DATA":
			List<String > statsData= new ArrayList<String>();
			List<PlayerStats> playersStats = new ArrayList<PlayerStats>();
			switch(valueToProcess.split(",")[1].toUpperCase()) {
			case "TEAM TOP SPEED": case "HIGHEST DISTANCE": case "BEST RUNNER": case "BEST SPRINTER":
			    if (Arrays.asList("team top speed", "highest distance", "best runner", "best sprinter").contains(valueToProcess.split(",")[1].toLowerCase())) {
				        for (TeamStats stats : FootballFunctions.getTopStatsDatafromXML(match)) {
				            for (TopStats topStats : stats.getTopStats()) {
				                if (topStats.getHeader().toLowerCase().equalsIgnoreCase(valueToProcess.split(",")[1].toLowerCase())) {
				                    playersStats.addAll(topStats.getPlayersStats());
				                }
				            }
				        }
				    }
			    	Collections.sort(playersStats,new FootballFunctions.PlayerStatsComparator());
			    	for(int i=0;i<5;i++) {
			    		if(playersStats.size()>i) {
				    		statsData.add(","+playersStats.get(i).getFirst_name()+", "+playersStats.get(i).getValue());
			    		}
			    	}
					break;
			case "TOUCHES": case "DUEL WON": case "SUCCESSFUL DRIBBLE": case "RECOVERIES": case "AERIAL DUELS WON":
				data = new FootballData();
				EuroLeague.LiveData(data);
				ArrayList<com.football.containers.Players> plyr = new ArrayList<com.football.containers.Players>();
				for(com.football.containers.Team teams : data.getTeam()) {
					for(com.football.containers.Players player : teams.getTeamPlayer()) {
						footballService.getAllPlayer().stream()
					    .filter(ply -> ply.getPlayerAPIId()!=null && player.getId()!=null && ply.getPlayerAPIId().equalsIgnoreCase(player.getId().trim()))
					    .findAny()
					    .ifPresent(matchingPlayer -> player.setName(matchingPlayer.getTicker_name()));

						plyr.add(player);
					}
				}
				switch(valueToProcess.split(",")[1].toUpperCase()) {
				case "TOUCHES":
					Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getTouches(), p1.getTouches()));
					for(int i=0;i<5;i++) {
			    		statsData.add(","+plyr.get(i).getName()+", "+plyr.get(i).getTouches());
			    	}
					break;
				case "DUEL WON":
					Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getDuelWon(), p1.getDuelWon()));
					for(int i=0;i<5;i++) {
			    		statsData.add(","+plyr.get(i).getName()+", "+plyr.get(i).getDuelWon());
			    	}
					break;
				case "SUCCESSFUL DRIBBLE":
					Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getWonContest(), p1.getWonContest()));
					for(int i=0;i<5;i++) {
			    		statsData.add(","+plyr.get(i).getName()+", "+plyr.get(i).getWonContest());
			    	}
					break;
				case "RECOVERIES":
					Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getBallRecovery(), p1.getBallRecovery()));
					for(int i=0;i<5;i++) {
			    		statsData.add(","+plyr.get(i).getName()+", "+plyr.get(i).getBallRecovery());
			    	}
					break;
				case "AERIAL DUELS WON":
					Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getAerialWon(), p1.getAerialWon()));
					for(int i=0;i<5;i++) {
			    		statsData.add(","+plyr.get(i).getName()+", "+plyr.get(i).getAerialWon());
			    	}
					break;
				}
				break;
			}
			return objectMapper.writeValueAsString(statsData);
			
		case "SCOREBUG_API_GRAPHICS-OPTIONS":
//			ApiMatch api_match = new ApiMatch();		
//			FootballFunctions.TeamStatApi(new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class), api_match);
			
//			String stats = "Possession,Shots,Shots on Target,Touches,Touches In OppBox,Offside," +
//		               "Passes,Accurate Pass,Passing Accuracy,Final Third Passes,passes final 3rd Accuracy," +
//		               "Final 3rd Entries,Crosses,Corners,Corners Won,Dribbles,Successful Dribbles," +
//		               "Duel,Duel won,Aerial,Tackles,Tackles Won,Interceptions,InterceptionsWon," +
//		               "Fouls Won,Fouls,Yellow Cards,Red Cards,Saves,Chance Created,long Pass," +
//		               "long Pass Success,Shots Inside Box,Possession Won in the Final Third,Possession Won";
			
//			List<String> this_data_str = FootballFunctions.MatchStatsSingle(api_match, stats.replace(" ", "_"));
			
			String stats = "Possession,Shots,Shots_on_Target,Corners,Saves,Crosses,Passes,Passing_Accuracy,Touches,Tackles,"
							+ "Offside,Fouls,Interceptions,Chance_Created,goalsConceded,duelWon,Red_Cards,Yellow_Cards,Duel_won,"
							+ "Duel,passes_final_3rd_Accuracy,Final_3rd_Entries,Touches_In_OppBox,Final_Third_Passes,Goals";
			
			List<String> this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, stats);
			
			return objectMapper.writeValueAsString(this_data_str);
		case "TEAMFIXTURE_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(footballService.getTeams());
		case "NAMESUPER_GRAPHICS-OPTIONS": 
			return objectMapper.writeValueAsString(footballService.getNameSupers());
		case "BUG_DB_GRAPHICS-OPTIONS":case "DB_GRAPHICS":
			return objectMapper.writeValueAsString(footballService.getBugs());
		case "STAFF_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(footballService.getStaffs());
		case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS": case "SCOREBUGPROMO_GRAPHICS-OPTIONS": case "RESULT_PROMO_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(FootballFunctions.processAllFixtures(footballService));
		case "LT_PLAYER_STATS_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(FootballFunctions.processAllPlayerStats(footballService));
		case "LEADERBOARD_GRAPHICS-OPTIONS":
			return objectMapper.writeValueAsString(FootballFunctions.processAllLeaderBoards(footballService));
		case "SCOREBUG_GRAPHICS-OPTIONS":
			List<String > data= new ArrayList<String>();
			try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY  + "ScoreBugTeamStatsOptions.txt"))) {
				String line;
	            while ((line = br.readLine()) != null) {
	                data.add(line);
	            }
			}
	        return objectMapper.writeValueAsString(data);
		case "CHECK_FOR_PLAYER_DATA":
			return objectMapper.writeValueAsString(checkForData(footballService.getTeams(), footballService.getAllPlayer(), 
				Integer.valueOf(valueToProcess.split(",")[1]), match));
		case "ANIMATE-SCOREBUG_STATS":	
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG_TEAM_STATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*TeamStats_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG-HEADTOHEAD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*HeadToHead_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG-PROMO":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG-SUBS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG_PLAYER_STATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*StatsImage_In START \0", print_writer);
			break;
		case "ANIMATE-SCOREBUG-CARD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_In START \0", print_writer);
			break;	
		case "ANIMATE-RED_CARD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_In START \0", print_writer);
			break;
		case "ANIMATE-IN-HIGHLIGHT_SCORE_BUG":
			AnimateInGraphics(print_writer, "HIGHLIGHT_SCOREBUG");
			which_graphics_onscreen = "HIGHLIGHT_SCOREBUG";
			break;
		case "ANIMATE-IN-SCOREBUG": case "ANIMATE-IN-SPONSOR": case "ANIMATE-IN-SUBS_CHANGE_ON": case "ANIMATE-IN-HIGHLIGHT_SCOREBUG": case "ANIMATE-IN-BUG-DB":
		case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-PROMO": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-HOMESUB": case "ANIMATE-IN-AWAYXI": case "ANIMATE-IN-AWAYSUB":
		case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-IN-FF_TEAMS": case "ANIMATE-IN-POINTS_TABLE":
		case "ANIMATE-IN-FIXTURES": case "ANIMATE-IN-QUAIFIERS": case "ANIMATE-IN-POINTS_TABLE2": case "ANIMATE-IN-PLAYOFFS":
		case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-NAMESUPER_CARD": case "ANIMATE-IN-NAMESUPER": case "ANIMATE-IN-NAMESUPERDB":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-OFFICIALS": case "ANIMATE-IN-HEATMAP": case "ANIMATE-IN-TOP_STATS": case "ANIMATE-IN-STAFF":
		case "ANIMATE-IN-PENALTY": case "ANIMATE-IN-LTPROMO": case "ANIMATE-IN-RESULT": case "ANIMATE-IN-ROAD-TO-FINAL": case "ANIMATE-IN-TEAMFIXTURE":
		case "ANIMATE-IN-LOF_LINEUP": case "ANIMATE-IN-VERTICAL_FLIPPER": case "ANIMATE-IN-LOF_LEADERBOARD": case "ANIMATE-IN-TOURNAMENT_STATS":
		case "ANIMATE-IN-TEAM_COMPARISON": case "ANIMATE-IN-CHETTRI": case "ANIMATE-IN-FF_HEADTOHEAD": case "ANIMATE-IN-FIXTUREANDRESULT": 
		case "ANIMATE-IN-TEAM_TOUCH": case "ANIMATE-IN-AVG_FORMATION": case "ANIMATE-IN-LT_PLAYER_STATS": case "ANIMATE-IN-PLAYOFF_TREE":
		case "ANIMATE-ATTACKING_ZONE": case "ANIMATE-IN-PLAYER_TOUCH_MAP": case "ANIMATE-IN-FF_SCORE":
		case "ANIMATE-SUB_CHANGE_ON": case "ANIMATE-IN-FLAG": case "ANIMATE-IN-MINI_POINTS_TABLE":
		case "CLEAR-ALL":case"ANIMATE-FF-PLAYER-POINTER":case"ANIMATE-FF-PLAYER-PROFILE":
		case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-OUT-EXTRA_TIME": case "ANIMATE-OUT-SCOREBUG_STAT": case"ANIMATE-OUT-RED_CARD":
		case "ANIMATE-OUT": case "CANCEL_GFX":
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-SCOREBUG":
				AnimateInGraphics(print_writer, "SCOREBUG");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				scorebug.setScorebug_sponsor_on_screen(false);
				scorebug.setScorebug_ET_on_screen(false);
				scorebug.setScorebug_flag_on_screen(false);
				break;
			case "ANIMATE-IN-TEST":
				AnimateInGraphics(print_writer, "TEST");
				which_graphics_onscreen = "TEST";
				break;
			case "ANIMATE-IN-BUG-DB":
				AnimateInGraphics(print_writer, "BUG-DB");
				which_graphics_onscreen = "BUG-DB";
				break;
			case "ANIMATE-IN-QUAIFIERS":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In START \0", print_writer);
				which_graphics_onscreen = "QUAIFIERS";
				break;
			case "ANIMATE-IN-PLAYOFF_TREE":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In START \0", print_writer);
				which_graphics_onscreen = "PLAYOFF_TREE";
				break;
			case "ANIMATE-SUB_CHANGE_ON":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In CONTINUE \0", print_writer);
				break;
			case "ANIMATE-IN-PLAYOFFS":
				AnimateInGraphics(print_writer, "PLAYOFFS");
				which_graphics_onscreen = "PLAYOFFS";
				break;
			case "ANIMATE-IN-LT_PLAYER_STATS":
				AnimateInGraphics(print_writer, "LT_PLAYER_STATS");
				which_graphics_onscreen = "LT_PLAYER_STATS";
				break;
			case "ANIMATE-IN-SPONSOR":
				if(scorebug.isScorebug_flag_on_screen() == false) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_In START \0", print_writer);
					scorebug.setScorebug_flag_on_screen(true);
				}
				else if(scorebug.isScorebug_flag_on_screen() == true) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_Out START \0", print_writer);
					scorebug.setScorebug_flag_on_screen(false);
				}
				break;
			case "ANIMATE-IN-FLAG":
				if(scorebug.isScorebug_sponsor_on_screen() == false) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Flag_In START \0", print_writer);
					scorebug.setScorebug_sponsor_on_screen(true);
				}
				else if(scorebug.isScorebug_sponsor_on_screen() == true) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Flag_Out START \0", print_writer);
					scorebug.setScorebug_sponsor_on_screen(false);
				}
				break;
			case "ANIMATE-IN-SUBS_CHANGE_ON":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Change START \0", print_writer);
				break;
			case "ANIMATE-IN-FF_TEAMS":
				AnimateInGraphics(print_writer, "FF_TEAMS");
				which_graphics_onscreen = "FF_TEAMS";
				break;
			case "ANIMATE-IN-MATCHID":
				AnimateInGraphics(print_writer, "MATCHID");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-FF_SCORE":
				AnimateInGraphics(print_writer, "FF_SCORE");
				which_graphics_onscreen = "FF_SCORE";
				break;
			case "ANIMATE-IN-FF_HEADTOHEAD":
				AnimateInGraphics(print_writer, "FF_HEADTOHEAD");
				which_graphics_onscreen = "FF_HEADTOHEAD";
				break;
			
			case "ANIMATE-IN-FIXTURES":
				AnimateInGraphics(print_writer, "FIXTURES");
				which_graphics_onscreen = "FIXTURES";
				break;
			case "ANIMATE-IN-MINI_POINTS_TABLE":
				AnimateInGraphics(print_writer, "MINI_POINTS_TABLE");
				which_graphics_onscreen = "MINI_POINTS_TABLE";
				break;
			case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-POINTS_TABLE2": case "ANIMATE-IN-ROAD-TO-FINAL":
				AnimateInGraphics(print_writer, "POINTS_TABLE");
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				AnimateInGraphics(print_writer, "MATCHSTATUS");
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-TOURNAMENT_STATS":
				AnimateInGraphics(print_writer, "TOURNAMENT_STATS");
				which_graphics_onscreen = "TOURNAMENT_STATS";
				break;
			case "ANIMATE-IN-TEAM_COMPARISON":
				AnimateInGraphics(print_writer, "TEAM_COMPARISON");
				which_graphics_onscreen = "TEAM_COMPARISON";
				break;
				
			case "ANIMATE-IN-MATCHSTATS":
				AnimateInGraphics(print_writer, "MATCHSTATS");
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PROMO":
				AnimateInGraphics(print_writer, "MATCHSINGLEPROMO");
				which_graphics_onscreen = "MATCHSINGLEPROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				AnimateInGraphics(print_writer, "DOUBLE_PROMO");
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-PENALTY":
				AnimateInGraphics(print_writer, "PENALTY");
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-HEATMAP":
				AnimateInGraphics(print_writer, "HEATMAP");
				which_graphics_onscreen = "HEATMAP";
				break;
			case "ANIMATE-IN-TOP_STATS":
				if(which_side == 1) {
					AnimateInGraphics(print_writer, "TOP_STATS");
				}else {
					ChangeOnGraphics(print_writer, "TOP_STATS");
					TimeUnit.MILLISECONDS.sleep(2200);
					which_side = 1;
					switch(top_stats_value.toUpperCase()) {
					case "TEAM TOP SPEED": case "HIGHEST DISTANCE": case "BEST RUNNER": case "BEST SPRINTER":
						populateTopStats(print_writer, "/Default/Lof_LeaderBoard", top_stats_value, FootballFunctions.getTopStatsDatafromXML(match),
								footballService.getAllPlayer(), footballService.getTeams(), match, session_selected_broadcaster);
						break;
					case "TOUCHES": case "DUEL WON": case "SUCCESSFUL DRIBBLE": case "RECOVERIES": case "AERIAL DUELS WON":
						populateTopStats(print_writer, "/Default/Lof_LeaderBoard", top_stats_value,footballService.getAllPlayer(), 
								footballService.getTeams(), match, session_selected_broadcaster);
						break;
					}
					TimeUnit.MILLISECONDS.sleep(500);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change SHOW 0.0 \0", print_writer);
				}
				which_graphics_onscreen = "TOP_STATS";
				break;
			case "ANIMATE-IN-TEAMFIXTURE":
				AnimateInGraphics(print_writer, "TEAMFIXTURE");
				which_graphics_onscreen = "TEAMFIXTURE";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				AnimateInGraphics(print_writer, "NAMESUPER_CARD");
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				AnimateInGraphics(print_writer, "NAMESUPER");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-STAFF":
				AnimateInGraphics(print_writer, "STAFF");
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				AnimateInGraphics(print_writer, "NAMESUPERDB");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-OFFICIALS":
				AnimateInGraphics(print_writer, "OFFICIALS");
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "ANIMATE-IN-HIGHLIGHT_SCORE_BUG":
				AnimateInGraphics(print_writer, "HIGHLIGHTT_SCORE_BUG");
				which_graphics_onscreen = "HIGHLIGHTT_SCORE_BUG";
				break;
			case "ANIMATE-IN-RESULT":
				AnimateInGraphics(print_writer, "RESULT");
				which_graphics_onscreen = "RESULT";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				AnimateInGraphics(print_writer, "SUBSTITUTE");
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-HOMESUB":
				AnimateInGraphics(print_writer, "HOMESUB");
				previousGFX="HOMESUB";
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-AWAYXI":
				AnimateInGraphics(print_writer, "AWAYXI");
				which_graphics_onscreen = "PLAYINGXI";
				previousGFX="AWAYXI";
				break;
			case "ANIMATE-IN-AWAYSUB":
				AnimateInGraphics(print_writer, "AWAYSUB");
				which_graphics_onscreen = "PLAYINGXI";
				previousGFX="AWAYSUB";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				AnimateInGraphics(print_writer, "PLAYINGXI");
				which_graphics_onscreen = "PLAYINGXI";
				previousGFX="PLAYINGXI";
				break;
			case"ANIMATE-FF-PLAYER-POINTER":
				AnimateInGraphics(print_writer, "PLAYER-POINTER");
				which_graphics_onscreen = "PLAYER-POINTER";
				break;
			case"ANIMATE-FF-PLAYER-PROFILE":
				AnimateInGraphics(print_writer, "PLAYER-PROFILE");
				which_graphics_onscreen = "PLAYER-PROFILE";
				break;
			case "ANIMATE-IN-FIXTUREANDRESULT":
				AnimateInGraphics(print_writer, "FIXTUREANDRESULT");
				which_graphics_onscreen = "FIXTUREANDRESULT";
				break;
			case "ANIMATE-IN-TEAM_TOUCH":
				AnimateInGraphics(print_writer, "TEAM_TOUCH");
				which_graphics_onscreen = "TEAM_TOUCH";
				break;
			case "ANIMATE-IN-LOF_LINEUP":
				if(which_side == 1) {
					AnimateInGraphics(print_writer, "LOF_LINEUP");
				}else {
					ChangeOnGraphics(print_writer, "LOF_LINEUP");
					TimeUnit.MILLISECONDS.sleep(2000);
					which_side = 1;
					populateLofLineUp(print_writer, "/Default/Lof_LineUp", TeamId, footballService.getFormations(), footballService.getTeams(),
							footballService.getVariousTexts(), match, session_selected_broadcaster);
					
					TimeUnit.MILLISECONDS.sleep(500);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change SHOW 0.0 \0", print_writer);
				}
				which_graphics_onscreen = "LOF_LINEUP";
				break;
			case "ANIMATE-IN-AVG_FORMATION":
				if(which_side == 1) {
					AnimateInGraphics(print_writer, "AVG_FORMATION");
				}else {
					ChangeOnGraphics(print_writer, "AVG_FORMATION");
					TimeUnit.MILLISECONDS.sleep(2000);
					which_side = 1;
					populateAvgFormation(print_writer, "/Default/Lof_LineUp", TeamId, footballService.getFormations(), footballService.getTeams(),
							footballService.getVariousTexts(), match, session_selected_broadcaster);
					
					TimeUnit.MILLISECONDS.sleep(500);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change SHOW 0.0 \0", print_writer);
				}
				which_graphics_onscreen = "AVG_FORMATION";
				break;
			case "ANIMATE-IN-VERTICAL_FLIPPER":
				AnimateInGraphics(print_writer, "VERTICAL_FLIPPER");
				which_graphics_onscreen = "VERTICAL_FLIPPER";
				break;
			case "ANIMATE-IN-CHETTRI":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In START \0", print_writer);
				which_graphics_onscreen = "CHETTRI";
				break;
			case"ANIMATE-ATTACKING_ZONE":
				AnimateInGraphics(print_writer, "ATTACKING_ZONE");
				which_graphics_onscreen = "ATTACKING_ZONE";
				break;
			case "ANIMATE-IN-PLAYER_TOUCH_MAP":
				AnimateInGraphics(print_writer, "PLAYER_TOUCH_MAP");
				which_graphics_onscreen = "PLAYER_TOUCH_MAP";
				break;
			case "ANIMATE-IN-SCOREBUG_PLAYER_STATS":
				AnimateInGraphics(print_writer, "PLAYER_STATS");
				which_graphics_onscreen = "PLAYER_STATS";
				break;
			case "ANIMATE-IN-LOF_LEADERBOARD":
				if(which_side == 1) {
					AnimateInGraphics(print_writer, "LOF_LEADERBOARD");
				}else {
					ChangeOnGraphics(print_writer, "LOF_LEADERBOARD");
					TimeUnit.MILLISECONDS.sleep(2000);
					which_side = 1;
					populateLofLeaderBoard(print_writer, "/Default/Lof_LeaderBoard", leaderBoard, footballService.getTeams(), 
							match, session_selected_broadcaster);
					
					TimeUnit.MILLISECONDS.sleep(500);
					AnimateInLeaderBoardPlayer(print_writer,top_stats_value,TeamId);
					TimeUnit.MILLISECONDS.sleep(1000);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change SHOW 0.0 \0", print_writer);
				}
				
				which_graphics_onscreen = "LOF_LEADERBOARD";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				if(is_infobar == true && scorebug.isScorebug_on_screen() == true) {
					AnimateOutGraphics(print_writer, "SCOREBUG");
					scorebug.setScorebug_on_screen(false);
					TimeUnit.MILLISECONDS.sleep(500);
				}
				AnimateInGraphics(print_writer, "SCOREUPDATE");
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						//processAnimation(session_socket, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						//processAnimation(session_socket, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						//processAnimation(session_socket, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-LTPROMO":
				AnimateInGraphics(print_writer, "LTPROMO");
				which_graphics_onscreen = "LTPROMO";
				break;
			case "ANIMATE-IN-LT_MATCHID":
				AnimateInGraphics(print_writer, "LT_MATCHID");
				which_graphics_onscreen = "LT_MATCHID";
				break;
			case "CLEAR-ALL":
				FootballFunctions.DoadWriteCommandToAllViz("-1 SCENE CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 IMAGE CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 GEOM CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 FONT CLEANUP\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 IMAGE INFO\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER SET_OBJECT SCENE*" + valueToProcess.split(",")[0] + "\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*SCENE_DATA INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*UPDATE SET 0\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE SHOW 0.0\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*UPDATE SET 1\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*/Default/ScoreBug-Single\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*UPDATE SET 0\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE SHOW 0.0\0", print_writer);

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*UPDATE SET 1\0", print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER SET_OBJECT SCENE*/Default/FullFrames\0", print_writer);
	           	
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*SCENE_DATA INITIALIZE\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*UPDATE SET 0\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE SHOW 0.0\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Reset START \0", print_writer);
               
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*UPDATE SET 1\0", print_writer);
	               
				FootballFunctions.DoadWriteCommandToAllViz("-1 SCENE CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 IMAGE CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 GEOM CLEANUP\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 FONT CLEANUP\0", print_writer);
				which_graphics_onscreen = "";
				which_Right_LBand_onscreen ="";
				which_Bottom_LBand_onscreen="";
				is_infobar = false;
				scorebug.setScorebug_on_screen(false);
				count = 0;extraTime=false;
				this.status = "";
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					AnimateOutGraphics(print_writer, "SCOREBUG");
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
					scorebug.setScorebug_sponsor_on_screen(false);
					scorebug.setScorebug_ET_on_screen(false);
					extraTime = false;
				}
				break;
				
			case "CANCEL_GFX":
				scorebug.setLast_scorebug_stat("");scorebug.setScorebug_stat("");
				scorebug.setLast_scorebug_card_goal("");scorebug.setScorebug_card_goal("");
				scorebug.setLast_scorebug_promo("");scorebug.setScorebug_promo("");
				scorebug.setLast_scorebug_subs("");scorebug.setScorebug_subs("");
				scorebug.setLast_scorebug_team_stat("");scorebug.setScorebug_team_stat("");
				scorebug.setLast_scorebug_player_stat("");scorebug.setScorebug_player_stat("");
				scorebug.setLast_scorebug_headTohead_stat("");scorebug.setScorebug_headTohead_stat("");
				break;
				
			case "ANIMATE-OUT-SCOREBUG_STAT":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_Out START \0", print_writer);
					scorebug.setLast_scorebug_stat("");scorebug.setScorebug_stat("");
				}
				else if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0", print_writer);
					scorebug.setLast_scorebug_card_goal("");scorebug.setScorebug_card_goal("");
				}
				else if(scorebug.getLast_scorebug_promo() != null && !scorebug.getLast_scorebug_promo().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_Out START \0", print_writer);
					scorebug.setLast_scorebug_promo("");scorebug.setScorebug_promo("");
				}
				else if(scorebug.getLast_scorebug_subs() != null && !scorebug.getLast_scorebug_subs().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_Out START \0", print_writer);
					TimeUnit.MILLISECONDS.sleep(1500);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes SHOW 0.0 \0", print_writer);
					scorebug.setLast_scorebug_subs("");scorebug.setScorebug_subs("");
				}
				else if(scorebug.getLast_scorebug_team_stat() != null && !scorebug.getLast_scorebug_team_stat().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*TeamStats_Out START \0", print_writer);
					scorebug.setLast_scorebug_team_stat("");scorebug.setScorebug_team_stat("");
				}
				else if(scorebug.getLast_scorebug_player_stat() != null && !scorebug.getLast_scorebug_player_stat().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*StatsImage_Out START \0", print_writer);
					scorebug.setLast_scorebug_player_stat("");scorebug.setScorebug_player_stat("");
				}
				else if(scorebug.getLast_scorebug_headTohead_stat() != null && !scorebug.getLast_scorebug_headTohead_stat().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*HeadToHead_Out START \0", print_writer);
					scorebug.setLast_scorebug_headTohead_stat("");scorebug.setScorebug_headTohead_stat("");
				}
				break;
			case"ANIMATE-OUT-RED_CARD":	
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_Out START \0", print_writer);
				break;
			case "ANIMATE-OUT-EXTRA_TIME":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_Out START \0", print_writer);
				extraTime=false;
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "PLAYOFF_TREE":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Out START \0", print_writer);
					break;
				case "SCOREUPDATE":
					AnimateOutGraphics(print_writer, which_graphics_onscreen);
					if(is_infobar == true && scorebug.isScorebug_on_screen() == false) {
						TimeUnit.MILLISECONDS.sleep(500);
						AnimateInGraphics(print_writer, "SCOREBUG");
						if(extraTime==true) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In SHOW 0.600 \0", print_writer);
						}
						scorebug.setScorebug_on_screen(true);
					}
					which_graphics_onscreen = "";
					break;
				case "MATCHID": case "PLAYINGXI": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "HEADTOHEAD":
				case "SUBSTITUTE": case "PLAYER_STATS":case "MATCHSINGLEPROMO": case "MATCHSTATUS": case "OFFICIALS": case "HEATMAP": case "MATCHSTATS": case "TOP_STATS":
				case "STAFF": case "PENALTY": case "DOUBLE_PROMO": case "FF_TEAMS": case "POINTS_TABLE": case "FIXTURES": case "QUAIFIERS": case "LTPROMO":
				case "PLAYOFFS": case "RESULT": case "ROAD-TO-FINAL": case "HIGHLIGHT_SCOREBUG": case "BUG-DB": case "TEAMFIXTURE": case "LOF_LINEUP":
				case "VERTICAL_FLIPPER": case "LOF_LEADERBOARD": case "TOURNAMENT_STATS": case "TEAM_COMPARISON": case "CHETTRI": case "FF_HEADTOHEAD":
				case "ATTACKING_ZONE": case "FIXTUREANDRESULT": case "PLAYER_TOUCH_MAP": case "TEAM_TOUCH": case "AVG_FORMATION": case "LT_PLAYER_STATS":
				case "SCOREBUG_PROMO":case "SCOREBUG_STATS": case "SCOREBUG_STATS_API":case "SCOREBUG-SUBS":case "SCOREBUG-CARD":case "SCOREBUG_TEAM_STATS":
				case"PLAYER-POINTER":case"PLAYER-PROFILE":case "HIGHLIGHT_SCORE_BUG": case "FF_SCORE": case "MINI_POINTS_TABLE":
					AnimateOutGraphics(print_writer, which_graphics_onscreen);
					which_graphics_onscreen = "";
					count = 0;
					this.status = "";
					break;
				}
				break;
			}
			break;
		}
		return null;
	}

	private void populateBugScores(List<PrintWriter> print_writer, Match match, String session_selected_broadcaster) {
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$Seperator$txt_HomeScore*GEOM*TEXT SET " + 
				(match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$Seperator$txt_AwayScore*GEOM*TEXT SET " + 
				(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + 
				match.getHomeTeam().getTeamName4() + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + 
				match.getAwayTeam().getTeamName4() + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$EventLogo*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0", print_writer);

		String time = "";
		if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")|| match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
			time = match.getClock().getMatchHalves().toUpperCase() + "  TIME";
		}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")||match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
			time = match.getClock().getMatchHalves().toUpperCase() + " HALF";
			
		}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1") || match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
			time = "EXTRA TIME";
		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$EventLogoOut$EventLogo*TEXTURE*IMAGE SET "+ logo_path + "TLogo" + "\0", print_writer);	
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$TimePart$TimeIn$TimePosition$txt_Clock*GEOM*TEXT SET " + 
				time.toUpperCase() + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
				colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
				colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0", print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + "/Default/HighlightScoreBug" + " C:/Temp/Preview.png In 1.500 \0", print_writer);
		
	}

	private void populateFF_Pointers(List<PrintWriter> print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws FileNotFoundException, IOException {
		String text_to_return = "";
		ArrayList<String> Chettri_data = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY  + "PlayerPointers.txt"))) {
			while((text_to_return = br.readLine()) != null) {
				Chettri_data.add(text_to_return);
			}
		}
		//header
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$Header$"
				+ "txt_Name*GEOM*TEXT SET " + Chettri_data.get(0) + "\0", print_writer);

		//subheader
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$Header$"
				+  "txt_CountryName*GEOM*TEXT SET " + Chettri_data.get(1) + "\0", print_writer);
		//flag
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$ImageShine$"
				+  "img_Flag_Id*TEXTURE*IMAGE SET " + logo_path + Chettri_data.get(2) + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$ImageShine$"
				+  "img_Flag_Id_Glow*TEXTURE*IMAGE SET " + logo_path + Chettri_data.get(2) + "\0", print_writer);
		
		//rows
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Side1$Select_Rows*FUNCTION*Omo*vis_con SET "+(Chettri_data.size()-3) + "\0", print_writer);
		for(int i=3;i<Chettri_data.size();i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Side1$Row" + (i-2) + "$Data$Dehighlight$Text$"
					+ "txt_Text*GEOM*TEXT SET " +Chettri_data.get(i)  + "\0", print_writer);

		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.200 \0", print_writer);

	}

	private void populateFF_Profile(List<PrintWriter> print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws FileNotFoundException, IOException {
		String text_to_return = "";
		ArrayList<String> Chettri_data = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY  + "PlayerStats.txt"))) {
			while((text_to_return = br.readLine()) != null) {
				Chettri_data.add(text_to_return);
			}
		}
		//header
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$Header$"
				+ "txt_Name*GEOM*TEXT SET " +Chettri_data.get(0) + "\0", print_writer);

		//subheader
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$Header$"
				+  "txt_CountryName*GEOM*TEXT SET " +Chettri_data.get(1) + "\0", print_writer);
		//flag
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$ImageShine$"
				+  "img_Flag_Id*TEXTURE*IMAGE SET " + flag_path +Chettri_data.get(2) + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$ImageShine$"
				+  "img_Flag_Id_Glow*TEXTURE*IMAGE SET " + flag_path +Chettri_data.get(2) + "\0", print_writer);
		
		//rows
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Select_Rows*FUNCTION*Omo*vis_con SET "+(Chettri_data.size()-3) + "\0", print_writer);
		for(int i=3;i<Chettri_data.size();i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Select_Rows$Row" + (i-2) + "$Data$SelectType*FUNCTION*Omo*vis_con SET "+"0" + "\0", print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i-2) + "$Data$Dehighlight$Text$"
					+ "txt_StatHead*GEOM*TEXT SET " +Chettri_data.get(i).split(",")[0]  + "\0", print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i-2) + "$Data$Dehighlight$Text$"
					+ "txt_StatValue*GEOM*TEXT SET " +Chettri_data.get(i).split(",")[1] + "\0", print_writer);

		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.200 \0", print_writer);

	}

	public void populateAttacking(List<PrintWriter> print_writer, String viz_scene, Match match, FootballData data, Integer TeamIndex) {
		
		double total_left = 0,total_center = 0, total_right = 0;
		int total = (data.getTeam().get(TeamIndex).getLeft() + data.getTeam().get(TeamIndex).getCenter() + data.getTeam().get(TeamIndex).getRight());
		total_left = ((double)data.getTeam().get(TeamIndex).getLeft() / total)*100;
		total_center = ((double)data.getTeam().get(TeamIndex).getCenter() / total)*100;
		total_right = ((double)data.getTeam().get(TeamIndex).getRight() / total)*100;
		
		DecimalFormat df = new DecimalFormat("#.##");
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FF_Required$HashTag*ACTIVE SET 0\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot1*ACTIVE SET 0 \0", print_writer);
		
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$LogoGrp$img_Badges*GEOM*TEXTURE*IMAGE SET " 
   				 + data.getTeam().get(TeamIndex).getCode().toUpperCase() + "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$txt_Header*GEOM*TEXT SET " 
   				 +data.getTeam().get(TeamIndex).getName().toUpperCase()+ "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " 
   				 +"ATTACKING ZONE"+ "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + "" + "\0", print_writer);
   		
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone1$txt_Value*GEOM*TEXT SET " 
   				 + Math.round(total_left) + "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone2$txt_Value*GEOM*TEXT SET " 
   				 + Math.round(total_center) + "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone3$txt_Value*GEOM*TEXT SET " 
   				 + Math.round(total_right) + "\0", print_writer);
   		
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone1$BarGrp$geom_BarScale_X"
   				+ "*TRANSFORMATION*SCALING*X SET " + df.format(((double)data.getTeam().get(TeamIndex).getLeft() / total)) + "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone2$BarGrp$geom_BarScale_X"
   				+ "*TRANSFORMATION*SCALING*X SET " + df.format(((double)data.getTeam().get(TeamIndex).getCenter() / total)) + "\0", print_writer);
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$Data$GroundOut$ZoneValues$Zone3$BarGrp$geom_BarScale_X"
   				+ "*TRANSFORMATION*SCALING*X SET " + df.format(((double)data.getTeam().get(TeamIndex).getRight() / total)) + "\0", print_writer);
   		
   		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 AttackingZone_In 2.500 \0", print_writer);
	}
	public void popualtePlayerTouchMap(List<PrintWriter> print_writer, String viz_scene, Match match, FootballData data, int TeamId, int PlayerId) throws StreamReadException, DatabindException, IOException {
		
		LiveMatch Event = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\MatchEvent.json"), LiveMatch.class);
		ArrayList<String> live_data = new ArrayList<String>();
		String PlayerApiId="",Player_Name="",Player_Photo="";
		
		if(match.getHomeTeamId() == TeamId) {
			for(Player plyr : match.getHomeSquad()) {
				if(plyr.getPlayerId() == PlayerId) {
					PlayerApiId = plyr.getPlayerAPIId();
					Player_Name = plyr.getFull_name();
					Player_Photo = plyr.getPhoto();
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayerTouch$LogoGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0", print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayerTouch$LogoGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0", print_writer);
					break;
				}
			}
		}else if(match.getAwayTeamId() == TeamId){
			for(Player plyr : match.getAwaySquad()) {
				if(plyr.getPlayerId() == PlayerId) {
					PlayerApiId = plyr.getPlayerAPIId();
					Player_Name = plyr.getFull_name();
					Player_Photo = plyr.getPhoto();
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayerTouch$LogoGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0", print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayerTouch$LogoGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0", print_writer);
					break;
				}
			}
		}
		
		for(Events event :   Event.getLiveData().getEvent()) { 
			if(event.getPlayerId() != null && event.getPlayerId().equalsIgnoreCase(PlayerApiId)) { 
				//1, 2,3, 4 (outcome = 1), 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 41, 42, 50, 54, 61,73, 74 
				if((event.getTypeId()>=1 && event.getTypeId()<=4)) { 
					if(event.getOutcome()==1) {
						live_data.add(event.getX() + "-" + event.getY());
					} 
				}else if((event.getTypeId()>=7 && event.getTypeId()<=16)|| (event.getTypeId()==41) || (event.getTypeId()==42)|| 
						(event.getTypeId()==50)||(event.getTypeId()==54)|| (event.getTypeId()==61) || (event.getTypeId()==73)||
						( event.getTypeId()==74)) {
					live_data.add(event.getX() + "-" + event.getY());
				}
			} 
		} 
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + 
				"TOUCH MAP" + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$txt_Header*GEOM*TEXT SET " + 
				Player_Name + "\0", print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$PlayerImageGrp$img_Player*TEXTURE*IMAGE SET " 
				+ photos_path + Player_Photo + FootballUtil.PNG_EXTENSION + "\0", print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FF_Required$HashTag*ACTIVE SET 0\0", print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$BottomInfoGrp$txt_Info"
				+ "*GEOM*TEXT SET " + "" + "\0", print_writer);
		
		for(int i=1;i<=300;i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$Data$GroundOut$TouchData$DotGrp"
					+ "$TouchDot" + i + "*ACTIVE SET 0 \0", print_writer);
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$Data$GroundOut$GroundAll$DotsAll*ACTIVE SET 1 \0", print_writer);
		
		for(int i=0;i<=live_data.size()-1;i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
					"*ACTIVE SET 1 \0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
					"*TRANSFORMATION*POSITION*X SET " + live_data.get(i).split("-")[0] + "\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayerTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
					"*TRANSFORMATION*POSITION*Y SET " + live_data.get(i).split("-")[1] + "\0", print_writer);
		}
	   		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PlayerTouch_In 2.500 \0", print_writer);
	}
	public void populateFF_TeamTouch(List<PrintWriter> print_writer, String viz_scene, int TeamId, String which_data, Match match, String selectedbroadcaster) throws StreamReadException, DatabindException, IOException {
		
		LiveMatch Event = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\MatchEvent.json"), LiveMatch.class);
		ArrayList<String> live_data = new ArrayList<String>();
		String TeamApiId="";
		
		if(match.getHomeTeamId() == TeamId) {
			TeamApiId = match.getHomeTeam().getTeamApiId();
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$txt_Header*GEOM*TEXT SET " + 
					match.getHomeTeam().getTeamName1() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamTouch$AllData$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamTouch$AllData$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
		}
		else if(match.getAwayTeamId() == TeamId){
			TeamApiId = match.getAwayTeam().getTeamApiId();
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$txt_Header*GEOM*TEXT SET " + 
					match.getAwayTeam().getTeamName1() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamTouch$AllData$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamTouch$AllData$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + 
				which_data.toUpperCase() + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FF_Required$HashTag*ACTIVE SET 0\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$BottomInfoGrp$txt_Info"
				+ "*GEOM*TEXT SET " + "" + "\0",print_writer);
		
		for(int i=1;i<=300;i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$Data$GroundOut$TouchData$DotGrp"
					+ "$TouchDot" + i + "*ACTIVE SET 0 \0",print_writer);
		}
	
		if(which_data.equalsIgnoreCase("successful passes")) {
			for(Events event :   Event.getLiveData().getEvent()) {
				if(event.getContestantId() != null && event.getContestantId().equalsIgnoreCase(TeamApiId)) {
					if(event.getTypeId() == 1 && event.getOutcome() == 1) {
						live_data.add(event.getX() + "-" + event.getY());
					}
				}
			}
			System.out.println(live_data.size());
		}else if(which_data.equalsIgnoreCase("successful dribble")){
			for(Events event :   Event.getLiveData().getEvent()) {
				if(event.getContestantId() != null && event.getContestantId().equalsIgnoreCase(TeamApiId)) {
					if(event.getTypeId() == 3 && event.getOutcome() == 1) {
						live_data.add(event.getX() + "-" + event.getY());
					}
				}
			}
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$Data$GroundOut$GroundAll$DotsAll*ACTIVE SET 1 \0",print_writer);
		
		for(int i=0;i<=live_data.size()-1;i++) {
			if(i< 300) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
						"*ACTIVE SET 1 \0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
						"*TRANSFORMATION*POSITION*X SET " + live_data.get(i).split("-")[0] + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$TeamTouch$AllData$Data$GroundOut$TouchData$DotGrp$TouchDot" + (i+1) + 
						"*TRANSFORMATION*POSITION*Y SET " + live_data.get(i).split("-")[1] + "\0",print_writer);
			}
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 TeamTouch_In 2.500 \0",print_writer);
		
	}
	
	public void populateChettri(List<PrintWriter> print_writer, String viz_scene, String which_data, String selectedbroadcaster) throws FileNotFoundException, IOException {
		
		String text_to_return = "";
		ArrayList<String> Chettri_data = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY + which_data + ".txt"))) {
			while((text_to_return = br.readLine()) != null) {
				Chettri_data.add(text_to_return);
			}
		}
		
		switch(which_data) {
		case "Chettri1":
			for(int i=0;i<=Chettri_data.size()-1;i++) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "txt_StatHead*GEOM*TEXT SET " + Chettri_data.get(i).split("-")[0] + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "txt_StatValue*GEOM*TEXT SET " + Chettri_data.get(i).split("-")[1] + "\0",print_writer);
		    }
			break;
		case "Chettri2":
			for(int i=0;i<=Chettri_data.size()-1;i++) {
				if(i<4) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Side1$Row" + (i+1) + "$Data$Dehighlight"
							+ "$Text$txt_Text*GEOM*TEXT SET " + Chettri_data.get(i) + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Side2$Row" + ((i-4)+1) + "$Data$Dehighlight"
							+ "$Text$txt_Text*GEOM*TEXT SET " + Chettri_data.get(i) + "\0",print_writer);
				}
		    }
			break;
		case "Chettri3":
			for(int i=0;i<=Chettri_data.size()-1;i++) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "txt_txt_Name*GEOM*TEXT SET " + Chettri_data.get(i).split(",")[0] + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "$img_Flag*TEXTURE*IMAGE SET "+ logo_path + Chettri_data.get(i).split(",")[1] + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "txt_StatValue*GEOM*TEXT SET " + Chettri_data.get(i).split(",")[2] + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$AllBg$AllData$Row" + (i+1) + "$Data$Highlight$Text$"
						+ "CapsGrp$txt_CapsFigure*GEOM*TEXT SET " + Chettri_data.get(i).split(",")[3] + "\0",print_writer);
		    }
			break;
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.200 \0",print_writer);
	}
	
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}
	
	public void AnimateInGraphics(List<PrintWriter> print_writer, String whichGraphic) throws InterruptedException, IOException {
		
		switch (whichGraphic) {
		case "SCOREBUG":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Reset START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_In START \0", print_writer);
			break;
		case "MINI_POINTS_TABLE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In START \0", print_writer);
			break;
			
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP":case "TOP_STATS": case "STAFF": case "PENALTY": case "LTPROMO": case "RESULT": case "HIGHLIGHT_SCOREBUG": case "BUG-DB":
		case "TEAMFIXTURE":case "LOF_LINEUP": case "VERTICAL_FLIPPER": case "LOF_LEADERBOARD": case "AVG_FORMATION": case "LT_PLAYER_STATS":
		case"PLAYER-POINTER":case"PLAYER-PROFILE":case "HIGHLIGHT_SCORE_BUG":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*In START \0", print_writer);
			break;
		case "MATCHID": case "MATCHSINGLEPROMO": case "FF_SCORE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchId_In START \0", print_writer);
			break;
		case "FF_HEADTOHEAD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*HeadToHead_In START \0", print_writer);
			break;
		case "FIXTURES":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Reset START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Fixtures_6_In START \0", print_writer);
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Fixtures_7_In START \0", print_writer);
			break;
		case "POINTS_TABLE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PointsTable_In START \0", print_writer);
			break;
		case "DOUBLE_PROMO":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*DoubleID_In START \0", print_writer);
			break;
		case "FF_TEAMS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Groups_In START \0", print_writer);
			break;
		case "MATCHSTATUS": case "TOURNAMENT_STATS": case "TEAM_COMPARISON":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchStats_In START \0", print_writer);
			break;
		case "PLAYOFFS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PlayOffs_In START \0", print_writer);
			break;
		case "MATCHSTATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchScorers_In START \0", print_writer);
			break;
		case "PLAYINGXI":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Reset START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team1$DataIn START \0", print_writer);
			break;
		case "FIXTUREANDRESULT":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Results_In START \0", print_writer);
			break;
		case "HOMESUB":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team1$Change START \0", print_writer);
			break;
		case "AWAYXI":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team1$DataOut START \0", print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team2$DataIn START \0", print_writer);
			break;
		case "AWAYSUB":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team2$Change START \0", print_writer);
			break;
		case "ATTACKING_ZONE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*AttackingZone_In START\0", print_writer);
			break;
		case "PLAYER_TOUCH_MAP":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PlayerTouch_In START\0", print_writer);
			break;
		case "TEAM_TOUCH":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_In START\0", print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*TeamTouch_In START\0", print_writer);
			break;
		}
	}
	public void AnimateOutGraphics(List<PrintWriter> print_writer, String whichGraphic) throws IOException, InterruptedException {
		
		switch (whichGraphic.toUpperCase()) {
		case "SCOREBUG":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_Out START \0",print_writer);
			break;
		case "MINI_POINTS_TABLE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Out START \0", print_writer);
			break;
			
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP": case "TOP_STATS": case "STAFF": case "PENALTY": case "QUAIFIERS": case "LTPROMO": case "RESULT": case "HIGHLIGHT_SCOREBUG":
		case "BUG-DB": case "TEAMFIXTURE": case "LOF_LINEUP": case "VERTICAL_FLIPPER": case "LOF_LEADERBOARD": case "CHETTRI": case "AVG_FORMATION":
		case "LT_PLAYER_STATS":case"PLAYER-POINTER":case"PLAYER-PROFILE":case "HIGHLIGHT_SCORE_BUG":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "MATCHID": case "MATCHSINGLEPROMO": case "FF_SCORE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchId_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG-CARD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchId_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG_TEAM_STATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchId_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG_STATS": 
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG_STATS_API":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_O START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG-SUBS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "SCOREBUG_PROMO":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "FF_HEADTOHEAD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*HeadToHead_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "HEADTOHEAD":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*HeadToHead_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "PLAYER_STATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*StatsImage_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(100);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "ATTACKING_ZONE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*AttackingZone_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START\0",print_writer);
			break;
		case "PLAYER_TOUCH_MAP":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PlayerTouch_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START\0",print_writer);
			break;
		case "TEAM_TOUCH":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*TeamTouch_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START\0",print_writer);
			break;
		case "PLAYOFFS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PlayOffs_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "FIXTURES":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Fixtures_6_Out START \0",print_writer);
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Fixtures_7_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(1000);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Reset START \0",print_writer);
			break;
		case "POINTS_TABLE":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*PointsTable_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "DOUBLE_PROMO":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*DoubleID_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "FF_TEAMS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Groups_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "MATCHSTATUS": case "TOURNAMENT_STATS": case "TEAM_COMPARISON":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchStats_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "MATCHSTATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*MatchScorers_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		case "PLAYINGXI":
			if(previousGFX.equalsIgnoreCase("HOMESUB")||previousGFX.equalsIgnoreCase("PLAYINGXI")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team1$DataOut START \0",print_writer);
			}else if(previousGFX.equalsIgnoreCase("AWAYXI")||previousGFX.equalsIgnoreCase("AWAYSUB")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp$Team2$DataOut START \0",print_writer);	
			}
			TimeUnit.MILLISECONDS.sleep(400);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(2000);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*LineUp SHOW 0.0\0",print_writer);
			break;
		case "FIXTUREANDRESULT":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Results_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*FF_Out START \0",print_writer);
			TimeUnit.MILLISECONDS.sleep(200);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*STAGE*DIRECTOR*Out START \0",print_writer);
			break;
		}
	}
	public void ChangeOnGraphics(List<PrintWriter> print_writer, String whichGraphic) throws InterruptedException, IOException {
		switch (whichGraphic) {
		case "LOF_LINEUP": case "LOF_LEADERBOARD": case "TOP_STATS":
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Change START \0",print_writer);
			
			if(whichGraphic.equalsIgnoreCase("LOF_LEADERBOARD")) {
				TimeUnit.MILLISECONDS.sleep(2200);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Side1 SHOW 0.0 \0",print_writer);
			}
			break;
			
		}
	}
	public void AnimateInLeaderBoardPlayer(List<PrintWriter> print_writer, String PhotoType, int PlayerNumber) throws InterruptedException, IOException {
		
		if(PhotoType.equalsIgnoreCase("with_photo")) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Side" + which_side + "$Anim_Highlight$" + PlayerNumber + "$In START \0",print_writer);
			
			for(int i=5;i>=Integer.valueOf(PlayerNumber);i--) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE*DIRECTOR*Side" + which_side + "$Position_Change$" + i + "$In START \0",print_writer);
			}
		}
		
	}
	
	public ScoreBug populateScoreBug(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + 
					(match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + 
					(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			//------------red cards
			String red = FootballFunctions.getRedCardCount(0, 0, match);
			int home_red = Integer.valueOf(red.split(",")[0]);
			int away_red = Integer.valueOf(red.split(",")[1]);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " +  
					home_red + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " +  
					away_red + "\0",print_writer);
			
			if(is_this_updating == false) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$Select_WaterMark*ACTIVE SET 0\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$Select_WaterMark*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$img_EventLogo*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$img_Flag*TEXTURE*IMAGE SET " + 
						logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$img_Flag*TEXTURE*IMAGE SET " + 
						logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
				
			}
		}
		return scorebug;
	}
	public ScoreBug populateScoreBugStats(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,int Homedata,int Awaydata ,Match match, String selectedbroadcaster) 
			throws MalformedURLException, IOException, CsvException, InterruptedException {
		
//		switch(scorebug.getScorebug_stat().toUpperCase()) {
//		case FootballUtil.YELLOW:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "YELLOW CARD" + "\0");
//			break;
//		case FootballUtil.RED:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "RED CARD" + "\0");
//			break;
//		case FootballUtil.OFF_SIDE:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "OFFSIDES" + "\0");
//			break;
//		case FootballUtil.SHOTS:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + FootballUtil.SHOTS + "\0");
//			break;
//		case FootballUtil.POSSESSION:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + FootballUtil.POSSESSION + " %" + "\0");
//			break;
//		case FootballUtil.SHOTS_ON_TARGET:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "SHOTS ON TARGET" + "\0");
//			break;
//		case FootballUtil.CORNERS:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + FootballUtil.CORNERS + "\0");
//			break;
//		case FootballUtil.TACKLES:
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + FootballUtil.TACKLES + "\0");
//			break;
//		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + scorebug.getScorebug_stat().toUpperCase()+ "\0",print_writer);

		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_HomeStatValue*GEOM*TEXT SET " + 
				Homedata + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_AwayStatValue*GEOM*TEXT SET " + 
				Awaydata + "\0",print_writer);
		
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateScoreBugStatsAPI(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,Match match, String selectedbroadcaster, FootballService footballService) 
			throws MalformedURLException, IOException, CsvException, InterruptedException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		  
//		ApiMatch api_match = new ApiMatch();		
//		FootballFunctions.TeamStatApi(new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class), api_match);
//		List<String> this_data_str = FootballFunctions.MatchStatsSingle(api_match, scorebug.getScorebug_stat());
//		String  WhichStyle = String.join(",", scorebug.getScorebug_stat()).replace("_", " ");
//		WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
		
		List<String> this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, scorebug.getScorebug_stat());
		String  WhichStyle = String.join(",", scorebug.getScorebug_stat()).replace("_", " ");
		WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
		
		 
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
        
        FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_HomeStatValue*GEOM*TEXT SET " + 
        		this_data_str.get(0).split(",")[0] + "\0",print_writer);
        
        FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_AwayStatValue*GEOM*TEXT SET " + 
        		this_data_str.get(0).split(",")[2] + "\0",print_writer);
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateRedcard(List<PrintWriter> print_writer,boolean is_this_updating, ScoreBug scorebug,int Homedata,int Awaydata, 
			Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$CardGrp$SelectCardNumber"
				+ "*FUNCTION*Omo*vis_con SET " + Homedata + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$CardGrp$SelectCardNumber"
				+ "*FUNCTION*Omo*vis_con SET " + Awaydata + "\0",print_writer);
			
		return scorebug;
	}
	public ScoreBug populateETONE_TWO(List<PrintWriter> print_writer,boolean is_this_updating, ScoreBug scorebug,Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		if(scorebug.isScorebug_ET_on_screen() == false) {
			if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1) || 
					match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$ExtraPart$TimeIn$txt_ExtraTime*GEOM*TEXT SET " + "ET" + "\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$ExtraPart$TimeIn$txt_ExtraTime*GEOM*TEXT SET " + "" + "\0",print_writer);
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*ExtraTime_In START \0",print_writer);
			scorebug.setScorebug_ET_on_screen(true);
		}
		else if(scorebug.isScorebug_ET_on_screen() == true) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*ExtraTime_Out START \0",print_writer);
			scorebug.setScorebug_ET_on_screen(false);
		}
		return scorebug;
	}
	public ScoreBug populateExtraTime(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug, String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		if(is_this_updating == false) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeIn$TimePosition$InjuryTimeGrp$txt_AddedMinute*GEOM*TEXT SET " + "+" +  time_value + "'" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0",print_writer);
			extraTime = true;
		}
		return scorebug;
	}
	public ScoreBug populateExtraTimeBoth(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		if(is_this_updating == false) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeIn$TimePosition$InjuryTimeGrp$txt_AddedMinute*GEOM*TEXT SET " + "+" +  time_value + "'" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0",print_writer);
		}
		return scorebug;
	}
	public ScoreBug populateScorebugCard(List<PrintWriter> print_writer,ScoreBug scorebug,int TeamId,int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 200;
			String team_name = "";
			if(TeamId == match.getHomeTeamId()) {
				team_name = match.getHomeTeam().getTeamName1();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0",print_writer);
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0",print_writer);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + hsub.getTicker_name().toUpperCase() + "\0",print_writer);
					}
				}
			}
			else {
				team_name = match.getAwayTeam().getTeamName1();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0",print_writer);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + as.getTicker_name().toUpperCase() + "\0",print_writer);
						
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0",print_writer);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + asub.getTicker_name().toUpperCase() + "\0",print_writer);
					}
				}
			}
			
			switch(scorebug.getScorebug_card_goal().toUpperCase())
			{
			case "YELLOW_CARD":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0",print_writer);
				break;
			case "RED_CARD":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0",print_writer);
				break;
			case "YELLOW_RED":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0",print_writer);
				break;
			case "PLAYER":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 0 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0",print_writer);
				break;
			}
			scorebug.setLast_scorebug_card_goal(scorebug.getScorebug_card_goal().toUpperCase());
		}
		return scorebug;
	}
	public ScoreBug populateScorebugSubs(List<PrintWriter> print_writer,ScoreBug scorebug,int TeamId,List<Player> plyr, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 200;

			if(match.getHomeTeamId() == TeamId) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$TeamNameGrp$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0",print_writer);
			}else if(match.getAwayTeamId() == TeamId) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$TeamNameGrp$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0",print_writer);
			}
			switch(scorebug.getScorebug_subs().split(":").length)
			{
			case 1:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				break;
			case 2:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				break;
				
			case 3:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "3" + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[2].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[2].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[2].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[2].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[1].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getTicker_name().toUpperCase() + "\0",print_writer);
				
				break;
			}
			
		}
		
		scorebug.setLast_scorebug_subs(scorebug.getScorebug_subs().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateScoreBugTeamStats(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug, int teamId ,List<Team> team, List<TeamStat> teamStats, Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			for(TeamStat teamStat : teamStats) {
				if(teamStat.getTeamId() == teamId) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$1$"
							+ "txt_StatHead*GEOM*TEXT SET " + teamStat.getHeadStats1() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$2$"
							+ "txt_StatHead*GEOM*TEXT SET " + teamStat.getHeadStats2() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$3$"
							+ "txt_StatHead*GEOM*TEXT SET " + teamStat.getHeadStats3() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$4$"
							+ "txt_StatHead*GEOM*TEXT SET " + teamStat.getHeadStats4() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$1$"
							+ "txt_StatValue*GEOM*TEXT SET " + teamStat.getValueStats1() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$2$"
							+ "txt_StatValue*GEOM*TEXT SET " + teamStat.getValueStats2() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$3$"
							+ "txt_StatValue*GEOM*TEXT SET " + teamStat.getValueStats3() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber$4$"
							+ "txt_StatValue*GEOM*TEXT SET " + teamStat.getValueStats4() + "\0",print_writer);
				}
			}
			
			if(teamId == match.getHomeTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$NameGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName1() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$NameGrp$txt_SubHead*GEOM*TEXT SET " + "" + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$ImageGrp$img_Badges*TEXTURE*IMAGE SET " +
						logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$ImageGrp$img_BadgesShadow*TEXTURE*IMAGE SET " +
						logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber"
						+ "*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
				
			}
			else if(teamId == match.getAwayTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$NameGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName1() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$NameGrp$txt_SubHead*GEOM*TEXT SET " + "" + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$ImageGrp$img_Badges*TEXTURE*IMAGE SET " +
						logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$ImageGrp$img_BadgesShadow*TEXTURE*IMAGE SET " +
						logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$TeamStats$SelectStats$AllStats$Select_LineNumber"
						+ "*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
			}
		}
		scorebug.setLast_scorebug_team_stat(scorebug.getScorebug_team_stat());
		return scorebug;
	}
	public ScoreBug populateScoreBugPlayerStats(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,int teamId, String StatType, int playerId , String PhotoType,
			List<Team> team, List<Player> players, List<PlayerStat> playerStats, Match match, String broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int teamApiId = 0;
			String cout = "";
			Player player = null;
			player = players.stream().filter(plyr -> plyr.getPlayerId() == playerId).findAny().orElse(null);
			
//			data = new FootballData();
//			EuroLeague.LiveData(data);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$NameGrp$txt_PlayerName*GEOM*TEXT SET " + 
					player.getTicker_name() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$NameGrp$txt_PlayerNumber*GEOM*TEXT SET " + 
					player.getJersey_number() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$TeamNameGrp$txt_TeamName*GEOM*TEXT SET " + 
					team.get(teamId-1).getTeamName1() + "\0",print_writer);

			if(teamId == match.getHomeTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$NameGrp$txt_Position*GEOM*TEXT SET " + 
					player.getRole().toUpperCase() + "\0",print_writer);
			
			if(PhotoType.equalsIgnoreCase("without_photo")) {
				cout = "WithOutImage";
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$Selec_tImage*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			}
			else {
				cout = "With_Image";
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$Selec_tImage*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$DataGrp$StatsWithImage$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
						+ player.getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$DataGrp$StatsWithImage$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
						+ player.getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			}
			int teamid=0;
			if(player.getTeamId()==match.getHomeTeamId()) {
				teamApiId = Integer.valueOf(match.getHomeTeam().getTeamApiId());
				teamid=0;
			}else if(player.getTeamId()==match.getAwayTeamId()) {
				teamid=1;
				teamApiId = Integer.valueOf(match.getAwayTeam().getTeamApiId());
			}
			if(StatType.equalsIgnoreCase("heatmap")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$HeatMap$GroundAlInl$img_HeatMap"
						+ "*TEXTURE*IMAGE SET "+ image_path + "playerheatmap"+teamid+"_"+player.getJersey_number()+".jpg" + "\0",print_writer);
			}
			else if(StatType.equalsIgnoreCase("stats")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$AllStats*FUNCTION*Grid*num_row SET 3" + "\0",print_writer);

//				com.football.containers.Players plyr = new com.football.containers.Players();
//				
//				for(com.football.containers.Team teams : data.getTeam()) {
//					for(com.football.containers.Players plyer : teams.getTeamPlayer()) {
//						if(player.getPlayerAPIId() != null && plyer.getId() != null && plyer.getId().equalsIgnoreCase(player.getPlayerAPIId())) {
//							plyr = plyer;
//							break;
//						}
//					}
//				}
//				System.out.println(plyr.toString());
				
				com.football.model.Football.Team.Player plyr = new com.football.model.Football.Team.Player();

				for (com.football.model.Football.Team t : IndexController.football.getTeams()) {
				    if (t.getTeamID() == teamApiId) {
				        for (com.football.model.Football.Team.Player p : t.getPlayers()) {
				            if (p.getPlayerID() == Integer.parseInt(player.getPlayerAPIId())) {
				                plyr = p;
				                break;
				            }
				        }
				        break;
				    }
				}
				
				switch(player.getRole()) {
				case "Goalkeeper":
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$$AllStats$1$"
//							+ "txt_StatHead*GEOM*TEXT SET " + "MINUTES PLAYED" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatHead*GEOM*TEXT SET " + "SAVES" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatHead*GEOM*TEXT SET " + "TACKLE" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$4$"
							+ "txt_StatHead*GEOM*TEXT SET " + "TOUCHES" + "\0",print_writer);
					
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getMinsPlayed() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getSaves() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getTackles() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$4$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getTouches() + "\0",print_writer);
					break;
				case "Defender":
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatHead*GEOM*TEXT SET " + "MINUTES PLAYED" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$$AllStats$1$"
							+ "txt_StatHead*GEOM*TEXT SET " + "CROSSES" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatHead*GEOM*TEXT SET " + "INTERCEPTIONS " + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatHead*GEOM*TEXT SET " + "TACKLES" + "\0",print_writer);
					
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getMinsPlayed() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getCrosses() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getInterceptions() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getTackles() + "\0",print_writer);
					break;
				case "MidFielder":
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$$AllStats$1$"
//							+ "txt_StatHead*GEOM*TEXT SET " + "MINUTES PLAYED" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
							+ "txt_StatHead*GEOM*TEXT SET " + "PASSES" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatHead*GEOM*TEXT SET " + "DUEL WON" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatHead*GEOM*TEXT SET " + "TACKLES" + "\0",print_writer);
					
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getMinsPlayed() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getPasses() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getDuelsWon() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getTackles() + "\0",print_writer);
					break;
				case "Forward":
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatHead*GEOM*TEXT SET " + "MINUTES PLAYED" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$$AllStats$1$"
							+ "txt_StatHead*GEOM*TEXT SET " + "GOALS" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatHead*GEOM*TEXT SET " + "SHOT ON TARGET" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatHead*GEOM*TEXT SET " + "ASSIST" + "\0",print_writer);
					
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
//							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getMinsPlayed() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$1$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getGoals() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$2$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getOnTarget() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsWithImage$" + cout + "$SelectStats$PlayerStat$AllStats$3$"
							+ "txt_StatValue*GEOM*TEXT SET " + plyr.getAssist() + "\0",print_writer);
					break;
				}
				
			}
		}
		scorebug.setLast_scorebug_player_stat(scorebug.getScorebug_player_stat());
		return scorebug;
	}
	public ScoreBug populateScoreBugPromo(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			String team_name = "",newDate = "";
			List<String> data_name = new ArrayList<String>();
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$lg_Badge1"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(200);
					team_name = TM.getTeamName4().toUpperCase();
					data_name.add(team_name);
					if(is_this_updating == false) {
						scorebug.setScorebug_name(team_name);
						scorebug.setLast_scorebug_name(team_name);
//						is_this_updating = true;
					}
				}
			}
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$lg_Badge2"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$Match$txt_Match"
							+ "*GEOM*TEXT SET " + team_name + " vs " + TM.getTeamName4().toUpperCase() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(200);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
				}
				
				
			}
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
//					+ "*GEOM*TEXT SET " + "" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
//					+ "*GEOM*TEXT SET " + "HERO CLUB PLAYOFF" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
					+ "*GEOM*TEXT SET " + (fix.get(match_number-1).getMatchnumber() < 10 ? "MATCH " + fix.get(match_number-1).getMatchnumber() :
						fix.get(match_number-1).getMatchfilename()) + "\0",print_writer);
			String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
//						+ "*GEOM*TEXT SET " + "TOMORROW " + fix.get(match_number-1).getTime() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
						+ "*GEOM*TEXT SET " + "TOMORROW " + fix.get(match_number-1).getTime() + "\0",print_writer);
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
//							+ "*GEOM*TEXT SET " + "COMING UP" + " AT " + fix.get(match_number-1).getTime() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
							+ "*GEOM*TEXT SET " + "COMING UP" + " AT " + fix.get(match_number-1).getTime() + "\0",print_writer);
				}else {
					newDate = fix.get(match_number-1).getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
//							+ "*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " " + Month.of(Integer.valueOf(fix.getDate().split("-")[1])) + " AT " + fix.get(match_number-1).getTime() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
							+ "*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " " + Month.of(Integer.valueOf(fix.get(match_number-1).getDate().split("-")[1])) + 
							" AT " + fix.get(match_number-1).getTime() + "\0",print_writer);
				}
				
			}
			
			data_name.clear();
		}
		
		scorebug.setLast_scorebug_promo(scorebug.getScorebug_promo());
		return scorebug;
	}
	public ScoreBug populateScoreBugHeadToHeadStats(List<PrintWriter> print_writer,boolean is_this_updating,ScoreBug scorebug,List<Team> team, List<HeadToHead> headToHeadStats, 
			Match match, String broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			data = new FootballData();
			EuroLeague.LiveData(data);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber"
					+ "*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
			
			switch(scorebug.getScorebug_headTohead_stat()) {
			case "HEADTOHEAD":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
						+ "txt_StatHead*GEOM*TEXT SET " + "POSSESSION(%)" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
						+ "txt_StatHead*GEOM*TEXT SET " + "TOTAL PASSES" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
						+ "txt_StatHead*GEOM*TEXT SET " + "TOUCHES" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
						+ "txt_StatHead*GEOM*TEXT SET " + "SUCCESSFUL DRIBBLES" + "\0",print_writer);
				
				for(com.football.containers.Team teams : data.getTeam()) {
					if(teams.getID().equalsIgnoreCase(match.getHomeTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue1*GEOM*TEXT SET " + Math.round(teams.getPossession()) + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTotalPass() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTouches() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getDribbles() + "\0",print_writer);
					}
					else if(teams.getID().equalsIgnoreCase(match.getAwayTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue2*GEOM*TEXT SET " + Math.round(teams.getPossession()) + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTotalPass() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTouches() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getDribbles() + "\0",print_writer);
					}
				}
				break;
			case "BOTH_TEAMS":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
						+ "txt_StatHead*GEOM*TEXT SET " + "POSSESSION(%)" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
						+ "txt_StatHead*GEOM*TEXT SET " + "YELLOW CARDS" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
						+ "txt_StatHead*GEOM*TEXT SET " + "RED CARDS" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
						+ "txt_StatHead*GEOM*TEXT SET " + "OFFSIDES" + "\0",print_writer);
				for(com.football.containers.Team teams : data.getTeam()) {
					if(teams.getID().equalsIgnoreCase(match.getHomeTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue1*GEOM*TEXT SET " + Math.round(teams.getPossession()) + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTotalYellowCard() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTotalRedCard() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTotalOffside() + "\0",print_writer);
					}
					else if(teams.getID().equalsIgnoreCase(match.getAwayTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue2*GEOM*TEXT SET " + Math.round(teams.getPossession()) + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTotalYellowCard() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTotalRedCard() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTotalOffside() + "\0",print_writer);
					}
				}
				break;
			case "TEAM_COMPARISON":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
						+ "txt_StatHead*GEOM*TEXT SET " + "TOUCHES" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
						+ "txt_StatHead*GEOM*TEXT SET " + "TOTAL PASSES" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
						+ "txt_StatHead*GEOM*TEXT SET " + "SUCCESSFUL DRIBBLES" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
						+ "txt_StatHead*GEOM*TEXT SET " + "DUELS WON" + "\0",print_writer);
				
				for(com.football.containers.Team teams : data.getTeam()) {
					if(teams.getID().equalsIgnoreCase(match.getHomeTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTouches() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getTotalPass() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getDribbles() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue1*GEOM*TEXT SET " + teams.getDuelWon() + "\0",print_writer);
					}
					else if(teams.getID().equalsIgnoreCase(match.getAwayTeam().getTeamApiId())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$1$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTouches() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$2$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getTotalPass() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$3$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getDribbles() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$HeadToHead$SelectStats$AllStats$Select_LineNumber$4$"
								+ "txt_StatValue2*GEOM*TEXT SET " + teams.getDuelWon() + "\0",print_writer);
					}
				}
				break;
			}						
		}
		scorebug.setLast_scorebug_headTohead_stat(scorebug.getScorebug_headTohead_stat());
		return scorebug;
	}
	
	public void populateFFTeams(List<PrintWriter> print_writer,String viz_scene, List<Team> team, List<VariousText> VariousText, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id_1=0,row_id_2=0,row_id_3=0,row_id_4=0;
			
			for(VariousText vt : VariousText) {
				if(vt.getVariousType().equalsIgnoreCase("FFTEAMSFOOTER") && vt.getUseThis().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
							vt.getVariousText().toUpperCase() + "\0",print_writer);
					break;
				}else if(vt.getVariousType().equalsIgnoreCase("FFTEAMSFOOTER") && vt.getUseThis().equalsIgnoreCase(FootballUtil.NO)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
							"GROUP WINNER WILL QUALIFY FOR THE SEMI-FINALS" + "\0",print_writer);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$txt_Header*GEOM*TEXT SET " + "GROUPS" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
			
			for(int i=0;i<=team.size()-1;i++) {
				if(team.get(i).getTeamGroup().equalsIgnoreCase("GROUP A")) {
					row_id_1 = row_id_1 + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group1$TeamData" + row_id_1 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0",print_writer);
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("GROUP B")) {
					row_id_2 = row_id_2 + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group2$TeamData" + row_id_2 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0",print_writer);
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("GROUP C")) {
					row_id_3 = row_id_3 + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group3$TeamData" + row_id_3 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0",print_writer);
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("GROUP D")) {
					row_id_4 = row_id_4 + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group4$TeamData" + row_id_4 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0",print_writer);
				}
			}
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Groups_In 2.580 \0",print_writer);
		}
	}
	public void populateMatchId(List<PrintWriter> print_writer,String viz_scene, Match match, String session_selected_broadcaster,List<VariousText> vt) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			//FootballFunctions.getFootballLiveDatafromAPI(FootballFunctions.getAccessToken());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " +"GROUP C"+ "\0",print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			//Tournament LOGO OFF 
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag*ACTIVE SET 1\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$img_TLogo*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$SelectSeparator$txt_Score*GEOM*TEXT SET " + "VS" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " + match.getMatchIdent() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$Header$txt_Header*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			for(VariousText vartext : vt) {
				if(vartext.getVariousType().equalsIgnoreCase("MatchIdentFooter") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + vartext.getVariousText() + "\0",print_writer);
					
				}else if(vartext.getVariousType().equalsIgnoreCase("MatchIdentFooter") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET LIVE FROM " + match.getVenueName().toUpperCase() + "\0",print_writer);
					
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.600 \0",print_writer);
		}
	}
	public void populateFFMatchScore(List<PrintWriter> print_writer,String viz_scene, Match match, String session_selected_broadcaster,List<VariousText> vt) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			//FootballFunctions.getFootballLiveDatafromAPI(FootballFunctions.getAccessToken());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			//Tournament LOGO OFF 
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag*ACTIVE SET 1\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$img_TLogo*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$SelectSeparator$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore()
					+ " - " + match.getAwayTeamScore() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " + match.getMatchIdent() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$Header$txt_Header*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET LIVE FROM " + match.getVenueName().toUpperCase() + "\0",print_writer);
			
			for(VariousText vartext : vt) {
				if(vartext.getVariousType().equalsIgnoreCase("FFSCORELINEFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + vartext.getVariousText() + "\0",print_writer);
					
				}else if(vartext.getVariousType().equalsIgnoreCase("FFSCORELINEFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + match.getVenueName().toUpperCase() + "\0",print_writer);
					
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.600 \0",print_writer);
		}
	}
	public void populateMatchPromoSingle(List<PrintWriter> print_writer,String viz_scene, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster, FootballService footballService) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			String newDate = "";
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			//Tournament LOGO OFF 
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$img_TLogo*ACTIVE SET 0 \0");
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$img_TLogo*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);

			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$Header$txt_Header*GEOM*TEXT SET " + "FIFA WORLD CUP 26 AFC QUALIFIERS - ROUND 2" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$Header$txt_Header*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$SelectSeparator$txt_Score*GEOM*TEXT SET " + "VS" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " + (fix.get(match_number - 1).getMatchnumber() < 8 
					? "MATCH " + fix.get(match_number - 1).getMatchnumber() : fix.get(match_number - 1).getMatchfilename()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
			VariousText vText = footballService.getVariousTexts().stream().filter(vs -> vs.getVariousType().equalsIgnoreCase("FF_PROMO_HEADER") && vs.getUseThis().equalsIgnoreCase(FootballUtil.YES)).findAny().orElse(null);
			if(vText!= null) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$Header$txt_Header*GEOM*TEXT SET " + vText.getVariousText() + "\0",print_writer);
			}
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {

					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp3$img_BadgesOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp4$img_Badges"
//							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
							TM.getTeamName2().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp3$img_BadgesOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp4$img_Badges"
//							+ "*TEXTURE*IMAGE SET "+ logo_path +TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);	
				}
			}
			String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + "TOMORROW " + 
						(fix.get(match_number-1).getTime()== null ? "" :"AT "+fix.get(match_number-1).getTime()+" LOCAL TIME ") 
						+ "(" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0",print_writer);
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + "COMING UP " + 
							(fix.get(match_number-1).getTime()== null ? "" :"AT "+fix.get(match_number-1).getTime()+" LOCAL TIME ")  
							+ "(" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0",print_writer);
				}else {
					newDate = fix.get(match_number-1).getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + newDate + 
							dateSuffix[Integer.valueOf(newDate)] + " " + Month.of(Integer.valueOf(fix.get(match_number-1).getDate().split("-")[1])) + 
							(fix.get(match_number-1).getTime()== null ? "" :" AT "+fix.get(match_number-1).getTime()+" LOCAL TIME ") 
							+ "(" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0",print_writer);
				}
			}
			
			VariousText VT_Footer = footballService.getVariousTexts().stream().filter(vs -> vs.getVariousType().equalsIgnoreCase("FF_MATCHPROMO_FOOTER") && 
					vs.getUseThis().equalsIgnoreCase(FootballUtil.YES)).findAny().orElse(null);
			if(VT_Footer != null) {
				if(VT_Footer.getVariousText() != null && !VT_Footer.getVariousText().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " 
							+ VT_Footer.getVariousText() + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET \0",print_writer);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.100 \0",print_writer);	
		}
	}
	public void populateMatchStatus(List<PrintWriter> print_writer,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			data = new FootballData();
			EuroLeague.LiveData(data);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp3$img_BadgesOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp4$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp3$img_BadgesOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp4$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SponsorGrpAll$SelectSponsorType$SponsorAll$SponsorBase"
					+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_Header*GEOM*TEXT SET " + 
					match.getTournament() + "\0",print_writer);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1") || match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "EXTRA TIME" + "\0",print_writer);
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_HomeTeamScore*GEOM*TEXT SET " + (match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "Separator*GEOM*TEXT SET " + "-" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_AwayTeamScore*GEOM*TEXT SET " + (match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_HomeTeamName*GEOM*TEXT SET " 
					+ match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_AwayTeamName*GEOM*TEXT SET " 
					+ match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "POSSESSION (%)" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "SHOTS" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "SHOTS ON TARGET" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "YELLOW CARDS" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "RED CARDS" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "CORNERS" + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
//					+ "txt_StatHead*GEOM*TEXT SET " + "OFFSIDES" + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + Math.round(data.getTeam().get(0).getPossession()) + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getShots() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getShotOnTarget() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getTotalYellowCard() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getTotalRedCard() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getCornerTaken() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
//					+ "txt_HomeStatValue*GEOM*TEXT SET " + data.getTeam().get(0).getTotalOffside() + "\0",print_writer);
//			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + Math.round(data.getTeam().get(1).getPossession()) + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getShots() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getShotOnTarget() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getTotalYellowCard() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getTotalRedCard() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getCornerTaken() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
//					+ "txt_AwayStatValue*GEOM*TEXT SET " + data.getTeam().get(1).getTotalOffside() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll*FUNCTION*Grid*num_row SET 7\0",print_writer);

			String text_to_return = "";
			ArrayList<String> Stats = new ArrayList<String>();
			try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY + "Stats.txt"))) {
				while((text_to_return = br.readLine()) != null) {
				    Stats.add(text_to_return);
				}
			}
		
		    for(int i=0;i<=Stats.size()-1;i++) {
		    	//System.out.println("VALUE : " + Stats.get(i));
		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$"
		    			+ "StatAllGrp$StatDataAll$txt_HomeStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[0] + "\0",print_writer);
		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$"
		    			+ "StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + Stats.get(i).split(",")[1].replace("_", " ") + "\0",print_writer);
		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$"
		    			+ "StatAllGrp$StatDataAll$txt_AwayStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[2] + "\0",print_writer);
		    }
			
		    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 MatchStats_In 2.200 \0",print_writer);
		}
	}
	public void populateTournamentStats(List<PrintWriter> print_writer,String viz_scene,FootballService footballService,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			List<String> this_data_str = new ArrayList<String>();
			String  WhichStyle = "";
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp3$img_BadgesOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp4$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp3$img_BadgesOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp4$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SponsorGrpAll$SelectSponsorType$SponsorAll$SponsorBase"
					+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_Header*GEOM*TEXT SET " + 
					match.getTournament() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);
						
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_HomeTeamScore*GEOM*TEXT SET " + match.getHomeTeamScore() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "Separator*GEOM*TEXT SET " + "-" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_AwayTeamScore*GEOM*TEXT SET " + match.getAwayTeamScore() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_HomeTeamName*GEOM*TEXT SET " + 
					match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_AwayTeamName*GEOM*TEXT SET " + 
					match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1") || match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "EXTRA TIME" + "\0",print_writer);
			}
			
//			String text_to_return = "";
//			ArrayList<String> TournamentStats = new ArrayList<String>();
//			try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY + "TournamentStats.txt"))) {
//				while((text_to_return = br.readLine()) != null) {
//					TournamentStats.add(text_to_return);
//				}
//			}
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll*FUNCTION*Grid*num_row SET "+TournamentStats.size()+ "\0",print_writer);
//		    for(int i=0;i<=TournamentStats.size()-1;i++) {
//		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$StatAllGrp$StatDataAll"
//		    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + TournamentStats.get(i).split(",")[0] + "\0",print_writer);
//		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$StatAllGrp$StatDataAll"
//		    			+ "$txt_StatHead*GEOM*TEXT SET " + TournamentStats.get(i).split(",")[1] + "\0",print_writer);
//		    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$StatAllGrp$StatDataAll"
//		    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + TournamentStats.get(i).split(",")[2] + "\0",print_writer);
//		    }
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll*FUNCTION*Grid*num_row SET 7\0",print_writer);
			
			this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Possession");
			WhichStyle = String.join(",", "Possession").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Shots");
			WhichStyle = String.join(",", "Shots").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Shots_on_Target");
			WhichStyle = String.join(",", "Shots_on_Target").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Yellow_Cards");
			WhichStyle = String.join(",", "Yellow_Cards").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Red_Cards");
			WhichStyle = String.join(",", "Red_Cards").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Corners");
			WhichStyle = String.join(",", "Corners").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
	    	
	    	this_data_str = FootballFunctions.MatchStatsSingle(IndexController.football, "Offside");

	    	WhichStyle = String.join(",", "Offside").replace("_", " ");
			WhichStyle = FootballFunctions.ChangedHeader(footballService.getHeaderText(),WhichStyle.toUpperCase());
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[0] + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_StatHead*GEOM*TEXT SET " + WhichStyle.toUpperCase() + "\0",print_writer);
	    	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll"
	    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + this_data_str.get(0).split(",")[2] + "\0",print_writer);
	    	
			
		    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 MatchStats_In 2.200 \0",print_writer);
		}
	}
	public void populateTeamComparison(List<PrintWriter> print_writer,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
//			data = new FootballData();
//			EuroLeague.LiveData(data);
			
			com.football.model.Football.Team team1 = new com.football.model.Football.Team();
			com.football.model.Football.Team team2 = new com.football.model.Football.Team();
			
			team1 = IndexController.football.getTeams().get(0);
			team2 = IndexController.football.getTeams().get(1);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SponsorGrpAll$SelectSponsorType$SponsorAll$SponsorBase"
					+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_Header*GEOM*TEXT SET " + 
					match.getTournament() + "\0",print_writer);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1") || match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "EXTRA TIME" + "\0",print_writer);
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_HomeTeamScore*GEOM*TEXT SET " + (match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "Separator*GEOM*TEXT SET " + "-" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$"
					+ "txt_AwayTeamScore*GEOM*TEXT SET " +(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_HomeTeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_AwayTeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SelectSponsorType*FUNCTION*Omo*vis_con SET "+1+"\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "CROSSES" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "TOUCHES" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "PASSES" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "TACKLES" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "INTERCEPTIONS" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "DUELS" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
					+ "txt_StatHead*GEOM*TEXT SET " + "DUELS WON" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getCrosses() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getTouches() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getPasses() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getTackles() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getInterceptions() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getDuels() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
					+ "txt_HomeStatValue*GEOM*TEXT SET " + team1.getDuelsWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getCorners() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getTouches() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getPasses() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getTackles() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getInterceptions() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getDuels() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$"
					+ "txt_AwayStatValue*GEOM*TEXT SET " + team2.getDuelsWon() + "\0",print_writer);
			
		    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 MatchStats_In 2.200 \0",print_writer);
		}
	}
	public void populateMatchStats(List<PrintWriter> print_writer,String viz_scene,FootballService footballService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			int l = 4;
			//String Home_player="",Away_player="";
			String h1="",h2="",h3="",a1="",a2="",a3="";
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$txt_Time*GEOM*TEXT SET " + 
					" " + "\0",print_writer);
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + match.getMatchIdent().toUpperCase() + " - " + match.getTournament() + "\0");
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + 
					(match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + " - " +(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$HomeLogoGrp$Ani_2$img_Logo" + "*TEXTURE*IMAGE SET "+ logo_path + 
//					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
//					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$SubHeader$txt_Info*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0",print_writer);
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
			
			if(match.getHomeTeamScore() == 0 ) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
			}else if(home_stats.size() > 0 && home_stats.size() <= 2) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET 1 \0",print_writer);

			}else if(home_stats.size() > 2 && home_stats.size() <= 4) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET 2 \0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET 3 \0",print_writer);
			}
			
			if(match.getAwayTeamScore() == 0 ) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);

			}else if(away_stats.size() > 0 && away_stats.size() <= 2) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET 1 \0",print_writer);

			}else if(away_stats.size() > 2 && away_stats.size() <= 4) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET 2 \0",print_writer);

			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET 3 \0",print_writer);

			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Home$First$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Home$Second$txt_Scorer2*GEOM*TEXT SET " + h2 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Home$Third$txt_Scorer3*GEOM*TEXT SET " + h3 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Away$First$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Away$Second$txt_Scorer2*GEOM*TEXT SET " + a2 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ScoreLine$Header$Dataall$Away$Third$txt_Scorer3*GEOM*TEXT SET " + a3 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 3.120 \0",print_writer);
		}
	}
	public void populateFF_H2H(List<PrintWriter> print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			//data = new FootballData();
			//EuroLeague.WinProbability(data);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$Header$txt_Header*GEOM*TEXT SET " + "HEAD TO HEAD" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ flag_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ flag_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
//			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ flag_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ flag_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			//logoGrp closed 
			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$SelectSeparator$LogoGrp*ACTIVE SET 0 \0");
			
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$SelectSeparator$txt_Score$txt_WinValue1*GEOM*TEXT SET " + 
//					data.getHomeContestantWins() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$SelectSeparator$txt_Score$txt_WinValue2*GEOM*TEXT SET " + 
//					data.getAwayContestantWins() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$DrawGrp$TextGrp$txt_DrawValue*GEOM*TEXT SET " + data.getDraws() + "\0");
			
			String text_to_return = "";
			int lineIndex1 = 1;
		    boolean found1 = false;
			BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY + "H2H.txt"));
		
		    while( (text_to_return = br.readLine()) != null) {
		        if(lineIndex1 == 1) {
		        	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$SelectSeparator$txt_Score$txt_WinValue1*GEOM*TEXT SET " + 
		        			text_to_return.split(" ")[0] + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$SelectSeparator$txt_Score$txt_WinValue2*GEOM*TEXT SET " + 
							text_to_return.split(" ")[1] + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$DrawGrp$TextGrp$txt_DrawValue*GEOM*TEXT SET " 
							+ text_to_return.split(" ")[2] + "\0",print_writer);

		            found1 = true;
		            break;
		        }
		        lineIndex1++;
		    }
		    if(!found1) {
		    	//System.out.println("Line Not There");
		    }
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + 
					match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + 
					match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$HeadToHead$All$InfoGrp$txt_Info*GEOM*TEXT SET " + "" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 HeadToHead_In 2.500 \0",print_writer);
		}
	}
	
	public void populateBugHighlight(List<PrintWriter> print_writer,String viz_scene, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$EventLogoOut*ACTIVE SET 0 \0",print_writer);

			//FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + match.getMatchIdent().toUpperCase() + " - " + match.getTournament() + "\0");
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$Seperator$txt_HomeScore*GEOM*TEXT SET " + (match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$Seperator$txt_AwayScore*GEOM*TEXT SET " +(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$TeamGrp1$txt_Name*GEOM*TEXT SET " + match.getHomeTeam().getTeamName4().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$TeamGrp2$txt_Name*GEOM*TEXT SET " + match.getAwayTeam().getTeamName4().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0",print_writer);
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$txt_Clock*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0",print_writer);
			}			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.500 \0",print_writer);
		}
	}
	public void populateBug(List<PrintWriter> print_writer,String viz_scene,Bugs bug, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			if(bug.getText1() != null && bug.getText2() != null) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$Base$SelectLineNumber*FUNCTION*Omo*vis_con SET 1 \0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber*FUNCTION*Omo*vis_con SET 1 \0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_BottomInfo*GEOM*TEXT SET " + bug.getText1() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_Text*GEOM*TEXT SET " + bug.getText2() + "\0",print_writer);
			}else if(bug.getText1() != null && bug.getText2() == null) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$Base$SelectLineNumber*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_BottomInfo*GEOM*TEXT SET " + bug.getText1() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_Text*GEOM*TEXT SET " + " " + "\0",print_writer);
			}else {
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$Base$SelectLineNumber*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_BottomInfo*GEOM*TEXT SET " + bug.getText2() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Bug$SelectLineNumber$txt_Text*GEOM*TEXT SET " + " " + "\0",print_writer);
			}		
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.500 \0",print_writer);
		}
	}
	
	public void populateAvgFormation(List<PrintWriter> print_writer,String viz_scene, int TeamId, List<Formation> formation, List<Team> team ,List<VariousText> var,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0,form_row_id=0;
			String TeamApiId = "";
			
			PassMatrix avg_form  = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\PassMatrix.json"), PassMatrix.class);
			ArrayList<String> live_data = new ArrayList<String>();
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Basic*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective*ACTIVE SET 1\0",print_writer);
			
			if(TeamId == match.getHomeTeamId()) {
				TeamApiId = match.getHomeTeam().getTeamApiId();
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName3() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
						formation.get(match.getHomeTeamFormationId()-1).getFormDescription() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NumberGrp$"
							+ "txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$"
							+ "Name_IconsAll$txt_Name*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective$TacticallNumber$Player" 
							+ row_id + "$In$Radikal-Bold*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectStatus*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				}
			}
			else if(TeamId == match.getAwayTeamId()) {
				row_id = 0;
				form_row_id=0;
				TeamApiId = match.getAwayTeam().getTeamApiId();
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName3() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
						formation.get(match.getAwayTeamFormationId()-1).getFormDescription() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NumberGrp$txt_Number*GEOM*TEXT SET " 
							+ as.getJersey_number() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$txt_Name*GEOM*TEXT SET " 
							+ as.getTicker_name().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective$TacticallNumber$Player" + row_id + 
							"$In$Radikal-Bold*GEOM*TEXT SET " + as.getJersey_number() + "\0",print_writer);
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectStatus*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				}
			}
			
			for(LineUp af : avg_form.getLiveData().getLineUp()) {
				if(af.getContestantId().equalsIgnoreCase(TeamApiId)) {
					for(Players plyr : af.getPlayer()) {
						if(plyr.getPosition() != null && !plyr.getPosition().equalsIgnoreCase("Substitute"))
						live_data.add(plyr.getX() + "-" + plyr.getY());
					}
				}
			}
			
			for(int i=0;i<=live_data.size()-1;i++) {
				form_row_id = form_row_id + 1;
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective$TacticallNumber$"
						+ "Player" + form_row_id + "*TRANSFORMATION*POSITION*X SET " + live_data.get(i).split("-")[0] + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective$TacticallNumber$"
						+ "Player" + form_row_id + "*TRANSFORMATION*POSITION*Y SET " + live_data.get(i).split("-")[1] + "\0",print_writer);
			}
		}
	}
	
	public void populateLofLineUp(List<PrintWriter> print_writer,String viz_scene, int TeamId, List<Formation> formation, List<Team> team ,List<VariousText> var,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0;
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Basic*ACTIVE SET 1\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$Effective*ACTIVE SET 0\0",print_writer);
			
			if(TeamId == match.getHomeTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
						formation.get(match.getHomeTeamFormationId()-1).getFormDescription() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NumberGrp$"
							+ "txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$"
							+ "Name_IconsAll$txt_Name*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player" 
							+ row_id + "$In$Radikal-Bold*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectStatus*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getHomeTeamFormationId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0",print_writer);
							
						}
					}
				}
			}
			else if(TeamId == match.getAwayTeamId()) {
				row_id = 0;
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
						formation.get(match.getAwayTeamFormationId()-1).getFormDescription() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NumberGrp$txt_Number*GEOM*TEXT SET " 
							+ as.getJersey_number() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$txt_Name*GEOM*TEXT SET " 
							+ as.getTicker_name().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player" + row_id + 
							"$In$Radikal-Bold*GEOM*TEXT SET " + as.getJersey_number() + "\0",print_writer);
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}
					else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
								+ "SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$LineUp$Row" + row_id + "$In$NameGrp$Name_IconsAll$"
							+ "SelectStatus*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					for(Formation form : formation) {
						if(form.getFormId() == match.getAwayTeamFormationId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$TextAll$Side" + which_side + "$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0",print_writer);
							
						}
					}
				}
			}
		}
	}
	public String populateLofVerticalFlipper(List<PrintWriter> print_writer,String viz_scene, int which_side, int Teamid, List<Formation> formations, List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0;
			
			if(TeamId == match.getHomeTeamId()) {
				
				if(which_side == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
							match.getHomeTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
							formations.get(match.getHomeTeamFormationId()-1).getFormDescription() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
							colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					formation = formations.get(match.getHomeTeamFormationId()-1).getFormDescription();
				}
				
				if(count > 0) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$"
							+ "Select_PlayerNumber*FUNCTION*Omo*vis_con SET " + formation.split("-")[count-1] + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$"
							+ "Select_PlayerNumber*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				}
				
				if(count == 0) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"GOAL-KEEPER" + "\0",print_writer);
					row_id = row_id + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(0).getTicker_name() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(0).getJersey_number() + "\0",print_writer);
					
					if(match.getHomeSquad().get(0).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
							match.getHomeSquad().get(0).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
								+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
								+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					for(Event event : match.getEvents()) {
						if(event.getOnPlayerId() == match.getHomeSquad().get(0).getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							break;
						}
					}
					
				}
				else if(count == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"DEFENDERS" + "\0",print_writer);
					for(int i = 1; i<= match.getHomeSquad().size(); i++) {
						if(i <= Integer.valueOf(formation.split("-")[count-1])) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getHomeSquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 2) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"MIDFIELDERS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getHomeSquad().size(); i++) {
						if(i > Integer.valueOf(formation.split("-")[0]) && i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getHomeSquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 3) {
					if(formation.split("-").length == 3) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
								"FORWARDS" + "\0",print_writer);
					}else if(formation.split("-").length == 4) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
								"MIDFIELDERS" + "\0",print_writer);
					}
					
					for(int i = 1; i<= match.getHomeSquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getHomeSquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 4) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"FORWARDS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getHomeSquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+Integer.valueOf(formation.split("-")[2])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
										Integer.valueOf(formation.split("-")[3]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getHomeSquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 5) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"FORWARDS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getHomeSquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
								Integer.valueOf(formation.split("-")[3])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
										Integer.valueOf(formation.split("-")[3])+Integer.valueOf(formation.split("-")[4]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getHomeSquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getHomeSquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getHomeSquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getHomeSquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}
			}
			else if(TeamId == match.getAwayTeamId()) {
				if(which_side == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$txt_TeamName*GEOM*TEXT SET " + 
							match.getAwayTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$txt_Formation*GEOM*TEXT SET " + 
							formations.get(match.getAwayTeamFormationId()-1).getFormDescription() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$HeaderGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
							colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					formation = formations.get(match.getAwayTeamFormationId()-1).getFormDescription();
				}
				
				if(count > 0) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$"
							+ "Select_PlayerNumber*FUNCTION*Omo*vis_con SET " + formation.split("-")[count-1] + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$"
							+ "Select_PlayerNumber*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				}
				
				if(count == 0) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"GOAL-KEEPER" + "\0",print_writer);
					row_id = row_id + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(0).getTicker_name() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(0).getJersey_number() + "\0",print_writer);
					
					if(match.getAwaySquad().get(0).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
							match.getAwaySquad().get(0).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
								+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
								+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
							+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					for(Event event : match.getEvents()) {
						if(event.getOnPlayerId() == match.getAwaySquad().get(0).getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							break;
						}
					}
				}
				else if(count == 1) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"DEFENDERS" + "\0",print_writer);
					for(int i = 1; i<= match.getAwaySquad().size(); i++) {
						if(i <= Integer.valueOf(formation.split("-")[count-1])) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getAwaySquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 2) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"MIDFIELDERS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getAwaySquad().size(); i++) {
						if(i > Integer.valueOf(formation.split("-")[0]) && i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getAwaySquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 3) {
					if(formation.split("-").length == 3) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
								"FORWARDS" + "\0",print_writer);
					}else if(formation.split("-").length == 4) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
								"MIDFIELDERS" + "\0",print_writer);
					}
					
					for(int i = 1; i<= match.getAwaySquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getAwaySquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 4) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"FORWARDS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getAwaySquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+Integer.valueOf(formation.split("-")[2])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
										Integer.valueOf(formation.split("-")[3]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getAwaySquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}else if(count == 5) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$txt_PositionHead*GEOM*TEXT SET " + 
							"FORWARDS" + "\0",print_writer);
					
					for(int i = 1; i<= match.getAwaySquad().size(); i++) {
						if(i > (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
								Integer.valueOf(formation.split("-")[3])) && 
								i <= (Integer.valueOf(formation.split("-")[0])+Integer.valueOf(formation.split("-")[1])+ Integer.valueOf(formation.split("-")[2])+
										Integer.valueOf(formation.split("-")[3])+Integer.valueOf(formation.split("-")[4]))) {
							row_id = row_id + 1;
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Name*GEOM*TEXT SET " + match.getAwaySquad().get(i).getTicker_name() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$txt_Number*GEOM*TEXT SET " + match.getAwaySquad().get(i).getJersey_number() + "\0",print_writer);
							
							if(match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN) || 
									match.getAwaySquad().get(i).getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
							}else {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
										+ row_id + "$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							}
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
									+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							for(Event event : match.getEvents()) {
								if(event.getOnPlayerId() == match.getAwaySquad().get(i).getPlayerId()) {
									FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$PlayerGrpAll$Select_PlayerNumber$"
											+ row_id + "$Select_Sub*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
									break;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	public void populateLofLeaderBoard(List<PrintWriter> print_writer,String viz_scene, LeaderBoard leaderboard, List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			String cont = "";
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
					leaderboard.getHeader() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + 
					(leaderboard.getSubHeader()==null?"":leaderboard.getSubHeader()) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$Out$Select_EventLogo*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);

			
			for(int i=0;i<2;i++) {
				if(i==0) {
					cont = "Dehighlight";
				}else {
					cont = "HIghtlight";
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row1$" + cont + "$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer1().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row1$" + cont + "$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer1().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row2$" + cont + "$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer2().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row2$" + cont + "$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer2().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row3$" + cont + "$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer3().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row3$" + cont + "$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer3().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row4$" + cont + "$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer4().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row4$" + cont + "$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer4().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row5$" + cont + "$ImageGrp$img_Player*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer5().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row5$" + cont + "$ImageGrp$img_PlayerShadow*TEXTURE*IMAGE SET " + photos_path 
							+ leaderboard.getPlayer5().getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row1$" + cont + "$StatGrp$txt_Name*GEOM*TEXT SET " + 
						leaderboard.getPlayer1().getTicker_name() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row1$" + cont + "$StatGrp$txt_TeamName*GEOM*TEXT SET " + 
						team.get(leaderboard.getPlayer1().getTeamId() - 1).getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row1$" + cont + "$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
						leaderboard.getPlayerStats1() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row2$" + cont + "$StatGrp$txt_Name*GEOM*TEXT SET " + 
						leaderboard.getPlayer2().getTicker_name() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row2$" + cont + "$StatGrp$txt_TeamName*GEOM*TEXT SET " + 
						team.get(leaderboard.getPlayer2().getTeamId() - 1).getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row2$" + cont + "$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
						leaderboard.getPlayerStats2() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row3$" + cont + "$StatGrp$txt_Name*GEOM*TEXT SET " + 
						leaderboard.getPlayer3().getTicker_name() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row3$" + cont + "$StatGrp$txt_TeamName*GEOM*TEXT SET " + 
						team.get(leaderboard.getPlayer3().getTeamId() - 1).getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row3$" + cont + "$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
						leaderboard.getPlayerStats3() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row4$" + cont + "$StatGrp$txt_Name*GEOM*TEXT SET " + 
						leaderboard.getPlayer4().getTicker_name() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row4$" + cont + "$StatGrp$txt_TeamName*GEOM*TEXT SET " + 
						team.get(leaderboard.getPlayer4().getTeamId() - 1).getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row4$" + cont + "$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
						leaderboard.getPlayerStats4() + "\0",print_writer);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row5$" + cont + "$StatGrp$txt_Name*GEOM*TEXT SET " + 
						leaderboard.getPlayer5().getTicker_name() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row5$" + cont + "$StatGrp$txt_TeamName*GEOM*TEXT SET " + 
						team.get(leaderboard.getPlayer5().getTeamId() - 1).getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row5$" + cont + "$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
						leaderboard.getPlayerStats5() + "\0",print_writer);
			}
		}
	}
	
	public void populateFF_FixtureAndResult(List<PrintWriter> print_writer,String viz_scene, String Header, String Group, List<Fixture> fixtures,List<Playoff> playoffs, Match match, String session_selected_broadcaster) {
		System.out.println(Header);
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int row_id =0, omo=0;
			String cout = "",home_data="",away_data="";
			List<Fixture> fix = new ArrayList<Fixture>();
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$ResultsAll$LogoGrp$LogoImageGrp1$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$ResultsAll$LogoGrp$LogoImageGrp2$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$txt_Header*GEOM*TEXT SET " + Group.toUpperCase() + " - " + 
					(Header.contains("FIXTURES_RESULTS") ? "FIXTURES & RESULTS" : Header) + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament()+ "\0",print_writer);
			
			
//			for(Fixture data : fixtures) {
//				if(data.getHome_Team().getTeamName1().equalsIgnoreCase(Header.split("_")[0].trim()) || 
//						data.getAway_Team().getTeamName1().equalsIgnoreCase(Header.split("_")[0].trim())) {
//					fix.add(data);
//				}
//			}
			
			fix = fixtures.stream().filter(fixs -> fixs.getGroupName().contains(Group.toUpperCase())).collect(Collectors.toList());

			for(int i=0;i <= fix.size()-1;i++) {	
//				if(i>6 && i<=10) {
					row_id = row_id + 1;
					
//					for(Playoff playoff : playoffs) {
//		    			if(playoff.getPlayoffId() == (fixtures.get(i).getMatchnumber()+1)) {
//		    				home_data = playoff.getTeam1();
//		    				away_data = playoff.getTeam2();
//		    				break;
//		    			}
//		    		}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber*FUNCTION*Omo*vis_con SET " + row_id + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$DateGrp$txt_Date"
							+ "*GEOM*TEXT SET " + (LocalDate.parse(fix.get(i).getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).isEqual(LocalDate.now()) ? "Tonight".toUpperCase() :
				                LocalDate.parse(fix.get(i).getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).isEqual(LocalDate.now().plusDays(1)) ? "Tomorrow".toUpperCase() :
					                LocalDate.parse(fix.get(i).getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).isEqual(LocalDate.now().plusDays(2)) ? fix.get(i).getDate().split("-")[0] + 
					                " " + Month.of(Integer.valueOf(fix.get(i).getDate().split("-")[1])).name() + " " + fix.get(i).getDate().split("-")[2] : fix.get(i).getDate().split("-")[0] + " " + 
					                Month.of(Integer.valueOf(fix.get(i).getDate().split("-")[1])).name() + " " + fix.get(i).getDate().split("-")[2]) + "\0",print_writer);
					
					if (match.getMatchFileName().toUpperCase().replace(".JSON", "").trim().replace(" 0", " ")
					        .equalsIgnoreCase(fix.get(i).getMatchfilename().replace(" 0", " ").toUpperCase().trim())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
					
					if(fix.get(i).getHome_Team().getTeamName1().equalsIgnoreCase("QF")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_TeamName1*GEOM*TEXT SET " + home_data + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_TeamName1*GEOM*TEXT SET " + fix.get(i).getHome_Team().getTeamName1() + "\0",print_writer);
					}
					
					if(fix.get(i).getAway_Team().getTeamName1().equalsIgnoreCase("QF")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_TeamName2*GEOM*TEXT SET " + away_data + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_TeamName2*GEOM*TEXT SET " + fix.get(i).getAway_Team().getTeamName1() + "\0",print_writer);
					}
					
					if(fix.get(i).getMargin() == null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_Separator*GEOM*TEXT SET  vs \0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer1*GEOM*TEXT SET \0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer2*GEOM*TEXT SET \0",print_writer);
						
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$SelectMatchType" 
								+ cout + "$Team1$txt_Separator*GEOM*TEXT SET " + fix.get(i).getMargin() + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer1*GEOM*TEXT SET \0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer2*GEOM*TEXT SET \0",print_writer);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer1" 
								+ "$*GEOM*TEXT SET " + (fix.get(i).getHomeScorer() != null ? fix.get(i).getHomeScorer() : "") + "\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$Select_RowNumber$Row" + row_id + "$txt_Scorer2" 
								+ "$*GEOM*TEXT SET " + (fix.get(i).getAwayScorer() != null ? fix.get(i).getAwayScorer() : "") + "\0",print_writer);
					}
//				}
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FF_Required$HashTag*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$ResultsAll$AllData$FixtureData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + "" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Results_In 2.500 \0",print_writer);
		}
	}
	
	public void populatePlayingXI(List<PrintWriter> print_writer,String viz_scene, int TeamId,String Type,List<Formation> formation, List<Team> team ,List<VariousText> var,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0,row_id_sub = 0,l=50;
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6*ACTIVE SET 0 \0",print_writer);

			String TeamJerseyColor = match.getHomeTeamId()==TeamId ? match.getHomeTeamJerseyColor():match.getAwayTeamJerseyColor();
			String GoalKeeperColor = match.getHomeTeamId()==TeamId ? match.getHomeTeamGKJerseyColor():match.getAwayTeamGKJerseyColor();

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
							
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$txt_TeamName*GEOM*TEXT SET " + 
					team.get(TeamId-1).getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead1*GEOM*TEXT SET " + 
					"STARTING XI" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead2*GEOM*TEXT SET " + 
					"SUBSTITUTES" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp1$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(TeamId-1).getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp2$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(TeamId-1).getTeamName4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + TeamJerseyColor + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1"
					+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + GoalKeeperColor.toUpperCase() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
			if(team.get(TeamId-1).getTeamCoach() == null) {
				if(team.get(TeamId-1).getTeamAssistantCoach() == null) {
					
					for(VariousText vartext : var) {
						if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
									vartext.getVariousText() + "\0",print_writer);
						}else if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
									"" + "\0",print_writer);
						}
					}
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							"" + "\0",print_writer);
				}else {

					for(VariousText vartext : var) {
						if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
									vartext.getVariousText() + "\0",print_writer);
						}else if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
									"ASST. COACH : " + "\0",print_writer);
						}
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamAssistantCoach() + "\0",print_writer);
				}
				
			}else {
				for(VariousText vartext : var) {
					if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								vartext.getVariousText() + "\0",print_writer);
					}else if(vartext.getVariousType().equalsIgnoreCase("FORMATIONHOMECOACH") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"COACH : " + "\0",print_writer);
					}
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
						team.get(TeamId-1).getTeamCoach() + "\0",print_writer);
			}
			
			
			
			for(Player hs : match.getHomeTeamId()==TeamId ? match.getHomeSquad():match.getAwaySquad()){
				row_id = row_id + 1;
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp"
						+ "*FUNCTION*Grid*num_row SET " + row_id + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
						"$Out$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
				
				if(hs.getSurname() != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFirstname().toUpperCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hs.getSurname().toUpperCase() + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFull_name().toUpperCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0",print_writer);
				}
				
				
				if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1"
							+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + GoalKeeperColor.toUpperCase() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1"
							+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + GoalKeeperColor.toUpperCase() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					
					TimeUnit.MILLISECONDS.sleep(l);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					TimeUnit.MILLISECONDS.sleep(l);
				}
				
				//FORMATION
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player" + row_id + 
						"$Out$In$Dehighlight$Radikal-Bold*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
				
				switch(Type.toUpperCase()) {
				case "WITHOUT_IMAGE":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
						"$In$SelectPlayerType$Jersey$img_Jersey"+ "*TEXTURE*IMAGE SET "+ colors_path + TeamJerseyColor.toUpperCase() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					
					if(TeamJerseyColor.equalsIgnoreCase("YELLOW")||TeamJerseyColor.equalsIgnoreCase("WHITE")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					}
					
					if(row_id == 1) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType$GK_Jersey$img_GK_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
					}
					
					
					break;
				case "WITH_IMAGE":
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$ImageAll$img_PlayerImage" + "*TEXTURE*IMAGE SET "+ photos_path + 
							(match.getHomeTeamId()==TeamId ? match.getHomeTeam().getTeamName4():match.getAwayTeam().getTeamName4()).toUpperCase() + 
								"//" + hs.getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
					
					break;
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
						"$In$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0",print_writer);
				
				TimeUnit.MILLISECONDS.sleep(l);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
						"$Out$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				
			}
			
			for(Formation form : formation) {
				if(form.getFormId() == (match.getHomeTeamId()==TeamId ? match.getHomeTeamFormationId():match.getAwayTeamFormationId())) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$txt_Formation*GEOM*TEXT SET " + form.getFormDescription() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0",print_writer);
					
					TimeUnit.MILLISECONDS.sleep(l);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0",print_writer);
				}
			}
			
			for(Player hsub :match.getHomeTeamId()==TeamId ? match.getHomeSubstitutes():match.getAwaySubstitutes()) {
				row_id_sub = row_id_sub + 1;
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench"
						+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
						"$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0",print_writer);
				
				if(hsub.getSurname() != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFirstname().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hsub.getSurname().toUpperCase() + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFull_name().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0",print_writer);
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
						"$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
				
				if(hsub.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
		}
		TimeUnit.MILLISECONDS.sleep(1000);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 LineUp$Team1$DataIn 2.520 \0",print_writer);
	}
	
	public void populateMatchDoublePromo(List<PrintWriter> print_writer,String viz_scene,String day,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, 
			List<VariousText> varText,String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
			
			int row_id = 1 ,l=4;
			String Date = "",grou = "",newDate = "";
			Calendar cal = Calendar.getInstance();
			boolean is_date_found = false;
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$TLogoGrp$LogoImageGrp1$img_Badges*TEXTURE*IMAGE SET "
					+ logo_path + "event" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$TLogoGrp$LogoImageGrp2$img_Badges*TEXTURE*IMAGE SET "
					+ logo_path + "event" + "\0",print_writer);
			
			for(VariousText vt : varText) {
				if(vt.getVariousType().equalsIgnoreCase("FFDOUBLEMATCHPROMO") && vt.getUseThis().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + vt.getVariousText() + "\0",print_writer);
					break;
				}else if(vt.getVariousType().equalsIgnoreCase("FFDOUBLEMATCHPROMO") && vt.getUseThis().equalsIgnoreCase(FootballUtil.NO)) {
					if(day.toUpperCase().equalsIgnoreCase("TODAY")) {
						Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + "TODAY'S MATCHES" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					else if(day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
						cal.add(Calendar.DATE, +1);
						Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + "TOMORROW'S MATCHES" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						cal.add(Calendar.DATE, +2);
						Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
						is_date_found = true;
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + 
					match.getTournament().toUpperCase() + "\0",print_writer);
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			for(int i = 0; i <= fixture.size()-1; i++) {
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					for(int j = 0; j <= ground.size()-1; j++) {
						if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(i).getVenue())) {
							grou = ground.get(j).getFullname();
						}
					}
					if(is_date_found == true) {
						newDate = fixture.get(i).getDate().split("-")[0];
						if(Integer.valueOf(newDate) < 10) {
							newDate = newDate.replaceFirst("0", "");
						}
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + "MATCHES ON " + 
								newDate + dateSuffix[Integer.valueOf(newDate)] + " " + Month.of(Integer.valueOf(fixture.get(i).getDate().split("-")[1])) + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET \0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$GroupNameGrp$txt_GroupName*GEOM*TEXT SET \0",print_writer);
					
					if(day.toUpperCase().equalsIgnoreCase("TODAY")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TimeGrp$txt_Time*GEOM*TEXT SET " + 
								" " + "\0",print_writer);
					}
					else if(day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TimeGrp$txt_Time*GEOM*TEXT SET " + 
								"KICK OFF AT " + fixture.get(i).getTime() + " LOCAL TIME" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TimeGrp$txt_Time*GEOM*TEXT SET " + 
								"KICK OFF AT " + fixture.get(i).getTime() + " LOCAL TIME" + "\0",print_writer);
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp3$img_BadgesOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp4$img_Badges"
//							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp1$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp2$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp3$img_BadgesOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp4$img_Badges"
//							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$SeparatorGrpGrp$txt_Separator*GEOM*TEXT SET " + "VS" + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + 
							team.get(fixture.get(i).getHometeamid() - 1).getTeamName1().toUpperCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + 
							team.get(fixture.get(i).getAwayteamid() - 1).getTeamName1().toUpperCase() + "\0",print_writer);
					
					row_id = row_id +1;
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 DoubleID_In 2.600 \0",print_writer);
		}
	}
	public void populateMiniPointsTable(List<PrintWriter> print_writer, String viz_sence_path, String Group, String WhichTable, List<LeagueTeam> point_table, List<LeagueTeam> new_point_table, 
			List<Team> team,String session_selected_broadcaster,Match match, FootballService footballService) throws InterruptedException, IOException 
	{		
		
		List<LeagueTeam> leaguetable = new ArrayList<LeagueTeam>();
		
		if(WhichTable.equalsIgnoreCase("AS IT STAND")) {
			leaguetable = new_point_table;
		}
		else if(WhichTable.equalsIgnoreCase("MINI POINTS TABLE")) {
			leaguetable = point_table;
		}
		
		
		int row_no = 1, omo = 0;
		String cout = "";
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
				Group.replace("LeagueTable", "GROUP ") + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$Header$Dataall$txt_Sub*GEOM*TEXT SET  " + 
				(WhichTable.equalsIgnoreCase("AS IT STAND") ? "AS IT STANDS" : "POINTS TABLE") + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row1$select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$Title$PointsData$txt_Draw*GEOM*TEXT SET L\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$Title$PointsData$txt_Lost*GEOM*TEXT SET D\0",print_writer);
		
		for(int i = 0; i <= leaguetable.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			if(match.getHomeTeam().getTeamName1().equalsIgnoreCase(leaguetable.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName1().equalsIgnoreCase(leaguetable.get(i).getTeamName())){
				omo = 2;
				cout = "$Highlight";
			}else {
				omo = 1;
				cout = "$Dehighlight";
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle*FUNCTION*Omo*vis_con SET " 
					+ omo + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$txt_Rank*GEOM*TEXT SET " + (row_no - 1) + "\0",print_writer);
			
			if(WhichTable.equalsIgnoreCase("AS IT STAND")) {
				for(int j=0;j<=point_table.size()-1;j++) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
							"$select_Arrow*ACTIVE SET 1\0",print_writer);
					if(point_table.get(j).getTeamName().equalsIgnoreCase(leaguetable.get(i).getTeamName())) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
								"$select_Arrow*FUNCTION*Omo*vis_con SET " + (i>j ? "2" : i<j ? "0" : "1") + "\0",print_writer);
					}
				}
			}else if(WhichTable.equalsIgnoreCase("MINI POINTS TABLE")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
						"$select_Arrow*ACTIVE SET 0\0",print_writer);
			}
						
			for(Team tm : team) {
				if(tm.getTeamName1().equalsIgnoreCase(leaguetable.get(i).getTeamName())) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
							"$txt_Name*GEOM*TEXT SET " + (leaguetable.get(i).getQualifiedStatus().trim().equalsIgnoreCase("") ? tm.getTeamName4() : 
								tm.getTeamName4() + " (Q)") + "\0",print_writer);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_Played*GEOM*TEXT SET " + leaguetable.get(i).getPlayed() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_Won*GEOM*TEXT SET " + leaguetable.get(i).getWon() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_Draw*GEOM*TEXT SET " + leaguetable.get(i).getLost() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_Lost*GEOM*TEXT SET " + leaguetable.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_GD*GEOM*TEXT SET " + leaguetable.get(i).getGD() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$Row" + row_no + "$select_DataStyle" + cout + 
					"$PointsData$txt_Points*GEOM*TEXT SET " + leaguetable.get(i).getPoints() + "\0",print_writer);
		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 2.320 \0",print_writer);
		
	}
	public void populatePointsTable(List<PrintWriter> print_writer,String viz_sence_path,String Group,List<LeagueTeam> point_table, List<Team> team,String session_selected_broadcaster,Match match, FootballService footballService) throws InterruptedException, IOException 
	{		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
		
		VariousText vText = footballService.getVariousTexts().stream().filter(vs -> vs.getVariousType().equalsIgnoreCase("FF_POINTS_TABLE_FOOTER") && vs.getUseThis().equalsIgnoreCase(FootballUtil.YES)).findAny().orElse(null);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " +"" + "\0",print_writer);

		if(vText!= null) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + vText.getVariousText() + "\0",print_writer);
		}
		int row_no = 0, omo = 0, l = 4;
		String cout = "";
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "POINTS TABLE" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET  " +match.getTournament()+ "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
		TimeUnit.MILLISECONDS.sleep(l);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$GroupHeadGrp1$txt_Group*GEOM*TEXT SET " + Group.replace("LeagueTable", "GROUP ") + "\0",print_writer);
		
		for(int k = 1; k <= 4; k++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" 
					+ k + "*ACTIVE SET 0" + "\0",print_writer);
		}
		
		for(int i = 0; i <= point_table.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			if(match.getHomeTeam().getTeamName1().equalsIgnoreCase(point_table.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName1().equalsIgnoreCase(point_table.get(i).getTeamName())){
				omo = 1;
				cout = "$Highlight";
			}else {
				omo = 0;
				cout = "$Dehighlight";
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" 
					+row_no + "*ACTIVE SET 1" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no + "\0",print_writer);
			
			for(Team tm : team) {
				if(point_table.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
							"$SelectType" + cout + "$Text$img_TeamBadge*TEXTURE*IMAGE SET "+ logo_path + "FCG" + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(point_table.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table.get(i).getTeamName())) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
							"$SelectType" + cout + "$Text$img_TeamBadge*TEXTURE*IMAGE SET "+ logo_path + tm.getTeamBadge() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + "*GEOM*ACTIVE SET 1" + "\0",print_writer);
					if(point_table.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table.get(i).getPlayed() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table.get(i).getWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table.get(i).getLost() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table.get(i).getGD() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table.get(i).getPoints() + "\0",print_writer);

		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0",print_writer);
		
	}
	public void populatePointsTableGrp(List<PrintWriter> print_writer,String viz_sence_path,String Group,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
		
		
		int row_no_1=0,row_no_2=0,omo=0,l=4;
		String cout = "";
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "GROUP STANDINGS" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
		TimeUnit.MILLISECONDS.sleep(l);
		
		if(Group.equalsIgnoreCase("SemiFinal1")) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP A" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP B" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
					"GROUP A WINNER WILL PLAY GROUP B WINNER IN THE 1st SEMI-FINAL" + "\0",print_writer);
			
		}else if(Group.equalsIgnoreCase("SemiFinal2")) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP C" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP D" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
					"GROUP C WINNER WILL PLAY GROUP D WINNER IN THE 2nd SEMI-FINAL" + "\0",print_writer);
		}
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no_1 = row_no_1 + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0",print_writer);
			
			for(Team tm : team) {
				if(point_table1.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table1.get(i).getTeamName())) {
					
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table1.get(i).getPlayed() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table1.get(i).getWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table1.get(i).getLost() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table1.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table1.get(i).getGD() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table1.get(i).getPoints() + "\0",print_writer);

		}
		
		for(int i = 0; i <= point_table2.size() - 1 ; i++) {
			row_no_2 = row_no_2 + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0",print_writer);
			
			for(Team tm : team) {
				if(point_table2.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table2.get(i).getTeamName())) {
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table2.get(i).getPlayed() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table2.get(i).getWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table2.get(i).getLost() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table2.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table2.get(i).getGD() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table2.get(i).getPoints() + "\0",print_writer);

		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0",print_writer);
		
	}
	public void populateFixtures_6(List<PrintWriter> print_writer, String viz_sence_path, String Group, String header, List<Playoff> playoffs,List<Fixture> fixture, 
			String session_selected_broadcaster, Match match) throws InterruptedException, IOException {
	    
		int date_row=0,int_i_value=0;
		String Time="";
		List<Fixture> team_fixture = new ArrayList<Fixture>();
		
//		team_fixture = fixture.stream().filter(fix -> fix.getHome_Team().getTeamName1().toUpperCase().contains(Group)
//                || fix.getAway_Team().getTeamName1().toUpperCase().contains(Group)).collect(Collectors.toList());

		team_fixture = fixture.stream().filter(fix -> fix.getGroupName().contains(Group.toUpperCase())).collect(Collectors.toList());

		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6*ACTIVE SET 1 \0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$BottomInfoGrp*ACTIVE SET 0\0",print_writer);
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$Fixtures_6$LogoGrp$LogoImageGrp1$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$Fixtures_6$LogoGrp$LogoImageGrp2$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);

	    String match_name = match.getMatchFileName().replace(".json", "");
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament().toUpperCase()+"\0",print_writer);
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Select_RowNumber*FUNCTION*Omo*vis_con SET 6\0",print_writer);
	     
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$txt_Header*GEOM*TEXT SET " + Group.toUpperCase() 
	    		+ " - " + (header.equalsIgnoreCase("FIXTURE") ? "FIXTURES" : "FIXTURES & RESULTS") + "\0",print_writer);
	    
	    for (int i=int_i_value;i<=team_fixture.size()-1;i++) {
	    	
            String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
	    	
			date_row = date_row + 1;
	    	
			if(team_fixture.get(i).getDate().equalsIgnoreCase(Date)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + 
						"$DateGrp$txt_Date*GEOM*TEXT SET " + "TOMORROW" + Time + "\0",print_writer);
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(team_fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + 
							"$DateGrp$txt_Date*GEOM*TEXT SET " + "TONIGHT" + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + 
							"$DateGrp$txt_Date*GEOM*TEXT SET " + team_fixture.get(i).getDate().split("-")[0] + " " + Month.of(Integer.parseInt(team_fixture.get(i).
									getDate().split("-")[1])) + " " + team_fixture.get(i).getDate().split("-")[2] + Time + "\0",print_writer);
				}
			}
			
            String cout = match_name.equalsIgnoreCase(team_fixture.get(i).getMatchfilename()) ? "$Highlight" : "$Dehighlight";
            
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + "$SelectMatchType"
            		+ "*FUNCTION*Omo*vis_con SET " + (cout.equals("$Highlight") ? 1 : 0) + "\0",print_writer);
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + cout + 
            		"$Team1$txt_Separator*GEOM*TEXT SET " + (team_fixture.get(i).getMargin() == null ? "VS" : team_fixture.get(i).getMargin()) + "\0",print_writer);
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + cout + 
            		"$Team1$txt_TeamName1*GEOM*TEXT SET " + team_fixture.get(i).getHome_Team().getTeamName1() + "\0",print_writer);
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_6$AllData$FixtureData$Row" + date_row + cout + 
            		"$Team1$txt_TeamName2*GEOM*TEXT SET " + team_fixture.get(i).getAway_Team().getTeamName1() + "\0",print_writer);
            
	    }
        
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Fixtures_6_In 2.580 \0",print_writer);
	}
	
	public void populateFixtures_7(List<PrintWriter> print_writer, String viz_sence_path, String Group, String header, List<Playoff> playoffs,List<Fixture> fixture, 
			String session_selected_broadcaster, Match match) throws InterruptedException, IOException {
	    
		int row_no = 0,date_row=0,int_i_value=0;
		String home_data= "",away_data="",data="",new_date="",Time="";
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7*ACTIVE SET 1 \0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$BottomInfoGrp*ACTIVE SET 0\0",print_writer);
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$Fixtures_7$LogoGrp$LogoImageGrp1$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$Fixtures_7$LogoGrp$LogoImageGrp2$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);

	    String match_name = match.getMatchFileName().replace(".json", "");
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament().toUpperCase()+"\0",print_writer);
	    
	    switch (Group.toUpperCase()) {
		case "ROUND_16":
			int_i_value = 0;
			data = "ROUND OF 16";
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row1*ACTIVE SET 1\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row4*ACTIVE SET 1\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row2$Grp3*ACTIVE SET 1\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row3$Grp5*ACTIVE SET 1\0",print_writer);
			break;
		case "QF":
			int_i_value = 7;
			data = "QUARTER FINALS";
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row1*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row4*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row2$Grp3*ACTIVE SET 1\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row3$Grp5*ACTIVE SET 1\0",print_writer);
			break;
		case "SF":
			int_i_value = 11;
			data = "SEMI FINALS";
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row1*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row4*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row2$Grp3*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row3$Grp5*ACTIVE SET 0\0",print_writer);
			break;
		}
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$txt_Header*GEOM*TEXT SET " + data + " - " +
	    		(header.equalsIgnoreCase("FIXTURE") ? "FIXTURES" : "FIXTURES & RESULTS") + "\0",print_writer);
	    
	    for (int i=int_i_value;i<=fixture.size()-1;i++) {
	    	
            String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
	    	
	    	if(i>6) {
	    		for(Playoff playoff : playoffs) {
	    			if(playoff.getPlayoffId() == (fixture.get(i).getMatchnumber()+1)) {
	    				home_data = playoff.getTeam1();
	    				away_data = playoff.getTeam2();
	    			}
	    		}
	    	}
	    	
			if(i<=6 && Group.equalsIgnoreCase("ROUND_16")) {
				
				row_no++;
				if(i==0 || i==1 || i==3 || i==5) {
		    		date_row ++;
		    	}
				new_date = fixture.get(i).getDate().split("-")[0].replaceFirst("^0", "");
				Time = "";
			}
			else if(i > 6 && i<=10 && Group.equalsIgnoreCase("QF")) {
				if(i==7) {
					row_no = 2;
		    		date_row = 2;;
		    	}else if(i==9) {
		    		row_no ++;
		    		date_row ++;
		    	}else {
		    		row_no ++;
		    	}
				new_date = fixture.get(i).getDate().split("-")[0].replaceFirst("^0", "");
				Time = "";
				
			}else if(i > 10 && i<=12 && Group.equalsIgnoreCase("SF")) {
				if(i == 11) {
					row_no = 2;
		    		date_row = 2;;
		    	}else if(i==12) {
		    		row_no = row_no + 2;
		    		date_row ++;
		    	}
				new_date = fixture.get(i).getDate().split("-")[0].replaceFirst("^0", "");
				Time = " AT " + fixture.get(i).getTime();
				
			}else {
				break;
			}
			
			if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row" + date_row + "$DateGrp$txt_Date*GEOM*TEXT SET " 
						+ "TOMORROW" + Time + "\0",print_writer);
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row" + date_row + "$DateGrp$txt_Date*GEOM*TEXT SET " 
							+ "TONIGHT" + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Row" + date_row + "$DateGrp$txt_Date*GEOM*TEXT SET " 
							+ new_date + " " + Month.of(Integer.parseInt(fixture.get(i).getDate().split("-")[1])) +" "+fixture.get(i).getDate().split("-")[2] + Time  + "\0",print_writer);
				}
			}
			
            String cout = match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename()) ? "$Highlight" : "$Dehighlight";
            
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + "$SelectMatchType"
            		+ "*FUNCTION*Omo*vis_con SET " + (cout.equals("$Highlight") ? 1 : 0) + "\0",print_writer);
            FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + cout + "$Team1$txt_Separator*GEOM*TEXT SET " 
            			+ (fixture.get(i).getMargin() == null ? "VS" : fixture.get(i).getMargin()) + "\0",print_writer);
            
            if(fixture.get(i).getHome_Team().getTeamName1().equalsIgnoreCase("QF") || fixture.get(i).getHome_Team().getTeamName1().equalsIgnoreCase("SF") ||
            		fixture.get(i).getHome_Team().getTeamName1().equalsIgnoreCase("FINAL")) {
            	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + cout + "$Team1$txt_TeamName1"
            			+ "*GEOM*TEXT SET " + home_data + "\0",print_writer);
            }else {
            	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + cout + "$Team1$txt_TeamName1"
            			+ "*GEOM*TEXT SET " + fixture.get(i).getHome_Team().getTeamName1() + "\0",print_writer);
            }
            if(fixture.get(i).getAway_Team().getTeamName1().equalsIgnoreCase("QF") || fixture.get(i).getAway_Team().getTeamName1().equalsIgnoreCase("SF") ||
            		fixture.get(i).getAway_Team().getTeamName1().equalsIgnoreCase("FINAL")) {
            	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + cout + "$Team1$txt_TeamName2"
            			+ "*GEOM*TEXT SET " + away_data + "\0",print_writer);
            }else {
            	FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$Fixtures_7$AllData$FixtureData$Grp" + row_no + cout + "$Team1$txt_TeamName2"
            			+ "*GEOM*TEXT SET " + fixture.get(i).getAway_Team().getTeamName1() + "\0",print_writer);
            }
	    }
        
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Fixtures_7_In 2.780 \0",print_writer);
	}
	public void populatePlayOffTree(List<PrintWriter> print_writer, String viz_sence_path, List<Playoff> playoffs, List<Team> teams, String session_selected_broadcaster, Match match) throws InterruptedException, IOException {
	    
	    int row_no = 0;
	    String new_date="",cont="";
	    Team team1 = null,team2 = null;
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Left$QF$1$txt_MatchNumber*GEOM*TEXT SET QF 1\0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Left$QF$2$txt_MatchNumber*GEOM*TEXT SET QF 2\0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Right$QF$1$txt_MatchNumber*GEOM*TEXT SET QF 3\0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Right$QF$2$txt_MatchNumber*GEOM*TEXT SET QF 4\0",print_writer);
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Left$SF$1$txt_MatchNumber*GEOM*TEXT SET SF 1\0",print_writer);
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Right$SF$1$txt_MatchNumber*GEOM*TEXT SET SF 2\0",print_writer);
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$txt_MatchNumber*GEOM*TEXT SET FINAL\0",print_writer);
	    
	    for(int i=0;i<=playoffs.size()-1;i++) {
	    	row_no = row_no + 1;
	    	new_date = playoffs.get(i).getDate().split("-")[0];
			if(Integer.valueOf(new_date) < 10) {
				new_date = new_date.replaceFirst("0", "");
			}
			
			System.out.println(playoffs.get(i).getPlayoffType());
			
			team1 = null;
			team2 = null;
			
			for(Team team : teams) {
    			if(team.getTeamBadge().equalsIgnoreCase(playoffs.get(i).getTeam1())) {
    				team1 = team;
    			}
    			if(team.getTeamBadge().equalsIgnoreCase(playoffs.get(i).getTeam2())) {
    				team2 = team;
    			}
    		}
	    	
	    	if(playoffs.get(i).getPlayoffType().contains("MATCH")) {	
	    		switch (i) {
				case 0: case 1: case 2: case 3:
					cont = "Left$PQF$" + row_no;
					break;
				case 4: case 5: case 6: case 7:
					cont = "Right$PQF$" + row_no;
					break;
				}
	    		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$txt_Date*GEOM*TEXT SET " + FootballFunctions.ordinal(Integer.valueOf(new_date)) + 
	    				" " + Month.of(Integer.valueOf(playoffs.get(i).getDate().split("-")[1])) + "\0",print_writer);
	    		
	    		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$txt_Name*GEOM*TEXT SET " + team1.getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team1.getTeamBadge() + "\0",print_writer);
				
				if(playoffs.get(i).getMargin() != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[0] + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$txt_Name*GEOM*TEXT SET " + team2.getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team2.getTeamBadge() + "\0",print_writer);
				
				if(playoffs.get(i).getMargin() != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[1] + "\0",print_writer);
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				}
	    		
	    	}
	    	else if(playoffs.get(i).getPlayoffType().contains("QF") || playoffs.get(i).getPlayoffType().contains("SF")) {
	    		switch (i) {
				case 8: case 9:
					cont = "Left$QF$" + row_no;
					break;
				case 10: case 11:
					cont = "Right$QF$" + row_no;
					break;
				case 12:
					cont = "Left$SF$" + row_no;
					break;
				case 13:
					cont = "Right$SF$" + row_no;
					break;
				}
	    		
	    		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$txt_Date*GEOM*TEXT SET " + FootballFunctions.ordinal(Integer.valueOf(new_date)) + 
	    				" " + Month.of(Integer.valueOf(playoffs.get(i).getDate().split("-")[1])) + "\0",print_writer);
	    		
	    		if(team1 != null) {
	    			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$Select_Style*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithTeam$txt_Name*GEOM*TEXT SET " + team1.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithTeam$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team1.getTeamBadge() + "\0",print_writer);
					
					if(playoffs.get(i).getMargin() != null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithTeam$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[0] + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					}
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$Select_Style*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team1$WithoutTeam$txt_Name*GEOM*TEXT SET " + playoffs.get(i).getTeam1() + "\0",print_writer);
				}
				
				if(team2 != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$Select_Style*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithTeam$txt_Name*GEOM*TEXT SET " + team2.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithTeam$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team2.getTeamBadge() + "\0",print_writer);
					
					if(playoffs.get(i).getMargin() != null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithTeam$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[1] + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					}
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$Select_Style*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$" + cont + "$Team2$WithoutTeam$txt_Name*GEOM*TEXT SET " + playoffs.get(i).getTeam2() + "\0",print_writer);
				}
	    	}
	    	else if(playoffs.get(i).getPlayoffType().equalsIgnoreCase("FINAL")) {
	    		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$txt_Date*GEOM*TEXT SET " + FootballFunctions.ordinal(Integer.valueOf(new_date)) + 
	    				" " + Month.of(Integer.valueOf(playoffs.get(i).getDate().split("-")[1])) + "\0",print_writer);
	    		
	    		if(team1 != null) {
	    			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$Select_Style*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithTeam$txt_Name*GEOM*TEXT SET " + team1.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithTeam$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team1.getTeamBadge() + "\0",print_writer);
					
					if(playoffs.get(i).getMargin() != null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithTeam$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[0] + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					}
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$Select_Style*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team1$WithoutTeam$txt_Name*GEOM*TEXT SET " + playoffs.get(i).getTeam1() + "\0",print_writer);
				}
				
				if(team2 != null) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$Select_Style*FUNCTION*Omo*vis_con SET 1\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithTeam$txt_Name*GEOM*TEXT SET " + team2.getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithTeam$img_BasgesBW*TEXTURE*IMAGE SET " + logo_bw_path + team2.getTeamBadge() + "\0",print_writer);
					
					if(playoffs.get(i).getMargin() != null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 1\0",print_writer);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithTeam$txt_Score*GEOM*TEXT SET " + playoffs.get(i).getMargin().split("-")[1] + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithTeam$Select_DataStyle*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					}
				}else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$Select_Style*FUNCTION*Omo*vis_con SET 0\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$AllBg$Data$TeamAll$Final$Team2$WithoutTeam$txt_Name*GEOM*TEXT SET " + playoffs.get(i).getTeam2() + "\0",print_writer);
				}	
	    	}
	    	
	    	if(i==3 || i == 7 || i == 9 || i >= 11) {
				row_no = 0;
			}
	    }
	    
	    
	    FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 1.700\0",print_writer);
	}

	public void populateFixtures(List<PrintWriter> print_writer,String viz_sence_path,String Group,String header,List<Fixture> fixture,List<Team> team,List<Ground> ground,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$BottomInfoGrp*ACTIVE SET 0\0",print_writer);
		int row_no=0,omo=0;
		String cout = "",match_name="",new_date="";
		
		String[] dateSuffix = {
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
				
				"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
				
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
				
				"th", "st"
		};
				  
		match_name = match.getMatchFileName().replace(".json", "");
		
		switch(header.toUpperCase()) {
		case "FIXTURE":
			if(Group.equalsIgnoreCase("group A")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP A" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group B")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP B" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group C")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP C" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group D")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP D" + "\0",print_writer);
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "GROUP STAGE" + "\0",print_writer);

			break;
		case "RESULT":
			if(Group.equalsIgnoreCase("group A")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "RESULTS - GROUP A" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group B")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "RESULTS - GROUP B" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group C")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "RESULTS - GROUP C" + "\0",print_writer);
			}else if(Group.equalsIgnoreCase("group D")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "RESULTS - GROUP D" + "\0",print_writer);
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "GROUP STAGE" + "\0",print_writer);

			break;
		}
				
		for(int i=0;i<=fixture.size()-1;i++) {
			if(fixture.get(i).getGroupName()== null || fixture.get(i).getGroupName().equalsIgnoreCase(Group.toUpperCase())) {
				row_no = row_no + 1;
				
				if(row_no <= 2) {
					new_date = fixture.get(i).getDate().split("-")[0];
					if(Integer.valueOf(new_date) < 10) {
						new_date = new_date.replaceFirst("0", "");
					}
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " " + Month.of(Integer.valueOf(fixture.get(i).getDate().split("-")[1])) + "\0",print_writer);
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0",print_writer);
					
					if(fixture.get(i).getMargin() == null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0",print_writer);
					}
					
					
					
				}else if(row_no > 2 && row_no <= 4) {
					new_date = fixture.get(i).getDate().split("-")[0];
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " " + Month.of(Integer.valueOf(fixture.get(i).getDate().split("-")[1])) + "\0",print_writer);
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0",print_writer);
					
					if(fixture.get(i).getMargin() == null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0",print_writer);
					}
					
					
				}else if(row_no > 4 && row_no <= 6) {
					new_date = fixture.get(i).getDate().split("-")[0];
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " " + Month.of(Integer.valueOf(fixture.get(i).getDate().split("-")[1])) + "\0",print_writer);
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0",print_writer);
					
					if(fixture.get(i).getMargin() == null) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0",print_writer);
					}
				}
			}
		}

		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Fixtures_In 2.500 \0",print_writer);
		
	}
	
	public void populateQulifiers(List<PrintWriter> print_writer,String viz_sence_path,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + 
				"RESULTS OF THE QUALIFIERS" + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				"ROUNDGLASS PUNJAB FC QUALIFIED DIRECTLY FOR THE GROUP STAGE" + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 2.500 FF_In 2.000 Fixtures_In 2.500 \0",print_writer);
		
	}
	
	public void populateLtPenalty(List<PrintWriter> print_writer,String viz_scene,String valueToProcess,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) 
			throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l=200;
			int iHomeCont = 0, iAwayCont = 0;
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + match.getHomeTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + match.getHomePenaltiesHits() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + match.getAwayPenaltiesHits() + "\0",print_writer);
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int p=1;p<=5;p++) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + p + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						p + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + p + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						p + "\0",print_writer);
			}
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
					
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
					
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES$" + "_" + FootballUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.INCREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
				}
				
				
				if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.HOME + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.HIT)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(FootballUtil.AWAY + "_" + FootballUtil.DECREMENT + "_" + "PENALTIES" + "_" + FootballUtil.MISS)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.700 \0",print_writer);
		}
	}
	public void populateLtPenaltyChange(List<PrintWriter> print_writer,Match match, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int iHomeCont = 0, iAwayCont = 0;
			int HomeTotal = 0,AwayTotal=0;
			
			iHomeCont = (match.getHomePenaltiesHits() + match.getHomePenaltiesMisses());
			iAwayCont = (match.getAwayPenaltiesHits() + match.getAwayPenaltiesMisses());
			
			HomeTotal = iHomeCont + 5;
			AwayTotal = iAwayCont + 5;
			
			if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
					penalties = new ArrayList<String>();
						for(int p=1;p<=5;p++) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
						}
				}
			}
			
			for(int h=iHomeCont+1;h<=HomeTotal;h++) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + (h-iHomeCont) + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						h + "\0",print_writer);
			}
			
			for(int a=iAwayCont+1;a<=AwayTotal;a++) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + (a-iAwayCont) + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						a + "\0",print_writer);
			}
			
		}
	}
	
	public void populateScoreUpdate(List<PrintWriter> print_writer,String viz_scene,String Header, FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			int l=200;
			String h1="",h2="",h3="",h4="",h5="",h6="",a1="",a2="",a3="",a4="",a5="",a6="";
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			TimeUnit.MILLISECONDS.sleep(l);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			
			System.out.println(Header);
			if(Header.equalsIgnoreCase("Without_Header")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Time*ACTIVE SET 0\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Time*ACTIVE SET 1\0",print_writer);
				if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.HALF)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FULL)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FIRST)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.SECOND)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1) || match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "EXTRA TIME" + "\0",print_writer);
				}	
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + 
					(match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "-" +(match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
						
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			for(VariousText vartext : footballService.getVariousTexts()) {
				if(vartext.getVariousType().equalsIgnoreCase("LTSCORELINEHEADER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + vartext.getVariousText() + "\0",print_writer);
				}
			}
			
			for(VariousText vartext : footballService.getVariousTexts()) {
				if(vartext.getVariousType().equalsIgnoreCase("LTSCORELINEFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + vartext.getVariousText() + "\0",print_writer);
				}
				else if(vartext.getVariousType().equalsIgnoreCase("LTSCORELINEFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + match.getTournament() + "\0",print_writer);
				}
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
							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(), match.getMatchStats().get(i).getTotalMatchSeconds()) + 
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
//				else if (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.RED)) {
//		            player = match.getMatchStats().get(i).getPlayer();
//		            String stats_txts = player.getTicker_name() + " " +
//		                FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(), match.getMatchStats().get(i).getTotalMatchSeconds()) +
//		                "(R)";
//		            
//		            if (player.getTeamId() == match.getHomeTeamId()) {
//		            	home_stats.add(stats_txts);
//		            } else if (player.getTeamId() == match.getAwayTeamId()) {
//		            	away_stats.add(stats_txts);
//		            }
//		        }
			}

			if (home_stats.size() == 0) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			}else if(home_stats.size() <= 2) { 
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
			}else if(home_stats.size() <= 4) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			}else if(home_stats.size() <= 6){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0",print_writer);
			}else if(home_stats.size() <= 8){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
			}else if(home_stats.size() <= 10){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "5" + "\0",print_writer);
			}else if(home_stats.size() <= 12){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "6" + "\0",print_writer);
			}
			
			if (away_stats.size() == 0) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			}else if(away_stats.size() <= 2) { 
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
			}else if(away_stats.size() <= 4) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			}else if(away_stats.size() <= 6){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0",print_writer);
			}else if(away_stats.size() <= 8){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
			}else if(away_stats.size() <= 10){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "5" + "\0",print_writer);
			}else if(away_stats.size() <= 12){
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "6" + "\0",print_writer);
			}
			
			
//			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore()==0) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
//			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$02$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$03$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$02$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h3 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 8){
					h4 = h4 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$04$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$03$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$02$txt_Scorer1*GEOM*TEXT SET " + h3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h4 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 10){
					h5 = h5 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$05$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$04$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$03$txt_Scorer1*GEOM*TEXT SET " + h3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$02$txt_Scorer1*GEOM*TEXT SET " + h4 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h5 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 12){
					h6 = h6 + home_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$06$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$05$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$04$txt_Scorer1*GEOM*TEXT SET " + h3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$03$txt_Scorer1*GEOM*TEXT SET " + h4 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$02$txt_Scorer1*GEOM*TEXT SET " + h5 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$1$txt_Scorer1*GEOM*TEXT SET " + h6 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$02$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$03$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$02$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a3 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 8){
					a4 = a4 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$04$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$03$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$02$txt_Scorer1*GEOM*TEXT SET " + a3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a4 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 10){
					a5 = a5 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$05$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$04$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$03$txt_Scorer1*GEOM*TEXT SET " + a3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$02$txt_Scorer1*GEOM*TEXT SET " + a4 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a5 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 12){
					a6 = a6 + away_stats.get(i);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$06$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$05$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$04$txt_Scorer1*GEOM*TEXT SET " + a3 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$03$txt_Scorer1*GEOM*TEXT SET " + a4 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$02$txt_Scorer1*GEOM*TEXT SET " + a5 + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$1$txt_Scorer1*GEOM*TEXT SET " + a6 + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
	}
	public void populateLtMatchId(List<PrintWriter> print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamBadge() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamBadge() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Time*ACTIVE SET 0\0",print_writer);
//			if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.HALF)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FULL)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0",print_writer);
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FIRST)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "FIRST HALF" + "\0",print_writer);
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.SECOND)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "SECOND HALF" + "\0",print_writer);
//			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1) || match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "EXTRA TIME" + "\0",print_writer);
//			}
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + 
//					(match.getHomeTeamScore() < 0 ? 0 : match.getHomeTeamScore()) + "-" + (match.getAwayTeamScore() < 0 ? 0 : match.getAwayTeamScore()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET VS\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			
			for(VariousText vartext : footballService.getVariousTexts()) {
				if(vartext.getVariousType().equalsIgnoreCase("LT_Match_PenaltyResult") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + vartext.getVariousText() + "\0",print_writer);
				}
				else if(vartext.getVariousType().equalsIgnoreCase("LT_Match_PenaltyResult") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET LIVE FROM " + match.getVenueName() + "\0",print_writer);
				}
			}
//			String h1="",h2="",h3="",h4="",a1="",a2="",a3="",a4="";
//			int l=200;
//			List<String> home_stats = new ArrayList<String>();
//			List<String> away_stats = new ArrayList<String>();
//			List<Integer> plyr_ids = new ArrayList<Integer>();
//			boolean plyr_exist = false;
// 			String stats_txt = "",stats_txt_og = "";
// 			
//			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
//				
//				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
//						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
//					
//					plyr_exist = false;
//					for(Integer plyr_id : plyr_ids) {
//						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
//								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.GOAL) 
//										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)
//										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
//							plyr_exist = true;
//							break;
//						}
//					}
//
//					if(plyr_exist == false) {
//						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
//						stats_txt = footballService.getPlayer(FootballUtil.PLAYER, 
//							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
//							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(), match.getMatchStats().get(i).getTotalMatchSeconds()) + 
//							FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
//						
//						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
//							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
//								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.GOAL)
//								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(FootballUtil.PENALTY))) {
//								
//								stats_txt = stats_txt.trim() + "," + 
//								FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
//										+ FootballFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
//							}
//						}
//						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
//						case FootballUtil.HOME:
//							home_stats.add(stats_txt);
//							break;
//						case FootballUtil.AWAY:
//							away_stats.add(stats_txt);
//							break;
//						}
//					}
//				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(FootballUtil.OWN_GOAL)) {
//					stats_txt_og = footballService.getPlayer(FootballUtil.PLAYER, 
//							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
//							FootballFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
//								FootballFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
//						
//						switch (FootballFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
//						case FootballUtil.HOME:
//							home_stats.add(stats_txt_og);
//							break;
//						case FootballUtil.AWAY:
//							away_stats.add(stats_txt_og);
//							break;
//						}
//				}
//			}
//
//			if (home_stats.size() == 0) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
//			}else if(home_stats.size() <= 2) { 
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
//			}else if(home_stats.size() <= 4) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
//			}else if(home_stats.size() <= 6){
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0",print_writer);
//			}else if(home_stats.size() <= 8){
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
//			}
//			
//			if (away_stats.size() == 0) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
//			}else if(away_stats.size() <= 2) { 
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
//			}else if(away_stats.size() <= 4) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
//			}else if(away_stats.size() <= 6){
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0",print_writer);
//			}else if(away_stats.size() <= 8){
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0",print_writer);
//			}
//			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore()==0) {
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
//			}
			
//			for(int i=0;i<=home_stats.size()-1;i++) {
//				if(i < 2) { 
//					h1 = h1 + home_stats.get(i); 
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$First$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 4) {
//					h2 = h2 + home_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Second$txt_Scorer2*GEOM*TEXT SET " + h1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$First$txt_Scorer1*GEOM*TEXT SET " + h2 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 6){
//					h3 = h3 + home_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Third$txt_Scorer3*GEOM*TEXT SET " + h1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Second$txt_Scorer2*GEOM*TEXT SET " + h2 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$First$txt_Scorer1*GEOM*TEXT SET " + h3 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 8){
//					h4 = h4 + home_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Fourth$txt_Scorer4*GEOM*TEXT SET " + h1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Third$txt_Scorer3*GEOM*TEXT SET " + h2 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Second$txt_Scorer2*GEOM*TEXT SET " + h3 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$First$txt_Scorer1*GEOM*TEXT SET " + h4 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}
//			}
//			
//			for(int i=0;i<=away_stats.size()-1;i++) {
//				if(i < 2) { 
//					a1 = a1 + away_stats.get(i); 
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$First$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 4) {
//					a2 = a2 + away_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Second$txt_Scorer2*GEOM*TEXT SET " + a1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$First$txt_Scorer1*GEOM*TEXT SET " + a2 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 6){
//					a3 = a3 + away_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Third$txt_Scorer3*GEOM*TEXT SET " + a1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Second$txt_Scorer2*GEOM*TEXT SET " + a2 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$First$txt_Scorer1*GEOM*TEXT SET " + a3 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}else if(i < 8){
//					a4 = a4 + away_stats.get(i);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Fourth$txt_Scorer4*GEOM*TEXT SET " + a1 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Third$txt_Scorer3*GEOM*TEXT SET " + a2 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Second$txt_Scorer2*GEOM*TEXT SET " + a3 + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$First$txt_Scorer1*GEOM*TEXT SET " + a4 + "\0",print_writer);
//					TimeUnit.MILLISECONDS.sleep(l);
//				}
//			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
	}
	public void populateLTMatchPromoSingle(List<PrintWriter> print_writer,String viz_scene, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
			
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + match.getMatchIdent() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + "VS" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
			
			String Date = "";
			Calendar cal = Calendar.getInstance();
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + "COMING UP AT " + fix.get(match_number-1).getTime() 
						+ " LOCAL TIME" + "\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + fix.get(match_number-1).getDate() + 
						" AT " + fix.get(match_number-1).getTime() + " LOCAL TIME" + "\0",print_writer);
			}
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
	}
	
	public void populateLTPlayerStats(List<PrintWriter> print_writer,String viz_scene, int playerStatsId ,List<Team> team,List<PlayerStat> playerStats,Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getPlayer().getJersey_number() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Header$Dataall$TextGrp$txt_Name*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getPlayer().getFull_name() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Header$Dataall$TextGrp$txt_Info*GEOM*TEXT SET " + 
					(playerStats.get(playerStatsId-1).getSubHeader()==null?"":playerStats.get(playerStatsId-1).getSubHeader()) + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$LogoGrp$Ani_1$img_Logo*TEXTURE*IMAGE SET "+ logo_path + 
					team.get(playerStats.get(playerStatsId-1).getPlayer().getTeamId() - 1).getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$LogoGrp$Ani_2$img_Logo*TEXTURE*IMAGE SET "+ logo_path + 
					team.get(playerStats.get(playerStatsId-1).getPlayer().getTeamId() - 1).getTeamName4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead1*TRANSFORMATION*POSITION*X SET " + "0" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead2*TRANSFORMATION*POSITION*X SET " + "420" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead3*TRANSFORMATION*POSITION*X SET " + "840" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead4*TRANSFORMATION*POSITION*X SET " + "1260" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue1*TRANSFORMATION*POSITION*X SET " + "0" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue2*TRANSFORMATION*POSITION*X SET " + "420" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue3*TRANSFORMATION*POSITION*X SET " + "840" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue4*TRANSFORMATION*POSITION*X SET " + "1260" + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead1*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getHeadStats1() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead2*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getHeadStats2() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead3*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getHeadStats3() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead4*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getHeadStats4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue1*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getValueStats1() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue2*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getValueStats2() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue3*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getValueStats3() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue4*GEOM*TEXT SET " + 
					playerStats.get(playerStatsId-1).getValueStats4() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead5*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline1$InfoGrp$txt_StatHead6*ACTIVE SET 0\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue5*ACTIVE SET 0\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$NameSuperCard$Subline2$InfoGrp$txt_StatValue6*ACTIVE SET 0\0",print_writer);
			
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.400 \0",print_writer);
		}
	}
	
	public void populateStaff(List<PrintWriter> print_writer,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(st.getClubId() - 1).getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(st.getClubId() - 1).getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + st.getName().toUpperCase() + "\0",print_writer);
				
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
					st.getRole().toUpperCase() + ", " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
	}
	public void populateNameSuper(List<PrintWriter> print_writer,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Logo_Grp$Nquad$img_Badges"
//					+ "*TEXTURE*IMAGE SET "+ logo2_path + "HeroTrination" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
					"" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$txt_Name*GEOM*TEXT SET " + 
				    (ns.getFirstname() == null ? ns.getSurname() : (ns.getSurname() == null ? ns.getFirstname() : ns.getFirstname() + " " + ns.getSurname())) + "\0",print_writer);

			if(ns.getSponsor() == null) {
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_Logo"
						+ "*TEXTURE*IMAGE SET "+logo_path + "event" + "\0",print_writer);				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$img_Logo"+ "*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
						(ns.getSubLine()==null ? " ":ns.getSubLine().toUpperCase()) + "\0",print_writer);
				
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + ns.getSponsor()+ "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + ns.getSponsor() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + ns.getSponsor() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_3$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_4$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
			}
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
					(ns.getSubLine()==null ? " ":ns.getSubLine().toUpperCase()) + "\0",print_writer);
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
		
	}
	public void populateNameSuperPlayer(List<PrintWriter> print_writer,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			String Home_or_Away="";
			int l = 4;
			
			if(captainGoalKeeper.equalsIgnoreCase("HERO OF THE MATCH")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 8 \0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$BottomGrp$InfoGrp$InfoDataGrp$txt_BottomInfo*GEOM*TEXT SET " + 
						" OF THE MATCH " + "\0",print_writer);
				
				if(TeamId == match.getHomeTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_1$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_3$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_4$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hs.getFull_name().toUpperCase() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hs.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hsub.getFull_name().toUpperCase() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hsub.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
				}
				else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_1$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_3$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_4$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									as.getFull_name().toUpperCase() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									as.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									asub.getFull_name().toUpperCase() + "\0",print_writer);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									asub.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
				}
				
				
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 1 \0",print_writer);
				
				if(TeamId == match.getHomeTeamId()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_1$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_3$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_4$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hs.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hs.getFull_name().toUpperCase() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
								TimeUnit.MILLISECONDS.sleep(l);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hsub.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hsub.getFull_name().toUpperCase() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}
						}
					}
				}
				else {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_1$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_3$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_4$img_LogoOutline"
//							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									as.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									as.getFull_name().toUpperCase() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									asub.getJersey_number() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									asub.getFull_name().toUpperCase() + "\0",print_writer);
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
							}
						}
					}
				}
				
				switch(captainGoalKeeper.toUpperCase())
				{
				case "CAPTAIN":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							captainGoalKeeper.toUpperCase() + ", " + Home_or_Away + "\0",print_writer);
					break;
				/*case "PLAYER OF THE MATCH":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"PLAYER OF THE MATCH " + "\0",print_writer);
					break;*/
				case "PLAYER OF THE MATCH":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"PLAYER OF THE MATCH " + "\0",print_writer);
					break;
				case "GOAL_KEEPER":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"GOALKEEPER" + ", " + Home_or_Away + "\0",print_writer);
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
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								"GOALS TODAY - " + player_goal_count + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								Home_or_Away + "\0",print_writer);
					}
					
					break;
				case "GOAL_SCORER":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"GOAL SCORER" + ", " + Home_or_Away + "\0",print_writer);
					break;
				case "CAPTAIN-GOALKEEPER":
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"CAPTAIN & GOALKEEPER" + ", " + Home_or_Away + "\0",print_writer);
					break;
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);	
		}
	}
	public void populateNameSuperCard(List<PrintWriter> print_writer,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$Select*FUNCTION*Omo*vis_con SET 3 \0",print_writer);
			
			if(TeamId == match.getHomeTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_1$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_3$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_4$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + hs.getFull_name().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								hs.getRole().toUpperCase() + ", " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + hsub.getFull_name().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								hsub.getRole().toUpperCase() + ", " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0",print_writer);
					}
				}
			}
			else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_1$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_3$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
//				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_4$img_LogoOutline"
//						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + as.getFull_name().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								as.getRole().toUpperCase() + ", " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + asub.getFull_name().toUpperCase() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
						
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								asub.getRole().toUpperCase() + ", " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0",print_writer);
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case FootballUtil.YELLOW:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 0 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case FootballUtil.RED:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 1 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 2 \0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);		
		}
	}
	public void populateSubstitute(List<PrintWriter> print_writer,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int l = 200;
			List<Event> evnt = new ArrayList<Event>();
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 4 \0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_1$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(Team_id - 1).getTeamName4() + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(Team_id - 1).getTeamName4() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_3$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0",print_writer);
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_4$img_LogoOutline"
//					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0",print_writer);
			
			
			switch(Num_Of_Subs.toUpperCase())
			{
			case "SINGLE":
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$InPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$InPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split("-")[1])) - 1).getFull_name().toUpperCase() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$OutPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$OutPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split("-")[0])) - 1).getFull_name().toUpperCase() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$OutPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getJersey_number() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$OutPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[0])) - 1).getFull_name().toUpperCase() + "\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$InPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getJersey_number() + "\0",print_writer);
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$InPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(Integer.valueOf((scorebug.getScorebug_subs().split(":")[0].split("-")[1])) - 1).getFull_name().toUpperCase() + "\0",print_writer);
				
				
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			/*case "DOUBLE":
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				break;
				
			case "TRIPLE":
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2C " + logo_path + "Red_Arrow" + FootballUtil.PNG_EXTENSION + ";");
			
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				FootballFunctions.DoadWriteCommandToAllViz("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1C " + logo_path + "Green_Arrow" + FootballUtil.PNG_EXTENSION + ";");
				
				break;*/
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);	
		}
	}
	public void populateOfficials(List<PrintWriter> print_writer,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 5 \0",print_writer);

			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getReferee()  + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$BottomGrp$InfoGrp$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getAssistantReferee1() + " & " + officials.get(0).getAssistantReferee2() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$BottomGrp$Dataall2$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getFourthOfficial() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);	
		}
	}
	public void populateHeatMapPeakDistance(List<PrintWriter> print_writer,String viz_scene,int TeamId,String Value,int Playerid,List<Player> plyr,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int team_number=0;
			String team_name="";
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 7 \0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_Number*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getJersey_number()  + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_PlayerName*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getTicker_name()  + "\0",print_writer);
			
			if(match.getHomeTeamId() == TeamId) {
				team_number = 0;
				team_name = match.getHomeTeam().getTeamName4();
			}else if(match.getAwayTeamId() == TeamId){
				team_number = 1;
				team_name = match.getAwayTeam().getTeamName4();
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_TeamName*GEOM*TEXT SET " + 
					team_name  + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$Logo_Grp$Nquad$img_Badges" + "*TEXTURE*IMAGE SET "+ photos_path + 
//					team_name + "\\" + plyr.get(Playerid-1).getPhoto() + FootballUtil.PNG_EXTENSION + "\0",print_writer);
			
//			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$NameDataGrp$Field$Football_Pitch*ACTIVE SET 0 \0",print_writer);
			
			switch(Value.toUpperCase()) {
			case "HEATMAP":
				TimeUnit.SECONDS.sleep(2);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$DataAll$DataOut$Ground$img_HeatMap*TEXTURE*IMAGE SET "+ image_path + 
						"playerheatmap" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0",print_writer);
				break;
			case "PEAKDISTANCE":
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$DataAll$DataOut$Ground$img_HeatMap*TEXTURE*IMAGE SET "+ image_path + 
						"playerpeakdistancegraph" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0",print_writer);
				break;
			}
			  
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);	
		}
	}
	public void populateTopStats(List<PrintWriter> print_writer,String viz_scene,String Value,List<TeamStats>teamStats,List<Player> player,List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			String team_name="";
			int row = 0;
			
			List<PlayerStats> top_stats = new ArrayList<PlayerStats>();
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$Out$Select_EventLogo*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
			
			if(Value.toUpperCase().equalsIgnoreCase("TEAM TOP SPEED")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"TOP SPEED" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + 
						"(KPH)" + "\0",print_writer);
			}else if(Value.toUpperCase().equalsIgnoreCase("HIGHEST DISTANCE")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"HIGHEST DISTANCE" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + 
						"(KMS)" + "\0",print_writer);
			}else if(Value.toUpperCase().equalsIgnoreCase("BEST RUNNER")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"TOP RUNNER" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + 
						"(KPH)" + "\0",print_writer);
			}else if(Value.toUpperCase().equalsIgnoreCase("BEST SPRINTER")) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"TOP SPRINTS" + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + 
						"" + "\0",print_writer);
			}
			
			
	        for(int i=0;i<= teamStats.size()-1;i++) {
        		for(int j=0;j<= teamStats.get(i).getTopStats().size()-1;j++) {
        			if(teamStats.get(i).getTopStats().get(j).getHeader().equalsIgnoreCase(Value)) {
        				//System.out.println(teamStats.get(i).getTopStats().toString());
        				for(PlayerStats ps : teamStats.get(i).getTopStats().get(j).getPlayersStats()) {
        					top_stats.add(ps);
        				}
		        	}
        		}
	        }
	        
	        Collections.sort(top_stats,new FootballFunctions.PlayerStatsComparator());
	        
	        for(int m=0; m<= top_stats.size() - 1; m++) {
	        	row = row + 1;
	        	if(row <= 5) {
					for(Team tm : team) {
						if(top_stats.get(m).getTeam_name().equalsIgnoreCase(tm.getTeamName5())) {
							team_name = tm.getTeamName3();
						}
					}
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
							+ "txt_Name*GEOM*TEXT SET " + top_stats.get(m).getFirst_name().toUpperCase() + "\0",print_writer);
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
							+ "txt_TeamName*GEOM*TEXT SET " + team_name + "\0",print_writer);
					
					if(Value.toUpperCase().equalsIgnoreCase("HIGHEST DISTANCE")) {
						DecimalFormat df_bo = new DecimalFormat("0.0");
						double df = Double.valueOf(top_stats.get(m).getValue())/1000;
						df_bo.setRoundingMode(RoundingMode.UP); 
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
								df_bo.format(df) + "\0",print_writer);
					}else if(Value.toUpperCase().equalsIgnoreCase("TEAM TOP SPEED")) {
						DecimalFormat df_ts = new DecimalFormat("0.0");
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
								df_ts.format(Double.valueOf(top_stats.get(m).getValue())) + "\0",print_writer);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$txt_StatValue*GEOM*TEXT SET " + 
								top_stats.get(m).getValue() + "\0",print_writer);
					}
	        	}
			}
//	        FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		}
	}
	public void populateTopStats(List<PrintWriter> print_writer,String viz_scene,String Value,List<Player> players,List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row = 0;
			
			data = new FootballData();
			ArrayList<com.football.containers.Players> plyr = new ArrayList<com.football.containers.Players>();
			
			EuroLeague.LiveData(data);
			
			for(com.football.containers.Team teams : data.getTeam()) {
				for(com.football.containers.Players player : teams.getTeamPlayer()) {
					players.stream()
				    .filter(ply -> ply.getPlayerAPIId()!=null && player.getId()!=null && ply.getPlayerAPIId().equalsIgnoreCase(player.getId().trim()))
				    .findAny()
				    .ifPresent(matchingPlayer -> player.setName(matchingPlayer.getTicker_name()));

					plyr.add(player);
				}
			}
	        
			switch(Value.toUpperCase()) {
			case "TOUCHES":
				Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getTouches(), p1.getTouches()));
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"TOUCHES" + "\0",print_writer);
				break;
			case "DUEL WON":
				Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getDuelWon(), p1.getDuelWon()));
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"DUELS WON" + "\0",print_writer);
				break;
			case "SUCCESSFUL DRIBBLE":
				Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getWonContest(), p1.getWonContest()));
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"SUCCESSFUL DRIBBLES" + "\0",print_writer);
				break;
			case "RECOVERIES":
				Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getBallRecovery(), p1.getBallRecovery()));
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"RECOVERIES" + "\0",print_writer);
				break;
			case "AERIAL DUELS WON":
				Collections.sort(plyr, (p1, p2) -> Integer.compare(p2.getAerialWon(), p1.getAerialWon()));
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_Header*GEOM*TEXT SET " + 
						"AERIAL DUELS WON" + "\0",print_writer);
				break;
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$Out$Select_EventLogo*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$HeaderGrp$txt_SubHeader*GEOM*TEXT SET " + "" + "\0",print_writer);
			
			for(int m=0; m<= plyr.size() - 1; m++) {
	        	row = row + 1;
	        	if(row <= 5) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
							+ "txt_Name*GEOM*TEXT SET " + plyr.get(m).getName().toUpperCase() + "\0",print_writer);
					
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
							+ "txt_TeamName*GEOM*TEXT SET " + "" + "\0",print_writer);
					
					for(Player player : players) {
						if(player.getPlayerAPIId() != null && player.getPlayerAPIId().equalsIgnoreCase(plyr.get(m).getId())) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
									+ "txt_TeamName*GEOM*TEXT SET " + team.get(player.getTeamId()-1).getTeamName1() + "\0",print_writer);
						}
					}
					
					switch(Value.toUpperCase()) {
					case "TOUCHES":
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
								+ "txt_StatValue*GEOM*TEXT SET " + plyr.get(m).getTouches() + "\0",print_writer);
						break;
					case "DUEL WON":
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
								+ "txt_StatValue*GEOM*TEXT SET " + plyr.get(m).getDuelWon() + "\0",print_writer);
						break;
					case "SUCCESSFUL DRIBBLE":
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
								+ "txt_StatValue*GEOM*TEXT SET " + plyr.get(m).getWonContest() + "\0",print_writer);
						break;
					case "RECOVERIES":
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
								+ "txt_StatValue*GEOM*TEXT SET " + plyr.get(m).getBallRecovery() + "\0",print_writer);
						break;
					case "AERIAL DUELS WON":
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$AllDataGrp$DataAll$Side" + which_side + "$LineUp$Row" + row + "$Dehighlight$StatGrp$"
								+ "txt_StatValue*GEOM*TEXT SET " + plyr.get(m).getAerialWon() + "\0",print_writer);
						break;
					}
					
	        	}
			}
			
	        FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.000 \0",print_writer);
		}
	}
	
	public void populateTeamFixture(List<PrintWriter> print_writer,String viz_scene,int TeamId,Match match, String broadcaster,List<Team> team, List<Fixture> fixture) throws InterruptedException, IOException 
	{

		int row_id = 0;
		String newDate = "";
		
		String[] dateSuffix = {
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
				
				"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
				
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
				
				"th", "st"
		};
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$Header$Dataall$sUBgRP$txt_Sub*GEOM*"
				+ "TEXT SET " + "FIXTURES"  + "\0",print_writer);
		for(int i=1;i<=3;i++) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + i + "*ACTIVE SET 0\0",print_writer);	
		}
		for(Fixture fix : fixture) {
			if(fix.getHometeamid() == TeamId) {
				row_id = row_id + 1; 
				newDate = fix.getDate().split("-")[0];
				if(Integer.valueOf(newDate) < 10) {
					newDate = newDate.replaceFirst("0", "");
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$NameGrp$txt_Fixture*GEOM*"
						+ "TEXT SET " + " " + "\0",print_writer);
				
				for(Team tm : team) {
					if(tm.getTeamId() == fix.getHometeamid()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$Header$Dataall$txt_Name*GEOM*"
								+ "TEXT SET " + tm.getTeamName1()  + "\0",print_writer);
					}
					
					if(tm.getTeamId() == fix.getAwayteamid()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$NameGrp$txt_Name*GEOM*"
								+ "TEXT SET " + "vs " + tm.getTeamName1()  + "\0",print_writer);
					}
				}
				
				if(fix.getMargin() != null && !fix.getMargin().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$StatValueGrp$txt_Value*GEOM*"
							+ "TEXT SET " + fix.getMargin()  + "\0",print_writer);
				}else {
					String Date = "";
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, +1);
					Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
					
					if(fix.getDate().equalsIgnoreCase(Date)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
								"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + "TOMORROW" + "\0",print_writer);
					}else {
						cal.add(Calendar.DATE, -1);
						Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
						
						if(fix.getDate().equalsIgnoreCase(Date)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
									"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + "TONIGHT" + "\0",print_writer);
						}else {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
									"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " " + 
									Month.of(Integer.valueOf(fix.getDate().split("-")[1]))  + "\0",print_writer);
						}
					}
				}
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "*ACTIVE SET 1\0",print_writer);	

			}else if(fix.getAwayteamid() == TeamId) {
				row_id = row_id + 1; 
				newDate = fix.getDate().split("-")[0];
				if(Integer.valueOf(newDate) < 10) {
					newDate = newDate.replaceFirst("0", "");
				}
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$NameGrp$txt_Fixture*GEOM*"
						+ "TEXT SET " + " " + "\0",print_writer);
				
				for(Team tm : team) {
					
					if(tm.getTeamId() == fix.getAwayteamid()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$Header$Dataall$txt_Name*GEOM*"
								+ "TEXT SET " + tm.getTeamName1()  + "\0",print_writer);
					}
					
					if(tm.getTeamId() == fix.getHometeamid()) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$NameGrp$txt_Name*GEOM*"
								+ "TEXT SET " + "vs " + tm.getTeamName1()  + "\0",print_writer);
					}
				}
				if(fix.getMargin() != null && !fix.getMargin().isEmpty()) {
					FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "$StatGrp$StatValueGrp$txt_Value*GEOM*"
							+ "TEXT SET " + fix.getMargin()  + "\0",print_writer);
				}else {
					String Date = "";
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, +1);
					
					Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
					
					if(fix.getDate().equalsIgnoreCase(Date)) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
								"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + "TOMORROW" + "\0",print_writer);
					}else {
						cal.add(Calendar.DATE, -1);
						Date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
						
						if(fix.getDate().equalsIgnoreCase(Date)) {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
									"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + "TONIGHT" + "\0",print_writer);
						}else {
							FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + 
									"$StatGrp$StatValueGrp$txt_Value*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " " + 
									Month.of(Integer.valueOf(fix.getDate().split("-")[1]))  + "\0",print_writer);
						}
					}
				}
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Top5All$DataAll$ScorrerTeam1$Row" + row_id + "*ACTIVE SET 1\0",print_writer);	
			}
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.320 \0",print_writer);
		
	}
	public void populatePlayoffs(List<PrintWriter> print_writer,String viz_scene,List<Playoff> playoffs,List<Team> team,List<VariousText> vt, String broadcaster,Match match) throws InterruptedException, IOException 
	{
		int row_id = 0;
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$txt_Header*GEOM*TEXT SET " + "ROAD TO THE FINAL"  + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "KNOCKOUT STAGE"  + "\0",print_writer);
		for(VariousText vartext : vt) {
			if(vartext.getVariousType().equalsIgnoreCase("PLAYOFFSFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.YES)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + vartext.getVariousText()  + "\0",print_writer);
			}else if(vartext.getVariousType().equalsIgnoreCase("PLAYOFFSFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(FootballUtil.NO)) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET \0",print_writer);
			}
		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET \0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayOffs$LogoGrp$LogoImageGrp1$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All_FullFRames$PlayOffs$LogoGrp$LogoImageGrp2$img_Badges*TEXTURE*IMAGE SET "+ logo_path + "event" + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group1$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + "SEMI-FINAL 1"  + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group2$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + "SEMI-FINAL 2"  + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group3$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + "FINAL"  + "\0",print_writer);
		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group2$txt_Date*GEOM*TEXT SET " + ""  + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group3$txt_Date*GEOM*TEXT SET " + ""  + "\0",print_writer);
		
		for(int i=0;i<=playoffs.size()-1;i++) {
			row_id ++;
			
			Integer Date = 0;
			
			if(playoffs.get(i).getDate() != null) {
				Date = Integer.valueOf(playoffs.get(i).getDate().split("-")[0]);
				int monthNumber = Integer.parseInt(playoffs.get(i).getDate().split("-")[1]); // → 10
				Month month = Month.of(monthNumber); 
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$txt_Date*GEOM*TEXT SET " + ordinal(Date) + " " 
						+ month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH).toUpperCase() + "\0",print_writer);
			}else if(playoffs.get(i).getTime() != null) {
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$txt_Date*GEOM*TEXT SET " 
						+ playoffs.get(i).getTime() + "\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$txt_Date*GEOM*TEXT SET \0",print_writer);
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " 
					+ playoffs.get(i).getTeam1().toUpperCase()  + "\0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " 
					+ playoffs.get(i).getTeam2().toUpperCase()  + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + playoffs.get(i).getPlayoffType().toUpperCase()  + "\0",print_writer);
			
			
			if(playoffs.get(i).getMargin() != null) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$BaseAll$select_Score*FUNCTION*Omo*vis_con SET 1\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$Team$select_Score*FUNCTION*Omo*vis_con SET 1\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$BaseAll$select_Score*FUNCTION*Omo*vis_con SET 1\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$Team$select_Score*FUNCTION*Omo*vis_con SET 1\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " 
						+ playoffs.get(i).getMargin().split("-")[0]  + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " 
						+ playoffs.get(i).getMargin().split("-")[1]  + "\0",print_writer);
			}else {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$BaseAll$select_Score*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData1$TeamAll$Team$select_Score*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$BaseAll$select_Score*FUNCTION*Omo*vis_con SET 0\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PlayOffs$AllData$Group" + row_id + "$TeamData2$TeamAll$Team$select_Score*FUNCTION*Omo*vis_con SET 0\0",print_writer);
			}
		}

		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PlayOffs_In 2.700 \0",print_writer);
		
	}
	public void populateMatchResult(List<PrintWriter> print_writer,String viz_sence_path,int match_number,List<Fixture> fix,List<Team> team,List<Ground> ground,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0",print_writer);
		
		for(Team TM : team) {
			if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {

				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
			}
			if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0",print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0",print_writer);
			}
		}
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "" + "\0",print_writer);
		
		if(fix.get(match_number - 1).getMargin() != null) {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + fix.get(match_number - 1).getMargin() + "\0",print_writer);
		}else {
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + "" + "\0",print_writer);
		}
		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + "\0",print_writer);
		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 2.340 \0",print_writer);
		
	}
	public void populateRoadToFinal(List<PrintWriter> print_writer,String viz_sence_path,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0",print_writer);
		
		
		int row_no_1=0,row_no_2=0,omo=0,omo1=0,l=4;
		String cout = "",cout1="";
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "ROAD TO THE FINAL" + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "GROUP STAGE" + "\0",print_writer);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "1" + "\0",print_writer);
		TimeUnit.MILLISECONDS.sleep(l);
		
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$"
				+ "GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP " + (match.getHomeTeam().getTeamGroup()==null?"":match.getHomeTeam().getTeamGroup()) + "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$"
				+ "GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP " +  ( match.getAwayTeam().getTeamGroup() ==null?"": match.getAwayTeam().getTeamGroup() )+ "\0",print_writer);
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				"GROUP WINNERS QUALIFIED FOR THE SEMI-FINALS" + "\0",print_writer);
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no_1 = row_no_1 + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 +
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + " \0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0",print_writer);
			
			for(Team tm : team) {
				if(tm.getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName())) {
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" 
								+ row_no_1 + "$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" 
								+ row_no_1 + "$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1() + " (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table1.get(i).getPlayed() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table1.get(i).getWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table1.get(i).getLost() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table1.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table1.get(i).getGD() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table1.get(i).getPoints() + "\0",print_writer);
			
		}
		
		for(int i = 0; i <= point_table2.size() - 1 ; i++) {
			row_no_2 = row_no_2 + 1;
			
			if(match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName())){
				omo1=1;
				cout1="$Highlight";
			}else {
				omo1=0;
				cout1="$Dehighlight";
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 +
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo1 + " \0",print_writer);
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0",print_writer);
			
			for(Team tm : team) {
				if(tm.getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName())) {
					
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row"
								+ row_no_2 + "$SelectType" + cout1 + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1() + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" 
								+ row_no_2 + "$SelectType" + cout1 + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1() + " (Q)" + "\0",print_writer);
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table2.get(i).getPlayed() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table2.get(i).getWon() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table2.get(i).getLost() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table2.get(i).getDrawn() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table2.get(i).getGD() + "\0",print_writer);
			
			FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout1 + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table2.get(i).getPoints() + "\0",print_writer);
			

		}
		FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0",print_writer);
		
	}
	public List<String> checkForData(List<Team> teams, List<Player> players, int Id, Match match) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		
		List<String> data_check = new ArrayList<String>();
		
//		data = new FootballData();
//		EuroLeague.LiveData(data);
		
//		com.football.containers.Players plyr = new com.football.containers.Players();
//		
//		for(com.football.containers.Team teams : data.getTeam()) {
//			for(com.football.containers.Players plyer : teams.getTeamPlayer()) {
//				if(player.getPlayerAPIId() != null && plyer.getId() != null && plyer.getId().equalsIgnoreCase(player.getPlayerAPIId())) {
//					plyr = plyer;
//					break;
//				}
//			}
//		}		

		Player player = null;
		Team team = null;
		
		player = players.stream().filter(plyr -> plyr.getPlayerId() == Id).findAny().orElse(null);
		for(Team tm : teams) {
			if(tm.getTeamId() == player.getTeamId()) {
				team = tm;
				break;
			}
		}
		
		com.football.model.Football.Team.Player plyr = new com.football.model.Football.Team.Player();
		
		int teamId = Integer.parseInt(team.getTeamApiId());
		int playerId = Integer.parseInt(player.getPlayerAPIId());

		for (com.football.model.Football.Team t : IndexController.football.getTeams()) {
		    if (t.getTeamID() == teamId) {
		        for (com.football.model.Football.Team.Player p : t.getPlayers()) {
		            if (p.getPlayerID() == playerId) {
		                plyr = p;
		                break;
		            }
		        }
		        break;
		    }
		}

		switch(player.getRole()) {
		case "Goalkeeper":
			//data_check.add("MINUTES PLAYED : " + plyr.getMinsPlayed());
			data_check.add("SAVES : " + plyr.getSaves());
			data_check.add("TACKLE : " + plyr.getTackles());
			data_check.add("TOUCHES : " + plyr.getTouches());
			break;
		case "Defender":
			//data_check.add("MINUTES PLAYED : " + plyr.getMinsPlayed());
			data_check.add("CROSSES : " + plyr.getCrosses());
			data_check.add("INTERCEPTIONS : " + plyr.getInterceptions());
			data_check.add("TACKLES : " + plyr.getTackles());
			break;
		case "MidFielder":
			//data_check.add("MINUTES PLAYED : " + plyr.getMinsPlayed());
			data_check.add("PASSES : " + plyr.getPasses());
			data_check.add("DUEL WON : " + plyr.getDuelsWon());
			data_check.add("TACKLE : " + plyr.getTackles());
			break;
		case "Forward":
			//data_check.add("MINUTES PLAYED : " + plyr.getMinsPlayed());
			data_check.add("GOALS : " + plyr.getGoals());
			data_check.add("ASSIST : " + plyr.getAssist());
			data_check.add("SHOT ON TARGET : " + plyr.getOnTarget());
			break;
		}
		return data_check;
	}
	
	public static String ordinal(int i) {
	    int mod100 = i % 100;
	    int mod10 = i % 10;
	    if(mod10 == 1 && mod100 != 11) {
	        return i + "st";
	    } else if(mod10 == 2 && mod100 != 12) {
	        return i + "nd";
	    } else if(mod10 == 3 && mod100 != 13) {
	        return i + "rd";
	    } else {
	        return i + "th";
	    }
	}
}