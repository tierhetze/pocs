package com.beep02.ps.datastream.api;

import com.beep02.ps.service.system.api.StreamDataSourceObserver;


/**

@author Vladimir Nabokov

 **/

public interface ObservableStreamDataSource {
	public void addObserver(StreamDataSourceObserver observer);
}
