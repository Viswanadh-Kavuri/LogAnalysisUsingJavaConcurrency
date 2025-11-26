package com.visu.java.disruptor;

import java.util.HashMap;
import java.util.Map;

import com.lmax.disruptor.EventHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatsEventHandler implements EventHandler<DisruptorLineEvent> {

	private int globalMax = Integer.MIN_VALUE;
	private final Map<String, Integer> requestsPerUser = new HashMap<>();
	private final Map<String, Integer> maxResponseTimePerEndpoint = new HashMap<>();
	
	@Override
	public void onEvent(DisruptorLineEvent event, long sequence, boolean endOfBatch) throws Exception {
		String line = event.getLine();
		if(line==null) return;
		String[] parts = line.split(",");
		if(parts.length!=4) return;
		int currentResponseTime = Integer.parseInt(parts[3]);
		globalMax=Integer.max(globalMax, currentResponseTime);
		requestsPerUser.merge(parts[1], 1, Integer::sum);
		maxResponseTimePerEndpoint.merge(parts[2], currentResponseTime, Integer::max);
		
		
		/*
		 * if (sequence % 1_000_000 == 0) { log.info("{}% completed", ((double)
		 * (sequence*100)/1_000_000_000)); }
		 */
		 		 
	}
	
	public int getGlobalMaxResponseTime() {
		return globalMax;
	}
	
	public Map<String, Integer> getRequestsPerUser(){
		return requestsPerUser;
	}
	
	public Map<String, Integer> getMaxResponseTimePerEndpoint() {
		return maxResponseTimePerEndpoint;
	}

}
