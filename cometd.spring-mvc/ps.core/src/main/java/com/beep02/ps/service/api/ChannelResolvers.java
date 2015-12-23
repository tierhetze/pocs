package com.beep02.ps.service.api;


/**

@author Vladimir Nabokov

defines types of channel resolvers:

channel resolver must contain a map:

service name - [array of service channels]

basically each service (of user or of system level) must have it's channels resolver

 **/

public enum ChannelResolvers {
	
	DB,    /*Data base is a source*/
	STATIC /*source - xml files in service project*/

}
