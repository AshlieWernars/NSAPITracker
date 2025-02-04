package com.Main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.ArrayList;

import com.FileIO.FileLoggers.Logger;
import com.TrainTracking.DisruptionLogger;
import com.TrainTracking.MaterialLogger;
import com.TrainTracking.TrainLogger;

public class Main extends Canvas {

	private static final long serialVersionUID = 1L;

	// Classes
	private final TrainLogger trainLogger;
	private final MaterialLogger materialLogger;
	private final DisruptionLogger disruptionLogger;

	@SuppressWarnings("unused")
	public Main() {
		trainLogger = new TrainLogger();
		trainLogger.start();

		materialLogger = new MaterialLogger();
		materialLogger.start();

		disruptionLogger = new DisruptionLogger();
		disruptionLogger.start();

		new Display("NS API Tracker", this);

		run();
	}

	public void run() {
		try {
			ConsoleCapture.redirectConsoleOutput();
		} catch (IOException e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}

		// Create BufferStrategy once
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}

		// Cache the FontMetrics to avoid recalculating
		Font font = new Font("Monospaced", Font.PLAIN, 9);
		FontMetrics fm = null;

		while (true) {
			Graphics2D g2d = (Graphics2D) bs.getDrawGraphics(); // Use Graphics2D for better rendering control
			g2d.setFont(font);

			// Cache font metrics only once
			if (fm == null) {
				fm = g2d.getFontMetrics();
			}

			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, 1366, 768);

			g2d.setColor(Color.white);

			g2d.drawString(Logger.getCurrentTime(), 1200, 15);

			drawMultilineString(g2d, Info.getActiveDisruptions(), 0, 15, fm);

			g2d.drawString("Console:", 0, 280);

			drawMultilineString(g2d, ConsoleCapture.getLastLinesAsString(), 0, 300, fm);

			g2d.drawString("Amount of active material: " + Info.getActiveMaterialCounter(), 325, 280);

			g2d.drawString("Last time updated: " + Info.getLastTimeUpdated(), 525, 280);

			drawActiveMaterial(g2d, Info.getActiveMaterial(), 325, 300, fm);

			bs.show();
			g2d.dispose();

			// Add a small delay to control frame rate (~60 FPS)
			try {
				Thread.sleep(16); // 16ms = ~60fps
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void drawActiveMaterial(Graphics2D g, ArrayList<Integer> activeMaterialIntList, int x, int y, FontMetrics fm) {
		if (activeMaterialIntList == null) {
			return;
		}

		int lineHeight = fm.getHeight();

		StringBuilder activeMaterial = new StringBuilder();
		for (Integer number : activeMaterialIntList) {
			activeMaterial.append(number).append("\n");
		}

		String[] lines = activeMaterial.toString().split("\n");
		int i = 0;
		for (String line : lines) {
			g.drawString(line, x, y + (i * lineHeight));

			i++;

			if (i == 35) {
				x += 50;
				i = 0;
			}
		}
	}

	private void drawMultilineString(Graphics2D g, String text, int x, int y, FontMetrics fm) {
		int lineHeight = fm.getHeight();

		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], x, y + (i * lineHeight));
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new Main();
	}
}