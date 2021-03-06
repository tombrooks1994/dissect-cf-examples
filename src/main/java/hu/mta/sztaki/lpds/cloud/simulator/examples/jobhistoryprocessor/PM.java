package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import java.io.File;
import java.io.RandomAccessFile;
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
import hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.AlwaysOnMachines;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.PhysicalMachineController;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.SchedulingDependentMachines;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.FirstFitScheduler;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.Scheduler;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.util.CloudLoader;
import hu.mta.sztaki.lpds.cloud.simulator.util.PowerTransitionGenerator;

/**
 * This class is for the creation of a physical machine. There is a physical machine
 * component helper at the top to show the user what the minimum and maximum values
 * are for each component in the physical machine. The 2nd stage of this class is 
 * creating 1000 physical machines with randomised values from a set array which is
 * commented right the way through this file
 * 
 * @author 
 * 		{Thomas Brooks, Liverpool John Moores University, Computer Foresnics Undergraduate}
 * @Date 
 * 		{2nd December 2016}
 * @Project
 * 		{Failure trace}
 * @E-mail
 * 		{t.s.brooks@2013.ljmu.ac.uk}
 * 
 */


public class PM {

	public static void main(String[] args) throws Exception {
		
		/**
		 * 1st stage of physicalMachine.java is the component helper
		 * 
		 * This stage of the file is to tell the user or the developer
		 * of this file what units that the components are measured in
		 * for example, cores being measured in bits etc. 
		 * 
		 * There is two different lists which are values which are 
		 * measured in bits and values measures in watts (for power) 
		 * */
		
		/** Start of the component helper */
		
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
				+ "4) maximum memory values: " + memoryArray[0] + ", "
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
		
		/**
		 * The power is measured in watts, the minimum is chosen by finding out what
		 * the minimum amount of watts a computer PSU can output and the maximum
		 * is chosen by finding out what the maximum for a computer is.
		 * */
		
		
		int[] maxPower = {5, 10}; // 5w, 10w
		double[] minPower = {0.9, 2.5}; // .9w, 2.5w
		
		System.out.println("\n---- the values below are measured in watts ---- \n" + 
							"\n7) maximum power for PM: " + maxPower[0] + ", "
							+ maxPower[1] + "\n"
							+ "8) minimum power for PM: " + minPower[0] 
							+ ", " + minPower[1]);
		
		/** End of component helper */
		System.out.println("------------------------------------------------");
	
	
	/**
	 * 2nd stage creates the 1000 physical machines using the array's that are created
	 * above. The helper above just sets example minimums and maximums there will be
	 * different values used in the actual PM creator. 
	 * */	
		
		/** Start of the physical machine creator */
			
		/** 
		 * These array below which are numbers are for the different values set
		 * in the component helper such as cores, memory, disk space and power. 
		 * These are all measure in the same way that the helper is measured in.		 * 
		 */
		
		/** Cores Array */
	    List<String> cores = new ArrayList<String>();
	    cores.add("32");
	    cores.add("64");
	    cores.add("128");
	    
	    /** Memory Array */
	    List<String> memory = new ArrayList<String>();
	    memory.add("8000");
	    memory.add("1600");
	    memory.add("24000");
	    memory.add("32000");
	    
	    /** Disk Space Array */	    
	    List<String> diskSpace = new ArrayList<String>();
	    diskSpace.add("500000");
	    diskSpace.add("1000000");
	    diskSpace.add("2000000");
	    diskSpace.add("3000000");
	    
	    /** Power Array */	    
	    List<String> power = new ArrayList<String>();
	    power.add("0.9");
	    power.add("2.5");
	    power.add("5.0");
	    power.add("10.0");
	    
	    /** Hard drives array */
	    List<String> hD = new ArrayList<String>();
	    hD.add("1000000");
	    hD.add("2000000");
	    hD.add("3000000");
	    hD.add("4000000");
	    
	    /** Motherboard Array */
	    /** There will be different types of motherboards added at a later date. */
	    List<String> mobo = new ArrayList<String>();
	    mobo.add("Type 1"); 
	    mobo.add("Type 2");
	    mobo.add("Type 3");
	    mobo.add("Type 4");
	    
	    /** Cd-rom drives */
	    List<String> cdrom = new ArrayList<String>();
	    cdrom.add("1 Present");
	    cdrom.add("2 Present");
	    cdrom.add("3 Present");
	    cdrom.add("4 Present");
	    cdrom.add("none present");
	    
	    /**
	     * A normal speed for latency is  5 and 40 ms
	     * This array below differenciates the different speeds and
	     * distributes them over the 1000 physical machines 
	     * */
	    
	    List<String> latency = new ArrayList<String>();
	    latency.add("5");
	    latency.add("10");
	    latency.add("15");
	    latency.add("20");
	    latency.add("25");
	    latency.add("30");
	    latency.add("35");
	    latency.add("40");
	    
	    /**
	     * Power max array 
	     * 250w-750w*/
	    	    
	    List<String> maxPowerPM = new ArrayList<String>();
	    maxPowerPM.add("250");
	    maxPowerPM.add("350");
	    maxPowerPM.add("450");
	    maxPowerPM.add("550");
	    maxPowerPM.add("650");
	    maxPowerPM.add("750");
	    
	    /**
	     * Power max array 
	     * 25w-75w*/
	    
	    List<String> idlePowerPM = new ArrayList<String>();
	    idlePowerPM.add("25");
	    idlePowerPM.add("35");
	    idlePowerPM.add("45");
	    idlePowerPM.add("55");
	    idlePowerPM.add("65");
	    idlePowerPM.add("75");
	    
	    /**
	     * This is the final stage of creation of the physical machines
	     * There is use of the Random(); function which is preinstalled into
	     * java. There is then a loop that iterates through the List Arrays
	     * and randomly picks a value from each one and returns the values
	     * In the output there is names for each component. The for loop iterates
	     * through the arrays 1000 times and chooses 1000 random values to generate
	     * 1000 physical machines.
	     * */
	    
	    Random random = new Random();

	    for (int x = 0; x < 1000; x++) {
	        /**System.out
	                .println("Physical Machine " + (x+1) + ") cores: " + cores.get(random.nextInt(cores.size()))
	                + ", memory: " + memory.get(random.nextInt(memory.size()))
	                + ", disk space: " + diskSpace.get(random.nextInt(diskSpace.size()))
	                + ", power: " + power.get(random.nextInt(power.size()))
	                + ", hard drive: " + hD.get(random.nextInt(hD.size()))
	                + ", motherboard: " + mobo.get(random.nextInt(mobo.size()))
	                + ", cdrom: " + cdrom.get(random.nextInt(cdrom.size())) + "\n"
	                );	*/
	        
	       /** String xml = "\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    			+ "<cloud id=1>\n"
	        		+ "<machine id=" + " " + (x+1) + ">\n"
	    			+ "<memory size=" + " " + (memory.get(random.nextInt(memory.size()))) + " " + "></memory>\n"
	        		+ "<diskSpace size=" + " " + (diskSpace.get(random.nextInt(diskSpace.size()))) + " " +"></diskSpace>\n"
	    			+ "<power output=" + " " + (power.get(random.nextInt(power.size()))) + " " + "></power>\n"
	        		+ "<hardDrive size=" + " " + (hD.get(random.nextInt(hD.size()))) + " " + "></hardDrive>\n"
	    			+ "<motherboard type=" + " " + (mobo.get(random.nextInt(mobo.size()))) + " " + "></motherboard>\n"
	        		+ "<cdrom amount=" + " " + (cdrom.get(random.nextInt(cdrom.size()))) + " " + "></cdrom>\n"
	    			+ "</machine>\n"
	    			+ "</cloud>";       */ 
	        
	        String newxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<cloud id=\"oxygen\"	scheduler=\"hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.FirstFitScheduler\" pmcontroller=\"hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.AlwaysOnMachines\">\n"
			+ "<machine id=\" \"" + (x+1) + " cores=\""+ (cores.get(random.nextInt(cores.size()))) +" processing=\"0.001\" memory=\"256000000000\">\n"
			+ "<powerstates kind=\"host\">\n"
			+ "<power	model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" idle=\"296\" max=\"493\" inState=\"default\" />\n"
			+ "<power	model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"20\" max=\"20\" inState=\"OFF\" />\n"
			+ "</powerstates>\n"
			+ "<statedelays startup=\"89000\" shutdown=\"29000\" />\n"
			+ "<repository id=\"disk\" capacity=\"5000000000000\" inBW=\"250000\" outBW=\"250000\" diskBW=\"50000\">\n"
			+ "<powerstates kind=\"storage\">\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" idle=\"6.5\" max=\"9\" inState=\"default\" />\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "</powerstates>\n"
			+ "<powerstates kind=\"network\">\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" idle=\"3.4\" max=\"3.8\" inState=\"default\" />\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "</powerstates>\n"
			+ "<latency towards=\"repo\" value=\"5\" />\n"
			+ "</repository>\n"
			+ "</machine>\n"
			+ "<repository id=\"repo\" capacity=\"38000000000000\" inBW=\"250000\" outBW=\"250000\" diskBW=\"100000\">\n"
			+ "<powerstates kind=\"storage\">\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" idle=\"65\" max=\"90\" inState=\"default\" /> \n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "</powerstates>\n"
			+ "<powerstates kind=\"network\">\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" idle=\"3.4\" max=\"3.8\" inState=\"default\" />\n"
			+ "<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "</powerstates>\n" + "<latency towards=\"disk\" value=\"5\" />\n"
			+ "</repository>\n" + "</cloud>\n";
	        
	        
	        System.out.println(newxml);
	        		
	    }
	   
	    /** End of physical machine creator */
	}
}
