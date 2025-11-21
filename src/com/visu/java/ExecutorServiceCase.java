package com.visu.java;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutorServiceCase {
	
	private AtomicLong globalMaxValue = new AtomicLong(Long.MIN_VALUE);

	public long getGlobalMaxvalue(int numberOfLogs) {
	
	final int cores = Utils.getCores();
	System.out.println("Number of cores are: "+cores);
	
	//create threadpool
	ExecutorService executor = Executors.newFixedThreadPool(cores);
	System.out.println("created thread pool successfully");
	long startTime = System.currentTimeMillis();
	//get logs
	List<String> logs = LogGenerator.generateLogs(numberOfLogs);
	long endTime=System.currentTimeMillis();
	System.out.println("generated "+ logs.size()+" Logs successfully in "+ (endTime-startTime)/1000);

	//divide into chunks
	int chunkSize = numberOfLogs/cores;
	
	System.out.println("chunkSize : "+chunkSize);
	
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
		System.out.println("global max value calculation Completed in ms"+ (endTime-startTime)/1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return globalMaxValue.get();
}

	private void processChunk(List<String> chunk) {
		// TODO Auto-generated method stub
		String[] currLine;
		for(String line: chunk) {
			currLine = line.split(",");
			if(currLine.length!=4) {
				continue;
			}
			globalMaxValue.accumulateAndGet(Long.parseLong(currLine[3]), Math::max);
		}
	}
}
