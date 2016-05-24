package net.vediogames.archipelomapeditor.world;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import net.vediogames.archipelomapeditor.MainEditor;

public class Assets {

	public static ArrayList<MapTile> TileList = new ArrayList<MapTile>();
	public static ArrayList<MapElement> ElementList = new ArrayList<MapElement>();
	
	public static MapTile getTileByID(String id){
		for(MapTile tile : TileList){
			if(tile.id.equalsIgnoreCase(id)){
				return tile;
			}
		}
		return null;
	}
	
	public static void drawTileByID(Graphics2D g, int x, int y, int climate, String id){
		for(MapTile tile : TileList){
			if(tile.id.equalsIgnoreCase(id)){
				tile.draw(g, x, y, climate);
				return;
			}
		}
		g.drawImage(MainEditor.invalidTile, x, y, null);
	}
	
	public static MapElement getElementByID(String id){
		for(MapElement element : ElementList){
			if(element.id.equalsIgnoreCase(id)){
				return element;
			}
		}
		return null;
	}
	
	public static void drawElementByID(Graphics2D g, int x, int y, int climate, String id){
		for(MapElement element : ElementList){
			if(element.id.equalsIgnoreCase(id)){
				element.draw(g, x, y, climate);
				return;
			}
		}
		if(!id.equalsIgnoreCase("0") && !id.equalsIgnoreCase("null"))
			g.drawImage(MainEditor.invalidTile, x, y, null);
	}
	
	//////////////////////////
	/*Initiates all Tiles!!!*/
	//////////////////////////
	public static void initiate(){
		//Loads default developer made tiles
		File[] assetPackFolders;	
		File assetPackDirectory = new File(MainEditor.PATH + "/assetpacks/");
		if(!assetPackDirectory.exists())
			assetPackDirectory.mkdirs();
		assetPackFolders = assetPackDirectory.listFiles();
		if(assetPackFolders != null){
			for(File assetPackFolder : assetPackFolders){
				if(assetPackFolder.isDirectory()){
					AssetPack assetPack = new AssetPack();
					if(assetPack.load(assetPackFolder)){
						//Load assetpack's tiles
						for(MapTile newTile : assetPack.getTiles()){
							boolean isNew = true;
							for(int i = 0; i < TileList.size(); i++){
								MapTile oldTile = TileList.get(i);
								if(newTile.id.equalsIgnoreCase(oldTile.id)){
									isNew = false;
									if(newTile.assetPack.priority > oldTile.assetPack.priority){
										TileList.set(i, newTile);
										break;
									}
								}
							}
							if(isNew)
								TileList.add(newTile);
						}
						//Load assetpack's elements
						for(MapElement newElement : assetPack.getElements()){
							boolean isNew = true;
							for(int i = 0; i < ElementList.size(); i++){
								MapElement oldElement = ElementList.get(i);
								if(newElement.id.equalsIgnoreCase(oldElement.id)){
									isNew = false;
									if(newElement.assetPack.priority > oldElement.assetPack.priority){
										ElementList.set(i, newElement);
										break;
									}
								}
							}
							if(isNew)
								ElementList.add(newElement);
						}
						
					}
				}
			}
		}
	}
	
}
