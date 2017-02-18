package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ConsumptionEventAdapter;

public class Timer extends ConsumptionEventAdapter {

	private boolean completed = false;
	
	@Override
	public void conComplete() {
		completed = true;
	}
	
}
