package com.visu.java.sink;

public interface LogSink {
 void accept(String line) throws Exception;
}
