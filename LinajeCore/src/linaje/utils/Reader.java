/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import linaje.statics.Constants;

public class Reader {

	public static final int BUFFER_SIZE_DEFAULT = 1024;

	public static String read(InputStreamReader in) throws IOException {
	
		StringBuffer sb = null;
		
		if (in != null) {
			
			BufferedReader br = null;
			
			try {
			
				br = new BufferedReader(in);
				sb = new StringBuffer(Reader.BUFFER_SIZE_DEFAULT);
	
				char[] chars = new char[Reader.BUFFER_SIZE_DEFAULT];
				int readedChars;
				while ((readedChars = br.read(chars)) != -1) {
					sb.append(chars, 0, readedChars);
				}
			}
			finally {
				if (br != null) {
					try { br.close(); } catch (IOException e) {}
				}
				if (in != null) {
					try { in.close(); } catch (IOException e) {}
				}
			}
		}
	
		return sb != null ? sb.toString() : null;
	}

	public static byte[] read(InputStream in) throws IOException {
	
		if (in != null) {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
			try {
				
				byte[] bytes = new byte[Reader.BUFFER_SIZE_DEFAULT];
				int readedBytes;				
				while ((readedBytes = in.read(bytes)) != -1) {
					baos.write(bytes, 0, readedBytes);
				}
			}
			finally {
				if (in != null) {
					try { in.close(); } catch (IOException e) {}
				}
	
				try { baos.close(); } catch (IOException e) {}
			}		
			
			return baos.toByteArray();
		}
		return null;
	}

	public static String readFirstLine(InputStreamReader in) throws IOException {
		List<String> lines = readLines(in, 1, false);
		return Lists.getFirstElement(lines);
	}
	
	public static List<String> readLines(InputStreamReader in) throws IOException {
		return readLines(in, -1, true);
	}
	public static List<String> readLines(InputStreamReader in, int numLines, boolean readBlankLines) throws IOException {
		//Si numLineas es negativo, se leerán todas las líneas del fichero
		List<String> lines = Lists.newList();
		
		if (in != null) {
			
			BufferedReader br = null;
			
			try {
			
				br = new BufferedReader(in);
				
				int remainingLines = numLines;
				String line = br.readLine();
				while (line != null && remainingLines != 0) {
					
					if (readBlankLines || !line.trim().equals(Constants.VOID)) {
						lines.add(line);
						line = br.readLine();
						remainingLines--;
					}
					else {
						line = br.readLine();
					}
				}
			}
			finally {
				if (br != null) {
					try { br.close(); } catch (IOException e) {}
				}
				if (in != null) {
					try { in.close(); } catch (IOException e) {}
				}
			}
		}
	
		return lines;
	}

	public static Object readSerializedObject(InputStream inputStream) throws IOException, ClassNotFoundException {
		
		Object object = null;
		ObjectInputStream ois = null;
		
		try {
	
			ois = new ObjectInputStream(inputStream);
		
	 		object = ois.readObject();
	 	}
		finally {
			if (ois != null) {
				try { ois.close(); } catch (IOException e) {}
			}
			if (inputStream != null) {
				try { inputStream.close(); } catch (IOException e) {}
			}
		}
		
		return object;
	}

	public static Properties readProperties(InputStream inputStream) throws IOException {
		
		Properties properties = new Properties();
		properties.load(inputStream);
		return properties;
	}

	public static void playSound(InputStream inputStream) throws LineUnavailableException, UnsupportedAudioFileException, IOException {

		Clip clip = AudioSystem.getClip();
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
		clip.open(audioInputStream);
		clip.start();
	}
}
