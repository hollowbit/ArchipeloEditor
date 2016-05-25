package net.vediogames.archipelomapeditor.world;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class MapSnapshot {
	
	public String name = "island";
	public String displayName = "Island";
	public int type = 0;
	public int climate = 0;
	public String[][] tileData;
	public String[][] elementData;
	public HashMap<String, String> properties;
	
	public MapSnapshot (File file) {
		properties = new HashMap<String, String>();
		
		//Load file
		Scanner scanner = null;
		String fileData = "";
		try{
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				fileData += scanner.next();
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Could not map read file!");
			e.printStackTrace();
		}
	}
	
	public void setTileData (String[][] tileData) {
		this.tileData = tileData;
	}
	
	public void setElementData (String[][] elementData) {
		this.elementData = elementData;
	}
	
	public void putFloat (String key, float value) {
		properties.put(key, "" + value);
	}
	
	public void putString (String key, String value) {
		properties.put(key, value);
	}
	
	public void putInt (String key, int value) {
		properties.put(key, "" + value);
	}
	
	public void putBoolean (String key, boolean value) {
		properties.put(key, "" + value);
	}
	
	public void clear () {
		properties.clear();
		tileData = null;
		elementData = null;
	}
	
	public float getFloat (String key, float currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Float.parseFloat(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public String getString (String key, String currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		
		return properties.get(key);
	}

	public int getInt (String key, int currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Integer.parseInt(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
	public boolean getBoolean (String key, boolean currentValue) {
		if (!properties.containsKey(key))
			return currentValue;
		try {
			return Boolean.parseBoolean(properties.get(key));
		} catch (Exception e) {
			return currentValue;
		}
	}
	
}
