package com.visu.java.models;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LogStatsResult {
	int maxResponseTime;
	Map<String, Integer> requestsPerUser;
	Map<String, Integer> maxResponsePerEndpoint;

}
