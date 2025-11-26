package com.visu.java.sink;

import java.util.ArrayList;
import java.util.List;

public class ListSink implements LogSink {
	private static final List<String> logs = new ArrayList<>();
	@Override
	public void accept(String line) throws Exception {
		logs.add(line);
	}
	
	public List<String> getLogs(){
		return logs;
	}

}
