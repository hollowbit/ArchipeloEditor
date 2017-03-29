package net.hollowbit.archipeloeditor.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.tools.FileReader;
import net.hollowbit.archipeloshared.ElementData;
import net.hollowbit.archipeloshared.ElementList;
import net.hollowbit.archipeloshared.TileData;
import net.hollowbit.archipeloshared.TileList;

public class AssetManager {

	private HashMap<String, MapTile> tileMap;
	private ArrayList<MapTile> tileList;
	private HashMap<String, MapElement> elementMap;
	private ArrayList<MapElement> elementList;
	
	//Manages tiles and elements
	
	public AssetManager() {
		clear();
	}
	
	public MapTile getTileByID(String id) {
		return tileMap.get(id);
	}
	
	public void drawTileByID(Graphics2D g, int x, int y, String id) {
		MapTile tile = tileMap.get(id);
		if (tile != null)
			tile.draw(g, x, y);
		else
			g.drawImage(MainEditor.invalidTile, x, y, null);
	}
	
	public MapElement getElementByID(String id) {
		return elementMap.get(id);
	}
	
	public void drawElementByID(Graphics2D g, int x, int y, String id) {
		MapElement element = elementMap.get(id);
		if (element != null)
			element.draw(g, x, y);
		else {
			if(!id.equalsIgnoreCase("0") && !id.equalsIgnoreCase("null"))
				g.drawImage(MainEditor.invalidTile, x, y, null);
		}
	}
	
	public void clear() {
		tileMap = new HashMap<String, MapTile>();
		tileList = new ArrayList<MapTile>();
		elementMap = new HashMap<String, MapElement>();
		elementList = new ArrayList<MapElement>();
	}
	
	public ArrayList<MapTile> getMapTiles() {
		return tileList;
	}
	
	public ArrayList<MapElement> getMapElements() {
		return elementList;
	}
	
	//////////////////////////
	/*Initiates all Elements!!!*/
	//////////////////////////
	public void load () {
		Json json = new Json();
		String fileData = "";
		
		//Load tiles first
		fileData = FileReader.readFileIntoString("/shared/map-elements/tiles.json");
		
		BufferedImage tileMapImage = null;
		try {
			tileMapImage = ImageIO.read(getClass().getResourceAsStream("/tiles.png"));
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		TileData[] tileDatas = json.fromJson(TileList.class, fileData).tileList;
		
		//Load each individual tile
		for (TileData data : tileDatas) {
			BufferedImage texture = tileMapImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
			MapTile tile = new MapTile(data, texture);
			tileMap.put(data.id, tile);
			tileList.add(tile);
		}
		
		//Load elements now
		fileData = FileReader.readFileIntoString("/shared/map-elements/elements.json");
				
		BufferedImage elementMapImage = null;
		try {
			elementMapImage = ImageIO.read(getClass().getResourceAsStream("/map_elements.png"));
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		ElementData[] elementDatas = json.fromJson(ElementList.class, fileData).elementList;
		
		//Load each individual element
		for (ElementData data : elementDatas) {
			BufferedImage texture = elementMapImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			MapElement element = new MapElement(data, texture);
			elementMap.put(data.id, element);
			elementList.add(element);
		}
	}
	
}
