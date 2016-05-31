package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class ResizeChange extends Change {

	String[][] tilemap;
	String[][] redoTilemap;
	String[][] elementmap;
	String[][] redoElementmap;
	int width;
	int height;
	
	int redoWidth;
	int redoHeight;
	
	Map map;
	
	public ResizeChange(Map map) {
		this.map = map;
		tilemap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				tilemap[i][u] = new String(map.getTiles()[i][u]);
		}
		elementmap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				elementmap[i][u] = new String(map.getElements()[i][u]);
		}
		width = new Integer(map.getWidth());
		height = new Integer(map.getHeight());
	}
	
	@Override
	public void undoChange() {
		redoTilemap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				redoTilemap[i][u] = new String(map.getTiles()[i][u]);
		}
		redoElementmap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				redoElementmap[i][u] = new String(map.getElements()[i][u]);
		}
		redoWidth = new Integer(map.getWidth());
		redoHeight = new Integer(map.getHeight());
		
		
		map.resize(new Integer(width), new Integer(height));
		String[][] newTilemap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				newTilemap[i][u] = new String(tilemap[i][u]);
		}
		map.setTiles(newTilemap);
		String[][] newElementmap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				newElementmap[i][u] = new String(elementmap[i][u]);
		}
		map.setElements(newElementmap);
		System.out.println("Resize Undone!");
	}
	
	@Override
	public void redoChanges() {
		map.resize(redoWidth, redoHeight);
		String[][] newTilemap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				newTilemap[i][u] = new String(redoTilemap[i][u]);
		}
		map.setTiles(newTilemap);
		String[][] newElementmap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				newElementmap[i][u] = new String(redoElementmap[i][u]);
		}
		map.setElements(newElementmap);
	}

}
