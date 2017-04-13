package ndg;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import java.io.*;

public class Sound {
	private File wavFile; 
	private AudioFileFormat.Type fileType;
	private TargetDataLine line;
	private AudioFormat format;

	public Sound(String path, int sampleRate, int sampleSizeInBits, int channels){
		path = path.replace("\\", "/");
		wavFile = new File(path + "/rec.wav");
		fileType = AudioFileFormat.Type.WAVE;
		format = getAudioFormat(sampleRate, sampleSizeInBits, channels);
	}

	public AudioFormat getAudioFormat(int sampleRate, int sampleSizeInBits, int channels) {
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	public void start() {
		Thread t = new Thread(){
			public void run(){
				try {
					DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
					// checks if system supports the data line
					if (!AudioSystem.isLineSupported(info)) {
						JOptionPane.showMessageDialog(null,
								"Recording Line not supported with the given parameters",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}

					line = (TargetDataLine) AudioSystem.getLine(info);

					line.open(format);
					line.start();   // start capturing

					System.out.println("Start capturing...");

					AudioInputStream ais = new AudioInputStream(line);

					System.out.println("Start recording...");

					// start recording
					AudioSystem.write(ais, fileType, wavFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void finish() {
		line.stop();
		line.close();
		System.out.println("Finished");
	}
}
