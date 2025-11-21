package com.visu.java;

import java.io.IOException;

public class Tester {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		ExecutorServiceWithMapReduce mapreduce =  new ExecutorServiceWithMapReduce();
		mapreduce.computeStats();
		long endTime = System.currentTimeMillis();
		System.out.println("total time in seconds for mapreduce: "+ (endTime-startTime)/1000);
		
		startTime = System.currentTimeMillis();
		ExecutorServiceMultiKeyAggregation executorService =  new ExecutorServiceMultiKeyAggregation();
		executorService.computeStats();
		endTime = System.currentTimeMillis();
		System.out.println("total time in seconds: "+ (endTime-startTime)/1000);
//		ExecutorServiceMultiKeyAggregation executorService = new ExecutorServiceMultiKeyAggregation();

	}

}
