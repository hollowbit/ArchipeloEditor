package net.vediogames.archipelomapeditor.world;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import net.vediogames.archipelomapeditor.MainEditor;

public class TileList {

	public static ArrayList<Tile> TileList = new ArrayList<Tile>();
	
	public static Tile getTileByID(String id){
		for(Tile tile : TileList){
			if(tile.id.equalsIgnoreCase(id)){
				return tile;
			}
		}
		return null;
	}
	
	public static void drawTileByID(Graphics2D g, int x, int y, int climate, String id){
		for(Tile tile : TileList){
			if(tile.id.equalsIgnoreCase(id)){
				tile.draw(g, x, y, climate);
			}
		}
	}
	
	//////////////////////////
	/*Initiates all Tiles!!!*/
	//////////////////////////
	public static void initiate(){
		//Loads default developer made tiles
		File[] tileFolder;	
		File tileDirectory = new File(MainEditor.PATH + "/tiles/");
		if(!tileDirectory.exists())
			tileDirectory.mkdirs();
		tileFolder = new File(MainEditor.PATH + "/tiles/").listFiles();
		//Searches through tilesets folder and initiates all tilesets and adds them to TileList
		if(tileFolder != null){
			for(File tilesetFolder : tileFolder){
				if(tilesetFolder.isDirectory()){
					try{
					ArrayList<Tile> tiles = new TileFile(new File(tilesetFolder.getPath() + "/init.tileset"), tilesetFolder.getPath()).getTiles();
					for(Tile newTile : tiles){
						for(Tile oldTile : TileList){
							if(newTile.id.equalsIgnoreCase(oldTile.id)){
								TileList.remove(oldTile);
							}
						}
						TileList.add(newTile);
					}
					}catch(Exception e){
						System.out.println("Could not load tileset '" + tilesetFolder.getName() + "'");
					}
				}
			}
		}
	}
	
}
