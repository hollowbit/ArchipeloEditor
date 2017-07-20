package net.hollowbit.archipeloeditor.world;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue.PrettyPrintSettings;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.ChunkLocation;
import net.hollowbit.archipeloshared.InvalidMapFolderException;
import net.hollowbit.archipeloshared.MapData;

public class Map implements Cloneable {
	
	private String displayName = "";
	private String music = "";
	private String name;
	private boolean naturalLighting;
	
	private ArrayList<ChunkRow> chunkRows;
	
	private int width, height;
	private int minTileX, maxTileX, minTileY, maxTileY;
	
	public Map() {
		chunkRows = new ArrayList<ChunkRow>();
	}
	
	//Create map from editor
	public Map(String name, String displayName, String music, boolean naturalLighting) {
		this.name = name;
		this.displayName = displayName;
		this.music = music;
		this.naturalLighting = naturalLighting;
		
		chunkRows = new ArrayList<ChunkRow>();
		
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
	
	public void load(File folder) throws InvalidMapFolderException {
		if (!folder.exists())
			throw new InvalidMapFolderException("No folder selected");
		
		File settingsFile = new File(folder, "settings.json");
		if (!settingsFile.exists())
			throw new InvalidMapFolderException("Settings file not found. There must be a settings.json file in the map's root directory.");
			
		Json json = new Json();
		MapData mapData;
		FileReader reader = null;
		try {
			 reader = new FileReader(settingsFile);
			mapData = (MapData) json.fromJson(MapData.class, reader);
		} catch (Exception e) {
			throw new InvalidMapFolderException("Settings file is invalid.");
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.name = mapData.name;
		this.displayName = mapData.displayName;
		this.naturalLighting = mapData.naturalLighting;
		this.music = mapData.music;
		
		//Load in chunks now that we have the settings
		File chunkFolder = new File(folder, "chunks/");
		for (ChunkLocation chunkLocation : mapData.chunks) {
			//Get the chunk row folder
			File chunkRowFolder = new File(chunkFolder, chunkLocation.y + "/");
			if (!chunkRowFolder.exists())
				throw new InvalidMapFolderException("Expected a chunk row folder called \"" + chunkLocation.y + "\" but it was not found.");
			
			//Get the chunk file in folder
			File chunkFile = new File(chunkRowFolder, chunkLocation.x + ".json");
			if (!chunkFile.exists())
				throw new InvalidMapFolderException("Could not find the expected chunk file at \"" + chunkLocation.y + "/" + chunkLocation.x + ".json\"");
			
			//Load data from chunk file
			reader = null;
			try {
				reader = new FileReader(chunkFile);
				ChunkData data = (ChunkData) json.fromJson(ChunkData.class, reader);
				
				this.addChunk(chunkLocation.x, chunkLocation.y, new Chunk(data));
				reader.close();
			} catch (Exception e) {
				throw new InvalidMapFolderException("Invalid chunk file at \"" + chunkLocation.y + "/" + chunkLocation.x + ".json\"");
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Serialize map with json and save it
	public void save(File parentFolder) throws IOException {
		parentFolder.mkdirs();
		File folder = new File(parentFolder, name + "/");
		folder.mkdirs();
		
		File settingsFile = new File(folder, "settings.json");
		settingsFile.createNewFile();

		Json json = new Json();
		PrettyPrintSettings settings = new PrettyPrintSettings();
		settings.singleLineColumns = 30;
		settings.wrapNumericArrays = false;
		settings.outputType = JsonWriter.OutputType.javascript;
		
		FileWriter settingsWriter = new FileWriter(settingsFile);
		MapData data = new MapData();
		data.name = this.name;
		data.displayName = this.displayName;
		data.naturalLighting = this.naturalLighting;
		data.music = this.music;
		
		File chunkFolder = new File(folder, "chunks/");
		chunkFolder.mkdirs();
		deleteFolderContents(chunkFolder);//Make sure chunk folder is empty before putting things in it
		
		for (ChunkRow row : chunkRows) {
			File rowFolder = new File(chunkFolder, row.getY() + "/");
			rowFolder.mkdirs();
			deleteFolderContents(rowFolder);//Make sure row folder is empty
			
			for (Chunk chunk : row.getChunks()) {
				FileWriter chunkFileWriter = new FileWriter(new File(rowFolder, chunk.getX() + ".json"));
				chunkFileWriter.write(json.prettyPrint(chunk.getData(), settings));
				chunkFileWriter.close();
				
				data.chunks.add(new ChunkLocation(chunk.getX(), chunk.getY()));
			}
		}
		
		settingsWriter.write(json.prettyPrint(data, settings));
		settingsWriter.close();
	}
	
	private void deleteFolderContents(File folder) {
		for (File child : folder.listFiles()) {
			if (child.isDirectory())
				deleteFolderContents(child);
			child.delete();
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
