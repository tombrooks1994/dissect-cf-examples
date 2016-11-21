package at.ac.uibk.dps.cloud.simulator.test.simple;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;

public class physicalMachine {
	
	
	//	Creation of xml to put into loop for creation of physical machines
	//	machine name = pm1 (physical machine) 
	//	64 cores, processing in mb's and so is the memory
	//	Figure out what parts need to be imported into the physical machine
	
	public String physicalMachine = "<?xml version '1.0' encoding = 'UTF-8'?>\n"
			+ "<machine name='pm1' cores='64' processing='1' memory='256000'>";

	
	//Out.println not working on this file #ask gabor why
	
	//System.out.println(physicalMachine);

}
