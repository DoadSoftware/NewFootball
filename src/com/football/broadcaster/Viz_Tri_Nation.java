package com.football.broadcaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import com.football.model.*;
import com.football.service.FootballService;
import com.football.util.FootballFunctions;
import com.football.util.FootballUtil;
import com.opencsv.exceptions.CsvException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.containers.Scene;
import com.football.containers.ScoreBug;

public class Viz_Tri_Nation extends Scene{
	
	public String session_selected_broadcaster = "VIZ_TRI_NATION";
	
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	public String logo_path = "IMAGE*/Default/Essentials/Badges/";
	public String logo2_path = "IMAGE*/Default/Design/";
	private String colors_path = "C:\\Images\\Tri_Nation\\Colours\\";
	private String photos_path = "C:\\Images\\Tri_Nation\\Photos\\";
	private String image_path = "C:\\Sports\\Football\\Statistic\\Match_Data\\";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	public ObjectMapper objectMapper = new ObjectMapper();
	
	public Viz_Tri_Nation() {
		super();
	}
	
	public ScoreBug updateScoreBug(PrintWriter print_writer,List<Scene> scenes, Match match,FootballService footballService) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, print_writer, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
			scorebug = populateExtraTime(true,scorebug,print_writer,null,match,session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(PrintWriter print_writer,String whatToProcess,Match match,Clock clock, FootballService footballService,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException, SAXException, ParserConfigurationException{
		
		if (which_graphics_onscreen == "PENALTY")
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
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD":
		case "POPULATE-SCOREBUG-CARD": case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON":
		case "POPULATE-FF-MATCHID": case "POPULATE-FF-PROMO": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-FF-PLAYINGXI": case "POPULATE-HOMESUB":
		case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB": case "POPULATE-FF-MATCHSTATS":
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-LT-MATCHID": case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-L3-NAMESUPER-CARD":
		case "POPULATE-L3-SUBSTITUTE": case "POPULATE-OFFICIALS": case "POPULATE-L3-HEATMAP": case "POPULATE-L3-TOP_STATS":
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD": case "POPULATE-SCOREBUG-CARD":
			case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON":
			case "POPULATE-HOMESUB": case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB":
				break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Reset START \0");
				break;	
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,print_writer, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG_STATS":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_Out START \0");
					TimeUnit.MILLISECONDS.sleep(500);
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,print_writer,Integer.valueOf(valueToProcess.split(",")[2]),Integer.valueOf(valueToProcess.split(",")[3]),
							match,session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_In START \0");
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,print_writer,Integer.valueOf(valueToProcess.split(",")[2]),Integer.valueOf(valueToProcess.split(",")[3]),
							match,session_selected_broadcaster);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_In START \0");
				}
				break;
			case "POPULATE-SCOREBUG-CARD":	
				if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0");
					TimeUnit.MILLISECONDS.sleep(500);
					
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_In START \0");
				}else {
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_In START \0");
				}
				break;
			case "POPULATE-SCOREBUG-SUBS":
				scorebug.setScorebug_subs(valueToProcess.split(",")[2]);
				populateScorebugSubs(scorebug,print_writer, Integer.valueOf(valueToProcess.split(",")[1]), footballService.getAllPlayer(), match, 
						session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_In START \0");
				break;
			case "POPULATE-RED_CARD":
				populateRedcard(false,scorebug,print_writer,Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),
						match,session_selected_broadcaster);
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
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(print_writer, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),footballService.getTeams(),
						footballService.getFixtures(),footballService.getGrounds(),match , session_selected_broadcaster);
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
			case "POPULATE-L3-SUBSTITUTE":
				populateSubstitute(print_writer, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						footballService.getAllPlayer(),footballService.getTeams(), match, session_selected_broadcaster);
				break;	
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],footballService.getFormations(), footballService.getTeams(),
						match, session_selected_broadcaster);
				break;
			case "POPULATE-HOMESUB":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png In 0.020 LineUp$Base_In 2.500 LineUp$Team1$DataIn 2.000 LineUp$Team1$Change 2.100 \0");
				break;
			case "POPULATE-AWAYXI":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png In 0.020 LineUp$Base_In 2.500 LineUp$Team1$DataIn 0.000 LineUp$Team2$DataIn 2.000 \0");
				break;
			case "POPULATE-AWAYSUB":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png In 0.020 LineUp$Base_In 2.500 LineUp$Team2$DataIn 2.000 LineUp$Team2$Change 2.100 \0");
				break;	
			case "POPULATE-L3-HEATMAP":
				populateHeatMapPeakDistance(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3] ,
						Integer.valueOf(valueToProcess.split(",")[4]),footballService.getAllPlayer(),match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-TOP_STATS":
				populateTopStats(print_writer, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3] ,
						FootballFunctions.getTopStatsDatafromXML(match),footballService.getAllPlayer(),match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(print_writer, valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(print_writer, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-OFFICIALS":
				populateOfficials(print_writer, valueToProcess.split(",")[1],footballService.getOfficials(),match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(print_writer,valueToProcess.split(",")[1], footballService,match,clock, session_selected_broadcaster);
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
			
		case "ANIMATE-IN-SCOREBUG": case "ANIMATE-IN-SPONSOR": case "ANIMATE-IN-SUBS_CHANGE_ON":
		case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-PROMO": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-HOMESUB": case "ANIMATE-IN-AWAYXI": case "ANIMATE-IN-AWAYSUB":
		case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-MATCHSTATS":
		case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-NAMESUPER_CARD": case "ANIMATE-IN-NAMESUPER": case "ANIMATE-IN-NAMESUPERDB":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-OFFICIALS": case "ANIMATE-IN-HEATMAP": case "ANIMATE-IN-TOP_STATS":
		case "CLEAR-ALL": 
		case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-OUT-EXTRA_TIME": case "ANIMATE-OUT-SCOREBUG_STAT": case"ANIMATE-OUT-RED_CARD": case "ANIMATE-OUT-SPONSOR":
		case "ANIMATE-OUT": 
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-SCOREBUG":
				AnimateInGraphics(print_writer, "SCOREBUG");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-TEST":
				AnimateInGraphics(print_writer, "TEST");
				which_graphics_onscreen = "TEST";
				break;
			case "ANIMATE-IN-SPONSOR":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_In START \0");
				break;
			case "ANIMATE-IN-SUBS_CHANGE_ON":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Change START \0");
				break;
			case "ANIMATE-IN-MATCHID":
				AnimateInGraphics(print_writer, "MATCHID");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				AnimateInGraphics(print_writer, "MATCHSTATUS");
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				AnimateInGraphics(print_writer, "MATCHSTATS");
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PROMO":
				AnimateInGraphics(print_writer, "MATCHSINGLEPROMO");
				which_graphics_onscreen = "MATCHSINGLEPROMO";
				break;
			case "ANIMATE-IN-HEATMAP":
				AnimateInGraphics(print_writer, "HEATMAP");
				which_graphics_onscreen = "HEATMAP";
				break;
			case "ANIMATE-IN-TOP_STATS":
				AnimateInGraphics(print_writer, "TOP_STATS");
				which_graphics_onscreen = "TOP_STATS";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				AnimateInGraphics(print_writer, "NAMESUPER_CARD");
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				AnimateInGraphics(print_writer, "NAMESUPER");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				AnimateInGraphics(print_writer, "NAMESUPERDB");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-OFFICIALS":
				AnimateInGraphics(print_writer, "OFFICIALS");
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				AnimateInGraphics(print_writer, "SUBSTITUTE");
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-HOMESUB":
				AnimateInGraphics(print_writer, "HOMESUB");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-AWAYXI":
				AnimateInGraphics(print_writer, "AWAYXI");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-AWAYSUB":
				AnimateInGraphics(print_writer, "AWAYSUB");
				which_graphics_onscreen = "PLAYINGXI";
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
			case "ANIMATE-OUT-SPONSOR":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_Out START \0");
				break;
			case "ANIMATE-OUT-SCOREBUG_STAT":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_Out START \0");
					scorebug.setLast_scorebug_stat("");scorebug.setScorebug_stat("");
				}else if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0");
					scorebug.setLast_scorebug_card_goal("");scorebug.setScorebug_card_goal("");
				}else if(scorebug.getScorebug_subs() != null || !scorebug.getScorebug_subs().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_Out START \0");
					scorebug.setLast_scorebug_subs("");scorebug.setScorebug_subs("");
				}
				break;
			case"ANIMATE-OUT-RED_CARD":	
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_Out START \0");
				break;
			case "ANIMATE-OUT-EXTRA_TIME":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_Out START \0");
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": 
				case "SUBSTITUTE": case "MATCHSINGLEPROMO": case "MATCHSTATUS": case "OFFICIALS": case "HEATMAP": case "MATCHSTATS": case "TOP_STATS":
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
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP": case "TOP_STATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			break;
		case "MATCHID": case "MATCHSINGLEPROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_In START \0");
			break;
		case "MATCHSTATUS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchStats_In START \0");
			break;
		case "MATCHSTATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchScorers_In START \0");
			break;
		case "PLAYINGXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Base_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$DataIn START \0");
			break;
		case "HOMESUB":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$Change START \0");
			break;
		case "AWAYXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$DataOut START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$DataIn START \0");
			break;
		case "AWAYSUB":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$Change START \0");
			break;	
		}
	}
	public void AnimateOutGraphics(PrintWriter print_writer, String whichGraphic) throws IOException, InterruptedException {
		switch (whichGraphic.toUpperCase()) {
		case "SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0");
			break;
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP": case "TOP_STATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHID": case "MATCHSINGLEPROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHSTATUS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchStats_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHSTATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchScorers_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "PLAYINGXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$DataOut START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Base_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		}
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$ScoreGrp$img_TeamTextColour$txt_Score*GEOM*TEXT SET " + 
					match.getHomeTeamScore() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$ScoreGrp$img_TeamTextColour$txt_Score*GEOM*TEXT SET " + 
					match.getAwayTeamScore() + "\0");
			
			if(is_this_updating == false) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$ScoreGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$ScoreGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + FootballUtil.PNG_EXTENSION + "\0");
				
				if(match.getHomeTeamJerseyColor().equalsIgnoreCase("WHITE")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$ScoreGrp$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp1$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$ScoreGrp$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp1$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
				}
				
				if(match.getAwayTeamJerseyColor().equalsIgnoreCase("WHITE")) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$ScoreGrp$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp2$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
				}else {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$ScoreGrp$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp2$img_TeamTextColour*TEXTURE*IMAGE SET " + 
							colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
				}
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$Sponsor$HeroTrination*TEXTURE*IMAGE SET " + 
						logo2_path + "HeroLogo" + "\0");
			}
		}
		return scorebug;
	}
	public ScoreBug populateScoreBugStats(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,int Homedata,int Awaydata ,Match match, String selectedbroadcaster) 
			throws MalformedURLException, IOException, CsvException, InterruptedException {
		
		
		switch(scorebug.getScorebug_stat().toUpperCase()) {
		case FootballUtil.YELLOW:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + "YELLOW CARD" + "\0");
			break;
		case FootballUtil.RED:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + "RED CARD" + "\0");
			break;
		case FootballUtil.OFF_SIDE:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + "OFFSIDES" + "\0");
			break;
		case FootballUtil.SHOTS:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + FootballUtil.SHOTS + "\0");
			break;
		case FootballUtil.POSSESSION:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + FootballUtil.POSSESSION + " %" + "\0");
			break;
		case FootballUtil.SHOTS_ON_TARGET:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + "SHOT ON TARGET" + "\0");
			break;
		case FootballUtil.CORNERS:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + FootballUtil.CORNERS + "\0");
			break;
		case FootballUtil.TACKLES:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$txt_Name*GEOM*TEXT SET " + FootballUtil.TACKLES + "\0");
			break;
		}
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp1$img_TeamTextColour$txt_StatValue*GEOM*TEXT SET " + 
				Homedata + "\0");
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$StatsGrp$TeamGrp2$img_TeamTextColour$txt_StatValue*GEOM*TEXT SET " + 
				Awaydata + "\0");
		
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateRedcard(boolean is_this_updating, ScoreBug scorebug, PrintWriter print_writer,int Homedata,int Awaydata, Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp1$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " + Homedata + "\0");
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$MainScorePart$TeamGrp2$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " + Awaydata + "\0");
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_In START \0");
		
		return scorebug;
	}
	public ScoreBug populateExtraTime(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeAll$txt_AddedMinute*GEOM*TEXT SET " + "+" +  time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0");
		}
		return scorebug;
	}
	public ScoreBug populateExtraTimeBoth(boolean is_this_updating,ScoreBug scorebug,PrintWriter print_writer,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeAll$txt_AddedMinute*GEOM*TEXT SET " + "+" + time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0");
		}
		return scorebug;
	}
	public ScoreBug populateScorebugCard(ScoreBug scorebug,PrintWriter print_writer,int TeamId,int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 200;
			String team_name4 = "" ,team_name = "";
			if(TeamId == match.getHomeTeamId()) {
				team_name4 = match.getHomeTeam().getTeamName4();
				team_name = match.getHomeTeam().getTeamName1();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
								hs.getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
								hs.getSurname() + "\0");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
								hsub.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
								hsub.getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
								hsub.getSurname() + "\0");
					}
				}
			}
			else {
				team_name4 = match.getAwayTeam().getTeamName4();
				team_name = match.getAwayTeam().getTeamName1();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
								as.getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
								as.getSurname() + "\0");
						
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
								asub.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
								asub.getFirstname() + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$PlayerGrpAll$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
								asub.getSurname() + "\0");
					}
				}
			}
			
			switch(scorebug.getScorebug_card_goal().toUpperCase())
			{
			case "YELLOW_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$TeamName$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "RED_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$TeamName$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$TeamName$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "GOAL":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$SelectCardType*ACTIVE SET 0 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$CardsAll$TeamName$txt_TeamName*GEOM*TEXT SET " + "GOAL SCORER" + " , " + team_name4 + "\0");
				break;
			}
			scorebug.setLast_scorebug_card_goal(scorebug.getScorebug_card_goal().toUpperCase());
		}
		return scorebug;
	}
	public ScoreBug populateScorebugSubs(ScoreBug scorebug,PrintWriter print_writer,int TeamId,List<Player> plyr, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 200;
			List<Event> evnt = new ArrayList<Event>();
			
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
			if(match.getHomeTeamId() == TeamId) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$TeamName$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
			}else if(match.getAwayTeamId() == TeamId) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$TeamName$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
			}
			switch(scorebug.getScorebug_subs().toUpperCase())
			{
			case "SINGLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getSurname() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getSurname() + "\0");
				
				break;
			case "DOUBLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getSurname() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getSurname() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getSurname() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getSurname() + "\0");
				
				break;
				
			case "TRIPLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "3" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber*FUNCTION*Omo*vis_con SET " + "3" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getSurname() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player1$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getSurname() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getSurname() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player2$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getSurname() + "\0");
				

				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player3$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player3$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$OutPlayerGrpAll$SelectPlayerNumber$Player3$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getSurname() + "\0");
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player3$PlayerNumber$HomeTeamName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player3$PlayeNameGrp$FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$SubstitutesAll$PlayersAll$InPlayerGrpAll$SelectPlayerNumber$Player3$PlayeNameGrp$LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getSurname() + "\0");
				
				break;
			}
			
		}
		
		scorebug.setLast_scorebug_subs(scorebug.getScorebug_subs().toUpperCase());
		return scorebug;
	}
	
	public void populateMatchId(PrintWriter print_writer,String viz_scene, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$TeamBadgeGrp1$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TopHeader*GEOM*TEXT SET " + match.getTournament() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TeamName1*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TeamName2*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$SubHeadOut$SubHeadIn$txt_Venue*GEOM*TEXT SET " + "LIVE FROM "+ match.getVenueName().toUpperCase() + "\0");
			
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.100 \0");
		}
	}
	public void populateMatchPromoSingle(PrintWriter print_writer,String viz_scene, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TopHeader*GEOM*TEXT SET " + match.getTournament() + "\0");
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$TeamBadgeGrp1$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
							TM.getTeamName2().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TeamName1*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
							TM.getTeamName2().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$txt_TeamName2*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");	
				}
			}
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$SubHeadOut$SubHeadIn$txt_Venue*GEOM*TEXT SET " + fix.get(match_number-1).getDate() + 
					"(" + fix.get(match_number-1).getTime() + ") -" + " LIVE FROM "+ match.getVenueName().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.100 \0");	
		}
	}
	public void populateMatchStatus(PrintWriter print_writer,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + match.getMatchIdent().toUpperCase() + " - " + match.getTournament() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$HeaderOut$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$TeamBadgeGrp1$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$TeamGrp1$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$TeamGrp2$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player1$Out$In$txt_Stathead*GEOM*TEXT SET " + "POSSESSION (%)" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player2$Out$In$txt_Stathead*GEOM*TEXT SET " + FootballUtil.SHOTS + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player3$Out$In$txt_Stathead*GEOM*TEXT SET " + FootballUtil.SHOTS_ON_TARGET.replace("_", " ") + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player4$Out$In$txt_Stathead*GEOM*TEXT SET " + FootballUtil.YELLOW + " CARDS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player5$Out$In$txt_Stathead*GEOM*TEXT SET " + FootballUtil.RED + " CARDS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player6$Out$In$txt_Stathead*GEOM*TEXT SET " + FootballUtil.CORNERS + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player7$Out$In$txt_Stathead*GEOM*TEXT SET " + "OFFSIDES" + "\0");
			
			
			String text_to_return = "";
			ArrayList<String> Stats = new ArrayList<String>();
			try (BufferedReader br = new BufferedReader(new FileReader(FootballUtil.FOOTBALL_DIRECTORY + "Stats.txt"))) {
				while((text_to_return = br.readLine()) != null) {
				    Stats.add(text_to_return);
				}
			}
		
		    for(int i=0;i<=Stats.size()-1;i++) {
		    	//System.out.println("VALUE : " + Stats.get(i));
		    	print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player" + (i+1) + "$Out$In$HomeValueGrp"
		    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[0] + "\0");
		    	print_writer.println("-1 RENDERER*TREE*$Main$All$MatchStats$DataAll$DataOut$LineUpData$Player" + (i+1) + "$Out$In$AwayValueGrp"
		    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[2] + "\0");
		    }
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 1.700 \0");
		}
	}
	public void populateMatchStats(PrintWriter print_writer,String viz_scene,FootballService footballService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			
			//int l = 4;
			//String Home_player="",Away_player="";
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + match.getMatchIdent().toUpperCase() + " - " + match.getTournament() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamBadgeGrp1$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamGrp1$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamGrp2$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0");
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
			
			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 0) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 0 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 0 \0");
			}else if(match.getHomeTeamScore() == 1 && match.getAwayTeamScore() == 0) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 1 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 0 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*FUNCTION*Grid*num_row SET " + home_stats.size() + "\0");
			}else if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 1) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 0 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 1 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*FUNCTION*Grid*num_row SET " + away_stats.size() + "\0");
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 1 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 1 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*FUNCTION*Grid*num_row SET " + home_stats.size() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*FUNCTION*Grid*num_row SET " + away_stats.size() + "\0");
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i<=6) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1$Scorer" + (i+1) + 
							"$Out$In$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i<=6) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2$Scorer" + (i+1) + 
							"$Out$In$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 1.700 \0");
		}
	}
	public void populatePlayingXI(PrintWriter print_writer,String viz_scene, int TeamId,String Type,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int row_id = 0,row_id_sub = 0,l=4;
			print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach*ACTIVE SET 0 \0");
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + 
						match.getMatchIdent() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$HeaderOut$txt_Header*GEOM*TEXT SET " + 
						team.get(TeamId-1).getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$txt_SubHeader"
						+ "*GEOM*TEXT SET " + "STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$TeamBadgeGrp$BadgeAll$img_Badge"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(TeamId-1).getTeamName2().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 0 \0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Base$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp2$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
				
				if(team.get(TeamId-1).getTeamCoach() == null) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							"" + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach().split(" ")[0] + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach().split(" ")[1] + "\0");
				}
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
					
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							hs.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							hs.getFirstname() + "\0");
					
					if(hs.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								hs.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_Jersey"
								+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
						if(match.getHomeTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType$JerseyAll$txt_Number*GEOM*TEXT SET " + 
								hs.getJersey_number() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								hs.getTicker_name() + "\0");
						break;
					case "WITH_IMAGE":
						
						if(team.get(match.getHomeTeamId()-1).getTeamCoach() != null) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach$Out$In$txt_Name*GEOM*TEXT SET " + 
									team.get(match.getHomeTeamId()-1).getTeamCoach() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach$Out$In$CoachImageGrp$img_Coach"
									+ "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + "//" + team.get(match.getHomeTeamId()-1).getCoachPhotoName() + FootballUtil.PNG_EXTENSION + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach*ACTIVE SET 1 \0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$PlayerImageGrp$img_Player"
								+ "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + "//" + hs.getPhoto() + FootballUtil.PNG_EXTENSION + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								hs.getTicker_name() + "\0");
						break;
					}
					
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				for(Player hsub : match.getHomeSubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							hsub.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							hsub.getFirstname() + "\0");
					
					if(hsub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								hsub.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					
					if(hsub.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				
				
			//-------------------------------------------------------------------------------------------------------------------------------------------------
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + 
						match.getMatchIdent() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$HeaderOut$txt_Header*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$txt_SubHeader"
						+ "*GEOM*TEXT SET " + "STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$TeamBadgeGrp$BadgeAll$img_Badge"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getAwayTeamId() -1).getTeamName2().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 0 \0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp2$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
				
				if(team.get(match.getAwayTeamId() -1).getTeamCoach() == null) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							"" + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							team.get(match.getAwayTeamId()-1).getTeamCoach().split(" ")[0] + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							team.get(match.getAwayTeamId()-1).getTeamCoach().split(" ")[1] + "\0");
				}
				row_id = 0;
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							as.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							as.getFirstname() + "\0");
					
					if(as.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								as.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_Jersey"
								+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
						if(match.getAwayTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
						}
						
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType$JerseyAll$txt_Number*GEOM*TEXT SET " + 
								as.getJersey_number() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								as.getTicker_name() + "\0");
						break;
					case "WITH_IMAGE":
						if(team.get(match.getAwayTeamId()-1).getTeamCoach() != null) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach$Out$In$txt_Name*GEOM*TEXT SET " + 
									team.get(match.getAwayTeamId()-1).getTeamCoach() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach$Out$In$CoachImageGrp$img_Coach"
									+ "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + "//" + team.get(match.getAwayTeamId()-1).getCoachPhotoName() + FootballUtil.PNG_EXTENSION + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 1 \0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$PlayerImageGrp$img_Player"
								+ "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + "//" +as.getPhoto() + FootballUtil.PNG_EXTENSION + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								as.getTicker_name() + "\0");
						break;
					}
					
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				row_id_sub = 0;
				for(Player asub : match.getAwaySubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							asub.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							asub.getFirstname() + "\0");
					
					if(asub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								asub.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					
					if(asub.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
			}else if(TeamId == match.getAwayTeamId()) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + 
						match.getMatchIdent() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$HeaderOut$txt_Header*GEOM*TEXT SET " + 
						team.get(TeamId-1).getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$txt_SubHeader"
						+ "*GEOM*TEXT SET " + "STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$TeamBadgeGrp$BadgeAll$img_Badge"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getAwayTeamId()-1).getTeamName2().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 0 \0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Base$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp2$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
				
				if(team.get(TeamId-1).getTeamCoach() == null) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							"" + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach().split(" ")[0] + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach().split(" ")[1] + "\0");
				}
				row_id = 0;
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							as.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							as.getFirstname() + "\0");
					
					if(as.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								as.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_Jersey"
								+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
						if(match.getAwayTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType$JerseyAll$txt_Number*GEOM*TEXT SET " + 
								as.getJersey_number() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								as.getTicker_name() + "\0");
						break;
					case "WITH_IMAGE":
						if(team.get(TeamId-1).getTeamCoach() != null) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach$Out$In$txt_Name*GEOM*TEXT SET " + 
									team.get(TeamId-1).getTeamCoach() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach$Out$In$CoachImageGrp$img_Coach"
									+ "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + "//" + team.get(TeamId-1).getCoachPhotoName() + FootballUtil.PNG_EXTENSION + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$Coach*ACTIVE SET 1 \0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$PlayerImageGrp$img_Player"
								+ "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + "//" + as.getPhoto() + FootballUtil.PNG_EXTENSION + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								as.getTicker_name() + "\0");
						break;
					}
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				row_id_sub = 0;
				for(Player asub : match.getAwaySubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
					
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							asub.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							asub.getFirstname() + "\0");
					
					if(asub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								asub.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					
					if(asub.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team1$List$Team1Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				
				//---------------------------------------------------------------------------------------------------------------------------------------------------
				
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + 
						match.getMatchIdent() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$HeaderOut$txt_Header*GEOM*TEXT SET " + 
						team.get(match.getHomeTeamId() -1).getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$txt_SubHeader"
						+ "*GEOM*TEXT SET " + "STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$TeamBadgeGrp$BadgeAll$img_Badge"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getHomeTeamId()-1).getTeamName2().toLowerCase() + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp1$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
//				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$Header$SubHeaderAll$SubHeadeOut$SubHeadGrp2$SubHeadOut$SubHeadIn$img_SecondaryColour"
//						+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 0 \0");
				if(team.get(match.getHomeTeamId()-1).getTeamCoach() == null) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							"" + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$CoachTextGrp$txt_Coach*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_FirstName*GEOM*TEXT SET " + 
							team.get(match.getHomeTeamId()-1).getTeamCoach().split(" ")[0] + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$CoachGrp$Out$In$noname$txt_LastName*GEOM*TEXT SET " + 
							team.get(match.getHomeTeamId()-1).getTeamCoach().split(" ")[1] + "\0");
				}
				
				row_id = 0;
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							hs.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							hs.getFirstname() + "\0");
					
					if(hs.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								hs.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_Jersey"
								+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + FootballUtil.PNG_EXTENSION + "\0");
						if(match.getHomeTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + FootballUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$JerseyAll$img_JerseyText"
									+ "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + FootballUtil.PNG_EXTENSION + "\0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType$JerseyAll$txt_Number*GEOM*TEXT SET " + 
								hs.getJersey_number() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								hs.getTicker_name() + "\0");
						break;
					case "WITH_IMAGE":
						if(team.get(match.getHomeTeamId()-1).getTeamCoach() != null) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach$Out$In$txt_Name*GEOM*TEXT SET " + 
									team.get(match.getHomeTeamId()-1).getTeamCoach() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach$Out$In$CoachImageGrp$img_Coach"
									+ "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + "//" + team.get(match.getHomeTeamId()-1).getCoachPhotoName() + FootballUtil.PNG_EXTENSION + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$Coach*ACTIVE SET 1 \0");
						}
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer"+ row_id +"$Out$In$SelectPlayerType$PlayerImageGrp$img_Player"
								+ "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + "//" + hs.getPhoto() + FootballUtil.PNG_EXTENSION + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer" + row_id + "$Out$In$txt_Name*GEOM*TEXT SET " + 
								hs.getTicker_name() + "\0");
						break;
					}
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$LineUpData$Player" + row_id + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$Tactical$TacticalPlayer11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				row_id_sub = 0;
				for(Player hsub : match.getHomeSubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNumberGrp$txt_Number*GEOM*TEXT SET " + 
							hsub.getJersey_number() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_FirstName*GEOM*TEXT SET " + 
							hsub.getFirstname() + "\0");
					
					if(hsub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								hsub.getSurname() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$PlayerNameGrp$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}
					
					
					if(hsub.getCaptainGoalKeeper().equalsIgnoreCase(FootballUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + 
								"1" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCaptain*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$LineUpAll$Team2$List$Team2Data$DataOut$Bench$Player" + row_id_sub + "$Out$In$SelectCard*FUNCTION*Omo*vis_con SET " + 
								"0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
			}
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 LineUp$Base_In 2.500 LineUp$Team1$DataIn 2.000 \0");
	}
	
	public void populateScoreUpdate(PrintWriter print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			int l=200;
			//String h1="",h2="",h3="",h4="",a1="",a2="",a3="",a4="";
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.HALF)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FULL)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.FIRST)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.SECOND)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA1)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(FootballUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogo_Grp$Nquad$img_Badges"+ "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogo_Grp$Nquad$img_Badges"+ "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Score$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + "\0");
			print_writer.println("-1 RENDERER*TREE*$$Main$All$Select$ScoreLine$Bottom_Grp*ACTIVE SET 0 \0");
			//print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Bottom_Grp$txt_Info*GEOM*TEXT SET " + "LIVE FROM " + match.getVenueName().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$NameDataGrp$HomeDetailGrp$Name$txt_HomeTeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$NameDataGrp$AwayDetailGrp$Name$txt_AwayTeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			
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
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			}else if(match.getHomeTeamScore() > match.getAwayTeamScore()) {
				if(home_stats.size() <= 4) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}
			}else if(match.getHomeTeamScore() < match.getAwayTeamScore()){
				if(away_stats.size() <= 4) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0");
				}
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				/*if(home_stats.size() > away_stats.size()) {
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}else if(home_stats.size() < away_stats.size()) {
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + home_stats.size() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + away_stats.size() + "\0");
				}*/
			}
			
			for(int i=home_stats.size()-1;i >= 0;i--) {
				if(home_stats.size() == 1) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
				}else if(home_stats.size() == 2) {
					if(i==home_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}
				}else if(home_stats.size() == 3) {
					if(i==home_stats.size()-3) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Third$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}
				}else {
					if(i==home_stats.size()-4) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-3) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Third$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}else if(i==home_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select$Fourth$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
						
					}
				}	
			}
			
			for(int i=away_stats.size()-1;i>=0;i--) {
				if(away_stats.size() == 1) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
				}else if(away_stats.size() == 2) {
					if(i==away_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}
				}else if(away_stats.size() == 3) {
					if(i==away_stats.size()-3) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Third$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}
				}else {
					if(i==away_stats.size()-4) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$First$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-3) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Second$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-2) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Third$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}else if(i==away_stats.size()-1) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select$Fourth$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
						
					}
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.760 \0");
		}
	}
	public void populateLtMatchId(PrintWriter print_writer,String viz_scene,FootballService footballService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Time_Grp$txt_Time*GEOM*TEXT SET " + match.getMatchIdent() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogo_Grp$Nquad$img_Badges"+ "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogo_Grp$Nquad$img_Badges"+ "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Score$txt_Score*GEOM*TEXT SET " + "VS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$$Main$All$Select$ScoreLine$Bottom_Grp*ACTIVE SET 1 \0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$Bottom_Grp$txt_Info*GEOM*TEXT SET " + "LIVE FROM " + match.getVenueName().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$NameDataGrp$HomeDetailGrp$Name$txt_HomeTeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$NameDataGrp$AwayDetailGrp$Name$txt_AwayTeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeBandAll$HomeDetailGrp$HomeScorers$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayBandAll$AwayDetailGrp$AwayScorers$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");
		}
	}
	public void populateNameSuper(PrintWriter print_writer,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Logo_Grp$Nquad$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo2_path + "HeroTrination" + "\0");
			if(ns.getFirstname() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
						ns.getSurname() + "\0");
			}
			else if(ns.getSurname() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
						ns.getFirstname() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
						"" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
						ns.getFirstname() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
						ns.getSurname() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
					ns.getSubLine().toUpperCase() + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");
		}
		
	}
	public void populateNameSuperPlayer(PrintWriter print_writer,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			
			String Home_or_Away="";
			int l = 4;
			
			if(captainGoalKeeper.equalsIgnoreCase("PLAYER OF THE MATCH")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 5 \0");
				
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$Logo_Grp$Nquad$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									hs.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									hs.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									hsub.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									hsub.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
						}
					}
				}
				else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$Logo_Grp$Nquad$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									as.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									as.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									asub.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeroOfThematch$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									asub.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
				}
				
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 1 \0");
				
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Logo_Grp$Nquad$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
									hs.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									hs.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									hs.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
								TimeUnit.MILLISECONDS.sleep(l);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + "\0");
							}
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
									hsub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									hsub.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									hsub.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + "\0");
							}
						}
					}
				}
				else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Logo_Grp$Nquad$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
									as.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									as.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									as.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() + "\0");
							}
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
									asub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
									asub.getFirstname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
									asub.getSurname() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
										asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() + "\0");
							}
						}
					}
				}
				
				switch(captainGoalKeeper.toUpperCase())
				{
				case "CAPTAIN":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
							captainGoalKeeper.toUpperCase() + " , " + Home_or_Away + "\0");
					break;
				/*case "PLAYER OF THE MATCH":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
							"HERO OF THE MATCH " + "\0");
					break;*/
				case "GOAL_KEEPER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
							"GOAL-KEEPER" + " , " + Home_or_Away + "\0");
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
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								"GOALS TODAY - " + player_goal_count + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								Home_or_Away + "\0");
					}
					
					break;
				case "GOAL_SCORER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
							"GOAL SCORER" + " , " + Home_or_Away + "\0");
					break;
				case "CAPTAIN-GOALKEEPER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
							"CAPTAIN & GOAL-KEEPER" + " , " + Home_or_Away + "\0");
					break;
				}
			}
			
			

			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");	
		}
	}
	public void populateNameSuperCard(PrintWriter print_writer,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l = 4;
			print_writer.println("-1 RENDERER*TREE*$Main$Select*FUNCTION*Omo*vis_con SET 3 \0");
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Logo_Grp$Nquad$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
								hs.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
								hs.getFirstname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
								hs.getSurname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + "\0");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
								hsub.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
								hsub.getFirstname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
								hsub.getSurname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName4().toUpperCase() + "\0");
					}
				}
			}
			else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Logo_Grp$Nquad$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
								as.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
								as.getFirstname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
								as.getSurname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() + "\0");
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
								asub.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
								asub.getFirstname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
								asub.getSurname() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + 
								asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName4().toUpperCase() + "\0");
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case FootballUtil.YELLOW:
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Card$Select*FUNCTION*Omo*vis_con SET 0 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case FootballUtil.RED:
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Card$Select*FUNCTION*Omo*vis_con SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BandAll$NameDataGrp$PlayerDetailGrp$Card$Select*FUNCTION*Omo*vis_con SET 2 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");	
		}
	}
	public void populateSubstitute(PrintWriter print_writer,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int l = 4;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 4 \0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Logo_Grp$Nquad$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(Team_id - 1).getTeamName2().toLowerCase() + "\0");
			
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
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$NameDataGrp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFirstname() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$NameDataGrp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getSurname() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$Bottom_Grp$PlayerDetailGrp$PlayerNumber$txt_PlayerNumber*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$Bottom_Grp$PlayerDetailGrp$Name$txt_FirstName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFirstname() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BandAll$Bottom_Grp$PlayerDetailGrp$Name$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getSurname() + "\0");
				break;
			/*case "DOUBLE":
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
				
				break;*/
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.940 \0");
		}
	}
	public void populateOfficials(PrintWriter print_writer,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 6 \0");

			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$OfficialSuper$BandAll$NameDataGrp$PlayerDetailGrp$OfficialGrp1$Name$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getReferee()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$OfficialSuper$BandAll$NameDataGrp$PlayerDetailGrp$OfficialGrp2$Name$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getAssistantReferee1()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$OfficialSuper$BandAll$NameDataGrp$PlayerDetailGrp$OfficialGrp3$Name$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getAssistantReferee2()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$OfficialSuper$BandAll$NameDataGrp$PlayerDetailGrp$OfficialGrp4$Name$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getFourthOfficial()  + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$OfficialSuper$BandAll$Bottom_Grp$txt_Info*GEOM*TEXT SET " + "MATCH OFFICIALS" + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.760 \0");
		}
	}
	public void populateHeatMapPeakDistance(PrintWriter print_writer,String viz_scene,int TeamId,String Value,int Playerid,List<Player> plyr,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int team_number=0;
			String team_name="";
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 8 \0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$PlayerNameGrp$Bottom_Grp$PlayerDetailGrp$PlayerNumber$txt_Number*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getJersey_number()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$PlayerNameGrp$Bottom_Grp$PlayerDetailGrp$NameAll$txt_LastName*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getTicker_name()  + "\0");
			
			if(match.getHomeTeamId() == TeamId) {
				team_number = 0;
				team_name = match.getHomeTeam().getTeamName4();
			}else if(match.getAwayTeamId() == TeamId){
				team_number = 1;
				team_name = match.getAwayTeam().getTeamName4();
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$Logo_Grp$Nquad$img_Badges" + "*TEXTURE*IMAGE SET "+ photos_path + 
					team_name + "\\" + plyr.get(Playerid-1).getPhoto() + FootballUtil.PNG_EXTENSION + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$NameDataGrp$Field$Football_Pitch*ACTIVE SET 0 \0");
			
			switch(Value.toUpperCase()) {
			case "HEATMAP":
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$NameDataGrp$Field$img_HeatMap" + "*TEXTURE*IMAGE SET "+ image_path + 
						"playerheatmap" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0");
				break;
			case "PEAKDISTANCE":
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$NameDataGrp$Field$img_HeatMap" + "*TEXTURE*IMAGE SET "+ image_path + 
						"playerpeakdistancegraph" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0");
				break;
			}
			
			/*try {
				
		         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		         Document doc = dBuilder.parse(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.STATISTIC_DIRECTORY + 
		        		 FootballUtil.MATCH_DATA_DIRECTORY + "SportVUStatistic_1.xml");
		         doc.getDocumentElement().normalize();
		         NodeList teamsList = doc.getElementsByTagName("Team");
		         for (int i = 0; i < teamsList.getLength(); i++) {
		        	//NodeList resultData = teamsList[i].getElementsByTagName("ResultData");
		        	//NodeList resultData = teamsList.item(i).getChildNodes();
		            System.out.println("\nCurrent Element :" + teamsList.item(i).getChildNodes());
		            
		            /*if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		               Element eElement = (Element) nNode;
		               
		               System.out.println("Name : " + eElement.getAttribute("Name"));
		               if(eElement.getAttribute("Name").equalsIgnoreCase("Best Runner")) {
			               //System.out.println("First Name : "  + eElement.getElementsByTagName("PlayerFirstName").item(0).getFirstChild().getNodeValue());
			               System.out.println("First Name : "  + eElement.getElementsByTagName("PlayerFirstName").item(0).getTextContent());
			               System.out.println("Last Name : " + eElement.getElementsByTagName("PlayerLastName").item(0).getTextContent());
			               System.out.println("Jersey Number : " + eElement.getElementsByTagName("PlayerJerseyNumber").item(0).getTextContent());
			               System.out.println("Value : " + eElement.getElementsByTagName("Value").item(0).getTextContent());
		               }
		            }
		         }
		      } catch (Exception e) {
		         e.printStackTrace();
		      }*/
			
			//ArrayList<TeamStats> teamStats = new ArrayList<TeamStats>();
			
//			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
//					new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.STATISTIC_DIRECTORY + FootballUtil.MATCH_DATA_DIRECTORY + "SportVUStatistic_1.xml"));
//		        doc.getDocumentElement().normalize();
//		        
//		        NodeList childNodes = doc.getDocumentElement().getChildNodes();
//		        for(int i = 0; i < childNodes.getLength(); i++) {
//		            if(childNodes.item(i).getNodeType() == Node.ELEMENT_NODE && childNodes.item(i).getNodeName().equals("Teams")) {
//		            	for(int j = 0; j < childNodes.item(i).getChildNodes().getLength(); j++) {
//		            		if(childNodes.item(i).getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE 
//		            				&& childNodes.item(i).getChildNodes().item(j).getNodeName().equalsIgnoreCase("Team")) {
//		                    	for(int k = 0; k < childNodes.item(i).getChildNodes().item(j).getChildNodes().getLength(); k++) {
//		                    		
//		                    		if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeType() 
//		                    				== Node.ELEMENT_NODE && childNodes.item(i).getChildNodes().item(j)
//		                    				.getChildNodes().item(k).getNodeName().equalsIgnoreCase("TeamData")) {
//		                    			
//		                    			for(int t = 0; t < childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); t++) {
//		                    				
//		                    				if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(t).getNodeType() 
//				                    				== Node.ELEMENT_NODE && childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(t)
//		                    						.getNodeName().equalsIgnoreCase("TeamName")) {
//		                    					
////		                    					System.out.println("TEAM : " + childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(t).getFirstChild()
////		                    						.getNodeValue());
//		                    					
//		                    					teamStats.add(new TeamStats(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(t).getFirstChild()
//		                    						.getNodeValue(), new ArrayList<TopStats>()));
//		                    					
//		                    				}
//		                    			} 
//		                    		}
//		                    		
//		                    		if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeType() 
//		                    				== Node.ELEMENT_NODE && childNodes.item(i).getChildNodes().item(j)
//		                    				.getChildNodes().item(k).getNodeName().equalsIgnoreCase("ResultData")) {
//		                    			
//		                    			if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("Name").getNodeValue().equalsIgnoreCase("Best Runner")||
//		                    					childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("Name").getNodeValue().equalsIgnoreCase("Best Sprinter")||
//		                    					childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("Name").getNodeValue().equalsIgnoreCase("Highest Distance")||
//		                    					childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getAttributes().getNamedItem("Name").getNodeValue().equalsIgnoreCase("Team Top Speed")) {
//		                    				
////		                    				System.out.println("Stat Type = " + childNodes.item(i).getChildNodes().item(j).getChildNodes()
////			                                		.item(k).getAttributes().getNamedItem("Name").getNodeValue());
//			                    			
//			                    			teamStats.get(teamStats.size()-1).getTopStats().add(new TopStats(childNodes.item(i).getChildNodes().item(j).getChildNodes()
//			                                		.item(k).getAttributes().getNamedItem("Name").getNodeValue(), new ArrayList<PlayerStats>()));
//		                    				
//			                    			for(int l = 0; l < childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().getLength(); l++) {
//			                    				
//			                            		if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k)
//			                            				.getChildNodes().item(l).getNodeType() == Node.ELEMENT_NODE 
//			                            				&& childNodes.item(i).getChildNodes().item(j)
//			                            				.getChildNodes().item(k).getChildNodes().item(l).getNodeName().equalsIgnoreCase("Result")) {
//			                            			
//			                                    	for(int m = 0; m < childNodes.item(i).getChildNodes().item(j).getChildNodes()
//			                                    			.item(k).getChildNodes().item(l).getChildNodes().getLength(); m++) {
//			                                    		
//			                                    		if(childNodes.item(i).getChildNodes().item(j).getChildNodes()
//			                                    			.item(k).getChildNodes().item(l).getChildNodes().item(m).getNodeType() 
//			                                    			== Node.ELEMENT_NODE) {
//			                                    			
//			                                    			if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getChildNodes()
//			                                            		.item(m).getNodeName().equalsIgnoreCase("PlayerJerseyNumber")) {
//			                                    				
////			                                    				System.out.println("PlayerJerseyNumber = " + childNodes.item(i).getChildNodes().item(j).getChildNodes()
////			                                        				.item(k).getChildNodes().item(l).getChildNodes().item(m).getFirstChild().getNodeValue());
//			                                    				
//			                                    				teamStats.get(teamStats.size()-1).getTopStats().get(teamStats.get(teamStats.size()-1).getTopStats().size()-1)
//			                                    					.getPlayersStats().add(new PlayerStats(Integer.valueOf(childNodes.item(i).getChildNodes().item(j).getChildNodes()
//					                                        				.item(k).getChildNodes().item(l).getChildNodes().item(m).getFirstChild().getNodeValue())));
//			                                    				
//			                                    			}else if(childNodes.item(i).getChildNodes().item(j).getChildNodes().item(k).getChildNodes().item(l).getChildNodes()
//			                                                		.item(m).getNodeName().equalsIgnoreCase("Value")) {
//			                                    				
//			                                    				teamStats.get(teamStats.size()-1).getTopStats().get(teamStats.get(teamStats.size()-1).getTopStats().size()-1)
//		                                    						.getPlayersStats().get(teamStats.get(teamStats.size()-1).getTopStats().get(teamStats.get(teamStats.size()-1)
//		                                    								.getTopStats().size()-1).getPlayersStats().size()-1).setValue(childNodes.item(i).getChildNodes().item(j)
//		                                    										.getChildNodes().item(k).getChildNodes().item(l).getChildNodes().item(m).getFirstChild().getNodeValue());
//			                                    				
////			                                    				System.out.println("Value = " + childNodes.item(i).getChildNodes().item(j).getChildNodes()
////			                                        					.item(k).getChildNodes().item(l).getChildNodes().item(m).getFirstChild().getNodeValue());
//			                                        		}
//			                                    			
//			                                    			
//			                                    		}
//			                                    	}
//			                            		}
//			                            	}
//		                    			}
//		                    		}
//		                    	}
//		            		}
//		            	}
//		            }
//		        }
		        
//		        for(int i=0;i<= teamStats.size()-1;i++) {
//		        	if(teamStats.get(i).getTeamName().equalsIgnoreCase("India")) {
//		        		for(int j=0;j<= teamStats.get(i).getTopStats().size()-1;j++) {
//		        			if(teamStats.get(i).getTopStats().get(j).getHeader().equalsIgnoreCase("Best Runner")) {
//				        		for(int k=0;k<= teamStats.get(i).getTopStats().get(j).getPlayersStats().size()-1;k++) {
//				        			System.out.println("JERSEY -" + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getJerseyNumber() + 
//				        				" Value - " + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getValue());
//				        		}
//				        	}
//		        		}
//		        	}
//		        	
//		        }
		       
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.760 \0");
		}
	}
	public void populateTopStats(PrintWriter print_writer,String viz_scene,int TeamId,String Value,List<TeamStats>teamStats,List<Player> player,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			String team_name="";
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 7 \0");
			
			if(match.getHomeTeamId() == TeamId) {
				team_name = match.getHomeTeam().getTeamName2();
			}else if(match.getAwayTeamId() == TeamId) {
				team_name = match.getAwayTeam().getTeamName2();
			}
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Top5Mini$Header$HeaderOut$txt_Score*GEOM*TEXT SET " + 
					team_name + " - " + Value.toUpperCase()  + "\0");
			
	        for(int i=0;i<= teamStats.size()-1;i++) {
	        	if(teamStats.get(i).getTeamName().equalsIgnoreCase(team_name)) {
	        		for(int j=0;j<= teamStats.get(i).getTopStats().size()-1;j++) {
	        			if(teamStats.get(i).getTopStats().get(j).getHeader().equalsIgnoreCase(Value)) {
	        				
	        				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Top5Mini$DataAll$DataOut$ScorrerTeam1"
	    							+ "*FUNCTION*Grid*num_row SET " + teamStats.get(i).getTopStats().get(j).getPlayersStats().size() + "\0");
	        				
			        		for(int k=0;k<= teamStats.get(i).getTopStats().get(j).getPlayersStats().size()-1;k++) {
			        			for(Player plyr : player) {
			        				if(plyr.getTeamId() == TeamId) {
			        					if(plyr.getJersey_number() == teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getJerseyNumber()) {
			        						
			        						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Top5Mini$DataAll$DataOut$ScorrerTeam1$Row" + (k+1) + ""
			        								+ "$Out$In$NumberGrp$txt_Number*GEOM*TEXT SET " + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getJerseyNumber()  + "\0");
			        						
			        						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Top5Mini$DataAll$DataOut$ScorrerTeam1$Row" + (k+1) + ""
			        								+ "$Out$In$txt_Name*GEOM*TEXT SET " + plyr.getTicker_name()  + "\0");
			        						
			        						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Top5Mini$DataAll$DataOut$ScorrerTeam1$Row" + (k+1) + ""
			        								+ "$Out$In$StatValueGrp$txt_Value*GEOM*TEXT SET " + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getValue()  + "\0");
			        						
			        					}
			        				}
			        			}
			        			
//			        			System.out.println("JERSEY -" + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getJerseyNumber() + 
//			        				" Value - " + teamStats.get(i).getTopStats().get(j).getPlayersStats().get(k).getValue());
			        		}
			        	}
	        		}
	        	}
	        }
		       
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.760 \0");
		}
	}
}