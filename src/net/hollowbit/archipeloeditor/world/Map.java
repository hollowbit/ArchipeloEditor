package net.hollowbit.archipeloeditor.world;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue.PrettyPrintSettings;
import com.badlogic.gdx.utils.JsonWriter;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.Entity;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.ChunkLocation;
import net.hollowbit.archipeloshared.EntityData;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.InvalidMapFolderException;
import net.hollowbit.archipeloshared.MapData;
import net.hollowbit.archipeloshared.Point;
import net.hollowbit.archipeloshared.TileData;

public class Map implements Cloneable {
	
	private String displayName = "";
	private String music = "";
	private String name;
	private boolean naturalLighting;
	
	private TreeMap<Integer, ChunkRow> chunkRows;
	
	private int width, height;
	private int minTileX, maxTileX, minTileY, maxTileY;
	
	public Map() {
		chunkRows = new TreeMap<Integer, ChunkRow>();
	}
	
	//Create map from editor
	public Map(String name, String displayName, String music, boolean naturalLighting) {
		this.name = name;
		this.displayName = displayName;
		this.music = music;
		this.naturalLighting = naturalLighting;
		
		chunkRows = new TreeMap<Integer, ChunkRow>();
		
		this.addChunk(0, 0);
	}
	
	public void draw (AssetManager assetManager, boolean showTiles, boolean showElements, boolean showGrid, boolean showCollisionMap, int tileX, int tileY, int selectedLayer, Object selectedListValue, SpriteBatch batch, int visibleX, int visibleY, int visibleWidth, int visibleHeight ){
		int visibleChunkX = (int) Math.floor((float) visibleX / ChunkData.SIZE);
		int visibleChunkY = (int) Math.floor((float) visibleY / ChunkData.SIZE);
		

		int hoverChunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int hoverChunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % ChunkData.SIZE) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % ChunkData.SIZE) - 1;
		
		//If show tiles and tiles exist, draw them
		if (showTiles) {
			MapTile hoverTile = null;
			if (selectedLayer == MainEditor.TILE_LAYER && selectedListValue != null)
				hoverTile = (MapTile) selectedListValue;
				
			for (ChunkRow chunkRow : chunkRows.values()) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (Chunk chunk : chunkRow.getChunks().values()) {
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
			MapElement hoverElement = null;
			if (selectedLayer == MainEditor.ELEMENT_LAYER && selectedListValue != null)
				hoverElement = (MapElement) selectedListValue;
			
			for (ChunkRow chunkRow : chunkRows.descendingMap().values()) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (int row = ChunkData.SIZE - 1; row >= 0; row--) {
					for (Chunk chunk : chunkRow.getChunks().values()) {
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
			for (ChunkRow chunkRow : chunkRows.values()) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (Chunk chunk : chunkRow.getChunks().values()) {
					if (chunk.getX() < visibleChunkX - 1 || chunk.getX() > visibleChunkX + (visibleWidth / ChunkData.SIZE) + 1)
						continue;
					
					for(int i = 0; i <= ChunkData.SIZE; i++){
						for(int u = 0; u <= ChunkData.SIZE; u++)
							batch.draw(assetManager.getGridTexture(), u * MainEditor.TILE_SIZE + chunk.getPixelX(), i * MainEditor.TILE_SIZE + chunk.getPixelY() - 2);
					}
				}
			}
		}
		
		if (showCollisionMap) {
			for (ChunkRow chunkRow : chunkRows.values()) {
				if (chunkRow.getY() < visibleChunkY - 1 || chunkRow.getY() > visibleChunkY + (visibleHeight / ChunkData.SIZE) + 1)
					continue;
				
				for (Chunk chunk : chunkRow.getChunks().values()) {
					if (chunk.getX() < visibleChunkX - 1 || chunk.getX() > visibleChunkX + (visibleWidth / ChunkData.SIZE) + 1)
						continue;
					
					for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
						for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++) {
							boolean render = true;
							if (chunk.getOverrideCollisionMap()[r][c] == Chunk.COLLISION_DEFAULT) {
								if (chunk.getNaturalCollisionMap()[r][c])
									batch.setColor(0.8f, 0.0f, 0.0f, 0.5f);
								else
									render = false;
							} else if (chunk.getOverrideCollisionMap()[r][c] == Chunk.COLLISION_YES)
								batch.setColor(0.8f, 0.0f, 0.0f, 0.5f);
							else if (chunk.getOverrideCollisionMap()[r][c] == Chunk.COLLISION_NO) {
								batch.setColor(0.7f, 0.6f, 0.0f, 0.5f);
								render = false;
							}
							
							if (render)
								batch.draw(assetManager.getBlank(), chunk.getPixelX() + c * MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE, chunk.getPixelY() + r * MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE, MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE, MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE);
							batch.setColor(1, 1, 1, 1);
						}
					}
				}
			}
		}
		
	}
	
	public void regenerateCollisionMaps(AssetManager assetManager) {
		//Clear collisions
		for (ChunkRow row : chunkRows.values()) {
			for (Chunk chunk : row.getChunks().values()) {
				for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
					for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++)
						chunk.getNaturalCollisionMap()[r][c] = false;
				}
			}
		}
		
		//Calculate collisions
		for (ChunkRow row : chunkRows.values()) {
			for (Chunk chunk : row.getChunks().values()) {
				int chunkColX = chunk.getX() * ChunkData.SIZE * TileData.COLLISION_MAP_SCALE;
				int chunkColY = chunk.getY() * ChunkData.SIZE * TileData.COLLISION_MAP_SCALE;
				
				for (int r = 0; r < ChunkData.SIZE; r++) {
					for (int c = 0; c < ChunkData.SIZE; c++) {
						//Apply tile to collision map
						MapTile tile = assetManager.getTileByID(chunk.getTiles()[r][c]);
						if (tile == null)
							continue;
						
						for (int colRow = 0; colRow < tile.collisionTable.length; colRow++) {
							for (int colCol = 0; colCol < tile.collisionTable[0].length; colCol++) {
								if (tile.collisionTable[colRow][colCol]) {
									int x = c * TileData.COLLISION_MAP_SCALE + colCol + chunkColX;
									int y = r * TileData.COLLISION_MAP_SCALE + colRow + chunkColY;
									
									this.setCollisionMapAtPos(x, y, true);
								}
							}
						}
						
						//Apply map element to collision map
						MapElement element = assetManager.getElementByID(chunk.getElements()[r][c]);
						if (element != null) {
							for (int colRow = 0; colRow < element.collisionTable.length; colRow++) {
								for (int colCol = 0; colCol < element.collisionTable[0].length; colCol++) {
									if (element.collisionTable[element.collisionTable.length - colRow - 1][colCol]) {
										int x = c * TileData.COLLISION_MAP_SCALE + colCol + chunkColX + element.offsetX;
										int y = r * TileData.COLLISION_MAP_SCALE + colRow + chunkColY + element.offsetY;
										
										this.setCollisionMapAtPos(x, y, true);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void setNaturalCollisionMapAtPos(int x, int y, boolean collides) {
		int chunkX = (int) Math.floor((float) x / (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE));
		int chunkY = (int) Math.floor((float) y / (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE));
		
		int xWithinChunk = x % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE);
		if (x <0)
			xWithinChunk = (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE) - (Math.abs(x + 1) % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE)) - 1;
		
		int yWithinChunk = y % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE);
		if (y < 0)
			yWithinChunk = (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE) - (Math.abs(y + 1) % (ChunkData.SIZE * TileData.COLLISION_MAP_SCALE)) - 1;
		
		Chunk chunk = getChunk(chunkX, chunkY);
		if (chunk != null)
			chunk.getNaturalCollisionMap()[yWithinChunk][xWithinChunk] = collides;
	}
	
	/**
	 * Artificially override a natural collision
	 * @param x
	 * @param y
	 * @param collides
	 */
	public void setCollisionMapAtPos(int x, int y, boolean collides) {
		this.setNaturalCollisionMapAtPos(x, y, collides);
	}
	
	public boolean doesChunkExist(int x, int y) {
		return this.getChunk(x, y) != null;
	}
	
	public void addChunk(int x, int y) {
		Chunk chunk = new Chunk(x, y, this);
		this.addChunk(x, y, chunk);
	}
	
	public void addChunk(int x, int y, Chunk chunk) {
		ChunkRow row = chunkRows.get(y);
		if (row != null)
			row.getChunks().put(x, chunk);
		
		//Add new row to accommodate for new chunk
		if (row == null) {
			row = new ChunkRow(y);
			chunkRows.put(y, row);
			row.getChunks().put(x, chunk);
		}
		
		recalculateSizes();
	}
	
	public void removeChunk(int x, int y) {
		ChunkRow row = chunkRows.get(y);
		if (row != null)
			row.getChunks().remove(x);
		
		if (row.getChunks().isEmpty())
			chunkRows.remove(row.getY());
		
		recalculateSizes();
	}
	
	public void load(File folder) throws InvalidMapFolderException {
		if (!folder.exists())
			throw new InvalidMapFolderException("No folder selected");

		//Load map settings
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
		File chunksFolder = new File(folder, "chunks/");
		for (ChunkLocation chunkLocation : mapData.chunks) {
			//Get the chunk row folder
			File chunkFolder = new File(chunksFolder, chunkLocation.y + "/" + chunkLocation.x + "/");
			if (!chunkFolder.exists())
				throw new InvalidMapFolderException("Expected a chunk folder called \"" + chunkLocation.y + "/" + chunkLocation.x + "\" but it was not found.");
			
			//Get the chunk file in folder
			File chunkFile = new File(chunkFolder, "data.json");
			if (!chunkFile.exists())
				throw new InvalidMapFolderException("Could not find the expected chunk file at \"" + chunkLocation.y + "/" + chunkLocation.x + "/data.json\"");
			
			File chunkCollisionFile = new File(chunkFolder, "collisions.json");
			if (!chunkCollisionFile.exists())
				throw new InvalidMapFolderException("Could not find the expected chunk collision file at \"" + chunkLocation.y + "/" + chunkLocation.x + "/collisions.json\"");
			
			File chunkEntityFile = new File(chunkFolder, "entities.json");
			if (!chunkEntityFile.exists())
				throw new InvalidMapFolderException("Could not find the expected chunk entities file at \"" + chunkLocation.y + "/" + chunkLocation.x + "/entities.json\"");
			
			//Load data from chunk file
			reader = null;
			FileReader collisionReader = null;
			FileReader entityReader = null;
			try {
				reader = new FileReader(chunkFile);
				collisionReader = new FileReader(chunkCollisionFile);
				entityReader = new FileReader(chunkEntityFile);
				
				ChunkData data = json.fromJson(ChunkData.class, reader);
				ChunkCollisionData collisionData = json.fromJson(ChunkCollisionData.class, collisionReader);
				EntityData entityData = json.fromJson(EntityData.class, entityReader);
				
				this.addChunk(chunkLocation.x, chunkLocation.y, new Chunk(data, collisionData, entityData, this));
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

		System.out.println("Preparing settings file...");
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
		
		File chunksFolder = new File(folder, "chunks/");
		chunksFolder.mkdirs();
		deleteFolderContents(chunksFolder);//Make sure chunk folder is empty before putting things in it
		
		int numChunks = 0;
		for (ChunkRow row : chunkRows.values()) {
			numChunks += row.getChunks().size();
		}
		
		int currentCount = 0;
		for (ChunkRow row : chunkRows.values()) {
			File rowFolder = new File(chunksFolder, row.getY() + "/");
			rowFolder.mkdirs();
			deleteFolderContents(rowFolder);//Make sure row folder is empty
			
			for (Chunk chunk : row.getChunks().values()) {
				currentCount++;
				
				System.out.println("Writing chunks..." + ((float) currentCount / numChunks * 100) + "%");
				
				File chunkFolder = new File(rowFolder, chunk.getX() + "/");
				chunkFolder.mkdirs();
				chunkFolder.createNewFile();
				deleteFolderContents(chunkFolder);
				
				FileWriter chunkFileWriter = new FileWriter(new File(chunkFolder, "data.json"));
				json.toJson(chunk.generateData(), chunkFileWriter);
				chunkFileWriter.close();
				
				FileWriter chunkCollisionFileWriter = new FileWriter(new File(chunkFolder, "collisions.json"));
				json.toJson(chunk.generateChunkCollisionData(), chunkCollisionFileWriter);
				chunkCollisionFileWriter.close();
				
				FileWriter chunkEntityFileWriter = new FileWriter(new File(chunkFolder, "entities.json"));
				json.toJson(chunk.generateEntityData(), chunkEntityFileWriter);
				chunkEntityFileWriter.close();
				
				data.chunks.add(new ChunkLocation(chunk.getX(), chunk.getY()));
			}
		}
		
		System.out.println("Writing settings file...");
		settingsWriter.write(json.prettyPrint(data, settings));
		settingsWriter.close();
		System.out.println("Done!");
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
		for (ChunkRow row : chunkRows.values())
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
	
	public int getMinPixelX() {
		return minTileX * MainEditor.TILE_SIZE;
	}
	
	public int getMinPixelY() {
		return minTileY * MainEditor.TILE_SIZE;
	}

	public int getMaxPixelX() {
		return maxTileX * MainEditor.TILE_SIZE + MainEditor.TILE_SIZE;
	}

	public int getMaxPixelY() {
		return maxTileY * MainEditor.TILE_SIZE + MainEditor.TILE_SIZE;
	}
	
	/**
	 * Returns a list of the entities who's rectangles intersect the given position.
	 * @param x
	 * @param y
	 * @return
	 */
	public ArrayList<EntitySnapshot> getEntitySnapshotsAtPos(int x, int y) {
		ArrayList<EntitySnapshot> snapshots = new ArrayList<EntitySnapshot>();
		for (ChunkRow chunkRow : chunkRows.values()) {
			for (Chunk chunk : chunkRow.getChunks().values()) {
				for (Entity entity : chunk.getEntities()) {
					EntityType type = entity.getType();
					Point p = entity.getPos();
					if (x >= p.x && x < p.x + type.getData().imgWidth && y >= p.y && y < p.y + type.getData().imgHeight) {
						snapshots.add(entity.getSnapshot());
						break;
					}
				}
			}
		}
		return snapshots;
	}
	
	/**
	 * Recalculates the width and height of the map. Since it is a fairly costly calculation, this should only be done when necessary.
	 */
	protected void recalculateSizes() {
		if (chunkRows.size() == 0) {
			width = 0;
			height = 0;
			minTileX = 0;
			minTileY = 0;
			maxTileX = 0;
			maxTileY = 0;
		} else {
			int lowestX = chunkRows.firstEntry().getValue().getChunks().firstKey();
			int highestX = chunkRows.firstEntry().getValue().getChunks().lastKey();
			
			for (ChunkRow row : chunkRows.values()) {
				if (row.getChunks().firstKey() < lowestX)
					lowestX = row.getChunks().firstKey();
					
				if (row.getChunks().lastKey() > highestX)
					highestX = row.getChunks().lastKey();
			}
			this.width = (highestX - lowestX + 1) * ChunkData.SIZE;
			this.minTileX = lowestX * ChunkData.SIZE;
			this.maxTileX = (highestX + 1) * ChunkData.SIZE - 1;
			
			int lowestY = chunkRows.firstKey();
			int highestY = chunkRows.lastKey();
			
			this.height = (highestY - lowestY + 1) * ChunkData.SIZE;
			this.minTileY = lowestY * ChunkData.SIZE;
			this.maxTileY = (highestY + 1) * ChunkData.SIZE - 1;
		}
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
		ChunkRow row = chunkRows.get(chunkY);
		if (row != null) {
			Chunk chunk = row.getChunks().get(chunkX);
			if (chunk != null)
				chunk.getTiles()[yWithinChunk][xWithinChunk] = tileId;
		}
	}
	
	public void setTile(int tileX, int tileY, String tileId) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % ChunkData.SIZE) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % ChunkData.SIZE) - 1;
		
		setTile(chunkX, chunkY, xWithinChunk, yWithinChunk, tileId);
	}
	
	public String getTile(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk) {
		ChunkRow row = chunkRows.get(chunkY);
		if (row != null) {
			Chunk chunk = row.getChunks().get(chunkX);
			if (chunk != null)
				return chunk.getTiles()[yWithinChunk][xWithinChunk];
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public String getTile(int tileX, int tileY) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % ChunkData.SIZE) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % ChunkData.SIZE) - 1;
		
		return getTile(chunkX, chunkY, xWithinChunk, yWithinChunk);
	}
	
	public void setElement(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk, String elementId) {
		ChunkRow row = chunkRows.get(chunkY);
		if (row != null) {
			Chunk chunk = row.getChunks().get(chunkX);
			if (chunk != null)
				chunk.getElements()[yWithinChunk][xWithinChunk] = elementId;
		}
	}
		
	public void setElement(int tileX, int tileY, String elementId) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);

		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % ChunkData.SIZE) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % ChunkData.SIZE) - 1;
		
		setElement(chunkX, chunkY, xWithinChunk, yWithinChunk, elementId);
	}
	
	public String getElement(int chunkX, int chunkY, int xWithinChunk, int yWithinChunk) {
		ChunkRow row = chunkRows.get(chunkY);
		if (row != null) {
			Chunk chunk = row.getChunks().get(chunkX);
			if (chunk != null)
				return chunk.getElements()[yWithinChunk][xWithinChunk];
		}
		
		//Chunk at location not found, return null
		return null;
	}
	
	public String getElement(int tileX, int tileY) {
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);

		int xWithinChunk = tileX % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - (Math.abs(tileX + 1) % ChunkData.SIZE) - 1;
		int yWithinChunk = tileY % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - (Math.abs(tileY + 1) % ChunkData.SIZE) - 1;
		
		return getElement(chunkX, chunkY, xWithinChunk, yWithinChunk);
	}
	
	public Chunk getChunk(int x, int y) {
		ChunkRow row = chunkRows.get(y);
		if (row != null) {
			return row.getChunks().get(x);
		}
		
		return null;
	}
	
	public TreeMap<Integer, ChunkRow> getChunkRows() {
		return chunkRows;
	}
	
	public boolean doesEntityExist(String name) {
		for (ChunkRow chunkRow : chunkRows.values()) {
			for (Chunk chunk : chunkRow.getChunks().values()) {
				for (Entity entity : chunk.getEntities()) {
					if (entity.getName().equalsIgnoreCase(name))
						return true;
				}
			}
		}
		return false;
	}
	
	public void addUpdateEntity(EntitySnapshot snapshot) {
		Point pos = snapshot.getObject("pos", null, Point.class);
		if (pos == null)
			return;
		
		this.removeEntity(snapshot.name);
		
		int chunkX = (int) Math.floor((float) pos.x / MainEditor.TILE_SIZE / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) pos.y / MainEditor.TILE_SIZE / ChunkData.SIZE);
		Chunk chunk = this.getChunk(chunkX, chunkY);
		chunk.getEntities().add(new Entity(snapshot, this));
	}
	
	public void removeEntity(String name) {
		for (ChunkRow chunkRow : chunkRows.values()) {
			for (Chunk chunk : chunkRow.getChunks().values()) {
				Entity entityToRemove = null;
				for (Entity entity : chunk.getEntities()) {
					if (entity.getName().equalsIgnoreCase(name)) {
						entityToRemove = entity;
						break;
					}
				}
				
				if (entityToRemove != null) {
					chunk.getEntities().remove(entityToRemove);
					return;
				}
			}
		}
	}
	
}
