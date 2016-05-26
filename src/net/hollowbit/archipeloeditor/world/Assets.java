package net.hollowbit.archipeloeditor.world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;

public class Assets {

	public static HashMap<String, MapTile> TileMap = new HashMap<String, MapTile>();
	public static ArrayList<MapTile> TileList = new ArrayList<MapTile>();
	public static HashMap<String, MapElement> ElementMap = new HashMap<String, MapElement>();
	public static ArrayList<MapElement> ElementList = new ArrayList<MapElement>();
	
	public static MapTile getTileByID(String id){
		return TileMap.get(id);
	}
	
	public static void drawTileByID(Graphics2D g, int x, int y, int climate, String id){
		MapTile tile = TileMap.get(id);
		if (tile != null)
			tile.draw(g, x, y);
		else
			g.drawImage(MainEditor.invalidTile, x, y, null);
	}
	
	public static MapElement getElementByID(String id){
		return ElementMap.get(id);
	}
	
	public static void drawElementByID(Graphics2D g, int x, int y, int climate, String id){
		MapElement element = ElementMap.get(id);
		if (element != null)
			element.draw(g, x, y);
		else {
			if(!id.equalsIgnoreCase("0") && !id.equalsIgnoreCase("null"))
				g.drawImage(MainEditor.invalidTile, x, y, null);
		}
	}
	
	//////////////////////////
	/*Initiates all Tiles!!!*/
	//////////////////////////
	public static void initiate(){
		Json json = new Json();
		Scanner scanner = null;
		String fileData = "";
		
		//Load tiles first
		try {
			scanner = new Scanner(new File(MainEditor.PATH + "/tilemaps/tiles.json"));
			while (scanner.hasNext()) {
				fileData += scanner.next();
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Could not read map tile file!");
			e.printStackTrace();
		}
		
		BufferedImage tileMap = null;
		try {
			tileMap = ImageIO.read(new File(MainEditor.PATH + "/tilemaps/tiles.png"));
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		TileData[] tileDatas = json.fromJson(TileList.class, fileData).tileList;
		
		//Load each individual tile
		for (TileData data : tileDatas) {
			BufferedImage texture = tileMap.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
			MapTile tile = new MapTile(data, texture);
			TileMap.put(data.id, tile);
			TileList.add(tile);
		}
		
		//Load elements now
		try {
			scanner = new Scanner(new File(MainEditor.PATH + "/tilemaps/elements.json"));
			while (scanner.hasNext()) {
				fileData += scanner.next();
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Could not read map element file!");
			e.printStackTrace();
		}
				
		BufferedImage elementMap = null;
		try {
			elementMap = ImageIO.read(new File(MainEditor.PATH + "/tilemaps/elements.png"));
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		ElementData[] elementDatas = json.fromJson(ElementList.class, fileData).elementList;
		
		//Load each individual element
		for (ElementData data : elementDatas) {
			BufferedImage texture = elementMap.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			MapElement element = new MapElement(data, texture);
			ElementMap.put(data.id, element);
			ElementList.add(element);
		}
	}
	
}
