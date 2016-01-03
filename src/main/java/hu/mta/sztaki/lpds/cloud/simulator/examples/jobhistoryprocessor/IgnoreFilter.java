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
 *  (C) Copyright 2015, Gabor Kecskemeti (kecskemeti.gabor@sztaki.mta.hu)
 */
package hu.mta.sztaki.lpds.cloud.simulator.examples.jobhistoryprocessor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.TraceFilter.Acceptor;

/**
 * Allows some of the jobs to be excluced from a trace based on a list in a
 * file. A single list item is expected to be specified in a single line of the
 * file. The line could be in any format but it should contain the word
 * 'IGNORED' at the end of the line and it should start with the job's id word.
 * It is assumed that the jobid does not contain spaces.
 * 
 * @author "Gabor Kecskemeti, Laboratory of Parallel and Distributed Systems, MTA SZTAKI (c) 2015"
 */
public class IgnoreFilter implements Acceptor {
	ArrayList<String> ids = new ArrayList<String>();

	/**
	 * Reads and parses the ignore list file stores the to be ignored list in
	 * the data member named 'ids'.
	 * 
	 * @param file
	 *            the file to be parsed
	 * @throws IOException
	 *             if there were some file handling issues
	 */
	public IgnoreFilter(String file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		String line = null;
		while ((line = raf.readLine()) != null) {
			String[] lineparts = line.split(" ");
			if (lineparts[lineparts.length - 1].equals("IGNORED")) {
				ids.add(lineparts[1]);
			}
		}
		raf.close();
	}

	/**
	 * Determines if the particular jobid is listed amongst the ignored ones.
	 */
	@Override
	public boolean accept(Job j) {
		return !ids.contains(j.getId());
	}
}
