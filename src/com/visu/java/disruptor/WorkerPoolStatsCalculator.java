package com.visu.java.disruptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.visu.java.Utils;
import com.visu.java.examples.ExecutorService.LogStatsCalculator;
import com.visu.java.models.LogStatsResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerPoolStatsCalculator implements LogStatsCalculator {

	@Override
	public LogStatsResult computeStats(String logFilePath) {
		final int cores = Utils.getCores();
		ExecutorService executor = Executors.newCachedThreadPool();
//		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		DisruptorlinEventFactory eventFactory = new DisruptorlinEventFactory();
		Disruptor<DisruptorLineEvent> disruptor = new Disruptor<>(eventFactory, 
				1024*64, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
		
		StatsWorkHandler[] workers = new StatsWorkHandler[cores];
		
		for(int i=0;i<workers.length;i++) {
		workers[i]= new StatsWorkHandler();
		}
		
		disruptor.handleEventsWithWorkerPool(workers);
		
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
		
	
			disruptor.shutdown();

			long endTime = System.currentTimeMillis();
			log.info("execution completed in {} seconds", Math.ceil(endTime-startTime)/1000);
			int globalMaxValue = Integer.MIN_VALUE;
			Map<String, Integer> globalRequests = new HashMap<>();
			Map<String, Integer> globalMaxResponseperEndpoint = new HashMap<>();

			for(var worker: workers) {
				globalMaxValue = Math.max(globalMaxValue, worker.getGlobalMaxResponseTime());
				worker.getRequestsPerUser().forEach((user,requests)->globalRequests.merge(user, requests, Integer::sum));
				worker.getMaxResponseTimePerEndpoint().forEach((endpoint, responseTime)->globalMaxResponseperEndpoint.merge(endpoint, responseTime, Integer::max));
			}
		
		return LogStatsResult.builder()
				.maxResponseTime(globalMaxValue)
				.requestsPerUser(globalRequests)
				.maxResponsePerEndpoint(globalMaxResponseperEndpoint)
				.build();
	}
	}
