package net.hollowbit.archipeloeditor.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Scanner;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapData;

public class Map implements Cloneable {
	
	private String displayName = "";
	private String music = "";
	private String name;
	private boolean naturalLighting;
	
	private ArrayList<Chunk> chunks;
	private ArrayList<EntitySnapshot> entitySnapshots;
	
	private int width, height;
	private int minTileX, maxTileX, minTileY, maxTileY;
	
	public Map() {
		chunks = new ArrayList<Chunk>();
		entitySnapshots = new ArrayList<EntitySnapshot>();
	}
	
	//Create map from editor
	public Map(String name, String displayName, String music, boolean naturalLighting) {
		this.name = name;
		this.displayName = displayName;
		this.music = music;
		this.naturalLighting = naturalLighting;

		chunks = new ArrayList<Chunk>();
		entitySnapshots = new ArrayList<EntitySnapshot>();
		
		this.addChunk(0, 0);
		this.addChunk(1, 0);
		this.addChunk(-1, 0);
		this.addChunk(1, 1);
		
		System.out.println("Map.java  " + width + "  " + height + "  " + minTileX + "  " + minTileY + "  " + maxTileX + "   " + maxTileY);
	}
	
	public void draw (AssetManager assetManager, boolean showTiles, boolean showElements, boolean showGrid, int tileX, int tileY, int selectedLayer, Object selectedListValue, SpriteBatch batch, int visibleX, int visibleY, int visibleWidth, int visibleHeight ){
		//If show tiles and tiles exist, draw them
		if (showTiles) {
			int hoverChunkX = tileX / ChunkData.SIZE;
			int hoverChunkY = tileY / ChunkData.SIZE;
			
			MapTile hoverTile = null;
			if (selectedLayer == MainEditor.TILE_LAYER && selectedListValue != null)
				hoverTile = (MapTile) selectedListValue;
				
			for (Chunk chunk : chunks) {
				if (hoverChunkX == chunk.getX() && hoverChunkY == chunk.getY())
					chunk.drawTiles(batch, assetManager, hoverTile, tileX % ChunkData.SIZE, tileY % ChunkData.SIZE);
				else
					chunk.drawTiles(batch, assetManager, null, -1, -1);
			}
		}
		
		//If show elements and elements exist, draw them
		if (showElements) {
			int hoverChunkX = tileX / ChunkData.SIZE;
			int hoverChunkY = tileY / ChunkData.SIZE;
			
			MapElement hoverElement = null;
			if (selectedLayer == MainEditor.ELEMENT_LAYER && selectedListValue != null)
				hoverElement = (MapElement) selectedListValue;
				
			for (Chunk chunk : chunks) {
				if (hoverChunkX == chunk.getX() && hoverChunkY == chunk.getY())
					chunk.drawElements(batch, assetManager, hoverElement, tileX % ChunkData.SIZE, tileY % ChunkData.SIZE);
				else
					chunk.drawElements(batch, assetManager, null, -1, -1);
			}
		}

		//Draw Grid
		if (showGrid) {
			for (Chunk chunk : chunks) {
				for(int i = 0; i <= ChunkData.SIZE; i++){
					for(int u = 0; u <= ChunkData.SIZE; u++)
						batch.draw(assetManager.getGridTexture(), u * MainEditor.TILE_SIZE + chunk.getPixelX(), i * MainEditor.TILE_SIZE + chunk.getPixelY() - 2);
				}
			}
		}
		
	}
	
	public void addChunk(int x, int y) {
		Chunk chunk = new Chunk(x, y);
		chunks.add(chunk);
		sortChunks();
		recalculateSizes();
	}
	
	public void removeChunk(int x, int y) {
		int indexToRemove = 0;
		for (int i = 0; i < chunks.size(); i++) {
			Chunk chunk = chunks.get(i);
			if (chunk.getX() == x && chunk.getY() == y) {
				indexToRemove = i;
				break;
			}
		}
		
		chunks.remove(indexToRemove);
		sortChunks();
		recalculateSizes();
	}
	
	private void sortChunks() {
		Collections.sort(chunks, new Comparator<Chunk>() {
			
			public int compare(Chunk o1, Chunk o2) {
				if (o1.getY() == o2.getY()) {
					return o1.getX() - o2.getX();
				} else {
					return o2.getY() - o1.getY();
				}
			};
			
		});
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
		naturalLighting = mapFile.naturalLighting;
		
		//TODO Load chunks from file
		entitySnapshots = mapFile.entitySnapshots;
		
		recalculateSizes();
	}
	
	//Serialize map with json and save it
	public void save(File file) {
		Json json = new Json();
		MapData mapFile = new MapData();
		mapFile.displayName = displayName;
		mapFile.music = music;
		mapFile.naturalLighting = naturalLighting;
		
		//TODO Save chunks to file
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
		chunks.clear();
		entitySnapshots.clear();
		width = 0;
		height = 0;
		minTileX = minTileY = maxTileX = maxTileY = 0;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMinTileX() {
		return minTileX;
	}
	
	public int getMaxTileX() {
		return maxTileX;
	}
	
	public int getMinTileY() {
		return minTileY;
	}
	
	public int getMaxTileY() {
		return maxTileY;
	}
	
	/**
	 * Recalculates the width and height of the map. Since it is a fairly costly calculation, this should only be done when necessary.
	 */
	protected void recalculateSizes() {
		int lowestX = chunks.get(0).getX();
		int highestX = chunks.get(0).getX();
		
		for (Chunk chunk : chunks) {
			if (chunk.getX() < lowestX)
				lowestX = chunk.getX();
			
			if (chunk.getX() > highestX)
				highestX = chunk.getX();
		}
		this.width = (highestX - lowestX + 1) * ChunkData.SIZE;
		this.minTileX = lowestX * ChunkData.SIZE;
		this.maxTileX = (highestX + 1) * ChunkData.SIZE - 1;
		
		int lowestY = chunks.get(0).getY();
		int highestY = chunks.get(0).getY();
		
		for (Chunk chunk : chunks) {
			if (chunk.getY() < lowestY)
				lowestY = chunk.getY();
			
			if (chunk.getY() > highestY)
				highestY = chunk.getY();
		}
		
		this.height = (highestY - lowestY + 1) * ChunkData.SIZE;
		this.minTileY = lowestY * ChunkData.SIZE;
		this.maxTileY = (highestY + 1) * ChunkData.SIZE - 1;
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
	
	public void setTile(int tileX, int tileY, String tileId) {
		int chunkX = tileX / ChunkData.SIZE;
		int chunkY = tileY / ChunkData.SIZE;
		
		for (Chunk chunk : chunks) {
			if (chunk.getX() == chunkX && chunk.getY() == chunkY) {
				int xWithinChunk = tileX % ChunkData.SIZE;
				int yWithinChunk = tileY % ChunkData.SIZE;
				
				chunk.getTiles()[yWithinChunk][xWithinChunk] = tileId;
				return;
			}
			
			//At this point, chunk is assumed to not be there
			if (chunk.getY() > chunkY && chunk.getX() > chunkX)
				return;
		}
		
		//Chunk at location not found, don't do anything
	}
	
	public String getTile(int tileX, int tileY) {
		int chunkX = tileX / ChunkData.SIZE;
		int chunkY = tileY / ChunkData.SIZE;
		
		for (Chunk chunk : chunks) {
			if (chunk.getX() == chunkX && chunk.getY() == chunkY) {
				int xWithinChunk = tileX % ChunkData.SIZE;
				int yWithinChunk = tileY % ChunkData.SIZE;
				
				return chunk.getTiles()[yWithinChunk][xWithinChunk];
			}
			
			//At this point, chunk is assumed to not be there
			if (chunk.getY() > chunkY && chunk.getX() > chunkX)
				return null;
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public void setElement(int tileX, int tileY, String elementId) {
		int chunkX = tileX / ChunkData.SIZE;
		int chunkY = tileY / ChunkData.SIZE;
		
		for (Chunk chunk : chunks) {
			if (chunk.getX() == chunkX && chunk.getY() == chunkY) {
				int xWithinChunk = tileX % ChunkData.SIZE;
				int yWithinChunk = tileY % ChunkData.SIZE;
				
				chunk.getElements()[yWithinChunk][xWithinChunk] = elementId;
				return;
			}
			
			//At this point, chunk is assumed to not be there
			if (chunk.getY() > chunkY && chunk.getX() > chunkX)
				return;
		}
		
		//Chunk at location not found, don't do anything
	}
	
	public String getElement(int tileX, int tileY) {
		int chunkX = tileX / ChunkData.SIZE;
		int chunkY = tileY / ChunkData.SIZE;
		
		for (Chunk chunk : chunks) {
			if (chunk.getX() == chunkX && chunk.getY() == chunkY) {
				int xWithinChunk = tileX % ChunkData.SIZE;
				int yWithinChunk = tileY % ChunkData.SIZE;
				
				return chunk.getElements()[yWithinChunk][xWithinChunk];
			}
			
			//At this point, chunk is assumed to not be there
			if (chunk.getY() > chunkY && chunk.getX() > chunkX)
				return null;
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public ArrayList<Chunk> getChunks() {
		return chunks;
	}
	
	public void setChunks(ArrayList<Chunk> chunks) {
		this.chunks = chunks;
	}

}
