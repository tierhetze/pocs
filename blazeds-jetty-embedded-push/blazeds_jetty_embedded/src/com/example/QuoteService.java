package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class QuoteService {
	
	
	private PushService pushService;
	
	Map<Long, Thread> subscribtions = new ConcurrentHashMap<Long, Thread>();
	
    private static final String[] MASTER_LIST = { "C", "FNM", "FRE", "F",
			"GOOG", "AIG", "CSCO", "MSFT", "AAPL", "YHOO", "BSX", "PORT", "F",
			"TNT", "ESP", "RET", "VBN", "EES" };
    
    private DataTypes dataType          = DataTypes.quotes;
	
	public QuoteService() {
		
	}
	
    public void startSubscribtion(final Long userId) {
		//emulates quotes
		Thread doSend = new Thread() {
            public void run() {
                while (true) {
                	
                	List<StockQuote> list = refillListOfQuotes();
                	pushService.sendMessage(userId, dataType.name(), list);
					try {Thread.sleep(2000);} catch (InterruptedException e) {}
					
					if(subscribtions.get(userId) == null){
					    break;	
					}
					
				}
			}
        };
        doSend.start();
        subscribtions.put(userId, doSend);
	}

	public void stopSubscribtion(Long userId) {
		subscribtions.remove(userId);
	}
    
    
	private List<StockQuote> refillListOfQuotes() {
		final List<StockQuote> list = new ArrayList<StockQuote>();
		Random r = new Random();
		list.clear();
		for (String s : MASTER_LIST) {
			StockQuote sq = new StockQuote();
			sq.setName(s);
			sq.setPrice(r.nextInt(50));
			list.add(sq);
		}
		return list;
	}

	public void setPushService(PushService pushService) {
		this.pushService = pushService;
	}

	
	
	
	
}
