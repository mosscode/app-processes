/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of app-processes.
 *
 * app-processes is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * app-processes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with app-processes; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.appprocs.dummy;

import java.util.Random;

import com.moss.appprocs.ApplicationProcess;
import com.moss.appprocs.UnstoppableProcessException;
import com.moss.appprocs.progress.BasicProgressReport;
import com.moss.appprocs.progress.ProgressReport;
import com.moss.appprocs.progress.TrackableProgressReport;

public class TestProcess implements ApplicationProcess {
	private State state = State.STOPPABLE;
	private ProgressReport report;

	public TestProcess() {
		report = new BasicProgressReport("Plotting Course");
		new Thread() {

			public void run() {
				try {
					Random r = new Random();
					for(int x=0; state!=ApplicationProcess.State.STOPPING && x < 5; x++){
						Thread.sleep((r.nextInt(5)+1)*200);
					}
					if(state!=ApplicationProcess.State.STOPPING){
						state = ApplicationProcess.State.UNSTOPPABLE;
						int length = 5;
						for (int progress =0; state!=ApplicationProcess.State.STOPPING &&  progress < length; progress++) {
							System.out.println("progress: " + progress);
							int sleepSeconds = 
								//							r.nextInt(3) + 
								1;
							System.out.println("Sleep seconds:" + sleepSeconds);
							report = new TrackableProgressReport(length, progress, "traveling", "parsecs");
							Thread.sleep(sleepSeconds * 1000);
							
						}
					}
					
					System.out.println("Stopped");
					state = ApplicationProcess.State.STOPPED;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public String description() {
		return "A process suitable for testing process monitors";
	}

	public String name() {
		return "Test Process";
	}

	public ProgressReport progressReport() {
		return report;
	}

	public State state() {
		return state;
	}

	public void stop() throws UnstoppableProcessException {
		if(state==State.STOPPABLE){
			System.out.println("Stopping");
			state = State.STOPPING;
		}else{
			throw new UnstoppableProcessException("This test process isn't stoppable");
		}
	}
}
