package com.visu.java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LogGenerator {
	
	public static List<String> generateLogs(long totalLogs){
		
		if(totalLogs<=0) throw new IllegalArgumentException("Invalid number of logs requested: "+ totalLogs);
		
		List<String> endpoints = Arrays.asList("/home", "/cart", "/search", "/order", "/profile");
	    List<String> users = Arrays.asList("u1", "u2", "u3", "u4", "u5");

	    Random random = new Random();

	    List<String> logs = new ArrayList<>();
	    for (int i = 0; i < totalLogs; i++) {
	        String timestamp = "2025-11-20T01:00:" + (i % 60) + "Z";
	        String userId = users.get(random.nextInt(users.size()));
	        String endpoint = endpoints.get(random.nextInt(endpoints.size()));
	        
	        // Response times: usually small but sometimes spikes
	        long responseTime = random.nextInt(500);
	        if (random.nextDouble() < 0.01) { // 1% heavy spike
	            responseTime += random.nextInt(700); // up to ~1200
	        }
	        
	        logs.add(timestamp + "," + userId + "," + endpoint + "," + responseTime);
	    }

		return logs;
		
	}
	
	public static void generateTestLogsToFile(String outputFile, int totalLogs) throws IOException {
	    List<String> endpoints = Arrays.asList("/home", "/cart", "/search", "/order", "/profile");
	    List<String> users = Arrays.asList("u1", "u2", "u3", "u4", "u5");

	    Random random = new Random();

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
	        for (int i = 0; i < totalLogs; i++) {
	            String timestamp = "2025-11-20T01:00:" + (i % 60) + "Z";
	            String userId = users.get(random.nextInt(users.size()));
	            String endpoint = endpoints.get(random.nextInt(endpoints.size()));
	            long responseTime = random.nextInt(500);
	            if (random.nextDouble() < 0.01) {
	                responseTime += random.nextInt(700);
	            }

	            writer.write(timestamp + "," + userId + "," + endpoint + "," + responseTime);
	            writer.newLine();
	        }
	    }
	}


}
