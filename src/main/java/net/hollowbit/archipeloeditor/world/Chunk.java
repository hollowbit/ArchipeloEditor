package net.hollowbit.archipeloeditor.world;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.Entity;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.EntityData;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.TileData;

public class Chunk {
	
	public static final byte COLLISION_DEFAULT = 0;
	public static final byte COLLISION_NO = 1;
	public static final byte COLLISION_YES = 2;
	
	private int x, y;
	private String[][] tiles;
	private String[][] elements;
	private boolean[][] naturalCollisionMap;
	private byte[][] overrideCollisionMap;
	private ArrayList<Entity> entities;
	private Map map;
	
	public Chunk(int x, int y, Map map) {
		super();
		this.x = x;
		this.y = y;
		this.map = map;
		this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
		this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		this.entities = new ArrayList<Entity>();
	}
	
	public Chunk(Chunk chunkToCopy, Map map) {
		this.x = chunkToCopy.x;
		this.y = chunkToCopy.y;
		this.map = map;
		
		if (chunkToCopy.tiles != null) {
			this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
			for (int r = 0; r < ChunkData.SIZE; r++) {
				for (int c = 0; c < ChunkData.SIZE; c++)
					this.tiles[r][c] = chunkToCopy.tiles[r][c];
			}
		}
		
		if (chunkToCopy.elements != null) {
			this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
			for (int r = 0; r < ChunkData.SIZE; r++) {
				for (int c = 0; c < ChunkData.SIZE; c++)
					this.elements[r][c] = chunkToCopy.elements[r][c];
			}
		}
		
		if (chunkToCopy.naturalCollisionMap != null) {
			this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
				for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++)
					this.naturalCollisionMap[r][c] = chunkToCopy.naturalCollisionMap[r][c];
			}
		}
		
		if (chunkToCopy.overrideCollisionMap != null) {
			this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
				for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++)
					this.overrideCollisionMap[r][c] = chunkToCopy.overrideCollisionMap[r][c];
			}
		}
		
		if (chunkToCopy.entities != null) {
			this.entities = new ArrayList<Entity>();
			for (Entity entity : chunkToCopy.entities)
				this.entities.add(entity);
		}
	}
	
	public Chunk(ChunkData data, ChunkCollisionData collisionData, EntityData entityData, Map map) {
		this.x = data.x;
		this.y = data.y;
		this.tiles = data.tiles;
		this.elements = data.elements;
		this.map = map;
		
		if (collisionData != null) {
			//Deserialize collision map
			this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			if (collisionData.collisionData != null && !collisionData.collisionData.equals("")) {
				int i = 0;
		        for (int r = 0; r < naturalCollisionMap.length; r++) {
		            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
		                naturalCollisionMap[r][c] = collisionData.collisionData.charAt(i) == '1';
		                i++;
		            }
		        }
			}
			
			this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			if (collisionData.overrideCollisionData != null && !collisionData.overrideCollisionData.equals("")) {
				int i = 0;
		        for (int r = 0; r < naturalCollisionMap.length; r++) {
		            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
		                this.overrideCollisionMap[r][c] = Byte.parseByte("" + collisionData.overrideCollisionData.charAt(i));
		                i++;
		            }
		        }
			}
		}
		
		if (entityData != null) {
			this.entities = new ArrayList<Entity>();
			for (EntitySnapshot snapshot : entityData.entities)
				this.entities.add(new Entity(snapshot, this.map));
		}
        
		if (this.tiles == null)
			this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		
		if (this.elements == null)
			this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
	}
	
	/**
	 * Returns the chunk data to be saved to a file
	 * @return
	 */
	public ChunkData generateData() {
		ChunkData data = new ChunkData();
		data.x = this.x;
		data.y = this.y;
		data.tiles = this.tiles;
		data.elements = this.elements;
		
		//Serialize collision data
		StringBuilder collisionData = new StringBuilder("");
        for (int r = 0; r < naturalCollisionMap.length; r++) {
            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
            	if (overrideCollisionMap[r][c] == COLLISION_DEFAULT)
            		collisionData.append(naturalCollisionMap[r][c] ? "1" : "0");
            	else if (overrideCollisionMap[r][c] == COLLISION_NO)
            		collisionData.append("0");
            	else if (overrideCollisionMap[r][c] == COLLISION_YES)
            		collisionData.append("1");
            }
        }
		
        data.collisionData = collisionData.toString();
		return data;
	}
	
	public ChunkCollisionData generateChunkCollisionData() {
		ChunkCollisionData data = new ChunkCollisionData();
		
		//Serialize collision data
		StringBuilder collisionData = new StringBuilder("");
        for (int r = 0; r < naturalCollisionMap.length; r++) {
            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
                collisionData.append(naturalCollisionMap[r][c] ? "1" : "0");
            }
        }
        data.collisionData = collisionData.toString();
        
        //Serialize override collision data
		collisionData = new StringBuilder("");
        for (int r = 0; r < overrideCollisionMap.length; r++) {
            for (int c = 0; c < overrideCollisionMap[0].length; c++) {
                collisionData.append(overrideCollisionMap[r][c]);
            }
        }
        data.overrideCollisionData = collisionData.toString();
		
		return data;
	}
	
	public EntityData generateEntityData() {
		EntityData data = new EntityData();
		
		ArrayList<EntitySnapshot> snapshots = new ArrayList<EntitySnapshot>();
		for (Entity entity : this.entities)
			snapshots.add(entity.getSnapshot());
		
		data.entities = snapshots;
		return data;
	}
	
	/**
	 * Duplicates the values of the given chunk into this one.
	 * @param chunk
	 */
	public void set(Chunk chunkToCopy) {
		this.x = chunkToCopy.x;
		this.y = chunkToCopy.y;
		
		if (chunkToCopy.tiles != null) {
			this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
			for (int r = 0; r < ChunkData.SIZE; r++) {
				for (int c = 0; c < ChunkData.SIZE; c++)
					this.tiles[r][c] = chunkToCopy.tiles[r][c];
			}
		}
		
		if (chunkToCopy.elements != null) {
			this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
			for (int r = 0; r < ChunkData.SIZE; r++) {
				for (int c = 0; c < ChunkData.SIZE; c++)
					this.elements[r][c] = chunkToCopy.elements[r][c];
			}
		}
		
		if (chunkToCopy.naturalCollisionMap != null) {
			this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
				for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++)
					this.naturalCollisionMap[r][c] = chunkToCopy.naturalCollisionMap[r][c];
			}
		}
		
		if (chunkToCopy.overrideCollisionMap != null) {
			this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
			for (int r = 0; r < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; r++) {
				for (int c = 0; c < ChunkData.SIZE * TileData.COLLISION_MAP_SCALE; c++)
					this.overrideCollisionMap[r][c] = chunkToCopy.overrideCollisionMap[r][c];
			}
		}
	}
	
	public void drawTiles(SpriteBatch batch, AssetManager assetManager, MapTile hoverTile, int hoverX, int hoverY) {
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++) {
				if (hoverTile != null && c == hoverX && r == hoverY) {
					assetManager.drawTileByID(batch, (int) (c * MainEditor.TILE_SIZE + getPixelX()), (int) (r * MainEditor.TILE_SIZE + getPixelY()), hoverTile.id);
				} else {
					if (tiles[r][c] != null)
						assetManager.drawTileByID(batch, (int) (c * MainEditor.TILE_SIZE + getPixelX()), (int) (r * MainEditor.TILE_SIZE + getPixelY()), tiles[r][c]);
					else {
						batch.setColor(0, 0, 0, 1);
						batch.draw(assetManager.getBlank(), (int) (c * MainEditor.TILE_SIZE + getPixelX()), (int) (r * MainEditor.TILE_SIZE + getPixelY()), MainEditor.TILE_SIZE, MainEditor.TILE_SIZE);
						batch.setColor(1, 1, 1, 1);
					}
				}
			}
		}
	}
	
	public void drawElements(SpriteBatch batch, AssetManager assetManager, int row, MapElement hoverElement, int hoverX, int hoverY) {
		for (int c = 0; c < ChunkData.SIZE; c++) {
			if (hoverElement != null && c == hoverX && row == hoverY) {
				assetManager.drawElementByID(batch, (int) (c * MainEditor.TILE_SIZE + getPixelX()), (int) (row * MainEditor.TILE_SIZE + getPixelY()), hoverElement.id);
			} else {
				if (elements[row][c] != null)
					assetManager.drawElementByID(batch, (int) (c * MainEditor.TILE_SIZE + getPixelX()), (int) (row * MainEditor.TILE_SIZE + getPixelY()), elements[row][c]);
			}
		}
	}
	
	public int getX() {
		return x;
	}
	
	public float getPixelX() {
		return x * ChunkData.SIZE * MainEditor.TILE_SIZE;
	}
	
	public int getY() {
		return y;
	}
	
	public float getPixelY() {
		return y * ChunkData.SIZE * MainEditor.TILE_SIZE;
	}
	
	public String[][] getElements() {
		return elements;
	}
	
	public String[][] getTiles() {
		return tiles;
	}
	
	public boolean[][] getNaturalCollisionMap() {
		return naturalCollisionMap;
	}
	
	public byte[][] getOverrideCollisionMap() {
		return overrideCollisionMap;
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
}
