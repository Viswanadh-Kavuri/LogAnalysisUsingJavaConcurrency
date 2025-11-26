package com.visu.java.logs;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.visu.java.sink.LogSink;

public class RandomLogGenerator implements InMemoryLogGenerator {

	@Override
	public void generate(int count, LogSink sink) {

		if(count<=0) throw new IllegalArgumentException("Invalid number of logs requested: "+ count);
		
		List<String> endpoints = Arrays.asList("/home", "/cart", "/search", "/order", "/profile");
	    List<String> users = Arrays.asList("u1", "u2", "u3", "u4", "u5");

	    Random random = new Random();

	    for (int i = 0; i < count; i++) {
	        String timestamp = "2025-11-20T01:00:" + (i % 60) + "Z";
	        String userId = users.get(random.nextInt(users.size()));
	        String endpoint = endpoints.get(random.nextInt(endpoints.size()));
	        
	        // Response times: usually small but sometimes spikes
	        long responseTime = random.nextInt(500);
	        if (random.nextDouble() < 0.01) { // 1% heavy spike
	            responseTime += random.nextInt(700); // up to ~1200
	        }
	        
	        try {
				sink.accept(timestamp + "," + userId + "," + endpoint + "," + responseTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}

}
