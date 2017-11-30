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
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.FileReader;
import net.hollowbit.archipeloshared.ElementData;
import net.hollowbit.archipeloshared.ElementList;
import net.hollowbit.archipeloshared.Pair;
import net.hollowbit.archipeloshared.TileData;
import net.hollowbit.archipeloshared.TileList;

public class AssetManager {

	private HashMap<String, MapTile> tileMap;
	private ArrayList<MapTile> tileList;
	private HashMap<String, MapElement> elementMap;
	private ArrayList<MapElement> elementList;
	
	private HashMap<String, ArrayList<MapTile>> categoryTiles;
	private HashMap<String, ArrayList<MapElement>> categoryElements;
	
	private Texture grid;
	private Texture invalid;
	private Texture blank;
	private Texture chunk;
	private Texture locationIcon;
	
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
		categoryTiles = new HashMap<String, ArrayList<MapTile>>();
		categoryElements = new HashMap<String, ArrayList<MapElement>>();
	}
	
	public ArrayList<MapTile> getMapTiles() {
		return tileList;
	}
	
	public ArrayList<MapElement> getMapElements() {
		return elementList;
	}
	
	public HashMap<String, ArrayList<MapTile>> getCategoryTiles() {
		return categoryTiles;
	}
	
	public HashMap<String, ArrayList<MapElement>> getCategoryElements() {
		return categoryElements;
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
	
	public Texture getLocationIcon() {
		return locationIcon;
	}
	
	/////////////////////////////
	/*Initiates all Elements!!!*/
	/////////////////////////////
	public void load () {
		chunk = new Texture(Gdx.files.internal("chunk.png"), true);
		grid = new Texture(Gdx.files.internal("grid.png"), true);
		invalid = new Texture(Gdx.files.internal("invalid.png"), true);
		blank = new Texture(Gdx.files.internal("blank.png"), true);
		locationIcon = new Texture(Gdx.files.internal("location_icon.png"), true);
		
		Json json = new Json();
		String fileData = "";
		
		//Load tiles first
		fileData = FileReader.readFileIntoString("/shared/map-elements/tiles.json");
		TileData[] tileDatas = json.fromJson(TileList.class, fileData).tileList;
		
		HashMap<String, Pair<TextureRegion[][], BufferedImage>> categoryImages = new HashMap<String, Pair<TextureRegion[][], BufferedImage>>();
		
		//Load each individual tile
		for (TileData data : tileDatas) {
			Pair<TextureRegion[][], BufferedImage> pair = categoryImages.get(data.category);
			
			if (pair == null) {
				try {
					pair = new Pair<TextureRegion[][], BufferedImage>(
							TextureRegion.split(new Texture("map-elements/tiles/" + data.category + ".png"), MainEditor.TILE_SIZE, MainEditor.TILE_SIZE),
							ImageIO.read(getClass().getResourceAsStream("/map-elements/tiles/" + data.category + ".png")));
				} catch (IOException e) {
					System.out.println("Could not load tiles with category " + data.category);
				}
				categoryImages.put(data.category, pair);
			}
			
			TextureRegion[][] tileMapImage = pair.getVal1();
			BufferedImage tileMapBufferedImage = pair.getVal2();
			
			TextureRegion texture = tileMapImage[data.y][data.x];
			BufferedImage icon = tileMapBufferedImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
			MapTile tile = new MapTile(data, texture, icon);
			tileMap.put(data.id, tile);
			tileList.add(tile);
			
			//Get list of tiles in same category
			ArrayList<MapTile> categoryTilesList = categoryTiles.get(data.category);
			if (categoryTilesList == null) {//If no list exists, create one
				categoryTilesList = new ArrayList<MapTile>();
				categoryTiles.put(data.category, categoryTilesList);
			}
			
			//Add the tile to the category list
			categoryTilesList.add(tile);
		}
		
		//Load elements now
		fileData = FileReader.readFileIntoString("/shared/map-elements/elements.json");
		ElementData[] elementDatas = json.fromJson(ElementList.class, fileData).elementList;
		
		HashMap<String, Pair<TextureRegion, BufferedImage>> elementCategoryImages = new HashMap<String, Pair<TextureRegion, BufferedImage>>();
		
		//Load each individual element
		for (ElementData data : elementDatas) {
			Pair<TextureRegion, BufferedImage> pair = elementCategoryImages.get(data.category);
			
			if (pair == null) {
				try {
					pair = new Pair<TextureRegion, BufferedImage>(
							new TextureRegion(new Texture("map-elements/elements/" + data.category + ".png")),
							ImageIO.read(getClass().getResourceAsStream("/map-elements/elements/" + data.category + ".png")));
				} catch (IOException e) {
					System.out.println("Could not load elements with category " + data.category);
				}
				elementCategoryImages.put(data.category, pair);
			}
			
			TextureRegion elementMapImage = pair.getVal1();
			BufferedImage elementMapBufferedImage = pair.getVal2();
			
			BufferedImage icon = elementMapBufferedImage.getSubimage(data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			TextureRegion texture = new TextureRegion(elementMapImage, data.x * MainEditor.TILE_SIZE, data.y * MainEditor.TILE_SIZE, MainEditor.TILE_SIZE * data.width, MainEditor.TILE_SIZE * data.height);
			MapElement element = new MapElement(data, texture, icon);
			elementMap.put(data.id, element);
			elementList.add(element);

			//Get list of elements in same category
			ArrayList<MapElement> categoryElementsList = categoryElements.get(data.category);
			if (categoryElementsList == null) {//If no list exists, create one
				categoryElementsList = new ArrayList<MapElement>();
				categoryElements.put(data.category, categoryElementsList);
			}

			//Add the element to the category list
			categoryElementsList.add(element);
		}
		
		//Load entity textures
		EntityType.loadAllImages();
	}
	
}
