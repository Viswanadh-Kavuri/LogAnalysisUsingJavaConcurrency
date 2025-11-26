package com.visu.java.examples.ExecutorService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.visu.java.Utils;
import com.visu.java.models.LogStatsResult;

public class ExecutorServiceStreamingInput implements LogStatsCalculator {
	

	@Override
	public LogStatsResult computeStats(String logFilePath) {
		int cores = Utils.getCores();
			
		ExecutorService executor = Executors.newFixedThreadPool(cores);
		
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10_000);
		AtomicInteger globalMaxValue = new AtomicInteger(Integer.MIN_VALUE);
		AtomicInteger countDown = new AtomicInteger(cores);

		for(int i=0;i<cores;i++) {
			executor.submit(()->{
				while(true) {
					String line;
					try {
						line = queue.take();
						if(line.equals("__END__")){
							if(countDown.decrementAndGet()==0) {
								executor.shutdown();
							}
							break;
						} else {
							String[] parts = line.split(",");
							if(parts.length!=4) {
								continue;
							} 
							globalMaxValue.accumulateAndGet(Integer.parseInt(parts[3]), Math::max);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
				}
			});
		}
		
		ExecutorService producer = Executors.newSingleThreadExecutor();
		producer.execute(()->{
			try(BufferedReader br = new BufferedReader(new FileReader("logs.txt"))){
				String line;
				while((line = br.readLine())!=null) {
					queue.put(line);
				}
                for (int i = 0; i < cores; i++) queue.put("__END__");

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		producer.shutdown();
		try {
			producer.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return LogStatsResult.builder().maxResponseTime(globalMaxValue.get()).build();
		
	}

}
