package com.example;

import java.io.Serializable;

public class StockQuote implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -862406111221334901L;
	private String name;
    private int price;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}
