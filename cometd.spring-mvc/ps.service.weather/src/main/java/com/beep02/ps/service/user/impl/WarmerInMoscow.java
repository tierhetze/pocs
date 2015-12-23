package com.beep02.ps.service.user.impl;

import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerMessage.Mutable;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerSession.Extension;


/**
Example of a service-wide extension
That able to modify data , streaming to the client
@author Vladimir Nabokov

 **/

public class WarmerInMoscow implements Extension{

	@Override
	public boolean rcv(ServerSession session, Mutable message) {
		
		WeatherPushData data = (WeatherPushData)message.getData();
		if(data.town.equals("moscow")){
			//
			data.temperature="+50C";
		}
		return true;
	}

	@Override
	public boolean rcvMeta(ServerSession session, Mutable message) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ServerMessage send(ServerSession to, ServerMessage message) {
		// TODO Auto-generated method stub
		return message;
	}

	@Override
	public boolean sendMeta(ServerSession session, Mutable message) {
		// TODO Auto-generated method stub
		return true;
	}
	
}
