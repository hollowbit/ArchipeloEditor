package net.hollowbit.archipeloeditor.tools.generators;

import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloshared.ChunkData;

public class ResetGenerator extends Generator {

	@Override
	public Chunk generate(int x, int y) {
		ChunkData data = new ChunkData();
		data.x = x;
		data.y = y;
		
		return new Chunk(data);
	}
	
	@Override
	public String toString() {
		return "Reset";
	}
	
}
