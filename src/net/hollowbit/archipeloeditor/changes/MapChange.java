package net.hollowbit.archipeloeditor.changes;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloeditor.world.Map;

public class MapChange extends Change {
	
	ArrayList<Chunk> chunks;
	ArrayList<Chunk> redoChunks;
	
	Map map;
	
	//Saves a state of the elements on the map to undo changes
	public MapChange(Map map) {
		this.map = map;
		
		chunks = new ArrayList<Chunk>();
		for (Chunk chunk : map.getChunks())
			chunks.add(new Chunk(chunk));
	}
	
	//Undoes the changes
	@Override
	public void undoChange() {
		redoChunks = new ArrayList<Chunk>();
		for (Chunk chunk : map.getChunks())
			chunks.add(new Chunk(chunk));
		
		ArrayList<Chunk> newChunks = new ArrayList<Chunk>();
		for (Chunk chunk : chunks)
			newChunks.add(new Chunk(chunk));
		map.setChunks(newChunks);
	}
	
	//Redoes undone changes
	@Override
	public void redoChanges() {
		ArrayList<Chunk> newChunks = new ArrayList<Chunk>();
		for (Chunk chunk : redoChunks)
			newChunks.add(new Chunk(chunk));
		map.setChunks(newChunks);
	}

}
