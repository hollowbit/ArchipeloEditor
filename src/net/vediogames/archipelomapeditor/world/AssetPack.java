package net.vediogames.archipelomapeditor.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class AssetPack {
	
	private ArrayList<MapTile> tiles = new ArrayList<MapTile>();
	private ArrayList<MapElement> elements = new ArrayList<MapElement>();
	
	private File folder;
	
	public String name = "Default";
	public String version = "1.0";
	public String archipeloVersion = "1.0";
	public String description = "An assetpack for Archipelo";
	public int priority = 3;
	
	public boolean load(File folder){
		try{
			this.folder = folder;
			
			Scanner scanner = new Scanner(new File(folder.getPath() + "/assetpack.config"));
		
			String index = "", next = "";
			while(scanner.hasNext()){
				index = scanner.next();
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
				}else if(index.equalsIgnoreCase("version:")){
					if(next.startsWith("\"")){
						StringBuilder stringBuilder = new StringBuilder(next);
						while(!next.endsWith("\"")){
							next = scanner.next();
							stringBuilder.append(" " + next);
						}
						stringBuilder.deleteCharAt(0);
						stringBuilder.deleteCharAt(stringBuilder.length() - 1);
						version = stringBuilder.toString();
					}else{
						version = next;
					}
				}else if(index.equalsIgnoreCase("version-archipelo:")){
					if(next.startsWith("\"")){
						StringBuilder stringBuilder = new StringBuilder(next);
						while(!next.endsWith("\"")){
							next = scanner.next();
							stringBuilder.append(" " + next);
						}
						stringBuilder.deleteCharAt(0);
						stringBuilder.deleteCharAt(stringBuilder.length() - 1);
						archipeloVersion = stringBuilder.toString();
					}else{
						archipeloVersion = next;
					}
				}else if(index.equalsIgnoreCase("description:")){
					if(next.startsWith("\"")){
						StringBuilder stringBuilder = new StringBuilder(next);
						while(!next.endsWith("\"")){
							next = scanner.next();
							stringBuilder.append(" " + next);
						}
						stringBuilder.deleteCharAt(0);
						stringBuilder.deleteCharAt(stringBuilder.length() - 1);
						description = stringBuilder.toString();
					}else{
						description = next;
					}
				}else if(index.equalsIgnoreCase("priority:"))
					priority = Integer.parseInt(next);
			}
			scanner.close();
			loadTiles();
			loadElements();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Could not load assetpack \"" + folder.getName() + "\"");
			return false;
		}
		return true;
	}
	
	private void loadTiles(){
		File tilesFolder = new File(folder.getPath() + "/tiles");
		if(!tilesFolder.exists()) return;
		if(!tilesFolder.isDirectory()){
			System.out.println("could not load tiles for assetpack \"" + name + "\"");
			return;
		}
		
		
		File[] tilesets = tilesFolder.listFiles();
		for(File tileset : tilesets){
			if(!tileset.getPath().endsWith(".tileset")) continue;
			String path = tileset.getPath().replaceFirst("[.][^.]+$", "");
			String tilesetName = tileset.getName().replaceFirst("[.][^.]+$", "");
			try{
				if(!tileset.isDirectory()){
					
					BufferedImage grassyTilesetTexture = ImageIO.read(new File(path + "_grassy.png"));
					BufferedImage sandyTilesetTexture = ImageIO.read(new File(path + "_sandy.png"));
					BufferedImage snowyTilesetTexture = ImageIO.read(new File(path + "_snowy.png"));
						
					Scanner scanner = new Scanner(new File(path + ".tileset"));
	
					while(scanner.hasNext()){
						String firstNext = scanner.next();
						if(firstNext.equalsIgnoreCase("tile")){
							String id = scanner.next();
							String tileName = "Tile";
							int x = 0, y = 0;
							boolean collidable = false;
							float damageSpeed = 0.5f;
							int damage = 0;
							float speedMultiplier = 1f;
							float animationSpeed = 0f;
							int numberOfFrames = 1;
							int rotation = 0;
							boolean swimmable = false;
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
										tileName = stringBuilder.toString();
									}else{
										tileName = next;
									}
								}else 
								if(index.equalsIgnoreCase("spritesheet-x:")){
									x = Integer.parseInt(next);
								}else 
								if(index.equalsIgnoreCase("spritesheet-y:")){
									y = Integer.parseInt(next);
								}else 
								if(index.equalsIgnoreCase("collidable:")){
									collidable = Boolean.parseBoolean(next);
								}else
								if(index.equalsIgnoreCase("damage-speed:")){
									damageSpeed = Float.parseFloat(next);
								}else
								if(index.equalsIgnoreCase("damage:")){
									damage = Integer.parseInt(next);
								}else
								if(index.equalsIgnoreCase("speed-multiplier:")){
								}else 
								if(index.equalsIgnoreCase("animation-speed:")){
									animationSpeed = Float.parseFloat(next);
								}else 
								if(index.equalsIgnoreCase("number-of-frames:")){
									numberOfFrames = Integer.parseInt(next);
								}else 
								if(index.equalsIgnoreCase("rotation:")){
									rotation =  Integer.parseInt(next);
								}else 
								if(index.equalsIgnoreCase("swimmable:")){
									swimmable = Boolean.parseBoolean(next);
								}else 
								if(index.equalsIgnoreCase("flip-x:")){
									flipX = Boolean.parseBoolean(next);
								}else 
								if(index.equalsIgnoreCase("flip-y:")){
									flipY = Boolean.parseBoolean(next);
								}
							}while(!index.equalsIgnoreCase("end;") && scanner.hasNext());
							BufferedImage grassyTexture = grassyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
							BufferedImage sandyTexture = sandyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
							BufferedImage snowyTexture = snowyTilesetTexture.getSubimage(x * 18, y * 18, 18, 18);
							tiles.add(new MapTile(id, tileName, this, numberOfFrames, animationSpeed, collidable, swimmable, damageSpeed, damage, speedMultiplier, rotation, flipX, flipY, grassyTexture, sandyTexture, snowyTexture));
						}else if(firstNext.equals("//")){
							firstNext = scanner.next();
							while(!firstNext.endsWith("//") && scanner.hasNext())
								firstNext = scanner.next();
						}
					}
					
					scanner.close();
				}
			}catch(Exception e){
				System.out.println("Could not load tileset \"" + tilesetName + "\"");
				continue;
			}
		}
		
	}
	
	private void loadElements(){
		File elementsFolder = new File(folder.getPath() + "/elements");
		if(!elementsFolder.exists()) return;
		if(!elementsFolder.isDirectory()){
			System.out.println("could not load elements for assetpack \"" + name + "\"");
			return;
		}
		
		
		File[] elementsets = elementsFolder.listFiles();
		for(File elementset : elementsets){
			if(!elementset.getPath().endsWith(".elementset") || elementset.isDirectory()) continue;
			String path = elementset.getPath().replaceFirst("[.][^.]+$", "");
			String elementsetName = elementset.getName().replaceFirst("[.][^.]+$", "");
			
			try{
				
				BufferedImage grassyElementsetTexture = ImageIO.read(new File(path + "_grassy.png"));
				BufferedImage sandyElementsetTexture = ImageIO.read(new File(path + "_sandy.png"));
				BufferedImage snowyElementsetTexture = ImageIO.read(new File(path + "_snowy.png"));
					
				Scanner scanner = new Scanner(new File(path + ".elementset"));
				
				while(scanner.hasNext()){
					String firstNext = scanner.next();
					if(firstNext.equalsIgnoreCase("element")){
						String id = scanner.next();
						String elementName = "Element";
						int x = 0, y = 0;
						int width = 1, height = 1;
						int offsetX = 0, offsetY = 0;
						int[][] collidable = new int[height][width];
						float animationSpeed = 0f;
						int numberOfFrames = 1;
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
									elementName = stringBuilder.toString();
								}else{
									elementName = next;
								}
							}else 
							if(index.equalsIgnoreCase("spritesheet-x:")){
								x = Integer.parseInt(next);
							}else 
							if(index.equalsIgnoreCase("spritesheet-y:")){
								y = Integer.parseInt(next);
							}else 
							if(index.equalsIgnoreCase("offset-x:")){
								offsetX = Integer.parseInt(next);
							}else 
							if(index.equalsIgnoreCase("offset-y:")){
								offsetY = Integer.parseInt(next);
							}else 
							if(index.equalsIgnoreCase("width:")){
								width = Integer.parseInt(next);
							}else 
							if(index.equalsIgnoreCase("height:")){
								height = Integer.parseInt(next);
							}else
							if(index.equalsIgnoreCase("collidable:")){
								collidable = new int[height][width];
								for(int i = 0; i < height; i++){
									for(int u = 0; u < width; u++){
										collidable[i][u] = Integer.parseInt(next);
										if(i * u != (height - 1) * (width - 1))
											next = scanner.next();
									}
								}
							}else 
							if(index.equalsIgnoreCase("animation-speed:")){
								animationSpeed = Float.parseFloat(next);
							}else 
							if(index.equalsIgnoreCase("number-of-frames:")){
								numberOfFrames = Integer.parseInt(next);
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
						}while(!index.equalsIgnoreCase("end;") && scanner.hasNext());
						BufferedImage grassyTexture = grassyElementsetTexture.getSubimage(x * 18, y * 18, width * 18, height * 18);
						BufferedImage sandyTexture = sandyElementsetTexture.getSubimage(x * 18, y * 18, width * 18, height * 18);
						BufferedImage snowyTexture = snowyElementsetTexture.getSubimage(x * 18, y * 18, width * 18, height * 18);
						elements.add(new MapElement(id, elementName, this, width, height, offsetX, offsetY, collidable, animationSpeed, numberOfFrames, rotation, flipX, flipY, grassyTexture, sandyTexture, snowyTexture));
					}else if(firstNext.equals("//")){
						firstNext = scanner.next();
						while(!firstNext.endsWith("//") && scanner.hasNext())
							firstNext = scanner.next();
					}
				}
				scanner.close();
				
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Could not load elementset \"" + elementsetName + "\"");
				continue;
			}
			
		}
	}
	
	public ArrayList<MapTile> getTiles(){
		return tiles;
	}
	
	public ArrayList<MapElement> getElements(){
		return elements;
	}
	
}
