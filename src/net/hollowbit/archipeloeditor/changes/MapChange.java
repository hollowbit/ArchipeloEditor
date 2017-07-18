package net.hollowbit.archipeloeditor.changes;

import java.util.ArrayList;

import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloeditor.world.Map;

public class MapChange extends Change {
	
	ArrayList<Chunk> editChunks;
	ArrayList<Chunk> chunks;
	ArrayList<Chunk> redoChunks;
	
	Map map;
	
	boolean finishedEditing = false;
	
	//Saves a state of the elements on the map to undo changes
	public MapChange(Map map) {
		this.map = map;
		editChunks = new ArrayList<Chunk>();
		chunks = new ArrayList<Chunk>();
	}
	
	public void addChunk(Chunk chunk) {
		if (!finishedEditing) {
			if (chunk != null && !doesChunkExist(chunk)) {
				chunks.add(chunk);
				editChunks.add(new Chunk(chunk));
			}
		}
	}
	
	protected boolean doesChunkExist(Chunk chunkToCheck) {
		for (Chunk chunk : chunks) {
			if (chunk == chunkToCheck)
				return true;
		}
		return false;
	}
	
	//Undoes the changes
	@Override
	public void undoChange() {
		finishedEditing = true;
		
		//Copy chunks to redo
		redoChunks = new ArrayList<Chunk>();
		for (Chunk chunk : chunks)
			redoChunks.add(new Chunk(chunk));
		
		//Undo changes to chunks
		for (int i = 0; i < chunks.size(); i++)
			chunks.get(i).set(editChunks.get(i));
	}
	
	//Redoes undone changes
	@Override
	public void redoChanges() {
		for (int i = 0; i < chunks.size(); i++)
			chunks.get(i).set(redoChunks.get(i));
	}

}
