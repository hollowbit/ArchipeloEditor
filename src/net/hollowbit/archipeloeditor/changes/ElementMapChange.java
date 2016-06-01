package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.world.Map;

public class ElementMapChange extends Change {
	
	String[][] elementmap;
	String[][] redoElementmap;
	
	Map map;
	
	//Saves a state of the elements on the map to undo changes
	public ElementMapChange(Map map) {
		this.map = map;
		elementmap = new String[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++){
			for(int u = 0; u < map.getWidth(); u++)
				elementmap[i][u] = new String(map.getElements()[i][u]);
		}
	}
	
	//Undoes the changes
	@Override
	public void undoChange() {
		redoElementmap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				redoElementmap[i][u] = new String(map.getElements()[i][u]);
		}
		
		String[][] newElementmap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				newElementmap[i][u] = new String(elementmap[i][u]);
		}
		map.setElements(newElementmap);
	}
	
	//Redoes undone changes
	@Override
	public void redoChanges() {
		String[][] newElementmap = new String[map.getHeight()][map.getWidth()];
		for (int i = 0; i < map.getHeight(); i++) {
			for (int u = 0; u < map.getWidth(); u++)
				newElementmap[i][u] = new String(redoElementmap[i][u]);
		}
		map.setElements(newElementmap);
	}

}
