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
package com.moss.appprocs.extras;

import com.moss.appprocs.ApplicationProcess;
import com.moss.appprocs.UnstoppableProcessException;
import com.moss.appprocs.progress.BasicProgressReport;
import com.moss.appprocs.progress.ProgressReport;

/**
 * A simple helper class for creating basic ApplicationProcess references
 */
public class SimpleProcess implements ApplicationProcess{
	private final String name;
	private final String description;
	private State state = State.UNSTOPPABLE;
	private ProgressReport report;
	
	public SimpleProcess(String name, String description){
		this.name = name;
		this.description = description;
		this.report = new BasicProgressReport(name);
	}
	
	public String description() {
		return description;
	}

	public String name() {
		return name;
	}

	public State state() {
		return state;
	}
	
	public void updateProgress(ProgressReport report) {
		this.report = report;
	}
	public ProgressReport progressReport() {
		return report;
	}
	
	public void stopped(){
		state = State.STOPPED;
	}
	
	public void stop() throws UnstoppableProcessException {
		throw new UnstoppableProcessException("This process isn't stoppable");
	}
}
