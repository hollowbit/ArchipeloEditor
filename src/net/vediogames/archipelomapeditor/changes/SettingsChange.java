package net.vediogames.archipelomapeditor.changes;

import net.vediogames.archipelomapeditor.MainEditor;

public class SettingsChange extends Change {
	
	private String name = "";
	private int type;
	private int climat;
	
	private String redoName = "";
	private int redoType;
	private int redoClimat;
	
	public SettingsChange() {
		name = new String(MainEditor.map.getName());
		type = MainEditor.map.getType();
		climat = MainEditor.map.getClimat();
	}
	
	@Override
	public void undoChange() {
		redoName = new String(MainEditor.map.getName());
		redoType = new Integer(MainEditor.map.getType());
		redoClimat = new Integer(MainEditor.map.getClimat());
		MainEditor.map.setName(new String(name));
		MainEditor.map.setType(type);
		MainEditor.map.setClimat(climat);
	}
	
	@Override
	public void redoChanges() {
		MainEditor.map.setName(new String(redoName));
		MainEditor.map.setType(redoType);
		MainEditor.map.setClimat(redoClimat);
	}

}
