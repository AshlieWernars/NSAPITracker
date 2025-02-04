package com.Main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Display {

	public Display(String title, Main main) {
		JFrame frame = new JFrame(title);
		frame.setUndecorated(true); // Remove title bar
		frame.setResizable(false); // Prevent resizing

		// Get screen size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		gs.setFullScreenWindow(frame); // Set fullscreen

		frame.add(main);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.requestFocus();
	}
}