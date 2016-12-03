package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.PowerState;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.FileBasedTraceProducerFactory;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.GenericTraceProducer;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.TraceFilter;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.filters.RunningAtaGivenTime;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.random.RepetitiveRandomTraceGenerator;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.PhysicalMachineController;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.SchedulingDependentMachines;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.FirstFitScheduler;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.Scheduler;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.util.CloudLoader;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;

public class physicalMachine {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		// Start of the component helper
		System.out.println("------------------------------------------------ \n"
							+ "Physical Machine Component Helper \n" + 
							"------------------------------------------------ \n" +
							"The components below are measures in bits\n" +
							"------------------------------------------------");
		
		int[] minCores = {64};
		int[] maxCores = {128};
		System.out.println("1) minimum cores: " + minCores[0] + "\n"
							+ "2) maximum cores: " + maxCores[0]);
		
		int[] memoryArray = {8000, 16000, 24000, 32000};
		int[] minMemory = {2000, 4000, 6000};
		
		System.out.println("3) minimum memory values: " + minMemory[0] + ", " 
				+ minMemory[1] + ", " + minMemory[2] + "\n"
				+ "4) amount of memory options in PM: " + memoryArray[0] + ", "
				+ memoryArray[1] + ", " + memoryArray[2] + ", " 
				+ memoryArray[3]);
		
		int[] maxDiskSpace = {1000000, 2000000, 3000000}; // 1TB, 2TB, 3TB 
		int[] minDiskSpace = {1000, 2000, 3000}; // 1GB, 2GB, 3GB
		
		System.out.println("5) maximum disk space allowed in PM: " + maxDiskSpace[0] + ", "
							+ maxDiskSpace[1] + ", "  
							+ maxDiskSpace[2] 
							+ "\n6) minimum disk space allowed in PM: "
							+ minDiskSpace[0] + ", "
							+ minDiskSpace[1] + ", " 
							+ minDiskSpace[2]);
		
		/*
		 * The power is measured in watts, the minimum is chosen by finding out what
		 * the minimum amount of watts a computer PSU can output and the maximum
		 * is chosen by finding out what the maximum for a computer is.		 * 
		 * */
		
		
		int[] maxPower = {5, 10}; // 5w, 10w
		double[] minPower = {0.9, 2.5}; // .9w, 2.5w
		
		System.out.println("\n---- the values below are measured in watts ---- \n" + 
							"\n7) maximum power for PM: " + maxPower[0] + ", "
							+ maxPower[1] + "\n"
							+ "8) minimum power for PM: " + minPower[0] 
							+ ", " + minPower[1]);
		
		// End of component helper
		System.out.println("------------------------------------------------");
	
	
	/*
	 * Class to create the 1000 physical machines using the array's that are created
	 * above. The helper above just sets example minimums and maximums there will be
	 * different values used in the actual PM creator. 
	 * */
	
		/*
		int[] cores = {32, 64, 128};
		
		
		int pm = new Random().nextInt(cores.length);
		int random = (cores[pm]);
		
		
		
		// Create a list
		List list = new ArrayList();

		// Add elements to list

		// Shuffle the elements in the list
		Collections.shuffle(list);

		// Create an array
		int[] array = new int[]{32, 64, 128};

		// Shuffle the elements in the array
		Collections.shuffle(Arrays.asList(array));
		
for (random = 0; random<cores[pm]; random++){
			
			System.out.println(list);
			//System.out.println(cores[pm]);
			//System.out.println(random);
			//System.out.println(Arrays.toString(cores));
			
		}
		*/
		
		
		/*
		 * Randomly iterate through this array (find a way to do this)
		 * */
		
		ArrayList cores = new ArrayList();
		cores.add(32);
		cores.add(64);
		cores.add(128);
		Collections.shuffle(cores);
		System.out.println(cores);
			
		
	}
}