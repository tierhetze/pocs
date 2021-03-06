package com.beep02.ps.service.system.impl;

import org.cometd.annotation.Service;

import com.beep02.ps.datastream.impl.WeatherSource;
import com.beep02.ps.service.system.api.StreamDataSources;
import com.beep02.ps.service.system.api.SystemService;


/**

service to construct data for system level price channels

@author Vladimir Nabokov
@since 11/06/2013

**/
@javax.inject.Named
@javax.inject.Singleton
@Service("system-weather-service")
@StreamDataSources(WeatherSource.class)
public class WeatherSystemService extends SystemService{}
