package net.hollowbit.archipeloeditor.world;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapData;

public class Map implements Cloneable {
	
	public static final int TYPE_ISLAND = 0;
	public static final int TYPE_DUNGEON = 1;
	public static final int TYPE_HOUSE = 2;
	public static final int TYPE_SHOP = 2;
	
	public static final int CLIMAT_GRASSY = 0;
	public static final int CLIMAT_SANDY = 1;
	public static final int CLIMAT_SNOWY = 2;
	
	private String displayName = "";
	private int type;
	private int climat;
	private String name;
	private String[][] tiles;
	private String[][] elements;
	private ArrayList<EntitySnapshot> entitySnapshots;
	
	public Map() {
		entitySnapshots = new ArrayList<EntitySnapshot>();
	}
	
	//Create map from editor
	public Map(String name, String displayName, int type, int climat, int width, int height) {
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.climat = climat;
		
		entitySnapshots = new ArrayList<EntitySnapshot>();
		
		tiles = new String[height][width];
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				tiles[r][c] = "null";
			}
		}
		
		elements = new String[height][width];
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				elements[r][c] = "null";
			}
		}
	}
	
	public void draw (AssetManager assetManager, boolean showTiles, boolean showElements, boolean showGrid, int tileY, int tileX, int selectedLayer, Object selectedListValue, Graphics2D g, int x, int y, int visibleX, int visibleY, int visibleWidth, int visibleHeight ){
		//If show tiles and tiles exist, draw them
		if (showTiles && tiles != null) {
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(tiles != null){
							if(tiles[i][u] != null)
								assetManager.drawTileByID(g, u * MainEditor.TILE_SIZE + x, i * MainEditor.TILE_SIZE + y, tiles[i][u]);
						}
						
						//Draw hover tile for user to see where is will go if placed
						if(i == tileX && u == tileY && selectedListValue != null && selectedLayer == 0)
							assetManager.getTileByID(((MapTile) selectedListValue).id).draw(g, u * MainEditor.TILE_SIZE + x, i * MainEditor.TILE_SIZE + y);
					}
				}
			}
		}
		
		//If show elements and elements exist, draw them
		if (showElements && elements != null) {
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(elements != null){
							if(elements[i][u] != null)
								assetManager.drawElementByID(g, u * MainEditor.TILE_SIZE + x, i * MainEditor.TILE_SIZE + y, elements[i][u]);
						}
						
						//Draw hover element for user to see where is will go if placed
						if(i == tileX && u == tileY && selectedListValue != null && selectedLayer == 1)
							assetManager.getElementByID(((MapElement) selectedListValue).id).draw(g, u * MainEditor.TILE_SIZE + x, i * MainEditor.TILE_SIZE + y);
					}
				}
			}
		}

		//Draw Grid
		if (showGrid) {			
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						g.drawImage(MainEditor.gridTile, u * MainEditor.TILE_SIZE + x, i * MainEditor.TILE_SIZE + y, null);
					}
				}
			}

		}
		
	}
	
	public void load(File file) {
		//Load file
		Scanner scanner = null;
		String fileData = "";
		try {
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				fileData += scanner.next();
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Could not read map file!");
			e.printStackTrace();
		}
		
		//Parse json
		Json json = new Json();
		MapData mapFile = json.fromJson(MapData.class, fileData);
		
		//Apply map data
		name = file.getName().replaceFirst("[.][^.]+$", "");
		displayName = mapFile.displayName;
		type = mapFile.type;
		climat = mapFile.climat;
		tiles = mapFile.tileData;
		elements = mapFile.elementData;
		entitySnapshots = mapFile.entitySnapshots;
	}
	
	//Serialize map with json and save it
	public void save(File file) {
		Json json = new Json();
		MapData mapFile = new MapData();
		mapFile.displayName = displayName;
		mapFile.type = type;
		mapFile.climat = climat;
		mapFile.tileData = tiles;
		mapFile.elementData = elements;
		mapFile.entitySnapshots = entitySnapshots;
		
		try {
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			Formatter formatter = new Formatter(file.getPath());
			
			formatter.format("%s", json.toJson(mapFile));
			
			formatter.flush();
			formatter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Close map
	public void close(){
		displayName = "";
		type = 0;
		climat = 0;
		name = "";
		tiles = null;
		elements = null;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getWidth() {
		if (tiles == null)
			return 0;
		else
			return tiles[0].length;
	}

	public  int getHeight() {
		if (tiles == null)
			return 0;
		else
			return tiles.length;
	}
	
	//Resize map while keeping current tiles/elements in place.
	//this method will soo be upgraded to allow for centering
	public  void resize(int width, int height){
		String[][] newTiles = new String[height][tiles[0].length];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < tiles[0].length; u++){
				if(u >= this.tiles[0].length || i >= tiles.length){
					newTiles[i][u] = "null";
				}else{
					if(tiles[i][u] == null)
						newTiles[i][u] = "null";
					else
						newTiles[i][u] = tiles[i][u];
				}
			}
		}
		tiles = newTiles;
		
		String[][] newElements = new String[height][tiles[0].length];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < tiles[0].length; u++){
				if(u >= this.tiles[0].length || i >= tiles.length){
					newElements[i][u] = "null";
				}else{
					if(elements[i][u] == null)
						newElements[i][u] = "null";
					else
						newElements[i][u] = elements[i][u];
				}
			}
		}
		elements = newElements;
	}
	
	public int getType() {
		return type;
	}

	public  void setType(int type) {
		this.type = type;
	}

	public int getClimat() {
		return climat;
	}

	public void setClimat(int climat) {
		this.climat = climat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[][] getTiles() {
		return tiles;
	}

	public void setTiles(String[][] tiles) {
		this.tiles = tiles;
	}

	public String[][] getElements() {
		return elements;
	}

	public void setElements(String[][] elements) {
		this.elements = elements;
	}

}
