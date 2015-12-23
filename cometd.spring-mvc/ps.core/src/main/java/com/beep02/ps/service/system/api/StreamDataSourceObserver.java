package com.beep02.ps.service.system.api;

import com.beep02.ps.datastream.api.PushData;


/**

@author Vladimir Nabokov

 **/

public interface StreamDataSourceObserver {
	
	/**
	 * A data source notifies me, that I have a data chunk
	 * I need publish that chunk to the channel
	 * @param data - to publish in a channel
	 */
	public void notify(final PushData data);

}
