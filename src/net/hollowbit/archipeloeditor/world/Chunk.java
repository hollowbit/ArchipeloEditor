package net.hollowbit.archipeloeditor.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.ChunkData;

public class Chunk {
	
	private int x, y;
	private String[][] tiles;
	private String[][] elements;
	
	public Chunk(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
	}
	
	public Chunk(Chunk chunkToCopy) {
		this.x = chunkToCopy.x;
		this.y = chunkToCopy.y;
		
		this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++)
				this.tiles[r][c] = chunkToCopy.tiles[r][c];
		}
		
		this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++)
				this.elements[r][c] = chunkToCopy.elements[r][c];
		}
	}
	
	public Chunk(ChunkData data) {
		this.x = data.x;
		this.y = data.y;
		this.tiles = data.tiles;
		this.elements = data.elements;
	}
	
	/**
	 * Duplicates the values of the given chunk into this one.
	 * @param chunk
	 */
	public void set(Chunk chunkToCopy) {
		this.x = chunkToCopy.x;
		this.y = chunkToCopy.y;
		
		this.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++)
				this.tiles[r][c] = chunkToCopy.tiles[r][c];
		}
		
		this.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++)
				this.elements[r][c] = chunkToCopy.elements[r][c];
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
	
}
