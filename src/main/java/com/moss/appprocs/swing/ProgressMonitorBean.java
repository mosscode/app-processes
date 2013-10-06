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
package com.moss.appprocs.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appprocs.ApplicationProcess;
import com.moss.appprocs.ApplicationProcess.State;
import com.moss.appprocs.UnstoppableProcessException;
import com.moss.appprocs.dummy.TestProcess;
import com.moss.appprocs.progress.BasicProgressReport;
import com.moss.appprocs.progress.ProgressReport;
import com.moss.appprocs.progress.ProgressReportVisitor;
import com.moss.appprocs.progress.TrackableProgressReport;

/**
 * A simple Swing component which provides a graphical indication of the state
 * of an {@link ApplicationProcess}
 */
public class ProgressMonitorBean extends ProgressMonitorBeanView implements Runnable{
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());
	
	private final ApplicationProcess process;
	private Action completionAction;
	
	/**
	 * Using this constructor will cause the component to display a button apon
	 * process completion which will be assosciated with the passed Action.
	 * <br>
	 * @param process The process to monitor.
	 * @param completionAction An action to assosciate with the completion action button.
	 */
	public ProgressMonitorBean(ApplicationProcess process, Action completionAction){
		this(process);
		this.completionAction = completionAction;
	}
	
	/**
	 * Creates a basic component which simply displays the status of the process passed.
	 */
	public ProgressMonitorBean(final ApplicationProcess process){
		this.process = process;
		super.getProgressBar().setStringPainted(true);
		
		stopButton().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					process.stop();
				} catch (UnstoppableProcessException e1) {
					JOptionPane.showMessageDialog(ProgressMonitorBean.this, "Process could not be stopped: " + e1.getMessage());
				}
			}
		});
		
		update();
		
		getCommandButton().setVisible(false);
		
		new Thread(this).start();
	}
	
	
	private void update(){
		if(log.isDebugEnabled()) log.debug("Updating " + process.name());
			
		ProgressReport report = process.progressReport();
		
		super.getLabelTaskname().setText(process.name());
		super.getLabelDescription().setText(process.description());
		
		final JProgressBar pBar = getProgressBar();
		
		String string = report.accept(new ProgressReportVisitor<String>() {
			public String visit(BasicProgressReport r) {
				getCommandButton().setEnabled(false);
				pBar.setIndeterminate(true);
				return r.describe() + "...";
			}
			public String visit(TrackableProgressReport t) {
				pBar.setIndeterminate(false);
				pBar.setMinimum(0);
				pBar.setMaximum((int)t.length());
				pBar.setValue((int)t.progress());
				return t.describe();
			}
		});
		
		if(process.state()==State.STOPPABLE){
			stopButton().setEnabled(true);
		}else{
			stopButton().setEnabled(false);
		}
		
		pBar.setString(string);
	}
	
	public void run() {
		while(process.state() != State.STOPPED){
			try {
				Thread.sleep(50);
				update();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		if(completionAction!=null){
			super.getCommandButton().setAction(completionAction);
		}else{
			super.getCommandButton().setEnabled(false);
		}
		super.getProgressBar().setEnabled(false);
		super.getProgressBar().setIndeterminate(false);
		super.getProgressBar().setMinimum(0);
		super.getProgressBar().setMaximum(1);
		super.getProgressBar().setValue(1);
		super.getProgressBar().setString("Complete");
		if(completionAction!=null){
			getCommandButton().setVisible(true);
			stopButton().setVisible(false);
		}
	}


	public static void main(String[] args) {
		JFrame window = new JFrame("ProcessTest");
		window.getContentPane().add(new ProgressMonitorBean(new TestProcess()));
		window.setSize(400,100);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
