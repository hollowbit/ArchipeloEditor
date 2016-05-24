package net.vediogames.archipelomapeditor.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class TileFile {
	
	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	public TileFile(File file, String path){
		Scanner scanner = null;
		BufferedImage grassyTilesetTexture = null;
		BufferedImage sandyTilesetTexture = null;
		BufferedImage snowyTilesetTexture = null;
		try{
			grassyTilesetTexture = ImageIO.read(new File(path + "/tiles/grassytileset.png"));
			sandyTilesetTexture = ImageIO.read(new File(path + "/tiles/sandytileset.png"));
			snowyTilesetTexture = ImageIO.read(new File(path + "/tiles/snowytileset.png"));
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(scanner.hasNext()){
			String firstNext = scanner.next();
			if(firstNext.equalsIgnoreCase("tile")){
				String id = scanner.next();
				String name = "Tile";
				int x = 0, y = 0;
				int rotation = 0;
				boolean flipX = false, flipY = false;
				String index, next;
				do{
					index = scanner.next();
					if(index.equalsIgnoreCase("end;")) break;
					next = scanner.next();
					
					if(index.equalsIgnoreCase("name:")){
						if(next.startsWith("\"")){
							StringBuilder stringBuilder = new StringBuilder(next);
							while(!next.endsWith("\"")){
								next = scanner.next();
								stringBuilder.append(" " + next);
							}
							stringBuilder.deleteCharAt(0);
							stringBuilder.deleteCharAt(stringBuilder.length() - 1);
							name = stringBuilder.toString();
						}else{
							name = next;
						}
					}else 
					if(index.equalsIgnoreCase("spritesheet-x:")){
						x = Integer.parseInt(next);
					}else 
					if(index.equalsIgnoreCase("spritesheet-y:")){
						y = Integer.parseInt(next);
					}else 
					if(index.equalsIgnoreCase("rotation:")){
						rotation =  Integer.parseInt(next);
					}else 
					if(index.equalsIgnoreCase("flip-x:")){
						flipX = Boolean.parseBoolean(next);
					}else 
					if(index.equalsIgnoreCase("flip-y:")){
						flipY = Boolean.parseBoolean(next);
					}
				}while(!index.equalsIgnoreCase("end;"));
				BufferedImage grassyTexture = grassyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
				BufferedImage sandyTexture = sandyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
				BufferedImage snowyTexture = snowyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
				tiles.add(new Tile(id, name, rotation, flipX, flipY, grassyTexture, sandyTexture, snowyTexture));
			}else if(firstNext.equals("//")){
				while(!firstNext.endsWith("//") && scanner.hasNext())
					firstNext = scanner.next();
			}
		}
		scanner.close();
		
	}
	
	public ArrayList<Tile> getTiles(){
		return tiles;
	}
	
}
