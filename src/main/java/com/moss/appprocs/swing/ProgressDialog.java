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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appprocs.ApplicationProcess;
import com.moss.appprocs.NewProcessWatcher;
import com.moss.appprocs.dummy.TestProcess;

/**
 * A modal dialog that will stay visible (blocking) as long as those 
 * processes on which it is configured to block on are still running.
 */
public class ProgressDialog implements NewProcessWatcher {
	public static void main(String[] args) {
		final JButton button = new JButton("Go");
		final JFrame f = new JFrame("Test Frame");
		f.getContentPane().add(button);
		f.setLocationRelativeTo(null);
		f.setSize(800, 600);
		f.setVisible(true);
		
		final ProgressDialog reporter = new ProgressDialog(button);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reporter.report(new TestProcess(), true);
			}
		});
	}
	
	private final Log log = LogFactory.getLog(getClass());
	
	private ProgressMonitorPanel panel = new ProgressMonitorPanel(true);
	private JDialog dialog;
	private List<ApplicationProcess> blockingProcesses = new LinkedList<ApplicationProcess>();
	private MonitorThread monitorThread = new MonitorThread();
	
	public ProgressDialog(Component owner) {
		dialog = makeDialogFor(owner);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.getContentPane().add(panel);
		dialog.setSize(640, 480);
		dialog.setLocationRelativeTo(owner);
		dialog.setModal(true);
		dialog.setTitle("Progress Monitor");
	}
	
	private static JDialog makeDialogFor(Component c){
		Window w = SwingUtilities.windowForComponent(c);
		if(w == null){
			throw new RuntimeException("The component heirarchy for the passed component does not end at a window:" + c);
		}else if(w instanceof Frame){
			return new JDialog((Frame)w);
		}else if(w instanceof Dialog){
			return new JDialog((Dialog)w);
		}else{
			throw new RuntimeException("Unsupported window type:" + w.getClass().getName());
		}
	}
	
	class MonitorThread extends Thread {
		public MonitorThread() {
			super("Progress Monitor Watch Thread");
		}
		public void run() {
			while(true){
				try {
					if(log.isDebugEnabled()) log.debug("Polling");
					List<ApplicationProcess> finished = new LinkedList<ApplicationProcess>();
					for(ApplicationProcess next : blockingProcesses){
						if(next.state()==ApplicationProcess.State.STOPPED){
							if(log.isDebugEnabled()) log.debug("Ended: " + next.name());
							finished.add(next);
						}
					}
					blockingProcesses.removeAll(finished);
					if(log.isDebugEnabled()) log.debug("Polling Complete");
					
					if(blockingProcesses.size()==0){
						if(log.isDebugEnabled()) log.debug("Hiding dialog");
						dialog.setVisible(false);
						synchronized (this) {
							if(log.isDebugEnabled()) log.debug("Sleeping");
							this.wait();
							if(log.isDebugEnabled()) log.debug("Awake");
						}
					}else{
						if(log.isDebugEnabled()) log.debug("Sleeping");
						if(!dialog.isVisible()) setVisible(true);
						Thread.sleep(1000);
						if(log.isDebugEnabled()) log.debug("Awake");
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void wakeUp(){
			synchronized (this) {
				if(isAlive()){
					if(log.isDebugEnabled()) log.debug("Waking-up");
					this.notify();
					if(log.isDebugEnabled()) log.debug("Woken-up");
				}else{
					if(log.isDebugEnabled()) log.debug("Starting monitor");
					start();
					if(log.isDebugEnabled()) log.debug("Started");
				}
			}
		}
	}
	
	private void setVisible(final boolean visible){
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				dialog.setVisible(visible);
			}
		});
	}
	
	public void newProcess(ApplicationProcess p) {
		report(p, true);
	}
	
	private void runOnEventDispatchThread(Runnable r){
		if(SwingUtilities.isEventDispatchThread()){
			r.run();
		}else{
			SwingUtilities.invokeLater(r);
		}
	}
	
	public void report(final ApplicationProcess p, final boolean blocks){
		runOnEventDispatchThread(new Runnable() {
			public void run() {
				if(log.isDebugEnabled()) log.debug("Registering process");
				if(blocks){
					blockingProcesses.add(p);
				}
				panel.addNonPersistentProcess(p);
				if(log.isDebugEnabled()) log.debug("Telling thread to wakeUp()");
				monitorThread.wakeUp();
			}
		});
		
	}
}
