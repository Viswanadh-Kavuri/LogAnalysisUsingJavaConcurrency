package com.visu.java.sink;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileSink implements LogSink, AutoCloseable {

	private final BufferedWriter bw;
	
	public FileSink(String filePath) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(filePath));
	}
	
	@Override
	public void accept(String line) throws Exception {
		this.bw.write(line);
		this.bw.newLine();
	}

	@Override
	public void close() throws Exception {
		this.bw.close();
	}

}
