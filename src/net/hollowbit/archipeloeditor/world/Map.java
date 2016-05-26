package net.hollowbit.archipeloeditor.world;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntitySnapshot;

public class Map implements Cloneable {
	
	public static final int TYPE_ISLAND = 0;
	public static final int TYPE_DUNGEON = 1;
	public static final int TYPE_HOUSE = 2;
	public static final int TYPE_SHOP = 2;
	
	public static final int CLIMATE_GRASSY = 0;
	public static final int CLIMATE_SANDY = 1;
	public static final int CLIMATE_SNOWY = 2;
	
	public static boolean isMapOpen = false;
	
	private String displayName = "";
	private int type;
	private int climat;
	private String name;
	private String[][] tiles;
	private String[][] elements;
	private ArrayList<EntitySnapshot> entitySnapshots;
	
	public void draw(Graphics2D g, int x, int y, int visibleX, int visibleY, int visibleWidth, int visibleHeight){
		if(MainEditor.showTiles && tiles != null){
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(tiles != null){
							if(tiles[i][u] != null)
								drawTileByCoords(g, u * 18 + x, i * 18 + y, u, i);
						}
						if(i == MainEditor.tileX && u == MainEditor.tileY && MainEditor.list.getSelectedValue() != null && MainEditor.selectedLayer == 0)
							Assets.getTileByID(((MapTile) MainEditor.list.getSelectedValue()).id).draw(g, u * 18 + x, i * 18 + y);
					}
				}
			}
		}

		if(MainEditor.showElements && elements != null){
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						if(elements != null){
							if(elements[i][u] != null)
								drawElementByCoords(g, u * 18 + x, i * 18 + y, u, i);
						}
						if(i == MainEditor.tileX && u == MainEditor.tileY && MainEditor.list.getSelectedValue() != null && MainEditor.selectedLayer == 1)
							Assets.getElementByID(((MapElement) MainEditor.list.getSelectedValue()).id).draw(g, u * 18 + x, i * 18 + y);
					}
				}
			}
		}

		//Draw Grid
		if(MainEditor.showGrid){			
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < tiles.length && u < tiles[0].length){
						g.drawImage(MainEditor.gridTile, u * 18 + x, i * 18 + y, null);
					}
				}
			}

		}
		
	}
	
	private void drawTileByCoords(Graphics2D g, int drawX, int drawY, int x, int y){
		Assets.drawTileByID(g, drawX, drawY, climat, tiles[y][x]);
	}
	
	private void drawElementByCoords(Graphics2D g, int drawX, int drawY, int x, int y){
		Assets.drawElementByID(g, drawX, drawY, climat, elements[y][x]);
	}
	
	public void load(File file){
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
		
		Json json = new Json();
		MapData mapFile = json.fromJson(MapData.class, fileData);
		name = file.getName().replaceFirst("[.][^.]+$", "");
		displayName = mapFile.displayName;
		type = mapFile.type;
		climat = mapFile.climat;
		tiles = mapFile.tileData;
		elements = mapFile.elementData;
		entitySnapshots = mapFile.entitySnapshots;
		
		isMapOpen = true;
	}
	
	public void save(File file){
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
			
			formatter.format("%s", json.prettyPrint(mapFile));
			
			formatter.flush();
			formatter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		isMapOpen = false;
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
	
	public  void resize(int width, int height){
		String[][] newTiles = new String[height][tiles[0].length];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < tiles[0].length; u++){
				if(u >= this.tiles[0].length || i >= tiles.length){
					newTiles[i][u] = "-1";
				}else{
					if(tiles[i][u] == null)
						newTiles[i][u] = "-1";
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
					newElements[i][u] = "0";
				}else{
					if(elements[i][u] == null)
						newElements[i][u] = "0";
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
