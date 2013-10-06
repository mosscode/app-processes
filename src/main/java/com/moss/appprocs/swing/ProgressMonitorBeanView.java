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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
class ProgressMonitorBeanView extends JPanel {

	private JButton stopButton;
	private JLabel icon;
	private JButton commandButton;
	private JProgressBar progressBar;
	private JLabel descriptionLabel;
	private JLabel tasknameLabel;
	/**
	 * Create the panel
	 */
	public ProgressMonitorBeanView() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0,0,0};
		setLayout(gridBagLayout);

		icon = new JLabel();
		icon.setFont(new Font("SansSerif", Font.PLAIN, 12));
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(icon, gridBagConstraints);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 1;
		add(panel, gridBagConstraints_1);

		tasknameLabel = new JLabel();
		tasknameLabel.setFont(new Font("Sans", Font.BOLD, 12));
		tasknameLabel.setText("TaskName");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 0;
		panel.add(tasknameLabel, gridBagConstraints_2);

		descriptionLabel = new JLabel();
		descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		descriptionLabel.setText("Description");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.insets = new Insets(0, 10, 5, 0);
		gridBagConstraints_3.gridy = 1;
		gridBagConstraints_3.gridx = 0;
		panel.add(descriptionLabel, gridBagConstraints_3);

		progressBar = new JProgressBar();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 10, 5, 5);
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 0;
		panel.add(progressBar, gridBagConstraints_5);

		stopButton = new JButton();
		stopButton.setText("Stop");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 10, 0, 5);
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 2;
		add(stopButton, gridBagConstraints_6);

		commandButton = new JButton();
		commandButton.setText("Action");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 5, 0, 5);
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 3;
		add(commandButton, gridBagConstraints_4);
		
	}
	public JLabel getLabelTaskname() {
		return tasknameLabel;
	}
	public JLabel getLabelDescription() {
		return descriptionLabel;
	}
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	public JButton getCommandButton() {
		return commandButton;
	}
	public JLabel getIcon() {
		return icon;
	}
	public JButton stopButton() {
		return stopButton;
	}

}
