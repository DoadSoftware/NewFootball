package com.football.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.SAXException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football.EuroLeague.LiveMatch;
import com.football.EuroLeague.PassMatrix;
import com.football.broadcaster.EuroLeague;
import com.football.broadcaster.I_League;
import com.football.broadcaster.Santosh_Trophy;
import com.football.broadcaster.Super_Cup;
import com.football.broadcaster.Viz_Santosh_Trophy;
import com.football.broadcaster.Viz_Tri_Nation;
import com.football.containers.Scene;
import com.football.containers.ScoreBug;
import com.football.model.Clock;
import com.football.model.Configurations;
import com.football.model.Event;
import com.football.model.EventFile;
import com.football.model.Football;
import com.football.model.LiveMatchData;
import com.football.model.Match;
import com.football.model.MatchStats;
import com.football.model.Player;
import com.football.service.FootballService;
import com.football.util.FootballFunctions;
import com.football.util.FootballUtil;

@Controller
public class IndexController 
{
	@Autowired
	FootballService footballService;
	
	public static String expiry_date = "2026-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static Clock session_clock = new Clock();
	public static Configurations session_configurations;
	public static List<PrintWriter> print_writers;
	public static Match session_match;
	public static LiveMatch session_live_event;
	public static EventFile session_event;
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static I_League session_i_league;
	public static EuroLeague session_EuroLeague;
	public static Santosh_Trophy session_santosh_trophy;
	public static Viz_Santosh_Trophy session_viz_santosh_trophy;
	public static Viz_Tri_Nation session_viz_tri_nation;
	public static Super_Cup session_super_cup;
	public static List<Scene> session_selected_scenes;
	public static Football football = new Football();
	public static String wtp = "",vtp = "";
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) 
		throws IOException, JAXBException 
	{
		
		if(current_date == null || current_date.isEmpty()) {
			current_date = FootballFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(FootballUtil.FOOTBALL_DIRECTORY + 
				FootballUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(FootballUtil.FOOTBALL_DIRECTORY 
				+ FootballUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		
		model.addAttribute("configuration_files", new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		
		if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY + FootballUtil.OUTPUT_XML).exists()) {
			session_configurations = (Configurations)JAXBContext.newInstance(
					Configurations.class).createUnmarshaller().unmarshal(
					new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY 
					+ FootballUtil.OUTPUT_XML));
		} else {
			session_configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY + 
					FootballUtil.OUTPUT_XML));
		}
		
		model.addAttribute("session_configurations",session_configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
	public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, 
		InvocationTargetException, IOException, ParseException  
	{
		model.addAttribute("match_files", new File(FootballUtil.FOOTBALL_DIRECTORY + 
				FootballUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_match", session_match);
		model.addAttribute("teams", footballService.getTeams());
		model.addAttribute("formations", footballService.getFormations());
		model.addAttribute("teamcolor", footballService.getTeamColors());
		model.addAttribute("grounds", footballService.getGrounds());
		model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

		return "setup";
	}
	
	@RequestMapping(value = {"/help"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String HelpPage()  
	{
		return "help";
	}
	
	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String footballMatchPage(ModelMap model,
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false) Integer vizPortNumber,
		@RequestParam(value = "vizSecondaryIPAddress", required = false, defaultValue = "") String vizSecondaryIPAddress,
		@RequestParam(value = "vizSecondaryPortNumber", required = false) Integer vizSecondaryPortNumber,
		@RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene)
			throws IOException, ParseException, JAXBException, InterruptedException, SAXException, ParserConfigurationException, FactoryConfigurationError  
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
			
			if(vizPortNumber == null)  vizPortNumber = 0;
			if(vizSecondaryPortNumber == null)  vizSecondaryPortNumber = 0;
			
			session_configurations = new Configurations(selectedBroadcaster, vizIPAddresss, vizPortNumber, vizSecondaryIPAddress, vizSecondaryPortNumber);

			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
				new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY + 
				FootballUtil.OUTPUT_XML));
			
			print_writers = FootballFunctions.processPrintWriter(session_configurations);

			session_selected_broadcaster = selectedBroadcaster;
			session_selected_scenes = new ArrayList<Scene>();
			if(!vizIPAddresss.trim().isEmpty() && vizPortNumber != null && vizPortNumber != 0) {			
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				switch (session_selected_broadcaster.toUpperCase()) {
				case FootballUtil.I_LEAGUE:
					session_selected_scenes.add(new Scene(FootballUtil.I_LEAGUE_SCORE_BUG_SCENE_PATH,FootballUtil.ONE)); // Front layer
					session_selected_scenes.add(new Scene("",FootballUtil.TWO));
					session_selected_scenes.get(0).scene_load(print_writers.get(0), session_selected_broadcaster);
					session_i_league = new I_League();
					session_i_league.scorebug = new ScoreBug();
					break;
				case FootballUtil.SANTOSH_TROPHY:
					session_selected_scenes.add(new Scene(FootballUtil.SANTOSH_TROPHY_SCORE_BUG_SCENE_PATH,FootballUtil.ONE)); // Front layer
					session_selected_scenes.add(new Scene("",FootballUtil.TWO));
					session_selected_scenes.get(0).scene_load(print_writers.get(0), session_selected_broadcaster);
					session_santosh_trophy = new Santosh_Trophy();
					session_santosh_trophy.scorebug = new ScoreBug();
					break;
				case FootballUtil.VIZ_SANTOSH_TROPHY:
					session_selected_scenes.add(new Scene(FootballUtil.VIZ_SANTOSH_TROPHY_SCORE_BUG_SCENE_PATH,FootballUtil.FRONT_LAYER)); // Front layer
					session_selected_scenes.add(new Scene("",FootballUtil.MIDDLE_LAYER));
					session_selected_scenes.get(0).scene_load(print_writers.get(0), session_selected_broadcaster);
					session_viz_santosh_trophy = new Viz_Santosh_Trophy();
					session_viz_santosh_trophy.scorebug = new ScoreBug();
					break;
				case FootballUtil.VIZ_TRI_NATION:
					session_selected_scenes.add(new Scene(FootballUtil.VIZ_TRI_NATION_SCORE_BUG_SCENE_PATH,FootballUtil.FRONT_LAYER)); // Front layer
					session_selected_scenes.add(new Scene("",FootballUtil.MIDDLE_LAYER));
					session_selected_scenes.get(0).scene_load(print_writers.get(0), session_selected_broadcaster);
					session_viz_tri_nation = new Viz_Tri_Nation();
					session_viz_tri_nation.scorebug = new ScoreBug();
					break;
				case FootballUtil.EURO_LEAGUE:
					session_selected_scenes.add(new Scene("",FootballUtil.MIDDLE_LAYER));
					session_selected_scenes.get(0).scene_load(print_writers.get(0), session_selected_broadcaster);
					session_EuroLeague = new EuroLeague();
					
					break;
				case FootballUtil.SUPER_CUP:
					session_selected_scenes.add(new Scene(FootballUtil.SUPER_CUP_SCORE_BUG_SCENE_PATH,FootballUtil.FRONT_LAYER)); // Front layer
					session_selected_scenes.add(new Scene(FootballUtil.SUPER_CUP_FF_SCENE_PATH,FootballUtil.BACK_LAYER)); // Back layer
					session_selected_scenes.add(new Scene("",FootballUtil.MIDDLE_LAYER));
					
					session_selected_scenes.get(0).scene_load(print_writers, session_selected_broadcaster);
					session_selected_scenes.get(1).scene_load(print_writers, session_selected_broadcaster);
					
					session_super_cup = new Super_Cup();
					session_super_cup.scorebug = new ScoreBug();
					
					break;
					
				}
			}
			
			System.out.println(session_selected_scenes);
			model.addAttribute("match_files", new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			session_event = new EventFile();
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0)
				session_event.setEvents(new ArrayList<Event>());
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getClock() == null) 
				session_match.setClock(new Clock());
			if(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
			        + FootballUtil.SPORTVUSTATISTIC 
			        + FootballUtil.XML_EXTENSION).exists()) {
				session_match.setXmlTimeSpan(new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss 'GMT'").format(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
				        + FootballUtil.SPORTVUSTATISTIC 
				        + FootballUtil.XML_EXTENSION).lastModified()));
			}
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_event", session_event);
			model.addAttribute("session_configurations", session_configurations);
			model.addAttribute("session_socket", session_socket);
			model.addAttribute("session_i_league", session_i_league);
			model.addAttribute("session_selected_scenes", session_selected_scenes);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/back_to_match"}, method = RequestMethod.POST)
	public String backToMatchPage(ModelMap model) throws ParseException
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
		
			model.addAttribute("match_files", new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".json") && pathname.isFile();
			    }
			}));
			if(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
			        + FootballUtil.SPORTVUSTATISTIC 
			        + FootballUtil.XML_EXTENSION).exists()) {
				session_match.setXmlTimeSpan(new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss 'GMT'").format(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
				        + FootballUtil.SPORTVUSTATISTIC 
				        + FootballUtil.XML_EXTENSION).lastModified()));
			}
			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);

			return "match";
		
		}
	}	
	
	@RequestMapping(value = {"/upload_match_setup_data", "/reset_and_upload_match_setup_data"}
		,method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String uploadFormDataToSessionObjects(MultipartHttpServletRequest request) 
			throws IllegalAccessException, InvocationTargetException, JAXBException, IOException
	{
		if (request.getRequestURI().contains("upload_match_setup_data") 
				|| request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
			
			List<Player> home_squad = new ArrayList<Player>(); List<Player> away_squad = new ArrayList<Player>();
			List<Player> home_substitutes = new ArrayList<Player>(); List<Player> away_substitutes = new ArrayList<Player>();

	   		boolean reset_all_variables = false;
			if(request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
				reset_all_variables = true;
			} else if(request.getRequestURI().contains("upload_match_setup_data")) {
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().equalsIgnoreCase("select_existing_football_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
						reset_all_variables = true;
						break;
					}
				}
			}
			if(reset_all_variables == true) {
				session_match = new Match(); 
				session_event = new EventFile();
				session_event.setEvents(new ArrayList<Event>());
				session_match.setMatchStats(new ArrayList<MatchStats>());
			}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
   					if(entry.getKey().split("_")[0].equalsIgnoreCase(FootballUtil.HOME + FootballUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					home_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), FootballUtil.PLAYER));
   							break;
   						default:
   		   					home_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), FootballUtil.SUBSTITUTE));
   							break;
   						}
   					} else if(entry.getKey().split("_")[0].equalsIgnoreCase(FootballUtil.AWAY + FootballUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					away_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), FootballUtil.PLAYER));
   							break;
   						default:
   		   					away_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), FootballUtil.SUBSTITUTE));
   							break;
   						}
   					}
	   			} else {
	   				BeanUtils.setProperty(session_match, entry.getKey(), entry.getValue()[0]);
	   			}
	   		}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
	   				if(entry.getKey().split("_")[0].equalsIgnoreCase(FootballUtil.HOME + FootballUtil.CAPTAIN 
	   						+ FootballUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:home_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:home_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   				} else if(entry.getKey().split("_")[0].equalsIgnoreCase(FootballUtil.AWAY + FootballUtil.CAPTAIN 
	   						+ FootballUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:away_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:away_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
   					}
	   			}
	   		}

			session_match.setHomeSquad(home_squad);
			session_match.setAwaySquad(away_squad);
			
			Collections.sort(session_match.getHomeSquad());
			Collections.sort(session_match.getAwaySquad());

			session_match.setHomeSubstitutes(home_substitutes);
			session_match.setAwaySubstitutes(away_substitutes);
			
			Collections.sort(session_match.getHomeSubstitutes());
			Collections.sort(session_match.getAwaySubstitutes());
			
			session_match.setHomeOtherSquad(FootballFunctions.getPlayersFromDB(footballService, FootballUtil.HOME, session_match));
			session_match.setAwayOtherSquad(FootballFunctions.getPlayersFromDB(footballService, FootballUtil.AWAY, session_match));

			new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			
			session_match = FootballFunctions.populateMatchVariables(footballService, session_match);

			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		}
		session_match.setEvents(session_event.getEvents());
		return objectMapper.writeValueAsString(session_match);
	}
	
	@RequestMapping(value = {"/processFootballProcedures.html"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processFootballProcedures(
			@ModelAttribute("session_MasterFootballDirectory") String session_MasterFootballDirectory,
			@ModelAttribute("session_configurations") Configurations session_configurations,
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws Exception
	{	
		Event this_event = new Event();
		if(session_selected_broadcaster != null && !session_selected_broadcaster.equalsIgnoreCase(FootballUtil.EURO_LEAGUE)) {
			if(!whatToProcess.equalsIgnoreCase(FootballUtil.LOAD_TEAMS)) {
				if(valueToProcess.contains(",")) {
					if(session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
						session_match = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
								session_match.getMatchFileName()), Match.class);
						session_event = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
								session_match.getMatchFileName()), EventFile.class);
						session_match.setEvents(session_event.getEvents());
						session_match = FootballFunctions.populateMatchVariables(footballService,session_match);
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "HEAD_TO_HEAD_FILE":
            return handleHeadToHead(session_MasterFootballDirectory);
		case "GET-CONFIG-DATA":
			session_configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
				new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CONFIGURATIONS_DIRECTORY 
						+ valueToProcess));
			
			return objectMapper.writeValueAsString(session_configurations).toString();
		case FootballUtil.LOG_STAT:

			if(valueToProcess.toUpperCase().contains(FootballUtil.PENALTIES)) {
				if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(FootballUtil.INCREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(FootballUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.HIT)) {
							session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.MISS)) {
							session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() + 1);
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(FootballUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.HIT)) {
							session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.MISS)) {
							session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() + 1);
						}
					}
				}else if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(FootballUtil.DECREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(FootballUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.HIT)) {
							if(session_match.getHomePenaltiesHits() > 0) {
								session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.MISS)) {
							if(session_match.getHomePenaltiesMisses() > 0) {
								session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() - 1);
							}
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(FootballUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.HIT)) {
							if(session_match.getAwayPenaltiesHits() > 0) {
								session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(FootballUtil.MISS)) {
							if(session_match.getAwayPenaltiesMisses() > 0) {
								session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() - 1);
							}
						}
					}
				}
			}
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			
			switch (session_selected_broadcaster) {
			case FootballUtil.I_LEAGUE:
				session_i_league.ProcessGraphicOption(print_writers.get(0),whatToProcess,session_match, session_clock,footballService,
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.SANTOSH_TROPHY:
				session_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService,
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.VIZ_SANTOSH_TROPHY:
				session_viz_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService,
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.VIZ_TRI_NATION:
				session_viz_tri_nation.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService,
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.SUPER_CUP:
				session_super_cup.ProcessGraphicOption(print_writers,whatToProcess, session_match, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.EURO_LEAGUE:
				session_EuroLeague.ProcessGraphicOption(print_writers.get(0), whatToProcess, session_live_event, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;
			}
			//session_i_league.ProcessGraphicOption(whatToProcess,session_match, session_clock,footballService,session_socket, session_selected_scenes, valueToProcess);
			return objectMapper.writeValueAsString(session_match).toString();
			
		case "RESET_PENALTY":
			
			session_match.setHomePenaltiesHits(0);
			session_match.setHomePenaltiesMisses(0);
			session_match.setAwayPenaltiesHits(0);
			session_match.setAwayPenaltiesMisses(0);
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			
			return objectMapper.writeValueAsString(session_match).toString();
		case "SCOREBUG_API_GRAPHICS-OPTIONS":case "TOP_STATS-OPTIONS_DATA":case "CHECK_FOR_PLAYER_DATA":
		case "NAMESUPER_GRAPHICS-OPTIONS": case "BUG_DB_GRAPHICS-OPTIONS": case "STAFF_GRAPHICS-OPTIONS": case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS":
		case "SCOREBUGPROMO_GRAPHICS-OPTIONS":	case "RESULT_PROMO_GRAPHICS-OPTIONS": case "TEAMFIXTURE_GRAPHICS-OPTIONS": case "DB_GRAPHICS":
		case "LEADERBOARD_GRAPHICS-OPTIONS": case "LT_PLAYER_STATS_GRAPHICS-OPTIONS":case"SCOREBUG_GRAPHICS-OPTIONS":
			switch (session_selected_broadcaster) {
			case FootballUtil.I_LEAGUE:
				return session_i_league.ProcessGraphicOption(print_writers.get(0),whatToProcess,session_match,session_clock, 
						footballService, session_selected_scenes, valueToProcess).toString();
			case FootballUtil.SANTOSH_TROPHY:
				return session_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess,session_match,session_clock, 
						footballService, session_selected_scenes, valueToProcess).toString();
			case FootballUtil.VIZ_SANTOSH_TROPHY:
				return session_viz_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, 
						footballService, session_selected_scenes, valueToProcess).toString();
			case FootballUtil.VIZ_TRI_NATION:
				return session_viz_tri_nation.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, 
						footballService, session_selected_scenes, valueToProcess).toString();
			case FootballUtil.SUPER_CUP:
				return session_super_cup.ProcessGraphicOption(print_writers,whatToProcess, session_match, session_clock, 
						footballService, session_selected_scenes, valueToProcess).toString();
			case FootballUtil.EURO_LEAGUE:
				return objectMapper.writeValueAsString(footballService.getBugs()).toString();
			}
			
		case "EXTRA_DATA_GS-OPTIONS":
			switch (session_selected_broadcaster) {
			case FootballUtil.EURO_LEAGUE:
				return objectMapper.writeValueAsString(footballService.getExtraData()).toString();
			}
			
		case "POPULATE-L3-HEATMAP":
			int team_number = 0;
			String player_data = "";
			if(session_match.getHomeTeamId() == Integer.valueOf(valueToProcess.split(",")[2])) {
				team_number = 0;
			}else if(session_match.getAwayTeamId() == Integer.valueOf(valueToProcess.split(",")[2])){
				team_number = 1;
			}
			
			switch(valueToProcess.split(",")[3].toUpperCase()) {
			case "HEATMAP":
				player_data = "playerheatmap" + team_number + "_" + footballService.getAllPlayer().get(Integer.valueOf(valueToProcess.split(",")[4]) - 1).getJersey_number();
				
				break;
			case "PEAKDISTANCE":
				player_data = "playerpeakdistancegraph" + team_number + "_" + footballService.getAllPlayer().get(Integer.valueOf(valueToProcess.split(",")[4]) - 1).getJersey_number();
				break;
			}
			
			if(new File("C:\\Sports\\Football\\Statistic\\Match_Data\\" + player_data + ".jpg").exists()) {
				session_match.setApi_photo("SUCCESS");
			}else {
				session_match.setApi_photo("UNSUCCESS");
			}
			
			//session_match.setApi_photo(FootballFunctions.FTPImageDownload(21, 0, "isl-dload", "Quod0ijai7aev3aewam7ifie8ae3ee", player_data,session_configurations));
			session_super_cup.ProcessGraphicOption(print_writers,whatToProcess, session_match, session_clock, footballService, 
					session_selected_scenes, valueToProcess);
			System.out.println("session_match.getApi_photo() = " + session_match.getApi_photo());
			return objectMapper.writeValueAsString(session_match).toString();
		case "APIDATA_GRAPHICS-OPTIONS":
			
			try {
		         URL url = new URL(FootballUtil.API_PATH1 + session_match.getMatchId()+ FootballUtil.API_PATH2);
		         URLConnection connection = url.openConnection();
		         connection.connect();
		         LiveMatchData my_data = new ObjectMapper().readValue(url, LiveMatchData.class);
					
					if(my_data.getTeamShortMatchStats().getTeam_stats_data().size() > 0) {
						for(int i = 0; i <= my_data.getTeamShortMatchStats().getTeam_stats_data().size() -1; i++ ) {
							session_match.setApiData(my_data.getTeamShortMatchStats().getTeam_stats_data());
						}
					}
		      } catch (MalformedURLException e) {
		         System.out.println("Internet is not connected");
		      } catch (IOException e) {
		         System.out.println("Internet is not connected");
		      }
			return objectMapper.writeValueAsString(session_match).toString();
			
		case FootballUtil.REPLACE:
			
			Player store_player = new Player();
		if(session_match.getHomeTeamId()== Integer.valueOf(valueToProcess.split(",")[3])) {
			for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
				
				if(session_match.getHomeSquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getHomeSquad().get(i);
					session_match.getHomeSquad().remove(i);
					for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
						if(session_match.getHomeSubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
							session_match.getHomeSubstitutes().remove(j);
							session_match.getHomeSubstitutes().add(j, store_player);
						}
					}
				}
			}
		}else if(session_match.getAwayTeamId()== Integer.valueOf(valueToProcess.split(",")[3])) {
			for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
				
				if(session_match.getAwaySquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getAwaySquad().get(i);
					session_match.getAwaySquad().remove(i);
					for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
						if(session_match.getAwaySubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
							session_match.getAwaySubstitutes().remove(j);
							session_match.getAwaySubstitutes().add(j, store_player);
						}
					}
				}
			}
		}
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, 0, session_match.getClock().getMatchHalves(), 
					0,whatToProcess, "replace", Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),0));
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match).toString();
				
		case FootballUtil.LOG_EVENT:
			
			if(!valueToProcess.trim().isEmpty() && valueToProcess.contains(",") == true) {
				
				if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
					session_match.setMatchStats(new ArrayList<MatchStats>());
				if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
					session_match.setEvents(new ArrayList<Event>());
				
				switch (valueToProcess.split(",")[1].toUpperCase()) {
				case FootballUtil.GOAL: case FootballUtil.OWN_GOAL: case FootballUtil.PENALTY: case FootballUtil.YELLOW: case FootballUtil.RED:
				case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.SHOTS: case FootballUtil.CORNERS_CONVERTED: case FootballUtil.CORNERS:
				case FootballUtil.ASSISTS: case FootballUtil.OFF_SIDE: case FootballUtil.FOULS:
					
					session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
							session_match.getClock().getMatchHalves(),valueToProcess.split(",")[1], 1, session_match.getClock().getMatchTotalMilliSeconds()));
					
					for(Player plyr : session_match.getHomeSquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case FootballUtil.GOAL: case FootballUtil.PENALTY:
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							case FootballUtil.OWN_GOAL: 
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							}
						}
					}
					for(Player plyr : session_match.getAwaySquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case FootballUtil.GOAL: case FootballUtil.PENALTY:
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							case FootballUtil.OWN_GOAL: 
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							}
						}
					}
					break;
				}

				if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
					session_event.setEvents(new ArrayList<Event>());
				
				session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
						session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, valueToProcess.split(",")[1], 0,0,1));
				
			}

			session_match = FootballFunctions.populateMatchVariables(footballService, session_match);
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match).toString();

		case "HOME_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"Home_Goal", 1, session_match.getClock().getMatchTotalMilliSeconds()));
			
			session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "Home_Goal", 0,0,1));

			session_match = FootballFunctions.populateMatchVariables(footballService, session_match);
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match).toString();
			
		case "AWAY_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"AWAY_GOAL", 1, session_match.getClock().getMatchTotalMilliSeconds()));
			
			session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "AWAY_GOAL", 0,0,1));

			session_match = FootballFunctions.populateMatchVariables(footballService, session_match);
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			
			return objectMapper.writeValueAsString(session_match).toString();	

		case "LOG_OVERWRITE_MATCH_SUBS":
			//System.out.println(valueToProcess);
			if(valueToProcess.contains(",")) {
				int overwrite_palyer_off_id = 0,overwrite_palyer_on_id = 0;
				Player sub_store_player = new Player();
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getEventNumber() == Integer.valueOf(valueToProcess.split(",")[1])) {
							if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) == 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[3]);
								overwrite_palyer_on_id = evnt.getOffPlayerId();;
								
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) == 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = evnt.getOnPlayerId();
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[2]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[3]);
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[2]);
								
								for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
									if(session_match.getHomeSquad().get(i).getPlayerId() == evnt.getOnPlayerId()) {
										sub_store_player = session_match.getHomeSquad().get(i);
										session_match.getHomeSquad().remove(i);
										for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
											if(session_match.getHomeSubstitutes().get(j).getPlayerId() == evnt.getOffPlayerId()) {
												session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
												session_match.getHomeSubstitutes().remove(j);
												session_match.getHomeSubstitutes().add(j, sub_store_player);
											}
										}
									}
								}
								for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
									if(session_match.getAwaySquad().get(i).getPlayerId() == evnt.getOnPlayerId()) {
										sub_store_player = session_match.getAwaySquad().get(i);
										session_match.getAwaySquad().remove(i);
										for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
											if(session_match.getAwaySubstitutes().get(j).getPlayerId() == evnt.getOffPlayerId()) {
												session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
												session_match.getAwaySubstitutes().remove(j);
												session_match.getAwaySubstitutes().add(j, sub_store_player);
											}
										}
									}
								}
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}
						}
					}
				}
				//System.out.println("ON - " + overwrite_palyer_on_id + " OFF - " + overwrite_palyer_off_id);
				for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
					if(session_match.getHomeSquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getHomeSquad().get(i);
						session_match.getHomeSquad().remove(i);
						for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
							if(session_match.getHomeSubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
								session_match.getHomeSubstitutes().remove(j);
								session_match.getHomeSubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
				for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
					if(session_match.getAwaySquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getAwaySquad().get(i);
						session_match.getAwaySquad().remove(i);
						for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
							if(session_match.getAwaySubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
								session_match.getAwaySubstitutes().remove(j);
								session_match.getAwaySubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
			}
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			return objectMapper.writeValueAsString(session_match).toString();
		case "LOG_OVERWRITE_MATCH_STATS":
		System.out.println("vtp:- "+valueToProcess);
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchStats() != null) {
					for(MatchStats ms : session_match.getMatchStats()) {
						if(ms.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							ms.setPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							ms.setStats_type(valueToProcess.split(",")[3]);
							ms.setTotalMatchSeconds(Long.valueOf((Long.parseLong(valueToProcess.split(",")[4].split(":")[0]) * 60000) + (Long.parseLong(valueToProcess.split(",")[4].split(":")[1]) * 1000)));
						}
					}
				}
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							evnt.setEventPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							evnt.setEventLog("LOG_EVENT");
							evnt.setEventType(valueToProcess.split(",")[3]);
						}
					}
				}
			}

			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
			
			session_match = FootballFunctions.populateMatchVariables(footballService, session_match);
			session_match.setEvents(session_event.getEvents());

			return objectMapper.writeValueAsString(session_match).toString();
		
		case FootballUtil.LOG_OVERWRITE_TEAM_SCORE: 
			
			session_match.setHomeTeamScore(Integer.valueOf(valueToProcess.split(",")[1]));
			session_match.setAwayTeamScore(Integer.valueOf(valueToProcess.split(",")[2]));

			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);

			return objectMapper.writeValueAsString(session_match).toString();
		
		case "HOME_UNDO":
		
			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					switch (this_event.getEventLog().toUpperCase()) {
					case "HOME_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "HOME_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							
							break;
						}
						break;
					}
				}
			}
			
	
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match).toString();
			
		case "AWAY_UNDO":

			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					
					switch (this_event.getEventLog().toUpperCase()) {
					case "AWAY_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "AWAY_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							break;
						}
						break;
					}
				}
			}
			
	
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);
		
			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match).toString();	
			
		case FootballUtil.UNDO:

			if(valueToProcess.contains(",")) {
				if(session_event.getEvents() != null && Integer.valueOf(valueToProcess.split(",")[1]) <= session_event.getEvents().size()) {
					for(int iUndo=1;iUndo<=Integer.valueOf(valueToProcess.split(",")[1]);iUndo++) {

						this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
						
						switch (this_event.getEventLog().toUpperCase()) {
						case FootballUtil.LOG_EVENT:
							switch (this_event.getEventType().toUpperCase()) {
							case FootballUtil.GOAL: case FootballUtil.OWN_GOAL: case FootballUtil.PENALTY: case FootballUtil.YELLOW: case FootballUtil.RED:
							case FootballUtil.ASSISTS: case FootballUtil.SHOTS: case FootballUtil.SHOTS_ON_TARGET: case FootballUtil.OFF_SIDE: case FootballUtil.FOULS:
							case FootballUtil.CORNERS_CONVERTED: case FootballUtil.CORNERS: case FootballUtil.SAVES:
								this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
								session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
								for(Player plyr : session_match.getHomeSquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case FootballUtil.GOAL: case FootballUtil.PENALTY:
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										case FootballUtil.OWN_GOAL: 
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										}
									}
								}
								for(Player plyr : session_match.getAwaySquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case FootballUtil.GOAL: case FootballUtil.PENALTY:
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										case FootballUtil.OWN_GOAL: 
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										}
									}
								}
								break;
							}
							break;
						case FootballUtil.REPLACE:
							ArrayList<Player> undo_store_player = new ArrayList<Player>();
							for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
								if(session_match.getHomeSquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getHomeSquad().get(i));
									session_match.getHomeSquad().remove(i);
									for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
										if(session_match.getHomeSubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
											session_match.getHomeSubstitutes().remove(j);
											session_match.getHomeSubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
								if(session_match.getAwaySquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getAwaySquad().get(i));
									session_match.getAwaySquad().remove(i);
									for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
										if(session_match.getAwaySubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
											session_match.getAwaySubstitutes().remove(j);
											session_match.getAwaySubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							break;
						}
						session_event.getEvents().remove(this_event);
					}
				}
			}
			
			
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					session_match.getMatchFileName()), session_match);
			new ObjectMapper().writeValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
					session_match.getMatchFileName()), session_event);

			session_match.setEvents(session_event.getEvents());
			return objectMapper.writeValueAsString(session_match).toString();
			
		case FootballUtil.LOAD_TEAMS:
			
			if(!valueToProcess.trim().isEmpty()) {
				
				session_match.setHomeTeam(footballService.getTeam(FootballUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwayTeam(footballService.getTeam(FootballUtil.TEAM, valueToProcess.split(",")[1]));
				
				boolean correctTeamLoaded = false;
				if(session_match.getHomeSquad() != null && session_match.getHomeSquad().size() > 0) {
					for (Player plyr : session_match.getHomeSquad()) {
						if(plyr.getTeamId() == Integer.valueOf(valueToProcess.split(",")[0])) {
							correctTeamLoaded = true;
						} else {
							correctTeamLoaded = false;
							break;
						}
					}
				}
				if (correctTeamLoaded == false) {
					session_match.setHomeSquad(footballService.getPlayers(FootballUtil.TEAM, valueToProcess.split(",")[0]));
				}

				correctTeamLoaded = false;
				if(session_match.getAwaySquad() != null && session_match.getAwaySquad().size() > 0) {
					for (Player plyr : session_match.getAwaySquad()) {
						if(plyr.getTeamId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							correctTeamLoaded = true;
						} else {
							correctTeamLoaded = false;
							break;
						}
					}
				}
				if (correctTeamLoaded == false) {
					session_match.setAwaySquad(footballService.getPlayers(FootballUtil.TEAM, valueToProcess.split(",")[1]));
				}				
			}
			
			return objectMapper.writeValueAsString(session_match).toString();

		case FootballUtil.LOAD_MATCH: case FootballUtil.LOAD_SETUP:

			session_match = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.MATCHES_DIRECTORY + 
					valueToProcess), Match.class);
			session_match.setMatchFileName(valueToProcess);
			switch (whatToProcess.toUpperCase()) {
			case FootballUtil.LOAD_MATCH:
				
				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + valueToProcess).exists()) {
					session_event = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + 
							valueToProcess), EventFile.class);
				} else {
					session_event = new EventFile();
					new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.EVENT_DIRECTORY + valueToProcess).createNewFile();
				}

				if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CLOCK_JSON).exists()) {
					session_clock = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CLOCK_JSON), Clock.class);
					session_match.setClock(session_clock);
				} else {
					session_match.setClock(new Clock());
				}
				if(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
				        + FootballUtil.SPORTVUSTATISTIC 
				        + FootballUtil.XML_EXTENSION).exists()) {
					session_match.setXmlTimeSpan(new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss 'GMT'").format(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
					        + FootballUtil.SPORTVUSTATISTIC 
					        + FootballUtil.XML_EXTENSION).lastModified()));
				}
				break;
				
			}
			
			switch (whatToProcess.toUpperCase()) {
			case FootballUtil.LOAD_SETUP:
				session_match.setHomeOtherSquad(FootballFunctions.getPlayersFromDB(footballService, FootballUtil.HOME, session_match));
				session_match.setAwayOtherSquad(FootballFunctions.getPlayersFromDB(footballService, FootballUtil.AWAY, session_match));
				break;
			}
			session_match = FootballFunctions.populateMatchVariables(footballService,session_match);

			session_match.setEvents(session_event.getEvents());

			return objectMapper.writeValueAsString(session_match).toString();			
		case "READ-MATCH-AND-POPULATE":
	        if (session_EuroLeague != null && valueToProcess != null && !valueToProcess.isEmpty()&& !"undefined".equalsIgnoreCase(valueToProcess.trim()) && 
	        		!", undefined".equalsIgnoreCase(valueToProcess.trim())) {
	        	 int lastIndex = valueToProcess.lastIndexOf(',');
	        	 String remainingString ="";
	        	 if(lastIndex >0) {
	        		 remainingString = valueToProcess.substring(0, lastIndex);
				     wtp = valueToProcess.substring(lastIndex + 1);
					 vtp = remainingString; 
	        	 }
				 session_EuroLeague.updateGraphic(print_writers.get(0), session_live_event, footballService, vtp);
			}
			return objectMapper.writeValueAsString(session_match).toString();
		case "READ_CLOCK":
			
			if (session_match == null) {
		        session_match = new Match();
		    }

		    File clockFile = new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CLOCK_JSON);		    
		    
			if(new File(FootballUtil.FOOTBALL_DIRECTORY + FootballUtil.CLOCK_JSON).exists() && clockFile.canRead()) {
				session_clock = new ObjectMapper().readValue(clockFile, Clock.class);
				session_match.setClock(session_clock);
				if(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY + FootballUtil.SPORTVUSTATISTIC + FootballUtil.XML_EXTENSION).exists()) {
					session_match.setXmlTimeSpan(new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss 'GMT'").format(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY
					        + FootballUtil.SPORTVUSTATISTIC + FootballUtil.XML_EXTENSION).lastModified()));
				}
				if(session_selected_broadcaster != null) {
					switch (session_selected_broadcaster) {
					case FootballUtil.I_LEAGUE:
						session_i_league.updateScoreBug(print_writers.get(0),session_selected_scenes,session_match);
						break;
					case FootballUtil.SANTOSH_TROPHY:
						session_santosh_trophy.updateScoreBug(print_writers.get(0),session_selected_scenes, session_match);
						break;
					case FootballUtil.VIZ_SANTOSH_TROPHY:
						session_viz_santosh_trophy.updateScoreBug(print_writers.get(0),session_selected_scenes, session_match,footballService);
						break;
					case FootballUtil.VIZ_TRI_NATION:
						session_viz_tri_nation.updateScoreBug(print_writers.get(0),session_selected_scenes, session_match,footballService);
						break;
					case FootballUtil.SUPER_CUP:
						session_super_cup.updateScoreBug(print_writers,session_selected_scenes, session_match,footballService);
						break;
					}
				}
			}
			
			if(session_selected_broadcaster != null) {
				switch (session_selected_broadcaster) {
				case FootballUtil.SUPER_CUP:
					if (football != null) {
				    	try {
				    		synchronized (football) {
				    			if(new File(FootballUtil.IN_MATCH).exists() && new File(FootballUtil.IN_MATCH).canRead()&& new File(FootballUtil.IN_MATCH).length() > 0) {
				    				football = new ObjectMapper().readValue(new File(FootballUtil.IN_MATCH), Football.class);
				    			}
							}
						} catch (Exception e) {
							System.err.println("setJsonDataInMatchApi issue in this function" + e.getMessage());
						}
				    } else {
				        System.err.println("api_session_match is null.");
				    }
					break;
				}
			}
			
			return objectMapper.writeValueAsString(session_match).toString();
		
		case "EVENT-OPTIONS":
			session_live_event = new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY + "MatchEvent.json"), LiveMatch.class);
			return objectMapper.writeValueAsString(session_live_event).toString();
		case "MATCH_INSIGHTS-OPTIONS":case "MATCH_INSIGHTS_GS-OPTIONS":case "MATCH_INSIGHTS_TEAM-OPTIONS": case "FT-OPTIONS":
		case "MATCH_INSIGHTS_RESULT-OPTIONS":	
			return objectMapper.writeValueAsString(new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY + "MatchInsights.json"), PassMatrix.class)).toString();
		case "HEATMAP-OPTIONS": case "HEATMAP-GRAPHIC-OPTION":
			return objectMapper.writeValueAsString(new ObjectMapper().readValue(new File(FootballUtil.FOOTBALL_STATISTICS_DIRECTORY + "LiveData.json"), LiveMatch.class)).toString();
		default:
			switch (session_selected_broadcaster) {
			case FootballUtil.I_LEAGUE:
				session_i_league.ProcessGraphicOption(print_writers.get(0),whatToProcess,session_match, session_clock,footballService, 
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.SANTOSH_TROPHY:
				session_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.VIZ_SANTOSH_TROPHY:
				session_viz_santosh_trophy.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;
			case FootballUtil.VIZ_TRI_NATION:
				session_viz_tri_nation.ProcessGraphicOption(print_writers.get(0),whatToProcess, session_match, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;	
			case FootballUtil.SUPER_CUP:
				session_super_cup.ProcessGraphicOption(print_writers,whatToProcess, session_match, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				return objectMapper.writeValueAsString(session_super_cup).toString();
			case FootballUtil.EURO_LEAGUE:
//				System.out.println("whatToProcess : " + whatToProcess + " - valueToProcess : " + valueToProcess);
				session_EuroLeague.ProcessGraphicOption(print_writers.get(0), whatToProcess, session_live_event, session_clock, footballService, 
						session_selected_scenes, valueToProcess);
				break;
			}
			return objectMapper.writeValueAsString(session_match).toString();
		}
	}
	private String handleHeadToHead(String session_MasterFootballDirectory) throws Exception {
	    FootballFunctions.exportMatchData(session_match, session_MasterFootballDirectory);
	    return objectMapper.writeValueAsString(session_match);
	}
}
