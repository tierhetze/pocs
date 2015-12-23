package com.beep02.ps.datastream.api;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSON.Output;

/**
Base class for any message that sent by comet-d channels
@author Vladimir Nabokov
@since 11/06/2013
**/
@SuppressWarnings("all")
public abstract class PushData implements Cloneable, JSON.Convertible {

	/**
	* channel type, e.g. price/eurusd
	*               or   weather
	*               or   deals/usdjpy
	*/
	private String type;
	private long sent;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	@Override
	public void toJSON(Output out) {
		out.add("type", type);
		out.add("sent", sent);
	}
	
	

	@Override
	public void fromJSON(Map object) {}
}
