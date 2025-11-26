package com.visu.java.examples.ExecutorService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.visu.java.Utils;
import com.visu.java.models.LogStatsResult;

public class ExecutorServiceWithMapReduce implements LogStatsCalculator {
		
	@SuppressWarnings("finally")
	@Override
	public LogStatsResult computeStats(String logFilePath) {
		final int cores = Utils.getCores();
			final String POISON_PILL = "__END__";
			ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10_000);
			
			record WorkerResult(int maxResponseTime, Map<String, Integer> requestsPerUser, Map<String, Integer> maxResponsePerEndpoint ) {}
			
			ConcurrentLinkedQueue<WorkerResult> resultQueue = new ConcurrentLinkedQueue<>();
			ExecutorService consumerExecutor  = Executors.newFixedThreadPool(cores);
			
			CountDownLatch counter = new CountDownLatch(cores);

				for(int i=0; i<cores;i++) {
					consumerExecutor.execute(()->{
						int localMax = Integer.MIN_VALUE;
						Map<String, Integer> localRequestsPerUser = new HashMap<>();
						Map<String, Integer> localMaxResponsePerEndpoint =  new HashMap<>();
						while(true){
							try {
								String line = queue.take();
								
								if(line.equals(POISON_PILL)) {
									break;
								}
								String[] parts = line.split(",");
								if(parts.length!=4) continue;
								else {
									localMax = Integer.max(localMax, Integer.parseInt(parts[3]));
									localRequestsPerUser.merge(parts[1], 1, Integer::sum);
									localMaxResponsePerEndpoint.merge(parts[2],Integer.parseInt(parts[3]), Integer::max);
								}
								
							} catch (InterruptedException e) {
								e.printStackTrace();
								Thread.currentThread().interrupt();
								break;
							}
						}
						
						resultQueue.add(new WorkerResult(localMax, localRequestsPerUser, localMaxResponsePerEndpoint));
						counter.countDown();
					});
				}
				
				ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
				producerExecutor.execute(
						()->{
							try(BufferedReader br = new BufferedReader(new FileReader("logs.txt"))){
								String line;
								while((line=br.readLine())!=null) {
									queue.put(line);
								}
								
								for(int j=0;j<cores;j++) {
									queue.put(POISON_PILL);
								}
								
							} catch(Exception e) {
								e.printStackTrace();
							
							}
							
							
						});
				
				try {
					producerExecutor.shutdown();
					consumerExecutor.shutdown();
					counter.await();
					producerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					consumerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);				

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					int globalMaxValue = Integer.MIN_VALUE;
					Map<String, Integer> globalRequestsPerUser = new HashMap<>();
					Map<String, Integer> globalMaxResponsePerEndPoint = new HashMap<>();
					for(WorkerResult result: resultQueue) {
						globalMaxValue = Integer.max(globalMaxValue, result.maxResponseTime);
						result.requestsPerUser.forEach((user, requests)->globalRequestsPerUser.merge(user, requests, Integer::sum));
						result.maxResponsePerEndpoint.forEach((endPoint, responseTime)->globalMaxResponsePerEndPoint.merge(endPoint, responseTime, Integer::max));
					}
					
					System.out.println("GlobalMaxValue is "+globalMaxValue);
					System.out.println("globalRequestsPerUser"+ globalRequestsPerUser);
					System.out.println("globalMaxResponsePerEndPoint "+globalMaxResponsePerEndPoint);
					return LogStatsResult.builder()
							.maxResponseTime(globalMaxValue)
							.requestsPerUser(globalRequestsPerUser)
							.maxResponsePerEndpoint(globalMaxResponsePerEndPoint)
							.build();
				}
				
		}

	}

