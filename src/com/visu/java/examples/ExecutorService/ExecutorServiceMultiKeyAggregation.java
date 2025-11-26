package com.visu.java.examples.ExecutorService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.visu.java.Utils;
import com.visu.java.models.LogStatsResult;

public class ExecutorServiceMultiKeyAggregation implements LogStatsCalculator {
	
	@Override
	public LogStatsResult computeStats(String logFilePath) {
		final int cores = Utils.getCores();
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10_000);
		AtomicInteger globalMaxValue = new AtomicInteger(Integer.MIN_VALUE);
		ConcurrentHashMap<String, Integer> maxResponsePerEndpoint =  new ConcurrentHashMap<>();
		ConcurrentHashMap<String, Integer> requestsPerUser =  new ConcurrentHashMap<>();
		
		ExecutorService consumerExecutor  = Executors.newFixedThreadPool(cores);
		
		CountDownLatch counter = new CountDownLatch(cores);

			for(int i=0; i<cores;i++) {
				consumerExecutor.execute(()->{
					while(true){
						try {
							String line = queue.take();
							
							if(line.equals("__END__")) {
								counter.countDown();
								if(counter.getCount()==0) {
									consumerExecutor.shutdown();
								}
								break;
							}
							String[] parts = line.split(",");
							if(parts.length!=4) continue;
							else {
								globalMaxValue.accumulateAndGet(Integer.parseInt(parts[3]), Math::max);
								requestsPerUser.merge(parts[1], 1, Integer::sum);
								maxResponsePerEndpoint.merge(parts[2],Integer.parseInt(parts[3]), Math::max);
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
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
								queue.put("__END__");
							}
							
						} catch(Exception e) {
							e.printStackTrace();
						
						}
						
						
					});
			
			try {
				producerExecutor.shutdown();
				counter.await();
				producerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("GlobalMaxValue is "+globalMaxValue.get());
				System.out.println("requestsperuser"+ requestsPerUser);
				System.out.println("maxResponsePerEndPoint "+maxResponsePerEndpoint);
			}
			return LogStatsResult.builder()
					.maxResponseTime(globalMaxValue.get())
					.maxResponsePerEndpoint(maxResponsePerEndpoint)
					.requestsPerUser(requestsPerUser)
					.build();
	}
}
