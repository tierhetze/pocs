package com.beep02.ps.service.user.impl;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import com.beep02.ps.datastream.api.PushData;




/**

Price data per pair

@author Vladimir Nabokov
@since 11/06/2013
 **/

public class LastDealsPushData extends PushData{
	
	private String pair;
	private List<DealPushData> deals;
	
	public LastDealsPushData(){}
	
	
	@Override
	public void toJSON(Output out) {
		super.toJSON(out);
		out.add("pair",pair);
		out.add("deals",deals);
	}

	@Override
	@SuppressWarnings("all")
	public void fromJSON(Map object) {
	}
	
	public LastDealsPushData clone()
    {
        try
        {
            return (LastDealsPushData)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }


	public List<DealPushData> getDeals() {
		return deals;
	}


	public void setDeals(List<DealPushData> deals) {
		this.deals = deals;
	}


	public String getPair() {
		return pair;
	}


	public void setPair(String pair) {
		this.pair = pair;
	}
	

}
