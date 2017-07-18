package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloeditor.world.Map;

public class ChunkRemoveChange extends Change {
	
	protected Map map;
	protected Chunk chunk;
	
	public ChunkRemoveChange(Map map, Chunk chunk) {
		super();
		this.map = map;
		this.chunk = new Chunk(chunk);
	}

	@Override
	public void undoChange() {
		map.addChunk(chunk.getX(), chunk.getY(), chunk);
	}

	@Override
	public void redoChanges() {
		map.removeChunk(chunk.getX(), chunk.getY());
	}

}
