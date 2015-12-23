package com.beep02.ps.service.user.impl;

import org.cometd.annotation.Service;

import com.beep02.ps.service.user.api.UserService;
import com.beep02.ps.service.user.api.UserServiceLevelThreadPool;


/**
 * User level price service
 * 
 * @author Vladimir Nabokov
 * @since 11/06/2013
 *
 */
@javax.inject.Named
@javax.inject.Singleton
@UserServiceLevelThreadPool(coreSize=10)
@Service("user-price-service")
public class UserPriceService extends UserService{}
