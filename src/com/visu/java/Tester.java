package com.visu.java;

import java.io.IOException;

import com.visu.java.disruptor.LmaxDisruptorStatsCalculator;
import com.visu.java.disruptor.WorkerPoolStatsCalculator;
import com.visu.java.models.LogStatsResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Tester {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		
		//generate logs
		//int numberOfLogs = 100000000;
		//LogGenerator.generateTestLogsToFile("logs.txt", numberOfLogs);

		
		/*
		 * long startTime = System.currentTimeMillis(); ExecutorServiceWithMapReduce
		 * mapreduce = new ExecutorServiceWithMapReduce(); mapreduce.computeStats();
		 * long endTime = System.currentTimeMillis();
		 * System.out.println("total time in seconds for mapreduce: "+
		 * (endTime-startTime)/1000);
		 * 
		 * startTime = System.currentTimeMillis(); ExecutorServiceMultiKeyAggregation
		 * executorService = new ExecutorServiceMultiKeyAggregation();
		 * executorService.computeStats(); endTime = System.currentTimeMillis();
		 * System.out.println("total time in seconds: "+ (endTime-startTime)/1000);
		 */
		/*
		 * log.info("LmaxDisruptorStatsCalculator example start");
		 * LmaxDisruptorStatsCalculator executorService = new
		 * LmaxDisruptorStatsCalculator(); LogStatsResult result =
		 * executorService.computeStats("logs.txt");
		 * log.info("LmaxDisruptorStatsCalculator example end {}", result);
		 */		
		log.info("WorkerPoolStatsCalculator example start");
		WorkerPoolStatsCalculator executorService2 = new WorkerPoolStatsCalculator();
		LogStatsResult result = executorService2.computeStats("logs.txt");
		log.info("WorkerPoolStatsCalculator example end {}", result);

	}

}
