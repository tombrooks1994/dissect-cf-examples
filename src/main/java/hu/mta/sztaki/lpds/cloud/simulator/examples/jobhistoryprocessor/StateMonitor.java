/*
 *  ========================================================================
 *  DISSECT-CF Examples
 *  ========================================================================
 *  
 *  This file is part of DISSECT-CF Examples.
 *  
 *  DISSECT-CF Examples is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  DISSECT-CF Examples is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with DISSECT-CF Examples.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2013-15, Gabor Kecskemeti (gkecskem@dps.uibk.ac.at,
 *   									  kecskemeti.gabor@sztaki.mta.hu)
 */
package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.energy.specialized.PhysicalMachineEnergyMeter;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Collects and aggregates statistical data representing a particular run of a
 * cloud system.
 * 
 * @author 
 *         "Gabor Kecskemeti, Laboratory of Parallel and Distributed Systems, MTA SZTAKI (c) 2012-5"
 */
class StateMonitor extends Timed {
	/**
	 * All collected data so far
	 */
	private ArrayList<OverallSystemState> monitoringDatabase = new ArrayList<OverallSystemState>();
	/**
	 * Where do we write the data? Allows to have a threshold on the monitoring
	 * database and in the future it can be used to continuously empty the
	 * database to the disk.
	 */
	private BufferedWriter bw;
	/**
	 * The list of energy meters controlled by this state monitor. During a
	 * regular runtime, here we will have a meter for every physical machine in
	 * the system. Please note that the monitor does not recognize the changes
	 * in the set of PMs in an IaaSService.
	 */
	private ArrayList<PhysicalMachineEnergyMeter> meters = new ArrayList<PhysicalMachineEnergyMeter>();
	/**
	 * The dispatcher which sends the jobs to the actual clouds in some VMs. The
	 * dispatcher is expected to unsubscribe from timing events if it is no
	 * longer having jobs to be executed in a trace. The monitor watches this
	 * case and ensures that the monitoring is terminated once the dispatcher is
	 * not pushing new jobs towards the clouds and there are no furhter
	 * activities observable on the cloud becaouse of the dispatcher.
	 */
	final MultiIaaSJobDispatcher dispatcher;
	/**
	 * The list if cloud services that should be monitored by this state
	 * monitor.
	 */
	final List<IaaSService> iaasList;

	/**
	 * Initiates the state monitoring process by setting up the energy meters,
	 * creating the output csv file (called [tracefile].converted) and
	 * subscribing to periodic timing events (for every 5 minutes) so the
	 * metering queires can be made automatically.
	 * 
	 * WARNING: this function keeps a file open until the dispatcher terminates
	 * its operation!
	 * 
	 * @param traceFile
	 *            the name of the output csv (without the .converted extension)
	 * @param dispatcher
	 *            the dispatcher that sends its jobs to the clouds
	 * @param iaasList
	 *            the clouds that needs to be monitored
	 * @param interval
	 *            the energy metering interval to be applied
	 * @throws IOException
	 *             if there was a output file creation error
	 */
	public StateMonitor(String traceFile, MultiIaaSJobDispatcher dispatcher,
			List<IaaSService> iaasList, int interval) throws IOException {
		bw = new BufferedWriter(new FileWriter(traceFile + ".converted"));
		bw.write("UnixTime*1000"
				+ ",NrFinished,NrQueued,VMNum,UsedCores,OnPMs,CentralRepoTX\n");
		System.err.println("Power metering started with delay " + interval);
		this.iaasList = iaasList;
		this.dispatcher = dispatcher;
		for (IaaSService iaas : iaasList) {
			for (final PhysicalMachine pm : iaas.machines) {
				// setting up the energy meters for every PM
				PhysicalMachineEnergyMeter pmMeter = new PhysicalMachineEnergyMeter(
						pm);
				pmMeter.startMeter(interval, false);
				meters.add(pmMeter);
			}
		}
		subscribe(300000); // 5 minutes in ms
	}

	/**
	 * The main event handling mechanism in this periodic state monitor. This
	 * function is called in every 5 simulated minutes.
	 */
	@Override
	public void tick(long fires) {
		// Collecting the monitoring data
		OverallSystemState current = new OverallSystemState();
		final int iaasCount = iaasList.size();
		for (int i = 0; i < iaasCount; i++) {
			IaaSService iaas = iaasList.get(i);
			int msize = iaas.machines.size();
			for (int j = 0; j < msize; j++) {
				PhysicalMachine pm = iaas.machines.get(j);
				current.finishedVMs += pm.getCompletedVMs();
				current.runningVMs += pm.numofCurrentVMs();
				current.usedCores += pm.getCapacities().requiredCPUs
						- pm.getFreeCapacities().requiredCPUs;
				current.runningPMs += pm.isRunning() ? 1 : 0;
			}
			current.queueLen += iaas.sched.getQueueLength();
			current.totalTransferredData += iaas.repositories.get(0).outbws
					.getTotalProcessed();
		}
		current.timeStamp = Timed.getFireCount();
		// Recording it
		monitoringDatabase.add(current);

		// Checking for termination conditions:
		if (!dispatcher.isSubscribed() && current.queueLen == 0
				&& current.runningVMs == 0) {
			// We now terminate
			unsubscribe(); // first we cancel our future events
			// then we cancel the future energy monitoring of all PMs
			for (PhysicalMachineEnergyMeter em : meters) {
				em.stopMeter();
			}
			try {
				// Afterwards we write out the collected statistics to the
				// output csv file
				for (OverallSystemState st : monitoringDatabase) {
					bw.write(st.toString());
				}
				bw.close();
			} catch (IOException e) {
				throw new RuntimeException(
						"Problem with writing out the monitoring database", e);
			}
			System.err.println("State information written "
					+ Calendar.getInstance().getTimeInMillis());
			double sum = 0;
			// finally we collect and aggregate the energy consumption data
			for (PhysicalMachineEnergyMeter m : meters) {
				sum += m.getTotalConsumption();
			}
			// Warning! assuming ms base.
			System.err.println("Total power consumption: " + sum / 1000
					/ 3600000 + " kWh");
		}
	}
}