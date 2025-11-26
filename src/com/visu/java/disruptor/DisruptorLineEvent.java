package com.visu.java.disruptor;

import lombok.Getter;
import lombok.Setter;


/**
 * This is the Event that lives in RingBuffer
 * 
 */
@Setter
@Getter
public class DisruptorLineEvent {
	String line;
}
