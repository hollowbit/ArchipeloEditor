package net.hollowbit.archipeloeditor.tools.editortools;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.world.MapElement;
import net.hollowbit.archipeloeditor.world.MapTile;
import net.hollowbit.archipeloeditor.world.worldrenderer.WorldRenderer;
import net.hollowbit.archipeloshared.ChunkData;

public class Bucket extends Tool {

	public Bucket(MainEditor editor, WorldRenderer worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY) {
		if(editor.getSelectedItemValue() != null) {
			editor.getChangeList().addChanges(new MapChange(editor.getMap()));
			editor.setJustSaved(false);
			startFill(tileX, tileY);
		}
	}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY) {}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {
		if(editor.getSelectedItemValue() != null)
			startFill(tileX, tileY);
	}
	
	protected void startFill(int tileX, int tileY) {
		boolean[][] filledTiles = new boolean[ChunkData.SIZE][ChunkData.SIZE];
		
		int chunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
		int chunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
		
		int xWithinChunk = Math.abs(tileX) % ChunkData.SIZE;
		if (tileX < 0)
			xWithinChunk = ChunkData.SIZE - xWithinChunk;
		int yWithinChunk = Math.abs(tileY) % ChunkData.SIZE;
		if (tileY < 0)
			yWithinChunk = ChunkData.SIZE - yWithinChunk;
		
		if (editor.getSelectedLayer() == MainEditor.TILE_LAYER) {
			String replaceTile = editor.getMap().getTile(tileX, tileY);
			bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk);
		} else if (editor.getSelectedLayer() == MainEditor.ELEMENT_LAYER) {
			String replaceTile = editor.getMap().getElement(tileX, tileY);
			bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk);
		}
	}

	//Recursion bucket fill algorithm for tiles
	public void bucketFillTiles(String replaceTile, boolean[][] filledTiles, int chunkX, int chunkY, int xWithinChunk, int yWithinChunk){
		if(xWithinChunk >= ChunkData.SIZE) return;
		if(yWithinChunk >= ChunkData.SIZE) return;
		if(xWithinChunk < 0) return;
		if(yWithinChunk < 0) return;
		
		if(filledTiles[yWithinChunk][xWithinChunk]) return;
		if(editor.getMap().getTile(chunkX, chunkY, xWithinChunk, yWithinChunk) == null) {
			if (replaceTile != null)
				return;
		} else {
			if (replaceTile == null)
				return;
			
			if(!editor.getMap().getTile(chunkX, chunkY, xWithinChunk, yWithinChunk).equals(replaceTile))
				return;
		}
		
		filledTiles[yWithinChunk][xWithinChunk] = true;
		
		editor.getMap().setTile(chunkX, chunkY, xWithinChunk, yWithinChunk, ((MapTile) editor.getSelectedItemValue()).id);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk + 1, yWithinChunk);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk - 1, yWithinChunk);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk + 1);
		bucketFillTiles(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk - 1);
	}

	//Recursion bucket fill algorithm for elements
	public void bucketFillElements(String replaceTile, boolean[][] filledTiles, int chunkX, int chunkY, int xWithinChunk, int yWithinChunk){
		if(xWithinChunk >= ChunkData.SIZE) return;
		if(yWithinChunk >= ChunkData.SIZE) return;
		if(xWithinChunk < 0) return;
		if(yWithinChunk < 0) return;
		
		if(filledTiles[yWithinChunk][xWithinChunk]) return;
		if(editor.getMap().getElement(chunkX, chunkY, xWithinChunk, yWithinChunk) == null) {
			if (replaceTile != null)
				return;
		} else {
			if (replaceTile == null)
				return;
			
			if(!editor.getMap().getElement(chunkX, chunkY, xWithinChunk, yWithinChunk).equals(replaceTile))
				return;
		}		filledTiles[yWithinChunk][xWithinChunk] = true;
		
		editor.getMap().setElement(chunkX, chunkY, xWithinChunk, yWithinChunk, ((MapElement) editor.getSelectedItemValue()).id);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk + 1, yWithinChunk);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk - 1, yWithinChunk);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk + 1);
		bucketFillElements(replaceTile, filledTiles, chunkX, chunkY, xWithinChunk, yWithinChunk - 1);
	}

}
