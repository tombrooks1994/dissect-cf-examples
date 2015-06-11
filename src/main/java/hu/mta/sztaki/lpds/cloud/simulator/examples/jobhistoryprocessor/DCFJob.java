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

/**
 * Implements the abstract methods of the Job class for the DISSECT-CF
 * simulator. Basically allows the recording of simulation time to the original
 * job representation.
 * 
 * @author 
 *         "Gabor Kecskemeti, Laboratory of Parallel and Distributed Systems, MTA SZTAKI (c) 2012"
 */
public class DCFJob extends Job {

	/**
	 * Forwards the constructor to the Job's appropriate constructor
	 * 
	 * @param jobLine
	 */
	public DCFJob(String jobLine) {
		super(jobLine);
	}

	/**
	 * Forwards the constructor to the Job's appropriate constructor
	 * 
	 * @param submit
	 * @param queue
	 * @param exec
	 * @param nprocs
	 * @param user
	 * @param executable
	 */
	public DCFJob(long submit, long queue, long exec, int nprocs, String user,
			String executable) {
		super(submit, queue, exec, nprocs, user, executable);
	}

	/**
	 * Records the duration this job spent in the queue.
	 */
	public void started() {
		setRealqueueTime(Timed.getFireCount() / 1000 - getSubmittimeSecs());
	}

	/**
	 * Records the duration this job was executed for.
	 */
	public void completed() {
		setRan(true);
		setRealstopTime(Timed.getFireCount() / 1000 - getSubmittimeSecs());
	}
}
