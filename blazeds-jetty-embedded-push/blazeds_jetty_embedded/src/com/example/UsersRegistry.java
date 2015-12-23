package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UsersRegistry {
	List<Long> users = new ArrayList<>();
	AtomicLong userId = new AtomicLong();
	public Long addNew() {
		long newUser = userId.incrementAndGet();
		return newUser;
	}
}
