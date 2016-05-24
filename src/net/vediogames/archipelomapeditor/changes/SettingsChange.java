package net.vediogames.archipelomapeditor.changes;

import net.vediogames.archipelomapeditor.MainEditor;

public class SettingsChange extends Change {
	
	private String name = "";
	private byte type;
	private byte climate;
	
	private String redoName = "";
	private byte redoType;
	private byte redoClimate;
	
	public SettingsChange() {
		name = new String(MainEditor.map.getName());
		type = new Byte(MainEditor.map.getType());
		climate = new Byte(MainEditor.map.getClimate());
	}
	
	@Override
	public void undoChange() {
		redoName = new String(MainEditor.map.getName());
		redoType = new Byte(MainEditor.map.getType());
		redoClimate = new Byte(MainEditor.map.getClimate());
		MainEditor.map.setName(new String(name));
		MainEditor.map.setType(new Byte(type));
		MainEditor.map.setClimate(new Byte(climate));
	}
	
	@Override
	public void redoChanges() {
		MainEditor.map.setName(new String(redoName));
		MainEditor.map.setType(new Byte(redoType));
		MainEditor.map.setClimate(new Byte(redoClimate));
	}

}
