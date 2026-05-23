package com.football.containers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.football.util.*;

public class Scene {
	
	private String scene_path;
	private String broadcaster;
	private String which_layer;
	
	public Scene() {
		super();
	}

	public Scene(String scene_path, String which_layer) {
		super();
		this.scene_path = scene_path;
		this.which_layer = which_layer;
	}
	
	public String getScene_path() {
		return scene_path;
	}

	public void setScene_path(String scene_path) {
		this.scene_path = scene_path;
	}
	
	public String getBroadcaster() {
		return broadcaster;
	}

	public void setBroadcaster(String broadcaster) {
		this.broadcaster = broadcaster;
	}

	public String getWhich_layer() {
		return which_layer;
	}

	public void setWhich_layer(String which_layer) {
		this.which_layer = which_layer;
	}

	public void scene_load(PrintWriter print_writer, String broadcaster) throws InterruptedException, IOException
	{
		switch (broadcaster.toUpperCase()) {
		case FootballUtil.I_LEAGUE: case FootballUtil.SANTOSH_TROPHY:
			switch(this.which_layer) {
			case FootballUtil.ONE:
				//System.out.println("Secne : " + this.scene_path);
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case FootballUtil.TWO:
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;
		case FootballUtil.VIZ_SANTOSH_TROPHY: case FootballUtil.VIZ_TRI_NATION: case FootballUtil.SUPER_CUP:
			switch(this.which_layer.toUpperCase()) {
			case FootballUtil.FRONT_LAYER:
				print_writer.println("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE \0");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case FootballUtil.MIDDLE_LAYER:
				print_writer.println("-1 RENDERER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE \0");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case FootballUtil.BACK_LAYER:
				print_writer.println("-1 RENDERER*BACK_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*BACK_LAYER*SCENE_DATA INITIALIZE \0");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;
		case FootballUtil.EURO_LEAGUE:
			switch(this.which_layer.toUpperCase()) {
			case FootballUtil.FRONT_LAYER:
				print_writer.println("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*All$In SHOW 0\0");
				//print_writer.println("-1 RENDERER*FRONT_LAYER INITIALIZE \0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE \0");
				//print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 1");
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Right$Sponsor$In SHOW 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Bottom$Sponsor$In SHOW 0\0");
				break;
			case FootballUtil.MIDDLE_LAYER:
				print_writer.println("-1 RENDERER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				//print_writer.println("-1 RENDERER INITIALIZE \0");
				print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE \0");
				//print_writer.println("-1 RENDERER*UPDATE SET 1");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;	
			
		}
	}
	public void scene_load(List<PrintWriter> print_writer, String broadcaster) throws InterruptedException, IOException
	{
		switch (broadcaster.toUpperCase()) {
		case FootballUtil.SUPER_CUP:
			switch(this.which_layer.toUpperCase()) {
			case FootballUtil.FRONT_LAYER:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE \0", print_writer);
				
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case FootballUtil.MIDDLE_LAYER:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER SET_OBJECT SCENE*" + this.scene_path + "\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*STAGE SHOW 0.0 \0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*SCENE_DATA INITIALIZE \0", print_writer);
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case FootballUtil.BACK_LAYER:
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0", print_writer);
				FootballFunctions.DoadWriteCommandToAllViz("-1 RENDERER*BACK_LAYER*SCENE_DATA INITIALIZE \0", print_writer);
				
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;
		}
	}
}
