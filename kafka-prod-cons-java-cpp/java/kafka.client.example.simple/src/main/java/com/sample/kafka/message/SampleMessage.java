package com.sample.kafka.message;

import java.io.Serializable;


public class SampleMessage implements Serializable{
    private static final long serialVersionUID = 6549965722392022234L;
    
	private int prop1;
	private String prop2;
	
	public int getProp1() {
		return prop1;
	}
	public void setProp1(int prop1) {
		this.prop1 = prop1;
	}
	public String getProp2() {
		return prop2;
	}
	public void setProp2(String prop2) {
		this.prop2 = prop2;
	}
	@Override
	public String toString() {
		return "SampleMessage [prop1=" + prop1 + ", prop2=" + prop2 + "]";
	}
}
