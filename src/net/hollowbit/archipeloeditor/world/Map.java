package net.hollowbit.archipeloeditor.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapData;

public class Map implements Cloneable {
	
	private static final int ELEMENT_RENDER_BUFFER = 3;
	
	private String displayName = "";
	private String music = "";
	private String name;
	private String[][] tiles;
	private String[][] elements;
	private ArrayList<EntitySnapshot> entitySnapshots;
	
	public Map() {
		entitySnapshots = new ArrayList<EntitySnapshot>();
	}
	
	//Create map from editor
	public Map(String name, String displayName, String music, int width, int height) {
		this.name = name;
		this.displayName = displayName;
		this.music = music;
		
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
	
	public void draw (AssetManager assetManager, boolean showTiles, boolean showElements, boolean showGrid, int tileY, int tileX, int selectedLayer, Object selectedListValue, SpriteBatch batch, int visibleX, int visibleY, int visibleWidth, int visibleHeight ){
		//If show tiles and tiles exist, draw them
		if (showTiles && tiles != null) {
			for(int i = visibleY; i <= visibleY + visibleHeight; i++){
				for(int u = visibleX; u <= visibleX + visibleWidth; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(tiles != null){
							if(tiles[i][u] != null)
								assetManager.drawTileByID(batch, u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE, tiles[i][u]);
							else {
								batch.setColor(0, 0, 0, 1);
								batch.draw(assetManager.getBlank(), u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE);
								batch.setColor(1, 1, 1, 1);
							}
						}
						
						//Draw hover tile for user to see where is will go if placed
						if(i == tileX && u == tileY && selectedListValue != null && selectedLayer == 0)
							assetManager.getTileByID(((MapTile) selectedListValue).id).draw(batch, u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE);
					}
				}
			}
		}
		
		//If show elements and elements exist, draw them
		if (showElements && elements != null) {
			for(int i = visibleY + visibleHeight + ELEMENT_RENDER_BUFFER - 1; i >= visibleY - ELEMENT_RENDER_BUFFER; i--){
				for(int u = visibleX - ELEMENT_RENDER_BUFFER; u < visibleX + visibleWidth + ELEMENT_RENDER_BUFFER; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(elements != null){
							if(elements[i][u] != null)
								assetManager.drawElementByID(batch, u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE, elements[i][u]);
						}
						
						//Draw hover element for user to see where is will go if placed
						if(i == tileX && u == tileY && selectedListValue != null && selectedLayer == 1)
							assetManager.getElementByID(((MapElement) selectedListValue).id).draw(batch, u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE);
					}
				}
			}
		}

		//Draw Grid
		if (showGrid) {			
			for(int i = visibleY; i <= visibleY + visibleHeight; i++){
				for(int u = visibleX; u <= visibleX + visibleWidth; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						batch.draw(assetManager.getGridTexture(), u * MainEditor.TILE_SIZE, i * MainEditor.TILE_SIZE - 2);
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
		music = mapFile.music;
		tiles = mapFile.tileData;
		elements = mapFile.elementData;
		entitySnapshots = mapFile.entitySnapshots;
	}
	
	//Serialize map with json and save it
	public void save(File file) {
		Json json = new Json();
		MapData mapFile = new MapData();
		mapFile.displayName = displayName;
		mapFile.music = music;
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
		music = "";
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
	
	public String getMusic() {
		return music;
	}
	
	public void setMusic(String music) {
		this.music = music;
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
