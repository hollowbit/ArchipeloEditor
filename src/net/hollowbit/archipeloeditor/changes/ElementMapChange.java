package net.hollowbit.archipeloeditor.changes;

import net.hollowbit.archipeloeditor.MainEditor;

public class ElementMapChange extends Change {
	
	String[][] elementmap;
	String[][] redoElementmap;
	
	public ElementMapChange() {
		elementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				elementmap[i][u] = new String(MainEditor.map.getElements()[i][u]);
		}
	}
	
	@Override
	public void undoChange() {
		redoElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				redoElementmap[i][u] = new String(MainEditor.map.getElements()[i][u]);
		}
		
		String[][] newElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newElementmap[i][u] = new String(elementmap[i][u]);
		}
		MainEditor.map.setElements(newElementmap);
	}

	@Override
	public void redoChanges() {
		String[][] newElementmap = new String[MainEditor.map.getHeight()][MainEditor.map.getWidth()];
		for(int i = 0; i < MainEditor.map.getHeight(); i++){
			for(int u = 0; u < MainEditor.map.getWidth(); u++)
				newElementmap[i][u] = new String(redoElementmap[i][u]);
		}
		MainEditor.map.setElements(newElementmap);
	}

}
