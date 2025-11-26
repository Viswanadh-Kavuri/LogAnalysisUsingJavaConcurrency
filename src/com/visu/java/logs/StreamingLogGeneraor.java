package com.visu.java.logs;

import com.visu.java.sink.LogSink;

public interface StreamingLogGeneraor {
	void generate(int count, LogSink sink);
}
