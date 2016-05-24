package net.vediogames.archipelomapeditor.world;

import java.awt.Graphics2D;
import java.io.File;

import net.vediogames.archipelomapeditor.MainEditor;

public class Map implements Cloneable {
	
	public static final int TYPE_ISLAND = 0;
	public static final int TYPE_DUNGEON = 1;
	public static final int TYPE_HOUSE = 2;
	public static final int TYPE_SHOP = 2;
	
	public static final int CLIMATE_GRASSY = 0;
	public static final int CLIMATE_SANDY = 1;
	public static final int CLIMATE_SNOWY = 2;
	
	public static boolean isMapOpen = false;
	
	private String name = "";
	private int width;
	private int height;
	private byte type;
	private byte climate;
	private String id;
	private String[][] tiles;
	private String[][] elements;
	
	public void draw(Graphics2D g, int x, int y, int visibleX, int visibleY, int visibleWidth, int visibleHeight){
		if(MainEditor.showTiles){
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < height && u < width){
						if(tiles != null){
							if(tiles[i][u] != null)
								drawTileByCoords(g, u * 18 + x, i * 18 + y, u, i);
						}
						if(i == MainEditor.tileX && u == MainEditor.tileY && MainEditor.list.getSelectedValue() != null && MainEditor.selectedLayer == 0)
							Assets.getTileByID(((MapTile) MainEditor.list.getSelectedValue()).id).draw(g, u * 18 + x, i * 18 + y, climate);
					}
				}
			}
		}

		//TODO Draw elements
		if(MainEditor.showElements){
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < height && u < width){
						if(elements != null){
							if(elements[i][u] != null)
								drawElementByCoords(g, u * 18 + x, i * 18 + y, u, i);
						}
						if(i == MainEditor.tileX && u == MainEditor.tileY && MainEditor.list.getSelectedValue() != null && MainEditor.selectedLayer == 1)
							Assets.getElementByID(((MapElement) MainEditor.list.getSelectedValue()).id).draw(g, u * 18 + x, i * 18 + y, climate);
					}
				}
			}
		}

		//Draw Grid
		if(MainEditor.showGrid){			
			for(int i = visibleY - 3; i < visibleY + visibleHeight + 3; i++){
				for(int u = visibleX - 3; u < visibleX + visibleWidth + 3; u++){
					if(i >= 0 && u >= 0 && i < height && u < width){
						g.drawImage(MainEditor.gridTile, u * 18 + x, i * 18 + y, null);
					}
				}
			}

		}
		
	}
	
	private void drawTileByCoords(Graphics2D g, int drawX, int drawY, int x, int y){
		Assets.drawTileByID(g, drawX, drawY, climate, tiles[y][x]);
	}
	
	private void drawElementByCoords(Graphics2D g, int drawX, int drawY, int x, int y){
		Assets.drawElementByID(g, drawX, drawY, climate, elements[y][x]);
	}
	
	public void load(File file){
		MapFile mapFile = new MapFile(file);
		id = mapFile.id;
		name = mapFile.name;
		width = mapFile.width;
		height = mapFile.height;
		type = mapFile.type;
		climate = mapFile.climate;
		tiles = mapFile.tiles;
		elements = mapFile.elements;
		isMapOpen = true;
	}
	
	public void save(File file){
		MapFile.writeFile(file, name, type, climate, width, height, tiles, elements);
	}
	
	public void close(){
		isMapOpen = false;
		name = "";
		width = 0;
		height = 0;
		type = 0;
		climate = 0;
		id = "";
		tiles = null;
		elements = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public  int getWidth() {
		return width;
	}

	public  int getHeight() {
		return height;
	}
	
	public  void resize(int width, int height){
		String[][] newTiles = new String[height][width];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < width; u++){
				if(u >= this.width || i >= this.height){
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
		String[][] newElements = new String[height][width];
		for(int i = 0; i < height; i++){
			for(int u = 0; u < width; u++){
				if(u >= this.width || i >= this.height){
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
		this.width = width;
		this.height = height;
	}
	
	public  byte getType() {
		return type;
	}

	public  void setType(byte type) {
		this.type = type;
	}

	public  byte getClimate() {
		return climate;
	}

	public  void setClimate(byte climate) {
		this.climate = climate;
	}

	public  String getID() {
		return id;
	}

	public  void setID(String id) {
		this.id = id;
	}

	public String[][] getTiles() {
		return tiles;
	}

	public  void setTiles(String[][] tiles) {
		this.tiles = tiles;
		height = tiles.length;
		width = tiles[0].length;
	}

	public  String[][] getElements() {
		return elements;
	}

	public  void setElements(String[][] elements) {
		this.elements = elements;
		height = elements.length;
		width = elements[0].length;
	}

}
