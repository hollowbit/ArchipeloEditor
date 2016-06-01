package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class TileMapChange extends Change {
	
	String[][] tilemap;
	String[][] redoTilemap;
	
	Map map;
	
	//Similar to other changes, it saves a state to be able to go back to it
	public TileMapChange(Map map) {
		this.map = map;
		tilemap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for(int u = 0; u < map.getWidth(); u++)
				tilemap[i][u] = new String(map.getTiles()[i][u]);
		}
	}
	
	//Saves current map in case of redo, then undoes them
	@Override
	public void undoChange() {
		redoTilemap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				redoTilemap[i][u] = new String(map.getTiles()[i][u]);
		}
		
		String[][] newTilemap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				newTilemap[i][u] = new String(tilemap[i][u]);
		}
		map.setTiles(newTilemap);
	}
	
	//Applies redo of changes
	@Override
	public void redoChanges() {
		String[][] newTilemap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				newTilemap[i][u] = new String(redoTilemap[i][u]);
		}
		map.setTiles(newTilemap);
	}
	
	
	
}
