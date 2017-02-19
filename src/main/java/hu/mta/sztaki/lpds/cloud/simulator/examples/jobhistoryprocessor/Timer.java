package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ConsumptionEventAdapter;

public class Timer extends ConsumptionEventAdapter {

	private boolean completed = false;
	private boolean cancelled = false;
	private long expectedTime; 
	private long failed; 
	
	
	@Override
	public void conComplete() {
		//completed = true;
		if (expectedTime < 0) {
			
			completed=true; 
			Timed.getFireCount();
			
		} else {
			
			completed=false;
			
		}
		
		
	}
	
	public void conCancelled() {
		if (expectedTime > 0) {
		
		cancelled = true; 
		
	} else {
	
		conComplete();
	
	}
	}
	
}
