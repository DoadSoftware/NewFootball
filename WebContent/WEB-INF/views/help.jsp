<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Help</title>

	<link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.3/css/bootstrap.min.css'/>">
	<link rel="stylesheet" href="<c:url value='/webjars/font-awesome/6.5.2/css/all.min.css'/>">
	<link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">

	<script type="text/javascript" src="<c:url value='/webjars/jquery/3.7.1/jquery.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/html2canvas/1.4.1/dist/html2canvas.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/resources/javascript/index.js'/>"></script>

    <style type="text/css">
        .nav-link {
            font-weight: bold;
        }
        .screenshot-section {
            padding: 20px;
            border: 1px solid #ccc;
            margin: 20px;
        }
    table {
      width: 100%;
      border-collapse: collapse;
      background-color: #f9f9f9; /* Light background color for the table */
      color: #333; /* Dark text color */
    }

    th, td {
      padding: 12px;
      text-align: center;
      border: 1px solid #ddd;
      font-family: Arial, sans-serif;
      font-weight: bold; /* Bold text */
    }

    /* Styling for the first "CAPTION" column (darker blue) */
    td:nth-child(1),td:nth-child(4),
    td:nth-child(7),td:nth-child(10) {
      background-color: #99c2ff; /* Darker light blue background */
      color: #003366; /* Dark blue text */
    }

    /* Styling for the second "CAPTION" column (darker green) */
    td:nth-child(2),td:nth-child(5),
    td:nth-child(8), td:nth-child(11) {
      background-color: #a8e6a1; /* Darker light green background */
      color: #155724; /* Dark green text */
    }

    /* Styling for the third "CAPTION" column (darker red) */
    td:nth-child(3), td:nth-child(6),
    td:nth-child(9), td:nth-child(12) {
      background-color: #f5c6cb; /* Darker light red background */
      color: #721c24; /* Dark red text */
    }
    </style>
</head>
<body>
    <div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">    
        <div class="container">
            <ul class="nav nav-pills mb-3" id="pills-tab" role="tablist">
                 <li class="nav-item" role="presentation">
 					<button class="nav-link active" id="pills-function-keys-tab" data-bs-toggle="pill" data-bs-target="#pills-function-keys" 
                            type="button" role="tab" aria-CONTROLs="pills-function-keys" aria-selected="true">SCOREBUG</button>
                </li>
                <li class="nav-item" role="presentation">
					<button class="nav-link" id="pills-letters-tab" data-bs-toggle="pill" data-bs-target="#pills-letters" 
                            type="button" role="tab" aria-CONTROLs="pills-letters" aria-selected="false">FULL FRAMES</button>
                </li>
                <li class="nav-item" role="presentation">
					<button class="nav-link" id="pills-CONTROL-tab" data-bs-toggle="pill" data-bs-target="#pills-CONTROL" 
                            type="button" role="tab" aria-CONTROLs="pills-CONTROL" aria-selected="false">LOWER THIRD</button>
                </li>
                <li class="nav-item" role="presentation">
					<button class="nav-link" id="pills-lof-keys-tab" data-bs-toggle="pill" data-bs-target="#pills-lof-keys" 
                            type="button" role="tab" aria-CONTROLs="pills-bug" aria-selected="false">LOF/BUGS</button>
                </li>
                <li class="nav-item" role="presentation">
					<button class="nav-link" id="pills-lband-keys-tab" data-bs-toggle="pill" data-bs-target="#pills-lband-keys" 
                            type="button" role="tab" aria-CONTROLs="pills-lband-keys" aria-selected="false">LBAND</button>
                </li>
               <!--  <li class="nav-item" role="presentation">
					<button class="nav-link" id="pills-mini-keys-tab" data-bs-toggle="pill" data-bs-target="#pills-mini-keys" 
                            type="button" role="tab" aria-CONTROLs="pills-mini-keys" aria-selected="false">MINI'S</button>
                </li> -->
                
            </ul>
            <div class="tab-content" id="pills-tabContent">
                <div class="tab-pane fade show active" id="pills-function-keys" role="tabpanel" aria-labelledby="pills-function-keys-tab">
            		 <button class="screenshot-button" onclick="takeScreenshot('infoTable', 'INFOBAR')">Take Screenshot</button>
                  <br><br>
	             <div class="screenshot-section" id="infoTable">       
		            <table border="1">
				  <thead>
				    <tr>
				      <th>CAPTION</th><th>IN</th><th>OUT</th>
				      <th>CAPTION</th><th>IN</th><th>OUT</th>
				      <th>CAPTION</th><th>IN</th><th>OUT</th>
				      <th>CAPTION</th><th>IN</th><th>OUT</th>
				    </tr>
				  </thead>
				  <tbody>
				    <tr>
				      <td>SCROEBUG</td><td>F1</td> <td>=</td>
				      <td>MATCH STATS DROPDOWN</td><td>I </td><td>O</td>
				      <td>SUBSTITUTION</td><td>M</td><td>O</td>
				      <td>PLAYER NAME & CARD</td> <td>K</td> <td>O</td>
				    </tr>
				    <tr>
				      <td>PROMO</td><td>N</td><td>O</td>
				      <td>MATCH STATS DATA FROM API</td><td>J</td> <td>O</td>
				      <td>HEAT MAP/Player Stats</td><td>R</td> <td>O</td>
				      <td>TEAM STATS</td><td>B</td><td>O</td>
				    </tr>
				    <tr>
				      <td>HEAD TO HEAD</td> <td>ALT+J</td><td>O</td>
				      <td>EXTRA TIME TEXT</td><td>V</td><td>V</td>
				      <td>SHOW RED CARD</td> <td>Z</td><td>X</td>
				      <td>INJURY TIME</td> <td>E</td><td>C</td>
				    </tr>
				    <tr>
				      <td>SPONSOR</td> <td>S</td><td>S</td>
				      <td>FLAG</td><td>ALT+G</td><td>ALT+G</td>
				      <td>PENALTY<br>Change</td><td>Y<br>U</td> <td></td>
				    </tr>
				  </tbody>
				</table>
		            
	            	</div>
           		</div>
           		<div class="tab-pane fade" id="pills-letters" role="tabpanel" aria-labelledby="pills-letters-tab">
				      <button class="screenshot-button" onclick="takeScreenshot('infoTable1', 'FULLFRAME')">Take Screenshot</button>				      
                  		<br><br> 
                  	<div class="screenshot-section" id="infoTable1">  
                  	<table>
					  <thead>
					   <tr>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					    </tr>
					    <tr>
					      <td>FF MATCH IDENT</td><td>F4</td><td></td>
					      <td>FF GROUPS</td> <td>G</td><td></td>
					      <td>FF PLAYING XI CHANGE ON</td> <td>F5</td><td></td>
					      <td>FF FIXTURES</td><td>F</td><td></td>
					    </tr>
					  </thead>
					  <tbody>
					    <tr>
					   	  <td>FF POINTS TABLE</td><td>P</td><td></td>
					      <td>FF SINGLE MATCH PROMO</td> <td>Q</td><td></td>
					      <td>FF DOUBLE MATCH IDENT/PROMO</td> <td>W</td><td></td>
					      <td>FF ADDITIONAL POINT TABLE</td><td>ALT+P</td><td></td>
					    </tr>
					    <tr>
					      <td>FF PLAY OFF TREE</td> <td>F11</td><td></td>
					      <td>FF ROAD TO FINAL</td> <td>L</td><td></td>
					      <td>FF MATCH STATS</td> <td>F8</td><td></td>
					      <td>HEAD TO HEAD</td> <td>ALT+D</td><td></td>
					    </tr>
					    <tr>
					      <td>FF-TOURNAMENT_STATS(Txt File)</td> <td>Shift_F8</td><td></td>
					      <td>FF-TEAM_COMPARISON(API)</td> <td>Alt_F8</td><td></td>
					    </tr>
					    <tr>
					  </tbody>
					</table>	
		            </div>
				  </div>
				  <div class="tab-pane fade" id="pills-CONTROL" role="tabpanel" aria-labelledby="pills-CONTROL-tab">
		        	 <button class="screenshot-button" onclick="takeScreenshot('infoTable2', 'LOWERTHIRDS')">Take Screenshot</button>
                  		<br><br>
                  	<div class="screenshot-section" id="infoTable2">
                  	<table>
					  <thead>
					    <tr>
					     <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					      <th>CAPTION</th><th>IN</th><th>OUT</th>
					    </tr>
					  </thead>
					  <tbody>
					  <tr>
					      <td>MATCH OFFICIALS</td> <td>T</td><td></td>
					      <td>LT MATCH IDENT</td><td>A</td><td></td>
					      <td>NAME SUPER PLAYER</td><td>F3</td><td></td>
					      <td>NAME SUPER DATABASE</td><td>F2</td><td></td>
					    </tr>
					    <tr>
					      <td>SCORELINE</td> <td>F7</td><td></td>
					      <td>PLAYER CARD - YELLOW/RED/2 YELLOWS - RED</td> <td>F9</td><td></td>
					      <td>SUBSTITUTIONS</td><td>F10</td><td></td>
					      <td>TEAM STAFFS - DATABASE</td> <td>F12</td><td></td>
					    </tr>
					     <tr>
					      <td>PLAYER PROFILE</td> <td>ALT+E</td><td></td>
					    </tr>
					  </tbody>
					</table>
				</div>
              </div>
		        <div class="tab-pane fade" id="pills-lof-keys" role="tabpanel" aria-labelledby="pills-lof-keys-tab">
		        	 <button class="screenshot-button" onclick="takeScreenshot('infoTable5', 'LOF')">Take Screenshot</button>
                 	 <br><br>
                 	 <div class="screenshot-section" id="infoTable5">
			        <table>
						  <thead>
						    <tr>
						     <tr>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						    </tr>
						  </thead>
						  <tbody>
						    <tr>
						     <td>TEAM TOP STATS</td><td>ALT+S</td><td></td>
						      <td>HEAT MAP</td><td>ALT+H</td><td></td>
						      <td>BUG - DATABASE</td><td>F6</td><td></td>
						     <td>HIGHLIGHT SCORE BUG</td> <td>ALT+F6</td><td></td>						      
						    </tr>
						    <tr>
						     <td>LINE-UP/FORMATION</td> <td>ALT+F</td><td></td>
						      <td>TEAM FIXTURES</td> <td>D</td><td></td>
						      <td>LEADERBOARD</td><td>ALT+B</td><td></td>
						      <td>VERTICAL FLIPPER</td><td>ALT+V</td><td></td>
						    </tr>
						  </tbody>
						</table>
			          </div>
		        </div>
            	<div class="tab-pane fade" id="pills-lband-keys" role="tabpanel" aria-labelledby="pills-lband-keys-tab">        		      
            	<button class="screenshot-button" onclick="takeScreenshot('infoTable5', 'LBAND')">Take Screenshot</button>
                 	 <br><br>
                 	 <div class="screenshot-section" id="infoTable5">
						<table>
						  <thead>
						    <tr>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						      <th>CAPTION</th><th>IN</th> <th>OUT</th>
						      <th>CAPTION</th><th>IN</th><th>OUT</th>
						      <th>CAPTION</th><th>IN</th> <th>OUT</th>
						    </tr>
						  </thead>
						  <tbody>
						    <tr>
						      <td>H2H WITH LAST 3 MATCH RESULTS</td><td>CONTROL+F1</td> <td></td>
						      <td>LIVE WIN PROBABILITY 3 MATCH RESULTS</td><td>CONTROL+F2</td><td></td>
						      <td>INSIGHTS WITH TEAM MATCH STATS</td> <td>CONTROL+F3</td><td></td>
						      <td>RIGHT SPONSOR IN</td> <td>CONTROL+F8</td> <td></td>
						    </tr>
						    <tr>
						      <td>RIGHT SPONSOR OUT</td><td>CONTROL+F9</td><td></td>
						      <td>BOTTOM SPONSOR IN</td><td>CONTROL+F10</td> <td></td>
						      <td>BOTTOM SPONSOR OUT</td><td>CONTROL+F11</td><td></td>
						      <td>EXPECTED GOALS WITH GOAL SCORERS</td> <td>CONTROL+G</td><td></td>
						    </tr>
						    <tr>
						      <td>CORNERS</td> <td>CONTROL+V</td> <td></td>
						      <td>INSIGHTS WITH SHOT TAKERS</td><td>CONTROL+S</td><td></td>
						      <td>INSIGHTS WITH GOAL SCORERS</td> <td>CONTROL_F4</td> <td></td>
						      <td>LIVE WIN PROBABILITY WITH LAST 3 MATCH RESULTS</td><td>CONTROL+I</td> <td></td>
						    </tr>
						    <tr>
						      <td>FOULS COMMITTED</td><td>CONTROL+F</td> <td></td>
						      <td>ACCURATE PASS</td><td>CONTROL+A</td><td></td>
						      <td>TACKLES</td><td>CONTROL_F5<b style="background-color: white;">SUPER CUP</b></td><td></td>
						      <td>CLEARANCES</td><td>CONTROL+D</td> <td></td>
						    </tr>
						    <tr>
						      <td>SET PIECES</td><td>CONTROL+P</td><td></td>
						      <td>SHOOTING ACCURACY</td><td>CONTROL+H</td> <td></td>
						      <td>DUELS</td> <td>CONTROL+B</td> <td></td>
						      <td>DB FREE TEXT WITH TEAM STATS</td> <td>CONTROL+E</td> <td></td>
						    </tr>
						    <tr>
						      <td>DRIBBLES</td> <td>CONTROL+M</td><td></td>
						      <td>H2D WITH LIVE WIN PROBABILITY</td><td>CONTROL+U</td><td></td>
						      <td>DB PLAYER SELECT</td> <td>CONTROL+Q</td> <td></td>
						      <td>EXPECTED GOAL FOR AND AGAINST MATCH WITH GOAL SCORERS</td> <td>CONTROL+Y</td> <td></td>
						    </tr>
						    <tr>
						      <td>PLAYER RATING</td> <td>CONTROL+J</td> <td></td>
						      <td>INSIGHTS WITH LAST 3 MATCH RESULTS</td> <td>CONTROL+K</td> <td></td>
							  <td>ATTACKING ZONE</td> <td>CONTROL+F12</td> <td></td>
						      <td>TOUCHES</td><td>CONTROL+Z<b style="background-color: white;">SUPER CUP</b></td><td></td>
						    </tr>
						    <tr>
						      <td>POSS WON ATT 3RD</td> <td>CONTROL+C</td><td></td>
						      <td>FINAL THIRD PASSES</td> <td>CONTROL+X</td> <td></td>
						    </tr>
						  </tbody>
						</table>

			          </div>
		        </div>
		        <div class="tab-pane fade" id="pills-mini-keys" role="tabpanel" aria-labelledby="pills-mini-keys-tab">        		      
            	<button class="screenshot-button" onclick="takeScreenshot('infoTable5', 'MINI')">Take Screenshot</button>
                 	 <br><br>
                 	 <div class="screenshot-section" id="infoTable5">

			          </div>
		        </div>
            </div>
           </div>
           </div>
     <script>
     function takeScreenshot(tableId, headerText) {
    	    var element = document.getElementById(tableId);
    	    if (!element) {
    	        console.error(`Element with ID "${tableId}" not found.`);
    	        return;
    	    }

    	    html2canvas(element).then(function(canvas) {
    	        var imgData = canvas.toDataURL('image/png');
    	        var link = document.createElement('a');
    	        link.href = imgData;
    	        var fileName = headerText.trim() !== '' ? headerText+'.png' : 'screenshot.png';
                link.download = fileName;
    	        document.body.appendChild(link);
    	        link.click();
    	        document.body.removeChild(link);
    	    }).catch(function(error) {
    	        console.error('Error capturing screenshot:', error);
    	    });
    	}

 </script>

</body>
</html>