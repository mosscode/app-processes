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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appprocs.ApplicationProcess;
import com.moss.appprocs.NewProcessWatcher;
import com.moss.appprocs.dummy.TestProcess;
import com.moss.appprocs.dummy.TrackableTestProcess;

/**
 * A ProcessMonitor implementation which displays monitored processes as a vertical list 
 * of ProgressMonitorBeanS, optionally embedded within a JScrollPane.
 * <br>
 * @see com.moss.appprocs.swing.ProgressMonitorBean
 */
public class ProgressMonitorPanel extends JPanel implements NewProcessWatcher{
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(getClass());
	
	/**
	 * The panel which holds the ProgressMonitorBeanS
	 */
	private JPanel monitorsPanel = new ListPanel();
	
	/**
	 * A map keying each ProgressMonitorBean to the
	 * ApplicationProcess it represents
	 */
	private Map<ApplicationProcess, ProgressMonitorBean> processMonitorsMap = new HashMap<ApplicationProcess, ProgressMonitorBean>();
	
	private long finishedActionRemovalDelay = 1500;
	
	/**
	 * Convenience method for ProgressMonitorPanel(true)
	 */
	public ProgressMonitorPanel(){
		this(true);
	}
	
	/**
	 * @param useScrollpane If true, the panel which holds the ProgressMonitorBeanS will be embedded in a JSrollPane
	 */
	public ProgressMonitorPanel(boolean useScrollpane){
		if(useScrollpane){
			JScrollPane scrollpane = new JScrollPane();
			setLayout(new BorderLayout());
			add(scrollpane, BorderLayout.CENTER);
			scrollpane.setViewportView(monitorsPanel);
			monitorsPanel.setLayout(new GridBagLayout());
		}else{
			setLayout(new BorderLayout());
			add(monitorsPanel, BorderLayout.CENTER);
			monitorsPanel.setLayout(new GridBagLayout());
		}
	}
	
	private void addComponent(JComponent c){
		int compNo = monitorsPanel.getComponents().length +1;
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx =0;
		gc.gridy = compNo;
		gc.weightx=1;
		
		monitorsPanel.add(c, gc);
		monitorsPanel.invalidate();
		monitorsPanel.getParent().validate();
		monitorsPanel.repaint();
	}
	
	public void setFinishedActionRemovalDelay(long completedActionRemovalDelay) {
		this.finishedActionRemovalDelay = completedActionRemovalDelay;
	}
	
	public void newProcess(ApplicationProcess p) {
		addNonPersistentProcess(p);
	}
	public void addNonPersistentProcess(ApplicationProcess p, Action completionAction){
		ProgressMonitorBean m = new ProgressMonitorBean(p, completionAction);
		processMonitorsMap.put(p, m);
		addComponent(m);
		new FinishedProcessCleanupThread(p);
	}
	
	public void addNonPersistentProcess(ApplicationProcess p){
		ProgressMonitorBean m = new ProgressMonitorBean(p);
		processMonitorsMap.put(p, m);
		addComponent(m);
		new FinishedProcessCleanupThread(p);
	}
	
	class FinishedProcessCleanupThread extends Thread{
		ApplicationProcess p;
		public FinishedProcessCleanupThread(ApplicationProcess p ){
			this.p = p;
			start();
		}
		public void run(){
			try {
				String label = p.name() + "(" + p + ")";
				if(log.isDebugEnabled()) log.debug("Watching " + label);
				while(p.state() != ApplicationProcess.State.STOPPED){
					if(log.isDebugEnabled()) log.debug(label + " is alive");
					sleep(10);
				}
				if(log.isDebugEnabled()) log.debug(label + " died");
				ProgressMonitorBean m = (ProgressMonitorBean) processMonitorsMap.get(p);
				sleep(finishedActionRemovalDelay);
				m.setVisible(false);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	public void addPersistentProcess(ApplicationProcess p, Action completionAction){
		ProgressMonitorBean m = new ProgressMonitorBean(p, completionAction);
		processMonitorsMap.put(p, m);
		addComponent(m);
	}
	public void addPersistentProcess(ApplicationProcess p){
		ProgressMonitorBean m = new ProgressMonitorBean(p);
		processMonitorsMap.put(p, m);
		addComponent(m);
	}


	@SuppressWarnings("serial")
	class ListPanel extends JPanel implements Scrollable{

		public Dimension getPreferredScrollableViewportSize() {
			return super.getSize();
		}

		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 40;
		}

		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 40;
		}
		
	}
	
	/**
	 * Not useful beyond testing this class.
	 */
	@SuppressWarnings("serial")
	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Action a = new AbstractAction("Do Something"){

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Hi");
			}
			
		};
		JFrame window = new JFrame("Test");
		ProgressMonitorPanel panel = new ProgressMonitorPanel(true);
		panel.addNonPersistentProcess(new TestProcess());
		panel.addNonPersistentProcess(new TestProcess());
		panel.addNonPersistentProcess(new TrackableTestProcess());
		panel.addNonPersistentProcess(new TestProcess());
		panel.addPersistentProcess(new TrackableTestProcess(), a);
//		
//		JPanel panel = new JPanel();
//		panel.add(new JLabel("Howdy"));
//		panel.setBorder(new LineBorder(Color.BLUE, 5));
		
		window.getContentPane().add(panel);
		window.setSize(400, 800);
		window.setVisible(true);

		
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
