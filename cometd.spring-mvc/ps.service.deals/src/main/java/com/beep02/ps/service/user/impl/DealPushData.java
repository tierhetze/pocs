package com.beep02.ps.service.user.impl;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import com.beep02.ps.datastream.api.PushData;


/**

@author Vladimir Nabokov

 **/

public class DealPushData extends PushData{

	double amount;
	String pair;
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPair() {
		return pair;
	}
	public void setPair(String pair) {
		this.pair = pair;
	}
	public DealPushData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DealPushData(double amount, String pair) {
		super();
		this.amount = amount;
		this.pair = pair;
	}
	@Override
	public void toJSON(Output out) {
		super.toJSON(out);
		out.add("amount",amount);
        out.add("pair",pair);
    }
	@Override
	@SuppressWarnings("all")
	public void fromJSON(Map object) {
		super.fromJSON(object);
	}
	
	@Override
	public DealPushData clone()
    {
        try
        {
            return (DealPushData)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
	
}
