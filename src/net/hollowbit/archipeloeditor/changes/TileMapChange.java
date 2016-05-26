package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.MainEditor;

public class TileMapChange extends Change {
	
	String[][] tilemap;
	String[][] redoTilemap;

	public TileMapChange() {
		tilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				tilemap[i][u] = new String(MainEditor.map.getTiles()[i][u]);
		}
	}
	
	@Override
	public void undoChange() {
		redoTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				redoTilemap[i][u] = new String(MainEditor.map.getTiles()[i][u]);
		}
		
		String[][] newTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newTilemap[i][u] = new String(tilemap[i][u]);
		}
		MainEditor.map.setTiles(newTilemap);
	}

	@Override
	public void redoChanges() {
		String[][] newTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newTilemap[i][u] = new String(redoTilemap[i][u]);
		}
		MainEditor.map.setTiles(newTilemap);
	}
	
	
	
}
