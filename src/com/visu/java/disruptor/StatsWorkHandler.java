package com.visu.java.disruptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;

public class StatsWorkHandler implements WorkHandler<DisruptorLineEvent> {
	private AtomicInteger globalMax = new AtomicInteger(Integer.MIN_VALUE);
	private final ConcurrentHashMap<String, Integer> requestsPerUser = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Integer> maxResponseTimePerEndpoint = new ConcurrentHashMap<>();

	@Override
	public void onEvent(DisruptorLineEvent event) throws Exception {
		
		String line = event.getLine();
		if(line==null) return;
		
		String[] parts = line.split(",");
		if(parts.length!=4) return;
		
		int currentResponseTime = Integer.parseInt(parts[3]);
		globalMax.accumulateAndGet(currentResponseTime, Integer::max);
		requestsPerUser.merge(parts[1], 1, Integer::sum);
		maxResponseTimePerEndpoint.merge(parts[2], currentResponseTime, Integer::max);
		
	}
	
	public int getGlobalMaxResponseTime() {
		return globalMax.get();
	}
	
	public Map<String, Integer> getRequestsPerUser(){
		return requestsPerUser;
	}
	
	public Map<String, Integer> getMaxResponseTimePerEndpoint() {
		return maxResponseTimePerEndpoint;
	}


}
