package net.vediogames.archipelomapeditor.changes;

import net.vediogames.archipelomapeditor.MainEditor;

public class ResizeChange extends Change {

	String[][] tilemap;
	String[][] redoTilemap;
	String[][] elementmap;
	String[][] redoElementmap;
	int width;
	int height;
	
	int redoWidth;
	int redoHeight;
	
	public ResizeChange() {
		tilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				tilemap[i][u] = new String(MainEditor.map.getTiles()[i][u]);
		}
		elementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				elementmap[i][u] = new String(MainEditor.map.getElements()[i][u]);
		}
		width = new Integer(MainEditor.map.getWidth());
		height = new Integer(MainEditor.map.getHeight());
	}
	
	@Override
	public void undoChange() {
		redoTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				redoTilemap[i][u] = new String(MainEditor.map.getTiles()[i][u]);
		}
		redoElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				redoElementmap[i][u] = new String(MainEditor.map.getElements()[i][u]);
		}
		redoWidth = new Integer(MainEditor.map.getWidth());
		redoHeight = new Integer(MainEditor.map.getHeight());
		
		
		MainEditor.map.resize(new Integer(width), new Integer(height));
		String[][] newTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newTilemap[i][u] = new String(tilemap[i][u]);
		}
		MainEditor.map.setTiles(newTilemap);
		String[][] newElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newElementmap[i][u] = new String(elementmap[i][u]);
		}
		MainEditor.map.setElements(newElementmap);
		System.out.println("Resize Undone!");
	}
	
	@Override
	public void redoChanges() {
		MainEditor.map.resize(redoWidth, redoHeight);
		String[][] newTilemap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newTilemap[i][u] = new String(redoTilemap[i][u]);
		}
		MainEditor.map.setTiles(newTilemap);
		String[][] newElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newElementmap[i][u] = new String(redoElementmap[i][u]);
		}
		MainEditor.map.setElements(newElementmap);
	}

}
