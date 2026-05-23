package com.football.broadcaster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import  com.football.EuroLeague.SeasonalStats;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.model.*;
import com.football.service.FootballService;
import com.football.util.FootballUtil;
import com.opencsv.exceptions.CsvException;
import com.football.EuroLeague.Players;
import com.football.EuroLeague.Qualifier;
import com.football.EuroLeague.SeasonalPlayerStats;
import com.football.EuroLeague.Stat;
import com.football.EuroLeague.TeamPlayerRanking;
import com.football.EuroLeague.TeamStat;
import com.football.EuroLeague.rankings;
import com.football.EuroLeague.teamData;
import com.football.EuroLeague.LiveMatch;
import com.football.EuroLeague.MatchPreview;
import com.football.EuroLeague.PassMatrix;
import com.football.EuroLeague.Card;
import com.football.EuroLeague.Contestant;
import com.football.EuroLeague.Events;
import com.football.EuroLeague.Form;
import com.football.containers.FootballData;
import com.football.containers.Lband;
import com.football.containers.Scene;
import com.football.containers.Stats;

public class EuroLeague extends Scene{
	
	public String session_selected_broadcaster = "EURO_LEAGUE";
	
	public Lband Lband = new Lband(); 
	public int WhichSide = 1,sponsor_side = 1,sponsor_bottom = 1;
	public String vtp = "",Value = "";
	public String logo_path="IMAGE*/Default/Essentials/Flag_Id/";
	public String flag_path = "IMAGE*/Default/Essentials/Flag_Id/";
	public String sponsor_path = "IMAGE*/Default/Essentials/Sponsor/";

	public static FootballData data = new FootballData();
	
	public EuroLeague() {
		super();
	}
	
	public void updateGraphic(PrintWriter print_writer,LiveMatch liveData,FootballService footballService,String valueToProcess) throws InterruptedException, 
		MalformedURLException, IOException, CsvException, SAXException, ParserConfigurationException, FactoryConfigurationError, JAXBException
	{
		if(!Lband.getWhich_Last_Right_LBand_onscreen().equalsIgnoreCase("PLAYING_XI")) {
			Lband = populateRightLband(Lband, 1, print_writer, footballService);
		}
		Lband = populateBottomLband(Lband, 1,print_writer,valueToProcess, footballService);	

	}
	
	public Object ProcessGraphicOption(PrintWriter print_writer,String whatToProcess,LiveMatch liveData,Clock clock, FootballService footballService,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		System.out.println("ProcessGraphicOption :-> whatToProcess = "+whatToProcess+"  valueToProcess = "+valueToProcess);
		switch (whatToProcess.toUpperCase()) {
			
			case "CANCEL":
				Lband.setWhich_Bottom_LBand_onscreen(Lband.getWhich_Last_Bottom_LBand_onscreen());
				Lband.setWhich_Right_LBand_onscreen(Lband.getWhich_Last_Right_LBand_onscreen());
				break;
			case "POPULATE-L-BAND":
				populateLBand(Long.valueOf(valueToProcess.split(",")[1]), liveData);
				break;		
			case "POPULATE-FOUL":case "POPULATE-LWP":case "POPULATE-CLEARANCE": case"POPULATE-ACCURATE_PASS": case"POPULATE-TACKLE": case"POPULATE-TOUCHES":case "POPULATE-DB_TEAM":
			case "POPULATE-TOTAL_FINAL_THIRD_PASSES": case"POPULATE-POSS_WON_ATT_3RD":case "POPULATE-SET_PIECES":case "POPULATE-SHOOTING_ACCURACY":case "POPULATE-INSIGHTS_GS":
			case "POPULATE-WON_CORNERS": case"POPULATE-DUEL_WON":case"POPULATE-WON_CONTEST":case "POPULATE-EXPECTED_GOALS":case "POPULATE-INSIGHTS":case"POPULATE-L3-BUG-DB":
			case "POPULATE-WIN_H2H":case "POPULATE-H2H":case "POPULATE-H2H_LIVE_WIN": case"POPULATE-INSIGHTS_TEAM":	case "POPULATE-DB_EXTRA_DATA": case "POPULATE-FT":
			case "POPULATE-PLAYER_RATING":	case "POPULATE-EXA_EXG": case "POPULATE-SPONSOR_EURO": case "POPULATE-SPONSOR_BOTTOM": case "POPULATE-INSIGHTS_RESULT":
			case "POPULATE-ATTACKING_ZONE":	case "POPULATE-PLAYING_XI":
			case "POPULATE-MATCH_DATA":case "POPULATE-POINTS_TABLE_LB":
				
				if((Lband.getWhich_Right_LBand_onscreen().trim() == "" && Lband.getWhich_Right_LBand_onscreen().trim().isEmpty()) && 
						(Lband.getWhich_Bottom_LBand_onscreen().trim() == "" && Lband.getWhich_Bottom_LBand_onscreen().trim().isEmpty())) {
					WhichSide = 1;
					scenes.get(0).setScene_path("/Default/Lband");
					scenes.get(0).scene_load(print_writer,session_selected_broadcaster);
					print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				}else {
					WhichSide = 2;
				}
				System.out.println("WhichSide  "+WhichSide);
				if(whatToProcess.equalsIgnoreCase("POPULATE-PLAYING_XI")) {
					vtp = "BOTH";
					Lband.setPlaying_XI_Team(valueToProcess);
					Lband.setWhich_Right_LBand_onscreen("PLAYING_XI");
					Lband.setWhich_Bottom_LBand_onscreen("SPONSOR");
				}else if(whatToProcess.equalsIgnoreCase("POPULATE-DB_TEAM")) {
					vtp = "BOTTOM";
					Value = valueToProcess.substring(valueToProcess.lastIndexOf(",")+1);
					Lband.setWhich_Bottom_LBand_onscreen("DB_TEAM");
				}else if(whatToProcess.equalsIgnoreCase("POPULATE-POINTS_TABLE_LB")) {
					vtp = "BOTH";
					Value = valueToProcess;
					Lband.setWhich_Bottom_LBand_onscreen("POINTS_TABLE");
					Lband.setWhich_Right_LBand_onscreen("POINTS_TABLE");
				}else if(whatToProcess.equalsIgnoreCase("POPULATE-LWP")) {
					vtp = "BOTH";
					Value = valueToProcess;
					Lband.setWhich_Bottom_LBand_onscreen("LWP");
					Lband.setWhich_Right_LBand_onscreen("LWP");
				}else {
					if(valueToProcess.contains(",")) {
						if(valueToProcess.split(",").length==3) {
							vtp = valueToProcess.split(",")[1].trim();
							Value = valueToProcess.split(",")[2].trim();
						}else {
							vtp = valueToProcess.split(",")[2].trim();
							Value = valueToProcess.split(",")[3].trim();
						}
					}
				}
				System.out.println("ProcessGraphicOption :-> vtp = "+vtp+"  Value = "+Value);

				if((Lband.getWhich_Last_Right_LBand_onscreen().trim() == "" && Lband.getWhich_Last_Right_LBand_onscreen().trim().isEmpty()) && 
						(Lband.getWhich_Last_Bottom_LBand_onscreen().trim() == "" && Lband.getWhich_Last_Bottom_LBand_onscreen().trim().isEmpty())) {
					Lband.setWhich_Last_Right_LBand_onscreen(Value);
					Lband.setWhich_Last_Bottom_LBand_onscreen(Value);

				}
				if(whatToProcess.equalsIgnoreCase("POPULATE-MATCH_DATA")) {
					if(vtp.equalsIgnoreCase("RIGHT")) {
						Lband.setWhich_Right_LBand_onscreen(Value);
					}else if(vtp.equalsIgnoreCase("BOTTOM")) {
						Lband.setWhich_Bottom_LBand_onscreen(Value);	
					}else if(vtp.equalsIgnoreCase("BOTH")) {
						Lband.setWhich_Right_LBand_onscreen(Value);
						Lband.setWhich_Bottom_LBand_onscreen(Value);
					}
				}
				if(vtp.equalsIgnoreCase("RIGHT")) {
					populateRightLband(Lband,WhichSide, print_writer,footballService);
				}else if(vtp.equalsIgnoreCase("BOTTOM")) {
					populateBottomLband(Lband,WhichSide,print_writer,valueToProcess,footballService);	
				}else if(vtp.equalsIgnoreCase("BOTH")) {
					populateRightLband(Lband,WhichSide, print_writer,footballService);	
					populateBottomLband(Lband,WhichSide,print_writer,valueToProcess,footballService);
				}
			
			break;
			
			case "ANIMATE-SPONSOR_EURO":
				if(!Lband.getSponsor_on_screen().isEmpty()) {
					populateAnimation(print_writer, whatToProcess.split("-")[1].toUpperCase(),data, valueToProcess,footballService);
				}else {
					AnimateInGraphics(print_writer, whatToProcess.split("-")[1].toUpperCase());
				}
				Lband.setSponsor_on_screen(whatToProcess.split("-")[1].toUpperCase());
				break;
			case "ANIMATE-SPONSOR_BOTTOM":
				if(!Lband.getBottom_sponsor_on_screen().isEmpty()) {
					populateAnimation(print_writer, whatToProcess.split("-")[1].toUpperCase(),data, valueToProcess,footballService);
				}else {
					AnimateInGraphics(print_writer, whatToProcess.split("-")[1].toUpperCase());
				}
				Lband.setBottom_sponsor_on_screen(whatToProcess.split("-")[1].toUpperCase());
				break;	
			case "ANIMATE-FOUL": case "ANIMATE-LWP":case "ANIMATE-ACCURATE_PASS":case "ANIMATE-CLEARANCE": case "ANIMATE-TACKLE": case"ANIMATE-INSIGHTS":case "ANIMATE-INSIGHTS_GS":
			case"ANIMATE-TOUCHES":case "ANIMATE-TOTAL_FINAL_THIRD_PASSES": case"ANIMATE-POSS_WON_ATT_3RD":case "ANIMATE-SET_PIECES":case"ANIMATE-IN-BUG-DB":
			case "ANIMATE-WON_CORNERS": case"ANIMATE-DUEL_WON":case"ANIMATE-WON_CONTEST":case "ANIMATE-EXPECTED_GOALS":case "ANIMATE-SHOOTING_ACCURACY":case "ANIMATE-DB_TEAM":
			case "ANIMATE-WIN_H2H":case "ANIMATE-H2H":case "ANIMATE-H2H_LIVE_WIN":	case"ANIMATE-INSIGHTS_TEAM": case "ANIMATE-DB_EXTRA_DATA":
			case "ANIMATE-FT":	case "ANIMATE-EXA_EXG": case "ANIMATE-PLAYER_RATING": case "ANIMATE-INSIGHTS_RESULT":
			case "ANIMATE-PLAYING_XI": case "ANIMATE-MATCH_DATA":case"ANIMATE-ATTACKING_ZONE":case"ANIMATE-POINTS_TABLE_LB":
				System.out.println("isLband_on_screen  "+Lband.isLband_on_screen()+"  vtp  "+vtp);
				if(!Lband.isLband_on_screen()) {
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In SHOW 0.0\0");
					AnimateInGraphics(print_writer, whatToProcess.split("-")[1].toUpperCase());
					Lband.setLband_on_screen(true);
					
				}else {
					if(whatToProcess.equalsIgnoreCase("ANIMATE-PLAYING_XI")) {
						populateAnimation(print_writer, "PLAYING_XI", data, valueToProcess, footballService);
					}else {
						System.out.println("valueToProcess  "+valueToProcess);
						populateAnimation(print_writer, Value, data, valueToProcess, footballService);
					}
				}
				if(vtp.equalsIgnoreCase("RIGHT")) {
					Lband.setWhich_Last_Right_LBand_onscreen(Lband.getWhich_Right_LBand_onscreen());
				}else if(vtp.equalsIgnoreCase("BOTTOM")) {
					Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
				}else {
					Lband.setWhich_Last_Right_LBand_onscreen(Lband.getWhich_Right_LBand_onscreen());
					Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
				}
				System.out.println("Current Bottom = " + Lband.getWhich_Bottom_LBand_onscreen());
				System.out.println("Current Right = " + Lband.getWhich_Right_LBand_onscreen());
				System.out.println("Last_Bottom = " + Lband.getWhich_Last_Bottom_LBand_onscreen());
				System.out.println("Last_Right = " + Lband.getWhich_Last_Right_LBand_onscreen());
				
				Thread.sleep(8 * 100);
				break;
	//AnimateOut
		case "ANIMATE-OUT":
			if(Lband.isLband_on_screen()) {
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START\0");
				Lband.setLband_on_screen(false);
				Lband.setWhich_Right_LBand_onscreen(""); Lband.setWhich_Bottom_LBand_onscreen("") ;
				WhichSide = 1;
			}
			break;
		case "CLEAR-ALL":
			print_writer.println("-1 SCENE CLEANUP\0");
			print_writer.println("-1 IMAGE CLEANUP\0");
			print_writer.println("-1 GEOM CLEANUP\0");
			print_writer.println("-1 FONT CLEANUP\0");

			print_writer.println("-1 IMAGE INFO\0");
			print_writer.println("-1 RENDERER SET_OBJECT SCENE*/Default/Lband\0");

			print_writer.println("-1 RENDERER INITIALIZE\0");
			print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE\0");
			print_writer.println("-1 RENDERER*UPDATE SET 0\0");
			print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");

			print_writer.println("-1 RENDERER*UPDATE SET 1\0");

			print_writer.println("-1 SCENE CLEANUP\0");
			print_writer.println("-1 IMAGE CLEANUP\0");
			print_writer.println("-1 GEOM CLEANUP\0");
			print_writer.println("-1 FONT CLEANUP\0");
			Lband.setWhich_Right_LBand_onscreen(""); Lband.setWhich_Bottom_LBand_onscreen("") ;
			Lband.setLband_on_screen(false);
			WhichSide = 1;
			break;
		case "ANIMATE-OUT-SPONSOR_EURO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Sponsor$Out START\0");
			Lband.setSponsor_on_screen("");
			break;
		case "ANIMATE-OUT-SPONSOR_RIGHT":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Sponsor$Out START\0");
			Lband.setSponsor_on_screen("");
			break;	
		}
		return null;
	}
	
	public void AnimateInGraphics(PrintWriter print_writer, String whichGraphic) throws InterruptedException, IOException {
		switch (whichGraphic) {
		case "SPONSOR_EURO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Sponsor$In START\0");
			break;
		case "SPONSOR_BOTTOM":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Sponsor$In START\0");
			break;
		case "PLAYING_XI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Header$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Score$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$LineUp$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Stats$In SHOW 0.760\0");
			
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Main$In START\0");
			Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
			break;
		case "FOUL": case"ACCURATE_PASS": case"TACKLE":case"TOUCHES":case "TOTAL_FINAL_THIRD_PASSES": case"H2H_LIVE_WIN":
		case"POSS_WON_ATT_3RD":case "WON_CORNERS": case"DUEL_WON":case"WON_CONTEST":case "EXPECTED_GOALS":
		case "CLEARANCE":case"SET_PIECES":	case "INSIGHTS":case "BUG-DB":case "INSIGHTS_GS":case "INSIGHTS_TEAM":
		case "WIN_H2H":case "H2H": case "FT": case "DB_EXTRA_DATA": case "EXA_EXG": case"LWP": 
		case "PLAYER_RATING": case "SHOOTING_ACCURACY": case "INSIGHTS_RESULT": case "MATCH_DATA":case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":case "TOTAL_OFF_SIDE":case "SAVES":
		case "BLOCKED_SCORING_ATTACK":case "SHOT_ON_TARGET":case"TOTAL_THROWS":case"BALL_RECOVERY":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":case"RED_CARD":case "POSSESSION":
			switch(vtp.toUpperCase()) {
				case "RIGHT":
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Header$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Score$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$Stats$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main$LineUp$In SHOW 0.760\0");
					Lband.setWhich_Last_Right_LBand_onscreen(Lband.getWhich_Right_LBand_onscreen());
					break;
				case "BOTTOM":
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Main$In START\0");
					Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
					break;
				case "BOTH":
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Main$In START\0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main START\0");
					Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
					Lband.setWhich_Last_Right_LBand_onscreen(Lband.getWhich_Right_LBand_onscreen());
					break;
			}
		case "DB_TEAM":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Main$In START\0");
			Lband.setWhich_Last_Bottom_LBand_onscreen(Lband.getWhich_Bottom_LBand_onscreen());
			break;
		case "POINTS_TABLE_LB":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*All$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Main$In START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Main START\0");
			break;
			
		}
	}
	public void AnimateOutGraphics(PrintWriter print_writer, String whichGraphic) throws IOException {
		
		switch (whichGraphic.toUpperCase()) {
		
		case "FOUL": case"LWP":case"ACCURATE_PASS": case"TACKLE":case"TOUCHES":case "WIN_H2H":case "H2H":case "EXPECTED_GOALS":
		case "TOTAL_FINAL_THIRD_PASSES": case"POSS_WON_ATT_3RD":case "WON_CORNERS": case"DUEL_WON":case"WON_CONTEST":case"RED_CARD":
		case"H2H_LIVE_WIN":case "INSIGHTS_TEAM": case "FT": case "DB_EXTRA_DATA": case "EXA_EXG": case "PLAYER_RATING":
		case "SHOOTING_ACCURACY":case"SET_PIECES": case "INSIGHTS_RESULT":case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":case "POSSESSION":case "TOTAL_OFF_SIDE":case "SAVES":
		case "BLOCKED_SCORING_ATTACK":case "SHOT_ON_TARGET":case"TOTAL_THROWS":case"BALL_RECOVERY":case "POINTS_TABLE":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":

			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Out START\0");
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Out START\0");
			break;
		case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START\0");
			break;
		}
	}

	public void populateAnimation(PrintWriter print_writer,String whichGraphic,FootballData data,String valueToProcess,
			FootballService footballService) throws InterruptedException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError, JAXBException {
		System.out.println("populateAnimation :- whichGraphic = "+whichGraphic+"  valueToProcess = "+valueToProcess+ 
				"  vtp = "+ vtp);
		switch (whichGraphic) {
			case"TOUCHES": case "TACKLE": case "ACCURATE_PASS": case "FOUL": case "DB_TEAM":
			case "CLEARANCE":case"DUEL_WON":case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":case "TOTAL_OFF_SIDE":case "SAVES":case "POSSESSION":
			case "BLOCKED_SCORING_ATTACK":case "ON_TARGET_SCORING_ATTACK":case"TOTAL_THROWS":case"BALL_RECOVERY":case "POINTS_TABLE":case "INTERCEPTIONS":case"RED_CARD":case"TOTAL_CROSS":case"TURNOVER":
				ChangeOnGraphic(print_writer, whichGraphic, "IN");
				
				if(vtp.equalsIgnoreCase("RIGHT")) {
					populateRightLband(Lband,1, print_writer,footballService);	
				}
				if(vtp.equalsIgnoreCase("BOTTOM")) {
					populateBottomLband(Lband,1,print_writer,valueToProcess,footballService);	
				}
				if(vtp.equalsIgnoreCase("BOTH")) {
					populateRightLband(Lband,1,print_writer,footballService);	
					populateBottomLband(Lband,1,print_writer,valueToProcess,footballService);	
				}
				Thread.sleep(3 * 1500);
				ChangeOnGraphic(print_writer, whichGraphic, "CHANGE_ON");
				WhichSide = 1;
				break;
			case "PLAYING_XI":
				ChangeOnGraphic(print_writer, whichGraphic, "IN");
				Thread.sleep(3 * 1000);
				if(vtp.equalsIgnoreCase("RIGHT")) {
					populateRightLband(Lband,1, print_writer,footballService);	
				}
				if(vtp.equalsIgnoreCase("BOTTOM")) {
					populateBottomLband(Lband,1,print_writer,valueToProcess,footballService);	
				}else if(vtp.equalsIgnoreCase("BOTH")) {
					populateRightLband(Lband,1, print_writer,footballService);	
					populateBottomLband(Lband,1,print_writer,valueToProcess,footballService);	
				}
				Thread.sleep(3 * 1000);
				ChangeOnGraphic(print_writer, whichGraphic, "CHANGE_ON");
				WhichSide = 1;
				break;
		}
	}
	public void ChangeOnGraphic(PrintWriter print_writer,String whichGraphic,String Command) throws InterruptedException {
		System.out.println("ChangeOnGraphic  :- "+" whichGraphic = "+whichGraphic+" Command = "+ Command +" vtp= "+ vtp);
		switch(Command.toUpperCase()) {
			case "IN":
				switch(whichGraphic.toUpperCase()) {
					case"TOUCHES": case "TACKLE": case "ACCURATE_PASS": case "FOUL": case "PLAYING_XI":case "DB_TEAM":case"RED_CARD":
					case "CLEARANCE":case"DUEL_WON":case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD": 
					case "TOTAL_OFF_SIDE":case "SAVES":case "POSSESSION": case "BLOCKED_SCORING_ATTACK":case "ON_TARGET_SCORING_ATTACK":
					case"TOTAL_THROWS":case"BALL_RECOVERY":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":case"POINTS_TABLE":

						switch(vtp.toUpperCase()) {
							case "RIGHT":
								print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Change START \0");
								break;
							case "BOTTOM":
								print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Change START \0");
								break;
							case "BOTH":
								print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Change CONTINUE \0");
								print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Change CONTINUE \0");
								break;
						}
						break;
				}
				break;
			case "CHANGE_ON":
				switch(whichGraphic.toUpperCase()) {
					case"TOUCHES": case "TACKLE": case "ACCURATE_PASS": case "FOUL": case "PLAYING_XI":case "DB_TEAM":case "POSSESSION":
					case "CLEARANCE":case"DUEL_WON":case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":
					case "TOTAL_OFF_SIDE":case "SAVES": case "BLOCKED_SCORING_ATTACK":case "ON_TARGET_SCORING_ATTACK": case"TOTAL_THROWS": 
					case"BALL_RECOVERY":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":case"RED_CARD":
					case"POINTS_TABLE":
						switch(vtp.toUpperCase()) {
						case "RIGHT":
							print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Change SHOW 0.0 \0");									
							break;
						case "BOTTOM":
							print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Change SHOW 0.0 \0");
							break;
						case "BOTH":
							print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Change SHOW 0.0 \0");
							print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Change SHOW 0.0 \0");
							break;
						}
						
					break;
				}
				break;	
		}
	}
	
	public void setLBandData(String whatToProcess) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		data = new FootballData();	
		switch(whatToProcess.toUpperCase() ){
			case "ATTACKING_ZONE":
				Event(data);
				break;
			case "TOTAL_FINAL_THIRD_PASSES":case"POSS_WON_ATT_3RD": case "TOUCHES":case "DB_TEAM":case"INSIGHTS_TEAM":
			case "TACKLE": case "PLAYING_XI":case "ACCURATE_PASS": case "FOUL":case"DUEL_WON":case "CLEARANCE":
			case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":case "TOTAL_OFF_SIDE":case "SAVES":case "POSSESSION":
			case "BLOCKED_SCORING_ATTACK":case "SHOT_ON_TARGET":case"TOTAL_THROWS":case"RED_CARD":
			case"BALL_RECOVERY":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":
				LiveData(data);
				break;
			
//			case "CLEARANCE":
//				SeasonalRanking(data);
//				break;
			case "WIN_H2H":case "H2H":case"H2H_LIVE_WIN": case "FT":case"LWP":
			case "INSIGHTS_RESULT":	case "POINTS_TABLE":
				WinProbability(data);
				break;
			case "SET_PIECES":case "SHOOTING_ACCURACY":case"WON_CONTEST":
			case "WON_CORNERS": case "DB_EXTRA_DATA":
				//TM4
				SeasonalStats(data);
				break;
			case "EXPECTED_GOALS":case "INSIGHTS_GS": case "EXA_EXG":
				ExpectedGoals(data);
				break;
			case "INSIGHTS":case"BUG-DB": 
				InsightTournaments(data);
				break;
			case "PLAYER_RATING":
				PlayerRating(data);
				break;
		}
	}

	public Lband populateBottomLband(Lband Lband,int WhichSide,PrintWriter print_writer,String valueToProcess,FootballService footballService) throws 
		StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError, JAXBException {
		
		if(!valueToProcess.contains("BOTH")) {
			setLBandData(Lband.getWhich_Bottom_LBand_onscreen());
		}
		
		switch(Lband.getWhich_Bottom_LBand_onscreen().toUpperCase() ){
		case "FOUL":
			populateFoul(print_writer,data, WhichSide,"BOTTOM");
			break;
		case"TOTAL_THROWS":
			populateThrows(print_writer,data, WhichSide,"BOTTOM");
			break;
		case"LWP":
			populateLWP(print_writer,data, WhichSide,"BOTTOM");
			break;
		case "TACKLE":
			populateTackle(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "ACCURATE_PASS":
			populateAccuratePass(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "TOTAL_PASS":
			populateTotalPass(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "SHOT_OFF_TARGET":
			populateShotOffTarget(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "CORNER_TAKEN":
			populateCornerTaken(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "YELLOW_CARD":case"RED_CARD":case "POSSESSION":
			populatemultidata(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "TOTAL_OFF_SIDE":
			populateTotalOffSide(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "SAVES":
			populateSaves(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "BLOCKED_SCORING_ATTACK":
			populateBlockedScoringAtt(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "SHOT_ON_TARGET":
			populateOnTargetScoringAttack(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "TOTAL_FINAL_THIRD_PASSES":
			populateFinal3rdPass(print_writer,data, WhichSide);
			break;
		case"POSS_WON_ATT_3RD":
			populateWonAtt3rd(print_writer,data, WhichSide);
			break;
		case "WON_CORNERS":
			populateWonCorners(print_writer,data, WhichSide, "BOTTOM");
			break;
		case"DUEL_WON":
			populateDuelWon(print_writer,data, WhichSide, "BOTTOM");
			break;
		case"WON_CONTEST":
			populateDribbles(print_writer,data, WhichSide);
			break;
		case "TOUCHES":
			populateTouches(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "EXPECTED_GOALS":
			populateExpectedGoals(print_writer,data, WhichSide);
			break;
		case "EXA_EXG":
			populateExpectedGoalsForAgainst(print_writer,data, WhichSide);
			break;
		case "CLEARANCE":
			populateClearance(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "SET_PIECES":
			populateSetPiece(print_writer,data, WhichSide);
			break;
		case "SHOOTING_ACCURACY":
			populateShootingAccuracy(print_writer, data, WhichSide, "BOTTOM");

			break;
		case "INSIGHTS":
			populateIngights(print_writer,data,WhichSide,vtp);
			break;
		case "INSIGHTS_GS":
			populateIngightsGs(print_writer,data,WhichSide,vtp);
			break;
		case"INSIGHTS_TEAM":
			populateIngightsTeam(print_writer,data,WhichSide,vtp);
			break;
		case "DB_EXTRA_DATA":
			for(ExtraData extraData : footballService.getExtraData()) {
			  if(extraData.getDataId() == Integer.valueOf(vtp)) {
				  populateExtraData(print_writer,extraData, data, WhichSide,footballService.getAllPlayer(), footballService.getTeams());
			  }
			}
			break;
		case "SPONSOR_EURO":
			populateSponsor(print_writer,data,sponsor_side,vtp);
			break;
		case "SPONSOR_BOTTOM":
			populateBottomSponsor(print_writer,data,sponsor_bottom,vtp);
			break;
		case"BUG-DB":
			for(Bugs bug : footballService.getBugs()) {
				  if(bug.getBugId() == Integer.valueOf(vtp)) {
					  populateVariousTextBottom(print_writer,bug, data,WhichSide);
				  }
				}
			break;
		case "DB_TEAM":
			System.out.println(valueToProcess);
			for(Bugs bug : footballService.getBugs()) {
				if(valueToProcess.split(",").length>1) {
					if(bug.getBugId() == Integer.valueOf(Value)) {
						  populateTeamDB(print_writer,bug, data,WhichSide,"BOTTOM");
					  }
				} 
			}
			break;
		case "WIN_H2H":
			populateWin(print_writer,data,WhichSide, "BOTTOM");
			break;
		case "H2H":
			populateH2H(print_writer,data,WhichSide, "BOTTOM");
			break;
		case "FT":
			populateFT(print_writer,data,WhichSide);
			break;
		case"H2H_LIVE_WIN":
			populateLiveH2H(print_writer,data,WhichSide, "BOTTOM");
			break;
		case "PLAYER_RATING":
			populateplayerRating(print_writer,data,WhichSide,footballService.getAllPlayer());
			break;
		case "INSIGHTS_RESULT":
			populateIngightsResult(print_writer,data,WhichSide,vtp);
			break;
		case"BALL_RECOVERY":
			populateBallRecovery(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "POINTS_TABLE":
			populatePointsTable(print_writer,data, WhichSide, "BOTTOM");
			break;
		case "INTERCEPTIONS":
			populateInterception(print_writer,data, WhichSide, "BOTTOM");
			break;
		case"TOTAL_CROSS":
			populateCross(print_writer,data, WhichSide, "BOTTOM");
			break;
		case"TURNOVER":
			populateTurnover(print_writer,data, WhichSide, "BOTTOM");
			break;
		}
		return Lband;
	
	}
	private void populateTurnover(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {

		if(data.getTeam()!=null && data.getTeam().size()==2) {
			
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "TURNOVER\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
							 + "1\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
							 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
							 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
						
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTurnover() <= 0);
						       Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTurnover(), p1.getTurnover()));
							}
						//TEAM 1
			   			if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTurnover()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTurnover()+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getTurnover()+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTurnover()+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTurnover()+ "\0");
			   	   			
			    			
			    			}
			    		}
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTurnover() <= 0);
						       Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTurnover(), p1.getTurnover()));
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
				    		if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(0).getTurnover()+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getTurnover()+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getTurnover()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   					 +data.getTeam().get(1).getTeamPlayer().get(0).getTurnover()+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getTurnover()+ "\0");
				   	   			
				   	   			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL TURNOVER "+ "\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
			   				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
			   				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
		   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTurnover()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTurnover()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTurnover()==0 && data.getTeam().get(1).getTurnover()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTurnover()==0 && data.getTeam().get(1).getTurnover()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTurnover()*401)/(data.getTeam().get(0).getTurnover()+data.getTeam().get(1).getTurnover()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTurnover()*401)/(data.getTeam().get(0).getTurnover()+data.getTeam().get(1).getTurnover())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTurnover()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTurnover()*401)/(data.getTeam().get(0).getTurnover()+data.getTeam().get(1).getTurnover()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTurnover()*401)/(data.getTeam().get(0).getTurnover()+data.getTeam().get(1).getTurnover())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
	}

	private void populateCross(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "MOST CROSSES \0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
							 + "1\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
							 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
							 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
						
						
				   		
				   		synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalCross() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalCross(), p1.getTotalCross()));
						}
						//TEAM 1
				   		if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalCross()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalCross()+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getTotalCross()+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalCross()+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalCross()+ "\0");			    			
			    			}
			    		  }
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalCross() <= 0);
								Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalCross(), p1.getTotalCross()));
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
				    		if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalCross()+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalCross()+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getTotalCross()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalCross()+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalCross()+ "\0");
				   	   			
				   	   			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");

			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL CROSSES "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTotalCross()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTotalCross()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTotalCross()==0 && data.getTeam().get(1).getTotalCross()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalCross()==0 && data.getTeam().get(1).getTotalCross()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTotalCross()*401)/(data.getTeam().get(0).getTotalCross()+data.getTeam().get(1).getTotalCross()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTotalCross()*401)/(data.getTeam().get(0).getTotalCross()+data.getTeam().get(1).getTotalCross())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalCross()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTotalCross()*401)/(data.getTeam().get(0).getTotalCross()+data.getTeam().get(1).getTotalCross()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTotalCross()*401)/(data.getTeam().get(0).getTotalCross()+data.getTeam().get(1).getTotalCross())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
	}

	private void populateInterception(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "MOST INTERCEPTIONS \0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								+ "3\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								   + "3\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								   + "0\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
							+ "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
						
				   		
				   		synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getInterception() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getInterception(), p1.getInterception()));
						}
						//TEAM 1
				   		if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getInterception()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getInterception()+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getInterception()+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(0).getInterception()+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(1).getInterception()+ "\0");			    			
			    			}
			    		  }
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getInterception() <= 0);
								Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getInterception(), p1.getInterception()));
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
				    		if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(0).getInterception()+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getInterception()+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getInterception()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   					 +data.getTeam().get(1).getTeamPlayer().get(0).getInterception()+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getInterception()+ "\0");
				   	   			
				   	   			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");

			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL INTERCEPTIONS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getInterception()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getInterception()+ "\0");
			   		
			   		if(data.getTeam().get(0).getInterception()==0 && data.getTeam().get(1).getInterception()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getInterception()==0 && data.getTeam().get(1).getInterception()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getInterception()*401)/(data.getTeam().get(0).getInterception()+data.getTeam().get(1).getInterception()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getInterception()*401)/(data.getTeam().get(0).getInterception()+data.getTeam().get(1).getInterception())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getInterception()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getInterception()*401)/(data.getTeam().get(0).getInterception()+data.getTeam().get(1).getInterception()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getInterception()*401)/(data.getTeam().get(0).getInterception()+data.getTeam().get(1).getInterception())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	private void populateBallRecovery(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "MOST BALL RECOVERY \0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
						
				   		
				   		synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getBallRecovery() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getBallRecovery(), p1.getBallRecovery()));
						}
				   	//TEAM 1
			   			if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getBallRecovery()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getBallRecovery()+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getBallRecovery()+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(0).getBallRecovery()+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(1).getBallRecovery()+ "\0");
			    			
			    				}
			    			}
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getBallRecovery() <= 0);
						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getBallRecovery(), p1.getBallRecovery()));
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
				    		if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(0).getBallRecovery()+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getBallRecovery()+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getBallRecovery()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   					 +data.getTeam().get(1).getTeamPlayer().get(0).getBallRecovery()+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getBallRecovery()+ "\0");
				   	   			
				   	   			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL BALL RECOVERY "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getBallRecovery()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getBallRecovery()+ "\0");
			   		
			   		if(data.getTeam().get(0).getBallRecovery()==0 && data.getTeam().get(1).getBallRecovery()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getBallRecovery()==0 && data.getTeam().get(1).getBallRecovery()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getBallRecovery()*401)/(data.getTeam().get(0).getBallRecovery()+data.getTeam().get(1).getBallRecovery()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getBallRecovery()*401)/(data.getTeam().get(0).getBallRecovery()+data.getTeam().get(1).getBallRecovery())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getBallRecovery()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getBallRecovery()*401)/(data.getTeam().get(0).getBallRecovery()+data.getTeam().get(1).getBallRecovery()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getBallRecovery()*401)/(data.getTeam().get(0).getBallRecovery()+data.getTeam().get(1).getBallRecovery())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	private void populateThrows(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				   				 + "THROWS\0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalThrows() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalThrows(), p1.getTotalThrows()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
			    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getTotalThrows()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalThrows()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getTotalThrows()+ "\0");
			   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			          				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getTotalThrows() +"\0");	       			
			   	       			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalThrows()+"\0");
			    			}
			    		}
			    		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalThrows() <= 0);
								Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalThrows(), p1.getTotalThrows()));
							}

							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTotalThrows()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalThrows()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTotalThrows()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				          					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTotalThrows()+ "\0");
				   	       			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalThrows()+ "\0");
				    			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL THROWS"+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTotalThrows()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTotalThrows()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTotalThrows()==0 && data.getTeam().get(1).getTotalThrows()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalThrows()==0 && data.getTeam().get(1).getTotalThrows()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTotalThrows()*401)/(data.getTeam().get(0).getTotalThrows()+data.getTeam().get(1).getTotalThrows()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTotalThrows()*401)/(data.getTeam().get(0).getTotalThrows()+data.getTeam().get(1).getTotalThrows())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalThrows()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTotalThrows()*401)/(data.getTeam().get(0).getTotalThrows()+data.getTeam().get(1).getTotalThrows()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTotalThrows()*401)/(data.getTeam().get(0).getTotalThrows()+data.getTeam().get(1).getTotalThrows())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
		 GeneratePreview(print_writer);
	}

	private void populateTotalPass(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
		        		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST PASSES \0");
		        		 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalPass() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalPass(), p1.getTotalPass()));
						}

				   	//TEAM 1

		        		if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getTotalPass()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalPass()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getTotalPass()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getTotalPass() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalPass()+"\0");
		        			}
		        		}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalPass() <= 0);
								Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalPass(), p1.getTotalPass()));
							}

							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTotalPass()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalPass()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTotalPass()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTotalPass()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalPass()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL PASSES "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTotalPass()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTotalPass()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTotalPass()==0 && data.getTeam().get(1).getTotalPass()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalPass()==0 && data.getTeam().get(1).getTotalPass()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTotalPass()*401)/(data.getTeam().get(0).getTotalPass()+data.getTeam().get(1).getTotalPass()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTotalPass()*401)/(data.getTeam().get(0).getTotalPass()+data.getTeam().get(1).getTotalPass())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalPass()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTotalPass()*401)/(data.getTeam().get(0).getTotalPass()+data.getTeam().get(1).getTotalPass()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTotalPass()*401)/(data.getTeam().get(0).getTotalPass()+data.getTeam().get(1).getTotalPass())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
		
	}

	private void populateShotOffTarget(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 		    print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
		        		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST SHOT OFF TARGET\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getShotOffTarget() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getShotOffTarget(), p1.getShotOffTarget()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getShotOffTarget()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getShotOffTarget()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getShotOffTarget()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getShotOffTarget() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getShotOffTarget()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getShotOffTarget() <= 0);
								Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getShotOffTarget(), p1.getShotOffTarget()));
							}

							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getShotOffTarget()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getShotOffTarget()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getShotOffTarget()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getShotOffTarget()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getShotOffTarget()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   			 +"TOTAL SHOT OFF TARGET "+ "\0");				   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getShotOffTarget()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getShotOffTarget()+ "\0");
			   		
			   		if(data.getTeam().get(0).getShotOffTarget()==0 && data.getTeam().get(1).getShotOffTarget()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getShotOffTarget()==0 && data.getTeam().get(1).getShotOffTarget()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getShotOffTarget()*401)/(data.getTeam().get(0).getShotOffTarget()+data.getTeam().get(1).getShotOffTarget()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getShotOffTarget()*401)/(data.getTeam().get(0).getShotOffTarget()+data.getTeam().get(1).getShotOffTarget())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getShotOffTarget()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getShotOffTarget()*401)/(data.getTeam().get(0).getShotOffTarget()+data.getTeam().get(1).getShotOffTarget()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getShotOffTarget()*401)/(data.getTeam().get(0).getShotOffTarget()+data.getTeam().get(1).getShotOffTarget())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
	}

	private void populateCornerTaken(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST CORNER TAKEN\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getCornerTaken() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getCornerTaken(), p1.getCornerTaken()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getCornerTaken()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getCornerTaken()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getCornerTaken()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getCornerTaken() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getCornerTaken()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getCornerTaken() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getCornerTaken(), p1.getCornerTaken()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getCornerTaken()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getCornerTaken()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getCornerTaken()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getCornerTaken()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getCornerTaken()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL CORNERS  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getCornerTaken()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getCornerTaken()+ "\0");
			   		
			   		if(data.getTeam().get(0).getCornerTaken()==0 && data.getTeam().get(1).getCornerTaken()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getCornerTaken()==0 && data.getTeam().get(1).getCornerTaken()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getCornerTaken()*401)/(data.getTeam().get(0).getCornerTaken()+data.getTeam().get(1).getCornerTaken()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getCornerTaken()*401)/(data.getTeam().get(0).getCornerTaken()+data.getTeam().get(1).getCornerTaken())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getCornerTaken()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getCornerTaken()*401)/(data.getTeam().get(0).getCornerTaken()+data.getTeam().get(1).getCornerTaken()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getCornerTaken()*401)/(data.getTeam().get(0).getCornerTaken()+data.getTeam().get(1).getCornerTaken())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	private void populatemultidata(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
				
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			switch(Lband.getWhich_Right_LBand_onscreen().toUpperCase() ){
			
			 case "YELLOW_CARD":
				 switch(valueToProcess.toUpperCase()) {
				 	case "RIGHT":
				 		if(data.getTeam().get(0).getYellowCards()!=null){

							synchronized (data.getTeam().get(0).getYellowCards()) {
								data.getTeam().get(0).getYellowCards().removeIf(player -> player.getYellowCard() <= 0);
								Collections.sort(data.getTeam().get(0).getYellowCards(), (p1, p2) -> Integer.compare(p2.getYellowCard(), p1.getYellowCard()));
							}
							//if(!data.getTeam().get(0).getYellowCards().isEmpty()) {
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				        				+ "MOST YELLOW CARD \0");
					 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
										 + "1\0");
//						        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
//										 + "3\0");
//						        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
//										 + "3\0");
						       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
										 + "0\0");
						   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
										 + "0\0");
						   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
										 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
						   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
								
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
										 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
						   		

//							}
				 			
					   	//TEAM 1
							if(data.getTeam().get(0).getYellowCards().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getYellowCards().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       				 + data.getTeam().get(0).getYellowCards().get(0).getYellowCard()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getYellowCards().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getYellowCards().get(1).getYellowCard()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getYellowCards().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getYellowCards().get(2).getYellowCard()+ "\0");
			       			
			        		}else if(data.getTeam().get(0).getYellowCards().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(0).getTeamPlayer().size()+" \0");
			        			if(data.getTeam().get(0).getYellowCards().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              				+ data.getTeam().get(0).getYellowCards().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       				+ data.getTeam().get(0).getYellowCards().get(0).getYellowCard() +"\0");	       			
			       	       			
			        			}
			        			if(data.getTeam().get(0).getYellowCards().size()==0) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " +"0 " +"\0");
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET "+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " + "\0");			       	       			
			        			}
			        			if(data.getTeam().get(0).getYellowCards().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 + data.getTeam().get(0).getYellowCards().get(1).getName()+ "\0");
			   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	       					 + data.getTeam().get(0).getYellowCards().get(1).getYellowCard()+"\0");
			        				}
			        			}
			        		
				 			}
							if(data.getTeam().get(1).getYellowCards()!=null){
								synchronized (data.getTeam().get(1).getYellowCards()) {
									data.getTeam().get(1).getYellowCards().removeIf(player -> player.getYellowCard() <= 0);

							        Collections.sort(data.getTeam().get(1).getYellowCards(), (p1, p2) -> Integer.compare(p2.getYellowCard(), p1.getYellowCard()));     
							   	}
								//if(!data.getTeam().get(1).getYellowCards().isEmpty()) {
									print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
											 + data.getTeam().get(1).getName().toUpperCase()+"\0");
									print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
											 + "3\0");
									print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
											 + "0\0");
									print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
									print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
											 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
									
								//}
								//TEAM 2
								if(data.getTeam().get(1).getYellowCards().size()>=3) {
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				        					+ data.getTeam().get(1).getYellowCards().get(0).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getYellowCards().get(0).getYellowCard()+ "\0");
					       			
					       			
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getYellowCards().get(1).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getYellowCards().get(1).getYellowCard()+ "\0");
					       			
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getYellowCards().get(2).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getYellowCards().get(2).getYellowCard()+ "\0");
					       			
				        		}else if(data.getTeam().get(1).getYellowCards().size()< 3) {
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				        			
				        			if(data.getTeam().get(1).getYellowCards().size()>=1) {
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				              					 + data.getTeam().get(1).getYellowCards().get(0).getName()+ "\0");
				       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       	       					 + data.getTeam().get(1).getYellowCards().get(0).getYellowCard()+ "\0");
				       	       			
				        			}
				        			if(data.getTeam().get(1).getYellowCards().size()==0) {
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " +"0 " +"\0");
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET "+ "\0");
				       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " + "\0");			       	       			
				        			}
				        			if(data.getTeam().get(1).getYellowCards().size()==2) {
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	       					 	+ data.getTeam().get(1).getYellowCards().get(1).getName()+ "\0");
					   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   	       					+ data.getTeam().get(1).getYellowCards().get(1).getYellowCard()+ "\0");
				        			}
				        		}
							}
					  break;
				 }
			 break;
			 case"RED_CARD":
				 switch(valueToProcess.toUpperCase()) {
				 	case "RIGHT":
				 		if(data.getTeam().get(0).getYellowCards()!=null){
				 			
				 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
			        				+ "MOST RED CARDS \0");
				 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
									 + "1\0");
					        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
					       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
					       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
					   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
					   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
					   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
					   		
							synchronized (data.getTeam().get(0).getRedCards()) {
								data.getTeam().get(0).getRedCards().removeIf(player -> player.getRedCard() <= 0);
								Collections.sort(data.getTeam().get(0).getRedCards(), (p1, p2) -> Integer.compare(p2.getRedCard(), p1.getRedCard()));
							}

					   	//TEAM 1

							if(data.getTeam().get(0).getRedCards().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getRedCards().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       				 + data.getTeam().get(0).getRedCards().get(0).getRedCard()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getRedCards().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getRedCards().get(1).getRedCard()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getRedCards().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getRedCards().get(2).getRedCard()+ "\0");
			       			
			        		}else if(data.getTeam().get(0).getRedCards().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			        			if(data.getTeam().get(0).getRedCards().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              				+ data.getTeam().get(0).getRedCards().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       				+ data.getTeam().get(0).getRedCards().get(0).getRedCard() +"\0");	       			
			       	       			
			        			}
			        			if(data.getTeam().get(0).getRedCards().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 + data.getTeam().get(0).getRedCards().get(1).getName()+ "\0");
			   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	       					 + data.getTeam().get(0).getRedCards().get(1).getRedCard()+"\0");
			        				}
			        			}
			        		
				 			}
							if(data.getTeam().get(1).getRedCards()!=null){
								synchronized (data.getTeam().get(1).getRedCards()) {
									data.getTeam().get(1).getRedCards().removeIf(player -> player.getRedCard() <= 0);

							        Collections.sort(data.getTeam().get(1).getRedCards(), (p1, p2) -> Integer.compare(p2.getRedCard(), p1.getRedCard()));     
							   	}
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
										 + data.getTeam().get(1).getName().toUpperCase()+"\0");
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
										 + "3\0");
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
										 + "0\0");
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
								print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
										 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
								
								//TEAM 2
								if(data.getTeam().get(1).getRedCards().size()>=3) {
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				        					+ data.getTeam().get(1).getRedCards().get(0).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getRedCards().get(0).getRedCard()+ "\0");
					       			
					       			
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getRedCards().get(1).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getRedCards().get(1).getRedCard()+ "\0");
					       			
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getRedCards().get(2).getName()+ "\0");
					       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					       					+ data.getTeam().get(1).getRedCards().get(2).getRedCard()+ "\0");
					       			
				        		}else if(data.getTeam().get(1).getRedCards().size()< 3) {
				        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				        			
				        			if(data.getTeam().get(1).getRedCards().size()>=1) {
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				              					 + data.getTeam().get(1).getRedCards().get(0).getName()+ "\0");
				       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       	       					 + data.getTeam().get(1).getRedCards().get(0).getRedCard()+ "\0");
				       	       			
				        			}
				        			
				        			if(data.getTeam().get(1).getRedCards().size()==2) {
				        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	       					 	+ data.getTeam().get(1).getRedCards().get(1).getName()+ "\0");
					   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   	       					+ data.getTeam().get(1).getRedCards().get(1).getRedCard()+ "\0");
				        			}
				        		}
							}
					  break;
				 }
			 break;
			}
		switch(Lband.getWhich_Bottom_LBand_onscreen().toUpperCase() ){
	        case "POSSESSION":
	        	 switch(valueToProcess.toUpperCase()) {
	      		case "BOTTOM":
	      		
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
	          				 + "0\0");
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Change$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
	          				 + "1\0");
	          		
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
	          				 +"MATCH STATS "+ "\0");
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	          				 +"TOTAL POSSESSION "+ "\0");
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
	          				 +data.getTeam().get(0).getPossession()+ "\0");
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
	          				+data.getTeam().get(1).getPossession()+ "\0");
	          		
	          		if(data.getTeam().get(0).getPossession()==0 && data.getTeam().get(1).getPossession()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getPossession()==0 && data.getTeam().get(1).getPossession()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getPossession()*401)/(data.getTeam().get(0).getPossession()+data.getTeam().get(1).getPossession()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getPossession()*401)/(data.getTeam().get(0).getPossession()+data.getTeam().get(1).getPossession())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getPossession()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getPossession()*401)/(data.getTeam().get(0).getPossession()+data.getTeam().get(1).getPossession()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getPossession()*401)/(data.getTeam().get(0).getPossession()+data.getTeam().get(1).getPossession())))+ "\0");
				   		
			   		}
	          		
	          	/****** bottom logos*****/
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
	          				 + flag_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
	          		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
	          				 + flag_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
	      		break;
	        }
	        	 
	        	break;
	        	case "YELLOW_CARD":
	        		 switch(valueToProcess.toUpperCase()) {
	             		case "BOTTOM":
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
		             				 + "0\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Change$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
		             				 + "1\0");
		             		
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
		             				 +"MATCH STATS "+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
		             				 +"TOTAL YELLOW CARDS "+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
		             				 +data.getTeam().get(0).getYellowCard()+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
		             				+data.getTeam().get(1).getYellowCard()+ "\0");
		             		if(data.getTeam().get(0).getYellowCard()==0 &&data.getTeam().get(1).getYellowCard()==0) {
		             			//first bar
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
			             				 +(401/2)+ "\0");
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
			             				 +(401-((401/2)))+ "\0");
			             		
		             		}else if(data.getTeam().get(0).getYellowCard()==0 && data.getTeam().get(1).getYellowCard()>0) {
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
						   				 +((data.getTeam().get(1).getYellowCard()*401)/(data.getTeam().get(0).getYellowCard()+data.getTeam().get(1).getYellowCard()))+ "\0");
						   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
						   				 +(401-((data.getTeam().get(1).getYellowCard()*401)/(data.getTeam().get(0).getYellowCard()+data.getTeam().get(1).getYellowCard())))+ "\0");
						   		
					   		}else {
		             			//first bar
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
			             				 +((data.getTeam().get(0).getYellowCard()*401)/(data.getTeam().get(0).getYellowCard()+data.getTeam().get(1).getYellowCard()))+ "\0");
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
			             				 +(401-((data.getTeam().get(0).getYellowCard()*401)/(data.getTeam().get(0).getYellowCard()+data.getTeam().get(1).getYellowCard())))+ "\0");
			             		
		             		}
		             		
		             	/****** bottom logos*****/
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
		             				 + flag_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
		             				 + flag_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
	             		break;
	             }
	        		break;
	        	case"RED_CARD":
	        		 switch(valueToProcess.toUpperCase()) {
	             		case "BOTTOM":
	                   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
		             				 + "0\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Change$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
		             				 + "1\0");
		             		
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
		             				 +"MATCH STATS "+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
		             				 +"TOTAL RED CARDS "+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
		             				 +data.getTeam().get(0).getRedCard()+ "\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
		             				+data.getTeam().get(1).getRedCard()+ "\0");
		             		if(data.getTeam().get(0).getRedCard()==0 &&data.getTeam().get(1).getRedCard()==0) {

		             			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
			             				 +(401/2)+ "\0");
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
			             				 +(401-((401/2)))+ "\0");
			             		
		             		}else if(data.getTeam().get(0).getRedCard()==0 && data.getTeam().get(1).getRedCard()>0) {
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
						   				 +((data.getTeam().get(1).getRedCard()*401)/(data.getTeam().get(0).getRedCard()+data.getTeam().get(1).getRedCard()))+ "\0");
						   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
						   				 +(401-((data.getTeam().get(1).getRedCard()*401)/(data.getTeam().get(0).getRedCard()+data.getTeam().get(1).getRedCard())))+ "\0");
						   		
					   		}else {

			             		//first bar
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
			             				 +((data.getTeam().get(0).getRedCard()*401)/(data.getTeam().get(0).getRedCard()+data.getTeam().get(1).getRedCard()))+ "\0");
			             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
			             				 +(401-((data.getTeam().get(0).getYellowCard()*401)/(data.getTeam().get(0).getRedCard()+data.getTeam().get(1).getRedCard())))+ "\0");
			             	
		             		}

		             	/****** bottom logos*****/
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
		             				 + flag_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		             		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
		             				 + flag_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
	             		break;
	             }
	        		break;
	        }		
		}
		
	}

	private void populateTotalOffSide(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST CORNER TAKEN\0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalOffside() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalOffside(), p1.getTotalOffside()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getTotalOffside()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalOffside()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getTotalOffside()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getTotalOffside() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalOffside()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalOffside() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalOffside(), p1.getTotalOffside()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTotalOffside()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalOffside()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTotalOffside()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTotalOffside()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalOffside()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL CORNERS  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTotalOffside()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTotalOffside()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTotalOffside()==0 && data.getTeam().get(1).getTotalOffside()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalOffside()==0 && data.getTeam().get(1).getTotalOffside()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTotalOffside()*401)/(data.getTeam().get(0).getTotalOffside()+data.getTeam().get(1).getTotalOffside()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTotalOffside()*401)/(data.getTeam().get(0).getTotalOffside()+data.getTeam().get(1).getTotalOffside())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalOffside()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTotalOffside()*401)/(data.getTeam().get(0).getTotalOffside()+data.getTeam().get(1).getTotalOffside()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTotalOffside()*401)/(data.getTeam().get(0).getTotalOffside()+data.getTeam().get(1).getTotalOffside())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
	}

	private void populateSaves(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST SAVES \0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getSaves() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSaves(), p1.getSaves()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getSaves()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getSaves()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getSaves()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getSaves() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getSaves()+"\0");
		        			}
		        		}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getSaves() <= 0);
						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSaves(), p1.getSaves()));     
						   	}
							
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getSaves()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getSaves()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getSaves()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getSaves()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getSaves()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL SAVES "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getSaves()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getSaves()+ "\0");
			   		
			   		if(data.getTeam().get(0).getSaves()==0 && data.getTeam().get(1).getSaves()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getSaves()==0 && data.getTeam().get(1).getSaves()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getSaves()*401)/(data.getTeam().get(0).getSaves()+data.getTeam().get(1).getSaves()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getSaves()*401)/(data.getTeam().get(0).getSaves()+data.getTeam().get(1).getSaves())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getSaves()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getSaves()*401)/(data.getTeam().get(0).getSaves()+data.getTeam().get(1).getSaves()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getSaves()*401)/(data.getTeam().get(0).getSaves()+data.getTeam().get(1).getSaves())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	private void populateBlockedScoringAtt(PrintWriter print_writer, FootballData data, int WhichSide,
			String valueToProcess) {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST BLOCKS \0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getBlockedScoringAtt() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getBlockedScoringAtt(), p1.getBlockedScoringAtt()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getBlockedScoringAtt()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getBlockedScoringAtt()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getBlockedScoringAtt()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getBlockedScoringAtt() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getBlockedScoringAtt()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getBlockedScoringAtt() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getBlockedScoringAtt(), p1.getBlockedScoringAtt()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getBlockedScoringAtt()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getBlockedScoringAtt()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getBlockedScoringAtt()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getBlockedScoringAtt()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getBlockedScoringAtt()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL BLOCKS  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getBlockedScoringAtt()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getBlockedScoringAtt()+ "\0");
			   		
			   		if(data.getTeam().get(0).getBlockedScoringAtt()==0 && data.getTeam().get(1).getBlockedScoringAtt()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getBlockedScoringAtt()==0 && data.getTeam().get(1).getBlockedScoringAtt()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getBlockedScoringAtt()*401)/(data.getTeam().get(0).getBlockedScoringAtt()+data.getTeam().get(1).getBlockedScoringAtt()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getBlockedScoringAtt()*401)/(data.getTeam().get(0).getBlockedScoringAtt()+data.getTeam().get(1).getBlockedScoringAtt())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getBlockedScoringAtt()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getBlockedScoringAtt()*401)/(data.getTeam().get(0).getBlockedScoringAtt()+data.getTeam().get(1).getBlockedScoringAtt()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getBlockedScoringAtt()*401)/(data.getTeam().get(0).getBlockedScoringAtt()+data.getTeam().get(1).getBlockedScoringAtt())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	private void populateOnTargetScoringAttack(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST SHOT ON TARGET\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getOntargetScoringAtt() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getOntargetScoringAtt(), p1.getOntargetScoringAtt()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getOntargetScoringAtt()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getOntargetScoringAtt()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getOntargetScoringAtt()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getOntargetScoringAtt() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getOntargetScoringAtt()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getOntargetScoringAtt() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getOntargetScoringAtt(), p1.getOntargetScoringAtt()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getOntargetScoringAtt()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getOntargetScoringAtt()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getOntargetScoringAtt()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getOntargetScoringAtt()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getOntargetScoringAtt()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL SHOT ON TARGET  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getOntargetScoringAtt()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getOntargetScoringAtt()+ "\0");
			   		
			   		if(data.getTeam().get(0).getOntargetScoringAtt()==0 && data.getTeam().get(1).getOntargetScoringAtt()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getOntargetScoringAtt()==0 && data.getTeam().get(1).getOntargetScoringAtt()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getOntargetScoringAtt()*401)/(data.getTeam().get(0).getOntargetScoringAtt()+data.getTeam().get(1).getOntargetScoringAtt()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getOntargetScoringAtt()*401)/(data.getTeam().get(0).getOntargetScoringAtt()+data.getTeam().get(1).getOntargetScoringAtt())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getOntargetScoringAtt()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getOntargetScoringAtt()*401)/(data.getTeam().get(0).getOntargetScoringAtt()+data.getTeam().get(1).getOntargetScoringAtt()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getOntargetScoringAtt()*401)/(data.getTeam().get(0).getOntargetScoringAtt()+data.getTeam().get(1).getOntargetScoringAtt())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	public Lband populateRightLband(Lband Lband,int WhichSide,PrintWriter print_writer,FootballService footballService) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError, InterruptedException, JAXBException {		
		
		setLBandData(Lband.getWhich_Right_LBand_onscreen());
		
		switch(Lband.getWhich_Right_LBand_onscreen().toUpperCase() ){
			case "TOTAL_FINAL_THIRD_PASSES":case"POSS_WON_ATT_3RD": case "TOUCHES":case "DB_TEAM":case"INSIGHTS_TEAM":
			case "TACKLE": case "ACCURATE_PASS": case "FOUL":case"DUEL_WON":case "CLEARANCE":case"H2H":
			case "TOTAL_PASS":case "SHOT_OFF_TARGET":case "CORNER_TAKEN":case "YELLOW_CARD":case "TOTAL_OFF_SIDE":case "SAVES":case "POSSESSION":
			case "BLOCKED_SCORING_ATTACK":case "SHOT_ON_TARGET":case"TOTAL_THROWS":case"RED_CARD": case "PLAYING_XI":
			case"BALL_RECOVERY":case "INTERCEPTIONS":case"TOTAL_CROSS":case"TURNOVER":case "POINTS_TABLE":
			if(data.getTeam()!=null && data.getTeam().size()==2) {	
				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$TeamGrp1$txt_TeamName*GEOM*TEXT SET " +data.getTeam().get(0).getCode() + "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$TeamGrp2$txt_TeamName*GEOM*TEXT SET " +data.getTeam().get(1).getCode()+ "\0");

				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$ScoreGrp$txt_Score1*GEOM*TEXT SET " + "    v" + "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$ScoreGrp$txt_Score2*GEOM*TEXT SET "  + "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$ScoreGrp$Separator*ACTIVE SET 0 \0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$TeamGrp1$img_Flag*TEXTURE*IMAGE SET " + flag_path + 
						data.getTeam().get(0).getCode()+"\0");
		   		print_writer.println("-1 RENDERER*TREE*$group$LBand$ScoreGrp$TeamGrp2$img_Flag*TEXTURE*IMAGE SET "  + flag_path + 
		   				data.getTeam().get(1).getCode()+"\0");
			}
			break;
		}
		switch(Lband.getWhich_Right_LBand_onscreen().toUpperCase()){
		case "ATTACKING_ZONE":
			populateAttacking(print_writer, data, WhichSide,Integer.valueOf(Lband.getPlaying_XI_Team().split(",")[2]));
			break;
		case "FOUL":
			populateFoul(print_writer, data, WhichSide, "RIGHT");
			break;
		case"TOTAL_THROWS":
			populateThrows(print_writer,data, WhichSide,"RIGHT");
			break;
		case"LWP":
			populateLWP(print_writer, data, WhichSide, "RIGHT");
			break;
		case"BALL_RECOVERY":
			populateBallRecovery(print_writer,data, WhichSide, "RIGHT");
			break;
		case "POINTS_TABLE":
			populatePointsTable(print_writer,data, WhichSide, "RIGHT");
			break;
		case "INTERCEPTIONS":
			populateInterception(print_writer,data, WhichSide, "RIGHT");
			break;
		case"TOTAL_CROSS":
			populateCross(print_writer,data, WhichSide, "RIGHT");
			break;
		case"TURNOVER":
			populateTurnover(print_writer,data, WhichSide, "RIGHT");
			break;
		case "TACKLE":
			populateTackle(print_writer, data, WhichSide,"RIGHT");
			break;
		case "ACCURATE_PASS":
			populateAccuratePass(print_writer, data, WhichSide,"RIGHT");
			break;
		case "TOTAL_FINAL_HIRD_PASSES":
			populateFinal3rdPass(print_writer, data, WhichSide);
			break;
		case"POSS_WON_ATT_3RD":
			populateWonAtt3rd(print_writer, data, WhichSide);
			break;
		case "WON_CORNERS":
			populateWonCorners(print_writer, data, WhichSide, "RIGHT");
			break;
		case"DUEL_WON":
			populateDuelWon(print_writer, data, WhichSide, "RIGHT");
			break;
		case"WON_CONTEST":
			populateDribbles(print_writer, data, WhichSide);
			break;
		case "TOUCHES":
			populateTouches(print_writer, data, WhichSide, "RIGHT");
			break;
		case "EXPECTED_GOALS":
			populateExpectedGoals(print_writer, data, WhichSide);
			break;
		case "EXA_EXG":
			populateExpectedGoalsForAgainst(print_writer, data, WhichSide);
			break;
		case "CLEARANCE":
			populateClearance(print_writer, data, WhichSide, "RIGHT");
			break;
		case "SET_PIECES":
			populateSetPiece(print_writer, data, WhichSide);
			break;
		case "SHOOTING_ACCURACY":
			populateShootingAccuracy(print_writer, data, WhichSide,"RIGHT");
			break;
		case "INSIGHTS":
			populateIngights(print_writer, data,WhichSide,vtp);
			break;
		case "INSIGHTS_GS":
			populateIngightsGs(print_writer, data,WhichSide,vtp);
			break;
		case"INSIGHTS_TEAM":
			populateIngightsTeam(print_writer, data,WhichSide,vtp);
			break;
		case "DB_EXTRA_DATA":
			for(ExtraData extraData : footballService.getExtraData()) {
			  if(extraData.getDataId() == Integer.valueOf(vtp)) {
				  populateExtraData(print_writer, extraData, data, WhichSide,footballService.getAllPlayer(), footballService.getTeams());
			  }
			}
			break;
		case "SPONSOR_EURO":
			populateSponsor(print_writer, data,sponsor_side,vtp);
			break;
		case "SPONSOR_BOTTOM":
			populateBottomSponsor(print_writer, data,sponsor_bottom,vtp);
			break;
		case"BUG-DB":
			for(Bugs bug : footballService.getBugs()) {
				  if(bug.getBugId() == Integer.valueOf(vtp)) {
					  populateVariousTextBottom(print_writer, bug, data,WhichSide);
				  }
				}
			break;
		case "PLAYING_XI":
			populatePlayingXI(print_writer, data, WhichSide, Integer.valueOf(Lband.getPlaying_XI_Team().split(",")[2]), 
					Lband.getPlaying_XI_Team().split(",")[3]);
			break;
		case "DB_TEAM":
			for(Bugs bug : footballService.getBugs()) {
				  if(bug.getBugId() == Integer.valueOf(Value.split(",")[2])) {
					  populateTeamDB(print_writer, bug, data,WhichSide,"RIGHT");
				  }
				}
			break;
		case "WIN_H2H":
			populateWin(print_writer, data,WhichSide,"RIGHT");
			break;
		case "H2H":
			populateH2H(print_writer, data,WhichSide,"RIGHT");
			break;
		case "FT":
			populateFT(print_writer, data,WhichSide);
			break;
		case"H2H_LIVE_WIN":
			populateLiveH2H(print_writer, data,WhichSide, "RIGHT");
			break;
		case "PLAYER_RATING":
			populateplayerRating(print_writer, data,WhichSide,footballService.getAllPlayer());
			break;
		case "INSIGHTS_RESULT":
			populateIngightsResult(print_writer,data,WhichSide,vtp);
			break;
		case "TOTAL_PASS":
			populateTotalPass(print_writer,data, WhichSide, "RIGHT");
			break;
		case "SHOT_OFF_TARGET":
			populateShotOffTarget(print_writer,data, WhichSide, "RIGHT");
			break;
		case "CORNER_TAKEN":
			populateCornerTaken(print_writer,data, WhichSide, "RIGHT");
			break;
		case "YELLOW_CARD":case"RED_CARD":
			populatemultidata(print_writer,data, WhichSide, "RIGHT");
			break;
		case "TOTAL_OFF_SIDE":
			populateTotalOffSide(print_writer,data, WhichSide, "RIGHT");
			break;
		case "SAVES":
			populateSaves(print_writer,data, WhichSide, "RIGHT");
			break;
		case "BLOCKED_SCORING_ATTACK":
			populateBlockedScoringAtt(print_writer,data, WhichSide, "RIGHT");
			break;
		case "SHOT_ON_TARGET":
			populateOnTargetScoringAttack(print_writer,data, WhichSide, "RIGHT");
			break;
		case "TOTAL_FINAL_THIRD_PASSES":
			populateFinal3rdPass(print_writer,data, WhichSide);
			break;
		}
		return Lband;
	
	}
	
	private void populatePointsTable(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) throws JAXBException {
		 switch(valueToProcess.toUpperCase()) {
	     	case "RIGHT":
	     	print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
	   				 + "1\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
	   				 + "3\0");
	        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
	   				 + "3\0");
	        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
	    	print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
	   				 + "LAST 3 MATCHES \0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
	   				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
	   				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	   	/****** right logos ****/
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
	   		
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
	   				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
	   				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
	   		
	   //1st team
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
	   				 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
	   		
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
	   				 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
	   		
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
	   				 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
	   //2nd team
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
	   				+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
	   		
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
	   				+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
	   		
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
	   				+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
	   				+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
	   		   		
	     		break;
	    	case "BOTTOM":
	    		LeagueTable group = null;
	    		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
		   				 + "4\0");
	    		if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA.xml").exists()) {
					group = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA.xml"));
				}
	    		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
    					+ "PointsGrp$HeadGrp$txt_Header*GEOM*TEXT SET "+"GROUP A " +"\0");
	    		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
    					+ "PointsGrp$GroupHeadGrp$txt_Group*GEOM*TEXT SET "+"POINTS TABLE" +"\0");
	    		for(int i=0; i<group.getLeagueTeams().size();i++) {
	    			
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$txt_Rank*GEOM*TEXT SET " 
			   				 +(i+1)+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getTeamName()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getPlayed()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_WinValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getWon()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getDrawn()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_LostValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getLost()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getGD()+ "\0");
	    			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$"
	    					+ "PointsGrp$Change$PointsTable$PointsData$Row"+(i+1)+"$Dehighlight$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " 
			   				 +group.getLeagueTeams().get(i).getPoints()+ "\0");
	    		}
	     		break;
		
		 }
		}
	public void populateTouches(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		if(data.getTeam()!=null) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST TOUCHES \0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTouches(), p1.getTouches()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getTouches()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTouches()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getTouches()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getTouches() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTouches()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTouches() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTouches(), p1.getTouches()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTouches()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTouches()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTouches()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTouches()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTouches()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL TOUCHES  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTouches()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTouches()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTouches()==0 && data.getTeam().get(1).getTouches()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTouches()==0 && data.getTeam().get(1).getTouches()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTouches()*401)/(data.getTeam().get(0).getTouches()+data.getTeam().get(1).getTouches()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTouches()*401)/(data.getTeam().get(0).getTouches()+data.getTeam().get(1).getTouches())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTouches()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTouches()*401)/(data.getTeam().get(0).getTouches()+data.getTeam().get(1).getTouches()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTouches()*401)/(data.getTeam().get(0).getTouches()+data.getTeam().get(1).getTouches())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}
	public void populateTackle(PrintWriter print_writer, FootballData data,int WhichSide, String valueToProcess) throws StreamReadException, DatabindException, IOException {
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
		        				+ "MOST TACKLES \0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalTackle() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalTackle(), p1.getTotalTackle()));
						}

				   	//TEAM 1

						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
		        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		       					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getTotalTackle()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalTackle()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getTotalTackle()+ "\0");
		       			
		        		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
		        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
		            				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
		        			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
		              				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
		       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
		       	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getTotalTackle() +"\0");	       			
		       	       			
		        			}
		        			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
		        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
		   	       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
		   	       					 + data.getTeam().get(0).getTeamPlayer().get(1).getTotalTackle()+"\0");
		        				}
		        			}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalTackle() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalTackle(), p1.getTotalTackle()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTotalTackle()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalTackle()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTotalTackle()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTotalTackle()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalTackle()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	        				 +"TOTAL TACKLES  "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTackle()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTackle()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTackle()==0 && data.getTeam().get(1).getTackle()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTackle()==0 && data.getTeam().get(1).getTackle()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTackle()*401)/(data.getTeam().get(0).getTackle()+data.getTeam().get(1).getTackle()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTackle()*401)/(data.getTeam().get(0).getTackle()+data.getTeam().get(1).getTackle())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTackle()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTackle()*401)/(data.getTeam().get(0).getTackle()+data.getTeam().get(1).getTackle()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTackle()*401)/(data.getTeam().get(0).getTackle()+data.getTeam().get(1).getTackle())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}
	
	public void populatePlayingXI(PrintWriter print_writer, FootballData data,int WhichSide, int Team_Value, String which_sponsor) throws StreamReadException, DatabindException, IOException {
		
   		int row_id = 0,TeamId=0;
   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$Select_DataType*FUNCTION*Omo*vis_con SET 5 \0");
   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$Select_DataType$Sponsor$event*TEXTURE*IMAGE SET " + 
   				sponsor_path + "sponsor" + which_sponsor + "\0");
   		
   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET 0 \0");
   		
   		if(Team_Value == 0 || Team_Value == 2) {
   			TeamId = 0;
   		}else if(Team_Value == 1 || Team_Value == 3) {
   			TeamId = 1;
   		}
   		
   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$TeamNameGrp$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(TeamId).getName().toUpperCase() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$TeamNameGrp$img_Flag*TEXTURE*IMAGE SET " 
				 + flag_path + data.getTeam().get(TeamId).getCode() +"\0");
		
		if(Team_Value == 0 || Team_Value == 1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side" + WhichSide + "$Header*GEOM*TEXT SET STARTING 11\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side" + WhichSide + "$Header*GEOM*TEXT SET SUBSTITUTE\0");
		}
		
   		for(com.football.containers.Players plyr : data.getTeam().get(TeamId).getTeamPlayer()) {
   			if(Team_Value == 0 || Team_Value == 1) {
   				if(plyr.getPosition() != null && !plyr.getPosition().equalsIgnoreCase("Substitute")) {
   	   				row_id = row_id + 1;
   	   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id + 
   	   						"$PlayerName*GEOM*TEXT SET " + plyr.getName() +"\0");
   	   				
   	   				if(plyr.getPosition().equalsIgnoreCase("Goalkeeper")) {
   	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
   	   							"$Select_Goalkeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
   	   				}else {
   	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
   	   							"$Select_Goalkeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
   	   				}
   	   				
   	   				if(plyr.getCaptain() != null && plyr.getCaptain().equalsIgnoreCase(FootballUtil.YES)) {
   	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
   	   							"$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
   	   				}else {
   	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
   	   							"$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
   	   				}
   	   			}
   	   		}else if(Team_Value == 2 || Team_Value == 3) {
	   	   		if(plyr.getPosition() != null && plyr.getPosition().equalsIgnoreCase("Substitute")) {
	   				row_id = row_id + 1;
	   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id + 
	   						"$PlayerName*GEOM*TEXT SET " + plyr.getName() +"\0");
	   				
	   				if(plyr.getSubPosition().equalsIgnoreCase("Goalkeeper")) {
	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
	   							"$Select_Goalkeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
	   				}else {
	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
	   							"$Select_Goalkeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
	   				}
	   				
	   				if(plyr.getCaptain() != null && plyr.getCaptain().equalsIgnoreCase(FootballUtil.YES)) {
	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
	   							"$Select_Captain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
	   				}else {
	   					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$LineUp$TeamDataAll$" + row_id +
	   							"$Select_Captain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
	   				}
	   			}
   	   		}
   		}
	}
	
	public void populateLWP(PrintWriter print_writer, FootballData data, int WhichSide,String value) {
		switch(value.toUpperCase()) {
		case"RIGHT":
			 /***********************************RIGHT*************************************/		
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
					 + "1\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
					 + "3\0");
	        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
					 + "3\0");
	       
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
					+ "HEAD TO HEAD \0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
					 +"OVERALL"+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
					 + "LAST 5 MATCHES "+"\0");
			
		/****** right logos ****/
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 0 "+"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 0 "+"\0");
			
	//1st team
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					+"INDIA WON \0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					+data.getHomeContestantWins()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					+"KUWAIT WON \0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					+data.getAwayContestantWins()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +"DRAWS"+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					+"2"+ "\0");
	//2nd team
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +"INDIA WON"+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					+"2"+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					+"KUWAIT WON"+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					+"2"+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					+"DRAWS"+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					+"2"+ "\0");
			
			break;
		case"BOTTOM":

			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
					 + "0\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
					 + "1\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
					 +"LIVE WIN PROBABILITY"+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
					 +"WINNING PROBABILITY"+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
					 +data.getWinHomeProbability()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
					+data.getWinAwayProbability()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
					 +data.getWinHomeProbability()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
					+data.getWinAwayProbability()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
					 +(401-data.getWinHomeProbability())+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
					 +(401-data.getWinAwayProbability())+ "\0");
			
		/****** bottom logos*****/
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
					 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
					 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

			break;
			
		}
       
/***********************************BOTTOM*************************************/		

		
	}
	public void populateIngightsTeam(PrintWriter print_writer, FootballData data, int WhichSide, String ValueToProcess) {
//		
//        /***********************************RIGHT*************************************/		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
//				 + "1\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
//				 + "3\0");
//       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
//				 + "3\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
//				 + "TEAM MATCH STATS\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
//				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
//				 + data.getTeam().get(1).getName().toUpperCase()+"\0");
//
//	/****** right logos ****/
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
//				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
//				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
//		
////1st team
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
//				 +"POSSESSION (%)"+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
//				 + data.getTeam().get(0).getPossession()+"\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
//				 +"TOTAL PASSES"+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
//				+ data.getTeam().get(0).getTotalPass() + "\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
//				 + "TOTAL TACKLES"+"\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
//				+ data.getTeam().get(0).getTackle() + "\0");
////2nd team
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
//				+"POSSESSION (%)"+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
//				+ data.getTeam().get(1).getPossession() + "\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
//				+"TOTAL PASSES"+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
//				+ data.getTeam().get(1).getTotalPass()  + "\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
//				+ "TOTAL TACKLES" + "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
//				+ data.getTeam().get(1).getTackle()  + "\0");
//		
///***********************************BOTTOM*************************************/		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
//				 + "2\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
//				 +vtp+ "\0");
//		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
//				 +"   "+ "\0");
//		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
//				 +" "+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
//				 +" "+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
//				+""+ "\0");
//		//first bar
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
//				 +""+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
//				 +""+ "\0");
//		
//		//second bar
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$SubHead*GEOM*TEXT SET " 
//				 +" "+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$HomeValue*GEOM*TEXT SET " 
//				+""+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$AwayValue*GEOM*TEXT SET " 
//				+""+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
//				+""+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
//				+""+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 + vtp + "\0");
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	public void populateIngightsResult(PrintWriter print_writer, FootballData data, int WhichSide, String ValueToProcess) {
		
		/***********************************RIGHT*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "LAST 3 MATCHES \0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
//1st team
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
//2nd team
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
		
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 +vtp+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"   "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +" "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +" "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+""+ "\0");
		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 + vtp + "\0");
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	public void populateLiveH2H(PrintWriter print_writer, FootballData data, int WhichSide, String value) {
		

		switch(value.toUpperCase()) {
			case "RIGHT":
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "1\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
		   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
		       
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
						 + "LAST 3 MATCHES \0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 + data.getTeam().get(1).getName().toUpperCase()+"\0");

			/****** right logos ****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
				
		//1st team
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
		//2nd team
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
				
			break;
			case "BOTTOM":
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
						 + "1\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
						 +"LIVE WIN PROBABILITY"+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
						 +"WINNING PROBABILITY"+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
						 +data.getWinHomeProbability()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
						+data.getWinAwayProbability()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
						 +data.getWinHomeProbability()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
						+data.getWinAwayProbability()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
						 +Double.valueOf(((data.getWinHomeProbability() + data.getWinDrawProbability())*401)/100.0)+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
						 +(401-(Double.valueOf(((data.getWinHomeProbability() + data.getWinDrawProbability())*401)/100.0)))+ "\0");
				
			/****** bottom logos*****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

			break;
		}				
	}
	public void populateH2H(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
        
		switch(valueToProcess) {
		case "RIGHT":
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
					 + "1\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
					 + "3\0");
	       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
					 + "3\0");
	       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
					 + "0\0");
	   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
					 + "0\0");
	       
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
					 + "LAST 3 MATCHES \0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
					 + data.getTeam().get(1).getName().toUpperCase()+"\0");

		/****** right logos ****/
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
					 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
					 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
			
	//1st team
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
	//2nd team
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
			
			break;
		case "BOTTOM":

			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
					 + "1\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$HeadToHead$Data_All$Header*GEOM*TEXT SET " 
					 +"HEAD TO HEAD"+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$HeadToHead$Data_All$Grp1$Value$HomeValue*GEOM*TEXT SET " 
					 + "2" + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$HeadToHead$Data_All$Grp1$Value$AwayValue*GEOM*TEXT SET " 
					+ "2" + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$HeadToHead$Data_All$SubHeader*GEOM*TEXT SET " 
					 +"DRAWS : " + "2" + "\0");
			
		/****** bottom logos*****/
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$HeadToHead$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
					 +flag_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$HeadToHead$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
					 +flag_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

			break;
		}
		
	}
	public void populateFT(PrintWriter print_writer, FootballData data, int WhichSide) {
		
        /***********************************RIGHT*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "LAST 3 MATCHES \0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
//1st team
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
//2nd team
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
		
/***********************************BOTTOM*************************************/		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 +vtp+ "\0");
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	public void populateSetPiece(PrintWriter print_writer, FootballData data,int WhichSide) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSetPiecesAttempts(), p1.getSetPiecesAttempts()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSetPiecesAttempts(), p1.getSetPiecesAttempts()));
		}
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getSetPiecesAttempts()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getSetPiecesAttempts() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getSetPiecesAttempts()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getSetPiecesAttempts()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getSetPiecesAttempts()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getSetPiecesAttempts()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {

			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getSetPiecesAttempts()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getSetPiecesAttempts()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getSetPiecesAttempts()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getSetPiecesAttempts()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getSetPiecesAttempts()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getSetPiecesAttempts()+ "\0");
		}
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST SET PIECES ATTEMPTS "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"TOURNAMENT STATS "+ "\0");
		//First bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"GOALS FROM SET PIECES "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				+data.getTeam().get(0).getSetPiecesGoals()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getSetPiecesGoals()+ "\0");
//		System.out.println(data.getTeam().get(0).getSetPiecesGoals()+"  "+data.getTeam().get(1).getSetPiecesGoals());
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
//				 +((data.getTeam().get(0).getSetPiecesGoals()*401)/(data.getTeam().get(0).getSetPiecesGoals()+data.getTeam().get(1).getSetPiecesGoals()))+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
//				 +(401-((data.getTeam().get(0).getSetPiecesGoals()*401)/(data.getTeam().get(0).getSetPiecesGoals()+data.getTeam().get(1).getSetPiecesGoals())))+ "\0");

		//Second bar//
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$SubHead*GEOM*TEXT SET " 
				 +" ATTEMPTS FROM SET PIECES"+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getSetPiecesAttempts()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getSetPiecesAttempts()+ "\0");		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getSetPiecesAttempts()*401)/(data.getTeam().get(0).getSetPiecesAttempts()+data.getTeam().get(1).getSetPiecesAttempts()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getSetPiecesAttempts()*401)/(data.getTeam().get(0).getSetPiecesAttempts()+data.getTeam().get(1).getSetPiecesAttempts())))+ "\0");
		
		
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

		
	}
	public void populateWin(PrintWriter print_writer, FootballData data, int WhichSide, String ValueToProcess) {
		
		switch(ValueToProcess) {
			case "RIGHT":
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "1\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
		   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
						 + "LAST 3 MATCHES \0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 + data.getTeam().get(1).getName().toUpperCase()+"\0");

			/****** right logos ****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
				
		//1st team
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(0).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(0).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(1).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(1).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
						 +"v "+data.getTeam().get(0).getStats().get(2).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getStats().get(2).getResult()+ "\0");
		//2nd team
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(0).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(0).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(1).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(1).getResult()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
						+"v "+data.getTeam().get(1).getStats().get(2).getOpponent()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getStats().get(2).getResult()+ "\0");
				break;
			case "BOTTOM":

				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
						 + "1\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
						 +"LIVE WIN PROBABILITY"+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
						 +"WINNING PROBABILITY"+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
						 +data.getWinHomeProbability()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
						+data.getWinAwayProbability()+ "\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
						 +Double.valueOf(((data.getWinHomeProbability() + data.getWinDrawProbability())*401)/100.0)+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
						 +(401-(Double.valueOf(((data.getWinHomeProbability() + data.getWinDrawProbability())*401)/100.0)))+ "\0");
			/****** bottom logos*****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
				break;
		}
		

	}
	public void populateTeamDB(PrintWriter print_writer, Bugs bug, FootballData data, int WhichSide, String VALUE) {
		switch(VALUE.toUpperCase()) {
			case"RIGHT":
			        /***********************************RIGHT*************************************/		
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
							 + "1\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
			        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
							 + "3\0");
			       
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
							 + "MATCH STATS\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
							 + data.getTeam().get(1).getName().toUpperCase()+"\0");
		
				/****** right logos ****/
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
							 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
							 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
					
			//1st team
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
							 +"Possession (%)"+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
							 + data.getTeam().get(0).getPossession()+"\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
							 +"Total Passes"+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
							+ data.getTeam().get(0).getTotalPass() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
							 + "Total Tackles"+"\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
							+ data.getTeam().get(0).getTackle() + "\0");
			//2nd team
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
							+"Possession (%)"+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
							+ data.getTeam().get(1).getPossession() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
							+"Total Passes"+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
							+ data.getTeam().get(1).getTotalPass()  + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
							+ "Total Tackles" + "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
							+ data.getTeam().get(1).getTackle()  + "\0");
				break;
			case"BOTTOM":
					print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
							 + "2\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
							 +bug.getText1()+ "\0");
				/****** bottom logos*****/
					print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
							 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
							 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

				break;
		}
		
	}
	public void populateIngightsGs(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getGoal(), p1.getGoal()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getGoal(), p1.getGoal()));
		}

        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST GOAL SCORERS\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getGoal()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getGoal()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getGoal()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {

			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getGoal()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getGoal()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getGoal()+ "\0");
			/***********************************BOTTOM*************************************/		
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
					 + "2\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
					 +vtp+ "\0");
		/****** bottom logos*****/
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
					 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
					 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
		}
	}
	
	public void populateSponsor(PrintWriter print_writer, FootballData data, int WhichSide,String value) {
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Sponsor$Side" + WhichSide + "$img_Sponsor*TEXTURE*IMAGE SET " 
				 + logo_path + value +"\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Sponsor$Side2$img_Sponsor*TEXTURE*IMAGE SET " 
//				 + logo_path + value +"\0");
		
//		print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Right$Sponsor$In START\0");
		
		
	}
	
	public void populateBottomSponsor(PrintWriter print_writer, FootballData data, int WhichSide,String value) {
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Sponsor$Side" + WhichSide + "$img_Sponsor*TEXTURE*IMAGE SET " 
				 + logo_path + value +"\0");
		
//		print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Bottom$Sponsor$In START\0");
		
	}
	public void populateVariousTextBottom(PrintWriter print_writer, Bugs bug, FootballData data, int WhichSide) {
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "2\0");		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 +bug.getText1()+ "\0");
		
	}

	public void populateExtraData(PrintWriter print_writer, ExtraData extraData, FootballData data, int WhichSide, List<Player> players, List<Team> teams) {
		
		Player homePlayer1, homePlayer2, homePlayer3, awayPlayer1, awayPlayer2, awayPlayer3;
		
		homePlayer1 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getHomePlayer1()).findAny().orElse(null);
		homePlayer2 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getHomePlayer2()).findAny().orElse(null);
		homePlayer3 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getHomePlayer3()).findAny().orElse(null);
		
		awayPlayer1 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getAwayPlayer1()).findAny().orElse(null);
		awayPlayer2 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getAwayPlayer2()).findAny().orElse(null);
		awayPlayer3 = players.stream().filter(plyr -> plyr.getPlayerId() == extraData.getAwayPlayer3()).findAny().orElse(null);
		
//		System.out.println(extraData.getPrompt());
//		System.out.println("HOME TEAM : " + homePlayer1.getFull_name() + " - " + extraData.getHomeStat1() + " / " +
//				homePlayer2.getFull_name() + " - " + extraData.getHomeStat2() + " / " + homePlayer3.getFull_name() + " - " + extraData.getHomeStat3());
//		
//		System.out.println("AWAY TEAM : " + awayPlayer1.getFull_name() + " - " + extraData.getAwayStat1() + " / " +
//				awayPlayer2.getFull_name() + " - " + extraData.getAwayStat2() + " / " + awayPlayer3.getFull_name() + " - " + extraData.getAwayStat3());
		
//		System.out.println(homePlayer1.getTeamId() + " - " + awayPlayer1.getTeamId());
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$HeaderGrp$Side" + WhichSide + "$Header*GEOM*TEXT SET " 
				 +  extraData.getPrompt() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType*FUNCTION*Omo*vis_con SET 1\0");
		
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
//				+ "TeamData$HomeLogo*TEXTURE*IMAGE SET " + teams.get(homePlayer1.getTeamId() - 1).getTeamName4() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "TeamData$TeamName*GEOM*TEXT SET " + teams.get(homePlayer1.getTeamId() - 1).getTeamName1() + "\0");
		
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
//				+ "TeamData$HomeLogo*TEXTURE*IMAGE SET " + teams.get(awayPlayer1.getTeamId() - 1).getTeamName4() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "TeamData$TeamName*GEOM*TEXT SET " + teams.get(awayPlayer1.getTeamId() - 1).getTeamName1() + "\0");
		
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "Select_Data*FUNCTION*Omo*vis_con SET 3\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$First$PlayerName*GEOM*TEXT SET " + homePlayer1.getTicker_name() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$Second$PlayerName*GEOM*TEXT SET " + homePlayer2.getTicker_name() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$Third$PlayerName*GEOM*TEXT SET " + homePlayer3.getTicker_name() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$First$PlayerValue*GEOM*TEXT SET " + extraData.getHomeStat1() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$Second$PlayerValue*GEOM*TEXT SET " + extraData.getHomeStat2() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team1_Grp$"
				+ "PlayerData$Third$PlayerValue*GEOM*TEXT SET " + extraData.getHomeStat3() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "Select_Data*FUNCTION*Omo*vis_con SET 3\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$First$PlayerName*GEOM*TEXT SET " + awayPlayer1.getTicker_name() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$Second$PlayerName*GEOM*TEXT SET " + awayPlayer2.getTicker_name() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$Third$PlayerName*GEOM*TEXT SET " + awayPlayer3.getTicker_name() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$First$PlayerValue*GEOM*TEXT SET " + extraData.getAwayStat1() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$Second$PlayerValue*GEOM*TEXT SET " + extraData.getAwayStat2() + "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Select_DataType$Stats$Team2_Grp$"
				+ "PlayerData$Third$PlayerValue*GEOM*TEXT SET " + extraData.getAwayStat3() + "\0");
		
		//-------------------------------BOTTOM------------------------------------------------//
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +" TOURNAMENT STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"SHOTS ON TARGET "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getShotOnTarget()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getShotOnTarget()+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getShotOnTarget()*401)/(data.getTeam().get(0).getShotOnTarget()+data.getTeam().get(1).getShotOnTarget()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getShotOnTarget()*401)/(data.getTeam().get(0).getShotOnTarget() +data.getTeam().get(1).getShotOnTarget())))+ "\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + teams.get(homePlayer1.getTeamId() - 1).getTeamName4().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + teams.get(awayPlayer1.getTeamId() - 1).getTeamName4().toUpperCase()+"\0");
				
				
	}
	
	public void populateIngights(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalShots(), p1.getTotalShots()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalShots(), p1.getTotalShots()));
		}

        /***********************************RIGHT*************************************/		

        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST SHOTS TAKERS\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalShots()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalShots()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getTotalShots()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalShots()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalShots()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getTotalShots()+ "\0");
		}
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$FreeText$txt_Text*GEOM*TEXT SET " 
				 +vtp+ "\0");
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	public void populateLBand(Long eventId, LiveMatch liveData) {
//		System.out.println("EVENT ID : "+liveData.getLiveData().getEvent().get(0).getContestantId());
	}

	public void populateShootingAccuracy(PrintWriter print_writer, FootballData data,int WhichSide, String ValueToProcess) throws StreamReadException, DatabindException, IOException {
		
		
		
		
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getShootingAccuracy(), p1.getShootingAccuracy()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getShootingAccuracy(), p1.getShootingAccuracy()));
		}

        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST SHOT ACCURACY (%)\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");
	
	/****** right logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getShootingAccuracy()+"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getShootingAccuracy() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getShootingAccuracy())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getShootingAccuracy())+"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getShootingAccuracy())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(2).getShootingAccuracy())+"\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getShootingAccuracy())+"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getShootingAccuracy())+"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getShootingAccuracy())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getShootingAccuracy())+"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getShootingAccuracy())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(2).getShootingAccuracy())+"\0");
		}
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +" TOURNAMENT STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"SHOTS ON TARGET "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getShotOnTarget()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getShotOnTarget()+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getShotOnTarget()*401)/(data.getTeam().get(0).getShotOnTarget()+data.getTeam().get(1).getShotOnTarget()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getShotOnTarget()*401)/(data.getTeam().get(0).getShotOnTarget() +data.getTeam().get(1).getShotOnTarget())))+ "\0");
		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode()+"\0");
				

	}
	public void populateClearance(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) {
		
		if(data.getTeam()!=null) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "MOST CLEARANCES\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalClearance() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalClearance(), p1.getTotalClearance()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalClearance()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalClearance()+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getTotalClearance()+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalClearance()+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalClearance()+ "\0");
			   	   			
			    			
			    			}
			    		}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalClearance() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalClearance(), p1.getTotalClearance()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
			        					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			        					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getTotalClearance()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalClearance()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getTotalClearance()+ "\0");
				       			
			        		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
			        			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			            				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			              					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			       	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getTotalClearance()+ "\0");
			       	       			
			        			}
			        			
			        			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			        				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   	       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getTotalClearance()+ "\0");
			        			}
			        		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL CLEARANCES "+ "\0");			   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getTotalClearance()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getTotalClearance()+ "\0");
			   		
			   		if(data.getTeam().get(0).getTotalClearance()==0 && data.getTeam().get(1).getTotalClearance()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalClearance()==0 && data.getTeam().get(1).getTotalClearance()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getTotalClearance()*401)/(data.getTeam().get(0).getTotalClearance()+data.getTeam().get(1).getTotalClearance()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getTotalClearance()*401)/(data.getTeam().get(0).getTotalClearance()+data.getTeam().get(1).getTotalClearance())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getTotalClearance()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getTotalClearance()*401)/(data.getTeam().get(0).getTotalClearance()+data.getTeam().get(1).getTotalClearance()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getTotalClearance()*401)/(data.getTeam().get(0).getTotalClearance()+data.getTeam().get(1).getTotalClearance())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}

	public void populateExpectedGoals(PrintWriter print_writer, FootballData data, int WhichSide) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalClearance(), p1.getTotalClearance()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getGoal(), p1.getGoal()));
		}

        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST GOAL SCORERS\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getGoal()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getGoal()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getGoal()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {

			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getGoal()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getGoal()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getGoal()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getGoal()+ "\0");
		}
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"MATCH STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"EXPECTED GOALS "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +String.format("%.2f",data.getTeam().get(0).getExpectedGoals())+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+String.format("%.2f",data.getTeam().get(1).getExpectedGoals())+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getExpectedGoals()*401)/(data.getTeam().get(0).getExpectedGoals()+data.getTeam().get(1).getExpectedGoals()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getExpectedGoals()*401)/(data.getTeam().get(0).getExpectedGoals()+data.getTeam().get(1).getExpectedGoals())))+ "\0");
		
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	
	public void populateExpectedGoalsForAgainst(PrintWriter print_writer, FootballData data, int WhichSide) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalShots(), p1.getTotalShots()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalShots(), p1.getTotalShots()));
		}

        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST GOAL SCORERS\0");

		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalShots()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalShots()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getTotalShots()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {

			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalShots()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalShots()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalShots()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getTotalShots()+ "\0");
		}
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"MATCH STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"EXPECTED GOALS FOR"+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +String.format("%.2f",data.getTeam().get(0).getExpectedGoals())+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+String.format("%.2f",data.getTeam().get(1).getExpectedGoals())+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getExpectedGoals()*401)/(data.getTeam().get(0).getExpectedGoals()+data.getTeam().get(1).getExpectedGoals()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getExpectedGoals()*401)/(data.getTeam().get(0).getExpectedGoals()+data.getTeam().get(1).getExpectedGoals())))+ "\0");
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	
	public void populateplayerRating(PrintWriter print_writer, FootballData data, int WhichSide,List<Player> plyr) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getTotalScorerating(), p1.getTotalScorerating()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getTotalScorerating(), p1.getTotalScorerating()));
		}

        
        List<com.football.containers.Players> plr = data.getTeam().get(0).getTeamPlayer().subList(0, Math.min(data.getTeam().get(0).getTeamPlayer().size(), 3));
        List<com.football.containers.Players> plrs = data.getTeam().get(1).getTeamPlayer().subList(0, Math.min(data.getTeam().get(1).getTeamPlayer().size(), 3));
        
        //data.getTeam().get(1).getTeamPlayer().subList(0, Math.min(data.getTeam().get(1).getTeamPlayer().size(), 3));
        for(Player pl : plyr) {
        	for(com.football.containers.Players plr1 : plr) {
        		if(pl.getPlayerAPIId().equalsIgnoreCase(plr1.getId())) {
        			plr1.setName(pl.getTicker_name());
        		}
        	}
        	
        	for(com.football.containers.Players plr2 : plrs) {
        		if(pl.getPlayerAPIId().equalsIgnoreCase(plr2.getId())) {
        			plr2.setName(pl.getTicker_name());
        		}
        	}
        }
        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST PLAYER SCORE RATING\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase() +"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase() +"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plr.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getTotalScorerating())+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plr.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getTotalScorerating()) +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +plr.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getTotalScorerating())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plr.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getTotalScorerating())+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +plr.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getTotalScorerating())+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +plr.get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(2).getTotalScorerating())+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {

			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getTotalScorerating())+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getTotalScorerating())+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getTotalScorerating())+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getTotalScorerating())+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getTotalScorerating())+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +plrs.get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(2).getTotalScorerating())+ "\0");
		}
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "1\0");

		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"TOURNAMENT STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"TOTAL SHOTS "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getSetPiecesGoals()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getSetPiecesGoals()+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getSetPiecesGoals()*401)/(data.getTeam().get(0).getSetPiecesGoals()+data.getTeam().get(1).getSetPiecesGoals()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getSetPiecesGoals()*401)/(data.getTeam().get(0).getSetPiecesGoals()+data.getTeam().get(1).getSetPiecesGoals())))+ "\0");

	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	
	public void populateDribbles(PrintWriter print_writer, FootballData data,int WhichSide) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSuccessfulDribbles(), p1.getSuccessfulDribbles()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getSuccessfulDribbles(), p1.getSuccessfulDribbles()));
		}

        /***********************************RIGHT*************************************/		
       List<com.football.containers.Players>player= data.getTeam().get(1).getTeamPlayer().stream().filter(py->py.getName()!= null).collect(Collectors.toList());
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "MOST DRIBBLES (%)\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(0).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(0).getUnsuccessfulDribbles())))+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(0).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(0).getUnsuccessfulDribbles()))) +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(1).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(1).getUnsuccessfulDribbles())))+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(0).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(0).getUnsuccessfulDribbles())))+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(1).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(1).getUnsuccessfulDribbles())))+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(data.getTeam().get(0).getTeamPlayer().get(2).getSuccessfulDribbles(),data.getTeam().get(0).getTeamPlayer().get(2).getUnsuccessfulDribbles())))+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(player.size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +player.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(0).getSuccessfulDribbles(),player.get(0).getUnsuccessfulDribbles())))+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(player.size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +player.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(0).getSuccessfulDribbles(),player.get(0).getUnsuccessfulDribbles())))+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +player.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(1).getSuccessfulDribbles(),player.get(1).getUnsuccessfulDribbles())))+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(player.size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +player.get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(0).getSuccessfulDribbles(),player.get(0).getUnsuccessfulDribbles())))+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +player.get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(1).getSuccessfulDribbles(),player.get(1).getUnsuccessfulDribbles())))+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +player.get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +Math.round(Double.valueOf(DribblesSuccessRate(player.get(2).getSuccessfulDribbles(),player.get(2).getUnsuccessfulDribbles())))+ "\0");
		}
/***********************************BOTTOM*************************************/		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "0\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
				 + "2\0");
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
	 			 +"TOURNAMENT STATS "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"TOTAL DRIBBLES "+ "\0");
		int team1=data.getTeam().get(0).getSuccessfulDribbles()+data.getTeam().get(0).getUnsuccessfulDribbles();
		int team2=data.getTeam().get(1).getSuccessfulDribbles()+data.getTeam().get(1).getUnsuccessfulDribbles();
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +team1+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+team2+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((team1*401)/(team1+team2))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((team1*401)/(team1+team2)))+ "\0");
		
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

	}
	public void populateDuelWon(PrintWriter print_writer, FootballData data,int WhichSide, String valueToProcess) {

		if(data.getTeam()!=null) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
								 + "MOST DUELS WON \0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getDuelWon() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getDuelWon(), p1.getDuelWon()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(0).getDuelWon()+ "\0");
				   			
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   					 +(data.getTeam().get(0).getTeamPlayer().get(1).getDuelWon())+ "\0");
				   			
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
				   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				   					 +(data.getTeam().get(0).getTeamPlayer().get(2).getDuelWon())+ "\0");
				   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	   					 +(data.getTeam().get(0).getTeamPlayer().get(0).getDuelWon())+ "\0");
			   	   			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   	   					 +(data.getTeam().get(0).getTeamPlayer().get(1).getDuelWon())+ "\0");
			   	   			
			    			
			    			}
			    		}
		        		
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalClearance() <= 0);

						        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalClearance(), p1.getTotalClearance()));     
						   	}
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +(data.getTeam().get(1).getTeamPlayer().get(0).getDuelWon())+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +(data.getTeam().get(1).getTeamPlayer().get(1).getDuelWon())+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +(data.getTeam().get(1).getTeamPlayer().get(2).getDuelWon())+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   					 +(data.getTeam().get(1).getTeamPlayer().get(0).getDuelWon())+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	   					 +(data.getTeam().get(1).getTeamPlayer().get(1).getDuelWon())+ "\0");
				   	   			
				   	   			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"TOTAL DUELS "+ "\0");	
			   		int team1 =(data.getTeam().get(0).getDuelWon()+data.getTeam().get(0).getDuelLost());
			   		int team2 =(data.getTeam().get(1).getDuelWon()+data.getTeam().get(1).getDuelLost());
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +team1+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+team2+ "\0");
			   		
			   		if(team1==0 && team2==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(team1==0 && team2>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((team1*401)/(team1+team2))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((team2*401)/(team1+team2)))+ "\0");
				   		
			   		}else if(team1>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((team1*401)/(team1+team2))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((team1*401)/(team1+team2)))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		
	}
	public void populateWonCorners(PrintWriter print_writer, FootballData data,int WhichSide, String ValueToProcess) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getWonCorners(), p1.getWonCorners()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getWonCorners(), p1.getWonCorners()));
		}
		switch(ValueToProcess) {
			
			case "RIGHT":
				/***********************************RIGHT*************************************/		
		        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "1\0");
		        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
						 + "3\0");
		       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
		   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
						 + "MOST CORNER TAKERS\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
						 + data.getTeam().get(1).getName().toUpperCase()+"\0");

			/****** right logos ****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
						 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
				if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(0).getWonCorners()+ "\0");
					
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(1).getWonCorners()+ "\0");
					
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
							 +data.getTeam().get(0).getTeamPlayer().get(2).getWonCorners()+ "\0");
				}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
					
					if(data.getTeam().get(0).getTeamPlayer().size()<= 1) {
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getTeamPlayer().get(0).getWonCorners()+ "\0");
					}
					if(data.getTeam().get(0).getTeamPlayer().size()== 2) {
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getTeamPlayer().get(1).getWonCorners()+ "\0");
						
					}
					
				}
				
				break;
			case "BOTTOM":
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
						 + "0\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
						 + "2\0");
				
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
						 +"MATCH STATS "+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
						 +"SUCCESSFUL CORNERS INTO BOX "+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
						+data.getTeam().get(0).getLostCorners()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
						+data.getTeam().get(1).getLostCorners()+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
						 +((data.getTeam().get(0).getLostCorners()*401)/(data.getTeam().get(0).getLostCorners()+data.getTeam().get(1).getLostCorners()))+ "\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
						 +(401-((data.getTeam().get(0).getLostCorners()*401)/(data.getTeam().get(0).getLostCorners()+data.getTeam().get(1).getLostCorners())))+ "\0");
				
			/****** bottom logos*****/
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
						 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

				
				break;
		}
        
	}
	public void populateWonAtt3rd(PrintWriter print_writer, FootballData data,int WhichSide) {
		
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getPossWonAtt3rd(), p1.getPossWonAtt3rd()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getPossWonAtt3rd(), p1.getPossWonAtt3rd()));
		}

        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "WON ATTACKING THIRD\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getPossWonAtt3rd()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getPossWonAtt3rd() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getPossWonAtt3rd()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getPossWonAtt3rd()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getPossWonAtt3rd()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getPossWonAtt3rd()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getPossWonAtt3rd()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getPossWonAtt3rd()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getPossWonAtt3rd()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getPossWonAtt3rd()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getPossWonAtt3rd()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getPossWonAtt3rd()+ "\0");
		}
/***********************************BOTTOM*************************************/		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"MATCH STATS POSSESSION TRANSITIONS  "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"WON ATTACKING THIRD "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getPossWonAtt3rd()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				 +data.getTeam().get(1).getPossWonAtt3rd()+ "\0");		
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getPossWonAtt3rd()*401)/(data.getTeam().get(0).getPossWonAtt3rd()+data.getTeam().get(1).getPossWonAtt3rd()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getPossWonAtt3rd()*401)/(data.getTeam().get(0).getPossWonAtt3rd()+data.getTeam().get(1).getPossWonAtt3rd())))+ "\0");
		
	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

		
	}

	public void populateFinal3rdPass(PrintWriter print_writer, FootballData data,int WhichSide) {
		synchronized (data.getTeam().get(0).getTeamPlayer()) {
			Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalFinalThirdPasses(), p1.getTotalFinalThirdPasses()));
		}

		synchronized (data.getTeam().get(1).getTeamPlayer()) {
	        Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getTotalFinalThirdPasses(), p1.getTotalFinalThirdPasses()));
		}
     
        /***********************************RIGHT*************************************/		
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
				 + "1\0");
        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				 + "3\0");
       
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				 + "FINAL THIRD PASSES\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
				 + data.getTeam().get(1).getName().toUpperCase()+"\0");

	/****** right logos ****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
				 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
		
		if(data.getTeam().get(0).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(0).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalFinalThirdPasses()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalFinalThirdPasses() +"\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalFinalThirdPasses()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(0).getTotalFinalThirdPasses()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(1).getTotalFinalThirdPasses()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(0).getTeamPlayer().get(2).getTotalFinalThirdPasses()+ "\0");
		}
		////team2
		if(data.getTeam().get(1).getTeamPlayer().size()==0) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		
		}if(data.getTeam().get(1).getTeamPlayer().size()==1) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalFinalThirdPasses()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + "\0");
		}if(data.getTeam().get(1).getTeamPlayer().size()==2) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalFinalThirdPasses()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalFinalThirdPasses()+"\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 + "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 + " \0");
		}if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(0).getTotalFinalThirdPasses()+ "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(1).getTotalFinalThirdPasses()+ "\0");
			
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					 +data.getTeam().get(1).getTeamPlayer().get(2).getTotalFinalThirdPasses()+ "\0");
		}
/***********************************BOTTOM*************************************/		
		
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
				 +"MATCH STATS FINAL THIRD PASSES "+ "\0");
		//first bar//*FUNCTION*BarValues*Bar_Value__2 SET 
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
				 +"TOTAL FINAL THIRD PASSES "+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
				 +data.getTeam().get(0).getTotalFinalThirdPasses()+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
				+data.getTeam().get(1).getTotalFinalThirdPasses()+ "\0");
		//first bar
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				 +((data.getTeam().get(0).getTotalFinalThirdPasses()*401)/(data.getTeam().get(0).getTotalFinalThirdPasses()+data.getTeam().get(1).getTotalFinalThirdPasses()))+ "\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				 +(401-((data.getTeam().get(0).getTotalFinalThirdPasses()*401)/(data.getTeam().get(0).getTotalFinalThirdPasses()+data.getTeam().get(1).getTotalFinalThirdPasses())))+ "\0");
		
//		//second bar
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$SubHead*GEOM*TEXT SET " 
//				 +"WON FINAL THIRD PASSES "+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$HomeValue*GEOM*TEXT SET " 
//				+((data.getTeam().get(0).getSuccessfulFinalThirdPasses()*100)/(data.getTeam().get(0).getSuccessfulFinalThirdPasses()+data.getTeam().get(1).getSuccessfulFinalThirdPasses()))+ "%\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Value$AwayValue*GEOM*TEXT SET " 
//				+(100-((data.getTeam().get(0).getSuccessfulFinalThirdPasses()*100)/(data.getTeam().get(0).getSuccessfulFinalThirdPasses()+data.getTeam().get(1).getSuccessfulFinalThirdPasses())))+ "%\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
//				 +((data.getTeam().get(0).getSuccessfulFinalThirdPasses()*401)/(data.getTeam().get(0).getSuccessfulFinalThirdPasses()+data.getTeam().get(1).getSuccessfulFinalThirdPasses()))+ "\0");
//		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar2_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
//				 +(401-((data.getTeam().get(0).getSuccessfulFinalThirdPasses()*401)/(data.getTeam().get(0).getSuccessfulFinalThirdPasses()+data.getTeam().get(1).getSuccessfulFinalThirdPasses())))+ "\0");

	/****** bottom logos*****/
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp1$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side" + WhichSide + "$BarGrp$FlagGrp2$img_Flag*TEXTURE*IMAGE SET " 
				 +logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");

		
		
	}
	public void populateAttacking(PrintWriter print_writer, FootballData data,int WhichSide, Integer TeamIndex) throws StreamReadException, DatabindException, IOException {

		double total_left = 0,total_center = 0, total_right = 0;
		int total = (data.getTeam().get(TeamIndex).getLeft() + data.getTeam().get(TeamIndex).getCenter() + data.getTeam().get(TeamIndex).getRight());
		
		total_left = ((double)data.getTeam().get(TeamIndex).getLeft() / total)*100;
		total_center = ((double)data.getTeam().get(TeamIndex).getCenter() / total)*100;
		total_right = ((double)data.getTeam().get(TeamIndex).getRight() / total)*100;
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$LogoGrp$img_Badges*GEOM*TEXTURE*IMAGE SET " 
	   				 +data.getTeam().get(0).getCode().toUpperCase()+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$txt_Header*GEOM*TEXT SET " 
	   				 +data.getTeam().get(1).getName().toUpperCase()+ "\0");
	   		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$txt_SubHead*GEOM*TEXT SET " 
	   				 +"ATTACKING ZONE"+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValues*FUNCTION*Omo*vis_con SET " 
	   				 +"3"+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone1$txt_Value*FUNCTION*Omo*vis_con SET " 
	   				 +total_left+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone2$txt_Value*FUNCTION*Omo*vis_con SET " 
	   				 +total_center+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone3$txt_Value*FUNCTION*Omo*vis_con SET " 
	   				 +total_right+ "\0");
	   		
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone1$BarGrp$geom_BarScale_X*width SET " 
	   				 +total_left+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone2$tBarGrp$geom_BarScale_X*width SET " 
	   				 +total_center+ "\0");
	   		print_writer.println("-1 RENDERER*BACK_LAYER*TREE*$Main$All$All_FullFRames$AttackingZone$AllData$GroundOut$ZoneValue$Zone3$BarGrp$geom_BarScale_X*width SET " 
	   				 +total_right+ "\0");		
	}
	public void populateFoul(PrintWriter print_writer, FootballData data, int WhichSide, String valueToProcess) throws StreamReadException, DatabindException, IOException {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
				   				 + "MOST FOULS COMMITTED\0");
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");

						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getFoul() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getFoul(), p1.getFoul()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
			    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 + data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			       				 + data.getTeam().get(0).getTeamPlayer().get(0).getFoul()+ "\0");
			       			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(1).getFoul()+ "\0");
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getFoul()+ "\0");
			   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			          				+ data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   	       				+ data.getTeam().get(0).getTeamPlayer().get(0).getFoul() +"\0");	       			
			   	       			
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
				       				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					 + data.getTeam().get(0).getTeamPlayer().get(1).getFoul()+"\0");
			    			}
			    		}
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getFoul() <= 0);
								Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Integer.compare(p2.getFoul(), p1.getFoul()));
							}

							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    					+ data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(0).getFoul()+ "\0");
				       			
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(1).getFoul()+ "\0");
				       			
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
				       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
				       					+ data.getTeam().get(1).getTeamPlayer().get(2).getFoul()+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				          					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				   	       					 + data.getTeam().get(1).getTeamPlayer().get(0).getFoul()+ "\0");
				   	       			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					       					 	+ data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	       					+ data.getTeam().get(1).getTeamPlayer().get(1).getFoul()+ "\0");
				    			}
				    		}
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
			   				 +"FOULS COMMITTED "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getFouls()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getFouls()+ "\0");
			   		
			   		if(data.getTeam().get(0).getFouls()==0 && data.getTeam().get(1).getFouls()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getFouls()==0 && data.getTeam().get(1).getFouls()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getFouls()*401)/(data.getTeam().get(0).getFouls()+data.getTeam().get(1).getFouls()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getFouls()*401)/(data.getTeam().get(0).getFouls()+data.getTeam().get(1).getFouls())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getFouls()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getFouls()*401)/(data.getTeam().get(0).getFouls()+data.getTeam().get(1).getFouls()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getFouls()*401)/(data.getTeam().get(0).getFouls()+data.getTeam().get(1).getFouls())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}	
		 GeneratePreview(print_writer);
	}

	public void populateAccuratePass(PrintWriter print_writer, FootballData data,int WhichSide, String valueToProcess) throws StreamReadException, DatabindException, IOException {
		
		if(data.getTeam()!=null && data.getTeam().size()==2) {
			 switch(valueToProcess.toUpperCase()) {
			 	case "RIGHT":
			 		if(data.getTeam().get(0).getTeamPlayer()!=null){
			 			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$HeaderGrp$Side"+WhichSide+"$Header*GEOM*TEXT SET " 
			 	   				 + "MOST ACCURATE PASSES\0");
			 			 print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
								 + "1\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
								 + "3\0");
				        print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$Select_TopData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
								 + "0\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$TeamName*GEOM*TEXT SET " 
								 +data.getTeam().get(0).getName().toUpperCase()+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
						
						print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
								 + logo_path + data.getTeam().get(0).getCode().toUpperCase()+"\0");
				   		
						synchronized (data.getTeam().get(0).getTeamPlayer()) {
							data.getTeam().get(0).getTeamPlayer().removeIf(player -> player.getTotalAccuratePass() <= 0);
							Collections.sort(data.getTeam().get(0).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getTotalAccuratePass(), p1.getTotalAccuratePass()));
						}

				   	//TEAM 1
						if(data.getTeam().get(0).getTeamPlayer().size()>=3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data"
			    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			   					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getTotalAccuratePass())+ "\0");
			   			
			       			
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			   					 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			   					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getTotalAccuratePass())+"\0");
			   			
			       			
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
			       					 + data.getTeam().get(0).getTeamPlayer().get(2).getName()+ "\0");
			       			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
			   					 +Math.round(data.getTeam().get(0).getTeamPlayer().get(2).getTotalAccuratePass())+ "\0");
			   			
			    		}else if(data.getTeam().get(0).getTeamPlayer().size()< 3) {
			    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team1_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
			        				+ data.getTeam().get(0).getTeamPlayer().size()+"\0");
			    			if(data.getTeam().get(0).getTeamPlayer().size()>=1) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(0).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
			    						 +Math.round(data.getTeam().get(0).getTeamPlayer().get(0).getTotalAccuratePass())+ "\0");
			    				
			    			}
			    			if(data.getTeam().get(0).getTeamPlayer().size()==2) {
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
			    						 +data.getTeam().get(0).getTeamPlayer().get(1).getName()+ "\0");
			    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team1_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
			    						 +Math.round(data.getTeam().get(0).getTeamPlayer().get(1).getTotalAccuratePass())+"\0");
			    				
			    			
			    			}
			    		}
			 			}
						if(data.getTeam().get(1).getTeamPlayer()!=null){
							synchronized (data.getTeam().get(1).getTeamPlayer()) {
								data.getTeam().get(1).getTeamPlayer().removeIf(player -> player.getTotalAccuratePass() <= 0);
								Collections.sort(data.getTeam().get(1).getTeamPlayer(), (p1, p2) -> Double.compare(p2.getTotalAccuratePass(), p1.getTotalAccuratePass()));
							}

							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$TeamName*GEOM*TEXT SET " 
									 + data.getTeam().get(1).getName().toUpperCase()+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
									 + "3\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$Select_BottomData*FUNCTION*Omo*vis_con SET " 
									 + "0\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*ACTIVE SET 1 "+"\0");
							print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$TeamData$HomeLogo*TEXTURE*IMAGE SET " 
									 + logo_path + data.getTeam().get(1).getCode().toUpperCase()+"\0");
							
							//TEAM 2
							if(data.getTeam().get(1).getTeamPlayer().size()>=3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data"
				    					+ "*FUNCTION*Omo*vis_con SET 3 \0");
				    			
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				   					 +data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
					   					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getTotalAccuratePass())+ "\0");
					   			
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
					   					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getTotalAccuratePass())+ "\0");
					   			
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerName*GEOM*TEXT SET " 
					   					 +data.getTeam().get(1).getTeamPlayer().get(2).getName()+ "\0");
					   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Third$PlayerValue*GEOM*TEXT SET " 
					   					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(2).getTotalAccuratePass())+ "\0");
				       			
				    		}else if(data.getTeam().get(1).getTeamPlayer().size()< 3) {
				    			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side"+WhichSide+"$Select_DataType$Stats$Team2_Grp$PlayerData$Select_Data*FUNCTION*Omo*vis_con SET " 
				        				+ data.getTeam().get(1).getTeamPlayer().size() +" \0");
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()>=1) {
				    					print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerName*GEOM*TEXT SET " 
				    	   					 + data.getTeam().get(1).getTeamPlayer().get(0).getName()+ "\0");
				    		   			print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$First$PlayerValue*GEOM*TEXT SET " 
				    		   				 + Math.round(data.getTeam().get(1).getTeamPlayer().get(0).getTotalAccuratePass())+ "\0");
				    		   			
				    			}
				    			
				    			if(data.getTeam().get(1).getTeamPlayer().size()==2) {
				    				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerName*GEOM*TEXT SET " 
				   	   					 +data.getTeam().get(1).getTeamPlayer().get(1).getName()+ "\0");
				   	   				print_writer.println("-1 RENDERER*TREE*$group$LBand$RightSideData$Y_Pos$Side" + WhichSide + "$Stats$Team2_Grp$PlayerData$Second$PlayerValue*GEOM*TEXT SET " 
				   	   					 +Math.round(data.getTeam().get(1).getTeamPlayer().get(1).getTotalAccuratePass())+ "\0");
				   	   			}
				    		}
				   			
						}
				  break;
			 	case "BOTTOM":
		   			
			 		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType*FUNCTION*Omo*vis_con SET " 
			   				 + "0\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Select_BarNumber*FUNCTION*Omo*vis_con SET " 
			   				 + "1\0");
			   		
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Header*GEOM*TEXT SET " 
			   				 +"MATCH STATS "+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$SubHead*GEOM*TEXT SET " 
	  						 +" ACCURATE PASSES "+ "\0");		   	
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$HomeValue*GEOM*TEXT SET " 
			   				 +data.getTeam().get(0).getAccuratePasses()+ "\0");
			   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Value$AwayValue*GEOM*TEXT SET " 
			   				+data.getTeam().get(1).getAccuratePasses()+ "\0");
			   		
			   		if(data.getTeam().get(0).getAccuratePasses()==0 && data.getTeam().get(1).getAccuratePasses()==0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401/2)+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401/2)+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getAccuratePasses()==0 && data.getTeam().get(1).getAccuratePasses()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +((data.getTeam().get(1).getAccuratePasses()*401)/(data.getTeam().get(0).getAccuratePasses()+data.getTeam().get(1).getAccuratePasses()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +(401-((data.getTeam().get(1).getAccuratePasses()*401)/(data.getTeam().get(0).getAccuratePasses()+data.getTeam().get(1).getAccuratePasses())))+ "\0");
				   		
			   		}else if(data.getTeam().get(0).getAccuratePasses()>0) {
			   			print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__1 SET " 
				   				 +((data.getTeam().get(0).getAccuratePasses()*401)/(data.getTeam().get(0).getAccuratePasses()+data.getTeam().get(1).getAccuratePasses()))+ "\0");
				   		print_writer.println("-1 RENDERER*TREE*$group$LBand$BottomGrp$Stats$Side"+WhichSide+"$Select_DataType$BarGrp$Data_All$Bar1_Grp$Bar$*FUNCTION*BarValues*Bar_Value__2 SET " 
				   				 +(401-((data.getTeam().get(0).getAccuratePasses()*401)/(data.getTeam().get(0).getAccuratePasses()+data.getTeam().get(1).getAccuratePasses())))+ "\0");
				   		
			   		}
			   		break;
			 }			
		}
		 GeneratePreview(print_writer);
	}
	public void GeneratePreview(PrintWriter print_writer) {
		if(!Lband.getWhich_graphics_onscreen().isEmpty()) {
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/Lband C:/Temp/Preview.png All$In 0.860 "
					+ "Right$Change 1.00 Bottom$Change 1.00\0");
		}else {
			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/Lband C:/Temp/Preview.png All$In 0.860 "
					+ "Right$Main$Header$In 0.760 Right$Main$Stats$In 0.760 Bottom$Main$In 0.760\0");
		}	
	}
	
    public static void WinProbability(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
    	//LiveMatch match = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\WinProbability.json"), LiveMatch.class);		
//    	LiveMatch match = new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class);		
//		if(data.getTeam()==null) {
//			 data.setTeam(new ArrayList<>());
//			 
//			 data.getTeam().add(new com.football.containers.Team(match.getMatchInfo().getContestant().get(0).getName(), match.getMatchInfo().getContestant().get(0).getCode(), match.getMatchInfo().getContestant().get(0).getId()));
//			 data.getTeam().add(new com.football.containers.Team(match.getMatchInfo().getContestant().get(1).getName(), match.getMatchInfo().getContestant().get(1).getCode(), match.getMatchInfo().getContestant().get(1).getId()));
//		}
//		for(Prediction pd: match.getLiveData().getLivePredictions().get(match.getLiveData().getLivePredictions().size()-1).getPrediction()) {
//			switch(pd.getType()) {
//			case "Home":
//				data.setWinHomeProbability(Float.valueOf(pd.getProbability()));
//				break;
//			case "Away":
//				data.setWinAwayProbability(Float.valueOf(pd.getProbability()));
//				break;
//			case "Draw":
//				data.setWinDrawProbability(Float.valueOf(pd.getProbability()));
//				break;
//			}	
//		}
//		for(Prediction pd: match.getLiveData().getOverallLivePredictions().get(match.getLiveData().getOverallLivePredictions().size()-1).getPrediction()) {
//			switch(pd.getType()) {
//			case "Home":
//				data.setOverallWinHomeProbability(Float.valueOf(pd.getProbability()));
//				break;
//			case "Away":
//				data.setOverallWinAwayProbability(Float.valueOf(pd.getProbability()));
//				break;
//			case "Draw":
//				data.setOverallWinDrawProbability(Float.valueOf(pd.getProbability()));
//				break;
//			}	
//		}
	/********************matchPreview**************************/
    	
		MatchPreview mp = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\MatchPreview.json"), MatchPreview.class);
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 data.getTeam().add(new com.football.containers.Team(mp.getMatchInfo().getContestant().get(0).getName(), mp.getMatchInfo().getContestant().get(0).getCode(), mp.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(mp.getMatchInfo().getContestant().get(1).getName(), mp.getMatchInfo().getContestant().get(1).getCode(), mp.getMatchInfo().getContestant().get(1).getId()));
		}
			Form form = mp.getForm().stream()
			        .filter(fm -> fm.getContestantId().equalsIgnoreCase(data.getTeam().get(0).getID())).findAny().orElse(null);
			 
			 if( data.getTeam().get(0).getStats()==null) {
				 data.getTeam().get(0).setStats(new ArrayList<>());
			 }
			 //previousMeetingsAnyComp
			 
			 data.setDraws(mp.getPreviousMeetingsAnyComp().getDraws());
			 data.setHomeContestantWins(mp.getPreviousMeetingsAnyComp().getHomeContestantWins());
			 data.setAwayContestantWins(mp.getPreviousMeetingsAnyComp().getAwayContestantWins());
			 
				if(form.getMatch().get(form.getMatch().size()-1).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-1)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-1))));
			  	}else {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-1)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-1))));
			  	}
			  	
			  	if(form.getMatch().get(form.getMatch().size()-2).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-2)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-2))));
			  	}else {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-2)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-2))));
			  	}
			  	
			  	if(form.getMatch().get(form.getMatch().size()-3).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-3)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-3))));
			  	}else {
			  		data.getTeam().get(0).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-3)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-3))));
			  	}
			  	
			 if( data.getTeam().get(1).getStats()==null) {
				 data.getTeam().get(1).setStats(new ArrayList<>());
			 }
			  form = mp.getForm().stream()
				        .filter(fm -> fm.getContestantId().equalsIgnoreCase(data.getTeam().get(1).getID())).findAny().orElse(null);
			  	
			  
			  if(form.getMatch().get(form.getMatch().size()-1).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-1)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-1))));
			  	}else {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-1)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-1))));
			  	}
			  	
			  	if(form.getMatch().get(form.getMatch().size()-2).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-2)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-2))));
			  	}else {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-2)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-2))));
			  	}
			  	
			  	if(form.getMatch().get(form.getMatch().size()-3).getContestants().getHomeContestantId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-3)
							 .getContestants().getAwayContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-3))));
			  	}else {
			  		data.getTeam().get(1).getStats().add(new Stats(form.getMatch().get(form.getMatch().size()-3)
							 .getContestants().getHomeContestantName(),
							 String.valueOf(form.getLastSix().charAt(form.getLastSix().length()-3))));
			  	}
		
	}
    public static void InsightTournaments(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
    	LiveMatch LiveData = new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class);
    	data.setMatchId(LiveData.getMatchInfo().getTournamentCalendar().getId());
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		SeasonalStats SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats1.json"), SeasonalStats.class);		for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
			com.football.containers.Players playerStats = new com.football.containers.Players();
            if(py.getStat()!=null) {
            	playerStats.setName(py.getMatchName());
                
                for (Stat stat : py.getStat()) {
                	 switch (stat.getName()) {
			            	case "Total Shots":
			            		playerStats.setTotalShots(Integer.valueOf(stat.getValue()));
								break;
			            	}
			            }
			            data.getTeam().get(0).getTeamPlayer().add(playerStats);
				}
			}
	 SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats2.json"), SeasonalStats.class);;

		for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
			com.football.containers.Players playerStats = new com.football.containers.Players();
            if(py.getStat()!=null) {
            	playerStats.setName(py.getMatchName());
                
                for (Stat stat : py.getStat()) {
                	 switch (stat.getName()) {
                	 case "Total Shots":
		            		playerStats.setTotalShots(Integer.valueOf(stat.getValue()));
							break;
                	 }
                }
	            data.getTeam().get(1).getTeamPlayer().add(playerStats);

            }
		}
    }
    public static void SeasonalRanking(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
    	LiveMatch LiveData = new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class);
    	
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		rankings rank  = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\SeasonalRanking.json"), rankings.class);
		for(teamData tm:rank.getTeam()) {
			if(tm.getId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
				for(Stat stat: tm.getStat()) {
					switch (stat.getType()) {
					case "total won corners":
	                    data.getTeam().get(0).setWonCorners(Integer.parseInt(stat.getValue()));
	                    break;
	                case "total lost corners":
	                    data.getTeam().get(0).setLostCorners(Integer.parseInt(stat.getValue()));
	                    break;
	                case "total was fouled":
                        data.getTeam().get(0).setWonFouls(Integer.parseInt(stat.getValue()));
                        break;
	                case "total fouls":
	                	data.getTeam().get(0).setFouls(Integer.parseInt(stat.getValue()));
	                	break;
	                case "total tackle":
                        data.getTeam().get(0).setTackle(Integer.parseInt(stat.getValue()));
                        break;
                    case "total won tackle":
                        data.getTeam().get(0).setWonTackle(Integer.parseInt(stat.getValue()));
                        break;
	                case "total clearance":
	                	data.getTeam().get(0).setTotalClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total clearance ranking":
                    	data.getTeam().get(0).setEffectiveClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total contest":
                         data.getTeam().get(0).setTotalDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "total contest ranking":
                        data.getTeam().get(0).setDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "total pass":
                        data.getTeam().get(0).setTotalPass(Integer.parseInt(stat.getValue()));
                        break;
                    case "total accurate pass":
                    	data.getTeam().get(0).setTotalAccuratePass(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total duels won":
                        data.getTeam().get(0).setDuelWon(Integer.parseInt(stat.getValue()));
                        break;
                    case "total duels lost":
                        data.getTeam().get(0).setDuelLost(Integer.parseInt(stat.getValue()));
                        break;
					}
				}
				for(TeamPlayerRanking py : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
			            playerStats.setName(py.getName());
			            for (Stat stat : py.getStat()) {
			                switch (stat.getType()) {
			                    case "total won corners":
			                        playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total fouls":
			                        playerStats.setFoul(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total tackle":
			                        playerStats.setTotalTackle(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total clearance":
			                        playerStats.setTotalClearance(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total won contest":
			                        playerStats.setDribbles(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total accurate pass":
			                        playerStats.setTotalAccuratePass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total pass":
			                        playerStats.setTotalPass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total duels won":
			                        playerStats.setDuelWon(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total duels lost":
			                        playerStats.setDuelLost(Integer.parseInt(stat.getValue()));
			                        break;
			                    
			                }
			            }
			            playerStats.setPassingAccuracy(Double.parseDouble(AccuracyPercentage(playerStats.getTotalPass(), playerStats.getTotalAccuratePass())));
			            playerStats.setDuelWonRate(Double.parseDouble(DualSuccessRate(playerStats.getDuelWon(), playerStats.getDuelLost())));
			            data.getTeam().get(0).getTeamPlayer().add(playerStats);
			            
				}
			}
			if(tm.getId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
				for(Stat stat: tm.getStat()) {
					switch (stat.getType()) {
					case "total won corners":
	                    data.getTeam().get(1).setWonCorners(Integer.parseInt(stat.getValue()));
	                    break;
	                case "total lost corners":
	                    data.getTeam().get(1).setLostCorners(Integer.parseInt(stat.getValue()));
	                    break;
	                case "total was fouled":
                        data.getTeam().get(1).setWonFouls(Integer.parseInt(stat.getValue()));
                        break;
	                case "total fouls":
	                	data.getTeam().get(1).setFouls(Integer.parseInt(stat.getValue()));
	                	break;
	                case "total tackle":
                        data.getTeam().get(1).setTackle(Integer.parseInt(stat.getValue()));
                        break;
                    case "total tackle ranking":
                        data.getTeam().get(1).setWonTackle(Integer.parseInt(stat.getValue()));
                        break;
                    case "total contest":
                        data.getTeam().get(1).setTotalDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "total contest ranking":
                        data.getTeam().get(1).setDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "total pass":
                        data.getTeam().get(1).setTotalPass(Integer.parseInt(stat.getValue()));
                        break;
                    case "total accurate pass":
                    	data.getTeam().get(1).setTotalAccuratePass(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total clearance":
	                	data.getTeam().get(1).setTotalClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total clearance ranking":
                    	data.getTeam().get(1).setEffectiveClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "total duels won":
                        data.getTeam().get(1).setDuelWon(Integer.parseInt(stat.getValue()));
                        break;
                    case "total duels lost":
                        data.getTeam().get(1).setDuelLost(Integer.parseInt(stat.getValue()));
                        break;
                    	
                   
					}
				}
				for(TeamPlayerRanking py : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
			            playerStats.setName(py.getName());
			            for (Stat stat : py.getStat()) {
			                switch (stat.getType()) {
			                    case "total won corners":
			                        playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total fouls":
			                        playerStats.setFoul(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total tackle":
			                        playerStats.setTotalTackle(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total won contest":
			                        playerStats.setDribbles(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total accurate pass":
			                        playerStats.setTotalAccuratePass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total pass":
			                        playerStats.setTotalPass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total clearance":
			                        playerStats.setTotalClearance(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total duels won":
			                        playerStats.setDuelWon(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "total duels lost":
			                        playerStats.setDuelLost(Integer.parseInt(stat.getValue()));
			                        break;
			                    
			                }
			            }
			            playerStats.setDuelWonRate(Double.parseDouble(DualSuccessRate(playerStats.getDuelWon(), playerStats.getDuelLost())));
			            playerStats.setPassingAccuracy(Double.parseDouble(AccuracyPercentage(playerStats.getTotalPass(), playerStats.getTotalAccuratePass())));

			            data.getTeam().get(1).getTeamPlayer().add(playerStats);
				}
			}
		}
		data.getTeam().get(0).setPassingAccuracy(Double.parseDouble(AccuracyPercentage(data.getTeam().get(0).getTotalPass(),data.getTeam().get(0).getTotalAccuratePass())));
		data.getTeam().get(1).setPassingAccuracy(Double.parseDouble(AccuracyPercentage(data.getTeam().get(1).getTotalPass(),data.getTeam().get(1).getTotalAccuratePass())));

		data.getTeam().get(0).setDuelWonRate(Double.parseDouble(DualSuccessRate(data.getTeam().get(0).getDuelWon(), data.getTeam().get(0).getDuelLost())));
		data.getTeam().get(1).setDuelWonRate(Double.parseDouble(DualSuccessRate(data.getTeam().get(1).getDuelWon(), data.getTeam().get(1).getDuelLost())));

	}
	public static void LiveData(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		
		LiveMatch LiveData = new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class);
		
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), 
					 LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), 
					 LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		data.getTeam().get(0).setGoal(LiveData.getLiveData().getMatchDetails().getScores().getTotal().getHome());
		data.getTeam().get(1).setGoal(LiveData.getLiveData().getMatchDetails().getScores().getTotal().getAway());
		//team1	
		data.getTeam().get(0).setYellowCard(0);data.getTeam().get(1).setYellowCard(0);
		data.getTeam().get(0).setRedCard(0);data.getTeam().get(1).setRedCard(0);
		if(LiveData.getLiveData().getCard()!=null) {
			for(Card card: LiveData.getLiveData().getCard()) {
				if(card.getContestantId().equalsIgnoreCase(LiveData.getMatchInfo().getContestant().get(0).getId())) {
					
					if(card.getType().equalsIgnoreCase("YC")) {
						if(data.getTeam().get(0).getYellowCards()==null) {
							data.getTeam().get(0).setYellowCards(new ArrayList<>());
						}
						if(data.getTeam().get(0).getYellowCards().stream().noneMatch(y->y.getId()==card.getPlayerId())) {
							data.getTeam().get(0).getYellowCards().add(new com.football.containers.Players(card.getPlayerId(),card.getPlayerName(),0,0));
						}
						com.football.containers.Players playerStats = data.getTeam().get(0).getYellowCards().stream().filter(py->py.getId()==card.getPlayerId()).findAny().orElse(null);
						if(playerStats!=null) {
							playerStats.setYellowCard(playerStats.getYellowCard()+1);
						}
						data.getTeam().get(0).setYellowCard(data.getTeam().get(0).getYellowCard() + 1);
					}
					if(card.getType().equalsIgnoreCase("RC")) {
						
						if(data.getTeam().get(0).getRedCards()==null) {
							data.getTeam().get(0).setRedCards(new ArrayList<>());
						}
						if(data.getTeam().get(0).getRedCards().stream().noneMatch(y->y.getId()==card.getPlayerId())) {
							data.getTeam().get(0).getRedCards().add(new com.football.containers.Players(card.getPlayerId(),card.getPlayerName(),0,0));
						}
						com.football.containers.Players playerStats = data.getTeam().get(0).getRedCards().stream().filter(py->py.getId()==card.getPlayerId()).findAny().orElse(null);
						if(playerStats!=null) {
							playerStats.setYellowCard(playerStats.getYellowCard()+1);
						}
						data.getTeam().get(0).setRedCard(data.getTeam().get(0).getRedCard() + 1);
					}
				}else if(card.getContestantId().equalsIgnoreCase(LiveData.getMatchInfo().getContestant().get(1).getId())) {
					if(card.getType().equalsIgnoreCase("YC")) {
						if(data.getTeam().get(1).getYellowCards()==null) {
							data.getTeam().get(1).setYellowCards(new ArrayList<>());
						}
						if(data.getTeam().get(1).getYellowCards().stream().noneMatch(y->y.getId()==card.getPlayerId())) {
							data.getTeam().get(1).getYellowCards().add(new com.football.containers.Players(card.getPlayerId(),card.getPlayerName(),0,0));
						}
						com.football.containers.Players playerStats = data.getTeam().get(1).getYellowCards().stream().filter(py->py.getId()==card.getPlayerId()).findAny().orElse(null);
						if(playerStats!=null) {
							playerStats.setYellowCard(playerStats.getYellowCard()+1);
						}
						data.getTeam().get(1).setYellowCard(data.getTeam().get(1).getYellowCard() + 1);
					}
					if(card.getType().equalsIgnoreCase("RC")) {
						if(data.getTeam().get(1).getRedCards()==null) {
							data.getTeam().get(1).setRedCards(new ArrayList<>());
						}
						if(data.getTeam().get(1).getRedCards().stream().noneMatch(y->y.getId()==card.getPlayerId())) {
							data.getTeam().get(1).getRedCards().add(new com.football.containers.Players(card.getPlayerId(),card.getPlayerName(),0,0));
						}
						com.football.containers.Players playerStats = data.getTeam().get(1).getRedCards().stream().filter(py->py.getId()==card.getPlayerId()).findAny().orElse(null);
						if(playerStats!=null) {
							playerStats.setYellowCard(playerStats.getRedCard()+1);
						}
						data.getTeam().get(1).setRedCard(data.getTeam().get(0).getRedCard() + 1);
					}
				}
			}
		}
		
		for(TeamStat stat: LiveData.getLiveData().getLineUp().get(0).getStat() ) {
	                switch (stat.getType()) {
		                case "fkFoulWon":
	                        data.getTeam().get(0).setWonFouls(Integer.parseInt(stat.getValue()));
	                        break;
		                case "fkFoulLost":
		                	data.getTeam().get(0).setFouls(Integer.parseInt(stat.getValue()));
		                	break;
		                case "totalClearance":
		                	data.getTeam().get(0).setTotalClearance(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "effectiveClearance":
	                    	data.getTeam().get(0).setEffectiveClearance(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "interception":
	                        data.getTeam().get(0).setInterception(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "interceptionWon":
	                        data.getTeam().get(0).setInterception(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "ballRecovery":
	                        data.getTeam().get(0).setBallRecovery(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "unsuccessfulTouch":
	                        data.getTeam().get(0).setUnsuccessfulTouch(Integer.parseInt(stat.getValue()));
	                        break;
//	                    case "turnover":
//	                        data.getTeam().get(0).setTurnover(Integer.parseInt(stat.getValue()));
//	                        break;
	                    case "totalTackle":
	                        data.getTeam().get(0).setTackle(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "wonTackle":
	                        data.getTeam().get(0).setWonTackle(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "accuratePass":
	                        data.getTeam().get(0).setAccuratePasses(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalPass":
	                    	data.getTeam().get(0).setTotalPass(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalFinalThirdPasses":
	                        data.getTeam().get(0).setTotalFinalThirdPasses(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "successfulFinalThirdPasses":
	                    	data.getTeam().get(0).setSuccessfulFinalThirdPasses(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "possWonAtt3rd":
	                        data.getTeam().get(0).setPossWonAtt3rd(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "possWonDef3rd":
	                        data.getTeam().get(0).setPossWonDef3rd(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "touches":
	                        data.getTeam().get(0).setTouches(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "touchesInOppBox":
	                        data.getTeam().get(0).setTouchesInOppBox(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "wonCorners":
	                        data.getTeam().get(0).setWonCorners(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "lostCorners":
	                        data.getTeam().get(0).setLostCorners(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "duelWon":
	                        data.getTeam().get(0).setDuelWon(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "duelLost":
	                        data.getTeam().get(0).setDuelLost(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "wonContest":
	                        data.getTeam().get(0).setDribbles(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalContest":
	                        data.getTeam().get(0).setTotalDribbles(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "possessionPercentage":
	                        data.getTeam().get(0).setPossession(Double.valueOf(stat.getValue()));
	                    	break;
	                    case "totalCross":
	                    	data.getTeam().get(0).setTotalCross(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalYellowCard":
	                    	data.getTeam().get(0).setTotalYellowCard(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalRedCard":
	                    	data.getTeam().get(0).setTotalRedCard(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalOffside":
	                    	data.getTeam().get(0).setTotalOffside(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "blockedScoringAtt":
	                    	data.getTeam().get(0).setBlockedScoringAtt(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "ontargetScoringAtt":
	                    	data.getTeam().get(0).setShotOnTarget(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "bigChanceCreated":
	                    	data.getTeam().get(0).setChancesCreated(Integer.parseInt(stat.getValue()));
		                	break;
	                    case "ShotOffTarget":
	                    	data.getTeam().get(0).setShotOffTarget(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "goalsConceded":
	                    	data.getTeam().get(0).setGoalsConceded(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "saves":
	                    	data.getTeam().get(0).setSaves(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "cornerTaken":
	                    	data.getTeam().get(0).setCornerTaken(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalThrows":
	                    	data.getTeam().get(0).setTotalThrows(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalScoringAtt":
	                    	data.getTeam().get(0).setShots(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "aerialWon":
	                    	data.getTeam().get(0).setAerialWon(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "aerialLost":
	                    	data.getTeam().get(0).setAerialLost(Integer.parseInt(stat.getValue()));
	                        break;
	                    
	                }
		}                
		data.getTeam().get(0).setPassingAccuracy(Double.valueOf(AccuracyPercentage( data.getTeam().get(0).getTotalPass(),  data.getTeam().get(0).getAccuratePasses())));
		//team1	player	
			for(Players py : LiveData.getLiveData().getLineUp().get(0).getPlayer()) {
				data.getTeam().get(1).getTeamPlayer().add(new com.football.containers.Players(0,0,0,py.getMatchName()));				
				com.football.containers.Players playerStats = new com.football.containers.Players();
		            playerStats.setId(py.getPlayerId().trim());
					playerStats.setName(py.getMatchName().trim());
					playerStats.setShirtNumber(py.getShirtNumber());
		            playerStats.setPosition(py.getPosition());
		            playerStats.setSubPosition(py.getSubPosition());
		            if(py.getCaptain() != null && py.getCaptain().equalsIgnoreCase(FootballUtil.YES)) {
		            	playerStats.setCaptain(py.getCaptain());
		            }
		            if(py.getStat() != null) {
		            	for (Stat stat : py.getStat()) {
			                switch (stat.getType()) {
			                    case "fouls":
			                        playerStats.setFoul(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalClearance":
			                    	playerStats.setTotalClearance(Integer.parseInt(stat.getValue()));
			                    	break;
			                    case "effectiveClearance":
			                    	playerStats.setEffectiveClearance(Integer.parseInt(stat.getValue()));
			                    	break;
			                    case "totalTackle":
			                        playerStats.setTotalTackle(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "accuratePass":
			                        playerStats.setTotalAccuratePass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalFinalThirdPasses":
			                        playerStats.setTotalFinalThirdPasses(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "possWonAtt3rd":
			                        playerStats.setPossWonAtt3rd(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "touches":
			                        playerStats.setTouches(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "wonCorners":
			                        playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "duelWon":
			                        playerStats.setDuelWon(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalContest":
			                        playerStats.setDribbles(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "interception":
			                    	playerStats.setInterception(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "interceptionWon":
			                    	playerStats.setInterception(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "ballRecovery":
			                        playerStats.setBallRecovery(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "unsuccessfulTouch":
			                    	playerStats.setUnsuccessfulTouch(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "turnover":
			                    	data.getTeam().get(0).setTurnover(data.getTeam().get(0).getTurnover()
			                        		+Integer.parseInt(stat.getValue()));
			                    	playerStats.setTurnover(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "minsPlayed":
			                    	playerStats.setMinsPlayed(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalPass":
			                    	playerStats.setTotalPass(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalCross":
			                    	playerStats.setTotalCross(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "wonTackle":
			                    	playerStats.setWonTackle(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "wonContest":
			                    	playerStats.setWonContest(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "duelLost":
			                        playerStats.setDuelLost(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "saves":
			                        playerStats.setSaves(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "goals":
			                    	playerStats.setGoal(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "bigChanceCreated":
			                    	playerStats.setChanceCreated(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalAttAssist":
			                    	playerStats.setAssists(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "blockedScoringAtt":
			                    	playerStats.setBlockedScoringAtt(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "ontargetScoringAtt":
			                    	playerStats.setShotOnTarget(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "ShotOffTarget":
			                    	playerStats.setShotOffTarget(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "cornerTaken":
			                    	playerStats.setCornerTaken(Integer.parseInt(stat.getValue()));
			                    	break;
			                    case "goalsConceded":
			                    	playerStats.setGoalsConceded(Integer.parseInt(stat.getValue()));
			                    	break;
			                    case "totalOffside":
			                    	playerStats.setTotalOffside(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "totalThrows":
			                    	playerStats.setTotalThrows(Integer.parseInt(stat.getValue()));
			                    	break;
			                    case "totalScoringAtt":
			                    	playerStats.setTotalShots(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "aerialWon":
			                    	playerStats.setAerialWon(Integer.parseInt(stat.getValue()));
			                        break;
			                    case "aerialLost":
			                    	playerStats.setAerialLost(Integer.parseInt(stat.getValue()));
			                        break;
			                    
			                }
			            }
		            }
		           
		            data.getTeam().get(0).getTeamPlayer().add(playerStats);
			}
	//team2		
			for(Players py : LiveData.getLiveData().getLineUp().get(1).getPlayer()) {
				com.football.containers.Players playerStats = new com.football.containers.Players();
				playerStats.setId(py.getPlayerId().trim());
				playerStats.setName(py.getMatchName().trim());
	            playerStats.setShirtNumber(py.getShirtNumber());
	            playerStats.setPosition(py.getPosition());
	            playerStats.setSubPosition(py.getSubPosition());
	            if(py.getCaptain() != null && py.getCaptain().equalsIgnoreCase(FootballUtil.YES)) {
	            	playerStats.setCaptain(py.getCaptain());
	            }
	            if(py.getStat() != null) {
	            	for (Stat stat : py.getStat()) {
		            	switch (stat.getType()) {
	                    case "fouls":
	                        playerStats.setFoul(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalClearance":
	                    	playerStats.setTotalClearance(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "effectiveClearance":
	                    	playerStats.setEffectiveClearance(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalTackle":
	                        playerStats.setTotalTackle(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "accuratePass":
	                        playerStats.setTotalAccuratePass(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalFinalThirdPasses":
	                        playerStats.setTotalFinalThirdPasses(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "possWonAtt3rd":
	                        playerStats.setPossWonAtt3rd(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "touches":
	                        playerStats.setTouches(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "wonCorners":
	                        playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "duelWon":
	                        playerStats.setDuelWon(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalContest":
	                        playerStats.setDribbles(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "interception":
	                    	playerStats.setInterception(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "interceptionWon":
	                    	playerStats.setInterception(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "ballRecovery":
	                        playerStats.setBallRecovery(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "unsuccessfulTouch":
	                    	playerStats.setUnsuccessfulTouch(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "turnover":
	                        data.getTeam().get(1).setTurnover(data.getTeam().get(1).getTurnover()
	                        		+Integer.parseInt(stat.getValue()));
	                    	playerStats.setTurnover(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "minsPlayed":
	                    	playerStats.setMinsPlayed(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalPass":
	                    	playerStats.setTotalPass(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalCross":
	                    	playerStats.setTotalCross(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "blockedScoringAtt":
	                    	playerStats.setBlockedScoringAtt(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "wonTackle":
	                    	playerStats.setWonTackle(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "wonContest":
	                    	playerStats.setWonContest(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "duelLost":
	                        playerStats.setDuelLost(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "saves":
	                        playerStats.setSaves(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "goals":
	                    	playerStats.setGoal(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "bigChanceCreated":
	                    	playerStats.setChanceCreated(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalAttAssist":
	                    	playerStats.setAssists(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "ShotOffTarget":
	                    	playerStats.setShotOffTarget(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "cornerTaken":
	                    	playerStats.setCornerTaken(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "goalsConceded":
	                    	playerStats.setGoalsConceded(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalOffside":
	                    	playerStats.setTotalOffside(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "totalThrows":
	                    	playerStats.setTotalThrows(Integer.parseInt(stat.getValue()));
	                    	break;
	                    case "totalScoringAtt":
	                    	playerStats.setTotalShots(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "ontargetScoringAtt":
	                    	playerStats.setShotOnTarget(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "aerialWon":
	                    	playerStats.setAerialWon(Integer.parseInt(stat.getValue()));
	                        break;
	                    case "aerialLost":
	                    	playerStats.setAerialLost(Integer.parseInt(stat.getValue()));
	                        break;
	                    
		            	}
		            }
	            }
	           
	            data.getTeam().get(1).getTeamPlayer().add(playerStats);
			}
	//team2
			
			for(TeamStat stat: LiveData.getLiveData().getLineUp().get(1).getStat() ) {
                switch (stat.getType()) {
	                case "fkFoulWon":
	                    data.getTeam().get(1).setWonFouls(Integer.parseInt(stat.getValue()));
	                    break;
	                case "fkFoulLost":
	                    data.getTeam().get(1).setFouls(Integer.parseInt(stat.getValue()));
	                    break;
	                case "bigChanceCreated":
                    	data.getTeam().get(1).setChancesCreated(Integer.parseInt(stat.getValue()));
	                	break;
	                case "saves":
                    	data.getTeam().get(1).setSaves(Integer.parseInt(stat.getValue()));
                        break;
	                case "totalClearance":
	                	data.getTeam().get(1).setTotalClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "effectiveClearance":
                    	data.getTeam().get(1).setEffectiveClearance(Integer.parseInt(stat.getValue()));
                    	break;
                    case "totalTackle":
                        data.getTeam().get(1).setTackle(Integer.parseInt(stat.getValue()));
                        break;
                    case "wonTackle":
                        data.getTeam().get(1).setWonTackle(Integer.parseInt(stat.getValue()));
                        break;
                    case "totalCross":
                    	data.getTeam().get(1).setTotalCross(Integer.parseInt(stat.getValue()));
                        break;
                    case "interception":
                        data.getTeam().get(1).setInterception(Integer.parseInt(stat.getValue()));
                        break;
                    case "interceptionWon":
                        data.getTeam().get(1).setInterception(Integer.parseInt(stat.getValue()));
                        break;
                    case "ballRecovery":
                        data.getTeam().get(1).setBallRecovery(Integer.parseInt(stat.getValue()));
                        break;
                    case "unsuccessfulTouch":
                        data.getTeam().get(1).setUnsuccessfulTouch(Integer.parseInt(stat.getValue()));
                        break;
//                    case "turnover":
//                        data.getTeam().get(1).setTurnover(Integer.parseInt(stat.getValue()));
//                        break;
                    case "accuratePass":
                        data.getTeam().get(1).setAccuratePasses(Integer.parseInt(stat.getValue()));
                        break;
                    case "totalPass":
                    	data.getTeam().get(1).setTotalPass(Integer.parseInt(stat.getValue()));
                    	break;
                    case "totalFinalThirdPasses":
                        data.getTeam().get(1).setTotalFinalThirdPasses(Integer.parseInt(stat.getValue()));
                        break;
                    case "successfulFinalThirdPasses":
                    	data.getTeam().get(1).setSuccessfulFinalThirdPasses(Integer.parseInt(stat.getValue()));
                    	break;
                    case "possWonAtt3rd":
                        data.getTeam().get(1).setPossWonAtt3rd(Integer.parseInt(stat.getValue()));
                        break;
                    case "possWonDef3rd":
                        data.getTeam().get(1).setPossWonDef3rd(Integer.parseInt(stat.getValue()));
                        break;
                    case "touches":
                        data.getTeam().get(1).setTouches(Integer.parseInt(stat.getValue()));
                        break;
                    case "touchesInOppBox":
                        data.getTeam().get(1).setTouchesInOppBox(Integer.parseInt(stat.getValue()));
                    	break;
                    case "wonCorners":
                        data.getTeam().get(1).setWonCorners(Integer.parseInt(stat.getValue()));
                        break;
                    case "lostCorners":
                        data.getTeam().get(1).setLostCorners(Integer.parseInt(stat.getValue()));
                        break;
                    case "duelWon":
                        data.getTeam().get(1).setDuelWon(Integer.parseInt(stat.getValue()));
                        break;
                    case "duelLost":
                        data.getTeam().get(1).setDuelLost(Integer.parseInt(stat.getValue()));
                        break;
                    case "wonContest":
                        data.getTeam().get(1).setDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "totalContest":
                        data.getTeam().get(1).setTotalDribbles(Integer.parseInt(stat.getValue()));
                        break;
                    case "possessionPercentage":
                        data.getTeam().get(1).setPossession(Double.valueOf(stat.getValue()));
                    	break;
                    case "totalYellowCard":
                    	data.getTeam().get(1).setTotalYellowCard(Integer.parseInt(stat.getValue()));
                        break;
                    case "totalRedCard":
                    	data.getTeam().get(1).setTotalRedCard(Integer.parseInt(stat.getValue()));
                        break;
                    case "totalOffside":
                    	data.getTeam().get(1).setTotalOffside(Integer.parseInt(stat.getValue()));
                        break;
                    case "blockedScoringAtt":
                    	data.getTeam().get(1).setBlockedScoringAtt(Integer.parseInt(stat.getValue()));
                        break;
                    case "ShotOffTarget":
                    	data.getTeam().get(1).setShotOffTarget(Integer.parseInt(stat.getValue()));
                        break;
                    case "cornerTaken":
                    	data.getTeam().get(1).setCornerTaken(Integer.parseInt(stat.getValue()));
                    	break;
                    case "goalsConceded":
                    	data.getTeam().get(1).setGoalsConceded(Integer.parseInt(stat.getValue()));
                    	break;
                    case "totalThrows":
                    	data.getTeam().get(1).setTotalThrows(Integer.parseInt(stat.getValue()));
                    	break;
                    case "totalScoringAtt":
                    	data.getTeam().get(1).setShots(Integer.parseInt(stat.getValue()));
                        break;
                    case "ontargetScoringAtt":
                    	data.getTeam().get(1).setShotOnTarget(Integer.parseInt(stat.getValue()));
                        break;
                    case "aerialWon":
                    	data.getTeam().get(1).setAerialWon(Integer.parseInt(stat.getValue()));
                        break;
                    case "aerialLost":
                    	data.getTeam().get(1).setAerialLost(Integer.parseInt(stat.getValue()));
                        break;
                    
                }
			}
            data.getTeam().get(1).setPassingAccuracy(Double.valueOf(AccuracyPercentage( data.getTeam().get(1).getTotalPass(),  data.getTeam().get(1).getAccuratePasses())));
	}
	public static void ExpectedGoals(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		LiveMatch LiveData = new ObjectMapper().readValue(new File(FootballUtil.LIVE_DATA), LiveMatch.class);
		
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		data.getTeam().get(0).setGoal(LiveData.getLiveData().getMatchDetails().getScores().getTotal().getHome());
		data.getTeam().get(1).setGoal(LiveData.getLiveData().getMatchDetails().getScores().getTotal().getAway());

	//team1
		for(TeamStat stat: LiveData.getLiveData().getLineUp().get(0).getStat() ) {
            switch (stat.getType()) {
                case "expectedGoals":
                    data.getTeam().get(0).setExpectedGoals(Double.parseDouble(stat.getValue()));
                    break;
                case "expectedGoalsConceded":
					data.getTeam().get(0).setExpectedGoalsConceded(Double.parseDouble(stat.getValue()));
                    break;
            }
		}
		//team2
		for(TeamStat stat: LiveData.getLiveData().getLineUp().get(1).getStat() ) {
            switch (stat.getType()) {
	            case "expectedGoals":
	                data.getTeam().get(1).setExpectedGoals(Double.parseDouble(stat.getValue()));
	                break;
	            case "expectedGoalsConceded":
	            	data.getTeam().get(1).setExpectedGoalsConceded(Double.parseDouble(stat.getValue()));
	                break;
            }
		}
		rankings rank  =new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\SeasonalRanking.json"), rankings.class);

		for(teamData tm:rank.getTeam()) {
			if(tm.getId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
				
				for(TeamPlayerRanking py : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
			            playerStats.setName(py.getName());
			            for (Stat stat : py.getStat()) {
			                switch (stat.getType()) {
			                	case "total goals":
			                		playerStats.setGoal(Integer.parseInt(stat.getValue()));
			                		break;
			                }
			            }
			            data.getTeam().get(0).getTeamPlayer().add(playerStats);
				}
				
			}if(tm.getId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
				
				for(TeamPlayerRanking py : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
			            playerStats.setName(py.getName());
			            for (Stat stat : py.getStat()) {
			                switch (stat.getType()) {
			                	case "total goals":
			                		playerStats.setGoal(Integer.parseInt(stat.getValue()));
			                		break;
			                }
			            }
			         data.getTeam().get(1).getTeamPlayer().add(playerStats);
			}
		}
	}
		//------------------------------------Shot Takers --------------------------------------------------------//
		SeasonalStats SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats1.json"), SeasonalStats.class);;
		for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
			com.football.containers.Players playerStats = new com.football.containers.Players();
            if(py.getStat()!=null) {
            	playerStats.setName(py.getMatchName());
                
                for (Stat stat : py.getStat()) {
                	 switch (stat.getName()) {
			            	case "Total Shots":
			            		playerStats.setTotalShots(Integer.valueOf(stat.getValue()));
								break;
			            	}
			            }
			            data.getTeam().get(0).getTeamPlayer().add(playerStats);
				}
			}
		SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats2.json"), SeasonalStats.class);;

		for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
			com.football.containers.Players playerStats = new com.football.containers.Players();
            if(py.getStat()!=null) {
            	playerStats.setName(py.getMatchName());
                
                for (Stat stat : py.getStat()) {
                	 switch (stat.getName()) {
                	 case "Total Shots":
		            		playerStats.setTotalShots(Integer.valueOf(stat.getValue()));
							break;
                	 }
                }
	            data.getTeam().get(1).getTeamPlayer().add(playerStats);

            }
		}
	}
	
	public static void SeasonalStats(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		LiveMatch LiveData =   new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\WinProbability.json"), LiveMatch.class);
		
		data.setMatchId(LiveData.getMatchInfo().getTournamentCalendar().getId());
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), 
					 LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), 
					 LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
/****************************team1*******************/
		SeasonalStats SeasonalStats =  new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats1.json"), SeasonalStats.class);;
		for(Stat st : SeasonalStats.getContestant().getStat()) {
			switch (st.getName()) {
	    	 	case "Shooting Accuracy":
	    	 		data.getTeam().get(0).setShootingAccuracy(Double.parseDouble(st.getValue()));
	    	 		break;
	    	 	case "Shots On Target ( inc goals )":
	    	 		data.getTeam().get(0).setShotOnTarget(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Set Pieces Goals":
	    	 		data.getTeam().get(0).setSetPiecesGoals(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case"Attempts from Set Pieces":
	    	 		data.getTeam().get(0).setSetPiecesAttempts(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Successful Corners into Box":
	    	 		data.getTeam().get(0).setLostCorners(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Corners Won":
	    	 		data.getTeam().get(0).setWonCorners(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Successful Dribbles":
	    	 		data.getTeam().get(0).setSuccessfulDribbles(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Unsuccessful Dribbles":
	    	 		data.getTeam().get(0).setUnsuccessfulDribbles(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 		
	    	 	
			}
		}
		/****************************team1 player*******************/

		for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
			com.football.containers.Players playerStats = new com.football.containers.Players();
            if(py.getStat()!=null) {
            	playerStats.setName(py.getMatchName());
                
                for (Stat stat : py.getStat()) {
                	 switch (stat.getName()) {
                	 	case "Total Shots":
                	 		playerStats.setTotalShots(Integer.parseInt(stat.getValue()));
                	 		break;
                	 	case "Shots On Target ( inc goals )":
                	 		playerStats.setShotOnTarget(Integer.parseInt(stat.getValue()));
        	    	 		break;
                	 	case "Attempts from Set Pieces":
                	 		playerStats.setSetPiecesAttempts(Integer.parseInt(stat.getValue()));
        	    	 		break;
                	 	case "Set Pieces Goals":
                	 		playerStats.setSetPiecesGoals(Integer.parseInt(stat.getValue()));
                	 		break;
                	 	case "Corners Taken (incl short corners)":
                	 		playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
        	    	 		break;
                	 	case "Successful Dribbles":
                	 		playerStats.setSuccessfulDribbles(Integer.parseInt(stat.getValue()));
        	    	 		break;
        	    	 	case "Unsuccessful Dribbles":
        	    	 		playerStats.setUnsuccessfulDribbles(Integer.parseInt(stat.getValue()));
        	    	 		break;
                	 }
                }
            }
			playerStats.setShootingAccuracy(Double.valueOf(ShootingAccuracy(playerStats.getTotalShots(),playerStats.getShotOnTarget())));
            data.getTeam().get(0).getTeamPlayer().add(playerStats);
		}
/****************************team2*******************/
		 SeasonalStats =  new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats2.json"), SeasonalStats.class);;

		 	for(Stat st : SeasonalStats.getContestant().getStat()) {
		 		switch (st.getName()) {
	    	 	case "Shooting Accuracy":
	    	 		data.getTeam().get(1).setShootingAccuracy(Double.parseDouble(st.getValue()));
	    	 		break;
	    	 	case "Set Pieces Goals":
	    	 		data.getTeam().get(1).setSetPiecesGoals(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case"Attempts from Set Pieces":
	    	 		data.getTeam().get(1).setSetPiecesAttempts(Integer.parseInt(st.getValue()));
	    	 		break;
		    	case "Shots On Target ( inc goals )":
	    	 		data.getTeam().get(1).setShotOnTarget(Integer.parseInt(st.getValue()));
	    	 		break;
		    	case "Successful Corners into Box":
	    	 		data.getTeam().get(1).setLostCorners(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Corners Won":
	    	 		data.getTeam().get(1).setWonCorners(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Successful Dribbles":
	    	 		data.getTeam().get(1).setSuccessfulDribbles(Integer.parseInt(st.getValue()));
	    	 		break;
	    	 	case "Unsuccessful Dribbles":
	    	 		data.getTeam().get(1).setUnsuccessfulDribbles(Integer.parseInt(st.getValue()));
	    	 		break;
			}
		 	}
		/****************************team2 player*******************/

			for(SeasonalPlayerStats py : SeasonalStats.getPlayer()) {
				com.football.containers.Players playerStats = new com.football.containers.Players();
				if(py.getStat()!=null) {
	            	playerStats.setName(py.getMatchName());
	                
	                for (Stat stat : py.getStat()) {
	                	 switch (stat.getName()) {
	                	 	case "Set Pieces Goals":
	                	 		playerStats.setSetPiecesGoals(Integer.parseInt(stat.getValue()));
	                	 		break;
	                	 	case "Total Shots":
	                	 		playerStats.setTotalShots(Integer.parseInt(stat.getValue()));
	                	 		break;
	                	 	case "Attempts from Set Pieces":
	                	 		playerStats.setSetPiecesAttempts(Integer.parseInt(stat.getValue()));
	        	    	 		break;
	                	 	case "Shots On Target ( inc goals )":
	                	 		playerStats.setShotOnTarget(Integer.parseInt(stat.getValue()));
	        	    	 		break;
	                	 	case "Corners Taken (incl short corners)":
	                	 		playerStats.setWonCorners(Integer.parseInt(stat.getValue()));
	        	    	 		break;
	                	 	case "Successful Dribbles":
	                	 		playerStats.setSuccessfulDribbles(Integer.parseInt(stat.getValue()));
	        	    	 		break;
	        	    	 	case "Unsuccessful Dribbles":
	        	    	 		playerStats.setUnsuccessfulDribbles(Integer.parseInt(stat.getValue()));
	        	    	 		break;
	                	 }
	                }
	            }
				playerStats.setShootingAccuracy(Double.valueOf(ShootingAccuracy(playerStats.getTotalShots(),playerStats.getShotOnTarget())));
	            data.getTeam().get(1).getTeamPlayer().add(playerStats);
			}
	}
	public static void PlayerRating(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		LiveMatch LiveData = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\LiveData.json"), LiveMatch.class);
		
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		PassMatrix rank  = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\MatchPlayerRatings.json"), PassMatrix.class);
		for(Contestant tm : rank.getPlayerRatings().getContestant()) {
			if(tm.getId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
				for(Players pl : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
					playerStats.setId(pl.getId());
//	            	System.out.println("Stat = " + pl.getMatchDataScore().getIndexScore().getValue());
	            	playerStats.setTotalScorerating(Double.parseDouble(pl.getMatchDataScore().getIndexScore().getValue()));
		            data.getTeam().get(0).getTeamPlayer().add(playerStats); 
				}
			}
			if(tm.getId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
				for(Players pl : tm.getPlayer()) {
					com.football.containers.Players playerStats = new com.football.containers.Players();
					playerStats.setId(pl.getId());
					playerStats.setTotalScorerating(Double.parseDouble(pl.getMatchDataScore().getIndexScore().getValue()));
		            data.getTeam().get(1).getTeamPlayer().add(playerStats);   
				}
			}
		}
		
		//----------------------------------------Shots-----------------------------------------------
		SeasonalStats SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats1.json"), SeasonalStats.class);

		/****************************team1 player*******************/

		for(Stat st : SeasonalStats.getContestant().getStat()) {
			switch (st.getName()) {
	    	 	case "Total Shots":
	    	 		data.getTeam().get(0).setSetPiecesGoals(Integer.parseInt(st.getValue()));
	    	 		break;
			}
		}
/****************************team2*******************/
		SeasonalStats = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_API_DIRECTORY + "SeasonalStats2.json"), SeasonalStats.class);

		/****************************team2 player*******************/

		 for(Stat st : SeasonalStats.getContestant().getStat()) {
				switch (st.getName()) {
		    	 	case "Total Shots":
		    	 		data.getTeam().get(1).setSetPiecesGoals(Integer.parseInt(st.getValue()));
		    	 		break;
		    	 		
		    	 	
				}
			}
	}
	public static void Event(FootballData data) throws StreamReadException, DatabindException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError{
		int home_left = 0,home_center = 0,home_right = 0,away_left = 0,away_center = 0,away_right = 0;
		LiveMatch LiveData = new ObjectMapper().readValue(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\MatchEvent.json"), LiveMatch.class);		
		
		if(data.getTeam()==null) {
			 data.setTeam(new ArrayList<>());
			 
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(0).getName(), LiveData.getMatchInfo().getContestant().get(0).getCode(), LiveData.getMatchInfo().getContestant().get(0).getId()));
			 data.getTeam().add(new com.football.containers.Team(LiveData.getMatchInfo().getContestant().get(1).getName(), LiveData.getMatchInfo().getContestant().get(1).getCode(), LiveData.getMatchInfo().getContestant().get(1).getId()));
		}
		
		for(Events event : LiveData.getLiveData().getEvent()) {
			if(event.getContestantId().equalsIgnoreCase(data.getTeam().get(0).getID())) {
				for(Qualifier quali : event.getQualifier()) {
					if(quali.getQualifierId() == 56) {
						if(quali.getValue().equalsIgnoreCase("LEFT")) {
							home_left = home_left + 1;
							data.getTeam().get(0).setLeft(home_left);
						}else if(quali.getValue().equalsIgnoreCase("CENTER")) {
							home_center = home_center + 1;
							data.getTeam().get(0).setCenter(home_center);
						}else if(quali.getValue().equalsIgnoreCase("RIGHT")) {
							home_right = home_right + 1;
							data.getTeam().get(0).setRight(home_right);
						}
					}
				}
			}
			
			if(event.getContestantId().equalsIgnoreCase(data.getTeam().get(1).getID())) {
				for(Qualifier quali : event.getQualifier()) {
					if(quali.getQualifierId() == 56) {
						if(quali.getValue().equalsIgnoreCase("LEFT")) {
							away_left = away_left + 1;
							data.getTeam().get(1).setLeft(away_left);
						}else if(quali.getValue().equalsIgnoreCase("CENTER")) {
							away_center = away_center + 1;
							data.getTeam().get(1).setCenter(away_center);
						}else if(quali.getValue().equalsIgnoreCase("RIGHT")) {
							away_right = away_right + 1;
							data.getTeam().get(1).setRight(away_right);
						}
					}
				}
			}
		}
	}
	
	public static String DualSuccessRate(int duelsWon, int duelsLost) {
	        int totalDuels = duelsWon + duelsLost;
	        if (totalDuels == 0) {
	            return "0.00";
	        }
	        double successRate = ((double) duelsWon / totalDuels) * 100.0;
	        return String.format("%.2f", successRate);
	    }
	public static String DribblesSuccessRate(int DribblesWon, int DribblesLost) {
        int totalDuels = DribblesWon + DribblesLost;
        if (totalDuels == 0) {
            return "0.00";
        }
        double successRate = ((double) DribblesWon / totalDuels) * 100.0;
        return String.format("%.2f", successRate);
    }
	public static String ShootingAccuracy(int shotsTaken, int shotsOnTarget) {
	        if (shotsTaken == 0) {
	            return "0.00";
	        }
	        double accuracy = ((double) shotsOnTarget / shotsTaken) * 100.0;
	        return String.format("%.2f", accuracy) ;
	    }
	public static String AccuracyPercentage(int totalPassesAttempted, int accuratePasses) {
	        if (totalPassesAttempted <= 0) {
	            return "0.00";
	        }
	        accuratePasses = Math.max(0, accuratePasses);
	        return String.format("%.2f", (double) accuratePasses / totalPassesAttempted * 100);
	    }
}