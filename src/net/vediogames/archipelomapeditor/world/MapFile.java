package net.vediogames.archipelomapeditor.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class MapFile {

	public String name = "Map Name";
	public String id = "";
	public byte type = 0;
	public byte climate = 0;
	public int width = 75;//In 18px tiles
	public int height = 42;//In 18px tiles
	public String[][] tiles = new String[this.width][this.height];
	public String[][] elements = new String[this.width][this.height];
	
	public MapFile(File file){
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.id = file.getName().replaceFirst("[.][^.]+$", "");
		
		String index = "";
		
		//Scans through file looking for keyword ("port:" in this) and then assigns the value of configuration variables to the value of the string after the keyword
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("name:") && scanner.hasNext());
			//Next code checks to see if string is longer than one word by seeing if it starts with an ' character. If it is, it adds the words together to make a complete string
			String next = scanner.next();
			if(next.startsWith("\"")){
				StringBuilder stringBuilder = new StringBuilder(next);
				while(!next.endsWith("\"")){
					next = scanner.next();
					stringBuilder.append(" " + next);
				}
				stringBuilder.deleteCharAt(0);
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
				this.name = stringBuilder.toString();
			}else{
				this.name = next;
			}
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("type:") && scanner.hasNext());
			String next = scanner.next();
			this.type = Byte.parseByte(next);
			}catch(Exception e){}
		scanner.reset();
		index = "";
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("climate:") && scanner.hasNext());
			String next = scanner.next();
			this.climate = Byte.parseByte(next);
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("size_width:") && scanner.hasNext());
			String next = scanner.next();
			this.width = Integer.parseInt(next);
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("size_height:") && scanner.hasNext());
			String next = scanner.next();
			this.height = Integer.parseInt(next);
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		this.tiles = new String[this.height][this.width];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < width; u++){
				tiles[i][u] = "-1";
			}
		}
		this.elements = new String[this.height][this.width];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < width; u++){
				elements[i][u] = "0";
			}
		}
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("tiles:") && scanner.hasNext());
			for(int i = 0; i < this.height; i++){
				for(int u = 0; u < this.width; u++){
					tiles[i][u] = scanner.next();
				}
			}
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		try{
			do{
				index = scanner.next();
			}while(!index.equalsIgnoreCase("elements:") && scanner.hasNext());
			for(int i = 0; i < this.height; i++){
				for(int u = 0; u < this.width; u++){
					elements[i][u] = scanner.next();
				}
			}
		}catch(Exception e){}
		scanner.reset();
		index = "";
		
		scanner.close();
	}
	
	public static void writeFile(File file, String name, int type, int climate, int width, int height, String[][] tiles, String[][] elements){
		try{
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			Formatter formatter = new Formatter(file.getPath());
			formatter.format("%s", "name: \"" + name + "\"\n");
			formatter.format("%s", "type: " + type + "\n");
			formatter.format("%s", "climate: " + climate + "\n");
			formatter.format("%s", "size_width: " + width + "\n");
			formatter.format("%s", "size_height: " + height + "\n");
			formatter.format("%s", "tiles: \n");
			for(int i = 0; i < height; i++){
				for(int u = 0; u < width; u++){
					formatter.format("%s", " " + tiles[i][u]);
				}
				formatter.format("%s", "\n");
			}
			formatter.format("%s", "elements: \n");
			for(int i = 0; i < height; i++){
				for(int u = 0; u < width; u++){
					formatter.format("%s", " " + elements[i][u]);
				}
				formatter.format("%s", "\n");
			}
			formatter.flush();
			formatter.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
}
