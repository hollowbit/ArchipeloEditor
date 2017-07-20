package net.hollowbit.archipeloeditor.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	
	private Texture grid;
	private Texture invalid;
	private Texture blank;
	private Texture chunk;
	
	//Manages tiles and elements
	
	public AssetManager() {
		clear();
	}
	
	public MapTile getTileByID(String id) {
		return tileMap.get(id);
	}
	
	public void drawTileByID(SpriteBatch batch, int x, int y, String id) {
		MapTile tile = tileMap.get(id);
		if (tile != null)
			tile.draw(batch, x, y);
		else
			batch.draw(invalid, x, y);
	}
	
	public MapElement getElementByID(String id) {
		return elementMap.get(id);
	}
	
	public void drawElementByID(SpriteBatch batch, int x, int y, String id) {
		MapElement element = elementMap.get(id);
		if (element != null)
			element.draw(batch, x, y);
		else {
			if(!id.equalsIgnoreCase("0") && !id.equalsIgnoreCase("null"))
				batch.draw(invalid, x, y);
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
	
	public Texture getChunkTexture() {
		return chunk;
	}
	
	public Texture getGridTexture() {
		return grid;
	}
	
	public Texture getInvalidTexture() {
		return invalid;
	}
	
	public Texture getBlank() {
		return blank;
	}
	
	//////////////////////////
	/*Initiates all Elements!!!*/
	//////////////////////////
	public void load () {
		chunk = new Texture(Gdx.files.internal("chunk.png"), true);
		grid = new Texture(Gdx.files.internal("grid.png"), true);
		invalid = new Texture(Gdx.files.internal("invalid.png"), true);
		blank = new Texture(Gdx.files.internal("blank.png"), true);
		
		Json json = new Json();
		String fileData = "";
		
		//Load tiles first
		fileData = FileReader.readFileIntoString("/shared/map-elements/tiles.json");
		
		Texture tileMapImage = null;
		BufferedImage tileMapBufferedImage = null;
		try {
			tileMapImage = new Texture(Gdx.files.internal("tiles.png"), true);
			tileMapBufferedImage = ImageIO.read(getClass().getResourceAsStream("/tiles.png"));
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		TileData[] tileDatas = json.fromJson(TileList.class, fileData).tileList;
		
		//Load each individual tile
		for (TileData data : tileDatas) {
			TextureRegion texture = new TextureRegion(tileMapImage, data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
			BufferedImage icon = tileMapBufferedImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
			MapTile tile = new MapTile(data, texture, icon);
			tileMap.put(data.id, tile);
			tileList.add(tile);
		}
		
		//Load elements now
		fileData = FileReader.readFileIntoString("/shared/map-elements/elements.json");
				
		BufferedImage elementMapBufferedImage = null;
		Texture elementMapImage = null;
		try {
			elementMapBufferedImage = ImageIO.read(getClass().getResourceAsStream("/map_elements.png"));
			elementMapImage = new Texture(Gdx.files.internal("map_elements.png"), true);
		} catch (IOException e) {
			System.out.println("Could not load tile map image.");
			e.printStackTrace();
		}
		ElementData[] elementDatas = json.fromJson(ElementList.class, fileData).elementList;
		
		//Load each individual element
		for (ElementData data : elementDatas) {
			BufferedImage icon = elementMapBufferedImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			TextureRegion texture = new TextureRegion(elementMapImage, data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			MapElement element = new MapElement(data, texture, icon);
			elementMap.put(data.id, element);
			elementList.add(element);
		}
	}
	
}
