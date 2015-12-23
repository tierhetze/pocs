package com.beep02.ps.service.user.impl;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import com.beep02.ps.datastream.api.PushData;


/**
@author Vladimir Nabokov
@since 11/06/2013
 **/

public class WeatherPushData extends PushData{
	
	
	String town;
	String temperature;
	String air;
	
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getAir() {
		return air;
	}
	public void setAir(String air) {
		this.air = air;
	}
	@Override
	public void toJSON(Output out) {
		super.toJSON(out);
		out.add("town",town);
		out.add("temperature",temperature);
		out.add("air",air);
	}
	@Override
	public void fromJSON(Map object) {
		// TODO Auto-generated method stub
		super.fromJSON(object);
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	

}
