package com.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public class ConsoleCapture {

	private static final int MAX_LINES = 30;
	private static final LinkedList<String> lastLines = new LinkedList<>();
	private static PrintStream originalOut = System.out; // Store original System.out

	@SuppressWarnings({ "unused", "resource" })
	public static void redirectConsoleOutput() throws IOException {
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream(pos);
		BufferedReader reader = new BufferedReader(new InputStreamReader(pis));

		PrintStream customOut = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				originalOut.write(b); // Also print to the console
				pos.write(b); // Send data to the piped stream
			}
		}, true);

		System.setOut(customOut);

		new Thread(() -> {
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					synchronized (lastLines) {
						if (lastLines.size() >= MAX_LINES) {
							lastLines.removeFirst();
						}
						lastLines.add(line);
					}
				}
			} catch (IOException e) {
				//
			}
		}).start();
	}

	public static String getLastLinesAsString() {
		synchronized (lastLines) {
			return String.join("\n", lastLines);
		}
	}
}
