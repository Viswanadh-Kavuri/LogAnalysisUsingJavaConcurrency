package com.visu.java.logs;

import java.util.ArrayList;
import java.util.List;

public interface InMemoryLogGenerator extends StreamingLogGeneraor {
	default List<String> getLogs(int numberOfLogs){
		List<String> logs = new ArrayList<>(numberOfLogs);
		generate(numberOfLogs, logs::add);
		return logs;
	}
}
