package com.beep02.ps.service.user.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cometd.bayeux.server.ServerSession.Extension;


/**

@author Vladimir Nabokov

 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserServiceLevelExtensions {
	Class<? extends Extension> [] value();
}
