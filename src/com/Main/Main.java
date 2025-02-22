package com.Main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

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
	private final List<String> existingImagePaths;

	@SuppressWarnings("unused")
	public Main() {
		trainLogger = new TrainLogger();
		trainLogger.start();

		materialLogger = new MaterialLogger();
		materialLogger.start();

		disruptionLogger = new DisruptionLogger();
		disruptionLogger.start();

		existingImagePaths = loadExistingImagePaths();

		Collections.shuffle(existingImagePaths);

		new Display("NS API Tracker", this);

		run();
	}

	public List<String> loadExistingImagePaths() {
		List<String> existingPaths = new ArrayList<>();
		File baseDir = new File(Info.getSavefilepath() + "/images/");

		if (baseDir.exists() && baseDir.isDirectory()) {
			scanDirectory(baseDir, existingPaths);
		}
		return existingPaths;
	}

	private void scanDirectory(File dir, List<String> existingPaths) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				scanDirectory(file, existingPaths);
			} else if (isImageFile(file)) {
				existingPaths.add(file.getAbsolutePath());
			}
		}
	}

	private boolean isImageFile(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".jpg");
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

		int i = 0;
		int imageIndex = 0;
		BufferedImage image = null;

		try {
			image = ImageIO.read(new File(existingImagePaths.get(imageIndex)));
		} catch (IOException e) {
			e.printStackTrace();
		}

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

			g2d.drawString(Logger.getCurrentTime(), 1180, 15);

			drawMultilineString(g2d, Info.getActiveDisruptions(), 0, 15, fm);

			g2d.drawString("Console:", 0, 280);

			drawMultilineString(g2d, ConsoleCapture.getLastLinesAsString(), 0, 300, fm);

			g2d.drawString("Amount of active material: " + Info.getActiveMaterialCounter(), 325, 280);

			g2d.drawString("Last time updated: " + Info.getLastTimeUpdated(), 525, 280);

			drawActiveMaterial(g2d, Info.getActiveMaterial(), 325, 300, fm);

			if (i >= 300) { // Every 5 sec new image
				imageIndex++;
				if (imageIndex >= existingImagePaths.size() - 1) {
					Collections.shuffle(existingImagePaths);
					imageIndex = 0;
				}

				try {
					image = ImageIO.read(new File(existingImagePaths.get(imageIndex)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				i = 0;
			}

			int maxWidth = 400;  // Desired maximum width
			int maxHeight = 400; // Desired maximum height

			if (image != null) {
			    int imgWidth = image.getWidth();
			    int imgHeight = image.getHeight();

			    // Calculate the scaling factor to maintain aspect ratio
			    double widthRatio = (double) maxWidth / imgWidth;
			    double heightRatio = (double) maxHeight / imgHeight;
			    double scaleFactor = Math.min(widthRatio, heightRatio); // Use the smaller ratio to fit within bounds

			    // Compute new scaled dimensions
			    int scaledWidth = (int) (imgWidth * scaleFactor);
			    int scaledHeight = (int) (imgHeight * scaleFactor);

			    // Draw the image with the new scaled dimensions
			    g2d.drawImage(image, 925, 50, scaledWidth, scaledHeight, null);
			}

			bs.show();
			g2d.dispose();

			i++;

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