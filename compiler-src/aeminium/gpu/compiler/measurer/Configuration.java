package aeminium.gpu.compiler.measurer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	protected static final Properties properties;
	protected static String filename;
	
	static {
		filename = System.getenv("AEMINIUMGPU_CONFIG");
		if ( filename == null ) {
			filename = "aeminiumgpu.config";
		}
		File file = new File(filename);
		properties = new Properties();
		if ( file.exists()  && file.canRead()) {
			FileReader freader;
			try {
				freader = new FileReader(file);
				properties.load(freader);
				freader.close();
			}  catch (IOException e) {
			} 
		} 
	}
	

	public static long get(String key) {
		return Long.parseLong(properties.getProperty(key));
	}

	public static void set(String key, long value) {
		properties.setProperty(key, "" + value);
		save();
	}
	
	private static void save() {
		File file = new File(filename);
		try {
			properties.store(new FileOutputStream(file), "");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

}
