package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;


import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.util.CloudLoader;
import uk.ac.ljmu.fet.cs.DDOSInterface;
import uk.ac.ljmu.fet.cs.DemoClass;
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
 */



public class physicalMachine implements DDOSInterface {
	
	@SuppressWarnings("static-access")
	private IaaSService createCloudAndReturnWithIt(IaaSService newiaas,  int n) throws IOException, SAXException, ParserConfigurationException {

		this.iaas = CloudLoader.loadNodes("C:\\Users\\Tom\\git\\dissect-cf-examples\\PM.xml");
		newiaas = this.iaas;
		return newiaas;
		
	}
	
	private int count = 1; 
	private int maxTries; 
	
	@Override
	public IaaSService createCloudAndReturnWithIt() {
		// TODO Auto-generated method stub
		if (count++ == 1) {
			try {
				IaaSService iaaas = createCloudAndReturnWithIt(iaas, 0);
				return iaas;
			} catch (IOException | SAXException | ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			return iaas;
	}

	
	@Override
	public PhysicalMachine getUnderDDOSMachine() {
		PhysicalMachine DDOS = iaas.machines.get(0);
		// TODO Auto-generated method stub
		return DDOS;
	}

	
	@Override
	public PhysicalMachine getUserOftheUnderDDOSMachine() {
		// TODO Auto-generated method stub
		PhysicalMachine DDOSUser = iaas.machines.get(1);
		// TODO Auto-generated method stub
		return DDOSUser;
	}
		
	@Override
	public void startDDOSing() {
		// TODO Auto-generated method stub
		   
		    	Random random = new Random();	    	
		    	
		    	final long s = random.nextInt(transferCount);
		    	try {
					this.iaas = CloudLoader.loadNodes("C:\\Users\\Tom\\git\\dissect-cf-examples\\PM.xml");
				} catch (IOException | SAXException | ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	for (int n = 1; n<1000; n++) {
		    		DemoClass name = new DemoClass();
		    		userofDDOSMachine=getUserOftheUnderDDOSMachine().localDisk;
		    		underDDOS=getUnderDDOSMachine().localDisk;
			    	Attacker = iaas.machines.get(random.nextInt(n)).localDisk;
			    	long begin = Timed.getFireCount();
		    		
			    				    	
			    	try {
						NetworkNode.initTransfer(s, ResourceConsumption.unlimitedProcessing, underDDOS, userofDDOSMachine, new Timer());
						begin++;
			    	} catch (NetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   
			    	try {
						NetworkNode.initTransfer(s, ResourceConsumption.unlimitedProcessing, Attacker, underDDOS, new Timer());
						begin++;
			    	} catch (NetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			    	try {
						
			    		NetworkNode.initTransfer(s, ResourceConsumption.unlimitedProcessing, underDDOS, Attacker, new Timer());
			    		begin++;
			    	} catch (NetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    	Timed.simulateUntilLastEvent();
			    	long end = Timed.getFireCount(); 
			    	name.getValue();
			    	return;
			    	
		    	//long time = Timed.getFireCount();
		    	//long exec = 80000;
		    	
		    	//System.out.println("Attacker " + n + ":"  + Attacker);
		    	//System.out.println((Timed.getFireCount()) + "ms\n");
		    	//System.out.println("DDOS Machine: " + underDDOS);
		    	}
		    } 

	
	public static int machineCount=1000;
	public static int transferCount=1000;
	public static String newxml;
    public static String xmlSchema;
    public static IaaSService cloud = null;
    public static IaaSService clouder;
    public static NetworkNode underDDOS;
    public static NetworkNode userofDDOSMachine;
    public static IaaSService iaas;
    public static NetworkNode Attacker;
    
	public static void main(String[] args) throws Exception {	
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
	     * 250w-750w
	     * */
	    	    
	    List<String> maxPowerPM = new ArrayList<String>();
	    maxPowerPM.add("250");
	    maxPowerPM.add("350");
	    maxPowerPM.add("450");
	    maxPowerPM.add("550");
	    maxPowerPM.add("650");
	    maxPowerPM.add("750");
	    
	    /**
	     * Power max array 
	     * 25w-75w
	     * */
	    
	    List<String> idlePowerPM = new ArrayList<String>();
	    idlePowerPM.add("25");
	    idlePowerPM.add("35");
	    idlePowerPM.add("45");
	    idlePowerPM.add("55");
	    idlePowerPM.add("65");
	    idlePowerPM.add("75");
	    
	    /**
	     * speed of the processor is counted in MHz meaning 1MHz = 1 cycle per second 
	     * */
	    List<String> processSpeed = new ArrayList<String>();
	    processSpeed.add("1000");
	    processSpeed.add("2000");
	    processSpeed.add("4000");
	    processSpeed.add("6000");
	    
	    /**
	     * speed of bandwidth is measured in bits per seconds 
	     * */
	    
	    List<String> inBW = new ArrayList<String>();
	    inBW.add("100000");
	    inBW.add("200000");
	    inBW.add("300000");
	    inBW.add("400000");
	    
	    List<String> outBW = new ArrayList<String>();
	    outBW.add("100000");
	    outBW.add("200000");
	    outBW.add("300000");
	    outBW.add("400000");
	    
	    List<String> diskBW = new ArrayList<String>();
	    diskBW.add("50000");
	    diskBW.add("100000");
	    diskBW.add("200000");
	    diskBW.add("300000");
	    
	    /** 
	     * End of bandwidth section
	     * */
	    
	    /**
	     * Repository ID = randomly named.
	     * */
	    
	    /**
	     * End of repository array
	     * */
	    
	    /**
	     * @xml
	     * This is the final stage of creation of the physical machines
	     * There is use of the Random(); function which is preinstalled into
	     * java. There is then a loop that iterates through the List Arrays
	     * and randomly picks a value from each one and returns the values
	     * In the output there is names for each component. The for loop iterates
	     * through the arrays 1000 times and chooses 1000 random values to generate
	     * 1000 physical machines.
	     * */
	    
	    /**
	     * @xml 
	     * 		This part of the code is creating 1 xml, 1 cloud and creating 1000
	     * 		machines within the xml file which have randomised values
	     * */
	    Random random = new Random();
	    try (FileWriter file = new FileWriter("PM.xml")) {
	    xmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
	    		+ "<cloud id=\"oxygen-1\"	scheduler=\"hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.FirstFitScheduler\" pmcontroller=\"hu.mta.sztaki.lpds.cloud.simulator.iaas.pmscheduling.AlwaysOnMachines\">\n";
	    file.write(xmlSchema);
	    //System.out.println(xmlSchema);
	    	    
	    List<String> resp = new ArrayList<String>();
	    resp.add("hydrogen" + (random.nextInt(machineCount)));
	    resp.add("carbon" + (random.nextInt(machineCount)));
	    resp.add("neon" + (random.nextInt(machineCount)));
	    resp.add("kepler"+ (random.nextInt(machineCount)));
	    
	    for (int x = 0; x < machineCount; x++) {    	    
			newxml = 
			"\t<machine id=\"" + (x+1) + "\" cores=\"" + (cores.get(random.nextInt(cores.size()))) 
			+"\" processing=\"" + (processSpeed.get(random.nextInt(processSpeed.size()))) 
			+ "\" memory=\"" + (diskSpace.get(random.nextInt(diskSpace.size()))) + "\">\n"
			
			+ "\t\t\t<powerstates kind=\"host\">\n"
			
			+ "\t\t\t\t<power	model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" "
			+ "idle=\"" + idlePowerPM.get(random.nextInt(idlePowerPM.size())) 
			+ "\" max=\"" + maxPowerPM.get(random.nextInt(maxPowerPM.size())) 
			+ "\" inState=\"default\" />\n"
			
			+ "\t\t\t\t<power	model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" "
			+ "idle=\"" + idlePowerPM.get(random.nextInt(idlePowerPM.size())) 
			+ "\" max=\"" + maxPowerPM.get(random.nextInt(maxPowerPM.size())) + "\" inState=\"OFF\" />\n"
			
			+ "\t\t\t</powerstates>\n"
			
			+ "\t\t<statedelays startup=\"89000\" shutdown=\"29000\" />\n"
			
			+ "\t\t<repository id=\""+ (x+1) + "\" capacity=\"" + diskSpace.get(random.nextInt(diskSpace.size())) 
			+ "\" inBW=\"" + inBW.get(random.nextInt(inBW.size())) + "\" outBW=\"" 
			+ outBW.get(random.nextInt(outBW.size())) + "\" diskBW=\"" + diskBW.get(random.nextInt(diskBW.size())) + "\">\n"
			
			+ "\t\t\t<powerstates kind=\"storage\">\n"
			+ "\t\t\t\t<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" "
			+ "idle=\"" + idlePowerPM.get(random.nextInt(idlePowerPM.size())) + "\" "
					+ "max=\"" + maxPowerPM.get(random.nextInt(maxPowerPM.size()))  + "\" inState=\"default\" />\n"
			+ "\t\t\t\t<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "\t\t\t</powerstates>\n"
			
			+ "\t\t\t<powerstates kind=\"network\">\n"
			+ "\t\t\t\t<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.LinearConsumptionModel\" "
			+ "idle=\"" + idlePowerPM.get(random.nextInt(idlePowerPM.size())) + "\" "
					+ "max=\"" + maxPowerPM.get(random.nextInt(maxPowerPM.size()))  +  "\" inState=\"default\" />\n"
			+ "\t\t\t\t<power model=\"hu.mta.sztaki.lpds.cloud.simulator.energy.powermodelling.ConstantConsumptionModel\" idle=\"0\" max=\"0\" inState=\"OFF\" />\n"
			+ "\t\t\t</powerstates>";

			file.write(newxml);
			
			for (int j = 0; j < machineCount; j++) {
		        String lat = "\t\t\t<latency towards=\"" + (j+1) + "\" value=\"5\"/>";     
		        //System.out.println(lat);
		        file.write(lat);
		        }
			String EOxmlF = "\t\t</repository>\n"
					+ "\t</machine>";
	        file.write(EOxmlF);

	    }   
	       	    
	    String cloudEnd = "</cloud>";
	    file.write(cloudEnd);
	    file.close();
	    //System.out.println(cloudEnd);
	    
	    iaas = CloudLoader.loadNodes("C:\\Users\\Tom\\git\\dissect-cf-examples\\PM.xml");
	    
	    //clouder = CloudLoader.loadNodes("PM.xml");
	 
	    
	    
	  }
	    
	}

	
}