package net.hollowbit.archipeloeditor.tools.generators;

import net.hollowbit.archipeloeditor.world.Chunk;

public abstract class Generator {
	
	public abstract Chunk generate(int x, int y);
	
}
