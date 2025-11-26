package com.visu.java.disruptor;

import com.lmax.disruptor.EventFactory;



/**
 * This is the factoru used to create empty DisruptorLineEvent objects for Ring Buffer
 * 
 */
public class DisruptorlinEventFactory implements EventFactory<DisruptorLineEvent> {

	@Override
	public DisruptorLineEvent newInstance() {
		return new DisruptorLineEvent();
	}

}
