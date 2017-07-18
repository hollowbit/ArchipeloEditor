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
	
	private ArrayList<ChunkRow> chunkRows;
	private ArrayList<EntitySnapshot> entitySnapshots;
	
	private int width, height;
	private int minTileX, maxTileX, minTileY, maxTileY;
	
	public Map() {
		chunkRows = new ArrayList<ChunkRow>();
		entitySnapshots = new ArrayList<EntitySnapshot>();
		
		this.addChunk(0, 0);
	}
	
	//Create map from editor
	public Map(String name, String displayName, String music, boolean naturalLighting) {
		this.name = name;
		this.displayName = displayName;
		this.music = music;
		this.naturalLighting = naturalLighting;
		
		chunkRows = new ArrayList<ChunkRow>();
		entitySnapshots = new ArrayList<EntitySnapshot>();
		
		this.addChunk(0, 0);
	}
	
	public void draw (AssetManager assetManager, boolean showTiles, boolean showElements, boolean showGrid, int tileX, int tileY, int selectedLayer, Object selectedListValue, SpriteBatch batch, int visibleX, int visibleY, int visibleWidth, int visibleHeight ){
		int visibleChunkX = (int) Math.floor((float) visibleX / ChunkData.SIZE);
		int visibleChunkY = (int) Math.floor((float) visibleY / ChunkData.SIZE);
		
		//If show tiles and tiles exist, draw them
		if (showTiles) {
			int hoverChunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
			int hoverChunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
			
			int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
			if (tileX < 0)
				xWithinChunk = ChunkData.SIZE - xWithinChunk;
			int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
			if (tileY < 0)
				yWithinChunk = ChunkData.SIZE - yWithinChunk;
			
			MapTile hoverTile = null;
			if (selectedLayer == MainEditor.TILE_LAYER && selectedListValue != null)
				hoverTile = (MapTile) selectedListValue;
				
			for (ChunkRow chunkRow : chunkRows) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (Chunk chunk : chunkRow.getChunks()) {
					if (chunk.getX() < visibleChunkX - 1 || chunk.getX() > visibleChunkX + (visibleWidth / ChunkData.SIZE) + 1)
						continue;
						
					if (hoverChunkX == chunk.getX() && hoverChunkY == chunk.getY())
						chunk.drawTiles(batch, assetManager, hoverTile, xWithinChunk, yWithinChunk);
					else
						chunk.drawTiles(batch, assetManager, null, -1, -1);
				}
			}
		}
		
		//If show elements and elements exist, draw them
		if (showElements) {
			int hoverChunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
			int hoverChunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);

			int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
			if (tileX < 0)
				xWithinChunk = ChunkData.SIZE - xWithinChunk;
			int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
			if (tileY < 0)
				yWithinChunk = ChunkData.SIZE - yWithinChunk;
			
			MapElement hoverElement = null;
			if (selectedLayer == MainEditor.ELEMENT_LAYER && selectedListValue != null)
				hoverElement = (MapElement) selectedListValue;
			
			for (ChunkRow chunkRow : chunkRows) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (int row = ChunkData.SIZE - 1; row >= 0; row--) {
					for (Chunk chunk : chunkRow.getChunks()) {
						if (chunk.getX() < visibleChunkX - 1 || chunk.getX() > visibleChunkX + (visibleWidth / ChunkData.SIZE) + 1)
							continue;
						
						if (hoverChunkX == chunk.getX() && hoverChunkY == chunk.getY())
							chunk.drawElements(batch, assetManager, row, hoverElement, xWithinChunk, yWithinChunk);
						else
							chunk.drawElements(batch, assetManager, row, null, -1, -1);
					}
				}
			}
		}

		//Draw Grid
		if (showGrid) {
			for (ChunkRow chunkRow : chunkRows) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (Chunk chunk : chunkRow.getChunks()) {
					if (chunk.getX() < visibleChunkX - 1 || chunk.getX() > visibleChunkX + (visibleWidth / ChunkData.SIZE) + 1)
						continue;
					
					for(int i = 0; i <= ChunkData.SIZE; i++){
						for(int u = 0; u <= ChunkData.SIZE; u++)
							batch.draw(assetManager.getGridTexture(), u * MainEditor.TILE_SIZE + chunk.getPixelX(), i * MainEditor.TILE_SIZE + chunk.getPixelY() - 2);
					}
				}
			}
		}
		
	}
	
	public boolean doesChunkExist(int x, int y) {
		return this.getChunk(x, y) != null;
	}
	
	public void addChunk(int x, int y) {
		Chunk chunk = new Chunk(x, y);
		this.addChunk(x, y, chunk);
	}
	
	public void addChunk(int x, int y, Chunk chunk) {
		ChunkRow rowFound = null;
		for (ChunkRow row : chunkRows) {
			if (row.getY() == y) {
				row.getChunks().add(chunk);
				rowFound = row;
				break;
			}
		}
		
		//Add new row to accommodate for new chunk
		if (rowFound == null) {
			rowFound = new ChunkRow(y);
			chunkRows.add(rowFound);
			rowFound.getChunks().add(chunk);
			sortChunkRows();
		}
		
		rowFound.sort();
		recalculateSizes();
	}
	
	public void removeChunk(int x, int y) {
		int rowToRemove = -1;
		for (int u = 0; u < chunkRows.size(); u++) {
			ChunkRow row = chunkRows.get(u);
			if (row.getY() == y) {
				int indexToRemove = 0;
				for (int i = 0; i < row.getChunks().size(); i++) {
					if (row.getChunks().get(i).getX() == x) {
						indexToRemove = i;
						break;
					}
				}
				row.getChunks().remove(indexToRemove);
				
				if (row.getChunks().isEmpty())
					rowToRemove = u;
				else
					row.sort();
				break;
			}
		}
		
		if (rowToRemove != -1) {
			chunkRows.remove(rowToRemove);
			sortChunkRows();
		}
		
		recalculateSizes();
	}
	
	private void sortChunkRows() {
		Collections.sort(chunkRows, new Comparator<ChunkRow>() {
			
			@Override
			public int compare(ChunkRow o1, ChunkRow o2) {
				return o2.getY() - o1.getY();
			}
			
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
		for (ChunkRow row : chunkRows)
			row.getChunks().clear();
		chunkRows.clear();
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
		int lowestX = chunkRows.get(0).getChunks().get(0).getX();
		int highestX = chunkRows.get(0).getChunks().get(0).getX();
		
		for (ChunkRow row : chunkRows) {
			for (Chunk chunk : row.getChunks()) {
				if (chunk.getX() < lowestX)
					lowestX = chunk.getX();
				
				if (chunk.getX() > highestX)
					highestX = chunk.getX();
			}
		}
		this.width = (highestX - lowestX + 1) * ChunkData.SIZE;
		this.minTileX = lowestX * ChunkData.SIZE;
		this.maxTileX = (highestX + 1) * ChunkData.SIZE - 1;
		
		int lowestY = chunkRows.get(chunkRows.size() - 1).getY();
		int highestY = chunkRows.get(0).getY();
		
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
	
	public void setTile(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk, String tileId) {
		for (ChunkRow row : chunkRows) {
			if (row.getY() == chunkY) {
				for (Chunk chunk : row.getChunks()) {
					if (chunk.getX() == chunkX) {
						chunk.getTiles()[yWithinChunk][xWithinChunk] = tileId;
						return;
					}
					
					//At this point, chunk is assumed to not be there
					if (chunk.getX() > chunkX)
						return;
				}
				break;
			}
			
			if (row.getY() < chunkY)
				break;
		}
		
		//Chunk at location not found, don't do anything
	}
	
	public void setTile(int tileX, int tileY, String tileId) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		setTile(chunkX, chunkY, xWithinChunk, yWithinChunk, tileId);
	}
	
	public String getTile(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk) {
		for (ChunkRow row : chunkRows) {
			if (row.getY() == chunkY) {
				for (Chunk chunk : row.getChunks()) {
					if (chunk.getX() == chunkX) {
						return chunk.getTiles()[yWithinChunk][xWithinChunk];
					}
					
					//At this point, chunk is assumed to not be there
					if (chunk.getX() > chunkX)
						return null;
				}
				break;
			}
			
			if (row.getY() < chunkY)
				break;
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public String getTile(int tileX, int tileY) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		return getTile(chunkX, chunkY, xWithinChunk, yWithinChunk);
	}
	
	public void setElement(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk, String elementId) {
		for (ChunkRow row : chunkRows) {
			if (row.getY() == chunkY) {
				for (Chunk chunk : row.getChunks()) {
					if (chunk.getX() == chunkX) {
						chunk.getElements()[yWithinChunk][xWithinChunk] = elementId;
						return;
					}
					
					//At this point, chunk is assumed to not be there
					if (chunk.getX() > chunkX)
						return;
				}
				break;
			}
			
			if (row.getY() < chunkY)
				break;
		}
		
		//Chunk at location not found, don't do anything
	}
		
	public void setElement(int tileX, int tileY, String elementId) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		setElement(chunkX, chunkY, xWithinChunk, yWithinChunk, elementId);
	}
	
	public String getElement(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk) {
		for (ChunkRow row : chunkRows) {
			if (row.getY() == chunkY) {
				for (Chunk chunk : row.getChunks()) {
					if (chunk.getX() == chunkX) {
						return chunk.getElements()[yWithinChunk][xWithinChunk];
					}
					
					//At this point, chunk is assumed to not be there
					if (chunk.getX() > chunkX)
						return null;
				}
				break;
			}
			
			if (row.getY() < chunkY)
				break;
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public String getElement(int tileX, int tileY) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		return getElement(chunkX, chunkY, xWithinChunk, yWithinChunk);
	}
	
	public ArrayList<ChunkRow> getChunkRows() {
		return chunkRows;
	}
	
	public void setChunkRows(ArrayList<ChunkRow> chunkRows) {
		this.chunkRows = chunkRows;
		sortChunkRows();
	}
	
	public Chunk getChunk(int x, int y) {
		for (ChunkRow row : chunkRows) {
			if (row.getY() == y) {
				for (Chunk chunk : row.getChunks()) {
					if (chunk.getX() == x)
						return chunk;
				}
			}
		}
		
		return null;
	}
}
