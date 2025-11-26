package com.visu.java.examples.ExecutorService;

import com.visu.java.models.LogStatsResult;

public interface LogStatsCalculator {
	
	 LogStatsResult computeStats(String logFilePath);

}
