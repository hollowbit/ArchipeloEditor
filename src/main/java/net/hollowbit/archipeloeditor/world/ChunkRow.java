package net.hollowbit.archipeloeditor.world;

import java.util.TreeMap;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.ChunkData;

public class ChunkRow {
	
	private int y;
	private TreeMap<Integer, Chunk> chunks;
	
	public ChunkRow(int y) {
		this.y = y;
		chunks = new TreeMap<Integer, Chunk>();
	}
	
	public ChunkRow(ChunkRow rowToCopy) {
		this.y = rowToCopy.y;
		chunks = new TreeMap<Integer, Chunk>(rowToCopy.chunks);
	}
	
	public int getY() {
		return y;
	}
	
	public TreeMap<Integer, Chunk> getChunks() {
		return chunks;
	}
	
	public float getPixelY() {
		return y * ChunkData.SIZE * MainEditor.TILE_SIZE;
	}
	
}
