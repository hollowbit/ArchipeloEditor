package net.hollowbit.archipeloeditor.tools.generators;

import net.hollowbit.archipeloeditor.tools.OpenSimplexNoise;
import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloshared.ChunkData;

public class IslandGenerator extends Generator {
	
	OpenSimplexNoise noiseGen;
	
	public IslandGenerator(Map map) {
		noiseGen = new OpenSimplexNoise(map.getName().hashCode());
	}
	
	@Override
	public Chunk generate(int x, int y) {
		ChunkData data = new ChunkData();
		data.tiles = new String[ChunkData.SIZE][ChunkData.SIZE];
		data.elements = new String[ChunkData.SIZE][ChunkData.SIZE];
		data.x = x;
		data.y = y;
		
		float[][] noise = generateOctavedSimplexNoise(x * ChunkData.SIZE, y * ChunkData.SIZE, ChunkData.SIZE, ChunkData.SIZE, 3, 0.6f, 0.001f);
		
		for (int r = 0; r < ChunkData.SIZE; r++) {
			for (int c = 0; c < ChunkData.SIZE; c++) {
				if (noise[r][c] < 0.4){//Ocean
					data.tiles[r][c] = "water";
				} else if (noise[r][c] < 0.45) {//shallow water
					data.tiles[r][c] = "water";
				} else if (noise[r][c] < 0.55) {//sand
					data.tiles[r][c] = "grass";
				} else if (noise[r][c] < 0.75) {//Grass
					data.tiles[r][c] = "grass";
				} else if (noise[r][c] < 0.9) {//Forest
					data.tiles[r][c] = "grass";
					data.elements[r][c] = "tree";
				} else if (noise[r][c] < 0.96) {//High
					data.tiles[r][c] = "dirt";
				} else if (noise[r][c] < 0.998) {//mountain
					data.tiles[r][c] = "dirt";
				} else {//Mountain peak
					data.tiles[r][c] = "dirt";
				}
			}
		}
		
		return new Chunk(data, null, null);
	}
	
	private float[][] generateOctavedSimplexNoise (int startX, int startY, int width, int height, int octaves, float roughness, float scale){
		float[][] totalNoise = new float[height][width];
		float layerFrequency = scale;
		float layerWeight = 1f;
		for (int octave = 0; octave < octaves; octave++) {
			//Calculate single layer/octave of simplex noise, then add it to total noise
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					totalNoise[y][x] += (float) noiseGen.eval((startX + x) * layerFrequency, (startY + y) * layerFrequency) * layerWeight;
				}
			}
			//Increase variables with each incrementing octave
			layerFrequency *= 2;
			layerWeight *= roughness;
		}
		return totalNoise;
	}
	
	@Override
	public String toString() {
		return "Island Generator";
	}

}
