package com.visu.java.disruptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.visu.java.Utils;
import com.visu.java.examples.ExecutorService.LogStatsCalculator;
import com.visu.java.models.LogStatsResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LmaxDisruptorStatsCalculator implements LogStatsCalculator {

	@Override
	public LogStatsResult computeStats(String logFilePath) {
		
		//uncomment this below 2 lines if logs file doesn't exist in your workspace
//		RandomLogGenerator logGenerator =  new RandomLogGenerator();
//		logGenerator.generate(1000000000, new FileSink(logFilePath));
		ExecutorService executor = Executors.newCachedThreadPool();
		DisruptorlinEventFactory eventFactory = new DisruptorlinEventFactory();
		StatsEventHandler handler = new StatsEventHandler();
		Disruptor<DisruptorLineEvent> disruptor = new Disruptor<>(eventFactory, 1024*64, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
		disruptor.handleEventsWith(handler);
		
		disruptor.start();
		log.info("disruptor started");
		RingBuffer<DisruptorLineEvent> ringBuffer = disruptor.getRingBuffer();
		long startTime = System.currentTimeMillis();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(logFilePath))){
			String line;
			long nextSlot;
			log.info("Reading from {}", logFilePath);
			while((line=reader.readLine())!=null) {
				nextSlot = ringBuffer.next();
				DisruptorLineEvent event = ringBuffer.get(nextSlot);
				event.setLine(line);
				ringBuffer.publish(nextSlot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
				try {
					disruptor.shutdown();
					executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("execution completed in {} seconds", Math.ceil(endTime-startTime)/1000);
		}
		
		return LogStatsResult.builder()
				.maxResponseTime(handler.getGlobalMaxResponseTime())
				.requestsPerUser(handler.getRequestsPerUser())
				.maxResponsePerEndpoint(handler.getMaxResponseTimePerEndpoint())
				.build();
	}

}
