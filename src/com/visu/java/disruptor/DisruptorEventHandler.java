package com.visu.java.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventHandler;

public class DisruptorEventHandler implements EventHandler<DisruptorLineEvent> {
	
	private final AtomicInteger globalMaxValue = new AtomicInteger(Integer.MIN_VALUE);

	@Override
	public void onEvent(DisruptorLineEvent event, long sequence, boolean endOfBatch) throws Exception {

		String line = event.getLine();
		if(line==null) return;
		
		String parts[] = line.split(",");
		if(parts.length!=4) return;
		
		
		int responseTime = Integer.parseInt(parts[3]);
		globalMaxValue.accumulateAndGet(responseTime, Integer::max);
		
	}
	
	public int getGlobalMaxResponseTime() {
		return globalMaxValue.get();
	}

}
