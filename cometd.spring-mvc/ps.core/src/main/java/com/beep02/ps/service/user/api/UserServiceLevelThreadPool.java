package com.beep02.ps.service.user.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**

@author Vladimir Nabokov

 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserServiceLevelThreadPool {
	int coreSize();
}
