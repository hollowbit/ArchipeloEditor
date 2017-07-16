package net.hollowbit.archipeloeditor.changes;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.world.ChunkRow;
import net.hollowbit.archipeloeditor.world.Map;

public class MapChange extends Change {
	
	ArrayList<ChunkRow> chunkRows;
	ArrayList<ChunkRow> redoChunkRows;
	
	Map map;
	
	//Saves a state of the elements on the map to undo changes
	public MapChange(Map map) {
		this.map = map;
		
		chunkRows = new ArrayList<ChunkRow>();
		for (ChunkRow chunkRow : map.getChunkRows())
			chunkRows.add(new ChunkRow(chunkRow));
	}
	
	//Undoes the changes
	@Override
	public void undoChange() {
		redoChunkRows = new ArrayList<ChunkRow>();
		for (ChunkRow chunkRow : map.getChunkRows())
			redoChunkRows.add(new ChunkRow(chunkRow));
		
		ArrayList<ChunkRow> newChunkRows = new ArrayList<ChunkRow>();
		for (ChunkRow chunkRow : chunkRows)
			newChunkRows.add(new ChunkRow(chunkRow));
		map.setChunkRows(newChunkRows);
	}
	
	//Redoes undone changes
	@Override
	public void redoChanges() {
		ArrayList<ChunkRow> newChunkRows = new ArrayList<ChunkRow>();
		for (ChunkRow chunkRow : redoChunkRows)
			newChunkRows.add(new ChunkRow(chunkRow));
		map.setChunkRows(newChunkRows);
	}

}
