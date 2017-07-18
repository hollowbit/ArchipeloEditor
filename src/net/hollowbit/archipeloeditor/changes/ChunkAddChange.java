package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class ChunkAddChange extends Change {
	
	protected Map map;
	protected int chunkX, chunkY;
	
	public ChunkAddChange(Map map, int chunkX, int chunkY) {
		this.map = map;
		this.chunkX = chunkX;
		this.chunkY = chunkY;
	}
	
	@Override
	public void undoChange() {
		map.removeChunk(chunkX, chunkY);
	}

	@Override
	public void redoChanges() {
		map.addChunk(chunkX, chunkY);
	}

}
