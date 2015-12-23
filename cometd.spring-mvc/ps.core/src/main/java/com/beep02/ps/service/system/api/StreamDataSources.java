package com.beep02.ps.service.system.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.beep02.ps.datastream.api.ObservableStreamDataSource;


/**

@author Vladimir Nabokov

 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StreamDataSources {
	
	Class<? extends ObservableStreamDataSource> [] value();

}
