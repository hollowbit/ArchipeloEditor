package net.hollowbit.archipeloeditor.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChunkRow {
	
	private int y;
	private ArrayList<Chunk> chunks;
	
	public ChunkRow(int y) {
		this.y = y;
		chunks = new ArrayList<Chunk>();
	}
	
	public ChunkRow(ChunkRow rowToCopy) {
		this.y = rowToCopy.y;
		chunks = new ArrayList<Chunk>();
		for (Chunk chunk : rowToCopy.chunks)
			chunks.add(new Chunk(chunk));
		
		sort();
	}
	
	public int getY() {
		return y;
	}
	
	public ArrayList<Chunk> getChunks() {
		return chunks;
	}
	
	public void sort() {
		Collections.sort(chunks, new Comparator<Chunk>() {
			@Override
			public int compare(Chunk o1, Chunk o2) {
				return o1.getX() - o2.getX();
			}
		});
	}
	
}
