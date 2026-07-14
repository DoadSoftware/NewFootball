var match_data,leaderboard,home_team,away_team,home_team_name,away_team_name,current_player;
var isTrue = true;
var graphics,preGraphic;
function millisToMinutesAndSeconds(millis) {
  var m = Math.floor(millis / 60000);
  var s = ((millis % 60000) / 1000).toFixed(0);
  return (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s :s);
}
function displayMatchTime() {
	switch ($('#selectedBroadcaster').val()) {
    case 'EURO_LEAGUE':
           processFootballProcedures('READ-MATCH-AND-POPULATE', graphics);
        break; 
     default:
       processFootballProcedures('READ_CLOCK',null);
     break;
	}
}

function startMatchTimeUpdate() {
   matchTimeInterval = setInterval(displayMatchTime, 5000);
}

function getPlayerMatchStats(playerId){
	var value='';
	if(match_data.events != null && match_data.events.length > 0)
	{
		for(var k = 0; k < match_data.events.length; k++){
			if(match_data.events[k].eventPlayerId == playerId){
				if(match_data.events[k].eventType == 'yellow'){
					value = value + 'Y';
				}else if(match_data.events[k].eventType == 'red'){
					value = value + 'R';
				}
			}else if(match_data.events[k].eventPlayerId == 0){
				if(match_data.events[k].offPlayerId == playerId){
					value = 'red,' + value;
				}else if(match_data.events[k].onPlayerId == playerId){
					value = 'green,' + value;
				}
			}
			else{
				value = value + '';
			}
		}
	}else{
		value = value + '';
	}
	return value ;
}
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function afterPageLoad(whichPageHasLoaded)
{	
	switch ($('#selectedBroadcaster').val()) {
    case 'EURO_LEAGUE':
        $("#select_event_div").empty();
        $("#match_configuration").empty();
        $("#football_div").empty();
		ShowCaption();
		startMatchTimeUpdate();
        break; 
	}


	switch (whichPageHasLoaded) {
	case 'SETUP':
		$('#homeTeamId').select2();
		$('#awayTeamId').select2();
		$('#homeTeamFormationId').select2();
		$('#awayTeamFormationId').select2();
		$('#homeTeamJerseyColor').select2();
		$('#awayTeamJerseyColor').select2();
		$('#homeTeamGKJerseyColor').select2();
		$('#awayTeamGKJerseyColor').select2();
		break;
	case 'MATCH':
		addItemsToList('LOAD_EVENTS',null);
		break;
	}
}
function ShowCaption(){
	if(isTrue){
			// Create a table element
		
        table = document.createElement('table');
        table.setAttribute('class', 'table table-bordered');
                
         tbody = document.createElement('tbody');
    
        table.appendChild(tbody);
        $("#football_div").append(table);
        $("#football_div").append("<br>");
        $("#football_div").append("<br>");
        $("#football_div").append("<br>");
       
        // Header row
        headerRow = document.createElement('tr');
        headerText = document.createElement('th');
        headerText.setAttribute('class', 'table thead-dark');
        headerText.innerHTML = 'Captions';
        headerRow.appendChild(headerText);
        tbody.appendChild(headerRow);

        // Data rows
      captions = [
			['PLAYING XI', 'Ctrl+F5'],
			['DB DATA', 'Ctrl+E'],
            ['MATCH STATS', 'Ctrl+F'],
            ['POINTS TABLE', 'Ctrl+S'],
	        ];

        captions.forEach(function(caption) {
        	row = document.createElement('tr');
            cell1 = document.createElement('td');
            cell2 = document.createElement('td');
            cell1.innerHTML = caption[0];
            cell2.innerHTML = caption[1];
            row.appendChild(cell1);
            row.appendChild(cell2);
            tbody.appendChild(row);
        });
         $("#football_div").show();
       	isTrue=false;
       }
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
	
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
			}if(document.getElementById('match_games_hdr')){
				document.getElementById('match_games_hdr').innerHTML = 'GAMES : ' + match_data.clock.matchTimeStatus;
			}
			document.getElementById('match_modified_hdr').innerHTML = '<b>SportVUStatistic File Last Modified Time</b> : ' + 
					millisToMinutesAndSeconds(match_data.xmlTimeSpan)
			
		}
		break;
	case 'UPDATE-CONFIG':
		document.getElementById('configuration_file_name').value = $('#select_configuration_file option:selected').val();
		document.getElementById('selectedBroadcaster').value = dataToProcess.broadcaster;
		document.getElementById('vizIPAddress').value = dataToProcess.ipAddress;
		document.getElementById('vizPortNumber').value = dataToProcess.portNumber;
		document.getElementById('vizSecondaryIPAddress').value = dataToProcess.secondaryipAddress;
		document.getElementById('vizSecondaryPortNumber').value = dataToProcess.secondaryportNumber;
		break;
/*	case 'MATCH':
	
		if(match_data) {
			document.getElementById('select_match_halves').value = match_data.clock.matchHalves;
		} else {
			document.getElementById('select_match_halves').selectedIndex = 0;
		}
		break;*/
		
	case 'SETUP':
		
		if(dataToProcess) {
			document.getElementById('matchFileName').value = dataToProcess.matchFileName;
			document.getElementById('tournament').value = dataToProcess.tournament;
			document.getElementById('matchIdent').value = dataToProcess.matchIdent;
			document.getElementById('matchId').value = dataToProcess.matchId;
			document.getElementById('groundId').value = dataToProcess.groundId;
			document.getElementById('homeSubstitutesPerTeam').value = dataToProcess.homeSubstitutesPerTeam;
			document.getElementById('awaySubstitutesPerTeam').value = dataToProcess.awaySubstitutesPerTeam;
			document.getElementById('homeTeamId').value = dataToProcess.homeTeamId;
			document.getElementById('awayTeamId').value = dataToProcess.awayTeamId;
			document.getElementById('homeTeamFormationId').value = dataToProcess.homeTeamFormationId;
			document.getElementById('awayTeamFormationId').value = dataToProcess.awayTeamFormationId;
			document.getElementById('homeTeamJerseyColor').value = dataToProcess.homeTeamJerseyColor;
			document.getElementById('awayTeamJerseyColor').value = dataToProcess.awayTeamJerseyColor;
			document.getElementById('homeTeamGKJerseyColor').value = dataToProcess.homeTeamGKJerseyColor;
			document.getElementById('awayTeamGKJerseyColor').value = dataToProcess.awayTeamGKJerseyColor;
			addItemsToList('LOAD_TEAMS',dataToProcess);
			document.getElementById('save_match_div').style.display = '';
		} else {
			document.getElementById('matchFileName').value = '';
			document.getElementById('tournament').value = '';
			document.getElementById('matchIdent').value = '';
			document.getElementById('matchId').value = '';
			document.getElementById('groundId').selectedIndex = 0;
			document.getElementById('homeSubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('awaySubstitutesPerTeam').selectedIndex = 0;
			document.getElementById('homeTeamId').selectedIndex = 0;
			document.getElementById('awayTeamId').selectedIndex = 1;
			document.getElementById('homeTeamFormationId').selectedIndex = 0;
			document.getElementById('awayTeamFormationId').selectedIndex = 1;
			document.getElementById('homeTeamJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamJerseyColor').selectedIndex = 1;
			document.getElementById('homeTeamGKJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamGKJerseyColor').selectedIndex = 1;
			addItemsToList('LOAD_TEAMS',null);
			document.getElementById('save_match_div').style.display = 'none';
		}
		$('#homeTeamId').prop('selectedIndex', document.getElementById('homeTeamId').options.selectedIndex).change();
		$('#awayTeamId').prop('selectedIndex', document.getElementById('awayTeamId').options.selectedIndex).change();
		
		$('#homeTeamFormationId').prop('selectedIndex', document.getElementById('homeTeamFormationId').options.selectedIndex).change();
		$('#awayTeamFormationId').prop('selectedIndex', document.getElementById('awayTeamFormationId').options.selectedIndex).change();
		
		$('#homeTeamJerseyColor').prop('selectedIndex', document.getElementById('homeTeamJerseyColor').options.selectedIndex).change();
		$('#awayTeamJerseyColor').prop('selectedIndex', document.getElementById('awayTeamJerseyColor').options.selectedIndex).change();
		
		$('#homeTeamGKJerseyColor').prop('selectedIndex', document.getElementById('homeTeamGKJerseyColor').options.selectedIndex).change();
		$('#awayTeamGKJerseyColor').prop('selectedIndex', document.getElementById('awayTeamGKJerseyColor').options.selectedIndex).change();
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {
        	switch(whatToProcess.toUpperCase()) {
			case 'RESET_MATCH_BEFORE_SETUP_MATCH':
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'back_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processUserSelectionData(whatToProcess,dataToProcess){

	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		document.getElementById('which_keypress').value = dataToProcess;
		switch (dataToProcess) {
			
		case '[': '219'
			processFootballProcedures('HOME_GOAL');
			break;
		case ']': '221'
			processFootballProcedures('AWAY_GOAL');
			break;
		case '.': '190'
			processFootballProcedures('HOME_UNDO');
			break;
		case '/': '191'
			processFootballProcedures('AWAY_UNDO');
			break;
		
		case 'Escape': '27'
			$('#select_graphic_options_div').empty();
			document.getElementById('select_graphic_options_div').style.display = 'none';
			$("#select_event_div").show();
			$("#match_configuration").show();
			$("#football_div").show();
			break;
			
		case '`': '192'
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('AD-OPTIONS',null);
					
					//processFootballProcedures('POPULATE-HERO-SPONSOR');
					break;
				default:
					processFootballProcedures('POPULATE-QUAIFIERS');
					break;	
			}
			break;
			
		case ' '://Space
			processFootballProcedures('CLEAR-ALL');
			break;
		case '-': '189'
			if(confirm('It will Also Delete Your Preview from Directory...\r\n\r\n Are You Sure To Animate Out?') == true){
				processFootballProcedures('ANIMATE-OUT');
			}
			break;
			
/*--------------------------------------------------- EURO LEAGUE ------------------------------------------------------------------------------*/
		case 'Control_F5':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('LBAND_PLAYINGXI_OPTION',null);
					break;
			}
			break;
		case 'Control_F1':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('POPULATE-H2H');
					break;
			}
			break;
		case 'Control_F2':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('POPULATE-H2H_LIVE_WIN');
					break;
			}
			break;
		case 'Control_F3':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('MATCH_INSIGHTS_TEAM-OPTIONS');
					break;
			}
			break;
			
		case 'Control_F8':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SPONSOR-OPTIONS',null);
					break;
			}
			break;
		case 'Control_F9':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('ANIMATE-OUT-SPONSOR_RIGHT');
					break;
			}
			break;
		case 'Control_F10':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SPONSOR_BOTTOM-OPTIONS',null);
					break;
			}
			break;
		case 'Control_F11':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('ANIMATE-OUT-SPONSOR_EURO');
					break;
			}
			break;
			
		case 'Control_g':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('POPULATE-EXPECTED_GOALS');
					break;
			}
			break;
		case 'Control_v':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					alert('hello');
					processFootballProcedures('POPULATE-WON_CORNERS');
					break;
			}
			break;
		case 'Control_s':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('POPULATE-POINTS_TABLE_LB');
					break;
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-FF_SCORE');
					break;
			}
			break;
		case 'Control_F4':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('MATCH_INSIGHTS_GS-OPTIONS');
					break;
			}
			break;
		case 'Control_i':
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('POPULATE-WIN_H2H');
					break;
			}
			break;
		case 'Control_f':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('LBAND_MATCH_DATA_OPTION',null);
					//processFootballProcedures('POPULATE-FOUL');
					break;
			}
			break;
		case 'Control_a':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('ACCURATE_PASS_OPTION',null);
					break;
			}
			break;
		case 'Control_d':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-CLEARANCE');
					break;
			}
			break;
		case 'Control_p':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-SET_PIECES');
					break;
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-PLAYOFF_TREE');
					break;
			}
			break;
		case 'Control_h':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-SHOOTING_ACCURACY');
					break;
			}
			break;
		case 'Control_b':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-DUEL_WON');
					break;
			}
			break;
		case 'Control_e':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE': case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('DB_GRAPHICS');
					break;
			}
			break;
		case 'Control_m':
			processFootballProcedures('POPULATE-WON_CONTEST');
			break;
			
		case 'Control_u':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-LWP');
					break;
			}
			break;
		case 'Control_q':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('EXTRA_DATA_GS-OPTIONS');
					break;
			}
			break;
		case 'Control_y':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-EXA_EXG');
					break;
			}
			break;
		case 'Control_j':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-PLAYER_RATING');
					break;
				case'SUPER_CUP':
					processFootballProcedures('POPULATE-FF-PLAYER-PROFILE');
					break;
			}
			break;
		
		case 'Control_F12':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide(); 
					processFootballProcedures('FT-OPTIONS');
					break;
			case'SUPER_CUP':
					processFootballProcedures('POPULATE-FF-PLAYER-POINTER');
					break;
			}
			break;
		
		case 'Control_c':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-POSS_WON_ATT_3RD');
					break;
			}
			break;
		case 'Control_x':
			switch ($('#selectedBroadcaster').val()){
				case'EURO_LEAGUE':
					processFootballProcedures('POPULATE-TOTAL_FINAL_THIRD_PASSES');
					break;
			}
			break;
/*------------------------------------------------------------------------------------------------------------------------------------------*/
			
		case '=': '187'
			processFootballProcedures('ANIMATE-OUT-SCOREBUG');
			break;
		case 'o': '79 - SCOREBUG_STAT'
			processFootballProcedures('ANIMATE-OUT-SCOREBUG_STAT');
			break;
		case 's': '83 - Animate In/Out Sponsor'
			processFootballProcedures('ANIMATE-IN-SPONSOR');
			break;
		case 'Alt_g': 'Animate In/Out Flag'
			processFootballProcedures('ANIMATE-IN-FLAG');
			break;
		case 'v': '86'
			processFootballProcedures('POPULATE-EXTRA_TIME_HALF');
			break;
			
		case 'F1': '112-ScoreBug'
			processFootballProcedures('POPULATE-SCOREBUG');
			break;
			
		case 'i': '73 - SCOREBUG MATCH STATS'	
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('SCOREBUG_GRAPHICS-OPTIONS');
					break;
				default:
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG_OPTION',null);
				break;
			}
			break;
		case 'j':
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('SCOREBUG_API_GRAPHICS-OPTIONS');
					break;
				default:
					if($('#selectedBroadcaster').val()!='I_LEAGUE'){
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG_API_OPTION',null);	
				}
				break;
			}
			break;
			
		case 'b': '66'
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG-TEAM_STATS-OPTIONS',null);
					break;
			}
			break;
		case 'Alt_j':
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG-H2H_BOTH-OPTIONS',null);
					break;
			}
			break;
		case 'Alt_k': 
			switch ($('#selectedBroadcaster').val()){
				case'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('LBAND_ATTACKING_ZONE',null);
					//processFootballProcedures('POPULATE-ATTACKING_ZONE');
					break;
			}
			break;
			
		case 'k': '75'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('SCOREBUG-CARD-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		
		case 'm': '77'
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					processFootballProcedures('EVENT-OPTIONS');
					break;
				default:
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG-SUBSTITUTION-OPTIONS',null);
					break;
			}
			break;
		
		case 'n': '78'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			processFootballProcedures('SCOREBUGPROMO_GRAPHICS-OPTIONS');
			break;	
			
		case 'z': '90'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					processFootballProcedures('POPULATE-RED_CARD');
					break;
				case 'VIZ_TRI_NATION': case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('RED_CARD_OPTION',null);
					break;
			}
			
			break;
		case 'x': '88'
			processFootballProcedures('ANIMATE-OUT-RED_CARD');
			break;
			
		case 'e': '69 Injury-TIME'
			addItemsToList('EXTRA-TIME_OPTION',null);
			break;
		case 'c': '67 AnimateOut InjuryTime' 
			processFootballProcedures('ANIMATE-OUT-EXTRA_TIME');
			break;
			
		case 'd': '68'
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('TEAMFIXTURE_GRAPHICS-OPTIONS');
					break;
			}
			break;
			
		case 'h': '72'
			switch ($('#selectedBroadcaster').val()){
				case 'EURO_LEAGUE':
					 processFootballProcedures('HEATMAP-OPTIONS');
					break;
			}
			
			break;
			
		case 'Alt_j': '74'
		switch ($('#selectedBroadcaster').val()){
			case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('MATCH_INSIGHTS-OPTIONS');
				break;
			case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': 
				processFootballProcedures('POPULATE-FF-MATCHSTATS');
				break;
			}
			
			break;
			
		case 'F2': '113 NAMESUPER DB'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			processFootballProcedures('NAMESUPER_GRAPHICS-OPTIONS');
			break;
			
		case 'F3': '114 NAMESUPER PLAYER'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('NAMESUPER_PLAYER-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
			
		case 'Alt_m': 
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('PLAYER_TOUCH_MAP-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		
		case 'F4': '115 FF-MATCHID'
			processFootballProcedures('POPULATE-FF-MATCHID');
			break;
			
		case 'F5':'116 FF-PLAYING XI'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('PLAYINGXI-OPTIONS',null);
			break;
			
		case 'Alt_f':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('LT_LINE_UP-OPTIONS',null);
			break;
		case 'Shift_F':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('LT_AVG_FORMATION-OPTIONS',null);
			break;
		
		case 'Alt_v':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('VERTICAL_FLIPPER-OPTIONS',null);
			break;
		case 'Alt_F6':
			processFootballProcedures('POPULATE_HIGHLIGHT_SCORE_BUG');
		break;	
		case 'F6': '117 BUG DB'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':case 'EURO_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('BUG_DB_GRAPHICS-OPTIONS');
					break;
				case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('BUG_DB_GRAPHICS-OPTIONS');
					break;
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('BUG_DB_GRAPHICS-OPTIONS');
					break;	
			}
			break;
			
		case 'F7': '118 SCORE-UPDATE'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('L3-SCOREUPDATE',null);
			//processFootballProcedures('POPULATE-L3-SCOREUPDATE');
			break;
			
		case 'F8': '119 MATCH_STAT'
			processFootballProcedures('POPULATE-L3-MATCHSTATUS');
			break;
		case 'Shift_F8': 'TOURNAMENT_STAT'
			processFootballProcedures('POPULATE-FF-TOURNAMENT_STATS');
			break;
		case 'Alt_F8': 'TEAM COMPARISON'
			processFootballProcedures('POPULATE-FF-TEAM_COMPARISON');
			break;
			
		case 'F9': '120 NAMESUPER CARD'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('NAMESUPER-CARD-OPTIONS',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
			
		case 'F10': '121 SUBSTITUTE'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('SUBSTITUTE-OPTIONS',null);
			break;
			
		case 'F11': '122 PLAYSOFF'
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-PLAYOFFS');
					break;
			}
			break;
			
		case 'F12': '123 - NAMESUPER STAFF'
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			home_team = match_data.homeTeamId;
			away_team = match_data.awayTeamId;
			home_team_name = match_data.homeTeam.teamName4;
			away_team_name = match_data.awayTeam.teamName4;
			processFootballProcedures('STAFF_GRAPHICS-OPTIONS');
			break;
		
		case 'Alt_F6': 'BUG HIGHLIGHT'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					processFootballProcedures('POPULATE-HIGHLIGHT');
					break;
				case 'SANTOSH_TROPHY':
					processFootballProcedures('POPULATE-HIGHLIGHT');
					break;
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-HIGHLIGHT_SCOREBUG');
					break;
			}
			break;
			
		case 'a': '65 - LT MATCHID'
			processFootballProcedures('POPULATE-LT-MATCHID');
			break;
		
		case 'f': '70 FIXTURES'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('FORMATION-OPTIONS',null);
					break;
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('FIXTURES-OPTIONS',null);
					break;
			}
			break;	
		
		case 'g': '71 GROUP'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					processFootballProcedures('POPULATE-DOUBLE_SUBS');
					break;
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-FF-TEAMS');
					break;
			}
			break;
			
		case 'l': '76 - ROAD TO FINAL'
			processFootballProcedures('POPULATE-ROAD-TO-FINAL');
			break;
		
		case 'Alt_p': 'POINTS TABLE 2'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG_OPTION_2',null); 
					break;
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('POINTS_TABLE2_OPTION',null);
					break;
			}
			break;
			
		case 'Shift_P': 'MINI POINTS TABLE / AS STANDS'
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('MINI_POINTS_TABLE_OPTION',null);
					break;
			}
			break;
		
		case 'p': '80 - POINTS TABLE'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE':
					processFootballProcedures('POPULATE-POINTS_TABLE');
					break;
				case 'SANTOSH_TROPHY': case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('POINT_TABLE-OPTIONS',null);
					break;
			}
			break;
		
		case 'q': '81 - SINGLE MATCH PROMO'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('PROMO_GRAPHICS-OPTIONS');
					break;
			}
			break;
		case 'Alt_e':
			switch ($('#selectedBroadcaster').val()){
				case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					processFootballProcedures('LT_PLAYER_STATS_GRAPHICS-OPTIONS');
					break;
			}
			break;
			
		case 'r': '82'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					//alert('I_LEAGUE');
					processFootballProcedures('POPULATE-LT-BUG_REPLAY');
					break;
				 case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SCOREBUG_PLAYERS_STATS-OPTIONS',null);
					addItemsToList('POPULATE-PLAYER',null);
					processFootballProcedures('CHECK_FOR_PLAYER_DATA');
					break;
			}
			break;
		
		case 't': '84 - OFFICIALS'
			processFootballProcedures('POPULATE-OFFICIALS');
			break;
		
		case 'w': '87 - DOUBLE MATCH PROMO'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					//alert('I_LEAGUE');
					processFootballProcedures('POPULATE-DOUBLE_PROMO');
					break;
				 case 'SUPER_CUP':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('DOUBLE_PROMO-OPTIONS',null);
					break;
			}
			break;
			
		case 'u': '85 - PENALTY COUNT RESET'
			switch ($('#selectedBroadcaster').val()){
				case 'I_LEAGUE': case 'SANTOSH_TROPHY':
					$("#select_event_div").hide();
					$("#match_configuration").hide();
					$("#football_div").hide();
					addItemsToList('SINGLE_SUBSTITUTE-OPTIONS',null);
					break;
				case 'SUPER_CUP':
					processFootballProcedures('POPULATE-CHANGE_PENALTY');
					break;
			}
			break;
		
		case 'y': '89 - PENALTY'
			processFootballProcedures('POPULATE-PENALTY');
			break;
			
		case 'Alt_h':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('HEATMAP_PEAKDISTACE-OPTION',null);
			addItemsToList('POPULATE-PLAYER',null);
			break;
		
		case 'Alt_s':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('TOP_STATS-OPTIONS',null);
			break;
			
		case 'Alt_b':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			processFootballProcedures('LEADERBOARD_GRAPHICS-OPTIONS');
			break;
		
		case 'Alt_c':
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('CHETTRI-OPTIONS',null);
			break;
		case 'Alt_d':
			processFootballProcedures('POPULATE-FF-HEADTOHEAD');
			break;
		case 'Alt_r': 
			switch ($('#selectedBroadcaster').val()){
				default:
				$("#select_event_div").hide();
				$("#match_configuration").hide();
				$("#football_div").hide();
				addItemsToList('FIXTURES_RESULTD-OPTIONS',null);
				break;
			}
			break;
		case 'Alt_q': 
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#football_div").hide();
			addItemsToList('TEAM_TOUCH-OPTION',null);
			break;
		
		}
		
		break;
	}
}
function processUserSelection(whichInput)
{	
	switch ($(whichInput).attr('name')) {
	case 'selectHomePlayersPosition': case 'selectAwayPlayersPosition':
		if(current_player == null) { 
			current_player = whichInput.id;
			document.getElementById(current_player).style.border = '2px solid red';
			return;
		}
		if(current_player == whichInput.id) {
			document.getElementById(current_player).style.border = '';
			current_player = null;
			alert('Same batter selected ' + current_player);
			return;
		}
		if(current_player.substring(0,4) == whichInput.id.substring(0,4)) {
			
			option = document.getElementById(
				current_player.substring(0,4) + 'Player_' + current_player.split("_")[1]).selectedIndex;
			document.getElementById(
				current_player.substring(0,4) + 'Player_' + current_player.split("_")[1]).selectedIndex 
				= document.getElementById(whichInput.id.substring(0,4) + 'Player_' 
				+ whichInput.id.split("_")[1]).selectedIndex;
			document.getElementById(
				whichInput.id.substring(0,4) + 'Player_' + whichInput.id.split("_")[1]).selectedIndex = option;	
				
			$("#" + whichInput.id.substring(0,4) + 'Player_' + whichInput.id.split("_")[1]).trigger("change");
			$("#" + current_player.substring(0,4) + 'Player_' + current_player.split("_")[1]).trigger("change");
					
			option = document.getElementById(
				current_player.substring(0,4) + 'CaptainGoalKeeper_' + current_player.split("_")[1]).selectedIndex;
			document.getElementById(
				current_player.substring(0,4) + 'CaptainGoalKeeper_' + current_player.split("_")[1]).selectedIndex 
				= document.getElementById(whichInput.id.substring(0,4) + 'CaptainGoalKeeper_' 
				+ whichInput.id.split("_")[1]).selectedIndex;
			document.getElementById(
				whichInput.id.substring(0,4) + 'CaptainGoalKeeper_' + whichInput.id.split("_")[1]).selectedIndex = option;	
				
			$("#" + whichInput.id.substring(0,4) + 'CaptainGoalKeeper_' + whichInput.id.split("_")[1]).trigger("change");
			$("#" + current_player.substring(0,4) + 'CaptainGoalKeeper_' + current_player.split("_")[1]).trigger("change");
			
			document.getElementById(current_player).style.border = '';
			current_player = null;
			
		} else {
			
			alert('Different team selected first team = ' + current_player.substring(0,4) 
				+ ', other team = ' + whichInput.id.substring(0,4) + '. Swap NOT available');
			document.getElementById(current_player).style.border = '';
			current_player = null;
	
		}
		break;		
	case 'select_configuration_file':
		processFootballProcedures('GET-CONFIG-DATA');
		break;
	case 'selectStatsType':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			if ($('#selectStatsType option:selected').val() == 'Formation_with_image') {
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum';
			}else{
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation_NO_Image.sum';
			}
			break;
		case 'SANTOSH_TROPHY':
			if ($('#selectStatsType option:selected').val() == 'Formation_with_image') {
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation.sum';
			}else{
				formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation_NO_Image.sum';
			}
			break;
		}
		break;
	case 'overwrite_match_stats_index':

		document.getElementById('overwrite_match_stats_player_id').selectedIndex = 0;
		document.getElementById('overwrite_match_stats_type').value = '';
		document.getElementById('overwrite_match_stats_total_seconds').value = '';
	
		match_data.matchStats.forEach(function(ms){
			if ($('#overwrite_match_stats_index option:selected').val() == ms.statsId) {
				document.getElementById('overwrite_match_stats_player_id').value = ms.playerId;
				document.getElementById('overwrite_match_stats_type').value = ms.statsType;
				document.getElementById('overwrite_match_stats_total_seconds').value = millisToMinutesAndSeconds(ms.totalMatchSeconds);
			}
		});

		break;

	case 'load_scene_btn':
	
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
    
	  	document.initialise_form.submit();
		break;
	
	case 'cancel_graphics_btn':
		switch ($('#selectedBroadcaster').val()) {
			case 'EURO_LEAGUE':
				processFootballProcedures('CANCEL');
				$('#select_graphic_options_div').empty();
				document.getElementById('select_graphic_options_div').style.display = 'none';
				$("#football_div").show();
				$("#check_data_div").hide();
				graphics = 'cancel';
				ShowCaption();
				break;
			default:
				$('#select_graphic_options_div').empty();
				document.getElementById('select_graphic_options_div').style.display = 'none';
				$("#select_event_div").show();
				$("#check_data_div").hide();
				$("#match_configuration").show();
				$("#football_div").show();
			break;
			}
		break;
		case 'cancel_dropdown_btn':
				$('#select_graphic_options_div').empty();
				document.getElementById('select_graphic_options_div').style.display = 'none';
				$("#select_event_div").show();
				$("#match_configuration").show();
				$("#football_div").show();
		break;
	case 'selectedBroadcaster':
		switch ($('#selectedBroadcaster :selected').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY':
			$('#vizPortNumber').attr('value','1980');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;
		case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			$('#vizPortNumber').attr('value','6100');
			$('label[for=vizScene], input#vizScene').hide();
			$('label[for=which_scene], select#which_scene').hide();
			$('label[for=which_layer], select#which_layer').hide();
			break;	
		}
		break;
	case 'homePlayers': case 'awayPlayers':
		$('#selected_player_name').html(whichInput.innerHTML);
		$('#selected_player_id').val(whichInput.value);
		document.getElementById('select_event_div').style.display = '';
		break;
	case 'log_teams_score_overwrite_btn': case 'log_match_stats_overwrite_btn': case 'log_match_subs_overwrite_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {
		case 'log_teams_score_overwrite_btn': 
			processFootballProcedures('LOG_OVERWRITE_TEAM_SCORE',whichInput);
			break;
		case 'log_match_stats_overwrite_btn':
			processFootballProcedures('LOG_OVERWRITE_MATCH_STATS',whichInput);
			break;
		case 'log_match_subs_overwrite_btn':
			processFootballProcedures('LOG_OVERWRITE_MATCH_SUBS',whichInput);
			break;
		}
		break;
	case 'number_of_undo_txt':
		if(whichInput.value < 0 && whichInput.value > match_data.events.length) {
			alert('Number of undos is invalid.\r\n Must be a positive number and less than the number of events available [' + match_data.events.length + ']');
			whichInput.selected = true;
			return false;
		}
		break;
	case 'selectPlayer':
		processFootballProcedures('CHECK_FOR_PLAYER_DATA');
		break;
	case 'selectTeams':
		processFootballProcedures('HEATMAP-GRAPHIC-OPTION');
		addItemsToList('POPULATE-PLAYER_CAREER',match_data);
		break;
	case 'selectLeaderBoard':
		addItemsToList('POPULATE-LEADERBOARD_PLAYER',leaderboard);
		break;	
	case 'selectTeam': case 'selectCaptainGoalKeeper':
		addItemsToList('POPULATE-PLAYER',match_data);
		if(document.getElementById('which_keypress').value == 'r'){
			processFootballProcedures('CHECK_FOR_PLAYER_DATA');
		}
		break;
	case 'select_existing_football_matches':
		if(whichInput.value.toLowerCase().includes('new_match')) {
			initialiseForm('SETUP',null);
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processFootballProcedures('LOAD_SETUP',$('#select_existing_football_matches option:selected'));
		}
		break;
	case 'log_undo_btn':
		if(match_data.events.length > 0) {
			if($('#number_of_undo_txt').val() > match_data.events.length) {
				if(confirm('Number of undo [' + $('#number_of_undo_txt').val() + '] is bigger than number of events [' 
						+ match_data.events.length + ']. We will make both of them similiar') == false) {
					return false;
				}
				$('#number_of_undo_txt').val(match_data.events.length);
			}
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processFootballProcedures('UNDO',$('#number_of_undo_txt'));
		} else {
			alert('No events found');
		}
		break;
	case 'log_replace_btn':
		processFootballProcedures('REPLACE',match_data);
		break;
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'back_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.json','') + '.json';
		}
		break;
	case 'save_match_btn': case 'reset_match_btn':
		switch ($(whichInput).attr('name')) {
		case 'reset_match_btn':
	    	if (confirm('The setup selections of this match will be retained ' +
	    			'but the match data will be deleted permanently. Are you sure, you want to RESET this match?') == false) {
	    		return false;
	    	}
			break;
		}
		if (!checkEmpty(document.getElementById('matchFileName'),'Match Name')) {
			return false;
		} 
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
			return false;
		}
		for(var tm=1;tm<=2;tm++) {
			for(var i=1;i<11;i++) {
				for(var j=i+1;j<=11;j++) {
					if(tm == 1) {
						if(document.getElementById('homePlayer_' + i).selectedIndex == document.getElementById('homePlayer_' + j).selectedIndex) {
							alert(document.getElementById('homePlayer_' + i).options[
								document.getElementById('homePlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for HOME team');
							return false;
						}
					} else {
						if(document.getElementById('awayPlayer_' + i).selectedIndex == document.getElementById('awayPlayer_' + j).selectedIndex) {
							alert(document.getElementById('awayPlayer_' + i).options[
								document.getElementById('awayPlayer_' + i).selectedIndex].text.toUpperCase() + 
								' selected multiple times for AWAY team');
							return false;
						}
					}
				}
			}
		}
		switch ($(whichInput).attr('name')) {
		case 'save_match_btn': 
			uploadFormDataToSessionObjects('SAVE_MATCH');
			break;
		case 'reset_match_btn':
			processWaitingButtonSpinner('START_WAIT_TIMER');
			uploadFormDataToSessionObjects('RESET_MATCH');
			break;
		}
		break;
	case 'load_default_team_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		if($('#homeTeamId option:selected').val() == $('#awayTeamId option:selected').val()) {
			alert('Both teams cannot be same. Please choose different home and away team');
    		processWaitingButtonSpinner('END_WAIT_TIMER');
			return false;
		}
		processFootballProcedures('LOAD_TEAMS',whichInput);
		document.getElementById('save_match_div').style.display = '';
		break;
	case 'setup_match_btn':
		document.football_form.method = 'post';
		document.football_form.action = 'setup';
	   	document.football_form.submit();
	   	processWaitingButtonSpinner('START_WAIT_TIMER');
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processFootballProcedures('LOAD_MATCH',$('#select_football_matches option:selected'));
		break;
	case 'log_event_btn':
		if(whichInput.id.toLowerCase() == 'undo') {
    		if(match_data == null || match_data.events.length <= 0) {
    			alert('No events found to perform undoes');
    			return false;
    		}
    		addItemsToList('LOAD_UNDO',match_data);
		} else if(whichInput.id.toLowerCase() == 'replace'){
			addItemsToList('LOAD_REPLACE',match_data);
			addItemsToList('POPULATE-OFF_PLAYER',match_data);
			addItemsToList('POPULATE-ON_PLAYER',match_data);
		} else if(whichInput.id.toLowerCase() == 'penalty'){
			processFootballProcedures('RESET_PENALTY', null);
			addItemsToList('LOAD_PENALTY',match_data);
		}else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processFootballProcedures('LOG_EVENT',whichInput);
		}
		break;
	case 'Home_goal_btn':
		processFootballProcedures('LOG_EVENT',whichInput);
		break;	
	case 'cancel_undo_btn': case 'cancel_overwrite_btn': case 'cancel_event_btn': case 'cancel_replace_btn': case 'cancel_penalty_btn':
		document.getElementById('select_event_div').style.display = 'none';
		addItemsToList('LOAD_EVENTS',match_data); 
		processWaitingButtonSpinner('END_WAIT_TIMER');
		break;
	case 'select_teams':
		addItemsToList('POPULATE-OFF_PLAYER',match_data);
		addItemsToList('POPULATE-ON_PLAYER',match_data);
		break;
	case 'change_on':
		processFootballProcedures('ANIMATE-CHANGE_ON');
		break;
	case 'change_on_formation':
		processFootballProcedures('ANIMATE-CHANGE_ON_FORMATION');
		break;
	case 'change_on_formation_without_image':
		processFootballProcedures('ANIMATE-CHANGE_ON_FORMATION_WITHOUT_IMAGE');
		break;	
	case 'populate_namesuper_btn': case 'populate_namesuper_player_btn': case 'populate_playingxi_btn': case 'populate_api_btn': case 'populate_bug_db_btn': case 'populate_namesuper_card_btn': 
	case 'populate_staff_btn': case 'populate_match_promo_btn':case 'populate_sponsor_btn': case 'populate_substitution_btn': case 'populate_formation_btn': case 'populate_scorebug_card_btn':
	case 'populate_scorebug_subs_btn': case 'populate_single_substitution_btn': case 'populate_playingxi_changeon_btn': case 'populate_points_table_btn': case 'populate_homesub_btn': 
	case 'populate_Away_btn': case 'populate_awaysub_btn': case 'populate_sub_btn': case 'populate_heatmap_btn': case 'populate_Top_Stats_btn': case 'populate_fixtures_btn':
	case 'populate_subchange_on_btn': case 'populate_double_promo_btn': case 'populate_ltmatch_promo_btn': case 'populate_scorebug_match_promo_btn': case 'populate_points_table2_btn':
	case 'populate_result_promo_btn': case 'populate_teamfixture_btn': case 'populate_lof_line_up_btn': case 'populate_leaderBoard_btn': case 'populate_avg_formation_btn':
	case 'populate_vertical_flipper_btn': case 'populate_change_on_btn': case 'populate_attackingZone_btn': case 'populate_player_touch_map_btn': case 'populate_chettri_btn':
	case 'populate_chettri_change_on_btn': case 'populate_fixture_results_btn': case 'populate_touch_btn': case 'populate_lt_player_stats_btn': case 'populate_mini_points_table_btn':
	
	case 'populateLBand':case'populate_Insights_btn':case'populate_Insights_gs_btn':
	case 'populate_Team_db_btn':case'populate_Insights_team_btn': case 'populate_ExtraData_db_btn': case 'populate_free_t_btn': case 'populate_sp_btn': case 'populate_sp_bottom_btn':
	case 'populate_Insights_result_btn':case "populate_ScoreLine_btn":
	case 'populate_playingXI_btn': case 'populate_match_data_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		switch ($(whichInput).attr('name')) {	
		case 'populate_playingXI_btn':
			processFootballProcedures('POPULATE-PLAYING_XI');
			break;
		case'populate_attackingZone_btn':
			processFootballProcedures('POPULATE-ATTACKING_ZONE');
			break;
		case 'populate_match_data_btn':
			processFootballProcedures('POPULATE-MATCH_DATA');
			break;
		
		case 'populate_subchange_on_btn':
			processFootballProcedures('ANIMATE-SUB_CHANGE_ON');
			break;
		case 'populate_Away_btn':
			processFootballProcedures('POPULATE-AWAYXI');
			break;
		case 'populate_awaysub_btn':
			processFootballProcedures('POPULATE-AWAYSUB');
			break;
		case 'populate_homesub_btn':
			processFootballProcedures('POPULATE-HOMESUB');
			break;
		case 'populate_sub_btn':
			processFootballProcedures('POPULATE-SUBS_CHANGE_ON');
			break;
		case 'populate_change_on_btn':
			processFootballProcedures('POPULATE-VERTICAL_CHANGE_ON');
			break;
		case 'populate_chettri_change_on_btn':
			 processFootballProcedures('POPULATE-CHETTRI_CHANGE_ON');
			break;
		case 'populate_heatmap_btn':
			processFootballProcedures('POPULATE-L3-HEATMAP');
			break;
		case 'populate_Top_Stats_btn':
			processFootballProcedures('POPULATE-L3-TOP_STATS');
			break;
		case 'populate_teamfixture_btn':
			processFootballProcedures('POPULATE-L3-TEAMFIXTURE');
			break;
		case 'populate_namesuper_btn':
			processFootballProcedures('POPULATE-L3-NAMESUPER');
			break;
		case 'populate_leaderBoard_btn':
			processFootballProcedures('POPULATE-LOF-LEADERBOARD');
			break;
		case 'populate_namesuper_player_btn':
			processFootballProcedures('POPULATE-L3-NAMESUPER-PLAYER');
			break;
		case 'populate_player_touch_map_btn':
			processFootballProcedures('POPULATE-FF-PLAYER_TOUCH_MAP');
			break;
		case 'populate_Insights_btn':
			processFootballProcedures('POPULATE-INSIGHTS');
			break;
		case "populate_ScoreLine_btn":
			processFootballProcedures('POPULATE-L3-SCOREUPDATE');
			break;
		case 'populate_free_t_btn':
			processFootballProcedures('POPULATE-FT');
			break;
		case'populate_Insights_gs_btn':
			processFootballProcedures('POPULATE-INSIGHTS_GS');
			break;
		case'populate_Insights_team_btn':
			processFootballProcedures('POPULATE-INSIGHTS_TEAM');
			break;
		case 'populate_Insights_result_btn':
			processFootballProcedures('POPULATE-INSIGHTS_RESULT');
			break;
		case 'populate_playingxi_btn':
			processFootballProcedures('POPULATE-FF-PLAYINGXI');
			break;
		case 'populate_playingxi_changeon_btn':
			processFootballProcedures('POPULATE-FF-PLAYINGXI_CHANGEON');
			break;
		case 'populate_lof_line_up_btn':
			processFootballProcedures('POPULATE-LOF-LINEUP');
			break;
		case 'populate_avg_formation_btn':
			processFootballProcedures('POPULATE-LOF-AVG_FORMATION');
			break;
		case 'populate_fixture_results_btn':
			processFootballProcedures('POPULATE-FF-FIXTUREANDRESULT');
			break;
		case 'populate_touch_btn':
			processFootballProcedures('POPULATE-FF-TEAM_TOUCH');
			break;
		case 'populate_vertical_flipper_btn':
			processFootballProcedures('POPULATE-LOF-VERTICAL_FLIPPER');
			break;
			
		case 'populate_chettri_btn':
			processFootballProcedures('POPULATE-FF-CHETTRI');
			break;
			
		case 'populateLBand':
			processFootballProcedures('POPULATE-L-BAND');
			processWaitingButtonSpinner('END_WAIT_TIMER');
			break;

		case 'populate_bug_db_btn':
			processFootballProcedures('POPULATE-L3-BUG-DB');
			break;
		case 'populate_Team_db_btn':
			processFootballProcedures('POPULATE-DB_TEAM');
			break;
		
		case 'populate_ExtraData_db_btn':
			processFootballProcedures('POPULATE-DB_EXTRA_DATA');
			break;
		
		case 'populate_namesuper_card_btn':
			processFootballProcedures('POPULATE-L3-NAMESUPER-CARD');
			break;
		case 'populate_staff_btn':
			processFootballProcedures('POPULATE-L3-STAFF');
			break;
		case 'populate_scorebug_match_promo_btn':
			processFootballProcedures('POPULATE-SCOREBUG-PROMO');
			break;
		case 'populate_ltmatch_promo_btn':
			processFootballProcedures('POPULATE-LT-PROMO');
			break;
		case 'populate_result_promo_btn':
			processFootballProcedures('POPULATE-LT-RESULT');
			break;
		case 'populate_match_promo_btn':
			processFootballProcedures('POPULATE-FF-PROMO');
			break;
		case 'populate_lt_player_stats_btn':
			processFootballProcedures('POPULATE-LT-PLAYER_STATS');
			break;
		case 'populate_sponsor_btn':
			processFootballProcedures('POPULATE-HERO-SPONSOR');
			break;
		case 'populate_substitution_btn':
			processFootballProcedures('POPULATE-L3-SUBSTITUTE');
			break;
		case 'populate_single_substitution_btn':
			processFootballProcedures('POPULATE-L3-SINGLE_SUBSTITUTE');
			break;
		case 'populate_scorebug_subs_btn':
			processFootballProcedures('POPULATE-SCOREBUG-SUBS');
			break;
		case 'populate_formation_btn':
			processFootballProcedures('POPULATE-FF-FORMATION');
			break;
		case 'populate_scorebug_card_btn':
			processFootballProcedures('POPULATE-SCOREBUG-CARD');
			break;
		case 'populate_points_table_btn':
			processFootballProcedures('POPULATE-POINTS_TABLE');
			break;
		case 'populate_mini_points_table_btn':
			processFootballProcedures('POPULATE-MINI_POINTS_TABLE');
			break;
		case 'populate_points_table2_btn':
			processFootballProcedures('POPULATE-POINTS_TABLE2');
			break;
		case 'populate_double_promo_btn':
			processFootballProcedures('POPULATE-DOUBLE_PROMO');
			break;
		case 'populate_fixtures_btn':
			processFootballProcedures('POPULATE-FIXTURES');
			break;
		case 'populate_sp_btn':
			processFootballProcedures('POPULATE-SPONSOR_EURO');
			break;
		case 'populate_sp_bottom_btn':
			processFootballProcedures('POPULATE-SPONSOR_BOTTOM');
			break;	
		}
		break;
	case 'populate_stats_api_btn':
		processFootballProcedures('POPULATE-SCOREBUG_STATS_API');
		break;
	case 'populate_stats_btn':
		processFootballProcedures('POPULATE-SCOREBUG_STATS');
		break;
	case 'populate_stats_two_btn':
		processFootballProcedures('POPULATE-SCOREBUG_STATS_TWO');
		break;
	case 'populate_extra_time_btn':
		processFootballProcedures('POPULATE-EXTRA_TIME');
		break;
	case 'populate_red_card_btn':
		processFootballProcedures('POPULATE-RED_CARD');
		break;
	case 'populate_extra_time_both_btn':
		processFootballProcedures('POPULATE-EXTRA_TIME_BOTH');
		break;
	case 'populate_team_stats_btn':
		processFootballProcedures('POPULATE-SCOREBUG_TEAM_STATS');
		break;
	case 'populate_h2h_btn':
		processFootballProcedures('POPULATE-HEADTOHEAD');
		break;
	case 'populate_player_stats_btn':
		processFootballProcedures('POPULATE-SCOREBUG_PLAYER_STATS');
		break;
	default:
		switch ($(whichInput).attr('id')) {
		case 'overwrite_teams_total': case 'overwrite_match_time': 
			addItemsToList('LOAD_' + $(whichInput).attr('id').toUpperCase(),null);
			document.getElementById('select_event_div').style.display = '';
			break;
		default:
			if($(whichInput).attr('id').includes('_btn') && $(whichInput).attr('id').split('_').length >= 4) {
	    		switch ($(whichInput).attr('id').split('_')[1]) {
	    		case 'increment':
	    			$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
						parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) + 1
					);
					break;
	    		case 'decrement':
					if(parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
						+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) > 0) {
		    			
						$('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val(
							parseInt($('#' + $(whichInput).attr('id').split('_')[0] + '_' + $(whichInput).attr('id').split('_')[2] 
							+ '_' + $(whichInput).attr('id').split('_')[3] + '_txt').val()) - 1
						);
						
					}
					break;
				}				
				processWaitingButtonSpinner('START_WAIT_TIMER');
				processFootballProcedures('LOG_STAT',whichInput);
			}
			break;
		}
		break;
	}
}
function processFootballProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	
	switch(whatToProcess) {
	case 'GET-CONFIG-DATA':
		value_to_process = $('#select_configuration_file option:selected').val();
		break;
	case 'READ-MATCH-AND-POPULATE':
		value_to_process = graphics;
		if ((graphics !== preGraphic)) {
		    clearInterval(matchTimeInterval); // Clear the existing interval
		    matchTimeInterval = setInterval(function() {
		        processFootballProcedures('READ-MATCH-AND-POPULATE', graphics);
		    }, 5000);
		    preGraphic = graphics;
		}
		break;
	case 'CHECK_FOR_PLAYER_DATA':
		value_to_process = $('#selectPlayer').val();
		break;
	case 'READ_CLOCK':
		valueToProcess = $('#matchFileTimeStamp').val();
		break;
	case 'CANCLE-OPTION':
		value_to_process = 'cancle';
		alert(value_to_process)
		break;	
	case 'LOG_STAT':
		value_to_process = whichInput.id;
		break;
	case 'LOG_OVERWRITE_TEAM_SCORE': case 'LOG_OVERWRITE_MATCH_STATS': case 'LOG_OVERWRITE_MATCH_SUBS': 
		switch (whatToProcess) {
		case 'LOG_OVERWRITE_TEAM_SCORE':
			value_to_process = $('#overwrite_home_team_score').val() + ',' + $('#overwrite_away_team_score').val();
			break;
		case 'LOG_OVERWRITE_MATCH_STATS':
			value_to_process = $('#overwrite_match_stats_index option:selected').val() 
				+ ',' + $('#overwrite_match_stats_player_id option:selected').val()+ ',' + $('#overwrite_match_stats_type option:selected').val()
				+ ',' + $('#overwrite_match_stats_total_seconds').val();
			break;
		case 'LOG_OVERWRITE_MATCH_SUBS':
			value_to_process = $('#overwrite_match_sub_index option:selected').val().split(",")[0]+ ',' + $('#overwrite_match_player_id option:selected').val()
				+ ',' + $('#overwrite_match_subs_player_id option:selected').val();
			break;
		}
		break;
		
	case 'LOAD_TEAMS':
		value_to_process = $('#homeTeamId option:selected').val() + ',' + $('#awayTeamId option:selected').val();
		break;

	case 'LOAD_MATCH': case 'LOAD_SETUP':
		value_to_process = whichInput.val();
		break;
		
	case 'LOG_EVENT':
		value_to_process =  whichInput.id + ',' + $('#selected_player_id').val();
		break;
	
	case 'UNDO':
		value_to_process = $('#number_of_undo_txt').val();
		break;
	case 'REPLACE':
		value_to_process = $('#select_player option:selected').val() + ',' + $('#select_sub_player option:selected').val()
			+ ',' + $('#select_teams option:selected').val();
		break;
	case 'POPULATE-L3-HEATMAP':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectHeatmappeakdistance option:selected').val() 
					+ ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectHeatmappeakdistance option:selected').val() 
					+ ',' + $('#selectPlayer option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L-BAND':
		value_to_process = $('#selectEvent option:selected').val();
		if($('#selectEvent option:last').val()){
		}
		$('#selectEvent option:selected').next().prop('selected', true);
		break;

	case 'POPULATE-L3-TOP_STATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectTopStats option:selected').val();
				break;	
			case 'SUPER_CUP':
				value_to_process = '/Default/Lof_LeaderBoard' + ',' + $('#selectTopStats option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-L3-TEAMFIXTURE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_GoalScorer.sum' + ',' + $('#selectNameSuper option:selected').val() ;
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/TeamFixture' + ',' + $('#selectTeamFixture option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-NAMESUPER':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_GoalScorer.sum' + ',' + $('#selectNameSuper option:selected').val() ;
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectNameSuper option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectNameSuper option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-LOF-LEADERBOARD':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/Lof_LeaderBoard' + ',' + $('#selectLeaderBoard option:selected').val() + ',' + 
					$('#selectLeaderBoardPlayer option:selected').val() + ',' + $('#selectPlayerPhoto option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-PENALTY':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Penalty.sum';
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Penalty.sum';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/Penalty';
				break;
		}
		break;
	case 'POPULATE-L3-STAFF':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_HeadCoach.sum' + ',' + $('#selectStaff option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectStaff option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectStaff option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-SCOREBUG-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-LT-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-LT-RESULT':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-LT-PLAYER_STATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/LT_PlayerStats' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-FF-PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val();
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchId.sum' + ',' + $('#selectMatchPromo option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectMatchPromo option:selected').val();
				break;
		}
		break;
	case 'POPULATE-INSIGHTS':case'POPULATE-INSIGHTS_GS':case'POPULATE-INSIGHTS_TEAM': case 'POPULATE-FT':
	case 'POPULATE-INSIGHTS_RESULT':
		value_to_process = $('#selectNameSuper option:selected').val();
	break;
	case 'POPULATE-FF-PLAYER_TOUCH_MAP':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-NAMESUPER-PLAYER':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_GoalScorer.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					//$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectCaptainGoalKeeper option:selected').val() + ',' + 
					$('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectCaptainGoalKeeper option:selected').val() + ',' + 
					$('#selectPlayer option:selected').val() ;
				break;	
		}
		break;
	case 'POPULATE-L3-NAMESUPER-CARD':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_NameSuper_Cards.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
					$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_NameSuper_Cards.sum' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' + 
				$('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-SCOREBUG-CARD':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
				value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectCaptainGoalKeeper option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-L3-ASTON-ADS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' ;
			//alert(value_to_process);
			break;
		}
		break;
	case 'POPULATE-LT-BUG_REPLAY':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug_Replay.sum' ;
			//alert(value_to_process);
			break;
		case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug_Replay.sum';
			break;	
		}
		break;
	case 'POPULATE-L3-MATCHPROMO':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Promo.sum' ;
			break;
		}
		break;
	case 'POPULATE-PLAYOFF_TREE':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/Playoff_Tree';
				break;
		}
		break;
	case 'POPULATE-QUAIFIERS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/Qualifier_winners';
				break;
		}
		break;
	case 'POPULATE-FF-TEAMS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-FF-HEADTOHEAD':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE_HIGHLIGHT_SCORE_BUG':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/HighlightScoreBug';
			break;
		}
	break;
	case 'POPULATE-FF-MATCHID': case 'POPULATE-FF_SCORE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchId.sum' ;
			//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/MatchID.sum';
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/GameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-LT-MATCHID':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LtGameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT';
				break;
		}
		break;
	case 'POPULATE-HIGHLIGHT_SCOREBUG':
		switch ($('#selectedBroadcaster').val()) {
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LtGameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/HighlightScoreBug';
				break;
		}
		break;	
	case 'POPULATE-FF-MATCHSTATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Score_Goalers.sum' ;
			break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Score_Goalers.sum';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-DOUBLE_PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Promo.sum' ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectDoublePromo option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-HIGHLIGHT':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
				break;
		}
		break;
	case 'POPULATE-ROAD-TO-FINAL':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-PLAYOFFS':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum' ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-FIXTURES':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectFixtures option:selected').val() + ',' + $('#selectFixturesHeader option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-SPONSOR_EURO':
		switch ($('#selectedBroadcaster').val()) {
			case 'EURO_LEAGUE':
				value_to_process = $('#selectSponsor option:selected').val();
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-SPONSOR_BOTTOM':
		switch ($('#selectedBroadcaster').val()) {
			case 'EURO_LEAGUE':
				value_to_process = $('#selectSponsor option:selected').val();
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-MINI_POINTS_TABLE':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/Mini_PointsTable' + ',' + $('#selectWhichGroup option:selected').val() + ',' + $('#selectLeagueTable option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-POINTS_TABLE2':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectLeagueTable option:selected').val() ;
				break;
		}
		break;
	case 'POPULATE-POINTS_TABLE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/PointsTable.sum' ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/PointsTable.sum' + ',' + $('#selectLeagueTable option:selected').val();
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectLeagueTable option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-L3-SUBSTITUTE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Subsitutes_Multi.sum' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Substitutes_Multi.sum' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val()
					+ ',' + $('#selectStatsType option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT' + ',' + $('#selectTeam option:selected').val() + ',' 
				+ $('#selectStatsType option:selected').val()+","+$('#selectTeamids option:selected').val() ;
				//alert(value_to_process);
				break;	
		}
		break;
	case 'POPULATE-L3-SINGLE_SUBSTITUTE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Subsitutes.sum' + ',' + $('#selectSingleSubTeam option:selected').val() ;
				//alert(value_to_process);
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Substitutes.sum' + ',' + $('#selectSingleSubTeam option:selected').val() ;
				//alert(value_to_process);
				break;
		}
		break;
	case 'POPULATE-FF-FIXTUREANDRESULT':
		switch ($('#selectedBroadcaster').val()){
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectFixtures option:selected').val() + ',' + $('#selectResultTeams option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-FF-TEAM_TOUCH':
		switch ($('#selectedBroadcaster').val()){
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectTeams option:selected').val() + ',' + 
					$('#selectTouch option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-LOF-LINEUP': case 'POPULATE-LOF-AVG_FORMATION':
		switch ($('#selectedBroadcaster').val()){
			case 'SUPER_CUP':
				value_to_process = '/Default/Lof_LineUp' + ',' + $('#selectLineUp option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-LOF-VERTICAL_FLIPPER':
		switch ($('#selectedBroadcaster').val()){
			case 'SUPER_CUP':
				value_to_process = '/Default/VerticalFlipper' + ',' + $('#selectVerticalFlipper option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-FF-CHETTRI':
		switch ($('#selectedBroadcaster').val()){
			case 'SUPER_CUP':
				value_to_process = '/Default/'+ $('#selectType option:selected').val() + ',' + $('#selectType option:selected').val();
				break;	
		}
		break;
		
	case 'POPULATE-FF-PLAYINGXI':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/TeamLineUp_Subs.sum' + ',' + $('#selectPlayingXI option:selected').val();
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/TeamLineUp_Subs.sum' + ',' + $('#selectPlayingXI option:selected').val();
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LineUp' + ',' + $('#selectPlayingXI option:selected').val();
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectPlayingXI option:selected').val() + ',' + $('#selectPlayingXIType option:selected').val();
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames' + ',' + $('#selectPlayingXI option:selected').val() + ',' + $('#selectPlayingXIType option:selected').val();
				break;	
		}
		break;
	case 'POPULATE-FF-PLAYINGXI_CHANGEON':
		switch ($('#selectedBroadcaster').val()) {
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/TeamLineUp_Subs_Change.sum' + ',' + $('#selectPlayingXI option:selected').val();
				break;
		}
		break;
	case 'POPULATE-L3-BUG-DB':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = $('#bugdbScene').val() + ',' + $('#selectBugdb option:selected').val() ;
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = $('#bugdbScene').val() + ',' + $('#selectBugdb option:selected').val() ;
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/2LineBug' + ',' + $('#selectBugdb option:selected').val() ;
				break;
			case 'EURO_LEAGUE':
				value_to_process = $('#selectBugdb option:selected').val();
				break;		
		}
		break;
	case 'POPULATE-DB_TEAM':
		switch ($('#selectedBroadcaster').val()) {
			case 'EURO_LEAGUE':
				value_to_process = '/Default/Lband' + ',' + $('#selectBugdb option:selected').val();
				graphics = value_to_process + ',' + whatToProcess;
				break;
		}
		break;
	
	case 'POPULATE-DB_EXTRA_DATA':
		value_to_process = $('#selectData option:selected').val();
		break;
	
	case 'POPULATE-L3-SCOREUPDATE':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_ScoreUpdate.sum';
				break;
			case 'SANTOSH_TROPHY':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_ScoreUpdate.sum';
				break;
			case 'VIZ_SANTOSH_TROPHY':
				value_to_process = '/Default/LtGameIntro';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/LT';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/LT'+','+$('#selectNameSuper option:selected').val();
				break;
		}
		break;
	case 'POPULATE-L3-MATCHSTATUS': case 'POPULATE-FF-TOURNAMENT_STATS': case 'POPULATE-FF-TEAM_COMPARISON':
		switch ($('#selectedBroadcaster').val()) {
			case 'I_LEAGUE':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/MatchStats.sum';
				break;
			case 'VIZ_TRI_NATION':
				value_to_process = '/Default/FullFrames';
				break;
			case 'SUPER_CUP':
				value_to_process = '/Default/FullFrames';
				break;
		}
		break;
	case 'POPULATE-SCOREBUG':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/ScoreBug.sum';
			//value_to_process = match_data.matchFileName + ',' + 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/ScoreBug.sum';
			break;
		case 'SANTOSH_TROPHY':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/ScoreBug.sum';
			break;
		}
		break;
	case 'POPULATE-SCOREBUG_STATS_API':
		switch ($('#selectedBroadcaster').val()) {
		case 'SUPER_CUP':
			value_to_process = $('#selectScorebugstats option:selected').val();
			break;
		}
		break
	case 'POPULATE-SCOREBUG_STATS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = $('#selectScorebugstats option:selected').val() ;
			break;
		case 'VIZ_TRI_NATION':
			value_to_process = $('#selectScorebugstats option:selected').val() + ',' + $('#selecthomedata').val() + ',' + $('#selectawaydata').val() ;
			break;
		case 'SUPER_CUP':
			value_to_process = $('#selectScorebugstats option:selected').val() + ',' + $('#selecthomedata').val() + ',' + $('#selectawaydata').val() ;
			break;
		}
		break
	case 'POPULATE-SCOREBUG_STATS_TWO':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = $('#selectScorebugstatstwo option:selected').val() ;
			break;
		}
		break
	
	case 'POPULATE-SCOREBUG_TEAM_STATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = $('#selectTeamStats option:selected').val();
				break;
			}
			break
	case 'POPULATE-HEADTOHEAD':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = $('#selectStatsType option:selected').val();
				break;
			}
			break
			
	case 'POPULATE-SCOREBUG_PLAYER_STATS':
		switch ($('#selectedBroadcaster').val()) {
			case 'SUPER_CUP':
				value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val() +
					',' + $('#selectPlayerPhoto option:selected').val() + ',' + $('#selectPlayer option:selected').val() ;
				break;
			}
			break
			
	case 'POPULATE-EXTRA_TIME':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = $('#selectExtratime').val();
			break;
		}
		break;
	case 'POPULATE-RED_CARD':
		switch ($('#selectedBroadcaster').val()) {
		case 'VIZ_TRI_NATION':
			value_to_process = $('#selecthometeamredcard').val() + ',' + $('#selectawayteamredcard').val();
			break;
		case 'SUPER_CUP':
			value_to_process = $('#selecthometeamredcard').val() + ',' + $('#selectawayteamredcard').val();
			break;
		}
		break;
	case 'POPULATE-EXTRA_TIME_BOTH':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = $('#selectExtratimeBoth').val();
			break;
		}
		break;
	case 'POPULATE-HERO-SPONSOR':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' + ',' + $('#selectSponsor option:selected').val();
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Aston_AD.sum' + ',' + 'DESTINI';
			break;
		}
		break;
	case 'POPULATE-FF-FORMATION':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = formationScene + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		case 'SANTOSH_TROPHY':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = formationScene + ',' + $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		}
		break;
	case 'POPULATE-SCOREBUG-SUBS':
		switch ($('#selectedBroadcaster').val()) {
		case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = $('#selectTeam option:selected').val() + ','+ 
				$('#selectTeamids option:selected').map(function() {return this.value;}).get().join(':');
			break;	
			case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY':
			value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		}
		break;
	case 'ANIMATE-CHANGE_ON_FORMATION':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			//value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation.sum' + ',' + $('#selectTeam option:selected').val();
			value_to_process = $('#selectTeam option:selected').val() + ',' + $('#selectStatsType option:selected').val();
			break;
		}
		break;
	case 'POPULATE-DOUBLE_SUBS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Subs_BothTeam.sum';
			break;
		}
		break;
	case'POPULATE-CLEARANCE':case'POPULATE-SHOOTING_ACCURACY': case 'TOTAL_FINAL_THIRD_PASSES':case'POPULATE-POSS_WON_ATT_3RD':
	case'POPULATE-WON_CORNERS':case 'POPULATE-TOTAL_FINAL_THIRD_PASSES': case 'POPULATE-DUEL_WON':case'POPULATE-WON_CONTEST': 
	case'POPULATE-EXPECTED_GOALS':case 'POPULATE-SET_PIECES': case 'POPULATE-WIN_H2H':case'POPULATE-H2H':case'POPULATE-H2H_LIVE_WIN': 
	case 'POPULATE-EXA_EXG': case'POPULATE-LWP': case 'POPULATE-PLAYER_RATING':
	case 'POPULATE-PLAYING_XI':
		switch ($('#selectedBroadcaster').val()) {
		case 'EURO_LEAGUE':
			value_to_process = '/Default/Lband' + ',' + $('#selectStats option:selected').val() + ',' + $('#whichSponsor option:selected').val() ;
			graphics = value_to_process + ',' + whatToProcess;
			break;
		}
		break;
	case "POPULATE-FF-PLAYER-POINTER":
		value_to_process = '/Default/PlayerPointer';
	break;
	case "POPULATE-FF-PLAYER-PROFILE":
	value_to_process = '/Default/PlayerProfile';
	break;
	case'POPULATE-POINTS_TABLE_LB':
		switch ($('#selectedBroadcaster').val()) {
			case 'EURO_LEAGUE':
					value_to_process = '/Default/Lband' ;
					graphics = value_to_process + ',' + whatToProcess;
					break;
			}
		break;
	case 'POPULATE-MATCH_DATA':
		switch ($('#selectedBroadcaster').val()) {
		case 'EURO_LEAGUE':
			value_to_process = '/Default/Lband' + ',' + $('#selectStats option:selected').val() + ',' + $('#selectData option:selected').val();
			graphics = value_to_process + ',' + whatToProcess;
			break;
		}
		break;
	case 'POPULATE-ATTACKING_ZONE':
		switch ($('#selectedBroadcaster').val()) {
		case'SUPER_CUP':
			value_to_process = '/Default/FullFrames' + ',' + $('#selectStats option:selected').val();
			graphics = value_to_process + ',' + whatToProcess;
			break;
		}
		break;
	case 'POPULATE-OFFICIALS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/LT_Officials.sum';
			break;
		case 'SANTOSH_TROPHY':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/LT_Officials.sum';
			break;
		case 'VIZ_TRI_NATION':
			value_to_process = '/Default/LT';
			break;
		case 'SUPER_CUP':
			value_to_process = '/Default/LT';
			break;
		}
		break;	
	case "TOP_STATS-OPTIONS_DATA":
		value_to_process = $('#selectTopStats option:selected').val();
		break;
	case 'HOME_GOAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = 'HOME_GOAL';
			break;
		}
		break;
	case 'AWAY_GOAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = 'AWAY_GOAL';
			break;
		}
		break;
	case 'HOME_UNDO': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'VIZ_TRI_NATION':
			value_to_process = 'HOME_UNDO';
			break;
		}
		break;
	case 'AWAY_UNDO': 
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':
			value_to_process = 'AWAY_UNDO';
			break;
		}
		break;	
	}
	if(whatToProcess != 'LOAD_TEAMS'){
		if(match_data){
			value_to_process = match_data.matchFileName + ',' + value_to_process;
		}
	}

	$.ajax({    
        type : 'Get',     
        url : 'processFootballProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
			//alert(whatToProcess);
        	switch(whatToProcess) {
			case 'GET-CONFIG-DATA':
				initialiseForm('UPDATE-CONFIG',data);
				break;
			case 'READ_CLOCK':
				if(match_data.clock) {
					if(document.getElementById('match_time_hdr')) {
						document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
							millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
					}
					if(document.getElementById('match_games_hdr')){
						document.getElementById('match_games_hdr').innerHTML = 'GAMES : ' + match_data.clock.matchTimeStatus;
					}
					if(document.getElementById('match_modified_hdr')) {
						document.getElementById('match_modified_hdr').innerHTML = 
						'<b>SportVUStatistic File Last Modified Time</b> : ' + match_data.xmlTimeSpan
					}
				}
				
				if(data){
						if($('#matchFileTimeStamp').val() != data.matchFileTimeStamp) {
							document.getElementById('matchFileTimeStamp').value = data.matchFileTimeStamp;
							//session_match = data;
							//addItemsToList('LOAD_MATCH',data);
							//addItemsToList('LOAD_EVENTS',data);
							//document.getElementById('select_event_div').style.display = 'none';
						}
					
				}
				break;
			case 'POPULATE-SPONSOR_EURO':
				processFootballProcedures('ANIMATE-SPONSOR_EURO');
				break;
			case 'POPULATE-SPONSOR_BOTTOM':
				processFootballProcedures('ANIMATE-SPONSOR_BOTTOM');
				break;	
				
			case 'CHECK_FOR_PLAYER_DATA':
				
				$('#check_data_div').empty();
				
				table = document.createElement('table');
				table.setAttribute('class', 'table table-bordered');
				tbody = document.createElement('tbody');
	
				table.appendChild(tbody);
				document.getElementById('check_data_div').appendChild(table);
	
				row = tbody.insertRow(tbody.rows.length);
				for (var j = 0; j <data.length; j++) {
				    text = document.createElement('label'); 
				    text.innerHTML = data[j];
				    row.insertCell(j).appendChild(text);
				}
				
				document.getElementById('check_data_div').style.display = '';
				break;	
			case 'POPULATE-VERTICAL_CHANGE_ON':
				if(data.status == 'END'){
					alert('FORMATION END HERE');
				}
				break;
			/**************  SCORE BUG   **************/		
			case "POPULATE-SCOREBUG_STATS":	case "POPULATE-SCOREBUG_STATS_API":	case "POPULATE-SCOREBUG-PROMO": case "POPULATE-HEADTOHEAD":
			case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON": case "POPULATE-SCOREBUG_PLAYER_STATS": case "POPULATE-SCOREBUG-CARD":
			case "POPULATE-SCOREBUG_TEAM_STATS": case "POPULATE-RED_CARD":case "POPULATE_HIGHLIGHT_SCORE_BUG":case "POPULATE-SCOREBUG_STATS_TWO":
				if (confirm('Animate In?') == true) {
			        switch (whatToProcess) {
			            case "POPULATE-SCOREBUG_STATS": case "POPULATE-SCOREBUG_STATS_API":case "POPULATE-SCOREBUG_STATS_TWO":
			                processFootballProcedures('ANIMATE-SCOREBUG_STATS');
			                break;
			            case "POPULATE-SCOREBUG-PROMO":
			                processFootballProcedures('ANIMATE-SCOREBUG-PROMO');
			                break;
			            case "POPULATE-HEADTOHEAD":
			                processFootballProcedures('ANIMATE-SCOREBUG-HEADTOHEAD');
			                break;
			            case "POPULATE-SCOREBUG-SUBS":
			                processFootballProcedures('ANIMATE-SCOREBUG-SUBS');
			                break;
			            case "POPULATE-SUBS_CHANGE_ON":
			                processFootballProcedures('ANIMATE-IN-SUBS_CHANGE_ON');
			                break;
			            case "POPULATE-SCOREBUG_PLAYER_STATS":
			                processFootballProcedures('ANIMATE-SCOREBUG_PLAYER_STATS');
			                break;
			            case "POPULATE-SCOREBUG-CARD":
			                processFootballProcedures('ANIMATE-SCOREBUG-CARD');
			                break;
			            case "POPULATE-SCOREBUG_TEAM_STATS":
			                processFootballProcedures('ANIMATE-SCOREBUG_TEAM_STATS');
			                break;
			            case "POPULATE-RED_CARD":
			                processFootballProcedures('ANIMATE-RED_CARD');
			                break;
			           case "POPULATE_HIGHLIGHT_SCORE_BUG":
							processFootballProcedures('ANIMATE-IN-HIGHLIGHT_SCORE_BUG');
							break;
			        }
			    } else {
					processFootballProcedures('CANCEL_GFX');
			        processUserSelection($('#cancel_graphics_btn').attr('value', 'cancel_graphics_btn'));
			    }
				break;
			case 'POPULATE-FF-MATCHID': case 'POPULATE-FF-PLAYINGXI': case 'POPULATE-L3-MATCHSTATUS': case 'POPULATE-L3-MATCHPROMO': case 'POPULATE-FF-MATCHSTATS':
			case 'POPULATE-FF-PROMO': case 'POPULATE-DOUBLE_PROMO': case 'POPULATE-HERO-SPONSOR': case 'POPULATE-POINTS_TABLE_LB': case 'POPULATE-FF-FORMATION':
			case 'POPULATE-DOUBLE_SUBS': case 'POPULATE-OFFICIALS': case 'POPULATE-HIGHLIGHT': case 'POPULATE-PENALTY': case 'POPULATE-FF-PLAYINGXI_CHANGEON':
			case 'POPULATE-LT-MATCHID': case 'POPULATE-HOMESUB': case 'POPULATE-AWAYXI': case 'POPULATE-AWAYSUB': case 'POPULATE-FF-TEAMS': case 'POPULATE-FIXTURES':
			case 'POPULATE-QUAIFIERS': case 'POPULATE-LT-PROMO': case 'POPULATE-POINTS_TABLE2': case 'POPULATE-PLAYOFFS': case 'POPULATE-LT-RESULT': case 'POPULATE-ROAD-TO-FINAL':
			case 'POPULATE-HIGHLIGHT_SCOREBUG':case 'POPULATE-FOUL':case'POPULATE-LWP': case'POPULATE-H2H_LIVE_WIN':
			case 'TOTAL_FINAL_THIRD_PASSES':case'POPULATE-POSS_WON_ATT_3RD':case'POPULATE-WON_CORNERS':case 'POPULATE-TOTAL_FINAL_THIRD_PASSES':
			case 'POPULATE-DUEL_WON':case'POPULATE-WON_CONTEST':case'POPULATE-EXPECTED_GOALS':case'POPULATE-CLEARANCE':case'POPULATE-SHOOTING_ACCURACY':
			case 'POPULATE-SET_PIECES':case 'POPULATE-WIN_H2H':case'POPULATE-H2H': case 'POPULATE-EXA_EXG': case 'POPULATE-PLAYER_RATING': case 'POPULATE-LOF-LINEUP':
			case 'POPULATE-LOF-VERTICAL_FLIPPER': case 'POPULATE-PLAYING_XI': case 'POPULATE-MATCH_DATA':case'POPULATE-ATTACKING_ZONE': case 'POPULATE-FF-TOURNAMENT_STATS': 
			case 'POPULATE-FF-TEAM_COMPARISON': case 'POPULATE-FF-CHETTRI': case 'POPULATE-FF-HEADTOHEAD': case 'POPULATE-FF-FIXTUREANDRESULT': case 'POPULATE-FF-TEAM_TOUCH':
			case 'POPULATE-LOF-AVG_FORMATION': case "POPULATE-FF-PLAYER-POINTER":case "POPULATE-FF-PLAYER-PROFILE":
			case 'POPULATE-LT-PLAYER_STATS':case'POPULATE-POINTS_TABLE': case 'POPULATE-PLAYOFF_TREE': case 'POPULATE-FF_SCORE': case 'POPULATE-MINI_POINTS_TABLE':
				if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-HIGHLIGHT_SCOREBUG':
						processFootballProcedures('ANIMATE-IN-HIGHLIGHT_SCOREBUG');	
						break;	
					case'POPULATE-POINTS_TABLE_LB':
						$('#select_graphic_options_div').empty();
						$("#select_graphic_options_div").html("");
						$("#football_div").show();
						processFootballProcedures('ANIMATE-POINTS_TABLE_LB');	
						break;
					case'POPULATE-ATTACKING_ZONE':
						processFootballProcedures('ANIMATE-ATTACKING_ZONE');	
						break;
					case 'POPULATE-ROAD-TO-FINAL':
						processFootballProcedures('ANIMATE-IN-ROAD-TO-FINAL');			
						break;
					case 'POPULATE-PLAYOFFS':
						processFootballProcedures('ANIMATE-IN-PLAYOFFS');			
						break;
					case 'POPULATE-SET_PIECES':
						processFootballProcedures('ANIMATE-SET_PIECES');			
						break
					case 'POPULATE-HOMESUB':
						processFootballProcedures('ANIMATE-IN-HOMESUB');			
						break;
					case'POPULATE-CLEARANCE':
						processFootballProcedures('ANIMATE-CLEARANCE');
						break;
					case'POPULATE-SHOOTING_ACCURACY':
						processFootballProcedures('ANIMATE-SHOOTING_ACCURACY');
						break;
					case 'POPULATE-PLAYER_RATING':
						processFootballProcedures('ANIMATE-PLAYER_RATING');
						break;
					case'POPULATE-H2H_LIVE_WIN':
						processFootballProcedures('ANIMATE-H2H_LIVE_WIN');
						break;
					case 'POPULATE-AWAYXI':
						processFootballProcedures('ANIMATE-IN-AWAYXI');			
						break;
					case 'POPULATE-FOUL':
						processFootballProcedures('ANIMATE-FOUL');
						pre_data = 'POPULATE-FOUL';	
						break;
					case'POPULATE-LWP':
						processFootballProcedures('ANIMATE-LWP');	
						break;
					case 'POPULATE-WIN_H2H':
						processFootballProcedures('ANIMATE-WIN_H2H');	
						break;
					case'POPULATE-H2H':
						processFootballProcedures('ANIMATE-H2H');	
						break;
					case 'POPULATE-TOTAL_FINAL_THIRD_PASSES':
						processFootballProcedures('ANIMATE-TOTAL_FINAL_THIRD_PASSES');	
						break; 
					case 'POPULATE-POSS_WON_ATT_3RD':
						processFootballProcedures('ANIMATE-POSS_WON_ATT_3RD');	
						break;
					case "POPULATE-FF-PLAYER-POINTER":
						processFootballProcedures('ANIMATE-FF-PLAYER-POINTER');	
						break;
					case "POPULATE-FF-PLAYER-PROFILE":
						processFootballProcedures('ANIMATE-FF-PLAYER-PROFILE');	
						break;
					case  'POPULATE-WON_CORNERS':
						processFootballProcedures('ANIMATE-WON_CORNERS');	
						break; 
					case 'POPULATE-DUEL_WON':
						processFootballProcedures('ANIMATE-DUEL_WON');	
						break;
					case 'POPULATE-WON_CONTEST':
						processFootballProcedures('ANIMATE-WON_CONTEST');	
						break;
					case 'POPULATE-PLAYING_XI':
						$('#select_graphic_options_div').empty();
						$("#select_graphic_options_div").html("");
						$("#football_div").show();
						processFootballProcedures('ANIMATE-PLAYING_XI');	
						break;
					case 'POPULATE-MATCH_DATA':
						$('#select_graphic_options_div').empty();
						$("#select_graphic_options_div").html("");
						$("#football_div").show();
						processFootballProcedures('ANIMATE-MATCH_DATA');	
						break;
					case 'POPULATE-EXA_EXG':
						processFootballProcedures('ANIMATE-EXA_EXG');
						break;
					case'POPULATE-EXPECTED_GOALS':
						processFootballProcedures('ANIMATE-EXPECTED_GOALS');
						break;
					case 'POPULATE-AWAYSUB':
						processFootballProcedures('ANIMATE-IN-AWAYSUB');			
						break;
					case 'POPULATE-PENALTY':
						processFootballProcedures('ANIMATE-IN-PENALTY');			
						break;
					case 'POPULATE-HIGHLIGHT':
						processFootballProcedures('ANIMATE-IN-HIGHLIGHT');			
						break;
					case 'POPULATE-FF-TEAMS':
						processFootballProcedures('ANIMATE-IN-FF_TEAMS');			
						break;
					case 'POPULATE-FF-MATCHID':
						processFootballProcedures('ANIMATE-IN-MATCHID');			
						break;
					case 'POPULATE-FF_SCORE':
						processFootballProcedures('ANIMATE-IN-FF_SCORE');			
						break;
					case 'POPULATE-FF-HEADTOHEAD':
						processFootballProcedures('ANIMATE-IN-FF_HEADTOHEAD');			
						break;
					case 'POPULATE-LT-MATCHID':
						processFootballProcedures('ANIMATE-IN-LT_MATCHID');			
						break;
					case 'POPULATE-FF-MATCHSTATS':
						processFootballProcedures('ANIMATE-IN-MATCHSTATS');	
						break;
					case 'POPULATE-DOUBLE_PROMO':
						processFootballProcedures('ANIMATE-IN-DOUBLE_PROMO');
						break;

					case 'POPULATE-LOF-LINEUP':
						processFootballProcedures('ANIMATE-IN-LOF_LINEUP');		
						break;
					case 'POPULATE-LOF-AVG_FORMATION':
						processFootballProcedures('ANIMATE-IN-AVG_FORMATION');		
						break;
					case 'POPULATE-LOF-VERTICAL_FLIPPER':
						processFootballProcedures('ANIMATE-IN-VERTICAL_FLIPPER');		
						break;
					
					case 'POPULATE-FF-CHETTRI':
						processFootballProcedures('ANIMATE-IN-CHETTRI');		
						break;
					case 'POPULATE-FF-FIXTUREANDRESULT':
						processFootballProcedures('ANIMATE-IN-FIXTUREANDRESULT');		
						break;
					case 'POPULATE-FF-TEAM_TOUCH':
						processFootballProcedures('ANIMATE-IN-TEAM_TOUCH');		
						break;
						
					case 'POPULATE-FF-PLAYINGXI':
						processFootballProcedures('ANIMATE-IN-PLAYINGXI');		
						break;
					case 'POPULATE-FF-PLAYINGXI_CHANGEON':
						processFootballProcedures('ANIMATE-IN-PLAYINGXI_CHANGEON');		
						break;
					case 'POPULATE-L3-MATCHSTATUS':
						processFootballProcedures('ANIMATE-IN-MATCHSTATUS');
						break;
					case 'POPULATE-FF-TOURNAMENT_STATS':
						processFootballProcedures('ANIMATE-IN-TOURNAMENT_STATS');
						break;
					case 'POPULATE-FF-TEAM_COMPARISON':
						processFootballProcedures('ANIMATE-IN-TEAM_COMPARISON');
						break;
					case 'POPULATE-L3-MATCHPROMO':
						processFootballProcedures('ANIMATE-IN-MATCHPROMO');
						break;
					case 'POPULATE-LT-PROMO':
						processFootballProcedures('ANIMATE-IN-LTPROMO');
						break;
					case 'POPULATE-LT-RESULT':
						processFootballProcedures('ANIMATE-IN-RESULT');
						break;
					case 'POPULATE-FF-PROMO':
						processFootballProcedures('ANIMATE-IN-PROMO');
						break;
					case 'POPULATE-LT-PLAYER_STATS':
						processFootballProcedures('ANIMATE-IN-LT_PLAYER_STATS');
						break;
					case 'POPULATE-HERO-SPONSOR':
						processFootballProcedures('ANIMATE-IN-ASTON-ADS');
						break;
					case 'POPULATE-POINTS_TABLE':
						processFootballProcedures('ANIMATE-IN-POINTS_TABLE');
						break;
					case 'POPULATE-POINTS_TABLE2':
						processFootballProcedures('ANIMATE-IN-POINTS_TABLE2');
						break;
					case 'POPULATE-MINI_POINTS_TABLE':
						processFootballProcedures('ANIMATE-IN-MINI_POINTS_TABLE');
						break;
					case 'POPULATE-FIXTURES':
						processFootballProcedures('ANIMATE-IN-FIXTURES');
						break;
					case 'POPULATE-QUAIFIERS':
						processFootballProcedures('ANIMATE-IN-QUAIFIERS');
						break;
					case 'POPULATE-PLAYOFF_TREE':
						processFootballProcedures('ANIMATE-IN-PLAYOFF_TREE');
						break;
					case 'POPULATE-FF-FORMATION':
						processFootballProcedures('ANIMATE-IN-FORMATION');
						break;
					case 'POPULATE-DOUBLE_SUBS':
						processFootballProcedures('ANIMATE-IN-DOUBLE_SUBS');
						break;
					case 'POPULATE-OFFICIALS':
						processFootballProcedures('ANIMATE-IN-OFFICIALS');
						break;
					}
				}else{
					processUserSelection($('#cancel_graphics_btn').attr('value','cancel_graphics_btn'));
				}
				break;
			case 'POPULATE-L3-HEATMAP':
				if(data.api_photo =='SUCCESS'){
					if(confirm('Animate In?') == true){
						switch(whatToProcess){
							case 'POPULATE-L3-HEATMAP':
							processFootballProcedures('ANIMATE-IN-HEATMAP');				
							break;
						}
					}
				}else{
					alert('file does not exist!')
				}
				break;
				
			case 'POPULATE-SCOREBUG': case 'POPULATE-L3-NAMESUPER': case 'POPULATE-L3-NAMESUPER-PLAYER':  case 'POPULATE-L3-TOP_STATS':
			case 'POPULATE-L3-BUG-DB': case 'POPULATE-L3-SCOREUPDATE':  case 'POPULATE-L3-NAMESUPER-CARD': case 'POPULATE-L3-TEAMFIXTURE':
			case 'POPULATE-L3-SUBSTITUTE':  case 'POPULATE-L3-STAFF':  case 'POPULATE-LT-BUG_REPLAY': case 'POPULATE-L3-SINGLE_SUBSTITUTE':
			case'POPULATE-INSIGHTS':case'POPULATE-INSIGHTS_GS':case 'POPULATE-DB_TEAM':case'POPULATE-INSIGHTS_TEAM': case 'POPULATE-DB_EXTRA_DATA':
			case 'POPULATE-FT': case 'POPULATE-INSIGHTS_RESULT': case 'POPULATE-LOF-LEADERBOARD': case 'POPULATE-FF-PLAYER_TOUCH_MAP':
				if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-L3-TEAMFIXTURE':
						processFootballProcedures('ANIMATE-IN-TEAMFIXTURE');				
						break;
					case 'POPULATE-SCOREBUG':
						processFootballProcedures('ANIMATE-IN-SCOREBUG');				
						break;
					case 'POPULATE-L3-TOP_STATS':
						processFootballProcedures('ANIMATE-IN-TOP_STATS');				
						break;
					case 'POPULATE-LOF-LEADERBOARD':
						processFootballProcedures('ANIMATE-IN-LOF_LEADERBOARD');				
						break;
					case 'POPULATE-L3-NAMESUPER':
						processFootballProcedures('ANIMATE-IN-NAMESUPERDB');				
						break;
					case 'POPULATE-L3-NAMESUPER-PLAYER':
						processFootballProcedures('ANIMATE-IN-NAMESUPER');				
						break;
					case 'POPULATE-FF-PLAYER_TOUCH_MAP':
						processFootballProcedures('ANIMATE-IN-PLAYER_TOUCH_MAP');				
						break;
					case 'POPULATE-INSIGHTS':
						processFootballProcedures('ANIMATE-INSIGHTS');	
						break;
					case 'POPULATE-FT':	
						processFootballProcedures('ANIMATE-FT');	
						break;
					case'POPULATE-INSIGHTS_GS':
						processFootballProcedures('ANIMATE-INSIGHTS_GS');	
						break;
					case'POPULATE-INSIGHTS_TEAM':
						processFootballProcedures('ANIMATE-INSIGHTS_TEAM');	
						break;
					case 'POPULATE-INSIGHTS_RESULT':
						processFootballProcedures('ANIMATE-INSIGHTS_RESULT');	
						break;
					case 'POPULATE-L3-NAMESUPER-CARD':
						processFootballProcedures('ANIMATE-IN-NAMESUPER_CARD');				
						break;
					case 'POPULATE-L3-BUG-DB':
						processFootballProcedures('ANIMATE-IN-BUG-DB');
						break;
					case 'POPULATE-DB_TEAM':
						$('#select_graphic_options_div').empty();
						$("#select_graphic_options_div").html("");
						$("#football_div").show();
						processFootballProcedures('ANIMATE-DB_TEAM');
						break;
					case 'POPULATE-DB_EXTRA_DATA':
						processFootballProcedures('ANIMATE-DB_EXTRA_DATA');
						break;
					case 'POPULATE-L3-SCOREUPDATE':
						processFootballProcedures('ANIMATE-IN-SCOREUPDATE');
						break;
					case 'POPULATE-LT-BUG_REPLAY':
						processFootballProcedures('ANIMATE-IN-BUG_REPLAY');
						break;
					case 'POPULATE-L3-SUBSTITUTE':
						processFootballProcedures('ANIMATE-IN-SUBSTITUTE');
						break;
					case 'POPULATE-L3-SINGLE_SUBSTITUTE':
						processFootballProcedures('ANIMATE-IN-SINGLE_SUBSTITUTE');
						break;
					case 'POPULATE-L3-STAFF':
						processFootballProcedures('ANIMATE-IN-STAFF');
						break;
					}
				}
				break;
			case 'TEAMFIXTURE_GRAPHICS-OPTIONS':
				addItemsToList('TEAMFIXTURE-OPTIONS',data);
				break;
			case 'SCOREBUG_GRAPHICS-OPTIONS':
				addItemsToList('SCOREBUG_OPTION',data);
			break;
			case "SCOREBUG_API_GRAPHICS-OPTIONS":
				addItemsToList('SCOREBUG_API_OPTION',data);
				dataset(data);
			break;
			case 'HEATMAP-OPTIONS':
				addItemsToList('PLAYER_CAREER-OPTIONS',null);
				addItemsToList('POPULATE-PLAYER_CAREER',data);
				break;	
			case 'NAMESUPER_GRAPHICS-OPTIONS':
				addItemsToList('NAMESUPER-OPTIONS',data);
				break;
			case 'LEADERBOARD_GRAPHICS-OPTIONS':
				leaderboard = data;
				addItemsToList('LEADERBOARD-OPTIONS',data);
				addItemsToList('POPULATE-LEADERBOARD_PLAYER',data);
				break;
			case "TOP_STATS-OPTIONS_DATA":
				dataset(data);
				break;
			case 'MATCH_INSIGHTS-OPTIONS':
				addItemsToList('INSIGHTS-OPTIONS',data);
				break;
			case 'FT-OPTIONS':
				addItemsToList('FREE_T-OPTIONS',data);
				break;
			case 'EXTRA_DATA_GS-OPTIONS':
				addItemsToList('EXTRA_DATA-OPTIONS',data);
				break;
				
			case 'MATCH_INSIGHTS_GS-OPTIONS':
				addItemsToList('INSIGHTS_GS-OPTIONS',data);
				break;
			case 'MATCH_INSIGHTS_TEAM-OPTIONS':
				addItemsToList('INSIGHTS_TEAM-OPTIONS',data);
				break;
			case 'MATCH_INSIGHTS_RESULT-OPTIONS':
				addItemsToList('MATCH_INSIGHTS_RESULT',data);
				break;
			case 'EVENT-OPTIONS':
				addItemsToList('L-BAND-EVENT-OPTIONS', data);
				break;
			case 'STAFF_GRAPHICS-OPTIONS':
				//alert(home_team);
				addItemsToList('STAFF-OPTIONS',data);
				break;
			case 'RESULT_PROMO_GRAPHICS-OPTIONS':
				addItemsToList('RESULT_PROMO-OPTIONS',data);
				break;
			case 'PROMO_GRAPHICS-OPTIONS':
				addItemsToList('MATCH-PROMO-OPTIONS',data);
				break;
			case 'LT_PLAYER_STATS_GRAPHICS-OPTIONS':
				addItemsToList('LT_PLAYER_STATS-OPTIONS',data);
				break;
			case 'SCOREBUGPROMO_GRAPHICS-OPTIONS':
				addItemsToList('SCOREBUGPROMO-OPTIONS',data);
				break;
			case 'LTPROMO_GRAPHICS-OPTIONS':
				addItemsToList('LT_MATCH-PROMO-OPTIONS',data);
				break;	
			case 'BUG_DB_GRAPHICS-OPTIONS':
				addItemsToList('BUG_DB-OPTIONS',data);
				switch ($('#selectedBroadcaster').val()) {
				case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_TRI_NATION':
					addItemsToList('POPULATE-BUG-SCENE',data);
					break;
				}
				break;
		case'DB_GRAPHICS':
			addItemsToList('BUG_-OPTIONS',data);
			break;
			
			case 'APIDATA_GRAPHICS-OPTIONS':
				addItemsToList('APIDATA-OPTIONS',data);				
				break;				
    		case 'LOG_OVERWRITE_TEAM_SCORE': case 'LOG_OVERWRITE_MATCH_STATS': case 'LOG_OVERWRITE_MATCH_SUBS': 
    		case 'UNDO': case 'REPLACE': case 'HOME_UNDO': case 'AWAY_UNDO':
        		addItemsToList('LOAD_MATCH',data);
				addItemsToList('LOAD_EVENTS',data);
				document.getElementById('select_event_div').style.display = 'none';
        		break;
        	case 'LOAD_TEAMS':
        		addItemsToList('LOAD_TEAMS',data);
        		break;
        	case 'HOME_GOAL': case 'AWAY_GOAL':
        		addItemsToList('LOAD_MATCH',data);
        		break;	
			case 'LOG_EVENT': case 'LOAD_MATCH':
				
        		addItemsToList('LOAD_MATCH',data);
	        	switch(whatToProcess) {
	        	case 'LOAD_MATCH':
					document.getElementById('football_div').style.display = '';
					document.getElementById('select_event_div').style.display = 'none';
					setInterval(displayMatchTime, 500);
					break;
				}
        		break;
        	case 'LOAD_SETUP':
        		initialiseForm('SETUP',data);
        		break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var max_cols,div,linkDiv,anchor,row,cell,header_text,select,option,tr,th,thead,text,table,tbody,playerName,api_value_home,api_value_away;
	var cellCount = 0;
	var addSelect = false;
	
	switch (whatToProcess) {
		case 'POPULATE-PLAYER_CAREER' :
		
		$('#selectPlayerName').empty();
		if(match_data.liveData.lineUp[0].contestantId == $('#selectTeams option:selected').val()){
			match_data.liveData.lineUp.forEach(function(lu,index,arr){
				if(lu.contestantId == $('#selectTeams option:selected').val()){
					lu.player.forEach(function(pl,index,arr){
						$('#selectPlayerName').append(
							$(document.createElement('option')).prop({
							value: pl.playerId,
							text: pl.firstName
						}))
					});
				}
			});
		}
		else if(match_data.liveData.lineUp[1].contestantId == $('#selectTeams option:selected').val()){
			match_data.liveData.lineUp.forEach(function(lu,index,arr){
				if(lu.contestantId == $('#selectTeams option:selected').val()){
					lu.player.forEach(function(pl,index,arr){
						$('#selectPlayerName').append(
							$(document.createElement('option')).prop({
							value: pl.playerId,
							text: pl.firstName
						}))
					});
				}
			});
		}
		break;
	case "L3-SCOREUPDATE":
		$('#football_div').hide();
		$('#select_graphic_options_div').empty();
	
		header_text = document.createElement('h6');
		header_text.innerHTML = 'Select Graphic Options';
		document.getElementById('select_graphic_options_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
	
		table.appendChild(tbody);
		document.getElementById('select_graphic_options_div').appendChild(table);
		
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.style = 'width:130px';
		select.id = 'selectNameSuper';
		select.name = select.id;
		
		["With_Header","Without_Header"].forEach(function(ns){
			option = document.createElement('option'); // ✅ FIXED: scoped inside loop
			option.value = ns;
			option.text = ns.replace("_"," ");
			select.appendChild(option);
		});
		
		row.insertCell(cellCount).appendChild(select);
		cellCount = cellCount + 1;
	
		 div = document.createElement('div'); // ✅ use let here
	
		let optionBtn1 = document.createElement('input'); // ✅ fixed reuse of 'option'
		optionBtn1.type = 'button';
		optionBtn1.name = 'populate_ScoreLine_btn';
		optionBtn1.value = 'Populate';
		optionBtn1.id = optionBtn1.name;
		optionBtn1.setAttribute('onclick',"processUserSelection(this)");
		div.append(optionBtn1);
	
		let optionBtn2 = document.createElement('input');
		optionBtn2.type = 'button';
		optionBtn2.name = 'cancel_graphics_btn';
		optionBtn2.id = optionBtn2.name;
		optionBtn2.value = 'Cancel';
		optionBtn2.setAttribute('onclick','processUserSelection(this)');
		div.append(optionBtn2);
		
		row.insertCell(cellCount++).appendChild(div);
		document.getElementById('select_graphic_options_div').style.display = '';
	break;

		case'INSIGHTS-OPTIONS': case'INSIGHTS_GS-OPTIONS':case'INSIGHTS_TEAM-OPTIONS': case 'FREE_T-OPTIONS':
		case 'MATCH_INSIGHTS_RESULT':
			$('#football_div').hide();
			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectNameSuper';
					select.name = select.id;
					
					dataToProcess.insight.forEach(function(ns,index,arr1){
						option = document.createElement('option');
						option.value = ns.text;
						option.text = ns.text ;
						select.appendChild(option);
					});
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
			switch (whatToProcess) {
				case'INSIGHTS-OPTIONS': 
					option = document.createElement('input');
			   	 	option.type = 'button';	
					option.name = 'populate_Insights_btn';
				    option.value = 'Populate Insights';
				    option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
					break;
				case 'FREE_T-OPTIONS':
					option = document.createElement('input');
			   	 	option.type = 'button';	
					option.name = 'populate_free_t_btn';
				    option.value = 'Populate Insights';
				    option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
					break;
				case'INSIGHTS_GS-OPTIONS':
					option = document.createElement('input');
			   	 	option.type = 'button';	
					option.name = 'populate_Insights_gs_btn';
				    option.value = 'Populate Insights';
				    option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				break;
				case 'INSIGHTS_TEAM-OPTIONS':
					option = document.createElement('input');
			   	 	option.type = 'button';	
					option.name = 'populate_Insights_team_btn';
				    option.value = 'Populate Insights';
				    option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
					break;
				case 'MATCH_INSIGHTS_RESULT':
					option = document.createElement('input');
			   	 	option.type = 'button';	
					option.name = 'populate_Insights_result_btn';
				    option.value = 'Populate Insights';
				    option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
					break;
				}
				
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
		break;
	
	case 'L-BAND-EVENT-OPTIONS':
		$('#football_div').hide();
		$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
	
			select = document.createElement('select');
			select.id = 'selectEvent';
			select.name = select.id;
			let count = 1;
			dataToProcess.liveData.event.forEach((data, index, arr)=>{
				option = document.createElement('option');
				option.value = data.id;
				option.text = count+' - '+data.id;	
				select.appendChild(option);
				count++;
			})
			option.setAttribute('onchange','processUserSelection(this)');
		
			row.insertCell(0).appendChild(select);
			
			option = document.createElement('input');
    		option.type = 'button';
    		
    		option.name = 'populateLBand';
    		option.value = 'Populate L-Band';
    		
    		option.id = option.name;
		    option.setAttribute('onclick',"processUserSelection(this)");
		    
		    div = document.createElement('div');
		    div.append(option);
		    
		    row.insertCell(1).appendChild(div);
		    
			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_graphics_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');
	
		    div.append(option);
		    
		    row.insertCell(2).appendChild(div);
			document.getElementById('select_graphic_options_div').style.display = '';
		break;

	case 'POPULATE-PLAYER':
		$('#selectPlayer').empty();
		if(match_data.homeTeamId ==  $('#selectTeam option:selected').val()){
			match_data.homeSquad.forEach(function(hs,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
			match_data.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: hsub.playerId,
					text: hsub.jersey_number + ' - ' + hsub.full_name + ' (SUB)'
				}))
			});
		}
		else {
			match_data.awaySquad.forEach(function(as,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
			match_data.awaySubstitutes.forEach(function(asub,index,arr){
				$('#selectPlayer').append(
					$(document.createElement('option')).prop({
					value: asub.playerId,
					text: asub.jersey_number + ' - ' + asub.full_name + ' (SUB)'
				}))
			});
		}
		
		break;
	case 'POPULATE-LEADERBOARD_PLAYER':
		$('#selectLeaderBoardPlayer').empty();
		dataToProcess.forEach(function(lb,index,arr1){
			if(lb.leaderboardId ==  $('#selectLeaderBoard option:selected').val()){
				$('#selectLeaderBoardPlayer').append(
					$(document.createElement('option')).prop({
	                value: 1,
	                text: lb.player1.jersey_number + ' - ' + lb.player1.full_name
		        }))
		        
		        $('#selectLeaderBoardPlayer').append(
					$(document.createElement('option')).prop({
	                value: 2,
	                text: lb.player2.jersey_number + ' - ' + lb.player2.full_name
		        }))
		        
		        $('#selectLeaderBoardPlayer').append(
					$(document.createElement('option')).prop({
	                value: 3,
	                text: lb.player3.jersey_number + ' - ' + lb.player3.full_name
		        }))
		        
		        $('#selectLeaderBoardPlayer').append(
					$(document.createElement('option')).prop({
	                value: 4,
	                text: lb.player4.jersey_number + ' - ' + lb.player4.full_name
		        }))
		        
		        $('#selectLeaderBoardPlayer').append(
					$(document.createElement('option')).prop({
	                value: 5,
	                text: lb.player5.jersey_number + ' - ' + lb.player5.full_name
		        }))
			}
		});
		
		/*option = document.createElement('option');
		option.value = ;
		option.text = lb.header;
		select.appendChild(option);*/
		break;
	
	case 'POPULATE-BUG-SCENE':
	
		$('#bugdbScene').empty();
		dataToProcess.forEach(function(bug,index,arr1){
			switch ($('#selectedBroadcaster').val().toUpperCase()) {
			case 'I_LEAGUE':
				if(bug.bugId == $('#selectBugdb option:selected').val()){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Bug.sum';
					}
				}
				break;
			case 'SANTOSH_TROPHY':
				if(bug.bugId == $('#selectBugdb option:selected').val()){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Bug.sum';
					}
				}
				break;	
			}
			
		});
		break;
	case 'PLAYER_CAREER-OPTIONS':
		switch ($('#selectedBroadcaster').val().toUpperCase()) {
			case 'EURO_LEAGUE':
				
				$('#select_graphic_options_div').empty();
	
				header_text = document.createElement('h6');
				header_text.innerHTML = 'Select Graphic Options';
				document.getElementById('select_graphic_options_div').appendChild(header_text);
				
				table = document.createElement('table');
				table.setAttribute('class', 'table table-bordered');
						
				tbody = document.createElement('tbody');
		
				table.appendChild(tbody);
				document.getElementById('select_graphic_options_div').appendChild(table);
				
				row = tbody.insertRow(tbody.rows.length);
				
				select = document.createElement('select');
				select.id = 'selectTeams';
				select.name = select.id;
				option = document.createElement('option');
				option.value = match_data.matchInfo.contestant[0].id;
				option.text = match_data.matchInfo.contestant[0].name;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.matchInfo.contestant[1].id;
				option.text = match_data.matchInfo.contestant[1].name;
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.id = 'selectPlayerName';
				select.name = select.id;
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				option = document.createElement('input');
			    option.type = 'button';
	    		
	    		option.name = 'populate_heatmap_btn';
	    		option.value = 'Populate HeatMap';
	    		
	    		option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			    
			    row.insertCell(1).appendChild(div);
			    
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(2).appendChild(div);
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
		}
		break;
	case 'NAMESUPER-OPTIONS': case "NAMESUPER_PLAYER-OPTIONS": case'PLAYINGXI-OPTIONS': case 'API-OPTIONS':case 'BUG_DB-OPTIONS': case 'NAMESUPER-CARD-OPTIONS': 
	case 'STAFF-OPTIONS': case 'MATCH-PROMO-OPTIONS':case 'AD-OPTIONS': case 'SUBSTITUTE-OPTIONS': case 'FORMATION-OPTIONS': case 'SCOREBUG-CARD-OPTIONS':
	case 'SCOREBUG-SUBSTITUTION-OPTIONS': case 'SINGLE_SUBSTITUTE-OPTIONS': case 'HEATMAP_PEAKDISTACE-OPTION': case 'LT_MATCH-PROMO-OPTIONS': case 'SCOREBUGPROMO-OPTIONS':
	case 'TOP_STATS-OPTIONS': case 'FIXTURES-OPTIONS': case 'POINT_TABLE-OPTIONS': case 'DOUBLE_PROMO-OPTIONS': case 'POINTS_TABLE2_OPTION': case 'RESULT_PROMO-OPTIONS':
	case 'TEAMFIXTURE-OPTIONS': case'BUG_-OPTIONS': case 'EXTRA_DATA-OPTIONS': case 'SPONSOR-OPTIONS': case 'SPONSOR_BOTTOM-OPTIONS': case 'LT_LINE_UP-OPTIONS':
	case 'VERTICAL_FLIPPER-OPTIONS': case 'LEADERBOARD-OPTIONS': case 'PLAYER_TOUCH_MAP-OPTIONS': case 'CHETTRI-OPTIONS': case 'FIXTURES_RESULTD-OPTIONS':
	case 'TEAM_TOUCH-OPTION': case 'LT_AVG_FORMATION-OPTIONS': case 'LT_PLAYER_STATS-OPTIONS': case 'MINI_POINTS_TABLE_OPTION':
		switch ($('#selectedBroadcaster').val().toUpperCase()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':case 'EURO_LEAGUE':

			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
			switch(whatToProcess){
				case 'SPONSOR_BOTTOM-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectSponsor';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'Shriram';
					option.text = 'Shriram';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'EatFit';
					option.text = 'EatFit';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Fancode';
					option.text = 'Fancode';
					select.appendChild(option);
					
					row.insertCell(0).appendChild(select);
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_sp_bottom_btn';
		    		option.value = 'Populate Sponsor';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(1).appendChild(div);
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(2).appendChild(div);
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'SPONSOR-OPTIONS':
					select = document.createElement('select');
							select.id = 'selectSponsor';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'Shriram';
							option.text = 'Shriram';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'EatFit';
							option.text = 'EatFit';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'Fancode';
							option.text = 'Fancode';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_sp_btn';
				    		option.value = 'Populate Sponsor';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
					
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
				case 'DOUBLE_PROMO-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP':
							select = document.createElement('select');
							select.id = 'selectDoublePromo';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'TODAY';
							option.text = 'TODAY';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'TOMORROW';
							option.text = 'TOMORROW';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'DAY_AFTER';
							option.text = 'DAY AFTER';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_double_promo_btn';
				    		option.value = 'Populate Double Promo';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
					
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
						}
					break;
				case 'FIXTURES-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP':
						select = document.createElement('select');
						select.id = 'selectFixtures';
						select.name = select.id;
						
						/*option = document.createElement('option');
						option.value = 'ROUND_16';
						option.text = 'Round Of 16';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'QF';
						option.text = 'Quarter Finals';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'SF';
						option.text = 'Semi Finals';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'END';
						option.text = 'Last 7 Matches';
						select.appendChild(option);*/
						
						option = document.createElement('option');
						option.value = 'group A';
						option.text = 'Group A';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'group B';
						option.text = 'Group B';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'group C';
						option.text = 'Group C';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'group D';
						option.text = 'Group D';
						select.appendChild(option);
						
						row.insertCell(0).appendChild(select);
						
						select = document.createElement('select');
						select.id = 'selectFixturesHeader';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'fixture';
						option.text = 'Fixture';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'result';
						option.text = 'Result';
						select.appendChild(option);
						
						row.insertCell(1).appendChild(select);
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_fixtures_btn';
			    		option.value = 'Populate Fixtures';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(2).appendChild(div);
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(3).appendChild(div);
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					}
						
					break;
				case 'POINTS_TABLE2_OPTION':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP':
							select = document.createElement('select');
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'SemiFinal1';
							option.text = 'Semi Final 1';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'SemiFinal2';
							option.text = 'Semi Final 2';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_points_table2_btn';
				    		option.value = 'Populate Points-Table';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
					
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
					}
					break;
				case 'MINI_POINTS_TABLE_OPTION':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
					case 'SUPER_CUP':
						select = document.createElement('select');
						select.id = 'selectWhichGroup';
						select.name = select.id;
						
						['A', 'B', 'C', 'D'].forEach(group => {
						  option = document.createElement('option');
						  option.value = `LeagueTable${group}`;
						  option.text = `Group ${group} League Table`;
						  select.appendChild(option);
						});
						
						row.insertCell(0).appendChild(select);
						
						select = document.createElement('select');
						select.id = 'selectLeagueTable';
						select.name = select.id;
						
						['AS IT STAND', 'MINI POINTS TABLE'].forEach(table => {
						  option = document.createElement('option');
						  option.value = table;
						  option.text = table;
						  select.appendChild(option);
						});
						
						row.insertCell(1).appendChild(select);
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_mini_points_table_btn';
			    		option.value = 'Populate Mini Points-Table';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(2).appendChild(div);
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					}
					break;
				case 'POINT_TABLE-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()){
						case 'SUPER_CUP':
							select = document.createElement('select');
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'LeagueTableA';
							option.text = 'Group A League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableB';
							option.text = 'Group B League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableC';
							option.text = 'Group C League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableD';
							option.text = 'Group D League Table';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							break;
						case 'SANTOSH_TROPHY': 
							select = document.createElement('select');
				
							select.id = 'selectLeagueTable';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'LeagueTableA';
							option.text = 'Group A League Table';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'LeagueTableB';
							option.text = 'Group B League Table';
							select.appendChild(option);
							
							row.insertCell(0).appendChild(select);
							break;
					}
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_points_table_btn';
		    		option.value = 'Populate Points-Table';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(1).appendChild(div);
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(2).appendChild(div);
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'SCOREBUG-SUBSTITUTION-OPTIONS':					
					switch ($('#selectedBroadcaster').val().toUpperCase()){
					case 'VIZ_TRI_NATION': case 'SUPER_CUP':
						let selectTeam = document.createElement('select');
						selectTeam.id = 'selectTeam';
						selectTeam.name = selectTeam.id;
						
						option = document.createElement('option');
						option.value = match_data.homeTeamId;
						option.text = match_data.homeTeam.teamName1;
						selectTeam.appendChild(option);
						
						option = document.createElement('option');
						option.value = match_data.awayTeamId;
						option.text = match_data.awayTeam.teamName1;
						selectTeam.appendChild(option);
						
						row.insertCell(cellCount).appendChild(selectTeam);
						cellCount = cellCount + 1;
						let selection = document.createElement('select');
						selection.id = 'selectTeamids';
						selection.name = selection.id;
						selection.style.width = 'auto';
    					selection.style.height = 'auto';
						selection.multiple = true; 
						
						selectTeam.addEventListener('change', function() {
			            selection.innerHTML = '';  
			            if (parseInt(this.value, 10) === match_data.homeTeamId) {
							match_data.events.reverse();
			                match_data.events.forEach(function(ev) {
						    if (ev.eventType == 'replace') {
						        let offPlayerName = getPlayerNameById(ev.offPlayerId, match_data.homeSquad, match_data.homeSubstitutes);
                				let onPlayerName = getPlayerNameById(ev.onPlayerId, match_data.homeSquad, match_data.homeSubstitutes);

						        if (offPlayerName && onPlayerName) {
						            let option = document.createElement('option');
						            option.value = ev.offPlayerId + "-" + ev.onPlayerId;
						            option.text = "(OFF) " + offPlayerName + " (ON) " + onPlayerName+"  ";
						            selection.appendChild(option);
						        }
						    }
						});
			               
			            } else {
			                match_data.events.reverse();
			                match_data.events.forEach(function(ev) {
						    if (ev.eventType == 'replace') {
						        let offPlayerName = getPlayerNameById(ev.offPlayerId, match_data.awaySquad, match_data.awaySubstitutes);
                				let onPlayerName = getPlayerNameById(ev.onPlayerId, match_data.awaySquad, match_data.awaySubstitutes);

						        if (offPlayerName && onPlayerName) {
						            let option = document.createElement('option');
						            option.value = ev.offPlayerId + "-" + ev.onPlayerId;
						            option.text = "(OFF) " + offPlayerName + " (ON) " + onPlayerName+"  ";
						            selection.appendChild(option);
						        }
						    }
						});
				            }
				        });
			        	selectTeam.dispatchEvent(new Event('change'));
						row.insertCell(cellCount).appendChild(selection);
						cellCount += 1;


						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_sub_btn';
			    		option.value = 'ChangeOn';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
						div.append(option);
							    
					    row.insertCell(cellCount).appendChild(div);
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_scorebug_subs_btn';
			    		option.value = 'Populate Subs';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY':
						select = document.createElement('select');
						select.id = 'selectTeam';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = match_data.homeTeamId;
						option.text = match_data.homeTeam.teamName1;
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = match_data.awayTeamId;
						option.text = match_data.awayTeam.teamName1;
						select.appendChild(option);
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.id = 'selectStatsType';
						select.name = select.id;
						
						option = document.createElement('option');
						option.value = 'single';
						option.text = 'Single Substitution';
						select.appendChild(option);
						
					    select.setAttribute('onchange',"processUserSelection(this)");
						row.insertCell(cellCount).appendChild(select);
						
						cellCount = cellCount + 1;
						
						option = document.createElement('input');
			    		option.type = 'button';
			    		
			    		option.name = 'populate_scorebug_subs_btn';
			    		option.value = 'Populate Subs';
			    		
			    		option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					}
					break;
				case 'FORMATION-OPTIONS':
					switch ($('#selectedBroadcaster').val().toUpperCase()) {
					case 'I_LEAGUE':
						formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_I-League_2022/Scenes/Formation_NO_Image.sum';
						break;
					case 'SANTOSH_TROPHY':
						formationScene = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_SantoshTrophy_2023/Scenes/Formation_NO_Image.sum';
						break;
					}
					select = document.createElement('select');
					
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'Formation_without_image';
					option.text = 'Formation Without Image';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Formation_with_image';
					option.text = 'Formation With Image';
					select.appendChild(option);
					
				    select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_formation_btn';
		    		option.value = 'Populate Formation';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				   /* option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'change_on_formation';
		    		option.value = 'Change On';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");*/
				    
				    div = document.createElement('div');
				    div.append(option);
					
					row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'SINGLE_SUBSTITUTE-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectSingleSubTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_single_substitution_btn';
		    		option.value = 'Populate Substitution';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;	
				case 'SUBSTITUTE-OPTIONS':
					let selectTeam = document.createElement('select');
					selectTeam.id = 'selectTeam';
					selectTeam.name = selectTeam.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					selectTeam.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					selectTeam.appendChild(option);
					
					row.insertCell(cellCount).appendChild(selectTeam);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'single';
					option.text = 'Single Substitution';
					select.appendChild(option);
					
					
					/*option = document.createElement('option');
					option.value = 'double';
					option.text = 'Double Substitution';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'triple';
					option.text = 'Triple Substitution';
					select.appendChild(option);*/
					
				    select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
				let selection = document.createElement('select');
				selection.id = 'selectTeamids';
				selection.name = selection.id;
				selection.style.width = 'auto';
				selection.style.height = 'auto';
				
				selectTeam.addEventListener('change', function() {
	            selection.innerHTML = '';  
	            if (parseInt(this.value, 10) === match_data.homeTeamId) {
					match_data.events.reverse();
	                match_data.events.forEach(function(ev) {
				    if (ev.eventType == 'replace') {
				        let offPlayerName = getPlayerNameById(ev.offPlayerId, match_data.homeSquad, match_data.homeSubstitutes);
        				let onPlayerName = getPlayerNameById(ev.onPlayerId, match_data.homeSquad, match_data.homeSubstitutes);

				        if (offPlayerName && onPlayerName) {
				            let option = document.createElement('option');
				            option.value = ev.offPlayerId + "-" + ev.onPlayerId;
				            option.text = "(OFF) " + offPlayerName + " (ON) " + onPlayerName+"  ";
				            selection.appendChild(option);
				        }
				    }
				});
	               
	            } else {
	                match_data.events.reverse();
	                match_data.events.forEach(function(ev) {
				    if (ev.eventType == 'replace') {
				        let offPlayerName = getPlayerNameById(ev.offPlayerId, match_data.awaySquad, match_data.awaySubstitutes);
        				let onPlayerName = getPlayerNameById(ev.onPlayerId, match_data.awaySquad, match_data.awaySubstitutes);

				        if (offPlayerName && onPlayerName) {
				            let option = document.createElement('option');
				            option.value = ev.offPlayerId + "-" + ev.onPlayerId;
				            option.text = "(OFF) " + offPlayerName + " (ON) " + onPlayerName+"  ";
				            selection.appendChild(option);
				        }
				    }
				});
		            }
		        });
	        	selectTeam.dispatchEvent(new Event('change'));
				row.insertCell(cellCount).appendChild(selection);
				cellCount += 1;

				if($('#selectedBroadcaster').val()!='I_LEAGUE'){
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_subchange_on_btn';
		    		option.value = 'ChangeOn';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
						    
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;
					}
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_substitution_btn';
		    		option.value = 'Populate Substitution';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'RESULT_PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'LT_PLAYER_STATS-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.playerStatsId;
                    option.text = oop.playerStatsId + ' - ' + oop.player.full_name;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'MATCH-PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'SCOREBUGPROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'LT_MATCH-PROMO-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectMatchPromo';
					select.name = select.id;
					
					dataToProcess.forEach(function(oop,index,arr1){	
					option = document.createElement('option');
                    option.value = oop.matchnumber;
                    option.text = oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
                    select.appendChild(option);
							
	                });
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				
				case 'VERTICAL_FLIPPER-OPTIONS':	
					select = document.createElement('select');
					select.id = 'selectVerticalFlipper';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_change_on_btn';
		    		option.value = 'ChangeOn';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
						    
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		option.name = 'populate_vertical_flipper_btn';
		    		option.value = 'Populate Vertical Flipper';
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				    option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'TEAM_TOUCH-OPTION':
					select = document.createElement('select');
					select.id = 'selectTeams';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectTouch';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'successful passes';
					option.text = 'Successful Passes';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'successful dribbles';
					option.text = 'Successful Dribbles';
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		option.name = 'populate_touch_btn';
		    		option.value = 'Populate Team Touch';
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				    option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'FIXTURES_RESULTD-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectFixtures';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'group A';
					option.text = 'Group A';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'group B';
					option.text = 'Group B';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'group C';
					option.text = 'Group C';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'group D';
					option.text = 'Group D';
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
				    cellCount = cellCount + 1;
				
					select = document.createElement('select');
					select.id = 'selectResultTeams';
					select.name = select.id;					
					switch ($('#selectedBroadcaster').val()) {
					    case 'SUPER_CUP':
					        option = document.createElement('option');
					        option.value = "FIXTURES";
					        option.text = "FIXTURES";
					        select.appendChild(option);
					        
					        option = document.createElement('option');
					        option.value = "FIXTURES_RESULTS";
					        option.text = "FIXTURES & RESULTS";
					        select.appendChild(option);
					        
					         option = document.createElement('option');
					        option.value = "RESULTS";
					        option.text = "RESULTS";
					        select.appendChild(option);
					        
					        row.insertCell(cellCount).appendChild(select);
					        cellCount = cellCount + 1;
					        
					        select.addEventListener('change', function() {
					            processUserSelection(this);
					        });
					        break;
						default:
							option = document.createElement('option');
							option.value = match_data.homeTeamId;
							option.text = match_data.homeTeam.teamName1;
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = match_data.awayTeamId;
							option.text = match_data.awayTeam.teamName1;
							select.appendChild(option);
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
						break;
					}
					
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		option.name = 'populate_fixture_results_btn';
		    		option.value = 'Populate Fixture & Result';
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				    option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'LT_LINE_UP-OPTIONS': case 'LT_AVG_FORMATION-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectLineUp';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		switch(whatToProcess){
						case 'LT_LINE_UP-OPTIONS':
							option.name = 'populate_lof_line_up_btn';
		    				option.value = 'Populate LineUp';
							break;
						
						case 'LT_AVG_FORMATION-OPTIONS':
							option.name = 'populate_avg_formation_btn';
		    				option.value = 'Populate AVG. Formation';
							break;
					}
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
				    option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
					
				case'PLAYINGXI-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectPlayingXI';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.id = 'selectPlayingXIType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'without_image';
					option.text = 'WITHOUT IMAGE';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'with_image';
					option.text = 'WITH IMAGE';
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
				    if($('#selectedBroadcaster').val()!='I_LEAGUE'){
						
				    option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_homesub_btn';
		    		option.value = 'ChangeOn';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
						    
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;
					
					/*option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_Away_btn';
		    		option.value = 'ChangeOn 2';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
					
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;
					
					option = document.createElement('input');
		    		option.type = 'button';
		    		
		    		option.name = 'populate_awaysub_btn';
		    		option.value = 'ChangeOn 3';
		    		
		    		option.id = option.name;
				    option.setAttribute('onclick',"processUserSelection(this)");
				    
				    div = document.createElement('div');
					div.append(option);
					
				    row.insertCell(cellCount).appendChild(div);
					cellCount = cellCount + 1;*/
					}
				    switch(whatToProcess){
						case'PLAYINGXI-OPTIONS':
							option = document.createElement('input');
				    		option.type = 'button';
				    		
				    		option.name = 'populate_playingxi_btn';
				    		option.value = 'Populate Teams LineUp';
				    		
				    		option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						    
						    row.insertCell(cellCount).appendChild(div);
						    cellCount = cellCount + 1;
							break;
					}
				    
					option = document.createElement('input');
					option.type = 'button';
					option.name = 'cancel_graphics_btn';
					option.id = option.name;
					option.value = 'Cancel';
					option.setAttribute('onclick','processUserSelection(this)');
			
				    div.append(option);
				    
				    row.insertCell(cellCount).appendChild(div);
				    cellCount = cellCount + 1;
				    
					document.getElementById('select_graphic_options_div').style.display = '';
					break;
				case 'TEAMFIXTURE-OPTIONS':
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectTeamFixture';
					select.name = select.id;
					
					dataToProcess.forEach(function(tf,index,arr1){
						option = document.createElement('option');
						option.value = tf.teamId;
						option.text = tf.teamName1 ;
						select.appendChild(option);
					});
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
			case 'LEADERBOARD-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectLeaderBoard';
				select.name = select.id;
				
				dataToProcess.forEach(function(lb,index,arr1){
					option = document.createElement('option');
					option.value = lb.leaderboardId;
					option.text = lb.header;
					select.appendChild(option);
				});
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectPlayerPhoto';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'without_photo';
				option.text = 'Without Photo' ;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'with_photo';
				option.text = 'With Photo' ;
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectLeaderBoardPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				break;
			case'NAMESUPER-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectNameSuper';
				select.name = select.id;
				
				dataToProcess.forEach(function(ns,index,arr1){
					option = document.createElement('option');
					option.value = ns.namesuperId;
					option.text = ns.subHeader ;
					select.appendChild(option);
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				break;
			case 'STAFF-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectStaff';
				select.name = select.id;
				
				dataToProcess.forEach(function(st,index,arr1){
					if(st.clubId == home_team){
						option = document.createElement('option');
						option.value = st.staffId;
						option.text = st.name + " - " + home_team_name ;
						select.appendChild(option);
					}else if(st.clubId == away_team){
						option = document.createElement('option');
						option.value = st.staffId;
						option.text = st.name + " - " + away_team_name ;
						select.appendChild(option);
					}
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				break;
			case 'AD-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectSponsor';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'xpulse';
				option.text = 'Xpulse 200';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'passion';
				option.text = 'Passion';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Glamour';
				option.text = 'Glamour';
				select.appendChild(option);

				option = document.createElement('option');
				option.value = 'Splendor';
				option.text = 'Splendor';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Destini';
				option.text = 'HeroDestini';
				select.appendChild(option);
				
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
						
			case 'API-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectStats';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'ball_possession';
				option.text = 'BALL POSSESSION';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'shots';
				option.text = 'SHOTS';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'shot_on_target';
				option.text = 'SHOTS ON TARGET';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_card';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				//select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			case 'CHETTRI-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectType';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'Chettri1';
				option.text = 'Name & Intro';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Chettri2';
				option.text = 'Record & Achivements';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Chettri3';
				option.text = 'All Time INTL. Scorers';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				option = document.createElement('input');
	    		option.type = 'button';
	    		
	    		option.name = 'populate_chettri_change_on_btn';
	    		option.value = 'ChangeOn - Record & Achivements';
	    		
	    		option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
				div.append(option);
					    
			    row.insertCell(cellCount).appendChild(div);
				cellCount = cellCount + 1;
				
				option = document.createElement('input');
	    		option.type = 'button';
	    		option.name = 'populate_chettri_btn';
	    		option.value = 'Populate Chettri';
	    		option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
			    option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				
				break;
			case 'TOP_STATS-OPTIONS':
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectTopStats';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'Best Runner';
				option.text = 'Best Runner';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Best Sprinter';
				option.text = 'Best Sprinter';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Highest Distance';
				option.text = 'Highest Distance';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Team Top Speed';
				option.text = 'Team Top Speed';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Touches';
				option.text = 'Touches';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'duel won';
				option.text = 'Duel Won';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Successful Dribbles';
				option.text = 'Successful Dribbles';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Recoveries';
				option.text = 'Recoveries';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Aerial Duels Won';
				option.text = 'Aerial Duels Won';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				 $('#selectTopStats').on('change', function() { 
				    processFootballProcedures('TOP_STATS-OPTIONS_DATA', $(this).val() || $(this).find('option').first().val()); 
				}).trigger('change'); // Trigger change event to handle default value on page load
				
				break;
			case 'HEATMAP_PEAKDISTACE-OPTION':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectHeatmappeakdistance';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'heatmap';
				option.text = 'Heat Map';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'peakdistance';
				option.text = 'Peak Distance';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
				
			case 'SCOREBUG-CARD-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectCaptainGoalKeeper';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'player';
				option.text = 'Player';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_card';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'red_card';
				option.text = 'RED CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_red';
				option.text = '2YELLOW';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;	
			case 'NAMESUPER-CARD-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;

				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectCaptainGoalKeeper';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = 'yellow';
				option.text = 'YELLOW CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'red';
				option.text = 'RED CARD';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'yellow_red';
				option.text = '2YELLOW/RED';
				select.appendChild(option);
				
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			case 'PLAYER_TOUCH_MAP-OPTIONS':
				select = document.createElement('select');
				select.id = 'selectTeam';
				select.name = select.id;
				
				option = document.createElement('option');
				option.value = match_data.homeTeamId;
				option.text = match_data.homeTeam.teamName1;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = match_data.awayTeamId;
				option.text = match_data.awayTeam.teamName1;
				select.appendChild(option);
			
				select.setAttribute('onchange',"processUserSelection(this)");
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				
				select = document.createElement('select');
				select.style = 'width:100px';
				select.id = 'selectPlayer';
				select.name = select.id;
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;
			
			case 'NAMESUPER_PLAYER-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
				
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
	
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectCaptainGoalKeeper';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'Player Of The Match';
					option.text = 'Player Of The Match';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Player';
					option.text = 'Player';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Player_Today_Goal';
					option.text = 'Player Goals Today';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Player_Role';
					option.text = 'Player Role';
					select.appendChild(option);
	
					/*option = document.createElement('option');
					option.value = 'Hero Of The Match';
					option.text = 'Hero Of The Match';
					select.appendChild(option);*/
					
					option = document.createElement('option');
					option.value = 'Goal_Scorer';
					option.text = 'Goal Scorer';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Captain';
					option.text = 'Captain';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Captain-GoalKeeper';
					option.text = 'Captain-GoalKeeper';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'Goal_Keeper';
					option.text = 'GoalKeeper';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectPlayer';
					select.name = select.id;
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
				break;
			case 'EXTRA_DATA-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectData';
				select.name = select.id;
				
				dataToProcess.forEach(function(datadb,index,arr1){
					option = document.createElement('option');
					option.value = datadb.dataId;
					option.text = datadb.prompt;
					select.appendChild(option);
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				break;	
			
			case 'BUG_DB-OPTIONS': case 'BUG_-OPTIONS':
				select = document.createElement('select');
				select.style = 'width:130px';
				select.id = 'selectBugdb';
				select.name = select.id;
				
				dataToProcess.forEach(function(bug,index,arr1){
					option = document.createElement('option');
					option.value = bug.bugId;
					option.text = bug.prompt;
					select.appendChild(option);
				});
				
				row.insertCell(cellCount).appendChild(select);
				cellCount = cellCount + 1;
				switch ($('#selectedBroadcaster').val().toUpperCase()){
					case 'I_LEAGUE': case 'SANTOSH_TROPHY':
						switch(whatToProcess){
						case 'BUG_DB-OPTIONS':
							select = document.createElement('input');
							select.type = "text";
							select.id = 'bugdbScene';
							select.name = select.id;
							//select.value = 'D:/DOAD_In_House_Everest/Everest_Cricket/EVEREST_GPCL2022/Scenes/Bug_SingleLine.sum';
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							break;
	
						}
					break;
				}
				
				break;
			}
			
			switch (whatToProcess) {
			case 'AD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_sponsor_btn';
				option.value = 'Populate Sponsor';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'RESULT_PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_result_promo_btn';
				option.value = 'Populate Result';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'LT_PLAYER_STATS-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_lt_player_stats_btn';
				option.value = 'Populate Lt Player Stats';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'MATCH-PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_match_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'SCOREBUGPROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_scorebug_match_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'LT_MATCH-PROMO-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
				option.name = 'populate_ltmatch_promo_btn';
				option.value = 'Populate Match Promo';
			    option.id = option.name;
			    
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;	
			case 'NAMESUPER-CARD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_namesuper_card_btn';
			    option.value = 'Populate Namesuper Card';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'TOP_STATS-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_Top_Stats_btn';
			    option.value = 'Populate Top Stats';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'HEATMAP_PEAKDISTACE-OPTION':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_heatmap_btn';
			    option.value = 'Populate Image';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'SCOREBUG-CARD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_scorebug_card_btn';
			    option.value = 'Populate ScoreBug Card';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;	
			case 'STAFF-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_staff_btn';
			    option.value = 'Populate Staff';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'TEAMFIXTURE-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_teamfixture_btn';
			    option.value = 'Populate Team Fixture';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'LEADERBOARD-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_leaderBoard_btn';
			    option.value = 'Populate LeaderBoard';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case'NAMESUPER-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';
			    option.name = 'populate_namesuper_btn';
			    option.value = 'Populate Namesuper';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'PLAYER_TOUCH_MAP-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';	
				option.name = 'populate_player_touch_map_btn';
			    option.value = 'Populate Player Touch Map';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'NAMESUPER_PLAYER-OPTIONS':
				option = document.createElement('input');
		   	 	option.type = 'button';	
				option.name = 'populate_namesuper_player_btn';
			    option.value = 'Populate Namesuper-Player';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			case 'BUG_DB-OPTIONS':
				option = document.createElement('input');
		    	option.type = 'button';
				option.name = 'populate_bug_db_btn';
			    option.value = 'Populate Bug';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
				
		case 'EXTRA_DATA-OPTIONS':
			option = document.createElement('input');
	    	option.type = 'button';
			option.name = 'populate_ExtraData_db_btn';
		    option.value = 'Populate ';
		    option.id = option.name;
		    option.setAttribute('onclick',"processUserSelection(this)");
		    
		    div = document.createElement('div');
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_graphics_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');
	
		    div.append(option);
		    
		    row.insertCell(cellCount).appendChild(div);
		    cellCount = cellCount + 1;
		    
			document.getElementById('select_graphic_options_div').style.display = '';
			break;
				
		case'BUG_-OPTIONS':
			option = document.createElement('input');
		    	option.type = 'button';
				option.name = 'populate_Team_db_btn';
			    option.value = 'Populate ';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
	
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
		
			    div.append(option);
			    
			    row.insertCell(cellCount).appendChild(div);
			    cellCount = cellCount + 1;
			    
				document.getElementById('select_graphic_options_div').style.display = '';
				break;
			/*case'PLAYINGXI-OPTIONS':
				option.name = 'populate_playingxi_btn';
		    	option.value = 'Populate Teams LineUp';
				break;*/	
			
		    
		}
			break;
		}
		break;
	/*case 'APIDATA-OPTIONS':
		var home_name,away_name;
		api_value_home = '';
		api_value_away = '';
		header_text = document.createElement('h6');
		header_text.innerHTML = 'DOAD API DATA';
		document.getElementById('select_graphic_options_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');

		table.appendChild(tbody);
		document.getElementById('select_graphic_options_div').appendChild(table);

		row = tbody.insertRow(tbody.rows.length);
		
		header_text = document.createElement('h6');
		if(dataToProcess.apiData.length > 0) {
			for(var i = 0; i <= dataToProcess.apiData.length -1; i++ ) {
				if(dataToProcess.apiData[i].team_id ==  dataToProcess.homeTeam.teamApiId) {
					home_name = dataToProcess.apiData[i].team_name;
					
					if(dataToProcess.apiData[i].param_name == 'Yellow card') {
						api_value_home =  'YELLOW: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Red card') {
						api_value_home = api_value_home + ' RED: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Offsides') {
						api_value_home = api_value_home + ' OFFSIDES: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots') {
						api_value_home = api_value_home + ' SHOTS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots on target') {
						api_value_home = api_value_home + ' SHOTS ON TARGET: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Corner') {
						api_value_home =  api_value_home + ' CORNERS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Tackles') {
						api_value_home =  api_value_home + ' TACKLES: ' + dataToProcess.apiData[i].value + ', ';
					}
				}
				
				
				if(dataToProcess.apiData[i].team_id == dataToProcess.awayTeam.teamApiId) {
					away_name = dataToProcess.apiData[i].team_name;
					
					if(dataToProcess.apiData[i].param_name == 'Yellow card') {
						api_value_away = ' YELLOW: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Red card') {
						api_value_away = api_value_away + ' RED: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Offsides') {
						api_value_away = api_value_away + ' OFFSIDES: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots') {
						api_value_away = api_value_away + ' SHOTS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Shots on target') {
						api_value_away = api_value_away + ' SHOTS ON TARGET: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Corner') {
						api_value_away = api_value_away + ' CORNERS: ' + dataToProcess.apiData[i].value + ', ';
					}
					if(dataToProcess.apiData[i].param_name == 'Tackles') {
						api_value_away = api_value_away + ' TACKLES: ' + dataToProcess.apiData[i].value + ', ';
					}
				}
			}
			header_text.innerHTML = header_text.innerHTML  + home_name + ' : ' + '[ ' + api_value_home + ' ]' + "<br>" + "<br>" 
									+ away_name  + ' : ' + '[ ' + api_value_away + ' ]';
			row.insertCell(0).appendChild(header_text);
			
		}
		break;*/	
	case 'ACCURATE_PASS_OPTION':
	case 'LBAND_PLAYINGXI_OPTION': case 'LBAND_MATCH_DATA_OPTION':case 'LBAND_ATTACKING_ZONE':
	    $('#select_graphic_options_div').empty();
	
	    header_text = document.createElement('h6');
	    switch (whatToProcess) {
            case 'LBAND_PLAYINGXI_OPTION':
            	header_text.innerHTML = 'PLAYING XI';
                break;
            case 'LBAND_MATCH_DATA_OPTION':
            	header_text.innerHTML = 'MATCH DATA';
                break;
            case 'LBAND_ATTACKING_ZONE':
            	header_text.innerHTML = 'TEAM ATTACKING_ZONE';
                break;
        }
	    
	    document.getElementById('select_graphic_options_div').appendChild(header_text);
	
	    table = document.createElement('table');
	    table.setAttribute('class', 'table table-bordered');
	
	    tbody = document.createElement('tbody');
	    table.appendChild(tbody);
	    document.getElementById('select_graphic_options_div').appendChild(table);
	
	    row = tbody.insertRow(tbody.rows.length);
	    cellCount = 0;
		
		switch (whatToProcess){
			case 'LBAND_MATCH_DATA_OPTION':
				select = document.createElement('select');
			    select.id = 'selectData';
			    select.name = select.id;
			    
			    options = ['TOUCHES','TACKLE','ACCURATE_PASS','FOUL','CLEARANCE','DUEL_WON',
			    'CORNER_TAKEN', 'TOTAL_OFF_SIDE','SAVES','YELLOW_CARD','RED_CARD','POSSESSION',
			    'BLOCKED_SCORING_ATTACK','SHOT_ON_TARGET ','TOTAL_PASS','TOTAL_THROWS',
			    'INTERCEPTIONS','BALL_RECOVERY','TOTAL_CROSS','TURNOVER','HEAD_TO_HEAD'];
			    
			    options.forEach(function(optionText) {
			        option = document.createElement('option');
			        option.value = optionText.replace('HEAD_TO_HEAD','H2H');
			        option.text = optionText.replace('ATTACK','ATT');
			        select.appendChild(option);
			    });
			    row.insertCell(cellCount).appendChild(select);
			    cellCount = cellCount + 1;
				break;
		}
		
	   
	    switch (whatToProcess) {
            case 'LBAND_MATCH_DATA_OPTION':
				let selectStats = document.createElement('select');
			        selectStats.id = 'selectStats';
			        selectStats.name = selectStats.id;
			        select.addEventListener('change', function() {
			            selectStats.innerHTML = '';  
			
			            if (this.value === 'POSSESSION') {
			                option = document.createElement('option');
			                option.value = 'BOTTOM';
			                option.text = 'BOTTOM';
			                selectStats.appendChild(option);
			            } else {
			                let statsOptions = ['BOTH', 'RIGHT', 'BOTTOM'];
			                statsOptions.forEach(function(optionText) {
			                    option = document.createElement('option');
			                    option.value = optionText;
			                    option.text = optionText;
			                    selectStats.appendChild(option);
			                });
			            }
			        });
			        select.dispatchEvent(new Event('change'));
			        row.insertCell(cellCount).appendChild(selectStats);
			        cellCount = cellCount + 1;
			        break;
            case 'LBAND_PLAYINGXI_OPTION': case 'LBAND_ATTACKING_ZONE':
            	select = document.createElement('select');
			    select.id = 'selectStats';
			    select.name = select.id;
			    
            	option = document.createElement('option');
		        option.value = '0';
		        option.text = 'HOME';
		        select.appendChild(option);
		        
		        option = document.createElement('option');
		        option.value = '2';
		        option.text = 'HOME SUB';
		        select.appendChild(option);
		        
		        option = document.createElement('option');
		        option.value = '1';
		        option.text = 'AWAY';
		        select.appendChild(option);
		        
		        option = document.createElement('option');
		        option.value = '3';
		        option.text = 'AWAY SUB';
		        select.appendChild(option);
		         row.insertCell(cellCount).appendChild(select);
	   			 cellCount = cellCount + 1;
                break;
        }
	    switch (whatToProcess){
			case 'LBAND_PLAYINGXI_OPTION':
				select = document.createElement('select');
			    select.id = 'whichSponsor';
			    select.name = select.id;
			    
			    option = document.createElement('option');
		        option.value = '1';
		        option.text = 'GOOGLE CLOUD';
		        select.appendChild(option);
		        
		        option = document.createElement('option');
		        option.value = '2';
		        option.text = 'AMAZON';
		        select.appendChild(option);
		        
		        option = document.createElement('option');
		        option.value = '3';
		        option.text = 'DREAM 11';
		        select.appendChild(option);
			    
			    row.insertCell(cellCount).appendChild(select);
			    cellCount = cellCount + 1;
				break;
		}
	
	    div = document.createElement('div'); // Initialize div
	
	    option = document.createElement('input');
	    option.type = 'button';
	
	    switch ($('#selectedBroadcaster').val()) {
	        case 'EURO_LEAGUE':case'SUPER_CUP':
	            switch (whatToProcess) {
			        case 'LBAND_PLAYINGXI_OPTION':
			        	option.name = 'populate_playingXI_btn';
			            break;
			        case 'LBAND_ATTACKING_ZONE':
			         	option.name = 'populate_attackingZone_btn';
			            break;
			        case 'LBAND_MATCH_DATA_OPTION':
			        	option.name = 'populate_match_data_btn';
			            break;
	            }
	            break;
	   	}
		option.value = 'Populate ';
		option.id = option.name;
		option.setAttribute('onclick',"processUserSelection(this)");
		div.append(option);
	    row.insertCell(cellCount).appendChild(div);
	    cellCount = cellCount + 1;
	    
    	option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_graphics_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(option);
	    
	    row.insertCell(cellCount).appendChild(div);
	    cellCount = cellCount + 1;
	    
		document.getElementById('select_graphic_options_div').style.display = '';
    	break;

	case 'SCOREBUG_OPTION': case 'SCOREBUG_API_OPTION': case 'EXTRA-TIME_OPTION': case 'SCOREBUG_OPTION_2': case 'EXTRA-TIME-BOTH_OPTION': case 'RED_CARD_OPTION':
	case 'SCOREBUG-TEAM_STATS-OPTIONS': case 'SCOREBUG_PLAYERS_STATS-OPTIONS': case 'SCOREBUG-H2H_BOTH-OPTIONS':
		switch ($('#selectedBroadcaster').val()) {
		case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': case 'VIZ_TRI_NATION': case 'SUPER_CUP':

			$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
			
			switch(whatToProcess){
				case 'SCOREBUG-H2H_BOTH-OPTIONS':
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'BOTH_TEAMS';
					option.text = 'Both Teams';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'HEADTOHEAD';
					option.text = 'HeadToHead';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'TEAM_COMPARISON';
					option.text = 'Team Comparison';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'SCOREBUG-TEAM_STATS-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectTeamStats';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'SCOREBUG_PLAYERS_STATS-OPTIONS':
					select = document.createElement('select');
					select.id = 'selectTeam';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = match_data.homeTeamId;
					option.text = match_data.homeTeam.teamName1;
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = match_data.awayTeamId;
					option.text = match_data.awayTeam.teamName1;
					select.appendChild(option);
				
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectStatsType';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'stats';
					option.text = 'Stats';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'heatmap';
					option.text = 'HeatMap';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectPlayerPhoto';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'without_photo';
					option.text = 'WithOut Photo';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'with_photo';
					option.text = 'With Photo';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('select');
					select.style = 'width:100px';
					select.id = 'selectPlayer';
					select.name = select.id;
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'RED_CARD_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selecthometeamredcard';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectawayteamredcard';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					break;
				case 'EXTRA-TIME_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectExtratime';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				case 'EXTRA-TIME-BOTH_OPTION':
					select = document.createElement('input');
					select.type = "text";
					select.id = 'selectExtratimeBoth';
					select.value = '';
					
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				case 'SCOREBUG_OPTION_2':
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectScorebugstatstwo';
					select.name = select.id;
					
					option = document.createElement('option');
					option.value = 'yellow_home';
					option.text = 'Yellow Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'yellow_away';
					option.text = 'yellow Away +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'red_home';
					option.text = 'Red Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'red_away';
					option.text = 'Red Away +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'corners_home';
					option.text = 'Corners Home +1';
					select.appendChild(option);
					
					option = document.createElement('option');
					option.value = 'corners_away';
					option.text = 'Corners Away +1';
					select.appendChild(option);
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					
					break;
				case 'SCOREBUG_API_OPTION':
					select = document.createElement('select');
					select.style = 'width:130px';
					select.id = 'selectScorebugstats';
					select.name = select.id;
					
					/*['Possession','Shots','Shots on Target','Touches','Touches In OppBox','Offside',
					 'Passes','Accurate Pass','Passing Accuracy','Final Third Passes', 'passes final 3rd Accuracy',
					 'Final 3rd Entries','Crosses','Corners','Corners Won','Dribbles','Successful Dribbles',
					 'Duel','Duel won','Aerial','Tackles','Tackles Won','Interceptions','InterceptionsWon',
					 'Fouls Won','Fouls','Yellow Cards','Red Cards','Saves','Chance Created','long Pass','long Pass Success',
					 'Shots Inside Box','Possession Won in the Final Third','Possession Won',].forEach(stat => {
						
						option = document.createElement('option');
						option.value =stat.replace(/\s+/g, "_");
						option.text = stat.replace('Successful Dribbles', 'Successful Dribbles(%)').replace('Duel won', 'Duel won (%)').replace('Duel', 'Duels');
						select.appendChild(option);
					});*/
					
					['Possession','Shots','Shots_on_Target','Corners','Saves','Crosses','Passes','Passing_Accuracy','Touches','Tackles',
							'Offside','Fouls','Interceptions','Chance_Created','goalsConceded','duelWon','Red_Cards','Yellow_Cards','Duel_won',
							'Duel','passes_final_3rd_Accuracy','Final_3rd_Entries','Touches_In_OppBox','Final_Third_Passes','Goals'].forEach(stat => {
						
						option = document.createElement('option');
						option.value =stat.replace(/\s+/g, "_");
						option.text = stat.replace('Duel_won', 'Duel won (%)').replace('Duel', 'Duels');
						select.appendChild(option);
					});
					
					
					
					select.setAttribute('onchange',"processUserSelection(this)");
					row.insertCell(cellCount).appendChild(select);
					cellCount = cellCount + 1;
					$('#selectScorebugstats').on('change', function() { 
					}).trigger('change');
				
					break;
					
				case 'SCOREBUG_OPTION':
					switch ($('#selectedBroadcaster').val()){
						case 'SUPER_CUP':
						    let labelRow = table.insertRow(0); 
						    let labelCell = labelRow.insertCell(0);
						    labelCell.textContent = 'Select Option'; 
						
						    labelCell = labelRow.insertCell(1);
						    labelCell.textContent = 'Home Data'; 
						
						    labelCell = labelRow.insertCell(2);
						    labelCell.textContent = 'Away Data'; 
							select = document.createElement('select');
							select.style = 'width:130px';
							select.id = 'selectScorebugstats';
							select.name = select.id;
							 dataToProcess.forEach(function(d) {
								option = document.createElement('option');
								option.value = d;
								option.text = d.split(',')[1];
								select.appendChild(option);
							});
							select.setAttribute('onchange',"processUserSelection(this)");
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							select = document.createElement('input');
								select.type = "text";
								select.id = 'selecthomedata';
								select.value = dataToProcess[0].split(",")[0];
								
								row.insertCell(cellCount).appendChild(select);
								cellCount = cellCount + 1;
								
								select = document.createElement('input');
								select.type = "text";
								select.id = 'selectawaydata';
								select.value = dataToProcess[0].split(",")[2];
								row.insertCell(cellCount).appendChild(select);
								cellCount = cellCount + 1;
							 $('#selectScorebugstats').on('change', function() {    
						        $('#selecthomedata').val($(this).val().split(",")[0]);
						        $('#selectawaydata').val($(this).val().split(",")[2]); 
						    });	
						break;
						case 'VIZ_TRI_NATION':
							select = document.createElement('select');
							select.style = 'width:130px';
							select.id = 'selectScorebugstats';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'yellow';
							option.text = 'Yellow Card';
							select.appendChild(option);
		
							option = document.createElement('option');
							option.value = 'red';
							option.text = 'Red Card';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'off_side';
							option.text = 'Offside';
							select.appendChild(option);
			
							option = document.createElement('option');
							option.value = 'shots';
							option.text = 'Shots';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'shots_on_target';
							option.text = 'Shots on Target';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'possession';
							option.text = 'Possession';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'corners';
							option.text = 'Corners';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'tackles';
							option.text = 'Tackles';
							select.appendChild(option);
							
							select.setAttribute('onchange',"processUserSelection(this)");
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							
							select = document.createElement('input');
							select.type = "text";
							select.id = 'selecthomedata';
							select.value = '';
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							
							select = document.createElement('input');
							select.type = "text";
							select.id = 'selectawaydata';
							select.value = '';
							
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							break;
						case 'I_LEAGUE': case 'SANTOSH_TROPHY': case 'VIZ_SANTOSH_TROPHY': 
							select = document.createElement('select');
							select.style = 'width:130px';
							select.id = 'selectScorebugstats';
							select.name = select.id;
							
							option = document.createElement('option');
							option.value = 'yellow';
							option.text = 'Yellow Card';
							select.appendChild(option);
		
							option = document.createElement('option');
							option.value = 'red';
							option.text = 'Red Card';
							select.appendChild(option);
							
							/*option = document.createElement('option');
							option.value = 'off_side';
							option.text = 'Offside';
							select.appendChild(option);
			
							option = document.createElement('option');
							option.value = 'shots';
							option.text = 'Shots';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'shots_on_target';
							option.text = 'Shots on Target';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'possession';
							option.text = 'Possession';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'corners';
							option.text = 'Corners';
							select.appendChild(option);
							
							option = document.createElement('option');
							option.value = 'tackles';
							option.text = 'Tackles';
							select.appendChild(option);*/
							
							select.setAttribute('onchange',"processUserSelection(this)");
							row.insertCell(cellCount).appendChild(select);
							cellCount = cellCount + 1;
							break;
					}
					
					break;
				}
			
			option = document.createElement('input');
		    option.type = 'button';
			switch (whatToProcess) {
			case 'SCOREBUG-TEAM_STATS-OPTIONS':
				option.name = 'populate_team_stats_btn';
		    	option.value = 'Populate Team Stats';
				break;
			case 'SCOREBUG-H2H_BOTH-OPTIONS':
				option.name = 'populate_h2h_btn';
		    	option.value = 'Populate Stats';
				break;
			case 'SCOREBUG_PLAYERS_STATS-OPTIONS':
				option.name = 'populate_player_stats_btn';
		    	option.value = 'Populate Player Stats';
				break;
			case 'RED_CARD_OPTION':
				option.name = 'populate_red_card_btn';
		    	option.value = 'Populate Red Card';
		    	break;
			case 'EXTRA-TIME_OPTION':
				option.name = 'populate_extra_time_btn';
		    	option.value = 'Populate Extra Time';
				break;
			case 'EXTRA-TIME-BOTH_OPTION':
				option.name = 'populate_extra_time_both_btn';
		    	option.value = 'Populate Extra Time Both';
				break;
			case 'SCOREBUG_OPTION_2':
				option.name = 'populate_stats_two_btn';
			    option.value = 'Populate Stats';
				break;
			case 'SCOREBUG_OPTION':
			    option.name = 'populate_stats_btn';
			    option.value = 'Populate Stats';
				break;
			case 'SCOREBUG_API_OPTION':
				option.name = 'populate_stats_api_btn';
			    option.value = 'Populate Stats';
				break;
			}
		    option.id = option.name;
		    option.setAttribute('onclick',"processUserSelection(this)");
		    
		    div = document.createElement('div');
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_graphics_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');
	
		    div.append(option);
		    
		    row.insertCell(cellCount).appendChild(div);
		    cellCount = cellCount + 1;
		    
			document.getElementById('select_graphic_options_div').style.display = '';

			break;
		}
		break;
	
	case 'LOAD_OVERWRITE_TEAMS_SCORE':

		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		max_cols = 1;
		for(var i=0; i<=max_cols; i++) {
			
		    option = document.createElement('input');
		    option.type = 'text';
		    header_text = document.createElement('label');

			switch (whatToProcess) {
			case 'LOAD_OVERWRITE_TEAMS_SCORE':
				switch(i) {
				case 0:
					header_text.innerHTML = match_data.homeTeam.teamName4 + ' Score';
					option.id = 'overwrite_home_team_score';
					option.value = match_data.homeTeamScore;
					break;
				case 1:
					header_text.innerHTML = match_data.awayTeam.teamName4 + ' Score';
					option.id = 'overwrite_away_team_score';
					option.value = match_data.awayTeamScore;
					break;
				}
				break;
			}
			
			header_text.htmlFor = option.id;
			row.insertCell(i).appendChild(header_text).appendChild(option);
		}

	    option = document.createElement('input');
	    option.type = 'button';
		switch (whatToProcess) {
		case 'LOAD_OVERWRITE_TEAMS_SCORE':
		    option.name = 'log_teams_score_overwrite_btn';
		    option.value = 'Log Team Score Overwrite';
			break;
		}
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    max_cols = max_cols + 1;
	    row.insertCell(max_cols).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;
	
	case 'LOAD_OVERWRITE_MATCH_SUB':
		$('#select_event_div').empty();
			
		var team_id,on_playerName = '',off_playerName="";
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		var selectTeam = document.createElement('select');
		selectTeam.style = 'width:75%';
		selectTeam.id = 'overwrite_match_sub_index';
		selectTeam.name = selectTeam.id;
		selectTeam.setAttribute('onchange',"processUserSelection(this)");
		if(match_data.events != null && match_data.events.length > 0){
			for(var i = 0; i < match_data.events.length; i++) {
				if(match_data.events[(match_data.events.length - 1) - i].eventType == 'replace') {
					option = document.createElement('option');
					
				    if(match_data.events[(match_data.events.length - 1) - i].onPlayerId != 0){
						match_data.homeSquad.forEach(function(hs,index,arr){
							if(match_data.events[(match_data.events.length - 1) - i].onPlayerId == hs.playerId){
								on_playerName = '{'+"("+hs.jersey_number+")"+ hs.ticker_name + '} ' ;
								which_team = ' [' + match_data.homeTeam.teamName4+']';
								team_id=match_data.homeTeamId;
							}				
						});
						match_data.awaySquad.forEach(function(as,index,arr){
							if(match_data.events[(match_data.events.length - 1) - i].onPlayerId == as.playerId){
								on_playerName = '{'+"("+as.jersey_number+")"+ as.ticker_name +'} ';
								which_team = ' [' + match_data.awayTeam.teamName4+']';
								team_id=match_data.awayTeamId;
							}				
						});
						
					}else{
						on_playerName = '';
					}
					
					if(match_data.events[(match_data.events.length - 1) - i].offPlayerId != 0){
						match_data.homeSubstitutes.forEach(function(hs,index,arr){
							if(match_data.events[(match_data.events.length - 1) - i].offPlayerId == hs.playerId){
								off_playerName = ' {'+ hs.ticker_name + '}' ;
							}				
						});
						match_data.awaySubstitutes.forEach(function(as,index,arr){
							if(match_data.events[(match_data.events.length - 1) - i].offPlayerId == as.playerId){
								off_playerName = ' {'+ as.ticker_name +'}';
							}				
						});
						
					}else{
						on_playerName = '';
					}
					
					option.value = match_data.events[(match_data.events.length - 1) - i].eventNumber + ',' +team_id ;
				    option.text = on_playerName + match_data.events[(match_data.events.length - 1) - i].eventType.replace("replace","Subs") + 
				    	off_playerName + which_team;
				    selectTeam.appendChild(option);
				}
			}
		}
		
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'SUBS';
		header_text.htmlFor = selectTeam.id;
		row.insertCell(0).appendChild(header_text).appendChild(selectTeam);
		
		var selectOnPlayer = document.createElement('select');
		selectOnPlayer.style = 'width:75%';
		selectOnPlayer.name = selectOnPlayer.id;
		selectOnPlayer.id = 'overwrite_match_player_id';
		
		selectTeam.addEventListener('change', function() {
        selectOnPlayer.innerHTML = ''; 
        	option = document.createElement('option');
			option.value = '0';
			option.text = '';
			selectOnPlayer.appendChild(option);
        if (parseInt(this.value.split(",")[1], 10) == match_data.homeTeamId) {
			    match_data.homeSubstitutes.forEach(function(hp){
				option = document.createElement('option');
				option.value = hp.playerId;
			    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +') - sub';
			    selectOnPlayer.appendChild(option);
			});
           
        } else {
			    match_data.awaySubstitutes.forEach(function(hp){
				option = document.createElement('option');
				option.value = hp.playerId;
			    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.awayTeam.teamName4 +') - sub';
			    selectOnPlayer.appendChild(option);
				});
            }
        });
    	selectTeam.dispatchEvent(new Event('change'));
	    header_text = document.createElement('label');
		header_text.innerHTML = 'On Player';
		header_text.htmlFor = selectOnPlayer.id;
		row.insertCell(1).appendChild(header_text).appendChild(selectOnPlayer);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_subs_player_id';
		
		selectTeam.addEventListener('change', function() {
        select.innerHTML = ''; 
        	option = document.createElement('option');
			option.value = '0';
			option.text = '';
			select.appendChild(option);
        if (parseInt(this.value.split(",")[1], 10) == match_data.homeTeamId) {
				match_data.homeSquad.forEach(function(hp){
				option = document.createElement('option');
				option.value = hp.playerId;
			    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +')';
			    select.appendChild(option);
			    });
           
        } else {
				match_data.awaySquad.forEach(function(hp){
				option = document.createElement('option');
				option.value = hp.playerId;
			    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.awayTeam.teamName4 +')';
			    select.appendChild(option);
			    });
            }
        });
    	selectTeam.dispatchEvent(new Event('change'));
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Off Player';
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
		option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_match_subs_overwrite_btn';
	    option.value = 'Log Match Subs Overwrite';
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
        // Trigger the change event
        document.getElementById('overwrite_match_sub_index').dispatchEvent(new Event('change'));
		break;
		
	case 'LOAD_OVERWRITE_MATCH_STATS':

		$('#select_event_div').empty();

		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);

		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_index';
		select.name = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		
		match_data.matchStats.forEach(function(ms,index,arr){
			option = document.createElement('option');
			option.value = ms.statsId;
		    option.text = ms.stats_type;
			if(ms.player.full_name) {
			    option.text = option.text + ' (' + ms.player.full_name + ')';
			}
		    option.text = option.text + ' [' + ms.totalMatchSeconds + ']';
		    select.appendChild(option);
		});
	    header_text = document.createElement('label');
		header_text.innerHTML = 'Stats';
		header_text.htmlFor = select.id;
		row.insertCell(0).appendChild(header_text).appendChild(select);

		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_player_id';

		match_data.homeSquad.forEach(function(hp,index,arr){
			option = document.createElement('option');
			option.value = hp.playerId;
		    option.text = hp.jersey_number + ' - ' + hp.full_name + ' ('+ match_data.homeTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.homeSubstitutes.forEach(function(hsub,index,arr){
			option = document.createElement('option');
			option.value = hsub.playerId;
		    option.text = hsub.jersey_number + ' - ' + hsub.full_name + ' ('+ match_data.homeTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		match_data.awaySquad.forEach(function(as,index,arr){
			option = document.createElement('option');
			option.value = as.playerId;
		    option.text = as.jersey_number + ' - ' + as.full_name + ' ('+ match_data.awayTeam.teamName4 +')';
		    select.appendChild(option);
		});
		match_data.awaySubstitutes.forEach(function(asub,index,arr){
			option = document.createElement('option');
			option.value = asub.playerId;
		    option.text = asub.jersey_number + ' - ' + asub.full_name + ' ('+ match_data.awayTeam.teamName4 +') - Sub';
		    select.appendChild(option);
		});
		

	    header_text = document.createElement('label');
		header_text.innerHTML = 'Player';
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.style = 'width:75%';
		select.id = 'overwrite_match_stats_type';
	    
	    option = document.createElement('option');
		option.value = 'goal';
	    option.text = 'Goal';
	    select.appendChild(option);
	    
	    option = document.createElement('option');
		option.value = 'own_goal';
	    option.text = 'Own Goal';
	    select.appendChild(option);
		    
		option = document.createElement('option');
		option.value = 'penalty';
	    option.text = 'Penalty';
	    select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Type';
		header_text.htmlFor = option.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
		match_data.matchStats.forEach(function(ms){
			option = document.createElement('input');
			option.type = "text";
			option.id = 'overwrite_match_stats_total_seconds';
			option.value = millisToMinutesAndSeconds(ms.totalMatchSeconds);
		});
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Time';
		header_text.htmlFor = option.id;
		row.insertCell(3).appendChild(header_text).appendChild(option);

	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_match_stats_overwrite_btn';
	    option.value = 'Log Match Stats Overwrite';
	    option.id = option.name;
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div = document.createElement('div');
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_overwrite_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);
	    
	    row.insertCell(4).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;		
		
	case 'LOAD_TEAMS':		
		$('#team_selection_div').empty();
		document.getElementById('team_selection_div').style.display = 'none';
		
		if (dataToProcess)
		{
			if(dataToProcess.homeSquad.length <=0 || dataToProcess.awaySquad.length <=0) {
				if(dataToProcess.homeSquad.length <=0) {
					alert(dataToProcess.homeTeam.teamName1 + ' has no players in the database');
				} else if(dataToProcess.awaySquad.length <=0) {
					alert(dataToProcess.awayTeam.teamName1 + ' has no players in the database');
				}
				return false;
			}
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			table.setAttribute('id', 'setup_teams');
			tr = document.createElement('tr');
			for (var j = 0; j <= 5; j++) {
			    th = document.createElement('th'); //column
			    switch (j) {
				case 0: case 3:
				    text = document.createTextNode('Position'); 
					break;
				case 1:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName1); 
					break;
				case 2:
				    text = document.createTextNode(dataToProcess.homeTeam.teamName4 + ' captain/keeper'); 
					break;
				case 4:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName1); 
					break;
				case 5:
				    text = document.createTextNode(dataToProcess.awayTeam.teamName4 + ' captain/keeper'); 
					break;
				}
			    th.appendChild(text);
			    tr.appendChild(th);
			}
			
			thead = document.createElement('thead');
			thead.appendChild(tr);
			table.appendChild(thead);

			tbody = document.createElement('tbody');
			max_cols = parseInt(10 + parseInt($('#homeSubstitutesPerTeam option:selected').val()));
			if(parseInt($('#homeSubstitutesPerTeam option:selected').val()) < parseInt($('#awaySubstitutesPerTeam option:selected').val())) {
				max_cols = parseInt(10 + parseInt($('#awaySubstitutesPerTeam option:selected').val()));
			}else if(parseInt($('#homeSubstitutesPerTeam option:selected').val()) > parseInt($('#awaySubstitutesPerTeam option:selected').val())) {
				max_cols = parseInt(10 + parseInt($('#homeSubstitutesPerTeam option:selected').val()));
			}
	
			for(var i=0; i <= max_cols; i++) {
				
				row = tbody.insertRow(tbody.rows.length);
				
				for(var j=0; j<=5; j++) {
					
					addSelect = false;
					switch(j) {
					case 1: case 2:
						if(i <= parseInt(10 + parseInt($('#homeSubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					case 4: case 5:
						if(i <= parseInt(10 + parseInt($('#awaySubstitutesPerTeam option:selected').val()))) {
							addSelect = true;
						}
						break;
					}
					
					cell = row.insertCell(j);
					
					if(addSelect == true) {

						select = document.createElement('select');
						select.style = 'width:75%';
						
						switch(j) {
						case 1: case 4:
						
							if(j==1) {
								select.name = 'selectHomePlayers';
								select.id = 'homePlayer_' + (i + 1);
							} else if(j==4) {
								select.name = 'selectAwayPlayers';
								select.id = 'awayPlayer_' + (i + 1);
							}
							if(j==1) {
								dataToProcess.homeSquad.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeSubstitutes.forEach(function(hp,index,arr){
									option = document.createElement('option');
									option.value = hp.playerId;
								    option.text = hp.jersey_number + ' - ' + hp.full_name;
								    select.appendChild(option);
								});
								dataToProcess.homeOtherSquad.forEach(function(hs,index,arr){
									option = document.createElement('option');
									option.value = hs.playerId;
								    option.text = hs.jersey_number + ' - ' + hs.full_name;
								    select.appendChild(option);
								});
								
							} else if (j==4) {
								
								dataToProcess.awaySquad.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awaySubstitutes.forEach(function(ap,index,arr){
									option = document.createElement('option');
									option.value = ap.playerId;
								    option.text = ap.jersey_number + ' - ' + ap.full_name;
								    select.appendChild(option);
								});
								dataToProcess.awayOtherSquad.forEach(function(as,index,arr){
									option = document.createElement('option');
									option.value = as.playerId;
								    option.text = as.jersey_number + ' - ' + as.full_name;
								    select.appendChild(option);
								});
							}
						    select.selectedIndex = i;
							break;
						
						case 2: case 5:
						
							if(j==2) {
								select.name = 'selectHomeCaptainGoalKeeper';
								select.id = 'homeCaptainGoalKeeper_' + (i + 1);
							} else {
								select.name = 'selectAwayCaptainGoalKeeper';
								select.id = 'awayCaptainGoalKeeper_' + (i + 1);
							}
							for(var k=0; k<=3; k++) {
								option = document.createElement('option');
								switch (k) {
								case 0:
									option.value = '';
								    option.text = '';
									break;
								case 1:
									option.value = 'captain';
								    option.text = 'Captain';
									break;
								case 2:
									option.value = 'goal_keeper';
								    option.text = 'Goal Keeper';
									break;
								case 3:
									option.value = 'captain_goal_keeper';
								    option.text = 'Captain And Goal Keeper';
									break;
								}
							    select.appendChild(option);
							}
							if(i <= 10) {
								switch(j) {
								case 2: 
									select.value = dataToProcess.homeSquad[i].captainGoalKeeper;
									break;
								case 5:
									select.value = dataToProcess.awaySquad[i].captainGoalKeeper;
									break;
								}
							}
							if(i > 10 && (i-11) <= dataToProcess.homeSubstitutes.length -1){
								switch(j) {
								case 2:
									select.value = dataToProcess.homeSubstitutes[i-11].captainGoalKeeper;
									break;
								}
							}
							if(i > 10 && (i-11) <= dataToProcess.awaySubstitutes.length -1){
								switch(j) {
								case 5:
									select.value = dataToProcess.awaySubstitutes[i-11].captainGoalKeeper;
									break;
								}
							}
							break;
						}
						row.insertCell(j).appendChild(select);
						removeSelectDuplicates(select.id);
						$(select).select2();
					} else {
						switch(j) {
						case 0: case 3:
							//cell = row.insertCell(j);
							if(j==0) {
								cell.setAttribute("name", 'selectHomePlayersPosition');
								cell.setAttribute("id", 'homePositionPlayer_' + (i + 1));
							} else {
								cell.setAttribute("name", 'selectAwayPlayersPosition');
								cell.setAttribute("id", 'awayPositionPlayer_' + (i + 1));
							}
							select = document.createElement('label');
							select.innerHTML = (i + 1);
							cell.appendChild(select);
							cell.setAttribute('onclick','processUserSelection(this)');
							//select = document.createElement('label');
							//select.innerHTML = (i + 1);
							break;
						default:
							break;
						}
						//row.insertCell(j).appendChild(select);
					}
				}
			}
		
			table.appendChild(tbody);
			document.getElementById('team_selection_div').appendChild(table);
			document.getElementById('team_selection_div').style.display = '';
		} 
		break;
		
	case 'POPULATE-OFF_PLAYER':
		
		$('#select_player').empty();
		
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSquad.forEach(function(hs,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: hs.playerId,
	                text: hs.jersey_number + ' - ' + hs.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySquad.forEach(function(as,index,arr){
				$('#select_player').append(
					$(document.createElement('option')).prop({
	                value: as.playerId,
	                text: as.jersey_number + ' - ' + as.full_name
		        }))					
			});
		}
		break;
		
	case 'POPULATE-ON_PLAYER':
		
		$('#select_sub_player').empty();
		if(dataToProcess.homeTeamId ==  $('#select_teams option:selected').val()){
			dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: hsub.playerId,
	                text: hsub.jersey_number + ' - ' + hsub.full_name
		        }))					
			});
		}
		else {
			dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
				$('#select_sub_player').append(
					$(document.createElement('option')).prop({
	                value: asub.playerId,
	                text: asub.jersey_number + ' - ' + asub.full_name
		        }))					
			});
		}
		break;

	case 'LOAD_PENALTY':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);

		for(var i=1; i<=2; i++) {
			div = document.createElement('div');
			div.style = 'text-align:center;';
			switch(i){
			case 1:
				text = 'home';
				break;
			case 2:
				text = 'away';
				break;
			}
			div.id = text + '_penalties_div';
			for(var j=0; j<=5; j++) {
				switch(j){
				case 0: case 3:
					header_text = document.createElement('label');
					header_text.htmlFor = div.id;
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 0:
						header_text.innerHTML = text.toUpperCase() + ' Hits: ';
						option.id = text + '_increment_penalties_hit_btn';
						break;
					case 3:
						header_text.innerHTML = 'Misses: ';
						option.id = text + '_increment_penalties_miss_btn';
						break;
					}
					option.value = "+";
					div.appendChild(header_text).appendChild(option);
					break;
				case 1: case 4:
	    			option = document.createElement('input');
					option.type = 'text';
					switch(j){
					case 1:
						option.id = text + '_penalties_hit_txt';
						break;
					case 4:
						option.id = text + '_penalties_miss_txt';
						break;
					}
					option.value = '0';
					option.style = 'width:10%;text-align:center;';
					div.appendChild(option);
					break;
				case 2: case 5:
					option = document.createElement('input');
					option.type = "button";
					option.style = 'text-align:center;';
					option.setAttribute('onclick','processUserSelection(this)');
					switch(j){
					case 2:
						option.id = text + '_decrement_penalties_hit_btn';
						break;
					case 5:
						option.id = text + '_decrement_penalties_miss_btn';
						break;
					}
					option.value = "-";
					div.appendChild(option);
				    div.append(document.createElement('br'));
					break;
				}
			}
			row.insertCell(i-1).appendChild(div);
		}

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_penalty_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.appendChild(option);

		row.insertCell(2).appendChild(div);
		
		table.appendChild(tbody);
		
		document.getElementById('select_event_div').appendChild(table);
		
		break;
				
	case 'LOAD_REPLACE':
		
		$('#select_event_div').empty();
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		row = tbody.insertRow(tbody.rows.length);
		
		select = document.createElement('select');
		select.id = 'select_teams';
		select.name = select.id;
		
		option = document.createElement('option');
		option.value = dataToProcess.homeTeamId;
		option.text = dataToProcess.homeTeam.teamName1;
		select.appendChild(option);
		
		option = document.createElement('option');
		option.value = dataToProcess.awayTeamId;
		option.text = dataToProcess.awayTeam.teamName1;
		select.appendChild(option);
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Teams: '
		header_text.htmlFor = select.id;
		select.setAttribute('onchange',"processUserSelection(this)");
		row.insertCell(0).appendChild(header_text).appendChild(select);
		
		select = document.createElement('select');
		select.id = 'select_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Player: '
		header_text.htmlFor = select.id;
		row.insertCell(1).appendChild(header_text).appendChild(select);

	    select = document.createElement('select');
		select.id = 'select_sub_player';
		select.name = select.id;
		
		header_text = document.createElement('label');
		header_text.innerHTML = 'Sub-Player: '
		header_text.htmlFor = select.id;
		row.insertCell(2).appendChild(header_text).appendChild(select);
		
	    div = document.createElement('div');

	    option = document.createElement('input');
	    option.type = 'button';
	    option.name = 'log_replace_btn';
	    option.id = option.name;
	    option.value = 'Replace Player';
	    option.setAttribute('onclick','processUserSelection(this);');
	    
	    div.append(option);

		option = document.createElement('input');
		option.type = 'button';
		option.name = 'cancel_replace_btn';
		option.id = option.name;
		option.value = 'Cancel';
		option.setAttribute('onclick','processUserSelection(this)');

	    div.append(document.createElement('br'));
	    div.append(option);

	    row.insertCell(3).appendChild(div);

		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
		
		break;
		
	case 'LOAD_UNDO':

		$('#select_event_div').empty();
		
		if(dataToProcess.events.length > 0) {

			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
			row = tbody.insertRow(tbody.rows.length);
			
			select = document.createElement('select');
			select.id = 'select_undo';
			dataToProcess.events = dataToProcess.events.reverse();
			var max_loop = dataToProcess.events.length;
			if(max_loop > 5) {
				max_loop = 5;
			}
			for(var i = 0; i < max_loop; i++) {
				option = document.createElement('option');
				option.value = dataToProcess.events[i].eventNumber;
			    option.text = dataToProcess.events[i].eventNumber + '. ' + dataToProcess.events[i].eventType;
			    select.appendChild(option);
			}
			header_text = document.createElement('label');
			header_text.innerHTML = 'Last Five Events: '
			header_text.htmlFor = select.id;
			row.insertCell(0).appendChild(header_text).appendChild(select);

		    option = document.createElement('input');
		    option.type = 'text';
		    option.name = 'number_of_undo_txt';
		    option.value = '1';
		    option.id = option.name;
		    option.setAttribute('onblur','processUserSelection(this)');
			header_text = document.createElement('label');
			header_text.innerHTML = 'Number of undos: '
			header_text.htmlFor = option.id;
			row.insertCell(1).appendChild(header_text).appendChild(option);
			
		    div = document.createElement('div');

		    option = document.createElement('input');
		    option.type = 'button';
		    option.name = 'log_undo_btn';
		    option.id = option.name;
		    option.value = 'Undo Last Event';
		    option.setAttribute('onclick','processUserSelection(this);');
		    
		    div.append(option);

			option = document.createElement('input');
			option.type = 'button';
			option.name = 'cancel_undo_btn';
			option.id = option.name;
			option.value = 'Cancel';
			option.setAttribute('onclick','processUserSelection(this)');

		    div.append(document.createElement('br'));
		    div.append(option);

		    row.insertCell(2).appendChild(div);

			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);

		} else {
			return false;
		}
		
		break;
	
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();

		header_text = document.createElement('label');
		header_text.id = 'selected_player_name';
		header_text.innerHTML = '';
		document.getElementById('select_event_div').appendChild(header_text);
		
		table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
				
		tbody = document.createElement('tbody');
		
		for(var iRow=0;iRow<=0;iRow++) {
			
			row = tbody.insertRow(tbody.rows.length);
			max_cols = 5;
			
			for(var iCol=0;iCol<=max_cols;iCol++) {
				
				cell = row.insertCell(iCol);
				
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'log_event_btn';
				
				switch (iRow) {
				case 0:
					
					switch (iCol) {
					case 0:
						option.id = 'goal';
						option.value = 'Goal';
						break;
					case 1:
						option.id = 'card';
						option.value = 'Card';
						break;
					case 2:
						option.id = 'replace';
						option.value = 'Replace';
						break;
					case 3:
						option.id = 'undo';
						option.value = 'Undo';
						break;
					case 4:
						option.id = 'overwrite';
						option.value = 'Overwrite';
						break;
					case 5:
						option.id = 'penalty';
						option.value = 'Penalty';
						break;
					case 6:
						option.name = 'cancel_event_btn';
						option.id = option.name;
						option.value = 'Cancel';
					}
					
					break;
					
				}
				
				if(option.id) {
					
					switch (option.id) {
					case 'overwrite': case 'goal': case 'card': case 'stats':
						
						option.setAttribute('data-bs-toggle', 'dropdown');
						option.setAttribute('aria-haspopup', 'true');
						option.setAttribute('aria-expanded', 'false');					
						
						div = document.createElement('div');
					    div.append(option);
					    div.className='dropdown';
					    
					    linkDiv = document.createElement('div');
					    linkDiv.id = option.id + '_div';
					    linkDiv.className='dropdown-menu';
					    linkDiv.setAttribute('aria-labelledby',option.id);

						switch (option.id) {
						case 'stats':
					
							for(var ibound=1; ibound<=8; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
								switch(ibound) {
								case 1:
								    anchor.id = 'off_side';
								    anchor.innerText = 'Off Side';
									break;
								case 2:
								    anchor.id = 'assists';
								    anchor.innerText = 'Assists';
									break;
								case 3:
								    anchor.id = 'shots';
								    anchor.innerText = 'Shots';
									break;
								case 4:
								    anchor.id = 'shots_on_target';
								    anchor.innerText = 'Shots On Target';
									break;
								case 5:
								    anchor.id = 'fouls';
								    anchor.innerText = 'Fouls';
									break;
								case 6:
								    anchor.id = 'corners';
								    anchor.innerText = 'Corners';
									break;
								case 7:
								    anchor.id = 'corners_converted';
								    anchor.innerText = 'Corners Converted';
									break;
								}
								switch(ibound){
									case 1: case 2: case 3: case 4: case 5: case 6: case 7:
										 anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFootballProcedures("LOG_EVENT",this);');
										break;
								}
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'overwrite': 
							
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    switch(ibound) {
								case 1:
								    anchor.id = 'overwrite_team_score';
								    anchor.innerText = 'Team Score';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_TEAMS_SCORE",this);');
									break;
								case 2:
								    anchor.id = 'overwrite_match_stats';
								    anchor.innerText = 'Match Stats';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_STATS",this);');
									break;
								case 3:
								    anchor.id = 'overwrite_match_substitute';
								    anchor.innerText = 'Match Subs';
								    anchor.setAttribute('onclick','addItemsToList("LOAD_OVERWRITE_MATCH_SUB",this);');
									break;
								}
							    
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'goal':
						
							for(var ibound=1; ibound<=3; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
			
							    if(ibound == 1) {
								    anchor.id = 'goal';
								    anchor.innerText = 'Goal';
							    } else if(ibound == 2) {
								    anchor.id = 'own_goal';
								    anchor.innerText = 'Own goal';
							    }else{
									anchor.id = 'penalty';
								    anchor.innerText = 'Penalty';
								}
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFootballProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
							
						case 'card':
							
							for(var ibound=1; ibound<=2; ibound++) 
							{
						    	anchor = document.createElement('a');
							    anchor.className = 'btn btn-success';
								
								if(ibound == 1) {
								    anchor.id = 'yellow';
								    anchor.innerText = 'Yellow Card';
							    } else if(ibound == 2) {
								    anchor.id = 'red';
								    anchor.innerText = 'Red Card';
							    }
							    
							    anchor.setAttribute('onclick','processWaitingButtonSpinner("START_WAIT_TIMER");processFootballProcedures("LOG_EVENT",this);');
							    anchor.style = 'display:block;';
							    linkDiv.append(anchor);
							}
							break;
						}
					    div.append(linkDiv);				    
						cell.append(div);
						break;
						
					default:
					
						option.onclick = function() {processUserSelection(this)};
						cell.appendChild(option);
						
						break;
					
					}
				}
			}
		}
			
		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);

		break;
				
	case 'LOAD_MATCH':
		
		$('#football_div').empty();
		
		if (dataToProcess)
		{	var rows = document.createElement('div');
			rows.setAttribute('class', 'row');
			var headerText = document.createElement('h6');
			headerText.id = 'match_modified_hdr';
			headerText.innerHTML = '<b>SportVUStatistic File Last Modified Time</b>';
			headerText.style.fontWeight = 'bold';
			headerText.style.fontSize = '1.3vh';
			rows.appendChild(headerText);
			document.getElementById('football_div').appendChild(rows);


			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');

			table.appendChild(tbody);
			document.getElementById('football_div').appendChild(table);

			row = tbody.insertRow(tbody.rows.length);
			header_text = document.createElement('h6');
			header_text.id = 'match_time_hdr';
			header_text.innerHTML = 'Match Time: 00:00:00';
			header_text.style.fontWeight = 'bold';
			row.insertCell(0).appendChild(header_text);
			
			header_text = document.createElement('h6');
			header_text.id = 'match_games_hdr';
			header_text.style.fontWeight = 'bold';
			header_text.innerHTML = 'GAMES : ';
			row.insertCell(1).appendChild(header_text);
			
			if(dataToProcess.events != null && dataToProcess.events.length > 0) {
				max_cols = dataToProcess.events.length;
				if (max_cols > 20) {
					max_cols = 20;
				}
				header_text = document.createElement('h6');
				for(var i = 0; i < max_cols; i++) {
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId != 0){
						dataToProcess.homeSquad.forEach(function(hs,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hs.playerId){
								playerName = ' {'+ hs.ticker_name +'}' ;
							}				
						});
						dataToProcess.awaySquad.forEach(function(as,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == as.playerId){
								playerName = ' {'+ as.ticker_name +'}';
							}				
						});
						dataToProcess.homeSubstitutes.forEach(function(hsub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == hsub.playerId){
								playerName = ' {'+ hsub.ticker_name +'}';
							}		
						});
						dataToProcess.awaySubstitutes.forEach(function(asub,index,arr){
							if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventPlayerId == asub.playerId){
								playerName = ' {'+ asub.ticker_name +'}';
							}			
						});
					}else{
						playerName = '';
					}
					
					if(dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType) {
						if(header_text.innerHTML) {
							header_text.innerHTML = header_text.innerHTML + ', ' + dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType + playerName;
						} else {
							header_text.innerHTML = dataToProcess.events[(dataToProcess.events.length - 1) - i].eventType + playerName;
						}
						
						header_text.innerHTML = header_text.innerHTML.replace("goal","G").replace("own_","O").replace("penalty","P")
							.replace("Home_Goal","HG").replace("AWAY_GOAL","AG").replace("replace","Subs");
					}
				}
				header_text.innerHTML = 'Events: ' + header_text.innerHTML;
				header_text.style.fontWeight = 'bold';
				row.insertCell(2).appendChild(header_text);
			}

			//Teams Score and other details
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			thead = document.createElement('thead');
			tr = document.createElement('tr');
			for (var j = 0; j <= 3; j++) {
			    th = document.createElement('th'); // Column
				th.scope = 'col';
			    switch (j) {
				case 0:
				    th.innerHTML = (dataToProcess.homeTeam.teamName1 + ': ' + dataToProcess.homeTeamScore) ;
				    th.style.fontSize = '2.2vh';
					break;
				case 1:
					th.innerHTML = dataToProcess.homeTeam.teamName4 + '- SUBS ' ;
					th.style.fontWeight = 'bold';
					break;
				case 2:
					th.innerHTML = dataToProcess.awayTeam.teamName1 + ': ' + dataToProcess.awayTeamScore ;
					th.style.fontSize = '2.2vh';
					break;
				case 3:
					th.innerHTML = dataToProcess.awayTeam.teamName4 + '- SUBS ' ;
					th.style.fontWeight = 'bold';
					break;
				}
			    tr.appendChild(th);
			}
			thead.appendChild(tr);
			table.appendChild(thead);
			document.getElementById('football_div').appendChild(table);
			
			tbody = document.createElement('tbody');
			max_cols = dataToProcess.homeSquad.length;
			if(dataToProcess.homeSquad.length < dataToProcess.awaySquad.length) {
				max_cols = dataToProcess.awaySquad.length;
			}else if(dataToProcess.homeSquad.length < dataToProcess.homeSubstitutes.length){
				max_cols = dataToProcess.homeSubstitutes.length;
			}else if(dataToProcess.homeSquad.length < dataToProcess.awaySubstitutes.length){
				max_cols = dataToProcess.awaySubstitutes.length;
			}
			for(var i = 0; i <= max_cols - 1; i++) {
				row = tbody.insertRow(tbody.rows.length);
				for(var j = 1; j <= 4; j++) {
					anchor = document.createElement('a');
					text = document.createElement('text');
					switch(j){
					case 1:
						if(i < dataToProcess.homeSquad.length){
							anchor.name = 'homePlayers';
							anchor.id = 'homePlayer_' + dataToProcess.homeSquad[i].playerId;
							anchor.style.fontWeight = 'bold';
							anchor.value = dataToProcess.homeSquad[i].playerId;
							if(getPlayerMatchStats(dataToProcess.homeSquad[i].playerId) == ''){
								anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].ticker_name ;
							}else{
								anchor.innerHTML = dataToProcess.homeSquad[i].jersey_number + ': ' + dataToProcess.homeSquad[i].ticker_name;
								anchor.style.color = getPlayerMatchStats(dataToProcess.homeSquad[i].playerId).split(',')[0];
							}
						}
						break;
					case 2:
						if(i < dataToProcess.homeSubstitutes.length){
							text.name = 'homeSubstitutes';
							text.id = 'homeSubstitute_' + dataToProcess.homeSubstitutes[i].playerId;
							text.value = dataToProcess.homeSubstitutes[i].playerId;
							text.style.fontWeight = 'bold';
							if(getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId) == ''){
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].ticker_name;
							}else{
								text.innerHTML = dataToProcess.homeSubstitutes[i].jersey_number + ': ' + dataToProcess.homeSubstitutes[i].ticker_name;
								text.style.color = getPlayerMatchStats(dataToProcess.homeSubstitutes[i].playerId).split(',')[0];
							}
						}
						break;
						
					case 3:
						if(i < dataToProcess.awaySquad.length){
							anchor.name = 'awayPlayers';
							anchor.style.fontWeight = 'bold';
							anchor.id = 'awayPlayer_' + dataToProcess.awaySquad[i].playerId;
							anchor.value = dataToProcess.awaySquad[i].playerId;
							if(getPlayerMatchStats(dataToProcess.awaySquad[i].playerId) == ''){
								anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].ticker_name ;
							}else{
								anchor.innerHTML = dataToProcess.awaySquad[i].jersey_number + ': ' + dataToProcess.awaySquad[i].ticker_name;
								anchor.style.color = getPlayerMatchStats(dataToProcess.awaySquad[i].playerId).split(',')[0];
							}
						}
						break;
					case 4:
						if(i < dataToProcess.awaySubstitutes.length){
							text.name = 'awaySubstitutes';
							text.style.fontWeight = 'bold';
							text.id = 'awaySubstitute_' + dataToProcess.awaySubstitutes[i].playerId;
							text.value = dataToProcess.awaySubstitutes[i].playerId;
							if(getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId) == '') {
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].ticker_name;
							}else{
								text.innerHTML = dataToProcess.awaySubstitutes[i].jersey_number + ': ' + dataToProcess.awaySubstitutes[i].ticker_name;
								text.style.color = getPlayerMatchStats(dataToProcess.awaySubstitutes[i].playerId).split(',')[0];
							}
						}
						break;
					}
					switch(j){
						case 1: case 3:
							anchor.setAttribute('onclick','processUserSelection(this);');
							//anchor.setAttribute('style','cursor: pointer;');
							row.insertCell(j - 1).appendChild(anchor).appendChild(text);
							break;
						case 2: case 4:
							row.insertCell(j - 1).appendChild(text);
							break;
					}
				}
			}								
			table.appendChild(tbody);
			document.getElementById('football_div').appendChild(table);
			
		}
		break;
	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	
function getPlayerNameById(playerId, squad, substitutes) {
    // Check the squad first
    let player = squad.find(p => p.playerId == playerId);
    if (player) {
        return player.ticker_name+"["+player.jersey_number+"]";
    }

    player = substitutes.find(p => p.playerId == playerId);
    return player ? player.ticker_name+"["+player.jersey_number+"]" : null; 
}
function dataset(dataToProcess) {	
    $("#check_data_div").empty();	
    let table = document.createElement('table');
    table.setAttribute('class', 'table table-bordered');
    table.style.width = '50%';        
    let tbody = document.createElement('tbody');
    table.appendChild(tbody);

    let headerRow = document.createElement('tr');
    let headerText = document.createElement('th');
    headerText.colSpan =3;
    headerText.setAttribute('class', 'table thead-dark');
    headerText.innerHTML = 'MATCH STATS';
    headerRow.appendChild(headerText);
    tbody.appendChild(headerRow);
    
    let header = document.createElement('tr');
    let cell1 = document.createElement('td');
    let cell2 = document.createElement('td');
    let cell3 = document.createElement('td');
    //cell1.innerHTML = "<b>HOME</b>";
    //cell2.innerHTML = "<b>STATS</b>";
    //cell3.innerHTML = "<b>AWAY</b>";
    header.appendChild(cell1);
    header.appendChild(cell2);
    header.appendChild(cell3);
    tbody.appendChild(header); 
    
    dataToProcess.forEach(function(caption) {
        let row = document.createElement('tr');
        let cell1 = document.createElement('td');
        let cell2 = document.createElement('td');
        let cell3 = document.createElement('td');
        let parts = caption.split(",");
        cell1.innerHTML = parts[0]; 
        cell2.innerHTML = parts[1]; 
        cell3.innerHTML = parts[2]; 
        row.appendChild(cell1);
        row.appendChild(cell2);
        row.appendChild(cell3);
        tbody.appendChild(row);
    });

    document.getElementById("check_data_div").appendChild(table);
    $("#check_data_div").show();
}

   
