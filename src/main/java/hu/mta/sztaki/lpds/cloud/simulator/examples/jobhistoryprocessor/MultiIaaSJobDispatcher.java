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
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.JobListAnalyser;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.GenericTraceProducer;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple trace processor that creates as many VMs in the cloud as many is
 * required to host single jobs (e.g., if the job requires 1024 processors then
 * it will create 16 VMs with 64 cores if the PM with the largest size in the
 * cloud could host 64 core VMs). After the VMs are created the jobs are sent to
 * them. After the jobs terminate their hosting VMs are also terminated. The
 * VMIs for the VMs are assumed to be capable of running all the jobs in the
 * trace.
 * 
 * @author 
 *         "Gabor Kecskemeti, Laboratory of Parallel and Distributed Systems, MTA SZTAKI (c) 2012-5"
 */
public class MultiIaaSJobDispatcher extends Timed {

	/**
	 * Allows the termination of the processing of the trace
	 */
	private boolean isStopped = false;
	/**
	 * The list of jobs (i.e., the trace) in a more rapidly processable form
	 */
	protected Job[] jobs;
	/**
	 * The first unprocessed job in the trace
	 */
	protected int minindex = 0;
	/**
	 * the iaas services to be used for executing the jobs
	 */
	protected List<IaaSService> target;
	/**
	 * the list of repositories that belong to the target iaas services.
	 */
	protected List<Repository> repo;
	/**
	 * the virtual appliance that will be used as the generic image for each VM
	 * in the clouds
	 */
	protected VirtualAppliance va;
	/**
	 * the first submission time
	 */
	protected long minsubmittime;
	/**
	 * maximum number of physical machines
	 */
	protected long maxmachinecores = 0;
	/**
	 * number of jobs ignored
	 */
	protected long ignorecounter = 0;
	/**
	 * number of VMs destroyed by this dispatcher - i.e., number of jobs
	 * completed
	 */
	protected long destroycounter = 0;
	/**
	 * the default processing power share to be requested during the resource
	 * allocation for the VMs - allows under-provisioning
	 */
	protected double useThisProcPower = 0.001;
	/**
	 * the processing power specified before for the single core of the VM
	 * should be guaranteed
	 */
	protected boolean isMinimumProcPower = false;
	/**
	 * current IaaS service to be used for VM creation
	 */
	private int targetIndex = 0;

	/**
	 * Dispatcher setup. Fetches all jobs from the given trace producer and
	 * analyzes them to fill out min- and max-submit-times. Finally it analyzes
	 * and prepares the target IaaS services. The analysis is targeted at
	 * finding the PM capacity characteristics of each IaaS, while the
	 * preparatory step ensures the availability of the VA to be used for
	 * instantiating the VMs for the jobs.
	 * 
	 * WARNING: only uniformly prepared IaaS systems are supported right now
	 * (i.e. all of them having the same amount of PMs and all of their PMs are
	 * constructed with the same amount of resources)
	 * 
	 * @param producer
	 *            the trace
	 * @param target
	 *            the iaas systems to be used for submitting the trace to
	 */
	public MultiIaaSJobDispatcher(GenericTraceProducer producer,
			List<IaaSService> target) {
		this.target = target;
		// Collecting the jobs
		List<Job> jobs = producer.getAllJobs();

		// Ensuring they are listed in submission order
		Collections.sort(jobs, JobListAnalyser.submitTimeComparator);
		// Analyzing the jobs for min and max submission time
		minsubmittime = JobListAnalyser.getEarliestSubmissionTime(jobs);
		// Transforming the job list for rapid access arrays:
		this.jobs = jobs.toArray(new Job[0]);
		jobs.clear();

		// Preparing the repositories with VAs
		repo = new ArrayList<Repository>(target.size());
		va = new VirtualAppliance("test", 30, 0, false, 100000000);
		for (IaaSService iaas : target) {
			Repository currentRepo = iaas.repositories.get(0);
			repo.add(currentRepo);
			// actually registering the VA
			currentRepo.registerObject(va);
			// determining the maximum number of CPU cores available in a PM
			for (PhysicalMachine pm : iaas.machines) {
				double cores = pm.getCapacities().requiredCPUs;
				if (cores > maxmachinecores) {
					maxmachinecores = (long) cores;
				}
			}
		}

		// Ensuring we will receive a notification once the first job should be
		// submitted
		subscribe(minsubmittime * 1000 - Timed.getFireCount());
	}

	/**
	 * Handling the jobs one by one when they are due.
	 */
	@Override
	public void tick(final long currTime) {
		// One ore more jobs must be submitted as we received this event
		for (int i = minindex; i < jobs.length; i++) {
			final Job toprocess = jobs[i];
			long submittime = toprocess.getSubmittimeSecs() * 1000;
			if (currTime == submittime) {
				// the ith job is due now

				// to fulfill the ith job's cpu core requirements we need the
				// following set of VMs with the following number of CPUs
				final int requestedinstances = maxmachinecores >= toprocess.nprocs ? 1
						: (toprocess.nprocs / ((int) maxmachinecores))
								+ ((toprocess.nprocs % (int) maxmachinecores) == 0 ? 0
										: 1);
				final double requestedprocs = (double) toprocess.nprocs
						/ requestedinstances;

				// Starting the VM for the job
				try {
					final VirtualMachine[] vms = target.get(targetIndex)
							.requestVM(
									va,
									new ResourceConstraints(requestedprocs,
											useThisProcPower,
											isMinimumProcPower, 512000000),
									repo.get(targetIndex), requestedinstances);

					// doing a round robin scheduling for the target
					// infrastructures
					targetIndex++;
					if (targetIndex == target.size()) {
						targetIndex = 0;
					}

					final ArrayList<Boolean> ignoreMarker = new ArrayList<Boolean>();
					for (final VirtualMachine vm : vms) {
						// The received VM set (which supposed to run the ith
						// job) is now processed
						if (vm.getState().equals(
								VirtualMachine.State.NONSERVABLE)) {
							// the job is not servable because it would need
							// more resources than the target cloud could offer
							// in total.
							ignorecounter++;
							break;
						}
						// Ensuring we receive state dependent events about the
						// new VMs
						vm.subscribeStateChange(new VirtualMachine.StateChange() {
							@Override
							public void stateChanged(
									VirtualMachine.State oldState,
									VirtualMachine.State newState) {
								// If the dispatching process was cancelled
								if (isStopped) {
									switch (newState) {
									case NONSERVABLE:
									case DESTROYED:
									case INITIAL_TR:
										// OK
										break;
									default:
										try {
											vm.destroy(true);
										} catch (VMManager.VMManagementException ex) {
											// Ignore as we want to get rid of
											// the VM
										}
									}
									return;
								}

								// If we can go further with the dispatching
								// process
								if (newState
										.equals(VirtualMachine.State.NONSERVABLE)
										&& ignoreMarker.isEmpty()) {
									// Still some ignored VMs are detected.
									ignorecounter++;
									ignoreMarker.add(true);
								}

								// Now to the real business of having a VM that
								// is actually capable of running the job
								if (newState
										.equals(VirtualMachine.State.RUNNING)) {
									try {
										// Mark that we start the job / no
										// further queuing
										toprocess.started();
										// run the job's relevant part in the VM
										vm.newComputeTask(
												toprocess.getExectimeSecs()
														* vm.getResourceAllocation().allocated.requiredCPUs,
												ResourceConsumption.unlimitedProcessing,
												new ResourceConsumption.ConsumptionEvent() {
													/**
													 * Event handler for the
													 * completion of the job
													 */
													@Override
													public void conComplete() {
														// everything went
														// smoothly we mark it
														// in the job
														toprocess.completed();
														try {
															// the VM is no
															// longer needed
															vm.destroy(false);
															destroycounter++;
														} catch (VMManager.VMManagementException e) {
															System.err
																	.println("VM could not be destroyed after compute task completes.");
															e.printStackTrace();
															System.exit(1);
														}
													}

													@Override
													public void conCancelled(
															ResourceConsumption problematic) {
														// just ignore this has
														// happened :)

														// In the current setup
														// we are not supposed
														// to have failing jobs
													}
												});
									} catch (Exception e) {
										e.printStackTrace();
										System.exit(1);
									}
								}
							}
						});
					}
				} catch (VMManager.VMManagementException e) {
					// VM cannot be served because of too large resource request
					ignorecounter++;
				} catch (Exception e) {
					System.err.println("Unknown VM creation error: "
							+ e.getMessage());
					e.printStackTrace();
					ignorecounter++;
				}
				minindex = i + 1;
			} else if (currTime < submittime) {
				// the ith job is not due yet, we have to ask for a new
				// notification which will arrive when the ith job is due
				updateFrequency(submittime - currTime);
				break;
			}
		}
		if (minindex == jobs.length) {
			// No more jobs are listed in the trace, we can just make sure no
			// further events are coming to this dispatcher
			unsubscribe();
		}
	}

	/**
	 * Collects the earilest submission time for the trace
	 * 
	 * @return
	 */
	public long getMinsubmittime() {
		return minsubmittime;
	}

	/**
	 * Tells how many jobs were unservable in the target clouds
	 * 
	 * @return
	 */
	public long getIgnorecounter() {
		return ignorecounter;
	}

	/**
	 * tells how many VMs executed their tasks successfully (and then
	 * successively how many of them got destroyed)
	 * 
	 * @return
	 */
	public long getDestroycounter() {
		return destroycounter;
	}

	/**
	 * Sets the processing power related requirements for the resource
	 * allocation requests for all VMs.
	 * 
	 * @param usableProcPower
	 *            the CPU's processing power instructions/ms
	 * @param minimum
	 *            is it the minimum required or the total you want to specify
	 */
	public void setUsableProcPower(final double usableProcPower,
			final boolean minimum) {
		this.useThisProcPower = usableProcPower;
		isMinimumProcPower = minimum;
	}

	/**
	 * Do not continue the trace processing, terminate all activities as soon as
	 * possible.
	 */
	public void stopTraceProcessing() {
		unsubscribe();
		isStopped = true;
	}
}
