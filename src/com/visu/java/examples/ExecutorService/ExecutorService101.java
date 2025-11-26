package com.visu.java.examples.ExecutorService;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.visu.java.models.LogStatsResult;

import lombok.extern.slf4j.Slf4j;

import com.visu.java.Utils;
import com.visu.java.logs.RandomLogGenerator;

@Slf4j
public class ExecutorService101 implements LogStatsCalculator{
	
	private AtomicInteger globalMaxValue = new AtomicInteger(Integer.MIN_VALUE);

	private void processChunk(List<String> chunk) {
		// TODO Auto-generated method stub
		String[] currLine;
		for(String line: chunk) {
			currLine = line.split(",");
			if(currLine.length!=4) {
				continue;
			}
			globalMaxValue.accumulateAndGet(Integer.parseInt(currLine[3]), Integer::max);
		}
	}

	@Override
	public LogStatsResult computeStats(String logFilePath) {
		
		final int cores = Utils.getCores();
		log.info("Number of cores are: {} ", cores);
		
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		log.info("created thread pool successfully");
		int numberOfLogs= 10000000;
		long startTime = System.currentTimeMillis();
		

		RandomLogGenerator logGenerator = new RandomLogGenerator();
		List<String> logs = logGenerator.getLogs(numberOfLogs);
		
		long endTime=System.currentTimeMillis();
		
		log.info("generated {} Logs successfully in {}", logs.size(), (endTime-startTime)/1000);

		//divide into chunks
		int chunkSize = numberOfLogs/cores;
		
		log.info("chunkSize : "+chunkSize);
		
		 int taskCount = 0;
	     for (int i = 0; i < numberOfLogs; i += chunkSize) {
	         taskCount++;
	     }
	     
	     CountDownLatch counter = new CountDownLatch(taskCount);
	 	startTime = System.currentTimeMillis();
	 	for(int i=0;i<numberOfLogs;i+=chunkSize) {
	 		//submit to executor
	 		List<String> chunk = logs.subList(i, Math.min(i + chunkSize, numberOfLogs));
	 		executor.submit(()->{
	 			try {
	 			processChunk(chunk);
	 			} finally {
	 				counter.countDown();
	 			}
	 		});
	 	}
	 	
	 	executor.shutdown();
	 	try {
	 		counter.await();
	 		endTime = System.currentTimeMillis();
	 		log.info("global max value calculation Completed in {} seconds", Math.ceil((endTime-startTime))/1000);
	 	} catch (InterruptedException e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	}
		
		return LogStatsResult
				.builder()
				.maxResponseTime(globalMaxValue.get())
				.build();
	}
}
