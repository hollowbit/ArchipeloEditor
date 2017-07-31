package net.hollowbit.archipeloeditor.world;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.TileData;

public class Chunk {
	
	public static final byte COLLISION_DEFAULT = 0;
	public static final byte COLLISION_NO = 1;
	public static final byte COLLISION_YES = 2;
	
	private int x, y;
	private String[][] tiles;
	private String[][] elements;
	private ArrayList<EntitySnapshot> entitySnapshots;
	private boolean[][] naturalCollisionMap;
	private byte[][] overrideCollisionMap;
	
	public Chunk(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
		this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		entitySnapshots = new ArrayList<EntitySnapshot>();
	}
	
	public Chunk(Chunk chunkToCopy) {
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
		
		entitySnapshots = new ArrayList<EntitySnapshot>();
		for (EntitySnapshot entity : chunkToCopy.entitySnapshots)
			this.entitySnapshots.add(new EntitySnapshot(entity));
	}
	
	public Chunk(ChunkData data) {
		this.x = data.x;
		this.y = data.y;
		this.tiles = data.tiles;
		this.elements = data.elements;
		this.entitySnapshots = data.entities;
		
		//Deserialize collision map
		this.naturalCollisionMap = new boolean[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		if (data.collisionData != null && !data.collisionData.equals("")) {
			char[] bytes = data.collisionData.toCharArray();
			int i2 = 0;
	        for (int r = 0; r < naturalCollisionMap.length; r++) {
	            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
	            	byte val = (byte) bytes[i2 / Byte.SIZE];
	                int pos = i2 % Byte.SIZE;
	                naturalCollisionMap[r][c] = ((val >> pos) & 1) == 1;
	                
	                i2++;
	            }
	        }
		}
		
		this.overrideCollisionMap = new byte[ChunkData.SIZE * TileData.COLLISION_MAP_SCALE][ChunkData.SIZE * TileData.COLLISION_MAP_SCALE];
		if (data.overrideCollisionData != null && !data.overrideCollisionData.equals("")) {
			char[] bytes = data.overrideCollisionData.toCharArray();
			int i2 = 0;
	        for (int r = 0; r < naturalCollisionMap.length; r++) {
	            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
	            	byte val = (byte) bytes[i2 / Byte.SIZE];
	                int pos = i2 % Byte.SIZE;
	                boolean tick1 = ((val >> pos) & 1) == 1;
	                
	                i2++;
	                val = (byte) bytes[i2 / Byte.SIZE];
	                pos = i2 % Byte.SIZE;
	                boolean tick2 = ((val >> pos) & 1) == 1;
	                
	                if (!tick1 && !tick2)
	                	overrideCollisionMap[r][c] = COLLISION_DEFAULT;
	                else if (!tick1 && tick2)
	                	overrideCollisionMap[r][c] = COLLISION_NO;
	                else if (tick1 && !tick2)
	                	overrideCollisionMap[r][c] = COLLISION_YES;
	                else
	                
	                i2++;
	            }
	        }
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
	public ChunkData getData() {
		ChunkData data = new ChunkData();
		data.x = this.x;
		data.y = this.y;
		data.tiles = this.tiles;
		data.elements = this.elements;
		data.entities = this.entitySnapshots;
		
		//Serialize collision data
		String collisionData = "";
        int i = 0;
        byte accum = 0;
        for (int r = 0; r < naturalCollisionMap.length; r++) {
            for (int c = 0; c < naturalCollisionMap[0].length; c++) {
                if (naturalCollisionMap[r][c])
                    accum |= (1 << i);
                else
                	accum &= ~(1 << i);
                    
                i++;
                if (i >= Byte.SIZE) {
                    i = 0;
                    collisionData += (char) accum;
                }
            }
        }
        collisionData += (char) accum;
		
        data.collisionData = collisionData;
        
        //Serialize override collision data
		collisionData = "";
        i = 0;
        accum = 0;
        for (int r = 0; r < overrideCollisionMap.length; r++) {
            for (int c = 0; c < overrideCollisionMap[0].length; c++) {
                if (overrideCollisionMap[r][c] == COLLISION_DEFAULT) {
                    accum &= ~(1 << i);
                    i++;
                    accum &= ~(1 << i);
                } else if (overrideCollisionMap[r][c] == COLLISION_NO) {
                	accum &= ~(1 << i);
                    i++;
                    accum |= (1 << i);
                } else if (overrideCollisionMap[r][c] == COLLISION_YES) {
                	accum |= (1 << i);
                    i++;
                    accum &= ~(1 << i);
                }
                
                i++;
                if (i >= Byte.SIZE) {
                    i = 0;
                    collisionData += (char) accum;
                }
            }
        }
        collisionData += (char) accum;
        
        data.overrideCollisionData = collisionData;
        
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
}
